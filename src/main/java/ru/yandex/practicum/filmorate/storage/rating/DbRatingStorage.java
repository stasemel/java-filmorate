package ru.yandex.practicum.filmorate.storage.rating;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.RatingRepository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository("dbRatingStorage")
@RequiredArgsConstructor
@Getter
public class DbRatingStorage implements RatingStorage {
    private final RatingRepository ratingRepository;

    @Override
    public Optional<Rating> getRatingById(Integer id) {
        return ratingRepository.getRaingsById(id);
    }

    @Override
    public Collection<Rating> findAll() {
        return ratingRepository.getAll();
    }
}
