/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

public interface FileManager {
    public void setReloadingConfigs(boolean var1);

    public boolean fileNeedsReloading(String var1);

    public boolean fileNeedsReloading(URL var1);

    public InputStream loadFile(URL var1);

    public void monitorFile(URL var1);

    public URL normalizeToFileProtocol(URL var1);

    public boolean support();

    public boolean internal();

    public Collection<? extends URL> getAllPhysicalUrls(URL var1) throws IOException;
}

