/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.notifications.batch.template;

import com.atlassian.confluence.notifications.batch.template.BatchTemplateElement;
import com.atlassian.confluence.notifications.batch.template.BatchTemplateEmpty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Deprecated
public class BatchTemplateTable
implements BatchTemplateElement {
    public static final String TEMPLATE_NAME = "table";
    private final List<String> headers;
    private final List<BatchTemplateRow> rows;

    private BatchTemplateTable(List<String> headers, List<BatchTemplateRow> rows) {
        this.headers = headers;
        this.rows = rows;
    }

    public List<String> getHeaders() {
        return this.headers;
    }

    public Iterable<BatchTemplateRow> getRows() {
        return this.rows;
    }

    @Override
    public String getTemplateName() {
        return TEMPLATE_NAME;
    }

    public static class Builder {
        private List<String> headers;
        private List<BatchTemplateRow.Builder> rowBuilders = new ArrayList<BatchTemplateRow.Builder>();

        public Builder(String ... headers) {
            this.headers = Arrays.asList(headers);
        }

        public BatchTemplateRow.Builder row() {
            BatchTemplateRow.Builder rowBuilder = new BatchTemplateRow.Builder(this);
            this.rowBuilders.add(rowBuilder);
            return rowBuilder;
        }

        public BatchTemplateTable build() {
            ArrayList<BatchTemplateRow> rows = new ArrayList<BatchTemplateRow>();
            for (BatchTemplateRow.Builder builder : this.rowBuilders) {
                rows.add(builder.build());
            }
            return new BatchTemplateTable(this.headers, rows);
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
            private final com.atlassian.confluence.notifications.batch.template.BatchTemplateTable$Builder parent;
            private final List<BatchTemplateElement> fields = new ArrayList<BatchTemplateElement>();

            Builder(com.atlassian.confluence.notifications.batch.template.BatchTemplateTable$Builder parent) {
                this.parent = parent;
            }

            public Builder emptyField() {
                this.fields.add(new BatchTemplateEmpty());
                return this;
            }

            public Builder field(BatchTemplateElement element) {
                this.fields.add(element);
                return this;
            }

            public com.atlassian.confluence.notifications.batch.template.BatchTemplateTable$Builder end() {
                return this.parent;
            }

            public BatchTemplateRow build() {
                return new BatchTemplateRow(this.fields);
            }
        }
    }
}

