/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.batch.template;

import com.atlassian.confluence.notifications.batch.template.BatchTemplateGroup;
import java.util.List;

public class BatchSection {
    private final int count;
    private final String header;
    private final String name;
    private final List<BatchTemplateGroup> groups;

    public BatchSection(int count, String header, String name, List<BatchTemplateGroup> groups) {
        this.count = count;
        this.header = header;
        this.name = name;
        this.groups = groups;
    }

    public int getCount() {
        return this.count;
    }

    public String getHeader() {
        return this.header;
    }

    public String getName() {
        return this.name;
    }

    public List<BatchTemplateGroup> getGroups() {
        return this.groups;
    }
}

