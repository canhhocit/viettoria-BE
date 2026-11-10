package com.viettoria.dto;

import java.util.Map;

public class AudioGuideDto {
    private String title;
    private String duration;
    private String audioUrl;
    private String narrator;
    private String transcript;
    private Map<String, String> audioUrls;

    public AudioGuideDto() {}

    public AudioGuideDto(String title, String duration, String audioUrl, String narrator, String transcript) {
        this.title = title;
        this.duration = duration;
        this.audioUrl = audioUrl;
        this.narrator = narrator;
        this.transcript = transcript;
    }

    public AudioGuideDto(String title, String duration, String audioUrl, String narrator, String transcript, Map<String, String> audioUrls) {
        this.title = title;
        this.duration = duration;
        this.audioUrl = audioUrl;
        this.narrator = narrator;
        this.transcript = transcript;
        this.audioUrls = audioUrls;
    }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getAudioUrl() { return audioUrl; }
    public void setAudioUrl(String audioUrl) { this.audioUrl = audioUrl; }

    public String getNarrator() { return narrator; }
    public void setNarrator(String narrator) { this.narrator = narrator; }

    public String getTranscript() { return transcript; }
    public void setTranscript(String transcript) { this.transcript = transcript; }

    public Map<String, String> getAudioUrls() { return audioUrls; }
    public void setAudioUrls(Map<String, String> audioUrls) { this.audioUrls = audioUrls; }
}
