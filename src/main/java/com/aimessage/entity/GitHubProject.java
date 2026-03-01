package com.aimessage.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "github_projects")
public class GitHubProject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String url;

    @Column(name = "stars")
    private Integer stars;

    @Column(name = "forks")
    private Integer forks;

    @Column(name = "language")
    private String language;

    @Column(name = "owner")
    private String owner;

    @Column(name = "trending_date")
    private LocalDateTime trendingDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "sync_date")
    private LocalDateTime syncDate;

    public GitHubProject() {}

    @PrePersist
    protected void onCreate() { createdAt = LocalDateTime.now(); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    public Integer getStars() { return stars; }
    public void setStars(Integer stars) { this.stars = stars; }
    public Integer getForks() { return forks; }
    public void setForks(Integer forks) { this.forks = forks; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public String getOwner() { return owner; }
    public void setOwner(String owner) { this.owner = owner; }
    public LocalDateTime getTrendingDate() { return trendingDate; }
    public void setTrendingDate(LocalDateTime trendingDate) { this.trendingDate = trendingDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getSyncDate() { return syncDate; }
    public void setSyncDate(LocalDateTime syncDate) { this.syncDate = syncDate; }
}
