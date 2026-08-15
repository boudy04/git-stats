package com.amagdy.gitstats;

public record FileChange(String path, int added, int deleted) {}