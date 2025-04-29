package com.ticket.moviebooking.repository;

import com.ticket.moviebooking.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, String> {
    @Query("SELECT DISTINCT s.startTime FROM Schedule s " +
            "WHERE s.startTime >= :startOfToday")
    List<LocalDateTime> getSchedules(@Param("startOfToday") LocalDateTime startOfToday);

    @Query("SELECT DISTINCT s.movie.id " +
            "FROM Schedule s " +
            "WHERE FUNCTION('DATE', s.startTime) = :targetDate")
    Set<String> getMovieByDate(@Param("targetDate") LocalDate targetDate);

    @Query("SELECT s FROM Schedule s " +
            "WHERE FUNCTION('DATE', s.startTime) = :targetDate " +
            "GROUP BY s.movie.id, s.id " +
            "ORDER BY s.startTime ASC")
    List<Schedule> findByStartDate(@Param("targetDate") LocalDate targetDate);
}
