/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.lang.reflect.Constructor;
import java.net.SocketPermission;
import java.security.UnresolvedPermission;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PropertyPermission;
import java.util.Set;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ExitException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.util.SecurityManagerUtil;

public class Permissions
extends ProjectComponent {
    private final List<Permission> grantedPermissions = new LinkedList<Permission>();
    private final List<Permission> revokedPermissions = new LinkedList<Permission>();
    private java.security.Permissions granted = null;
    private SecurityManager origSm = null;
    private boolean active = false;
    private final boolean delegateToOldSM;
    private static final Class<?>[] PARAMS = new Class[]{String.class, String.class};

    public Permissions() {
        this(false);
    }

    public Permissions(boolean delegateToOldSM) {
        this.delegateToOldSM = delegateToOldSM;
    }

    public void addConfiguredGrant(Permission perm) {
        this.grantedPermissions.add(perm);
    }

    public void addConfiguredRevoke(Permission perm) {
        this.revokedPermissions.add(perm);
    }

    public synchronized void setSecurityManager() throws BuildException {
        if (!SecurityManagerUtil.isSetSecurityManagerAllowed()) {
            String msg = "Use of <permissions> or " + Permissions.class.getName() + " is disallowed in current Java runtime version";
            if (SecurityManagerUtil.warnOnSecurityManagerUsage(this.getProject())) {
                this.log("Security checks are disabled - " + msg, 1);
                return;
            }
            throw new BuildException(msg);
        }
        this.origSm = System.getSecurityManager();
        this.init();
        System.setSecurityManager(new MySM());
        this.active = true;
    }

    private void init() throws BuildException {
        this.granted = new java.security.Permissions();
        for (Permission p : this.revokedPermissions) {
            if (p.getClassName() != null) continue;
            throw new BuildException("Revoked permission " + p + " does not contain a class.");
        }
        for (Permission p : this.grantedPermissions) {
            if (p.getClassName() == null) {
                throw new BuildException("Granted permission " + p + " does not contain a class.");
            }
            java.security.Permission perm = this.createPermission(p);
            this.granted.add(perm);
        }
        this.granted.add(new SocketPermission("localhost:1024-", "listen"));
        this.granted.add(new PropertyPermission("java.version", "read"));
        this.granted.add(new PropertyPermission("java.vendor", "read"));
        this.granted.add(new PropertyPermission("java.vendor.url", "read"));
        this.granted.add(new PropertyPermission("java.class.version", "read"));
        this.granted.add(new PropertyPermission("os.name", "read"));
        this.granted.add(new PropertyPermission("os.version", "read"));
        this.granted.add(new PropertyPermission("os.arch", "read"));
        this.granted.add(new PropertyPermission("file.encoding", "read"));
        this.granted.add(new PropertyPermission("file.separator", "read"));
        this.granted.add(new PropertyPermission("path.separator", "read"));
        this.granted.add(new PropertyPermission("line.separator", "read"));
        this.granted.add(new PropertyPermission("java.specification.version", "read"));
        this.granted.add(new PropertyPermission("java.specification.vendor", "read"));
        this.granted.add(new PropertyPermission("java.specification.name", "read"));
        this.granted.add(new PropertyPermission("java.vm.specification.version", "read"));
        this.granted.add(new PropertyPermission("java.vm.specification.vendor", "read"));
        this.granted.add(new PropertyPermission("java.vm.specification.name", "read"));
        this.granted.add(new PropertyPermission("java.vm.version", "read"));
        this.granted.add(new PropertyPermission("java.vm.vendor", "read"));
        this.granted.add(new PropertyPermission("java.vm.name", "read"));
    }

    private java.security.Permission createPermission(Permission permission) {
        try {
            Class<java.security.Permission> clazz = Class.forName(permission.getClassName()).asSubclass(java.security.Permission.class);
            String name = permission.getName();
            String actions = permission.getActions();
            Constructor<java.security.Permission> ctr = clazz.getConstructor(PARAMS);
            return ctr.newInstance(name, actions);
        }
        catch (Exception e) {
            return new UnresolvedPermission(permission.getClassName(), permission.getName(), permission.getActions(), null);
        }
    }

    public synchronized void restoreSecurityManager() throws BuildException {
        if (!SecurityManagerUtil.isSetSecurityManagerAllowed()) {
            String msg = "Use of <permissions> or " + Permissions.class.getName() + " is disallowed in current Java runtime version";
            if (SecurityManagerUtil.warnOnSecurityManagerUsage(this.getProject())) {
                this.log("Security checks are disabled - " + msg, 1);
                return;
            }
            throw new BuildException(msg);
        }
        this.active = false;
        System.setSecurityManager(this.origSm);
    }

    private class MySM
    extends SecurityManager {
        private MySM() {
        }

        @Override
        public void checkExit(int status) {
            RuntimePermission perm = new RuntimePermission("exitVM", null);
            try {
                this.checkPermission(perm);
            }
            catch (SecurityException e) {
                throw new ExitException(e.getMessage(), status);
            }
        }

        @Override
        public void checkPermission(java.security.Permission perm) {
            if (Permissions.this.active) {
                if (Permissions.this.delegateToOldSM && !perm.getName().equals("exitVM")) {
                    boolean permOK = Permissions.this.granted.implies(perm);
                    this.checkRevoked(perm);
                    if (!permOK && Permissions.this.origSm != null) {
                        Permissions.this.origSm.checkPermission(perm);
                    }
                } else {
                    if (!Permissions.this.granted.implies(perm)) {
                        throw new SecurityException("Permission " + perm + " was not granted.");
                    }
                    this.checkRevoked(perm);
                }
            }
        }

        private void checkRevoked(java.security.Permission perm) {
            for (Permission revoked : Permissions.this.revokedPermissions) {
                if (!revoked.matches(perm)) continue;
                throw new SecurityException("Permission " + perm + " was revoked.");
            }
        }
    }

    public static class Permission {
        private String className;
        private String name;
        private String actionString;
        private Set<String> actions;

        public void setClass(String aClass) {
            this.className = aClass.trim();
        }

        public String getClassName() {
            return this.className;
        }

        public void setName(String aName) {
            this.name = aName.trim();
        }

        public String getName() {
            return this.name;
        }

        public void setActions(String actions) {
            this.actionString = actions;
            if (!actions.isEmpty()) {
                this.actions = this.parseActions(actions);
            }
        }

        public String getActions() {
            return this.actionString;
        }

        boolean matches(java.security.Permission perm) {
            if (!this.className.equals(perm.getClass().getName())) {
                return false;
            }
            if (this.name != null && (this.name.endsWith("*") ? !perm.getName().startsWith(this.name.substring(0, this.name.length() - 1)) : !this.name.equals(perm.getName()))) {
                return false;
            }
            if (this.actions != null) {
                Set<String> as = this.parseActions(perm.getActions());
                int size = as.size();
                as.removeAll(this.actions);
                return as.size() != size;
            }
            return true;
        }

        private Set<String> parseActions(String actions) {
            HashSet<String> result = new HashSet<String>();
            StringTokenizer tk = new StringTokenizer(actions, ",");
            while (tk.hasMoreTokens()) {
                String item = tk.nextToken().trim();
                if (item.isEmpty()) continue;
                result.add(item);
            }
            return result;
        }

        public String toString() {
            return "Permission: " + this.className + " (\"" + this.name + "\", \"" + this.actions + "\")";
        }
    }
}

