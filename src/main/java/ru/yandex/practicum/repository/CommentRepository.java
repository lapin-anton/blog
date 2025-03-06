package ru.yandex.practicum.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Comment;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepository {

    private final JdbcTemplate jdbcTemplate;


    public List<Comment> findAllCommentsByPostId(Long postId) {
        return jdbcTemplate.query("select id, post_id, text from comment where post_id=" + postId,
                (rs, rowNum) -> new Comment(
                        rs.getLong("id"),
                        rs.getLong("post_id"),
                        rs.getString("text")
                ));
    }

    public void deleteCommentsByPostId(Long postId) {
        jdbcTemplate.update("delete from comment where post_id = ?", postId);
    }

    public void addComment(Long postId, String text) {
        jdbcTemplate.update(con -> {
            PreparedStatement statement = con.prepareStatement(
                    "insert into comment (post_id, text) values (?, ?)");
            statement.setLong(1, postId);
            statement.setString(2, text);
            return statement;
        });
    }
}
