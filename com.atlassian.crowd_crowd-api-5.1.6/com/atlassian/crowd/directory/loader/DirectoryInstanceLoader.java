/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 */
package com.atlassian.crowd.directory.loader;

import com.atlassian.crowd.directory.RemoteDirectory;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import java.util.Map;

public interface DirectoryInstanceLoader {
    public RemoteDirectory getDirectory(Directory var1) throws DirectoryInstantiationException;

    public RemoteDirectory getRawDirectory(Long var1, String var2, Map<String, String> var3) throws DirectoryInstantiationException;

    public boolean canLoad(String var1);
}

