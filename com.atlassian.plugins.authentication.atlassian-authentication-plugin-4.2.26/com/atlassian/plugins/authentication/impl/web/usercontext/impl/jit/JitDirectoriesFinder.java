/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit;

import com.atlassian.crowd.embedded.api.Directory;
import java.util.List;

public interface JitDirectoriesFinder {
    public List<Directory> findAllActiveInternalDirectories();
}

