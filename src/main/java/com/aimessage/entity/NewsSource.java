package com.aimessage.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "news_sources")
public class NewsSource {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String url;
    
    @Column(name = "source_type")
    @Enumerated(EnumType.STRING)
    private SourceType sourceType;
    
    @Column(name = "category_mapping")
    private String categoryMapping;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "last_sync_time")
    private LocalDateTime lastSyncTime;
    
    @Column(name = "sync_count")
    private Integer syncCount = 0;
    
    @Column(name = "priority")
    private Integer priority = 5;
    
    public enum SourceType {
        RSS,
        API,
        TWITTER,
        ARXIV,
        GITHUB
    }
    
    public NewsSource() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public SourceType getSourceType() { return sourceType; }
    public void setSourceType(SourceType sourceType) { this.sourceType = sourceType; }
    
    public String getCategoryMapping() { return categoryMapping; }
    public void setCategoryMapping(String categoryMapping) { this.categoryMapping = categoryMapping; }
    
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
    
    public LocalDateTime getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(LocalDateTime lastSyncTime) { this.lastSyncTime = lastSyncTime; }
    
    public Integer getSyncCount() { return syncCount; }
    public void setSyncCount(Integer syncCount) { this.syncCount = syncCount; }
    
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
}
