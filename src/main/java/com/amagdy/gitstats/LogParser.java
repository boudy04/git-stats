package com.amagdy.gitstats;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

    private static final Pattern STAT = Pattern.compile("^(\\d+|-)\\t(\\d+|-)\\t(.*)$");
    private int malformed = 0;

    public int malformedCount() {
        return malformed;
    }

    public List<Commit> parse(String rawLog) {
        malformed = 0;
        List<Commit> commits = new ArrayList<>();
        if (rawLog == null || rawLog.isBlank()) {
            return commits;
        }
        Commit current = null;
        for (String line : rawLog.split("\n", -1)) {
            if (line.isEmpty()) {
                continue;
            }
            Matcher m = STAT.matcher(line);
            if (m.matches()) {
                if (current == null) {
                    malformed++;
                    continue;
                }
                int added = m.group(1).equals("-") ? 0 : Integer.parseInt(m.group(1));
                int deleted = m.group(2).equals("-") ? 0 : Integer.parseInt(m.group(2));
                current.changes().add(new FileChange(unquote(m.group(3)), added, deleted));
            } else if (isStatShaped(line)) {
                malformed++;
            } else {
                int bar = line.lastIndexOf('|');
                String author = bar >= 0 ? line.substring(0, bar) : line;
                String date = bar >= 0 ? line.substring(bar + 1) : "";
                current = new Commit(author.strip(), date.strip(), new ArrayList<>());
                commits.add(current);
            }
        }
        return commits;
    }

    private static boolean isStatShaped(String line) {
        char first = line.charAt(0);
        return (first == '-' || Character.isDigit(first)) && line.indexOf('\t') > 0;
    }

    private String unquote(String path) {
        if (path.length() >= 2 && path.startsWith("\"") && path.endsWith("\"")) {
            return path.substring(1, path.length() - 1)
                    .replace("\\t", "\t").replace("\\n", "\n").replace("\\\\", "\\");
        }
        return path;
    }
}