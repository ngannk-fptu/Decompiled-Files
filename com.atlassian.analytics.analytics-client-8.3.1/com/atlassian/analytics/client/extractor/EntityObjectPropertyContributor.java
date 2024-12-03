/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.bean.EntityObject
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.analytics.client.extractor;

import com.atlassian.analytics.client.extractor.PropertyContributor;
import com.atlassian.core.bean.EntityObject;
import com.google.common.collect.ImmutableMap;

public class EntityObjectPropertyContributor
implements PropertyContributor {
    @Override
    public void contribute(ImmutableMap.Builder<String, Object> builder, String name, Object value) {
        if (value instanceof EntityObject) {
            builder.put((Object)(name + ".id"), (Object)String.valueOf(((EntityObject)value).getId()));
        }
    }
}

