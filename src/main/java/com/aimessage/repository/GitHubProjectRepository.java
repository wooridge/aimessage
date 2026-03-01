package com.aimessage.repository;

import com.aimessage.entity.GitHubProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GitHubProjectRepository extends JpaRepository<GitHubProject, Long> {
    
    List<GitHubProject> findBySyncDateBetweenOrderByStarsDesc(LocalDateTime start, LocalDateTime end);
    
    List<GitHubProject> findTop20ByOrderBySyncDateDesc();
    
    Optional<GitHubProject> findByUrl(String url);
    
    boolean existsByUrl(String url);
    
    List<GitHubProject> findByLanguageOrderByStarsDesc(String language);
}
