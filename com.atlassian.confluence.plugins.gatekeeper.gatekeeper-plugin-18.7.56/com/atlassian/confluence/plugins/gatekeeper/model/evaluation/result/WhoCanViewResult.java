/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAutoDetect
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result;

import com.atlassian.confluence.plugins.gatekeeper.dto.TinyOwnerDto;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect
public class WhoCanViewResult {
    @JsonProperty
    private final int totalOwners;
    @JsonProperty
    private final List<TinyOwnerDto> owners;
    @JsonProperty
    private final String helpLink;

    public WhoCanViewResult(int totalOwners, int size, String helpLink) {
        this.totalOwners = totalOwners;
        this.owners = new ArrayList<TinyOwnerDto>(size);
        this.helpLink = helpLink;
    }

    public void add(TinyOwnerDto owner) {
        this.owners.add(owner);
    }

    public int getTotalOwners() {
        return this.totalOwners;
    }

    public List<TinyOwnerDto> getOwners() {
        return this.owners;
    }

    public String getHelpLink() {
        return this.helpLink;
    }
}

