/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Collections;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import javax.xml.parsers.SAXParser;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Location;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.optional.ejb.DescriptorHandler;
import org.apache.tools.ant.taskdefs.optional.ejb.EJBDeploymentTool;
import org.apache.tools.ant.taskdefs.optional.ejb.EjbJar;
import org.apache.tools.ant.taskdefs.optional.ejb.InnerClassFilenameFilter;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.depend.DependencyAnalyzer;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class GenericDeploymentTool
implements EJBDeploymentTool {
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    public static final int JAR_COMPRESS_LEVEL = 9;
    protected static final String META_DIR = "META-INF/";
    protected static final String MANIFEST = "META-INF/MANIFEST.MF";
    protected static final String EJB_DD = "ejb-jar.xml";
    public static final String ANALYZER_SUPER = "super";
    public static final String ANALYZER_FULL = "full";
    public static final String ANALYZER_NONE = "none";
    public static final String DEFAULT_ANALYZER = "super";
    public static final String ANALYZER_CLASS_SUPER = "org.apache.tools.ant.util.depend.bcel.AncestorAnalyzer";
    public static final String ANALYZER_CLASS_FULL = "org.apache.tools.ant.util.depend.bcel.FullAnalyzer";
    private EjbJar.Config config;
    private File destDir;
    private Path classpath;
    private String genericJarSuffix = "-generic.jar";
    private Task task;
    private ClassLoader classpathLoader = null;
    private Set<String> addedfiles;
    private DescriptorHandler handler;
    private DependencyAnalyzer dependencyAnalyzer;

    public void setDestdir(File inDir) {
        this.destDir = inDir;
    }

    protected File getDestDir() {
        return this.destDir;
    }

    @Override
    public void setTask(Task task) {
        this.task = task;
    }

    protected Task getTask() {
        return this.task;
    }

    protected EjbJar.Config getConfig() {
        return this.config;
    }

    protected boolean usingBaseJarName() {
        return this.config.baseJarName != null;
    }

    public void setGenericJarSuffix(String inString) {
        this.genericJarSuffix = inString;
    }

    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.task.getProject());
        }
        return this.classpath.createPath();
    }

    public void setClasspath(Path classpath) {
        this.classpath = classpath;
    }

    protected Path getCombinedClasspath() {
        Path combinedPath = this.classpath;
        if (this.config.classpath != null) {
            if (combinedPath == null) {
                combinedPath = this.config.classpath;
            } else {
                combinedPath.append(this.config.classpath);
            }
        }
        return combinedPath;
    }

    protected void log(String message, int level) {
        this.getTask().log(message, level);
    }

    protected Location getLocation() {
        return this.getTask().getLocation();
    }

    private void createAnalyzer() {
        String analyzer = this.config.analyzer;
        if (analyzer == null) {
            analyzer = "super";
        }
        if (analyzer.equals(ANALYZER_NONE)) {
            return;
        }
        String analyzerClassName = null;
        switch (analyzer) {
            case "super": {
                analyzerClassName = ANALYZER_CLASS_SUPER;
                break;
            }
            case "full": {
                analyzerClassName = ANALYZER_CLASS_FULL;
                break;
            }
            default: {
                analyzerClassName = analyzer;
            }
        }
        try {
            Class<DependencyAnalyzer> analyzerClass = Class.forName(analyzerClassName).asSubclass(DependencyAnalyzer.class);
            this.dependencyAnalyzer = analyzerClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            this.dependencyAnalyzer.addClassPath(new Path(this.task.getProject(), this.config.srcDir.getPath()));
            this.dependencyAnalyzer.addClassPath(this.config.classpath);
        }
        catch (NoClassDefFoundError e) {
            this.dependencyAnalyzer = null;
            this.task.log("Unable to load dependency analyzer: " + analyzerClassName + " - dependent class not found: " + e.getMessage(), 1);
        }
        catch (Exception e) {
            this.dependencyAnalyzer = null;
            this.task.log("Unable to load dependency analyzer: " + analyzerClassName + " - exception: " + e.getMessage(), 1);
        }
    }

    @Override
    public void configure(EjbJar.Config config) {
        this.config = config;
        this.createAnalyzer();
        this.classpathLoader = null;
    }

    protected void addFileToJar(JarOutputStream jStream, File inputFile, String logicalFilename) throws BuildException {
        if (!this.addedfiles.contains(logicalFilename)) {
            try (InputStream iStream = Files.newInputStream(inputFile.toPath(), new OpenOption[0]);){
                ZipEntry zipEntry = new ZipEntry(logicalFilename.replace('\\', '/'));
                jStream.putNextEntry(zipEntry);
                byte[] byteBuffer = new byte[2048];
                int count = 0;
                do {
                    jStream.write(byteBuffer, 0, count);
                } while ((count = iStream.read(byteBuffer, 0, byteBuffer.length)) != -1);
                this.addedfiles.add(logicalFilename);
            }
            catch (IOException ioe) {
                this.log("WARNING: IOException while adding entry " + logicalFilename + " to jarfile from " + inputFile.getPath() + " " + ioe.getClass().getName() + "-" + ioe.getMessage(), 1);
            }
        }
    }

    protected DescriptorHandler getDescriptorHandler(File srcDir) {
        DescriptorHandler h = new DescriptorHandler(this.getTask(), srcDir);
        this.registerKnownDTDs(h);
        for (EjbJar.DTDLocation dtdLocation : this.getConfig().dtdLocations) {
            h.registerDTD(dtdLocation.getPublicId(), dtdLocation.getLocation());
        }
        return h;
    }

    protected void registerKnownDTDs(DescriptorHandler handler) {
    }

    @Override
    public void processDescriptor(String descriptorFileName, SAXParser saxParser) {
        this.checkConfiguration(descriptorFileName, saxParser);
        try {
            File jarFile;
            this.handler = this.getDescriptorHandler(this.config.srcDir);
            Hashtable<String, File> ejbFiles = this.parseEjbFiles(descriptorFileName, saxParser);
            this.addSupportClasses(ejbFiles);
            String baseName = this.getJarBaseName(descriptorFileName);
            String ddPrefix = this.getVendorDDPrefix(baseName, descriptorFileName);
            File manifestFile = this.getManifestFile(ddPrefix);
            if (manifestFile != null) {
                ejbFiles.put(MANIFEST, manifestFile);
            }
            ejbFiles.put("META-INF/ejb-jar.xml", new File(this.config.descriptorDir, descriptorFileName));
            this.addVendorFiles(ejbFiles, ddPrefix);
            this.checkAndAddDependants(ejbFiles);
            if (this.config.flatDestDir && !baseName.isEmpty()) {
                int startName = baseName.lastIndexOf(File.separator);
                if (startName == -1) {
                    startName = 0;
                }
                int endName = baseName.length();
                baseName = baseName.substring(startName, endName);
            }
            if (this.needToRebuild(ejbFiles, jarFile = this.getVendorOutputJarFile(baseName))) {
                this.log("building " + jarFile.getName() + " with " + ejbFiles.size() + " files", 2);
                String publicId = this.getPublicId();
                this.writeJar(baseName, jarFile, ejbFiles, publicId);
            } else {
                this.log(jarFile.toString() + " is up to date.", 3);
            }
        }
        catch (SAXException se) {
            throw new BuildException("SAXException while parsing '" + descriptorFileName + "'. This probably indicates badly-formed XML.  Details: " + se.getMessage(), se);
        }
        catch (IOException ioe) {
            throw new BuildException("IOException while parsing'" + descriptorFileName + "'.  This probably indicates that the descriptor doesn't exist. Details: " + ioe.getMessage(), ioe);
        }
    }

    protected void checkConfiguration(String descriptorFileName, SAXParser saxParser) throws BuildException {
    }

    protected Hashtable<String, File> parseEjbFiles(String descriptorFileName, SAXParser saxParser) throws IOException, SAXException {
        try (InputStream descriptorStream = Files.newInputStream(new File(this.config.descriptorDir, descriptorFileName).toPath(), new OpenOption[0]);){
            saxParser.parse(new InputSource(descriptorStream), (HandlerBase)this.handler);
            Hashtable<String, File> hashtable = this.handler.getFiles();
            return hashtable;
        }
    }

    protected void addSupportClasses(Hashtable<String, File> ejbFiles) {
        Project project = this.task.getProject();
        for (FileSet supportFileSet : this.config.supportFileSets) {
            File supportBaseDir = supportFileSet.getDir(project);
            DirectoryScanner supportScanner = supportFileSet.getDirectoryScanner(project);
            for (String supportFile : supportScanner.getIncludedFiles()) {
                ejbFiles.put(supportFile, new File(supportBaseDir, supportFile));
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected String getJarBaseName(String descriptorFileName) {
        String baseName = "";
        if ("basejarname".equals(this.config.namingScheme.getValue())) {
            String canonicalDescriptor = descriptorFileName.replace('\\', '/');
            int index = canonicalDescriptor.lastIndexOf(47);
            if (index == -1) return baseName + this.config.baseJarName;
            baseName = descriptorFileName.substring(0, index + 1);
            return baseName + this.config.baseJarName;
        }
        if ("descriptor".equals(this.config.namingScheme.getValue())) {
            int lastSeparatorIndex = descriptorFileName.lastIndexOf(File.separator);
            int endBaseName = -1;
            endBaseName = lastSeparatorIndex != -1 ? descriptorFileName.indexOf(this.config.baseNameTerminator, lastSeparatorIndex) : descriptorFileName.indexOf(this.config.baseNameTerminator);
            if (endBaseName == -1) throw new BuildException("Unable to determine jar name from descriptor \"%s\"", descriptorFileName);
            return descriptorFileName.substring(0, endBaseName);
        }
        if ("directory".equals(this.config.namingScheme.getValue())) {
            File descriptorFile = new File(this.config.descriptorDir, descriptorFileName);
            String path = descriptorFile.getAbsolutePath();
            int lastSeparatorIndex = path.lastIndexOf(File.separator);
            if (lastSeparatorIndex == -1) {
                throw new BuildException("Unable to determine directory name holding descriptor");
            }
            String dirName = path.substring(0, lastSeparatorIndex);
            int dirSeparatorIndex = dirName.lastIndexOf(File.separator);
            if (dirSeparatorIndex == -1) return dirName;
            dirName = dirName.substring(dirSeparatorIndex + 1);
            return dirName;
        }
        if (!"ejb-name".equals(this.config.namingScheme.getValue())) return baseName;
        return this.handler.getEjbName();
    }

    public String getVendorDDPrefix(String baseName, String descriptorFileName) {
        String ddPrefix = null;
        if (this.config.namingScheme.getValue().equals("descriptor")) {
            ddPrefix = baseName + this.config.baseNameTerminator;
        } else if (this.config.namingScheme.getValue().equals("basejarname") || this.config.namingScheme.getValue().equals("ejb-name") || this.config.namingScheme.getValue().equals("directory")) {
            String canonicalDescriptor = descriptorFileName.replace('\\', '/');
            int index = canonicalDescriptor.lastIndexOf(47);
            ddPrefix = index == -1 ? "" : descriptorFileName.substring(0, index + 1);
        }
        return ddPrefix;
    }

    protected void addVendorFiles(Hashtable<String, File> ejbFiles, String ddPrefix) {
    }

    File getVendorOutputJarFile(String baseName) {
        return new File(this.destDir, baseName + this.genericJarSuffix);
    }

    protected boolean needToRebuild(Hashtable<String, File> ejbFiles, File jarFile) {
        if (jarFile.exists()) {
            long lastBuild = jarFile.lastModified();
            for (File currentFile : ejbFiles.values()) {
                if (lastBuild >= currentFile.lastModified()) continue;
                this.log("Build needed because " + currentFile.getPath() + " is out of date", 3);
                return true;
            }
            return false;
        }
        return true;
    }

    protected String getPublicId() {
        return this.handler.getPublicId();
    }

    protected File getManifestFile(String prefix) {
        File manifestFile = new File(this.getConfig().descriptorDir, prefix + "manifest.mf");
        if (manifestFile.exists()) {
            return manifestFile;
        }
        if (this.config.manifest != null) {
            return this.config.manifest;
        }
        return null;
    }

    protected void writeJar(String baseName, File jarfile, Hashtable<String, File> files, String publicId) throws BuildException {
        if (this.addedfiles == null) {
            this.addedfiles = new HashSet<String>();
        } else {
            this.addedfiles.clear();
        }
        try {
            if (jarfile.exists()) {
                jarfile.delete();
            }
            jarfile.getParentFile().mkdirs();
            jarfile.createNewFile();
            Manifest manifest = null;
            try (InputStream in = null;){
                File manifestFile = files.get(MANIFEST);
                if (manifestFile != null && manifestFile.exists()) {
                    in = Files.newInputStream(manifestFile.toPath(), new OpenOption[0]);
                } else {
                    String defaultManifest = "/org/apache/tools/ant/defaultManifest.mf";
                    in = this.getClass().getResourceAsStream(defaultManifest);
                    if (in == null) {
                        throw new BuildException("Could not find default manifest: %s", defaultManifest);
                    }
                }
                manifest = new Manifest(in);
            }
            try (JarOutputStream jarStream = new JarOutputStream(Files.newOutputStream(jarfile.toPath(), new OpenOption[0]), manifest);){
                jarStream.setMethod(8);
                for (Map.Entry<String, File> entryFiles : files.entrySet()) {
                    String entryName = entryFiles.getKey();
                    if (entryName.equals(MANIFEST)) continue;
                    File entryFile = entryFiles.getValue();
                    this.log("adding file '" + entryName + "'", 3);
                    this.addFileToJar(jarStream, entryFile, entryName);
                    InnerClassFilenameFilter flt = new InnerClassFilenameFilter(entryFile.getName());
                    File entryDir = entryFile.getParentFile();
                    String[] innerfiles = entryDir.list(flt);
                    if (innerfiles == null) continue;
                    for (String innerfile : innerfiles) {
                        int entryIndex = entryName.lastIndexOf(entryFile.getName()) - 1;
                        entryName = entryIndex < 0 ? innerfile : entryName.substring(0, entryIndex) + File.separatorChar + innerfile;
                        entryFile = new File(this.config.srcDir, entryName);
                        this.log("adding innerclass file '" + entryName + "'", 3);
                        this.addFileToJar(jarStream, entryFile, entryName);
                    }
                }
            }
        }
        catch (IOException ioe) {
            String msg = "IOException while processing ejb-jar file '" + jarfile.toString() + "'. Details: " + ioe.getMessage();
            throw new BuildException(msg, ioe);
        }
    }

    protected void checkAndAddDependants(Hashtable<String, File> checkEntries) throws BuildException {
        if (this.dependencyAnalyzer == null) {
            return;
        }
        this.dependencyAnalyzer.reset();
        for (String entryName : checkEntries.keySet()) {
            if (!entryName.endsWith(".class")) continue;
            String className = entryName.substring(0, entryName.length() - ".class".length());
            className = className.replace(File.separatorChar, '/');
            className = className.replace('/', '.');
            this.dependencyAnalyzer.addRootClass(className);
        }
        for (String classname : Collections.list(this.dependencyAnalyzer.getClassDependencies())) {
            String location = classname.replace('.', File.separatorChar) + ".class";
            File classFile = new File(this.config.srcDir, location);
            if (!classFile.exists()) continue;
            checkEntries.put(location, classFile);
            this.log("dependent class: " + classname + " - " + classFile, 3);
        }
    }

    protected ClassLoader getClassLoaderForBuild() {
        if (this.classpathLoader != null) {
            return this.classpathLoader;
        }
        Path combinedClasspath = this.getCombinedClasspath();
        this.classpathLoader = combinedClasspath == null ? this.getClass().getClassLoader() : this.getTask().getProject().createClassLoader(combinedClasspath);
        return this.classpathLoader;
    }

    @Override
    public void validateConfigured() throws BuildException {
        if (this.destDir == null || !this.destDir.isDirectory()) {
            throw new BuildException("A valid destination directory must be specified using the \"destdir\" attribute.", this.getLocation());
        }
    }
}

