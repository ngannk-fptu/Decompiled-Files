/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.permission;

import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.core.Plugin;
import com.atlassian.upm.core.permission.Permission;
import com.atlassian.upm.core.permission.UserAttributes;
import java.net.URI;

public interface PermissionService {
    public Option<PermissionError> getPermissionError(UserAttributes var1, Permission var2);

    public Option<PermissionError> getPermissionError(UserAttributes var1, Permission var2, Plugin var3);

    public Option<PermissionError> getPermissionError(UserAttributes var1, Permission var2, Plugin.Module var3);

    public Option<PermissionError> getInProcessInstallationFromUriPermissionError(UserAttributes var1, URI var2);

    public static enum PermissionError {
        UNAUTHORIZED,
        FORBIDDEN,
        CONFLICT;

    }
}

