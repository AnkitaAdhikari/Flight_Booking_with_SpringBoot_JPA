package com.example.jwt.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.jwt.entity.Booking;
import com.example.jwt.entity.Flight;
import com.example.jwt.entity.User;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
	List<Booking> findByUser(User user);

	int countByFlight(Flight flight);

	boolean existsByFlightAndSeatNo(Flight flight, String seatNo);
}
