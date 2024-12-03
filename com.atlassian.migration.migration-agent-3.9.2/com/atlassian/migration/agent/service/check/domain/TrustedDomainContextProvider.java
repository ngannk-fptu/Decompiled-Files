/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.check.domain;

import com.atlassian.migration.agent.service.check.CheckContextProvider;
import com.atlassian.migration.agent.service.check.domain.TrustedDomainContext;
import java.util.Map;

public class TrustedDomainContextProvider
implements CheckContextProvider<TrustedDomainContext> {
    @Override
    public TrustedDomainContext apply(Map<String, Object> parameters) {
        return new TrustedDomainContext();
    }
}

