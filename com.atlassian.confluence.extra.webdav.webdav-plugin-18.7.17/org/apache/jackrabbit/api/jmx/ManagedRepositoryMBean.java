/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.api.jmx;

import java.util.Map;
import javax.jcr.RepositoryException;

public interface ManagedRepositoryMBean {
    public String getName();

    public String getVersion();

    public Map<String, String> getDescriptors();

    public String[] getWorkspaceNames();

    public void createWorkspace(String var1) throws RepositoryException;
}

