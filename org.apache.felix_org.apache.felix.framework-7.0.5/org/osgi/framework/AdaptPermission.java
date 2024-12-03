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
import org.osgi.framework.AdaptPermissionCollection;
import org.osgi.framework.Bundle;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.SignerProperty;

public final class AdaptPermission
extends BasicPermission {
    private static final long serialVersionUID = 1L;
    public static final String ADAPT = "adapt";
    private static final int ACTION_ADAPT = 1;
    private static final int ACTION_ALL = 1;
    static final int ACTION_NONE = 0;
    transient int action_mask;
    private volatile String actions = null;
    final transient Bundle bundle;
    transient Filter filter;
    private volatile transient Map<String, Object> properties;

    public AdaptPermission(String filter, String actions) {
        this(AdaptPermission.parseFilter(filter), AdaptPermission.parseActions(actions));
    }

    public AdaptPermission(String adaptClass, Bundle adaptableBundle, String actions) {
        super(adaptClass);
        this.setTransients(null, AdaptPermission.parseActions(actions));
        this.bundle = adaptableBundle;
        if (adaptClass == null) {
            throw new NullPointerException("adaptClass must not be null");
        }
        if (adaptableBundle == null) {
            throw new NullPointerException("adaptableBundle must not be null");
        }
    }

    AdaptPermission(Filter filter, int mask) {
        super(filter == null ? "*" : filter.toString());
        this.setTransients(filter, mask);
        this.bundle = null;
    }

    private void setTransients(Filter filter, int mask) {
        this.filter = filter;
        if (mask == 0 || (mask & 1) != mask) {
            throw new IllegalArgumentException("invalid action string");
        }
        this.action_mask = mask;
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
            if (!(i < 4 || a[i - 4] != 'a' && a[i - 4] != 'A' || a[i - 3] != 'd' && a[i - 3] != 'D' || a[i - 2] != 'a' && a[i - 2] != 'A' || a[i - 1] != 'p' && a[i - 1] != 'P' || a[i] != 't' && a[i] != 'T')) {
                matchlen = 5;
                mask |= 1;
            } else {
                throw new IllegalArgumentException("invalid actions: " + actions);
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
            throw new IllegalArgumentException("invalid actions: " + actions);
        }
        return mask;
    }

    private static Filter parseFilter(String filterString) {
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
        if (!(p instanceof AdaptPermission)) {
            return false;
        }
        AdaptPermission requested = (AdaptPermission)p;
        if (this.bundle != null) {
            return false;
        }
        if (requested.filter != null) {
            return false;
        }
        return this.implies0(requested, 0);
    }

    boolean implies0(AdaptPermission requested, int effective) {
        int desired = requested.action_mask;
        if (((effective |= this.action_mask) & desired) != desired) {
            return false;
        }
        Filter f = this.filter;
        if (f == null) {
            return true;
        }
        return f.matches(requested.getProperties());
    }

    @Override
    public String getActions() {
        String result = this.actions;
        if (result == null) {
            result = ADAPT;
            this.actions = ADAPT;
        }
        return result;
    }

    @Override
    public PermissionCollection newPermissionCollection() {
        return new AdaptPermissionCollection();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AdaptPermission)) {
            return false;
        }
        AdaptPermission cp = (AdaptPermission)obj;
        return this.action_mask == cp.action_mask && this.getName().equals(cp.getName()) && (this.bundle == cp.bundle || this.bundle != null && this.bundle.equals(cp.bundle));
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
        this.setTransients(AdaptPermission.parseFilter(this.getName()), AdaptPermission.parseActions(this.actions));
    }

    private Map<String, Object> getProperties() {
        Map<String, Object> result = this.properties;
        if (result != null) {
            return result;
        }
        final HashMap<String, Object> map = new HashMap<String, Object>(5);
        map.put("adaptClass", this.getName());
        if (this.bundle != null) {
            AccessController.doPrivileged(new PrivilegedAction<Void>(){

                @Override
                public Void run() {
                    SignerProperty signer;
                    map.put("id", AdaptPermission.this.bundle.getBundleId());
                    map.put("location", AdaptPermission.this.bundle.getLocation());
                    String name = AdaptPermission.this.bundle.getSymbolicName();
                    if (name != null) {
                        map.put("name", name);
                    }
                    if ((signer = new SignerProperty(AdaptPermission.this.bundle)).isBundleSigned()) {
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

