/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.extra.masterdetail;

import com.atlassian.confluence.plugins.pageproperties.api.model.PageProperty;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;

public class CachingDetails
implements Serializable {
    private ImmutableMap<String, ImmutableList<ImmutableMap<String, PageProperty>>> value;

    public CachingDetails(ImmutableMap<String, ImmutableList<ImmutableMap<String, PageProperty>>> value) {
        this.value = value;
    }

    public ImmutableMap<String, ImmutableList<ImmutableMap<String, PageProperty>>> getValue() {
        return this.value;
    }
}

