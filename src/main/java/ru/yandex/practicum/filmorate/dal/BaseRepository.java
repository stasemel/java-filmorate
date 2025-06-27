package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import ru.yandex.practicum.filmorate.exception.InternalServerException;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseRepository<T> {
    protected final JdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> findOne(String query, Object... params) {
        try {
            T result = jdbc.queryForObject(query, mapper, params);
            return Optional.ofNullable(result);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> findMany(String query, Object... params) {
        return jdbc.query(query, mapper, params);
    }

    protected boolean delete(String query, Object... params) {
        int rowsDeleted = jdbc.update(query, params);
        return rowsDeleted > 0;
    }

    protected void update(String query, Object... params) {
        int rowsUpdated = jdbc.update(query, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить данные");
        }
    }

    protected long insert(String query, Object... params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection
                    .prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            for (int idx = 0; idx < params.length; idx++) {
                ps.setObject(idx + 1, params[idx]);
            }
            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);

        // Возвращаем id нового пользователя
        if (id != null) {
            return id;
        } else {
            throw new InternalServerException("Не удалось сохранить данные");
        }
    }

    protected int countRows(String query, Object... params) {
        return jdbc.queryForObject(query, Integer.class, params);
    }

    protected String buildSQLSelect(String tableName) {
        StringBuilder query = new StringBuilder("SELECT * FROM ");
        query.append(tableName);
        return query.toString();
    }

    protected String buildSQLSelect(String tableName, String[] columns, String[] wheres, String[] orders) {
        StringBuilder query = new StringBuilder("SELECT ");
        query.append(String.join(", ", columns));
        query.append(" FROM ");
        query.append(tableName);
        if (wheres.length > 0) {
            query.append(" WHERE ");
            query.append(String.join(" AND ", wheres));
        }
        if (orders.length > 0) {
            query.append(" ORDER BY ");
            query.append(String.join(", ", orders));
        }
        return query.toString();
    }

    protected String buildSQLUpdate(String tableName, String[] fields, String[] wheres) {
        StringBuilder query = new StringBuilder("UPDATE ");
        query.append(tableName);
        query.append(" SET ");
        query.append(String.join(", ", fields));
        query.append(" WHERE ");
        query.append(String.join(" AND ", wheres));
        return query.toString();
    }

    protected String buildSQLDelete(String tableName, String[] wheres) {
        StringBuilder query = new StringBuilder("DELETE FROM ");
        query.append(tableName);
        query.append(" WHERE ");
        query.append(String.join(" AND ", wheres));
        return query.toString();
    }

    protected String buildSQLInsert(String tableName, String[] fields) {
        StringBuilder query = new StringBuilder("INSERT INTO ");
        query.append(tableName);
        query.append(" (");
        query.append(String.join(", ", fields));
        query.append(" ) VALUES (");
        String[] values = new String[fields.length];
        Arrays.fill(values, "?");
        query.append(String.join(", ", values));
        query.append(" )");
        return query.toString();
    }

    protected String buildSQLMerge(String tableName, String[] fields, String[] values) {
        StringBuilder query = new StringBuilder("MERGE INTO ");
        query.append(tableName);
        query.append(" key (");
        query.append(String.join(", ", fields));
        query.append(" ) VALUES (");
        query.append(String.join(", ", values));
        query.append(" )");
        return query.toString();
    }
}