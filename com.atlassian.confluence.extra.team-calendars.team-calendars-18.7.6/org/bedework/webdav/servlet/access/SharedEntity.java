/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.webdav.servlet.access;

import org.bedework.webdav.servlet.access.AccessState;

public interface SharedEntity {
    public void setOwnerHref(String var1);

    public String getOwnerHref();

    public void setAccess(String var1);

    public String getAccess();

    public void setParentPath(String var1);

    public String getParentPath();

    public String getPath();

    public boolean isCollection();

    public void setAccessState(AccessState var1);

    public AccessState getAccessState();
}

