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
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import org.osgi.framework.BundlePermission;

final class BundlePermissionCollection
extends PermissionCollection {
    private static final long serialVersionUID = 3258407326846433079L;
    private transient Map<String, BundlePermission> permissions = new HashMap<String, BundlePermission>();
    private boolean all_allowed = false;
    private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("permissions", Hashtable.class), new ObjectStreamField("all_allowed", Boolean.TYPE)};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(Permission permission) {
        if (!(permission instanceof BundlePermission)) {
            throw new IllegalArgumentException("invalid permission: " + permission);
        }
        if (this.isReadOnly()) {
            throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
        }
        BundlePermission bp = (BundlePermission)permission;
        String name = bp.getName();
        BundlePermissionCollection bundlePermissionCollection = this;
        synchronized (bundlePermissionCollection) {
            Map<String, BundlePermission> pc = this.permissions;
            BundlePermission existing = pc.get(name);
            if (existing != null) {
                int newMask;
                int oldMask = existing.getActionsMask();
                if (oldMask != (newMask = bp.getActionsMask())) {
                    pc.put(name, new BundlePermission(name, oldMask | newMask));
                }
            } else {
                pc.put(name, bp);
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
        if (!(permission instanceof BundlePermission)) {
            return false;
        }
        BundlePermission requested = (BundlePermission)permission;
        String requestedName = requested.getName();
        int desired = requested.getActionsMask();
        int effective = 0;
        BundlePermissionCollection bundlePermissionCollection = this;
        synchronized (bundlePermissionCollection) {
            int last;
            BundlePermission bp;
            Map<String, BundlePermission> pc = this.permissions;
            if (this.all_allowed && (bp = pc.get("*")) != null && ((effective |= bp.getActionsMask()) & desired) == desired) {
                return true;
            }
            bp = pc.get(requestedName);
            if (bp != null && ((effective |= bp.getActionsMask()) & desired) == desired) {
                return true;
            }
            int offset = requestedName.length() - 1;
            while ((last = requestedName.lastIndexOf(46, offset)) != -1) {
                bp = pc.get(requestedName = requestedName.substring(0, last + 1) + "*");
                if (bp != null && ((effective |= bp.getActionsMask()) & desired) == desired) {
                    return true;
                }
                offset = last - 1;
            }
            return false;
        }
    }

    @Override
    public synchronized Enumeration<Permission> elements() {
        ArrayList<BundlePermission> all = new ArrayList<BundlePermission>(this.permissions.values());
        return Collections.enumeration(all);
    }

    private synchronized void writeObject(ObjectOutputStream out) throws IOException {
        Hashtable<String, BundlePermission> hashtable = new Hashtable<String, BundlePermission>(this.permissions);
        ObjectOutputStream.PutField pfields = out.putFields();
        pfields.put("permissions", hashtable);
        pfields.put("all_allowed", this.all_allowed);
        out.writeFields();
    }

    private synchronized void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        ObjectInputStream.GetField gfields = in.readFields();
        Hashtable hashtable = (Hashtable)gfields.get("permissions", null);
        this.permissions = new HashMap<String, BundlePermission>(hashtable);
        this.all_allowed = gfields.get("all_allowed", false);
    }
}

