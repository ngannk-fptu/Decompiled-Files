/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.oauth.Consumer
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken
 *  com.atlassian.oauth.serviceprovider.ServiceProviderToken$Version
 *  javax.annotation.Nullable
 *  net.oauth.OAuthMessage
 */
package com.atlassian.oauth.serviceprovider.internal;

import com.atlassian.oauth.Consumer;
import com.atlassian.oauth.serviceprovider.ServiceProviderToken;
import java.net.URI;
import javax.annotation.Nullable;
import net.oauth.OAuthMessage;

public interface TokenFactory {
    public ServiceProviderToken generateRequestToken(Consumer var1, @Nullable URI var2, OAuthMessage var3, ServiceProviderToken.Version var4);

    public ServiceProviderToken generateAccessToken(ServiceProviderToken var1);
}

