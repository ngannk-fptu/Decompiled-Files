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
import org.osgi.framework.Filter;
import org.osgi.framework.PackagePermission;

final class PackagePermissionCollection
extends PermissionCollection {
    static final long serialVersionUID = -3350758995234427603L;
    private transient Map<String, PackagePermission> permissions = new HashMap<String, PackagePermission>();
    private boolean all_allowed = false;
    private Map<String, PackagePermission> filterPermissions;
    private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("permissions", Hashtable.class), new ObjectStreamField("all_allowed", Boolean.TYPE), new ObjectStreamField("filterPermissions", HashMap.class)};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(Permission permission) {
        if (!(permission instanceof PackagePermission)) {
            throw new IllegalArgumentException("invalid permission: " + permission);
        }
        if (this.isReadOnly()) {
            throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
        }
        PackagePermission pp = (PackagePermission)permission;
        if (pp.bundle != null) {
            throw new IllegalArgumentException("cannot add to collection: " + pp);
        }
        String name = pp.getName();
        Filter f = pp.filter;
        PackagePermissionCollection packagePermissionCollection = this;
        synchronized (packagePermissionCollection) {
            PackagePermission existing;
            Map<String, PackagePermission> pc;
            if (f != null) {
                pc = this.filterPermissions;
                if (pc == null) {
                    this.filterPermissions = pc = new HashMap<String, PackagePermission>();
                }
            } else {
                pc = this.permissions;
            }
            if ((existing = pc.get(name)) != null) {
                int oldMask = existing.action_mask;
                int newMask = pp.action_mask;
                if (oldMask != newMask) {
                    pc.put(name, new PackagePermission(name, oldMask | newMask));
                }
            } else {
                pc.put(name, pp);
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
        Collection<PackagePermission> perms;
        if (!(permission instanceof PackagePermission)) {
            return false;
        }
        PackagePermission requested = (PackagePermission)permission;
        if (requested.filter != null) {
            return false;
        }
        String requestedName = requested.getName();
        int desired = requested.action_mask;
        int effective = 0;
        PackagePermissionCollection packagePermissionCollection = this;
        synchronized (packagePermissionCollection) {
            int last;
            PackagePermission pp;
            Map<String, PackagePermission> pc = this.permissions;
            if (this.all_allowed && (pp = pc.get("*")) != null && ((effective |= pp.action_mask) & desired) == desired) {
                return true;
            }
            pp = pc.get(requestedName);
            if (pp != null && ((effective |= pp.action_mask) & desired) == desired) {
                return true;
            }
            int offset = requestedName.length() - 1;
            while ((last = requestedName.lastIndexOf(46, offset)) != -1) {
                pp = pc.get(requestedName = requestedName.substring(0, last + 1) + "*");
                if (pp != null && ((effective |= pp.action_mask) & desired) == desired) {
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
        for (PackagePermission perm : perms) {
            if (!perm.implies0(requested, effective)) continue;
            return true;
        }
        return false;
    }

    @Override
    public synchronized Enumeration<Permission> elements() {
        ArrayList<PackagePermission> all = new ArrayList<PackagePermission>(this.permissions.values());
        Map<String, PackagePermission> pc = this.filterPermissions;
        if (pc != null) {
            all.addAll(pc.values());
        }
        return Collections.enumeration(all);
    }

    private synchronized void writeObject(ObjectOutputStream out) throws IOException {
        Hashtable<String, PackagePermission> hashtable = new Hashtable<String, PackagePermission>(this.permissions);
        ObjectOutputStream.PutField pfields = out.putFields();
        pfields.put("permissions", hashtable);
        pfields.put("all_allowed", this.all_allowed);
        pfields.put("filterPermissions", this.filterPermissions);
        out.writeFields();
    }

    private synchronized void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        HashMap fp;
        ObjectInputStream.GetField gfields = in.readFields();
        Hashtable hashtable = (Hashtable)gfields.get("permissions", null);
        this.permissions = new HashMap<String, PackagePermission>(hashtable);
        this.all_allowed = gfields.get("all_allowed", false);
        this.filterPermissions = fp = (HashMap)gfields.get("filterPermissions", null);
    }
}

