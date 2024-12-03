/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalSpi
 *  com.atlassian.confluence.notifications.ConfluenceUserRole
 *  com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient
 *  com.atlassian.plugin.notifications.spi.UserRole
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.confluence.notifications.batch.service;

import com.atlassian.annotations.ExperimentalSpi;
import com.atlassian.confluence.notifications.ConfluenceUserRole;
import com.atlassian.plugin.notifications.api.medium.recipient.UserKeyRoleRecipient;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.sal.api.user.UserKey;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

@ExperimentalSpi
public class BatchingRoleRecipient
extends UserKeyRoleRecipient {
    private final BitSet payloadIdxs = new BitSet();
    private final Set<UserRole> userRoles = new HashSet<UserRole>();

    public BatchingRoleRecipient(UserRole role, UserKey userKey) {
        super((UserRole)new ConfluenceUserRole("BATCH"), userKey);
    }

    public void setPayloadIdx(int idx) {
        this.payloadIdxs.set(idx);
    }

    public boolean isPayloadIdx(int idx) {
        return this.payloadIdxs.get(idx);
    }

    public void addUserRole(UserRole userRole) {
        this.userRoles.add(userRole);
    }

    public Iterable<UserRole> getUserRoles() {
        return this.userRoles;
    }
}

