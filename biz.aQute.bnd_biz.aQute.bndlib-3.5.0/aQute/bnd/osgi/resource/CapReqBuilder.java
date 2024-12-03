/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Version
 *  org.osgi.framework.VersionRange
 */
package aQute.bnd.osgi.resource;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.resource.CapabilityBuilder;
import aQute.bnd.osgi.resource.CapabilityImpl;
import aQute.bnd.osgi.resource.Filters;
import aQute.bnd.osgi.resource.RequirementBuilder;
import aQute.bnd.osgi.resource.RequirementImpl;
import aQute.bnd.osgi.resource.ResourceUtils;
import aQute.bnd.version.VersionRange;
import aQute.lib.converter.Converter;
import aQute.libg.filters.AndFilter;
import aQute.libg.filters.Filter;
import aQute.libg.filters.LiteralFilter;
import aQute.libg.filters.SimpleFilter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;
import org.osgi.framework.Version;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

public class CapReqBuilder {
    private static final String REQ_ALIAS_IDENTITY = "bnd.identity";
    private static final String REQ_ALIAS_IDENTITY_NAME_ATTRIB = "id";
    private static final String REQ_ALIAS_IDENTITY_VERSION_ATTRIB = "version";
    private static final String REQ_ALIAS_LITERAL = "bnd.literal";
    private static final String REQ_ALIAS_LITERAL_ATTRIB = "bnd.literal";
    private final String namespace;
    private Resource resource;
    private final Map<String, Object> attributes = new HashMap<String, Object>();
    private final Map<String, String> directives = new HashMap<String, String>();
    static Pattern ESCAPE_FILTER_VALUE_P = Pattern.compile("[\\\\()*]");

    public CapReqBuilder(String namespace) {
        this.namespace = namespace;
    }

    public CapReqBuilder(String ns, Attrs attrs) throws Exception {
        this.namespace = ns;
        for (Map.Entry<String, String> entry : attrs.entrySet()) {
            String key = entry.getKey();
            if (key.endsWith(":")) {
                this.addDirective(key.substring(0, key.length() - 1), entry.getValue());
                continue;
            }
            this.addAttribute(key, entry.getValue());
        }
    }

    public CapReqBuilder(Resource resource, String namespace) {
        this(namespace);
        this.setResource(resource);
    }

    public static CapReqBuilder clone(Capability capability) throws Exception {
        CapabilityBuilder builder = new CapabilityBuilder(capability.getNamespace());
        builder.addAttributes(capability.getAttributes());
        builder.addDirectives(capability.getDirectives());
        return builder;
    }

    public static CapReqBuilder clone(Requirement requirement) throws Exception {
        RequirementBuilder builder = new RequirementBuilder(requirement.getNamespace());
        builder.addAttributes(requirement.getAttributes());
        builder.addDirectives(requirement.getDirectives());
        return builder;
    }

    public String getNamespace() {
        return this.namespace;
    }

    public Resource getResource() {
        return this.resource;
    }

    public CapReqBuilder setResource(Resource resource) {
        this.resource = resource;
        return this;
    }

    public CapReqBuilder addAttribute(String name, Object value) throws Exception {
        if (value == null) {
            return this;
        }
        if (value.getClass().isArray()) {
            value = Converter.cnv(List.class, value);
        }
        if (REQ_ALIAS_IDENTITY_VERSION_ATTRIB.equals(name)) {
            value = this.toVersions(value);
        }
        this.attributes.put(name, value);
        return this;
    }

    public boolean isVersion(Object value) {
        if (value instanceof Version) {
            return true;
        }
        if (value instanceof Collection) {
            if (((Collection)value).isEmpty()) {
                return true;
            }
            return this.isVersion(((Collection)value).iterator().next());
        }
        if (value.getClass().isArray()) {
            if (Array.getLength(value) == 0) {
                return true;
            }
            return this.isVersion(((Object[])value)[0]);
        }
        return false;
    }

    public CapReqBuilder addAttributes(Map<? extends String, ? extends Object> attributes) throws Exception {
        for (Map.Entry<? extends String, ? extends Object> e : attributes.entrySet()) {
            this.addAttribute(e.getKey(), e.getValue());
        }
        return this;
    }

    public CapReqBuilder addDirective(String name, String value) {
        if (value == null) {
            return this;
        }
        this.directives.put(ResourceUtils.stripDirective(name), value);
        return this;
    }

    public CapReqBuilder addDirectives(Attrs directives) {
        for (Map.Entry<String, String> e : directives.entrySet()) {
            String key = Attrs.toDirective(e.getKey());
            if (key == null) continue;
            this.addDirective(key, e.getValue());
        }
        return this;
    }

    public CapReqBuilder addDirectives(Map<String, String> directives) {
        for (Map.Entry<String, String> e : directives.entrySet()) {
            this.addDirective(e.getKey(), e.getValue());
        }
        return this;
    }

    public Capability buildCapability() {
        if (this.resource == null) {
            throw new IllegalStateException("Cannot build Capability with null Resource.");
        }
        return new CapabilityImpl(this.namespace, this.resource, this.directives, this.attributes);
    }

    public Capability buildSyntheticCapability() {
        return new CapabilityImpl(this.namespace, this.resource, this.directives, this.attributes);
    }

    public Requirement buildRequirement() {
        if (this.resource == null) {
            throw new IllegalStateException("Cannot build Requirement with null Resource. use buildSyntheticRequirement");
        }
        return new RequirementImpl(this.namespace, this.resource, this.directives, this.attributes);
    }

    public Requirement buildSyntheticRequirement() {
        return new RequirementImpl(this.namespace, null, this.directives, this.attributes);
    }

    public static final CapReqBuilder createPackageRequirement(String pkgName, String range) {
        SimpleFilter pkgNameFilter = new SimpleFilter("osgi.wiring.package", pkgName);
        Filter filter = range != null ? new AndFilter().addChild(pkgNameFilter).addChild(new LiteralFilter(Filters.fromVersionRange(range))) : pkgNameFilter;
        return new CapReqBuilder("osgi.wiring.package").addDirective("filter", filter.toString());
    }

    public static CapReqBuilder createBundleRequirement(String bsn, String range) {
        SimpleFilter bsnFilter = new SimpleFilter("osgi.identity", bsn);
        Filter filter = range != null ? new AndFilter().addChild(bsnFilter).addChild(new LiteralFilter(Filters.fromVersionRange(range))) : bsnFilter;
        return new CapReqBuilder("osgi.identity").addDirective("filter", filter.toString());
    }

    public static CapReqBuilder createSimpleRequirement(String ns, String name, String range) {
        SimpleFilter bsnFilter = new SimpleFilter(ns, name);
        Filter filter = range != null ? new AndFilter().addChild(bsnFilter).addChild(new LiteralFilter(Filters.fromVersionRange(range))) : bsnFilter;
        return new CapReqBuilder(ns).addDirective("filter", filter.toString());
    }

    public CharSequence and(Object ... exprs) {
        StringBuilder sb = new StringBuilder();
        sb.append("(&");
        for (Object expr : exprs) {
            sb.append("(").append(this.toFilter(expr)).append(")");
        }
        sb.append(")");
        return sb;
    }

    public CharSequence or(Object ... exprs) {
        StringBuilder sb = new StringBuilder();
        sb.append("(|");
        for (Object expr : exprs) {
            sb.append("(").append(this.toFilter(expr)).append(")");
        }
        sb.append(")");
        return sb;
    }

    public CharSequence not(Object expr) {
        StringBuilder sb = new StringBuilder();
        sb.append("(!(").append(this.toFilter(expr)).append(")");
        return sb;
    }

    private CharSequence toFilter(Object expr) {
        if (expr instanceof CharSequence) {
            return (CharSequence)expr;
        }
        if (expr instanceof Filter) {
            return expr.toString();
        }
        if (expr instanceof VersionRange) {
            return ((VersionRange)expr).toFilter();
        }
        return expr.toString();
    }

    public CapReqBuilder filter(CharSequence f) {
        return this.addDirective("filter", f.toString());
    }

    public static List<Requirement> getRequirementsFrom(Parameters rr) throws Exception {
        return CapReqBuilder.getRequirementsFrom(rr, true);
    }

    public static List<Requirement> getRequirementsFrom(Parameters rr, boolean unalias) throws Exception {
        ArrayList<Requirement> requirements = new ArrayList<Requirement>();
        for (Map.Entry<String, Attrs> e : rr.entrySet()) {
            Requirement req = CapReqBuilder.getRequirementFrom(Processor.removeDuplicateMarker(e.getKey()), e.getValue(), unalias);
            requirements.add(req);
        }
        return requirements;
    }

    public static Requirement getRequirementFrom(String namespace, Attrs attrs) throws Exception {
        return CapReqBuilder.getRequirementFrom(namespace, attrs, true);
    }

    public static Requirement getRequirementFrom(String namespace, Attrs attrs, boolean unalias) throws Exception {
        CapReqBuilder builder = CapReqBuilder.createCapReqBuilder(namespace, attrs);
        Requirement requirement = builder.buildSyntheticRequirement();
        if (unalias) {
            requirement = CapReqBuilder.unalias(requirement);
        }
        return requirement;
    }

    public static CapReqBuilder createCapReqBuilder(String namespace, Attrs attrs) throws Exception {
        CapReqBuilder builder = new CapReqBuilder(namespace);
        for (String key : attrs.keySet()) {
            Object value;
            if (key.endsWith(":")) {
                value = attrs.get(key);
                key = key.substring(0, key.length() - 1);
                builder.addDirective(key, (String)value);
                continue;
            }
            value = attrs.getTyped(key);
            builder.addAttribute(key, value);
        }
        return builder;
    }

    private static Requirement unalias(Requirement requirement) throws Exception {
        if (requirement == null) {
            return null;
        }
        String ns = requirement.getNamespace();
        HashSet<String> consumedAttribs = new HashSet<String>();
        HashSet<String> consumedDirectives = new HashSet<String>();
        if ("bnd.literal".equals(ns)) {
            String literalNs = Objects.toString(requirement.getAttributes().get("bnd.literal"), null);
            consumedAttribs.add("bnd.literal");
            if (literalNs == null) {
                throw new IllegalArgumentException(String.format("Requirement alias %s is missing mandatory attribute '%s' of type String", "bnd.literal", "bnd.literal"));
            }
            CapReqBuilder builder = new CapReqBuilder(literalNs);
            CapReqBuilder.copyAttribs(requirement, builder, consumedAttribs);
            CapReqBuilder.copyDirectives(requirement, builder, Collections.emptySet());
            requirement = builder.buildSyntheticRequirement();
        } else if (REQ_ALIAS_IDENTITY.equals(ns)) {
            String bsn = Objects.toString(requirement.getAttributes().get(REQ_ALIAS_IDENTITY_NAME_ATTRIB), null);
            consumedAttribs.add(REQ_ALIAS_IDENTITY_NAME_ATTRIB);
            if (bsn == null) {
                throw new IllegalArgumentException(String.format("Requirement alias '%s' is missing mandatory attribute '%s' of type String", REQ_ALIAS_IDENTITY, REQ_ALIAS_IDENTITY_NAME_ATTRIB));
            }
            VersionRange range = CapReqBuilder.toRange(requirement.getAttributes().get(REQ_ALIAS_IDENTITY_VERSION_ATTRIB));
            consumedAttribs.add(REQ_ALIAS_IDENTITY_VERSION_ATTRIB);
            CapReqBuilder b = CapReqBuilder.createBundleRequirement(bsn, Objects.toString(range, null));
            CapReqBuilder.copyAttribs(requirement, b, consumedAttribs);
            CapReqBuilder.copyDirectives(requirement, b, consumedDirectives);
            requirement = b.buildSyntheticRequirement();
        }
        return requirement;
    }

    private static void copyAttribs(Requirement req, CapReqBuilder builder, Set<String> excludes) throws Exception {
        for (Map.Entry<String, Object> entry : req.getAttributes().entrySet()) {
            if (excludes.contains(entry.getKey())) continue;
            builder.addAttribute(entry.getKey(), entry.getValue());
        }
    }

    private static void copyDirectives(Requirement req, CapReqBuilder builder, Set<String> excludes) throws Exception {
        for (Map.Entry<String, String> entry : req.getDirectives().entrySet()) {
            if (excludes.contains(entry.getKey())) continue;
            builder.addDirective(entry.getKey(), entry.getValue());
        }
    }

    private static VersionRange toRange(Object o) throws IllegalArgumentException {
        VersionRange range;
        if (o == null) {
            range = null;
        } else if (o instanceof VersionRange) {
            range = (VersionRange)o;
        } else if (o instanceof org.osgi.framework.VersionRange || o instanceof Version || o instanceof String) {
            range = VersionRange.parseOSGiVersionRange(o.toString());
        } else {
            throw new IllegalArgumentException("Expected type String, Version or VersionRange");
        }
        return range;
    }

    public static List<Capability> getCapabilitiesFrom(Parameters rr) throws Exception {
        ArrayList<Capability> capabilities = new ArrayList<Capability>();
        for (Map.Entry<String, Attrs> e : rr.entrySet()) {
            capabilities.add(CapReqBuilder.getCapabilityFrom(Processor.removeDuplicateMarker(e.getKey()), e.getValue()));
        }
        return capabilities;
    }

    public static Capability getCapabilityFrom(String namespace, Attrs attrs) throws Exception {
        CapReqBuilder builder = CapReqBuilder.createCapReqBuilder(namespace, attrs);
        return builder.buildSyntheticCapability();
    }

    public CapReqBuilder from(Capability c) throws Exception {
        this.addAttributes(c.getAttributes());
        this.addDirectives(c.getDirectives());
        return this;
    }

    public CapReqBuilder from(Requirement r) throws Exception {
        this.addAttributes(r.getAttributes());
        this.addDirectives(r.getDirectives());
        return this;
    }

    public static Capability copy(Capability c, Resource r) throws Exception {
        CapReqBuilder from = new CapReqBuilder(c.getNamespace()).from(c);
        if (r == null) {
            return from.buildSyntheticCapability();
        }
        return from.setResource(r).buildCapability();
    }

    public static Requirement copy(Requirement c, Resource r) throws Exception {
        CapReqBuilder from = new CapReqBuilder(c.getNamespace()).from(c);
        if (r == null) {
            return from.buildSyntheticRequirement();
        }
        return from.setResource(r).buildRequirement();
    }

    public void addAttributesOrDirectives(Attrs attrs) throws Exception {
        for (Map.Entry<String, String> e : attrs.entrySet()) {
            String directive = Attrs.toDirective(e.getKey());
            if (directive != null) {
                this.addDirective(directive, e.getValue());
                continue;
            }
            Object typed = attrs.getTyped(e.getKey());
            if (typed instanceof aQute.bnd.version.Version) {
                typed = new Version(typed.toString());
            }
            this.addAttribute(e.getKey(), typed);
        }
    }

    public void addFilter(String ns, String name, String version, Attrs attrs) {
        String mandatory;
        ArrayList<String> parts = new ArrayList<String>();
        parts.add("(" + ns + "=" + name + ")");
        if (version != null && VersionRange.isOSGiVersionRange(version)) {
            VersionRange range = VersionRange.parseOSGiVersionRange(version);
            parts.add(range.toFilter());
        }
        if ((mandatory = attrs.get("mandatory:")) != null) {
            Object[] mandatoryAttrs = mandatory.split("\\s*,\\s*");
            Arrays.sort(mandatoryAttrs);
            for (Object mandatoryAttr : mandatoryAttrs) {
                String value = attrs.get((String)mandatoryAttr);
                if (value == null) continue;
                parts.add("(" + (String)mandatoryAttr + "=" + CapReqBuilder.escapeFilterValue(value) + ")");
            }
        }
        StringBuilder sb = new StringBuilder();
        if (parts.size() > 0) {
            sb.append("(&");
        }
        for (String s : parts) {
            sb.append(s);
        }
        if (parts.size() > 0) {
            sb.append(")");
        }
        this.addDirective("filter", sb.toString());
    }

    public static String escapeFilterValue(String value) {
        return ESCAPE_FILTER_VALUE_P.matcher(value).replaceAll("\\\\$0");
    }

    public void and(String ... s) {
        String previous = this.directives == null ? null : this.directives.get("filter");
        StringBuilder filter = new StringBuilder();
        if (previous != null) {
            filter.append("(&").append(previous);
        }
        for (String subexpr : s) {
            filter.append(subexpr);
        }
        if (previous != null) {
            filter.append(")");
        }
        this.addDirective("filter", filter.toString());
    }

    public boolean isPackage() {
        return "osgi.wiring.package".equals(this.getNamespace());
    }

    public boolean isHost() {
        return "osgi.wiring.host".equals(this.getNamespace());
    }

    public boolean isBundle() {
        return "osgi.wiring.bundle".equals(this.getNamespace());
    }

    public boolean isService() {
        return "osgi.service".equals(this.getNamespace());
    }

    public boolean isContract() {
        return "osgi.contract".equals(this.getNamespace());
    }

    public boolean isIdentity() {
        return "osgi.identity".equals(this.getNamespace());
    }

    public boolean isContent() {
        return "osgi.content".equals(this.getNamespace());
    }

    public boolean isEE() {
        return "osgi.ee".equals(this.getNamespace());
    }

    public boolean isExtender() {
        return "osgi.extender".equals(this.getNamespace());
    }

    public Attrs toAttrs() {
        Attrs attrs = new Attrs();
        if (this.attributes != null) {
            for (Map.Entry<String, Object> entry : this.attributes.entrySet()) {
                Object value = entry.getValue();
                if (entry.getKey().equals(REQ_ALIAS_IDENTITY_VERSION_ATTRIB) || value instanceof Version) {
                    value = this.toBndVersions(value);
                }
                attrs.putTyped(entry.getKey(), value);
            }
        }
        if (this.directives != null) {
            for (Map.Entry<String, Object> entry : this.directives.entrySet()) {
                attrs.put(entry.getKey() + ":", (String)entry.getValue());
            }
        }
        return attrs;
    }

    private Object toBndVersions(Object value) {
        if (value instanceof aQute.bnd.version.Version) {
            return value;
        }
        if (value instanceof Version) {
            return new aQute.bnd.version.Version(value.toString());
        }
        if (value instanceof String) {
            return new aQute.bnd.version.Version((String)value);
        }
        if (value instanceof Collection) {
            ArrayList<aQute.bnd.version.Version> bnds = new ArrayList<aQute.bnd.version.Version>();
            for (Object m : (Collection)value) {
                bnds.add((aQute.bnd.version.Version)this.toBndVersions(m));
            }
            return bnds;
        }
        throw new IllegalArgumentException("cannot convert " + value + " to a bnd Version(s) object as requested");
    }

    private Object toVersions(Object value) {
        if (value instanceof Version) {
            return value;
        }
        if (value instanceof aQute.bnd.version.Version) {
            return new Version(value.toString());
        }
        if (value instanceof String) {
            try {
                return new Version((String)value);
            }
            catch (Exception e) {
                return value;
            }
        }
        if (value instanceof Number) {
            try {
                return new Version(((Number)value).intValue(), 0, 0);
            }
            catch (Exception e) {
                return value;
            }
        }
        if (value instanceof Collection) {
            Collection v = (Collection)value;
            if (v.isEmpty()) {
                return value;
            }
            if (v.iterator().next() instanceof Version) {
                return value;
            }
            ArrayList<Version> osgis = new ArrayList<Version>();
            for (Object m : (Collection)value) {
                osgis.add((Version)this.toVersions(m));
            }
            return osgis;
        }
        throw new IllegalArgumentException("cannot convert " + value + " to a org.osgi.framework Version(s) object as requested");
    }

    public static RequirementBuilder createRequirementFromCapability(Capability cap) {
        RequirementBuilder req = new RequirementBuilder(cap.getNamespace());
        StringBuilder sb = new StringBuilder("(&");
        for (Map.Entry<String, Object> e : cap.getAttributes().entrySet()) {
            Object v = e.getValue();
            if (v instanceof Version || e.getKey().equals(REQ_ALIAS_IDENTITY_VERSION_ATTRIB)) {
                VersionRange r = new VersionRange(v.toString());
                String filter = r.toFilter();
                sb.append(filter);
                continue;
            }
            sb.append("(").append(e.getKey()).append("=").append(CapReqBuilder.escapeFilterValue((String)v)).append(")");
        }
        sb.append(")");
        req.and(sb.toString());
        return req;
    }
}

