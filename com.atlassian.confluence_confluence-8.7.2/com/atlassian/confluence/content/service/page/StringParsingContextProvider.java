/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  net.jcip.annotations.NotThreadSafe
 */
package com.atlassian.confluence.content.service.page;

import com.atlassian.confluence.content.service.page.CreateContextProvider;
import com.atlassian.confluence.util.QueryStringUtil;
import com.google.common.collect.Maps;
import java.io.Serializable;
import java.util.Map;
import net.jcip.annotations.NotThreadSafe;

@NotThreadSafe
public class StringParsingContextProvider
implements CreateContextProvider {
    private Map<String, Serializable> context;

    @Override
    public Map<String, Serializable> getContext() {
        return this.context;
    }

    public void setContext(String queryString) {
        this.context = Maps.newHashMap();
        this.context.putAll(QueryStringUtil.toMap(queryString));
    }
}

