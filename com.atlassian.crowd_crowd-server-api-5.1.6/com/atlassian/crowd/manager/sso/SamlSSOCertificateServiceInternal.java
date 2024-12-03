/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.crowd.model.sso.KeyCertificatePair
 */
package com.atlassian.crowd.manager.sso;

import com.atlassian.annotations.Internal;
import com.atlassian.crowd.manager.sso.SamlSSOCertificateService;
import com.atlassian.crowd.model.sso.KeyCertificatePair;
import java.util.Optional;

@Internal
public interface SamlSSOCertificateServiceInternal
extends SamlSSOCertificateService {
    public Optional<KeyCertificatePair> getKeyCertificatePair(long var1);
}

