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
import java.util.AbstractMap;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.osgi.framework.Bundle;
import org.osgi.framework.CapabilityPermissionCollection;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.SignerProperty;

public final class CapabilityPermission
extends BasicPermission {
    static final long serialVersionUID = -7662148639076511574L;
    public static final String REQUIRE = "require";
    public static final String PROVIDE = "provide";
    private static final int ACTION_REQUIRE = 1;
    private static final int ACTION_PROVIDE = 2;
    private static final int ACTION_ALL = 3;
    static final int ACTION_NONE = 0;
    transient int action_mask;
    private volatile String actions = null;
    final transient Map<String, Object> attributes;
    final transient Bundle bundle;
    transient Filter filter;
    private volatile transient Map<String, Object> properties;

    public CapabilityPermission(String name, String actions) {
        this(name, CapabilityPermission.parseActions(actions));
        if (this.filter != null && (this.action_mask & 3) != 1) {
            throw new IllegalArgumentException("invalid action string for filter expression");
        }
    }

    public CapabilityPermission(String namespace, Map<String, ?> attributes, Bundle providingBundle, String actions) {
        super(namespace);
        this.setTransients(namespace, CapabilityPermission.parseActions(actions));
        if (attributes == null) {
            throw new IllegalArgumentException("attributes must not be null");
        }
        if (providingBundle == null) {
            throw new IllegalArgumentException("bundle must not be null");
        }
        this.attributes = new HashMap(attributes);
        this.bundle = providingBundle;
        if ((this.action_mask & 3) != 1) {
            throw new IllegalArgumentException("invalid action string");
        }
    }

    CapabilityPermission(String name, int mask) {
        super(name);
        this.setTransients(name, mask);
        this.attributes = null;
        this.bundle = null;
    }

    private void setTransients(String name, int mask) {
        if (mask == 0 || (mask & 3) != mask) {
            throw new IllegalArgumentException("invalid action string");
        }
        this.action_mask = mask;
        this.filter = CapabilityPermission.parseFilter(name);
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
            if (!(i < 6 || a[i - 6] != 'r' && a[i - 6] != 'R' || a[i - 5] != 'e' && a[i - 5] != 'E' || a[i - 4] != 'q' && a[i - 4] != 'Q' || a[i - 3] != 'u' && a[i - 3] != 'U' || a[i - 2] != 'i' && a[i - 2] != 'I' || a[i - 1] != 'r' && a[i - 1] != 'R' || a[i] != 'e' && a[i] != 'E')) {
                matchlen = 7;
                mask |= 1;
            } else if (!(i < 6 || a[i - 6] != 'p' && a[i - 6] != 'P' || a[i - 5] != 'r' && a[i - 5] != 'R' || a[i - 4] != 'o' && a[i - 4] != 'O' || a[i - 3] != 'v' && a[i - 3] != 'V' || a[i - 2] != 'i' && a[i - 2] != 'I' || a[i - 1] != 'd' && a[i - 1] != 'D' || a[i] != 'e' && a[i] != 'E')) {
                matchlen = 7;
                mask |= 2;
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
        if (!(p instanceof CapabilityPermission)) {
            return false;
        }
        CapabilityPermission requested = (CapabilityPermission)p;
        if (this.bundle != null) {
            return false;
        }
        if (requested.filter != null) {
            return false;
        }
        return this.implies0(requested, 0);
    }

    boolean implies0(CapabilityPermission requested, int effective) {
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
                sb.append(REQUIRE);
                comma = true;
            }
            if ((mask & 2) == 2) {
                if (comma) {
                    sb.append(',');
                }
                sb.append(PROVIDE);
            }
            this.actions = result = sb.toString();
        }
        return result;
    }

    @Override
    public PermissionCollection newPermissionCollection() {
        return new CapabilityPermissionCollection();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CapabilityPermission)) {
            return false;
        }
        CapabilityPermission cp = (CapabilityPermission)obj;
        return this.action_mask == cp.action_mask && this.getName().equals(cp.getName()) && (this.attributes == cp.attributes || this.attributes != null && this.attributes.equals(cp.attributes)) && (this.bundle == cp.bundle || this.bundle != null && this.bundle.equals(cp.bundle));
    }

    @Override
    public int hashCode() {
        int h = 527 + this.getName().hashCode();
        h = 31 * h + this.getActions().hashCode();
        if (this.attributes != null) {
            h = 31 * h + this.attributes.hashCode();
        }
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
        this.setTransients(this.getName(), CapabilityPermission.parseActions(this.actions));
    }

    private Map<String, Object> getProperties() {
        Map<String, Object> result = this.properties;
        if (result != null) {
            return result;
        }
        final HashMap<String, Object> props = new HashMap<String, Object>(5);
        props.put("capability.namespace", this.getName());
        if (this.bundle == null) {
            this.properties = props;
            return this.properties;
        }
        AccessController.doPrivileged(new PrivilegedAction<Void>(){

            @Override
            public Void run() {
                SignerProperty signer;
                props.put("id", CapabilityPermission.this.bundle.getBundleId());
                props.put("location", CapabilityPermission.this.bundle.getLocation());
                String name = CapabilityPermission.this.bundle.getSymbolicName();
                if (name != null) {
                    props.put("name", name);
                }
                if ((signer = new SignerProperty(CapabilityPermission.this.bundle)).isBundleSigned()) {
                    props.put("signer", signer);
                }
                return null;
            }
        });
        this.properties = new Properties(props, this.attributes);
        return this.properties;
    }

    private static final class Properties
    extends AbstractMap<String, Object> {
        private final Map<String, Object> properties;
        private final Map<String, Object> attributes;
        private volatile transient Set<Map.Entry<String, Object>> entries;

        Properties(Map<String, Object> properties, Map<String, Object> attributes) {
            this.properties = properties;
            this.attributes = attributes;
            this.entries = null;
        }

        @Override
        public Object get(Object k) {
            if (!(k instanceof String)) {
                return null;
            }
            String key = (String)k;
            if (key.charAt(0) == '@') {
                return this.attributes.get(key.substring(1));
            }
            Object value = this.properties.get(key);
            if (value != null) {
                return value;
            }
            return this.attributes.get(key);
        }

        @Override
        public Set<Map.Entry<String, Object>> entrySet() {
            if (this.entries != null) {
                return this.entries;
            }
            HashSet<Map.Entry<String, Object>> all = new HashSet<Map.Entry<String, Object>>(this.attributes.size() + this.properties.size());
            all.addAll(this.attributes.entrySet());
            all.addAll(this.properties.entrySet());
            this.entries = Collections.unmodifiableSet(all);
            return this.entries;
        }
    }
}

