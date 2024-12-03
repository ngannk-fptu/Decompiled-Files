/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.admin.criteria;

import com.atlassian.confluence.admin.criteria.WritableDirectoryExistsCriteria;

public class CanInviteUserCriteria {
    private final WritableDirectoryExistsCriteria writableDirectoryExistsCriteria;

    public CanInviteUserCriteria(WritableDirectoryExistsCriteria writableDirectoryExistsCriteria) {
        this.writableDirectoryExistsCriteria = writableDirectoryExistsCriteria;
    }

    public boolean isMet() {
        return this.writableDirectoryExistsCriteria.isMet();
    }
}

