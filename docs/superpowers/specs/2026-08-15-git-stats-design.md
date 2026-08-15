# git-stats — Java CLI Design

> Project #2 of the CV Improvement Plan. Analyzes a git repo's history and
> prints author + file churn stats. Closes gaps G1 (shipped project),
> G4 (tooling), M2/M3 (systems/tooling depth) from CV-PLAN.md.

## Goal

A single-purpose Java CLI that runs `git log` on a repository, parses the
output, and reports:

- `authors` — commits per author (count + % of total)
- `files` — top files by churn (commits touching them, lines added/removed)

## Stack

- Java 21 (Temurin JDK, installed)
- Maven 3.9.11 (build + fat-jar packaging)
- picocli 4.x (CLI framework)
- JUnit 5 (tests)
- Zero other dependencies. `git` is required at runtime (invoked via subprocess).

## Architecture

Package `com.amagdy.gitstats`, one responsibility per class:

```
src/main/java/com/amagdy/gitstats/
  App.java              — picocli entry point
  GitRunner.java        — ProcessBuilder wrapper: runs `git log --numstat`
  LogParser.java        — parses git output into Commit records
  StatsAggregator.java  — builds AuthorStats / FileStats summaries
  model/
    Commit.java         — author, files (name, added, deleted)
  AuthorsCommand.java   — picocli subcommand, prints author table
  FilesCommand.java     — picocli subcommand, prints file churn table
```

`git log` invocation (single pass, parseable):

```
git log --numstat --pretty=format:%an
```

- Each commit: author name line, then `+\t-\tfile` lines per file.
- Empty stats (`-\t-\tfile`) = binary file, treated as 0/0.
- File names with spaces/quotes need handling (git quotes them) — parser
  unquotes.

## CLI surface

```
git-stats authors [<path>]   # default: current dir
git-stats files   [<path>]   # files sorted by commit count desc
```

Output is a plain aligned text table. No colors, no interactive modes.

## Error handling

- `git` not found / not a repo → clear message + exit code 1.
- Empty history → "No commits found." + exit code 1.
- Malformed lines → skipped with a warning count shown at the end.

## Testing (JUnit 5)

Min 5 tests across the two pure-logic classes:

- `LogParserTest` — parses 2-commit sample, handles binary files, handles
  quoted filenames, ignores malformed lines.
- `StatsAggregatorTest` — author ordering/percentages, file churn sums.

CLI + subprocess stay thin and untested (integration smoke-test manually).

## Definition of done (per CV-PLAN Part F)

- [ ] Public GitHub repo, pinned
- [ ] README.md: what/stack/how-to-run/screenshot/≥5 test commands
- [ ] Tests passing
- [ ] `.github/workflows/ci.yml` runs `mvn test` on push
- [ ] Fat jar built via `mvn package`; usable standalone