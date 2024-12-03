/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.crowd.embedded.admin;

import com.atlassian.crowd.embedded.api.Directory;
import java.util.List;

public interface DelegatedDirectoryFinder {
    public List<Directory> findDirectories();
}

