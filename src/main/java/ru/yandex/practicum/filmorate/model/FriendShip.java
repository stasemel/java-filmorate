package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.util.Objects;

@Data
public class FriendShip {
    private final Long userId;

    private final Long friendId;

    private Boolean confirm = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendShip friendShip = (FriendShip) o;
        return (Objects.equals(userId, friendShip.userId) && Objects.equals(friendId, friendShip.friendId))
                || (Objects.equals(userId, friendShip.friendId) && Objects.equals(friendId, friendShip.userId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, friendId);
    }
}
