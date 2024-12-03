/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.jfr.enums;

import java.util.Objects;

public enum RecordingTemplate {
    DEFAULT("atst_in_product_diagnostic", true, true);

    private final String recordingName;
    private final boolean toDisk;
    private final boolean dumpOnExit;

    private RecordingTemplate(String recordingName, boolean toDisk, boolean dumpOnExit) {
        this.recordingName = Objects.requireNonNull(recordingName);
        this.toDisk = toDisk;
        this.dumpOnExit = dumpOnExit;
    }

    public String getRecordingName() {
        return this.recordingName;
    }

    public String getTemplateName() {
        return this.name().toLowerCase();
    }

    public boolean isToDisk() {
        return this.toDisk;
    }

    public boolean isDumpOnExit() {
        return this.dumpOnExit;
    }
}

