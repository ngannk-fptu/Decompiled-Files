/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.AccessController;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedAction;
import java.util.HashMap;
import java.util.Map;
import org.osgi.framework.AdminPermissionCollection;
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.SignerProperty;

public final class AdminPermission
extends BasicPermission {
    static final long serialVersionUID = 307051004521261705L;
    public static final String CLASS = "class";
    public static final String EXECUTE = "execute";
    public static final String EXTENSIONLIFECYCLE = "extensionLifecycle";
    public static final String LIFECYCLE = "lifecycle";
    public static final String LISTENER = "listener";
    public static final String METADATA = "metadata";
    public static final String RESOLVE = "resolve";
    public static final String RESOURCE = "resource";
    public static final String STARTLEVEL = "startlevel";
    public static final String CONTEXT = "context";
    public static final String WEAVE = "weave";
    private static final int ACTION_CLASS = 1;
    private static final int ACTION_EXECUTE = 2;
    private static final int ACTION_LIFECYCLE = 4;
    private static final int ACTION_LISTENER = 8;
    private static final int ACTION_METADATA = 16;
    private static final int ACTION_RESOLVE = 64;
    private static final int ACTION_RESOURCE = 128;
    private static final int ACTION_STARTLEVEL = 256;
    private static final int ACTION_EXTENSIONLIFECYCLE = 512;
    private static final int ACTION_CONTEXT = 1024;
    private static final int ACTION_WEAVE = 2048;
    private static final int ACTION_ALL = 4063;
    static final int ACTION_NONE = 0;
    private volatile String actions = null;
    transient int action_mask;
    transient Filter filter;
    final transient Bundle bundle;
    private volatile transient Map<String, Object> properties;
    private static final ThreadLocal<Bundle> recurse = new ThreadLocal();

    public AdminPermission() {
        this(null, 4063);
    }

    public AdminPermission(String filter, String actions) {
        this(AdminPermission.parseFilter(filter), AdminPermission.parseActions(actions));
    }

    public AdminPermission(Bundle bundle, String actions) {
        super(AdminPermission.createName(bundle));
        this.setTransients(null, AdminPermission.parseActions(actions));
        this.bundle = bundle;
    }

    private static String createName(Bundle bundle) {
        if (bundle == null) {
            throw new IllegalArgumentException("bundle must not be null");
        }
        StringBuilder sb = new StringBuilder("(id=");
        sb.append(bundle.getBundleId());
        sb.append(")");
        return sb.toString();
    }

    AdminPermission(Filter filter, int mask) {
        super(filter == null ? "*" : filter.toString());
        this.setTransients(filter, mask);
        this.bundle = null;
    }

    private void setTransients(Filter filter, int mask) {
        this.filter = filter;
        if (mask == 0 || (mask & 0xFDF) != mask) {
            throw new IllegalArgumentException("invalid action string");
        }
        this.action_mask = mask;
    }

    private static int parseActions(String actions) {
        if (actions == null || actions.equals("*")) {
            return 4063;
        }
        boolean seencomma = false;
        int mask = 0;
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
            if (!(i < 4 || a[i - 4] != 'c' && a[i - 4] != 'C' || a[i - 3] != 'l' && a[i - 3] != 'L' || a[i - 2] != 'a' && a[i - 2] != 'A' || a[i - 1] != 's' && a[i - 1] != 'S' || a[i] != 's' && a[i] != 'S')) {
                matchlen = 5;
                mask |= 0x41;
            } else if (!(i < 6 || a[i - 6] != 'e' && a[i - 6] != 'E' || a[i - 5] != 'x' && a[i - 5] != 'X' || a[i - 4] != 'e' && a[i - 4] != 'E' || a[i - 3] != 'c' && a[i - 3] != 'C' || a[i - 2] != 'u' && a[i - 2] != 'U' || a[i - 1] != 't' && a[i - 1] != 'T' || a[i] != 'e' && a[i] != 'E')) {
                matchlen = 7;
                mask |= 0x42;
            } else if (!(i < 17 || a[i - 17] != 'e' && a[i - 17] != 'E' || a[i - 16] != 'x' && a[i - 16] != 'X' || a[i - 15] != 't' && a[i - 15] != 'T' || a[i - 14] != 'e' && a[i - 14] != 'E' || a[i - 13] != 'n' && a[i - 13] != 'N' || a[i - 12] != 's' && a[i - 12] != 'S' || a[i - 11] != 'i' && a[i - 11] != 'I' || a[i - 10] != 'o' && a[i - 10] != 'O' || a[i - 9] != 'n' && a[i - 9] != 'N' || a[i - 8] != 'l' && a[i - 8] != 'L' || a[i - 7] != 'i' && a[i - 7] != 'I' || a[i - 6] != 'f' && a[i - 6] != 'F' || a[i - 5] != 'e' && a[i - 5] != 'E' || a[i - 4] != 'c' && a[i - 4] != 'C' || a[i - 3] != 'y' && a[i - 3] != 'Y' || a[i - 2] != 'c' && a[i - 2] != 'C' || a[i - 1] != 'l' && a[i - 1] != 'L' || a[i] != 'e' && a[i] != 'E')) {
                matchlen = 18;
                mask |= 0x200;
            } else if (!(i < 8 || a[i - 8] != 'l' && a[i - 8] != 'L' || a[i - 7] != 'i' && a[i - 7] != 'I' || a[i - 6] != 'f' && a[i - 6] != 'F' || a[i - 5] != 'e' && a[i - 5] != 'E' || a[i - 4] != 'c' && a[i - 4] != 'C' || a[i - 3] != 'y' && a[i - 3] != 'Y' || a[i - 2] != 'c' && a[i - 2] != 'C' || a[i - 1] != 'l' && a[i - 1] != 'L' || a[i] != 'e' && a[i] != 'E')) {
                matchlen = 9;
                mask |= 4;
            } else if (!(i < 7 || a[i - 7] != 'l' && a[i - 7] != 'L' || a[i - 6] != 'i' && a[i - 6] != 'I' || a[i - 5] != 's' && a[i - 5] != 'S' || a[i - 4] != 't' && a[i - 4] != 'T' || a[i - 3] != 'e' && a[i - 3] != 'E' || a[i - 2] != 'n' && a[i - 2] != 'N' || a[i - 1] != 'e' && a[i - 1] != 'E' || a[i] != 'r' && a[i] != 'R')) {
                matchlen = 8;
                mask |= 8;
            } else if (!(i < 7 || a[i - 7] != 'm' && a[i - 7] != 'M' || a[i - 6] != 'e' && a[i - 6] != 'E' || a[i - 5] != 't' && a[i - 5] != 'T' || a[i - 4] != 'a' && a[i - 4] != 'A' || a[i - 3] != 'd' && a[i - 3] != 'D' || a[i - 2] != 'a' && a[i - 2] != 'A' || a[i - 1] != 't' && a[i - 1] != 'T' || a[i] != 'a' && a[i] != 'A')) {
                matchlen = 8;
                mask |= 0x10;
            } else if (!(i < 6 || a[i - 6] != 'r' && a[i - 6] != 'R' || a[i - 5] != 'e' && a[i - 5] != 'E' || a[i - 4] != 's' && a[i - 4] != 'S' || a[i - 3] != 'o' && a[i - 3] != 'O' || a[i - 2] != 'l' && a[i - 2] != 'L' || a[i - 1] != 'v' && a[i - 1] != 'V' || a[i] != 'e' && a[i] != 'E')) {
                matchlen = 7;
                mask |= 0x40;
            } else if (!(i < 7 || a[i - 7] != 'r' && a[i - 7] != 'R' || a[i - 6] != 'e' && a[i - 6] != 'E' || a[i - 5] != 's' && a[i - 5] != 'S' || a[i - 4] != 'o' && a[i - 4] != 'O' || a[i - 3] != 'u' && a[i - 3] != 'U' || a[i - 2] != 'r' && a[i - 2] != 'R' || a[i - 1] != 'c' && a[i - 1] != 'C' || a[i] != 'e' && a[i] != 'E')) {
                matchlen = 8;
                mask |= 0xC0;
            } else if (!(i < 9 || a[i - 9] != 's' && a[i - 9] != 'S' || a[i - 8] != 't' && a[i - 8] != 'T' || a[i - 7] != 'a' && a[i - 7] != 'A' || a[i - 6] != 'r' && a[i - 6] != 'R' || a[i - 5] != 't' && a[i - 5] != 'T' || a[i - 4] != 'l' && a[i - 4] != 'L' || a[i - 3] != 'e' && a[i - 3] != 'E' || a[i - 2] != 'v' && a[i - 2] != 'V' || a[i - 1] != 'e' && a[i - 1] != 'E' || a[i] != 'l' && a[i] != 'L')) {
                matchlen = 10;
                mask |= 0x100;
            } else if (!(i < 6 || a[i - 6] != 'c' && a[i - 6] != 'C' || a[i - 5] != 'o' && a[i - 5] != 'O' || a[i - 4] != 'n' && a[i - 4] != 'N' || a[i - 3] != 't' && a[i - 3] != 'T' || a[i - 2] != 'e' && a[i - 2] != 'E' || a[i - 1] != 'x' && a[i - 1] != 'X' || a[i] != 't' && a[i] != 'T')) {
                matchlen = 7;
                mask |= 0x400;
            } else if (!(i < 4 || a[i - 4] != 'w' && a[i - 4] != 'W' || a[i - 3] != 'e' && a[i - 3] != 'E' || a[i - 2] != 'a' && a[i - 2] != 'A' || a[i - 1] != 'v' && a[i - 1] != 'V' || a[i] != 'e' && a[i] != 'E')) {
                matchlen = 5;
                mask |= 0x800;
            } else if (i >= 0 && a[i] == '*') {
                matchlen = 1;
                mask |= 0xFDF;
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

    private static Filter parseFilter(String filterString) {
        if (filterString == null) {
            return null;
        }
        if ((filterString = filterString.trim()).equals("*")) {
            return null;
        }
        try {
            return FrameworkUtil.createFilter(filterString);
        }
        catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException("invalid filter", e);
        }
    }

    @Override
    public boolean implies(Permission p) {
        if (!(p instanceof AdminPermission)) {
            return false;
        }
        AdminPermission requested = (AdminPermission)p;
        if (this.bundle != null) {
            return false;
        }
        if (requested.filter != null) {
            return false;
        }
        return this.implies0(requested, 0);
    }

    boolean implies0(AdminPermission requested, int effective) {
        int desired = requested.action_mask;
        if (((effective |= this.action_mask) & desired) != desired) {
            return false;
        }
        Filter f = this.filter;
        if (f == null) {
            return true;
        }
        if (requested.bundle == null) {
            return false;
        }
        Map<String, Object> requestedProperties = requested.getProperties();
        if (requestedProperties == null) {
            return true;
        }
        return f.matches(requestedProperties);
    }

    @Override
    public String getActions() {
        String result = this.actions;
        if (result == null) {
            StringBuilder sb = new StringBuilder();
            int mask = this.action_mask;
            if ((mask & 1) == 1) {
                sb.append(CLASS);
                sb.append(',');
            }
            if ((mask & 2) == 2) {
                sb.append(EXECUTE);
                sb.append(',');
            }
            if ((mask & 0x200) == 512) {
                sb.append(EXTENSIONLIFECYCLE);
                sb.append(',');
            }
            if ((mask & 4) == 4) {
                sb.append(LIFECYCLE);
                sb.append(',');
            }
            if ((mask & 8) == 8) {
                sb.append(LISTENER);
                sb.append(',');
            }
            if ((mask & 0x10) == 16) {
                sb.append(METADATA);
                sb.append(',');
            }
            if ((mask & 0x40) == 64) {
                sb.append(RESOLVE);
                sb.append(',');
            }
            if ((mask & 0x80) == 128) {
                sb.append(RESOURCE);
                sb.append(',');
            }
            if ((mask & 0x100) == 256) {
                sb.append(STARTLEVEL);
                sb.append(',');
            }
            if ((mask & 0x400) == 1024) {
                sb.append(CONTEXT);
                sb.append(',');
            }
            if ((mask & 0x800) == 2048) {
                sb.append(WEAVE);
                sb.append(',');
            }
            if (sb.length() > 0) {
                sb.setLength(sb.length() - 1);
            }
            this.actions = result = sb.toString();
        }
        return result;
    }

    @Override
    public PermissionCollection newPermissionCollection() {
        return new AdminPermissionCollection();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AdminPermission)) {
            return false;
        }
        AdminPermission ap = (AdminPermission)obj;
        return this.action_mask == ap.action_mask && (this.bundle == ap.bundle || this.bundle != null && this.bundle.equals(ap.bundle)) && (this.filter == null ? ap.filter == null : this.filter.equals(ap.filter));
    }

    @Override
    public int hashCode() {
        int h = 527 + this.getName().hashCode();
        h = 31 * h + this.getActions().hashCode();
        if (this.bundle != null) {
            h = 31 * h + this.bundle.hashCode();
        }
        return h;
    }

    private synchronized void writeObject(ObjectOutputStream s) throws IOException {
        if (this.bundle != null) {
            throw new NotSerializableException("cannot serialize");
        }
        if (this.actions == null) {
            this.getActions();
        }
        s.defaultWriteObject();
    }

    private synchronized void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.setTransients(AdminPermission.parseFilter(this.getName()), AdminPermission.parseActions(this.actions));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Map<String, Object> getProperties() {
        Map<String, Object> result = this.properties;
        if (result != null) {
            return result;
        }
        Bundle mark = recurse.get();
        if (mark == this.bundle) {
            return null;
        }
        recurse.set(this.bundle);
        try {
            final HashMap<String, Object> map = new HashMap<String, Object>(4);
            AccessController.doPrivileged(new PrivilegedAction<Void>(){

                @Override
                public Void run() {
                    SignerProperty signer;
                    map.put("id", AdminPermission.this.bundle.getBundleId());
                    map.put("location", AdminPermission.this.bundle.getLocation());
                    String name = AdminPermission.this.bundle.getSymbolicName();
                    if (name != null) {
                        map.put("name", name);
                    }
                    if ((signer = new SignerProperty(AdminPermission.this.bundle)).isBundleSigned()) {
                        map.put("signer", signer);
                    }
                    return null;
                }
            });
            this.properties = map;
            HashMap<String, Object> hashMap = this.properties;
            return hashMap;
        }
        finally {
            recurse.set(null);
        }
    }
}

