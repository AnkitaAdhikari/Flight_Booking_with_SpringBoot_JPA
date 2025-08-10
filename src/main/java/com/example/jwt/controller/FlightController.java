package com.example.jwt.controller;

import java.math.BigDecimal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.example.jwt.dto.FlightDto;
import com.example.jwt.entity.Flight;
import com.example.jwt.service.FlightService;

@RestController
@RequestMapping("/api/flights")
public class FlightController {

    @Autowired
    private FlightService flightService;

    // ✅ 1. Search flights by source and destination
    @GetMapping("/search")
    public List<Flight> searchFlights(@RequestParam String source, @RequestParam String destination) {
        return flightService.searchFlights(source, destination);
    }

    // ✅ 2. Get dynamic price by flight ID
    @GetMapping("/{id}/price")
    public ResponseEntity<BigDecimal> getPrice(@PathVariable Long id) {
        Flight flight = flightService.getFlight(id);
        if (flight == null) {
            return ResponseEntity.notFound().build();
        }
        BigDecimal price = flightService.calculatePrice(flight);
        return ResponseEntity.ok(price);
    }

    // ✅ 3. Add new flight (ADMIN only) - using DTO
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Flight> addFlight(@Valid @RequestBody FlightDto flightDto) {
        Flight flight = new Flight();
        flight.setFlightNumber(flightDto.getFlightNumber());
        flight.setSource(flightDto.getSource());
        flight.setDestination(flightDto.getDestination());
        flight.setDepartureTime(flightDto.getDepartureTime());
        flight.setArrivalTime(flightDto.getArrivalTime());
        flight.setTotalSeats(flightDto.getTotalSeats());
        flight.setPrice(BigDecimal.valueOf(flightDto.getPrice())); // assuming price is int in DTO

        Flight savedFlight = flightService.saveFlight(flight);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFlight);
    }

    // ✅ 4. Delete flight by ID (ADMIN only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteFlight(@PathVariable Long id) {
        flightService.deleteFlight(id);
        return ResponseEntity.ok("Flight deleted successfully");
    }
}
