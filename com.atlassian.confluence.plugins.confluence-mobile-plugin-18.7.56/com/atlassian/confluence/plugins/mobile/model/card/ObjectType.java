/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.BaseApiEnum
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 */
package com.atlassian.confluence.plugins.mobile.model.card;

import com.atlassian.confluence.api.model.BaseApiEnum;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;

public final class ObjectType
extends BaseApiEnum {
    public static final ObjectType PAGE_OBJECT = new ObjectType("page");
    public static final ObjectType BLOGPOST_OBJECT = new ObjectType("blogpost");
    public static final List<ObjectType> BUILT_IN = ImmutableList.of((Object)((Object)PAGE_OBJECT), (Object)((Object)BLOGPOST_OBJECT));

    @JsonCreator
    public static ObjectType valueOf(String type) {
        for (ObjectType objectType : BUILT_IN) {
            if (!type.equals(objectType.getType())) continue;
            return objectType;
        }
        return new ObjectType(type);
    }

    @JsonIgnore
    private ObjectType(String value) {
        super(value);
    }

    public String getType() {
        return this.serialise();
    }
}

