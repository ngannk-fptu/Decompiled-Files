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
import java.util.Hashtable;
import java.util.Map;
import org.osgi.framework.AdminPermission;

final class AdminPermissionCollection
extends PermissionCollection {
    private static final long serialVersionUID = 3906372644575328048L;
    private transient Map<String, AdminPermission> permissions = new HashMap<String, AdminPermission>();
    private boolean all_allowed;
    private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("permissions", Hashtable.class), new ObjectStreamField("all_allowed", Boolean.TYPE)};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(Permission permission) {
        if (!(permission instanceof AdminPermission)) {
            throw new IllegalArgumentException("invalid permission: " + permission);
        }
        if (this.isReadOnly()) {
            throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
        }
        AdminPermission ap = (AdminPermission)permission;
        if (ap.bundle != null) {
            throw new IllegalArgumentException("cannot add to collection: " + ap);
        }
        String name = ap.getName();
        AdminPermissionCollection adminPermissionCollection = this;
        synchronized (adminPermissionCollection) {
            Map<String, AdminPermission> pc = this.permissions;
            AdminPermission existing = pc.get(name);
            if (existing != null) {
                int oldMask = existing.action_mask;
                int newMask = ap.action_mask;
                if (oldMask != newMask) {
                    pc.put(name, new AdminPermission(existing.filter, oldMask | newMask));
                }
            } else {
                pc.put(name, ap);
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
        Collection<AdminPermission> perms;
        if (!(permission instanceof AdminPermission)) {
            return false;
        }
        AdminPermission requested = (AdminPermission)permission;
        if (requested.filter != null) {
            return false;
        }
        int effective = 0;
        AdminPermissionCollection adminPermissionCollection = this;
        synchronized (adminPermissionCollection) {
            int desired;
            AdminPermission ap;
            Map<String, AdminPermission> pc = this.permissions;
            if (this.all_allowed && (ap = pc.get("*")) != null && ((effective |= ap.action_mask) & (desired = requested.action_mask)) == desired) {
                return true;
            }
            perms = pc.values();
        }
        for (AdminPermission perm : perms) {
            if (!perm.implies0(requested, effective)) continue;
            return true;
        }
        return false;
    }

    @Override
    public synchronized Enumeration<Permission> elements() {
        ArrayList<AdminPermission> all = new ArrayList<AdminPermission>(this.permissions.values());
        return Collections.enumeration(all);
    }

    private synchronized void writeObject(ObjectOutputStream out) throws IOException {
        Hashtable<String, AdminPermission> hashtable = new Hashtable<String, AdminPermission>(this.permissions);
        ObjectOutputStream.PutField pfields = out.putFields();
        pfields.put("permissions", hashtable);
        pfields.put("all_allowed", this.all_allowed);
        out.writeFields();
    }

    private synchronized void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField gfields = in.readFields();
        Hashtable hashtable = (Hashtable)gfields.get("permissions", null);
        this.permissions = new HashMap<String, AdminPermission>(hashtable);
        this.all_allowed = gfields.get("all_allowed", false);
    }
}

