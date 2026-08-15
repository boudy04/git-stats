package com.amagdy.gitstats;

import java.util.List;

public record Commit(String author, String date, List<FileChange> changes) {}