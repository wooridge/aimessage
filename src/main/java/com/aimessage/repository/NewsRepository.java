package com.aimessage.repository;

import com.aimessage.entity.News;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends JpaRepository<News, Long> {
    
    List<News> findByCategoryIdOrderByImportanceDesc(Long categoryId);
    
    List<News> findBySyncDateBetweenOrderByImportanceDesc(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT n FROM News n WHERE n.syncDate >= ?1 ORDER BY n.importance DESC, n.syncDate DESC")
    List<News> findTodayNews(LocalDateTime today);
    
    Optional<News> findByUrl(String url);
    
    boolean existsByUrl(String url);
    
    @Query("SELECT n FROM News n WHERE n.importance >= 8 ORDER BY n.syncDate DESC")
    List<News> findImportantNews();
}
