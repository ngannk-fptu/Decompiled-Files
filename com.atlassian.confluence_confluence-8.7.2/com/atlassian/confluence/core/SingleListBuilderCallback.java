/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.ListBuilderCallback;
import java.util.Collections;
import java.util.List;

public class SingleListBuilderCallback<T>
implements ListBuilderCallback<T> {
    private final List<T> items;

    public SingleListBuilderCallback(List<T> spaces) {
        this.items = spaces;
    }

    @Override
    public int getAvailableSize() {
        return this.items.size();
    }

    @Override
    public List<T> getElements(int offset, int maxResults) {
        if (offset >= this.items.size()) {
            return Collections.emptyList();
        }
        int toIndex = Math.min(this.items.size(), offset + maxResults);
        return this.items.subList(offset, toIndex);
    }
}

