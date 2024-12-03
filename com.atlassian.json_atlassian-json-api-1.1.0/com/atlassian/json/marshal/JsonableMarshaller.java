/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.json.marshal;

import com.atlassian.annotations.PublicApi;
import com.atlassian.json.marshal.Jsonable;

@PublicApi
public interface JsonableMarshaller {
    public Jsonable marshal(Object var1);
}

