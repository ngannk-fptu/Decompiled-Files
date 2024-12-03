/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.batch.template;

import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateGroup;
import java.util.ArrayList;
import java.util.List;

public class BatchTemplateLine
implements BatchTemplateElement {
    public static final String TEMPLATE_NAME = "line";
    public static final String TEMPLATE_NAME_CELL = "cellLine";
    @Deprecated
    private final int level;
    @Deprecated
    private final boolean contextual;
    @Deprecated
    private final boolean highlighted;
    @Deprecated
    private final boolean cellLine;
    @Deprecated
    private final int maxColspan;
    @Deprecated
    private final int fillXIndex;
    private final List<BatchTemplateElement> elements;

    private BatchTemplateLine(int level, boolean contextual, boolean highlighted, boolean cellLine, int maxColspan, int fillXIndex, List<BatchTemplateElement> elements) {
        this.level = level;
        this.contextual = contextual;
        this.highlighted = highlighted;
        this.cellLine = cellLine;
        this.maxColspan = maxColspan;
        this.fillXIndex = fillXIndex;
        this.elements = elements;
    }

    public Iterable<BatchTemplateElement> getElements() {
        return this.elements;
    }

    @Override
    public String getTemplateName() {
        return this.cellLine ? TEMPLATE_NAME_CELL : TEMPLATE_NAME;
    }

    @Deprecated
    public int getLevel() {
        return this.level;
    }

    @Deprecated
    public boolean isContextual() {
        return this.contextual;
    }

    @Deprecated
    public boolean isHighlighted() {
        return this.highlighted;
    }

    @Deprecated
    public boolean isCellLine() {
        return this.cellLine;
    }

    @Deprecated
    public int getMaxColspan() {
        return this.maxColspan;
    }

    @Deprecated
    public int getFillXIndex() {
        return this.fillXIndex;
    }

    public static class Builder {
        private final BatchTemplateGroup.Builder parent;
        private final int level;
        private final boolean contextual;
        @Deprecated
        private final boolean highlighted;
        @Deprecated
        private final boolean cellLine;
        private final List<BatchTemplateElement> elements = new ArrayList<BatchTemplateElement>();
        @Deprecated
        private int fillXIndex = -1;

        protected Builder(BatchTemplateGroup.Builder parent, int level, boolean contextual, boolean highlighted, boolean cellLine) {
            this.parent = parent;
            this.level = level;
            this.contextual = contextual;
            this.highlighted = highlighted;
            this.cellLine = cellLine;
        }

        public Builder element(BatchTemplateElement element) {
            return this.element(element, false);
        }

        @Deprecated
        public Builder emptyElement() {
            return this.emptyElement(false);
        }

        @Deprecated
        public Builder emptyElement(boolean fillX) {
            if (!this.cellLine) {
                throw new RuntimeException("emptyElement not supported unless you're on a cell line");
            }
            if (fillX) {
                this.fillXIndex = this.elements.size();
            }
            this.elements.add(null);
            return this;
        }

        public BatchTemplateGroup.Builder end() {
            return this.parent;
        }

        int getElementCount() {
            return this.elements.size();
        }

        BatchTemplateLine build(int maxColspan) {
            return new BatchTemplateLine(this.level, this.contextual, this.highlighted, this.cellLine, maxColspan, this.fillXIndex, this.elements);
        }

        @Deprecated
        public Builder element(BatchTemplateElement element, boolean fillX) {
            if (fillX) {
                this.fillXIndex = this.elements.size();
            }
            this.elements.add(element);
            return this;
        }
    }
}

