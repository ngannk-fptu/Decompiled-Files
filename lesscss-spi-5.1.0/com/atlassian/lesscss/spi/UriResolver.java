/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 */
package com.atlassian.lesscss.spi;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@TenantAware(value=TenancyScope.TENANTLESS)
public interface UriResolver {
    public boolean exists(URI var1);

    public String encodeState(URI var1);

    public InputStream open(URI var1) throws IOException;

    public boolean supports(URI var1);
}

