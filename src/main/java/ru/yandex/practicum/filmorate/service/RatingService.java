package ru.yandex.practicum.filmorate.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Rating;
import ru.yandex.practicum.filmorate.storage.rating.RatingStorage;

import java.util.Collection;
import java.util.Optional;

@Service
@Getter
@Slf4j
@RequiredArgsConstructor
public class RatingService {
    @Qualifier("dbRatingStorage")
    private final RatingStorage storage;

    public Collection<Rating> findAll() {
        return storage.findAll();
    }

    public Rating getRatingById(Integer id) {
        Optional<Rating> optionalRating = storage.getRatingById(id);
        if (optionalRating.isEmpty()) {
            throw new NotFoundException(String.format("Не найден рейтинг с id %d", id));
        }
        return optionalRating.get();
    }


}
