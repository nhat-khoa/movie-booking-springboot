package com.ticket.moviebooking.repository;

import com.ticket.moviebooking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MovieRepository extends JpaRepository<Movie, String> {
    @Query("SELECT m FROM Movie m WHERE m.id NOT IN (SELECT s.movie.id FROM Schedule s)")
    List<Movie> findMoviesNotInSchedule();

    @Query("SELECT m FROM Movie m WHERE m.id IN (SELECT s.movie.id FROM Schedule s)")
    List<Movie> findMoviesInSchedule();
}
