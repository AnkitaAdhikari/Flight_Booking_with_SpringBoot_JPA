package com.example.jwt.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.jwt.entity.Flight;
import com.example.jwt.exception.ResourceNotFoundException;
import com.example.jwt.repository.BookingRepository;
import com.example.jwt.repository.FlightRepository;

@Service
public class FlightService {

	@Autowired
	private FlightRepository flightRepo;

	@Autowired
	private BookingRepository bookingRepo;

	public List<Flight> searchFlights(String source, String destination) {
		return flightRepo.findBySourceAndDestination(source, destination);
	}

	/**
	 * Calculates dynamic price based on: - Date (within 7 days: +20%) - Occupancy
	 * (>80%: +30%)
	 */
	public BigDecimal calculatePrice(Flight flight) {
		BigDecimal basePrice = flight.getPrice();
		LocalDate flightDate = flight.getDepartureTime().toLocalDate();
		BigDecimal finalPrice = basePrice;

		// 1. Check if flight is within 7 days
		long daysUntilDeparture = ChronoUnit.DAYS.between(LocalDate.now(), flightDate);
		if (daysUntilDeparture <= 7) {
			finalPrice = finalPrice.multiply(BigDecimal.valueOf(1.2));
		}

		// 2. Check occupancy
		int bookedSeats = bookingRepo.countByFlight(flight);
		int totalSeats = flight.getTotalSeats();

		if (totalSeats > 0) {
			double occupancyRate = (double) bookedSeats / totalSeats;
			if (occupancyRate > 0.8) {
				finalPrice = finalPrice.multiply(BigDecimal.valueOf(1.3));
			}
		}

		return finalPrice.setScale(2, BigDecimal.ROUND_HALF_UP);
	}

//    public Flight getFlight(Long id) {
//        return flightRepo.findById(id)
//                .orElseThrow(() -> new ResourceNotFoundException("Flight with ID " + id + " not found."));
//    }
	public Flight getFlight(Long id) {
		return flightRepo.findFlightWithBookingsById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Flight with ID " + id + " not found."));
	}

	public Flight saveFlight(Flight flight) {
		return flightRepo.save(flight);
	}

	public void deleteFlight(Long id) {
		if (!flightRepo.existsById(id)) {
			throw new ResourceNotFoundException("Flight with ID " + id + " does not exist.");
		}
		flightRepo.deleteById(id);
	}
}
