package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.Objects;

@Data
public class Friend {
    private final Long userId;

    private final Long friendId;

    private Boolean confirm = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Friend friend = (Friend) o;
        return (Objects.equals(userId, friend.userId) && Objects.equals(friendId, friend.friendId))
                || (Objects.equals(userId, friend.friendId) && Objects.equals(friendId, friend.userId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, friendId);
    }
}
