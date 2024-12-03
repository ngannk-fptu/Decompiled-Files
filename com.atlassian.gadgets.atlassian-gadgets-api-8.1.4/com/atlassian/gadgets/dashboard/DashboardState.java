/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.Assertions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  net.jcip.annotations.Immutable
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 *  org.apache.commons.lang3.builder.ToStringBuilder
 */
package com.atlassian.gadgets.dashboard;

import com.atlassian.gadgets.DashboardItemState;
import com.atlassian.gadgets.GadgetState;
import com.atlassian.gadgets.dashboard.DashboardColumns;
import com.atlassian.gadgets.dashboard.DashboardId;
import com.atlassian.gadgets.dashboard.Layout;
import com.atlassian.gadgets.dashboard.util.Iterables;
import com.atlassian.plugin.util.Assertions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import net.jcip.annotations.Immutable;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

@Immutable
public class DashboardState
implements Serializable {
    private static final long serialVersionUID = 4862870053224734927L;
    private final DashboardId id;
    private final String title;
    private final Layout layout;
    private final DashboardColumns dashboardColumns;
    private final long version;

    private DashboardState(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.layout = builder.layout;
        this.version = builder.version;
        this.dashboardColumns = DashboardColumns.from(builder.columns, this.layout.getNumberOfColumns());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (this.id == null) {
            throw new InvalidObjectException("id cannot be null");
        }
        if (this.title == null) {
            throw new InvalidObjectException("title cannot be null");
        }
        if (this.layout == null) {
            throw new InvalidObjectException("layout cannot be null");
        }
        if (this.dashboardColumns.numberOfColumns() != this.layout.getNumberOfColumns()) {
            throw new InvalidObjectException("columns size must be " + this.layout.getNumberOfColumns());
        }
    }

    public DashboardId getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public Layout getLayout() {
        return this.layout;
    }

    @Deprecated
    public Iterable<GadgetState> getGadgetsInColumn(ColumnIndex column) {
        return this.toGadgetStates(this.getDashboardColumns().getItemsInColumn(column));
    }

    @Deprecated
    public Iterable<? extends Iterable<GadgetState>> getColumns() {
        return this.getDashboardColumns().getColumns().stream().map(this::toGadgetStates).collect(Collectors.toList());
    }

    private Iterable<GadgetState> toGadgetStates(Collection<DashboardItemState> dashboardItemStates) {
        return dashboardItemStates.stream().filter(GadgetState.class::isInstance).map(GadgetState.class::cast).collect(Collectors.toList());
    }

    public DashboardColumns getDashboardColumns() {
        return this.dashboardColumns;
    }

    public long getVersion() {
        return this.version;
    }

    private DashboardState add(DashboardItemState gadgetState, ColumnIndex index, boolean prepend) {
        boolean foundRequestedColumn = false;
        LinkedList<Object> modifiedColumns = new LinkedList<Object>();
        for (ColumnIndex i : this.layout.getColumnRange()) {
            List<DashboardItemState> column = this.getDashboardColumns().getItemsInColumn(i);
            if (i.equals((Object)index)) {
                foundRequestedColumn = true;
                LinkedList<DashboardItemState> newColumn = new LinkedList<DashboardItemState>();
                if (prepend) {
                    newColumn.add(gadgetState);
                    this.addExistingGadgetsToColumn(column, newColumn);
                } else {
                    this.addExistingGadgetsToColumn(column, newColumn);
                    newColumn.add(gadgetState);
                }
                modifiedColumns.add(ImmutableList.copyOf(newColumn));
                continue;
            }
            modifiedColumns.add(column);
        }
        if (!foundRequestedColumn) {
            throw new IllegalArgumentException("index is out of this dashboard's columns range");
        }
        return DashboardState.dashboard(this.id).title(this.title).layout(this.layout).dashboardColumns(modifiedColumns).version(this.version).build();
    }

    private void addExistingGadgetsToColumn(Iterable<DashboardItemState> column, List<DashboardItemState> newColumn) {
        for (DashboardItemState gadget : column) {
            newColumn.add(gadget);
        }
    }

    @Deprecated
    public DashboardState prependGadgetToColumn(GadgetState dashboardItemState, ColumnIndex index) {
        return this.prependItemToColumn(dashboardItemState, index);
    }

    @Deprecated
    public DashboardState appendGadgetToColumn(GadgetState gadgetState, ColumnIndex index) {
        return this.appendItemToColumn(gadgetState, index);
    }

    public DashboardState prependItemToColumn(DashboardItemState itemState, ColumnIndex index) {
        return this.add(itemState, index, true);
    }

    public DashboardState appendItemToColumn(DashboardItemState itemState, ColumnIndex index) {
        return this.add(itemState, index, false);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DashboardState)) {
            return false;
        }
        DashboardState rhs = (DashboardState)o;
        boolean equals = new EqualsBuilder().append((Object)this.getId(), (Object)rhs.getId()).append((Object)this.getTitle(), (Object)rhs.getTitle()).append((Object)this.getLayout(), (Object)rhs.getLayout()).isEquals();
        if (!equals) {
            return false;
        }
        for (ColumnIndex columnIndex : this.getLayout().getColumnRange()) {
            equals = Iterables.elementsEqual(this.getDashboardColumns().getItemsInColumn(columnIndex), rhs.getDashboardColumns().getItemsInColumn(columnIndex));
            if (equals) continue;
            break;
        }
        return equals;
    }

    public int hashCode() {
        HashCodeBuilder builder = new HashCodeBuilder().append((Object)this.getId()).append((Object)this.getTitle()).append((Object)this.getLayout());
        for (ColumnIndex columnIndex : this.getLayout().getColumnRange()) {
            builder.append(this.getDashboardColumns().getItemsInColumn(columnIndex));
        }
        return builder.toHashCode();
    }

    public String toString() {
        return new ToStringBuilder((Object)this).append("id", (Object)this.getId()).append("title", (Object)this.getTitle()).append("layout", (Object)this.getLayout()).append("columns", (Object)this.getDashboardColumns()).toString();
    }

    public static Builder dashboard(DashboardState state) {
        return new Builder((DashboardState)Assertions.notNull((String)"state", (Object)state));
    }

    public static TitleBuilder dashboard(DashboardId id) {
        return new TitleBuilder((DashboardId)Assertions.notNull((String)"id", (Object)id));
    }

    public static enum ColumnIndex {
        ZERO(0),
        ONE(1),
        TWO(2);

        private final int index;

        private ColumnIndex(int index) {
            this.index = index;
        }

        public int index() {
            return this.index;
        }

        public boolean hasNext() {
            return this != TWO;
        }

        public ColumnIndex next() {
            if (!this.hasNext()) {
                throw new IllegalStateException("No next column index, already at the max");
            }
            return ColumnIndex.from(this.index + 1);
        }

        public static ColumnIndex from(int index) {
            switch (index) {
                case 0: {
                    return ZERO;
                }
                case 1: {
                    return ONE;
                }
                case 2: {
                    return TWO;
                }
            }
            throw new IllegalArgumentException("Valid values for Column are 0-2");
        }

        public static Iterable<ColumnIndex> range(final ColumnIndex start, final ColumnIndex end) {
            return () -> new Iterator<ColumnIndex>(){
                private ColumnIndex nextIndex;
                {
                    this.nextIndex = start;
                }

                @Override
                public boolean hasNext() {
                    return this.nextIndex != null;
                }

                @Override
                public ColumnIndex next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    ColumnIndex currentIndex = this.nextIndex;
                    this.nextIndex = currentIndex.hasNext() && currentIndex != end ? currentIndex.next() : null;
                    return currentIndex;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException("Cannot remove elements from this iterator");
                }
            };
        }
    }

    public static class Builder {
        private DashboardId id;
        private String title;
        private Layout layout = Layout.AA;
        private List<List<DashboardItemState>> columns = Collections.emptyList();
        private long version = 0L;

        private Builder(DashboardId id, String title) {
            this.id = id;
            this.title = title;
        }

        private Builder(DashboardState state) {
            this.id = state.getId();
            this.title = state.getTitle();
            this.layout = state.getLayout();
            this.columns = new ArrayList<List<DashboardItemState>>();
            for (ColumnIndex columnIndex : this.layout.getColumnRange()) {
                this.columns.add(state.getDashboardColumns().getItemsInColumn(columnIndex));
            }
            this.version = state.getVersion();
        }

        public Builder layout(Layout layout) {
            this.layout = (Layout)((Object)Assertions.notNull((String)"layout", (Object)((Object)layout)));
            return this;
        }

        public Builder title(String title) {
            this.title = (String)Assertions.notNull((String)"title", (Object)title);
            return this;
        }

        @Deprecated
        public Builder columns(Iterable<? extends Iterable<GadgetState>> columns) {
            this.columns = new ArrayList<List<DashboardItemState>>();
            for (Iterable<GadgetState> iterable : columns) {
                this.columns.add(Lists.newArrayList(iterable));
            }
            return this;
        }

        public Builder dashboardColumns(Iterable<? extends Iterable<DashboardItemState>> columns) {
            this.columns = new ArrayList<List<DashboardItemState>>();
            for (Iterable<DashboardItemState> iterable : columns) {
                this.columns.add(Lists.newArrayList(iterable));
            }
            return this;
        }

        public Builder version(long version) {
            this.version = version;
            return this;
        }

        public DashboardState build() {
            return new DashboardState(this);
        }
    }

    public static class TitleBuilder {
        private DashboardId id;

        private TitleBuilder(DashboardId id) {
            this.id = id;
        }

        public Builder title(String title) {
            return new Builder(this.id, (String)Assertions.notNull((String)"title", (Object)title));
        }
    }
}

