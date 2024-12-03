/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.FilenameUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.jfr.service;

import com.atlassian.troubleshooting.jfr.config.JfrProperties;
import com.atlassian.troubleshooting.jfr.enums.JfrEvent;
import com.atlassian.troubleshooting.jfr.exception.JfrException;
import com.atlassian.troubleshooting.jfr.service.JfrEventExtractorService;
import com.atlassian.troubleshooting.jfr.util.JfrRecordingUtils;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedThread;
import jdk.jfr.consumer.RecordingFile;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class DefaultJfrEventExtractorService
implements JfrEventExtractorService {
    private static final Logger LOG = LoggerFactory.getLogger(DefaultJfrEventExtractorService.class);
    private static final String RESULT_FIELD_NAME = "result";
    private static final String CPU_USER_MODE = "user";
    private static final String CPU_SYSTEM_MODE = "system";
    private static final String THREAD_DUMP_EXT = ".txt";
    private static final String CPU_LOAD_DUMP_EXT = "_thread_cpu_utilisation.txt";
    private static final String TABLE_ROW_FORMAT = "%13s\t%12s\t%14s\t%16s\t%12s\t%s";
    private static final String TABLE_HEADER = String.format("%13s\t%12s\t%14s\t%16s\t%12s\t%s", "JVM_THREAD_ID", "OS_THREAD_ID", "%CPU_USER_MODE", "%CPU_SYSTEM_MODE", "SYSTEM_TIME", "THREAD_NAME");
    private static final Comparator<RecordedEvent> BY_CPU_LOAD = (a, b) -> Float.compare(b.getFloat(CPU_USER_MODE) + b.getFloat(CPU_SYSTEM_MODE), a.getFloat(CPU_USER_MODE) + a.getFloat(CPU_SYSTEM_MODE));
    private static final ZoneId SYSTEM_ZONE_ID = TimeZone.getDefault().toZoneId();
    private final JfrProperties jfrProperties;

    @Autowired
    public DefaultJfrEventExtractorService(JfrProperties jfrProperties) {
        this.jfrProperties = jfrProperties;
    }

    @Override
    public Path extractThreadDumps(Path pathToJfrDump) {
        Objects.requireNonNull(pathToJfrDump);
        Path threadDumpsDir = Paths.get(FilenameUtils.removeExtension((String)pathToJfrDump.toString()), this.jfrProperties.getThreadDumpPath());
        List<RecordedEvent> threadDumpEvents = this.getRecordedEventsFromJfrDump(pathToJfrDump, event -> JfrEvent.THREAD_DUMP.getName().equals(event.getEventType().getName()));
        try {
            Files.createDirectories(threadDumpsDir, new FileAttribute[0]);
            for (RecordedEvent threadDumpEvent : threadDumpEvents) {
                if (!threadDumpEvent.hasField(RESULT_FIELD_NAME)) continue;
                this.writeToFile(threadDumpEvent.getString(RESULT_FIELD_NAME), threadDumpsDir.toString(), JfrRecordingUtils.DATE_TIME_FORMAT.withZone(SYSTEM_ZONE_ID).format(threadDumpEvent.getStartTime()) + THREAD_DUMP_EXT);
            }
            LOG.debug("Thread dumps from JFR bundle are extracted successfully. Path: {}", (Object)threadDumpsDir);
            return threadDumpsDir;
        }
        catch (IOException exception) {
            throw new JfrException("Error extracting thread dumps", exception);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private List<RecordedEvent> getRecordedEventsFromJfrDump(Path pathToRecording, Predicate<RecordedEvent> filter) {
        LinkedList<RecordedEvent> recordedEvents = new LinkedList<RecordedEvent>();
        try (RecordingFile recordingFile = new RecordingFile(pathToRecording);){
            while (recordingFile.hasMoreEvents()) {
                RecordedEvent recordedEvent = recordingFile.readEvent();
                if (!filter.test(recordedEvent)) continue;
                recordedEvents.add(recordedEvent);
            }
            LinkedList<RecordedEvent> linkedList = recordedEvents;
            return linkedList;
        }
        catch (IOException exception) {
            throw new JfrException("Error reading events from JFR recording", exception);
        }
    }

    private void writeToFile(String fileContent, String directory, String fileName) throws IOException {
        Path pathToFile = Paths.get(directory, fileName);
        if (Files.notExists(pathToFile, new LinkOption[0])) {
            Files.createFile(pathToFile, new FileAttribute[0]);
        }
        try (BufferedWriter fileWriter = Files.newBufferedWriter(pathToFile, StandardCharsets.UTF_8, new OpenOption[0]);){
            fileWriter.write(fileContent);
        }
    }

    @Override
    public Path extractThreadCpuLoadDumps(Path pathToJfrDump) {
        Objects.requireNonNull(pathToJfrDump);
        Path threadDumpsDir = Paths.get(FilenameUtils.removeExtension((String)pathToJfrDump.toString()), this.jfrProperties.getThreadDumpPath());
        List<RecordedEvent> threadDumpEvents = this.getRecordedEventsFromJfrDump(pathToJfrDump, event -> JfrEvent.THREAD_DUMP.getName().equals(event.getEventType().getName()));
        List<RecordedEvent> cpuLoadEvents = this.getRecordedEventsFromJfrDump(pathToJfrDump, event -> JfrEvent.THREAD_CPU_LOAD.getName().equals(event.getEventType().getName()));
        try {
            Files.createDirectories(threadDumpsDir, new FileAttribute[0]);
            threadDumpEvents.sort(Comparator.comparing(RecordedEvent::getStartTime));
            for (RecordedEvent threadDumpEvent : threadDumpEvents) {
                if (!threadDumpEvent.hasField(RESULT_FIELD_NAME)) continue;
                Instant eventTime = threadDumpEvent.getStartTime();
                List eventsByTime = cpuLoadEvents.stream().filter(DefaultJfrEventExtractorService.isBefore(eventTime)).sorted(BY_CPU_LOAD).collect(Collectors.toList());
                StringBuilder threadInfo = new StringBuilder(TABLE_HEADER).append(System.lineSeparator());
                for (RecordedEvent event2 : eventsByTime) {
                    threadInfo.append(this.convertCpuLoadEventToString(event2)).append(System.lineSeparator());
                }
                String fileName = JfrRecordingUtils.DATE_TIME_FORMAT.withZone(SYSTEM_ZONE_ID).format(eventTime) + CPU_LOAD_DUMP_EXT;
                this.writeToFile(threadInfo.toString(), threadDumpsDir.toString(), fileName);
                cpuLoadEvents.removeIf(DefaultJfrEventExtractorService.isBefore(eventTime));
            }
            LOG.debug("Thread CPU load dumps from JFR bundle are extracted successfully. Path: {}", (Object)threadDumpsDir);
            return threadDumpsDir;
        }
        catch (IOException exception) {
            throw new JfrException("Error extracting thread CPU load dumps", exception);
        }
    }

    private static Predicate<RecordedEvent> isBefore(Instant eventTime) {
        return e -> e.getStartTime().truncatedTo(ChronoUnit.SECONDS).compareTo(eventTime) <= 0;
    }

    private String convertCpuLoadEventToString(RecordedEvent cpuLoadEvent) {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("HH:mm.ss").withZone(SYSTEM_ZONE_ID);
        RecordedThread recordedThread = (RecordedThread)cpuLoadEvent.getValue("eventThread");
        return String.format(TABLE_ROW_FORMAT, recordedThread.getJavaThreadId(), recordedThread.getOSThreadId(), JfrRecordingUtils.formatAsPercentage(cpuLoadEvent.getFloat(CPU_USER_MODE)), JfrRecordingUtils.formatAsPercentage(cpuLoadEvent.getFloat(CPU_SYSTEM_MODE)), timeFormat.format(cpuLoadEvent.getStartTime()), recordedThread.getJavaName());
    }
}

