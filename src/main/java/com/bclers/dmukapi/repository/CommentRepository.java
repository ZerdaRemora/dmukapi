package com.bclers.dmukapi.repository;

import com.bclers.dmukapi.enums.NewsSource;
import com.bclers.dmukapi.model.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends CrudRepository<Comment, String>
{
    @Transactional
    Optional<Comment> findById(int id);
    Optional<Comment> findFirstByCommentSourceOrderByScoreDesc(NewsSource source);
    Optional<Comment> findFirstByCommentSourceOrderByScoreAsc(NewsSource source);
    Optional<Comment> findFirstByOrderByScoreAsc();
    List<Comment> findAllByCommentSource(NewsSource source);
    Optional<Comment> findByCommentSourceAndLocalSiteCmtID(NewsSource source, String localId);
    List<Comment> findAll(Pageable pageable);
}
