package com.example.jwt.dto;

import java.util.List;

import javax.validation.constraints.NotNull;

//BookingDto.java
public class BookingDto {
	private Long flightId;
	private List<PassengerDto> passengers;

	// Getters and setters
	public Long getFlightId() {
		return flightId;
	}

	public void setFlightId(Long flightId) {
		this.flightId = flightId;
	}

	public List<PassengerDto> getPassengers() {
		return passengers;
	}

	public void setPassengers(List<PassengerDto> passengers) {
		this.passengers = passengers;
	}
}
