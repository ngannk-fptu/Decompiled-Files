/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAutoDetect
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.confluence.plugins.gatekeeper.model.permission;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect
public class RefinedExplanation {
    @JsonProperty
    private String goodToKnow;
    @JsonProperty
    private String why;

    public RefinedExplanation(String why, String goodToKnow) {
        this.why = why;
        this.goodToKnow = goodToKnow;
    }

    public void setWhy(String why) {
        this.why = why;
    }

    public String getWhy() {
        return this.why;
    }

    public String getGoodToKnow() {
        return this.goodToKnow;
    }

    public void setGoodToKnow(String goodToKnow) {
        this.goodToKnow = goodToKnow;
    }
}

