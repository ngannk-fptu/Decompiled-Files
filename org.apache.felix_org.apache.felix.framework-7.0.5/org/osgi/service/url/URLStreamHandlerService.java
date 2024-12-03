/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.service.url;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import org.osgi.annotation.versioning.ConsumerType;
import org.osgi.service.url.URLStreamHandlerSetter;

@ConsumerType
public interface URLStreamHandlerService {
    public URLConnection openConnection(URL var1) throws IOException;

    public void parseURL(URLStreamHandlerSetter var1, URL var2, String var3, int var4, int var5);

    public String toExternalForm(URL var1);

    public boolean equals(URL var1, URL var2);

    public int getDefaultPort();

    public InetAddress getHostAddress(URL var1);

    public int hashCode(URL var1);

    public boolean hostsEqual(URL var1, URL var2);

    public boolean sameFile(URL var1, URL var2);
}

