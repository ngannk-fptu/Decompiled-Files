/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.optional.ejb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Java;
import org.apache.tools.ant.taskdefs.optional.ejb.DescriptorHandler;
import org.apache.tools.ant.taskdefs.optional.ejb.GenericDeploymentTool;
import org.apache.tools.ant.types.Environment;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;
import org.xml.sax.HandlerBase;
import org.xml.sax.InputSource;

public class WeblogicDeploymentTool
extends GenericDeploymentTool {
    public static final String PUBLICID_EJB11 = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 1.1//EN";
    public static final String PUBLICID_EJB20 = "-//Sun Microsystems, Inc.//DTD Enterprise JavaBeans 2.0//EN";
    public static final String PUBLICID_WEBLOGIC_EJB510 = "-//BEA Systems, Inc.//DTD WebLogic 5.1.0 EJB//EN";
    public static final String PUBLICID_WEBLOGIC_EJB600 = "-//BEA Systems, Inc.//DTD WebLogic 6.0.0 EJB//EN";
    public static final String PUBLICID_WEBLOGIC_EJB700 = "-//BEA Systems, Inc.//DTD WebLogic 7.0.0 EJB//EN";
    protected static final String DEFAULT_WL51_EJB11_DTD_LOCATION = "/weblogic/ejb/deployment/xml/ejb-jar.dtd";
    protected static final String DEFAULT_WL60_EJB11_DTD_LOCATION = "/weblogic/ejb20/dd/xml/ejb11-jar.dtd";
    protected static final String DEFAULT_WL60_EJB20_DTD_LOCATION = "/weblogic/ejb20/dd/xml/ejb20-jar.dtd";
    protected static final String DEFAULT_WL51_DTD_LOCATION = "/weblogic/ejb/deployment/xml/weblogic-ejb-jar.dtd";
    protected static final String DEFAULT_WL60_51_DTD_LOCATION = "/weblogic/ejb20/dd/xml/weblogic510-ejb-jar.dtd";
    protected static final String DEFAULT_WL60_DTD_LOCATION = "/weblogic/ejb20/dd/xml/weblogic600-ejb-jar.dtd";
    protected static final String DEFAULT_WL70_DTD_LOCATION = "/weblogic/ejb20/dd/xml/weblogic700-ejb-jar.dtd";
    protected static final String DEFAULT_COMPILER = "default";
    protected static final String WL_DD = "weblogic-ejb-jar.xml";
    protected static final String WL_CMP_DD = "weblogic-cmp-rdbms-jar.xml";
    protected static final String COMPILER_EJB11 = "weblogic.ejbc";
    protected static final String COMPILER_EJB20 = "weblogic.ejbc20";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private String jarSuffix = ".jar";
    private String weblogicDTD;
    private String ejb11DTD;
    private boolean keepgenerated = false;
    private String ejbcClass = null;
    private String additionalArgs = "";
    private String additionalJvmArgs = "";
    private boolean keepGeneric = false;
    private String compiler = null;
    private boolean alwaysRebuild = true;
    private boolean noEJBC = false;
    private boolean newCMP = false;
    private Path wlClasspath = null;
    private List<Environment.Variable> sysprops = new Vector<Environment.Variable>();
    private Integer jvmDebugLevel = null;
    private File outputDir;

    public void addSysproperty(Environment.Variable sysp) {
        this.sysprops.add(sysp);
    }

    public Path createWLClasspath() {
        if (this.wlClasspath == null) {
            this.wlClasspath = new Path(this.getTask().getProject());
        }
        return this.wlClasspath.createPath();
    }

    public void setOutputDir(File outputDir) {
        this.outputDir = outputDir;
    }

    public void setWLClasspath(Path wlClasspath) {
        this.wlClasspath = wlClasspath;
    }

    public void setCompiler(String compiler) {
        this.compiler = compiler;
    }

    public void setRebuild(boolean rebuild) {
        this.alwaysRebuild = rebuild;
    }

    public void setJvmDebugLevel(Integer jvmDebugLevel) {
        this.jvmDebugLevel = jvmDebugLevel;
    }

    public Integer getJvmDebugLevel() {
        return this.jvmDebugLevel;
    }

    public void setSuffix(String inString) {
        this.jarSuffix = inString;
    }

    public void setKeepgeneric(boolean inValue) {
        this.keepGeneric = inValue;
    }

    public void setKeepgenerated(String inValue) {
        this.keepgenerated = Boolean.parseBoolean(inValue);
    }

    public void setArgs(String args) {
        this.additionalArgs = args;
    }

    public void setJvmargs(String args) {
        this.additionalJvmArgs = args;
    }

    public void setEjbcClass(String ejbcClass) {
        this.ejbcClass = ejbcClass;
    }

    public String getEjbcClass() {
        return this.ejbcClass;
    }

    public void setWeblogicdtd(String inString) {
        this.setEJBdtd(inString);
    }

    public void setWLdtd(String inString) {
        this.weblogicDTD = inString;
    }

    public void setEJBdtd(String inString) {
        this.ejb11DTD = inString;
    }

    public void setOldCMP(boolean oldCMP) {
        this.newCMP = !oldCMP;
    }

    public void setNewCMP(boolean newCMP) {
        this.newCMP = newCMP;
    }

    public void setNoEJBC(boolean noEJBC) {
        this.noEJBC = noEJBC;
    }

    @Override
    protected void registerKnownDTDs(DescriptorHandler handler) {
        handler.registerDTD(PUBLICID_EJB11, DEFAULT_WL51_EJB11_DTD_LOCATION);
        handler.registerDTD(PUBLICID_EJB11, DEFAULT_WL60_EJB11_DTD_LOCATION);
        handler.registerDTD(PUBLICID_EJB11, this.ejb11DTD);
        handler.registerDTD(PUBLICID_EJB20, DEFAULT_WL60_EJB20_DTD_LOCATION);
    }

    protected DescriptorHandler getWeblogicDescriptorHandler(final File srcDir) {
        DescriptorHandler handler = new DescriptorHandler(this.getTask(), srcDir){

            @Override
            protected void processElement() {
                if ("type-storage".equals(this.currentElement)) {
                    this.ejbFiles.put(this.currentText, new File(srcDir, this.currentText.substring("META-INF/".length())));
                }
            }
        };
        handler.registerDTD(PUBLICID_WEBLOGIC_EJB510, DEFAULT_WL51_DTD_LOCATION);
        handler.registerDTD(PUBLICID_WEBLOGIC_EJB510, DEFAULT_WL60_51_DTD_LOCATION);
        handler.registerDTD(PUBLICID_WEBLOGIC_EJB600, DEFAULT_WL60_DTD_LOCATION);
        handler.registerDTD(PUBLICID_WEBLOGIC_EJB700, DEFAULT_WL70_DTD_LOCATION);
        handler.registerDTD(PUBLICID_WEBLOGIC_EJB510, this.weblogicDTD);
        handler.registerDTD(PUBLICID_WEBLOGIC_EJB600, this.weblogicDTD);
        this.getConfig().dtdLocations.forEach(l -> handler.registerDTD(l.getPublicId(), l.getLocation()));
        return handler;
    }

    @Override
    protected void addVendorFiles(Hashtable<String, File> ejbFiles, String ddPrefix) {
        File weblogicDD = new File(this.getConfig().descriptorDir, ddPrefix + WL_DD);
        if (!weblogicDD.exists()) {
            this.log("Unable to locate weblogic deployment descriptor. It was expected to be in " + weblogicDD.getPath(), 1);
            return;
        }
        ejbFiles.put("META-INF/weblogic-ejb-jar.xml", weblogicDD);
        if (!this.newCMP) {
            this.log("The old method for locating CMP files has been DEPRECATED.", 3);
            this.log("Please adjust your weblogic descriptor and set newCMP=\"true\" to use the new CMP descriptor inclusion mechanism. ", 3);
            File weblogicCMPDD = new File(this.getConfig().descriptorDir, ddPrefix + WL_CMP_DD);
            if (weblogicCMPDD.exists()) {
                ejbFiles.put("META-INF/weblogic-cmp-rdbms-jar.xml", weblogicCMPDD);
            }
        } else {
            try {
                File ejbDescriptor = ejbFiles.get("META-INF/ejb-jar.xml");
                SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
                saxParserFactory.setValidating(true);
                SAXParser saxParser = saxParserFactory.newSAXParser();
                DescriptorHandler handler = this.getWeblogicDescriptorHandler(ejbDescriptor.getParentFile());
                try (InputStream in = Files.newInputStream(weblogicDD.toPath(), new OpenOption[0]);){
                    saxParser.parse(new InputSource(in), (HandlerBase)handler);
                }
                ejbFiles.putAll(handler.getFiles());
            }
            catch (Exception e) {
                throw new BuildException("Exception while adding Vendor specific files: " + e.toString(), e);
            }
        }
    }

    @Override
    File getVendorOutputJarFile(String baseName) {
        return new File(this.getDestDir(), baseName + this.jarSuffix);
    }

    private void buildWeblogicJar(File sourceJar, File destJar, String publicId) {
        if (this.noEJBC) {
            try {
                FILE_UTILS.copyFile(sourceJar, destJar);
                if (!this.keepgenerated) {
                    sourceJar.delete();
                }
                return;
            }
            catch (IOException e) {
                throw new BuildException("Unable to write EJB jar", e);
            }
        }
        String ejbcClassName = this.ejbcClass;
        try {
            Java javaTask = new Java(this.getTask());
            javaTask.setTaskName("ejbc");
            javaTask.createJvmarg().setLine(this.additionalJvmArgs);
            this.sysprops.forEach(javaTask::addSysproperty);
            if (this.getJvmDebugLevel() != null) {
                javaTask.createJvmarg().setLine(" -Dweblogic.StdoutSeverityLevel=" + this.jvmDebugLevel);
            }
            if (ejbcClassName == null) {
                if (PUBLICID_EJB11.equals(publicId)) {
                    ejbcClassName = COMPILER_EJB11;
                } else if (PUBLICID_EJB20.equals(publicId)) {
                    ejbcClassName = COMPILER_EJB20;
                } else {
                    this.log("Unrecognized publicId " + publicId + " - using EJB 1.1 compiler", 1);
                    ejbcClassName = COMPILER_EJB11;
                }
            }
            javaTask.setClassname(ejbcClassName);
            javaTask.createArg().setLine(this.additionalArgs);
            if (this.keepgenerated) {
                javaTask.createArg().setValue("-keepgenerated");
            }
            if (this.compiler == null) {
                String buildCompiler = this.getTask().getProject().getProperty("build.compiler");
                if ("jikes".equals(buildCompiler)) {
                    javaTask.createArg().setValue("-compiler");
                    javaTask.createArg().setValue("jikes");
                }
            } else if (!DEFAULT_COMPILER.equals(this.compiler)) {
                javaTask.createArg().setValue("-compiler");
                javaTask.createArg().setLine(this.compiler);
            }
            Path combinedClasspath = this.getCombinedClasspath();
            if (this.wlClasspath != null && combinedClasspath != null && !combinedClasspath.toString().trim().isEmpty()) {
                javaTask.createArg().setValue("-classpath");
                javaTask.createArg().setPath(combinedClasspath);
            }
            javaTask.createArg().setValue(sourceJar.getPath());
            if (this.outputDir == null) {
                javaTask.createArg().setValue(destJar.getPath());
            } else {
                javaTask.createArg().setValue(this.outputDir.getPath());
            }
            Path classpath = this.wlClasspath;
            if (classpath == null) {
                classpath = this.getCombinedClasspath();
            }
            javaTask.setFork(true);
            if (classpath != null) {
                javaTask.setClasspath(classpath);
            }
            this.log("Calling " + ejbcClassName + " for " + sourceJar.toString(), 3);
            if (javaTask.executeJava() != 0) {
                throw new BuildException("Ejbc reported an error");
            }
        }
        catch (Exception e) {
            throw new BuildException("Exception while calling " + ejbcClassName + ". Details: " + e.toString(), e);
        }
    }

    @Override
    protected void writeJar(String baseName, File jarFile, Hashtable<String, File> files, String publicId) throws BuildException {
        File genericJarFile = super.getVendorOutputJarFile(baseName);
        super.writeJar(baseName, genericJarFile, files, publicId);
        if (this.alwaysRebuild || this.isRebuildRequired(genericJarFile, jarFile)) {
            this.buildWeblogicJar(genericJarFile, jarFile, publicId);
        }
        if (!this.keepGeneric) {
            this.log("deleting generic jar " + genericJarFile.toString(), 3);
            genericJarFile.delete();
        }
    }

    @Override
    public void validateConfigured() throws BuildException {
        super.validateConfigured();
    }

    protected boolean isRebuildRequired(File genericJarFile, File weblogicJarFile) {
        boolean rebuild = false;
        JarFile genericJar = null;
        JarFile wlJar = null;
        File newWLJarFile = null;
        JarOutputStream newJarStream = null;
        ClassLoader genericLoader = null;
        try {
            this.log("Checking if weblogic Jar needs to be rebuilt for jar " + weblogicJarFile.getName(), 3);
            if (genericJarFile.exists() && genericJarFile.isFile() && weblogicJarFile.exists() && weblogicJarFile.isFile()) {
                genericJar = new JarFile(genericJarFile);
                wlJar = new JarFile(weblogicJarFile);
                HashMap<String, JarEntry> replaceEntries = new HashMap<String, JarEntry>();
                Map<String, JarEntry> genericEntries = genericJar.stream().collect(Collectors.toMap(je -> je.getName().replace('\\', '/'), je -> je, (a, b) -> b));
                Map<String, JarEntry> wlEntries = wlJar.stream().collect(Collectors.toMap(ZipEntry::getName, je -> je, (a, b) -> b));
                genericLoader = this.getClassLoaderFromJar(genericJarFile);
                for (String filepath : genericEntries.keySet()) {
                    if (!wlEntries.containsKey(filepath)) {
                        this.log("File " + filepath + " not present in weblogic jar", 3);
                        rebuild = true;
                        break;
                    }
                    JarEntry genericEntry = genericEntries.get(filepath);
                    JarEntry wlEntry = wlEntries.get(filepath);
                    if (genericEntry.getCrc() == wlEntry.getCrc() && genericEntry.getSize() == wlEntry.getSize()) continue;
                    if (genericEntry.getName().endsWith(".class")) {
                        String classname = genericEntry.getName().replace(File.separatorChar, '.').replace('/', '.');
                        Class<?> genclass = genericLoader.loadClass(classname = classname.substring(0, classname.lastIndexOf(".class")));
                        if (genclass.isInterface()) {
                            this.log("Interface " + genclass.getName() + " has changed", 3);
                            rebuild = true;
                            break;
                        }
                        replaceEntries.put(filepath, genericEntry);
                        continue;
                    }
                    if (genericEntry.getName().equals("META-INF/MANIFEST.MF")) continue;
                    this.log("Non class file " + genericEntry.getName() + " has changed", 3);
                    rebuild = true;
                    break;
                }
                if (!rebuild) {
                    this.log("No rebuild needed - updating jar", 3);
                    newWLJarFile = new File(weblogicJarFile.getAbsolutePath() + ".temp");
                    if (newWLJarFile.exists()) {
                        newWLJarFile.delete();
                    }
                    newJarStream = new JarOutputStream(Files.newOutputStream(newWLJarFile.toPath(), new OpenOption[0]));
                    newJarStream.setLevel(0);
                    for (JarEntry je2 : wlEntries.values()) {
                        int bytesRead;
                        InputStream is;
                        if (je2.getCompressedSize() == -1L || je2.getCompressedSize() == je2.getSize()) {
                            newJarStream.setLevel(0);
                        } else {
                            newJarStream.setLevel(9);
                        }
                        if (replaceEntries.containsKey(je2.getName())) {
                            this.log("Updating Bean class from generic Jar " + je2.getName(), 3);
                            je2 = (JarEntry)replaceEntries.get(je2.getName());
                            is = genericJar.getInputStream(je2);
                        } else {
                            is = wlJar.getInputStream(je2);
                        }
                        newJarStream.putNextEntry(new JarEntry(je2.getName()));
                        byte[] buffer = new byte[1024];
                        while ((bytesRead = is.read(buffer)) != -1) {
                            newJarStream.write(buffer, 0, bytesRead);
                        }
                        is.close();
                    }
                } else {
                    this.log("Weblogic Jar rebuild needed due to changed interface or XML", 3);
                }
            } else {
                rebuild = true;
            }
        }
        catch (ClassNotFoundException cnfe) {
            String cnfmsg = "ClassNotFoundException while processing ejb-jar file. Details: " + cnfe.getMessage();
            throw new BuildException(cnfmsg, cnfe);
        }
        catch (IOException ioe) {
            String msg = "IOException while processing ejb-jar file . Details: " + ioe.getMessage();
            throw new BuildException(msg, ioe);
        }
        finally {
            FileUtils.close(genericJar);
            FileUtils.close(wlJar);
            FileUtils.close(newJarStream);
            if (newJarStream != null) {
                try {
                    FILE_UTILS.rename(newWLJarFile, weblogicJarFile);
                }
                catch (IOException renameException) {
                    this.log(renameException.getMessage(), 1);
                    rebuild = true;
                }
            }
            if (genericLoader instanceof AntClassLoader) {
                AntClassLoader loader = (AntClassLoader)genericLoader;
                loader.cleanup();
            }
        }
        return rebuild;
    }

    protected ClassLoader getClassLoaderFromJar(File classjar) throws IOException {
        Path lookupPath = new Path(this.getTask().getProject());
        lookupPath.setLocation(classjar);
        Path classpath = this.getCombinedClasspath();
        if (classpath != null) {
            lookupPath.append(classpath);
        }
        return this.getTask().getProject().createClassLoader(lookupPath);
    }
}

