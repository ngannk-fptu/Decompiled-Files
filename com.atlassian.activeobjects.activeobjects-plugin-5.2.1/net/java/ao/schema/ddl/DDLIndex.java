/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  org.apache.commons.lang3.StringUtils
 */
package net.java.ao.schema.ddl;

import com.google.common.base.Objects;
import java.util.Arrays;
import java.util.stream.Stream;
import net.java.ao.schema.ddl.DDLIndexField;
import org.apache.commons.lang3.StringUtils;

public class DDLIndex {
    private String table;
    private DDLIndexField[] fields = new DDLIndexField[0];
    private String indexName;

    public static DDLIndexBuilder builder() {
        return new DDLIndexBuilder();
    }

    private DDLIndex(String table, DDLIndexField[] fields, String indexName) {
        this.table = table;
        this.fields = fields;
        this.indexName = indexName;
    }

    public String getTable() {
        return this.table;
    }

    public DDLIndexField[] getFields() {
        return this.fields;
    }

    public String getIndexName() {
        return this.indexName;
    }

    public boolean containsFieldWithName(String fieldName) {
        return Stream.of(this.getFields()).map(DDLIndexField::getFieldName).anyMatch(indexFieldName -> indexFieldName.equals(fieldName));
    }

    public boolean containsFieldWithNameIgnoreCase(String fieldName) {
        return Stream.of(this.getFields()).map(DDLIndexField::getFieldName).anyMatch(indexFieldName -> indexFieldName.equalsIgnoreCase(fieldName));
    }

    public String toString() {
        return "DDLIndex{table='" + this.table + '\'' + ", fields=" + Arrays.toString(this.fields) + ", indexName='" + this.indexName + '\'' + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DDLIndex index = (DDLIndex)o;
        return StringUtils.equalsIgnoreCase((CharSequence)this.table, (CharSequence)index.table) && Arrays.equals(this.fields, index.fields);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.table}) + Arrays.hashCode(this.fields);
    }

    public static class DDLIndexBuilder {
        private String table;
        private DDLIndexField[] fields;
        private String indexName;

        private DDLIndexBuilder() {
        }

        public DDLIndexBuilder table(String table) {
            this.table = table;
            return this;
        }

        public DDLIndexBuilder field(DDLIndexField field) {
            this.fields = new DDLIndexField[]{field};
            return this;
        }

        public DDLIndexBuilder fields(DDLIndexField ... fields) {
            this.fields = fields;
            return this;
        }

        public DDLIndexBuilder indexName(String indexName) {
            this.indexName = indexName;
            return this;
        }

        public DDLIndex build() {
            return new DDLIndex(this.table, this.fields, this.indexName);
        }
    }
}

