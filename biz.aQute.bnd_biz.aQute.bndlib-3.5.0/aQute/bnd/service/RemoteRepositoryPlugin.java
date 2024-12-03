/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.service;

import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.service.ResourceHandle;
import aQute.bnd.service.Strategy;
import java.io.File;
import java.util.Map;

public interface RemoteRepositoryPlugin
extends RepositoryPlugin {
    public ResourceHandle getHandle(String var1, String var2, Strategy var3, Map<String, String> var4) throws Exception;

    public File getCacheDirectory();
}

