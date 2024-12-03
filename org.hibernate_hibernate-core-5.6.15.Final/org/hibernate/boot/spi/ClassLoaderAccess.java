/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.spi;

import java.net.URL;

public interface ClassLoaderAccess {
    public <T> Class<T> classForName(String var1);

    public URL locateResource(String var1);
}

