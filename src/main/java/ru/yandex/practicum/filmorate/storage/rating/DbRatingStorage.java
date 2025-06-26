package ru.yandex.practicum.filmorate.storage.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Repository("dbRatingStorage")
public class DbRatingStorage implements RatingStorage {
    @Override
    public Optional<Rating> getRatingById(Integer id) {
        return Optional.empty();
    }

    @Override
    public Collection<Rating> findAll() {
        return null;
    }
}
