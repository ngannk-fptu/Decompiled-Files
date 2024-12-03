/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.xwork.HttpMethod
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  lombok.Generated
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.migration.agent.service.guardrails.logs;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.service.guardrails.logs.PageType;
import com.atlassian.migration.agent.service.guardrails.logs.PageTypeProvider;
import com.atlassian.xwork.HttpMethod;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.io.File;
import java.nio.file.Files;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;
import lombok.Generated;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TomcatAccessLogParser {
    private static final Logger log = LoggerFactory.getLogger(TomcatAccessLogParser.class);
    private static final Map<String, Integer> SPACES_PER_PATTERN = ImmutableMap.of((Object)"%r", (Object)2, (Object)"%t", (Object)2);
    private static final ImmutableSet<String> USER_PATTERNS = ImmutableSet.of((Object)"%u", (Object)"%{X-AUSERNAME}o");
    private static final String DEFAULT_PATTERN = "%t %{X-AUSERNAME}o %I %h %r %s %Dms %b %{Referer}i %{User-Agent}i";
    private final PageTypeProvider pageTypeProvider;
    private final int userPos;

    public TomcatAccessLogParser(PageTypeProvider pageTypeProvider, String pattern) {
        this.pageTypeProvider = pageTypeProvider;
        this.userPos = this.findUserPos(pattern);
    }

    @VisibleForTesting
    LogEntry parseLine(String line) {
        String[] data = line.split(" ");
        String date = StringUtils.substringBetween((String)line, (String)"[", (String)"]");
        return new LogEntry(date, this.userPos >= 0 && this.userPos < data.length ? this.cleanupUsername(data[this.userPos]) : null, this.getPageType(line));
    }

    private PageType getPageType(String line) {
        for (HttpMethod method : HttpMethod.values()) {
            String search = method + " ";
            int idx = line.indexOf(search);
            if (idx < 0) continue;
            int start = idx + search.length();
            int end = line.indexOf(" ", start);
            if (end < 0) {
                return PageType.UNKNOWN;
            }
            if (line.charAt(end - 1) == '\"') {
                --end;
            }
            return this.pageTypeProvider.pageType(method.toString(), line.substring(start, end));
        }
        return PageType.UNKNOWN;
    }

    public void processLines(File file, Consumer<LogEntry> lineConsumer) {
        try (Stream<String> lines = Files.lines(file.toPath());){
            lines.map(this::parseLine).forEach(lineConsumer);
        }
        catch (Throwable t) {
            log.error("Error reading the log file:", t);
        }
    }

    private String cleanupUsername(String username) {
        return StringUtils.isBlank((CharSequence)username) || "-".equals(username) ? null : StringUtils.strip((String)username, (String)"'\"");
    }

    private int findUserPos(String pattern) {
        if (pattern == null) {
            pattern = DEFAULT_PATTERN;
        }
        int index = 0;
        for (String element : pattern.split(" ")) {
            if (USER_PATTERNS.contains((Object)(element = StringUtils.strip((String)element, (String)"'\"")))) {
                return index;
            }
            index += SPACES_PER_PATTERN.getOrDefault(element, 1).intValue();
        }
        return this.findUserPos(DEFAULT_PATTERN);
    }

    public static final class LogEntry {
        final String date;
        final String user;
        final PageType pageType;

        @Generated
        public LogEntry(String date, String user, PageType pageType) {
            this.date = date;
            this.user = user;
            this.pageType = pageType;
        }

        @Generated
        public String getDate() {
            return this.date;
        }

        @Generated
        public String getUser() {
            return this.user;
        }

        @Generated
        public PageType getPageType() {
            return this.pageType;
        }

        @Generated
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (!(o instanceof LogEntry)) {
                return false;
            }
            LogEntry other = (LogEntry)o;
            String this$date = this.getDate();
            String other$date = other.getDate();
            if (this$date == null ? other$date != null : !this$date.equals(other$date)) {
                return false;
            }
            String this$user = this.getUser();
            String other$user = other.getUser();
            if (this$user == null ? other$user != null : !this$user.equals(other$user)) {
                return false;
            }
            PageType this$pageType = this.getPageType();
            PageType other$pageType = other.getPageType();
            return !(this$pageType == null ? other$pageType != null : !((Object)((Object)this$pageType)).equals((Object)other$pageType));
        }

        @Generated
        public int hashCode() {
            int PRIME = 59;
            int result = 1;
            String $date = this.getDate();
            result = result * 59 + ($date == null ? 43 : $date.hashCode());
            String $user = this.getUser();
            result = result * 59 + ($user == null ? 43 : $user.hashCode());
            PageType $pageType = this.getPageType();
            result = result * 59 + ($pageType == null ? 43 : ((Object)((Object)$pageType)).hashCode());
            return result;
        }

        @Generated
        public String toString() {
            return "TomcatAccessLogParser.LogEntry(date=" + this.getDate() + ", user=" + this.getUser() + ", pageType=" + (Object)((Object)this.getPageType()) + ")";
        }
    }
}

