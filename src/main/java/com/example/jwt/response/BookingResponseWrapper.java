package com.example.jwt.response;

import java.util.List;

public class BookingResponseWrapper {

	private double totalPrice;
	private List<BookingResponseDto> bookings;

	public BookingResponseWrapper(double totalPrice, List<BookingResponseDto> bookings) {
		this.totalPrice = totalPrice;
		this.bookings = bookings;
	}

	public double getTotalPrice() {
		return totalPrice;
	}

	public List<BookingResponseDto> getBookings() {
		return bookings;
	}
}
