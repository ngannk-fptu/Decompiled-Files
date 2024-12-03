/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.portfolioanalyzer.model;

import java.net.URI;

public class ApplinkNotAuthorizedException
extends RuntimeException {
    public ApplinkNotAuthorizedException(URI authUri, Throwable e) {
        super("Authorization Required: To enable relation analysis, please authorize the application link for one of your Confluence instances. This authorization is necessary for the analysis of relationships. To proceed, kindly open the following link to grant authorization for the application link:" + authUri.toString(), e);
    }
}

