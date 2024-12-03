/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.wiring;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.function.Function;
import org.apache.felix.framework.util.Util;
import org.apache.felix.framework.util.manifestparser.ManifestParser;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRevision;

public class BundleCapabilityImpl
implements BundleCapability {
    public static final String VERSION_ATTR = "version";
    private final BundleRevision m_revision;
    private final String m_namespace;
    private final Map<String, String> m_dirs;
    private final Map<String, Object> m_attrs;
    private final List<String> m_uses;
    private final Set<String> m_mandatory;

    public static BundleCapabilityImpl createFrom(BundleCapabilityImpl capability, Function<Object, Object> cache) {
        String namespaceI = (String)cache.apply(capability.m_namespace);
        HashMap<String, String> dirsI = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : capability.m_dirs.entrySet()) {
            dirsI.put((String)cache.apply(entry.getKey()), (String)cache.apply(entry.getValue()));
        }
        dirsI = (Map)cache.apply(dirsI);
        HashMap<String, Object> attrsI = new HashMap<String, Object>();
        for (Map.Entry<String, Object> entry : capability.m_attrs.entrySet()) {
            attrsI.put((String)cache.apply(entry.getKey()), cache.apply(entry.getValue()));
        }
        attrsI = (Map)cache.apply(attrsI);
        return new BundleCapabilityImpl(capability.m_revision, namespaceI, dirsI, attrsI);
    }

    public BundleCapabilityImpl(BundleRevision revision, String namespace, Map<String, String> dirs, Map<String, Object> attrs) {
        this.m_namespace = namespace;
        this.m_revision = revision;
        this.m_dirs = Util.newImmutableMap(dirs);
        this.m_attrs = Util.newImmutableMap(attrs);
        List uses = Collections.emptyList();
        String value = this.m_dirs.get("uses");
        if (value != null) {
            StringTokenizer tok = new StringTokenizer(value, ",");
            uses = new ArrayList(tok.countTokens());
            while (tok.hasMoreTokens()) {
                uses.add(tok.nextToken().trim().intern());
            }
        }
        this.m_uses = uses;
        Set mandatory = Collections.emptySet();
        value = this.m_dirs.get("mandatory");
        if (value != null) {
            List<String> names = ManifestParser.parseDelimitedString(value, ",");
            mandatory = new HashSet(names.size());
            for (String name : names) {
                if (this.m_attrs.containsKey(name)) {
                    mandatory.add(name.intern());
                    continue;
                }
                throw new IllegalArgumentException("Mandatory attribute '" + name + "' does not exist.");
            }
        }
        this.m_mandatory = mandatory;
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

    public boolean isAttributeMandatory(String name) {
        return !this.m_mandatory.isEmpty() && this.m_mandatory.contains(name);
    }

    public List<String> getUses() {
        return this.m_uses;
    }

    public String toString() {
        if (this.m_revision == null) {
            return this.m_attrs.toString();
        }
        return "[" + this.m_revision + "] " + this.m_namespace + "; " + this.m_attrs;
    }
}

