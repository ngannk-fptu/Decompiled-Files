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
import org.osgi.framework.ServicePermission;

final class ServicePermissionCollection
extends PermissionCollection {
    static final long serialVersionUID = 662615640374640621L;
    private transient Map<String, ServicePermission> permissions = new HashMap<String, ServicePermission>();
    private boolean all_allowed = false;
    private Map<String, ServicePermission> filterPermissions;
    private static final ObjectStreamField[] serialPersistentFields = new ObjectStreamField[]{new ObjectStreamField("permissions", Hashtable.class), new ObjectStreamField("all_allowed", Boolean.TYPE), new ObjectStreamField("filterPermissions", HashMap.class)};

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(Permission permission) {
        if (!(permission instanceof ServicePermission)) {
            throw new IllegalArgumentException("invalid permission: " + permission);
        }
        if (this.isReadOnly()) {
            throw new SecurityException("attempt to add a Permission to a readonly PermissionCollection");
        }
        ServicePermission sp = (ServicePermission)permission;
        if (sp.service != null) {
            throw new IllegalArgumentException("cannot add to collection: " + sp);
        }
        String name = sp.getName();
        Filter f = sp.filter;
        ServicePermissionCollection servicePermissionCollection = this;
        synchronized (servicePermissionCollection) {
            ServicePermission existing;
            Map<String, ServicePermission> pc;
            if (f != null) {
                pc = this.filterPermissions;
                if (pc == null) {
                    this.filterPermissions = pc = new HashMap<String, ServicePermission>();
                }
            } else {
                pc = this.permissions;
            }
            if ((existing = pc.get(name)) != null) {
                int oldMask = existing.action_mask;
                int newMask = sp.action_mask;
                if (oldMask != newMask) {
                    pc.put(name, new ServicePermission(name, oldMask | newMask));
                }
            } else {
                pc.put(name, sp);
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
        Collection<ServicePermission> perms;
        if (!(permission instanceof ServicePermission)) {
            return false;
        }
        ServicePermission requested = (ServicePermission)permission;
        if (requested.filter != null) {
            return false;
        }
        int effective = 0;
        ServicePermissionCollection servicePermissionCollection = this;
        synchronized (servicePermissionCollection) {
            Map<String, ServicePermission> pc;
            ServicePermission sp;
            int desired = requested.action_mask;
            if (this.all_allowed && (sp = this.permissions.get("*")) != null && ((effective |= sp.action_mask) & desired) == desired) {
                return true;
            }
            String[] requestedNames = requested.objectClass;
            if (requestedNames == null) {
                if (((effective |= this.effective(requested.getName(), desired, effective)) & desired) == desired) {
                    return true;
                }
            } else {
                int l = requestedNames.length;
                for (int i = 0; i < l; ++i) {
                    if ((this.effective(requestedNames[i], desired, effective) & desired) != desired) continue;
                    return true;
                }
            }
            if ((pc = this.filterPermissions) == null) {
                return false;
            }
            perms = pc.values();
        }
        for (ServicePermission perm : perms) {
            if (!perm.implies0(requested, effective)) continue;
            return true;
        }
        return false;
    }

    private int effective(String requestedName, int desired, int effective) {
        int last;
        Map<String, ServicePermission> pc = this.permissions;
        ServicePermission sp = pc.get(requestedName);
        if (sp != null && ((effective |= sp.action_mask) & desired) == desired) {
            return effective;
        }
        int offset = requestedName.length() - 1;
        while ((last = requestedName.lastIndexOf(46, offset)) != -1) {
            sp = pc.get(requestedName = requestedName.substring(0, last + 1) + "*");
            if (sp != null && ((effective |= sp.action_mask) & desired) == desired) {
                return effective;
            }
            offset = last - 1;
        }
        return effective;
    }

    @Override
    public synchronized Enumeration<Permission> elements() {
        ArrayList<ServicePermission> all = new ArrayList<ServicePermission>(this.permissions.values());
        Map<String, ServicePermission> pc = this.filterPermissions;
        if (pc != null) {
            all.addAll(pc.values());
        }
        return Collections.enumeration(all);
    }

    private synchronized void writeObject(ObjectOutputStream out) throws IOException {
        Hashtable<String, ServicePermission> hashtable = new Hashtable<String, ServicePermission>(this.permissions);
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
        this.permissions = new HashMap<String, ServicePermission>(hashtable);
        this.all_allowed = gfields.get("all_allowed", false);
        this.filterPermissions = fp = (HashMap)gfields.get("filterPermissions", null);
    }
}

