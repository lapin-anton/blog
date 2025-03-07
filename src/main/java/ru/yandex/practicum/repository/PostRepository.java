package ru.yandex.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
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
        var offset = pageSize * (pageNumber - 1);
        var query = "".equals(search) ? "select id, title, image, text, tags, likes_count from post limit ? offset ?"
                : "select id, title, image, text, tags, likes_count from post where tags like '%" + search + "%' limit ? offset ?";
        return jdbcTemplate.query(query,
                (rs, rowNum) -> new Post(
                        rs.getLong("id"),
                        rs.getString("title"),
                        rs.getBytes("image"),
                        rs.getString("text"),
                        rs.getString("tags"),
                        rs.getInt("likes_count")
                ), pageSize, offset);
    }

    public long saveNewPost(String title, byte[] image, String tags, String text) {
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
            PreparedStatement statement = con.prepareStatement("update post set title = ?, text = ?, tags = ?, likes_count = ? where id = ?");
            statement.setString(1, post.getTitle());
            statement.setString(2, post.getText());
            statement.setString(3, post.getTagsAsText());
            statement.setInt(4, post.getLikesCount());
            statement.setLong(5, post.getId());
            return statement;
        });
    }

    public Post findById(Long postId) {
        return jdbcTemplate.query("select id, title, image, text, tags, likes_count from post where id=?",
                (rs, rowNum) -> new Post(
                     rs.getLong("id"),
                     rs.getString("title"),
                     rs.getBytes("image"),
                     rs.getString("text"),
                     rs.getString("tags"),
                     rs.getInt("likes_count")
                ), postId).get(0);
    }

    public int getPostCount(String search) {
        var query = "".equals(search) ? "select count(1) as count from post" : "select count(1) as count from post where tags like '%" + search + "%'";
        return jdbcTemplate.queryForObject(query, Integer.class);
    }

    public void delete(Long postId) {
        jdbcTemplate.update("delete from post where id = ?", postId);
    }
}
