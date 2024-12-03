/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.oauth.serviceprovider;

import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;

public interface ConsumerInformationRenderer {
    public boolean canRender(ServiceProviderToken var1, HttpServletRequest var2);

    public void render(ServiceProviderToken var1, HttpServletRequest var2, Writer var3) throws IOException;
}

