/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  org.apache.commons.lang3.StringUtils
 */
package net.java.ao.schema.ddl;

import com.google.common.base.Objects;
import net.java.ao.types.TypeInfo;
import org.apache.commons.lang3.StringUtils;

public class DDLIndexField {
    private String fieldName;
    private TypeInfo<?> type;

    private DDLIndexField(String fieldName, TypeInfo<?> type) {
        this.fieldName = fieldName;
        this.type = type;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public TypeInfo<?> getType() {
        return this.type;
    }

    public String toString() {
        return "DDLIndexField{fieldName='" + this.fieldName + '\'' + ", type=" + this.type + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DDLIndexField that = (DDLIndexField)o;
        return StringUtils.equalsIgnoreCase((CharSequence)this.fieldName, (CharSequence)that.fieldName);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.fieldName});
    }

    public static DDLIndexFieldBuilder builder() {
        return new DDLIndexFieldBuilder();
    }

    public static class DDLIndexFieldBuilder {
        private String fieldName;
        private TypeInfo<?> type;

        private DDLIndexFieldBuilder() {
        }

        public DDLIndexFieldBuilder fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public DDLIndexFieldBuilder type(TypeInfo<?> type) {
            this.type = type;
            return this;
        }

        public DDLIndexField build() {
            return new DDLIndexField(this.fieldName, this.type);
        }
    }
}

