/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao.schema.ddl;

import java.util.Arrays;
import java.util.Comparator;
import net.java.ao.schema.ddl.DDLField;
import net.java.ao.schema.ddl.DDLForeignKey;
import net.java.ao.schema.ddl.DDLIndex;

public class DDLTable {
    private String name;
    private DDLField[] fields = new DDLField[0];
    private DDLForeignKey[] foreignKeys = new DDLForeignKey[0];
    private DDLIndex[] indexes = new DDLIndex[0];

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DDLField[] getFields() {
        return this.fields;
    }

    public void setFields(DDLField[] fields) {
        Arrays.sort(fields, new FieldComparator());
        this.fields = fields;
    }

    public DDLForeignKey[] getForeignKeys() {
        return this.foreignKeys;
    }

    public void setForeignKeys(DDLForeignKey[] foreignKeys) {
        this.foreignKeys = foreignKeys;
    }

    public DDLIndex[] getIndexes() {
        return this.indexes;
    }

    public void setIndexes(DDLIndex[] indexes) {
        this.indexes = indexes;
    }

    public String toString() {
        return this.getName();
    }

    public int hashCode() {
        int back = 0;
        if (this.name != null) {
            back += this.name.hashCode();
        }
        return back;
    }

    public boolean equals(Object obj) {
        if (obj instanceof DDLTable) {
            DDLTable table = (DDLTable)obj;
            if (table == this) {
                return true;
            }
            return table.getName() != null && table.getName().equals(this.name);
        }
        return super.equals(obj);
    }

    private static class FieldComparator
    implements Comparator<DDLField> {
        private FieldComparator() {
        }

        @Override
        public int compare(DDLField f1, DDLField f2) {
            if (f1 == null && f2 == null) {
                return 0;
            }
            if (f1 == null) {
                return -1;
            }
            if (f2 == null) {
                return 1;
            }
            return f1.getName().compareTo(f2.getName());
        }
    }
}

