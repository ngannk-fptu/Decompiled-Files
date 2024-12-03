/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.core.util.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PagerFilter
implements Serializable {
    int max = 20;
    int start = 0;

    public <T> Collection<T> getCurrentPage(Collection<T> itemsCol) {
        ArrayList<T> items = null;
        items = itemsCol instanceof List ? (ArrayList<T>)itemsCol : new ArrayList<T>(itemsCol);
        if (items == null || items.size() == 0) {
            this.start = 0;
            return Collections.emptyList();
        }
        if (this.start > items.size()) {
            this.start = 0;
            return items.subList(0, this.max);
        }
        return items.subList(this.start, Math.min(this.start + this.max, items.size()));
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getStart() {
        return this.start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return this.start + this.max;
    }

    public int getNextStart() {
        return this.start + this.max;
    }

    public int getPreviousStart() {
        return Math.max(0, this.start - this.max);
    }
}

