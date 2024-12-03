/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.diagnostics.internal.perflog;

import com.atlassian.diagnostics.internal.perflog.IpdLogFileReader;
import com.atlassian.diagnostics.internal.perflog.model.InstrumentQueryResults;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IpdLogService {
    private static final Logger log = LoggerFactory.getLogger(IpdLogService.class);
    private static final Pattern IPD_LOG_LINE_PATTERN = Pattern.compile("^.*\\s(\\{.*\\})");
    private static final Map<String, String> metricTypesToIdentifiersMap = Stream.of({"DB", "\"label\":\"DB."}, {"HTTP", "\"label\":\"HTTP."}).collect(Collectors.collectingAndThen(Collectors.toMap(data -> data[0], data -> data[1]), Collections::unmodifiableMap));
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final IpdLogFileReader ipdLogFileReader;

    @Nonnull
    public List<InstrumentQueryResults> readLog(@Nonnull List<String> metricTypes) {
        List metricIdentifiers = metricTypes.stream().map(metricTypesToIdentifiersMap::get).filter(Objects::nonNull).collect(Collectors.toList());
        return this.ipdLogFileReader.readLogLines().filter(line -> metricTypes.isEmpty() || this.filterLine((String)line, metricIdentifiers)).map(this::parseLine).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private boolean filterLine(String line, List<String> metricIdentifiers) {
        return metricIdentifiers.stream().anyMatch(line::contains);
    }

    private InstrumentQueryResults parseLine(String line) {
        Matcher matcher = IPD_LOG_LINE_PATTERN.matcher(line);
        if (matcher.find()) {
            String serializedQueryResult = matcher.group(1);
            try {
                return (InstrumentQueryResults)this.objectMapper.readValue(serializedQueryResult, InstrumentQueryResults.class);
            }
            catch (IOException e) {
                log.error(String.format("Can't deserialize log line: %s", line), (Throwable)e);
            }
        }
        return null;
    }

    public IpdLogService(IpdLogFileReader ipdLogFileReader) {
        this.ipdLogFileReader = ipdLogFileReader;
    }
}

