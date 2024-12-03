/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.build;

import aQute.bnd.build.Container;
import aQute.bnd.build.Project;
import aQute.bnd.build.Run;
import aQute.bnd.differ.Baseline;
import aQute.bnd.differ.DiffPluginImpl;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Builder;
import aQute.bnd.osgi.Descriptors;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.Instructions;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Packages;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Verifier;
import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.service.diff.Diff;
import aQute.bnd.service.repository.InfoRepository;
import aQute.bnd.service.repository.Phase;
import aQute.bnd.service.repository.SearchableRepository;
import aQute.bnd.version.Version;
import aQute.lib.collections.SortedList;
import aQute.lib.io.IO;
import aQute.service.reporter.Report;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProjectBuilder
extends Builder {
    private static final Logger logger = LoggerFactory.getLogger(ProjectBuilder.class);
    private final DiffPluginImpl differ = new DiffPluginImpl();
    Project project;
    boolean initialized;
    private static final Pattern PATTERN_EXPORT_PACKAGE = Pattern.compile(Pattern.quote("Export-Package"), 2);
    private static final Pattern PATTERN_EXPORT_CONTENTS = Pattern.compile(Pattern.quote("-exportcontents"), 2);
    private static final Pattern PATTERN_VERSION_ANNOTATION = Pattern.compile("@(?:\\p{javaJavaIdentifierStart}\\p{javaJavaIdentifierPart}*\\.)*Version\\s*([^)]+)");
    private static final Pattern PATTERN_VERSION_PACKAGEINFO = Pattern.compile("^\\s*version\\s.*$");

    public ProjectBuilder(Project project) {
        super(project);
        this.project = project;
    }

    public ProjectBuilder(ProjectBuilder builder) {
        super(builder);
        this.project = builder.project;
    }

    @Override
    public long lastModified() {
        return Math.max(this.project.lastModified(), super.lastModified());
    }

    @Override
    protected Object[] getMacroDomains() {
        return new Object[]{this.project, this.project.getWorkspace()};
    }

    @Override
    public Builder getSubBuilder() throws Exception {
        return this.project.getBuilder(this);
    }

    public Project getProject() {
        return this.project;
    }

    @Override
    public void init() {
        try {
            if (!this.initialized) {
                this.initialized = true;
                this.doRequireBnd();
                for (Container file : this.project.getClasspath()) {
                    this.addClasspath(file);
                }
                File output = this.project.getOutput();
                if (output.exists()) {
                    this.addClasspath(output);
                }
                for (Container container : this.project.getBuildpath()) {
                    this.addClasspath(container);
                }
                for (Container container : this.project.getBootclasspath()) {
                    this.addClasspath(container);
                }
                for (File file : this.project.getAllsourcepath()) {
                    this.addSourcepath(file);
                }
            }
        }
        catch (Exception e) {
            this.msgs.Unexpected_Error_("ProjectBuilder init", e);
        }
    }

    public void addClasspath(Container c) throws IOException {
        Jar jar = new Jar(c.getFile());
        super.addClasspath(jar);
        this.project.unreferencedClasspathEntries.put(jar.getName(), c);
    }

    @Override
    public List<Jar> getClasspath() {
        this.init();
        return super.getClasspath();
    }

    @Override
    protected void changedFile(File f) {
        this.project.getWorkspace().changedFile(f);
    }

    @Override
    public void doBaseline(Jar dot) throws Exception {
        String diffignore = this.project.getProperty("-diffignore");
        logger.debug("ignore headers & paths {}", (Object)diffignore);
        this.differ.setIgnore(diffignore);
        Instructions diffpackages = new Instructions(new Parameters(this.project.getProperty("-diffpackages"), this));
        logger.debug("diffpackages {}", (Object)diffpackages);
        try (Jar fromRepo = this.getBaselineJar();){
            SearchableRepository.ResourceDescriptor descriptor;
            RepositoryPlugin rr;
            if (fromRepo == null) {
                logger.debug("No baseline jar {}", (Object)this.getProperty("-baseline"));
                return;
            }
            Version newer = new Version(this.getVersion());
            Version older = new Version(fromRepo.getVersion());
            if (!this.getBsn().equals(fromRepo.getBsn())) {
                this.error("The symbolic name of this project (%s) is not the same as the baseline: %s", this.getBsn(), fromRepo.getBsn());
                return;
            }
            if (newer.getWithoutQualifier().equals(older.getWithoutQualifier()) && (rr = this.getBaselineRepo()) instanceof InfoRepository && (descriptor = ((InfoRepository)rr).getDescriptor(this.getBsn(), older)) != null && descriptor.phase != Phase.STAGING) {
                this.error("Baselining %s against same version %s but the repository says the older repository version is not the required %s but is instead %s", new Object[]{this.getBsn(), this.getVersion(), Phase.STAGING, descriptor.phase});
                return;
            }
            logger.debug("baseline {}-{} against: {}", new Object[]{this.getBsn(), this.getVersion(), fromRepo.getName()});
            Baseline baseliner = new Baseline(this, this.differ);
            Set<Baseline.Info> infos = baseliner.baseline(dot, fromRepo, diffpackages);
            if (infos.isEmpty()) {
                logger.debug("no deltas");
            }
            StringBuffer sb = new StringBuffer();
            try (Formatter f = new Formatter(sb, Locale.US);){
                for (Baseline.Info info : infos) {
                    if (!info.mismatch) continue;
                    sb.setLength(0);
                    Diff packageDiff = info.packageDiff;
                    f.format("Baseline mismatch for package %s, %s change. Current is %s, repo is %s, suggest %s or %s%n%#S", new Object[]{packageDiff.getName(), packageDiff.getDelta(), info.newerVersion, info.olderVersion != null && info.olderVersion.equals(Version.LOWEST) ? Character.valueOf('-') : info.olderVersion, info.suggestedVersion != null && info.suggestedVersion.compareTo(info.newerVersion) <= 0 ? "ok" : info.suggestedVersion, info.suggestedIfProviders == null ? "-" : info.suggestedIfProviders, packageDiff});
                    Reporter.SetLocation l = this.error("%s", f.toString());
                    l.header("-baseline");
                    this.fillInLocationForPackageInfo(l.location(), packageDiff.getName());
                    if (this.getPropertiesFile() != null) {
                        l.file(this.getPropertiesFile().getAbsolutePath());
                    }
                    l.details(info);
                }
                Baseline.BundleInfo binfo = baseliner.getBundleInfo();
                if (binfo.mismatch) {
                    sb.setLength(0);
                    f.format("The bundle version (%s/%s) is too low, must be at least %s%n%#S", binfo.olderVersion, binfo.newerVersion, binfo.suggestedVersion, baseliner.getDiff());
                    Reporter.SetLocation error = this.error("%s", f.toString());
                    error.context("Baselining");
                    error.header("Bundle-Version");
                    error.details(binfo);
                    Processor.FileLine fl = this.getHeader(Pattern.compile("^Bundle-Version", 8));
                    if (fl != null) {
                        error.file(fl.file.getAbsolutePath());
                        error.line(fl.line);
                        error.length(fl.length);
                    }
                }
            }
        }
    }

    public void fillInLocationForPackageInfo(Report.Location location, String packageName) throws Exception {
        Processor.FileLine fl;
        Processor.FileLine fl2;
        Parameters eps = this.getExportPackage();
        Attrs attrs = eps.get(packageName);
        if (attrs != null && attrs.containsKey("version") && (fl2 = this.getHeader(PATTERN_EXPORT_PACKAGE)) != null) {
            location.file = fl2.file.getAbsolutePath();
            location.line = fl2.line;
            location.length = fl2.length;
            return;
        }
        Parameters ecs = this.getExportContents();
        attrs = ecs.get(packageName);
        if (attrs != null && attrs.containsKey("version") && (fl = this.getHeader(PATTERN_EXPORT_CONTENTS)) != null) {
            location.file = fl.file.getAbsolutePath();
            location.line = fl.line;
            location.length = fl.length;
            return;
        }
        String path = packageName.replace('.', '/');
        for (File src : this.project.getSourcePath()) {
            Processor.FileLine fl3;
            File packageDir = IO.getFile(src, path);
            File pi = IO.getFile(packageDir, "package-info.java");
            if (pi.isFile() && (fl3 = ProjectBuilder.findHeader(pi, PATTERN_VERSION_ANNOTATION)) != null) {
                location.file = fl3.file.getAbsolutePath();
                location.line = fl3.line;
                location.length = fl3.length;
                return;
            }
            pi = IO.getFile(packageDir, "packageinfo");
            if (!pi.isFile() || (fl3 = ProjectBuilder.findHeader(pi, PATTERN_VERSION_PACKAGEINFO)) == null) continue;
            location.file = fl3.file.getAbsolutePath();
            location.line = fl3.line;
            location.length = fl3.length;
            return;
        }
    }

    public Jar getLastRevision() throws Exception {
        RepositoryPlugin releaseRepo = this.getReleaseRepo();
        SortedSet<Version> versions = releaseRepo.versions(this.getBsn());
        if (versions.isEmpty()) {
            return null;
        }
        Jar jar = new Jar(releaseRepo.get(this.getBsn(), versions.last(), null, new RepositoryPlugin.DownloadListener[0]));
        this.addClose(jar);
        return jar;
    }

    public Jar getBaselineJar() throws Exception {
        String bl = this.getProperty("-baseline");
        if (bl == null || "none".equals(bl)) {
            return null;
        }
        Instructions baselines = new Instructions(this.getProperty("-baseline"));
        if (baselines.isEmpty()) {
            return null;
        }
        RepositoryPlugin repo = this.getBaselineRepo();
        if (repo == null) {
            return null;
        }
        String bsn = this.getBsn();
        Version version = new Version(this.getVersion());
        SortedSet<Version> versions = this.removeStagedAndFilter(repo.versions(bsn), repo, bsn);
        if (versions.isEmpty()) {
            Version v = Version.parseVersion(this.getVersion()).getWithoutQualifier();
            if (v.compareTo(Version.ONE) > 0) {
                this.warning("There is no baseline for %s in the baseline repo %s. The build is for version %s, which is higher than 1.0.0 which suggests that there should be a prior version.", this.getBsn(), repo, v);
            }
            return null;
        }
        for (Map.Entry<Instruction, Attrs> e : baselines.entrySet()) {
            File file;
            Version target;
            if (!e.getKey().matches(bsn)) continue;
            Attrs attrs = e.getValue();
            if (attrs.containsKey("version")) {
                String v = attrs.get("version");
                if (!Verifier.isVersion(v)) {
                    this.error("Not a valid version in %s %s", "-baseline", v);
                    return null;
                }
                Version base = new Version(v);
                SortedSet<Version> later = versions.tailSet(base);
                if (later.isEmpty()) {
                    this.error("For baselineing %s-%s, specified version %s not found", bsn, version, base);
                    return null;
                }
                target = later.first();
            } else {
                if (attrs.containsKey("file")) {
                    File f = this.getProject().getFile(attrs.get("file"));
                    if (f != null && f.isFile()) {
                        Jar jar = new Jar(f);
                        this.addClose(jar);
                        return jar;
                    }
                    this.error("Specified file for baseline but could not find it %s", f);
                    return null;
                }
                target = versions.last();
            }
            if (target.getWithoutQualifier().compareTo(version.getWithoutQualifier()) > 0) {
                this.error("The baseline version %s is higher than the current version %s for %s in %s", target, version, bsn, repo);
                return null;
            }
            if (target.getWithoutQualifier().compareTo(version.getWithoutQualifier()) == 0 && this.isPedantic()) {
                this.warning("Baselining against jar", new Object[0]);
            }
            if ((file = repo.get(bsn, target, attrs, new RepositoryPlugin.DownloadListener[0])) == null || !file.isFile()) {
                this.error("Decided on version %s-%s but cannot get file from repo %s", bsn, version, repo);
                return null;
            }
            Jar jar = new Jar(file);
            this.addClose(jar);
            return jar;
        }
        return null;
    }

    private SortedSet<Version> removeStagedAndFilter(SortedSet<Version> versions, RepositoryPlugin repo, String bsn) throws Exception {
        ArrayList<Version> filtered = new ArrayList<Version>(versions);
        Collections.reverse(filtered);
        InfoRepository ir = repo instanceof InfoRepository ? (InfoRepository)repo : null;
        Version last = null;
        Iterator i = filtered.iterator();
        while (i.hasNext()) {
            Version v = (Version)i.next();
            Version current = v.getWithoutQualifier();
            if (last != null && current.equals(last)) {
                i.remove();
                continue;
            }
            if (ir != null && !this.isMaster(ir, bsn, v)) {
                i.remove();
            }
            last = current;
        }
        SortedList<Version> set = new SortedList<Version>(filtered);
        logger.debug("filtered for only latest staged: {} from {} in range ", set, versions);
        return set;
    }

    private boolean isMaster(InfoRepository repo, String bsn, Version v) throws Exception {
        SearchableRepository.ResourceDescriptor descriptor = repo.getDescriptor(bsn, v);
        if (descriptor == null) {
            return true;
        }
        return descriptor.phase == Phase.MASTER;
    }

    private RepositoryPlugin getReleaseRepo() {
        String repoName = this.getProperty("-releaserepo");
        List<RepositoryPlugin> repos = this.getPlugins(RepositoryPlugin.class);
        for (RepositoryPlugin r : repos) {
            if (!r.canWrite() || repoName != null && !r.getName().equals(repoName)) continue;
            return r;
        }
        if (repoName == null) {
            this.error("Could not find a writable repo for the release repo (-releaserepo is not set)", new Object[0]);
        } else {
            this.error("No such -releaserepo %s found", repoName);
        }
        return null;
    }

    private RepositoryPlugin getBaselineRepo() {
        String repoName = this.getProperty("-baselinerepo");
        if (repoName == null) {
            return this.getReleaseRepo();
        }
        List<RepositoryPlugin> repos = this.getPlugins(RepositoryPlugin.class);
        for (RepositoryPlugin r : repos) {
            if (!r.getName().equals(repoName)) continue;
            return r;
        }
        this.error("Could not find -baselinerepo %s", repoName);
        return null;
    }

    @Override
    public void report(Map<String, Object> table) throws Exception {
        super.report(table);
        table.put("Baseline repo", this.getBaselineRepo());
        table.put("Release repo", this.getReleaseRepo());
    }

    @Override
    public String toString() {
        return this.getBsn();
    }

    public List<Run> getExportedRuns() throws Exception {
        Instructions runspec = new Instructions(this.getProperty("-export"));
        ArrayList<Run> runs = new ArrayList<Run>();
        Map<File, Attrs> files = runspec.select(this.getBase());
        for (Map.Entry<File, Attrs> e : files.entrySet()) {
            Run run = new Run(this.project.getWorkspace(), this.getBase(), e.getKey());
            for (Map.Entry<String, String> ee : e.getValue().entrySet()) {
                run.setProperty(ee.getKey(), ee.getValue());
            }
            runs.add(run);
        }
        return runs;
    }

    @Override
    public Jar[] builds() throws Exception {
        this.project.exportedPackages.clear();
        this.project.importedPackages.clear();
        this.project.containedPackages.clear();
        Jar[] jars = super.builds();
        if (this.isOk()) {
            for (Run export : this.getExportedRuns()) {
                this.addClose(export);
                if (export.getProperty("Bundle-SymbolicName") == null) {
                    export.setProperty("Bundle-SymbolicName", this.getBsn() + ".run");
                }
                Jar pack = export.pack(this.getProperty("-profile"));
                this.getInfo(export);
                if (pack == null) continue;
                jars = this.concat(Jar.class, jars, pack);
                this.addClose(pack);
            }
        }
        return jars;
    }

    @Override
    protected void startBuild(Builder builder) throws Exception {
        super.startBuild(builder);
        this.project.versionMap.remove(builder.getBsn());
        if (!this.project.isNoBundles() && builder.getJar() == null && builder.getProperty("-resourceonly") == null && builder.getProperty("Private-Package") == null && builder.getProperty("Export-Package") == null && builder.getProperty("Include-Resource") == null && builder.getProperty("-includeresource") == null && this.project.getOutput().isDirectory()) {
            Jar outputDirJar = new Jar(this.project.getName(), this.project.getOutput());
            outputDirJar.setManifest(new Manifest());
            builder.setJar(outputDirJar);
        }
    }

    @Override
    protected void doneBuild(Builder builder) throws Exception {
        this.project.exportedPackages.putAll(builder.getExports());
        this.project.importedPackages.putAll(builder.getImports());
        this.project.containedPackages.putAll(builder.getContained());
        this.xrefClasspath(this.project.unreferencedClasspathEntries, builder.getImports());
        this.xrefClasspath(this.project.unreferencedClasspathEntries, builder.getContained());
        Version version = new Version(ProjectBuilder.cleanupVersion(builder.getVersion()));
        this.project.versionMap.put(builder.getBsn(), version);
        super.doneBuild(builder);
    }

    private void xrefClasspath(Map<String, Container> unreferencedClasspathEntries, Packages packages) {
        for (Attrs attrs : packages.values()) {
            String from = attrs.get("from:");
            if (from == null) continue;
            unreferencedClasspathEntries.remove(from);
        }
    }

    @Override
    public String getSourceFileFor(Descriptors.TypeRef type) throws Exception {
        return super.getSourceFileFor(type, this.getSourcePath());
    }
}

