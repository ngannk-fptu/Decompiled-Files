/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.event;

import com.google.common.base.MoreObjects;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimestampBasedEventToken {
    static final String VERSION_STAMP = "1";
    private static final Logger log = LoggerFactory.getLogger(TimestampBasedEventToken.class);
    final long timestamp;
    final List<Long> dirIds;

    public TimestampBasedEventToken(long timestamp, List<Long> dirIds) {
        this.timestamp = timestamp;
        this.dirIds = dirIds;
    }

    public static Optional<TimestampBasedEventToken> unmarshall(String marshalled) {
        String[] tokens = marshalled.split("\\.");
        if (tokens.length != 3 || !Objects.equals(tokens[0], VERSION_STAMP)) {
            log.debug("Unknown token format for version {}: {}", (Object)VERSION_STAMP, (Object)marshalled);
            return Optional.empty();
        }
        try {
            Long timestamp = Long.valueOf(tokens[1]);
            List<Long> dirIds = Arrays.stream(tokens[2].split("-")).map(Long::valueOf).collect(Collectors.toList());
            return Optional.of(new TimestampBasedEventToken(timestamp, dirIds));
        }
        catch (NumberFormatException e) {
            log.debug("Error parsing token", (Throwable)e);
            return Optional.empty();
        }
    }

    public String marshall() {
        return String.format("%s.%s.%s", VERSION_STAMP, String.valueOf(this.timestamp), this.dirIds.stream().map(String::valueOf).collect(Collectors.joining("-")));
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public List<Long> getDirIds() {
        return this.dirIds;
    }

    public String toString() {
        return MoreObjects.toStringHelper((Object)this).add("timestamp", this.timestamp).add("dirIds", this.dirIds).toString();
    }
}

