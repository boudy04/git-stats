package com.amagdy.gitstats;

import com.amagdy.gitstats.model.Commit;
import com.amagdy.gitstats.model.FileChange;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogParser {

    private static final Pattern STAT = Pattern.compile("^(\\d+|-)\\t(\\d+|-)\\t(.*)$");
    private static final Pattern AUTHOR = Pattern.compile("^[A-Za-z0-9][A-Za-z0-9 .'\\[\\]]*$");
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
            } else if (AUTHOR.matcher(line.strip()).matches()) {
                current = new Commit(line.strip(), new ArrayList<>());
                commits.add(current);
            } else {
                malformed++;
            }
        }
        return commits;
    }

    private String unquote(String path) {
        if (path.length() >= 2 && path.startsWith("\"") && path.endsWith("\"")) {
            return path.substring(1, path.length() - 1)
                    .replace("\\t", "\t").replace("\\n", "\n").replace("\\\\", "\\");
        }
        return path;
    }
}