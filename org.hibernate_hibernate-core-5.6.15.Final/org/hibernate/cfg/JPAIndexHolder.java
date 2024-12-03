/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.Index
 */
package org.hibernate.cfg;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.persistence.Index;

public class JPAIndexHolder {
    private final String name;
    private final String[] columns;
    private final String[] ordering;
    private final boolean unique;

    public JPAIndexHolder(Index index) {
        StringTokenizer tokenizer = new StringTokenizer(index.columnList(), ",");
        ArrayList<String> tmp = new ArrayList<String>();
        while (tokenizer.hasMoreElements()) {
            tmp.add(tokenizer.nextToken().trim());
        }
        this.name = index.name();
        this.columns = new String[tmp.size()];
        this.ordering = new String[tmp.size()];
        this.unique = index.unique();
        this.initializeColumns(this.columns, this.ordering, tmp);
    }

    public String[] getColumns() {
        return this.columns;
    }

    public String getName() {
        return this.name;
    }

    public String[] getOrdering() {
        return this.ordering;
    }

    public boolean isUnique() {
        return this.unique;
    }

    private void initializeColumns(String[] columns, String[] ordering, List<String> list) {
        int size = list.size();
        for (int i = 0; i < size; ++i) {
            String description = list.get(i);
            String tmp = description.toLowerCase(Locale.ROOT);
            if (tmp.endsWith(" desc")) {
                columns[i] = description.substring(0, description.length() - 5);
                ordering[i] = "desc";
                continue;
            }
            if (tmp.endsWith(" asc")) {
                columns[i] = description.substring(0, description.length() - 4);
                ordering[i] = "asc";
                continue;
            }
            columns[i] = description;
            ordering[i] = null;
        }
    }
}

