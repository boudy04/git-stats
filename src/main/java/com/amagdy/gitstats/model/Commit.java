package com.amagdy.gitstats.model;

import java.util.List;

public record Commit(String author, List<FileChange> changes) {}