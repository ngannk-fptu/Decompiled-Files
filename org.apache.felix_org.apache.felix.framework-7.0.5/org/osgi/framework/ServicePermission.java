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
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServicePermissionCollection;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.SignerProperty;

public final class ServicePermission
extends BasicPermission {
    static final long serialVersionUID = -7662148639076511574L;
    public static final String GET = "get";
    public static final String REGISTER = "register";
    private static final int ACTION_GET = 1;
    private static final int ACTION_REGISTER = 2;
    private static final int ACTION_ALL = 3;
    static final int ACTION_NONE = 0;
    transient int action_mask;
    private volatile String actions = null;
    final transient ServiceReference<?> service;
    final transient String[] objectClass;
    transient Filter filter;
    private volatile transient Map<String, Object> properties;
    private transient boolean wildcard;
    private transient String prefix;

    public ServicePermission(String name, String actions) {
        this(name, ServicePermission.parseActions(actions));
        if (this.filter != null && (this.action_mask & 3) != 1) {
            throw new IllegalArgumentException("invalid action string for filter expression");
        }
    }

    public ServicePermission(ServiceReference<?> reference, String actions) {
        super(ServicePermission.createName(reference));
        this.setTransients(null, ServicePermission.parseActions(actions));
        this.service = reference;
        this.objectClass = (String[])reference.getProperty("objectClass");
        if ((this.action_mask & 3) != 1) {
            throw new IllegalArgumentException("invalid action string");
        }
    }

    private static String createName(ServiceReference<?> reference) {
        if (reference == null) {
            throw new IllegalArgumentException("reference must not be null");
        }
        StringBuilder sb = new StringBuilder("(service.id=");
        sb.append(reference.getProperty("service.id"));
        sb.append(")");
        return sb.toString();
    }

    ServicePermission(String name, int mask) {
        super(name);
        this.setTransients(ServicePermission.parseFilter(name), mask);
        this.service = null;
        this.objectClass = null;
    }

    private void setTransients(Filter f, int mask) {
        if (mask == 0 || (mask & 3) != mask) {
            throw new IllegalArgumentException("invalid action string");
        }
        this.action_mask = mask;
        this.filter = f;
        if (f == null) {
            int l;
            String name = this.getName();
            boolean bl = this.wildcard = name.charAt((l = name.length()) - 1) == '*' && (l == 1 || name.charAt(l - 2) == '.');
            if (this.wildcard && l > 1) {
                this.prefix = name.substring(0, l - 1);
            }
        }
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
            if (!(i < 2 || a[i - 2] != 'g' && a[i - 2] != 'G' || a[i - 1] != 'e' && a[i - 1] != 'E' || a[i] != 't' && a[i] != 'T')) {
                matchlen = 3;
                mask |= 1;
            } else if (!(i < 7 || a[i - 7] != 'r' && a[i - 7] != 'R' || a[i - 6] != 'e' && a[i - 6] != 'E' || a[i - 5] != 'g' && a[i - 5] != 'G' || a[i - 4] != 'i' && a[i - 4] != 'I' || a[i - 3] != 's' && a[i - 3] != 'S' || a[i - 2] != 't' && a[i - 2] != 'T' || a[i - 1] != 'e' && a[i - 1] != 'E' || a[i] != 'r' && a[i] != 'R')) {
                matchlen = 8;
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
        if (!(p instanceof ServicePermission)) {
            return false;
        }
        ServicePermission requested = (ServicePermission)p;
        if (this.service != null) {
            return false;
        }
        if (requested.filter != null) {
            return false;
        }
        return this.implies0(requested, 0);
    }

    boolean implies0(ServicePermission requested, int effective) {
        int desired = requested.action_mask;
        if (((effective |= this.action_mask) & desired) != desired) {
            return false;
        }
        if (this.wildcard && this.prefix == null) {
            return true;
        }
        Filter f = this.filter;
        if (f != null) {
            return f.matches(requested.getProperties());
        }
        String[] requestedNames = requested.objectClass;
        if (requestedNames == null) {
            return super.implies(requested);
        }
        if (this.wildcard) {
            int pl = this.prefix.length();
            for (String requestedName : requestedNames) {
                if (requestedName.length() <= pl || !requestedName.startsWith(this.prefix)) continue;
                return true;
            }
        } else {
            String name = this.getName();
            int l = requestedNames.length;
            for (int i = 0; i < l; ++i) {
                if (!requestedNames[i].equals(name)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public String getActions() {
        String result = this.actions;
        if (result == null) {
            StringBuilder sb = new StringBuilder();
            boolean comma = false;
            int mask = this.action_mask;
            if ((mask & 1) == 1) {
                sb.append(GET);
                comma = true;
            }
            if ((mask & 2) == 2) {
                if (comma) {
                    sb.append(',');
                }
                sb.append(REGISTER);
            }
            this.actions = result = sb.toString();
        }
        return result;
    }

    @Override
    public PermissionCollection newPermissionCollection() {
        return new ServicePermissionCollection();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ServicePermission)) {
            return false;
        }
        ServicePermission sp = (ServicePermission)obj;
        return this.action_mask == sp.action_mask && this.getName().equals(sp.getName()) && (this.service == sp.service || this.service != null && this.service.compareTo(sp.service) == 0);
    }

    @Override
    public int hashCode() {
        int h = 527 + this.getName().hashCode();
        h = 31 * h + this.getActions().hashCode();
        if (this.service != null) {
            h = 31 * h + this.service.hashCode();
        }
        return h;
    }

    private synchronized void writeObject(ObjectOutputStream s) throws IOException {
        if (this.service != null) {
            throw new NotSerializableException("cannot serialize");
        }
        if (this.actions == null) {
            this.getActions();
        }
        s.defaultWriteObject();
    }

    private synchronized void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        s.defaultReadObject();
        this.setTransients(ServicePermission.parseFilter(this.getName()), ServicePermission.parseActions(this.actions));
    }

    private Map<String, Object> getProperties() {
        Map<String, Object> result = this.properties;
        if (result != null) {
            return result;
        }
        if (this.service == null) {
            result = new HashMap<String, Object>(1);
            result.put("objectClass", new String[]{this.getName()});
            this.properties = result;
            return this.properties;
        }
        final HashMap<String, Object> props = new HashMap<String, Object>(4);
        final Bundle bundle = this.service.getBundle();
        if (bundle != null) {
            AccessController.doPrivileged(new PrivilegedAction<Void>(){

                @Override
                public Void run() {
                    SignerProperty signer;
                    props.put("id", bundle.getBundleId());
                    props.put("location", bundle.getLocation());
                    String name = bundle.getSymbolicName();
                    if (name != null) {
                        props.put("name", name);
                    }
                    if ((signer = new SignerProperty(bundle)).isBundleSigned()) {
                        props.put("signer", signer);
                    }
                    return null;
                }
            });
        }
        this.properties = new Properties(props, this.service);
        return this.properties;
    }

    private static final class Properties
    extends AbstractMap<String, Object> {
        private final Map<String, Object> properties;
        private final ServiceReference<?> service;
        private volatile transient Set<Map.Entry<String, Object>> entries;

        Properties(Map<String, Object> properties, ServiceReference<?> service) {
            this.properties = properties;
            this.service = service;
            this.entries = null;
        }

        @Override
        public Object get(Object k) {
            if (!(k instanceof String)) {
                return null;
            }
            String key = (String)k;
            if (key.charAt(0) == '@') {
                return this.service.getProperty(key.substring(1));
            }
            Object value = this.properties.get(key);
            if (value != null) {
                return value;
            }
            return this.service.getProperty(key);
        }

        @Override
        public Set<Map.Entry<String, Object>> entrySet() {
            if (this.entries != null) {
                return this.entries;
            }
            HashSet<Map.Entry<String, Object>> all = new HashSet<Map.Entry<String, Object>>(this.properties.entrySet());
            block0: for (String key : this.service.getPropertyKeys()) {
                for (String k : this.properties.keySet()) {
                    if (!key.equalsIgnoreCase(k)) continue;
                    continue block0;
                }
                all.add(new Entry(key, this.service.getProperty(key)));
            }
            this.entries = Collections.unmodifiableSet(all);
            return this.entries;
        }

        private static final class Entry
        implements Map.Entry<String, Object> {
            private final String k;
            private final Object v;

            Entry(String key, Object value) {
                this.k = key;
                this.v = value;
            }

            @Override
            public String getKey() {
                return this.k;
            }

            @Override
            public Object getValue() {
                return this.v;
            }

            @Override
            public Object setValue(Object value) {
                throw new UnsupportedOperationException();
            }

            public String toString() {
                return this.k + "=" + this.v;
            }

            @Override
            public int hashCode() {
                return (this.k == null ? 0 : this.k.hashCode()) ^ (this.v == null ? 0 : this.v.hashCode());
            }

            @Override
            public boolean equals(Object obj) {
                Object value;
                if (obj == this) {
                    return true;
                }
                if (!(obj instanceof Map.Entry)) {
                    return false;
                }
                Map.Entry e = (Map.Entry)obj;
                Object key = e.getKey();
                return (this.k == key || this.k != null && this.k.equals(key)) && (this.v == (value = e.getValue()) || this.v != null && this.v.equals(value));
            }
        }
    }
}

