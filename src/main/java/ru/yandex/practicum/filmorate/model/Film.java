package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.exception.ValidationException;

import java.time.LocalDate;

/**
 * Film.
 */
@Data
public class Film {
    public static final int DESCRIPTION_MAX_LENGTH = 200;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    @EqualsAndHashCode.Exclude
    private Integer id;

    @NotBlank(message = "Название фильма должно быть указано")
    @NotEmpty(message = "Название фильма должно быть указано")
    private String name;

    @Size(max = DESCRIPTION_MAX_LENGTH, message = "Описание не может превышать {max} символов")
    @EqualsAndHashCode.Exclude
    private String description;

    private LocalDate releaseDate;

    private Integer duration;

    public boolean validate() {
        if ((getName() == null) || (getName().isBlank())) {
            throw new ValidationException("Название фильма должно быть указано");
        }
        if ((getDescription() != null) && (getDescription().length() > DESCRIPTION_MAX_LENGTH)) {
            throw new ValidationException(String.format("Описание не должно превышать %d символов", 200));
        }
        if ((getDuration() != null) && (getDuration() < 0)) {
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
        if ((getReleaseDate() != null) && (getReleaseDate().isBefore(MIN_RELEASE_DATE))) {
            throw new ValidationException(String.format("Дата релиза не может быть раньше %s", MIN_RELEASE_DATE));
        }
        return true;
    }

    public Film cloneFilm() {
        Film cloneFilm = new Film();
        cloneFilm.setId(getId());
        cloneFilm.setName(getName());
        cloneFilm.setDescription(getDescription());
        cloneFilm.setDuration(getDuration());
        cloneFilm.setReleaseDate(getReleaseDate());
        return cloneFilm;
    }
}
