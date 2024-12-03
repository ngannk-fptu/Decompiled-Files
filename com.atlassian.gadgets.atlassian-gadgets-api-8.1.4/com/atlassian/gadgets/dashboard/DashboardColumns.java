/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.gadgets.dashboard;

import com.atlassian.gadgets.DashboardItemState;
import com.atlassian.gadgets.dashboard.DashboardState;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class DashboardColumns
implements Serializable {
    private final List<List<DashboardItemState>> columns;

    private DashboardColumns(List<List<DashboardItemState>> columns) {
        this.columns = columns;
    }

    public List<DashboardItemState> getItemsInColumn(DashboardState.ColumnIndex column) {
        return this.columns.get(column.index());
    }

    public List<List<DashboardItemState>> getColumns() {
        return this.columns;
    }

    public int numberOfColumns() {
        return this.columns.size();
    }

    public static DashboardColumns from(Iterable<? extends Iterable<DashboardItemState>> columns, int numberOfColumns) {
        return new DashboardColumns(DashboardColumns.copy(columns, numberOfColumns));
    }

    private static List<List<DashboardItemState>> copy(Iterable<? extends Iterable<DashboardItemState>> columns, int size) {
        ArrayList listBuilder = Lists.newArrayList();
        if (columns != null) {
            for (Iterable<DashboardItemState> iterable : columns) {
                listBuilder.add(ImmutableList.copyOf(iterable));
            }
        }
        DashboardColumns.pad(listBuilder, size, Collections.emptyList());
        return ImmutableList.copyOf((Collection)listBuilder);
    }

    private static <E, T extends E> void pad(Collection<E> list, int toSize, T obj) {
        list.addAll(Collections.nCopies(toSize - list.size(), obj));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        DashboardColumns that = (DashboardColumns)o;
        return Objects.equal(this.columns, that.columns);
    }

    public int hashCode() {
        return Objects.hashCode((Object[])new Object[]{this.columns});
    }

    public String toString() {
        return this.columns.toString();
    }
}

