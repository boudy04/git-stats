package com.amagdy.gitstats;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GitRunner {

    public String runGitLog(String repoPath) {
        return run(repoPath, "log", "--numstat", "--date=short", "--pretty=format:%an|%ad");
    }

    public int countMerges(String repoPath) {
        return countLines(run(repoPath, "log", "--merges", "--oneline"));
    }

    public int countBranches(String repoPath) {
        return countLines(run(repoPath, "branch", "-a"));
    }

    public int countTags(String repoPath) {
        return countLines(run(repoPath, "tag"));
    }

    private String run(String repoPath, String... args) {
        List<String> cmd = new java.util.ArrayList<>(List.of("git", "-C", repoPath));
        cmd.addAll(Arrays.asList(args));
        ProcessBuilder pb = new ProcessBuilder(cmd);
        try {
            Process p = pb.start();
            String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            String err = new String(p.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            int code = p.waitFor();
            if (code != 0) {
                throw new IllegalStateException(
                        "git " + args[0] + " failed (exit " + code + "): " + err.strip());
            }
            return out;
        } catch (IOException e) {
            throw new IllegalStateException("git not found or not a repo: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("interrupted while running git", e);
        }
    }

    private int countLines(String out) {
        return out.isBlank() ? 0 : (int) out.lines().count();
    }
}