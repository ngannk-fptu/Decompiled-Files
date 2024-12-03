/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.service.obr;

import java.net.URL;
import org.osgi.service.obr.Resource;

public interface Repository {
    public URL getURL();

    public Resource[] getResources();

    public String getName();

    public long getLastModified();
}

