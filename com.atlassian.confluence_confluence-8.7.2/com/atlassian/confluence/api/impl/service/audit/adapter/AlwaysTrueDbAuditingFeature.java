/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.spi.feature.DatabaseAuditingFeature
 */
package com.atlassian.confluence.api.impl.service.audit.adapter;

import com.atlassian.audit.spi.feature.DatabaseAuditingFeature;

public class AlwaysTrueDbAuditingFeature
implements DatabaseAuditingFeature {
    public boolean isEnabled() {
        return true;
    }
}

