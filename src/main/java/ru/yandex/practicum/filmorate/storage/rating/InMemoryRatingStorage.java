package ru.yandex.practicum.filmorate.storage.rating;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@Repository("memoryRatingStorage")
public class InMemoryRatingStorage implements RatingStorage {
    private final HashMap<Integer, Rating> ratings = new HashMap<>();

    public InMemoryRatingStorage() {
        init();
    }

    private void init() {
        ratings.put(1, new Rating(1, "G"));
        ratings.put(2, new Rating(2, "PG"));
        ratings.put(3, new Rating(3, "PG-13"));
        ratings.put(4, new Rating(4, "R"));
        ratings.put(5, new Rating(5, "NC-17"));
    }

    @Override
    public Optional<Rating> getRatingById(Integer id) {
        if (ratings.containsKey(id)) {
            return Optional.of(ratings.get(id));
        }
        return Optional.empty();
    }

    @Override
    public Collection<Rating> findAll() {
        return ratings.values();
    }
}
