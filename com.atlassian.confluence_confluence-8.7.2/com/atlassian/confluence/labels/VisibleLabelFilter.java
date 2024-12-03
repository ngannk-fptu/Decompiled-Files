/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.filter.Filter
 *  com.google.common.base.Predicate
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.labels;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.labels.persistence.dao.LabelSearchResult;
import com.atlassian.core.util.filter.Filter;
import com.google.common.base.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;

public class VisibleLabelFilter
implements Filter,
Predicate<Label> {
    private String currentUser;

    public VisibleLabelFilter() {
    }

    public VisibleLabelFilter(String user) {
        this.currentUser = user;
    }

    public boolean apply(@NonNull Label input) {
        return input.isVisibleTo(this.currentUser);
    }

    public boolean isIncluded(Object object) {
        if (object instanceof Label) {
            return this.apply((Label)object);
        }
        if (object instanceof LabelSearchResult) {
            Label label = ((LabelSearchResult)object).getLabel();
            return this.apply(label);
        }
        return false;
    }
}

