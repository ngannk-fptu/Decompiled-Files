/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
 */
package org.apache.felix.framework;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.felix.framework.BundleImpl;
import org.apache.felix.framework.BundleProtectionDomain;
import org.apache.felix.framework.BundleRevisionImpl;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.ServiceRegistrationImpl;
import org.apache.felix.framework.StatefulResolver;
import org.apache.felix.framework.URLHandlersBundleURLConnection;
import org.apache.felix.framework.WovenClassImpl;
import org.apache.felix.framework.cache.ConnectContentContent;
import org.apache.felix.framework.cache.Content;
import org.apache.felix.framework.capabilityset.SimpleFilter;
import org.apache.felix.framework.resolver.ResourceNotFoundException;
import org.apache.felix.framework.util.CompoundEnumeration;
import org.apache.felix.framework.util.SecurityManagerEx;
import org.apache.felix.framework.util.Util;
import org.apache.felix.framework.util.manifestparser.ManifestParser;
import org.apache.felix.framework.util.manifestparser.NativeLibrary;
import org.apache.felix.framework.wiring.BundleRequirementImpl;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.osgi.framework.BundleException;
import org.osgi.framework.BundleReference;
import org.osgi.framework.CapabilityPermission;
import org.osgi.framework.PackagePermission;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.hooks.weaving.WeavingException;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.hooks.weaving.WovenClassListener;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.resource.Wire;
import org.osgi.service.resolver.ResolutionException;

public class BundleWiringImpl
implements BundleWiring {
    public static final int LISTRESOURCES_DEBUG = 0x100000;
    public static final int EAGER_ACTIVATION = 0;
    public static final int LAZY_ACTIVATION = 1;
    public static final ClassLoader CNFE_CLASS_LOADER = new ClassLoader(){

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            throw new ClassNotFoundException("Unable to load class '" + name + "'");
        }
    };
    private final Logger m_logger;
    private final Map m_configMap;
    private final StatefulResolver m_resolver;
    private final BundleRevisionImpl m_revision;
    private final List<BundleRevision> m_fragments;
    private volatile List<BundleWire> m_wires;
    private volatile Map<String, BundleRevision> m_importedPkgs;
    private final Map<String, List<BundleRevision>> m_requiredPkgs;
    private final List<BundleCapability> m_resolvedCaps;
    private final Map<String, List<List<String>>> m_includedPkgFilters;
    private final Map<String, List<List<String>>> m_excludedPkgFilters;
    private final List<BundleRequirement> m_resolvedReqs;
    private final List<NativeLibrary> m_resolvedNativeLibs;
    private final List<Content> m_fragmentContents;
    private volatile List<BundleRequirement> m_wovenReqs = null;
    private volatile ClassLoader m_classLoader;
    private final ClassLoader m_bootClassLoader;
    private static final ClassLoader m_defBootClassLoader;
    private final boolean m_implicitBootDelegation;
    private final boolean m_useLocalURLs;
    private static SecurityManagerEx m_sm;
    private final ThreadLocal m_cycleCheck = new ThreadLocal();
    private static final ThreadLocal m_deferredActivation;
    private volatile boolean m_isDisposed = false;
    private volatile ConcurrentHashMap<String, ClassLoader> m_accessorLookupCache;
    private final ThreadLocal m_listResourcesCycleCheck = new ThreadLocal();

    BundleWiringImpl(Logger logger, Map configMap, StatefulResolver resolver, BundleRevisionImpl revision, List<BundleRevision> fragments, List<BundleWire> wires, Map<String, BundleRevision> importedPkgs, Map<String, List<BundleRevision>> requiredPkgs) throws Exception {
        Object l;
        Object map;
        Object effective;
        this.m_logger = logger;
        this.m_configMap = configMap;
        this.m_resolver = resolver;
        this.m_revision = revision;
        this.m_importedPkgs = importedPkgs;
        this.m_requiredPkgs = requiredPkgs;
        this.m_wires = Util.newImmutableList(wires);
        ArrayList<Content> fragmentContents = null;
        if (fragments != null) {
            if (fragments.size() > 1) {
                TreeMap<String, BundleRevision> sorted = new TreeMap<String, BundleRevision>();
                for (BundleRevision bundleRevision : fragments) {
                    sorted.put(((BundleRevisionImpl)bundleRevision).getId(), bundleRevision);
                }
                fragments = new ArrayList(sorted.values());
            }
            fragmentContents = new ArrayList<Content>(fragments.size());
            for (int i = 0; fragments != null && i < fragments.size(); ++i) {
                fragmentContents.add(((BundleRevisionImpl)fragments.get(i)).getContent().getEntryAsContent("."));
            }
        }
        this.m_fragments = fragments;
        this.m_fragmentContents = fragmentContents;
        HashSet<String> imports = new HashSet<String>();
        ArrayList<BundleRequirement> reqList = new ArrayList<BundleRequirement>();
        for (BundleWire bw : wires) {
            if (bw.getRequirement().getNamespace().equals("osgi.wiring.host") && reqList.contains(bw.getRequirement())) continue;
            reqList.add(bw.getRequirement());
            if (!bw.getRequirement().getNamespace().equals("osgi.wiring.package")) continue;
            imports.add((String)bw.getCapability().getAttributes().get("osgi.wiring.package"));
        }
        for (BundleRequirement req : this.m_revision.getDeclaredRequirements(null)) {
            Object resolution;
            if (!req.getNamespace().equals("osgi.wiring.package") || (resolution = req.getDirectives().get("resolution")) == null || !((String)resolution).equals("dynamic")) continue;
            reqList.add(req);
        }
        if (this.m_fragments != null) {
            for (BundleRevision fragment : this.m_fragments) {
                for (BundleRequirement req : fragment.getDeclaredRequirements(null)) {
                    Iterator<BundleRevision> resolution;
                    if (!req.getNamespace().equals("osgi.wiring.package") || (resolution = req.getDirectives().get("resolution")) == null || !((String)((Object)resolution)).equals("dynamic")) continue;
                    reqList.add(req);
                }
            }
        }
        this.m_resolvedReqs = Util.newImmutableList(reqList);
        boolean bl = Util.isFragment(revision);
        ArrayList<BundleCapability> capList = new ArrayList<BundleCapability>();
        HashMap<String, List<List<String>>> includedPkgFilters = new HashMap<String, List<List<String>>>();
        HashMap<String, List<List<String>>> excludedPkgFilters = new HashMap<String, List<List<String>>>();
        if (bl) {
            for (BundleCapability cap : this.m_revision.getDeclaredCapabilities(null)) {
                if (!"osgi.identity".equals(cap.getNamespace()) || (effective = cap.getDirectives().get("effective")) != null && !((String)effective).equals("resolve")) continue;
                capList.add(cap);
            }
        } else {
            for (BundleCapability cap : this.m_revision.getDeclaredCapabilities(null)) {
                if (cap.getNamespace().equals("osgi.wiring.package") && (!cap.getNamespace().equals("osgi.wiring.package") || imports.contains(cap.getAttributes().get("osgi.wiring.package").toString())) || (effective = cap.getDirectives().get("effective")) != null && !((String)effective).equals("resolve")) continue;
                capList.add(cap);
                if (!cap.getNamespace().equals("osgi.wiring.package")) continue;
                List<List<String>> filters = BundleWiringImpl.parsePkgFilters(cap, "include");
                if (filters != null) {
                    includedPkgFilters.put((String)cap.getAttributes().get("osgi.wiring.package"), filters);
                }
                if ((filters = BundleWiringImpl.parsePkgFilters(cap, "exclude")) == null) continue;
                excludedPkgFilters.put((String)cap.getAttributes().get("osgi.wiring.package"), filters);
            }
            if (this.m_fragments != null) {
                for (BundleRevision fragment : this.m_fragments) {
                    for (BundleCapability cap : fragment.getDeclaredCapabilities(null)) {
                        String effective2;
                        if ("osgi.identity".equals(cap.getNamespace()) || cap.getNamespace().equals("osgi.wiring.package") && (!cap.getNamespace().equals("osgi.wiring.package") || imports.contains(cap.getAttributes().get("osgi.wiring.package").toString())) || (effective2 = cap.getDirectives().get("effective")) != null && !effective2.equals("resolve")) continue;
                        capList.add(cap);
                        if (!cap.getNamespace().equals("osgi.wiring.package")) continue;
                        List<List<String>> filters = BundleWiringImpl.parsePkgFilters(cap, "include");
                        if (filters != null) {
                            includedPkgFilters.put((String)cap.getAttributes().get("osgi.wiring.package"), filters);
                        }
                        if ((filters = BundleWiringImpl.parsePkgFilters(cap, "exclude")) == null) continue;
                        excludedPkgFilters.put((String)cap.getAttributes().get("osgi.wiring.package"), filters);
                    }
                }
            }
        }
        if (System.getSecurityManager() != null) {
            Iterator iter = capList.iterator();
            while (iter.hasNext()) {
                BundleCapability cap;
                cap = (BundleCapability)iter.next();
                String bundleNamespace = cap.getNamespace();
                if (bundleNamespace.isEmpty()) {
                    iter.remove();
                    continue;
                }
                if (bundleNamespace.equals("osgi.wiring.package")) {
                    if (((BundleProtectionDomain)((BundleRevisionImpl)cap.getRevision()).getProtectionDomain()).impliesDirect(new PackagePermission((String)cap.getAttributes().get("osgi.wiring.package"), "exportonly"))) continue;
                    iter.remove();
                    continue;
                }
                if (bundleNamespace.equals("osgi.wiring.host") || bundleNamespace.equals("osgi.wiring.bundle") || bundleNamespace.equals("osgi.ee")) continue;
                CapabilityPermission permission = new CapabilityPermission(bundleNamespace, "provide");
                if (((BundleProtectionDomain)((BundleRevisionImpl)cap.getRevision()).getProtectionDomain()).impliesDirect(permission)) continue;
                iter.remove();
            }
        }
        this.m_resolvedCaps = Util.newImmutableList(capList);
        this.m_includedPkgFilters = includedPkgFilters.isEmpty() ? Collections.EMPTY_MAP : includedPkgFilters;
        this.m_excludedPkgFilters = excludedPkgFilters.isEmpty() ? Collections.EMPTY_MAP : excludedPkgFilters;
        ArrayList<NativeLibrary> libList = this.m_revision.getDeclaredNativeLibraries() == null ? new ArrayList<NativeLibrary>() : new ArrayList<NativeLibrary>(this.m_revision.getDeclaredNativeLibraries());
        for (int fragIdx = 0; this.m_fragments != null && fragIdx < this.m_fragments.size(); ++fragIdx) {
            List<NativeLibrary> libs = ((BundleRevisionImpl)this.m_fragments.get(fragIdx)).getDeclaredNativeLibraries();
            for (int reqIdx = 0; libs != null && reqIdx < libs.size(); ++reqIdx) {
                libList.add(libs.get(reqIdx));
            }
        }
        this.m_resolvedNativeLibs = libList.isEmpty() ? null : Util.newImmutableList(libList);
        ClassLoader bootLoader = m_defBootClassLoader;
        if (revision.getBundle().getBundleId() != 0L && (map = this.m_configMap.get("felix.bootdelegation.classloaders")) instanceof Map && (l = ((Map)map).get(this.m_revision.getBundle())) instanceof ClassLoader) {
            bootLoader = (ClassLoader)l;
        }
        this.m_bootClassLoader = bootLoader;
        this.m_implicitBootDelegation = this.m_configMap.get("felix.bootdelegation.implicit") == null || Boolean.valueOf((String)this.m_configMap.get("felix.bootdelegation.implicit")) != false;
        this.m_useLocalURLs = this.m_configMap.get("felix.jarurls") != null;
    }

    private static List<List<String>> parsePkgFilters(BundleCapability cap, String filtername) {
        ArrayList<List<String>> filters = null;
        String include = cap.getDirectives().get(filtername);
        if (include != null) {
            List<String> filterStrings = ManifestParser.parseDelimitedString(include, ",");
            filters = new ArrayList<List<String>>(filterStrings.size());
            for (int filterIdx = 0; filterIdx < filterStrings.size(); ++filterIdx) {
                List<String> substrings = SimpleFilter.parseSubstring(filterStrings.get(filterIdx));
                filters.add(substrings);
            }
        }
        return filters;
    }

    public String toString() {
        return this.m_revision.getBundle().toString();
    }

    public synchronized void dispose() {
        if (this.m_fragmentContents != null) {
            for (Content content : this.m_fragmentContents) {
                content.close();
            }
        }
        this.m_classLoader = null;
        this.m_isDisposed = true;
        this.m_accessorLookupCache = null;
    }

    public boolean hasPackageSource(String pkgName) {
        return this.m_importedPkgs.containsKey(pkgName) || this.m_requiredPkgs.containsKey(pkgName);
    }

    public BundleRevision getImportedPackageSource(String pkgName) {
        return this.m_importedPkgs.get(pkgName);
    }

    List<BundleRevision> getFragments() {
        return this.m_fragments;
    }

    List<Content> getFragmentContents() {
        return this.m_fragmentContents;
    }

    @Override
    public boolean isCurrent() {
        BundleRevision current = this.getBundle().adapt(BundleRevision.class);
        return current != null && current.getWiring() == this;
    }

    @Override
    public boolean isInUse() {
        return !this.m_isDisposed;
    }

    @Override
    public List<Capability> getResourceCapabilities(String namespace) {
        return BundleRevisionImpl.asCapabilityList(this.getCapabilities(namespace));
    }

    @Override
    public List<BundleCapability> getCapabilities(String namespace) {
        if (this.isInUse()) {
            List<BundleCapability> result = this.m_resolvedCaps;
            if (namespace != null) {
                result = new ArrayList<BundleCapability>();
                for (BundleCapability cap : this.m_resolvedCaps) {
                    if (!cap.getNamespace().equals(namespace)) continue;
                    result.add(cap);
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<Requirement> getResourceRequirements(String namespace) {
        return BundleRevisionImpl.asRequirementList(this.getRequirements(namespace));
    }

    @Override
    public List<BundleRequirement> getRequirements(String namespace) {
        if (this.isInUse()) {
            List<BundleRequirement> searchReqs = this.m_resolvedReqs;
            List<BundleRequirement> wovenReqs = this.m_wovenReqs;
            List<BundleRequirement> result = this.m_resolvedReqs;
            if (wovenReqs != null) {
                searchReqs = new ArrayList<BundleRequirement>(this.m_resolvedReqs);
                searchReqs.addAll(wovenReqs);
                result = searchReqs;
            }
            if (namespace != null) {
                result = new ArrayList<BundleRequirement>();
                for (BundleRequirement req : searchReqs) {
                    if (!req.getNamespace().equals(namespace)) continue;
                    result.add(req);
                }
            }
            return result;
        }
        return null;
    }

    public List<NativeLibrary> getNativeLibraries() {
        return this.m_resolvedNativeLibs;
    }

    private static List<Wire> asWireList(List wires) {
        return wires;
    }

    @Override
    public List<Wire> getProvidedResourceWires(String namespace) {
        return BundleWiringImpl.asWireList(this.getProvidedWires(namespace));
    }

    @Override
    public List<BundleWire> getProvidedWires(String namespace) {
        if (this.isInUse()) {
            return this.m_revision.getBundle().getFramework().getDependencies().getProvidedWires(this.m_revision, namespace);
        }
        return null;
    }

    @Override
    public List<Wire> getRequiredResourceWires(String namespace) {
        return BundleWiringImpl.asWireList(this.getRequiredWires(namespace));
    }

    @Override
    public List<BundleWire> getRequiredWires(String namespace) {
        if (this.isInUse()) {
            List<BundleWire> result = this.m_wires;
            if (namespace != null) {
                result = new ArrayList<BundleWire>();
                for (BundleWire bw : this.m_wires) {
                    if (!bw.getRequirement().getNamespace().equals(namespace)) continue;
                    result.add(bw);
                }
            }
            return result;
        }
        return null;
    }

    public synchronized void addDynamicWire(BundleWire wire) {
        ArrayList<BundleWire> wires = new ArrayList<BundleWire>(this.m_wires);
        wires.add(wire);
        if (wire.getCapability().getAttributes().get("osgi.wiring.package") != null) {
            HashMap<String, BundleRevision> importedPkgs = new HashMap<String, BundleRevision>(this.m_importedPkgs);
            importedPkgs.put((String)wire.getCapability().getAttributes().get("osgi.wiring.package"), wire.getProviderWiring().getRevision());
            this.m_importedPkgs = importedPkgs;
        }
        this.m_wires = Util.newImmutableList(wires);
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
    public ClassLoader getClassLoader() {
        if (this.m_isDisposed || Util.isFragment(this.m_revision)) {
            return null;
        }
        return this.getClassLoaderInternal();
    }

    private ClassLoader getClassLoaderInternal() {
        ClassLoader classLoader = this.m_classLoader;
        if (classLoader != null) {
            return classLoader;
        }
        return this._getClassLoaderInternal();
    }

    private synchronized ClassLoader _getClassLoaderInternal() {
        if (!this.m_isDisposed && this.m_classLoader == null) {
            if (this.m_revision.getContent() instanceof ConnectContentContent) {
                this.m_classLoader = ((ConnectContentContent)this.m_revision.getContent()).getClassLoader();
            }
            if (this.m_classLoader == null) {
                this.m_classLoader = BundleRevisionImpl.getSecureAction().run(new PrivilegedAction<BundleClassLoader>(){

                    @Override
                    public BundleClassLoader run() {
                        return new BundleClassLoader(BundleWiringImpl.this, BundleWiringImpl.this.determineParentClassLoader(), BundleWiringImpl.this.m_logger);
                    }
                });
            }
        }
        return this.m_classLoader;
    }

    @Override
    public List<URL> findEntries(String path, String filePattern, int options) {
        if (this.isInUse()) {
            if (!Util.isFragment(this.m_revision)) {
                Enumeration e = this.m_revision.getBundle().getFramework().findBundleEntries(this.m_revision, path, filePattern, (options & 1) > 0);
                ArrayList entries = new ArrayList();
                while (e != null && e.hasMoreElements()) {
                    entries.add(e.nextElement());
                }
                return Util.newImmutableList(entries);
            }
            return Collections.EMPTY_LIST;
        }
        return null;
    }

    @Override
    public Collection<String> listResources(String path, String filePattern, int options) {
        List<String> pattern;
        Collection<ResourceSource> sources;
        TreeSet<String> resources = null;
        if (path.length() > 0 && path.charAt(0) == '/') {
            path = path.substring(1);
        }
        if (path.length() > 0 && path.charAt(path.length() - 1) != '/') {
            path = path + '/';
        }
        if ((sources = this.listResourcesInternal(path, pattern = SimpleFilter.parseSubstring(filePattern = filePattern == null ? "*" : filePattern), options)) != null) {
            boolean debug = (options & 0x100000) > 0;
            resources = new TreeSet<String>();
            for (ResourceSource source : sources) {
                if (debug) {
                    resources.add(source.toString());
                    continue;
                }
                resources.add(source.m_resource);
            }
        }
        return resources;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized Collection<ResourceSource> listResourcesInternal(String path, List<String> pattern, int options) {
        if (this.isInUse()) {
            boolean recurse = (options & 1) > 0;
            boolean localOnly = (options & 2) > 0;
            HashSet<String> cycles = (HashSet<String>)this.m_listResourcesCycleCheck.get();
            if (cycles == null) {
                cycles = new HashSet<String>();
                this.m_listResourcesCycleCheck.set(cycles);
            }
            if (cycles.contains(path)) {
                return Collections.EMPTY_LIST;
            }
            cycles.add(path);
            try {
                TreeSet<ResourceSource> treeSet;
                TreeSet<ResourceSource> remoteResources = new TreeSet<ResourceSource>();
                HashSet<String> noMerging = new HashSet<String>();
                for (BundleWire bw : this.m_wires) {
                    if (bw.getCapability().getNamespace().equals("osgi.wiring.package")) {
                        remoteResources.addAll(this.calculateRemotePackageResources(bw, bw.getCapability(), recurse, path, pattern, noMerging));
                        continue;
                    }
                    if (!bw.getCapability().getNamespace().equals("osgi.wiring.bundle")) continue;
                    List<BundleCapability> exports = bw.getProviderWiring().getRevision().getDeclaredCapabilities("osgi.wiring.package");
                    for (BundleCapability bundleCapability : exports) {
                        remoteResources.addAll(this.calculateRemotePackageResources(bw, bundleCapability, recurse, path, pattern, null));
                    }
                    List<BundleWire> requiredBundles = bw.getProviderWiring().getRequiredWires("osgi.wiring.bundle");
                    for (BundleWire rbWire : requiredBundles) {
                        String visibility = rbWire.getRequirement().getDirectives().get("visibility");
                        if (visibility == null || !visibility.equals("reexport")) continue;
                        List<BundleCapability> reexports = rbWire.getProviderWiring().getRevision().getDeclaredCapabilities("osgi.wiring.package");
                        for (BundleCapability reexport : reexports) {
                            remoteResources.addAll(this.calculateRemotePackageResources(bw, reexport, recurse, path, pattern, null));
                        }
                    }
                }
                TreeSet<ResourceSource> localResources = new TreeSet<ResourceSource>();
                List<Content> contentPath = this.m_revision.getContentPath();
                for (Content content : contentPath) {
                    Enumeration<String> enumeration = content.getEntries();
                    if (enumeration == null) continue;
                    while (enumeration.hasMoreElements()) {
                        String resource = enumeration.nextElement();
                        String resourcePath = BundleWiringImpl.getTrailingPath(resource);
                        if (noMerging.contains(resourcePath) || (recurse || !resourcePath.equals(path)) && (!recurse || !resourcePath.startsWith(path)) || !BundleWiringImpl.matchesPattern(pattern, BundleWiringImpl.getPathHead(resource))) continue;
                        localResources.add(new ResourceSource(resource, this.m_revision));
                    }
                }
                if (localOnly) {
                    treeSet = localResources;
                    return treeSet;
                }
                remoteResources.addAll(localResources);
                treeSet = remoteResources;
                return treeSet;
            }
            finally {
                cycles.remove(path);
                if (cycles.isEmpty()) {
                    this.m_listResourcesCycleCheck.set(null);
                }
            }
        }
        return null;
    }

    private Collection<ResourceSource> calculateRemotePackageResources(BundleWire bw, BundleCapability cap, boolean recurse, String path, List<String> pattern, Set<String> noMerging) {
        Collection<Object> resources = Collections.EMPTY_SET;
        String subpath = (String)cap.getAttributes().get("osgi.wiring.package");
        subpath = subpath.replace('.', '/') + '/';
        if (noMerging != null) {
            noMerging.add(subpath);
        }
        if (!recurse && subpath.equals(path) || recurse && subpath.startsWith(path)) {
            resources = ((BundleWiringImpl)bw.getProviderWiring()).listResourcesInternal(subpath, pattern, 0);
            Iterator<Object> it = resources.iterator();
            while (it.hasNext()) {
                ResourceSource reqResource = (ResourceSource)it.next();
                if (reqResource.m_resource.charAt(reqResource.m_resource.length() - 1) != '/') continue;
                it.remove();
            }
        } else if (!recurse && subpath.startsWith(path)) {
            int idx = subpath.indexOf(47, path.length());
            if (idx >= 0) {
                subpath = subpath.substring(0, idx + 1);
            }
            if (BundleWiringImpl.matchesPattern(pattern, BundleWiringImpl.getPathHead(subpath))) {
                resources = Collections.singleton(new ResourceSource(subpath, bw.getProviderWiring().getRevision()));
            }
        }
        return resources;
    }

    private static String getPathHead(String resource) {
        int idx;
        if (resource.length() == 0) {
            return resource;
        }
        int n = idx = resource.charAt(resource.length() - 1) == '/' ? resource.lastIndexOf(47, resource.length() - 2) : resource.lastIndexOf(47);
        if (idx < 0) {
            return resource;
        }
        return resource.substring(idx + 1);
    }

    private static String getTrailingPath(String resource) {
        int idx;
        if (resource.length() == 0) {
            return null;
        }
        int n = idx = resource.charAt(resource.length() - 1) == '/' ? resource.lastIndexOf(47, resource.length() - 2) : resource.lastIndexOf(47);
        if (idx < 0) {
            return "";
        }
        return resource.substring(0, idx + 1);
    }

    private static boolean matchesPattern(List<String> pattern, String resource) {
        if (resource.charAt(resource.length() - 1) == '/') {
            resource = resource.substring(0, resource.length() - 1);
        }
        return SimpleFilter.compareSubstring(pattern, resource);
    }

    @Override
    public BundleImpl getBundle() {
        return this.m_revision.getBundle();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Enumeration getResourcesByDelegation(String name) {
        HashSet<String> requestSet = (HashSet<String>)this.m_cycleCheck.get();
        if (requestSet == null) {
            requestSet = new HashSet<String>();
            this.m_cycleCheck.set(requestSet);
        }
        if (!requestSet.contains(name)) {
            requestSet.add(name);
            try {
                Enumeration enumeration = this.findResourcesByDelegation(name);
                return enumeration;
            }
            finally {
                requestSet.remove(name);
            }
        }
        return null;
    }

    private Enumeration findResourcesByDelegation(String name) {
        BundleRevision provider;
        Enumeration urls = null;
        ArrayList<Enumeration> completeUrlList = new ArrayList<Enumeration>();
        String pkgName = Util.getResourcePackage(name);
        if (this.shouldBootDelegate(pkgName)) {
            try {
                ClassLoader bdcl = this.getBootDelegationClassLoader();
                urls = bdcl.getResources(name);
            }
            catch (IOException bdcl) {
                // empty catch block
            }
            if (pkgName.startsWith("java.")) {
                return urls;
            }
            completeUrlList.add(urls);
        }
        if ((provider = this.m_importedPkgs.get(pkgName)) != null) {
            urls = ((BundleWiringImpl)provider.getWiring()).getResourcesByDelegation(name);
            if (urls != null && urls.hasMoreElements()) {
                completeUrlList.add(urls);
            }
            return new CompoundEnumeration(completeUrlList.toArray(new Enumeration[completeUrlList.size()]));
        }
        List<BundleRevision> providers = this.m_requiredPkgs.get(pkgName);
        if (providers != null) {
            for (BundleRevision p : providers) {
                urls = ((BundleWiringImpl)p.getWiring()).getResourcesByDelegation(name);
                if (urls == null || !urls.hasMoreElements()) continue;
                completeUrlList.add(urls);
            }
        }
        if ((urls = this.m_revision.getResourcesLocal(name)) != null && urls.hasMoreElements()) {
            completeUrlList.add(urls);
        } else {
            try {
                provider = this.m_resolver.resolve(this.m_revision, pkgName);
            }
            catch (ResolutionException resolutionException) {
            }
            catch (BundleException bundleException) {
                // empty catch block
            }
            if (provider != null && (urls = ((BundleWiringImpl)provider.getWiring()).getResourcesByDelegation(name)) != null && urls.hasMoreElements()) {
                completeUrlList.add(urls);
            }
        }
        return new CompoundEnumeration(completeUrlList.toArray(new Enumeration[completeUrlList.size()]));
    }

    private ClassLoader determineParentClassLoader() {
        String cfg = (String)this.m_configMap.get("org.osgi.framework.bundle.parent");
        String string = cfg = cfg == null ? "boot" : cfg;
        ClassLoader parent = cfg.equalsIgnoreCase("app") ? BundleRevisionImpl.getSecureAction().getSystemClassLoader() : (cfg.equalsIgnoreCase("ext") ? BundleRevisionImpl.getSecureAction().getParentClassLoader(BundleRevisionImpl.getSecureAction().getSystemClassLoader()) : (cfg.equalsIgnoreCase("framework") ? BundleRevisionImpl.getSecureAction().getClassLoader(BundleRevisionImpl.class) : (this.m_bootClassLoader == null ? BundleRevisionImpl.getSecureAction().getSystemClassLoader() : null)));
        return parent;
    }

    boolean shouldBootDelegate(String pkgName) {
        if (this.m_bootClassLoader != m_defBootClassLoader) {
            return true;
        }
        boolean result = false;
        if (pkgName.length() > 0) {
            for (int i = 0; !result && i < this.getBundle().getFramework().getBootPackages().length; ++i) {
                if (this.getBundle().getFramework().getBootPackageWildcards()[i] && pkgName.startsWith(this.getBundle().getFramework().getBootPackages()[i])) {
                    return true;
                }
                if (!this.getBundle().getFramework().getBootPackages()[i].equals(pkgName)) continue;
                return true;
            }
        }
        return result;
    }

    ClassLoader getBootDelegationClassLoader() {
        ClassLoader loader = this.m_classLoader;
        ClassLoader parent = loader == null ? this.determineParentClassLoader() : BundleRevisionImpl.getSecureAction().getParentClassLoader(loader);
        return parent == null ? this.m_bootClassLoader : parent;
    }

    public Class getClassByDelegation(String name) throws ClassNotFoundException {
        if (name != null && name.length() > 0 && name.charAt(0) == '[') {
            return Class.forName(name, false, this.getClassLoader());
        }
        if (this.isFiltered(name)) {
            throw new ClassNotFoundException(name);
        }
        ClassLoader cl = this.getClassLoaderInternal();
        if (cl == null) {
            throw new ClassNotFoundException("Unable to load class '" + name + "' because the bundle wiring for " + this.m_revision.getSymbolicName() + " is no longer valid.");
        }
        return cl.loadClass(name);
    }

    private boolean isFiltered(String name) {
        String pkgName = Util.getClassPackage(name);
        List<List<String>> includeFilters = this.m_includedPkgFilters.get(pkgName);
        List<List<String>> excludeFilters = this.m_excludedPkgFilters.get(pkgName);
        if (includeFilters == null && excludeFilters == null) {
            return false;
        }
        String className = Util.getClassName(name);
        boolean included = includeFilters == null;
        for (int i = 0; !included && includeFilters != null && i < includeFilters.size(); ++i) {
            included = SimpleFilter.compareSubstring(includeFilters.get(i), className);
        }
        boolean excluded = false;
        for (int i = 0; !excluded && excludeFilters != null && i < excludeFilters.size(); ++i) {
            excluded = SimpleFilter.compareSubstring(excludeFilters.get(i), className);
        }
        return !included || excluded;
    }

    public URL getResourceByDelegation(String name) {
        try {
            return (URL)this.findClassOrResourceByDelegation(name, false);
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (ResourceNotFoundException ex) {
            this.m_logger.log(this.m_revision.getBundle(), 4, ex.getMessage());
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * WARNING - void declaration
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private Object findClassOrResourceByDelegation(String name, boolean isClass) throws ClassNotFoundException, ResourceNotFoundException {
        void var3_15;
        Object var3_3 = null;
        HashSet<String> requestSet = (HashSet<String>)this.m_cycleCheck.get();
        if (requestSet == null) {
            requestSet = new HashSet<String>();
            this.m_cycleCheck.set(requestSet);
        }
        if (!requestSet.add(name)) return null;
        try {
            Object object;
            boolean accessor;
            String pkgName;
            block27: {
                pkgName = isClass ? Util.getClassPackage(name) : Util.getResourcePackage(name);
                boolean bl = accessor = name.startsWith("sun.reflect.Generated") || name.startsWith("jdk.internal.reflect.");
                if (accessor) {
                    ClassLoader loader;
                    if (this.m_accessorLookupCache == null) {
                        this.m_accessorLookupCache = new ConcurrentHashMap();
                    }
                    if ((loader = this.m_accessorLookupCache.get(name)) != null) {
                        Class<?> clazz = loader.loadClass(name);
                        return clazz;
                    }
                }
                if (this.shouldBootDelegate(pkgName)) {
                    try {
                        Serializable serializable;
                        ClassLoader bdcl = this.getBootDelegationClassLoader();
                        Serializable serializable2 = serializable = isClass ? bdcl.loadClass(name) : bdcl.getResource(name);
                        if (pkgName.startsWith("java.") || serializable != null) {
                            if (accessor) {
                                this.m_accessorLookupCache.put(name, bdcl);
                            }
                            Serializable serializable3 = serializable;
                            return serializable3;
                        }
                    }
                    catch (ClassNotFoundException ex) {
                        if (!pkgName.startsWith("java.")) break block27;
                        throw ex;
                    }
                }
            }
            if (accessor) {
                ArrayList<Collection<BundleRevision>> allRevisions = new ArrayList<Collection<BundleRevision>>(1 + this.m_requiredPkgs.size());
                allRevisions.add(this.m_importedPkgs.values());
                allRevisions.addAll(this.m_requiredPkgs.values());
                for (Collection collection : allRevisions) {
                    for (BundleRevision revision : collection) {
                        BundleClassLoader bundleClassLoader;
                        Class<?> clazz;
                        ClassLoader loader = revision.getWiring().getClassLoader();
                        if (loader == null || !(loader instanceof BundleClassLoader) || (clazz = (bundleClassLoader = (BundleClassLoader)loader).findLoadedClassInternal(name)) == null) continue;
                        this.m_accessorLookupCache.put(name, bundleClassLoader);
                        Class<?> clazz2 = clazz;
                        return clazz2;
                    }
                }
                try {
                    Serializable serializable;
                    ClassLoader classLoader = this.getBootDelegationClassLoader();
                    Serializable serializable4 = serializable = isClass ? classLoader.loadClass(name) : classLoader.getResource(name);
                    if (serializable != null) {
                        this.m_accessorLookupCache.put(name, classLoader);
                        Serializable serializable5 = serializable;
                        return serializable5;
                    }
                }
                catch (ClassNotFoundException classNotFoundException) {
                    // empty catch block
                }
                this.m_accessorLookupCache.put(name, CNFE_CLASS_LOADER);
                CNFE_CLASS_LOADER.loadClass(name);
            }
            if ((object = this.searchImports(pkgName, name, isClass)) == null) {
                void var3_13;
                ClassLoader cl = this.getClassLoaderInternal();
                if (cl == null) {
                    if (!isClass) throw new ResourceNotFoundException("Unable to load resource '" + name + "' because the bundle wiring for " + this.m_revision.getSymbolicName() + " is no longer valid.");
                    throw new ClassNotFoundException("Unable to load class '" + name + "' because the bundle wiring for " + this.m_revision.getSymbolicName() + " is no longer valid.");
                }
                if (cl instanceof BundleClassLoader) {
                    Serializable serializable = isClass ? ((BundleClassLoader)cl).findClass(name) : ((BundleClassLoader)cl).findResource(name);
                } else {
                    Class<?> clazz;
                    Serializable serializable = isClass ? cl.loadClass(name) : (clazz = !name.startsWith("/") ? cl.getResource(name) : cl.getResource(name.substring(1)));
                }
                if (var3_13 == null) {
                    Object object2 = this.searchDynamicImports(pkgName, name, isClass);
                }
            }
        }
        finally {
            requestSet.remove(name);
        }
        if (var3_15 != null) return var3_15;
        if (!isClass) throw new ResourceNotFoundException(name + " not found by " + this.getBundle());
        throw new ClassNotFoundException(name + " not found by " + this.getBundle());
    }

    private Object searchImports(String pkgName, String name, boolean isClass) throws ClassNotFoundException, ResourceNotFoundException {
        BundleRevision provider = this.m_importedPkgs.get(pkgName);
        if (provider != null) {
            Serializable result;
            Serializable serializable = result = isClass ? ((BundleWiringImpl)provider.getWiring()).getClassByDelegation(name) : ((BundleWiringImpl)provider.getWiring()).getResourceByDelegation(name);
            if (result != null) {
                return result;
            }
            if (isClass) {
                throw new ClassNotFoundException(name);
            }
            throw new ResourceNotFoundException(name);
        }
        List<BundleRevision> providers = this.m_requiredPkgs.get(pkgName);
        if (providers != null) {
            for (BundleRevision p : providers) {
                try {
                    Serializable result = isClass ? ((BundleWiringImpl)p.getWiring()).getClassByDelegation(name) : ((BundleWiringImpl)p.getWiring()).getResourceByDelegation(name);
                    if (result == null) continue;
                    return result;
                }
                catch (ClassNotFoundException classNotFoundException) {
                }
            }
        }
        return null;
    }

    private Object searchDynamicImports(String pkgName, String name, boolean isClass) throws ClassNotFoundException, ResourceNotFoundException {
        BundleRevision provider = null;
        try {
            provider = this.m_resolver.resolve(this.m_revision, pkgName);
        }
        catch (ResolutionException resolutionException) {
        }
        catch (BundleException bundleException) {
            // empty catch block
        }
        if (provider != null) {
            return isClass ? ((BundleWiringImpl)provider.getWiring()).getClassByDelegation(name) : ((BundleWiringImpl)provider.getWiring()).getResourceByDelegation(name);
        }
        return this.tryImplicitBootDelegation(name, isClass);
    }

    private Object tryImplicitBootDelegation(final String name, final boolean isClass) throws ClassNotFoundException, ResourceNotFoundException {
        if (this.m_implicitBootDelegation) {
            final Class[] classes = m_sm.getClassContext();
            try {
                if (System.getSecurityManager() != null) {
                    return AccessController.doPrivileged(new PrivilegedExceptionAction(){

                        public Object run() throws Exception {
                            return BundleWiringImpl.this.doImplicitBootDelegation(classes, name, isClass);
                        }
                    });
                }
                return this.doImplicitBootDelegation(classes, name, isClass);
            }
            catch (PrivilegedActionException ex) {
                Exception cause = ex.getException();
                if (cause instanceof ClassNotFoundException) {
                    throw (ClassNotFoundException)cause;
                }
                throw (ResourceNotFoundException)cause;
            }
        }
        return null;
    }

    private Object doImplicitBootDelegation(Class[] classes, String name, boolean isClass) throws ClassNotFoundException, ResourceNotFoundException {
        for (int i = 1; !(i >= classes.length || Thread.class.equals((Object)classes[i]) || this.isClassLoadedFromBundleRevision(classes[i]) || BundleImpl.class.equals((Object)classes[i]) || ServiceRegistrationImpl.ServiceReferenceImpl.class.equals((Object)classes[i])); ++i) {
            if (!this.isClassExternal(classes[i])) continue;
            try {
                return isClass ? BundleRevisionImpl.getSecureAction().getClassLoader(this.getClass()).loadClass(name) : BundleRevisionImpl.getSecureAction().getClassLoader(this.getClass()).getResource(name);
            }
            catch (NoClassDefFoundError noClassDefFoundError) {
                break;
            }
        }
        return null;
    }

    private boolean isClassLoadedFromBundleRevision(Class clazz) {
        if (BundleClassLoader.class.isInstance(BundleRevisionImpl.getSecureAction().getClassLoader(clazz))) {
            return true;
        }
        ClassLoader last = null;
        ClassLoader cl = BundleRevisionImpl.getSecureAction().getClassLoader(clazz);
        while (cl != null && last != cl) {
            last = cl;
            if (BundleClassLoader.class.isInstance(cl)) {
                return true;
            }
            cl = BundleRevisionImpl.getSecureAction().getClassLoader(cl.getClass());
        }
        return false;
    }

    private boolean isClassExternal(Class clazz) {
        if (clazz.getName().startsWith("org.apache.felix.framework.")) {
            return false;
        }
        if (clazz.getName().startsWith("org.osgi.framework.")) {
            return false;
        }
        if (ClassLoader.class.equals((Object)clazz)) {
            return false;
        }
        return !Class.class.equals((Object)clazz);
    }

    static URL convertToLocalUrl(URL url) {
        if (url.getProtocol().equals("bundle")) {
            try {
                url = ((URLHandlersBundleURLConnection)url.openConnection()).getLocalURL();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        return url;
    }

    private static String diagnoseClassLoadError(StatefulResolver resolver, BundleRevision revision, String name) {
        String pkgName = Util.getClassPackage(name);
        if (pkgName.length() == 0) {
            return null;
        }
        String importer = revision.getBundle().toString();
        List<BundleWire> wires = revision.getWiring() == null ? null : revision.getWiring().getProvidedWires(null);
        for (int i = 0; wires != null && i < wires.size(); ++i) {
            if (!wires.get(i).getCapability().getNamespace().equals("osgi.wiring.package") || !wires.get(i).getCapability().getAttributes().get("osgi.wiring.package").equals(pkgName)) continue;
            String exporter = wires.get(i).getProviderWiring().getBundle().toString();
            StringBuilder sb = new StringBuilder("*** Package '");
            sb.append(pkgName);
            sb.append("' is imported by bundle ");
            sb.append(importer);
            sb.append(" from bundle ");
            sb.append(exporter);
            sb.append(", but the exported package from bundle ");
            sb.append(exporter);
            sb.append(" does not contain the requested class '");
            sb.append(name);
            sb.append("'. Please verify that the class name is correct in the importing bundle ");
            sb.append(importer);
            sb.append(" and/or that the exported package is correctly bundled in ");
            sb.append(exporter);
            sb.append(". ***");
            return sb.toString();
        }
        List<BundleRequirement> reqs = revision.getWiring().getRequirements(null);
        if (resolver.isAllowedDynamicImport(revision, pkgName)) {
            Map dirs = Collections.EMPTY_MAP;
            Map<String, Object> attrs = Collections.singletonMap("osgi.wiring.package", pkgName);
            BundleRequirementImpl req = new BundleRequirementImpl(revision, "osgi.wiring.package", dirs, attrs);
            List<BundleCapability> exporters = resolver.findProviders(req, false);
            BundleRevision provider = null;
            try {
                provider = resolver.resolve(revision, pkgName);
            }
            catch (Exception ex) {
                provider = null;
            }
            String exporter = exporters.isEmpty() ? null : exporters.iterator().next().toString();
            StringBuilder sb = new StringBuilder("*** Class '");
            sb.append(name);
            sb.append("' was not found, but this is likely normal since package '");
            sb.append(pkgName);
            sb.append("' is dynamically imported by bundle ");
            sb.append(importer);
            sb.append(".");
            if (exporters.size() > 0 && provider == null) {
                sb.append(" However, bundle ");
                sb.append(exporter);
                sb.append(" does export this package with attributes that do not match.");
            }
            sb.append(" ***");
            return sb.toString();
        }
        Map dirs = Collections.EMPTY_MAP;
        Map<String, Object> attrs = Collections.singletonMap("osgi.wiring.package", pkgName);
        BundleRequirementImpl req = new BundleRequirementImpl(revision, "osgi.wiring.package", dirs, attrs);
        List<BundleCapability> exports = resolver.findProviders(req, false);
        if (exports.size() > 0) {
            boolean classpath = false;
            try {
                BundleRevisionImpl.getSecureAction().getClassLoader(BundleClassLoader.class).loadClass(name);
                classpath = true;
            }
            catch (NoClassDefFoundError exporter) {
            }
            catch (Exception exporter) {
                // empty catch block
            }
            String exporter = exports.iterator().next().toString();
            StringBuilder sb = new StringBuilder("*** Class '");
            sb.append(name);
            sb.append("' was not found because bundle ");
            sb.append(importer);
            sb.append(" does not import '");
            sb.append(pkgName);
            sb.append("' even though bundle ");
            sb.append(exporter);
            sb.append(" does export it.");
            if (classpath) {
                sb.append(" Additionally, the class is also available from the system class loader. There are two fixes: 1) Add an import for '");
                sb.append(pkgName);
                sb.append("' to bundle ");
                sb.append(importer);
                sb.append("; imports are necessary for each class directly touched by bundle code or indirectly touched, such as super classes if their methods are used. ");
                sb.append("2) Add package '");
                sb.append(pkgName);
                sb.append("' to the '");
                sb.append("org.osgi.framework.bootdelegation");
                sb.append("' property; a library or VM bug can cause classes to be loaded by the wrong class loader. The first approach is preferable for preserving modularity.");
            } else {
                sb.append(" To resolve this issue, add an import for '");
                sb.append(pkgName);
                sb.append("' to bundle ");
                sb.append(importer);
                sb.append(".");
            }
            sb.append(" ***");
            return sb.toString();
        }
        try {
            BundleRevisionImpl.getSecureAction().getClassLoader(BundleClassLoader.class).loadClass(name);
            StringBuilder sb = new StringBuilder("*** Package '");
            sb.append(pkgName);
            sb.append("' is not imported by bundle ");
            sb.append(importer);
            sb.append(", nor is there any bundle that exports package '");
            sb.append(pkgName);
            sb.append("'. However, the class '");
            sb.append(name);
            sb.append("' is available from the system class loader. There are two fixes: 1) Add package '");
            sb.append(pkgName);
            sb.append("' to the '");
            sb.append("org.osgi.framework.system.packages.extra");
            sb.append("' property and modify bundle ");
            sb.append(importer);
            sb.append(" to import this package; this causes the system bundle to export class path packages. 2) Add package '");
            sb.append(pkgName);
            sb.append("' to the '");
            sb.append("org.osgi.framework.bootdelegation");
            sb.append("' property; a library or VM bug can cause classes to be loaded by the wrong class loader. The first approach is preferable for preserving modularity.");
            sb.append(" ***");
            return sb.toString();
        }
        catch (Exception sb) {
            StringBuilder sb2 = new StringBuilder("*** Class '");
            sb2.append(name);
            sb2.append("' was not found. Bundle ");
            sb2.append(importer);
            sb2.append(" does not import package '");
            sb2.append(pkgName);
            sb2.append("', nor is the package exported by any other bundle or available from the system class loader.");
            sb2.append(" ***");
            return sb2.toString();
        }
    }

    static {
        ClassLoader cl = null;
        try {
            cl = (ClassLoader)BundleRevisionImpl.getSecureAction().invokeDirect(BundleRevisionImpl.getSecureAction().getMethod(ClassLoader.class, "getPlatformClassLoader", null), null, null);
        }
        catch (Throwable t) {
            try {
                Constructor ctor = BundleRevisionImpl.getSecureAction().getDeclaredConstructor(SecureClassLoader.class, new Class[]{ClassLoader.class});
                BundleRevisionImpl.getSecureAction().setAccesssible(ctor);
                cl = (ClassLoader)BundleRevisionImpl.getSecureAction().invoke(ctor, new Object[]{null});
            }
            catch (Throwable ex) {
                cl = null;
                System.err.println("Problem creating boot delegation class loader: " + ex);
            }
        }
        m_defBootClassLoader = cl;
        m_sm = new SecurityManagerEx();
        m_deferredActivation = new ThreadLocal();
    }

    private static class ResourceSource
    implements Comparable<ResourceSource> {
        public final String m_resource;
        public final BundleRevision m_revision;

        public ResourceSource(String resource, BundleRevision revision) {
            this.m_resource = resource;
            this.m_revision = revision;
        }

        public boolean equals(Object o) {
            if (o instanceof ResourceSource) {
                return this.m_resource.equals(((ResourceSource)o).m_resource);
            }
            return false;
        }

        public int hashCode() {
            return this.m_resource.hashCode();
        }

        @Override
        public int compareTo(ResourceSource t) {
            return this.m_resource.compareTo(t.m_resource);
        }

        public String toString() {
            return this.m_resource + " -> " + this.m_revision.getSymbolicName() + " [" + this.m_revision + "]";
        }
    }

    public static class BundleClassLoader
    extends SecureClassLoader
    implements BundleReference {
        static final boolean m_isParallel = BundleClassLoader.registerAsParallel();
        private volatile boolean m_isActivationTriggered = false;
        private Object[][] m_cachedLibs = new Object[0][];
        private static final int LIBNAME_IDX = 0;
        private static final int LIBPATH_IDX = 1;
        private final ConcurrentHashMap<String, Thread> m_classLocks = new ConcurrentHashMap();
        private final BundleWiringImpl m_wiring;
        private final Logger m_logger;

        @IgnoreJRERequirement
        private static boolean registerAsParallel() {
            boolean registered = false;
            try {
                registered = ClassLoader.registerAsParallelCapable();
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return registered;
        }

        public BundleClassLoader(BundleWiringImpl wiring, ClassLoader parent, Logger logger) {
            super(parent);
            this.m_wiring = wiring;
            this.m_logger = logger;
        }

        public boolean isActivationTriggered() {
            return this.m_isActivationTriggered;
        }

        @Override
        public BundleImpl getBundle() {
            return this.m_wiring.getBundle();
        }

        protected Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
            Class clazz = this.findLoadedClass(name);
            if (clazz == null) {
                try {
                    clazz = (Class)this.m_wiring.findClassOrResourceByDelegation(name, true);
                }
                catch (ResourceNotFoundException resourceNotFoundException) {
                }
                catch (ClassNotFoundException cnfe) {
                    ClassNotFoundException ex = cnfe;
                    if (this.m_logger.getLogLevel() >= 4) {
                        String msg = BundleWiringImpl.diagnoseClassLoadError(this.m_wiring.m_resolver, this.m_wiring.m_revision, name);
                        ex = msg != null ? new ClassNotFoundException(msg, cnfe) : ex;
                    }
                    throw ex;
                }
                if (clazz == null) {
                    throw new ClassNotFoundException("Cycle detected while trying to load class: " + name);
                }
            }
            if (resolve) {
                this.resolveClass(clazz);
            }
            return clazz;
        }

        protected Class findClass(String name) throws ClassNotFoundException {
            Class clazz = this.findLoadedClass(name);
            if (clazz == null) {
                if (this.m_wiring.m_isDisposed) {
                    throw new ClassNotFoundException("Unable to load class '" + name + "' because the bundle wiring for " + this.m_wiring.m_revision.getSymbolicName() + " is no longer valid.");
                }
                String actual = name.replace('.', '/') + ".class";
                byte[] bytes = null;
                List<Content> contentPath = this.m_wiring.m_revision.getContentPath();
                Content content = null;
                for (int i = 0; bytes == null && i < contentPath.size(); ++i) {
                    bytes = contentPath.get(i).getEntryAsBytes(actual);
                    content = contentPath.get(i);
                }
                if (bytes != null) {
                    String pkgName = Util.getClassPackage(name);
                    Felix felix = this.m_wiring.m_revision.getBundle().getFramework();
                    Set<ServiceReference<WeavingHook>> hooks = felix.getHookRegistry().getHooks(WeavingHook.class);
                    Set<ServiceReference<WovenClassListener>> wovenClassListeners = felix.getHookRegistry().getHooks(WovenClassListener.class);
                    WovenClassImpl wci = null;
                    if (!hooks.isEmpty()) {
                        wci = new WovenClassImpl(name, this.m_wiring, bytes);
                        try {
                            this.transformClass(felix, wci, hooks, wovenClassListeners, name, bytes);
                        }
                        catch (Error e) {
                            wci.complete();
                            wci.setState(8);
                            this.callWovenClassListeners(felix, wovenClassListeners, wci);
                            throw e;
                        }
                    }
                    try {
                        clazz = this.isParallel() ? this.defineClassParallel(name, felix, wovenClassListeners, wci, bytes, content, pkgName) : this.defineClassNotParallel(name, felix, wovenClassListeners, wci, bytes, content, pkgName);
                    }
                    catch (ClassFormatError e) {
                        if (wci != null) {
                            wci.setState(16);
                            this.callWovenClassListeners(felix, wovenClassListeners, wci);
                        }
                        throw e;
                    }
                    List deferredList = (List)m_deferredActivation.get();
                    if (deferredList != null && deferredList.size() > 0 && ((Object[])deferredList.get(0))[0].equals(name)) {
                        m_deferredActivation.set(null);
                        while (!deferredList.isEmpty()) {
                            Object[] lazy = (Object[])deferredList.remove(deferredList.size() - 1);
                            try {
                                felix.getFramework().activateBundle((BundleImpl)lazy[1], true);
                            }
                            catch (Throwable ex) {
                                this.m_logger.log((BundleImpl)lazy[1], 2, "Unable to lazily start bundle.", ex);
                            }
                        }
                    }
                }
            }
            return clazz;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        Class defineClassParallel(String name, Felix felix, Set<ServiceReference<WovenClassListener>> wovenClassListeners, WovenClassImpl wci, byte[] bytes, Content content, String pkgName) throws ClassFormatError {
            Class clazz = null;
            Thread me = Thread.currentThread();
            while (clazz == null && this.m_classLocks.putIfAbsent(name, me) != me) {
                clazz = this.findLoadedClass(name);
            }
            if (clazz == null) {
                try {
                    clazz = this.findLoadedClass(name);
                    if (clazz == null) {
                        clazz = this.defineClass(felix, wovenClassListeners, wci, name, bytes, content, pkgName);
                    }
                }
                finally {
                    this.m_classLocks.remove(name);
                }
            }
            return clazz;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        Class defineClassNotParallel(String name, Felix felix, Set<ServiceReference<WovenClassListener>> wovenClassListeners, WovenClassImpl wci, byte[] bytes, Content content, String pkgName) throws ClassFormatError {
            Class clazz = this.findLoadedClass(name);
            if (clazz == null) {
                ConcurrentHashMap<String, Thread> concurrentHashMap = this.m_classLocks;
                synchronized (concurrentHashMap) {
                    clazz = this.findLoadedClass(name);
                    if (clazz == null) {
                        clazz = this.defineClass(felix, wovenClassListeners, wci, name, bytes, content, pkgName);
                    }
                }
            }
            return clazz;
        }

        /*
         * WARNING - void declaration
         */
        Class defineClass(Felix felix, Set<ServiceReference<WovenClassListener>> wovenClassListeners, WovenClassImpl wci, String name, byte[] bytes, Content content, String pkgName) throws ClassFormatError {
            boolean isTriggerClass;
            if (wci != null) {
                bytes = wci._getBytes();
                List<String> wovenImports = wci.getDynamicImportsInternal();
                ArrayList<BundleRequirement> allWovenReqs = new ArrayList<BundleRequirement>();
                for (String string : wovenImports) {
                    try {
                        List<BundleRequirement> wovenReqs = ManifestParser.parseDynamicImportHeader(this.m_logger, this.m_wiring.m_revision, string);
                        allWovenReqs.addAll(wovenReqs);
                    }
                    catch (BundleException wovenReqs) {}
                }
                if (!allWovenReqs.isEmpty()) {
                    void var11_17;
                    HashSet<String> filters = new HashSet<String>();
                    if (this.m_wiring.m_wovenReqs != null) {
                        for (BundleRequirement req : this.m_wiring.m_wovenReqs) {
                            filters.add(((BundleRequirementImpl)req).getFilter().toString());
                        }
                    }
                    int n = allWovenReqs.size();
                    while (var11_17 < allWovenReqs.size()) {
                        BundleRequirement wovenReq = (BundleRequirement)allWovenReqs.get((int)var11_17);
                        String filter = ((BundleRequirementImpl)wovenReq).getFilter().toString();
                        if (!filters.contains(filter)) {
                            filters.add(filter);
                            ++var11_17;
                            continue;
                        }
                        allWovenReqs.remove((int)var11_17);
                    }
                    if (!allWovenReqs.isEmpty()) {
                        if (this.m_wiring.m_wovenReqs != null) {
                            allWovenReqs.addAll(0, this.m_wiring.m_wovenReqs);
                        }
                        this.m_wiring.m_wovenReqs = allWovenReqs;
                    }
                }
            }
            int activationPolicy = this.getBundle().isDeclaredActivationPolicyUsed() ? this.getBundle().adapt(BundleRevisionImpl.class).getDeclaredActivationPolicy() : 0;
            boolean bl = isTriggerClass = this.m_isActivationTriggered ? false : this.m_wiring.m_revision.isActivationTrigger(pkgName);
            if (!this.m_isActivationTriggered && isTriggerClass && activationPolicy == 1 && this.getBundle().getState() == 8) {
                ArrayList<Object[]> deferredList = (ArrayList<Object[]>)m_deferredActivation.get();
                if (deferredList == null) {
                    deferredList = new ArrayList<Object[]>();
                    m_deferredActivation.set(deferredList);
                }
                deferredList.add(new Object[]{name, this.getBundle()});
            }
            if (pkgName.length() > 0 && this.getPackage(pkgName) == null) {
                Object[] params = this.definePackage(pkgName);
                try {
                    this.definePackage(pkgName, (String)params[0], (String)params[1], (String)params[2], (String)params[3], (String)params[4], (String)params[5], null);
                }
                catch (IllegalArgumentException illegalArgumentException) {
                    // empty catch block
                }
            }
            Class<?> clazz = null;
            clazz = this.m_wiring.m_revision.getProtectionDomain() != null ? this.defineClass(name, bytes, 0, bytes.length, this.m_wiring.m_revision.getProtectionDomain()) : this.defineClass(name, bytes, 0, bytes.length);
            if (wci != null) {
                wci.completeDefine(clazz);
                wci.setState(4);
                this.callWovenClassListeners(felix, wovenClassListeners, wci);
            }
            if (!this.m_isActivationTriggered && isTriggerClass && clazz != null) {
                this.m_isActivationTriggered = true;
            }
            return clazz;
        }

        void transformClass(Felix felix, WovenClassImpl wci, Set<ServiceReference<WeavingHook>> hooks, Set<ServiceReference<WovenClassListener>> wovenClassListeners, String name, byte[] bytes) throws Error {
            for (ServiceReference<WeavingHook> sr : hooks) {
                WeavingHook wh;
                if (felix.getHookRegistry().isHookBlackListed(sr) || (wh = felix.getService(felix, sr, false)) == null) continue;
                try {
                    BundleRevisionImpl.getSecureAction().invokeWeavingHook(wh, wci);
                }
                catch (Throwable th) {
                    if (!(th instanceof WeavingException)) {
                        felix.getHookRegistry().blackListHook(sr);
                    }
                    felix.fireFrameworkEvent(2, sr.getBundle(), th);
                    ClassFormatError error = new ClassFormatError("Weaving hook failed.");
                    error.initCause(th);
                    throw error;
                }
                finally {
                    felix.ungetService(felix, sr, null);
                }
            }
            wci.setState(2);
            this.callWovenClassListeners(felix, wovenClassListeners, wci);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        protected void callWovenClassListeners(Felix felix, Set<ServiceReference<WovenClassListener>> wovenClassListeners, WovenClass wovenClass) {
            if (wovenClassListeners != null) {
                for (ServiceReference<WovenClassListener> currentWovenClassListenerRef : wovenClassListeners) {
                    WovenClassListener currentWovenClassListner = felix.getService(felix, currentWovenClassListenerRef, false);
                    try {
                        BundleRevisionImpl.getSecureAction().invokeWovenClassListener(currentWovenClassListner, wovenClass);
                    }
                    catch (Exception e) {
                        this.m_logger.log(1, "Woven Class Listner failed.", e);
                    }
                    finally {
                        felix.ungetService(felix, currentWovenClassListenerRef, null);
                    }
                }
            }
        }

        private Object[] definePackage(String pkgName) {
            String spectitle = (String)this.m_wiring.m_revision.getHeaders().get("Specification-Title");
            String specversion = (String)this.m_wiring.m_revision.getHeaders().get("Specification-Version");
            String specvendor = (String)this.m_wiring.m_revision.getHeaders().get("Specification-Vendor");
            String impltitle = (String)this.m_wiring.m_revision.getHeaders().get("Implementation-Title");
            String implversion = (String)this.m_wiring.m_revision.getHeaders().get("Implementation-Version");
            String implvendor = (String)this.m_wiring.m_revision.getHeaders().get("Implementation-Vendor");
            if (spectitle != null || specversion != null || specvendor != null || impltitle != null || implversion != null || implvendor != null) {
                return new Object[]{spectitle, specversion, specvendor, impltitle, implversion, implvendor};
            }
            return new Object[]{null, null, null, null, null, null};
        }

        @Override
        public URL getResource(String name) {
            URL url = this.m_wiring.getResourceByDelegation(name);
            if (this.m_wiring.m_useLocalURLs) {
                url = BundleWiringImpl.convertToLocalUrl(url);
            }
            return url;
        }

        @Override
        protected URL findResource(String name) {
            return this.m_wiring.m_revision.getResourceLocal(name);
        }

        protected Enumeration findResources(String name) {
            return this.m_wiring.m_revision.getResourcesLocal(name);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        protected String findLibrary(String name) {
            if (name.startsWith("/")) {
                name = name.substring(1);
            }
            String result = null;
            BundleClassLoader bundleClassLoader = this;
            synchronized (bundleClassLoader) {
                for (int i = 0; result == null && i < this.m_cachedLibs.length; ++i) {
                    if (!this.m_cachedLibs[i][0].equals(name)) continue;
                    result = (String)this.m_cachedLibs[i][1];
                }
                if (result == null) {
                    List<NativeLibrary> libs = this.m_wiring.getNativeLibraries();
                    for (int libIdx = 0; libs != null && libIdx < libs.size(); ++libIdx) {
                        if (!libs.get(libIdx).match(this.m_wiring.m_configMap, name)) continue;
                        result = this.m_wiring.m_revision.getContent().getEntryAsNativeLibrary(libs.get(libIdx).getEntryName());
                        for (int i = 0; result == null && this.m_wiring.m_fragmentContents != null && i < this.m_wiring.m_fragmentContents.size(); ++i) {
                            result = ((Content)this.m_wiring.m_fragmentContents.get(i)).getEntryAsNativeLibrary(libs.get(libIdx).getEntryName());
                        }
                    }
                    if (result != null) {
                        Object[][] tmp = new Object[this.m_cachedLibs.length + 1][];
                        System.arraycopy(this.m_cachedLibs, 0, tmp, 0, this.m_cachedLibs.length);
                        tmp[this.m_cachedLibs.length] = new Object[]{name, result};
                        this.m_cachedLibs = tmp;
                    }
                }
            }
            return result;
        }

        protected boolean isParallel() {
            return m_isParallel;
        }

        public Enumeration getResources(String name) {
            Enumeration urls = this.m_wiring.getResourcesByDelegation(name);
            if (this.m_wiring.m_useLocalURLs) {
                urls = new ToLocalUrlEnumeration(urls);
            }
            return urls;
        }

        public String toString() {
            return this.m_wiring.toString();
        }

        Class<?> findLoadedClassInternal(String name) {
            return super.findLoadedClass(name);
        }
    }

    static class ToLocalUrlEnumeration
    implements Enumeration {
        final Enumeration m_enumeration;

        ToLocalUrlEnumeration(Enumeration enumeration) {
            this.m_enumeration = enumeration;
        }

        @Override
        public boolean hasMoreElements() {
            return this.m_enumeration.hasMoreElements();
        }

        public Object nextElement() {
            return BundleWiringImpl.convertToLocalUrl((URL)this.m_enumeration.nextElement());
        }
    }
}

