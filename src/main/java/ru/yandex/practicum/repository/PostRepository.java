package ru.yandex.practicum.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("select count(p) from Post p where p.tags like :search")
    long getCountByTagsLike(@Param("search") String search);

    Page<Post> findAllByTagsContaining(String search, Pageable pageable);

}
