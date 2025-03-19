package ru.yandex.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Post;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class PostRepository {

    private final JdbcTemplate jdbcTemplate;

    public List<Post> findAll(String search, int pageNumber, int pageSize) {
        List<Post> result;
        var offset = pageSize * (pageNumber - 1);
        RowMapper<Post> postRowMapper = (rs, rwNum) -> new Post(
                rs.getLong("id"),
                rs.getString("title"),
                rs.getBytes("image"),
                rs.getString("text"),
                rs.getString("tags"),
                rs.getInt("likes_count"));
        if ("".equals(search)) {
            result = jdbcTemplate.query(
                    "select id, title, image, text, tags, likes_count from post limit ? offset ?",
                    postRowMapper,
                    pageSize,
                    offset
            );
        } else {
            result = jdbcTemplate.query(
                    "select id, title, image, text, tags, likes_count from post where tags like ? limit ? offset ?",
                    postRowMapper,
                    "%" + search + "%",
                    pageSize,
                    offset
            );
        }
        return result;
    }

    public long savePost(String title, byte[] image, String tags, String text) {
        GeneratedKeyHolder holder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(
                    "insert into post (title, image, text, tags) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            statement.setString(1, title);
            statement.setBytes(2, image);
            statement.setString(3, text);
            statement.setString(4, tags);
            return statement;
        }, holder);

        return (Long) holder.getKeys().get("id");
    }

    public void updatePost(Post post) {
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement("update post set title = ?, image = ?, text = ?, tags = ?, likes_count = ? where id = ?");
            statement.setString(1, post.getTitle());
            statement.setBytes(2, post.getImage());
            statement.setString(3, post.getText());
            statement.setString(4, post.getTagsAsText());
            statement.setInt(5, post.getLikesCount());
            statement.setLong(6, post.getId());
            return statement;
        });
    }

    public Post findById(Long postId) throws Exception {
        return jdbcTemplate.query("select id, title, image, text, tags, likes_count from post where id=?",
                (rs, rowNum) -> new Post(
                     rs.getLong("id"),
                     rs.getString("title"),
                     rs.getBytes("image"),
                     rs.getString("text"),
                     rs.getString("tags"),
                     rs.getInt("likes_count")
                ), postId).stream().findFirst().orElseThrow();
    }

    public int getPostCount(String search) {
        int postCount;
        if ("".equals(search)) {
            postCount = jdbcTemplate.queryForObject("select count(1) as count from post", Integer.class);
        } else {
            postCount = jdbcTemplate.queryForObject("select count(1) as count from post where tags like ?",
                    Integer.class,
                    "%" + search + "%"
            );
        }
        return postCount;
    }

    public void delete(Long postId) {
        jdbcTemplate.update("delete from post where id = ?", postId);
    }
}
