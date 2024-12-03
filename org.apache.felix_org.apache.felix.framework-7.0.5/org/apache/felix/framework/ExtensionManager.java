/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
 */
package org.apache.felix.framework;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.felix.framework.BundleImpl;
import org.apache.felix.framework.BundleProtectionDomain;
import org.apache.felix.framework.BundleRevisionImpl;
import org.apache.felix.framework.BundleWiringImpl;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.Logger;
import org.apache.felix.framework.VersionConverter;
import org.apache.felix.framework.cache.ConnectContentContent;
import org.apache.felix.framework.cache.Content;
import org.apache.felix.framework.cache.DirectoryContent;
import org.apache.felix.framework.cache.JarContent;
import org.apache.felix.framework.ext.ClassPathExtenderFactory;
import org.apache.felix.framework.util.ClassParser;
import org.apache.felix.framework.util.StringMap;
import org.apache.felix.framework.util.Util;
import org.apache.felix.framework.util.manifestparser.ManifestParser;
import org.apache.felix.framework.util.manifestparser.NativeLibrary;
import org.apache.felix.framework.util.manifestparser.NativeLibraryClause;
import org.apache.felix.framework.wiring.BundleCapabilityImpl;
import org.apache.felix.framework.wiring.BundleRequirementImpl;
import org.apache.felix.framework.wiring.BundleWireImpl;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;
import org.osgi.framework.AdminPermission;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleCapability;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;

class ExtensionManager
implements Content {
    static final ClassPathExtenderFactory.ClassPathExtender m_extenderFramework;
    static final ClassPathExtenderFactory.ClassPathExtender m_extenderBoot;
    private static final Set<String> IDENTITY;
    private final Logger m_logger;
    private volatile ExtensionManagerRevision m_systemBundleRevision;
    private final List<ExtensionTuple> m_extensionTuples = Collections.synchronizedList(new ArrayList());
    private final List<BundleRevisionImpl> m_resolvedExtensions = new CopyOnWriteArrayList<BundleRevisionImpl>();
    private final List<BundleRevisionImpl> m_unresolvedExtensions = new CopyOnWriteArrayList<BundleRevisionImpl>();
    private final List<BundleRevisionImpl> m_failedExtensions = new CopyOnWriteArrayList<BundleRevisionImpl>();

    ExtensionManager(Logger logger, Map configMap, Felix felix) {
        this.m_logger = logger;
        this.m_systemBundleRevision = new ExtensionManagerRevision(configMap, felix);
    }

    protected BundleCapability buildNativeCapabilites(BundleRevisionImpl revision, Map configMap) {
        String osArchitecture = (String)configMap.get("org.osgi.framework.processor");
        String osName = (String)configMap.get("org.osgi.framework.os.name");
        String osVersion = (String)configMap.get("org.osgi.framework.os.version");
        String userLang = (String)configMap.get("org.osgi.framework.language");
        HashMap<String, Object> attributes = new HashMap<String, Object>();
        attributes.putAll(configMap);
        if (osArchitecture != null) {
            attributes.put("osgi.native.processor", NativeLibraryClause.getProcessorWithAliases(osArchitecture));
        }
        if (osName != null) {
            attributes.put("osgi.native.osname", NativeLibraryClause.getOsNameWithAliases(osName));
        }
        if (osVersion != null) {
            attributes.put("osgi.native.osversion", VersionConverter.toOsgiVersion(osVersion));
        }
        if (userLang != null) {
            attributes.put("osgi.native.language", userLang);
        }
        return new BundleCapabilityImpl(revision, "osgi.native", Collections.emptyMap(), attributes);
    }

    @IgnoreJRERequirement
    void updateRevision(Felix felix, Map configMap) {
        String string;
        Map<String, Set<String>> exports;
        HashMap<String, String> config = new HashMap<String, String>(configMap);
        Properties defaultProperties = Util.loadDefaultProperties(this.m_logger);
        Util.initializeJPMSEE(felix._getProperty("java.specification.version"), defaultProperties, this.m_logger);
        String sysprops = felix._getProperty("org.osgi.framework.system.packages");
        boolean subst = "true".equalsIgnoreCase(felix._getProperty("felix.systempackages.substitution"));
        if (sysprops != null && sysprops.isEmpty() && felix.hasConnectFramework()) {
            subst = true;
            sysprops = "${osgi-exports}";
            config.put("org.osgi.framework.system.packages", sysprops);
        }
        if ((exports = Util.initializeJPMS(defaultProperties)) != null && (sysprops == null || "true".equalsIgnoreCase(felix._getProperty("felix.systempackages.substitution")))) {
            ClassParser classParser = new ClassParser();
            HashSet<String> hashSet = new HashSet<String>();
            for (Set<String> moduleImport : exports.values()) {
                for (String pkg : moduleImport) {
                    if (pkg.startsWith("java.")) continue;
                    hashSet.add(pkg);
                }
            }
            for (String moduleKey : exports.keySet()) {
                int idx = moduleKey.indexOf("@");
                String module = idx == -1 ? moduleKey : moduleKey.substring(0, idx);
                if (felix._getProperty(module) != null || exports.get(moduleKey).isEmpty() || defaultProperties.getProperty(module) != null) continue;
                TreeMap<String, TreeSet<String>> referred = new TreeMap<String, TreeSet<String>>();
                if ("true".equalsIgnoreCase(felix._getProperty("felix.systempackages.calculate.uses"))) {
                    FileSystem fs = FileSystems.getFileSystem(URI.create("jrt:/"));
                    try {
                        Properties cachedProps = new Properties();
                        File modulesDir = felix.getDataFile(felix, "modules");
                        Felix.m_secureAction.mkdirs(modulesDir);
                        File cached = new File(modulesDir, moduleKey + ".properties");
                        if (Felix.m_secureAction.isFile(cached)) {
                            InputStream input = Felix.m_secureAction.getInputStream(cached);
                            cachedProps.load(new InputStreamReader(input, "UTF-8"));
                            input.close();
                            Enumeration<?> keys = cachedProps.propertyNames();
                            while (keys.hasMoreElements()) {
                                String pkg = (String)keys.nextElement();
                                referred.put(pkg, new TreeSet<String>(Arrays.asList(cachedProps.getProperty(pkg).split(","))));
                            }
                        } else {
                            Path path = fs.getPath("modules", module.substring("felix.jpms.".length()));
                            Files.walkFileTree(path, (FileVisitor)Felix.class.getClassLoader().loadClass("org.apache.felix.framework.util.ClassFileVisitor").getConstructor(Set.class, Set.class, ClassParser.class, SortedMap.class).newInstance(hashSet, exports.get(moduleKey), classParser, referred));
                            for (String pkg : referred.keySet()) {
                                SortedSet uses = (SortedSet)referred.get(pkg);
                                if (uses == null || uses.isEmpty()) continue;
                                cachedProps.setProperty(pkg, String.join((CharSequence)",", uses));
                            }
                            OutputStream output = Felix.m_secureAction.getOutputStream(cached);
                            cachedProps.store(new OutputStreamWriter(output, "UTF-8"), null);
                            output.close();
                        }
                    }
                    catch (Throwable e) {
                        this.m_logger.log(2, "Exception calculating JPMS module exports", e);
                    }
                }
                String pkgs = "";
                for (String pkg : exports.get(moduleKey)) {
                    pkgs = pkgs + "," + pkg;
                    SortedSet uses = (SortedSet)referred.get(pkg);
                    if (uses != null && !uses.isEmpty()) {
                        pkgs = pkgs + ";uses:=\"";
                        String sep = "";
                        for (String u : uses) {
                            pkgs = pkgs + sep + u;
                            sep = ",";
                        }
                        pkgs = pkgs + "\"";
                    }
                    pkgs = pkgs + ";version=\"" + defaultProperties.getProperty("felix.detect.java.version") + "\"";
                }
                defaultProperties.put(module, pkgs);
            }
        }
        for (Map.Entry entry : defaultProperties.entrySet()) {
            if (config.containsKey(entry.getKey())) continue;
            config.put((String)entry.getKey(), (String)entry.getValue());
        }
        if (sysprops != null && subst) {
            config.put("org.osgi.framework.system.packages", Util.getPropertyWithSubs(Util.toProperties(config), "org.osgi.framework.system.packages"));
        } else if (sysprops == null) {
            config.put("org.osgi.framework.system.packages", Util.getPropertyWithSubs(Util.toProperties(config), "org.osgi.framework.system.packages"));
        }
        String syspropsExtra = felix._getProperty("org.osgi.framework.system.packages.extra");
        if (syspropsExtra != null && "true".equalsIgnoreCase(felix._getProperty("felix.systempackages.substitution"))) {
            config.put("org.osgi.framework.system.packages.extra", Util.getPropertyWithSubs(Util.toProperties(config), "org.osgi.framework.system.packages.extra"));
        }
        if ((string = felix._getProperty("org.osgi.framework.system.capabilities")) != null && "true".equalsIgnoreCase(felix._getProperty("felix.systempackages.substitution"))) {
            config.put("org.osgi.framework.system.capabilities", Util.getPropertyWithSubs(Util.toProperties(config), "org.osgi.framework.system.capabilities"));
        } else if (string == null) {
            config.put("org.osgi.framework.system.capabilities", Util.getPropertyWithSubs(Util.toProperties(config), "org.osgi.framework.system.capabilities"));
        }
        String syscapsExtra = felix._getProperty("org.osgi.framework.system.capabilities.extra");
        if (syscapsExtra != null && "true".equalsIgnoreCase(felix._getProperty("felix.systempackages.substitution"))) {
            config.put("org.osgi.framework.system.capabilities.extra", Util.getPropertyWithSubs(Util.toProperties(config), "org.osgi.framework.system.capabilities.extra"));
        }
        this.m_systemBundleRevision.update(config);
    }

    public BundleRevisionImpl getRevision() {
        return this.m_systemBundleRevision;
    }

    void addExtensionBundle(BundleImpl bundle) throws Exception {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            sm.checkPermission(new AdminPermission(bundle, "extensionLifecycle"));
            if (!((BundleProtectionDomain)bundle.getProtectionDomain()).impliesDirect(new AllPermission())) {
                throw new SecurityException("Extension Bundles must have AllPermission");
            }
        }
        String directive = ManifestParser.parseExtensionBundleHeader((String)((BundleRevisionImpl)bundle.adapt(BundleRevision.class)).getHeaders().get("Fragment-Host"));
        Content content = bundle.adapt(BundleRevisionImpl.class).getContent();
        File file = content instanceof JarContent ? ((JarContent)content).getFile() : (content instanceof DirectoryContent ? ((DirectoryContent)content).getFile() : null);
        if (file == null && !(content instanceof ConnectContentContent)) {
            this.m_logger.log(bundle, 2, "Unable to add extension bundle - wrong revision type?");
            throw new UnsupportedOperationException("Unable to add extension bundle.");
        }
        if (!"framework".equals(directive)) {
            throw new BundleException("Unsupported Extension Bundle type: " + directive, new UnsupportedOperationException("Unsupported Extension Bundle type!"));
        }
        if (m_extenderFramework == null && file != null) {
            this.m_logger.log(bundle, 2, "Unable to add extension bundle - Maybe ClassLoader is not supported (on java9, try --add-opens=java.base/jdk.internal.loader=ALL-UNNAMED)?");
            throw new UnsupportedOperationException("Unable to add extension bundle.");
        }
        BundleRevisionImpl bri = bundle.adapt(BundleRevisionImpl.class);
        bri.resolve(null);
        this.m_unresolvedExtensions.addAll(this.m_failedExtensions);
        this.m_failedExtensions.clear();
        this.m_unresolvedExtensions.add(bri);
    }

    public synchronized List<Bundle> resolveExtensionBundles(Felix felix) {
        if (this.m_unresolvedExtensions.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<BundleRevisionImpl> extensions = new ArrayList<BundleRevisionImpl>();
        ArrayList<BundleRevisionImpl> alt = new ArrayList<BundleRevisionImpl>();
        block4: for (BundleRevisionImpl revision : this.m_unresolvedExtensions) {
            for (BundleRevisionImpl bundleRevisionImpl : this.m_resolvedExtensions) {
                if (!bundleRevisionImpl.getSymbolicName().equals(revision.getSymbolicName())) continue;
                continue block4;
            }
            for (BundleRevisionImpl bundleRevisionImpl : this.m_unresolvedExtensions) {
                if (revision == bundleRevisionImpl || !revision.getSymbolicName().equals(bundleRevisionImpl.getSymbolicName()) || revision.getVersion().compareTo(bundleRevisionImpl.getVersion()) >= 0) continue;
                alt.add(revision);
                continue block4;
            }
            extensions.add(revision);
        }
        Map<BundleRevisionImpl, List<BundleWire>> wirings = this.findResolvableExtensions(extensions, alt);
        ArrayList<Bundle> result = new ArrayList<Bundle>();
        for (Map.Entry entry : wirings.entrySet()) {
            BundleRevisionImpl revision = (BundleRevisionImpl)entry.getKey();
            this.m_unresolvedExtensions.remove(revision);
            this.m_resolvedExtensions.add(revision);
            BundleWireImpl wire = new BundleWireImpl(revision, revision.getDeclaredRequirements("osgi.wiring.host").get(0), this.m_systemBundleRevision, this.m_systemBundleRevision.getWiring().getCapabilities("osgi.wiring.host").get(0));
            try {
                revision.resolve(new BundleWiringImpl(this.m_logger, this.m_systemBundleRevision.m_configMap, null, revision, null, Collections.singletonList(wire), Collections.EMPTY_MAP, Collections.EMPTY_MAP));
            }
            catch (Exception ex) {
                this.m_logger.log(revision.getBundle(), 1, "Error resolving extension bundle : " + revision.getBundle(), (Throwable)ex);
            }
            felix.getDependencies().addDependent(wire);
            ArrayList<BundleCapability> caps = new ArrayList<BundleCapability>();
            for (BundleCapability cap : ((BundleRevisionImpl)entry.getKey()).getDeclaredCapabilities(null)) {
                if (IDENTITY.contains(cap.getNamespace())) continue;
                caps.add(cap);
            }
            this.m_systemBundleRevision.appendCapabilities(caps);
            for (BundleWire w : (List)entry.getValue()) {
                if (w.getRequirement().getNamespace().equals("osgi.wiring.host") || w.getRequirement().getNamespace().equals("osgi.wiring.package")) continue;
                ((BundleWiringImpl)w.getRequirer().getWiring()).addDynamicWire(w);
                felix.getDependencies().addDependent(w);
            }
            Content revisionContent = revision.getContent();
            final File f = revisionContent instanceof JarContent ? ((JarContent)revisionContent).getFile() : (revisionContent instanceof DirectoryContent ? ((DirectoryContent)revisionContent).getFile() : null);
            if (f != null) {
                try {
                    AccessController.doPrivileged(new PrivilegedExceptionAction<Void>(){

                        @Override
                        public Void run() throws Exception {
                            m_extenderFramework.add(f);
                            return null;
                        }
                    });
                }
                catch (Exception ex) {
                    this.m_logger.log(revision.getBundle(), 1, "Error adding extension bundle to framework classloader: " + revision.getBundle(), (Throwable)ex);
                }
            }
            felix.setBundleStateAndNotify(revision.getBundle(), 4);
            result.add(revision.getBundle());
        }
        this.m_failedExtensions.addAll(this.m_unresolvedExtensions);
        this.m_unresolvedExtensions.clear();
        return result;
    }

    void startExtensionBundle(Felix felix, BundleImpl bundle) {
        Map<String, Object> headers = bundle.adapt(BundleRevisionImpl.class).getHeaders();
        String activatorClass = (String)headers.get("ExtensionBundle-Activator");
        boolean felixExtension = false;
        if (activatorClass == null) {
            felixExtension = true;
            activatorClass = (String)headers.get("Felix-Activator");
        }
        if (activatorClass != null) {
            ExtensionTuple tuple = null;
            try {
                BundleActivator activator = (BundleActivator)Felix.m_secureAction.getClassLoader(felix.getClass()).loadClass(activatorClass.trim()).newInstance();
                BundleContext context = felix._getBundleContext();
                bundle.setBundleContext(context);
                if (!felixExtension) {
                    tuple = new ExtensionTuple(activator, bundle);
                    this.m_extensionTuples.add(tuple);
                } else {
                    felix.m_activatorList.add(activator);
                }
                if (felix.getState() == 32 || felix.getState() == 8) {
                    if (tuple != null) {
                        tuple.m_started = true;
                    }
                    Felix.m_secureAction.startActivator(activator, context);
                }
            }
            catch (Throwable ex) {
                if (tuple != null) {
                    tuple.m_failed = true;
                }
                felix.fireFrameworkEvent(2, bundle, new BundleException("Unable to start Bundle", ex));
                this.m_logger.log(bundle, 2, "Unable to start Extension Activator", ex);
            }
        }
    }

    void startPendingExtensionBundles(Felix felix) {
        for (int i = 0; i < this.m_extensionTuples.size(); ++i) {
            if (this.m_extensionTuples.get(i).m_started) continue;
            this.m_extensionTuples.get(i).m_started = true;
            try {
                Felix.m_secureAction.startActivator(this.m_extensionTuples.get(i).m_activator, felix._getBundleContext());
                continue;
            }
            catch (Throwable ex) {
                this.m_extensionTuples.get(i).m_failed = true;
                felix.fireFrameworkEvent(2, this.m_extensionTuples.get(i).m_bundle, new BundleException("Unable to start Bundle", 5, ex));
                this.m_logger.log(this.m_extensionTuples.get(i).m_bundle, 2, "Unable to start Extension Activator", ex);
            }
        }
    }

    void stopExtensionBundles(Felix felix) {
        for (int i = this.m_extensionTuples.size() - 1; i >= 0; --i) {
            if (!this.m_extensionTuples.get(i).m_started || this.m_extensionTuples.get(i).m_failed) continue;
            try {
                Felix.m_secureAction.stopActivator(this.m_extensionTuples.get(i).m_activator, felix._getBundleContext());
                continue;
            }
            catch (Throwable ex) {
                felix.fireFrameworkEvent(2, this.m_extensionTuples.get(i).m_bundle, new BundleException("Unable to stop Bundle", 5, ex));
                this.m_logger.log(this.m_extensionTuples.get(i).m_bundle, 2, "Unable to stop Extension Activator", ex);
            }
        }
        this.m_extensionTuples.clear();
    }

    public synchronized void removeExtensionBundles() {
        this.m_resolvedExtensions.clear();
        this.m_unresolvedExtensions.clear();
        this.m_failedExtensions.clear();
    }

    private Map<BundleRevisionImpl, List<BundleWire>> findResolvableExtensions(List<BundleRevisionImpl> extensions, List<BundleRevisionImpl> alt) {
        LinkedHashMap<BundleRevisionImpl, List<BundleWire>> wires = new LinkedHashMap<BundleRevisionImpl, List<BundleWire>>();
        for (BundleRevisionImpl bri : extensions) {
            ArrayList<BundleWireImpl> wi = new ArrayList<BundleWireImpl>();
            boolean resolved = true;
            block1: for (BundleRequirement req : bri.getDeclaredRequirements(null)) {
                for (BundleCapability cap : this.m_systemBundleRevision.getWiring().getCapabilities(req.getNamespace())) {
                    if (!req.matches(cap)) continue;
                    wi.add(new BundleWireImpl(req.getNamespace().equals("osgi.ee") ? bri : this.m_systemBundleRevision, req, this.m_systemBundleRevision, cap));
                    continue block1;
                }
                for (BundleRevisionImpl extension : this.m_resolvedExtensions) {
                    for (BundleCapability cap : extension.getDeclaredCapabilities(req.getNamespace())) {
                        if (!req.matches(cap)) continue;
                        wi.add(new BundleWireImpl(this.m_systemBundleRevision, req, extension, cap));
                        continue block1;
                    }
                }
                for (BundleRevisionImpl extension : extensions) {
                    for (BundleCapability cap : extension.getDeclaredCapabilities(req.getNamespace())) {
                        if (!req.matches(cap)) continue;
                        wi.add(new BundleWireImpl(this.m_systemBundleRevision, req, IDENTITY.contains(cap.getNamespace()) ? extension : this.m_systemBundleRevision, cap));
                        continue block1;
                    }
                    for (BundleCapability cap : extension.getDeclaredCapabilities(req.getNamespace())) {
                        if (!req.matches(cap)) continue;
                        wi.add(new BundleWireImpl(this.m_systemBundleRevision, req, extension, cap));
                        continue block1;
                    }
                }
                if (((BundleRequirementImpl)req).isOptional()) continue;
                resolved = false;
                break;
            }
            if (resolved) {
                wires.put(bri, wi);
                continue;
            }
            ArrayList<BundleRevisionImpl> next = new ArrayList<BundleRevisionImpl>(extensions);
            ArrayList<BundleRevisionImpl> nextAlt = new ArrayList<BundleRevisionImpl>();
            block8: for (BundleRevisionImpl replacement : alt) {
                if (bri.getSymbolicName().equals(replacement.getSymbolicName())) {
                    for (BundleRevisionImpl other : alt) {
                        if (replacement == other || !replacement.getSymbolicName().equals(other.getSymbolicName()) || replacement.getVersion().compareTo(other.getVersion()) >= 0) continue;
                        nextAlt.add(replacement);
                        continue block8;
                    }
                    next.set(next.indexOf(bri), replacement);
                    break;
                }
                nextAlt.add(replacement);
            }
            next.remove(bri);
            return next.isEmpty() ? Collections.EMPTY_MAP : this.findResolvableExtensions(next, nextAlt);
        }
        return wires;
    }

    @Override
    public void close() {
    }

    public Enumeration getEntries() {
        return new Enumeration(){

            @Override
            public boolean hasMoreElements() {
                return false;
            }

            public Object nextElement() throws NoSuchElementException {
                throw new NoSuchElementException();
            }
        };
    }

    @Override
    public boolean hasEntry(String name) {
        return false;
    }

    @Override
    public boolean isDirectory(String name) {
        return false;
    }

    @Override
    public byte[] getEntryAsBytes(String name) {
        return null;
    }

    @Override
    public InputStream getEntryAsStream(String name) throws IOException {
        return null;
    }

    @Override
    public Content getEntryAsContent(String name) {
        return null;
    }

    @Override
    public String getEntryAsNativeLibrary(String name) {
        return null;
    }

    @Override
    public URL getEntryAsURL(String name) {
        return null;
    }

    @Override
    public long getContentTime(String name) {
        return -1L;
    }

    static {
        IDENTITY = new HashSet<String>(Arrays.asList("osgi.wiring.bundle", "osgi.wiring.host", "osgi.identity"));
        ClassPathExtenderFactory.ClassPathExtender extenderFramework = null;
        ClassPathExtenderFactory.ClassPathExtender extenderBoot = null;
        if (!"true".equalsIgnoreCase(Felix.m_secureAction.getSystemProperty("felix.extensions.disable", "false"))) {
            ServiceLoader<ClassPathExtenderFactory> loader = ServiceLoader.load(ClassPathExtenderFactory.class, ExtensionManager.class.getClassLoader());
            Iterator<ClassPathExtenderFactory> iter = loader.iterator();
            while (iter.hasNext() && (extenderFramework == null || extenderBoot == null)) {
                try {
                    ClassPathExtenderFactory factory = iter.next();
                    if (extenderFramework == null) {
                        try {
                            extenderFramework = factory.getExtender(ExtensionManager.class.getClassLoader());
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                    }
                    if (extenderBoot != null) continue;
                    try {
                        extenderBoot = factory.getExtender(null);
                    }
                    catch (Throwable throwable) {
                    }
                }
                catch (Throwable throwable) {}
            }
            try {
                if (extenderFramework == null) {
                    extenderFramework = new ClassPathExtenderFactory.DefaultClassLoaderExtender().getExtender(ExtensionManager.class.getClassLoader());
                }
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        m_extenderFramework = extenderFramework;
        m_extenderBoot = extenderBoot;
    }

    class ExtensionManagerWiring
    extends BundleWiringImpl {
        ExtensionManagerWiring(Logger logger, Map configMap, BundleRevisionImpl revision) throws Exception {
            super(logger, configMap, null, revision, null, Collections.EMPTY_LIST, null, null);
        }

        @Override
        public ClassLoader getClassLoader() {
            return this.getClass().getClassLoader();
        }

        @Override
        public List<BundleCapability> getCapabilities(String namespace) {
            return ExtensionManager.this.m_systemBundleRevision.getDeclaredCapabilities(namespace);
        }

        @Override
        public List<NativeLibrary> getNativeLibraries() {
            return Collections.EMPTY_LIST;
        }

        @Override
        public Class getClassByDelegation(String name) throws ClassNotFoundException {
            return this.getClass().getClassLoader().loadClass(name);
        }

        @Override
        public URL getResourceByDelegation(String name) {
            return this.getClass().getClassLoader().getResource(name);
        }

        @Override
        public Enumeration getResourcesByDelegation(String name) {
            try {
                return this.getClass().getClassLoader().getResources(name);
            }
            catch (IOException ex) {
                return null;
            }
        }

        @Override
        public void dispose() {
        }
    }

    class ExtensionManagerRevision
    extends BundleRevisionImpl {
        private volatile Map m_configMap;
        private final Map m_headerMap;
        private volatile List<BundleCapability> m_capabilities;
        private volatile Version m_version;
        private volatile BundleWiring m_wiring;

        ExtensionManagerRevision(Map configMap, Felix felix) {
            super(felix, "0");
            this.m_headerMap = new StringMap();
            this.m_capabilities = Collections.EMPTY_LIST;
            this.m_configMap = configMap;
            this.m_headerMap.put("Bundle-Version", this.m_configMap.get("felix.version"));
            this.m_headerMap.put("Bundle-SymbolicName", "org.apache.felix.framework");
            this.m_headerMap.put("Bundle-Name", "System Bundle");
            this.m_headerMap.put("Bundle-Description", "This bundle is system specific; it implements various system services.");
            this.m_headerMap.put("Export-Service", "org.osgi.service.packageadmin.PackageAdmin,org.osgi.service.startlevel.StartLevel,org.osgi.service.url.URLHandlers");
            this.m_headerMap.put("Bundle-ManifestVersion", "2");
            this.m_version = new Version((String)this.m_configMap.get("felix.version"));
            try {
                ManifestParser mp = new ManifestParser(ExtensionManager.this.m_logger, this.m_configMap, this, this.m_headerMap);
                List<BundleCapability> caps = ManifestParser.aliasSymbolicName(mp.getCapabilities(), this);
                caps.add(ExtensionManager.this.buildNativeCapabilites(this, this.m_configMap));
                this.appendCapabilities(caps);
                this.m_headerMap.put("Export-Package", this.convertCapabilitiesToHeaders(caps));
            }
            catch (Exception ex) {
                this.m_capabilities = Collections.EMPTY_LIST;
                ExtensionManager.this.m_logger.log(1, "Error parsing system bundle statement", ex);
            }
        }

        private void update(Map configMap) {
            Properties configProps = Util.toProperties(configMap);
            String syspkgs = configProps.getProperty("org.osgi.framework.system.packages");
            syspkgs = syspkgs == null ? "" : syspkgs;
            String pkgextra = configProps.getProperty("org.osgi.framework.system.packages.extra");
            String string = pkgextra == null || pkgextra.trim().length() == 0 ? syspkgs : (syspkgs = syspkgs + (pkgextra.trim().startsWith(",") ? pkgextra : "," + pkgextra));
            if (syspkgs.startsWith(",")) {
                syspkgs = syspkgs.substring(1);
            }
            this.m_headerMap.put("Export-Package", syspkgs);
            String syscaps = configProps.getProperty("org.osgi.framework.system.capabilities");
            syscaps = syscaps == null ? "" : syscaps;
            String capextra = configProps.getProperty("org.osgi.framework.system.capabilities.extra");
            syscaps = capextra == null || capextra.trim().length() == 0 ? syscaps : syscaps + (capextra.trim().startsWith(",") ? capextra : "," + capextra);
            this.m_headerMap.put("Provide-Capability", syscaps);
            try {
                ManifestParser mp = new ManifestParser(ExtensionManager.this.m_logger, this.m_configMap, this, this.m_headerMap);
                List<BundleCapability> caps = ManifestParser.aliasSymbolicName(mp.getCapabilities(), this);
                caps.add(ExtensionManager.this.buildNativeCapabilites(this, this.m_configMap));
                this.m_capabilities = Collections.EMPTY_LIST;
                this.appendCapabilities(caps);
                this.m_headerMap.put("Export-Package", this.convertCapabilitiesToHeaders(caps));
            }
            catch (Exception ex) {
                this.m_capabilities = Collections.EMPTY_LIST;
                ExtensionManager.this.m_logger.log(1, "Error parsing system bundle statement.", ex);
            }
        }

        private void appendCapabilities(List<BundleCapability> caps) {
            ArrayList<BundleCapability> newCaps = new ArrayList<BundleCapability>(this.m_capabilities.size() + caps.size());
            newCaps.addAll(this.m_capabilities);
            newCaps.addAll(caps);
            this.m_capabilities = Util.newImmutableList(newCaps);
        }

        private String convertCapabilitiesToHeaders(List<BundleCapability> caps) {
            StringBuilder exportSB = new StringBuilder();
            for (BundleCapability cap : caps) {
                if (!cap.getNamespace().equals("osgi.wiring.package")) continue;
                if (exportSB.length() > 0) {
                    exportSB.append(", ");
                }
                exportSB.append(cap.getAttributes().get("osgi.wiring.package"));
                for (Map.Entry<String, String> entry : cap.getDirectives().entrySet()) {
                    exportSB.append("; ");
                    exportSB.append(entry.getKey());
                    exportSB.append(":=\"");
                    exportSB.append(entry.getValue());
                    exportSB.append("\"");
                }
                for (Map.Entry<String, Object> entry : cap.getAttributes().entrySet()) {
                    if (entry.getKey().equals("osgi.wiring.package") || entry.getKey().equals("bundle-symbolic-name") || entry.getKey().equals("bundle-version")) continue;
                    exportSB.append("; ");
                    exportSB.append(entry.getKey());
                    exportSB.append("=\"");
                    exportSB.append(entry.getValue());
                    exportSB.append("\"");
                }
            }
            return exportSB.toString();
        }

        public Map getHeaders() {
            return Util.newImmutableMap(this.m_headerMap);
        }

        @Override
        public List<BundleCapability> getDeclaredCapabilities(String namespace) {
            ArrayList<BundleCapability> result;
            List<BundleCapability> caps = this.m_capabilities;
            if (namespace != null) {
                result = new ArrayList();
                for (BundleCapability cap : caps) {
                    if (!cap.getNamespace().equals(namespace)) continue;
                    result.add(cap);
                }
            } else {
                result = new ArrayList<BundleCapability>(this.m_capabilities);
            }
            return result;
        }

        @Override
        public String getSymbolicName() {
            return "org.apache.felix.framework";
        }

        @Override
        public Version getVersion() {
            return this.m_version;
        }

        @Override
        public void close() {
        }

        @Override
        public Content getContent() {
            return ExtensionManager.this;
        }

        @Override
        public URL getEntry(String name) {
            return null;
        }

        @Override
        public boolean hasInputStream(int index, String urlPath) {
            return this.getClass().getClassLoader().getResource(urlPath) != null;
        }

        @Override
        public InputStream getInputStream(int index, String urlPath) {
            return this.getClass().getClassLoader().getResourceAsStream(urlPath);
        }

        @Override
        public URL getLocalURL(int index, String urlPath) {
            return this.getClass().getClassLoader().getResource(urlPath);
        }

        @Override
        public void resolve(BundleWiringImpl wire) {
            try {
                this.m_wiring = new ExtensionManagerWiring(ExtensionManager.this.m_logger, this.m_configMap, this);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        @Override
        public BundleWiring getWiring() {
            return this.m_wiring;
        }
    }

    private static class ExtensionTuple {
        private final BundleActivator m_activator;
        private final Bundle m_bundle;
        private volatile boolean m_failed;
        private volatile boolean m_started;

        public ExtensionTuple(BundleActivator activator, Bundle bundle) {
            this.m_activator = activator;
            this.m_bundle = bundle;
        }
    }
}

