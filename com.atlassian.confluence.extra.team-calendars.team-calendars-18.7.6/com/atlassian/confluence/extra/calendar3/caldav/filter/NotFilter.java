/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.filter;

import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterType;
import com.atlassian.confluence.extra.calendar3.caldav.filter.OrFilter;

public class NotFilter
extends FilterBase {
    public NotFilter() {
        super("NotFilter");
        this.setType(FilterType.OR);
    }

    @Override
    protected FilterBase clone() {
        return new OrFilter();
    }
}

