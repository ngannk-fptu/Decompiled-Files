/*
 * Decompiled with CFR 0.152.
 */
package ognl.security;

import java.io.FilePermission;
import java.security.Permission;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class OgnlSecurityManager
extends SecurityManager {
    private static final String OGNL_SANDBOX_CLASS_NAME = "ognl.security.UserMethod";
    private static final Class<?> CLASS_LOADER_CLASS = ClassLoader.class;
    private static final Class<?> FILE_PERMISSION_CLASS = FilePermission.class;
    private SecurityManager parentSecurityManager;
    private List<Long> residents = new ArrayList<Long>();
    private SecureRandom rnd = new SecureRandom();

    public OgnlSecurityManager(SecurityManager parentSecurityManager) {
        this.parentSecurityManager = parentSecurityManager;
    }

    private boolean isAccessDenied(Permission perm) {
        Class<?>[] classContext = this.getClassContext();
        Boolean isInsideClassLoader = null;
        for (Class<?> c : classContext) {
            if (isInsideClassLoader == null && CLASS_LOADER_CLASS.isAssignableFrom(c)) {
                if (FILE_PERMISSION_CLASS.equals(perm.getClass()) && "read".equals(perm.getActions())) {
                    return false;
                }
                isInsideClassLoader = false;
            }
            if (!OGNL_SANDBOX_CLASS_NAME.equals(c.getName())) continue;
            return true;
        }
        return false;
    }

    @Override
    public void checkPermission(Permission perm) {
        if (this.parentSecurityManager != null) {
            this.parentSecurityManager.checkPermission(perm);
        }
        if (this.isAccessDenied(perm)) {
            super.checkPermission(perm);
        }
    }

    @Override
    public void checkPermission(Permission perm, Object context) {
        if (this.parentSecurityManager != null) {
            this.parentSecurityManager.checkPermission(perm, context);
        }
        if (this.isAccessDenied(perm)) {
            super.checkPermission(perm, context);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Long enter() {
        OgnlSecurityManager ognlSecurityManager = this;
        synchronized (ognlSecurityManager) {
            long token = this.rnd.nextLong();
            if (this.residents.size() == 0) {
                if (this.install()) {
                    this.residents.add(token);
                    return token;
                }
                return null;
            }
            this.residents.add(token);
            return token;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void leave(long token) throws SecurityException {
        OgnlSecurityManager ognlSecurityManager = this;
        synchronized (ognlSecurityManager) {
            if (!this.residents.contains(token)) {
                throw new SecurityException();
            }
            this.residents.remove(token);
            if (this.residents.size() == 0) {
                this.uninstall();
            }
        }
    }

    private boolean install() {
        try {
            System.setSecurityManager(this);
        }
        catch (SecurityException ex) {
            return false;
        }
        return true;
    }

    private void uninstall() {
        System.setSecurityManager(this.parentSecurityManager);
    }
}

