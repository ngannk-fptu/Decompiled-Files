/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 */
package com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.JitDirectoriesFinder;
import com.atlassian.plugins.authentication.impl.web.usercontext.impl.jit.JitException;
import java.util.List;

public abstract class AbstractJitDirectoriesFinder
implements JitDirectoriesFinder {
    @Override
    public final List<Directory> findAllActiveInternalDirectories() {
        List<Directory> internalActiveDirectories = this.executeDirectoriesQuery();
        if (internalActiveDirectories.isEmpty()) {
            throw new JitException("Could not find any writable active internal directories");
        }
        return internalActiveDirectories;
    }

    protected abstract List<Directory> executeDirectoriesQuery();
}

