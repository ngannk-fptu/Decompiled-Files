/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util.manifestparser;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.apache.felix.framework.BundleRevisionImpl;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.cache.ConnectContentContent;
import org.apache.felix.framework.capabilityset.SimpleFilter;
import org.apache.felix.framework.util.manifestparser.NativeLibrary;
import org.apache.felix.framework.util.manifestparser.NativeLibraryClause;
import org.apache.felix.framework.util.manifestparser.ParsedHeaderClause;
import org.apache.felix.framework.wiring.BundleCapabilityImpl;
import org.apache.felix.framework.wiring.BundleRequirementImpl;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;

public class ManifestParser {
    private static final String BUNDLE_LICENSE_HEADER = "Bundle-License";
    private final Logger m_logger;
    private final Map<String, Object> m_configMap;
    private final Map<String, Object> m_headerMap;
    private volatile int m_activationPolicy = 0;
    private volatile String m_activationIncludeDir;
    private volatile String m_activationExcludeDir;
    private volatile boolean m_isExtension = false;
    private volatile String m_bundleSymbolicName;
    private volatile Version m_bundleVersion;
    private volatile List<BundleCapability> m_capabilities;
    private volatile List<BundleRequirement> m_requirements;
    private volatile List<NativeLibraryClause> m_libraryClauses;
    private volatile boolean m_libraryHeadersOptional = false;
    private static final Map<Object, WeakReference<Object>> objectCache = new WeakHashMap<Object, WeakReference<Object>>();
    private static final Function<Object, Object> cache = foo -> {
        if (foo instanceof String) {
            return ((String)foo).intern();
        }
        if (foo != null) {
            Map<Object, WeakReference<Object>> map = objectCache;
            synchronized (map) {
                Object refValue;
                WeakReference<Object> ref = objectCache.get(foo);
                if (ref != null && (refValue = ref.get()) != null) {
                    return refValue;
                }
                objectCache.put(foo, new WeakReference<Object>(foo));
            }
        }
        return foo;
    };
    private static final char EOF = '\uffff';
    private static final int CLAUSE_START = 0;
    private static final int PARAMETER_START = 1;
    private static final int KEY = 2;
    private static final int DIRECTIVE_OR_TYPEDATTRIBUTE = 4;
    private static final int ARGUMENT = 8;
    private static final int VALUE = 16;

    public ManifestParser(Logger logger, Map<String, Object> configMap, BundleRevision owner, Map<String, Object> headerMap) throws BundleException {
        this.m_logger = logger;
        this.m_configMap = configMap;
        this.m_headerMap = headerMap;
        String manifestVersion = ManifestParser.getManifestVersion(this.m_headerMap);
        if (manifestVersion != null && !manifestVersion.equals("2")) {
            throw new BundleException("Unknown 'Bundle-ManifestVersion' value: " + manifestVersion);
        }
        ArrayList<BundleCapabilityImpl> capList = new ArrayList<BundleCapabilityImpl>();
        this.m_bundleVersion = Version.emptyVersion;
        if (headerMap.get("Bundle-Version") != null) {
            try {
                this.m_bundleVersion = Version.parseVersion((String)headerMap.get("Bundle-Version"));
            }
            catch (RuntimeException ex) {
                if (this.getManifestVersion().equals("2")) {
                    throw ex;
                }
                this.m_bundleVersion = Version.emptyVersion;
            }
        }
        this.m_bundleVersion = (Version)cache.apply(this.m_bundleVersion);
        BundleCapabilityImpl bundleCap = ManifestParser.parseBundleSymbolicName(logger, owner, this.m_headerMap);
        if (bundleCap != null) {
            this.m_bundleSymbolicName = (String)bundleCap.getAttributes().get("osgi.wiring.bundle");
            if (headerMap.get("Fragment-Host") == null) {
                capList.add(bundleCap);
                String attachment = bundleCap.getDirectives().get("fragment-attachment");
                String string = attachment = attachment == null ? "resolve-time" : attachment;
                if (!attachment.equalsIgnoreCase("never")) {
                    HashMap<String, Object> hostAttrs = new HashMap<String, Object>(bundleCap.getAttributes());
                    Object value = hostAttrs.remove("osgi.wiring.bundle");
                    hostAttrs.put("osgi.wiring.host", value);
                    capList.add(new BundleCapabilityImpl(owner, "osgi.wiring.host", bundleCap.getDirectives(), hostAttrs));
                }
            }
            capList.add(ManifestParser.addIdentityCapability(owner, headerMap, bundleCap));
        }
        if (this.getManifestVersion().equals("2") && this.m_bundleSymbolicName == null) {
            throw new BundleException("R4 bundle manifests must include bundle symbolic name.");
        }
        this.m_isExtension = ManifestParser.checkExtensionBundle(headerMap);
        List<BundleRequirementImpl> hostReqs = ManifestParser.parseFragmentHost(this.m_logger, owner, this.m_headerMap);
        List<ParsedHeaderClause> rbClauses = ManifestParser.parseStandardHeader((String)headerMap.get("Require-Bundle"));
        rbClauses = ManifestParser.normalizeRequireClauses(this.m_logger, rbClauses, this.getManifestVersion());
        List<BundleRequirementImpl> rbReqs = ManifestParser.convertRequires(rbClauses, owner);
        List<ParsedHeaderClause> importClauses = ManifestParser.parseStandardHeader((String)headerMap.get("Import-Package"));
        importClauses = ManifestParser.normalizeImportClauses(this.m_logger, importClauses, this.getManifestVersion());
        List<BundleRequirement> importReqs = ManifestParser.convertImports(importClauses, owner);
        List<ParsedHeaderClause> dynamicClauses = ManifestParser.parseStandardHeader((String)headerMap.get("DynamicImport-Package"));
        dynamicClauses = ManifestParser.normalizeDynamicImportClauses(this.m_logger, dynamicClauses, this.getManifestVersion());
        List<BundleRequirement> dynamicReqs = ManifestParser.convertImports(dynamicClauses, owner);
        List<ParsedHeaderClause> requireClauses = ManifestParser.parseStandardHeader((String)headerMap.get("Require-Capability"));
        importClauses = ManifestParser.normalizeCapabilityClauses(this.m_logger, requireClauses, this.getManifestVersion());
        List<BundleRequirement> requireReqs = ManifestParser.convertRequireCapabilities(importClauses, owner);
        List<BundleRequirement> breeReqs = ManifestParser.parseBreeHeader((String)headerMap.get("Bundle-RequiredExecutionEnvironment"), owner);
        List<ParsedHeaderClause> exportClauses = ManifestParser.parseStandardHeader((String)headerMap.get("Export-Package"));
        exportClauses = ManifestParser.normalizeExportClauses(logger, exportClauses, this.getManifestVersion(), this.m_bundleSymbolicName, this.m_bundleVersion, owner instanceof BundleRevisionImpl && ((BundleRevisionImpl)owner).getContent() instanceof ConnectContentContent);
        List<BundleCapability> exportCaps = ManifestParser.convertExports(exportClauses, owner);
        List<ParsedHeaderClause> provideClauses = ManifestParser.parseStandardHeader((String)headerMap.get("Provide-Capability"));
        provideClauses = ManifestParser.normalizeCapabilityClauses(logger, provideClauses, this.getManifestVersion());
        List<BundleCapability> provideCaps = ManifestParser.convertProvideCapabilities(provideClauses, owner);
        if (!this.getManifestVersion().equals("2")) {
            List<ParsedHeaderClause> implicitClauses = ManifestParser.calculateImplicitImports(exportCaps, importClauses);
            importReqs.addAll(ManifestParser.convertImports(implicitClauses, owner));
            ArrayList<ParsedHeaderClause> allImportClauses = new ArrayList<ParsedHeaderClause>(implicitClauses.size() + importClauses.size());
            allImportClauses.addAll(importClauses);
            allImportClauses.addAll(implicitClauses);
            exportCaps = ManifestParser.calculateImplicitUses(exportCaps, allImportClauses);
        }
        this.m_libraryClauses = ManifestParser.parseLibraryStrings(this.m_logger, ManifestParser.parseDelimitedString((String)this.m_headerMap.get("Bundle-NativeCode"), ","));
        if (!this.m_libraryClauses.isEmpty() && this.m_libraryClauses.get(this.m_libraryClauses.size() - 1).getLibraryEntries() == null) {
            this.m_libraryHeadersOptional = true;
            this.m_libraryClauses.remove(this.m_libraryClauses.size() - 1);
        }
        List<BundleRequirement> nativeCodeReqs = ManifestParser.convertNativeCode(owner, this.m_libraryClauses, this.m_libraryHeadersOptional);
        this.m_requirements = new ArrayList<BundleRequirement>(hostReqs.size() + importReqs.size() + rbReqs.size() + requireReqs.size() + dynamicReqs.size() + breeReqs.size());
        this.m_requirements.addAll(hostReqs.stream().map(req -> BundleRequirementImpl.createFrom(req, cache)).collect(Collectors.toList()));
        this.m_requirements.addAll(importReqs.stream().map(req -> BundleRequirementImpl.createFrom((BundleRequirementImpl)req, cache)).collect(Collectors.toList()));
        this.m_requirements.addAll(rbReqs.stream().map(req -> BundleRequirementImpl.createFrom(req, cache)).collect(Collectors.toList()));
        this.m_requirements.addAll(requireReqs.stream().map(req -> BundleRequirementImpl.createFrom((BundleRequirementImpl)req, cache)).collect(Collectors.toList()));
        this.m_requirements.addAll(dynamicReqs.stream().map(req -> BundleRequirementImpl.createFrom((BundleRequirementImpl)req, cache)).collect(Collectors.toList()));
        this.m_requirements.addAll(breeReqs.stream().map(req -> BundleRequirementImpl.createFrom((BundleRequirementImpl)req, cache)).collect(Collectors.toList()));
        this.m_requirements.addAll(nativeCodeReqs.stream().map(req -> BundleRequirementImpl.createFrom((BundleRequirementImpl)req, cache)).collect(Collectors.toList()));
        this.m_capabilities = new ArrayList<BundleCapability>(capList.size() + exportCaps.size() + provideCaps.size());
        this.m_capabilities.addAll(capList.stream().map(cap -> BundleCapabilityImpl.createFrom(cap, cache)).collect(Collectors.toList()));
        this.m_capabilities.addAll(exportCaps.stream().map(cap -> BundleCapabilityImpl.createFrom((BundleCapabilityImpl)cap, cache)).collect(Collectors.toList()));
        this.m_capabilities.addAll(provideCaps.stream().map(cap -> BundleCapabilityImpl.createFrom((BundleCapabilityImpl)cap, cache)).collect(Collectors.toList()));
        this.parseActivationPolicy(headerMap);
    }

    private static List<ParsedHeaderClause> normalizeImportClauses(Logger logger, List<ParsedHeaderClause> clauses, String mv) throws BundleException {
        HashSet<String> dupeSet = new HashSet<String>();
        for (ParsedHeaderClause clause : clauses) {
            Object v = clause.m_attrs.get("version");
            Object sv = clause.m_attrs.get("specification-version");
            if (v != null && sv != null && !((String)v).trim().equals(((String)sv).trim())) {
                throw new IllegalArgumentException("Both version and specification-version are specified, but they are not equal.");
            }
            if (v != null || sv != null) {
                clause.m_attrs.remove("specification-version");
                v = v == null ? sv : v;
                clause.m_attrs.put("version", new VersionRange(v.toString()));
            }
            if ((v = clause.m_attrs.get("bundle-version")) != null) {
                clause.m_attrs.put("bundle-version", new VersionRange(v.toString()));
            }
            for (String pkgName : clause.m_paths) {
                if (!dupeSet.contains(pkgName)) {
                    if (pkgName.equals(".")) {
                        throw new BundleException("Imporing '.' is invalid.");
                    }
                    if (pkgName.length() == 0) {
                        throw new BundleException("Imported package names cannot be zero length.");
                    }
                    dupeSet.add(pkgName);
                    continue;
                }
                throw new BundleException("Duplicate import: " + pkgName);
            }
            if (mv.equals("2")) continue;
            if (!clause.m_dirs.isEmpty()) {
                throw new BundleException("R3 imports cannot contain directives.");
            }
            if (clause.m_attrs.isEmpty()) continue;
            Object pkgVersion = clause.m_attrs.get("version");
            pkgVersion = pkgVersion == null ? new VersionRange('[', Version.emptyVersion, null, ']') : pkgVersion;
            for (Map.Entry<String, Object> entry : clause.m_attrs.entrySet()) {
                if (entry.getKey().equals("version")) continue;
                logger.log(2, "Unknown R3 import attribute: " + entry.getKey());
            }
            clause.m_attrs.clear();
            clause.m_attrs.put("version", pkgVersion);
        }
        return clauses;
    }

    public static List<BundleRequirement> parseDynamicImportHeader(Logger logger, BundleRevision owner, String header) throws BundleException {
        List<ParsedHeaderClause> importClauses = ManifestParser.parseStandardHeader(header);
        importClauses = ManifestParser.normalizeDynamicImportClauses(logger, importClauses, "2");
        List<BundleRequirement> reqs = ManifestParser.convertImports(importClauses, owner);
        return reqs;
    }

    private static List<BundleRequirement> convertImports(List<ParsedHeaderClause> clauses, BundleRevision owner) {
        ArrayList<BundleRequirement> reqList = new ArrayList<BundleRequirement>();
        for (ParsedHeaderClause clause : clauses) {
            for (String path : clause.m_paths) {
                Map<String, Object> attrs = clause.m_attrs;
                LinkedHashMap<String, Object> newAttrs = new LinkedHashMap<String, Object>(attrs.size() + 1);
                newAttrs.put("osgi.wiring.package", path);
                newAttrs.putAll(attrs);
                newAttrs.put("osgi.wiring.package", path);
                SimpleFilter sf = SimpleFilter.convert(newAttrs);
                Map<String, String> dirs = clause.m_dirs;
                HashMap<String, String> newDirs = new HashMap<String, String>(dirs.size() + 1);
                newDirs.putAll(dirs);
                newDirs.put("filter", sf.toString());
                reqList.add(new BundleRequirementImpl(owner, "osgi.wiring.package", newDirs, Collections.EMPTY_MAP, sf));
            }
        }
        return reqList;
    }

    private static List<ParsedHeaderClause> normalizeDynamicImportClauses(Logger logger, List<ParsedHeaderClause> clauses, String mv) throws BundleException {
        for (ParsedHeaderClause clause : clauses) {
            if (!mv.equals("2") && !clause.m_dirs.isEmpty()) {
                throw new BundleException("R3 imports cannot contain directives.");
            }
            clause.m_dirs.put("resolution", "dynamic");
            Object v = clause.m_attrs.get("version");
            Object sv = clause.m_attrs.get("specification-version");
            if (v != null && sv != null && !((String)v).trim().equals(((String)sv).trim())) {
                throw new IllegalArgumentException("Both version and specification-version are specified, but they are not equal.");
            }
            if (v != null || sv != null) {
                clause.m_attrs.remove("specification-version");
                v = v == null ? sv : v;
                clause.m_attrs.put("version", new VersionRange(v.toString()));
            }
            if ((v = clause.m_attrs.get("bundle-version")) != null) {
                clause.m_attrs.put("bundle-version", new VersionRange(v.toString()));
            }
            for (String pkgName : clause.m_paths) {
                if (pkgName.equals("*") || !pkgName.endsWith("*") || pkgName.endsWith(".*")) continue;
                throw new BundleException("Partial package name wild carding is not allowed: " + pkgName);
            }
        }
        return clauses;
    }

    private static List<BundleRequirement> convertRequireCapabilities(List<ParsedHeaderClause> clauses, BundleRevision owner) throws BundleException {
        ArrayList<BundleRequirement> reqList = new ArrayList<BundleRequirement>();
        for (ParsedHeaderClause clause : clauses) {
            try {
                String filterStr = clause.m_dirs.get("filter");
                SimpleFilter sf = filterStr != null ? SimpleFilter.parse(filterStr) : new SimpleFilter(null, null, 0);
                for (String path : clause.m_paths) {
                    if (path.startsWith("osgi.wiring.")) {
                        throw new BundleException("Manifest cannot use Require-Capability for '" + path + "' namespace.");
                    }
                    reqList.add(new BundleRequirementImpl(owner, path, clause.m_dirs, clause.m_attrs, sf));
                }
            }
            catch (Exception ex) {
                throw new BundleException("Error creating requirement: " + ex);
            }
        }
        return reqList;
    }

    static List<BundleRequirement> convertNativeCode(BundleRevision owner, List<NativeLibraryClause> nativeLibraryClauses, boolean hasOptionalLibraryDirective) {
        ArrayList<BundleRequirement> result = new ArrayList<BundleRequirement>();
        ArrayList<SimpleFilter> nativeFilterClauseList = new ArrayList<SimpleFilter>();
        if (nativeLibraryClauses != null && !nativeLibraryClauses.isEmpty()) {
            for (NativeLibraryClause clause : nativeLibraryClauses) {
                String[] osNameArray = clause.getOSNames();
                String[] osVersionArray = clause.getOSVersions();
                String[] processorArray = clause.getProcessors();
                String[] languageArray = clause.getLanguages();
                String currentSelectionFilter = clause.getSelectionFilter();
                ArrayList<SimpleFilter> nativeFilterList = new ArrayList<SimpleFilter>();
                if (osNameArray != null && osNameArray.length > 0) {
                    nativeFilterList.add(ManifestParser.buildFilterFromArray("osgi.native.osname", osNameArray, 9));
                }
                if (osVersionArray != null && osVersionArray.length > 0) {
                    nativeFilterList.add(ManifestParser.buildFilterFromArray("osgi.native.osversion", osVersionArray, 4));
                }
                if (processorArray != null && processorArray.length > 0) {
                    nativeFilterList.add(ManifestParser.buildFilterFromArray("osgi.native.processor", processorArray, 9));
                }
                if (languageArray != null && languageArray.length > 0) {
                    nativeFilterList.add(ManifestParser.buildFilterFromArray("osgi.native.language", languageArray, 9));
                }
                if (currentSelectionFilter != null) {
                    nativeFilterList.add(SimpleFilter.parse(currentSelectionFilter));
                }
                if (nativeFilterList.isEmpty()) continue;
                SimpleFilter nativeClauseFilter = new SimpleFilter(null, nativeFilterList, 1);
                nativeFilterClauseList.add(nativeClauseFilter);
            }
            HashMap<String, String> requirementDirectives = new HashMap<String, String>();
            SimpleFilter consolidatedNativeFilter = null;
            if (hasOptionalLibraryDirective) {
                requirementDirectives.put("resolution", "optional");
            }
            if (nativeFilterClauseList.size() > 1) {
                consolidatedNativeFilter = new SimpleFilter(null, nativeFilterClauseList, 2);
                requirementDirectives.put("filter", consolidatedNativeFilter.toString());
            } else if (nativeFilterClauseList.size() == 1) {
                consolidatedNativeFilter = (SimpleFilter)nativeFilterClauseList.get(0);
                requirementDirectives.put("filter", consolidatedNativeFilter.toString());
            }
            if (requirementDirectives.size() > 0) {
                result.add(new BundleRequirementImpl(owner, "osgi.native", requirementDirectives, Collections.emptyMap(), consolidatedNativeFilter));
            }
        }
        return result;
    }

    private static SimpleFilter buildFilterFromArray(String attributeName, String[] stringArray, int operation) {
        SimpleFilter result = null;
        ArrayList<SimpleFilter> filterSet = new ArrayList<SimpleFilter>();
        if (stringArray != null) {
            for (String currentValue : stringArray) {
                filterSet.add(new SimpleFilter(attributeName, currentValue.toLowerCase(), operation));
            }
            result = filterSet.size() == 1 ? (SimpleFilter)filterSet.get(0) : new SimpleFilter(null, filterSet, 2);
        }
        return result;
    }

    private static List<ParsedHeaderClause> normalizeCapabilityClauses(Logger logger, List<ParsedHeaderClause> clauses, String mv) throws BundleException {
        if (mv == null || mv.equals("2") || !clauses.isEmpty()) {
            // empty if block
        }
        for (ParsedHeaderClause clause : clauses) {
            for (Map.Entry<String, String> entry : clause.m_types.entrySet()) {
                String type = entry.getValue();
                if (type.equals("String")) continue;
                if (type.equals("Double")) {
                    clause.m_attrs.put(entry.getKey(), new Double(clause.m_attrs.get(entry.getKey()).toString().trim()));
                    continue;
                }
                if (type.equals("Version")) {
                    clause.m_attrs.put(entry.getKey(), new Version(clause.m_attrs.get(entry.getKey()).toString().trim()));
                    continue;
                }
                if (type.equals("Long")) {
                    clause.m_attrs.put(entry.getKey(), new Long(clause.m_attrs.get(entry.getKey()).toString().trim()));
                    continue;
                }
                if (type.startsWith("List")) {
                    int startIdx = type.indexOf(60);
                    int endIdx = type.indexOf(62);
                    if (startIdx > 0 && endIdx <= startIdx || startIdx < 0 && endIdx > 0) {
                        throw new BundleException("Invalid Provide-Capability attribute list type for '" + entry.getKey() + "' : " + type);
                    }
                    String listType = "String";
                    if (endIdx > startIdx) {
                        listType = type.substring(startIdx + 1, endIdx).trim();
                    }
                    List<String> tokens = ManifestParser.parseDelimitedString(clause.m_attrs.get(entry.getKey()).toString(), ",", false);
                    ArrayList<Object> values = new ArrayList<Object>(tokens.size());
                    for (String token : tokens) {
                        if (listType.equals("String")) {
                            values.add(token);
                            continue;
                        }
                        if (listType.equals("Double")) {
                            values.add(new Double(token.trim()));
                            continue;
                        }
                        if (listType.equals("Version")) {
                            values.add(new Version(token.trim()));
                            continue;
                        }
                        if (listType.equals("Long")) {
                            values.add(new Long(token.trim()));
                            continue;
                        }
                        throw new BundleException("Unknown Provide-Capability attribute list type for '" + entry.getKey() + "' : " + type);
                    }
                    clause.m_attrs.put(entry.getKey(), values);
                    continue;
                }
                throw new BundleException("Unknown Provide-Capability attribute type for '" + entry.getKey() + "' : " + type);
            }
        }
        return clauses;
    }

    private static List<BundleCapability> convertProvideCapabilities(List<ParsedHeaderClause> clauses, BundleRevision owner) throws BundleException {
        ArrayList<BundleCapability> capList = new ArrayList<BundleCapability>();
        for (ParsedHeaderClause clause : clauses) {
            for (String path : clause.m_paths) {
                if (path.startsWith("osgi.wiring.")) {
                    throw new BundleException("Manifest cannot use Provide-Capability for '" + path + "' namespace.");
                }
                if (!(!path.startsWith("osgi.ee") && !path.startsWith("osgi.native") || owner != null && "org.apache.felix.framework".equals(owner.getSymbolicName()))) {
                    throw new BundleException("Only System Bundle can use Provide-Capability for '" + path + "' namespace.", 3);
                }
                capList.add(new BundleCapabilityImpl(owner, path, clause.m_dirs, clause.m_attrs));
            }
        }
        return capList;
    }

    private static List<ParsedHeaderClause> normalizeExportClauses(Logger logger, List<ParsedHeaderClause> clauses, String mv, String bsn, Version bv, boolean connectModule) throws BundleException {
        for (ParsedHeaderClause clause : clauses) {
            for (String pkgName : clause.m_paths) {
                if (!"org.apache.felix.framework".equals(bsn) && !connectModule && pkgName.startsWith("java.")) {
                    throw new BundleException("Exporting java.* packages not allowed: " + pkgName, 3);
                }
                if (pkgName.equals(".")) {
                    throw new BundleException("Exporing '.' is invalid.");
                }
                if (pkgName.length() != 0) continue;
                throw new BundleException("Exported package names cannot be zero length.");
            }
            Object v = clause.m_attrs.get("version");
            Object sv = clause.m_attrs.get("specification-version");
            if (v != null && sv != null && !((String)v).trim().equals(((String)sv).trim())) {
                throw new IllegalArgumentException("Both version and specification-version are specified, but they are not equal.");
            }
            if (v == null && sv == null) {
                v = Version.emptyVersion;
            }
            if (v != null || sv != null) {
                clause.m_attrs.remove("specification-version");
                v = v == null ? sv : v;
                clause.m_attrs.put("version", Version.parseVersion(v.toString()));
            }
            if (mv.equals("2")) {
                if (clause.m_attrs.containsKey("bundle-version") || clause.m_attrs.containsKey("bundle-symbolic-name")) {
                    throw new BundleException("Exports must not specify bundle symbolic name or bundle version.");
                }
                clause.m_attrs.put("bundle-symbolic-name", bsn);
                clause.m_attrs.put("bundle-version", bv);
                continue;
            }
            if (mv.equals("2")) continue;
            if (!clause.m_dirs.isEmpty()) {
                throw new BundleException("R3 exports cannot contain directives.");
            }
            if (clause.m_attrs.isEmpty()) continue;
            Object pkgVersion = clause.m_attrs.get("version");
            pkgVersion = pkgVersion == null ? Version.emptyVersion : pkgVersion;
            for (Map.Entry<String, Object> entry : clause.m_attrs.entrySet()) {
                if (entry.getKey().equals("version")) continue;
                logger.log(2, "Unknown R3 export attribute: " + entry.getKey());
            }
            clause.m_attrs.clear();
            clause.m_attrs.put("version", pkgVersion);
        }
        return clauses;
    }

    private static List<BundleCapability> convertExports(List<ParsedHeaderClause> clauses, BundleRevision owner) {
        ArrayList<BundleCapability> capList = new ArrayList<BundleCapability>();
        for (ParsedHeaderClause clause : clauses) {
            for (String pkgName : clause.m_paths) {
                Map<String, Object> attrs = clause.m_attrs;
                HashMap<String, Object> newAttrs = new HashMap<String, Object>(attrs.size() + 1);
                newAttrs.putAll(attrs);
                newAttrs.put("osgi.wiring.package", pkgName);
                capList.add(new BundleCapabilityImpl(owner, "osgi.wiring.package", clause.m_dirs, newAttrs));
            }
        }
        return capList;
    }

    public String getManifestVersion() {
        String manifestVersion = ManifestParser.getManifestVersion(this.m_headerMap);
        return manifestVersion == null ? "1" : manifestVersion;
    }

    private static String getManifestVersion(Map<String, Object> headerMap) {
        String manifestVersion = (String)headerMap.get("Bundle-ManifestVersion");
        return manifestVersion == null ? null : manifestVersion.trim();
    }

    public int getActivationPolicy() {
        return this.m_activationPolicy;
    }

    public String getActivationIncludeDirective() {
        return this.m_activationIncludeDir;
    }

    public String getActivationExcludeDirective() {
        return this.m_activationExcludeDir;
    }

    public boolean isExtension() {
        return this.m_isExtension;
    }

    public String getSymbolicName() {
        return this.m_bundleSymbolicName;
    }

    public Version getBundleVersion() {
        return this.m_bundleVersion;
    }

    public List<BundleCapability> getCapabilities() {
        return this.m_capabilities;
    }

    public List<BundleRequirement> getRequirements() {
        return this.m_requirements;
    }

    public List<NativeLibrary> getLibraries() {
        ArrayList<NativeLibrary> libs = null;
        try {
            NativeLibraryClause clause = this.getSelectedLibraryClause();
            if (clause != null) {
                String[] entries = clause.getLibraryEntries();
                libs = new ArrayList(entries.length);
                int current = 0;
                for (int i = 0; i < entries.length; ++i) {
                    String name = this.getName(entries[i]);
                    boolean found = false;
                    for (int j = 0; !found && j < current; ++j) {
                        found = this.getName(entries[j]).equals(name);
                    }
                    if (found) continue;
                    libs.add(new NativeLibrary(clause.getLibraryEntries()[i], clause.getOSNames(), clause.getProcessors(), clause.getOSVersions(), clause.getLanguages(), clause.getSelectionFilter()));
                }
                libs.trimToSize();
            }
        }
        catch (Exception ex) {
            libs = new ArrayList<NativeLibrary>(0);
        }
        return libs;
    }

    private String getName(String path) {
        int idx = path.lastIndexOf(47);
        if (idx > -1) {
            return path.substring(idx);
        }
        return path;
    }

    private NativeLibraryClause getSelectedLibraryClause() throws BundleException {
        if (this.m_libraryClauses != null && this.m_libraryClauses.size() > 0) {
            ArrayList<NativeLibraryClause> clauseList = new ArrayList<NativeLibraryClause>();
            for (NativeLibraryClause libraryClause : this.m_libraryClauses) {
                if (!libraryClause.match(this.m_configMap)) continue;
                clauseList.add(libraryClause);
            }
            int selected = 0;
            if (clauseList.isEmpty()) {
                if (this.m_libraryHeadersOptional) {
                    return null;
                }
                throw new BundleException("Unable to select a native library clause.");
            }
            if (clauseList.size() == 1) {
                selected = 0;
            } else if (clauseList.size() > 1) {
                selected = this.firstSortedClause(clauseList);
            }
            return (NativeLibraryClause)clauseList.get(selected);
        }
        return null;
    }

    private int firstSortedClause(List<NativeLibraryClause> clauseList) {
        VersionRange range;
        int k;
        String[] osversions;
        int index;
        int i;
        ArrayList<String> indexList = new ArrayList<String>();
        ArrayList<String> selection = new ArrayList<String>();
        for (int i2 = 0; i2 < clauseList.size(); ++i2) {
            indexList.add("" + i2);
        }
        Version osVersionRangeMaxFloor = new Version(0, 0, 0);
        for (i = 0; i < indexList.size(); ++i) {
            index = Integer.parseInt(((String)indexList.get(i)).toString());
            osversions = clauseList.get(index).getOSVersions();
            if (osversions != null) {
                selection.add("" + (String)indexList.get(i));
            }
            for (k = 0; osversions != null && k < osversions.length; ++k) {
                range = new VersionRange(osversions[k]);
                if (range.getLeft().compareTo(osVersionRangeMaxFloor) < 0) continue;
                osVersionRangeMaxFloor = range.getLeft();
            }
        }
        if (selection.size() == 1) {
            return Integer.parseInt(((String)selection.get(0)).toString());
        }
        if (selection.size() > 1) {
            indexList = selection;
            selection = new ArrayList();
            for (i = 0; i < indexList.size(); ++i) {
                index = Integer.parseInt(((String)indexList.get(i)).toString());
                osversions = clauseList.get(index).getOSVersions();
                for (k = 0; k < osversions.length; ++k) {
                    range = new VersionRange(osversions[k]);
                    if (range.getLeft().compareTo(osVersionRangeMaxFloor) < 0) continue;
                    selection.add("" + (String)indexList.get(i));
                }
            }
        }
        if (selection.isEmpty()) {
            selection.clear();
            indexList.clear();
            for (i = 0; i < clauseList.size(); ++i) {
                indexList.add("" + i);
            }
        } else {
            if (selection.size() == 1) {
                return Integer.parseInt(((String)selection.get(0)).toString());
            }
            indexList = selection;
            selection.clear();
        }
        for (i = 0; i < indexList.size(); ++i) {
            index = Integer.parseInt(((String)indexList.get(i)).toString());
            if (clauseList.get(index).getLanguages() == null) continue;
            selection.add("" + (String)indexList.get(i));
        }
        if (selection.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(((String)selection.get(0)).toString());
    }

    private static List<ParsedHeaderClause> calculateImplicitImports(List<BundleCapability> exports, List<ParsedHeaderClause> imports) throws BundleException {
        ArrayList<ParsedHeaderClause> clauseList = new ArrayList<ParsedHeaderClause>();
        HashMap<String, String> map = new HashMap<String, String>();
        for (int impIdx = 0; impIdx < imports.size(); ++impIdx) {
            for (int pathIdx = 0; pathIdx < imports.get((int)impIdx).m_paths.size(); ++pathIdx) {
                map.put(imports.get((int)impIdx).m_paths.get(pathIdx), imports.get((int)impIdx).m_paths.get(pathIdx));
            }
        }
        for (int i = 0; i < exports.size(); ++i) {
            if (map.get(exports.get(i).getAttributes().get("osgi.wiring.package")) != null) continue;
            HashMap<String, Object> attrs = new HashMap<String, Object>();
            Object version = exports.get(i).getAttributes().get("version");
            if (version != null) {
                attrs.put("version", new VersionRange(version.toString()));
            }
            ArrayList<String> paths = new ArrayList<String>();
            paths.add((String)exports.get(i).getAttributes().get("osgi.wiring.package"));
            clauseList.add(new ParsedHeaderClause(paths, Collections.EMPTY_MAP, attrs, Collections.EMPTY_MAP));
        }
        return clauseList;
    }

    private static List<BundleCapability> calculateImplicitUses(List<BundleCapability> exports, List<ParsedHeaderClause> imports) throws BundleException {
        int i;
        String usesValue = "";
        for (i = 0; i < imports.size(); ++i) {
            for (int pathIdx = 0; pathIdx < imports.get((int)i).m_paths.size(); ++pathIdx) {
                usesValue = usesValue + (usesValue.length() > 0 ? "," : "") + imports.get((int)i).m_paths.get(pathIdx);
            }
        }
        for (i = 0; i < exports.size(); ++i) {
            HashMap<String, String> dirs = new HashMap<String, String>(1);
            dirs.put("uses", usesValue);
            exports.set(i, new BundleCapabilityImpl(exports.get(i).getRevision(), "osgi.wiring.package", dirs, exports.get(i).getAttributes()));
        }
        return exports;
    }

    private static boolean checkExtensionBundle(Map<String, Object> headerMap) throws BundleException {
        String extension = ManifestParser.parseExtensionBundleHeader((String)headerMap.get("Fragment-Host"));
        if (extension != null) {
            if (!"framework".equals(extension) && !"bootclasspath".equals(extension)) {
                throw new BundleException("Extension bundle must have either 'extension:=framework' or 'extension:=bootclasspath'");
            }
            if (headerMap.containsKey("Require-Bundle") || headerMap.containsKey("Bundle-NativeCode") || headerMap.containsKey("DynamicImport-Package") || headerMap.containsKey("Bundle-Activator")) {
                throw new BundleException("Invalid extension bundle manifest");
            }
            return true;
        }
        return false;
    }

    private static BundleCapabilityImpl parseBundleSymbolicName(Logger logger, BundleRevision owner, Map<String, Object> headerMap) throws BundleException {
        List<ParsedHeaderClause> clauses = ManifestParser.normalizeCapabilityClauses(logger, ManifestParser.parseStandardHeader((String)headerMap.get("Bundle-SymbolicName")), ManifestParser.getManifestVersion(headerMap));
        if (clauses.size() > 0) {
            if (clauses.size() > 1) {
                throw new BundleException("Cannot have multiple symbolic names: " + headerMap.get("Bundle-SymbolicName"));
            }
            if (clauses.get((int)0).m_paths.size() > 1) {
                throw new BundleException("Cannot have multiple symbolic names: " + headerMap.get("Bundle-SymbolicName"));
            }
            if (clauses.get((int)0).m_attrs.containsKey("Bundle-Version")) {
                throw new BundleException("Cannot have a bundle version: " + headerMap.get("Bundle-Version"));
            }
            Version bundleVersion = Version.emptyVersion;
            if (headerMap.get("Bundle-Version") != null) {
                try {
                    bundleVersion = Version.parseVersion((String)headerMap.get("Bundle-Version"));
                }
                catch (RuntimeException ex) {
                    String mv = ManifestParser.getManifestVersion(headerMap);
                    if (mv != null) {
                        throw ex;
                    }
                    bundleVersion = Version.emptyVersion;
                }
            }
            Object tagList = clauses.get((int)0).m_attrs.get("tags");
            LinkedHashSet<String> tags = new LinkedHashSet<String>();
            if (tagList != null) {
                if (tagList instanceof List) {
                    for (Object member : (List)tagList) {
                        if (member instanceof String) {
                            tags.add((String)member);
                            continue;
                        }
                        throw new BundleException("Invalid tags list: " + headerMap.get("Bundle-SymbolicName"));
                    }
                } else if (tagList instanceof String) {
                    tags.add((String)tagList);
                } else {
                    throw new BundleException("Invalid tags list: " + headerMap.get("Bundle-SymbolicName"));
                }
            }
            if (tags.contains("osgi.connect")) {
                throw new BundleException("Invalid tags list: " + headerMap.get("Bundle-SymbolicName"));
            }
            if (owner != null && ((BundleRevisionImpl)owner).getContent() instanceof ConnectContentContent) {
                tags.add("osgi.connect");
            }
            if (!tags.isEmpty()) {
                clauses.get((int)0).m_attrs.put("tags", new ArrayList(tags));
            }
            String symName = clauses.get((int)0).m_paths.get(0);
            clauses.get((int)0).m_attrs.put("osgi.wiring.bundle", symName);
            clauses.get((int)0).m_attrs.put("bundle-version", bundleVersion);
            return new BundleCapabilityImpl(owner, "osgi.wiring.bundle", clauses.get((int)0).m_dirs, clauses.get((int)0).m_attrs);
        }
        return null;
    }

    private static BundleCapabilityImpl addIdentityCapability(BundleRevision owner, Map<String, Object> headerMap, BundleCapabilityImpl bundleCap) throws BundleException {
        HashMap<String, Object> attrs = new HashMap<String, Object>(bundleCap.getAttributes());
        attrs.put("osgi.identity", bundleCap.getAttributes().get("osgi.wiring.bundle"));
        attrs.put("type", headerMap.get("Fragment-Host") == null ? "osgi.bundle" : "osgi.fragment");
        attrs.put("version", bundleCap.getAttributes().get("bundle-version"));
        if (headerMap.get("Bundle-Copyright") != null) {
            attrs.put("copyright", headerMap.get("Bundle-Copyright"));
        }
        if (headerMap.get("Bundle-Description") != null) {
            attrs.put("description", headerMap.get("Bundle-Description"));
        }
        if (headerMap.get("Bundle-DocURL") != null) {
            attrs.put("documentation", headerMap.get("Bundle-DocURL"));
        }
        if (headerMap.get(BUNDLE_LICENSE_HEADER) != null) {
            attrs.put("license", headerMap.get(BUNDLE_LICENSE_HEADER));
        }
        Map<String, String> dirs = bundleCap.getDirectives().get("singleton") != null ? Collections.singletonMap("singleton", bundleCap.getDirectives().get("singleton")) : Collections.emptyMap();
        return new BundleCapabilityImpl(owner, "osgi.identity", dirs, attrs);
    }

    private static List<BundleRequirementImpl> parseFragmentHost(Logger logger, BundleRevision owner, Map<String, Object> headerMap) throws BundleException {
        ArrayList<BundleRequirementImpl> reqs = new ArrayList<BundleRequirementImpl>();
        String mv = ManifestParser.getManifestVersion(headerMap);
        if (mv != null && mv.equals("2")) {
            List<ParsedHeaderClause> clauses = ManifestParser.parseStandardHeader((String)headerMap.get("Fragment-Host"));
            if (clauses.size() > 0) {
                if (clauses.size() > 1) {
                    throw new BundleException("Fragments cannot have multiple hosts: " + headerMap.get("Fragment-Host"));
                }
                if (clauses.get((int)0).m_paths.size() > 1) {
                    throw new BundleException("Fragments cannot have multiple hosts: " + headerMap.get("Fragment-Host"));
                }
                Object value = clauses.get((int)0).m_attrs.get("bundle-version");
                Object object = value = value == null ? "0.0.0" : value;
                if (value != null) {
                    clauses.get((int)0).m_attrs.put("bundle-version", new VersionRange(value.toString()));
                }
                Map<String, Object> attrs = clauses.get((int)0).m_attrs;
                LinkedHashMap<String, Object> newAttrs = new LinkedHashMap<String, Object>(attrs.size() + 1);
                newAttrs.put("osgi.wiring.host", clauses.get((int)0).m_paths.get(0));
                newAttrs.putAll(attrs);
                newAttrs.put("osgi.wiring.host", clauses.get((int)0).m_paths.get(0));
                SimpleFilter sf = SimpleFilter.convert(newAttrs);
                Map<String, String> dirs = clauses.get((int)0).m_dirs;
                HashMap<String, String> newDirs = new HashMap<String, String>(dirs.size() + 1);
                newDirs.putAll(dirs);
                newDirs.put("filter", sf.toString());
                reqs.add(new BundleRequirementImpl(owner, "osgi.wiring.host", newDirs, newAttrs));
            }
        } else if (headerMap.get("Fragment-Host") != null) {
            String s = (String)headerMap.get("Bundle-SymbolicName");
            s = s == null ? (String)headerMap.get("Bundle-Name") : s;
            s = s == null ? headerMap.toString() : s;
            logger.log(2, "Only R4 bundles can be fragments: " + s);
        }
        return reqs;
    }

    private static List<BundleRequirement> parseBreeHeader(String header, BundleRevision owner) {
        String reqFilter;
        ArrayList<String> filters = new ArrayList<String>();
        for (String entry : ManifestParser.parseDelimitedString(header, ",")) {
            String versionClause;
            Version lVer;
            List<String> names = ManifestParser.parseDelimitedString(entry, "/");
            List<String> left = ManifestParser.parseDelimitedString(names.get(0), "-");
            String lName = left.get(0);
            try {
                lVer = Version.parseVersion(left.get(1));
            }
            catch (Exception ex) {
                lName = names.get(0);
                lVer = null;
            }
            String rName = null;
            Version rVer = null;
            if (names.size() > 1) {
                List<String> right = ManifestParser.parseDelimitedString(names.get(1), "-");
                rName = right.get(0);
                try {
                    rVer = Version.parseVersion(right.get(1));
                }
                catch (Exception ex) {
                    rName = names.get(1);
                    rVer = null;
                }
            }
            if (lVer != null) {
                if (rVer != null && !rVer.equals(lVer)) {
                    lName = names.get(0);
                    rName = names.get(1);
                    versionClause = null;
                } else {
                    versionClause = ManifestParser.getBreeVersionClause(lVer);
                }
            } else {
                versionClause = ManifestParser.getBreeVersionClause(rVer);
            }
            if ("J2SE".equals(lName)) {
                lName = "JavaSE";
            }
            String nameClause = rName != null ? "(osgi.ee=" + lName + "/" + rName + ")" : "(osgi.ee=" + lName + ")";
            String filter = versionClause != null ? "(&" + nameClause + versionClause + ")" : nameClause;
            filters.add(filter);
        }
        if (filters.size() == 0) {
            return Collections.emptyList();
        }
        if (filters.size() == 1) {
            reqFilter = (String)filters.get(0);
        } else {
            StringBuilder sb = new StringBuilder("(|");
            for (String f : filters) {
                sb.append(f);
            }
            sb.append(")");
            reqFilter = sb.toString();
        }
        SimpleFilter sf = SimpleFilter.parse(reqFilter);
        return Collections.singletonList(new BundleRequirementImpl(owner, "osgi.ee", Collections.singletonMap("filter", reqFilter), Collections.emptyMap(), sf));
    }

    private static String getBreeVersionClause(Version ver) {
        if (ver == null) {
            return null;
        }
        return "(version=" + ver + ")";
    }

    private static List<ParsedHeaderClause> normalizeRequireClauses(Logger logger, List<ParsedHeaderClause> clauses, String mv) {
        if (!mv.equals("2")) {
            clauses.clear();
        } else {
            for (ParsedHeaderClause clause : clauses) {
                Object value = clause.m_attrs.get("bundle-version");
                if (value == null) continue;
                clause.m_attrs.put("bundle-version", new VersionRange(value.toString()));
            }
        }
        return clauses;
    }

    private static List<BundleRequirementImpl> convertRequires(List<ParsedHeaderClause> clauses, BundleRevision owner) {
        ArrayList<BundleRequirementImpl> reqList = new ArrayList<BundleRequirementImpl>();
        for (ParsedHeaderClause clause : clauses) {
            for (String path : clause.m_paths) {
                Map<String, Object> attrs = clause.m_attrs;
                LinkedHashMap<String, Object> newAttrs = new LinkedHashMap<String, Object>(attrs.size() + 1);
                newAttrs.put("osgi.wiring.bundle", path);
                newAttrs.putAll(attrs);
                newAttrs.put("osgi.wiring.bundle", path);
                SimpleFilter sf = SimpleFilter.convert(newAttrs);
                Map<String, String> dirs = clause.m_dirs;
                HashMap<String, String> newDirs = new HashMap<String, String>(dirs.size() + 1);
                newDirs.putAll(dirs);
                newDirs.put("filter", sf.toString());
                reqList.add(new BundleRequirementImpl(owner, "osgi.wiring.bundle", newDirs, newAttrs));
            }
        }
        return reqList;
    }

    public static String parseExtensionBundleHeader(String header) throws BundleException {
        List<ParsedHeaderClause> clauses = ManifestParser.parseStandardHeader(header);
        String result = null;
        if (clauses.size() == 1) {
            for (Map.Entry<String, String> entry : clauses.get((int)0).m_dirs.entrySet()) {
                if (!"extension".equals(entry.getKey())) continue;
                result = entry.getValue();
            }
            if ("org.apache.felix.framework".equals(clauses.get((int)0).m_paths.get(0)) || "system.bundle".equals(clauses.get((int)0).m_paths.get(0))) {
                result = result == null ? "framework" : result;
            } else if (result != null) {
                throw new BundleException("Only the system bundle can have extension bundles.");
            }
        }
        return result;
    }

    private void parseActivationPolicy(Map<String, Object> headerMap) {
        this.m_activationPolicy = 0;
        List<ParsedHeaderClause> clauses = ManifestParser.parseStandardHeader((String)headerMap.get("Bundle-ActivationPolicy"));
        if (clauses.size() > 0) {
            for (String path : clauses.get((int)0).m_paths) {
                if (!path.equals("lazy")) continue;
                this.m_activationPolicy = 1;
                for (Map.Entry<String, String> entry : clauses.get((int)0).m_dirs.entrySet()) {
                    if (entry.getKey().equalsIgnoreCase("include")) {
                        this.m_activationIncludeDir = entry.getValue();
                        continue;
                    }
                    if (!entry.getKey().equalsIgnoreCase("exclude")) continue;
                    this.m_activationExcludeDir = entry.getValue();
                }
            }
        }
    }

    public static void main(String[] headers) {
        String header = headers[0];
        if (header != null) {
            if (header.length() == 0) {
                throw new IllegalArgumentException("A header cannot be an empty string.");
            }
            List<ParsedHeaderClause> clauses = ManifestParser.parseStandardHeader(header);
            for (ParsedHeaderClause clause : clauses) {
                System.out.println("PATHS " + clause.m_paths);
                System.out.println("    DIRS  " + clause.m_dirs);
                System.out.println("    ATTRS " + clause.m_attrs);
                System.out.println("    TYPES " + clause.m_types);
            }
        }
    }

    private static char charAt(int pos, String headers, int length) {
        if (pos >= length) {
            return '\uffff';
        }
        return headers.charAt(pos);
    }

    private static List<ParsedHeaderClause> parseStandardHeader(String header) {
        ArrayList<ParsedHeaderClause> clauses = new ArrayList<ParsedHeaderClause>();
        if (header == null) {
            return clauses;
        }
        ParsedHeaderClause clause = null;
        String key = null;
        Map<String, Object> targetMap = null;
        int state = 0;
        int currentPosition = 0;
        int startPosition = 0;
        int length = header.length();
        boolean quoted = false;
        boolean escaped = false;
        char currentChar = '\uffff';
        do {
            currentChar = ManifestParser.charAt(currentPosition, header, length);
            switch (state) {
                case 0: {
                    clause = new ParsedHeaderClause(new ArrayList<String>(), new HashMap<String, String>(), new HashMap<String, Object>(), new HashMap<String, String>());
                    clauses.add(clause);
                    state = 1;
                }
                case 1: {
                    startPosition = currentPosition;
                    state = 2;
                }
                case 2: {
                    switch (currentChar) {
                        case ':': 
                        case '=': {
                            key = header.substring(startPosition, currentPosition).trim();
                            startPosition = currentPosition + 1;
                            targetMap = clause.m_attrs;
                            state = currentChar == ':' ? 4 : 8;
                            break;
                        }
                        case ',': 
                        case ';': 
                        case '\uffff': {
                            clause.m_paths.add(header.substring(startPosition, currentPosition).trim());
                            state = currentChar == ',' ? 0 : 1;
                            break;
                        }
                    }
                    ++currentPosition;
                    break;
                }
                case 4: {
                    switch (currentChar) {
                        case '=': {
                            if (startPosition != currentPosition) {
                                clause.m_types.put(key, header.substring(startPosition, currentPosition).trim());
                            } else {
                                targetMap = clause.m_dirs;
                            }
                            state = 8;
                            startPosition = currentPosition + 1;
                            break;
                        }
                    }
                    ++currentPosition;
                    break;
                }
                case 8: {
                    if (currentChar == '\"') {
                        quoted = true;
                        ++currentPosition;
                    } else {
                        quoted = false;
                    }
                    if (!Character.isWhitespace(currentChar)) {
                        state = 16;
                        break;
                    }
                    ++currentPosition;
                    break;
                }
                case 16: {
                    if (escaped) {
                        escaped = false;
                    } else if (currentChar == '\\') {
                        escaped = true;
                    } else if (quoted && currentChar == '\"') {
                        quoted = false;
                    } else if (!quoted) {
                        String value = null;
                        switch (currentChar) {
                            case ',': 
                            case ';': 
                            case '\uffff': {
                                value = header.substring(startPosition, currentPosition).trim();
                                if (value.startsWith("\"") && value.endsWith("\"")) {
                                    value = value.substring(1, value.length() - 1);
                                }
                                if (targetMap.put(key, value) != null) {
                                    throw new IllegalArgumentException("Duplicate '" + key + "' in: " + header);
                                }
                                state = currentChar == ';' ? 1 : 0;
                                break;
                            }
                        }
                    }
                    ++currentPosition;
                    break;
                }
            }
        } while (currentChar != 65535);
        if (state > 1) {
            throw new IllegalArgumentException("Unable to parse header: " + header);
        }
        return clauses;
    }

    public static List<String> parseDelimitedString(String value, String delim) {
        return ManifestParser.parseDelimitedString(value, delim, true);
    }

    public static List<String> parseDelimitedString(String value, String delim, boolean trim) {
        if (value == null) {
            value = "";
        }
        ArrayList<String> list = new ArrayList<String>();
        int CHAR = 1;
        int DELIMITER = 2;
        int STARTQUOTE = 4;
        int ENDQUOTE = 8;
        StringBuilder sb = new StringBuilder();
        int expecting = CHAR | DELIMITER | STARTQUOTE;
        boolean isEscaped = false;
        for (int i = 0; i < value.length(); ++i) {
            boolean isDelimiter;
            char c = value.charAt(i);
            boolean bl = isDelimiter = delim.indexOf(c) >= 0;
            if (!isEscaped && c == '\\') {
                isEscaped = true;
                continue;
            }
            if (isEscaped) {
                sb.append(c);
            } else if (isDelimiter && (expecting & DELIMITER) > 0) {
                if (trim) {
                    list.add(sb.toString().trim());
                } else {
                    list.add(sb.toString());
                }
                sb.delete(0, sb.length());
                expecting = CHAR | DELIMITER | STARTQUOTE;
            } else if (c == '\"' && (expecting & STARTQUOTE) > 0) {
                sb.append(c);
                expecting = CHAR | ENDQUOTE;
            } else if (c == '\"' && (expecting & ENDQUOTE) > 0) {
                sb.append(c);
                expecting = CHAR | STARTQUOTE | DELIMITER;
            } else if ((expecting & CHAR) > 0) {
                sb.append(c);
            } else {
                throw new IllegalArgumentException("Invalid delimited string: " + value);
            }
            isEscaped = false;
        }
        if (sb.length() > 0) {
            if (trim) {
                list.add(sb.toString().trim());
            } else {
                list.add(sb.toString());
            }
        }
        return list;
    }

    private static List<NativeLibraryClause> parseLibraryStrings(Logger logger, List<String> libStrs) throws IllegalArgumentException {
        if (libStrs == null) {
            return new ArrayList<NativeLibraryClause>(0);
        }
        ArrayList<NativeLibraryClause> libList = new ArrayList<NativeLibraryClause>(libStrs.size());
        for (int i = 0; i < libStrs.size(); ++i) {
            NativeLibraryClause clause = NativeLibraryClause.parse(logger, libStrs.get(i));
            libList.add(clause);
        }
        return libList;
    }

    public static List<BundleCapability> aliasSymbolicName(List<BundleCapability> caps, BundleRevision owner) {
        if (caps == null) {
            return new ArrayList<BundleCapability>(0);
        }
        ArrayList<BundleCapability> aliasCaps = new ArrayList<BundleCapability>(caps);
        String[] aliases = new String[]{"org.apache.felix.framework", "system.bundle"};
        block0: for (int capIdx = 0; capIdx < aliasCaps.size(); ++capIdx) {
            BundleCapability cap = (BundleCapability)aliasCaps.get(capIdx);
            if (cap.getNamespace().equals("osgi.wiring.bundle") || cap.getNamespace().equals("osgi.wiring.host")) {
                HashMap<String, Object> aliasAttrs = new HashMap<String, Object>(cap.getAttributes());
                aliasAttrs.put(cap.getNamespace(), aliases);
                cap = new BundleCapabilityImpl(owner, cap.getNamespace(), cap.getDirectives(), aliasAttrs);
                aliasCaps.set(capIdx, cap);
            }
            for (Map.Entry<String, Object> entry : cap.getAttributes().entrySet()) {
                if (!entry.getKey().equalsIgnoreCase("bundle-symbolic-name")) continue;
                HashMap<String, Object> aliasAttrs = new HashMap<String, Object>(cap.getAttributes());
                aliasAttrs.put("bundle-symbolic-name", aliases);
                aliasCaps.set(capIdx, new BundleCapabilityImpl(owner, cap.getNamespace(), cap.getDirectives(), aliasAttrs));
                continue block0;
            }
        }
        return aliasCaps;
    }
}

