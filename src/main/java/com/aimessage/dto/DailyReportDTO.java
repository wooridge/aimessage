package com.aimessage.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class DailyReportDTO {
    private LocalDate date;
    private String title;
    private List<NewsDTO> highlights;
    private Map<String, List<NewsDTO>> categorizedNews;
    private int totalNewsCount;
    private String lastSyncTime;

    public DailyReportDTO() {}

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public List<NewsDTO> getHighlights() { return highlights; }
    public void setHighlights(List<NewsDTO> highlights) { this.highlights = highlights; }
    public Map<String, List<NewsDTO>> getCategorizedNews() { return categorizedNews; }
    public void setCategorizedNews(Map<String, List<NewsDTO>> categorizedNews) { this.categorizedNews = categorizedNews; }
    public int getTotalNewsCount() { return totalNewsCount; }
    public void setTotalNewsCount(int totalNewsCount) { this.totalNewsCount = totalNewsCount; }
    public String getLastSyncTime() { return lastSyncTime; }
    public void setLastSyncTime(String lastSyncTime) { this.lastSyncTime = lastSyncTime; }
}
