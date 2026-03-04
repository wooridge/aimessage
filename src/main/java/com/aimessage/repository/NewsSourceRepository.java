package com.aimessage.repository;

import com.aimessage.entity.NewsSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsSourceRepository extends JpaRepository<NewsSource, Long> {
    List<NewsSource> findByIsActiveTrue();
    List<NewsSource> findBySourceType(NewsSource.SourceType sourceType);
}
