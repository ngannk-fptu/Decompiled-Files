/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.osgi;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.About;
import aQute.bnd.osgi.AnalyzerMessages;
import aQute.bnd.osgi.Annotation;
import aQute.bnd.osgi.AnnotationHeaders;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.ClassDataCollectors;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Contracts;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Macro;
import aQute.bnd.osgi.Packages;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.bnd.osgi.URLResource;
import aQute.bnd.osgi.Verifier;
import aQute.bnd.service.AnalyzerPlugin;
import aQute.bnd.service.classparser.ClassParser;
import aQute.bnd.version.Version;
import aQute.bnd.version.VersionRange;
import aQute.lib.base64.Base64;
import aQute.lib.collections.MultiMap;
import aQute.lib.collections.SortedList;
import aQute.lib.filter.Filter;
import aQute.lib.hex.Hex;
import aQute.lib.io.IO;
import aQute.lib.utf8properties.UTF8Properties;
import aQute.libg.cryptography.Digester;
import aQute.libg.cryptography.MD5;
import aQute.libg.cryptography.SHA1;
import aQute.libg.generics.Create;
import aQute.libg.glob.Glob;
import aQute.libg.reporter.ReporterMessages;
import aQute.libg.tuple.Pair;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Analyzer
extends Processor {
    private static final Logger logger = LoggerFactory.getLogger(Analyzer.class);
    private final SortedSet<Clazz.JAVA> ees = new TreeSet<Clazz.JAVA>();
    static Properties bndInfo;
    private Jar dot;
    private final Packages contained = new Packages();
    private final Packages referred = new Packages();
    private Packages exports;
    private Packages imports;
    private Descriptors.TypeRef activator;
    private final MultiMap<Descriptors.PackageRef, Descriptors.PackageRef> uses = new MultiMap<Descriptors.PackageRef, Descriptors.PackageRef>(Descriptors.PackageRef.class, Descriptors.PackageRef.class, true);
    private final MultiMap<Descriptors.PackageRef, Descriptors.PackageRef> apiUses = new MultiMap<Descriptors.PackageRef, Descriptors.PackageRef>(Descriptors.PackageRef.class, Descriptors.PackageRef.class, true);
    private final Contracts contracts = new Contracts(this);
    private final Packages classpathExports = new Packages();
    private final Descriptors descriptors = new Descriptors();
    private final List<Jar> classpath = Create.list();
    private final Map<Descriptors.TypeRef, Clazz> classspace = Create.map();
    private final Map<Descriptors.TypeRef, Clazz> importedClassesCache = Create.map();
    private boolean analyzed = false;
    private boolean diagnostics = false;
    private boolean inited = false;
    protected final AnalyzerMessages msgs = ReporterMessages.base(this, AnalyzerMessages.class);
    private AnnotationHeaders annotationHeaders;
    private Set<Descriptors.PackageRef> packagesVisited = new HashSet<Descriptors.PackageRef>();
    private Set<Check> checks;
    static Pattern OLD_PACKAGEINFO_SYNTAX_P;
    static Pattern OBJECT_REFERENCE;
    static SimpleDateFormat df;
    boolean firstUse = true;
    static Pattern fuzzyVersion;
    static Pattern fuzzyVersionRange;
    static Pattern fuzzyModifier;
    static Pattern nummeric;
    static final String DEFAULT_PROVIDER_POLICY = "${range;[==,=+)}";
    static final String DEFAULT_CONSUMER_POLICY = "${range;[==,+)}";
    static String _classesHelp;
    static String _packagesHelp;
    static String _md5Help;
    static String _sha1Help;

    public Analyzer(Jar jar) throws Exception {
        this.dot = Objects.requireNonNull(jar);
        Manifest manifest = this.dot.getManifest();
        if (manifest != null) {
            this.copyFrom(Domain.domain(manifest));
        }
    }

    public Analyzer(Processor parent) {
        super(parent);
    }

    public Analyzer() {
    }

    public static Properties getManifest(File dirOrJar) throws Exception {
        try (Analyzer analyzer = new Analyzer();){
            analyzer.setJar(dirOrJar);
            UTF8Properties properties = new UTF8Properties();
            properties.put("Import-Package", "*");
            properties.put("Export-Package", "*");
            analyzer.setProperties(properties);
            Manifest m = analyzer.calcManifest();
            UTF8Properties result = new UTF8Properties();
            for (Attributes.Name name : m.getMainAttributes().keySet()) {
                result.put(name.toString(), m.getMainAttributes().getValue(name));
            }
            UTF8Properties uTF8Properties = result;
            return uTF8Properties;
        }
    }

    public void analyze() throws Exception {
        if (!this.analyzed) {
            this.analyzed = true;
            this.uses.clear();
            this.apiUses.clear();
            this.classspace.clear();
            this.classpathExports.clear();
            this.contracts.clear();
            this.packagesVisited.clear();
            this.analyzeBundleClasspath();
            for (Jar current : this.getClasspath()) {
                this.getManifestInfoFromClasspath(current, this.classpathExports, this.contracts);
                Manifest manifest = current.getManifest();
                if (manifest != null) continue;
                for (String dir : current.getDirectories().keySet()) {
                    this.learnPackage(current, "", this.getPackageRef(dir), this.classpathExports);
                }
            }
            String s = this.getProperty("Bundle-Activator");
            if (s != null && !s.isEmpty()) {
                this.activator = this.getTypeRefFromFQN(s);
                this.referTo(this.activator);
                logger.debug("activator {} {}", (Object)s, (Object)this.activator);
            }
            this.doConditionalPackages();
            this.doPlugins();
            for (Clazz clazz : this.classspace.values()) {
                this.ees.add(clazz.getFormat());
            }
            if (this.since(About._2_3)) {
                Throwable throwable = null;
                try (ClassDataCollectors cds = new ClassDataCollectors(this);){
                    List<ClassParser> parsers = this.getPlugins(ClassParser.class);
                    for (ClassParser cp : parsers) {
                        cds.add(cp.getClassDataCollector(this));
                    }
                    this.annotationHeaders = new AnnotationHeaders(this);
                    cds.add(this.annotationHeaders);
                    for (Clazz c2 : this.classspace.values()) {
                        cds.parse(c2);
                    }
                }
                catch (Throwable x2) {
                    Throwable throwable2 = x2;
                    throw x2;
                }
            }
            this.referred.keySet().removeAll(this.contained.keySet());
            Set<Instruction> unused = Create.set();
            Instructions instructions = new Instructions(this.getExportPackage());
            instructions.append(this.getExportContents());
            this.exports = this.filter(instructions, this.contained, unused);
            if (!unused.isEmpty()) {
                this.warning("Unused Export-Package instructions: %s ", unused).header("Export-Package").context(unused.iterator().next().input);
            }
            this.augmentExports(this.exports);
            Packages referredAndExported = new Packages(this.referred);
            referredAndExported.putAll(this.doExportsToImports(this.exports));
            this.removeDynamicImports(referredAndExported);
            Iterator<Descriptors.PackageRef> iterator = referredAndExported.keySet().iterator();
            while (iterator.hasNext()) {
                if (!iterator.next().isJava()) continue;
                iterator.remove();
            }
            Set<Instruction> set = Create.set();
            String h = this.getProperty("Import-Package");
            if (h == null) {
                h = "*";
            }
            if (this.isPedantic() && h.trim().length() == 0) {
                this.warning("Empty Import-Package header", new Object[0]);
            }
            Instructions filter2 = new Instructions(h);
            this.imports = this.filter(filter2, referredAndExported, set);
            if (!(set.isEmpty() || set.size() == 1 && set.iterator().next().toString().equals("*"))) {
                this.warning("Unused Import-Package instructions: %s ", set).header("Import-Package").context(set.iterator().next().input);
            }
            this.augmentImports(this.imports, this.exports);
            boolean api = true;
            this.doUses(this.exports, api ? this.apiUses : this.uses, this.imports);
            Set<Descriptors.PackageRef> set2 = this.getPrivates();
            Iterator<Descriptors.PackageRef> p = set2.iterator();
            while (p.hasNext()) {
                if (!p.next().isJava()) continue;
                p.remove();
            }
            for (Descriptors.PackageRef exported : this.exports.keySet()) {
                List used = (List)this.uses.get(exported);
                if (used == null) continue;
                TreeSet<Descriptors.PackageRef> privateReferences = new TreeSet<Descriptors.PackageRef>((Collection)this.apiUses.get(exported));
                privateReferences.retainAll(set2);
                if (privateReferences.isEmpty()) continue;
                this.msgs.Export_Has_PrivateReferences_(exported, privateReferences.size(), privateReferences);
            }
            if (this.referred.containsKey(Descriptors.DEFAULT_PACKAGE)) {
                this.error("The default package '.' is not permitted by the Import-Package syntax.%n This can be caused by compile errors in Eclipse because Eclipse creates%nvalid class files regardless of compile errors.%nThe following package(s) import from the default package %s", this.uses.transpose().get(Descriptors.DEFAULT_PACKAGE));
            }
        }
    }

    private void doConditionalPackages() throws Exception {
        this.packagesVisited.clear();
        Jar extra = this.getExtra();
        while (extra != null) {
            this.dot.addAll(extra);
            this.analyzeJar(extra, "", true);
            extra = this.getExtra();
        }
    }

    private void learnPackage(Jar jar, String prefix, Descriptors.PackageRef packageRef, Packages map) throws Exception {
        Attrs info;
        Attrs info2;
        Resource resource;
        if (packageRef.isMetaData() || packageRef.isJava() || packageRef.isPrimitivePackage()) {
            return;
        }
        Map<String, Resource> dir = jar.getDirectories().get(prefix + packageRef.getBinary());
        if (dir == null || dir.size() == 0) {
            return;
        }
        if (this.packagesVisited.contains(packageRef)) {
            return;
        }
        this.packagesVisited.add(packageRef);
        Attrs attrs = map.get(packageRef);
        if (attrs != null && attrs.size() > 1) {
            return;
        }
        if ((map != this.classpathExports || this.since(About._2_3)) && (resource = jar.getResource(prefix + packageRef.getBinary() + "/package-info.class")) != null && (info2 = this.parsePackageInfoClass(resource)) != null && info2.containsKey("version")) {
            info2.put("from:", resource.toString());
            info2.put("-internal-source:", this.getName(jar));
            map.put(packageRef, info2);
            return;
        }
        String path = prefix + packageRef.getBinary() + "/packageinfo";
        Resource resource2 = jar.getResource(path);
        if (resource2 != null && (info = this.parsePackageinfo(packageRef, resource2)) != null) {
            info.put("from:", resource2.toString());
            info.put("-internal-source:", this.getName(jar));
            this.fixupOldStyleVersions(info);
            map.put(packageRef, info);
            return;
        }
        map.put(packageRef).put("-internal-source:", this.getName(jar));
    }

    protected String getName(Jar jar) throws Exception {
        String version;
        String name = jar.getBsn();
        if (name == null && (name = jar.getName()).equals("dot") && jar.getSource() != null) {
            name = jar.getSource().getName();
        }
        if ((version = jar.getVersion()) == null) {
            version = "0.0.0";
        }
        return name + "-" + version;
    }

    Attrs parsePackageinfo(Descriptors.PackageRef packageRef, Resource r) throws Exception {
        UTF8Properties p = new UTF8Properties();
        try {
            try (InputStream in = r.openInputStream();){
                ((Properties)p).load(in);
            }
            Attrs attrs = new Attrs();
            Enumeration<?> t = p.propertyNames();
            while (t.hasMoreElements()) {
                String value;
                Matcher m;
                String key = (String)t.nextElement();
                String propvalue = p.getProperty(key);
                if (key.equalsIgnoreCase("include") && (m = OLD_PACKAGEINFO_SYNTAX_P.matcher(propvalue)).matches()) {
                    key = "version";
                    propvalue = m.group(2);
                    if (this.isPedantic()) {
                        this.warning("found old syntax in package info in package %s, from resource %s", packageRef, r);
                    }
                }
                if ((value = propvalue).startsWith(":")) {
                    key = key + ":";
                    value = value.substring(1);
                }
                attrs.put(key, value);
            }
            return attrs;
        }
        catch (Exception e) {
            this.msgs.NoSuchFile_(r);
            return null;
        }
    }

    private Attrs parsePackageInfoClass(Resource r) throws Exception {
        final Attrs info = new Attrs();
        final Clazz clazz = new Clazz(this, "", r);
        clazz.parseClassFileWithCollector(new ClassDataCollector(){

            @Override
            public void annotation(Annotation a) {
                String name;
                switch (name = a.getName().getFQN()) {
                    case "aQute.bnd.annotation.Version": {
                        Analyzer.this.warning("%s annotation used in class %s. Bnd versioning annotations are deprecated as of Bnd 3.2 and support will be removed in Bnd 4.0. Please change to use OSGi versioning annotations.", name, clazz);
                    }
                    case "org.osgi.annotation.versioning.Version": {
                        String version = (String)a.get("value");
                        if (!info.containsKey("version")) {
                            if (version == null) break;
                            version = Analyzer.this.getReplacer().process(version);
                            if (Verifier.VERSION.matcher(version).matches()) {
                                info.put("version", version);
                                break;
                            }
                            Analyzer.this.error("Version annotation in %s has invalid version info: %s", clazz, version);
                            break;
                        }
                        String presentVersion = info.get("version");
                        try {
                            Version av = new Version(presentVersion).getWithoutQualifier();
                            Version bv = new Version(version).getWithoutQualifier();
                            if (av.equals(bv)) break;
                            Analyzer.this.error("Version from annotation for %s differs with packageinfo or Manifest", clazz.getClassName().getFQN());
                        }
                        catch (Exception e) {}
                        break;
                    }
                    case "aQute.bnd.annotation.Export": {
                        StringBuilder sb;
                        Object[] uses;
                        Object[] excluded;
                        Object[] included;
                        Attrs attrs = Processor.doAttrbutes((Object[])a.get("mandatory"), clazz, Analyzer.this.getReplacer());
                        if (!attrs.isEmpty()) {
                            info.putAll(attrs);
                            info.put("mandatory:", Processor.join(attrs.keySet()));
                        }
                        if (!(attrs = Processor.doAttrbutes((Object[])a.get("optional"), clazz, Analyzer.this.getReplacer())).isEmpty()) {
                            info.putAll(attrs);
                        }
                        if ((included = (Object[])a.get("include")) != null && included.length > 0) {
                            StringBuilder sb2 = new StringBuilder();
                            String del = "";
                            for (Object i : included) {
                                Matcher m = OBJECT_REFERENCE.matcher(((Descriptors.TypeRef)i).getFQN());
                                if (!m.matches()) continue;
                                sb2.append(del);
                                sb2.append(m.group(2));
                                del = ",";
                            }
                            info.put("include:", sb2.toString());
                        }
                        if ((excluded = (Object[])a.get("exclude")) != null && excluded.length > 0) {
                            StringBuilder sb3 = new StringBuilder();
                            String del = "";
                            for (Object i : excluded) {
                                Matcher m = OBJECT_REFERENCE.matcher(((Descriptors.TypeRef)i).getFQN());
                                if (!m.matches()) continue;
                                sb3.append(del);
                                sb3.append(m.group(2));
                                del = ",";
                            }
                            info.put("exclude:", sb3.toString());
                        }
                        if ((uses = (Object[])a.get("uses")) == null || uses.length <= 0) break;
                        String old = info.get("uses:");
                        if (old == null) {
                            old = "";
                        }
                        String del = (sb = new StringBuilder(old)).length() == 0 ? "" : ",";
                        for (Object use : uses) {
                            sb.append(del);
                            sb.append(use);
                            del = ",";
                        }
                        info.put("uses:", sb.toString());
                    }
                }
            }
        });
        return info;
    }

    void removeDynamicImports(Packages referredAndExported) {
    }

    protected Jar getExtra() throws Exception {
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void doPlugins() {
        for (AnalyzerPlugin plugin : this.getPlugins(AnalyzerPlugin.class)) {
            try {
                boolean reanalyze;
                Processor previous = this.beginHandleErrors(plugin.toString());
                try {
                    reanalyze = plugin.analyzeJar(this);
                }
                finally {
                    this.endHandleErrors(previous);
                }
                if (!reanalyze) continue;
                this.classspace.clear();
                this.analyzeBundleClasspath();
            }
            catch (Exception e) {
                this.exception(e, "Analyzer Plugin %s failed %s", plugin, e);
            }
        }
    }

    boolean isResourceOnly() {
        return Analyzer.isTrue(this.getProperty("-resourceonly"));
    }

    public Manifest calcManifest() throws Exception {
        try {
            String exportHeader;
            this.analyze();
            Manifest manifest = new Manifest();
            Attributes main = manifest.getMainAttributes();
            main.put(Attributes.Name.MANIFEST_VERSION, "1.0");
            main.putValue("Bundle-ManifestVersion", "2");
            boolean noExtraHeaders = "true".equalsIgnoreCase(this.getProperty("-noextraheaders"));
            if (!noExtraHeaders) {
                main.putValue("Created-By", System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")");
                main.putValue("Tool", "Bnd-" + this.getBndVersion());
                main.putValue("Bnd-LastModified", "" + System.currentTimeMillis());
            }
            if ((exportHeader = Analyzer.printClauses(this.exports, true)).length() > 0) {
                main.putValue("Export-Package", exportHeader);
            } else {
                main.remove("Export-Package");
            }
            Pair<Packages, Parameters> regularAndDynamicImports = this.divideRegularAndDynamicImports();
            Packages regularImports = regularAndDynamicImports.getFirst();
            if (!regularImports.isEmpty()) {
                main.putValue("Import-Package", Analyzer.printClauses(regularImports));
            } else {
                main.remove("Import-Package");
            }
            Parameters dynamicImports = regularAndDynamicImports.getSecond();
            if (!dynamicImports.isEmpty()) {
                main.putValue("DynamicImport-Package", Analyzer.printClauses(dynamicImports));
            } else {
                main.remove("DynamicImport-Package");
            }
            Packages temp = new Packages(this.contained);
            temp.keySet().removeAll(this.exports.keySet());
            Iterator<Descriptors.PackageRef> i = temp.keySet().iterator();
            while (i.hasNext()) {
                String binary = i.next().getBinary();
                Resource r = this.dot.getResource(binary);
                if (r == null) continue;
                i.remove();
            }
            if (!temp.isEmpty()) {
                main.putValue("Private-Package", Analyzer.printClauses(temp));
            } else {
                main.remove("Private-Package");
            }
            Parameters bcp = this.getBundleClasspath();
            if (bcp.isEmpty() || bcp.containsKey(".") && bcp.size() == 1) {
                main.remove("Bundle-ClassPath");
            } else {
                main.putValue("Bundle-ClassPath", Analyzer.printClauses(bcp));
            }
            Parameters requirements = new Parameters(this.annotationHeaders.getHeader("Require-Capability"), this);
            Parameters capabilities = new Parameters(this.annotationHeaders.getHeader("Provide-Capability"), this);
            this.contracts.addToRequirements(requirements);
            if (!Analyzer.isTrue(this.getProperty("-noee")) && !this.ees.isEmpty() && this.since(About._2_3) && !requirements.containsKey("osgi.ee")) {
                Clazz.JAVA highest = this.ees.last();
                Attrs attrs = new Attrs();
                String filter = this.doEEProfiles(highest);
                attrs.put("filter:", filter);
                requirements.add("osgi.ee", attrs);
            }
            if (!requirements.isEmpty()) {
                main.putValue("Require-Capability", requirements.toString());
            }
            if (!capabilities.isEmpty()) {
                main.putValue("Provide-Capability", capabilities.toString());
            }
            this.doNamesection(this.dot, manifest);
            Enumeration<?> h = this.getProperties().propertyNames();
            while (h.hasMoreElements()) {
                String header = (String)h.nextElement();
                if (header.trim().length() == 0) {
                    this.warning("Empty property set with value: %s", this.getProperties().getProperty(header));
                    continue;
                }
                if (this.isMissingPlugin(header.trim())) {
                    this.error("Missing plugin for command %s", header);
                }
                if (!Character.isUpperCase(header.charAt(0))) {
                    if (header.charAt(0) != '@') continue;
                    this.doNameSection(manifest, header);
                    continue;
                }
                if (header.equals("Bundle-ClassPath") || header.equals("Export-Package") || header.equals("Import-Package") || header.equals("DynamicImport-Package") || header.equals("Require-Capability") || header.equals("Provide-Capability")) continue;
                if (header.equalsIgnoreCase("Name")) {
                    this.error("Your bnd file contains a header called 'Name'. This interferes with the manifest name section.", new Object[0]);
                    continue;
                }
                if (!Verifier.HEADER_PATTERN.matcher(header).matches()) continue;
                this.doHeader(main, header);
            }
            this.doHeader(main, "Bundle-License");
            this.doHeader(main, "Bundle-Developers");
            this.doHeader(main, "Bundle-Contributors");
            this.doHeader(main, "Bundle-Copyright");
            this.doHeader(main, "Bundle-DocURL");
            this.doHeader(main, "Bundle-License");
            this.doHeader(main, "Bundle-Category");
            this.merge(manifest, this.dot.getManifest());
            String bsn = this.getBsn();
            if (main.getValue("Bundle-SymbolicName") == null) {
                main.putValue("Bundle-SymbolicName", bsn);
            }
            if (main.getValue("Bundle-Name") == null) {
                main.putValue("Bundle-Name", bsn);
            }
            if (main.getValue("Bundle-Version") == null) {
                main.putValue("Bundle-Version", "0");
            }
            Instructions instructions = new Instructions(this.mergeProperties("-removeheaders"));
            Collection<Object> result = instructions.select(main.keySet(), false);
            main.keySet().removeAll(result);
            return manifest;
        }
        catch (Exception e) {
            throw new IllegalStateException("Calc manifest failed, state=\n" + this.getFlattenedProperties(), e);
        }
    }

    private String doEEProfiles(Clazz.JAVA highest) throws IOException {
        Map<String, Set<String>> profiles;
        String ee = this.getProperty("-eeprofile");
        if (ee == null) {
            return highest.getFilter();
        }
        if ((ee = ee.trim()).equals("auto")) {
            profiles = highest.getProfiles();
            if (profiles == null) {
                return highest.getFilter();
            }
        } else {
            Attrs t = OSGiHeader.parseProperties(ee);
            profiles = new HashMap<String, Set<String>>();
            for (Map.Entry entry : t.entrySet()) {
                String profile = (String)entry.getKey();
                String l = (String)entry.getValue();
                SortedList sl = new SortedList((Comparable<? super T>[])l.split("\\s*,\\s*"));
                profiles.put(profile, sl);
            }
        }
        TreeSet<String> found = new TreeSet<String>();
        block1: for (Descriptors.PackageRef packageRef : this.referred.keySet()) {
            if (!packageRef.isJava()) continue;
            String fqn = packageRef.getFQN();
            for (Map.Entry<String, Set<String>> entry : profiles.entrySet()) {
                if (!entry.getValue().contains(fqn)) continue;
                found.add(entry.getKey());
                if (found.size() != profiles.size()) continue block1;
                break block1;
            }
            return highest.getFilter();
        }
        String filter = highest.getFilter();
        if (!found.isEmpty()) {
            filter = filter.replaceAll("JavaSE", "JavaSE/" + (String)found.last());
        }
        return filter;
    }

    private void doHeader(Attributes main, String header) {
        String value = this.annotationHeaders.getHeader(header);
        if (value != null && main.getValue(header) == null) {
            if (value.trim().length() == 0) {
                main.remove(header);
            } else if (value.trim().equals("<<EMPTY>>")) {
                main.putValue(header, "");
            } else {
                main.putValue(header, value);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doNamesection(Jar dot, Manifest manifest) {
        Parameters namesection = this.parseHeader(this.getProperties().getProperty("-namesection"));
        Instructions instructions = new Instructions(namesection);
        HashSet<String> resources = new HashSet<String>(dot.getResources().keySet());
        for (Map.Entry<Instruction, Attrs> instr : instructions.entrySet()) {
            boolean matched = false;
            Iterator i = resources.iterator();
            while (i.hasNext()) {
                String path = (String)i.next();
                if (!instr.getKey().matches(path)) continue;
                matched = true;
                if (!instr.getKey().isNegated()) {
                    Attributes attrs = manifest.getAttributes(path);
                    if (attrs == null) {
                        attrs = new Attributes();
                        manifest.getEntries().put(path, attrs);
                    }
                    for (Map.Entry<String, String> property : instr.getValue().entrySet()) {
                        this.setProperty("@", path);
                        try {
                            String processed = this.getReplacer().process(property.getValue());
                            attrs.putValue(property.getKey(), processed);
                        }
                        finally {
                            this.unsetProperty("@");
                        }
                    }
                }
                i.remove();
            }
            if (matched || resources.size() <= 0) continue;
            this.warning("The instruction %s in %s did not match any resources", instr.getKey(), "-namesection");
        }
    }

    void doNameSection(Manifest manifest, String header) {
        String path = header.replace('@', '/');
        int n = path.lastIndexOf(47);
        String name = path.substring(n + 1);
        path = path.substring(1, n == 0 ? 1 : n);
        if (name.length() != 0 && path.length() != 0) {
            Attributes attrs = manifest.getAttributes(path);
            if (attrs == null) {
                attrs = new Attributes();
                manifest.getEntries().put(path, attrs);
            }
            attrs.putValue(name, this.getProperty(header));
        } else {
            this.warning("Invalid header (starts with @ but does not seem to be for the Name section): %s", header);
        }
    }

    public String getBsn() {
        String value = this.getProperty("Bundle-SymbolicName");
        if (value == null) {
            if (this.getPropertiesFile() != null) {
                value = this.getPropertiesFile().getName();
            }
            String projectName = this.getBase().getName();
            if (value == null || value.equals("bnd.bnd")) {
                value = projectName;
            } else if (value.endsWith(".bnd")) {
                value = value.substring(0, value.length() - 4);
                value = projectName + "." + value;
            }
        }
        if (value == null) {
            return "untitled";
        }
        int n = value.indexOf(59);
        if (n > 0) {
            value = value.substring(0, n);
        }
        return value.trim();
    }

    public String _bsn(String[] args) {
        return this.getBsn();
    }

    public String calculateExportsFromContents(Jar bundle) {
        String ddel = "";
        StringBuilder sb = new StringBuilder();
        Map<String, Map<String, Resource>> map = bundle.getDirectories();
        for (String directory : map.keySet()) {
            Map<String, Resource> resources;
            if (directory.equals("META-INF") || directory.startsWith("META-INF/") || directory.equals("OSGI-OPT") || directory.startsWith("OSGI-OPT/") || directory.equals("/") || (resources = map.get(directory)) == null || resources.isEmpty()) continue;
            if (directory.endsWith("/")) {
                directory = directory.substring(0, directory.length() - 1);
            }
            directory = directory.replace('/', '.');
            sb.append(ddel);
            sb.append(directory);
            ddel = ",";
        }
        return sb.toString();
    }

    public Packages getContained() {
        return this.contained;
    }

    public Packages getExports() {
        return this.exports;
    }

    public Packages getImports() {
        return this.imports;
    }

    public Set<Descriptors.PackageRef> getPrivates() {
        HashSet<Descriptors.PackageRef> privates = new HashSet<Descriptors.PackageRef>(this.contained.keySet());
        privates.removeAll(this.exports.keySet());
        privates.removeAll(this.imports.keySet());
        return privates;
    }

    public Jar getJar() {
        return this.dot;
    }

    public Packages getReferred() {
        return this.referred;
    }

    public Set<Descriptors.PackageRef> getUnreachable() {
        HashSet<Descriptors.PackageRef> unreachable = new HashSet<Descriptors.PackageRef>(this.uses.keySet());
        for (Descriptors.PackageRef packageRef : this.exports.keySet()) {
            this.removeTransitive(packageRef, unreachable);
        }
        if (this.activator != null) {
            this.removeTransitive(this.activator.getPackageRef(), unreachable);
        }
        return unreachable;
    }

    public Map<Descriptors.PackageRef, List<Descriptors.PackageRef>> getUses() {
        return this.uses;
    }

    public Map<Descriptors.PackageRef, List<Descriptors.PackageRef>> getAPIUses() {
        return this.apiUses;
    }

    public Packages getClasspathExports() {
        return this.classpathExports;
    }

    public String getBndVersion() {
        return this.getBndInfo("version", "<unknown>");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public long getBndLastModified() {
        String time = this.getBndInfo("lastmodified", "0");
        if (time.matches("\\d+")) {
            return Long.parseLong(time);
        }
        try {
            SimpleDateFormat simpleDateFormat = df;
            synchronized (simpleDateFormat) {
                Date parse = df.parse(time);
                if (parse != null) {
                    return parse.getTime();
                }
            }
        }
        catch (ParseException parseException) {
            // empty catch block
        }
        return 0L;
    }

    public String getBndInfo(String key, String defaultValue) {
        String value;
        if (bndInfo == null) {
            try {
                String v;
                UTF8Properties bndInfoLocal = new UTF8Properties();
                URL url = Analyzer.class.getResource("bnd.info");
                if (url != null) {
                    try (InputStream in = url.openStream();){
                        ((Properties)bndInfoLocal).load(in);
                    }
                }
                if (!Version.isVersion(v = bndInfoLocal.getProperty("version"))) {
                    bndInfoLocal.put("version", About.CURRENT.toString());
                }
                bndInfo = bndInfoLocal;
            }
            catch (Exception e) {
                e.printStackTrace();
                return defaultValue;
            }
        }
        if ((value = bndInfo.getProperty(key)) == null) {
            return defaultValue;
        }
        return value;
    }

    public void mergeManifest(Manifest manifest) throws IOException {
        if (manifest != null) {
            Attributes attributes = manifest.getMainAttributes();
            for (Attributes.Name name : attributes.keySet()) {
                String key = name.toString();
                if (key.startsWith("-") || this.getProperty(key) != null) continue;
                this.setProperty(key, attributes.getValue(name));
            }
        }
    }

    @Override
    public void setBase(File file) {
        super.setBase(file);
        this.getProperties().put("project.dir", this.getBase().getAbsolutePath());
    }

    public void setClasspath(Collection<?> classpath) throws IOException {
        for (Object cpe : classpath) {
            if (cpe instanceof Jar) {
                this.addClasspath((Jar)cpe);
                continue;
            }
            if (cpe instanceof File) {
                File f = (File)cpe;
                if (!f.exists()) {
                    this.error("Missing file on classpath: %s", f.getAbsolutePath().replace(File.separatorChar, '/'));
                    continue;
                }
                this.addClasspath(f);
                continue;
            }
            if (cpe instanceof String) {
                Jar j = this.getJarFromName((String)cpe, " setting classpath");
                if (j == null) continue;
                this.addClasspath(j);
                continue;
            }
            this.error("Cannot convert to JAR to add to classpath %s. Not a File, Jar, or String", cpe);
        }
    }

    public void setClasspath(File[] classpath) throws IOException {
        ArrayList<Jar> list = new ArrayList<Jar>();
        for (int i = 0; i < classpath.length; ++i) {
            if (classpath[i].exists()) {
                Jar current = new Jar(classpath[i]);
                list.add(current);
                continue;
            }
            this.error("Missing file on classpath: %s", classpath[i].getAbsolutePath().replace(File.separatorChar, '/'));
        }
        Iterator i = list.iterator();
        while (i.hasNext()) {
            this.addClasspath((Jar)i.next());
        }
    }

    public void setClasspath(Jar[] classpath) {
        for (int i = 0; i < classpath.length; ++i) {
            this.addClasspath(classpath[i]);
        }
    }

    public void setClasspath(String[] classpath) {
        for (int i = 0; i < classpath.length; ++i) {
            Jar jar = this.getJarFromName(classpath[i], " setting classpath");
            if (jar == null) continue;
            this.addClasspath(jar);
        }
    }

    public Jar setJar(File file) throws IOException {
        Jar jar = new Jar(file);
        this.setJar(jar);
        return jar;
    }

    public Jar setJar(Jar jar) {
        if (this.dot != null) {
            this.removeClose(this.dot);
        }
        this.dot = jar;
        if (this.dot != null) {
            this.addClose(this.dot);
        }
        return jar;
    }

    @Override
    protected void begin() {
        if (!this.inited) {
            this.inited = true;
            super.begin();
            this.updateModified(this.getBndLastModified(), "bnd last modified");
            this.verifyManifestHeadersCase(this.getProperties());
        }
    }

    @Override
    public Jar getJarFromName(String name, String from) {
        Jar j = super.getJarFromName(name, from);
        Glob g = new Glob(name);
        if (j == null) {
            for (Jar entry : this.getClasspath()) {
                if (entry.getSource() == null || !g.matcher(entry.getSource().getName()).matches()) continue;
                return entry;
            }
        }
        return j;
    }

    public List<Jar> getJarsFromName(String name, String from) {
        Jar j = super.getJarFromName(name, from);
        if (j != null) {
            return Collections.singletonList(j);
        }
        Glob g = new Glob(name);
        ArrayList<Jar> result = new ArrayList<Jar>();
        for (Jar entry : this.getClasspath()) {
            if (entry.getSource() == null || !g.matcher(entry.getSource().getName()).matches()) continue;
            result.add(entry);
        }
        return result;
    }

    private void merge(Manifest result, Manifest old) {
        if (old != null) {
            for (Map.Entry<Object, Object> entry : old.getMainAttributes().entrySet()) {
                Attributes.Name name = (Attributes.Name)entry.getKey();
                String value = (String)entry.getValue();
                if (name.toString().equalsIgnoreCase("Created-By")) {
                    name = new Attributes.Name("Originally-Created-By");
                }
                if (result.getMainAttributes().containsKey(name)) continue;
                result.getMainAttributes().put(name, value);
            }
            Map<String, Attributes> oldEntries = old.getEntries();
            Map<String, Attributes> newEntries = result.getEntries();
            for (Map.Entry<String, Attributes> entry : oldEntries.entrySet()) {
                if (newEntries.containsKey(entry.getKey())) continue;
                newEntries.put(entry.getKey(), entry.getValue());
            }
        }
    }

    void verifyManifestHeadersCase(Properties properties) {
        block0: for (String string : properties.keySet()) {
            for (int j = 0; j < headers.length; ++j) {
                if (headers[j].equals(string) || !headers[j].equalsIgnoreCase(string)) continue;
                this.warning("Using a standard OSGi header with the wrong case (bnd is case sensitive!), using: %s and expecting: %s", string, headers[j]);
                continue block0;
            }
        }
    }

    Packages doExportsToImports(Packages exports) {
        HashSet<Descriptors.PackageRef> privatePackages = new HashSet<Descriptors.PackageRef>(this.contained.keySet());
        privatePackages.removeAll(exports.keySet());
        Set containedReferences = this.newSet();
        for (Descriptors.PackageRef p : this.contained.keySet()) {
            Collection uses = (Collection)this.uses.get(p);
            if (uses == null) continue;
            containedReferences.addAll(uses);
        }
        HashSet<Descriptors.PackageRef> toBeImported = new HashSet<Descriptors.PackageRef>(exports.keySet());
        toBeImported.retainAll(containedReferences);
        Iterator i = toBeImported.iterator();
        block1: while (i.hasNext()) {
            Descriptors.PackageRef next = (Descriptors.PackageRef)i.next();
            Collection usedByExportedPackage = (Collection)this.uses.get(next);
            if (usedByExportedPackage == null || usedByExportedPackage.isEmpty()) continue;
            for (Descriptors.PackageRef privatePackage : privatePackages) {
                if (!usedByExportedPackage.contains(privatePackage)) continue;
                i.remove();
                continue block1;
            }
        }
        Packages result = new Packages();
        for (Descriptors.PackageRef ep : toBeImported) {
            String noimport;
            Attrs parameters = exports.get(ep);
            String string = noimport = parameters == null ? null : parameters.get("-noimport:");
            if (noimport != null && noimport.equalsIgnoreCase("true")) continue;
            parameters = new Attrs();
            parameters.remove("version");
            result.put(ep, parameters);
        }
        return result;
    }

    public boolean referred(Descriptors.PackageRef packageName) {
        for (Map.Entry contained : this.uses.entrySet()) {
            if (((Descriptors.PackageRef)contained.getKey()).equals(packageName) || !((List)contained.getValue()).contains(packageName)) continue;
            return true;
        }
        return false;
    }

    private void getManifestInfoFromClasspath(Jar jar, Packages classpathExports, Contracts contracts) {
        try {
            Manifest m = jar.getManifest();
            if (m != null) {
                Domain domain = Domain.domain(m);
                Parameters exported = domain.getExportPackage();
                for (Map.Entry<String, Attrs> e : exported.entrySet()) {
                    Descriptors.PackageRef ref = this.getPackageRef(e.getKey());
                    if (classpathExports.containsKey(ref)) continue;
                    e.getValue().put("-internal-exported:", jar.getBsn() + "-" + jar.getVersion());
                    Attrs attrs = e.getValue();
                    this.fixupOldStyleVersions(attrs);
                    classpathExports.put(ref, attrs);
                }
                Parameters pcs = domain.getProvideCapability();
                contracts.collectContracts(jar.getName(), pcs);
            }
        }
        catch (Exception e) {
            this.warning("Erroneous Manifest for %s %s", jar, e);
        }
    }

    private void fixupOldStyleVersions(Attrs attrs) {
        if (attrs.containsKey("specification-version") && attrs.getVersion() == null) {
            attrs.put("version", attrs.get("specification-version"));
            attrs.remove("specification-version");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void augmentImports(Packages imports, Packages exports) throws Exception {
        List noimports = Create.list();
        Set<Descriptors.PackageRef> provided = this.findProvidedPackages();
        for (Descriptors.PackageRef packageRef : imports.keySet()) {
            String packageName = packageRef.getFQN();
            this.setProperty("@package", packageName);
            try {
                String mandatory;
                Attrs defaultAttrs = new Attrs();
                Attrs importAttributes = imports.get(packageRef);
                Attrs exportAttributes = exports.get(packageRef, this.classpathExports.get(packageRef, defaultAttrs));
                String exportVersion = exportAttributes.getVersion();
                String importRange = importAttributes.getVersion();
                if (this.check(Check.IMPORTS)) {
                    if (exportAttributes == defaultAttrs) {
                        this.warning("Import package %s not found in any bundle on the -buildpath. List explicitly in Import-Package: p,* to get rid of this warning if false", packageRef);
                        continue;
                    }
                    if (!exportAttributes.containsKey("-internal-exported:") && !exports.containsKey(packageRef)) {
                        this.warning("'%s' is a private package import from %s", packageRef, exportAttributes.get("-internal-source:"));
                        continue;
                    }
                }
                if (this.contracts.isContracted(packageRef)) {
                    importAttributes.remove("version");
                    continue;
                }
                if (exportVersion != null) {
                    boolean provider = Analyzer.isTrue(importAttributes.get("provide:")) || Analyzer.isTrue(exportAttributes.get("provide:")) || provided.contains(packageRef);
                    importRange = this.applyVersionPolicy(exportVersion = Analyzer.cleanupVersion(exportVersion), importRange, provider);
                    if (!importRange.trim().isEmpty()) {
                        importAttributes.put("version", importRange);
                    }
                }
                if ((mandatory = exportAttributes.get("mandatory:")) != null) {
                    String[] attrs = mandatory.split("\\s*,\\s*");
                    for (int i = 0; i < attrs.length; ++i) {
                        if (importAttributes.containsKey(attrs[i])) continue;
                        importAttributes.put(attrs[i], exportAttributes.get(attrs[i]));
                    }
                }
                if (exportAttributes.containsKey("-import:")) {
                    importAttributes.put("-import:", exportAttributes.get("-import:"));
                }
                this.fixupAttributes(packageRef, importAttributes);
                this.removeAttributes(importAttributes);
                String result = importAttributes.get("version");
                if (result != null && Verifier.isVersionRange(result)) continue;
                noimports.add(packageRef);
            }
            finally {
                this.unsetProperty("@package");
            }
        }
        if (this.isPedantic() && noimports.size() != 0) {
            this.warning("Imports that lack version ranges: %s", noimports);
        }
    }

    Pair<Packages, Parameters> divideRegularAndDynamicImports() {
        Packages regularImports = new Packages(this.imports);
        Parameters dynamicImports = this.getDynamicImportPackage();
        Iterator<Map.Entry<Descriptors.PackageRef, Attrs>> regularImportsIterator = regularImports.entrySet().iterator();
        while (regularImportsIterator.hasNext()) {
            Map.Entry<Descriptors.PackageRef, Attrs> packageEntry = regularImportsIterator.next();
            Descriptors.PackageRef packageRef = packageEntry.getKey();
            Attrs attrs = packageEntry.getValue();
            String resolution = attrs.get("resolution:");
            if (!"dynamic".equals(resolution)) continue;
            attrs.remove("resolution:");
            dynamicImports.put(packageRef.fqn, attrs);
            regularImportsIterator.remove();
        }
        return new Pair<Packages, Parameters>(regularImports, dynamicImports);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    String applyVersionPolicy(String exportVersion, String importRange, boolean provider) {
        try {
            this.setProperty("@", exportVersion);
            if (importRange != null) {
                importRange = Analyzer.cleanupVersion(importRange);
                importRange = this.getReplacer().process(importRange);
            } else {
                importRange = this.getVersionPolicy(provider);
            }
        }
        finally {
            this.unsetProperty("@");
        }
        return importRange;
    }

    Set<Descriptors.PackageRef> findProvidedPackages() throws Exception {
        Set<Descriptors.PackageRef> providers = Create.set();
        Set cached = Create.set();
        for (Clazz c : this.classspace.values()) {
            Descriptors.TypeRef[] interfaces = c.getInterfaces();
            if (interfaces == null) continue;
            for (Descriptors.TypeRef t : interfaces) {
                if (!cached.contains(t) && !this.isProvider(t)) continue;
                cached.add(t);
                providers.add(t.getPackageRef());
            }
        }
        return providers;
    }

    private boolean isProvider(Descriptors.TypeRef t) throws Exception {
        Clazz c = this.findClass(t);
        if (c == null) {
            return false;
        }
        if (c.annotations == null) {
            return false;
        }
        Descriptors.TypeRef r6pt = this.getTypeRefFromFQN("org.osgi.annotation.versioning.ProviderType");
        if (c.annotations.contains(r6pt)) {
            return true;
        }
        Descriptors.TypeRef pt = this.getTypeRefFromFQN("aQute.bnd.annotation.ProviderType");
        if (c.annotations.contains(pt)) {
            this.warning("%s annotation used in class %s. Bnd versioning annotations are deprecated as of Bnd 3.2 and support will be removed in Bnd 4.0. Please change to use OSGi versioning annotations.", "aQute.bnd.annotation.ProviderType", c);
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void augmentExports(Packages exports) throws IOException {
        for (Descriptors.PackageRef packageRef : exports.keySet()) {
            String packageName = packageRef.getFQN();
            this.setProperty("@package", packageName);
            Attrs attributes = exports.get(packageRef);
            try {
                Attrs exporterAttributes = this.classpathExports.get(packageRef);
                if (exporterAttributes == null) {
                    Map<String, Resource> map;
                    if (this.check(Check.EXPORTS) && ((map = this.dot.getDirectories().get(packageRef.getBinary())) == null || map.isEmpty())) {
                        this.error("Exporting an empty package '%s'", packageRef.getFQN());
                    }
                } else {
                    for (Map.Entry<String, String> entry : exporterAttributes.entrySet()) {
                        String key = entry.getKey();
                        if (key.endsWith(":")) continue;
                        if (!attributes.containsKey(key)) {
                            attributes.put(key, entry.getValue());
                            continue;
                        }
                        if (!this.since(About._2_4) || !key.equals("version")) continue;
                        try {
                            Version fromSet;
                            Version fromExport = new Version(Analyzer.cleanupVersion(exporterAttributes.getVersion()));
                            if (fromExport.equals(fromSet = new Version(Analyzer.cleanupVersion(attributes.getVersion())))) continue;
                            Reporter.SetLocation location = this.warning("Version for package %s is set to different values in the source (%s) and in the manifest (%s). The version in the manifest is not picked up by an other sibling bundles in this project or projects that directly depend on this project", packageName, attributes.get(key), exporterAttributes.get(key));
                            if (this.getPropertiesFile() != null) {
                                location.file(this.getPropertiesFile().getAbsolutePath());
                            }
                            location.header("Export-Package");
                            location.context(packageName);
                        }
                        catch (Exception e) {}
                    }
                }
            }
            finally {
                this.unsetProperty("@package");
            }
            this.fixupAttributes(packageRef, attributes);
            this.removeAttributes(attributes);
        }
    }

    void fixupAttributes(Descriptors.PackageRef packageRef, Attrs attributes) throws IOException {
        for (String key : attributes.keySet()) {
            String value = attributes.get(key);
            if (value.indexOf(36) >= 0) {
                value = this.getReplacer().process(value);
                attributes.put(key, value);
            }
            if (key.endsWith(":")) continue;
            String from = attributes.get("from:");
            this.verifyAttribute(from, "package info for " + packageRef, key, value);
        }
    }

    void removeAttributes(Attrs attributes) {
        String remove = attributes.remove("-remove-attribute:");
        if (remove != null) {
            Instructions removeInstr = new Instructions(remove);
            attributes.keySet().removeAll(removeInstr.select(attributes.keySet(), false));
        }
        Iterator<Map.Entry<String, String>> i = attributes.entrySet().iterator();
        while (i.hasNext()) {
            String v = i.next().getValue();
            if (!v.equals("!")) continue;
            i.remove();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    String calculateVersionRange(String version, boolean impl) {
        this.setProperty("@", version);
        try {
            String string = this.getVersionPolicy(impl);
            return string;
        }
        finally {
            this.unsetProperty("@");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void doUses(Packages exports, Map<Descriptors.PackageRef, List<Descriptors.PackageRef>> uses, Packages imports) {
        if (Analyzer.isTrue(this.getProperty("-nouses"))) {
            return;
        }
        for (Descriptors.PackageRef packageRef : exports.keySet()) {
            String packageName = packageRef.getFQN();
            this.setProperty("@package", packageName);
            try {
                this.doUses(packageRef, exports, uses, imports);
            }
            finally {
                this.unsetProperty("@package");
            }
        }
    }

    protected void doUses(Descriptors.PackageRef packageRef, Packages exports, Map<Descriptors.PackageRef, List<Descriptors.PackageRef>> uses, Packages imports) {
        Collection usedPackages;
        Attrs clause = exports.get(packageRef);
        String override = clause.get("uses:");
        if (override == null) {
            override = "<<USES>>";
        }
        if ((usedPackages = (Collection)uses.get(packageRef)) != null) {
            TreeSet<Descriptors.PackageRef> sharedPackages = new TreeSet<Descriptors.PackageRef>();
            sharedPackages.addAll(imports.keySet());
            sharedPackages.addAll(exports.keySet());
            sharedPackages.retainAll(usedPackages);
            sharedPackages.remove(packageRef);
            StringBuilder sb = new StringBuilder();
            String del = "";
            for (Descriptors.PackageRef usedPackage : sharedPackages) {
                if (usedPackage.isJava()) continue;
                sb.append(del);
                sb.append(usedPackage.getFQN());
                del = ",";
            }
            if (override.indexOf(36) >= 0) {
                this.setProperty("@uses", sb.toString());
                override = this.getReplacer().process(override);
                this.unsetProperty("@uses");
            } else {
                override = override.replaceAll("<<USES>>", Matcher.quoteReplacement(sb.toString())).trim();
            }
            if (override.endsWith(",")) {
                override = override.substring(0, override.length() - 1);
            }
            if (override.startsWith(",")) {
                override = override.substring(1);
            }
            if (override.length() > 0) {
                clause.put("uses:", override);
            }
        }
    }

    void removeTransitive(Descriptors.PackageRef name, Set<Descriptors.PackageRef> unreachable) {
        if (!unreachable.contains(name)) {
            return;
        }
        unreachable.remove(name);
        List ref = (List)this.uses.get(name);
        if (ref != null) {
            for (Descriptors.PackageRef element : ref) {
                this.removeTransitive(element, unreachable);
            }
        }
    }

    private void verifyAttribute(String path, String where, String key, String value) throws IOException {
        Processor.FileLine fl;
        File f;
        Reporter.SetLocation location;
        if (!Verifier.isExtended(key)) {
            location = this.error("%s attribute [%s='%s'], key must be an EXTENDED (CORE1.3.2 %s). From %s", where, key, value, Verifier.EXTENDED_S, path);
        } else if (value == null || value.trim().length() == 0) {
            location = this.error("%s attribute [%s='%s'], value is empty which is not allowed in ARGUMENT_S (CORE1.3.2 %s). From %s", where, key, value, Verifier.ARGUMENT_S, path);
        } else {
            return;
        }
        if (path != null && (f = new File(path)).isFile() && (fl = Analyzer.findHeader(f, key)) != null) {
            fl.set(location);
        }
    }

    @Override
    public void close() throws IOException {
        if (this.diagnostics) {
            PrintStream out = System.err;
            out.printf("Current directory            : %s%n", new File("").getAbsolutePath());
            out.println("Classpath used");
            for (Jar jar : this.getClasspath()) {
                out.printf("File                                : %s%n", jar.getSource());
                out.printf("File abs path                       : %s%n", jar.getSource().getAbsolutePath());
                out.printf("Name                                : %s%n", jar.getName());
                Map<String, Map<String, Resource>> dirs = jar.getDirectories();
                for (Map.Entry<String, Map<String, Resource>> entry : dirs.entrySet()) {
                    Map<String, Resource> dir = entry.getValue();
                    String name = entry.getKey().replace('/', '.');
                    if (dir != null) {
                        out.printf("                                      %-30s %d%n", name, dir.size());
                        continue;
                    }
                    out.printf("                                      %-30s <<empty>>%n", name);
                }
            }
        }
        super.close();
        if (this.classpath != null) {
            for (Jar jar : this.classpath) {
                jar.close();
            }
        }
    }

    public String _findpath(String[] args) {
        return this.findPath("findpath", args, true);
    }

    public String _findname(String[] args) {
        return this.findPath("findname", args, false);
    }

    String findPath(String name, String[] args, boolean fullPathName) {
        if (args.length > 3) {
            this.warning("Invalid nr of arguments to %s %s, syntax: ${%s (; reg-expr (; replacement)? )? }", name, Arrays.asList(args), name);
            return null;
        }
        String regexp = ".*";
        String replace = null;
        switch (args.length) {
            case 3: {
                replace = args[2];
            }
            case 2: {
                regexp = args[1];
            }
        }
        StringBuilder sb = new StringBuilder();
        String del = "";
        Pattern expr = Pattern.compile(regexp);
        for (String path : this.dot.getResources().keySet()) {
            Matcher m;
            int n;
            if (!fullPathName && (n = path.lastIndexOf(47)) >= 0) {
                path = path.substring(n + 1);
            }
            if (!(m = expr.matcher(path)).matches()) continue;
            if (replace != null) {
                path = m.replaceAll(replace);
            }
            sb.append(del);
            sb.append(path);
            del = ", ";
        }
        return sb.toString();
    }

    public void putAll(Map<String, String> additional, boolean force) {
        for (Map.Entry<String, String> entry : additional.entrySet()) {
            if (!force && this.getProperties().get(entry.getKey()) != null) continue;
            this.setProperty(entry.getKey(), entry.getValue());
        }
    }

    public List<Jar> getClasspath() {
        if (this.firstUse) {
            this.firstUse = false;
            String cp = this.getProperty("-classpath");
            if (cp != null) {
                for (String s : Analyzer.split(cp)) {
                    Jar jar = this.getJarFromName(s, "getting classpath");
                    if (jar != null) {
                        this.addClasspath(jar);
                        continue;
                    }
                    this.warning("Cannot find entry on -classpath: %s", s);
                }
            }
        }
        return this.classpath;
    }

    public void addClasspath(Jar jar) {
        if (this.isPedantic() && jar.getResources().isEmpty()) {
            this.warning("There is an empty jar or directory on the classpath: %s", jar.getName());
        }
        this.addClose(jar);
        this.classpath.add(jar);
        this.updateModified(jar.lastModified(), jar.toString());
    }

    public void addClasspath(Collection<?> jars) throws IOException {
        for (Object jar : jars) {
            if (jar instanceof Jar) {
                this.addClasspath((Jar)jar);
                continue;
            }
            if (jar instanceof File) {
                this.addClasspath((File)jar);
                continue;
            }
            if (jar instanceof String) {
                this.addClasspath(this.getFile((String)jar));
                continue;
            }
            this.error("Cannot convert to JAR to add to classpath %s. Not a File, Jar, or String", jar);
        }
    }

    public void addClasspath(File cp) throws IOException {
        if (!cp.exists()) {
            this.warning("File on classpath that does not exist: %s", cp);
        }
        Jar jar = new Jar(cp);
        this.addClasspath(jar);
    }

    @Override
    public void clear() {
        this.classpath.clear();
    }

    @Override
    public void forceRefresh() {
        super.forceRefresh();
        this.checks = null;
    }

    public Jar getTarget() {
        return this.getJar();
    }

    private void analyzeBundleClasspath() throws Exception {
        Parameters bcp = this.getBundleClasspath();
        if (bcp.isEmpty()) {
            this.analyzeJar(this.dot, "", true);
        } else {
            boolean okToIncludeDirs = true;
            for (String path : bcp.keySet()) {
                if (!this.dot.getDirectories().containsKey(path)) continue;
                okToIncludeDirs = false;
                break;
            }
            for (String path : bcp.keySet()) {
                Attrs info = bcp.get(path);
                if (path.equals(".")) {
                    this.analyzeJar(this.dot, "", okToIncludeDirs);
                    continue;
                }
                Resource resource = this.dot.getResource(path);
                if (resource != null) {
                    try {
                        Jar jar = Jar.fromResource(path, resource);
                        this.addClose(jar);
                        this.analyzeJar(jar, "", true);
                    }
                    catch (Exception e) {
                        this.warning("Invalid bundle classpath entry: %s: %s", path, e);
                    }
                    continue;
                }
                if (this.dot.getDirectories().containsKey(path)) {
                    if (bcp.containsKey(".")) {
                        this.warning("Bundle-ClassPath uses a directory '%s' as well as '.'. This means bnd does not know if a directory is a package.", path);
                    }
                    this.analyzeJar(this.dot, Processor.appendPath(path) + "/", true);
                    continue;
                }
                if ("optional".equals(info.get("resolution:"))) continue;
                this.warning("No sub JAR or directory %s", path);
            }
        }
    }

    private boolean analyzeJar(Jar jar, String prefix, boolean okToIncludeDirs) throws Exception {
        HashMap<String, Clazz> mismatched = new HashMap<String, Clazz>();
        for (String path : jar.getResources().keySet()) {
            Clazz clazz;
            if (!path.startsWith(prefix)) continue;
            String relativePath = path.substring(prefix.length());
            if (okToIncludeDirs) {
                int n = relativePath.lastIndexOf(47);
                if (n < 0) {
                    n = relativePath.length();
                }
                String relativeDir = relativePath.substring(0, n);
                Descriptors.PackageRef packageRef = this.getPackageRef(relativeDir);
                this.learnPackage(jar, prefix, packageRef, this.contained);
            }
            if (!path.endsWith(".class")) continue;
            Resource resource = jar.getResource(path);
            try {
                clazz = new Clazz(this, path, resource);
                clazz.parseClassFile();
            }
            catch (Throwable e) {
                this.exception(e, "Invalid class file %s (%s)", relativePath, e);
                continue;
            }
            String calculatedPath = clazz.getClassName().getPath();
            if (!calculatedPath.equals(relativePath)) {
                if (!okToIncludeDirs) continue;
                mismatched.put(clazz.getAbsolutePath(), clazz);
                continue;
            }
            this.classspace.put(clazz.getClassName(), clazz);
            Descriptors.PackageRef packageRef = clazz.getClassName().getPackageRef();
            this.learnPackage(jar, prefix, packageRef, this.contained);
            Set refs = Create.set();
            for (Descriptors.PackageRef p : clazz.getReferred()) {
                this.referred.put(p);
                refs.add(p);
            }
            refs.remove(packageRef);
            this.uses.addAll(packageRef, refs);
            this.apiUses.addAll(packageRef, clazz.getAPIUses());
        }
        if (mismatched.size() > 0) {
            this.error("Classes found in the wrong directory: %s", mismatched);
            return false;
        }
        return true;
    }

    public static String cleanupVersion(String version) {
        if (version == null) {
            return "0";
        }
        Matcher m = Verifier.VERSIONRANGE.matcher(version);
        if (m.matches()) {
            try {
                VersionRange vr = new VersionRange(version);
                return version;
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        if ((m = fuzzyVersionRange.matcher(version)).matches()) {
            String prefix = m.group(1);
            String first = m.group(2);
            String last = m.group(3);
            String suffix = m.group(4);
            return prefix + Analyzer.cleanupVersion(first) + "," + Analyzer.cleanupVersion(last) + suffix;
        }
        m = fuzzyVersion.matcher(version);
        if (m.matches()) {
            StringBuilder result = new StringBuilder();
            String major = Analyzer.removeLeadingZeroes(m.group(1));
            String minor = Analyzer.removeLeadingZeroes(m.group(3));
            String micro = Analyzer.removeLeadingZeroes(m.group(5));
            String qualifier = m.group(7);
            if (qualifier == null) {
                if (!Analyzer.isInteger(minor)) {
                    qualifier = minor;
                    minor = "0";
                } else if (!Analyzer.isInteger(micro)) {
                    qualifier = micro;
                    micro = "0";
                }
            }
            if (major != null) {
                result.append(major);
                if (minor != null) {
                    result.append(".");
                    result.append(minor);
                    if (micro != null) {
                        result.append(".");
                        result.append(micro);
                        if (qualifier != null) {
                            result.append(".");
                            Analyzer.cleanupModifier(result, qualifier);
                        }
                    } else if (qualifier != null) {
                        result.append(".0.");
                        Analyzer.cleanupModifier(result, qualifier);
                    }
                } else if (qualifier != null) {
                    result.append(".0.0.");
                    Analyzer.cleanupModifier(result, qualifier);
                }
                return result.toString();
            }
        }
        return version;
    }

    private static boolean isInteger(String minor) {
        return minor.length() < 10 || minor.length() == 10 && minor.compareTo("2147483647") < 0;
    }

    private static String removeLeadingZeroes(String group) {
        int n;
        if (group == null) {
            return "0";
        }
        for (n = 0; n < group.length() - 1 && group.charAt(n) == '0'; ++n) {
        }
        if (n == 0) {
            return group;
        }
        return group.substring(n);
    }

    static void cleanupModifier(StringBuilder result, String modifier) {
        Matcher m = fuzzyModifier.matcher(modifier);
        if (m.matches()) {
            modifier = m.group(2);
        }
        for (int i = 0; i < modifier.length(); ++i) {
            char c = modifier.charAt(i);
            if (!(c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_') && c != '-') continue;
            result.append(c);
        }
    }

    public String getVersionPolicy(boolean implemented) {
        if (implemented) {
            return this.getProperty("-provider-policy", DEFAULT_PROVIDER_POLICY);
        }
        return this.getProperty("-consumer-policy", DEFAULT_CONSUMER_POLICY);
    }

    public String _classes(String ... args) throws Exception {
        Collection<Clazz> matched = this.getClasses(args);
        if (matched.isEmpty()) {
            return "";
        }
        return Analyzer.join(matched);
    }

    public Collection<Clazz> getClasses(String ... args) throws Exception {
        HashSet<Clazz> matched = new HashSet<Clazz>(this.classspace.values());
        for (int i = 1; i < args.length; ++i) {
            if (args.length < i + 1) {
                throw new IllegalArgumentException("${classes} macro must have odd number of arguments. " + _classesHelp);
            }
            String typeName = args[i];
            if (typeName.equalsIgnoreCase("extending")) {
                typeName = "extends";
            } else if (typeName.equalsIgnoreCase("importing")) {
                typeName = "imports";
            } else if (typeName.equalsIgnoreCase("annotation")) {
                typeName = "annotated";
            } else if (typeName.equalsIgnoreCase("implementing")) {
                typeName = "implements";
            }
            Clazz.QUERY type = Clazz.QUERY.valueOf(typeName.toUpperCase());
            if (type == null) {
                throw new IllegalArgumentException("${classes} has invalid type: " + typeName + ". " + _classesHelp);
            }
            Instruction instr = null;
            if (Clazz.HAS_ARGUMENT.contains((Object)type)) {
                String s = args[++i];
                instr = new Instruction(s);
            }
            Iterator c = matched.iterator();
            while (c.hasNext()) {
                Clazz clazz = (Clazz)c.next();
                if (clazz.is(type, instr, this)) continue;
                c.remove();
            }
        }
        return new SortedList<Clazz>(matched, Clazz.NAME_COMPARATOR);
    }

    public String _packages(String ... args) throws Exception {
        Collection<Descriptors.PackageRef> matched = this.getPackages(this.contained, args);
        return matched.isEmpty() ? "" : Analyzer.join(matched);
    }

    public Collection<Descriptors.PackageRef> getPackages(Packages scope, String ... args) throws Exception {
        Instruction instr;
        Packages.QUERY queryType;
        LinkedList<Descriptors.PackageRef> pkgs = new LinkedList<Descriptors.PackageRef>();
        if (args.length == 1) {
            queryType = null;
            instr = null;
        } else if (args.length >= 2) {
            queryType = Packages.QUERY.valueOf(args[1].toUpperCase());
            instr = args.length > 2 ? new Instruction(args[2]) : null;
        } else {
            throw new IllegalArgumentException("${packages} macro: invalid argument count");
        }
        for (Map.Entry<Descriptors.PackageRef, Attrs> entry : scope.entrySet()) {
            Descriptors.PackageRef pkg = entry.getKey();
            Descriptors.TypeRef pkgInfoTypeRef = this.getTypeRefFromFQN(pkg.getFQN() + ".package-info");
            Clazz pkgInfo = this.classspace.get(pkgInfoTypeRef);
            boolean accept = false;
            if (queryType != null) {
                switch (queryType) {
                    case ANY: {
                        accept = true;
                        break;
                    }
                    case NAMED: {
                        if (instr == null) {
                            throw new IllegalArgumentException("Not enough arguments in ${packages} macro");
                        }
                        accept = instr.matches(pkg.getFQN()) ^ instr.isNegated();
                        break;
                    }
                    case ANNOTATED: {
                        if (instr == null) {
                            throw new IllegalArgumentException("Not enough arguments in ${packages} macro");
                        }
                        accept = pkgInfo != null && pkgInfo.is(Clazz.QUERY.ANNOTATED, instr, this);
                        break;
                    }
                    case VERSIONED: {
                        accept = entry.getValue().getVersion() != null;
                    }
                }
            } else {
                accept = true;
            }
            if (!accept) continue;
            pkgs.add(pkg);
        }
        return pkgs;
    }

    public String _exporters(String[] args) throws Exception {
        Macro.verifyCommand(args, "${exporters;<packagename>}, returns the list of jars that export the given package", null, 2, 2);
        StringBuilder sb = new StringBuilder();
        String del = "";
        String pack = args[1].replace('.', '/');
        for (Jar jar : this.classpath) {
            if (!jar.getDirectories().containsKey(pack)) continue;
            sb.append(del);
            sb.append(jar.getName());
        }
        return sb.toString();
    }

    public Map<Descriptors.TypeRef, Clazz> getClassspace() {
        return this.classspace;
    }

    public String _packageattribute(String[] args) {
        Attrs attrs;
        Macro.verifyCommand(args, "${packageattribute;<packagename>[;<attributename>]}, Return an attribute of a package, default the version. Only available after analysis", null, 2, 3);
        String packageName = args[1];
        String attrName = "version";
        if (args.length > 2) {
            attrName = args[2];
        }
        if ((attrs = this.contained.getByFQN(packageName)) == null) {
            return "version".equals(attrName) ? "0" : "";
        }
        String value = attrs.get(attrName);
        if (value == null) {
            return "version".equals(attrName) ? "0" : "";
        }
        return value;
    }

    public Resource findResource(String path) {
        for (Jar entry : this.getClasspath()) {
            Resource r = entry.getResource(path);
            if (r == null) continue;
            return r;
        }
        return null;
    }

    public Clazz findClass(Descriptors.TypeRef typeRef) throws Exception {
        Clazz c = this.classspace.get(typeRef);
        if (c != null) {
            return c;
        }
        c = this.importedClassesCache.get(typeRef);
        if (c != null) {
            return c;
        }
        Resource r = this.findResource(typeRef.getPath());
        if (r == null) {
            this.getClass().getClassLoader();
            URL url = ClassLoader.getSystemResource(typeRef.getPath());
            if (url != null) {
                r = new URLResource(url);
            }
        }
        if (r != null) {
            c = new Clazz(this, typeRef.getPath(), r);
            c.parseClassFile();
            this.importedClassesCache.put(typeRef, c);
        }
        return c;
    }

    public String getVersion() {
        String version = this.getProperty("Bundle-Version");
        if (version == null) {
            version = "0.0.0";
        }
        return version;
    }

    public boolean isNoBundle() {
        return Analyzer.isTrue(this.getProperty("-resourceonly")) || Analyzer.isTrue(this.getProperty("-nomanifest"));
    }

    public void referTo(Descriptors.TypeRef ref) {
        Descriptors.PackageRef pack = ref.getPackageRef();
        if (!this.referred.containsKey(pack)) {
            this.referred.put(pack, new Attrs());
        }
    }

    public void referToByBinaryName(String binaryClassName) {
        Descriptors.TypeRef ref = this.descriptors.getTypeRef(binaryClassName);
        this.referTo(ref);
    }

    protected void doRequireBnd() {
        Attrs require = OSGiHeader.parseProperties(this.getProperty("-require-bnd"));
        if (require == null || require.isEmpty()) {
            return;
        }
        Hashtable<String, String> map = new Hashtable<String, String>();
        map.put("version", this.getBndVersion());
        for (String filter : require.keySet()) {
            try {
                Filter f = new Filter(filter);
                if (f.match(map)) continue;
                this.error("%s fails for filter %s values=%s", "-require-bnd", require.get(filter), map);
            }
            catch (Exception t) {
                this.exception(t, "%s with value %s throws exception", "-require-bnd", require);
            }
        }
    }

    public String _md5(String[] args) throws Exception {
        Macro.verifyCommand(args, _md5Help, new Pattern[]{null, null, Pattern.compile("base64|hex")}, 2, 3);
        try (Digester<MD5> digester = MD5.getDigester(new OutputStream[0]);){
            boolean hex;
            Resource r = this.dot.getResource(args[1]);
            if (r == null) {
                throw new FileNotFoundException("From " + digester + ", not found " + args[1]);
            }
            IO.copy(r.openInputStream(), digester);
            boolean bl = hex = args.length > 2 && args[2].equals("hex");
            if (hex) {
                String string = Hex.toHexString(digester.digest().digest());
                return string;
            }
            String string = Base64.encodeBase64(digester.digest().digest());
            return string;
        }
    }

    public String _sha1(String[] args) throws Exception {
        Macro.verifyCommand(args, _sha1Help, new Pattern[]{null, null, Pattern.compile("base64|hex")}, 2, 3);
        try (Digester<SHA1> digester = SHA1.getDigester(new OutputStream[0]);){
            Resource r = this.dot.getResource(args[1]);
            if (r == null) {
                throw new FileNotFoundException("From sha1, not found " + args[1]);
            }
            IO.copy(r.openInputStream(), digester);
            String string = Base64.encodeBase64(digester.digest().digest());
            return string;
        }
    }

    public Descriptors.Descriptor getDescriptor(String descriptor) {
        return this.descriptors.getDescriptor(descriptor);
    }

    public Descriptors.TypeRef getTypeRef(String binaryClassName) {
        return this.descriptors.getTypeRef(binaryClassName);
    }

    public Descriptors.PackageRef getPackageRef(String binaryName) {
        return this.descriptors.getPackageRef(binaryName);
    }

    public Descriptors.TypeRef getTypeRefFromFQN(String fqn) {
        return this.descriptors.getTypeRefFromFQN(fqn);
    }

    public Descriptors.TypeRef getTypeRefFromPath(String path) {
        return this.descriptors.getTypeRefFromPath(path);
    }

    public boolean isImported(Descriptors.PackageRef packageRef) {
        return this.imports.containsKey(packageRef);
    }

    Packages filter(Instructions instructions, Packages source, Set<Instruction> nomatch) {
        Packages result = new Packages();
        ArrayList<Descriptors.PackageRef> refs = new ArrayList<Descriptors.PackageRef>(source.keySet());
        Collections.sort(refs);
        ArrayList<Instruction> filters = new ArrayList<Instruction>(instructions.keySet());
        if (nomatch == null) {
            nomatch = Create.set();
        }
        for (Instruction instruction : filters) {
            boolean match = false;
            Iterator i = refs.iterator();
            while (i.hasNext()) {
                Descriptors.PackageRef packageRef = (Descriptors.PackageRef)i.next();
                if (packageRef.isMetaData()) {
                    i.remove();
                    continue;
                }
                String packageName = packageRef.getFQN();
                if (!instruction.matches(packageName)) continue;
                match = true;
                if (!instruction.isNegated()) {
                    result.merge(packageRef, instruction.isDuplicate(), source.get(packageRef), instructions.get(instruction));
                }
                i.remove();
            }
            if (match || instruction.isAny()) continue;
            nomatch.add(instruction);
        }
        Iterator<Instruction> i = nomatch.iterator();
        while (i.hasNext()) {
            Instruction instruction;
            instruction = i.next();
            if (instruction.isLiteral() && !instruction.isNegated()) {
                result.merge(this.getPackageRef(instruction.getLiteral()), true, instructions.get(instruction));
                i.remove();
                continue;
            }
            if (instruction.isNegated()) {
                i.remove();
                continue;
            }
            if (!instruction.isOptional()) continue;
            i.remove();
        }
        return result;
    }

    public void setDiagnostics(boolean b) {
        this.diagnostics = b;
    }

    public Clazz.JAVA getLowestEE() {
        if (this.ees.isEmpty()) {
            return Clazz.JAVA.JDK1_4;
        }
        return this.ees.first();
    }

    public Clazz.JAVA getHighestEE() {
        if (this.ees.isEmpty()) {
            return Clazz.JAVA.JDK1_4;
        }
        return this.ees.last();
    }

    public String _ee(String[] args) {
        return this.getHighestEE().getEE();
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public File getOutputFile(String output) {
        String nm;
        Map.Entry<String, Attrs> name;
        File outputDir;
        if (output == null) {
            output = this.get("-output");
        }
        if (output != null) {
            File outputFile = this.getFile(output);
            if (!outputFile.isDirectory()) return outputFile;
            outputDir = outputFile;
        } else {
            outputDir = this.getBase();
        }
        if ((name = this.getBundleSymbolicName()) != null) {
            String bsn = name.getKey();
            String version = this.getBundleVersion();
            Version v = Version.parseVersion(version);
            String outputName = bsn + "-" + v.getWithoutQualifier() + ".jar";
            return new File(outputDir, outputName);
        }
        File source = this.getJar().getSource();
        if (source != null) {
            String outputName = source.getName();
            return new File(outputDir, outputName);
        }
        if (this.getPropertiesFile() != null && (nm = this.getPropertiesFile().getName()).endsWith(".bnd")) {
            nm = nm.substring(0, nm.length() - ".bnd".length()) + ".jar";
            logger.debug("name is {}", (Object)nm);
            return new File(outputDir, nm);
        }
        this.error("Cannot establish an output name from %s, nor bsn, nor source file name, using Untitled", output);
        int n = 0;
        File f = Analyzer.getFile(outputDir, "Untitled");
        while (f.isFile()) {
            f = Analyzer.getFile(outputDir, "Untitled-" + n++);
        }
        return f;
    }

    public boolean save(File output, boolean force) throws Exception {
        if (output == null) {
            output = this.getOutputFile(null);
        }
        Jar jar = this.getJar();
        File source = jar.getSource();
        logger.debug("check for modified build={} file={}, diff={}", new Object[]{jar.lastModified(), output.lastModified(), jar.lastModified() - output.lastModified()});
        if (!output.exists() || output.lastModified() <= jar.lastModified() || force) {
            File op = output.getParentFile();
            IO.mkdirs(op);
            if (source != null && output.getCanonicalPath().equals(source.getCanonicalPath())) {
                File bak = new File(source.getParentFile(), source.getName() + ".bak");
                try {
                    IO.rename(source, bak);
                }
                catch (IOException e) {
                    this.exception(e, "Could not create backup file %s", bak);
                }
            }
            try {
                logger.debug("Saving jar to {}", (Object)output);
                this.getJar().write(output);
            }
            catch (Exception e) {
                IO.delete(output);
                this.exception(e, "Cannot write JAR file to %s due to %s", output, e);
            }
            return true;
        }
        logger.debug("Not modified {}", (Object)output);
        return false;
    }

    public void setDefaults(String bsn, Version version) {
        if (this.getExportPackage() == null) {
            this.setExportPackage("*");
        }
        if (this.getImportPackage() == null) {
            this.setExportPackage("*");
        }
        if (bsn != null && this.getBundleSymbolicName() == null) {
            this.setBundleSymbolicName(bsn);
        }
        if (version != null && this.getBundleVersion() == null) {
            this.setBundleVersion(version);
        }
    }

    public Map<Descriptors.PackageRef, List<Descriptors.PackageRef>> cleanupUses(Map<Descriptors.PackageRef, List<Descriptors.PackageRef>> apiUses, boolean removeJava) {
        MultiMap<Descriptors.PackageRef, Descriptors.PackageRef> map = new MultiMap<Descriptors.PackageRef, Descriptors.PackageRef>(apiUses);
        for (Map.Entry e : map.entrySet()) {
            ((List)e.getValue()).remove(e.getKey());
            if (!removeJava) continue;
            Iterator i = ((List)e.getValue()).iterator();
            while (i.hasNext()) {
                if (!((Descriptors.PackageRef)i.next()).isJava()) continue;
                i.remove();
            }
        }
        return map;
    }

    public Set<Clazz> getClassspace(Descriptors.PackageRef source) {
        HashSet<Clazz> result = new HashSet<Clazz>();
        for (Clazz c : this.getClassspace().values()) {
            if (c.getClassName().getPackageRef() != source) continue;
            result.add(c);
        }
        return result;
    }

    public Map<Clazz.Def, List<Descriptors.TypeRef>> getXRef(Descriptors.PackageRef source, final Collection<Descriptors.PackageRef> dest, final int sourceModifiers) throws Exception {
        final MultiMap<Clazz.Def, Descriptors.TypeRef> xref = new MultiMap<Clazz.Def, Descriptors.TypeRef>(Clazz.Def.class, Descriptors.TypeRef.class, true);
        for (final Clazz clazz : this.getClassspace().values()) {
            if ((clazz.accessx & sourceModifiers) == 0 || source != null && source != clazz.getClassName().getPackageRef()) continue;
            clazz.parseClassFileWithCollector(new ClassDataCollector(){
                Clazz.Def member;

                @Override
                public void extendsClass(Descriptors.TypeRef zuper) throws Exception {
                    if (dest.contains(zuper.getPackageRef())) {
                        xref.add(clazz.getExtends(zuper), zuper);
                    }
                }

                @Override
                public void implementsInterfaces(Descriptors.TypeRef[] interfaces) throws Exception {
                    for (Descriptors.TypeRef i : interfaces) {
                        if (!dest.contains(i.getPackageRef())) continue;
                        xref.add(clazz.getImplements(i), i);
                    }
                }

                @Override
                public void referTo(Descriptors.TypeRef to, int modifiers) {
                    if (to.isJava()) {
                        return;
                    }
                    if (!dest.contains(to.getPackageRef())) {
                        return;
                    }
                    if (this.member != null && (modifiers & sourceModifiers) != 0) {
                        xref.add(this.member, to);
                    }
                }

                @Override
                public void method(Clazz.MethodDef defined) {
                    this.member = defined;
                }

                @Override
                public void field(Clazz.FieldDef defined) {
                    this.member = defined;
                }
            });
        }
        return xref;
    }

    public String _exports(String[] args) {
        return Analyzer.join(this.filter(this.getExports().keySet(), args));
    }

    public String _imports(String[] args) {
        return Analyzer.join(this.filter(this.getImports().keySet(), args));
    }

    private <T> Collection<T> filter(Collection<T> list, String[] args) {
        if (args == null || args.length <= 1) {
            return list;
        }
        if (args.length > 2) {
            this.warning("Too many arguments for ${%s} macro", args[0]);
        }
        Instructions instrs = new Instructions(args[1]);
        return instrs.select(list, false);
    }

    @Override
    public void report(Map<String, Object> table) throws Exception {
        super.report(table);
        this.analyze();
        table.put("Contained", this.getContained().entrySet());
        table.put("Imported", this.getImports().entrySet());
        table.put("Exported", this.getExports().entrySet());
        table.put("Referred", this.getReferred().entrySet());
        table.put("Referred", this.getReferred().entrySet());
        table.put("Bundle Symbolic Name", this.getBsn());
        table.put("Execution Environments", this.ees);
    }

    public SortedSet<Clazz.JAVA> getEEs() {
        return this.ees;
    }

    public String validResourcePath(String name, String reportIfWrong) {
        boolean changed = false;
        StringBuilder sb = new StringBuilder(name);
        for (int i = 0; i < sb.length(); ++i) {
            char c = sb.charAt(i);
            if (c == '-' || c == '.' || c == '_' || c == '$' || Character.isLetterOrDigit(c)) continue;
            sb.replace(i, i + 1, "-");
            changed = true;
        }
        if (changed) {
            if (reportIfWrong != null) {
                this.warning("%s: %s", reportIfWrong, name);
            }
            return sb.toString();
        }
        return name;
    }

    public boolean check(Check key) {
        if (this.checks == null) {
            Parameters p = new Parameters(this.getProperty("-check"), this);
            this.checks = new HashSet<Check>();
            for (String k : p.keySet()) {
                try {
                    if (k.equalsIgnoreCase("all")) {
                        this.checks = EnumSet.allOf(Check.class);
                        break;
                    }
                    Check c = Enum.valueOf(Check.class, k.toUpperCase().replace('-', '_'));
                    this.checks.add(c);
                }
                catch (Exception e) {
                    this.error("Invalid -check constant, allowed values are %s", Arrays.toString((Object[])Check.values()));
                }
            }
            if (!this.checks.isEmpty()) {
                this.checks = EnumSet.copyOf(this.checks);
            }
        }
        return this.checks.contains((Object)key) || this.checks.contains((Object)Check.ALL);
    }

    public String getSourceFileFor(Descriptors.TypeRef type) throws Exception {
        Set<File> sp = Collections.singleton(this.getFile(this.getProperty("src", "src")));
        return this.getSourceFileFor(type, sp);
    }

    public String getSourceFileFor(Descriptors.TypeRef type, Collection<File> sourcePath) throws Exception {
        Clazz clazz = this.findClass(type);
        if (clazz == null) {
            Attrs attrs = this.classpathExports.get(type.getPackageRef());
            String from = attrs.get("from:");
            if (from != null) {
                return from;
            }
            return null;
        }
        String path = type.getPackageRef().getBinary() + "/" + clazz.sourceFile;
        for (File srcDir : sourcePath) {
            File file;
            if (!srcDir.isFile() || !(file = IO.getFile(srcDir, path)).isFile()) continue;
            String abspath = file.getAbsolutePath();
            if (File.separatorChar == '/') {
                return abspath;
            }
            return abspath.replace(File.separatorChar, '/');
        }
        return "";
    }

    public void setTypeLocation(Reporter.SetLocation location, Descriptors.TypeRef type) throws Exception {
        String sf = this.getSourceFileFor(type);
        if (sf != null) {
            File sff = IO.getFile(sf);
            if (sff != null) {
                String[] names;
                for (String name : names = new String[]{type.getShorterName(), type.getFQN(), type.getShortName().replace('$', '.')}) {
                    Processor.FileLine fl = Processor.findHeader(sff, Pattern.compile("(class|interface)\\s*" + name, 32));
                    if (fl == null) continue;
                    fl.set(location);
                }
            }
            location.file(sf);
        }
    }

    public boolean assignable(String annoService, String inferredService) {
        if (annoService == null || annoService.isEmpty() || inferredService == null || inferredService.isEmpty() || Object.class.getName().equals(inferredService)) {
            return true;
        }
        try {
            Clazz annoServiceClazz = this.findClass(this.getTypeRefFromFQN(annoService));
            Clazz inferredServiceClazz = this.findClass(this.getTypeRefFromFQN(inferredService));
            return this.assignable(annoServiceClazz, inferredServiceClazz);
        }
        catch (Exception exception) {
            return true;
        }
    }

    public boolean assignable(Clazz annoServiceClazz, Clazz inferredServiceClazz) {
        Descriptors.TypeRef superType;
        if (annoServiceClazz == null || inferredServiceClazz == null) {
            return true;
        }
        if (annoServiceClazz.equals(inferredServiceClazz)) {
            return true;
        }
        if (!inferredServiceClazz.isInterface()) {
            if (annoServiceClazz.isInterface()) {
                return false;
            }
            Descriptors.TypeRef zuper = annoServiceClazz.getSuper();
            if (zuper == null) {
                return false;
            }
            try {
                return this.assignable(this.findClass(zuper), inferredServiceClazz);
            }
            catch (Exception e) {
                return true;
            }
        }
        Descriptors.TypeRef[] intfs = annoServiceClazz.getInterfaces();
        if (intfs != null) {
            for (Descriptors.TypeRef intf : intfs) {
                try {
                    if (!this.assignable(this.findClass(intf), inferredServiceClazz)) continue;
                    return true;
                }
                catch (Exception e) {
                    return true;
                }
            }
        }
        if ((superType = annoServiceClazz.getSuper()) != null) {
            try {
                Clazz zuper = this.findClass(superType);
                if (zuper != null) {
                    return this.assignable(zuper, inferredServiceClazz);
                }
                return true;
            }
            catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    static {
        OLD_PACKAGEINFO_SYNTAX_P = Pattern.compile("class\\s+(.+)\\s+version\\s+([0-9]{1,9}(:?\\.[0-9]{1,9}(:?\\.[0-9]{1,9}(:?\\.[0-9A-Za-z_-]+)?)?)?)");
        OBJECT_REFERENCE = Pattern.compile("([^\\.]+\\.)*([^\\.]+)");
        df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);
        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        fuzzyVersion = Pattern.compile("(\\d+)(\\.(\\d+)(\\.(\\d+))?)?([^a-zA-Z0-9](.*))?", 32);
        fuzzyVersionRange = Pattern.compile("(\\(|\\[)\\s*([-\\da-zA-Z.]+)\\s*,\\s*([-\\da-zA-Z.]+)\\s*(\\]|\\))", 32);
        fuzzyModifier = Pattern.compile("(\\d+[.-])*(.*)", 32);
        nummeric = Pattern.compile("\\d*");
        _classesHelp = "${classes;'implementing'|'extending'|'importing'|'named'|'version'|'any';<pattern>}, Return a list of class fully qualified class names that extend/implement/import any of the contained classes matching the pattern\n";
        _packagesHelp = "${packages;'named'|'annotated'|'any';<pattern>}, Return a list of packages contained in the bundle that match the pattern\n";
        _md5Help = "${md5;path}";
        _sha1Help = "${sha1;path}";
    }

    public static enum Check {
        ALL,
        IMPORTS,
        EXPORTS;

    }
}

