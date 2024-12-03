/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.taskdefs.LogOutputStream;
import org.apache.tools.ant.taskdefs.PumpStreamHandler;
import org.apache.tools.ant.types.Commandline;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.EnumeratedAttribute;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileProvider;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JavaEnvUtils;

public class Javadoc
extends Task {
    private static final String LOAD_FRAME = "function loadFrames() {";
    private static final int LOAD_FRAME_LEN = "function loadFrames() {".length();
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private final Commandline cmd = new Commandline();
    private boolean failOnError = false;
    private boolean failOnWarning = false;
    private Path sourcePath = null;
    private File destDir = null;
    private final List<SourceFile> sourceFiles = new Vector<SourceFile>();
    private final List<PackageName> packageNames = new Vector<PackageName>();
    private final List<PackageName> excludePackageNames = new Vector<PackageName>(1);
    private final List<PackageName> moduleNames = new ArrayList<PackageName>();
    private boolean author = true;
    private boolean version = true;
    private DocletInfo doclet = null;
    private Path classpath = null;
    private Path bootclasspath = null;
    private Path modulePath = null;
    private Path moduleSourcePath = null;
    private String group = null;
    private String packageList = null;
    private final List<LinkArgument> links = new Vector<LinkArgument>();
    private final List<GroupArgument> groups = new Vector<GroupArgument>();
    private final List<Object> tags = new Vector<Object>();
    private boolean useDefaultExcludes = true;
    private Html doctitle = null;
    private Html header = null;
    private Html footer = null;
    private Html bottom = null;
    private boolean useExternalFile = false;
    private String source = null;
    private boolean linksource = false;
    private boolean breakiterator = false;
    private String noqualifier;
    private boolean includeNoSourcePackages = false;
    private String executable = null;
    private boolean docFilesSubDirs = false;
    private String excludeDocFilesSubDir = null;
    private String docEncoding = null;
    private boolean postProcessGeneratedJavadocs = true;
    private final ResourceCollectionContainer nestedSourceFiles = new ResourceCollectionContainer();
    private final List<DirSet> packageSets = new Vector<DirSet>();
    static final String[] SCOPE_ELEMENTS = new String[]{"overview", "packages", "types", "constructors", "methods", "fields"};

    private void addArgIf(boolean b, String arg) {
        if (b) {
            this.cmd.createArgument().setValue(arg);
        }
    }

    private void addArgIfNotEmpty(String key, String value) {
        if (value == null || value.isEmpty()) {
            this.log("Warning: Leaving out empty argument '" + key + "'", 1);
        } else {
            this.cmd.createArgument().setValue(key);
            this.cmd.createArgument().setValue(value);
        }
    }

    public void setUseExternalFile(boolean b) {
        this.useExternalFile = b;
    }

    public void setDefaultexcludes(boolean useDefaultExcludes) {
        this.useDefaultExcludes = useDefaultExcludes;
    }

    public void setMaxmemory(String max) {
        this.cmd.createArgument().setValue("-J-Xmx" + max);
    }

    public void setAdditionalparam(String add) {
        this.cmd.createArgument().setLine(add);
    }

    public Commandline.Argument createArg() {
        return this.cmd.createArgument();
    }

    public void setSourcepath(Path src) {
        if (this.sourcePath == null) {
            this.sourcePath = src;
        } else {
            this.sourcePath.append(src);
        }
    }

    public Path createSourcepath() {
        if (this.sourcePath == null) {
            this.sourcePath = new Path(this.getProject());
        }
        return this.sourcePath.createPath();
    }

    public void setSourcepathRef(Reference r) {
        this.createSourcepath().setRefid(r);
    }

    public void setModulePath(Path mp) {
        if (this.modulePath == null) {
            this.modulePath = mp;
        } else {
            this.modulePath.append(mp);
        }
    }

    public Path createModulePath() {
        if (this.modulePath == null) {
            this.modulePath = new Path(this.getProject());
        }
        return this.modulePath.createPath();
    }

    public void setModulePathref(Reference r) {
        this.createModulePath().setRefid(r);
    }

    public void setModuleSourcePath(Path mp) {
        if (this.moduleSourcePath == null) {
            this.moduleSourcePath = mp;
        } else {
            this.moduleSourcePath.append(mp);
        }
    }

    public Path createModuleSourcePath() {
        if (this.moduleSourcePath == null) {
            this.moduleSourcePath = new Path(this.getProject());
        }
        return this.moduleSourcePath.createPath();
    }

    public void setModuleSourcePathref(Reference r) {
        this.createModuleSourcePath().setRefid(r);
    }

    public void setDestdir(File dir) {
        this.destDir = dir;
        this.cmd.createArgument().setValue("-d");
        this.cmd.createArgument().setFile(this.destDir);
    }

    public void setSourcefiles(String src) {
        StringTokenizer tok = new StringTokenizer(src, ",");
        while (tok.hasMoreTokens()) {
            String f = tok.nextToken();
            SourceFile sf = new SourceFile();
            sf.setFile(this.getProject().resolveFile(f.trim()));
            this.addSource(sf);
        }
    }

    public void addSource(SourceFile sf) {
        this.sourceFiles.add(sf);
    }

    public void setPackagenames(String packages) {
        StringTokenizer tok = new StringTokenizer(packages, ",");
        while (tok.hasMoreTokens()) {
            String p = tok.nextToken();
            PackageName pn = new PackageName();
            pn.setName(p);
            this.addPackage(pn);
        }
    }

    public void setModulenames(String modules) {
        for (String m : modules.split(",")) {
            PackageName mn = new PackageName();
            mn.setName(m);
            this.addModule(mn);
        }
    }

    public void addPackage(PackageName pn) {
        this.packageNames.add(pn);
    }

    public void addModule(PackageName mn) {
        this.moduleNames.add(mn);
    }

    public void setExcludePackageNames(String packages) {
        StringTokenizer tok = new StringTokenizer(packages, ",");
        while (tok.hasMoreTokens()) {
            String p = tok.nextToken();
            PackageName pn = new PackageName();
            pn.setName(p);
            this.addExcludePackage(pn);
        }
    }

    public void addExcludePackage(PackageName pn) {
        this.excludePackageNames.add(pn);
    }

    public void setOverview(File f) {
        this.cmd.createArgument().setValue("-overview");
        this.cmd.createArgument().setFile(f);
    }

    public void setPublic(boolean b) {
        this.addArgIf(b, "-public");
    }

    public void setProtected(boolean b) {
        this.addArgIf(b, "-protected");
    }

    public void setPackage(boolean b) {
        this.addArgIf(b, "-package");
    }

    public void setPrivate(boolean b) {
        this.addArgIf(b, "-private");
    }

    public void setAccess(AccessType at) {
        this.cmd.createArgument().setValue("-" + at.getValue());
    }

    public void setDoclet(String docletName) {
        if (this.doclet == null) {
            this.doclet = new DocletInfo();
            this.doclet.setProject(this.getProject());
        }
        this.doclet.setName(docletName);
    }

    public void setDocletPath(Path docletPath) {
        if (this.doclet == null) {
            this.doclet = new DocletInfo();
            this.doclet.setProject(this.getProject());
        }
        this.doclet.setPath(docletPath);
    }

    public void setDocletPathRef(Reference r) {
        if (this.doclet == null) {
            this.doclet = new DocletInfo();
            this.doclet.setProject(this.getProject());
        }
        this.doclet.createPath().setRefid(r);
    }

    public DocletInfo createDoclet() {
        if (this.doclet == null) {
            this.doclet = new DocletInfo();
        }
        return this.doclet;
    }

    public void addTaglet(ExtensionInfo tagletInfo) {
        this.tags.add(tagletInfo);
    }

    public void setOld(boolean b) {
        this.log("Javadoc 1.4 doesn't support the -1.1 switch anymore", 1);
    }

    public void setClasspath(Path path) {
        if (this.classpath == null) {
            this.classpath = path;
        } else {
            this.classpath.append(path);
        }
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspathRef(Reference r) {
        this.createClasspath().setRefid(r);
    }

    public void setBootclasspath(Path path) {
        if (this.bootclasspath == null) {
            this.bootclasspath = path;
        } else {
            this.bootclasspath.append(path);
        }
    }

    public Path createBootclasspath() {
        if (this.bootclasspath == null) {
            this.bootclasspath = new Path(this.getProject());
        }
        return this.bootclasspath.createPath();
    }

    public void setBootClasspathRef(Reference r) {
        this.createBootclasspath().setRefid(r);
    }

    @Deprecated
    public void setExtdirs(String path) {
        this.cmd.createArgument().setValue("-extdirs");
        this.cmd.createArgument().setValue(path);
    }

    public void setExtdirs(Path path) {
        this.cmd.createArgument().setValue("-extdirs");
        this.cmd.createArgument().setPath(path);
    }

    public void setVerbose(boolean b) {
        this.addArgIf(b, "-verbose");
    }

    public void setLocale(String locale) {
        this.cmd.createArgument(true).setValue(locale);
        this.cmd.createArgument(true).setValue("-locale");
    }

    public void setEncoding(String enc) {
        this.cmd.createArgument().setValue("-encoding");
        this.cmd.createArgument().setValue(enc);
    }

    public void setVersion(boolean b) {
        this.version = b;
    }

    public void setUse(boolean b) {
        this.addArgIf(b, "-use");
    }

    public void setAuthor(boolean b) {
        this.author = b;
    }

    public void setSplitindex(boolean b) {
        this.addArgIf(b, "-splitindex");
    }

    public void setWindowtitle(String title) {
        this.addArgIfNotEmpty("-windowtitle", title);
    }

    public void setDoctitle(String doctitle) {
        Html h = new Html();
        h.addText(doctitle);
        this.addDoctitle(h);
    }

    public void addDoctitle(Html text) {
        this.doctitle = text;
    }

    public void setHeader(String header) {
        Html h = new Html();
        h.addText(header);
        this.addHeader(h);
    }

    public void addHeader(Html text) {
        this.header = text;
    }

    public void setFooter(String footer) {
        Html h = new Html();
        h.addText(footer);
        this.addFooter(h);
    }

    public void addFooter(Html text) {
        this.footer = text;
    }

    public void setBottom(String bottom) {
        Html h = new Html();
        h.addText(bottom);
        this.addBottom(h);
    }

    public void addBottom(Html text) {
        this.bottom = text;
    }

    public void setLinkoffline(String src) {
        LinkArgument le = this.createLink();
        le.setOffline(true);
        String linkOfflineError = "The linkoffline attribute must include a URL and a package-list file location separated by a space";
        if (src.trim().isEmpty()) {
            throw new BuildException("The linkoffline attribute must include a URL and a package-list file location separated by a space");
        }
        StringTokenizer tok = new StringTokenizer(src, " ", false);
        le.setHref(tok.nextToken());
        if (!tok.hasMoreTokens()) {
            throw new BuildException("The linkoffline attribute must include a URL and a package-list file location separated by a space");
        }
        le.setPackagelistLoc(this.getProject().resolveFile(tok.nextToken()));
    }

    public void setGroup(String src) {
        this.group = src;
    }

    public void setLink(String src) {
        this.createLink().setHref(src);
    }

    public void setNodeprecated(boolean b) {
        this.addArgIf(b, "-nodeprecated");
    }

    public void setNodeprecatedlist(boolean b) {
        this.addArgIf(b, "-nodeprecatedlist");
    }

    public void setNotree(boolean b) {
        this.addArgIf(b, "-notree");
    }

    public void setNoindex(boolean b) {
        this.addArgIf(b, "-noindex");
    }

    public void setNohelp(boolean b) {
        this.addArgIf(b, "-nohelp");
    }

    public void setNonavbar(boolean b) {
        this.addArgIf(b, "-nonavbar");
    }

    public void setSerialwarn(boolean b) {
        this.addArgIf(b, "-serialwarn");
    }

    public void setStylesheetfile(File f) {
        this.cmd.createArgument().setValue("-stylesheetfile");
        this.cmd.createArgument().setFile(f);
    }

    public void setHelpfile(File f) {
        this.cmd.createArgument().setValue("-helpfile");
        this.cmd.createArgument().setFile(f);
    }

    public void setDocencoding(String enc) {
        this.cmd.createArgument().setValue("-docencoding");
        this.cmd.createArgument().setValue(enc);
        this.docEncoding = enc;
    }

    public void setPackageList(String src) {
        this.packageList = src;
    }

    public LinkArgument createLink() {
        LinkArgument la = new LinkArgument();
        this.links.add(la);
        return la;
    }

    public TagArgument createTag() {
        TagArgument ta = new TagArgument();
        this.tags.add(ta);
        return ta;
    }

    public GroupArgument createGroup() {
        GroupArgument ga = new GroupArgument();
        this.groups.add(ga);
        return ga;
    }

    public void setCharset(String src) {
        this.addArgIfNotEmpty("-charset", src);
    }

    public void setFailonerror(boolean b) {
        this.failOnError = b;
    }

    public void setFailonwarning(boolean b) {
        this.failOnWarning = b;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public void addPackageset(DirSet packageSet) {
        this.packageSets.add(packageSet);
    }

    public void addFileset(FileSet fs) {
        this.createSourceFiles().add(fs);
    }

    public ResourceCollectionContainer createSourceFiles() {
        return this.nestedSourceFiles;
    }

    public void setLinksource(boolean b) {
        this.linksource = b;
    }

    public void setBreakiterator(boolean b) {
        this.breakiterator = b;
    }

    public void setNoqualifier(String noqualifier) {
        this.noqualifier = noqualifier;
    }

    public void setIncludeNoSourcePackages(boolean b) {
        this.includeNoSourcePackages = b;
    }

    public void setDocFilesSubDirs(boolean b) {
        this.docFilesSubDirs = b;
    }

    public void setExcludeDocFilesSubDir(String s) {
        this.excludeDocFilesSubDir = s;
    }

    public void setPostProcessGeneratedJavadocs(boolean b) {
        this.postProcessGeneratedJavadocs = b;
    }

    @Override
    public void execute() throws BuildException {
        this.checkTaskName();
        Vector<String> packagesToDoc = new Vector<String>();
        Path sourceDirs = new Path(this.getProject());
        this.checkPackageAndSourcePath();
        if (this.sourcePath != null) {
            sourceDirs.addExisting(this.sourcePath);
        }
        this.parsePackages(packagesToDoc, sourceDirs);
        this.checkPackages(packagesToDoc, sourceDirs);
        ArrayList<SourceFile> sourceFilesToDoc = new ArrayList<SourceFile>(this.sourceFiles);
        this.addSourceFiles(sourceFilesToDoc);
        this.checkPackagesToDoc(packagesToDoc, sourceFilesToDoc);
        this.log("Generating Javadoc", 2);
        Commandline toExecute = (Commandline)this.cmd.clone();
        if (this.executable != null) {
            toExecute.setExecutable(this.executable);
        } else {
            toExecute.setExecutable(JavaEnvUtils.getJdkExecutable("javadoc"));
        }
        this.generalJavadocArguments(toExecute);
        this.doSourcePath(toExecute, sourceDirs);
        this.doDoclet(toExecute);
        this.doBootPath(toExecute);
        this.doLinks(toExecute);
        this.doGroup(toExecute);
        this.doGroups(toExecute);
        this.doDocFilesSubDirs(toExecute);
        this.doModuleArguments(toExecute);
        this.doTags(toExecute);
        this.doSource(toExecute);
        this.doLinkSource(toExecute);
        this.doNoqualifier(toExecute);
        if (this.breakiterator) {
            toExecute.createArgument().setValue("-breakiterator");
        }
        if (this.useExternalFile) {
            this.writeExternalArgs(toExecute);
        }
        File tmpList = null;
        FileWriter wr = null;
        try {
            BufferedWriter srcListWriter = null;
            if (this.useExternalFile) {
                tmpList = FILE_UTILS.createTempFile(this.getProject(), "javadoc", "", null, true, true);
                toExecute.createArgument().setValue("@" + tmpList.getAbsolutePath());
                wr = new FileWriter(tmpList.getAbsolutePath(), true);
                srcListWriter = new BufferedWriter(wr);
            }
            this.doSourceAndPackageNames(toExecute, packagesToDoc, sourceFilesToDoc, this.useExternalFile, tmpList, srcListWriter);
            if (this.useExternalFile) {
                srcListWriter.flush();
            }
        }
        catch (IOException e) {
            if (tmpList != null) {
                tmpList.delete();
            }
            throw new BuildException("Error creating temporary file", e, this.getLocation());
        }
        finally {
            FileUtils.close(wr);
        }
        if (this.packageList != null) {
            toExecute.createArgument().setValue("@" + this.packageList);
        }
        this.log(toExecute.describeCommand(), 3);
        this.log("Javadoc execution", 2);
        JavadocOutputStream out = new JavadocOutputStream(2);
        JavadocOutputStream err = new JavadocOutputStream(1);
        Execute exe = new Execute(new PumpStreamHandler(out, err));
        exe.setAntRun(this.getProject());
        exe.setWorkingDirectory(null);
        try {
            exe.setCommandline(toExecute.getCommandline());
            int ret = exe.execute();
            if (ret != 0 && this.failOnError) {
                throw new BuildException("Javadoc returned " + ret, this.getLocation());
            }
            if (this.failOnWarning && (out.sawWarnings() || err.sawWarnings())) {
                throw new BuildException("Javadoc issued warnings.", this.getLocation());
            }
            this.postProcessGeneratedJavadocs();
        }
        catch (IOException e) {
            throw new BuildException("Javadoc failed: " + e, e, this.getLocation());
        }
        finally {
            if (tmpList != null) {
                tmpList.delete();
                tmpList = null;
            }
            out.logFlush();
            err.logFlush();
            FileUtils.close(out);
            FileUtils.close(err);
        }
    }

    private void checkTaskName() {
        if ("javadoc2".equals(this.getTaskType())) {
            this.log("Warning: the task name <javadoc2> is deprecated. Use <javadoc> instead.", 1);
        }
    }

    private void checkPackageAndSourcePath() {
        if (this.packageList != null && this.sourcePath == null) {
            String msg = "sourcePath attribute must be set when specifying packagelist.";
            throw new BuildException("sourcePath attribute must be set when specifying packagelist.");
        }
    }

    private void checkPackages(List<String> packagesToDoc, Path sourceDirs) {
        if (!packagesToDoc.isEmpty() && sourceDirs.isEmpty()) {
            throw new BuildException("sourcePath attribute must be set when specifying package names.");
        }
    }

    private void checkPackagesToDoc(List<String> packagesToDoc, List<SourceFile> sourceFilesToDoc) {
        if (this.packageList == null && packagesToDoc.isEmpty() && sourceFilesToDoc.isEmpty() && this.moduleNames.isEmpty()) {
            throw new BuildException("No source files, no packages and no modules have been specified.");
        }
    }

    private void doSourcePath(Commandline toExecute, Path sourceDirs) {
        if (!sourceDirs.isEmpty()) {
            toExecute.createArgument().setValue("-sourcepath");
            toExecute.createArgument().setPath(sourceDirs);
        }
    }

    private void generalJavadocArguments(Commandline toExecute) {
        if (this.doctitle != null) {
            toExecute.createArgument().setValue("-doctitle");
            toExecute.createArgument().setValue(this.expand(this.doctitle.getText()));
        }
        if (this.header != null) {
            toExecute.createArgument().setValue("-header");
            toExecute.createArgument().setValue(this.expand(this.header.getText()));
        }
        if (this.footer != null) {
            toExecute.createArgument().setValue("-footer");
            toExecute.createArgument().setValue(this.expand(this.footer.getText()));
        }
        if (this.bottom != null) {
            toExecute.createArgument().setValue("-bottom");
            toExecute.createArgument().setValue(this.expand(this.bottom.getText()));
        }
        this.classpath = this.classpath == null ? new Path(this.getProject()).concatSystemClasspath("last") : this.classpath.concatSystemClasspath("ignore");
        if (this.classpath.size() > 0) {
            toExecute.createArgument().setValue("-classpath");
            toExecute.createArgument().setPath(this.classpath);
        }
        if (this.version && this.doclet == null) {
            toExecute.createArgument().setValue("-version");
        }
        if (this.author && this.doclet == null) {
            toExecute.createArgument().setValue("-author");
        }
        if (this.doclet == null && this.destDir == null) {
            throw new BuildException("destdir attribute must be set!");
        }
    }

    private void doDoclet(Commandline toExecute) {
        if (this.doclet != null) {
            Path docletPath;
            if (this.doclet.getName() == null) {
                throw new BuildException("The doclet name must be specified.", this.getLocation());
            }
            toExecute.createArgument().setValue("-doclet");
            toExecute.createArgument().setValue(this.doclet.getName());
            if (this.doclet.getPath() != null && (docletPath = this.doclet.getPath().concatSystemClasspath("ignore")).size() != 0) {
                toExecute.createArgument().setValue("-docletpath");
                toExecute.createArgument().setPath(docletPath);
            }
            for (DocletParam param : Collections.list(this.doclet.getParams())) {
                if (param.getName() == null) {
                    throw new BuildException("Doclet parameters must have a name");
                }
                toExecute.createArgument().setValue(param.getName());
                if (param.getValue() == null) continue;
                toExecute.createArgument().setValue(param.getValue());
            }
        }
    }

    private void writeExternalArgs(Commandline toExecute) {
        File optionsTmpFile = null;
        try {
            optionsTmpFile = FILE_UTILS.createTempFile(this.getProject(), "javadocOptions", "", null, true, true);
            String[] listOpt = toExecute.getArguments();
            toExecute.clearArgs();
            toExecute.createArgument().setValue("@" + optionsTmpFile.getAbsolutePath());
            try (BufferedWriter optionsListWriter = new BufferedWriter(new FileWriter(optionsTmpFile.getAbsolutePath(), true));){
                for (String opt : listOpt) {
                    if (opt.startsWith("-J-")) {
                        toExecute.createArgument().setValue(opt);
                        continue;
                    }
                    if (opt.startsWith("-")) {
                        optionsListWriter.write(opt);
                        optionsListWriter.write(" ");
                        continue;
                    }
                    optionsListWriter.write(this.quoteString(opt));
                    optionsListWriter.newLine();
                }
            }
        }
        catch (IOException ex) {
            if (optionsTmpFile != null) {
                optionsTmpFile.delete();
            }
            throw new BuildException("Error creating or writing temporary file for javadoc options", ex, this.getLocation());
        }
    }

    private void doBootPath(Commandline toExecute) {
        Path bcp = new Path(this.getProject());
        if (this.bootclasspath != null) {
            bcp.append(this.bootclasspath);
        }
        if ((bcp = bcp.concatSystemBootClasspath("ignore")).size() > 0) {
            toExecute.createArgument().setValue("-bootclasspath");
            toExecute.createArgument().setPath(bcp);
        }
    }

    private void doLinks(Commandline toExecute) {
        for (LinkArgument la : this.links) {
            File hrefAsFile;
            if (la.getHref() == null || la.getHref().isEmpty()) {
                this.log("No href was given for the link - skipping", 3);
                continue;
            }
            String link = null;
            if (la.shouldResolveLink() && (hrefAsFile = this.getProject().resolveFile(la.getHref())).exists()) {
                try {
                    link = FILE_UTILS.getFileURL(hrefAsFile).toExternalForm();
                }
                catch (MalformedURLException ex) {
                    this.log("Warning: link location was invalid " + hrefAsFile, 1);
                }
            }
            if (link == null) {
                try {
                    URL base = new URL("file://.");
                    new URL(base, la.getHref());
                    link = la.getHref();
                }
                catch (MalformedURLException mue) {
                    this.log("Link href \"" + la.getHref() + "\" is not a valid url - skipping link", 1);
                    continue;
                }
            }
            if (la.isLinkOffline()) {
                File packageListLocation = la.getPackagelistLoc();
                URL packageListURL = la.getPackagelistURL();
                if (packageListLocation == null && packageListURL == null) {
                    throw new BuildException("The package list location for link " + la.getHref() + " must be provided because the link is offline");
                }
                if (packageListLocation != null) {
                    File packageListFile = new File(packageListLocation, "package-list");
                    if (packageListFile.exists()) {
                        try {
                            packageListURL = FILE_UTILS.getFileURL(packageListLocation);
                        }
                        catch (MalformedURLException ex) {
                            this.log("Warning: Package list location was invalid " + packageListLocation, 1);
                        }
                    } else {
                        this.log("Warning: No package list was found at " + packageListLocation, 3);
                    }
                }
                if (packageListURL == null) continue;
                toExecute.createArgument().setValue("-linkoffline");
                toExecute.createArgument().setValue(link);
                toExecute.createArgument().setValue(packageListURL.toExternalForm());
                continue;
            }
            toExecute.createArgument().setValue("-link");
            toExecute.createArgument().setValue(link);
        }
    }

    private void doGroup(Commandline toExecute) {
        if (this.group != null) {
            StringTokenizer tok = new StringTokenizer(this.group, ",", false);
            while (tok.hasMoreTokens()) {
                String grp = tok.nextToken().trim();
                int space = grp.indexOf(32);
                if (space <= 0) continue;
                String name = grp.substring(0, space);
                String pkgList = grp.substring(space + 1);
                toExecute.createArgument().setValue("-group");
                toExecute.createArgument().setValue(name);
                toExecute.createArgument().setValue(pkgList);
            }
        }
    }

    private void doGroups(Commandline toExecute) {
        for (GroupArgument ga : this.groups) {
            String title = ga.getTitle();
            String packages = ga.getPackages();
            if (title == null || packages == null) {
                throw new BuildException("The title and packages must be specified for group elements.");
            }
            toExecute.createArgument().setValue("-group");
            toExecute.createArgument().setValue(this.expand(title));
            toExecute.createArgument().setValue(packages);
        }
    }

    private void doNoqualifier(Commandline toExecute) {
        if (this.noqualifier != null && this.doclet == null) {
            toExecute.createArgument().setValue("-noqualifier");
            toExecute.createArgument().setValue(this.noqualifier);
        }
    }

    private void doLinkSource(Commandline toExecute) {
        if (this.linksource && this.doclet == null) {
            toExecute.createArgument().setValue("-linksource");
        }
    }

    private void doSource(Commandline toExecute) {
        String sourceArg;
        String string = sourceArg = this.source != null ? this.source : this.getProject().getProperty("ant.build.javac.source");
        if (sourceArg != null) {
            toExecute.createArgument().setValue("-source");
            toExecute.createArgument().setValue(sourceArg);
        }
    }

    private void doTags(Commandline toExecute) {
        for (Object element : this.tags) {
            Path tagletPath;
            if (element instanceof TagArgument) {
                TagArgument ta = (TagArgument)element;
                File tagDir = ta.getDir(this.getProject());
                if (tagDir == null) {
                    toExecute.createArgument().setValue("-tag");
                    toExecute.createArgument().setValue(ta.getParameter());
                    continue;
                }
                DirectoryScanner tagDefScanner = ta.getDirectoryScanner(this.getProject());
                for (String file : tagDefScanner.getIncludedFiles()) {
                    File tagDefFile = new File(tagDir, file);
                    try (BufferedReader in = new BufferedReader(new FileReader(tagDefFile));){
                        in.lines().forEach(line -> {
                            toExecute.createArgument().setValue("-tag");
                            toExecute.createArgument().setValue((String)line);
                        });
                    }
                    catch (IOException ioe) {
                        throw new BuildException("Couldn't read tag file from " + tagDefFile.getAbsolutePath(), ioe);
                    }
                }
                continue;
            }
            ExtensionInfo tagletInfo = (ExtensionInfo)element;
            toExecute.createArgument().setValue("-taglet");
            toExecute.createArgument().setValue(tagletInfo.getName());
            if (tagletInfo.getPath() == null || (tagletPath = tagletInfo.getPath().concatSystemClasspath("ignore")).isEmpty()) continue;
            toExecute.createArgument().setValue("-tagletpath");
            toExecute.createArgument().setPath(tagletPath);
        }
    }

    private void doDocFilesSubDirs(Commandline toExecute) {
        if (this.docFilesSubDirs) {
            toExecute.createArgument().setValue("-docfilessubdirs");
            if (this.excludeDocFilesSubDir != null && !this.excludeDocFilesSubDir.trim().isEmpty()) {
                toExecute.createArgument().setValue("-excludedocfilessubdir");
                toExecute.createArgument().setValue(this.excludeDocFilesSubDir);
            }
        }
    }

    private void doSourceAndPackageNames(Commandline toExecute, List<String> packagesToDoc, List<SourceFile> sourceFilesToDoc, boolean useExternalFile, File tmpList, BufferedWriter srcListWriter) throws IOException {
        for (String packageName : packagesToDoc) {
            if (useExternalFile) {
                srcListWriter.write(packageName);
                srcListWriter.newLine();
                continue;
            }
            toExecute.createArgument().setValue(packageName);
        }
        for (SourceFile sf : sourceFilesToDoc) {
            String sourceFileName = sf.getFile().getAbsolutePath();
            if (useExternalFile) {
                if (sourceFileName.contains(" ")) {
                    String name = sourceFileName;
                    if (File.separatorChar == '\\') {
                        name = sourceFileName.replace(File.separatorChar, '/');
                    }
                    srcListWriter.write("\"" + name + "\"");
                } else {
                    srcListWriter.write(sourceFileName);
                }
                srcListWriter.newLine();
                continue;
            }
            toExecute.createArgument().setValue(sourceFileName);
        }
    }

    private String quoteString(String str) {
        if (!(this.containsWhitespace(str) || str.contains("'") || str.contains("\""))) {
            return str;
        }
        if (!str.contains("'")) {
            return this.quoteString(str, '\'');
        }
        return this.quoteString(str, '\"');
    }

    private boolean containsWhitespace(String s) {
        for (char c : s.toCharArray()) {
            if (!Character.isWhitespace(c)) continue;
            return true;
        }
        return false;
    }

    private String quoteString(String str, char delim) {
        StringBuilder buf = new StringBuilder(str.length() * 2);
        buf.append(delim);
        boolean lastCharWasCR = false;
        block5: for (char c : str.toCharArray()) {
            if (c == delim) {
                buf.append('\\').append(c);
                lastCharWasCR = false;
                continue;
            }
            switch (c) {
                case '\\': {
                    buf.append("\\\\");
                    lastCharWasCR = false;
                    continue block5;
                }
                case '\r': {
                    buf.append("\\\r");
                    lastCharWasCR = true;
                    continue block5;
                }
                case '\n': {
                    if (!lastCharWasCR) {
                        buf.append("\\\n");
                    } else {
                        buf.append("\n");
                    }
                    lastCharWasCR = false;
                    continue block5;
                }
                default: {
                    buf.append(c);
                    lastCharWasCR = false;
                }
            }
        }
        buf.append(delim);
        return buf.toString();
    }

    private void addSourceFiles(List<SourceFile> sf) {
        for (ResourceCollection rc : this.nestedSourceFiles) {
            FileSet fs;
            if (!rc.isFilesystemOnly()) {
                throw new BuildException("only file system based resources are supported by javadoc");
            }
            if (rc instanceof FileSet && !(fs = (FileSet)rc).hasPatterns() && !fs.hasSelectors()) {
                FileSet fs2 = (FileSet)fs.clone();
                fs2.createInclude().setName("**/*.java");
                if (this.includeNoSourcePackages) {
                    fs2.createInclude().setName("**/package.html");
                }
                rc = fs2;
            }
            for (Resource r : rc) {
                sf.add(new SourceFile(r.as(FileProvider.class).getFile()));
            }
        }
    }

    private void parsePackages(List<String> pn, Path sp) {
        HashSet<String> addedPackages = new HashSet<String>();
        ArrayList<DirSet> dirSets = new ArrayList<DirSet>(this.packageSets);
        if (this.sourcePath != null) {
            PatternSet ps = new PatternSet();
            ps.setProject(this.getProject());
            if (this.packageNames.isEmpty()) {
                ps.createInclude().setName("**");
            } else {
                this.packageNames.stream().map(PackageName::getName).map(s -> s.replace('.', '/').replaceFirst("\\*$", "**")).forEach(pkg -> ps.createInclude().setName((String)pkg));
            }
            this.excludePackageNames.stream().map(PackageName::getName).map(s -> s.replace('.', '/').replaceFirst("\\*$", "**")).forEach(pkg -> ps.createExclude().setName((String)pkg));
            for (String pathElement : this.sourcePath.list()) {
                File dir = new File(pathElement);
                if (dir.isDirectory()) {
                    DirSet ds = new DirSet();
                    ds.setProject(this.getProject());
                    ds.setDefaultexcludes(this.useDefaultExcludes);
                    ds.setDir(dir);
                    ds.createPatternSet().addConfiguredPatternset(ps);
                    dirSets.add(ds);
                    continue;
                }
                this.log("Skipping " + pathElement + " since it is no directory.", 1);
            }
        }
        for (DirSet ds : dirSets) {
            File baseDir = ds.getDir(this.getProject());
            this.log("scanning " + baseDir + " for packages.", 4);
            DirectoryScanner dsc = ds.getDirectoryScanner(this.getProject());
            boolean containsPackages = false;
            for (String dir : dsc.getIncludedDirectories()) {
                File pd = new File(baseDir, dir);
                String[] files = pd.list((directory, name) -> name.endsWith(".java") || this.includeNoSourcePackages && name.equals("package.html"));
                if (files.length <= 0) continue;
                if (dir.isEmpty()) {
                    this.log(baseDir + " contains source files in the default package, you must specify them as source files not packages.", 1);
                    continue;
                }
                containsPackages = true;
                String packageName = dir.replace(File.separatorChar, '.');
                if (addedPackages.contains(packageName)) continue;
                addedPackages.add(packageName);
                pn.add(packageName);
            }
            if (containsPackages) {
                sp.createPathElement().setLocation(baseDir);
                continue;
            }
            this.log(baseDir + " doesn't contain any packages, dropping it.", 3);
        }
    }

    private void postProcessGeneratedJavadocs() throws IOException {
        String fixData;
        if (!this.postProcessGeneratedJavadocs) {
            return;
        }
        if (this.destDir != null && !this.destDir.isDirectory()) {
            this.log("No javadoc created, no need to post-process anything", 3);
            return;
        }
        InputStream in = Javadoc.class.getResourceAsStream("javadoc-frame-injections-fix.txt");
        if (in == null) {
            throw new FileNotFoundException("Missing resource 'javadoc-frame-injections-fix.txt' in classpath.");
        }
        try {
            fixData = this.fixLineFeeds(FileUtils.readFully(new InputStreamReader(in, StandardCharsets.US_ASCII))).trim();
        }
        finally {
            FileUtils.close(in);
        }
        DirectoryScanner ds = new DirectoryScanner();
        ds.setBasedir(this.destDir);
        ds.setCaseSensitive(false);
        ds.setIncludes(new String[]{"**/index.html", "**/index.htm", "**/toc.html", "**/toc.htm"});
        ds.addDefaultExcludes();
        ds.scan();
        int patched = 0;
        for (String f : ds.getIncludedFiles()) {
            patched += this.postProcess(new File(this.destDir, f), fixData);
        }
        if (patched > 0) {
            this.log("Patched " + patched + " link injection vulnerable javadocs", 2);
        }
    }

    private int postProcess(File file, String fixData) throws IOException {
        String patchedFileContents;
        String fileContents;
        String enc = this.docEncoding != null ? this.docEncoding : FILE_UTILS.getDefaultEncoding();
        try (InputStreamReader reader = new InputStreamReader(Files.newInputStream(file.toPath(), new OpenOption[0]), enc);){
            fileContents = this.fixLineFeeds(FileUtils.safeReadFully(reader));
        }
        if (!fileContents.contains("function validURL(url) {") && !(patchedFileContents = this.patchContent(fileContents, fixData)).equals(fileContents)) {
            try (OutputStreamWriter w = new OutputStreamWriter(Files.newOutputStream(file.toPath(), new OpenOption[0]), enc);){
                w.write(patchedFileContents);
                w.close();
                int n = 1;
                return n;
            }
        }
        return 0;
    }

    private String fixLineFeeds(String orig) {
        return orig.replace("\r\n", "\n").replace("\n", System.lineSeparator());
    }

    private String patchContent(String fileContents, String fixData) {
        int start = fileContents.indexOf(LOAD_FRAME);
        if (start >= 0) {
            return fileContents.substring(0, start) + fixData + fileContents.substring(start + LOAD_FRAME_LEN);
        }
        return fileContents;
    }

    private void doModuleArguments(Commandline toExecute) {
        if (!this.moduleNames.isEmpty()) {
            toExecute.createArgument().setValue("--module");
            toExecute.createArgument().setValue(this.moduleNames.stream().map(PackageName::getName).collect(Collectors.joining(",")));
        }
        if (this.modulePath != null) {
            toExecute.createArgument().setValue("--module-path");
            toExecute.createArgument().setPath(this.modulePath);
        }
        if (this.moduleSourcePath != null) {
            toExecute.createArgument().setValue("--module-source-path");
            toExecute.createArgument().setPath(this.moduleSourcePath);
        }
    }

    protected String expand(String content) {
        return this.getProject().replaceProperties(content);
    }

    public class DocletInfo
    extends ExtensionInfo {
        private final List<DocletParam> params = new Vector<DocletParam>();

        public DocletParam createParam() {
            DocletParam param = new DocletParam();
            this.params.add(param);
            return param;
        }

        public Enumeration<DocletParam> getParams() {
            return Collections.enumeration(this.params);
        }
    }

    public static class Html {
        private final StringBuffer text = new StringBuffer();

        public void addText(String t) {
            this.text.append(t);
        }

        public String getText() {
            return this.text.substring(0);
        }
    }

    public class ResourceCollectionContainer
    implements Iterable<ResourceCollection> {
        private final List<ResourceCollection> rcs = new ArrayList<ResourceCollection>();

        public void add(ResourceCollection rc) {
            this.rcs.add(rc);
        }

        @Override
        public Iterator<ResourceCollection> iterator() {
            return this.rcs.iterator();
        }
    }

    public static class SourceFile {
        private File file;

        public SourceFile() {
        }

        public SourceFile(File file) {
            this.file = file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public File getFile() {
            return this.file;
        }
    }

    public static class PackageName {
        private String name;

        public void setName(String name) {
            this.name = name.trim();
        }

        public String getName() {
            return this.name;
        }

        public String toString() {
            return this.getName();
        }
    }

    public static class AccessType
    extends EnumeratedAttribute {
        @Override
        public String[] getValues() {
            return new String[]{"protected", "public", "package", "private"};
        }
    }

    public class LinkArgument {
        private String href;
        private boolean offline = false;
        private File packagelistLoc;
        private URL packagelistURL;
        private boolean resolveLink = false;

        public void setHref(String hr) {
            this.href = hr;
        }

        public String getHref() {
            return this.href;
        }

        public void setPackagelistLoc(File src) {
            this.packagelistLoc = src;
        }

        public File getPackagelistLoc() {
            return this.packagelistLoc;
        }

        public void setPackagelistURL(URL src) {
            this.packagelistURL = src;
        }

        public URL getPackagelistURL() {
            return this.packagelistURL;
        }

        public void setOffline(boolean offline) {
            this.offline = offline;
        }

        public boolean isLinkOffline() {
            return this.offline;
        }

        public void setResolveLink(boolean resolve) {
            this.resolveLink = resolve;
        }

        public boolean shouldResolveLink() {
            return this.resolveLink;
        }
    }

    public class TagArgument
    extends FileSet {
        private String name = null;
        private boolean enabled = true;
        private String scope = "a";

        public void setName(String name) {
            this.name = name;
        }

        public void setScope(String verboseScope) throws BuildException {
            int i;
            verboseScope = verboseScope.toLowerCase(Locale.ENGLISH);
            boolean[] elements = new boolean[SCOPE_ELEMENTS.length];
            boolean gotAll = false;
            boolean gotNotAll = false;
            StringTokenizer tok = new StringTokenizer(verboseScope, ",");
            while (tok.hasMoreTokens()) {
                String next = tok.nextToken().trim();
                if ("all".equals(next)) {
                    if (gotAll) {
                        this.getProject().log("Repeated tag scope element: all", 3);
                    }
                    gotAll = true;
                    continue;
                }
                for (i = 0; i < SCOPE_ELEMENTS.length && !SCOPE_ELEMENTS[i].equals(next); ++i) {
                }
                if (i == SCOPE_ELEMENTS.length) {
                    throw new BuildException("Unrecognised scope element: %s", next);
                }
                if (elements[i]) {
                    this.getProject().log("Repeated tag scope element: " + next, 3);
                }
                elements[i] = true;
                gotNotAll = true;
            }
            if (gotNotAll && gotAll) {
                throw new BuildException("Mixture of \"all\" and other scope elements in tag parameter.");
            }
            if (!gotNotAll && !gotAll) {
                throw new BuildException("No scope elements specified in tag parameter.");
            }
            if (gotAll) {
                this.scope = "a";
            } else {
                StringBuilder buff = new StringBuilder(elements.length);
                for (i = 0; i < elements.length; ++i) {
                    if (!elements[i]) continue;
                    buff.append(SCOPE_ELEMENTS[i].charAt(0));
                }
                this.scope = buff.toString();
            }
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getParameter() throws BuildException {
            if (this.name == null || this.name.isEmpty()) {
                throw new BuildException("No name specified for custom tag.");
            }
            if (this.getDescription() != null) {
                return this.name + ":" + (this.enabled ? "" : "X") + this.scope + ":" + this.getDescription();
            }
            if (!this.enabled || !"a".equals(this.scope)) {
                return this.name + ":" + (this.enabled ? "" : "X") + this.scope;
            }
            return this.name;
        }
    }

    public class GroupArgument {
        private Html title;
        private final List<PackageName> packages = new Vector<PackageName>();

        public void setTitle(String src) {
            Html h = new Html();
            h.addText(src);
            this.addTitle(h);
        }

        public void addTitle(Html text) {
            this.title = text;
        }

        public String getTitle() {
            return this.title != null ? this.title.getText() : null;
        }

        public void setPackages(String src) {
            StringTokenizer tok = new StringTokenizer(src, ",");
            while (tok.hasMoreTokens()) {
                String p = tok.nextToken();
                PackageName pn = new PackageName();
                pn.setName(p);
                this.addPackage(pn);
            }
        }

        public void addPackage(PackageName pn) {
            this.packages.add(pn);
        }

        public String getPackages() {
            return this.packages.stream().map(Object::toString).collect(Collectors.joining(":"));
        }
    }

    private class JavadocOutputStream
    extends LogOutputStream {
        private String queuedLine;
        private boolean sawWarnings;

        JavadocOutputStream(int level) {
            super(Javadoc.this, level);
            this.queuedLine = null;
            this.sawWarnings = false;
        }

        @Override
        protected void processLine(String line, int messageLevel) {
            if (line.matches("(\\d) warning[s]?$")) {
                this.sawWarnings = true;
            }
            if (messageLevel == 2 && line.startsWith("Generating ")) {
                if (this.queuedLine != null) {
                    super.processLine(this.queuedLine, 3);
                }
                this.queuedLine = line;
            } else {
                if (this.queuedLine != null) {
                    if (line.startsWith("Building ")) {
                        super.processLine(this.queuedLine, 3);
                    } else {
                        super.processLine(this.queuedLine, 2);
                    }
                    this.queuedLine = null;
                }
                super.processLine(line, messageLevel);
            }
        }

        protected void logFlush() {
            if (this.queuedLine != null) {
                super.processLine(this.queuedLine, 3);
                this.queuedLine = null;
            }
        }

        public boolean sawWarnings() {
            return this.sawWarnings;
        }
    }

    public class DocletParam {
        private String name;
        private String value;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }
    }

    public static class ExtensionInfo
    extends ProjectComponent {
        private String name;
        private Path path;

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public void setPath(Path path) {
            if (this.path == null) {
                this.path = path;
            } else {
                this.path.append(path);
            }
        }

        public Path getPath() {
            return this.path;
        }

        public Path createPath() {
            if (this.path == null) {
                this.path = new Path(this.getProject());
            }
            return this.path.createPath();
        }

        public void setPathRef(Reference r) {
            this.createPath().setRefid(r);
        }
    }
}

