package com.example.jwt.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.jwt.entity.Flight;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {
	List<Flight> findBySourceAndDestination(String source, String destination);

	@Query("SELECT f FROM Flight f LEFT JOIN FETCH f.bookings WHERE f.id = :id")
	Optional<Flight> findFlightWithBookingsById(Long id);

//	Object findByIdWithBookings(Long id);

}
