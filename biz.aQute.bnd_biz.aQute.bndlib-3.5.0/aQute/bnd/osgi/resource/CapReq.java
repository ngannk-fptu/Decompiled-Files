/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi.resource;

import aQute.bnd.osgi.resource.ResourceUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

class CapReq {
    private final MODE mode;
    private final String namespace;
    private final Resource resource;
    private final Map<String, String> directives;
    private final Map<String, Object> attributes;
    private transient int hashCode = 0;

    CapReq(MODE mode, String namespace, Resource resource, Map<String, String> directives, Map<String, Object> attributes) {
        this.mode = ResourceUtils.requireNonNull(mode);
        this.namespace = ResourceUtils.requireNonNull(namespace);
        this.resource = resource;
        this.directives = Collections.unmodifiableMap(new HashMap<String, String>(directives));
        this.attributes = Collections.unmodifiableMap(new HashMap<String, Object>(attributes));
    }

    public String getNamespace() {
        return this.namespace;
    }

    public Map<String, String> getDirectives() {
        return this.directives;
    }

    public Map<String, Object> getAttributes() {
        return this.attributes;
    }

    public Resource getResource() {
        return this.resource;
    }

    public int hashCode() {
        if (this.hashCode != 0) {
            return this.hashCode;
        }
        int prime = 31;
        int result = 1;
        result = 31 * result + this.attributes.hashCode();
        result = 31 * result + this.directives.hashCode();
        result = 31 * result + this.mode.hashCode();
        result = 31 * result + this.namespace.hashCode();
        this.hashCode = result = 31 * result + (this.resource == null ? 0 : this.resource.hashCode());
        return this.hashCode;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof CapReq) {
            return this.equalsNative((CapReq)obj);
        }
        if (this.mode == MODE.Capability && obj instanceof Capability) {
            return this.equalsCap((Capability)obj);
        }
        if (this.mode == MODE.Requirement && obj instanceof Requirement) {
            return this.equalsReq((Requirement)obj);
        }
        return false;
    }

    private boolean equalsCap(Capability other) {
        if (!this.namespace.equals(other.getNamespace())) {
            return false;
        }
        if (!this.attributes.equals(other.getAttributes())) {
            return false;
        }
        if (!this.directives.equals(other.getDirectives())) {
            return false;
        }
        return this.resource == null ? other.getResource() == null : this.resource.equals(other.getResource());
    }

    private boolean equalsNative(CapReq other) {
        if (this.mode != other.mode) {
            return false;
        }
        if (!this.namespace.equals(other.namespace)) {
            return false;
        }
        if (!this.attributes.equals(other.attributes)) {
            return false;
        }
        if (!this.directives.equals(other.directives)) {
            return false;
        }
        return this.resource == null ? other.resource == null : this.resource.equals(other.resource);
    }

    private boolean equalsReq(Requirement other) {
        if (!this.namespace.equals(other.getNamespace())) {
            return false;
        }
        if (!this.attributes.equals(other.getAttributes())) {
            return false;
        }
        if (!this.directives.equals(other.getDirectives())) {
            return false;
        }
        return this.resource == null ? other.getResource() == null : this.resource.equals(other.getResource());
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.mode == MODE.Capability) {
            Object value = this.attributes.get(this.namespace);
            builder.append(this.namespace).append('=').append(value);
        } else {
            String filter = this.directives.get("filter");
            builder.append(filter);
            if ("optional".equals(this.directives.get("resolution"))) {
                builder.append("%OPT");
            }
        }
        return builder.toString();
    }

    protected void toString(StringBuilder sb) {
        sb.append("[").append(this.namespace).append("]");
        sb.append(this.attributes);
        sb.append(this.directives);
    }

    static enum MODE {
        Capability,
        Requirement;

    }
}

