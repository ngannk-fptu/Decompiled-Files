/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.caldav.filter;

import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterBase;
import com.atlassian.confluence.extra.calendar3.caldav.filter.FilterType;

public class AndFilter
extends FilterBase {
    public AndFilter() {
        super("AndFilter");
        this.setType(FilterType.AND);
    }

    @Override
    protected FilterBase clone() {
        return new AndFilter();
    }
}

