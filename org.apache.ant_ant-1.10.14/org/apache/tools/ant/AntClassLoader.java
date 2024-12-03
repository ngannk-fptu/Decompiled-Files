/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.launch.Locator
 */
package org.apache.tools.ant;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.SubBuildListener;
import org.apache.tools.ant.launch.Locator;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.LoaderUtils;
import org.apache.tools.ant.util.ReflectUtil;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.util.VectorSet;
import org.apache.tools.zip.ZipLong;

public class AntClassLoader
extends ClassLoader
implements SubBuildListener,
Closeable {
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private static final boolean IS_ATLEAST_JAVA9 = JavaEnvUtils.isAtLeastJavaVersion("9");
    private static final Class[] MR_JARFILE_CTOR_ARGS;
    private static final Object MR_JARFILE_CTOR_RUNTIME_VERSION_VAL;
    private static final int BUFFER_SIZE = 8192;
    private static final int NUMBER_OF_STRINGS = 256;
    private final Vector<File> pathComponents = new VectorSet<File>();
    private Project project;
    private boolean parentFirst = true;
    private final Vector<String> systemPackages = new Vector();
    private final Vector<String> loaderPackages = new Vector();
    private boolean ignoreBase = false;
    private ClassLoader parent = null;
    private Hashtable<File, JarFile> jarFiles = new Hashtable();
    private static Map<String, String> pathMap;
    private ClassLoader savedContextLoader = null;
    private boolean isContextLoaderSaved = false;
    private static final ZipLong EOCD_SIG;
    private static final ZipLong SINGLE_SEGMENT_SPLIT_MARKER;

    public AntClassLoader(ClassLoader parent, Project project, Path classpath) {
        this.setParent(parent);
        this.setClassPath(classpath);
        this.setProject(project);
    }

    public AntClassLoader() {
        this.setParent(null);
    }

    public AntClassLoader(Project project, Path classpath) {
        this.setParent(null);
        this.setProject(project);
        this.setClassPath(classpath);
    }

    public AntClassLoader(ClassLoader parent, Project project, Path classpath, boolean parentFirst) {
        this(project, classpath);
        if (parent != null) {
            this.setParent(parent);
        }
        this.setParentFirst(parentFirst);
        this.addJavaLibraries();
    }

    public AntClassLoader(Project project, Path classpath, boolean parentFirst) {
        this(null, project, classpath, parentFirst);
    }

    public AntClassLoader(ClassLoader parent, boolean parentFirst) {
        this.setParent(parent);
        this.project = null;
        this.parentFirst = parentFirst;
    }

    public void setProject(Project project) {
        this.project = project;
        if (project != null) {
            project.addBuildListener(this);
        }
    }

    public void setClassPath(Path classpath) {
        this.pathComponents.removeAllElements();
        if (classpath != null) {
            for (String pathElement : classpath.concatSystemClasspath("ignore").list()) {
                try {
                    this.addPathElement(pathElement);
                }
                catch (BuildException e) {
                    this.log("Ignoring path element " + pathElement + " from classpath due to exception " + e, 4);
                }
            }
        }
    }

    public void setParent(ClassLoader parent) {
        this.parent = parent == null ? AntClassLoader.class.getClassLoader() : parent;
    }

    public void setParentFirst(boolean parentFirst) {
        this.parentFirst = parentFirst;
    }

    protected void log(String message, int priority) {
        if (this.project != null) {
            this.project.log(message, priority);
        } else if (priority < 2) {
            System.err.println(message);
        }
    }

    public void setThreadContextLoader() {
        if (this.isContextLoaderSaved) {
            throw new BuildException("Context loader has not been reset");
        }
        if (LoaderUtils.isContextLoaderAvailable()) {
            this.savedContextLoader = LoaderUtils.getContextClassLoader();
            ClassLoader loader = this;
            if (this.project != null && "only".equals(this.project.getProperty("build.sysclasspath"))) {
                loader = this.getClass().getClassLoader();
            }
            LoaderUtils.setContextClassLoader(loader);
            this.isContextLoaderSaved = true;
        }
    }

    public void resetThreadContextLoader() {
        if (LoaderUtils.isContextLoaderAvailable() && this.isContextLoaderSaved) {
            LoaderUtils.setContextClassLoader(this.savedContextLoader);
            this.savedContextLoader = null;
            this.isContextLoaderSaved = false;
        }
    }

    public void addPathElement(String pathElement) throws BuildException {
        File pathComponent = this.project != null ? this.project.resolveFile(pathElement) : new File(pathElement);
        try {
            this.addPathFile(pathComponent);
        }
        catch (IOException e) {
            throw new BuildException(e);
        }
    }

    public void addPathComponent(File file) {
        if (this.pathComponents.contains(file)) {
            return;
        }
        this.pathComponents.addElement(file);
    }

    protected void addPathFile(File pathComponent) throws IOException {
        if (!this.pathComponents.contains(pathComponent)) {
            this.pathComponents.addElement(pathComponent);
        }
        if (pathComponent.isDirectory()) {
            return;
        }
        String absPathPlusTimeAndLength = pathComponent.getAbsolutePath() + pathComponent.lastModified() + "-" + pathComponent.length();
        String classpath = pathMap.get(absPathPlusTimeAndLength);
        if (classpath == null) {
            try (JarFile jarFile = AntClassLoader.newJarFile(pathComponent);){
                Manifest manifest = jarFile.getManifest();
                if (manifest == null) {
                    return;
                }
                classpath = manifest.getMainAttributes().getValue(Attributes.Name.CLASS_PATH);
            }
            if (classpath == null) {
                classpath = "";
            }
            pathMap.put(absPathPlusTimeAndLength, classpath);
        }
        if (!classpath.isEmpty()) {
            URL baseURL = FILE_UTILS.getFileURL(pathComponent);
            StringTokenizer st = new StringTokenizer(classpath);
            while (st.hasMoreTokens()) {
                String classpathElement = st.nextToken();
                URL libraryURL = new URL(baseURL, classpathElement);
                if (!libraryURL.getProtocol().equals("file")) {
                    this.log("Skipping jar library " + classpathElement + " since only relative URLs are supported by this loader", 3);
                    continue;
                }
                String decodedPath = Locator.decodeUri((String)libraryURL.getFile());
                File libraryFile = new File(decodedPath);
                if (!libraryFile.exists() || this.isInPath(libraryFile)) continue;
                this.addPathFile(libraryFile);
            }
        }
    }

    public String getClasspath() {
        StringBuilder sb = new StringBuilder();
        for (File component : this.pathComponents) {
            if (sb.length() > 0) {
                sb.append(File.pathSeparator);
            }
            sb.append(component.getAbsolutePath());
        }
        return sb.toString();
    }

    public synchronized void setIsolated(boolean isolated) {
        this.ignoreBase = isolated;
    }

    @Deprecated
    public static void initializeClass(Class<?> theClass) {
        Constructor<?>[] cons = theClass.getDeclaredConstructors();
        if (cons != null && cons.length > 0 && cons[0] != null) {
            String[] strs = new String[256];
            try {
                cons[0].newInstance(strs);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
    }

    public void addSystemPackageRoot(String packageRoot) {
        this.systemPackages.addElement(packageRoot + (packageRoot.endsWith(".") ? "" : "."));
    }

    public void addLoaderPackageRoot(String packageRoot) {
        this.loaderPackages.addElement(packageRoot + (packageRoot.endsWith(".") ? "" : "."));
    }

    public Class<?> forceLoadClass(String classname) throws ClassNotFoundException {
        this.log("force loading " + classname, 4);
        Class<?> theClass = this.findLoadedClass(classname);
        if (theClass == null) {
            theClass = this.findClass(classname);
        }
        return theClass;
    }

    public Class<?> forceLoadSystemClass(String classname) throws ClassNotFoundException {
        this.log("force system loading " + classname, 4);
        Class<?> theClass = this.findLoadedClass(classname);
        if (theClass == null) {
            theClass = this.findBaseClass(classname);
        }
        return theClass;
    }

    @Override
    public InputStream getResourceAsStream(String name) {
        InputStream resourceStream = null;
        if (this.isParentFirst(name)) {
            resourceStream = this.loadBaseResource(name);
        }
        if (resourceStream != null) {
            this.log("ResourceStream for " + name + " loaded from parent loader", 4);
        } else {
            resourceStream = this.loadResource(name);
            if (resourceStream != null) {
                this.log("ResourceStream for " + name + " loaded from ant loader", 4);
            }
        }
        if (resourceStream == null && !this.isParentFirst(name) && (resourceStream = this.ignoreBase ? (this.getRootLoader() == null ? null : this.getRootLoader().getResourceAsStream(name)) : this.loadBaseResource(name)) != null) {
            this.log("ResourceStream for " + name + " loaded from parent loader", 4);
        }
        if (resourceStream == null) {
            this.log("Couldn't load ResourceStream for " + name, 4);
        }
        return resourceStream;
    }

    private InputStream loadResource(String name) {
        return this.pathComponents.stream().map(path -> this.getResourceStream((File)path, name)).filter(Objects::nonNull).findFirst().orElse(null);
    }

    private InputStream loadBaseResource(String name) {
        return this.parent == null ? super.getResourceAsStream(name) : this.parent.getResourceAsStream(name);
    }

    private InputStream getResourceStream(File file, String resourceName) {
        try {
            JarFile jarFile = this.jarFiles.get(file);
            if (jarFile == null && file.isDirectory()) {
                File resource = new File(file, resourceName);
                if (resource.exists()) {
                    return Files.newInputStream(resource.toPath(), new OpenOption[0]);
                }
            } else {
                JarEntry entry;
                if (jarFile == null) {
                    if (!file.exists()) {
                        return null;
                    }
                    jarFile = AntClassLoader.newJarFile(file);
                    this.jarFiles.put(file, jarFile);
                    jarFile = this.jarFiles.get(file);
                }
                if ((entry = jarFile.getJarEntry(resourceName)) != null) {
                    return jarFile.getInputStream(entry);
                }
            }
        }
        catch (Exception e) {
            this.log("Ignoring Exception " + e.getClass().getName() + ": " + e.getMessage() + " reading resource " + resourceName + " from " + file, 3);
        }
        return null;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean isParentFirst(String resourceName) {
        if (!this.loaderPackages.stream().noneMatch(resourceName::startsWith)) return false;
        if (this.systemPackages.stream().anyMatch(resourceName::startsWith)) return true;
        if (!this.parentFirst) return false;
        return true;
    }

    private ClassLoader getRootLoader() {
        ClassLoader ret;
        for (ret = this.getClass().getClassLoader(); ret != null && ret.getParent() != null; ret = ret.getParent()) {
        }
        return ret;
    }

    @Override
    public URL getResource(String name) {
        URL url = null;
        if (this.isParentFirst(name)) {
            URL uRL = url = this.parent == null ? super.getResource(name) : this.parent.getResource(name);
        }
        if (url != null) {
            this.log("Resource " + name + " loaded from parent loader", 4);
        } else {
            url = this.getUrl(name);
        }
        if (url == null && !this.isParentFirst(name)) {
            if (this.ignoreBase) {
                url = this.getRootLoader() == null ? null : this.getRootLoader().getResource(name);
            } else {
                URL uRL = url = this.parent == null ? super.getResource(name) : this.parent.getResource(name);
            }
            if (url != null) {
                this.log("Resource " + name + " loaded from parent loader", 4);
            }
        }
        if (url == null) {
            this.log("Couldn't load Resource " + name, 4);
        }
        return url;
    }

    private URL getUrl(String name) {
        URL url = null;
        for (File pathComponent : this.pathComponents) {
            url = this.getResourceURL(pathComponent, name);
            if (url == null) continue;
            this.log("Resource " + name + " loaded from ant loader", 4);
            break;
        }
        return url;
    }

    public Enumeration<URL> getNamedResources(String name) throws IOException {
        return this.findResources(name, false);
    }

    @Override
    protected URL findResource(String name) {
        return this.getUrl(name);
    }

    @Override
    protected Enumeration<URL> findResources(String name) throws IOException {
        return this.findResources(name, true);
    }

    protected Enumeration<URL> findResources(String name, boolean skipParent) throws IOException {
        ResourceEnumeration mine = new ResourceEnumeration(name);
        Enumeration<URL> base = this.parent != null && !skipParent ? this.parent.getResources(name) : Collections.emptyEnumeration();
        if (this.isParentFirst(name)) {
            return AntClassLoader.append(base, mine);
        }
        if (this.ignoreBase) {
            return this.getRootLoader() == null ? mine : AntClassLoader.append(mine, this.getRootLoader().getResources(name));
        }
        return AntClassLoader.append(mine, base);
    }

    private static Enumeration<URL> append(Enumeration<URL> one, Enumeration<URL> two) {
        return Stream.concat(Collections.list(one).stream(), Collections.list(two).stream()).collect(Collectors.collectingAndThen(Collectors.toList(), Collections::enumeration));
    }

    protected URL getResourceURL(File file, String resourceName) {
        try {
            JarFile jarFile = this.jarFiles.get(file);
            if (jarFile == null && file.isDirectory()) {
                File resource = new File(file, resourceName);
                if (resource.exists()) {
                    try {
                        return FILE_UTILS.getFileURL(resource);
                    }
                    catch (MalformedURLException ex) {
                        return null;
                    }
                }
            } else {
                JarEntry entry;
                if (jarFile == null) {
                    if (file.exists()) {
                        if (!AntClassLoader.isZip(file)) {
                            String msg = "CLASSPATH element " + file + " is not a JAR.";
                            this.log(msg, 1);
                            return null;
                        }
                    } else {
                        return null;
                    }
                    jarFile = AntClassLoader.newJarFile(file);
                    this.jarFiles.put(file, jarFile);
                    jarFile = this.jarFiles.get(file);
                }
                if ((entry = jarFile.getJarEntry(resourceName)) != null) {
                    try {
                        return new URL("jar:" + FILE_UTILS.getFileURL(file) + "!/" + entry);
                    }
                    catch (MalformedURLException ex) {
                        return null;
                    }
                }
            }
        }
        catch (Exception e) {
            String msg = "Unable to obtain resource from " + file + ": ";
            this.log(msg + e, 1);
            this.log(StringUtils.getStackTrace(e), 1);
        }
        return null;
    }

    @Override
    protected synchronized Class<?> loadClass(String classname, boolean resolve) throws ClassNotFoundException {
        Class<?> theClass = this.findLoadedClass(classname);
        if (theClass != null) {
            return theClass;
        }
        if (this.isParentFirst(classname)) {
            try {
                theClass = this.findBaseClass(classname);
                this.log("Class " + classname + " loaded from parent loader (parentFirst)", 4);
            }
            catch (ClassNotFoundException cnfe) {
                theClass = this.findClass(classname);
                this.log("Class " + classname + " loaded from ant loader (parentFirst)", 4);
            }
        } else {
            try {
                theClass = this.findClass(classname);
                this.log("Class " + classname + " loaded from ant loader", 4);
            }
            catch (ClassNotFoundException cnfe) {
                if (this.ignoreBase) {
                    throw cnfe;
                }
                theClass = this.findBaseClass(classname);
                this.log("Class " + classname + " loaded from parent loader", 4);
            }
        }
        if (resolve) {
            this.resolveClass(theClass);
        }
        return theClass;
    }

    private String getClassFilename(String classname) {
        return classname.replace('.', '/') + ".class";
    }

    protected Class<?> defineClassFromData(File container, byte[] classData, String classname) throws IOException {
        this.definePackage(container, classname);
        ProtectionDomain currentPd = Project.class.getProtectionDomain();
        String classResource = this.getClassFilename(classname);
        CodeSource src = new CodeSource(FILE_UTILS.getFileURL(container), this.getCertificates(container, classResource));
        ProtectionDomain classesPd = new ProtectionDomain(src, currentPd.getPermissions(), this, currentPd.getPrincipals());
        return this.defineClass(classname, classData, 0, classData.length, classesPd);
    }

    protected void definePackage(File container, String className) throws IOException {
        int classIndex = className.lastIndexOf(46);
        if (classIndex == -1) {
            return;
        }
        String packageName = className.substring(0, classIndex);
        if (this.getPackage(packageName) != null) {
            return;
        }
        Manifest manifest = this.getJarManifest(container);
        if (manifest == null) {
            this.definePackage(packageName, null, null, null, null, null, null, null);
        } else {
            this.definePackage(container, packageName, manifest);
        }
    }

    private Manifest getJarManifest(File container) throws IOException {
        if (container.isDirectory()) {
            return null;
        }
        JarFile jarFile = this.jarFiles.get(container);
        if (jarFile == null) {
            return null;
        }
        return jarFile.getManifest();
    }

    private Certificate[] getCertificates(File container, String entry) {
        if (container.isDirectory()) {
            return null;
        }
        JarFile jarFile = this.jarFiles.get(container);
        if (jarFile == null) {
            return null;
        }
        JarEntry ent = jarFile.getJarEntry(entry);
        return ent == null ? null : ent.getCertificates();
    }

    protected void definePackage(File container, String packageName, Manifest manifest) {
        Attributes mainAttributes;
        String sectionName = packageName.replace('.', '/') + "/";
        String specificationTitle = null;
        String specificationVendor = null;
        String specificationVersion = null;
        String implementationTitle = null;
        String implementationVendor = null;
        String implementationVersion = null;
        String sealedString = null;
        URL sealBase = null;
        Attributes sectionAttributes = manifest.getAttributes(sectionName);
        if (sectionAttributes != null) {
            specificationTitle = sectionAttributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
            specificationVendor = sectionAttributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
            specificationVersion = sectionAttributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
            implementationTitle = sectionAttributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
            implementationVendor = sectionAttributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
            implementationVersion = sectionAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            sealedString = sectionAttributes.getValue(Attributes.Name.SEALED);
        }
        if ((mainAttributes = manifest.getMainAttributes()) != null) {
            if (specificationTitle == null) {
                specificationTitle = mainAttributes.getValue(Attributes.Name.SPECIFICATION_TITLE);
            }
            if (specificationVendor == null) {
                specificationVendor = mainAttributes.getValue(Attributes.Name.SPECIFICATION_VENDOR);
            }
            if (specificationVersion == null) {
                specificationVersion = mainAttributes.getValue(Attributes.Name.SPECIFICATION_VERSION);
            }
            if (implementationTitle == null) {
                implementationTitle = mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_TITLE);
            }
            if (implementationVendor == null) {
                implementationVendor = mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VENDOR);
            }
            if (implementationVersion == null) {
                implementationVersion = mainAttributes.getValue(Attributes.Name.IMPLEMENTATION_VERSION);
            }
            if (sealedString == null) {
                sealedString = mainAttributes.getValue(Attributes.Name.SEALED);
            }
        }
        if (sealedString != null && sealedString.equalsIgnoreCase("true")) {
            try {
                sealBase = new URL(FileUtils.getFileUtils().toURI(container.getAbsolutePath()));
            }
            catch (MalformedURLException malformedURLException) {
                // empty catch block
            }
        }
        this.definePackage(packageName, specificationTitle, specificationVersion, specificationVendor, implementationTitle, implementationVersion, implementationVendor, sealBase);
    }

    private Class<?> getClassFromStream(InputStream stream, String classname, File container) throws IOException, SecurityException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int bytesRead = -1;
        byte[] buffer = new byte[8192];
        while ((bytesRead = stream.read(buffer, 0, 8192)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        byte[] classData = baos.toByteArray();
        return this.defineClassFromData(container, classData, classname);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        this.log("Finding class " + name, 4);
        return this.findClassInComponents(name);
    }

    protected boolean isInPath(File component) {
        return this.pathComponents.contains(component);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private Class<?> findClassInComponents(String name) throws ClassNotFoundException {
        String classFilename = this.getClassFilename(name);
        Iterator<File> iterator = this.pathComponents.iterator();
        while (iterator.hasNext()) {
            File pathComponent = iterator.next();
            try {
                InputStream stream = this.getResourceStream(pathComponent, classFilename);
                try {
                    if (stream == null) continue;
                    this.log("Loaded from " + pathComponent + " " + classFilename, 4);
                    Class<?> clazz = this.getClassFromStream(stream, name, pathComponent);
                    return clazz;
                }
                finally {
                    if (stream == null) continue;
                    stream.close();
                }
            }
            catch (SecurityException se) {
                throw se;
            }
            catch (IOException ioe) {
                this.log("Exception reading component " + pathComponent + " (reason: " + ioe.getMessage() + ")", 3);
            }
        }
        throw new ClassNotFoundException(name);
    }

    private Class<?> findBaseClass(String name) throws ClassNotFoundException {
        return this.parent == null ? this.findSystemClass(name) : this.parent.loadClass(name);
    }

    public synchronized void cleanup() {
        for (JarFile jarFile : this.jarFiles.values()) {
            FileUtils.close(jarFile);
        }
        this.jarFiles = new Hashtable();
        if (this.project != null) {
            this.project.removeBuildListener(this);
        }
        this.project = null;
    }

    public ClassLoader getConfiguredParent() {
        return this.parent;
    }

    @Override
    public void buildStarted(BuildEvent event) {
    }

    @Override
    public void buildFinished(BuildEvent event) {
        this.cleanup();
    }

    @Override
    public void subBuildFinished(BuildEvent event) {
        if (event.getProject() == this.project) {
            this.cleanup();
        }
    }

    @Override
    public void subBuildStarted(BuildEvent event) {
    }

    @Override
    public void targetStarted(BuildEvent event) {
    }

    @Override
    public void targetFinished(BuildEvent event) {
    }

    @Override
    public void taskStarted(BuildEvent event) {
    }

    @Override
    public void taskFinished(BuildEvent event) {
    }

    @Override
    public void messageLogged(BuildEvent event) {
    }

    public void addJavaLibraries() {
        JavaEnvUtils.getJrePackages().forEach(this::addSystemPackageRoot);
    }

    public String toString() {
        return "AntClassLoader[" + this.getClasspath() + "]";
    }

    @Override
    public Enumeration<URL> getResources(String name) throws IOException {
        return this.getNamedResources(name);
    }

    @Override
    public void close() {
        this.cleanup();
    }

    public static AntClassLoader newAntClassLoader(ClassLoader parent, Project project, Path path, boolean parentFirst) {
        return new AntClassLoader(parent, project, path, parentFirst);
    }

    private static boolean isZip(File file) throws IOException {
        byte[] sig = new byte[4];
        if (AntClassLoader.readFully(file, sig)) {
            ZipLong start = new ZipLong(sig);
            return ZipLong.LFH_SIG.equals(start) || EOCD_SIG.equals(start) || ZipLong.DD_SIG.equals(start) || SINGLE_SEGMENT_SPLIT_MARKER.equals(start);
        }
        return false;
    }

    private static boolean readFully(File f, byte[] b) throws IOException {
        try (InputStream fis = Files.newInputStream(f.toPath(), new OpenOption[0]);){
            int count;
            int len = b.length;
            int x = 0;
            for (count = 0; count != len && (x = fis.read(b, count, len - count)) != -1; count += x) {
            }
            boolean bl = count == len;
            return bl;
        }
    }

    private static JarFile newJarFile(File file) throws IOException {
        if (!IS_ATLEAST_JAVA9 || MR_JARFILE_CTOR_ARGS == null || MR_JARFILE_CTOR_RUNTIME_VERSION_VAL == null) {
            return new JarFile(file);
        }
        return ReflectUtil.newInstance(JarFile.class, MR_JARFILE_CTOR_ARGS, new Object[]{file, true, 1, MR_JARFILE_CTOR_RUNTIME_VERSION_VAL});
    }

    static {
        AntClassLoader.registerAsParallelCapable();
        if (IS_ATLEAST_JAVA9) {
            Class[] ctorArgs = null;
            Object runtimeVersionVal = null;
            try {
                Class<?> runtimeVersionClass = Class.forName("java.lang.Runtime$Version");
                ctorArgs = new Class[]{File.class, Boolean.TYPE, Integer.TYPE, runtimeVersionClass};
                runtimeVersionVal = Runtime.class.getDeclaredMethod("version", new Class[0]).invoke(null, new Object[0]);
            }
            catch (Exception exception) {
                // empty catch block
            }
            MR_JARFILE_CTOR_ARGS = ctorArgs;
            MR_JARFILE_CTOR_RUNTIME_VERSION_VAL = runtimeVersionVal;
        } else {
            MR_JARFILE_CTOR_ARGS = null;
            MR_JARFILE_CTOR_RUNTIME_VERSION_VAL = null;
        }
        pathMap = Collections.synchronizedMap(new HashMap());
        EOCD_SIG = new ZipLong(101010256L);
        SINGLE_SEGMENT_SPLIT_MARKER = new ZipLong(808471376L);
    }

    private class ResourceEnumeration
    implements Enumeration<URL> {
        private final String resourceName;
        private int pathElementsIndex;
        private URL nextResource;

        ResourceEnumeration(String name) {
            this.resourceName = name;
            this.pathElementsIndex = 0;
            this.findNextResource();
        }

        @Override
        public boolean hasMoreElements() {
            return this.nextResource != null;
        }

        @Override
        public URL nextElement() {
            URL ret = this.nextResource;
            if (ret == null) {
                throw new NoSuchElementException();
            }
            this.findNextResource();
            return ret;
        }

        private void findNextResource() {
            URL url = null;
            while (this.pathElementsIndex < AntClassLoader.this.pathComponents.size() && url == null) {
                try {
                    File pathComponent = (File)AntClassLoader.this.pathComponents.elementAt(this.pathElementsIndex);
                    url = AntClassLoader.this.getResourceURL(pathComponent, this.resourceName);
                    ++this.pathElementsIndex;
                }
                catch (BuildException buildException) {}
            }
            this.nextResource = url;
        }
    }
}

