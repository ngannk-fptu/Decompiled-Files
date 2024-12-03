/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.annotation.Nullable
 *  org.apache.commons.text.StringEscapeUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.guardrails.logs;

import com.atlassian.annotations.VisibleForTesting;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.commons.text.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomcatAccessLogsFinder {
    private static final Logger log = LoggerFactory.getLogger(TomcatAccessLogsFinder.class);
    private static final String LOGS_FOLDER = "logs";
    private static final String REGEX_YYYY_MM_DD = "\\d{4}-\\d{2}-\\d{2}";
    private static final Pattern DATE_PATTERN_REGEX = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final File tomcatDirectory;
    private final File accessLogsDirectory;
    private static final Pattern LOGS_PATTERN = Pattern.compile(".*pattern=\"([^\"]*)\".*");

    public TomcatAccessLogsFinder() {
        Optional<Path> tomcatPath = this.tomcatDir();
        this.tomcatDirectory = tomcatPath.map(Path::toFile).orElse(null);
        this.accessLogsDirectory = tomcatPath.map(dir -> dir.resolve(LOGS_FOLDER)).map(Path::toFile).filter(File::exists).orElse(null);
    }

    @Nullable
    public Map<LocalDate, File> listAccessLogFilesFromDaysAgo(LocalDate startDate) {
        if (this.accessLogsDirectory == null) {
            log.info("Tomcat log folder is not configured properly.");
            return null;
        }
        File[] logFiles = this.accessLogsDirectory.listFiles();
        if (logFiles == null) {
            log.info("Tomcat log folder is not configured properly.");
            return null;
        }
        ConcurrentHashMap<LocalDate, File> files = new ConcurrentHashMap<LocalDate, File>();
        for (File file : logFiles) {
            this.getDate(file).filter(date -> !date.isBefore(startDate)).ifPresent(date -> files.put((LocalDate)date, file));
        }
        Optional lastDate = files.keySet().stream().max(Comparator.naturalOrder());
        lastDate.ifPresent(files::remove);
        return files;
    }

    private Optional<LocalDate> getDate(File file) {
        return Optional.of(file.getName()).filter(name -> name.contains("access_log.")).map(DATE_PATTERN_REGEX::matcher).filter(Matcher::find).map(Matcher::group).map(DATE_TIME_FORMATTER::parse).map(LocalDate::from);
    }

    @VisibleForTesting
    Optional<File> getTomcatLogsDirectory() {
        return Optional.ofNullable(this.accessLogsDirectory);
    }

    private Optional<Path> tomcatDir() {
        return Stream.of("catalina.base", "catalina.home", "working.dir").map(System::getProperty).filter(Objects::nonNull).map(x$0 -> Paths.get(x$0, new String[0])).findFirst();
    }

    @Nullable
    public String getLogFormat() {
        try {
            if (this.tomcatDirectory == null) {
                return null;
            }
            Path serverFile = this.tomcatDirectory.toPath().resolve("conf").resolve("server.xml");
            if (!serverFile.toFile().exists()) {
                return null;
            }
            for (String line : Files.readAllLines(serverFile)) {
                String pattern = this.retrieveFormat(line);
                if (pattern == null) continue;
                return pattern;
            }
            return null;
        }
        catch (Exception e) {
            return null;
        }
    }

    protected String retrieveFormat(String line) {
        Matcher matcher = LOGS_PATTERN.matcher(line);
        return matcher.matches() ? StringEscapeUtils.unescapeXml((String)matcher.group(1)) : null;
    }
}

