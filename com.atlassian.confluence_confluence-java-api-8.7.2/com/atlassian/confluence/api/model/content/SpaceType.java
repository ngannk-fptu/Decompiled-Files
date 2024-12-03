/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonCreator
 *  org.codehaus.jackson.annotate.JsonIgnore
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.BaseApiEnum;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonIgnore;

@ExperimentalApi
public final class SpaceType
extends BaseApiEnum {
    public static final SpaceType GLOBAL = new SpaceType("global");
    public static final SpaceType PERSONAL = new SpaceType("personal");
    private static final SpaceType[] BUILT_IN = new SpaceType[]{GLOBAL, PERSONAL};

    @JsonIgnore
    private SpaceType(String type) {
        super(type);
    }

    @JsonCreator
    public static SpaceType forName(String type) {
        for (SpaceType spaceType : BUILT_IN) {
            if (!type.toLowerCase().equals(spaceType.getType())) continue;
            return spaceType;
        }
        return new SpaceType(type);
    }

    public String getType() {
        return this.getValue();
    }

    @Override
    public String toString() {
        return "SpaceType{value='" + this.value + '\'' + '}';
    }
}

