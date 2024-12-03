/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.BuildException
 *  org.apache.tools.ant.DirectoryScanner
 *  org.apache.tools.ant.Task
 *  org.apache.tools.ant.types.DirSet
 *  org.apache.tools.ant.types.Path
 *  org.apache.tools.ant.types.PatternSet
 */
package org.codehaus.groovy.ant;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.DirSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.PatternSet;
import org.codehaus.groovy.ant.LoggingHelper;
import org.codehaus.groovy.runtime.ResourceGroovyMethods;
import org.codehaus.groovy.tools.groovydoc.ClasspathResourceManager;
import org.codehaus.groovy.tools.groovydoc.FileOutputTool;
import org.codehaus.groovy.tools.groovydoc.GroovyDocTool;
import org.codehaus.groovy.tools.groovydoc.LinkArgument;
import org.codehaus.groovy.tools.groovydoc.gstringTemplates.GroovyDocTemplateInfo;

public class Groovydoc
extends Task {
    private final LoggingHelper log = new LoggingHelper(this);
    private Path sourcePath;
    private File destDir;
    private List<String> packageNames;
    private List<String> excludePackageNames;
    private String windowTitle = "Groovy Documentation";
    private String docTitle = "Groovy Documentation";
    private String footer = "Groovy Documentation";
    private String header = "Groovy Documentation";
    private Boolean privateScope = false;
    private Boolean protectedScope = false;
    private Boolean packageScope = false;
    private Boolean publicScope = false;
    private Boolean author = true;
    private Boolean processScripts = true;
    private Boolean includeMainForScripts = true;
    private boolean useDefaultExcludes = true;
    private boolean includeNoSourcePackages = false;
    private Boolean noTimestamp = false;
    private Boolean noVersionStamp = false;
    private List<DirSet> packageSets;
    private List<String> sourceFilesToDoc;
    private List<LinkArgument> links = new ArrayList<LinkArgument>();
    private File overviewFile;
    private File styleSheetFile;
    private String extensions = ".java:.groovy:.gv:.gvy:.gsh";
    private String charset;
    private String fileEncoding;

    public Groovydoc() {
        this.packageNames = new ArrayList<String>();
        this.excludePackageNames = new ArrayList<String>();
        this.packageSets = new ArrayList<DirSet>();
        this.sourceFilesToDoc = new ArrayList<String>();
    }

    public void setSourcepath(Path src) {
        if (this.sourcePath == null) {
            this.sourcePath = src;
        } else {
            this.sourcePath.append(src);
        }
    }

    public void setDestdir(File dir) {
        this.destDir = dir;
    }

    public void setAuthor(boolean author) {
        this.author = author;
    }

    public void setNoTimestamp(boolean noTimestamp) {
        this.noTimestamp = noTimestamp;
    }

    public void setNoVersionStamp(boolean noVersionStamp) {
        this.noVersionStamp = noVersionStamp;
    }

    public void setProcessScripts(boolean processScripts) {
        this.processScripts = processScripts;
    }

    public void setIncludeMainForScripts(boolean includeMainForScripts) {
        this.includeMainForScripts = includeMainForScripts;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    public void setPackagenames(String packages) {
        StringTokenizer tok = new StringTokenizer(packages, ",");
        while (tok.hasMoreTokens()) {
            String packageName = tok.nextToken();
            this.packageNames.add(packageName);
        }
    }

    public void setUse(boolean b) {
    }

    public void setWindowtitle(String title) {
        this.windowTitle = title;
    }

    public void setDoctitle(String htmlTitle) {
        this.docTitle = htmlTitle;
    }

    public void setOverview(File file) {
        this.overviewFile = file;
    }

    public void setAccess(String access) {
        if ("public".equals(access)) {
            this.publicScope = true;
        } else if ("protected".equals(access)) {
            this.protectedScope = true;
        } else if ("package".equals(access)) {
            this.packageScope = true;
        } else if ("private".equals(access)) {
            this.privateScope = true;
        }
    }

    public void setPrivate(boolean b) {
        this.privateScope = b;
    }

    public void setPublic(boolean b) {
        this.publicScope = b;
    }

    public void setProtected(boolean b) {
        this.protectedScope = b;
    }

    public void setPackage(boolean b) {
        this.packageScope = b;
    }

    public void setFooter(String footer) {
        this.footer = footer;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public void setFileEncoding(String fileEncoding) {
        this.fileEncoding = fileEncoding;
    }

    public void setStyleSheetFile(File styleSheetFile) {
        this.styleSheetFile = styleSheetFile;
    }

    private void parsePackages(List<String> resultantPackages, Path sourcePath) {
        ArrayList<String> addedPackages = new ArrayList<String>();
        ArrayList<DirSet> dirSets = new ArrayList<DirSet>(this.packageSets);
        if (this.sourcePath != null) {
            String[] pathElements;
            String pkg;
            PatternSet ps = new PatternSet();
            if (!this.packageNames.isEmpty()) {
                for (String string : this.packageNames) {
                    pkg = string.replace('.', '/');
                    if (pkg.endsWith("*")) {
                        pkg = pkg + "*";
                    }
                    ps.createInclude().setName(pkg);
                }
            } else {
                ps.createInclude().setName("**");
            }
            for (String string : this.excludePackageNames) {
                pkg = string.replace('.', '/');
                if (pkg.endsWith("*")) {
                    pkg = pkg + "*";
                }
                ps.createExclude().setName(pkg);
            }
            for (String pathElement : pathElements = this.sourcePath.list()) {
                File dir = new File(pathElement);
                if (dir.isDirectory()) {
                    DirSet ds = new DirSet();
                    ds.setDefaultexcludes(this.useDefaultExcludes);
                    ds.setDir(dir);
                    ds.createPatternSet().addConfiguredPatternset(ps);
                    dirSets.add(ds);
                    continue;
                }
                this.log.warn("Skipping " + pathElement + " since it is no directory.");
            }
        }
        for (DirSet ds : dirSets) {
            File file = ds.getDir(this.getProject());
            this.log.debug("scanning " + file + " for packages.");
            DirectoryScanner dsc = ds.getDirectoryScanner(this.getProject());
            String[] dirs = dsc.getIncludedDirectories();
            boolean containsPackages = false;
            for (String dir : dirs) {
                File pd = new File(file, dir);
                String[] files = pd.list(new FilenameFilter(){

                    @Override
                    public boolean accept(File dir1, String name) {
                        if (!Groovydoc.this.includeNoSourcePackages && name.equals("package.html")) {
                            return true;
                        }
                        StringTokenizer tokenizer = new StringTokenizer(Groovydoc.this.extensions, ":");
                        while (tokenizer.hasMoreTokens()) {
                            String ext = tokenizer.nextToken();
                            if (!name.endsWith(ext)) continue;
                            return true;
                        }
                        return false;
                    }
                });
                for (String filename : Arrays.asList(files)) {
                    this.sourceFilesToDoc.add(dir + File.separator + filename);
                }
                if (files.length <= 0) continue;
                if ("".equals(dir)) {
                    this.log.warn(file + " contains source files in the default package, you must specify them as source files not packages.");
                    continue;
                }
                containsPackages = true;
                String pn = dir.replace(File.separatorChar, '.');
                if (addedPackages.contains(pn)) continue;
                addedPackages.add(pn);
                resultantPackages.add(pn);
            }
            if (containsPackages) {
                sourcePath.createPathElement().setLocation(file);
                continue;
            }
            this.log.verbose(file + " doesn't contain any packages, dropping it.");
        }
    }

    public void execute() throws BuildException {
        ArrayList<String> packagesToDoc = new ArrayList<String>();
        Path sourceDirs = new Path(this.getProject());
        Properties properties = new Properties();
        properties.setProperty("windowTitle", this.windowTitle);
        properties.setProperty("docTitle", this.docTitle);
        properties.setProperty("footer", this.footer);
        properties.setProperty("header", this.header);
        this.checkScopeProperties(properties);
        properties.setProperty("publicScope", this.publicScope.toString());
        properties.setProperty("protectedScope", this.protectedScope.toString());
        properties.setProperty("packageScope", this.packageScope.toString());
        properties.setProperty("privateScope", this.privateScope.toString());
        properties.setProperty("author", this.author.toString());
        properties.setProperty("processScripts", this.processScripts.toString());
        properties.setProperty("includeMainForScripts", this.includeMainForScripts.toString());
        properties.setProperty("overviewFile", this.overviewFile != null ? this.overviewFile.getAbsolutePath() : "");
        properties.setProperty("charset", this.charset != null ? this.charset : "");
        properties.setProperty("fileEncoding", this.fileEncoding != null ? this.fileEncoding : "");
        properties.setProperty("timestamp", Boolean.valueOf(this.noTimestamp == false).toString());
        properties.setProperty("versionStamp", Boolean.valueOf(this.noVersionStamp == false).toString());
        if (this.sourcePath != null) {
            sourceDirs.addExisting(this.sourcePath);
        }
        this.parsePackages(packagesToDoc, sourceDirs);
        GroovyDocTool htmlTool = new GroovyDocTool(new ClasspathResourceManager(), this.sourcePath.list(), this.getDocTemplates(), this.getPackageTemplates(), this.getClassTemplates(), this.links, properties);
        try {
            htmlTool.add(this.sourceFilesToDoc);
            FileOutputTool output = new FileOutputTool();
            htmlTool.renderToOutput(output, this.destDir.getCanonicalPath());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        if (this.styleSheetFile != null) {
            try {
                String css = ResourceGroovyMethods.getText(this.styleSheetFile);
                File outfile = new File(this.destDir, "stylesheet.css");
                ResourceGroovyMethods.setText(outfile, css);
            }
            catch (IOException e) {
                System.out.println("Warning: Unable to copy specified stylesheet '" + this.styleSheetFile.getAbsolutePath() + "'. Using default stylesheet instead. Due to: " + e.getMessage());
            }
        }
    }

    private void checkScopeProperties(Properties properties) {
        int scopeCount = 0;
        if (this.packageScope.booleanValue()) {
            ++scopeCount;
        }
        if (this.privateScope.booleanValue()) {
            ++scopeCount;
        }
        if (this.protectedScope.booleanValue()) {
            ++scopeCount;
        }
        if (this.publicScope.booleanValue()) {
            ++scopeCount;
        }
        if (scopeCount == 0) {
            this.protectedScope = true;
        } else if (scopeCount > 1) {
            throw new BuildException("More than one of public, private, package, or protected scopes specified.");
        }
    }

    public LinkArgument createLink() {
        LinkArgument result = new LinkArgument();
        this.links.add(result);
        return result;
    }

    protected String[] getPackageTemplates() {
        return GroovyDocTemplateInfo.DEFAULT_PACKAGE_TEMPLATES;
    }

    protected String[] getDocTemplates() {
        return GroovyDocTemplateInfo.DEFAULT_DOC_TEMPLATES;
    }

    protected String[] getClassTemplates() {
        return GroovyDocTemplateInfo.DEFAULT_CLASS_TEMPLATES;
    }
}

