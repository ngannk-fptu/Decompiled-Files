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
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.PackagePermissionCollection;
import org.osgi.framework.SignerProperty;

public final class PackagePermission
extends BasicPermission {
    static final long serialVersionUID = -5107705877071099135L;
    public static final String EXPORT = "export";
    public static final String EXPORTONLY = "exportonly";
    public static final String IMPORT = "import";
    private static final int ACTION_EXPORT = 1;
    private static final int ACTION_IMPORT = 2;
    private static final int ACTION_ALL = 3;
    static final int ACTION_NONE = 0;
    transient int action_mask;
    private volatile String actions = null;
    final transient Bundle bundle;
    transient Filter filter;
    private volatile transient Map<String, Object> properties;

    public PackagePermission(String name, String actions) {
        this(name, PackagePermission.parseActions(actions));
        if (this.filter != null && (this.action_mask & 3) != 2) {
            throw new IllegalArgumentException("invalid action string for filter expression");
        }
    }

    public PackagePermission(String name, Bundle exportingBundle, String actions) {
        super(name);
        this.setTransients(name, PackagePermission.parseActions(actions));
        this.bundle = exportingBundle;
        if (exportingBundle == null) {
            throw new IllegalArgumentException("bundle must not be null");
        }
        if (this.filter != null) {
            throw new IllegalArgumentException("invalid name");
        }
        if ((this.action_mask & 3) != 2) {
            throw new IllegalArgumentException("invalid action string");
        }
    }

    PackagePermission(String name, int mask) {
        super(name);
        this.setTransients(name, mask);
        this.bundle = null;
    }

    private void setTransients(String name, int mask) {
        if (mask == 0 || (mask & 3) != mask) {
            throw new IllegalArgumentException("invalid action string");
        }
        this.action_mask = mask;
        this.filter = PackagePermission.parseFilter(name);
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
            if (!(i < 5 || a[i - 5] != 'i' && a[i - 5] != 'I' || a[i - 4] != 'm' && a[i - 4] != 'M' || a[i - 3] != 'p' && a[i - 3] != 'P' || a[i - 2] != 'o' && a[i - 2] != 'O' || a[i - 1] != 'r' && a[i - 1] != 'R' || a[i] != 't' && a[i] != 'T')) {
                matchlen = 6;
                mask |= 2;
            } else if (!(i < 5 || a[i - 5] != 'e' && a[i - 5] != 'E' || a[i - 4] != 'x' && a[i - 4] != 'X' || a[i - 3] != 'p' && a[i - 3] != 'P' || a[i - 2] != 'o' && a[i - 2] != 'O' || a[i - 1] != 'r' && a[i - 1] != 'R' || a[i] != 't' && a[i] != 'T')) {
                matchlen = 6;
                mask |= 3;
            } else if (!(i < 9 || a[i - 9] != 'e' && a[i - 9] != 'E' || a[i - 8] != 'x' && a[i - 8] != 'X' || a[i - 7] != 'p' && a[i - 7] != 'P' || a[i - 6] != 'o' && a[i - 6] != 'O' || a[i - 5] != 'r' && a[i - 5] != 'R' || a[i - 4] != 't' && a[i - 4] != 'T' || a[i - 3] != 'o' && a[i - 3] != 'O' || a[i - 2] != 'n' && a[i - 2] != 'N' || a[i - 1] != 'l' && a[i - 1] != 'L' || a[i] != 'y' && a[i] != 'Y')) {
                matchlen = 10;
                mask |= 1;
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
        if ((filterString = filterString.trim()).charAt(0) != '(') {
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
        if (!(p instanceof PackagePermission)) {
            return false;
        }
        PackagePermission requested = (PackagePermission)p;
        if (this.bundle != null) {
            return false;
        }
        if (requested.filter != null) {
            return false;
        }
        return this.implies0(requested, 0);
    }

    boolean implies0(PackagePermission requested, int effective) {
        int desired = requested.action_mask;
        if (((effective |= this.action_mask) & desired) != desired) {
            return false;
        }
        Filter f = this.filter;
        if (f == null) {
            return super.implies(requested);
        }
        return f.matches(requested.getProperties());
    }

    @Override
    public String getActions() {
        String result = this.actions;
        if (result == null) {
            StringBuilder sb = new StringBuilder();
            boolean comma = false;
            int mask = this.action_mask;
            if ((mask & 1) == 1) {
                sb.append(EXPORTONLY);
                comma = true;
            }
            if ((mask & 2) == 2) {
                if (comma) {
                    sb.append(',');
                }
                sb.append(IMPORT);
            }
            this.actions = result = sb.toString();
        }
        return result;
    }

    @Override
    public PermissionCollection newPermissionCollection() {
        return new PackagePermissionCollection();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PackagePermission)) {
            return false;
        }
        PackagePermission pp = (PackagePermission)obj;
        return this.action_mask == pp.action_mask && this.getName().equals(pp.getName()) && (this.bundle == pp.bundle || this.bundle != null && this.bundle.equals(pp.bundle));
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
        this.setTransients(this.getName(), PackagePermission.parseActions(this.actions));
    }

    private Map<String, Object> getProperties() {
        Map<String, Object> result = this.properties;
        if (result != null) {
            return result;
        }
        final HashMap<String, Object> map = new HashMap<String, Object>(5);
        map.put("package.name", this.getName());
        if (this.bundle != null) {
            AccessController.doPrivileged(new PrivilegedAction<Void>(){

                @Override
                public Void run() {
                    SignerProperty signer;
                    map.put("id", PackagePermission.this.bundle.getBundleId());
                    map.put("location", PackagePermission.this.bundle.getLocation());
                    String name = PackagePermission.this.bundle.getSymbolicName();
                    if (name != null) {
                        map.put("name", name);
                    }
                    if ((signer = new SignerProperty(PackagePermission.this.bundle)).isBundleSigned()) {
                        map.put("signer", signer);
                    }
                    return null;
                }
            });
        }
        this.properties = map;
        return this.properties;
    }
}

