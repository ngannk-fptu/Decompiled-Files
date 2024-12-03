/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.persister.walking.spi;

import java.util.Arrays;

public class AssociationKey {
    private final String table;
    private final String[] columns;
    private String str;

    public AssociationKey(String table, String[] columns) {
        this.table = table;
        this.columns = columns;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AssociationKey that = (AssociationKey)o;
        return this.table.equals(that.table) && Arrays.equals(this.columns, that.columns);
    }

    public int hashCode() {
        return this.table.hashCode();
    }

    public String toString() {
        if (this.str == null) {
            this.str = "AssociationKey(table=" + this.table + ", columns={" + String.join((CharSequence)",", this.columns) + "})";
        }
        return this.str;
    }
}

