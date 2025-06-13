package com.ticket.moviebooking.service;

import com.ticket.moviebooking.dto.request.TicketRequest;
import com.ticket.moviebooking.dto.response.SeatResponse;
import com.ticket.moviebooking.dto.response.TicketResponse;
import com.ticket.moviebooking.entity.*;
        import com.ticket.moviebooking.exception.AppException;
import com.ticket.moviebooking.exception.ErrorCode;
import com.ticket.moviebooking.mapper.SeatMapper;
import com.ticket.moviebooking.mapper.TicketMapper;
import com.ticket.moviebooking.repository.ScheduleRepository;
import com.ticket.moviebooking.repository.SeatRepository;
import com.ticket.moviebooking.repository.TicketRepository;
import com.ticket.moviebooking.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TicketService {
    TicketRepository ticketRepository;
    ScheduleRepository scheduleRepository;
    UserRepository userRepository;
    SeatRepository seatRepository;
    TicketMapper ticketMapper;

    public Boolean existsByScheduleIdAndSeatId(String scheduleId, String seatId){
        return ticketRepository.existsByScheduleIdAndSeatId(scheduleId, seatId);
    }

    public TicketResponse createTicket(TicketRequest request) {

        Schedule schedule = scheduleRepository.findById(request.getScheduleId())
                .orElseThrow(() -> new AppException(ErrorCode.SCHEDULE_NOT_EXISTED));
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        // Tạo Ticket
        Ticket ticket = new Ticket();
        ticket.setBookedAt(LocalDateTime.now());
        ticket.setTotalPrice(request.getTotalPrice());
        ticket.setSchedule(schedule);
        ticket.setUser(user);

        // Lấy danh sách ghế
        List<Seat> seats = seatRepository.findAllById(request.getSeatIds());

        log.info("Seats count: {}", (long) seats.size());
        // Tạo TicketSeat cho từng ghế
        for (Seat seat : seats) {
            log.info("SeatId: {}", seat.getId());
            TicketSeat ts = new TicketSeat();
            ts.setTicket(ticket);
            ts.setSeat(seat);
            ticket.getTicketSeats().add(ts);
        }

        // Lưu Ticket (các TicketSeat sẽ tự lưu nhờ cascade)
        return ticketMapper.toTicketResponse(ticketRepository.save(ticket));
    }

    public List<TicketResponse> getTicketByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<Ticket> tickets = ticketRepository.findAllByUser(user, Sort.by("bookedAt").descending());
        return tickets.stream()
                .map(ticketMapper::toTicketResponse)
                .toList();
    }
}
