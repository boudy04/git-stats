package com.amagdy.gitstats.model;

public record FileChange(String path, int added, int deleted) {}