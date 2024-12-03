/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.spi.feature.DelegatedViewFeature
 */
package com.atlassian.confluence.api.impl.service.audit;

import com.atlassian.audit.spi.feature.DelegatedViewFeature;
import com.atlassian.confluence.internal.license.EnterpriseFeatureFlag;
import java.util.Objects;

public class DcOnlyDelegatedViewFeature
implements DelegatedViewFeature {
    private final EnterpriseFeatureFlag enterpriseFeatureFlag;

    public DcOnlyDelegatedViewFeature(EnterpriseFeatureFlag enterpriseFeatureFlag) {
        this.enterpriseFeatureFlag = Objects.requireNonNull(enterpriseFeatureFlag);
    }

    public boolean isEnabled() {
        return this.enterpriseFeatureFlag.isEnabled();
    }
}

