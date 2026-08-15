package com.amagdy.gitstats;

import com.amagdy.gitstats.model.Commit;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LogParserTest {

    private final LogParser parser = new LogParser();

    @Test
    void parsesTwoCommits() {
        String log = """
                Alice
                3\t1\tsrc/A.java
                0\t5\tsrc/B.java
                Bob
                10\t0\tdocs/readme.md
                """;
        List<Commit> commits = parser.parse(log);
        assertEquals(2, commits.size());
        assertEquals("Alice", commits.get(0).author());
        assertEquals(2, commits.get(0).changes().size());
        assertEquals(3, commits.get(0).changes().get(0).added());
        assertEquals(5, commits.get(0).changes().get(1).deleted());
        assertEquals("Bob", commits.get(1).author());
        assertEquals(1, commits.get(1).changes().size());
    }

    @Test
    void handlesBinaryFilesAsZero() {
        String log = """
                Alice
                -\t-\timage.png
                """;
        List<Commit> commits = parser.parse(log);
        assertEquals(1, commits.size());
        assertEquals(0, commits.get(0).changes().get(0).added());
        assertEquals(0, commits.get(0).changes().get(0).deleted());
    }

    @Test
    void unquotesFilenamesWithSpaces() {
        String log = """
                Alice
                1\t1\t"my file.txt"
                """;
        List<Commit> commits = parser.parse(log);
        assertEquals("my file.txt", commits.get(0).changes().get(0).path());
    }

    @Test
    void skipsMalformedLinesAndCountsThem() {
        String log = """
                Alice
                12\tX\tfile.txt
                1\t1\ta.txt
                """;
        List<Commit> commits = parser.parse(log);
        assertEquals(1, commits.size());
        assertEquals("Alice", commits.get(0).author());
        assertEquals(1, commits.get(0).changes().size());
        assertEquals("a.txt", commits.get(0).changes().get(0).path());
        assertEquals(1, parser.malformedCount());
    }

    @Test
    void emptyLogYieldsNoCommits() {
        assertEquals(0, parser.parse("").size());
    }
}