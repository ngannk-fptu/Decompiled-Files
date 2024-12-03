/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import org.osgi.framework.CapabilityPermission;
import org.osgi.framework.Filter;

final class CapabilityPermissionCollection
extends PermissionCollection {
    static final long serialVersionUID = -615322242639008920L;
    private Map<String, CapabilityPermission> permissions = new HashMap<String, CapabilityPermission>();
    private boolean all_allowed = false;
    private Map<String, CapabilityPermission> filterPermissions;
    private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("permissions", HashMap.class), new ObjectStreamField("all_allowed", Boolean.TYPE), new ObjectStreamField("filterPermissions", HashMap.class)};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(Permission permission) {
        if (!(permission instanceof CapabilityPermission)) {
            throw new IllegalArgumentException("invalid permission: " + permission);
        }
        if (this.isReadOnly()) {
            throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
        }
        CapabilityPermission cp = (CapabilityPermission)permission;
        if (cp.bundle != null) {
            throw new IllegalArgumentException("cannot add to collection: " + cp);
        }
        String name = cp.getName();
        Filter f = cp.filter;
        CapabilityPermissionCollection capabilityPermissionCollection = this;
        synchronized (capabilityPermissionCollection) {
            CapabilityPermission existing;
            Map<String, CapabilityPermission> pc;
            if (f != null) {
                pc = this.filterPermissions;
                if (pc == null) {
                    this.filterPermissions = pc = new HashMap<String, CapabilityPermission>();
                }
            } else {
                pc = this.permissions;
            }
            if ((existing = pc.get(name)) != null) {
                int oldMask = existing.action_mask;
                int newMask = cp.action_mask;
                if (oldMask != newMask) {
                    pc.put(name, new CapabilityPermission(name, oldMask | newMask));
                }
            } else {
                pc.put(name, cp);
            }
            if (!this.all_allowed && name.equals("*")) {
                this.all_allowed = true;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean implies(Permission permission) {
        Collection<CapabilityPermission> perms;
        if (!(permission instanceof CapabilityPermission)) {
            return false;
        }
        CapabilityPermission requested = (CapabilityPermission)permission;
        if (requested.filter != null) {
            return false;
        }
        String requestedName = requested.getName();
        int desired = requested.action_mask;
        int effective = 0;
        CapabilityPermissionCollection capabilityPermissionCollection = this;
        synchronized (capabilityPermissionCollection) {
            int last;
            CapabilityPermission cp;
            Map<String, CapabilityPermission> pc = this.permissions;
            if (this.all_allowed && (cp = pc.get("*")) != null && ((effective |= cp.action_mask) & desired) == desired) {
                return true;
            }
            cp = pc.get(requestedName);
            if (cp != null && ((effective |= cp.action_mask) & desired) == desired) {
                return true;
            }
            int offset = requestedName.length() - 1;
            while ((last = requestedName.lastIndexOf(46, offset)) != -1) {
                cp = pc.get(requestedName = requestedName.substring(0, last + 1) + "*");
                if (cp != null && ((effective |= cp.action_mask) & desired) == desired) {
                    return true;
                }
                offset = last - 1;
            }
            pc = this.filterPermissions;
            if (pc == null) {
                return false;
            }
            perms = pc.values();
        }
        for (CapabilityPermission perm : perms) {
            if (!perm.implies0(requested, effective)) continue;
            return true;
        }
        return false;
    }

    @Override
    public synchronized Enumeration<Permission> elements() {
        ArrayList<CapabilityPermission> all = new ArrayList<CapabilityPermission>(this.permissions.values());
        Map<String, CapabilityPermission> pc = this.filterPermissions;
        if (pc != null) {
            all.addAll(pc.values());
        }
        return Collections.enumeration(all);
    }

    private synchronized void writeObject(ObjectOutputStream out) throws IOException {
        ObjectOutputStream.PutField pfields = out.putFields();
        pfields.put("permissions", this.permissions);
        pfields.put("all_allowed", this.all_allowed);
        pfields.put("filterPermissions", this.filterPermissions);
        out.writeFields();
    }

    private synchronized void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        HashMap fp;
        HashMap p;
        ObjectInputStream.GetField gfields = in.readFields();
        this.permissions = p = (HashMap)gfields.get("permissions", null);
        this.all_allowed = gfields.get("all_allowed", false);
        this.filterPermissions = fp = (HashMap)gfields.get("filterPermissions", null);
    }
}

