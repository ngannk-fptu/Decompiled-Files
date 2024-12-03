/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonAutoDetect
 */
package com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result;

import com.atlassian.confluence.plugins.gatekeeper.dto.TinyOwnerDto;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.PermissionSet;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.ArrayList;
import java.util.List;

@JsonAutoDetect
public class EvaluationResult {
    private List<TinyOwnerDto> owners;
    private List<int[]> permissions;

    public EvaluationResult(int size) {
        this.owners = new ArrayList<TinyOwnerDto>(size);
        this.permissions = new ArrayList<int[]>(size);
    }

    public void add(TinyOwnerDto owner, PermissionSet[] permissionSets) {
        this.owners.add(owner);
        int[] ownerPermissions = new int[permissionSets.length];
        for (int i = 0; i < ownerPermissions.length; ++i) {
            ownerPermissions[i] = permissionSets[i].toTransferFormat();
        }
        this.permissions.add(ownerPermissions);
    }

    public List<TinyOwnerDto> getOwners() {
        return this.owners;
    }

    public List<int[]> getPermissions() {
        return this.permissions;
    }
}

