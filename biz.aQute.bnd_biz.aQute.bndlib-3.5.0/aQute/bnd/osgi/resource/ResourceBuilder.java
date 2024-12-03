/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.osgi.framework.Version
 */
package aQute.bnd.osgi.resource;

import aQute.bnd.build.model.EE;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Verifier;
import aQute.bnd.osgi.resource.CapReqBuilder;
import aQute.bnd.osgi.resource.CapabilityBuilder;
import aQute.bnd.osgi.resource.FilterBuilder;
import aQute.bnd.osgi.resource.RequirementBuilder;
import aQute.bnd.osgi.resource.ResourceImpl;
import aQute.bnd.version.VersionRange;
import aQute.lib.converter.Converter;
import aQute.lib.filter.Filter;
import aQute.libg.cryptography.SHA256;
import aQute.libg.reporter.ReporterAdapter;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.osgi.framework.Version;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Resource;

public class ResourceBuilder {
    private static final String BUNDLE_MIME_TYPE = "application/vnd.osgi.bundle";
    private static final String JAR_MIME_TYPE = "application/java-archive";
    private final ResourceImpl resource = new ResourceImpl();
    private final List<Capability> capabilities = new LinkedList<Capability>();
    private final List<Requirement> requirements = new LinkedList<Requirement>();
    private ReporterAdapter reporter = new ReporterAdapter();
    private boolean built = false;

    public ResourceBuilder(Resource source) throws Exception {
        this.addCapabilities(source.getCapabilities(null));
        this.addRequirements(source.getRequirements(null));
    }

    public ResourceBuilder() {
    }

    public ResourceBuilder addCapability(Capability capability) throws Exception {
        CapReqBuilder builder = CapReqBuilder.clone(capability);
        return this.addCapability(builder);
    }

    public ResourceBuilder addCapability(CapReqBuilder builder) {
        if (this.built) {
            throw new IllegalStateException("Resource already built");
        }
        this.addCapability0(builder);
        return this;
    }

    public Capability addCapability0(CapReqBuilder builder) {
        Capability cap = builder.setResource(this.resource).buildCapability();
        this.capabilities.add(cap);
        return cap;
    }

    public ResourceBuilder addRequirement(Requirement requirement) throws Exception {
        if (requirement == null) {
            return this;
        }
        CapReqBuilder builder = CapReqBuilder.clone(requirement);
        return this.addRequirement(builder);
    }

    public ResourceBuilder addRequirement(CapReqBuilder builder) {
        if (builder == null) {
            return this;
        }
        if (this.built) {
            throw new IllegalStateException("Resource already built");
        }
        Requirement req = builder.setResource(this.resource).buildRequirement();
        this.requirements.add(req);
        return this;
    }

    public Resource build() {
        if (this.built) {
            throw new IllegalStateException("Resource already built");
        }
        this.built = true;
        this.resource.setCapabilities(this.capabilities);
        this.resource.setRequirements(this.requirements);
        return this.resource;
    }

    public List<Capability> getCapabilities() {
        return this.capabilities;
    }

    public boolean addManifest(Domain manifest) throws Exception {
        String license;
        String docurl;
        String description;
        String copyright;
        int bundleManifestVersion = Integer.parseInt(manifest.get("Bundle-ManifestVersion", "1"));
        Map.Entry<String, Attrs> bsn = manifest.getBundleSymbolicName();
        if (bsn == null) {
            this.reporter.warning("No BSN set, not a bundle", new Object[0]);
            return false;
        }
        boolean singleton = "true".equals(bsn.getValue().get("singleton:"));
        boolean fragment = manifest.getFragmentHost() != null;
        String versionString = manifest.getBundleVersion();
        if (versionString == null) {
            versionString = "0";
        } else if (!aQute.bnd.version.Version.isVersion(versionString)) {
            throw new IllegalArgumentException("Invalid version in bundle " + bsn + ": " + versionString);
        }
        aQute.bnd.version.Version version = aQute.bnd.version.Version.parseVersion(versionString);
        CapReqBuilder identity = new CapReqBuilder(this.resource, "osgi.identity");
        identity.addAttribute("osgi.identity", bsn.getKey());
        identity.addAttribute("type", fragment ? "osgi.fragment" : "osgi.bundle");
        identity.addAttribute("version", version);
        if (singleton) {
            identity.addDirective("singleton", "true");
        }
        if ((copyright = manifest.get("Bundle-Copyright")) != null) {
            identity.addAttribute("copyright", copyright);
        }
        if ((description = manifest.get("Bundle-Description")) != null) {
            identity.addAttribute("description", description);
        }
        if ((docurl = manifest.get("Bundle-DocURL")) != null) {
            identity.addAttribute("documentation", docurl);
        }
        if ((license = manifest.get("Bundle-License")) != null) {
            identity.addAttribute("license", license);
        }
        this.addCapability(identity.buildCapability());
        if (bundleManifestVersion >= 2 && !fragment) {
            CapReqBuilder provideBundle = new CapReqBuilder(this.resource, "osgi.wiring.bundle");
            provideBundle.addAttributesOrDirectives(bsn.getValue());
            provideBundle.addAttribute("osgi.wiring.bundle", bsn.getKey());
            provideBundle.addAttribute("bundle-version", version);
            this.addCapability(provideBundle.buildCapability());
        }
        Parameters importServices = OSGiHeader.parseHeader(manifest.get("Import-Service"));
        this.addImportServices(importServices);
        Parameters exportServices = OSGiHeader.parseHeader(manifest.get("Export-Service"));
        this.addExportServices(exportServices);
        Parameters requireBundle = manifest.getRequireBundle();
        this.addRequireBundles(requireBundle);
        if (fragment) {
            Map.Entry<String, Attrs> fragmentHost = manifest.getFragmentHost();
            this.addFragmentHost(fragmentHost.getKey(), fragmentHost.getValue());
        } else {
            this.addFragmentHostCap(bsn.getKey(), version);
        }
        this.addExportPackages(manifest.getExportPackage());
        this.addImportPackages(manifest.getImportPackage());
        this.addProvideCapabilities(manifest.getProvideCapability());
        this.addRequireCapabilities(manifest.getRequireCapability());
        this.addRequirement(this.getNativeCode(manifest.getBundleNative()));
        return true;
    }

    public void addExportServices(Parameters exportServices) throws Exception {
        for (Map.Entry<String, Attrs> e : exportServices.entrySet()) {
            String service = Processor.removeDuplicateMarker(e.getKey());
            CapabilityBuilder cb = new CapabilityBuilder("osgi.service");
            cb.addAttributesOrDirectives(e.getValue());
            cb.addAttribute("objectClass", service);
            this.addCapability(cb);
        }
    }

    public void addImportServices(Parameters importServices) {
        for (Map.Entry<String, Attrs> e : importServices.entrySet()) {
            String service = Processor.removeDuplicateMarker(e.getKey());
            boolean optional = "optional".equals(e.getValue().get("availability:"));
            boolean multiple = "true".equalsIgnoreCase(e.getValue().get("multiple:"));
            StringBuilder filter = new StringBuilder();
            filter.append('(').append("objectClass").append('=').append(service).append(')');
            RequirementBuilder rb = new RequirementBuilder("osgi.service");
            rb.addFilter(filter.toString());
            rb.addDirective("effective", "active");
            if (optional) {
                rb.addDirective("resolution", "optional");
            }
            rb.addDirective("cardinality", multiple ? "multiple" : "single");
            this.addRequirement(rb);
        }
    }

    public RequirementBuilder getNativeCode(String header) throws Exception {
        if (header == null || header.isEmpty()) {
            return null;
        }
        Parameters bundleNative = OSGiHeader.parseHeader(header, null, new Parameters(true));
        if (bundleNative.isEmpty()) {
            return null;
        }
        boolean optional = false;
        LinkedList options = new LinkedList();
        RequirementBuilder rb = new RequirementBuilder("osgi.native");
        FilterBuilder sb = new FilterBuilder();
        sb.or();
        for (Map.Entry<String, Attrs> entry : bundleNative.entrySet()) {
            String name = Processor.removeDuplicateMarker(entry.getKey());
            if ("*".equals(name)) {
                optional = true;
                continue;
            }
            sb.and();
            ResourceBuilder.doOr(sb, "osname", "osgi.native.osname", entry.getValue());
            ResourceBuilder.doOr(sb, "processor", "osgi.native.processor", entry.getValue());
            ResourceBuilder.doOr(sb, "language", "osgi.native.language", entry.getValue());
            block13: for (String key : entry.getValue().keySet()) {
                Object value = entry.getValue().getTyped(key);
                switch (key = Processor.removeDuplicateMarker(key)) {
                    case "osname": 
                    case "processor": 
                    case "language": {
                        continue block13;
                    }
                    case "osversion": {
                        sb.eq("osgi.native.osversion", value);
                        continue block13;
                    }
                    case "selection-filter": {
                        String filter = value.toString();
                        String validateFilter = Verifier.validateFilter(filter);
                        if (validateFilter != null) {
                            this.reporter.error("Invalid 'selection-filter' on Bundle-NativeCode %s", filter);
                        }
                        sb.literal(value.toString());
                        continue block13;
                    }
                }
                this.reporter.warning("Unknown attribute on Bundle-NativeCode header %s=%s", key, value);
            }
            sb.endAnd();
        }
        sb.endOr();
        if (optional) {
            rb.addDirective("resolution", "optional");
        }
        rb.addFilter(sb.toString());
        return rb;
    }

    private static void doOr(FilterBuilder sb, String key, String attribute, Attrs attrs) throws Exception {
        sb.or();
        while (attrs.containsKey(key)) {
            String[] names;
            for (String name : names = Converter.cnv(String[].class, attrs.getTyped(key))) {
                sb.approximate(attribute, name);
            }
            key = key + "~";
        }
        sb.endOr();
    }

    public void addRequireBundles(Parameters requireBundle) throws Exception {
        for (Map.Entry<String, Attrs> clause : requireBundle.entrySet()) {
            this.addRequireBundle(Processor.removeDuplicateMarker(clause.getKey()), clause.getValue());
        }
    }

    public void addRequireBundle(String bsn, VersionRange range) throws Exception {
        Attrs attrs = new Attrs();
        attrs.put("bundle-version", range.toString());
        this.addRequireBundle(bsn, attrs);
    }

    public void addRequireBundle(String bsn, Attrs attrs) throws Exception {
        CapReqBuilder rbb = new CapReqBuilder(this.resource, "osgi.wiring.bundle");
        rbb.addDirectives(attrs);
        StringBuilder filter = new StringBuilder();
        filter.append("(").append("osgi.wiring.bundle").append("=").append(bsn).append(")");
        String v = attrs.get("bundle-version");
        if (v != null && VersionRange.isOSGiVersionRange(v)) {
            VersionRange range = VersionRange.parseOSGiVersionRange(v);
            filter.insert(0, "(&");
            filter.append(this.toBundleVersionFilter(range));
            filter.append(")");
        }
        rbb.addDirective("filter", filter.toString());
        this.addRequirement(rbb.buildRequirement());
    }

    Object toBundleVersionFilter(VersionRange range) {
        return range.toFilter().replaceAll("version", "bundle-version");
    }

    void addFragmentHostCap(String bsn, aQute.bnd.version.Version version) throws Exception {
        CapReqBuilder rbb = new CapReqBuilder(this.resource, "osgi.wiring.host");
        rbb.addAttribute("osgi.wiring.host", bsn);
        rbb.addAttribute("bundle-version", version);
        this.addCapability(rbb.buildCapability());
    }

    public void addFragmentHost(String bsn, Attrs attrs) throws Exception {
        CapReqBuilder rbb = new CapReqBuilder(this.resource, "osgi.wiring.host");
        rbb.addDirectives(attrs);
        StringBuilder filter = new StringBuilder();
        filter.append("(").append("osgi.wiring.host").append("=").append(bsn).append(")");
        String v = attrs.get("bundle-version");
        if (v != null && VersionRange.isOSGiVersionRange(v)) {
            VersionRange range = VersionRange.parseOSGiVersionRange(v);
            filter.insert(0, "(&");
            filter.append(range.toFilter("bundle-version"));
            filter.append(")");
        }
        rbb.addDirective("filter", filter.toString());
        this.addRequirement(rbb.buildRequirement());
    }

    public void addRequireCapabilities(Parameters required) throws Exception {
        for (Map.Entry<String, Attrs> clause : required.entrySet()) {
            String namespace = Processor.removeDuplicateMarker(clause.getKey());
            this.addRequireCapability(namespace, Processor.removeDuplicateMarker(clause.getKey()), clause.getValue());
        }
    }

    public void addRequireCapability(String namespace, String name, Attrs attrs) throws Exception {
        CapReqBuilder req = new CapReqBuilder(this.resource, namespace);
        req.addAttributesOrDirectives(attrs);
        this.addRequirement(req.buildRequirement());
    }

    public List<Capability> addProvideCapabilities(Parameters capabilities) throws Exception {
        ArrayList<Capability> added = new ArrayList<Capability>();
        for (Map.Entry<String, Attrs> clause : capabilities.entrySet()) {
            String namespace = Processor.removeDuplicateMarker(clause.getKey());
            Attrs attrs = clause.getValue();
            Capability addedCapability = this.addProvideCapability(namespace, attrs);
            added.add(addedCapability);
        }
        return added;
    }

    public List<Capability> addProvideCapabilities(String clauses) throws Exception {
        return this.addProvideCapabilities(new Parameters(clauses, this.reporter));
    }

    public Capability addProvideCapability(String namespace, Attrs attrs) throws Exception {
        CapReqBuilder capb = new CapReqBuilder(this.resource, namespace);
        capb.addAttributesOrDirectives(attrs);
        return this.addCapability0(capb);
    }

    public void addExportPackages(Parameters exports) throws Exception {
        for (Map.Entry<String, Attrs> clause : exports.entrySet()) {
            String pname = Processor.removeDuplicateMarker(clause.getKey());
            Attrs attrs = clause.getValue();
            this.addExportPackage(pname, attrs);
        }
    }

    public void addEE(EE ee) throws Exception {
        this.addExportPackages(ee.getPackages());
        EE[] compatibles = ee.getCompatible();
        this.addExecutionEnvironment(ee);
        for (EE compatible : compatibles) {
            this.addExecutionEnvironment(compatible);
        }
    }

    public void addExportPackage(String packageName, Attrs attrs) throws Exception {
        CapReqBuilder capb = new CapReqBuilder(this.resource, "osgi.wiring.package");
        capb.addAttributesOrDirectives(attrs);
        if (!attrs.containsKey("version")) {
            capb.addAttribute("version", Version.emptyVersion);
        }
        capb.addAttribute("osgi.wiring.package", packageName);
        this.addCapability(capb);
    }

    public void addImportPackages(Parameters imports) throws Exception {
        for (Map.Entry<String, Attrs> clause : imports.entrySet()) {
            String pname = Processor.removeDuplicateMarker(clause.getKey());
            Attrs attrs = clause.getValue();
            this.addImportPackage(pname, attrs);
        }
    }

    public Requirement addImportPackage(String pname, Attrs attrs) throws Exception {
        CapReqBuilder reqb = new CapReqBuilder(this.resource, "osgi.wiring.package");
        reqb.addDirectives(attrs);
        reqb.addFilter("osgi.wiring.package", pname, attrs.getVersion(), attrs);
        Requirement requirement = reqb.buildRequirement();
        this.addRequirement(requirement);
        return requirement;
    }

    public void addExecutionEnvironment(EE ee) throws Exception {
        CapReqBuilder builder = new CapReqBuilder(this.resource, "osgi.ee");
        builder.addAttribute("osgi.ee", ee.getCapabilityName());
        builder.addAttribute("version", ee.getCapabilityVersion());
        this.addCapability(builder);
        builder = new CapReqBuilder(this.resource, "osgi.ee");
        builder.addAttribute("osgi.ee", ee.getEEName());
        this.addCapability(builder);
    }

    public void addAllExecutionEnvironments(EE ee) throws Exception {
        this.addExportPackages(ee.getPackages());
        this.addExecutionEnvironment(ee);
        for (EE compatibleEE : ee.getCompatible()) {
            this.addExecutionEnvironment(compatibleEE);
        }
    }

    public void copyCapabilities(Set<String> ignoreNamespaces, Resource r) throws Exception {
        for (Capability c : r.getCapabilities(null)) {
            if (ignoreNamespaces.contains(c.getNamespace())) continue;
            this.addCapability(c);
        }
    }

    public void addCapabilities(List<Capability> capabilities) throws Exception {
        if (capabilities == null || capabilities.isEmpty()) {
            return;
        }
        for (Capability c : capabilities) {
            this.addCapability(c);
        }
    }

    public void addRequirement(List<Requirement> requirements) throws Exception {
        if (requirements == null || requirements.isEmpty()) {
            return;
        }
        for (Requirement rq : requirements) {
            this.addRequirement(rq);
        }
    }

    public void addRequirements(List<Requirement> requires) throws Exception {
        for (Requirement req : requires) {
            this.addRequirement(req);
        }
    }

    public List<Capability> findCapabilities(String ns, String filter) throws Exception {
        if (filter == null || this.capabilities.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Capability> capabilities = new ArrayList<Capability>();
        Filter f = new Filter(filter);
        for (Capability c : this.getCapabilities()) {
            Map<String, Object> attributes;
            if (ns != null && !ns.equals(c.getNamespace()) || (attributes = c.getAttributes()) == null || !f.matchMap(attributes)) continue;
            capabilities.add(c);
        }
        return capabilities;
    }

    public Map<Capability, Capability> from(Resource bundle) throws Exception {
        HashMap<Capability, Capability> mapping = new HashMap<Capability, Capability>();
        this.addRequirements(bundle.getRequirements(null));
        for (Capability c : bundle.getCapabilities(null)) {
            CapReqBuilder clone = CapReqBuilder.clone(c);
            Capability addedCapability = this.addCapability0(clone);
            mapping.put(c, addedCapability);
        }
        return mapping;
    }

    public Reporter getReporter() {
        return this.reporter;
    }

    public void addContentCapability(URI uri, String sha256, long length, String mime) throws Exception {
        assert (uri != null);
        assert (sha256 != null && sha256.length() == 64);
        assert (length >= 0L);
        CapabilityBuilder c = new CapabilityBuilder("osgi.content");
        c.addAttribute("osgi.content", sha256);
        c.addAttribute("url", uri.toString());
        c.addAttribute("size", length);
        c.addAttribute("mime", mime != null ? mime : BUNDLE_MIME_TYPE);
        this.addCapability(c);
    }

    public boolean addFile(File file, URI uri) throws Exception {
        if (uri == null) {
            uri = file.toURI();
        }
        Domain manifest = Domain.domain(file);
        String mime = BUNDLE_MIME_TYPE;
        boolean hasIdentity = false;
        if (manifest != null) {
            hasIdentity = this.addManifest(manifest);
        } else {
            mime = JAR_MIME_TYPE;
        }
        String sha256 = SHA256.digest(file).asHex();
        this.addContentCapability(uri, sha256, file.length(), mime);
        return hasIdentity;
    }
}

