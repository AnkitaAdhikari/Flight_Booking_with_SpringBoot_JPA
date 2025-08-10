package com.example.jwt.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.jwt.dto.BookingDto;
import com.example.jwt.dto.PassengerDto;
import com.example.jwt.entity.Booking;
import com.example.jwt.entity.Flight;
import com.example.jwt.entity.User;
import com.example.jwt.exception.ResourceNotFoundException;
import com.example.jwt.repository.BookingRepository;
import com.example.jwt.repository.FlightRepository;
import com.example.jwt.response.BookingResponseDto;

@Service
public class BookingService {

	@Autowired
	private BookingRepository bookingRepo;

	@Autowired
	private FlightRepository flightRepo;

	private static final BigDecimal GST_RATE = new BigDecimal("0.18");
	private static final BigDecimal HIGH_OCCUPANCY_HIKE = new BigDecimal("0.20");
	private static final BigDecimal MID_OCCUPANCY_HIKE = new BigDecimal("0.10");
	private static final BigDecimal URGENCY_HIKE = new BigDecimal("0.15");

	// Book a flight for multiple passengers
	public List<BookingResponseDto> bookMultipleSeats(BookingDto request, User user) {
		Flight flight = flightRepo.findById(request.getFlightId())
				.orElseThrow(() -> new ResourceNotFoundException("Flight not found"));

		List<BookingResponseDto> responseList = new ArrayList<>();

		for (PassengerDto passenger : request.getPassengers()) {

			if (bookingRepo.existsByFlightAndSeatNo(flight, passenger.getSeatNo())) {
				throw new RuntimeException("Seat " + passenger.getSeatNo() + " is already booked.");
			}

			Booking booking = new Booking();
			booking.setFlight(flight);
			booking.setUser(user);
			booking.setSeatNo(passenger.getSeatNo());
			booking.setBookingTime(LocalDateTime.now());

			// Set passenger details
			booking.setPassengerName(passenger.getName());
			booking.setPassengerAge(passenger.getAge());
			booking.setPassengerGender(passenger.getGender());
			booking.setPassengerType(passenger.getType());

			// Price calculation
			BigDecimal basePrice = flight.getPrice();
			BigDecimal multiplier = calculateDynamicMultiplier(flight);
			BigDecimal dynamicPrice = basePrice.multiply(multiplier);

			BigDecimal gst = dynamicPrice.multiply(GST_RATE);
			BigDecimal convenienceFee = new BigDecimal("100");
			BigDecimal otherTaxes = new BigDecimal("50");
			BigDecimal finalPrice = dynamicPrice.add(gst).add(convenienceFee).add(otherTaxes);

			booking.setBasePrice(basePrice);
			booking.setDynamicMultiplier(multiplier);
			booking.setGstAmount(gst);
			booking.setConvenienceFee(convenienceFee);
			booking.setOtherTaxes(otherTaxes);
			booking.setFinalPrice(finalPrice);

			bookingRepo.save(booking);
			responseList.add(toDto(booking));
		}

		// Update flight seat count
		int bookedCount = bookingRepo.countByFlight(flight);
		flight.setBookedSeatsCount(bookedCount);
		flightRepo.save(flight);

		return responseList;
	}

	private BigDecimal calculateDynamicMultiplier(Flight flight) {
		int bookedSeats = bookingRepo.countByFlight(flight);
		return getMultiplier(flight.getDepartureTime(), flight.getTotalSeats(), bookedSeats);
	}

	private BigDecimal getMultiplier(LocalDateTime departureTime, int totalSeats, int bookedSeats) {
		BigDecimal multiplier = BigDecimal.ONE;
		double occupancyRate = (double) bookedSeats / totalSeats;

		if (occupancyRate > 0.8) {
			multiplier = multiplier.add(HIGH_OCCUPANCY_HIKE);
		} else if (occupancyRate > 0.5) {
			multiplier = multiplier.add(MID_OCCUPANCY_HIKE);
		}

		if (departureTime.isBefore(LocalDateTime.now().plusHours(48))) {
			multiplier = multiplier.add(URGENCY_HIKE);
		}

		return multiplier;
	}

	private BookingResponseDto toDto(Booking booking) {
		BookingResponseDto dto = new BookingResponseDto();
		dto.setId(booking.getId());
		dto.setSeatNo(booking.getSeatNo());
		dto.setBasePrice(booking.getBasePrice());
		dto.setDynamicMultiplier(booking.getDynamicMultiplier());
		dto.setGstAmount(booking.getGstAmount());
		dto.setConvenienceFee(booking.getConvenienceFee());
		dto.setOtherTaxes(booking.getOtherTaxes());
		dto.setFinalPrice(booking.getFinalPrice());
		dto.setBookingTime(booking.getBookingTime());
		dto.setPassengerName(booking.getPassengerName());
		dto.setPassengerAge(booking.getPassengerAge());
		dto.setPassengerGender(booking.getPassengerGender());
		dto.setPassengerType(booking.getPassengerType());
		dto.setUser(booking.getUser());
		dto.setFlight(booking.getFlight());

		return dto;
	}

	public List<BookingResponseDto> getBookingsByUser(User user) {
		List<Booking> bookings = bookingRepo.findByUser(user);
		return bookings.stream().map(this::toDto).collect(Collectors.toList());
	}
}

// The Logic............... 
//Multiplier = 1 + 0.10 + 0.15 = 1.25  
//Price = ₹4000 * 1.25 = ₹5000  
//GST = ₹5000 * 0.18 = ₹900  
//Final Price = ₹5900
