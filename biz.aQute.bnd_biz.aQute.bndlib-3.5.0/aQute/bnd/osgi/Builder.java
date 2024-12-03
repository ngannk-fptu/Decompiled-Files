/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.osgi;

import aQute.bnd.component.DSAnnotations;
import aQute.bnd.differ.DiffPluginImpl;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.make.Make;
import aQute.bnd.make.MakeBnd;
import aQute.bnd.make.MakeCopy;
import aQute.bnd.make.component.ServiceComponent;
import aQute.bnd.make.metatype.MetatypePlugin;
import aQute.bnd.maven.PomPropertiesResource;
import aQute.bnd.maven.PomResource;
import aQute.bnd.metatype.MetatypeAnnotations;
import aQute.bnd.osgi.About;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.CombinedResource;
import aQute.bnd.osgi.CommandResource;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.EmbeddedResource;
import aQute.bnd.osgi.FileResource;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.JarResource;
import aQute.bnd.osgi.Macro;
import aQute.bnd.osgi.Packages;
import aQute.bnd.osgi.PermissionGenerator;
import aQute.bnd.osgi.PreprocessResource;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.bnd.osgi.Verifier;
import aQute.bnd.service.SignerPlugin;
import aQute.bnd.service.diff.Delta;
import aQute.bnd.service.diff.Diff;
import aQute.bnd.service.diff.Tree;
import aQute.bnd.service.diff.Type;
import aQute.bnd.version.Version;
import aQute.lib.collections.MultiMap;
import aQute.lib.hex.Hex;
import aQute.lib.io.IO;
import aQute.libg.generics.Create;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Builder
extends Analyzer {
    private static final Logger logger = LoggerFactory.getLogger(Builder.class);
    static Pattern IR_PATTERN = Pattern.compile("[{]?-?@?(?:[^=]+=)?\\s*([^}!]+).*");
    private final DiffPluginImpl differ = new DiffPluginImpl();
    private Pattern xdoNotCopy = null;
    private static final int SPLIT_MERGE_LAST = 1;
    private static final int SPLIT_MERGE_FIRST = 2;
    private static final int SPLIT_ERROR = 3;
    private static final int SPLIT_FIRST = 4;
    private static final int SPLIT_DEFAULT = 0;
    private final List<File> sourcePath = new ArrayList<File>();
    private final Make make = new Make(this);
    private Instructions defaultPreProcessMatcher = null;
    boolean firstUse = true;
    private Tree tree;
    static MakeBnd makeBnd = new MakeBnd();
    static MakeCopy makeCopy = new MakeCopy();
    static ServiceComponent serviceComponent = new ServiceComponent();
    static DSAnnotations dsAnnotations = new DSAnnotations();
    static MetatypePlugin metatypePlugin = new MetatypePlugin();
    static MetatypeAnnotations metatypeAnnotations = new MetatypeAnnotations();
    static Pattern GITREF_P = Pattern.compile("ref:\\s*(refs/(heads|tags|remotes)/([^\\s]+))\\s*");
    static String _githeadHelp = "${githead}, provide the SHA for the current git head";

    public Builder(Processor parent) {
        super(parent);
    }

    public Builder() {
    }

    public Jar build() throws Exception {
        Jar dot;
        logger.debug("build");
        this.init();
        if (Builder.isTrue(this.getProperty("-nobundles"))) {
            return null;
        }
        if (this.getProperty("-conduit") != null) {
            this.error("Specified -conduit but calls build() instead of builds() (might be a programmer error", new Object[0]);
        }
        if ((dot = this.getJar()) == null) {
            dot = new Jar("dot");
            this.setJar(dot);
        }
        try {
            long modified = Long.parseLong(this.getProperty("base.modified"));
            dot.updateModified(modified, "Base modified");
        }
        catch (Exception e) {
            // empty catch block
        }
        this.doExpand(dot);
        this.doIncludeResources(dot);
        this.doWab(dot);
        Manifest manifest = this.calcManifest();
        String mf = this.getProperty("-manifest");
        if (mf != null) {
            File mff = this.getFile(mf);
            if (mff.isFile()) {
                this.updateModified(mff.lastModified(), "Manifest " + mff);
                try (InputStream in = IO.stream(mff);){
                    manifest = new Manifest(in);
                }
                catch (Exception e) {
                    this.exception(e, "%s: exception while reading manifest file", "-manifest");
                }
            } else {
                this.error("%s: no such file %s", "-manifest", mf);
            }
        }
        if (!Builder.isTrue(this.getProperty("-nomanifest"))) {
            dot.setManifest(manifest);
            String manifestName = this.getProperty("-manifest-name");
            if (manifestName != null) {
                dot.setManifestName(manifestName);
            }
        } else {
            dot.setDoNotTouchManifest();
        }
        this.addSources(dot);
        this.doPom(dot);
        if (!this.isNoBundle()) {
            this.doVerify(dot);
        }
        if (dot.getResources().isEmpty()) {
            this.warning("The JAR is empty: The instructions for the JAR named %s did not cause any content to be included, this is likely wrong", this.getBsn());
        }
        dot.updateModified(this.lastModified(), "Last Modified Processor");
        dot.setName(this.getBsn());
        this.doDigests(dot);
        this.sign(dot);
        this.doSaveManifest(dot);
        this.doDiff(dot);
        this.doBaseline(dot);
        String expand = this.getProperty("-expand");
        if (expand != null) {
            File out = this.getFile(expand);
            IO.mkdirs(out);
            dot.expand(out);
        }
        return dot;
    }

    void doPom(Jar dot) throws Exception, IOException {
        try (Processor scoped = new Processor(this);){
            String pom;
            String version;
            String bsn = this.getBsn();
            if (bsn != null) {
                scoped.setProperty("@bsn", bsn);
            }
            if ((version = this.getBundleVersion()) != null) {
                scoped.setProperty("@version", version);
            }
            if ((pom = scoped.getProperty("-pom")) != null && !pom.equalsIgnoreCase("false")) {
                dot.removePrefix("META-INF/maven/");
                scoped.addProperties(OSGiHeader.parseProperties(pom));
                PomResource pomXml = new PomResource(scoped, dot.getManifest());
                String v = pomXml.validate();
                if (v != null) {
                    this.error("Invalid pom for %s: %s", this.getBundleSymbolicName(), v);
                }
                PomPropertiesResource pomProperties = new PomPropertiesResource(pomXml);
                dot.putResource(pomXml.getWhere(), pomXml);
                if (!pomProperties.getWhere().equals(pomXml.getWhere())) {
                    dot.putResource(pomProperties.getWhere(), pomProperties);
                }
            }
        }
    }

    private void doDigests(Jar dot) throws Exception {
        Parameters ps = OSGiHeader.parseHeader(this.getProperty("-digests"));
        if (ps.isEmpty()) {
            return;
        }
        logger.debug("digests {}", (Object)ps);
        String[] digests = ps.keySet().toArray(new String[0]);
        dot.setDigestAlgorithms(digests);
    }

    public void init() throws Exception {
        this.begin();
        this.doRequireBnd();
        if (this.getClasspath().size() == 0 && (this.getProperty("Export-Package") != null || this.getProperty("Export-Package") != null || this.getProperty("Private-Package") != null || this.getProperty("-privatepackage") != null)) {
            this.warning("Classpath is empty. Private-Package (-privatepackage) and Export-Package can only expand from the classpath when there is one", new Object[0]);
        }
    }

    private Jar doWab(Jar dot) throws Exception {
        String wab = this.getProperty("-wab");
        String wablib = this.getProperty("-wablib");
        if (wab == null && wablib == null) {
            return dot;
        }
        logger.debug("wab {} {}", (Object)wab, (Object)wablib);
        this.setBundleClasspath(Builder.append("WEB-INF/classes", this.getProperty("Bundle-ClassPath")));
        HashSet<String> paths = new HashSet<String>(dot.getResources().keySet());
        for (String path : paths) {
            if (path.indexOf(47) <= 0 || Character.isUpperCase(path.charAt(0))) continue;
            logger.debug("wab: moving: {}", (Object)path);
            dot.rename(path, "WEB-INF/classes/" + path);
        }
        Parameters clauses = this.parseHeader(this.getProperty("-wablib"));
        for (String key : clauses.keySet()) {
            File f = this.getFile(key);
            this.addWabLib(dot, f);
        }
        this.doIncludeResource(dot, wab);
        return dot;
    }

    private void addWabLib(Jar dot, File f) throws Exception {
        if (f.exists()) {
            String cp;
            Jar jar = new Jar(f);
            jar.setDoNotTouchManifest();
            this.addClose(jar);
            String path = "WEB-INF/lib/" + f.getName();
            dot.putResource(path, new JarResource(jar));
            this.setProperty("Bundle-ClassPath", Builder.append(this.getProperty("Bundle-ClassPath"), path));
            Manifest m = jar.getManifest();
            if (m != null && (cp = m.getMainAttributes().getValue("Class-Path")) != null) {
                Collection<String> parts = Builder.split(cp, ",");
                for (String part : parts) {
                    File sub = Builder.getFile(f.getParentFile(), part);
                    if (!sub.exists() || !sub.getParentFile().equals(f.getParentFile())) {
                        this.warning("Invalid Class-Path entry %s in %s, must exist and must reside in same directory", sub, f);
                        continue;
                    }
                    this.addWabLib(dot, sub);
                }
            }
        } else {
            this.error("WAB lib does not exist %s", f);
        }
    }

    private void doSaveManifest(Jar dot) throws Exception {
        String output = this.getProperty("-savemanifest");
        if (output == null) {
            return;
        }
        File f = this.getFile(output);
        if (f.isDirectory()) {
            f = new File(f, "MANIFEST.MF");
        }
        if (!f.exists() || f.lastModified() < dot.lastModified()) {
            IO.delete(f);
            File fp = f.getParentFile();
            IO.mkdirs(fp);
            try (OutputStream out = IO.outputStream(f);){
                Jar.writeManifest(dot.getManifest(), out);
            }
            this.changedFile(f);
        }
    }

    protected void changedFile(File f) {
    }

    void sign(Jar jar) throws Exception {
        String signing = this.getProperty("-sign");
        if (signing == null) {
            return;
        }
        logger.debug("Signing {}, with {}", (Object)this.getBsn(), (Object)signing);
        List<SignerPlugin> signers = this.getPlugins(SignerPlugin.class);
        Parameters infos = this.parseHeader(signing);
        for (Map.Entry<String, Attrs> entry : infos.entrySet()) {
            for (SignerPlugin signer : signers) {
                signer.sign(this, entry.getKey());
            }
        }
    }

    public boolean hasSources() {
        return Builder.isTrue(this.getProperty("-sources"));
    }

    @Override
    protected Jar getExtra() throws Exception {
        Parameters conditionals = this.getMergedParameters("Conditional-Package");
        conditionals.putAll(this.getMergedParameters("-conditionalpackage"));
        if (conditionals.isEmpty()) {
            return null;
        }
        logger.debug("do Conditional Package {}", (Object)conditionals);
        Instructions instructions = new Instructions(conditionals);
        Collection<Descriptors.PackageRef> referred = instructions.select(this.getReferred().keySet(), false);
        referred.removeAll(this.getContained().keySet());
        if (referred.isEmpty()) {
            logger.debug("no additional conditional packages to add");
            return null;
        }
        Jar jar = new Jar("conditional-import");
        this.addClose(jar);
        block0: for (Descriptors.PackageRef pref : referred) {
            for (Jar cpe : this.getClasspath()) {
                Map<String, Resource> map = cpe.getDirectories().get(pref.getPath());
                if (map == null) continue;
                this.copy(jar, cpe, pref.getPath(), false);
                continue block0;
            }
        }
        if (jar.getDirectories().size() == 0) {
            logger.debug("extra dirs {}", jar.getDirectories());
            return null;
        }
        return jar;
    }

    @Override
    public void analyze() throws Exception {
        super.analyze();
        this.cleanupVersion(this.getImports(), null);
        this.cleanupVersion(this.getExports(), this.getVersion());
        String version = this.getProperty("Bundle-Version");
        if (version != null) {
            version = Builder.cleanupVersion(version);
            version = this.doSnapshot(version);
            this.setProperty("Bundle-Version", version);
        }
    }

    private String doSnapshot(String version) {
        Version v;
        String q;
        String snapshot = this.getProperty("-snapshot");
        if (snapshot == null) {
            return version;
        }
        if (snapshot.isEmpty()) {
            snapshot = null;
        }
        if ((q = (v = Version.parseVersion(version)).getQualifier()) == null) {
            return version;
        }
        if (q.equals("SNAPSHOT")) {
            q = snapshot;
        } else if (q.endsWith("-SNAPSHOT")) {
            int end = q.length() - "SNAPSHOT".length();
            q = snapshot == null ? q.substring(0, end - 1) : q.substring(0, end) + snapshot;
        } else {
            return version;
        }
        return new Version(v.getMajor(), v.getMinor(), v.getMicro(), q).toString();
    }

    public void cleanupVersion(Packages packages, String defaultVersion) {
        Matcher m;
        if (defaultVersion != null && (m = Verifier.VERSION.matcher(defaultVersion)).matches()) {
            defaultVersion = Version.parseVersion(defaultVersion).getWithoutQualifier().toString();
        }
        for (Map.Entry<Descriptors.PackageRef, Attrs> entry : packages.entrySet()) {
            Attrs attributes = entry.getValue();
            String v = attributes.get("version");
            if (v == null && defaultVersion != null) {
                if (!Builder.isTrue(this.getProperty("-nodefaultversion"))) {
                    v = defaultVersion;
                    if (this.isPedantic()) {
                        this.warning("Used bundle version %s for exported package %s", v, entry.getKey());
                    }
                } else if (this.isPedantic()) {
                    this.warning("No export version for exported package %s", entry.getKey());
                }
            }
            if (v == null) continue;
            attributes.put("version", Builder.cleanupVersion(v));
        }
    }

    private void addSources(Jar dot) throws Exception {
        if (!this.hasSources()) {
            return;
        }
        Set packages = Create.set();
        for (Descriptors.TypeRef typeRef : this.getClassspace().keySet()) {
            Descriptors.PackageRef packageRef = typeRef.getPackageRef();
            String sourcePath = typeRef.getSourcePath();
            String packagePath = packageRef.getPath();
            boolean found = false;
            String[] fixed = new String[]{"packageinfo", "package.html", "module-info.java", "package-info.java"};
            for (File root : this.getSourcePath()) {
                File f = Builder.getFile(root, sourcePath);
                if (!f.exists()) continue;
                found = true;
                if (!packages.contains(packageRef)) {
                    packages.add(packageRef);
                    block2: for (int j = 0; j < fixed.length; ++j) {
                        for (File sp : this.getSourcePath()) {
                            File bdir = Builder.getFile(sp, packagePath);
                            File ff = Builder.getFile(bdir, fixed[j]);
                            if (!ff.isFile()) continue;
                            String name = "OSGI-OPT/src/" + packagePath + "/" + fixed[j];
                            dot.putResource(name, new FileResource(ff));
                            continue block2;
                        }
                    }
                }
                if (packageRef.isDefaultPackage()) {
                    logger.debug("Package reference is default package");
                }
                dot.putResource("OSGI-OPT/src/" + sourcePath, new FileResource(f));
            }
            if (!this.getSourcePath().isEmpty()) continue;
            this.warning("Including sources but -sourcepath does not contain any source directories ", new Object[0]);
        }
    }

    public Collection<File> getSourcePath() {
        if (this.firstUse) {
            this.firstUse = false;
            String sp = this.mergeProperties("-sourcepath");
            if (sp != null) {
                Parameters map = this.parseHeader(sp);
                for (String file : map.keySet()) {
                    if (Builder.isDuplicate(file)) continue;
                    File f = this.getFile(file);
                    if (!f.isDirectory()) {
                        this.error("Adding a sourcepath that is not a directory: %s", f).header("-sourcepath").context(file);
                        continue;
                    }
                    this.sourcePath.add(f);
                }
            }
        }
        return this.sourcePath;
    }

    private void doVerify(Jar dot) throws Exception {
        try (Verifier verifier = new Verifier(this);){
            verifier.setFrombuilder(true);
            verifier.verify();
            this.getInfo(verifier);
        }
    }

    private void doExpand(Jar dot) throws Exception {
        Parameters exportedPackage;
        Instructions privateFilter;
        Set<Instruction> unused;
        MultiMap<String, Jar> packages = new MultiMap<String, Jar>();
        for (Jar srce : this.getClasspath()) {
            dot.updateModified(srce.lastModified, srce + " (" + srce.lastModifiedReason + ")");
            for (Map.Entry<String, Map<String, Resource>> e : srce.getDirectories().entrySet()) {
                if (e.getValue() == null) continue;
                packages.add(e.getKey(), srce);
            }
        }
        Parameters privatePackages = this.getPrivatePackage();
        if (Builder.isTrue(this.getProperty("-undertest"))) {
            String h = this.mergeProperties("-testpackages", "test;presence:=optional");
            privatePackages.putAll(this.parseHeader(h));
        }
        if (!privatePackages.isEmpty() && !(unused = this.doExpand(dot, packages, privateFilter = new Instructions(privatePackages))).isEmpty()) {
            this.warning("Unused Private-Package instructions, no such package(s) on the class path: %s", unused).header("Private-Package").context(unused.iterator().next().input);
        }
        if (!(exportedPackage = this.getExportPackage()).isEmpty()) {
            Instructions exportedFilter = new Instructions(exportedPackage);
            this.doExpand(dot, packages, exportedFilter);
        }
    }

    private Set<Instruction> doExpand(Jar jar, MultiMap<String, Jar> index, Instructions filter) throws Exception {
        Set<Instruction> unused = Create.set();
        for (Map.Entry<Instruction, Attrs> e : filter.entrySet()) {
            Instruction instruction = e.getKey();
            if (instruction.isDuplicate()) continue;
            Attrs directives = e.getValue();
            Instruction from = new Instruction(directives.get("from:", "*"));
            boolean used = false;
            Iterator entry = index.entrySet().iterator();
            while (entry.hasNext()) {
                Map.Entry p = entry.next();
                String directory = (String)p.getKey();
                Descriptors.PackageRef packageRef = this.getPackageRef(directory);
                if (packageRef.isMetaData() && instruction.isAny() || !instruction.matches(packageRef.getFQN())) continue;
                entry.remove();
                if (instruction.isNegated()) {
                    used = true;
                    continue;
                }
                List<Jar> providers = this.filterFrom(from, (List)p.getValue());
                if (providers.isEmpty()) continue;
                int splitStrategy = this.getSplitStrategy(directives.get("-split-package:"));
                this.copyPackage(jar, providers, directory, splitStrategy);
                Attrs contained = this.getContained().put(packageRef);
                contained.put("-internal-source:", this.getName(providers.get(0)));
                used = true;
            }
            if (used || Builder.isTrue(directives.get("optional:"))) continue;
            unused.add(instruction);
        }
        return unused;
    }

    private List<Jar> filterFrom(Instruction from, List<Jar> providers) {
        if (from.isAny()) {
            return providers;
        }
        ArrayList<Jar> np = new ArrayList<Jar>();
        for (Jar j : providers) {
            if (!from.matches(j.getName())) continue;
            np.add(j);
        }
        return np;
    }

    private void copyPackage(Jar dest, List<Jar> providers, String path, int splitStrategy) {
        switch (splitStrategy) {
            case 1: {
                for (Jar srce : providers) {
                    this.copy(dest, srce, path, true);
                }
                break;
            }
            case 2: {
                for (Jar srce : providers) {
                    this.copy(dest, srce, path, false);
                }
                break;
            }
            case 3: {
                this.error("%s", this.diagnostic(path, providers));
                break;
            }
            case 4: {
                this.copy(dest, providers.get(0), path, false);
                break;
            }
            default: {
                if (providers.size() > 1) {
                    this.warning("%s", this.diagnostic(path, providers));
                }
                for (Jar srce : providers) {
                    this.copy(dest, srce, path, false);
                }
            }
        }
    }

    private void copy(Jar dest, Jar srce, String path, boolean overwrite) {
        String bndInfoPath;
        Resource r;
        logger.debug("copy d={} s={} p={}", new Object[]{dest, srce, path});
        dest.copy(srce, path, overwrite);
        if (this.hasSources()) {
            dest.copy(srce, "OSGI-OPT/src/" + path, overwrite);
        }
        if ((r = dest.getResource(bndInfoPath = path + "/bnd.info")) != null && !(r instanceof PreprocessResource)) {
            logger.debug("preprocessing bnd.info");
            PreprocessResource pp = new PreprocessResource(this, r);
            dest.putResource(bndInfoPath, pp);
        }
        if (this.hasSources()) {
            String srcPath = "OSGI-OPT/src/" + path;
            Map<String, Resource> srcContents = srce.getDirectories().get(srcPath);
            if (srcContents != null) {
                dest.addDirectory(srcContents, overwrite);
            }
        }
    }

    private String diagnostic(String pack, List<Jar> culprits) {
        return "Split package, multiple jars provide the same package:" + pack + "\nUse Import/Export Package directive -split-package:=(merge-first|merge-last|error|first) to get rid of this warning\n" + "Package found in   " + culprits + "\n" + "Class path         " + this.getClasspath();
    }

    private int getSplitStrategy(String type) {
        if (type == null) {
            return 0;
        }
        if (type.equals("merge-last")) {
            return 1;
        }
        if (type.equals("merge-first")) {
            return 2;
        }
        if (type.equals("error")) {
            return 3;
        }
        if (type.equals("first")) {
            return 4;
        }
        this.error("Invalid strategy for split-package: %s", type);
        return 0;
    }

    private Instruction matches(Instructions instructions, String pack, Set<Instruction> unused, String source) {
        for (Map.Entry<Instruction, Attrs> entry : instructions.entrySet()) {
            Instruction f;
            Instruction pattern = entry.getKey();
            String from = entry.getValue().get("from:");
            if (from != null && (!(f = new Instruction(from)).matches(source) || f.isNegated()) || !pattern.matches(pack)) continue;
            if (unused != null) {
                unused.remove(pattern);
            }
            return pattern;
        }
        return null;
    }

    private void doIncludeResources(Jar jar) throws Exception {
        String includes = this.getProperty("Bundle-Includes");
        if (includes == null) {
            includes = this.mergeProperties("-includeresource");
            if (includes == null || includes.length() == 0) {
                includes = this.mergeProperties("Include-Resource");
            }
        } else {
            this.warning("Please use -includeresource instead of Bundle-Includes", new Object[0]);
        }
        this.doIncludeResource(jar, includes);
    }

    private void doIncludeResource(Jar jar, String includes) throws Exception {
        Parameters clauses = this.parseHeader(includes);
        this.doIncludeResource(jar, clauses);
    }

    private void doIncludeResource(Jar jar, Parameters clauses) throws ZipException, IOException, Exception {
        for (Map.Entry<String, Attrs> entry : clauses.entrySet()) {
            String key = Builder.removeDuplicateMarker(entry.getKey());
            this.doIncludeResource(jar, key, entry.getValue());
        }
    }

    private void doIncludeResource(Jar jar, String name, Map<String, String> extra) throws ZipException, IOException, Exception {
        Instructions preprocess = null;
        boolean absentIsOk = false;
        if (name.startsWith("{") && name.endsWith("}")) {
            preprocess = this.getPreProcessMatcher(extra);
            name = name.substring(1, name.length() - 1).trim();
        }
        String[] parts = name.split("\\s*=\\s*");
        String source = parts[0];
        String destination = parts[0];
        if (parts.length == 2) {
            source = parts[1];
        }
        if (source.startsWith("-")) {
            source = source.substring(1);
            absentIsOk = true;
        }
        if (source.startsWith("@")) {
            this.extractFromJar(jar, source.substring(1), parts.length == 1 ? "" : destination, absentIsOk);
        } else if (extra.containsKey("cmd")) {
            this.doCommand(jar, source, destination, extra, preprocess, absentIsOk);
        } else if (extra.containsKey("literal")) {
            String literal = extra.get("literal");
            EmbeddedResource r = new EmbeddedResource(literal.getBytes(StandardCharsets.UTF_8), 0L);
            String x = extra.get("extra");
            if (x != null) {
                r.setExtra(x);
            }
            this.copy(jar, name, r, extra);
        } else {
            File sourceFile = this.getFile(source);
            String destinationPath = parts.length == 1 ? (sourceFile.isDirectory() ? "" : sourceFile.getName()) : parts[0];
            if (sourceFile.isDirectory()) {
                destinationPath = this.doResourceDirectory(jar, extra, preprocess, sourceFile, destinationPath);
                return;
            }
            if (!sourceFile.exists()) {
                if (absentIsOk) {
                    return;
                }
                this.noSuchFile(jar, name, extra, source, destinationPath);
            } else {
                this.copy(jar, destinationPath, sourceFile, preprocess, extra);
            }
        }
    }

    private Instructions getPreProcessMatcher(Map<String, String> extra) {
        if (this.defaultPreProcessMatcher == null) {
            String preprocessmatchers = this.mergeProperties("-preprocessmatchers");
            if (preprocessmatchers == null || preprocessmatchers.trim().length() == 0) {
                preprocessmatchers = "!*.(ico|jpg|jpeg|jif|jfif|jp2|jpx|j2k|j2c|fpx|png|gif|swf|doc|pdf|tiff|tif|raw|bmp|ppm|pgm|pbm|pnm|pfm|webp|zip|jar|gz|tar|tgz|exe|com|bin|mp[0-9]|mpeg|mov|):i, *";
            }
            this.defaultPreProcessMatcher = new Instructions(preprocessmatchers);
        }
        if (extra == null) {
            return this.defaultPreProcessMatcher;
        }
        String additionalMatchers = extra.get("-preprocessmatchers");
        if (additionalMatchers == null) {
            return this.defaultPreProcessMatcher;
        }
        Instructions specialMatcher = new Instructions(additionalMatchers);
        specialMatcher.putAll(this.defaultPreProcessMatcher);
        return specialMatcher;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void doCommand(Jar jar, String source, String destination, Map<String, String> extra, Instructions preprocess, boolean absentIsOk) throws Exception {
        String repeat = extra.get("for");
        if (repeat == null) {
            repeat = source;
        }
        Collection<String> requires = Builder.split(extra.get("requires"));
        long lastModified = 0L;
        for (String required : requires) {
            File file = this.getFile(required);
            if (!file.exists()) {
                this.error("Include-Resource.cmd for %s, requires %s, but no such file %s", source, required, file.getAbsoluteFile()).header("-includeresource|Include-Resource");
                continue;
            }
            lastModified = this.findLastModifiedWhileOlder(file, this.lastModified());
        }
        String cmd = extra.get("cmd");
        ArrayList<String> paths = new ArrayList<String>();
        for (String item : Processor.split(repeat)) {
            File f = IO.getFile(item);
            this.traverse(paths, f);
        }
        CombinedResource cr = null;
        if (!destination.contains("${@}")) {
            cr = new CombinedResource();
            cr.lastModified = lastModified;
        }
        this.setProperty("@requires", Builder.join(requires, " "));
        try {
            for (String item : paths) {
                this.setProperty("@", item);
                try {
                    CommandResource cmdresource;
                    String path = this.getReplacer().process(destination);
                    String command = this.getReplacer().process(cmd);
                    File file = this.getFile(item);
                    if (file.exists()) {
                        lastModified = Math.max(lastModified, file.lastModified());
                    }
                    Resource r = cmdresource = new CommandResource(command, this, lastModified, this.getBase());
                    FileResource fr = new FileResource(r);
                    this.addClose(fr);
                    r = fr;
                    if (preprocess != null && preprocess.matches(path)) {
                        r = new PreprocessResource(this, r);
                    }
                    if (cr == null) {
                        jar.putResource(path, r);
                        continue;
                    }
                    cr.addResource(r);
                }
                finally {
                    this.unsetProperty("@");
                }
            }
        }
        finally {
            this.unsetProperty("@requires");
        }
        if (cr != null) {
            jar.putResource(destination, cr);
        }
        this.updateModified(lastModified, "Include-Resource: cmd");
    }

    private void traverse(List<String> paths, File item) {
        if (item.isDirectory()) {
            for (File sub : item.listFiles()) {
                this.traverse(paths, sub);
            }
        } else if (item.isFile()) {
            paths.add(item.getAbsolutePath());
        } else {
            paths.add(item.getName());
        }
    }

    private long findLastModifiedWhileOlder(File file, long lastModified) {
        if (file.isDirectory()) {
            File[] children;
            for (File child : children = file.listFiles()) {
                if (child.lastModified() > lastModified) {
                    return child.lastModified();
                }
                long lm = this.findLastModifiedWhileOlder(child, lastModified);
                if (lm <= lastModified) continue;
                return lm;
            }
        }
        return file.lastModified();
    }

    private String doResourceDirectory(Jar jar, Map<String, String> extra, Instructions preprocess, File sourceFile, String destinationPath) throws Exception {
        String filter = extra.get("filter:");
        boolean flatten = Builder.isTrue(extra.get("flatten:"));
        boolean recursive = true;
        String directive = extra.get("recursive:");
        if (directive != null) {
            recursive = Builder.isTrue(directive);
        }
        Instruction.Filter iFilter = null;
        iFilter = filter != null ? new Instruction.Filter(new Instruction(filter), recursive, this.getDoNotCopy()) : new Instruction.Filter(null, recursive, this.getDoNotCopy());
        Map<String, File> files = Builder.newMap();
        this.resolveFiles(sourceFile, iFilter, recursive, destinationPath, files, flatten);
        for (Map.Entry<String, File> entry : files.entrySet()) {
            this.copy(jar, entry.getKey(), entry.getValue(), preprocess, extra);
        }
        return destinationPath;
    }

    private void resolveFiles(File dir, FileFilter filter, boolean recursive, String path, Map<String, File> files, boolean flatten) {
        File[] fs;
        if (this.doNotCopy(dir)) {
            return;
        }
        for (File file : fs = dir.listFiles(filter)) {
            if (file.isDirectory()) {
                if (!recursive) continue;
                String nextPath = flatten ? path : Builder.appendPath(path, file.getName());
                this.resolveFiles(file, filter, recursive, nextPath, files, flatten);
                continue;
            }
            String p = Builder.appendPath(path, file.getName());
            if (files.containsKey(p)) {
                this.warning("Include-Resource overwrites entry %s from file %s", p, file);
            }
            files.put(p, file);
        }
        if (fs.length == 0) {
            File empty = new File(dir, "<<EMPTY>>");
            files.put(Builder.appendPath(path, empty.getName()), empty);
        }
    }

    private void noSuchFile(Jar jar, String clause, Map<String, String> extra, String source, String destinationPath) throws Exception {
        List<Jar> src = this.getJarsFromName(source, "Include-Resource " + source);
        if (!src.isEmpty()) {
            for (Jar j : src) {
                String quoted = j.getSource() != null ? j.getSource().getName() : j.getName();
                j.setDoNotTouchManifest();
                JarResource jarResource = new JarResource(j);
                String path = destinationPath.replace(source, quoted);
                logger.debug("copy d={} s={} path={}", new Object[]{jar, j, path});
                this.copy(jar, path, jarResource, extra);
            }
        } else {
            Resource lastChance = this.make.process(source);
            if (lastChance != null) {
                String x = extra.get("extra");
                if (x != null) {
                    lastChance.setExtra(x);
                }
                this.copy(jar, destinationPath, lastChance, extra);
            } else {
                this.error("Input file does not exist: %s", source).header(source).context(clause);
            }
        }
    }

    private void extractFromJar(Jar jar, String source, String destination, boolean absentIsOk) throws ZipException, IOException {
        List<Jar> sub;
        int n = source.lastIndexOf("!/");
        Instruction instr = null;
        if (n > 0) {
            instr = new Instruction(source.substring(n + 2));
            source = source.substring(0, n);
        }
        if ((sub = this.getJarsFromName(source, "extract from jar")).isEmpty()) {
            if (absentIsOk) {
                return;
            }
            this.error("Can not find JAR file '%s'", source);
        } else {
            for (Jar j : sub) {
                this.addAll(jar, j, instr, destination);
            }
        }
    }

    public boolean addAll(Jar to, Jar sub, Instruction filter) {
        return this.addAll(to, sub, filter, "");
    }

    public boolean addAll(Jar to, Jar sub, Instruction filter, String destination) {
        boolean dupl = false;
        for (String name : sub.getResources().keySet()) {
            if ("META-INF/MANIFEST.MF".equals(name) || filter != null && filter.matches(name) == filter.isNegated()) continue;
            dupl |= to.putResource(Processor.appendPath(destination, name), sub.getResource(name), true);
        }
        return dupl;
    }

    private void copy(Jar jar, String path, File from, Instructions preprocess, Map<String, String> extra) throws Exception {
        if (this.doNotCopy(from)) {
            return;
        }
        logger.debug("copy d={} s={} path={}", new Object[]{jar, from, path});
        if (from.isDirectory()) {
            File[] files = from.listFiles();
            for (int i = 0; i < files.length; ++i) {
                this.copy(jar, Builder.appendPath(path, files[i].getName()), files[i], preprocess, extra);
            }
        } else if (from.exists()) {
            String x;
            Resource resource = new FileResource(from);
            if (preprocess != null && preprocess.matches(path)) {
                resource = new PreprocessResource(this, resource);
            }
            if ((x = extra.get("extra")) != null) {
                resource.setExtra(x);
            }
            if (path.endsWith("/")) {
                path = path + from.getName();
            }
            this.copy(jar, path, resource, extra);
        } else if (from.getName().equals("<<EMPTY>>")) {
            jar.putResource(path, new EmbeddedResource(new byte[0], 0L));
        } else {
            this.error("Input file does not exist: %s", from).header("-includeresource|Include-Resource");
        }
    }

    private void copy(Jar jar, String path, Resource resource, Map<String, String> extra) {
        jar.putResource(path, resource);
        if (Builder.isTrue(extra.get("lib:"))) {
            this.setProperty("Bundle-ClassPath", Builder.append(this.getProperty("Bundle-ClassPath", "."), path));
        }
    }

    public void setSourcepath(File[] files) {
        for (int i = 0; i < files.length; ++i) {
            this.addSourcepath(files[i]);
        }
    }

    public void addSourcepath(File cp) {
        if (!cp.exists()) {
            this.warning("File on sourcepath that does not exist: %s", cp);
        }
        this.sourcePath.add(cp);
    }

    public Jar[] builds() throws Exception {
        this.begin();
        String conduit = this.getProperty("-conduit");
        if (conduit != null) {
            Parameters map = this.parseHeader(conduit);
            Jar[] result = new Jar[map.size()];
            int n = 0;
            for (String file : map.keySet()) {
                Jar c = new Jar(this.getFile(file));
                this.addClose(c);
                String name = map.get(file).get("name");
                if (name != null) {
                    c.setName(name);
                }
                result[n++] = c;
            }
            return result;
        }
        ArrayList<Jar> result = new ArrayList<Jar>();
        List<Builder> builders = this.getSubBuilders();
        for (Builder builder : builders) {
            try {
                this.startBuild(builder);
                Jar jar = builder.build();
                jar.setName(builder.getBsn());
                result.add(jar);
                this.doneBuild(builder);
            }
            catch (Exception e) {
                builder.exception(e, "Exception Building %s", builder.getBsn());
            }
            if (builder == this) continue;
            this.getInfo(builder, builder.getBsn() + ": ");
        }
        return result.toArray(new Jar[0]);
    }

    protected void startBuild(Builder builder) throws Exception {
    }

    protected void doneBuild(Builder builder) throws Exception {
    }

    public List<Builder> getSubBuilders() throws Exception {
        ArrayList<Builder> builders = new ArrayList<Builder>();
        String sub = this.getProperty("-sub");
        if (sub == null || sub.trim().length() == 0 || "<<EMPTY>>".equals(sub)) {
            builders.add(this);
            return builders;
        }
        if (Builder.isTrue(this.getProperty("-nobundles"))) {
            return builders;
        }
        Parameters subsMap = this.parseHeader(sub);
        Iterator<String> i = subsMap.keySet().iterator();
        while (i.hasNext()) {
            File file = this.getFile(i.next());
            if (!file.isFile() || file.getName().startsWith(".")) continue;
            builders.add(this.getSubBuilder(file));
            i.remove();
        }
        Instructions instructions = new Instructions(subsMap);
        ArrayList<File> members = new ArrayList<File>(Arrays.asList(this.getBase().listFiles()));
        block1: while (members.size() > 0) {
            File file = (File)members.remove(0);
            for (Processor p = this; p != null; p = p.getParent()) {
                if (file.equals(p.getPropertiesFile())) continue block1;
            }
            for (Instruction instruction : instructions.keySet()) {
                if (!instruction.matches(file.getName())) continue;
                if (instruction.isNegated()) continue block1;
                builders.add(this.getSubBuilder(file));
                continue block1;
            }
        }
        return builders;
    }

    public Builder getSubBuilder(File file) throws Exception {
        Builder builder = this.getSubBuilder();
        if (builder != null) {
            builder.setProperties(file);
            this.addClose(builder);
        }
        return builder;
    }

    public Builder getSubBuilder() throws Exception {
        Builder builder = new Builder(this);
        builder.setBase(this.getBase());
        builder.use(this);
        for (Jar file : this.getClasspath()) {
            builder.addClasspath(file);
        }
        return builder;
    }

    public String _maven_version(String[] args) {
        if (args.length > 2) {
            this.error("${maven_version} macro receives too many arguments %s", Arrays.toString(args));
        } else if (args.length < 2) {
            this.error("${maven_version} macro has no arguments, use ${maven_version;1.2.3-SNAPSHOT}", new Object[0]);
        } else {
            return Builder.cleanupVersion(args[1]);
        }
        return null;
    }

    public String _permissions(String[] args) {
        return new PermissionGenerator(this, args).generate();
    }

    public void removeBundleSpecificHeaders() {
        HashSet<String> set = new HashSet<String>(Arrays.asList(BUNDLE_SPECIFIC_HEADERS));
        this.setForceLocal(set);
    }

    public boolean isInScope(Collection<File> resources) throws Exception {
        Parameters clauses = this.parseHeader(this.mergeProperties("Export-Package"));
        clauses.putAll(this.parseHeader(this.mergeProperties("Private-Package")));
        clauses.putAll(this.parseHeader(this.mergeProperties("-privatepackage")));
        if (Builder.isTrue(this.getProperty("-undertest"))) {
            clauses.putAll(this.parseHeader(this.mergeProperties("-testpackages", "test;presence:=optional")));
        }
        Collection<String> ir = this.getIncludedResourcePrefixes();
        Instructions instructions = new Instructions(clauses);
        for (File r : resources) {
            String cpEntry = this.getClasspathEntrySuffix(r);
            if (cpEntry != null) {
                if (cpEntry.equals("")) {
                    return true;
                }
                String pack = Descriptors.getPackage(cpEntry);
                Instruction i = this.matches(instructions, pack, null, r.getName());
                if (i != null) {
                    return !i.isNegated();
                }
            }
            String path = r.getAbsolutePath();
            for (String p : ir) {
                if (!path.startsWith(p)) continue;
                return true;
            }
        }
        return false;
    }

    private Collection<String> getIncludedResourcePrefixes() {
        ArrayList<String> prefixes = new ArrayList<String>();
        Parameters includeResource = this.getIncludeResource();
        for (Map.Entry<String, Attrs> p : includeResource.entrySet()) {
            Matcher m;
            if (p.getValue().containsKey("literal") || !(m = IR_PATTERN.matcher(p.getKey())).matches()) continue;
            File f = this.getFile(m.group(1));
            prefixes.add(f.getAbsolutePath());
        }
        return prefixes;
    }

    public String getClasspathEntrySuffix(File resource) throws Exception {
        for (Jar jar : this.getClasspath()) {
            String resourcePath;
            File source = jar.getSource();
            if (source == null) continue;
            String sourcePath = (source = source.getCanonicalFile()).getAbsolutePath();
            if (sourcePath.equals(resourcePath = resource.getAbsolutePath())) {
                return "";
            }
            if (!resourcePath.startsWith(sourcePath)) continue;
            String filePath = resourcePath.substring(sourcePath.length() + 1);
            return filePath.replace(File.separatorChar, '/');
        }
        return null;
    }

    public boolean doNotCopy(String v) {
        return this.getDoNotCopy().matcher(v).matches();
    }

    public boolean doNotCopy(File from) {
        if (this.doNotCopy(from.getName())) {
            return true;
        }
        if (!this.since(About._3_1)) {
            return false;
        }
        URI uri = this.getBaseURI().relativize(from.toURI());
        return this.doNotCopy(uri.getPath());
    }

    public Pattern getDoNotCopy() {
        if (this.xdoNotCopy == null) {
            String string = null;
            try {
                string = this.mergeProperties("-donotcopy");
                if (string == null || string.isEmpty()) {
                    string = "CVS|\\.svn|\\.git|\\.DS_Store";
                }
                this.xdoNotCopy = Pattern.compile(string);
            }
            catch (Exception e) {
                this.error("Invalid value for %s, value is %s", "-donotcopy", string).header("-donotcopy");
                this.xdoNotCopy = Pattern.compile("CVS|\\.svn|\\.git|\\.DS_Store");
            }
        }
        return this.xdoNotCopy;
    }

    @Override
    protected void setTypeSpecificPlugins(Set<Object> list) {
        list.add(makeBnd);
        list.add(makeCopy);
        list.add(serviceComponent);
        list.add(dsAnnotations);
        list.add(metatypePlugin);
        list.add(metatypeAnnotations);
        super.setTypeSpecificPlugins(list);
    }

    public void doDiff(Jar dot) throws Exception {
        Parameters diffs = this.parseHeader(this.getProperty("-diff"));
        if (diffs.isEmpty()) {
            return;
        }
        logger.debug("diff {}", (Object)diffs);
        if (this.tree == null) {
            this.tree = this.differ.tree(this);
        }
        for (Map.Entry<String, Attrs> entry : diffs.entrySet()) {
            String path = entry.getKey();
            File file = this.getFile(path);
            if (!file.isFile()) {
                this.error("Diffing against %s that is not a file", file).header("-diff").context(path);
                continue;
            }
            boolean full = entry.getValue().get("--full") != null;
            boolean warning = entry.getValue().get("--warning") != null;
            Tree other = this.differ.tree(file);
            Diff api = this.tree.diff(other).get("<api>");
            Instructions instructions = new Instructions(entry.getValue().get("--pack"));
            logger.debug("diff against {} --full={} --pack={} --warning={}", new Object[]{file, full, instructions});
            for (Diff diff : api.getChildren()) {
                String pname = diff.getName();
                if (diff.getType() != Type.PACKAGE || !instructions.matches(pname) || diff.getDelta() == Delta.UNCHANGED) continue;
                if (!full) {
                    if (warning) {
                        this.warning("Differ %s", diff).header("-diff").context(path);
                        continue;
                    }
                    this.error("Differ %s", diff).header("-diff").context(path);
                    continue;
                }
                if (warning) {
                    this.warning("Diff found a difference in %s for packages %s", file, instructions).header("-diff").context(path);
                } else {
                    this.error("Diff found a difference in %s for packages %s", file, instructions).header("-diff").context(path);
                }
                this.show(diff, "", warning);
            }
        }
    }

    private void show(Diff p, String indent, boolean warning) {
        Delta d = p.getDelta();
        if (d == Delta.UNCHANGED) {
            return;
        }
        if (warning) {
            this.warning("%s%s", indent, p).header("-diff");
        } else {
            this.error("%s%s", indent, p).header("-diff");
        }
        indent = indent + " ";
        switch (d) {
            case CHANGED: 
            case MAJOR: 
            case MINOR: 
            case MICRO: {
                break;
            }
            default: {
                return;
            }
        }
        for (Diff diff : p.getChildren()) {
            this.show(diff, indent, warning);
        }
    }

    public void addSourcepath(Collection<File> sourcepath) {
        for (File f : sourcepath) {
            this.addSourcepath(f);
        }
    }

    protected void doBaseline(Jar dot) throws Exception {
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public String _githead(String[] args) throws IOException {
        Macro.verifyCommand(args, _githeadHelp, null, 1, 1);
        for (File rover = this.getBase(); rover != null && rover.isDirectory(); rover = rover.getParentFile()) {
            File headFile = IO.getFile(rover, ".git/HEAD");
            if (!headFile.isFile()) continue;
            String head = IO.collect(headFile).trim();
            if (Hex.isHex(head)) return head.trim().toUpperCase();
            Matcher m = GITREF_P.matcher(head);
            if (m.matches()) {
                String reference = m.group(1);
                File file = IO.getFile(rover, ".git/" + reference);
                if (!file.isFile()) {
                    file = IO.getFile(rover, ".git/packed-refs");
                    if (!file.isFile()) return "";
                    String refs = IO.collect(file);
                    Pattern packedReferenceLinePattern = Pattern.compile("([a-fA-F0-9]{40,40})\\s+" + reference + "\\s*\n");
                    Matcher packedReferenceMatcher = packedReferenceLinePattern.matcher(refs);
                    if (!packedReferenceMatcher.find()) return "";
                    head = packedReferenceMatcher.group(1);
                    return head.trim().toUpperCase();
                } else {
                    head = IO.collect(file);
                }
                return head.trim().toUpperCase();
            } else {
                this.error("Git repo seems corrupt. It exists, find the HEAD but the content is neither hex nor a sym-ref: %s", head);
            }
            return head.trim().toUpperCase();
        }
        return "";
    }

    @Override
    public void report(Map<String, Object> table) throws Exception {
        this.build();
        super.report(table);
        table.put("Do Not Copy", this.getDoNotCopy());
        table.put("Git head", this._githead(new String[]{"githead"}));
    }
}

