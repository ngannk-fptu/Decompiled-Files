/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.io.file.attribute;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public final class FileTimes {
    public static final FileTime EPOCH = FileTime.from(Instant.EPOCH);
    static final long WINDOWS_EPOCH_OFFSET = -116444736000000000L;
    private static final long HUNDRED_NANOS_PER_SECOND = TimeUnit.SECONDS.toNanos(1L) / 100L;
    static final long HUNDRED_NANOS_PER_MILLISECOND = TimeUnit.MILLISECONDS.toNanos(1L) / 100L;

    public static FileTime minusMillis(FileTime fileTime, long millisToSubtract) {
        return FileTime.from(fileTime.toInstant().minusMillis(millisToSubtract));
    }

    public static FileTime minusNanos(FileTime fileTime, long nanosToSubtract) {
        return FileTime.from(fileTime.toInstant().minusNanos(nanosToSubtract));
    }

    public static FileTime minusSeconds(FileTime fileTime, long secondsToSubtract) {
        return FileTime.from(fileTime.toInstant().minusSeconds(secondsToSubtract));
    }

    public static FileTime now() {
        return FileTime.from(Instant.now());
    }

    public static Date ntfsTimeToDate(long ntfsTime) {
        long javaHundredNanos = Math.addExact(ntfsTime, -116444736000000000L);
        long javaMillis = Math.floorDiv(javaHundredNanos, HUNDRED_NANOS_PER_MILLISECOND);
        return new Date(javaMillis);
    }

    public static FileTime ntfsTimeToFileTime(long ntfsTime) {
        long javaHundredsNanos = Math.addExact(ntfsTime, -116444736000000000L);
        long javaSeconds = Math.floorDiv(javaHundredsNanos, HUNDRED_NANOS_PER_SECOND);
        long javaNanos = Math.floorMod(javaHundredsNanos, HUNDRED_NANOS_PER_SECOND) * 100L;
        return FileTime.from(Instant.ofEpochSecond(javaSeconds, javaNanos));
    }

    public static FileTime plusMillis(FileTime fileTime, long millisToAdd) {
        return FileTime.from(fileTime.toInstant().plusMillis(millisToAdd));
    }

    public static FileTime plusNanos(FileTime fileTime, long nanosToSubtract) {
        return FileTime.from(fileTime.toInstant().plusNanos(nanosToSubtract));
    }

    public static FileTime plusSeconds(FileTime fileTime, long secondsToAdd) {
        return FileTime.from(fileTime.toInstant().plusSeconds(secondsToAdd));
    }

    public static void setLastModifiedTime(Path path) throws IOException {
        Files.setLastModifiedTime(path, FileTimes.now());
    }

    public static Date toDate(FileTime fileTime) {
        return fileTime != null ? new Date(fileTime.toMillis()) : null;
    }

    public static FileTime toFileTime(Date date) {
        return date != null ? FileTime.fromMillis(date.getTime()) : null;
    }

    public static long toNtfsTime(Date date) {
        long javaHundredNanos = date.getTime() * HUNDRED_NANOS_PER_MILLISECOND;
        return Math.subtractExact(javaHundredNanos, -116444736000000000L);
    }

    public static long toNtfsTime(FileTime fileTime) {
        Instant instant = fileTime.toInstant();
        long javaHundredNanos = instant.getEpochSecond() * HUNDRED_NANOS_PER_SECOND + (long)(instant.getNano() / 100);
        return Math.subtractExact(javaHundredNanos, -116444736000000000L);
    }

    private FileTimes() {
    }
}

