/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.exception.DirectoryInstantiationException
 */
package com.atlassian.crowd.directory.loader;

import com.atlassian.crowd.directory.InternalRemoteDirectory;
import com.atlassian.crowd.directory.loader.DirectoryInstanceLoader;
import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.exception.DirectoryInstantiationException;
import java.util.Map;

public interface InternalDirectoryInstanceLoader
extends DirectoryInstanceLoader {
    @Override
    public InternalRemoteDirectory getDirectory(Directory var1) throws DirectoryInstantiationException;

    @Override
    public InternalRemoteDirectory getRawDirectory(Long var1, String var2, Map<String, String> var3) throws DirectoryInstantiationException;
}

