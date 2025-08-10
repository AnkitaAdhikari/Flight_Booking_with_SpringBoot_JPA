package com.example.jwt.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.example.jwt.config.CustomUserDetails;
import com.example.jwt.dto.BookingDto;
import com.example.jwt.entity.User;
import com.example.jwt.response.BookingResponseDto;
import com.example.jwt.response.BookingResponseWrapper;
import com.example.jwt.service.BookingService;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

	@Autowired
	private BookingService bookingService;

	// ✅ POST: Book a flight
	@PostMapping
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	public ResponseEntity<?> bookFlight(@RequestBody BookingDto bookingDto,
			@AuthenticationPrincipal CustomUserDetails userDetails) {
		if (userDetails == null || userDetails.getUser() == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
		}

		try {
			User user = userDetails.getUser();

			// 🧾 Book multiple passengers
			List<BookingResponseDto> responses = bookingService.bookMultipleSeats(bookingDto, user);

			// 💰 Calculate total price
			double total = responses.stream()
					.mapToDouble(r -> r.getFinalPrice() != null ? r.getFinalPrice().doubleValue() : 0.0).sum();

			// 📦 Build response
			Map<String, Object> result = new HashMap<>();
			result.put("totalPrice", total);
			result.put("bookings", responses);

			return ResponseEntity.status(HttpStatus.CREATED).body(result);

		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Booking failed: " + e.getMessage());
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("An unexpected error occurred: " + e.getMessage());
		}
	}

	// ✅ GET: List bookings for the current user
	@GetMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<?> getMyBookings(@AuthenticationPrincipal CustomUserDetails userDetails) {
		if (userDetails == null || userDetails.getUser() == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
		}

		try {
			List<BookingResponseDto> bookings = bookingService.getBookingsByUser(userDetails.getUser());
			return ResponseEntity.ok(bookings);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("Failed to fetch bookings: " + ex.getMessage());
		}
	}

}
