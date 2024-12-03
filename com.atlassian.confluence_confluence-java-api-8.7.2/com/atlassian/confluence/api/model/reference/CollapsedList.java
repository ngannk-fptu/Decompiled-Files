/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.api.model.reference;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.reference.Collapsed;
import com.atlassian.confluence.api.nav.Navigation;
import com.atlassian.confluence.api.nav.NavigationService;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;

@Internal
class CollapsedList<T>
extends AbstractList<T>
implements Collapsed,
PageResponse<T> {
    private final Navigation.Builder navBuilder;

    CollapsedList() {
        this.navBuilder = null;
    }

    CollapsedList(Navigation.Builder navBuilder) {
        this.navBuilder = navBuilder;
    }

    @Override
    public Navigation.Builder resolveNavigation(NavigationService navigationService) {
        return this.navBuilder;
    }

    @Override
    public T get(int index) {
        throw Collapsed.Exceptions.throwCollapsedException("get()");
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Iterator<T> iterator() {
        throw Collapsed.Exceptions.throwCollapsedException("iterator()");
    }

    @Override
    public List<T> getResults() {
        return this;
    }

    @Override
    public boolean hasMore() {
        return false;
    }

    @Override
    public PageRequest getPageRequest() {
        throw Collapsed.Exceptions.throwCollapsedException("getPageRequest()");
    }

    @Override
    public String toString() {
        return "null (CollapsedList)";
    }
}

