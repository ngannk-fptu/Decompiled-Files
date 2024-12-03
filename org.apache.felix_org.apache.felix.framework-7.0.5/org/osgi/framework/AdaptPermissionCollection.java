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
import org.osgi.framework.AdaptPermission;

final class AdaptPermissionCollection
extends PermissionCollection {
    static final long serialVersionUID = -3350758995234427603L;
    private Map<String, AdaptPermission> permissions = new HashMap<String, AdaptPermission>();
    private boolean all_allowed = false;
    private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("permissions", HashMap.class), new ObjectStreamField("all_allowed", Boolean.TYPE)};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(Permission permission) {
        if (!(permission instanceof AdaptPermission)) {
            throw new IllegalArgumentException("invalid permission: " + permission);
        }
        if (this.isReadOnly()) {
            throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
        }
        AdaptPermission ap = (AdaptPermission)permission;
        if (ap.bundle != null) {
            throw new IllegalArgumentException("cannot add to collection: " + ap);
        }
        String name = ap.getName();
        AdaptPermissionCollection adaptPermissionCollection = this;
        synchronized (adaptPermissionCollection) {
            Map<String, AdaptPermission> pc = this.permissions;
            AdaptPermission existing = pc.get(name);
            if (existing != null) {
                int oldMask = existing.action_mask;
                int newMask = ap.action_mask;
                if (oldMask != newMask) {
                    pc.put(name, new AdaptPermission(existing.filter, oldMask | newMask));
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
        Collection<AdaptPermission> perms;
        if (!(permission instanceof AdaptPermission)) {
            return false;
        }
        AdaptPermission requested = (AdaptPermission)permission;
        if (requested.filter != null) {
            return false;
        }
        int effective = 0;
        AdaptPermissionCollection adaptPermissionCollection = this;
        synchronized (adaptPermissionCollection) {
            int desired;
            AdaptPermission ap;
            Map<String, AdaptPermission> pc = this.permissions;
            if (this.all_allowed && (ap = pc.get("*")) != null && ((effective |= ap.action_mask) & (desired = requested.action_mask)) == desired) {
                return true;
            }
            perms = pc.values();
        }
        for (AdaptPermission perm : perms) {
            if (!perm.implies0(requested, effective)) continue;
            return true;
        }
        return false;
    }

    @Override
    public synchronized Enumeration<Permission> elements() {
        ArrayList<AdaptPermission> all = new ArrayList<AdaptPermission>(this.permissions.values());
        return Collections.enumeration(all);
    }

    private synchronized void writeObject(ObjectOutputStream out) throws IOException {
        ObjectOutputStream.PutField pfields = out.putFields();
        pfields.put("permissions", this.permissions);
        pfields.put("all_allowed", this.all_allowed);
        out.writeFields();
    }

    private synchronized void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        HashMap p;
        ObjectInputStream.GetField gfields = in.readFields();
        this.permissions = p = (HashMap)gfields.get("permissions", null);
        this.all_allowed = gfields.get("all_allowed", false);
    }
}

