/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import org.osgi.framework.BundlePermissionCollection;

public final class BundlePermission
extends BasicPermission {
    private static final long serialVersionUID = 3257846601685873716L;
    public static final String PROVIDE = "provide";
    public static final String REQUIRE = "require";
    public static final String HOST = "host";
    public static final String FRAGMENT = "fragment";
    private static final int ACTION_PROVIDE = 1;
    private static final int ACTION_REQUIRE = 2;
    private static final int ACTION_HOST = 4;
    private static final int ACTION_FRAGMENT = 8;
    private static final int ACTION_ALL = 15;
    static final int ACTION_NONE = 0;
    private transient int action_mask;
    private volatile String actions = null;

    public BundlePermission(String symbolicName, String actions) {
        this(symbolicName, BundlePermission.parseActions(actions));
    }

    BundlePermission(String symbolicName, int mask) {
        super(symbolicName);
        this.setTransients(mask);
    }

    private synchronized void setTransients(int mask) {
        if (mask == 0 || (mask & 0xF) != mask) {
            throw new IllegalArgumentException("invalid action string");
        }
        this.action_mask = mask;
    }

    synchronized int getActionsMask() {
        return this.action_mask;
    }

    private static int parseActions(String actions) {
        boolean seencomma = false;
        int mask = 0;
        if (actions == null) {
            return mask;
        }
        char[] a = actions.toCharArray();
        int i = a.length - 1;
        if (i < 0) {
            return mask;
        }
        while (i != -1) {
            int matchlen;
            char c;
            while (i != -1 && ((c = a[i]) == ' ' || c == '\r' || c == '\n' || c == '\f' || c == '\t')) {
                --i;
            }
            if (!(i < 6 || a[i - 6] != 'p' && a[i - 6] != 'P' || a[i - 5] != 'r' && a[i - 5] != 'R' || a[i - 4] != 'o' && a[i - 4] != 'O' || a[i - 3] != 'v' && a[i - 3] != 'V' || a[i - 2] != 'i' && a[i - 2] != 'I' || a[i - 1] != 'd' && a[i - 1] != 'D' || a[i] != 'e' && a[i] != 'E')) {
                matchlen = 7;
                mask |= 3;
            } else if (!(i < 6 || a[i - 6] != 'r' && a[i - 6] != 'R' || a[i - 5] != 'e' && a[i - 5] != 'E' || a[i - 4] != 'q' && a[i - 4] != 'Q' || a[i - 3] != 'u' && a[i - 3] != 'U' || a[i - 2] != 'i' && a[i - 2] != 'I' || a[i - 1] != 'r' && a[i - 1] != 'R' || a[i] != 'e' && a[i] != 'E')) {
                matchlen = 7;
                mask |= 2;
            } else if (!(i < 3 || a[i - 3] != 'h' && a[i - 3] != 'H' || a[i - 2] != 'o' && a[i - 2] != 'O' || a[i - 1] != 's' && a[i - 1] != 'S' || a[i] != 't' && a[i] != 'T')) {
                matchlen = 4;
                mask |= 4;
            } else if (!(i < 7 || a[i - 7] != 'f' && a[i - 7] != 'F' || a[i - 6] != 'r' && a[i - 6] != 'R' || a[i - 5] != 'a' && a[i - 5] != 'A' || a[i - 4] != 'g' && a[i - 4] != 'G' || a[i - 3] != 'm' && a[i - 3] != 'M' || a[i - 2] != 'e' && a[i - 2] != 'E' || a[i - 1] != 'n' && a[i - 1] != 'N' || a[i] != 't' && a[i] != 'T')) {
                matchlen = 8;
                mask |= 8;
            } else {
                throw new IllegalArgumentException("invalid permission: " + actions);
            }
            seencomma = false;
            while (i >= matchlen && !seencomma) {
                switch (a[i - matchlen]) {
                    case ',': {
                        seencomma = true;
                    }
                    case '\t': 
                    case '\n': 
                    case '\f': 
                    case '\r': 
                    case ' ': {
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("invalid permission: " + actions);
                    }
                }
                --i;
            }
            i -= matchlen;
        }
        if (seencomma) {
            throw new IllegalArgumentException("invalid permission: " + actions);
        }
        return mask;
    }

    @Override
    public boolean implies(Permission p) {
        int desired;
        if (!(p instanceof BundlePermission)) {
            return false;
        }
        BundlePermission requested = (BundlePermission)p;
        int effective = this.getActionsMask();
        return (effective & (desired = requested.getActionsMask())) == desired && super.implies(requested);
    }

    @Override
    public String getActions() {
        String result = this.actions;
        if (result == null) {
            StringBuilder sb = new StringBuilder();
            boolean comma = false;
            if ((this.action_mask & 1) == 1) {
                sb.append(PROVIDE);
                comma = true;
            }
            if ((this.action_mask & 2) == 2) {
                if (comma) {
                    sb.append(',');
                }
                sb.append(REQUIRE);
                comma = true;
            }
            if ((this.action_mask & 4) == 4) {
                if (comma) {
                    sb.append(',');
                }
                sb.append(HOST);
                comma = true;
            }
            if ((this.action_mask & 8) == 8) {
                if (comma) {
                    sb.append(',');
                }
                sb.append(FRAGMENT);
            }
            this.actions = result = sb.toString();
        }
        return result;
    }

    @Override
    public PermissionCollection newPermissionCollection() {
        return new BundlePermissionCollection();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof BundlePermission)) {
            return false;
        }
        BundlePermission bp = (BundlePermission)obj;
        return this.getActionsMask() == bp.getActionsMask() && this.getName().equals(bp.getName());
    }

    @Override
    public int hashCode() {
        int h = 527 + this.getName().hashCode();
        h = 31 * h + this.getActions().hashCode();
        return h;
    }

    private synchronized void writeObject(ObjectOutputStream s) throws IOException {
        if (this.actions == null) {
            this.getActions();
        }
        s.defaultWriteObject();
    }

    private synchronized void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.setTransients(BundlePermission.parseActions(this.actions));
    }
}

