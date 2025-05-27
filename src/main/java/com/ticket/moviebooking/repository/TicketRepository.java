package com.ticket.moviebooking.repository;

import com.ticket.moviebooking.entity.Ticket;
import com.ticket.moviebooking.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, String> {
    @Query("""
    SELECT CASE WHEN COUNT(ts) > 0 THEN TRUE ELSE FALSE END
    FROM TicketSeat ts
    WHERE ts.ticket.schedule.id = :scheduleId AND ts.seat.id = :seatId
    """)
    Boolean existsByScheduleIdAndSeatId(@Param("scheduleId") String scheduleId,
                                        @Param("seatId") String seatId);

    List<Ticket> findAllByUser(User user, Sort sort);
}
