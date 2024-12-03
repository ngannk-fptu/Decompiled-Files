/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.filter;

import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import java.util.Optional;

public interface FilterBaseTransformer<T> {
    public Optional<T> transform(FilterBase var1);
}

