/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.projectcreate.linking.spi.LocalRoot
 *  com.atlassian.plugins.projectcreate.linking.spi.RemoteRoot
 */
package com.atlassian.plugins.projectcreate.producer.link.util;

import com.atlassian.plugins.projectcreate.linking.spi.LocalRoot;
import com.atlassian.plugins.projectcreate.linking.spi.RemoteRoot;
import java.net.URI;
import java.net.URISyntaxException;

public interface LinkingUrlFactory {
    public String getLinkUrl(LocalRoot var1);

    public String getRootUrl(LocalRoot var1);

    public String getLinkDetailsUrl(LocalRoot var1, RemoteRoot var2);

    public String getInstanceIdHash(URI var1);

    public LocalRoot getLocalRootForUrl(String var1);

    public RemoteRoot getRemoteRootForUrl(String var1) throws URISyntaxException;

    public String getRootUrlForRemote(RemoteRoot var1);
}

