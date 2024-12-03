/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 *  com.atlassian.extras.api.crowd.CrowdLicense
 */
package com.atlassian.crowd.service.license;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.extras.api.crowd.CrowdLicense;

@ExperimentalApi
public interface LicenseService {
    public CrowdLicense getLicense();
}

