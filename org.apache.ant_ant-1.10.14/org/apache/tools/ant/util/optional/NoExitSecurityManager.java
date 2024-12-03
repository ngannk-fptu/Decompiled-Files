/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util.optional;

import java.security.Permission;
import org.apache.tools.ant.ExitException;

public class NoExitSecurityManager
extends SecurityManager {
    @Override
    public void checkExit(int status) {
        throw new ExitException(status);
    }

    @Override
    public void checkPermission(Permission perm) {
    }
}

