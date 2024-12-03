/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAutoDetect
 *  com.fasterxml.jackson.annotation.JsonProperty
 */
package com.atlassian.confluence.plugins.gatekeeper.dto;

import com.atlassian.confluence.plugins.gatekeeper.dto.TinyOwnerDto;
import com.atlassian.confluence.plugins.gatekeeper.dto.TinySpaceDto;
import com.atlassian.confluence.plugins.gatekeeper.model.page.TinyPage;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permission;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.RefinedExplanation;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

@JsonAutoDetect
public class PermissionExplanations {
    @JsonProperty
    private int permissions;
    @JsonProperty
    private TinyOwnerDto owner;
    @JsonProperty
    private TinySpaceDto space;
    @JsonProperty
    private TinyPage page;
    @JsonProperty
    private Map<Permission, RefinedExplanation> explanations;

    public PermissionExplanations(int permissions, TinyOwnerDto owner, TinySpaceDto space, TinyPage page, Map<Permission, RefinedExplanation> explanations) {
        this.permissions = permissions;
        this.owner = owner;
        this.space = space;
        this.page = page;
        this.explanations = explanations;
    }

    public TinyOwnerDto getOwner() {
        return this.owner;
    }

    public TinySpaceDto getSpace() {
        return this.space;
    }

    public TinyPage getPage() {
        return this.page;
    }

    public Map<Permission, RefinedExplanation> getExplanations() {
        return this.explanations;
    }

    public int getPermissions() {
        return this.permissions;
    }
}

