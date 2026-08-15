package com.amagdy.gitstats;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class GitRunner {

    public String runGitLog(String repoPath) {
        ProcessBuilder pb = new ProcessBuilder(
                "git", "-C", repoPath, "log", "--numstat", "--pretty=format:%an");
        try {
            Process p = pb.start();
            String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String err = new String(p.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            int code = p.waitFor();
            if (code != 0) {
                throw new IllegalStateException(
                        "git log failed (exit " + code + "): " + err.strip());
            }
            return out;
        } catch (IOException e) {
            throw new IllegalStateException("git not found or not a repo: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while running git", e);
        }
    }
}