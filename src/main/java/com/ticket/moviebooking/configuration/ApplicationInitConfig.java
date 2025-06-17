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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.client.RestTemplate;

import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
            log.info("Data seeding started.");

            if (movieRepository.count() == 0 ) {
               importMoviesFromCSV(); //resources/data/movie.csv
               log.info("import movie data from data/movie.csv");
            }

            if (roomRepository.count() == 0) {
                importRoomsFromCSV(); //resources/data/room.csv
                log.info("import room data from data/room.csv");
            }

            if (seatRepository.count() == 0) {
                log.info("init seat data by room data");
                roomRepository.findAll()
                        .forEach(room -> initSeatData(room.getId()));
            }

            if (scheduleRepository.count() == 0) {
                log.info("init schedule data because scheduleRepository.count() == 0");
                LocalDate today = LocalDate.now();
                for(int i = 0; i < 7; i++) {
                    initScheduleData(today.plusDays(i));
                }
            } else {
                LocalDateTime maxStartTime = scheduleRepository.findMaxStartTime();
                LocalDate maxDate = maxStartTime.toLocalDate();
                LocalDate today = LocalDate.now();

                long daysBetween = ChronoUnit.DAYS.between(today, maxDate);

                if (daysBetween < 6) {
                    log.info("init schedule data because existing schedule only covers {} days", daysBetween + 1);
                    for (int i = 1; i <= (6 - daysBetween); i++) {
                        initScheduleData(maxDate.plusDays(i));
                    }
                }
            }

            log.info("Data seeding completed.");
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

    private void initScheduleData(LocalDate date) {
        List<Room> rooms = roomRepository.findAll();
        List<Movie> movies = movieRepository.findAll();

        if (rooms.isEmpty() || movies.isEmpty()) {
            log.warn("No room or movie available to init schedule.");
            return;
        }

        List<LocalTime> timeSlots = Arrays.asList(
                LocalTime.of(8, 30),
                LocalTime.of(10, 0),
                LocalTime.of(12, 30),
                LocalTime.of(15, 0),
                LocalTime.of(17, 30),
                LocalTime.of(20, 0),
                LocalTime.of(21, 30)
        );

        Random random = new Random();
        int base = Math.min(5, movies.size());
        int numberMovie = base + random.nextInt(movies.size() - base + 1);

        // Dùng Set để đánh dấu movie và room đã được sử dụng
        Set<String> usedRoomIds = new HashSet<>();
        Set<String> usedMovieIds = new HashSet<>();

        for(int i = 0; i < numberMovie; i++) {
            Room room = rooms.get(random.nextInt(rooms.size()));
            Movie movie = movies.get(random.nextInt(movies.size()));

            // Kiểm tra xem room và movie đã được sử dụng chưa
            if( usedRoomIds.contains(room.getId()) || usedMovieIds.contains(movie.getId())) {
                continue; // Bỏ qua nếu đã sử dụng
            }

            for (LocalTime time : timeSlots) {
                if( random.nextBoolean() ) {
                    continue;
                }
                LocalDateTime startTime = LocalDateTime.of(date, time);

                Schedule schedule = new Schedule();
                schedule.setStartTime(startTime);
                schedule.setRoom(room);
                schedule.setMovie(movie);

                try {
                    scheduleRepository.save(schedule);
                } catch (DataIntegrityViolationException | AppException e) {
                    log.error("Failed to save schedule: {} | Room ID: {}, Movie ID: {}",
                            e.getMessage(), room.getId(), movie.getId());
                }
            }
            usedRoomIds.add(room.getId());
            usedMovieIds.add(movie.getId());
        }
    }

    private void importMoviesFromCSV() throws Exception {
        var resource = new ClassPathResource("data/movie.csv");
        var reader = new InputStreamReader(resource.getInputStream());

        List<Movie> movies = new ArrayList<>();
        try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                Movie movie = new Movie();
                movie.setTitle(record.get("title"));
                movie.setTitleEnglish(record.get("title_english"));
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
                movie.setBackgroundUrl(record.get("background_url"));
                movie.setAgeRating(
                        AgeRating.valueOf(record.get("age_rating").toUpperCase(Locale.ROOT))
                );
                movie.setCategory(record.get("category"));
                movies.add(movie);
            }
        }

        movieRepository.saveAll(movies);
    }

    private void importRoomsFromCSV() throws Exception {
        var resource = new ClassPathResource("data/room.csv");
        var reader = new InputStreamReader(resource.getInputStream());

        List<Room> rooms = new ArrayList<>();
        try (CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT.withFirstRecordAsHeader())) {
            for (CSVRecord record : csvParser) {
                Room room = new Room();
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
