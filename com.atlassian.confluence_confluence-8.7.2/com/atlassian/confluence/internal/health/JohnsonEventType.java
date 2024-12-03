/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.johnson.event.EventType
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.internal.health;

import com.atlassian.johnson.event.EventType;
import java.util.Arrays;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;

public enum JohnsonEventType {
    ATTACHMENT_MIGRATION("attachment-migration"),
    BOOTSTRAP("bootstrap"),
    CLUSTER("cluster"),
    CLUSTER_MIGRATION("cluster-migration"),
    DATABASE("database"),
    TOMCAT("tomcat"),
    EXPORT("export"),
    IMPORT("import"),
    LICENSE_INCONSISTENCY("license-inconsistency"),
    LICENSE_TOO_OLD("license-too-old"),
    REINDEX("reindex"),
    RESTART("restart"),
    SETUP("setup"),
    STARTUP("startup"),
    UPGRADE("upgrade"),
    FREE_MEMORY("free-memory"),
    LICENSE_INCOMPATIBLE("license-incompatible");

    private final String johnsonEventType;

    private JohnsonEventType(String johnsonEventType) {
        this.johnsonEventType = Objects.requireNonNull(johnsonEventType);
    }

    public static JohnsonEventType withName(String eventName) {
        return Arrays.stream(JohnsonEventType.values()).filter(value -> value.typeName().equalsIgnoreCase(eventName)).findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("No JohnsonEventType called '%s'", eventName)));
    }

    public @NonNull String typeName() {
        return this.johnsonEventType;
    }

    public @NonNull EventType eventType() {
        return Objects.requireNonNull(EventType.get((String)this.johnsonEventType));
    }
}

