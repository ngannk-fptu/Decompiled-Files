/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.wiring;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import org.apache.felix.framework.capabilityset.CapabilitySet;
import org.apache.felix.framework.capabilityset.SimpleFilter;
import org.apache.felix.framework.util.Util;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;

public class BundleRequirementImpl
implements BundleRequirement {
    private final BundleRevision m_revision;
    private final String m_namespace;
    private final SimpleFilter m_filter;
    private final boolean m_optional;
    private final Map<String, String> m_dirs;
    private final Map<String, Object> m_attrs;

    public static BundleRequirementImpl createFrom(BundleRequirementImpl requirement, Function<Object, Object> cache) {
        String namespaceI = (String)cache.apply(requirement.m_namespace);
        HashMap<String, String> dirsI = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : requirement.m_dirs.entrySet()) {
            dirsI.put((String)cache.apply(entry.getKey()), (String)cache.apply(entry.getValue()));
        }
        dirsI = (Map)cache.apply(dirsI);
        HashMap<String, Object> attrsI = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : requirement.m_attrs.entrySet()) {
            attrsI.put((String)cache.apply(entry.getKey()), cache.apply(entry.getValue()));
        }
        attrsI = (Map)cache.apply(attrsI);
        SimpleFilter simpleFilter = (SimpleFilter)cache.apply(requirement.m_filter);
        return new BundleRequirementImpl(requirement.m_revision, namespaceI, dirsI, attrsI, simpleFilter);
    }

    public BundleRequirementImpl(BundleRevision revision, String namespace, Map<String, String> dirs, Map<String, Object> attrs, SimpleFilter filter) {
        this.m_revision = revision;
        this.m_namespace = namespace;
        this.m_dirs = Util.newImmutableMap(dirs);
        this.m_attrs = Util.newImmutableMap(attrs);
        this.m_filter = filter;
        boolean optional = false;
        if (this.m_dirs.containsKey("resolution") && this.m_dirs.get("resolution").equals("optional")) {
            optional = true;
        }
        this.m_optional = optional;
    }

    public BundleRequirementImpl(BundleRevision revision, String namespace, Map<String, String> dirs, Map<String, Object> attrs) {
        this(revision, namespace, dirs, Collections.emptyMap(), SimpleFilter.convert(attrs));
    }

    @Override
    public String getNamespace() {
        return this.m_namespace;
    }

    @Override
    public Map<String, String> getDirectives() {
        return this.m_dirs;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return this.m_attrs;
    }

    @Override
    public BundleRevision getResource() {
        return this.m_revision;
    }

    @Override
    public BundleRevision getRevision() {
        return this.m_revision;
    }

    @Override
    public boolean matches(BundleCapability cap) {
        return CapabilitySet.matches(cap, this.getFilter());
    }

    public boolean isOptional() {
        return this.m_optional;
    }

    public SimpleFilter getFilter() {
        return this.m_filter;
    }

    public String toString() {
        return "[" + this.m_revision + "] " + this.m_namespace + "; " + this.getFilter().toString();
    }
}

