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
import com.atlassian.confluence.labels.Namespace;
import com.atlassian.core.util.filter.Filter;
import com.google.common.base.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;

public class SpecialLabelFilter
implements Filter,
Predicate<Label> {
    public boolean isIncluded(Object o) {
        if (o instanceof Label) {
            return this.apply((Label)o);
        }
        return true;
    }

    public boolean apply(@NonNull Label label) {
        if (Namespace.PERSONAL.equals(label.getNamespace())) {
            return !"favourite".equals(label.getName()) && !"favorite".equals(label.getName());
        }
        return true;
    }
}

