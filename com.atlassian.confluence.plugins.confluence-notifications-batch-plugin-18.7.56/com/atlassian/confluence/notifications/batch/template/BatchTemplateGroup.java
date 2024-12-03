/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.batch.template;

import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateLine;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BatchTemplateGroup
implements BatchTemplateElement {
    public static final String TEMPLATE_NAME = "group";
    private List<BatchTemplateLine> lines = new ArrayList<BatchTemplateLine>();

    private BatchTemplateGroup(List<BatchTemplateLine.Builder> lines) {
        int maxColSpan = lines.stream().mapToInt(BatchTemplateLine.Builder::getElementCount).reduce(Math::max).getAsInt();
        this.lines.addAll(lines.stream().map(builder -> builder.build(maxColSpan)).collect(Collectors.toList()));
    }

    public Iterable<BatchTemplateLine> getLines() {
        return this.lines;
    }

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }

    public static class Builder {
        private final List<BatchTemplateLine.Builder> lines = new ArrayList<BatchTemplateLine.Builder>();

        public BatchTemplateLine.Builder line() {
            return this.line(0, false, false);
        }

        @Deprecated
        public BatchTemplateLine.Builder cellLine() {
            return this.cellLine(0, false, false);
        }

        public BatchTemplateLine.Builder line(int level, boolean contextual, boolean highlighted) {
            return this.newLine(level, contextual, highlighted, false);
        }

        @Deprecated
        public BatchTemplateLine.Builder cellLine(int level, boolean contextual, boolean highlighted) {
            return this.newLine(level, contextual, highlighted, true);
        }

        @Deprecated
        private BatchTemplateLine.Builder newLine(int level, boolean contextual, boolean highlighted, boolean cellLine) {
            BatchTemplateLine.Builder builder = new BatchTemplateLine.Builder(this, level, contextual, highlighted, cellLine);
            this.lines.add(builder);
            return builder;
        }

        public BatchTemplateGroup build() {
            return new BatchTemplateGroup(this.lines);
        }
    }
}

