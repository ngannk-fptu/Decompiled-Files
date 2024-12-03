/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security.permission;

import com.hazelcast.security.permission.ClusterPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.util.Enumeration;

public final class AllPermissions
extends ClusterPermission {
    public AllPermissions() {
        super("<all permissions>");
    }

    @Override
    public boolean implies(Permission permission) {
        return true;
    }

    @Override
    public String getActions() {
        return "<all actions>";
    }

    @Override
    public PermissionCollection newPermissionCollection() {
        return new AllPermissionsCollection();
    }

    @Override
    public String toString() {
        return "<allow all permissions>";
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof AllPermissions;
    }

    @Override
    public int hashCode() {
        return 111;
    }

    public static final class AllPermissionsCollection
    extends PermissionCollection {
        private static final AllPermissions ALL_PERMISSIONS = new AllPermissions();
        private boolean all;

        public AllPermissionsCollection() {
        }

        public AllPermissionsCollection(boolean all) {
            this.all = all;
        }

        @Override
        public void add(Permission permission) {
            if (permission instanceof AllPermissions) {
                this.all = true;
            }
        }

        @Override
        public boolean implies(Permission permission) {
            return this.all;
        }

        @Override
        public Enumeration<Permission> elements() {
            return new Enumeration<Permission>(){
                boolean more;
                {
                    this.more = all;
                }

                @Override
                public boolean hasMoreElements() {
                    return this.more;
                }

                @Override
                public Permission nextElement() {
                    this.more = false;
                    return ALL_PERMISSIONS;
                }
            };
        }

        public int hashCode() {
            return this.all ? 13 : -13;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            AllPermissionsCollection other = (AllPermissionsCollection)obj;
            return this.all == other.all;
        }

        @Override
        public String toString() {
            return "<allow all permissions>";
        }
    }
}

