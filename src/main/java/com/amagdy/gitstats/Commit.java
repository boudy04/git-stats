package com.amagdy.gitstats;

import java.util.List;

public record Commit(String author, List<FileChange> changes) {}