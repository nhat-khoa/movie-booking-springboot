package com.ticket.moviebooking.configuration;

import com.ticket.moviebooking.entity.Movie;
import com.ticket.moviebooking.entity.Room;
import com.ticket.moviebooking.entity.Schedule;
import com.ticket.moviebooking.entity.Seat;
import com.ticket.moviebooking.enums.AgeRating;
import com.ticket.moviebooking.exception.AppException;
import com.ticket.moviebooking.exception.ErrorCode;
import com.ticket.moviebooking.repository.MovieRepository;
import com.ticket.moviebooking.repository.RoomRepository;
import com.ticket.moviebooking.repository.ScheduleRepository;
import com.ticket.moviebooking.repository.SeatRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.apache.commons.csv.CSVParser;
import org.springframework.web.client.RestTemplate;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    ScheduleRepository scheduleRepository;
    MovieRepository movieRepository;
    RoomRepository roomRepository;
    SeatRepository seatRepository;

    @Bean
    ApplicationRunner applicationRunner() {
        log.info("Initializing application.....");
        return args -> {
            log.info("Seeding test schedule data...");

            // Import movies from CSV file by dbeaver before run this code

//            scheduleRepository.deleteAll();
//            initScheduleData(
//                    "fc123dd7-5abe-4494-9eb5-d06226d6a4f5", // Room ID
//                    "38b8eede-0ac4-4265-9496-49d15a716b19"  // Movie ID
//            );
//            initScheduleData(
//                    "11120cde-ab97-4116-b64b-010beabf1d92", // Room ID
//                    "27ce06d3-4460-4953-87da-b6fcb007c164"  // Movie ID
//            );
//            initScheduleData(
//                    "ce3963d0-19c7-4a7f-a6bd-129f968cf743", // Room ID
//                    "d78c8577-2358-4caf-b5c9-73b191b8c92e"  // Movie ID
//            );
//            initScheduleData(
//                    "2df721c7-3922-4093-808f-0263ffed3c9d", // Room ID
//                    "710af9c9-408f-4ea9-bc61-257e751fff83"  // Movie ID
//            );

            log.info("seatRepository.count() = {}",seatRepository.count());
            if (seatRepository.count() == 0) {
                roomRepository.findAll()
                        .forEach(room -> initSeatData(room.getId()));
            }

            log.info("Seeding completed.");
        };
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    private void initSeatData(String roomId) {
        for (int i = 0; i < 12; i++) {
            char line = (char) ('A' + i);
            for (int number = 1; number <= 12; number++) {
                Seat seat = new Seat();
                seat.setRoom(roomRepository
                        .findById(roomId)
                        .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED)));
                seat.setLine(String.valueOf(line));
                seat.setNumber(number);
                seat.setIsActive(true);
                seat.setIsDoubleSeat(false);
                seatRepository.save(seat);
            }
        }
        // Dòng thứ 13 (line = 'M'), 6 ghế đôi
        char doubleSeatLine = 'M';
        for (int number = 1; number <= 6; number++) {
            Seat seat = new Seat();
            seat.setRoom(roomRepository
                    .findById(roomId)
                    .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED)));
            seat.setLine(String.valueOf(doubleSeatLine));
            seat.setNumber(number);
            seat.setIsActive(true);
            seat.setIsDoubleSeat(true);
            seatRepository.save(seat);
        }
    }

    private void initScheduleData(String roomId, String movieId) {
        LocalDateTime startTime;
        for (int i = 0; i < 7; i++) {
            startTime = LocalDateTime.of(
                    LocalDate.now().plusDays(i),
                    LocalTime.of(8, 30)
            );
            for (int j = 0; j < 6; j++) {
                try {
                    Schedule schedule = new Schedule();
                    schedule.setStartTime(startTime);
                    schedule.setRoom(roomRepository
                            .findById(roomId)
                            .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_EXISTED)));
                    schedule.setMovie(movieRepository
                            .findById(movieId)
                            .orElseThrow(() -> new AppException(ErrorCode.MOVIE_NOT_EXISTED)));
                    scheduleRepository.save(schedule);
                } catch (AppException e) {
                    log.error("Failed to save schedule: {} | Room ID: {}, Movie ID: {}",
                            e.getErrorCode(), roomId, movieId);
                }
                startTime = startTime.plusHours(2).plusMinutes(30);
            }
        }
    }

    private void importMoviesFromCSV(String filePath) throws Exception {
        var resource = new ClassPathResource(filePath);
        var reader = new InputStreamReader(resource.getInputStream());

        List<Movie> movies = new ArrayList<>();
        try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                Movie movie = new Movie();
                movie.setId(record.get("id"));
                movie.setTitle(record.get("title"));
                movie.setTitle(record.get("title_english"));
                movie.setDescription(record.get("description"));
                movie.setReleaseDate(LocalDate.parse(record.get("release_date")));

                String durationStr = record.get("duration");
                if (durationStr == null || durationStr.isBlank()) {
                    movie.setDuration(null);
                } else {
                    movie.setDuration(Integer.parseInt(durationStr));
                }

                movie.setLanguage(record.get("language"));
                movie.setDirector(record.get("director"));
                movie.setCast(record.get("cast"));
                movie.setPosterUrl(record.get("poster_url"));
                movie.setTrailerVideoUrl(record.get("trailer_video_url"));
                movie.setAgeRating(
                        AgeRating.valueOf(record.get("age_rating").toUpperCase(Locale.ROOT))
                );
                movie.setCategory(record.get("category"));
                movies.add(movie);
            }
        }

        movieRepository.saveAll(movies);
    }

    private void importRoomsFromCSV(String filePath) throws Exception {
        var resource = new ClassPathResource(filePath);
        var reader = new InputStreamReader(resource.getInputStream());

        List<Room> rooms = new ArrayList<>();
        try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                Room room = new Room();
                room.setId(record.get("id"));
                room.setName(record.get("name"));
                room.setTotalSeats(Integer.parseInt(record.get("total_seats")));
                room.setSingleSeats(Integer.parseInt(record.get("single_seats")));
                room.setDoubleSeats(Integer.parseInt(record.get("double_seats")));
                room.setDescription(record.get("description"));
                rooms.add(room);
            }
        }

        roomRepository.saveAll(rooms);
    }
}
