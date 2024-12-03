/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.About;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Domain;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.bnd.service.verifier.VerifierPlugin;
import aQute.bnd.util.dto.DTO;
import aQute.bnd.version.VersionRange;
import aQute.lib.base64.Base64;
import aQute.lib.filter.Filter;
import aQute.lib.io.IO;
import aQute.libg.cryptography.Digester;
import aQute.libg.cryptography.SHA1;
import aQute.libg.qtokens.QuotedTokenizer;
import aQute.service.reporter.Report;
import aQute.service.reporter.Reporter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Verifier
extends Processor {
    private final Jar dot;
    private final Manifest manifest;
    private final Domain main;
    private boolean r3;
    private boolean usesRequire;
    static final int V1_1 = 45;
    static final int V1_2 = 46;
    static final int V1_3 = 47;
    static final int V1_4 = 48;
    static final int V1_5 = 49;
    static final int V1_6 = 50;
    static final int V1_7 = 51;
    static final int V1_8 = 52;
    static final int V9_0 = 53;
    static final EE[] ees = new EE[]{new EE("CDC-1.0/Foundation-1.0", 47, 45), new EE("CDC-1.1/Foundation-1.1", 47, 46), new EE("OSGi/Minimum-1.0", 47, 45), new EE("OSGi/Minimum-1.1", 47, 46), new EE("OSGi/Minimum-1.2", 47, 46), new EE("JRE-1.1", 45, 45), new EE("J2SE-1.2", 46, 45), new EE("J2SE-1.3", 47, 45), new EE("J2SE-1.4", 47, 46), new EE("J2SE-1.5", 49, 49), new EE("JavaSE-1.6", 50, 50), new EE("JavaSE-1.7", 51, 51), new EE("JavaSE-1.8", 52, 52), new EE("JavaSE-9", 53, 53), new EE("PersonalJava-1.1", 45, 45), new EE("PersonalJava-1.2", 45, 45), new EE("CDC-1.0/PersonalBasis-1.0", 47, 45), new EE("CDC-1.0/PersonalJava-1.0", 47, 45), new EE("CDC-1.1/PersonalBasis-1.1", 47, 46), new EE("CDC-1.1/PersonalJava-1.1", 47, 46)};
    static final Pattern EENAME = Pattern.compile("CDC-1\\.0/Foundation-1\\.0|CDC-1\\.1/Foundation-1\\.1|OSGi/Minimum-1\\.[0-2]|JRE-1\\.1|J2SE-1\\.[2-5]|JavaSE-1\\.[6-8]|JavaSE-9|PersonalJava-1\\.[12]|CDC-1\\.0/PersonalBasis-1\\.0|CDC-1\\.0/PersonalJava-1\\.0|CDC-1\\.1/PersonalBasis-1\\.1|CDC-1\\.1/PersonalJava-1\\.1");
    public static final String[] EES = new String[]{"CDC-1.0/Foundation-1.0", "CDC-1.1/Foundation-1.1", "OSGi/Minimum-1.0", "OSGi/Minimum-1.1", "OSGi/Minimum-1.2", "JRE-1.1", "J2SE-1.2", "J2SE-1.3", "J2SE-1.4", "J2SE-1.5", "JavaSE-1.6", "JavaSE-1.7", "JavaSE-1.8", "JavaSE-9", "PersonalJava-1.1", "PersonalJava-1.2", "CDC-1.0/PersonalBasis-1.0", "CDC-1.0/PersonalJava-1.0", "CDC-1.1/PersonalBasis-1.1", "CDC-1.1/PersonalJava-1.1"};
    public static final Pattern ReservedFileNames = Pattern.compile("CON(\\..+)?|PRN(\\..+)?|AUX(\\..+)?|CLOCK\\$|NUL(\\..+)?|COM[1-9](\\..+)?|LPT[1-9](\\..+)?|\\$Mft|\\$MftMirr|\\$LogFile|\\$Volume|\\$AttrDef|\\$Bitmap|\\$Boot|\\$BadClus|\\$Secure|\\$Upcase|\\$Extend|\\$Quota|\\$ObjId|\\$Reparse", 2);
    static final Pattern CARDINALITY_PATTERN = Pattern.compile("single|multiple");
    static final Pattern RESOLUTION_PATTERN = Pattern.compile("optional|mandatory");
    static final Pattern BUNDLEMANIFESTVERSION = Pattern.compile("2");
    public static final String SYMBOLICNAME_STRING = "[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)*";
    public static final Pattern SYMBOLICNAME = Pattern.compile("[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)*");
    public static final String VERSION_STRING = "[0-9]{1,9}(\\.[0-9]{1,9}(\\.[0-9]{1,9}(\\.[0-9A-Za-z_-]+)?)?)?";
    public static final Pattern VERSION = Pattern.compile("[0-9]{1,9}(\\.[0-9]{1,9}(\\.[0-9]{1,9}(\\.[0-9A-Za-z_-]+)?)?)?");
    static final Pattern FILTEROP = Pattern.compile("=|<=|>=|~=");
    public static final Pattern VERSIONRANGE = Pattern.compile("((\\(|\\[)[0-9]{1,9}(\\.[0-9]{1,9}(\\.[0-9]{1,9}(\\.[0-9A-Za-z_-]+)?)?)?,[0-9]{1,9}(\\.[0-9]{1,9}(\\.[0-9]{1,9}(\\.[0-9A-Za-z_-]+)?)?)?(\\]|\\)))|[0-9]{1,9}(\\.[0-9]{1,9}(\\.[0-9]{1,9}(\\.[0-9A-Za-z_-]+)?)?)?");
    static final Pattern FILE = Pattern.compile("/?[^/\"\n\r\u0000]+(/[^/\"\n\r\u0000]+)*");
    static final Pattern WILDCARDPACKAGE = Pattern.compile("((\\p{Alnum}|_)+(\\.(\\p{Alnum}|_)+)*(\\.\\*)?)|\\*");
    public static final Pattern ISO639 = Pattern.compile("[A-Z][A-Z]");
    public static final Pattern HEADER_PATTERN = Pattern.compile("[A-Za-z0-9][-a-zA-Z0-9_]+");
    public static final Pattern TOKEN = Pattern.compile("[-a-zA-Z0-9_]+");
    public static final Pattern NUMBERPATTERN = Pattern.compile("\\d+");
    public static final Pattern PACKAGEPATTERN = Pattern.compile("\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*(\\.\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*)*");
    public static final Pattern PACKAGEPATTERN_OR_EMPTY = Pattern.compile(PACKAGEPATTERN + "|^$");
    public static final Pattern MULTIPACKAGEPATTERN = Pattern.compile("(\\s*" + PACKAGEPATTERN + ")(" + "\\s*,\\s*" + PACKAGEPATTERN + ")*\\s*");
    public static final Pattern PATHPATTERN = Pattern.compile(".*");
    public static final Pattern FQNPATTERN = Pattern.compile(".*");
    public static final Pattern URLPATTERN = Pattern.compile(".*");
    public static final Pattern ANYPATTERN = Pattern.compile(".*");
    public static final Pattern FILTERPATTERN = Pattern.compile(".*");
    public static final Pattern TRUEORFALSEPATTERN = Pattern.compile("true|false|TRUE|FALSE");
    public static final Pattern WILDCARDNAMEPATTERN = Pattern.compile(".*");
    public static final Pattern BUNDLE_ACTIVATIONPOLICYPATTERN = Pattern.compile("lazy");
    public static final String VERSION_S = "[0-9]{1,9}(:?\\.[0-9]{1,9}(:?\\.[0-9]{1,9}(:?\\.[0-9A-Za-z_-]+)?)?)?";
    public static final Pattern VERSION_P = Pattern.compile("[0-9]{1,9}(:?\\.[0-9]{1,9}(:?\\.[0-9]{1,9}(:?\\.[0-9A-Za-z_-]+)?)?)?");
    public static final String VERSION_RANGE_S = "(?:(:?\\(|\\[)[0-9]{1,9}(:?\\.[0-9]{1,9}(:?\\.[0-9]{1,9}(:?\\.[0-9A-Za-z_-]+)?)?)?,[0-9]{1,9}(:?\\.[0-9]{1,9}(:?\\.[0-9]{1,9}(:?\\.[0-9A-Za-z_-]+)?)?)?(\\]|\\)))|[0-9]{1,9}(:?\\.[0-9]{1,9}(:?\\.[0-9]{1,9}(:?\\.[0-9A-Za-z_-]+)?)?)?";
    public static final Pattern VERSIONRANGE_P = VERSIONRANGE;
    public static String EXTENDED_S = "[-a-zA-Z0-9_.]+";
    public static Pattern EXTENDED_P = Pattern.compile(EXTENDED_S);
    public static String QUOTEDSTRING = "\"[^\"]*\"";
    public static Pattern QUOTEDSTRING_P = Pattern.compile(QUOTEDSTRING);
    public static String ARGUMENT_S = "(:?" + EXTENDED_S + ")|(?:" + QUOTEDSTRING + ")";
    public static Pattern ARGUMENT_P = Pattern.compile(ARGUMENT_S);
    public static final String[] OSNAMES = new String[]{"AIX", "DigitalUnix", "Embos", "Epoc32", "FreeBSD", "HPUX", "IRIX", "Linux", "MacOS", "NetBSD", "Netware", "OpenBSD", "OS2", "QNX", "Solaris", "SunOS", "VxWorks", "Windows95", "Win32", "Windows98", "WindowsNT", "WindowsCE", "Windows2000", "Windows2003", "WindowsXP", "WindowsVista"};
    public static final String[] PROCESSORNAMES = new String[]{"68k", "ARM_LE", "arm_le", "arm_be", "Alpha", "ia64n", "ia64w", "Ignite", "Mips", "PArisc", "PowerPC", "Sh4", "Sparc", "Sparcv9", "S390", "S390x", "V850E", "x86", "i486", "x86-64"};
    final Analyzer analyzer;
    private Instructions dynamicImports;
    private boolean frombuilder;

    public Verifier(Jar jar) throws Exception {
        this.analyzer = new Analyzer(this);
        this.analyzer.use(this);
        this.addClose(this.analyzer);
        this.analyzer.setJar(jar);
        this.manifest = this.analyzer.calcManifest();
        this.main = Domain.domain(this.manifest);
        this.dot = jar;
        this.getInfo(this.analyzer);
    }

    public Verifier(Analyzer analyzer) throws Exception {
        super(analyzer);
        this.analyzer = analyzer;
        this.dot = analyzer.getJar();
        this.manifest = this.dot.getManifest();
        this.main = Domain.domain(this.manifest);
    }

    private void verifyHeaders() {
        for (String h : this.main) {
            if (HEADER_PATTERN.matcher(h).matches()) continue;
            this.error("Invalid Manifest header: %s, pattern=%s", h, HEADER_PATTERN);
        }
    }

    public void verifyNative() {
        String nc = this.get("Bundle-NativeCode");
        this.doNative(nc);
    }

    public void doNative(String nc) {
        if (nc != null) {
            char del;
            QuotedTokenizer qt = new QuotedTokenizer(nc, ",;=", false);
            do {
                String key;
                String name;
                if ((name = qt.nextToken()) == null) {
                    this.error("Can not parse name from bundle native code header: %s", nc);
                    return;
                }
                del = qt.getSeparator();
                if (del == ';') {
                    if (this.dot == null || this.dot.exists(name)) continue;
                    this.error("Native library not found in JAR: %s", name);
                    continue;
                }
                String value = null;
                if (del == '=') {
                    value = qt.nextToken();
                }
                if (!(key = name.toLowerCase()).equals("osname")) {
                    if (key.equals("osversion")) {
                        Verifier.verify(value, VERSIONRANGE);
                    } else if (key.equals("language")) {
                        Verifier.verify(value, ISO639);
                    } else if (!key.equals("processor")) {
                        if (key.equals("selection-filter")) {
                            this.verifyFilter(value);
                        } else if (name.equals("*") && value == null) {
                            if (qt.nextToken() != null) {
                                this.error("Bundle-Native code header may only END in wildcard %s", nc);
                            }
                        } else {
                            this.warning("Unknown attribute in native code: %s=%s", name, value);
                        }
                    }
                }
                del = qt.getSeparator();
            } while (del == ';' || del == ',');
        }
    }

    public boolean verifyFilter(String value) {
        String s = Verifier.validateFilter(value);
        if (s == null) {
            return true;
        }
        this.error("%s", s);
        return false;
    }

    public static String validateFilter(String value) {
        try {
            Verifier.verifyFilter(value, 0);
            return null;
        }
        catch (Exception e) {
            return "Not a valid filter: " + value + ": " + e;
        }
    }

    private void verifyActivator() throws Exception {
        String raw;
        String bactivator = this.main.get("Bundle-Activator");
        if (bactivator != null) {
            if (!PACKAGEPATTERN.matcher(bactivator).matches()) {
                boolean allElementsAreTypes = true;
                for (String element : Verifier.split(bactivator)) {
                    if (PACKAGEPATTERN.matcher(element.trim()).matches()) continue;
                    allElementsAreTypes = false;
                    break;
                }
                if (allElementsAreTypes) {
                    this.registerActivatorErrorLocation(this.error("The Bundle-Activator header only supports a single type. The following types were found: %s. This usually happens when a macro resolves to multiple types", bactivator), bactivator, ActivatorErrorType.MULTIPLE_TYPES);
                } else {
                    this.registerActivatorErrorLocation(this.error("A Bundle-Activator header is present and its value is not a valid type name %s", bactivator), bactivator, ActivatorErrorType.INVALID_TYPE_NAME);
                }
                return;
            }
            Descriptors.TypeRef ref = this.analyzer.getTypeRefFromFQN(bactivator);
            if (this.analyzer.getClassspace().containsKey(ref)) {
                Clazz activatorClazz = this.analyzer.getClassspace().get(ref);
                if (activatorClazz.isInterface()) {
                    this.registerActivatorErrorLocation(this.error("The Bundle Activator %s is an interface and therefore cannot be instantiated.", bactivator), bactivator, ActivatorErrorType.IS_INTERFACE);
                } else {
                    if (activatorClazz.isAbstract()) {
                        this.registerActivatorErrorLocation(this.error("The Bundle Activator %s is abstract and therefore cannot be instantiated.", bactivator), bactivator, ActivatorErrorType.IS_ABSTRACT);
                    }
                    if (!activatorClazz.isPublic()) {
                        this.registerActivatorErrorLocation(this.error("Bundle Activator classes must be public, and %s is not.", bactivator), bactivator, ActivatorErrorType.NOT_PUBLIC);
                    }
                    if (!activatorClazz.hasPublicNoArgsConstructor()) {
                        this.registerActivatorErrorLocation(this.error("Bundle Activator classes must have a public zero-argument constructor and %s does not.", bactivator), bactivator, ActivatorErrorType.NO_SUITABLE_CONSTRUCTOR);
                    }
                    if (!this.analyzer.assignable(activatorClazz.getFQN(), "org.osgi.framework.BundleActivator")) {
                        this.registerActivatorErrorLocation(this.error("The Bundle Activator %s does not implement BundleActivator.", bactivator), bactivator, ActivatorErrorType.NOT_AN_ACTIVATOR);
                    }
                }
                return;
            }
            Descriptors.PackageRef packageRef = ref.getPackageRef();
            if (packageRef.isDefaultPackage()) {
                this.registerActivatorErrorLocation(this.error("The Bundle Activator is not in the bundle and it is in the default package ", new Object[0]), bactivator, ActivatorErrorType.DEFAULT_PACKAGE);
            } else if (!this.analyzer.isImported(packageRef)) {
                this.registerActivatorErrorLocation(this.error("Bundle-Activator not found on the bundle class path nor in imports: %s", bactivator), bactivator, ActivatorErrorType.NOT_ACCESSIBLE);
            } else {
                this.registerActivatorErrorLocation(this.warning("Bundle-Activator %s is being imported into the bundle rather than being contained inside it. This is usually a bundle packaging error", bactivator), bactivator, ActivatorErrorType.IS_IMPORTED);
            }
        } else if (this.parent != null && (raw = this.parent.getUnprocessedProperty("Bundle-Activator", null)) != null) {
            if (raw.isEmpty()) {
                this.registerActivatorErrorLocation(this.warning("A Bundle-Activator header was present but no activator class was defined", new Object[0]), "", ActivatorErrorType.NOT_SET);
            } else {
                this.registerActivatorErrorLocation(this.error("A Bundle-Activator header is present but no activator class was found using the macro %s", raw), raw, ActivatorErrorType.NO_RESULT_FROM_MACRO);
            }
            return;
        }
    }

    private void registerActivatorErrorLocation(Reporter.SetLocation location, String activator, ActivatorErrorType errorType) throws Exception {
        Processor.FileLine fl = this.getHeader("Bundle-Activator");
        location.header("Bundle-Activator").file(fl.file.getAbsolutePath()).line(fl.line).length(fl.length).details(new BundleActivatorError(activator, errorType));
    }

    private void verifyComponent() {
        String serviceComponent = this.main.get("Service-Component");
        if (serviceComponent != null) {
            Parameters map = this.parseHeader(serviceComponent);
            for (String component : map.keySet()) {
                if (component.indexOf(42) >= 0 || this.dot.exists(component)) continue;
                this.error("Service-Component entry can not be located in JAR: %s", component);
            }
        }
    }

    private void verifyUnresolvedReferences() throws Exception {
        if (this.isFrombuilder()) {
            return;
        }
        Manifest m = this.analyzer.getJar().getManifest();
        if (m == null) {
            this.error("No manifest", new Object[0]);
            return;
        }
        Domain domain = Domain.domain(m);
        TreeSet<Descriptors.PackageRef> unresolvedReferences = new TreeSet<Descriptors.PackageRef>(this.analyzer.getReferred().keySet());
        unresolvedReferences.removeAll(this.analyzer.getContained().keySet());
        for (String pname : domain.getImportPackage().keySet()) {
            Descriptors.PackageRef pref = this.analyzer.getPackageRef(pname);
            unresolvedReferences.remove(pref);
        }
        Iterator p = unresolvedReferences.iterator();
        while (p.hasNext()) {
            Descriptors.PackageRef pack = (Descriptors.PackageRef)p.next();
            if (pack.isJava()) {
                p.remove();
                continue;
            }
            if (!this.isDynamicImport(pack)) continue;
            p.remove();
        }
        if (domain.getRequireBundle().isEmpty() && domain.get("ExtensionBundle-Activator") == null && (domain.getFragmentHost() == null || domain.getFragmentHost().getKey().equals("system.bundle"))) {
            if (!unresolvedReferences.isEmpty()) {
                HashSet<String> culprits = new HashSet<String>();
                for (Clazz clazz : this.analyzer.getClassspace().values()) {
                    if (!this.hasOverlap(unresolvedReferences, clazz.getReferred())) continue;
                    culprits.add(clazz.getAbsolutePath());
                }
                if (this.analyzer instanceof Builder) {
                    this.warning("Unresolved references to %s by class(es) %s on the Bundle-ClassPath: %s", unresolvedReferences, culprits, this.analyzer.getBundleClasspath().keySet());
                } else {
                    this.error("Unresolved references to %s by class(es) %s on the Bundle-ClassPath: %s", unresolvedReferences, culprits, this.analyzer.getBundleClasspath().keySet());
                }
                return;
            }
        } else if (this.isPedantic()) {
            this.warning("Use of Require-Bundle, ExtensionBundle-Activator, or a system bundle fragment makes it impossible to verify unresolved references", new Object[0]);
        }
    }

    private boolean isDynamicImport(Descriptors.PackageRef pack) {
        if (this.dynamicImports == null) {
            this.dynamicImports = new Instructions(this.main.getDynamicImportPackage());
        }
        if (this.dynamicImports.isEmpty()) {
            return false;
        }
        return this.dynamicImports.matches(pack.getFQN());
    }

    private boolean hasOverlap(Set<?> a, Set<?> b) {
        Iterator<?> i = a.iterator();
        while (i.hasNext()) {
            if (!b.contains(i.next())) continue;
            return true;
        }
        return false;
    }

    public void verify() throws Exception {
        this.verifyHeaders();
        this.verifyDirectives("Export-Package", "uses:|mandatory:|include:|exclude:|-import:", PACKAGEPATTERN, "package");
        this.verifyDirectives("Import-Package", "resolution:", PACKAGEPATTERN, "package");
        this.verifyDirectives("Require-Bundle", "visibility:|resolution:", SYMBOLICNAME, "bsn");
        this.verifyDirectives("Fragment-Host", "extension:", SYMBOLICNAME, "bsn");
        this.verifyDirectives("Provide-Capability", "effective:|uses:", null, null);
        this.verifyDirectives("Require-Capability", "effective:|resolution:|filter:|cardinality:", null, null);
        this.verifyDirectives("Bundle-SymbolicName", "singleton:|fragment-attachment:|mandatory:", SYMBOLICNAME, "bsn");
        this.verifyManifestFirst();
        this.verifyActivator();
        this.verifyActivationPolicy();
        this.verifyComponent();
        this.verifyNative();
        this.verifyImports();
        this.verifyExports();
        this.verifyUnresolvedReferences();
        this.verifySymbolicName();
        this.verifyListHeader("Bundle-RequiredExecutionEnvironment", EENAME, false);
        this.verifyHeader("Bundle-ManifestVersion", BUNDLEMANIFESTVERSION, false);
        this.verifyHeader("Bundle-Version", VERSION, true);
        this.verifyListHeader("Bundle-ClassPath", FILE, false);
        this.verifyDynamicImportPackage();
        this.verifyBundleClasspath();
        this.verifyUses();
        if (this.usesRequire && !this.getErrors().isEmpty()) {
            this.getWarnings().add(0, "Bundle uses Require Bundle, this can generate false errors because then not enough information is available without the required bundles");
        }
        this.verifyRequirements();
        this.verifyCapabilities();
        this.verifyMetaPersistence();
        this.verifyPathNames();
        this.doVerifierPlugins();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doVerifierPlugins() {
        for (VerifierPlugin plugin : this.getPlugins(VerifierPlugin.class)) {
            try {
                Processor previous = this.beginHandleErrors(plugin.toString());
                try {
                    plugin.verify(this.analyzer);
                }
                finally {
                    this.endHandleErrors(previous);
                }
            }
            catch (Exception e) {
                e.printStackTrace(System.err);
                this.error("Verifier Plugin %s failed %s", plugin, e);
            }
        }
    }

    void verifyPathNames() {
        if (!this.since(About._2_3)) {
            return;
        }
        HashSet<String> invalidPaths = new HashSet<String>();
        Pattern pattern = ReservedFileNames;
        this.setProperty("@", ReservedFileNames.pattern());
        String p = this.getProperty("-invalidfilenames");
        this.unsetProperty("@");
        if (p != null) {
            try {
                pattern = Pattern.compile(p, 2);
            }
            catch (Exception e) {
                Reporter.SetLocation error = this.exception(e, "%s is not a valid regular expression %s: %s", "-invalidfilenames", e, p);
                error.context(p).header("-invalidfilenames");
                return;
            }
        }
        HashSet<String> segments = new HashSet<String>();
        for (String path : this.dot.getResources().keySet()) {
            String[] parts;
            for (String part : parts = path.split("/")) {
                if (!segments.add(part) || !pattern.matcher(part).matches()) continue;
                invalidPaths.add(path);
            }
        }
        if (invalidPaths.isEmpty()) {
            return;
        }
        this.error("Invalid file/directory names for Windows in JAR: %s. You can set the regular expression used with %s, the default expression is %s", invalidPaths, "-invalidfilenames", ReservedFileNames.pattern());
    }

    private void verifyImports() {
        if (this.isStrict()) {
            Report.Location location;
            Parameters map = this.parseHeader(this.manifest.getMainAttributes().getValue("Import-Package"));
            HashSet<String> noimports = new HashSet<String>();
            HashSet<String> toobroadimports = new HashSet<String>();
            for (Map.Entry<String, Attrs> e : map.entrySet()) {
                Report.Location location2;
                String version = e.getValue().get("version");
                if (version == null) {
                    if (e.getKey().startsWith("javax.")) continue;
                    noimports.add(e.getKey());
                    continue;
                }
                if (!VERSIONRANGE.matcher(version).matches()) {
                    Report.Location location3 = this.error("Import Package %s has an invalid version range syntax %s", e.getKey(), version).location();
                    location3.header = "Import-Package";
                    location3.context = e.getKey();
                    continue;
                }
                try {
                    VersionRange range = new VersionRange(version);
                    if (!range.isRange()) {
                        toobroadimports.add(e.getKey());
                    }
                    if (range.includeHigh() || range.includeLow() || !range.getLow().equals(range.getHigh())) continue;
                    location2 = this.error("Import Package %s has an empty version range syntax %s, likely want to use [%s,%s]", e.getKey(), version, range.getLow(), range.getHigh()).location();
                    location2.header = "Import-Package";
                    location2.context = e.getKey();
                }
                catch (Exception ee) {
                    location2 = this.exception(ee, "Import Package %s has an invalid version range syntax %s: %s", e.getKey(), version, ee).location();
                    location2.header = "Import-Package";
                    location2.context = e.getKey();
                }
            }
            if (!noimports.isEmpty()) {
                location = this.error("Import Package clauses without version range (excluding javax.*): %s", noimports).location();
                location.header = "Import-Package";
            }
            if (!toobroadimports.isEmpty()) {
                location = this.error("Import Package clauses which use a version instead of a version range. This imports EVERY later package and not as many expect until the next major number: %s", toobroadimports).location();
                location.header = "Import-Package";
            }
        }
    }

    private void verifyExports() {
        if (this.isStrict()) {
            Parameters map = this.parseHeader(this.manifest.getMainAttributes().getValue("Export-Package"));
            HashSet<String> noexports = new HashSet<String>();
            for (Map.Entry<String, Attrs> e : map.entrySet()) {
                String mandatory;
                Report.Location location;
                String version;
                if (!this.analyzer.getContained().containsFQN(e.getKey())) {
                    Reporter.SetLocation warning = this.warning("Export-Package or -exportcontents refers to missing package '%s'", e.getKey());
                    warning.header("Export-Package|-exportcontents");
                    warning.context(e.getKey());
                }
                if ((version = e.getValue().get("version")) == null) {
                    noexports.add(e.getKey());
                } else if (!VERSION.matcher(version).matches()) {
                    location = VERSIONRANGE.matcher(version).matches() ? this.error("Export Package %s version is a range: %s; Exports do not allow for ranges.", e.getKey(), version).location() : this.error("Export Package %s version has invalid syntax: %s", e.getKey(), version).location();
                    location.header = "Export-Package";
                    location.context = e.getKey();
                }
                if (e.getValue().containsKey("specification-version")) {
                    location = this.error("Export Package %s uses deprecated specification-version instead of version", e.getKey()).location();
                    location.header = "Export-Package";
                    location.context = e.getKey();
                }
                if ((mandatory = e.getValue().get("mandatory:")) == null) continue;
                HashSet<String> missing = new HashSet<String>(Verifier.split(mandatory));
                missing.removeAll(e.getValue().keySet());
                if (missing.isEmpty()) continue;
                Report.Location location2 = this.error("Export Package %s misses mandatory attribute: %s", e.getKey(), missing).location();
                location2.header = "Export-Package";
                location2.context = e.getKey();
            }
            if (!noexports.isEmpty()) {
                Report.Location location = this.error("Export Package clauses without version range: %s", noexports).location();
                location.header = "Export-Package";
            }
        }
    }

    private void verifyRequirements() throws IllegalArgumentException, Exception {
        Parameters map = this.parseHeader(this.manifest.getMainAttributes().getValue("Require-Capability"));
        for (String key : map.keySet()) {
            String verify;
            this.verifyNamespace(key, "Require");
            Attrs attrs = map.get(key);
            this.verify(attrs, "filter:", FILTERPATTERN, false, "Requirement %s filter not correct", key);
            String filter = attrs.get("filter:");
            if (filter != null && (verify = new Filter(filter).verify()) != null) {
                this.error("Invalid filter syntax in requirement %s=%s. Reason %s", key, attrs, verify);
            }
            this.verify(attrs, "cardinality:", CARDINALITY_PATTERN, false, "Requirement %s cardinality not correct", key);
            this.verify(attrs, "resolution:", RESOLUTION_PATTERN, false, "Requirement %s resolution not correct", key);
            if (!(key.equals("osgi.extender") || key.equals("osgi.serviceloader") || key.equals("osgi.contract") || key.equals("osgi.service") || key.equals("osgi.ee") || key.equals("osgi.native") || key.equals("osgi.identity") || !key.startsWith("osgi.wiring."))) {
                this.error("%s namespace must not be specified with generic requirements", key);
            }
            this.verifyAttrs(key, attrs);
            if (attrs.containsKey("mandatory:")) {
                this.error("%s directive is intended for Capabilities, not Requirement %s", "mandatory:", key);
            }
            if (!attrs.containsKey("uses:")) continue;
            this.error("%s directive is intended for Capabilities, not Requirement %s", "uses:", key);
        }
    }

    void verifyAttrs(String key, Attrs attrs) {
        for (String a : attrs.keySet()) {
            String v = attrs.get(a);
            if (a.endsWith(":")) continue;
            Attrs.Type t = attrs.getType(a);
            if ("version".equals(a)) {
                if (t == Attrs.Type.VERSION || t == Attrs.Type.VERSIONS) continue;
                this.error("Version attributes should always be of type Version or List<Version>, it is version:%s=%s for %s", new Object[]{t, v, key});
                continue;
            }
            this.verifyType(t, v);
        }
    }

    private void verifyCapabilities() {
        Parameters map = this.parseHeader(this.manifest.getMainAttributes().getValue("Provide-Capability"));
        for (String key : map.keySet()) {
            this.verifyNamespace(key, "Provide");
            Attrs attrs = map.get(key);
            this.verify(attrs, "cardinality:", CARDINALITY_PATTERN, false, "Capability %s cardinality not correct", key);
            this.verify(attrs, "resolution:", RESOLUTION_PATTERN, false, "Capability %s resolution not correct", key);
            if (key.equals("osgi.extender")) {
                this.verify(attrs, "osgi.extender", SYMBOLICNAME, true, "%s must have the %s attribute set", key, "osgi.extender");
                this.verify(attrs, "version", VERSION, true, "%s must have the %s attribute set", key, "version");
            } else if (key.equals("osgi.serviceloader")) {
                this.verify(attrs, "register:", PACKAGEPATTERN_OR_EMPTY, false, "Service Loader extender register: directive not a fully qualified Java name", new String[0]);
            } else if (key.equals("osgi.contract")) {
                this.verify(attrs, "osgi.contract", SYMBOLICNAME, true, "%s must have the %s attribute set", key, "osgi.contract");
            } else if (key.equals("osgi.service")) {
                this.verify(attrs, "objectClass", MULTIPACKAGEPATTERN, true, "%s must have the %s attribute set", key, "objectClass");
            } else if (key.startsWith("osgi.wiring.") || key.equals("osgi.identity") || key.equals("osgi.ee") || key.equals("osgi.native")) {
                this.error("%s namespace must not be specified with generic capabilities", key);
            }
            this.verifyAttrs(key, attrs);
            if (attrs.containsKey("filter:")) {
                this.error("%s directive is intended for Requirements, not Capability %s", "filter:", key);
            }
            if (attrs.containsKey("cardinality:")) {
                this.error("%s directive is intended for Requirements, not Capability %s", "cardinality:", key);
            }
            if (!attrs.containsKey("resolution:")) continue;
            this.error("%s directive is intended for Requirements, not Capability %s", "resolution:", key);
        }
    }

    private void verifyNamespace(String ns, String type) {
        if (!Verifier.isBsn(Processor.removeDuplicateMarker(ns))) {
            this.error("The %s-Capability with namespace %s is not a symbolic name", type, ns);
        }
    }

    private void verify(Attrs attrs, String ad, Pattern pattern, boolean mandatory, String msg, String ... args) {
        String v = attrs.get(ad);
        if (v == null) {
            if (mandatory) {
                this.error("Missing required attribute/directive %s", ad);
            }
        } else {
            Matcher m = pattern.matcher(v);
            if (!m.matches()) {
                this.error(msg, args);
            }
        }
    }

    private void verifyType(Attrs.Type type, String string) {
    }

    private void verifyDirectives(String header, String directives, Pattern namePattern, String type) {
        Pattern pattern = Pattern.compile(directives);
        Parameters map = this.parseHeader(this.manifest.getMainAttributes().getValue(header));
        for (Map.Entry<String, Attrs> entry : map.entrySet()) {
            String pname = Verifier.removeDuplicateMarker(entry.getKey());
            if (namePattern != null && !namePattern.matcher(pname).matches()) {
                if (this.isPedantic()) {
                    this.error("Invalid %s name: '%s'", type, pname);
                } else {
                    this.warning("Invalid %s name: '%s'", type, pname);
                }
            }
            for (String key : entry.getValue().keySet()) {
                Matcher m;
                if (!key.endsWith(":") || key.startsWith("x-") || (m = pattern.matcher(key)).matches()) continue;
                this.warning("Unknown directive %s in %s, allowed directives are %s, and 'x-*'.", key, header, directives.replace('|', ','));
            }
        }
    }

    private void verifyUses() {
    }

    public boolean verifyActivationPolicy() {
        String policy = this.main.get("Bundle-ActivationPolicy");
        if (policy == null) {
            return true;
        }
        return this.verifyActivationPolicy(policy);
    }

    public boolean verifyActivationPolicy(String policy) {
        Parameters map = this.parseHeader(policy);
        if (map.size() == 0) {
            this.warning("Bundle-ActivationPolicy is set but has no argument %s", policy);
        } else if (map.size() > 1) {
            this.warning("Bundle-ActivationPolicy has too many arguments %s", policy);
        } else {
            Attrs s = map.get("lazy");
            if (s == null) {
                this.warning("Bundle-ActivationPolicy set but is not set to lazy: %s", policy);
            } else {
                return true;
            }
        }
        return false;
    }

    public void verifyBundleClasspath() {
        Parameters bcp = this.main.getBundleClassPath();
        if (bcp.isEmpty() || bcp.containsKey(".")) {
            return;
        }
        for (String path : bcp.keySet()) {
            if (path.endsWith("/")) {
                this.error("A Bundle-ClassPath entry must not end with '/': %s", path);
            }
            if (!this.dot.getDirectories().containsKey(path)) continue;
            return;
        }
        for (String path : this.dot.getResources().keySet()) {
            if (!path.endsWith(".class")) continue;
            this.warning("The Bundle-ClassPath does not contain the actual bundle JAR (as specified with '.' in the Bundle-ClassPath) but the JAR does contain classes. Is this intentional?", new Object[0]);
            return;
        }
    }

    private void verifyDynamicImportPackage() {
        this.verifyListHeader("DynamicImport-Package", WILDCARDPACKAGE, true);
        String dynamicImportPackage = this.get("DynamicImport-Package");
        if (dynamicImportPackage == null) {
            return;
        }
        Parameters map = this.main.getDynamicImportPackage();
        for (String name : map.keySet()) {
            if (!Verifier.verify(name = name.trim(), WILDCARDPACKAGE)) {
                this.error("DynamicImport-Package header contains an invalid package name: %s", name);
            }
            Attrs sub = map.get(name);
            if (!this.r3 || sub.size() == 0) continue;
            this.error("DynamicPackage-Import has attributes on import: %s. This is however, an <=R3 bundle and attributes on this header were introduced in R4.", name);
        }
    }

    private void verifyManifestFirst() {
        if (!this.dot.isManifestFirst()) {
            this.error("Invalid JAR stream: Manifest should come first to be compatible with JarInputStream, it was not", new Object[0]);
        }
    }

    private void verifySymbolicName() {
        Parameters bsn = this.parseHeader(this.main.get("Bundle-SymbolicName"));
        if (!bsn.isEmpty()) {
            String name;
            if (bsn.size() > 1) {
                this.error("More than one BSN specified %s", bsn);
            }
            if (!Verifier.isBsn(name = bsn.keySet().iterator().next())) {
                this.error("Symbolic Name has invalid format: %s", name);
            }
        }
    }

    public static boolean isBsn(String name) {
        return SYMBOLICNAME.matcher(name).matches();
    }

    public static int verifyFilter(String expr, int index) {
        try {
            while (Character.isWhitespace(expr.charAt(index))) {
                ++index;
            }
            if (expr.charAt(index) != '(') {
                throw new IllegalArgumentException("Filter mismatch: expected ( at position " + index + " : " + expr);
            }
            ++index;
            while (Character.isWhitespace(expr.charAt(index))) {
                ++index;
            }
            switch (expr.charAt(index)) {
                case '!': {
                    ++index;
                    while (Character.isWhitespace(expr.charAt(index))) {
                        ++index;
                    }
                    if (expr.charAt(index) != '(') {
                        throw new IllegalArgumentException("Filter mismatch: ! (not) must have one sub expression " + index + " : " + expr);
                    }
                    while (Character.isWhitespace(expr.charAt(index))) {
                        ++index;
                    }
                    index = Verifier.verifyFilter(expr, index);
                    while (Character.isWhitespace(expr.charAt(index))) {
                        ++index;
                    }
                    if (expr.charAt(index) != ')') {
                        throw new IllegalArgumentException("Filter mismatch: expected ) at position " + index + " : " + expr);
                    }
                    return index + 1;
                }
                case '&': 
                case '|': {
                    ++index;
                    while (Character.isWhitespace(expr.charAt(index))) {
                        ++index;
                    }
                    while (expr.charAt(index) == '(') {
                        index = Verifier.verifyFilter(expr, index);
                        while (Character.isWhitespace(expr.charAt(index))) {
                            ++index;
                        }
                    }
                    if (expr.charAt(index) != ')') {
                        throw new IllegalArgumentException("Filter mismatch: expected ) at position " + index + " : " + expr);
                    }
                    return index + 1;
                }
            }
            index = Verifier.verifyFilterOperation(expr, index);
            if (expr.charAt(index) != ')') {
                throw new IllegalArgumentException("Filter mismatch: expected ) at position " + index + " : " + expr);
            }
            return index + 1;
        }
        catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Filter mismatch: early EOF from " + index);
        }
    }

    private static int verifyFilterOperation(String expr, int index) {
        StringBuilder sb = new StringBuilder();
        while ("=><~()".indexOf(expr.charAt(index)) < 0) {
            sb.append(expr.charAt(index++));
        }
        String attr = sb.toString().trim();
        if (attr.length() == 0) {
            throw new IllegalArgumentException("Filter mismatch: attr at index " + index + " is 0");
        }
        sb = new StringBuilder();
        while ("=><~".indexOf(expr.charAt(index)) >= 0) {
            sb.append(expr.charAt(index++));
        }
        String operator = sb.toString();
        if (!Verifier.verify(operator, FILTEROP)) {
            throw new IllegalArgumentException("Filter error, illegal operator " + operator + " at index " + index);
        }
        sb = new StringBuilder();
        while (")".indexOf(expr.charAt(index)) < 0) {
            switch (expr.charAt(index)) {
                case '\\': {
                    if ("\\)(*".indexOf(expr.charAt(index + 1)) >= 0) break;
                    throw new IllegalArgumentException("Filter error, illegal use of backslash at index " + index + ". Backslash may only be used before * or () or \\");
                }
            }
            int n = ++index;
            ++index;
            sb.append(expr.charAt(n));
        }
        return index;
    }

    private boolean verifyHeader(String name, Pattern regex, boolean error) {
        String value = this.manifest.getMainAttributes().getValue(name);
        if (value == null) {
            return false;
        }
        QuotedTokenizer st = new QuotedTokenizer(value.trim(), ",");
        Iterator<String> i = st.getTokenSet().iterator();
        while (i.hasNext()) {
            if (Verifier.verify(i.next(), regex)) continue;
            if (error) {
                this.error("Invalid value for %s, %s does not match %s", name, value, regex.pattern());
                continue;
            }
            this.warning("Invalid value for %s, %s does not match %s", name, value, regex.pattern());
        }
        return true;
    }

    private static boolean verify(String value, Pattern regex) {
        return regex.matcher(value).matches();
    }

    private boolean verifyListHeader(String name, Pattern regex, boolean error) {
        String value = this.manifest.getMainAttributes().getValue(name);
        if (value == null) {
            return false;
        }
        Parameters map = this.parseHeader(value);
        for (String header : map.keySet()) {
            if (regex.matcher(header).matches()) continue;
            if (error) {
                this.error("Invalid value for %s, %s does not match %s", name, value, regex.pattern());
                continue;
            }
            this.warning("Invalid value for %s, %s does not match %s", name, value, regex.pattern());
        }
        return true;
    }

    public static boolean isVersion(String version) {
        return version != null && VERSION.matcher(version).matches();
    }

    public static boolean isIdentifier(String value) {
        if (value.length() < 1) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(value.charAt(0))) {
            return false;
        }
        for (int i = 1; i < value.length(); ++i) {
            if (Character.isJavaIdentifierPart(value.charAt(i))) continue;
            return false;
        }
        return true;
    }

    public static boolean isMember(String value, String[] matches) {
        for (String match : matches) {
            if (!match.equals(value)) continue;
            return true;
        }
        return false;
    }

    public static boolean isFQN(String name) {
        if (name.length() == 0) {
            return false;
        }
        if (!Character.isJavaIdentifierStart(name.charAt(0))) {
            return false;
        }
        for (int i = 1; i < name.length(); ++i) {
            char c = name.charAt(i);
            if (Character.isJavaIdentifierPart(c) || c == '$' || c == '.') continue;
            return false;
        }
        return true;
    }

    public void verifyChecksums(boolean all) throws Exception {
        Manifest m = this.dot.getManifest();
        if (m == null || m.getEntries().isEmpty()) {
            if (all) {
                this.error("Verify checksums with all but no digests", new Object[0]);
            }
            return;
        }
        ArrayList<String> missingDigest = new ArrayList<String>();
        for (String path : this.dot.getResources().keySet()) {
            if (path.equals("META-INF/MANIFEST.MF")) continue;
            Attributes a = m.getAttributes(path);
            String digest = a.getValue("SHA1-Digest");
            if (digest == null) {
                if (path.matches("")) continue;
                missingDigest.add(path);
                continue;
            }
            byte[] d = Base64.decodeBase64(digest);
            SHA1 expected = new SHA1(d);
            Digester<SHA1> digester = SHA1.getDigester(new OutputStream[0]);
            Throwable throwable = null;
            try {
                InputStream in = this.dot.getResource(path).openInputStream();
                Throwable throwable2 = null;
                try {
                    IO.copy(in, digester);
                    digester.digest();
                    if (expected.equals(digester.digest())) continue;
                    this.error("Checksum mismatch %s, expected %s, got %s", path, expected, digester.digest());
                }
                catch (Throwable throwable3) {
                    throwable2 = throwable3;
                    throw throwable3;
                }
                finally {
                    if (in == null) continue;
                    if (throwable2 != null) {
                        try {
                            in.close();
                        }
                        catch (Throwable x2) {
                            throwable2.addSuppressed(x2);
                        }
                        continue;
                    }
                    in.close();
                }
            }
            catch (Throwable throwable4) {
                throwable = throwable4;
                throw throwable4;
            }
            finally {
                if (digester == null) continue;
                if (throwable != null) {
                    try {
                        digester.close();
                    }
                    catch (Throwable x2) {
                        throwable.addSuppressed(x2);
                    }
                    continue;
                }
                digester.close();
            }
        }
        if (missingDigest.size() > 0) {
            this.error("Entries in the manifest are missing digests: %s", missingDigest);
        }
    }

    public static boolean isExtended(String key) {
        if (key == null) {
            return false;
        }
        return EXTENDED_P.matcher(key).matches();
    }

    public static boolean isArgument(String arg) {
        return arg != null && ARGUMENT_P.matcher(arg).matches();
    }

    public static boolean isQuotedString(String s) {
        return s != null && QUOTEDSTRING_P.matcher(s).matches();
    }

    public static boolean isVersionRange(String range) {
        return range != null && VERSIONRANGE_P.matcher(range).matches();
    }

    public void verifyMetaPersistence() throws Exception {
        ArrayList<String> list = new ArrayList<String>();
        String mp = this.dot.getManifest().getMainAttributes().getValue("Meta-Persistence");
        for (String location : OSGiHeader.parseHeader(mp).keySet()) {
            String[] parts = location.split("!/");
            Resource resource = this.dot.getResource(parts[0]);
            if (resource == null) {
                list.add(location);
                continue;
            }
            if (parts.length <= 1) continue;
            try {
                Jar jar = new Jar("", resource.openInputStream());
                Throwable throwable = null;
                try {
                    if (jar.getResource(parts[1]) != null) continue;
                    list.add(location);
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    if (jar == null) continue;
                    if (throwable != null) {
                        try {
                            jar.close();
                        }
                        catch (Throwable x2) {
                            throwable.addSuppressed(x2);
                        }
                        continue;
                    }
                    jar.close();
                }
            }
            catch (Exception e) {
                list.add(location);
            }
        }
        if (list.isEmpty()) {
            return;
        }
        this.error("Meta-Persistence refers to resources not in the bundle: %s", list).header("Meta-Persistence");
    }

    public boolean isFrombuilder() {
        return this.frombuilder;
    }

    public void setFrombuilder(boolean frombuilder) {
        this.frombuilder = frombuilder;
    }

    public static enum ActivatorErrorType {
        IS_INTERFACE,
        IS_ABSTRACT,
        NOT_PUBLIC,
        NO_SUITABLE_CONSTRUCTOR,
        NOT_AN_ACTIVATOR,
        DEFAULT_PACKAGE,
        NOT_ACCESSIBLE,
        IS_IMPORTED,
        NOT_SET,
        NO_RESULT_FROM_MACRO,
        MULTIPLE_TYPES,
        INVALID_TYPE_NAME;

    }

    public static class BundleActivatorError
    extends DTO {
        public final String activatorClassName;
        public final ActivatorErrorType errorType;

        public BundleActivatorError(String activatorName, ActivatorErrorType type) {
            this.activatorClassName = activatorName;
            this.errorType = type;
        }
    }

    static class EE {
        String name;
        int target;

        EE(String name, int source, int target) {
            this.name = name;
            this.target = target;
        }

        public String toString() {
            return this.name + "(" + this.target + ")";
        }
    }
}

