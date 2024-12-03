/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.annotation.versioning.ConsumerType
 */
package org.osgi.service.url;

import java.net.URL;
import org.osgi.annotation.versioning.ConsumerType;

@ConsumerType
public interface URLStreamHandlerSetter {
    public void setURL(URL var1, String var2, String var3, int var4, String var5, String var6);

    public void setURL(URL var1, String var2, String var3, int var4, String var5, String var6, String var7, String var8, String var9);
}

