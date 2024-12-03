/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.build;

import aQute.bnd.build.CircularDependencyException;
import aQute.bnd.build.Container;
import aQute.bnd.build.DownloadBlocker;
import aQute.bnd.build.JUnitLauncher;
import aQute.bnd.build.Makefile;
import aQute.bnd.build.PackageInfo;
import aQute.bnd.build.ProjectBuilder;
import aQute.bnd.build.ProjectLauncher;
import aQute.bnd.build.ProjectMessages;
import aQute.bnd.build.ProjectTester;
import aQute.bnd.build.ReflectAction;
import aQute.bnd.build.Run;
import aQute.bnd.build.ScriptAction;
import aQute.bnd.build.Workspace;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.help.Syntax;
import aQute.bnd.maven.support.Pom;
import aQute.bnd.maven.support.ProjectPom;
import aQute.bnd.osgi.About;
import aQute.bnd.osgi.Analyzer;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Macro;
import aQute.bnd.osgi.Packages;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Resource;
import aQute.bnd.osgi.Verifier;
import aQute.bnd.osgi.eclipse.EclipseClasspath;
import aQute.bnd.osgi.resource.CapReqBuilder;
import aQute.bnd.osgi.resource.ResourceUtils;
import aQute.bnd.service.CommandPlugin;
import aQute.bnd.service.DependencyContributor;
import aQute.bnd.service.Deploy;
import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.service.Scripter;
import aQute.bnd.service.Strategy;
import aQute.bnd.service.action.Action;
import aQute.bnd.service.action.NamedAction;
import aQute.bnd.service.release.ReleaseBracketingPlugin;
import aQute.bnd.version.Version;
import aQute.bnd.version.VersionRange;
import aQute.lib.collections.ExtList;
import aQute.lib.converter.Converter;
import aQute.lib.io.IO;
import aQute.lib.strings.Strings;
import aQute.lib.utf8properties.UTF8Properties;
import aQute.libg.command.Command;
import aQute.libg.generics.Create;
import aQute.libg.glob.Glob;
import aQute.libg.qtokens.QuotedTokenizer;
import aQute.libg.reporter.ReporterMessages;
import aQute.libg.sed.Replacer;
import aQute.libg.sed.Sed;
import aQute.libg.tuple.Pair;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import org.osgi.resource.Capability;
import org.osgi.resource.Requirement;
import org.osgi.service.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Project
extends Processor {
    private static final Logger logger = LoggerFactory.getLogger(Project.class);
    static final String DEFAULT_ACTIONS = "build; label='Build', test; label='Test', run; label='Run', clean; label='Clean', release; label='Release', refreshAll; label=Refresh, deploy;label=Deploy";
    public static final String BNDFILE = "bnd.bnd";
    public static final String BNDCNF = "cnf";
    public static final String SHA_256 = "SHA-256";
    final Workspace workspace;
    private final AtomicBoolean preparedPaths = new AtomicBoolean();
    final Collection<Project> dependson = new LinkedHashSet<Project>();
    final Collection<Container> classpath = new LinkedHashSet<Container>();
    final Collection<Container> buildpath = new LinkedHashSet<Container>();
    final Collection<Container> testpath = new LinkedHashSet<Container>();
    final Collection<Container> runpath = new LinkedHashSet<Container>();
    final Collection<Container> runbundles = new LinkedHashSet<Container>();
    final Collection<Container> runfw = new LinkedHashSet<Container>();
    File runstorage;
    final Map<File, Attrs> sourcepath = new LinkedHashMap<File, Attrs>();
    final Collection<File> allsourcepath = new LinkedHashSet<File>();
    final Collection<Container> bootclasspath = new LinkedHashSet<Container>();
    final Map<String, Version> versionMap = new LinkedHashMap<String, Version>();
    File output;
    File target;
    private final AtomicInteger revision = new AtomicInteger();
    private File[] files;
    boolean delayRunDependencies = true;
    final ProjectMessages msgs = ReporterMessages.base(this, ProjectMessages.class);
    private Properties ide;
    final Packages exportedPackages = new Packages();
    final Packages importedPackages = new Packages();
    final Packages containedPackages = new Packages();
    final PackageInfo packageInfo = new PackageInfo(this);
    private Makefile makefile;
    private volatile RefreshData data = new RefreshData();
    public Map<String, Container> unreferencedClasspathEntries = new HashMap<String, Container>();
    static String _repoHelp = "${repo ';'<bsn> [ ; <version> [; ('HIGHEST'|'LOWEST')]}";
    static List<String> ignore = new ExtList<String>(BUNDLE_SPECIFIC_HEADERS);

    public Project(Workspace workspace, File unused, File buildFile) throws Exception {
        super(workspace);
        this.workspace = workspace;
        this.setFileMustExist(false);
        if (buildFile != null) {
            this.setProperties(buildFile);
        }
        this.readBuildProperties();
    }

    public Project(Workspace workspace, File buildDir) throws Exception {
        this(workspace, buildDir, new File(buildDir, BNDFILE));
    }

    private void readBuildProperties() throws Exception {
        try {
            File f = this.getFile("build.properties");
            if (f.isFile()) {
                Properties p = this.loadProperties(f);
                Enumeration<?> e = p.propertyNames();
                while (e.hasMoreElements()) {
                    String key;
                    String newkey = key = (String)e.nextElement();
                    if (key.indexOf(36) >= 0) {
                        newkey = this.getReplacer().process(key);
                    }
                    this.setProperty(newkey, p.getProperty(key));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Project getUnparented(File propertiesFile) throws Exception {
        propertiesFile = propertiesFile.getAbsoluteFile();
        Workspace workspace = new Workspace(propertiesFile.getParentFile());
        Project project = new Project(workspace, propertiesFile.getParentFile());
        project.setProperties(propertiesFile);
        project.setFileMustExist(true);
        return project;
    }

    public boolean isValid() {
        if (this.getBase() == null || !this.getBase().isDirectory()) {
            return false;
        }
        return this.getPropertiesFile() == null || this.getPropertiesFile().isFile();
    }

    public ProjectBuilder getBuilder(ProjectBuilder parent) throws Exception {
        ProjectBuilder builder = parent == null ? new ProjectBuilder(this) : new ProjectBuilder(parent);
        builder.setBase(this.getBase());
        builder.use(this);
        return builder;
    }

    public int getChanged() {
        return this.revision.get();
    }

    public void setChanged() {
        this.preparedPaths.set(false);
        this.files = null;
        this.revision.getAndIncrement();
    }

    public Workspace getWorkspace() {
        return this.workspace;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void prepare() throws Exception {
        if (!this.isValid()) {
            this.warning("Invalid project attempts to prepare: %s", this);
            return;
        }
        AtomicBoolean atomicBoolean = this.preparedPaths;
        synchronized (atomicBoolean) {
            if (this.preparedPaths.get()) {
                this.getSrcOutput0();
                this.getTarget0();
                return;
            }
            if (!this.workspace.trail.add(this)) {
                throw new CircularDependencyException(this.workspace.trail.toString() + "," + this);
            }
            try {
                Parameters srces;
                String prefix = this.getBase().getAbsolutePath();
                this.dependson.clear();
                this.buildpath.clear();
                this.sourcepath.clear();
                this.allsourcepath.clear();
                this.bootclasspath.clear();
                this.testpath.clear();
                this.runpath.clear();
                this.runbundles.clear();
                this.runfw.clear();
                this.setProperty("basedir", this.getBase().getAbsolutePath());
                if (!this.getPropertiesFile().isFile() && new File(this.getBase(), ".classpath").isFile()) {
                    this.doEclipseClasspath();
                }
                if ((srces = new Parameters(this.mergeProperties("src"), this)).isEmpty()) {
                    srces.add("src", new Attrs());
                }
                for (Map.Entry<String, Attrs> e : srces.entrySet()) {
                    File dir = this.getFile(Project.removeDuplicateMarker(e.getKey()));
                    if (!dir.getAbsolutePath().startsWith(prefix)) {
                        this.error("The source directory lies outside the project %s directory: %s", this, dir).header("src").context(e.getKey());
                        continue;
                    }
                    if (!dir.isDirectory()) {
                        IO.mkdirs(dir);
                    }
                    if (dir.isDirectory()) {
                        this.sourcepath.put(dir, new Attrs(e.getValue()));
                        this.allsourcepath.add(dir);
                        continue;
                    }
                    this.error("the src path (src property) contains an entry that is not a directory %s", dir).header("src").context(e.getKey());
                }
                this.output = this.getSrcOutput0();
                if (!this.output.isDirectory()) {
                    this.msgs.NoOutputDirectory_(this.output);
                }
                this.target = this.getTarget0();
                String runStorageStr = this.getProperty("-runstorage");
                this.runstorage = runStorageStr != null ? this.getFile(runStorageStr) : null;
                LinkedHashSet<String> requiredProjectNames = new LinkedHashSet<String>(this.getMergedParameters("-dependson").keySet());
                List<DependencyContributor> dcs = this.getPlugins(DependencyContributor.class);
                for (DependencyContributor dc : dcs) {
                    dc.addDependencies(this, requiredProjectNames);
                }
                Instructions is = new Instructions(requiredProjectNames);
                HashSet<Instruction> unused = new HashSet<Instruction>();
                Collection<Project> projects = this.getWorkspace().getAllProjects();
                Collection<Project> dependencies = is.select(projects, unused, false);
                for (Instruction u : unused) {
                    this.msgs.MissingDependson_(u.getInput());
                }
                this.doPath(this.buildpath, dependencies, this.parseBuildpath(), this.bootclasspath, false, "-buildpath");
                this.doPath(this.testpath, dependencies, this.parseTestpath(), this.bootclasspath, false, "-testpath");
                if (!this.delayRunDependencies) {
                    this.doPath(this.runfw, dependencies, this.parseRunFw(), null, false, "-runfw");
                    this.doPath(this.runpath, dependencies, this.parseRunpath(), null, false, "-runpath");
                    this.doPath(this.runbundles, dependencies, this.parseRunbundles(), null, true, "-runbundles");
                }
                HashSet<Project> done = new HashSet<Project>();
                done.add(this);
                for (Project project : dependencies) {
                    project.traverse(this.dependson, done);
                }
                for (Project project : this.dependson) {
                    this.allsourcepath.addAll(project.getSourcePath());
                }
                this.preparedPaths.set(true);
            }
            finally {
                this.workspace.trail.remove(this);
            }
        }
    }

    private File getSrcOutput0() throws IOException {
        File output = this.getSrcOutput().getAbsoluteFile();
        if (!output.exists()) {
            IO.mkdirs(output);
            this.getWorkspace().changedFile(output);
        }
        return output;
    }

    private File getTarget0() throws IOException {
        File target = this.getTargetDir();
        if (!target.exists()) {
            IO.mkdirs(target);
            this.getWorkspace().changedFile(target);
        }
        return target;
    }

    @Deprecated
    public File getSrc() throws Exception {
        this.prepare();
        if (this.sourcepath.isEmpty()) {
            return this.getFile("src");
        }
        return this.sourcepath.keySet().iterator().next();
    }

    public File getSrcOutput() {
        return this.getFile(this.getProperty("bin"));
    }

    public File getTestSrc() {
        return this.getFile(this.getProperty("testsrc"));
    }

    public File getTestOutput() {
        return this.getFile(this.getProperty("testbin"));
    }

    public File getTargetDir() {
        return this.getFile(this.getProperty("target-dir"));
    }

    private void traverse(Collection<Project> dependencies, Set<Project> visited) throws Exception {
        if (visited.contains(this)) {
            return;
        }
        visited.add(this);
        for (Project project : this.getDependson()) {
            project.traverse(dependencies, visited);
        }
        dependencies.add(this);
    }

    private void doPath(Collection<Container> resultpath, Collection<Project> projects, Collection<Container> entries, Collection<Container> bootclasspath, boolean noproject, String name) {
        for (Container cpe : entries) {
            if (cpe.getError() != null) {
                this.error("%s", cpe.getError()).header(name).context(cpe.getBundleSymbolicName());
                continue;
            }
            if (cpe.getType() == Container.TYPE.PROJECT) {
                projects.add(cpe.getProject());
                if (noproject && this.since(About._2_3) && "project".equals(cpe.getAttributes().get("version"))) {
                    this.error("%s is specified with version=project on %s. This version uses the project's output directory, which is not allowed since it must be an actual JAR file for this list.", cpe.getBundleSymbolicName(), name).header(name).context(cpe.getBundleSymbolicName());
                }
            }
            if (bootclasspath != null && (cpe.getBundleSymbolicName().startsWith("ee.") || cpe.getAttributes().containsKey("boot"))) {
                bootclasspath.add(cpe);
                continue;
            }
            resultpath.add(cpe);
        }
    }

    private List<Container> parseBuildpath() throws Exception {
        List<Container> bundles = this.getBundles(Strategy.LOWEST, this.mergeProperties("-buildpath"), "-buildpath");
        return bundles;
    }

    private List<Container> parseRunpath() throws Exception {
        return this.getBundles(Strategy.HIGHEST, this.mergeProperties("-runpath"), "-runpath");
    }

    private List<Container> parseRunbundles() throws Exception {
        return this.getBundles(Strategy.HIGHEST, this.mergeProperties("-runbundles"), "-runbundles");
    }

    private List<Container> parseRunFw() throws Exception {
        return this.getBundles(Strategy.HIGHEST, this.getProperty("-runfw"), "-runfw");
    }

    private List<Container> parseTestpath() throws Exception {
        return this.getBundles(Strategy.HIGHEST, this.mergeProperties("-testpath"), "-testpath");
    }

    /*
     * Unable to fully structure code
     */
    public List<Container> getBundles(Strategy strategyx, String spec, String source) throws Exception {
        result = new ArrayList<Container>();
        bundles = new Parameters(spec, this);
        try {
            for (Map.Entry<String, Attrs> entry : bundles.entrySet()) {
                block17: {
                    bsn = Project.removeDuplicateMarker(entry.getKey());
                    attrs = entry.getValue();
                    found = null;
                    versionRange = (String)attrs.get("version");
                    triedGetBundle = false;
                    if (bsn.indexOf(42) >= 0) {
                        return this.getBundlesWildcard(bsn, versionRange, strategyx, attrs);
                    }
                    if (versionRange != null && (versionRange.equals("latest") || versionRange.equals("snapshot"))) {
                        found = this.getBundle(bsn, versionRange, strategyx, attrs);
                        triedGetBundle = true;
                    }
                    if (found != null) break block17;
                    if (versionRange == null || !versionRange.equals("project") && !versionRange.equals("latest")) ** GOTO lbl25
                    project = this.getWorkspace().getProject(bsn);
                    if (project != null && project.exists()) {
                        f = project.getOutput();
                        found = new Container(project, bsn, versionRange, Container.TYPE.PROJECT, f, null, attrs, null);
                    } else {
                        this.msgs.NoSuchProject(bsn, spec).context(bsn).header(source);
                        continue;
lbl25:
                        // 1 sources

                        if (versionRange != null && versionRange.equals("file")) {
                            f = this.getFile(bsn);
                            error = null;
                            if (!f.exists()) {
                                error = "File does not exist: " + f.getAbsolutePath();
                            }
                            found = f.getName().endsWith(".lib") ? new Container(this, bsn, "file", Container.TYPE.LIBRARY, f, error, attrs, null) : new Container(this, bsn, "file", Container.TYPE.EXTERNAL, f, error, attrs, null);
                        } else if (!triedGetBundle) {
                            found = this.getBundle(bsn, versionRange, strategyx, attrs);
                        }
                    }
                }
                if (found != null) {
                    libs = found.getMembers();
                    for (Container cc : libs) {
                        if (result.contains(cc)) {
                            if (!this.isPedantic()) continue;
                            this.warning("Multiple bundles with the same final URL: %s, dropped duplicate", new Object[]{cc});
                            continue;
                        }
                        if (cc.getError() != null) {
                            this.error("Cannot find %s", new Object[]{cc}).context(bsn).header(source);
                        }
                        result.add(cc);
                    }
                    continue;
                }
                x = new Container(this, bsn, versionRange, Container.TYPE.ERROR, null, bsn + ";version=" + versionRange + " not found", attrs, null);
                result.add(x);
                this.error("Can not find URL for bsn %s", new Object[]{bsn}).context(bsn).header(source);
            }
        }
        catch (CircularDependencyException e) {
            message = e.getMessage();
            if (source != null) {
                message = String.format("%s (from property: %s)", new Object[]{message, source});
            }
            this.msgs.CircularDependencyContext_Message_(this.getName(), message);
        }
        catch (Exception e) {
            this.msgs.Unexpected_Error_(spec, e);
        }
        return result;
    }

    Collection<Container> getBundles(Strategy strategy, String spec) throws Exception {
        return this.getBundles(strategy, spec, null);
    }

    public List<Container> getBundlesWildcard(String bsnPattern, String range, Strategy strategyx, Map<String, String> attrs) throws Exception {
        if ("snapshot".equals(range) || "project".equals(range)) {
            return Collections.singletonList(new Container(this, bsnPattern, range, Container.TYPE.ERROR, null, "Cannot use snapshot or project version with wildcard matches", null, null));
        }
        if (strategyx == Strategy.EXACT) {
            return Collections.singletonList(new Container(this, bsnPattern, range, Container.TYPE.ERROR, null, "Cannot use exact version strategy with wildcard matches", null, null));
        }
        VersionRange versionRange = range == null || "latest".equals(range) ? new VersionRange("0") : new VersionRange(range);
        RepoFilter repoFilter = this.parseRepoFilter(attrs);
        if (bsnPattern != null && ((bsnPattern = bsnPattern.trim()).length() == 0 || bsnPattern.equals("*"))) {
            bsnPattern = null;
        }
        TreeMap<String, Pair<Version, RepositoryPlugin>> providerMap = new TreeMap<String, Pair<Version, RepositoryPlugin>>();
        List<RepositoryPlugin> plugins = this.workspace.getRepositories();
        for (RepositoryPlugin plugin : plugins) {
            List<String> bsns;
            if (repoFilter != null && !repoFilter.match(plugin) || (bsns = plugin.list(bsnPattern)) == null) continue;
            block5: for (String bsn : bsns) {
                SortedSet<Version> versions = plugin.versions(bsn);
                if (versions == null || versions.isEmpty()) continue;
                Pair currentProvider = (Pair)providerMap.get(bsn);
                switch (strategyx) {
                    case HIGHEST: {
                        Version candidate = versions.last();
                        if (currentProvider != null && candidate.compareTo((Version)currentProvider.getFirst()) <= 0) continue block5;
                        providerMap.put(bsn, new Pair<Version, RepositoryPlugin>(candidate, plugin));
                        continue block5;
                    }
                    case LOWEST: {
                        Version candidate = versions.first();
                        if (currentProvider != null && candidate.compareTo((Version)currentProvider.getFirst()) >= 0) continue block5;
                        providerMap.put(bsn, new Pair<Version, RepositoryPlugin>(candidate, plugin));
                        continue block5;
                    }
                }
                throw new IllegalStateException("Cannot use exact version strategy with wildcard matches");
            }
        }
        ArrayList<Container> containers = new ArrayList<Container>(providerMap.size());
        for (Map.Entry entry : providerMap.entrySet()) {
            DownloadBlocker downloadBlocker;
            String bsn = (String)entry.getKey();
            Version version = (Version)((Pair)entry.getValue()).getFirst();
            RepositoryPlugin repo = (RepositoryPlugin)((Pair)entry.getValue()).getSecond();
            File bundle = repo.get(bsn, version, attrs, downloadBlocker = new DownloadBlocker(this));
            if (bundle == null || bundle.getName().endsWith(".lib")) continue;
            containers.add(new Container(this, bsn, range, Container.TYPE.REPO, bundle, null, attrs, downloadBlocker));
        }
        return containers;
    }

    static void mergeNames(String names, Set<String> set) {
        StringTokenizer tokenizer = new StringTokenizer(names, ",");
        while (tokenizer.hasMoreTokens()) {
            set.add(tokenizer.nextToken().trim());
        }
    }

    static String flatten(Set<String> names) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String name : names) {
            if (!first) {
                builder.append(',');
            }
            builder.append(name);
            first = false;
        }
        return builder.toString();
    }

    static void addToPackageList(Container container, String newPackageNames) {
        HashSet<String> merged = new HashSet<String>();
        String packageListStr = container.getAttributes().get("packages");
        if (packageListStr != null) {
            Project.mergeNames(packageListStr, merged);
        }
        if (newPackageNames != null) {
            Project.mergeNames(newPackageNames, merged);
        }
        container.putAttribute("packages", Project.flatten(merged));
    }

    public void doMavenPom(Strategy strategyx, List<Container> result, String action) throws Exception {
        File pomFile = this.getFile("pom.xml");
        if (!pomFile.isFile()) {
            this.msgs.MissingPom();
        } else {
            ProjectPom pom = this.getWorkspace().getMaven().createProjectModel(pomFile);
            if (action == null) {
                action = "compile";
            }
            Pom.Scope act = Pom.Scope.valueOf(action);
            Set<Pom> dependencies = pom.getDependencies(act);
            for (Pom sub : dependencies) {
                File artifact = sub.getArtifact();
                Container container = new Container(artifact, null);
                result.add(container);
            }
        }
    }

    public Collection<Project> getDependson() throws Exception {
        this.prepare();
        return this.dependson;
    }

    public Collection<Container> getBuildpath() throws Exception {
        this.prepare();
        return this.buildpath;
    }

    public Collection<Container> getTestpath() throws Exception {
        this.prepare();
        return this.testpath;
    }

    private void justInTime(Collection<Container> path, List<Container> entries, boolean noproject, String name) {
        if (this.delayRunDependencies && path.isEmpty()) {
            this.doPath(path, this.dependson, entries, null, noproject, name);
        }
    }

    public Collection<Container> getRunpath() throws Exception {
        this.prepare();
        this.justInTime(this.runpath, this.parseRunpath(), false, "-runpath");
        return this.runpath;
    }

    public Collection<Container> getRunbundles() throws Exception {
        this.prepare();
        this.justInTime(this.runbundles, this.parseRunbundles(), true, "-runbundles");
        return this.runbundles;
    }

    public Collection<Container> getRunFw() throws Exception {
        this.prepare();
        this.justInTime(this.runfw, this.parseRunFw(), false, "-runfw");
        return this.runfw;
    }

    public File getRunStorage() throws Exception {
        this.prepare();
        return this.runstorage;
    }

    public boolean getRunBuilds() {
        String runBuildsStr = this.getProperty("-runbuilds");
        boolean result = runBuildsStr == null ? !this.getPropertiesFile().getName().toLowerCase().endsWith(".bndrun") : Boolean.parseBoolean(runBuildsStr);
        return result;
    }

    public Collection<File> getSourcePath() throws Exception {
        this.prepare();
        return this.sourcepath.keySet();
    }

    public Collection<File> getAllsourcepath() throws Exception {
        this.prepare();
        return this.allsourcepath;
    }

    public Collection<Container> getBootclasspath() throws Exception {
        this.prepare();
        return this.bootclasspath;
    }

    public File getOutput() throws Exception {
        this.prepare();
        return this.output;
    }

    private void doEclipseClasspath() throws Exception {
        EclipseClasspath eclipse = new EclipseClasspath(this, this.getWorkspace().getBase(), this.getBase());
        eclipse.setRecurse(false);
        for (File dependent : eclipse.getDependents()) {
            Project required = this.workspace.getProject(dependent.getName());
            this.dependson.add(required);
        }
        for (File f : eclipse.getClasspath()) {
            this.buildpath.add(new Container(f, null));
        }
        for (File f : eclipse.getBootclasspath()) {
            this.bootclasspath.add(new Container(f, null));
        }
        for (File f : eclipse.getSourcepath()) {
            this.sourcepath.put(f, new Attrs());
        }
        this.allsourcepath.addAll(eclipse.getAllSources());
        this.output = eclipse.getOutput();
    }

    public String _p_dependson(String[] args) throws Exception {
        return this.list(args, this.toFiles(this.getDependson()));
    }

    private Collection<?> toFiles(Collection<Project> projects) {
        ArrayList<File> files = new ArrayList<File>();
        for (Project p : projects) {
            files.add(p.getBase());
        }
        return files;
    }

    public String _p_buildpath(String[] args) throws Exception {
        return this.list(args, this.getBuildpath());
    }

    public String _p_testpath(String[] args) throws Exception {
        return this.list(args, this.getRunpath());
    }

    public String _p_sourcepath(String[] args) throws Exception {
        return this.list(args, this.getSourcePath());
    }

    public String _p_allsourcepath(String[] args) throws Exception {
        return this.list(args, this.getAllsourcepath());
    }

    public String _p_bootclasspath(String[] args) throws Exception {
        return this.list(args, this.getBootclasspath());
    }

    public String _p_output(String[] args) throws Exception {
        if (args.length != 1) {
            throw new IllegalArgumentException("${output} should not have arguments");
        }
        return this.getOutput().getAbsolutePath();
    }

    private String list(String[] args, Collection<?> list) {
        if (args.length > 3) {
            throw new IllegalArgumentException("${" + args[0] + "[;<separator>]} can only take a separator as argument, has " + Arrays.toString(args));
        }
        String separator = ",";
        if (args.length == 2) {
            separator = args[1];
        }
        return Project.join(list, separator);
    }

    @Override
    protected Object[] getMacroDomains() {
        return new Object[]{this.workspace};
    }

    public File release(String jarName, InputStream jarStream) throws Exception {
        return this.release(null, jarName, jarStream);
    }

    public URI releaseURI(String jarName, InputStream jarStream) throws Exception {
        return this.releaseURI(null, jarName, jarStream);
    }

    public File release(String name, String jarName, InputStream jarStream) throws Exception {
        URI uri = this.releaseURI(name, jarName, jarStream);
        if (uri != null && uri.getScheme().equals("file")) {
            return new File(uri);
        }
        return null;
    }

    public URI releaseURI(String name, String jarName, InputStream jarStream) throws Exception {
        List<RepositoryPlugin> releaseRepos = this.getReleaseRepos(name);
        if (releaseRepos.isEmpty()) {
            return null;
        }
        RepositoryPlugin releaseRepo = releaseRepos.get(0);
        return this.releaseRepo(releaseRepo, jarName, jarStream);
    }

    private URI releaseRepo(RepositoryPlugin releaseRepo, String jarName, InputStream jarStream) throws Exception {
        logger.debug("release to {}", (Object)releaseRepo.getName());
        try {
            RepositoryPlugin.PutOptions putOptions = new RepositoryPlugin.PutOptions();
            putOptions.context = this;
            RepositoryPlugin.PutResult r = releaseRepo.put(jarStream, putOptions);
            logger.debug("Released {} to {} in repository {}", new Object[]{jarName, r.artifact, releaseRepo});
            return r.artifact;
        }
        catch (Exception e) {
            this.msgs.Release_Into_Exception_(jarName, releaseRepo, e);
            return null;
        }
    }

    private List<RepositoryPlugin> getReleaseRepos(String names) {
        Parameters repoNames = this.parseReleaseRepos(names);
        List<RepositoryPlugin> plugins = this.getPlugins(RepositoryPlugin.class);
        ArrayList<RepositoryPlugin> result = new ArrayList<RepositoryPlugin>();
        if (repoNames == null) {
            for (RepositoryPlugin plugin : plugins) {
                if (!plugin.canWrite()) continue;
                result.add(plugin);
                break;
            }
            if (result.isEmpty()) {
                this.msgs.NoNameForReleaseRepository();
            }
            return result;
        }
        block1: for (String repoName : repoNames.keySet()) {
            for (RepositoryPlugin plugin : plugins) {
                if (!plugin.canWrite() || !repoName.equals(plugin.getName())) continue;
                result.add(plugin);
                continue block1;
            }
            this.msgs.ReleaseRepository_NotFoundIn_(repoName, plugins);
        }
        return result;
    }

    private Parameters parseReleaseRepos(String names) {
        if (names == null && (names = this.mergeProperties("-releaserepo")) == null) {
            return null;
        }
        return new Parameters(names, this);
    }

    public void release(boolean test) throws Exception {
        this.release(null, test);
    }

    public void release(String name, boolean test) throws Exception {
        List<RepositoryPlugin> releaseRepos = this.getReleaseRepos(name);
        if (releaseRepos.isEmpty()) {
            return;
        }
        logger.debug("release");
        File[] jars = this.getBuildFiles(false);
        if (jars == null && (jars = this.build(test)) == null) {
            logger.debug("no jars built");
            return;
        }
        logger.debug("releasing {} - {}", (Object)jars, releaseRepos);
        for (RepositoryPlugin releaseRepo : releaseRepos) {
            for (File jar : jars) {
                this.releaseRepo(releaseRepo, jar.getName(), new BufferedInputStream(IO.stream(jar)));
            }
        }
    }

    public Container getBundle(String bsn, String range, Strategy strategy, Map<String, String> attrs) throws Exception {
        if (range == null) {
            range = "0";
        }
        if ("snapshot".equals(range) || "project".equals(range)) {
            return this.getBundleFromProject(bsn, attrs);
        }
        if ("hash".equals(range)) {
            return this.getBundleByHash(bsn, attrs);
        }
        Strategy useStrategy = strategy;
        if ("latest".equals(range)) {
            Container c = this.getBundleFromProject(bsn, attrs);
            if (c != null) {
                return c;
            }
            useStrategy = Strategy.HIGHEST;
        }
        useStrategy = this.overrideStrategy(attrs, useStrategy);
        RepoFilter repoFilter = this.parseRepoFilter(attrs);
        List<RepositoryPlugin> plugins = this.workspace.getRepositories();
        if (useStrategy == Strategy.EXACT) {
            if (!Verifier.isVersion(range)) {
                return new Container(this, bsn, range, Container.TYPE.ERROR, null, bsn + ";version=" + range + " Invalid version", null, null);
            }
            Version version = new Version(range);
            for (RepositoryPlugin plugin : plugins) {
                DownloadBlocker blocker;
                File result = plugin.get(bsn, version, attrs, blocker = new DownloadBlocker(this));
                if (result == null) continue;
                return this.toContainer(bsn, range, attrs, result, blocker);
            }
        } else {
            DownloadBlocker blocker;
            Object version;
            VersionRange versionRange = "latest".equals(range) ? new VersionRange("0") : new VersionRange(range);
            TreeMap<Version, RepositoryPlugin> versions = new TreeMap<Version, RepositoryPlugin>();
            for (RepositoryPlugin plugin : plugins) {
                if (repoFilter != null && !repoFilter.match(plugin)) continue;
                try {
                    SortedSet<Version> vs = plugin.versions(bsn);
                    if (vs == null) continue;
                    for (Version v : vs) {
                        if (versions.containsKey(v) || !versionRange.includes(v)) continue;
                        versions.put(v, plugin);
                    }
                }
                catch (UnsupportedOperationException ose) {
                    File file;
                    if (versions.isEmpty() || !Verifier.isVersion(range) || (file = plugin.get(bsn, (Version)(version = new Version(range)), attrs, blocker = new DownloadBlocker(this))) == null) continue;
                    return this.toContainer(bsn, range, attrs, file, blocker);
                }
            }
            SortedSet<Version> localVersions = this.getWorkspace().getWorkspaceRepository().versions(bsn);
            for (Version v : localVersions) {
                if (versions.containsKey(v) || !versionRange.includes(v)) continue;
                versions.put(v, null);
            }
            if (!versions.isEmpty()) {
                Version provider = null;
                switch (useStrategy) {
                    case HIGHEST: {
                        provider = (Version)versions.lastKey();
                        break;
                    }
                    case LOWEST: {
                        provider = (Version)versions.firstKey();
                        break;
                    }
                }
                if (provider != null) {
                    RepositoryPlugin repo = (RepositoryPlugin)versions.get(provider);
                    if (repo == null) {
                        return this.getBundleFromProject(bsn, attrs);
                    }
                    version = provider.toString();
                    blocker = new DownloadBlocker(this);
                    File result = repo.get(bsn, provider, attrs, blocker);
                    if (result != null) {
                        return this.toContainer(bsn, (String)version, attrs, result, blocker);
                    }
                } else {
                    this.msgs.FoundVersions_ForStrategy_ButNoProvider(versions, useStrategy);
                }
            }
        }
        return new Container(this, bsn, range, Container.TYPE.ERROR, null, bsn + ";version=" + range + " Not found in " + plugins, null, null);
    }

    protected Strategy overrideStrategy(Map<String, String> attrs, Strategy useStrategy) {
        String overrideStrategy;
        if (attrs != null && (overrideStrategy = attrs.get("strategy")) != null) {
            if ("highest".equalsIgnoreCase(overrideStrategy)) {
                useStrategy = Strategy.HIGHEST;
            } else if ("lowest".equalsIgnoreCase(overrideStrategy)) {
                useStrategy = Strategy.LOWEST;
            } else if ("exact".equalsIgnoreCase(overrideStrategy)) {
                useStrategy = Strategy.EXACT;
            }
        }
        return useStrategy;
    }

    protected RepoFilter parseRepoFilter(Map<String, String> attrs) {
        if (attrs == null) {
            return null;
        }
        String patternStr = attrs.get("repo");
        if (patternStr == null) {
            return null;
        }
        LinkedList<Pattern> patterns = new LinkedList<Pattern>();
        QuotedTokenizer tokenize = new QuotedTokenizer(patternStr, ",");
        String token = tokenize.nextToken();
        while (token != null) {
            patterns.add(Glob.toPattern(token));
            token = tokenize.nextToken();
        }
        return new RepoFilter(patterns.toArray(new Pattern[0]));
    }

    protected Container toContainer(String bsn, String range, Map<String, String> attrs, File result, DownloadBlocker db) {
        File f = result;
        if (f == null) {
            this.msgs.ConfusedNoContainerFile();
            f = new File("was null");
        }
        Container container = f.getName().endsWith("lib") ? new Container(this, bsn, range, Container.TYPE.LIBRARY, f, null, attrs, db) : new Container(this, bsn, range, Container.TYPE.REPO, f, null, attrs, db);
        return container;
    }

    private Container getBundleFromProject(String bsn, Map<String, String> attrs) throws Exception {
        String pname = bsn;
        while (true) {
            Project p;
            if ((p = this.getWorkspace().getProject(pname)) != null && p.isValid()) {
                Container c = p.getDeliverable(bsn, attrs);
                return c;
            }
            int n = pname.lastIndexOf(46);
            if (n <= 0) {
                return null;
            }
            pname = pname.substring(0, n);
        }
    }

    private Container getBundleByHash(String bsn, Map<String, String> attrs) throws Exception {
        String hashStr = attrs.get("hash");
        String algo = SHA_256;
        String hash = hashStr;
        int colonIndex = hashStr.indexOf(58);
        if (colonIndex > -1) {
            algo = hashStr.substring(0, colonIndex);
            int afterColon = colonIndex + 1;
            hash = colonIndex < hashStr.length() ? hashStr.substring(afterColon) : "";
        }
        for (RepositoryPlugin plugin : this.workspace.getRepositories()) {
            DownloadBlocker blocker = new DownloadBlocker(this);
            File result = plugin.get(bsn, Version.LOWEST, Collections.unmodifiableMap(attrs), blocker);
            if (result == null && plugin instanceof Repository) {
                Collection<Capability> caps;
                Repository repo = (Repository)((Object)plugin);
                if (!SHA_256.equals(algo)) continue;
                Requirement contentReq = new CapReqBuilder("osgi.content").filter(String.format("(%s=%s)", "osgi.content", hash)).buildSyntheticRequirement();
                Set<Requirement> reqs = Collections.singleton(contentReq);
                Map<Requirement, Collection<Capability>> providers = repo.findProviders(reqs);
                Collection<Capability> collection = caps = providers != null ? providers.get(contentReq) : null;
                if (caps != null && !caps.isEmpty()) {
                    Version bndVersion;
                    Capability cap = caps.iterator().next();
                    ResourceUtils.IdentityCapability idCap = ResourceUtils.getIdentityCapability(cap.getResource());
                    Map<String, Object> idAttrs = idCap.getAttributes();
                    String id = (String)idAttrs.get("osgi.identity");
                    Object version = idAttrs.get("version");
                    Version version2 = bndVersion = version != null ? Version.parseVersion(version.toString()) : Version.LOWEST;
                    if (!bsn.equals(id)) {
                        String error = String.format("Resource with requested hash does not match ID '%s' [hash: %s]", bsn, hashStr);
                        return new Container(this, bsn, "hash", Container.TYPE.ERROR, null, error, null, null);
                    }
                    result = plugin.get(id, bndVersion, null, blocker);
                }
            }
            if (result == null) continue;
            return this.toContainer(bsn, "hash", attrs, result, blocker);
        }
        return new Container(this, bsn, "hash", Container.TYPE.ERROR, null, "Could not find resource by content hash " + hashStr, null, null);
    }

    public void deploy(String name, File file) throws Exception {
        List<RepositoryPlugin> plugins = this.getPlugins(RepositoryPlugin.class);
        RepositoryPlugin rp = null;
        for (RepositoryPlugin plugin : plugins) {
            if (!plugin.canWrite()) continue;
            if (name == null) {
                rp = plugin;
                break;
            }
            if (!name.equals(plugin.getName())) continue;
            rp = plugin;
            break;
        }
        if (rp != null) {
            try {
                rp.put(new BufferedInputStream(IO.stream(file)), new RepositoryPlugin.PutOptions());
                return;
            }
            catch (Exception e) {
                this.msgs.DeployingFile_On_Exception_(file, rp.getName(), e);
                return;
            }
        }
        logger.debug("No repo found {}", (Object)file);
        throw new IllegalArgumentException("No repository found for " + file);
    }

    public void deploy(File file) throws Exception {
        String name = this.getProperty("-deployrepo");
        this.deploy(name, file);
    }

    public void deploy() throws Exception {
        File[] outputs;
        Parameters deploy = new Parameters(this.getProperty("-deploy"), this);
        if (deploy.isEmpty()) {
            this.warning("Deploying but %s is not set to any repo", "-deploy");
            return;
        }
        for (File output : outputs = this.getBuildFiles()) {
            for (Deploy d : this.getPlugins(Deploy.class)) {
                logger.debug("Deploying {} to: {}", (Object)output.getName(), (Object)d);
                try {
                    if (!d.deploy(this, output.getName(), new BufferedInputStream(IO.stream(output)))) continue;
                    logger.debug("deployed {} successfully to {}", (Object)output, (Object)d);
                }
                catch (Exception e) {
                    this.msgs.Deploying(e);
                }
            }
        }
    }

    public String _repo(String[] args) throws Exception {
        if (args.length < 2) {
            this.msgs.RepoTooFewArguments(_repoHelp, args);
            return null;
        }
        String bsns = args[1];
        String version = null;
        Strategy strategy = Strategy.HIGHEST;
        if (args.length > 2) {
            version = args[2];
            if (args.length == 4) {
                if (args[3].equalsIgnoreCase("HIGHEST")) {
                    strategy = Strategy.HIGHEST;
                } else if (args[3].equalsIgnoreCase("LOWEST")) {
                    strategy = Strategy.LOWEST;
                } else if (args[3].equalsIgnoreCase("EXACT")) {
                    strategy = Strategy.EXACT;
                } else {
                    this.msgs.InvalidStrategy(_repoHelp, args);
                }
            }
        }
        Collection<String> parts = Project.split(bsns);
        ArrayList<String> paths = new ArrayList<String>();
        for (String bsn : parts) {
            Container container = this.getBundle(bsn, version, strategy, null);
            if (container.getError() != null) {
                this.error("${repo} macro refers to an artifact %s-%s (%s) that has an error: %s", new Object[]{bsn, version, strategy, container.getError()});
                continue;
            }
            this.add(paths, container);
        }
        return Project.join(paths);
    }

    private void add(List<String> paths, Container container) throws Exception {
        if (container.getType() == Container.TYPE.LIBRARY) {
            List<Container> members = container.getMembers();
            for (Container sub : members) {
                this.add(paths, sub);
            }
        } else if (container.getError() == null) {
            paths.add(container.getFile().getAbsolutePath());
        } else {
            paths.add("<<${repo} = " + container.getBundleSymbolicName() + "-" + container.getVersion() + " : " + container.getError() + ">>");
            if (this.isPedantic()) {
                this.warning("Could not expand repo path request: %s ", container);
            }
        }
    }

    public File getTarget() throws Exception {
        this.prepare();
        return this.target;
    }

    public File[] build(boolean underTest) throws Exception {
        if (this.isNoBundles()) {
            return null;
        }
        if (this.getProperty("-nope") != null) {
            this.warning("Please replace -nope with %s", "-nobundles");
            return null;
        }
        logger.debug("building {}", (Object)this);
        File[] files = this.buildLocal(underTest);
        this.install(files);
        return files;
    }

    private void install(File[] files) throws Exception {
        if (files == null) {
            return;
        }
        Parameters p = this.getInstallRepositories();
        for (Map.Entry<String, Attrs> e : p.entrySet()) {
            RepositoryPlugin rp = this.getWorkspace().getRepository(e.getKey());
            if (rp != null) {
                for (File f : files) {
                    this.install(f, rp, e.getValue());
                }
                continue;
            }
            this.warning("No such repository to install into: %s", e.getKey());
        }
    }

    public Parameters getInstallRepositories() {
        if (this.data.installRepositories == null) {
            this.data.installRepositories = new Parameters(this.mergeProperties("-buildrepo"), this);
        }
        return this.data.installRepositories;
    }

    private void install(File f, RepositoryPlugin repo, Attrs value) throws Exception {
        try (Processor p = new Processor();){
            p.getProperties().putAll((Map<?, ?>)value);
            RepositoryPlugin.PutOptions options = new RepositoryPlugin.PutOptions();
            options.context = p;
            try (InputStream in = IO.stream(f);){
                repo.put(in, options);
            }
            catch (Exception e) {
                this.exception(e, "Cannot install %s into %s because %s", f, repo.getName(), e);
            }
        }
    }

    public File[] getFiles() {
        return this.files;
    }

    public boolean isStale() throws Exception {
        HashSet<Project> visited = new HashSet<Project>();
        return this.isStale(visited);
    }

    boolean isStale(Set<Project> visited) throws Exception {
        if (this.isNoBundles()) {
            return false;
        }
        if (!visited.add(this)) {
            return false;
        }
        long buildTime = 0L;
        File[] files = this.getBuildFiles(false);
        if (files == null) {
            return true;
        }
        for (File f : files) {
            if (f.lastModified() < this.lastModified()) {
                return true;
            }
            if (buildTime >= f.lastModified()) continue;
            buildTime = f.lastModified();
        }
        for (Project dependency : this.getDependson()) {
            if (dependency == this || dependency.isNoBundles()) continue;
            if (dependency.isStale(visited)) {
                return true;
            }
            File[] deps = dependency.getBuildFiles(false);
            if (deps == null) {
                return true;
            }
            for (File f : deps) {
                if (buildTime >= f.lastModified()) continue;
                return true;
            }
        }
        return false;
    }

    public File[] getBuildFiles() throws Exception {
        return this.getBuildFiles(true);
    }

    public File[] getBuildFiles(boolean buildIfAbsent) throws Exception {
        File[] current = this.files;
        if (current != null) {
            return current;
        }
        File bfs = new File(this.getTarget(), "buildfiles");
        if (bfs.isFile()) {
            try (BufferedReader rdr = IO.reader(bfs);){
                List<File> list = this.newList();
                String s = rdr.readLine();
                while (s != null) {
                    File ff = new File(s = s.trim());
                    if (!ff.isFile()) {
                        rdr.close();
                        IO.delete(bfs);
                        this.files = buildIfAbsent ? this.buildLocal(false) : null;
                        File[] fileArray = this.files;
                        return fileArray;
                    }
                    list.add(ff);
                    s = rdr.readLine();
                }
                this.files = list.toArray(new File[0]);
                File[] fileArray = this.files;
                return fileArray;
            }
        }
        this.files = buildIfAbsent ? this.buildLocal(false) : null;
        return this.files;
    }

    /*
     * Exception decompiling
     */
    public File[] buildLocal(boolean underTest) throws Exception {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [1[TRYBLOCK]], but top level block is 42[SIMPLE_IF_TAKEN]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public boolean isNoBundles() {
        return Project.isTrue(this.getProperty("-nobundles"));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public File saveBuild(Jar jar) throws Exception {
        try {
            File outputFile;
            File logicalFile = outputFile = this.getOutputFile(jar.getName(), jar.getVersion());
            String msg = "";
            if (!outputFile.exists() || outputFile.lastModified() < jar.lastModified()) {
                String overwritestrategy;
                this.reportNewer(outputFile.lastModified(), jar);
                File fp = outputFile.getParentFile();
                if (!fp.isDirectory()) {
                    IO.mkdirs(fp);
                }
                block10 : switch (overwritestrategy = this.getProperty("-x-overwritestrategy", "classic")) {
                    case "delay": {
                        for (int i = 0; i < 10; ++i) {
                            try {
                                IO.deleteWithException(outputFile);
                                jar.write(outputFile);
                                break block10;
                            }
                            catch (Exception e) {
                                Thread.sleep(500L);
                                continue;
                            }
                        }
                    }
                    case "classic": {
                        IO.deleteWithException(outputFile);
                        jar.write(outputFile);
                        break;
                    }
                    case "gc": {
                        try {
                            IO.deleteWithException(outputFile);
                        }
                        catch (Exception e) {
                            System.gc();
                            System.runFinalization();
                            IO.deleteWithException(outputFile);
                        }
                        jar.write(outputFile);
                        break;
                    }
                    case "windows-only-disposable-names": {
                        boolean isWindows;
                        boolean bl = isWindows = File.separatorChar == '\\';
                        if (!isWindows) {
                            IO.deleteWithException(outputFile);
                            jar.write(outputFile);
                            break;
                        }
                    }
                    case "disposable-names": {
                        int suffix = 0;
                        while (true) {
                            outputFile = new File(outputFile.getParentFile(), outputFile.getName() + "-" + suffix);
                            IO.delete(outputFile);
                            if (!outputFile.isFile()) {
                                jar.write(outputFile);
                                Files.createSymbolicLink(logicalFile.toPath(), outputFile.toPath(), new FileAttribute[0]);
                                break block10;
                            }
                            this.warning("Could not delete build file {} ", overwritestrategy);
                            logger.warn("Cannot delete file {} but that should be ok", (Object)outputFile);
                            ++suffix;
                        }
                    }
                    default: {
                        this.error("Invalid value for -x-overwritestrategy: %s, expected classic, delay, gc, windows-only-disposable-names, disposable-names", overwritestrategy);
                        IO.deleteWithException(outputFile);
                        jar.write(outputFile);
                        break;
                    }
                }
                File canonical = new File(this.getTarget(), jar.getName() + ".jar");
                if (!canonical.equals(logicalFile)) {
                    IO.delete(canonical);
                    if (!IO.createSymbolicLink(canonical, outputFile)) {
                        IO.copy(outputFile, canonical);
                    }
                    this.getWorkspace().changedFile(canonical);
                }
                this.getWorkspace().changedFile(outputFile);
                if (!outputFile.equals(logicalFile)) {
                    this.getWorkspace().changedFile(logicalFile);
                }
            } else {
                msg = "(not modified since " + new Date(outputFile.lastModified()) + ")";
            }
            logger.debug("{} ({}) {} {}", new Object[]{jar.getName(), outputFile.getName(), jar.getResources().size(), msg});
            File file = logicalFile;
            return file;
        }
        finally {
            jar.close();
        }
    }

    public File getOutputFile(String bsn, String version) throws Exception {
        if (version == null) {
            version = "0";
        }
        try (Processor scoped = new Processor(this);){
            scoped.setProperty("@bsn", bsn);
            scoped.setProperty("@version", version);
            String path = scoped.getProperty("-outputmask", bsn + ".jar");
            File file = IO.getFile(this.getTarget(), path);
            return file;
        }
    }

    public File getOutputFile(String bsn) throws Exception {
        return this.getOutputFile(bsn, "0.0.0");
    }

    private void reportNewer(long lastModified, Jar jar) {
        if (Project.isTrue(this.getProperty("-reportnewer"))) {
            StringBuilder sb = new StringBuilder();
            String del = "Newer than " + new Date(lastModified);
            for (Map.Entry<String, Resource> entry : jar.getResources().entrySet()) {
                if (entry.getValue().lastModified() <= lastModified) continue;
                sb.append(del);
                del = ", \n     ";
                sb.append(entry.getKey());
            }
            if (sb.length() > 0) {
                this.warning("%s", sb.toString());
            }
        }
    }

    @Override
    public boolean refresh() {
        this.versionMap.clear();
        this.data = new RefreshData();
        boolean changed = false;
        if (this.isCnf()) {
            changed = this.workspace.refresh();
        }
        return super.refresh() || changed;
    }

    public boolean isCnf() {
        try {
            return this.getBase().getCanonicalPath().equals(this.getWorkspace().getBuildDir().getCanonicalPath());
        }
        catch (IOException e) {
            return false;
        }
    }

    @Override
    public void propertiesChanged() {
        super.propertiesChanged();
        this.preparedPaths.set(false);
        this.files = null;
        this.makefile = null;
        this.versionMap.clear();
        this.data = new RefreshData();
    }

    public String getName() {
        return this.getBase().getName();
    }

    public Map<String, Action> getActions() {
        Map<String, Action> all = Project.newMap();
        Map<String, Action> actions = Project.newMap();
        this.fillActions(all);
        this.getWorkspace().fillActions(all);
        for (Map.Entry<String, Action> action : all.entrySet()) {
            String key = this.getReplacer().process(action.getKey());
            if (key == null || key.trim().length() == 0) continue;
            actions.put(key, action.getValue());
        }
        return actions;
    }

    public void fillActions(Map<String, Action> all) {
        List<NamedAction> plugins = this.getPlugins(NamedAction.class);
        for (NamedAction a : plugins) {
            all.put(a.getName(), a);
        }
        Parameters actions = new Parameters(this.getProperty("-actions", DEFAULT_ACTIONS), this);
        for (Map.Entry<String, Attrs> entry : actions.entrySet()) {
            String key = Processor.removeDuplicateMarker(entry.getKey());
            Action action = entry.getValue().get("script") != null ? new ScriptAction(entry.getValue().get("type"), entry.getValue().get("script")) : new ReflectAction(key);
            String label = entry.getValue().get("label");
            all.put(label.toLowerCase(), action);
        }
    }

    public void release() throws Exception {
        this.release(false);
    }

    public void export(String runFilePath, boolean keep, File output) throws Exception {
        Project packageProject;
        this.prepare();
        if (runFilePath == null || runFilePath.length() == 0 || ".".equals(runFilePath)) {
            packageProject = this;
        } else {
            File runFile = IO.getFile(this.getBase(), runFilePath);
            if (!runFile.isFile()) {
                throw new IOException(String.format("Run file %s does not exist (or is not a file).", runFile.getAbsolutePath()));
            }
            packageProject = new Run(this.getWorkspace(), this.getBase(), runFile);
        }
        packageProject.clear();
        try (ProjectLauncher launcher = packageProject.getProjectLauncher();){
            launcher.setKeep(keep);
            try (Jar jar = launcher.executable();){
                this.getInfo(launcher);
                jar.write(output);
            }
        }
    }

    public void exportRunbundles(String runFilePath, File outputDir) throws Exception {
        Project packageProject;
        this.prepare();
        if (runFilePath == null || runFilePath.length() == 0 || ".".equals(runFilePath)) {
            packageProject = this;
        } else {
            File runFile = IO.getFile(this.getBase(), runFilePath);
            if (!runFile.isFile()) {
                throw new IOException(String.format("Run file %s does not exist (or is not a file).", runFile.getAbsolutePath()));
            }
            packageProject = new Run(this.getWorkspace(), this.getBase(), runFile);
        }
        packageProject.clear();
        IO.mkdirs(outputDir);
        Collection<Container> runbundles = packageProject.getRunbundles();
        Path outputPath = outputDir.toPath();
        for (Container container : runbundles) {
            Path source = container.getFile().toPath();
            Path target = this.nonCollidingPath(outputPath, source);
            Files.copy(source, target, StandardCopyOption.COPY_ATTRIBUTES);
        }
    }

    Path nonCollidingPath(Path outputDir, Path source) {
        String fileName = source.getFileName().toString();
        Path target = outputDir.resolve(fileName);
        String[] parts = Strings.extension(fileName);
        if (parts == null) {
            parts = new String[]{fileName, ""};
        }
        int i = 1;
        while (Files.exists(target, new LinkOption[0])) {
            target = outputDir.resolve(String.format("%s[%d].%s", parts[0], i++, parts[1]));
        }
        return target;
    }

    public void release(String name) throws Exception {
        this.release(name, false);
    }

    public void clean() throws Exception {
        this.clean(this.getTarget(), "target");
        this.clean(this.getSrcOutput(), "source output");
        this.clean(this.getTestOutput(), "test output");
        this.clean(this.getOutput(), "output");
    }

    void clean(File dir, String type) throws IOException {
        if (!dir.exists()) {
            return;
        }
        String basePath = this.getBase().getCanonicalPath();
        String dirPath = dir.getCanonicalPath();
        if (!dirPath.startsWith(basePath)) {
            logger.debug("path outside the project dir {}", (Object)type);
            return;
        }
        if (dirPath.length() == basePath.length()) {
            this.error("Trying to delete the project directory for %s", type);
            return;
        }
        IO.delete(dir);
        if (dir.exists()) {
            this.error("Trying to delete %s (%s), but failed", dir, type);
            return;
        }
        IO.mkdirs(dir);
    }

    public File[] build() throws Exception {
        return this.build(false);
    }

    private Makefile getMakefile() {
        if (this.makefile == null) {
            this.makefile = new Makefile(this);
        }
        return this.makefile;
    }

    public void run() throws Exception {
        try (ProjectLauncher pl = this.getProjectLauncher();){
            pl.setTrace(this.isTrace() || Project.isTrue(this.getProperty("-runtrace")));
            pl.launch();
        }
    }

    public void runLocal() throws Exception {
        try (ProjectLauncher pl = this.getProjectLauncher();){
            pl.setTrace(this.isTrace() || Project.isTrue(this.getProperty("-runtrace")));
            pl.start(null);
        }
    }

    public void test() throws Exception {
        this.test(null);
    }

    public void test(List<String> tests) throws Exception {
        String testcases = this.getProperties().getProperty("Test-Cases");
        if (testcases == null) {
            this.warning("No %s set", "Test-Cases");
            return;
        }
        this.clear();
        this.test(null, tests);
    }

    public void test(File reportDir, List<String> tests) throws Exception {
        ProjectTester tester = this.getProjectTester();
        if (reportDir != null) {
            logger.debug("Setting reportDir {}", (Object)reportDir);
            IO.delete(reportDir);
            tester.setReportDir(reportDir);
        }
        if (tests != null) {
            logger.debug("Adding tests {}", tests);
            for (String test : tests) {
                tester.addTest(test);
            }
        }
        tester.prepare();
        if (!this.isOk()) {
            logger.error("Tests not run because project has errors");
            return;
        }
        int errors = tester.test();
        if (errors == 0) {
            logger.info("No Errors");
        } else if (errors > 0) {
            logger.info("{} Error(s)", (Object)errors);
        } else {
            logger.info("Error {}", (Object)errors);
        }
    }

    public void junit() throws Exception {
        JUnitLauncher launcher = new JUnitLauncher(this);
        launcher.launch();
    }

    public Jar getValidJar(File f) throws Exception {
        Jar jar = new Jar(f);
        return this.getValidJar(jar, f.getAbsolutePath());
    }

    public Jar getValidJar(URL url) throws Exception {
        try (InputStream in = url.openStream();){
            Jar jar = new Jar(url.getFile().replace('/', '.'), in, System.currentTimeMillis());
            Jar jar2 = this.getValidJar(jar, url.toString());
            return jar2;
        }
    }

    public Jar getValidJar(Jar jar, String id) throws Exception {
        Manifest manifest = jar.getManifest();
        if (manifest == null) {
            logger.debug("Wrapping with all defaults");
            Builder b = new Builder(this);
            this.addClose(b);
            b.addClasspath(jar);
            b.setProperty("Bnd-Message", "Wrapped from " + id + "because lacked manifest");
            b.setProperty("Export-Package", "*");
            b.setProperty("Import-Package", "*;resolution:=optional");
            jar = b.build();
        } else if (manifest.getMainAttributes().getValue("Bundle-ManifestVersion") == null) {
            logger.debug("Not a release 4 bundle, wrapping with manifest as source");
            Builder b = new Builder(this);
            this.addClose(b);
            b.addClasspath(jar);
            b.setProperty("Private-Package", "*");
            b.mergeManifest(manifest);
            String imprts = manifest.getMainAttributes().getValue("Import-Package");
            imprts = imprts == null ? "" : imprts + ",";
            imprts = imprts + "*;resolution=optional";
            b.setProperty("Import-Package", imprts);
            b.setProperty("Bnd-Message", "Wrapped from " + id + "because had incomplete manifest");
            jar = b.build();
        }
        return jar;
    }

    public String _project(String[] args) {
        return this.getBase().getAbsolutePath();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void bump(String mask) throws Exception {
        String pattern = "(Bundle-Version\\s*(:|=)\\s*)(([0-9]+(\\.[0-9]+(\\.[0-9]+)?)?))";
        String replace = "$1${version;" + mask + ";$3}";
        try {
            if (this.replace(this.getPropertiesFile(), pattern, replace)) {
                return;
            }
            logger.debug("no version in bnd.bnd");
            List<File> included = this.getIncluded();
            if (included != null) {
                ArrayList<File> copy = new ArrayList<File>(included);
                Collections.reverse(copy);
                for (File file : copy) {
                    if (!this.replace(file, pattern, replace)) continue;
                    logger.debug("replaced version in file {}", (Object)file);
                    return;
                }
            }
            logger.debug("no version in included files");
            boolean found = false;
            try (ProjectBuilder b = this.getBuilder(null);){
                for (Builder sub : b.getSubBuilders()) {
                    found |= this.replace(sub.getPropertiesFile(), pattern, replace);
                }
            }
            if (!found) {
                logger.debug("no version in sub builders, add it to bnd.bnd");
                String bndfile = IO.collect(this.getPropertiesFile());
                bndfile = bndfile + "\n# Added by by bump\nBundle-Version: 0.0.0\n";
                IO.store((Object)bndfile, this.getPropertiesFile());
            }
        }
        finally {
            this.forceRefresh();
        }
    }

    boolean replace(File f, String pattern, String replacement) throws IOException {
        final Macro macro = this.getReplacer();
        Sed sed = new Sed(new Replacer(){

            @Override
            public String process(String line) {
                return macro.process(line);
            }
        }, f);
        sed.replace(pattern, replacement);
        return sed.doIt() > 0;
    }

    public void bump() throws Exception {
        this.bump(this.getProperty("-bumppolicy", "=+0"));
    }

    public void action(String command) throws Exception {
        this.action(command, new Object[0]);
    }

    public void action(String command, Object ... args) throws Exception {
        Map<String, Action> actions = this.getActions();
        Action a = actions.get(command);
        if (a == null) {
            a = new ReflectAction(command);
        }
        this.before(this, command);
        try {
            if (args.length == 0) {
                a.execute(this, command);
            } else {
                a.execute(this, args);
            }
        }
        catch (Exception t) {
            this.after(this, command, t);
            throw t;
        }
    }

    void before(Project p, String a) {
        List<CommandPlugin> testPlugins = this.getPlugins(CommandPlugin.class);
        for (CommandPlugin testPlugin : testPlugins) {
            testPlugin.before(this, a);
        }
    }

    void after(Project p, String a, Throwable t) {
        List<CommandPlugin> testPlugins = this.getPlugins(CommandPlugin.class);
        for (int i = testPlugins.size() - 1; i >= 0; --i) {
            testPlugins.get(i).after(this, a, t);
        }
    }

    public void refreshAll() {
        this.workspace.refresh();
        this.refresh();
    }

    public void script(String type, String script) throws Exception {
        this.script(type, script, new Object[0]);
    }

    public void script(String type, String script, Object ... args) throws Exception {
        List<Scripter> scripters = this.getPlugins(Scripter.class);
        if (scripters.isEmpty()) {
            this.msgs.NoScripters_(script);
            return;
        }
        UTF8Properties p = new UTF8Properties(this.getProperties());
        for (int i = 0; i < args.length; ++i) {
            p.setProperty("" + i, Converter.cnv(String.class, args[i]));
        }
        scripters.get(0).eval(p, new StringReader(script));
    }

    public String _repos(String[] args) throws Exception {
        List<RepositoryPlugin> repos = this.getPlugins(RepositoryPlugin.class);
        ArrayList<String> names = new ArrayList<String>();
        for (RepositoryPlugin rp : repos) {
            names.add(rp.getName());
        }
        return Project.join(names, ", ");
    }

    public String _help(String[] args) throws Exception {
        if (args.length == 1) {
            return "Specify the option or header you want information for";
        }
        Syntax syntax = Syntax.HELP.get(args[1]);
        if (syntax == null) {
            return "No help for " + args[1];
        }
        String what = null;
        if (args.length > 2) {
            what = args[2];
        }
        if (what == null || what.equals("lead")) {
            return syntax.getLead();
        }
        if (what.equals("example")) {
            return syntax.getExample();
        }
        if (what.equals("pattern")) {
            return syntax.getPattern();
        }
        if (what.equals("values")) {
            return syntax.getValues();
        }
        return "Invalid type specified for help: lead, example, pattern, values";
    }

    public Collection<Container> getDeliverables() throws Exception {
        ArrayList<Container> result = new ArrayList<Container>();
        try (ProjectBuilder pb = this.getBuilder(null);){
            for (Builder builder : pb.getSubBuilders()) {
                Container c = new Container(this, builder.getBsn(), builder.getVersion(), Container.TYPE.PROJECT, this.getOutputFile(builder.getBsn(), builder.getVersion()), null, null, null);
                result.add(c);
            }
            ArrayList<Container> arrayList = result;
            return arrayList;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Builder getSubBuilder(File bndFile) throws Exception {
        bndFile = bndFile.getCanonicalFile();
        File base = this.getBase().getCanonicalFile();
        if (!bndFile.getAbsolutePath().startsWith(base.getAbsolutePath())) {
            return null;
        }
        ProjectBuilder pb = this.getBuilder(null);
        boolean close = true;
        try {
            for (Builder b : pb.getSubBuilders()) {
                File propertiesFile = b.getPropertiesFile();
                if (propertiesFile == null || !propertiesFile.getCanonicalFile().equals(bndFile)) continue;
                if (b == pb) {
                    close = false;
                } else {
                    pb.removeClose(b);
                }
                Builder builder = b;
                return builder;
            }
            Builder builder = null;
            return builder;
        }
        finally {
            if (close) {
                pb.close();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public ProjectBuilder getSubBuilder(String string) throws Exception {
        ProjectBuilder pb = this.getBuilder(null);
        boolean close = true;
        try {
            for (Builder b : pb.getSubBuilders()) {
                if (!b.getBsn().equals(string) && !b.getBsn().endsWith("." + string)) continue;
                if (b == pb) {
                    close = false;
                } else {
                    pb.removeClose(b);
                }
                ProjectBuilder projectBuilder = (ProjectBuilder)b;
                return projectBuilder;
            }
            ProjectBuilder projectBuilder = null;
            return projectBuilder;
        }
        finally {
            if (close) {
                pb.close();
            }
        }
    }

    public Container getDeliverable(String bsn, Map<String, String> attrs) throws Exception {
        try (ProjectBuilder pb = this.getBuilder(null);){
            for (Builder b : pb.getSubBuilders()) {
                if (!b.getBsn().equals(bsn)) continue;
                Container container = new Container(this, this.getOutputFile(bsn, b.getVersion()), attrs);
                return container;
            }
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Deprecated
    public Collection<? extends Builder> getSubBuilders() throws Exception {
        ProjectBuilder pb = this.getBuilder(null);
        boolean close = true;
        try {
            List<Builder> builders = pb.getSubBuilders();
            for (Builder b : builders) {
                if (b == pb) {
                    close = false;
                    continue;
                }
                pb.removeClose(b);
            }
            List<Builder> list = builders;
            return list;
        }
        finally {
            if (close) {
                pb.close();
            }
        }
    }

    Collection<File> toFile(Collection<Container> containers) throws Exception {
        ArrayList<File> files = new ArrayList<File>();
        for (Container container : containers) {
            container.contributeFiles(files, this);
        }
        return files;
    }

    public Collection<String> getRunVM() {
        Parameters hdr = this.getMergedParameters("-runvm");
        return hdr.keySet();
    }

    public Collection<String> getRunProgramArgs() {
        Parameters hdr = this.getMergedParameters("-runprogramargs");
        return hdr.keySet();
    }

    public Map<String, String> getRunProperties() {
        return OSGiHeader.parseProperties(this.mergeProperties("-runproperties"));
    }

    public ProjectLauncher getProjectLauncher() throws Exception {
        return this.getHandler(ProjectLauncher.class, this.getRunpath(), "Launcher-Plugin", "biz.aQute.launcher");
    }

    public ProjectTester getProjectTester() throws Exception {
        String defaultDefault = this.since(About._3_0) ? "biz.aQute.tester" : "biz.aQute.junit";
        return this.getHandler(ProjectTester.class, this.getTestpath(), "Tester-Plugin", this.getProperty("-tester", defaultDefault));
    }

    private <T> T getHandler(Class<T> target, Collection<Container> containers, String header, String defaultHandler) throws Exception {
        Class<T> handlerClass = target;
        List<Container> withDefault = Create.list();
        withDefault.addAll(containers);
        withDefault.addAll(this.getBundles(Strategy.HIGHEST, defaultHandler, null));
        logger.debug("candidates for handler {}: {}", target, withDefault);
        for (Container c : withDefault) {
            Class<?> clz;
            String launcher;
            Manifest manifest = c.getManifest();
            if (manifest == null || (launcher = manifest.getMainAttributes().getValue(header)) == null || (clz = this.getClass(launcher, c.getFile())) == null) continue;
            if (!target.isAssignableFrom(clz)) {
                this.msgs.IncompatibleHandler_For_(launcher, defaultHandler);
                continue;
            }
            logger.debug("found handler {} from {}", (Object)defaultHandler, (Object)c);
            handlerClass = clz.asSubclass(target);
            try {
                Constructor<T> constructor = handlerClass.getConstructor(Project.class, Container.class);
                return constructor.newInstance(this, c);
            }
            catch (Exception e) {
                Constructor<T> constructor = handlerClass.getConstructor(Project.class);
                return constructor.newInstance(this);
            }
        }
        throw new IllegalArgumentException("Default handler for " + header + " not found in " + defaultHandler);
    }

    public void setDelayRunDependencies(boolean x) {
        this.delayRunDependencies = x;
    }

    public void addClasspath(File f) {
        if (!f.isFile() && !f.isDirectory()) {
            this.msgs.AddingNonExistentFileToClassPath_(f);
        }
        Container container = new Container(f, null);
        this.classpath.add(container);
    }

    public void clearClasspath() {
        this.classpath.clear();
        this.unreferencedClasspathEntries.clear();
    }

    public Collection<Container> getClasspath() {
        return this.classpath;
    }

    /*
     * Exception decompiling
     */
    public Jar pack(String profile) throws Exception {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public void baseline() throws Exception {
        try (ProjectBuilder pb = this.getBuilder(null);){
            for (Builder b : pb.getSubBuilders()) {
                Jar build = b.build();
                this.getInfo(b);
            }
            this.getInfo(pb);
        }
    }

    public void verifyDependencies(boolean test) throws Exception {
        this.verifyDependencies("-runbundles", this.getRunbundles());
        this.verifyDependencies("-runpath", this.getRunpath());
        if (test) {
            this.verifyDependencies("-testpath", this.getTestpath());
        }
        this.verifyDependencies("-buildpath", this.getBuildpath());
    }

    private void verifyDependencies(String title, Collection<Container> path) throws Exception {
        ArrayList<String> msgs = new ArrayList<String>();
        for (Container c : new ArrayList<Container>(path)) {
            for (Container cc : c.getMembers()) {
                if (cc.getError() != null) {
                    msgs.add(cc + " - " + cc.getError());
                    continue;
                }
                if (cc.getFile().isFile() || cc.getFile().equals(cc.getProject().getOutput()) || cc.getFile().equals(cc.getProject().getTestOutput())) continue;
                msgs.add(cc + " file does not exists: " + cc.getFile());
            }
        }
        if (msgs.isEmpty()) {
            return;
        }
        this.error("%s: has errors: %s", title, Strings.join(msgs));
    }

    @Override
    public void report(Map<String, Object> table) throws Exception {
        super.report(table);
        this.report(table, true);
    }

    protected void report(Map<String, Object> table, boolean isProject) throws Exception {
        if (isProject) {
            table.put("Target", this.getTarget());
            table.put("Source", this.getSrc());
            table.put("Output", this.getOutput());
            File[] buildFiles = this.getBuildFiles();
            if (buildFiles != null) {
                table.put("BuildFiles", Arrays.asList(buildFiles));
            }
            table.put("Classpath", this.getClasspath());
            table.put("Actions", this.getActions());
            table.put("AllSourcePath", this.getAllsourcepath());
            table.put("BootClassPath", this.getBootclasspath());
            table.put("BuildPath", this.getBuildpath());
            table.put("Deliverables", this.getDeliverables());
            table.put("DependsOn", this.getDependson());
            table.put("SourcePath", this.getSourcePath());
        }
        table.put("RunPath", this.getRunpath());
        table.put("TestPath", this.getTestpath());
        table.put("RunProgramArgs", this.getRunProgramArgs());
        table.put("RunVM", this.getRunVM());
        table.put("Runfw", this.getRunFw());
        table.put("Runbundles", this.getRunbundles());
    }

    public void compile(boolean test) throws Exception {
        Command javac = this.getCommonJavac(false);
        javac.add("-d", this.getOutput().getAbsolutePath());
        StringBuilder buildpath = new StringBuilder();
        String buildpathDel = "";
        List<Container> bp = Container.flatten(this.getBuildpath());
        logger.debug("buildpath {}", this.getBuildpath());
        for (Container c : bp) {
            buildpath.append(buildpathDel).append(c.getFile().getAbsolutePath());
            buildpathDel = File.pathSeparator;
        }
        if (buildpath.length() != 0) {
            javac.add("-classpath", buildpath.toString());
        }
        ArrayList<File> sp = new ArrayList<File>(this.getAllsourcepath());
        StringBuilder sourcepath = new StringBuilder();
        String sourcepathDel = "";
        for (File sourceDir : sp) {
            sourcepath.append(sourcepathDel).append(sourceDir.getAbsolutePath());
            sourcepathDel = File.pathSeparator;
        }
        javac.add("-sourcepath", sourcepath.toString());
        Glob javaFiles = new Glob("*.java");
        List<File> files = javaFiles.getFiles(this.getSrc(), true, false);
        for (File file : files) {
            javac.add(file.getAbsolutePath());
        }
        if (files.isEmpty()) {
            logger.debug("Not compiled, no source files");
        } else {
            this.compile(javac, "src");
        }
        if (test) {
            javac = this.getCommonJavac(true);
            javac.add("-d", this.getTestOutput().getAbsolutePath());
            List<Container> tp = Container.flatten(this.getTestpath());
            for (Container c : tp) {
                buildpath.append(buildpathDel).append(c.getFile().getAbsolutePath());
                buildpathDel = File.pathSeparator;
            }
            if (buildpath.length() != 0) {
                javac.add("-classpath", buildpath.toString());
            }
            sourcepath.append(sourcepathDel).append(this.getTestSrc().getAbsolutePath());
            javac.add("-sourcepath", sourcepath.toString());
            javaFiles.getFiles(this.getTestSrc(), files, true, false);
            for (File file : files) {
                javac.add(file.getAbsolutePath());
            }
            if (files.isEmpty()) {
                logger.debug("Not compiled for test, no test src files");
            } else {
                this.compile(javac, "test");
            }
        }
    }

    private void compile(Command javac, String what) throws Exception {
        logger.debug("compile {} {}", (Object)what, (Object)javac);
        StringBuilder stdout = new StringBuilder();
        StringBuilder stderr = new StringBuilder();
        int n = javac.execute(stdout, stderr);
        logger.debug("javac stdout: {}", (Object)stdout);
        logger.debug("javac stderr: {}", (Object)stderr);
        if (n != 0) {
            this.error("javac failed %s", stderr);
        }
    }

    private Command getCommonJavac(boolean test) throws Exception {
        Command javac = new Command();
        javac.add(this.getProperty("javac", "javac"));
        String target = this.getProperty("javac.target", "1.6");
        String profile = this.getProperty("javac.profile", "");
        String source = this.getProperty("javac.source", "1.6");
        String debug = this.getProperty("javac.debug");
        if ("on".equalsIgnoreCase(debug) || "true".equalsIgnoreCase(debug)) {
            debug = "vars,source,lines";
        }
        Parameters options = new Parameters(this.getProperty("java.options"), this);
        boolean deprecation = Project.isTrue(this.getProperty("java.deprecation"));
        javac.add("-encoding", "UTF-8");
        javac.add("-source", source);
        javac.add("-target", target);
        if (!profile.isEmpty()) {
            javac.add("-profile", profile);
        }
        if (deprecation) {
            javac.add("-deprecation");
        }
        if (test || debug == null) {
            javac.add("-g:source,lines,vars" + debug);
        } else {
            javac.add("-g:" + debug);
        }
        for (String option : options.keySet()) {
            javac.add(option);
        }
        StringBuilder bootclasspath = new StringBuilder();
        String bootclasspathDel = "-Xbootclasspath/p:";
        List<Container> bcp = Container.flatten(this.getBootclasspath());
        for (Container c : bcp) {
            bootclasspath.append(bootclasspathDel).append(c.getFile().getAbsolutePath());
            bootclasspathDel = File.pathSeparator;
        }
        if (bootclasspath.length() != 0) {
            javac.add(bootclasspath.toString());
        }
        return javac;
    }

    public String _ide(String[] args) throws IOException {
        String deflt;
        if (args.length < 2) {
            this.error("The ${ide;<>} macro needs an argument", new Object[0]);
            return null;
        }
        if (this.ide == null) {
            this.ide = new UTF8Properties();
            File file = this.getFile(".settings/org.eclipse.jdt.core.prefs");
            if (!file.isFile()) {
                this.error("The ${ide;<>} macro requires a .settings/org.eclipse.jdt.core.prefs file in the project", new Object[0]);
                return null;
            }
            try (InputStream in = IO.stream(file);){
                this.ide.load(in);
            }
        }
        String string = deflt = args.length > 2 ? args[2] : null;
        if ("javac.target".equals(args[1])) {
            return this.ide.getProperty("org.eclipse.jdt.core.compiler.codegen.targetPlatform", deflt);
        }
        if ("javac.source".equals(args[1])) {
            return this.ide.getProperty("org.eclipse.jdt.core.compiler.source", deflt);
        }
        return null;
    }

    public Map<String, Version> getVersions() throws Exception {
        if (this.versionMap.isEmpty()) {
            try (ProjectBuilder pb = this.getBuilder(null);){
                for (Builder builder : pb.getSubBuilders()) {
                    String v = builder.getVersion();
                    if (v == null) {
                        v = "0";
                    } else if (!Verifier.isVersion(v = Analyzer.cleanupVersion(v))) continue;
                    Version version = new Version(v);
                    this.versionMap.put(builder.getBsn(), version);
                }
            }
        }
        return new LinkedHashMap<String, Version>(this.versionMap);
    }

    public Collection<String> getBsns() throws Exception {
        return new ArrayList<String>(this.getVersions().keySet());
    }

    public Version getVersion(String bsn) throws Exception {
        Version version = this.getVersions().get(bsn);
        if (version == null) {
            throw new IllegalArgumentException("Bsn " + bsn + " does not exist in project " + this.getName());
        }
        return version;
    }

    public Packages getExports() {
        return this.exportedPackages;
    }

    public Packages getImports() {
        return this.importedPackages;
    }

    public Packages getContained() {
        return this.containedPackages;
    }

    public void remove() throws Exception {
        this.getWorkspace().removeProject(this);
        IO.delete(this.getBase());
    }

    public boolean getRunKeep() {
        return this.is("-runkeep");
    }

    public void setPackageInfo(String packageName, Version newVersion) throws Exception {
        this.packageInfo.setPackageInfo(packageName, newVersion);
    }

    public Version getPackageInfo(String packageName) throws Exception {
        return this.packageInfo.getPackageInfo(packageName);
    }

    public void preRelease() {
        for (ReleaseBracketingPlugin rp : this.getWorkspace().getPlugins(ReleaseBracketingPlugin.class)) {
            rp.begin(this);
        }
    }

    public void postRelease() {
        for (ReleaseBracketingPlugin rp : this.getWorkspace().getPlugins(ReleaseBracketingPlugin.class)) {
            rp.end(this);
        }
    }

    public void copy(RepositoryPlugin source, String filter, RepositoryPlugin destination) throws Exception {
        this.copy(source, filter == null ? null : new Instructions(filter), destination);
    }

    public void copy(RepositoryPlugin source, Instructions filter, RepositoryPlugin destination) throws Exception {
        assert (source != null);
        assert (destination != null);
        logger.info("copy from repo {} to {} with filter {}", new Object[]{source, destination, filter});
        for (String bsn : source.list(null)) {
            for (Version version : source.versions(bsn)) {
                if (filter != null && !filter.matches(bsn)) continue;
                logger.info("copy {}:{}", (Object)bsn, (Object)version);
                File file = source.get(bsn, version, null, new RepositoryPlugin.DownloadListener[0]);
                if (!file.getName().endsWith(".jar")) continue;
                try {
                    InputStream in = IO.stream(file);
                    Throwable throwable = null;
                    try {
                        RepositoryPlugin.PutOptions po = new RepositoryPlugin.PutOptions();
                        po.bsn = bsn;
                        po.context = null;
                        po.type = "bundle";
                        po.version = version;
                        RepositoryPlugin.PutResult put = destination.put(in, po);
                    }
                    catch (Throwable throwable2) {
                        throwable = throwable2;
                        throw throwable2;
                    }
                    finally {
                        if (in == null) continue;
                        if (throwable != null) {
                            try {
                                in.close();
                            }
                            catch (Throwable x2) {
                                throwable.addSuppressed(x2);
                            }
                            continue;
                        }
                        in.close();
                    }
                }
                catch (Exception e) {
                    logger.error("Failed to copy {}-{}", new Object[]{e, bsn, version});
                    this.error("Failed to copy %s:%s from %s to %s, error: %s", bsn, version, source, destination, e);
                }
            }
        }
    }

    private static class RepoFilter {
        private Pattern[] patterns;

        RepoFilter(Pattern[] patterns) {
            this.patterns = patterns;
        }

        boolean match(RepositoryPlugin repo) {
            if (this.patterns == null) {
                return true;
            }
            for (Pattern pattern : this.patterns) {
                if (!pattern.matcher(repo.getName()).matches()) continue;
                return true;
            }
            return false;
        }
    }

    static class RefreshData {
        Parameters installRepositories;

        RefreshData() {
        }
    }
}

