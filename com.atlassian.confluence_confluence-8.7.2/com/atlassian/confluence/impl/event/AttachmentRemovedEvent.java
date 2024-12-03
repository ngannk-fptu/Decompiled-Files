/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.event;

import com.atlassian.confluence.pages.Attachment;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AttachmentRemovedEvent {
    private final List<Attachment> removedVersions;

    public AttachmentRemovedEvent(List<Attachment> removedVersions) {
        this.removedVersions = new ArrayList<Attachment>(removedVersions);
    }

    public List<Attachment> getRemovedVersions() {
        return Collections.unmodifiableList(this.removedVersions);
    }
}

