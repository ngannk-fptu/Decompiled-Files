/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.rest.api.graphql;

import java.lang.reflect.Type;
import java.util.Map;

public interface GraphQL {
    public boolean isDynamicType(Type var1);

    public Type createDynamicType(String var1, Map<String, Type> var2);

    public Map<String, Type> getDynamicTypeFields(Type var1);
}

