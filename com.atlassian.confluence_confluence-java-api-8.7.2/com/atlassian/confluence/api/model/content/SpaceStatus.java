/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  org.codehaus.jackson.annotate.JsonIgnore
 */
package com.atlassian.confluence.api.model.content;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.api.model.BaseApiEnum;
import org.codehaus.jackson.annotate.JsonIgnore;

@ExperimentalApi
public class SpaceStatus
extends BaseApiEnum {
    public static final SpaceStatus CURRENT = new SpaceStatus("current");
    public static final SpaceStatus ARCHIVED = new SpaceStatus("archived");
    private static final SpaceStatus[] BUILT_IN = new SpaceStatus[]{CURRENT, ARCHIVED};

    @JsonIgnore
    private SpaceStatus(String type) {
        super(type);
    }

    public static SpaceStatus valueOf(String type) {
        for (SpaceStatus spaceStatus : BUILT_IN) {
            if (!type.toLowerCase().equals(spaceStatus.getStatus())) continue;
            return spaceStatus;
        }
        return new SpaceStatus(type);
    }

    public String getStatus() {
        return this.getValue();
    }
}

