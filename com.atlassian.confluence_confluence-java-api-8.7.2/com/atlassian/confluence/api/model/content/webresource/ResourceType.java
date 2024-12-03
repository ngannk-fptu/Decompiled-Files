/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  org.codehaus.jackson.annotate.JsonCreator
 */
package com.atlassian.confluence.api.model.content.webresource;

import com.atlassian.confluence.api.model.BaseApiEnum;
import com.google.common.collect.ImmutableList;
import java.util.stream.Stream;
import org.codehaus.jackson.annotate.JsonCreator;

public class ResourceType
extends BaseApiEnum {
    public static final ResourceType CSS = new ResourceType("css");
    public static final ResourceType JS = new ResourceType("js");
    public static final ResourceType DATA = new ResourceType("data");
    public static final ResourceType ALL = new ResourceType("all");
    @Deprecated
    public static final ImmutableList<ResourceType> BUILT_IN = ImmutableList.of((Object)CSS, (Object)JS, (Object)DATA);

    protected ResourceType(String value) {
        super(value);
    }

    @JsonCreator
    public ResourceType valueOf(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Stream.of(CSS, JS, DATA).filter(input -> value.equals(input.getValue())).findFirst().orElse(new ResourceType(value));
    }
}

