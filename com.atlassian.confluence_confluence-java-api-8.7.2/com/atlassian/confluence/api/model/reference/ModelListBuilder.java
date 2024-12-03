/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.api.model.reference;

import com.atlassian.confluence.api.model.reference.CollapsedList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelListBuilder<T> {
    private List<T> entries;
    private CollapsedList<T> collapsedList;
    private boolean isExpanded = false;

    private ModelListBuilder() {
    }

    public static <T> ModelListBuilder<T> newInstance() {
        return new ModelListBuilder<T>();
    }

    public static <T> ModelListBuilder<T> newExpandedInstance() {
        ModelListBuilder<T> listBuilder = ModelListBuilder.newInstance();
        super.setExpanded(true);
        return listBuilder;
    }

    private void setExpanded(boolean expanded) {
        this.isExpanded = expanded;
    }

    public ModelListBuilder<T> copy(Iterable<? extends T> toAdd) {
        this.entries = null;
        this.collapsedList = null;
        this.putAll(toAdd);
        return this;
    }

    public ModelListBuilder<T> putAll(Iterable<? extends T> toAdd) {
        if (toAdd == null) {
            return this;
        }
        if (toAdd instanceof CollapsedList) {
            CollapsedList list;
            if (this.collapsedList != null) {
                throw new IllegalStateException(String.format("Cannot set list to be a CollapsedList because its value has already been set. New value: %s, existing value: %s", toAdd, this.collapsedList));
            }
            if (this.entries != null) {
                throw new IllegalStateException(String.format("Cannot set list to be a CollapsedList because other items have already been added. New value: %s, existing values: %s", toAdd, this.entries));
            }
            this.collapsedList = list = (CollapsedList)toAdd;
            this.setExpanded(false);
        } else {
            if (this.entries == null) {
                this.entries = new ArrayList<T>();
            }
            toAdd.forEach(this.entries::add);
            this.setExpanded(true);
        }
        return this;
    }

    public List<T> build() {
        if (this.entries != null) {
            return Collections.unmodifiableList(this.entries);
        }
        if (this.collapsedList != null) {
            return this.collapsedList;
        }
        if (this.isExpanded) {
            return Collections.emptyList();
        }
        return new CollapsedList();
    }
}

