/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.batch.template;

import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateEmpty;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class BatchTemplateTableLayout
implements BatchTemplateElement {
    public static final String TEMPLATE_NAME = "tableLayout";
    private final int fillXIndex;
    private final List<BatchTemplateRow> rows;

    public BatchTemplateTableLayout(int fillXIndex, List<BatchTemplateRow> rows) {
        this.fillXIndex = fillXIndex;
        this.rows = rows;
    }

    public int getFillXIndex() {
        return this.fillXIndex;
    }

    public List<BatchTemplateRow> getRows() {
        return this.rows;
    }

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }

    public static class Builder {
        private final int fillXIndex;
        private List<BatchTemplateRow.Builder> rowBuilders = new ArrayList<BatchTemplateRow.Builder>();

        public Builder(int fillXIndex) {
            this.fillXIndex = fillXIndex;
        }

        public BatchTemplateRow.Builder row() {
            BatchTemplateRow.Builder rowBuilder = new BatchTemplateRow.Builder(this);
            this.rowBuilders.add(rowBuilder);
            return rowBuilder;
        }

        public BatchTemplateTableLayout build() {
            ArrayList<BatchTemplateRow> rows = new ArrayList<BatchTemplateRow>();
            for (BatchTemplateRow.Builder builder : this.rowBuilders) {
                rows.add(builder.build());
            }
            return new BatchTemplateTableLayout(this.fillXIndex, rows);
        }
    }

    public static class BatchTemplateRow {
        private final List<BatchTemplateElement> fields;

        public BatchTemplateRow(List<BatchTemplateElement> fields) {
            this.fields = fields;
        }

        public List<BatchTemplateElement> getFields() {
            return this.fields;
        }

        public static class Builder {
            private final com.atlassian.confluence.notifications.batch.template.BatchTemplateTableLayout$Builder parent;
            private final List<BatchTemplateElement> fields = new ArrayList<BatchTemplateElement>();
            private int fillXIndex;

            Builder(com.atlassian.confluence.notifications.batch.template.BatchTemplateTableLayout$Builder parent) {
                this.parent = parent;
            }

            public Builder emptyField() {
                this.fields.add(new BatchTemplateEmpty());
                return this;
            }

            public Builder field(BatchTemplateElement element) {
                return this.field(element, false);
            }

            public Builder field(BatchTemplateElement element, boolean fillX) {
                if (fillX) {
                    this.fillXIndex = this.fields.size();
                }
                this.fields.add(element);
                return this;
            }

            public com.atlassian.confluence.notifications.batch.template.BatchTemplateTableLayout$Builder end() {
                return this.parent;
            }

            public BatchTemplateRow build() {
                return new BatchTemplateRow(this.fields);
            }
        }
    }
}

