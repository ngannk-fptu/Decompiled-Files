/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.launch.Locator
 */
package org.apache.tools.ant;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.WeakHashMap;
import java.util.stream.Collectors;
import org.apache.tools.ant.AntClassLoader;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.ComponentHelper;
import org.apache.tools.ant.Executor;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Main;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.SubBuildListener;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskAdapter;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.helper.DefaultExecutor;
import org.apache.tools.ant.input.DefaultInputHandler;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.launch.Locator;
import org.apache.tools.ant.types.Description;
import org.apache.tools.ant.types.FilterSet;
import org.apache.tools.ant.types.FilterSetCollection;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceFactory;
import org.apache.tools.ant.types.resources.FileResource;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.JavaEnvUtils;
import org.apache.tools.ant.util.VectorSet;

public class Project
implements ResourceFactory {
    public static final int MSG_ERR = 0;
    public static final int MSG_WARN = 1;
    public static final int MSG_INFO = 2;
    public static final int MSG_VERBOSE = 3;
    public static final int MSG_DEBUG = 4;
    private static final String VISITING = "VISITING";
    private static final String VISITED = "VISITED";
    @Deprecated
    public static final String JAVA_1_0 = "1.0";
    @Deprecated
    public static final String JAVA_1_1 = "1.1";
    @Deprecated
    public static final String JAVA_1_2 = "1.2";
    @Deprecated
    public static final String JAVA_1_3 = "1.3";
    @Deprecated
    public static final String JAVA_1_4 = "1.4";
    public static final String TOKEN_START = "@";
    public static final String TOKEN_END = "@";
    private static final FileUtils FILE_UTILS = FileUtils.getFileUtils();
    private String name;
    private String description;
    private final Object referencesLock = new Object();
    private final Hashtable<String, Object> references = new AntRefTable();
    private final HashMap<String, Object> idReferences = new HashMap();
    private String defaultTarget;
    private final Hashtable<String, Target> targets = new Hashtable();
    private final FilterSet globalFilterSet = new FilterSet();
    private final FilterSetCollection globalFilters;
    private File baseDir;
    private final Object listenersLock;
    private volatile BuildListener[] listeners;
    private final ThreadLocal<Boolean> isLoggingMessage;
    private ClassLoader coreLoader;
    private final Map<Thread, Task> threadTasks;
    private final Map<ThreadGroup, Task> threadGroupTasks;
    private InputHandler inputHandler;
    private InputStream defaultInputStream;
    private boolean keepGoingMode;

    public void setInputHandler(InputHandler handler) {
        this.inputHandler = handler;
    }

    public void setDefaultInputStream(InputStream defaultInputStream) {
        this.defaultInputStream = defaultInputStream;
    }

    public InputStream getDefaultInputStream() {
        return this.defaultInputStream;
    }

    public InputHandler getInputHandler() {
        return this.inputHandler;
    }

    public Project() {
        this.globalFilterSet.setProject(this);
        this.globalFilters = new FilterSetCollection(this.globalFilterSet);
        this.listenersLock = new Object();
        this.listeners = new BuildListener[0];
        this.isLoggingMessage = ThreadLocal.withInitial(() -> Boolean.FALSE);
        this.coreLoader = null;
        this.threadTasks = Collections.synchronizedMap(new WeakHashMap());
        this.threadGroupTasks = Collections.synchronizedMap(new WeakHashMap());
        this.inputHandler = null;
        this.defaultInputStream = null;
        this.keepGoingMode = false;
        this.inputHandler = new DefaultInputHandler();
    }

    public Project createSubProject() {
        Project subProject = null;
        try {
            subProject = (Project)this.getClass().getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (Exception e) {
            subProject = new Project();
        }
        this.initSubProject(subProject);
        return subProject;
    }

    public void initSubProject(Project subProject) {
        ComponentHelper.getComponentHelper(subProject).initSubProject(ComponentHelper.getComponentHelper(this));
        subProject.setDefaultInputStream(this.getDefaultInputStream());
        subProject.setKeepGoingMode(this.isKeepGoingMode());
        subProject.setExecutor(this.getExecutor().getSubProjectExecutor());
    }

    public void init() throws BuildException {
        this.initProperties();
        ComponentHelper.getComponentHelper(this).initDefaultDefinitions();
    }

    public void initProperties() throws BuildException {
        this.setJavaVersionProperty();
        this.setSystemProperties();
        this.setPropertyInternal("ant.version", Main.getAntVersion());
        this.setAntLib();
    }

    private void setAntLib() {
        File antlib = Locator.getClassSource(Project.class);
        if (antlib != null) {
            this.setPropertyInternal("ant.core.lib", antlib.getAbsolutePath());
        }
    }

    public AntClassLoader createClassLoader(Path path) {
        return this.createClassLoader(Optional.ofNullable(this.getCoreLoader()).orElse(this.getClass().getClassLoader()), path);
    }

    public AntClassLoader createClassLoader(ClassLoader parent, Path path) {
        return AntClassLoader.newAntClassLoader(parent, this, path, true);
    }

    public void setCoreLoader(ClassLoader coreLoader) {
        this.coreLoader = coreLoader;
    }

    public ClassLoader getCoreLoader() {
        return this.coreLoader;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addBuildListener(BuildListener listener) {
        Object object = this.listenersLock;
        synchronized (object) {
            for (BuildListener buildListener : this.listeners) {
                if (buildListener != listener) continue;
                return;
            }
            BuildListener[] newListeners = new BuildListener[this.listeners.length + 1];
            System.arraycopy(this.listeners, 0, newListeners, 0, this.listeners.length);
            newListeners[this.listeners.length] = listener;
            this.listeners = newListeners;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeBuildListener(BuildListener listener) {
        Object object = this.listenersLock;
        synchronized (object) {
            for (int i = 0; i < this.listeners.length; ++i) {
                if (this.listeners[i] != listener) continue;
                BuildListener[] newListeners = new BuildListener[this.listeners.length - 1];
                System.arraycopy(this.listeners, 0, newListeners, 0, i);
                System.arraycopy(this.listeners, i + 1, newListeners, i, this.listeners.length - i - 1);
                this.listeners = newListeners;
                break;
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Vector<BuildListener> getBuildListeners() {
        Object object = this.listenersLock;
        synchronized (object) {
            Vector<BuildListener> r = new Vector<BuildListener>(this.listeners.length);
            Collections.addAll(r, this.listeners);
            return r;
        }
    }

    public void log(String message) {
        this.log(message, 2);
    }

    public void log(String message, int msgLevel) {
        this.log(message, null, msgLevel);
    }

    public void log(String message, Throwable throwable, int msgLevel) {
        this.fireMessageLogged(this, message, throwable, msgLevel);
    }

    public void log(Task task, String message, int msgLevel) {
        this.fireMessageLogged(task, message, null, msgLevel);
    }

    public void log(Task task, String message, Throwable throwable, int msgLevel) {
        this.fireMessageLogged(task, message, throwable, msgLevel);
    }

    public void log(Target target, String message, int msgLevel) {
        this.log(target, message, null, msgLevel);
    }

    public void log(Target target, String message, Throwable throwable, int msgLevel) {
        this.fireMessageLogged(target, message, throwable, msgLevel);
    }

    public FilterSet getGlobalFilterSet() {
        return this.globalFilterSet;
    }

    public void setProperty(String name, String value) {
        PropertyHelper.getPropertyHelper(this).setProperty(name, value, true);
    }

    public void setNewProperty(String name, String value) {
        PropertyHelper.getPropertyHelper(this).setNewProperty(name, value);
    }

    public void setUserProperty(String name, String value) {
        PropertyHelper.getPropertyHelper(this).setUserProperty(name, value);
    }

    public void setInheritedProperty(String name, String value) {
        PropertyHelper.getPropertyHelper(this).setInheritedProperty(name, value);
    }

    private void setPropertyInternal(String name, String value) {
        PropertyHelper.getPropertyHelper(this).setProperty(name, value, false);
    }

    public String getProperty(String propertyName) {
        Object value = PropertyHelper.getPropertyHelper(this).getProperty(propertyName);
        return value == null ? null : String.valueOf(value);
    }

    public String replaceProperties(String value) throws BuildException {
        return PropertyHelper.getPropertyHelper(this).replaceProperties(null, value, null);
    }

    public String getUserProperty(String propertyName) {
        return (String)PropertyHelper.getPropertyHelper(this).getUserProperty(propertyName);
    }

    public Hashtable<String, Object> getProperties() {
        return PropertyHelper.getPropertyHelper(this).getProperties();
    }

    public Set<String> getPropertyNames() {
        return PropertyHelper.getPropertyHelper(this).getPropertyNames();
    }

    public Hashtable<String, Object> getUserProperties() {
        return PropertyHelper.getPropertyHelper(this).getUserProperties();
    }

    public Hashtable<String, Object> getInheritedProperties() {
        return PropertyHelper.getPropertyHelper(this).getInheritedProperties();
    }

    public void copyUserProperties(Project other) {
        PropertyHelper.getPropertyHelper(this).copyUserProperties(other);
    }

    public void copyInheritedProperties(Project other) {
        PropertyHelper.getPropertyHelper(this).copyInheritedProperties(other);
    }

    @Deprecated
    public void setDefaultTarget(String defaultTarget) {
        this.setDefault(defaultTarget);
    }

    public String getDefaultTarget() {
        return this.defaultTarget;
    }

    public void setDefault(String defaultTarget) {
        if (defaultTarget != null) {
            this.setUserProperty("ant.project.default-target", defaultTarget);
        }
        this.defaultTarget = defaultTarget;
    }

    public void setName(String name) {
        this.setUserProperty("ant.project.name", name);
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        if (this.description == null) {
            this.description = Description.getDescription(this);
        }
        return this.description;
    }

    @Deprecated
    public void addFilter(String token, String value) {
        if (token == null) {
            return;
        }
        this.globalFilterSet.addFilter(new FilterSet.Filter(token, value));
    }

    @Deprecated
    public Hashtable<String, String> getFilters() {
        return this.globalFilterSet.getFilterHash();
    }

    public void setBasedir(String baseD) throws BuildException {
        this.setBaseDir(new File(baseD));
    }

    public void setBaseDir(File baseDir) throws BuildException {
        if (!(baseDir = FILE_UTILS.normalize(baseDir.getAbsolutePath())).exists()) {
            throw new BuildException("Basedir " + baseDir.getAbsolutePath() + " does not exist");
        }
        if (!baseDir.isDirectory()) {
            throw new BuildException("Basedir " + baseDir.getAbsolutePath() + " is not a directory");
        }
        this.baseDir = baseDir;
        this.setPropertyInternal("basedir", this.baseDir.getPath());
        String msg = "Project base dir set to: " + this.baseDir;
        this.log(msg, 3);
    }

    public File getBaseDir() {
        if (this.baseDir == null) {
            try {
                this.setBasedir(".");
            }
            catch (BuildException ex) {
                ex.printStackTrace();
            }
        }
        return this.baseDir;
    }

    public void setKeepGoingMode(boolean keepGoingMode) {
        this.keepGoingMode = keepGoingMode;
    }

    public boolean isKeepGoingMode() {
        return this.keepGoingMode;
    }

    @Deprecated
    public static String getJavaVersion() {
        return JavaEnvUtils.getJavaVersion();
    }

    public void setJavaVersionProperty() throws BuildException {
        String javaVersion = JavaEnvUtils.getJavaVersion();
        this.setPropertyInternal("ant.java.version", javaVersion);
        if (!JavaEnvUtils.isAtLeastJavaVersion("1.8")) {
            throw new BuildException("Ant cannot work on Java prior to 1.8");
        }
        this.log("Detected Java version: " + javaVersion + " in: " + System.getProperty("java.home"), 3);
        this.log("Detected OS: " + System.getProperty("os.name"), 3);
    }

    public void setSystemProperties() {
        Properties systemP = System.getProperties();
        for (String propertyName : systemP.stringPropertyNames()) {
            String value = systemP.getProperty(propertyName);
            if (value == null) continue;
            this.setPropertyInternal(propertyName, value);
        }
    }

    public void addTaskDefinition(String taskName, Class<?> taskClass) throws BuildException {
        ComponentHelper.getComponentHelper(this).addTaskDefinition(taskName, taskClass);
    }

    public void checkTaskClass(Class<?> taskClass) throws BuildException {
        ComponentHelper.getComponentHelper(this).checkTaskClass(taskClass);
        if (!Modifier.isPublic(taskClass.getModifiers())) {
            String message = taskClass + " is not public";
            this.log(message, 0);
            throw new BuildException(message);
        }
        if (Modifier.isAbstract(taskClass.getModifiers())) {
            String message = taskClass + " is abstract";
            this.log(message, 0);
            throw new BuildException(message);
        }
        try {
            taskClass.getConstructor(new Class[0]);
        }
        catch (NoSuchMethodException e) {
            String message = "No public no-arg constructor in " + taskClass;
            this.log(message, 0);
            throw new BuildException(message);
        }
        catch (LinkageError e) {
            String message = "Could not load " + taskClass + ": " + e;
            this.log(message, 0);
            throw new BuildException(message, e);
        }
        if (!Task.class.isAssignableFrom(taskClass)) {
            TaskAdapter.checkTaskClass(taskClass, this);
        }
    }

    public Hashtable<String, Class<?>> getTaskDefinitions() {
        return ComponentHelper.getComponentHelper(this).getTaskDefinitions();
    }

    public Map<String, Class<?>> getCopyOfTaskDefinitions() {
        return new HashMap(this.getTaskDefinitions());
    }

    public void addDataTypeDefinition(String typeName, Class<?> typeClass) {
        ComponentHelper.getComponentHelper(this).addDataTypeDefinition(typeName, typeClass);
    }

    public Hashtable<String, Class<?>> getDataTypeDefinitions() {
        return ComponentHelper.getComponentHelper(this).getDataTypeDefinitions();
    }

    public Map<String, Class<?>> getCopyOfDataTypeDefinitions() {
        return new HashMap(this.getDataTypeDefinitions());
    }

    public void addTarget(Target target) throws BuildException {
        this.addTarget(target.getName(), target);
    }

    public void addTarget(String targetName, Target target) throws BuildException {
        if (this.targets.get(targetName) != null) {
            throw new BuildException("Duplicate target: `" + targetName + "'");
        }
        this.addOrReplaceTarget(targetName, target);
    }

    public void addOrReplaceTarget(Target target) {
        this.addOrReplaceTarget(target.getName(), target);
    }

    public void addOrReplaceTarget(String targetName, Target target) {
        String msg = " +Target: " + targetName;
        this.log(msg, 4);
        target.setProject(this);
        this.targets.put(targetName, target);
    }

    public Hashtable<String, Target> getTargets() {
        return this.targets;
    }

    public Map<String, Target> getCopyOfTargets() {
        return new HashMap<String, Target>(this.targets);
    }

    public Task createTask(String taskType) throws BuildException {
        return ComponentHelper.getComponentHelper(this).createTask(taskType);
    }

    public Object createDataType(String typeName) throws BuildException {
        return ComponentHelper.getComponentHelper(this).createDataType(typeName);
    }

    public void setExecutor(Executor e) {
        this.addReference("ant.executor", e);
    }

    public Executor getExecutor() {
        Object o = this.getReference("ant.executor");
        if (o == null) {
            String classname = this.getProperty("ant.executor.class");
            if (classname == null) {
                classname = DefaultExecutor.class.getName();
            }
            this.log("Attempting to create object of type " + classname, 4);
            try {
                o = Class.forName(classname, true, this.coreLoader).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (ClassNotFoundException seaEnEfEx) {
                try {
                    o = Class.forName(classname).getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
                }
                catch (Exception ex) {
                    this.log(ex.toString(), 0);
                }
            }
            catch (Exception ex) {
                this.log(ex.toString(), 0);
            }
            if (o == null) {
                throw new BuildException("Unable to obtain a Target Executor instance.");
            }
            this.setExecutor((Executor)o);
        }
        return (Executor)o;
    }

    public void executeTargets(Vector<String> names) throws BuildException {
        this.setUserProperty("ant.project.invoked-targets", String.join((CharSequence)",", names));
        this.getExecutor().executeTargets(this, names.toArray(new String[0]));
    }

    public void demuxOutput(String output, boolean isWarning) {
        Task task = this.getThreadTask(Thread.currentThread());
        if (task == null) {
            this.log(output, isWarning ? 1 : 2);
        } else if (isWarning) {
            task.handleErrorOutput(output);
        } else {
            task.handleOutput(output);
        }
    }

    public int defaultInput(byte[] buffer, int offset, int length) throws IOException {
        if (this.defaultInputStream == null) {
            throw new EOFException("No input provided for project");
        }
        System.out.flush();
        return this.defaultInputStream.read(buffer, offset, length);
    }

    public int demuxInput(byte[] buffer, int offset, int length) throws IOException {
        Task task = this.getThreadTask(Thread.currentThread());
        if (task == null) {
            return this.defaultInput(buffer, offset, length);
        }
        return task.handleInput(buffer, offset, length);
    }

    public void demuxFlush(String output, boolean isError) {
        Task task = this.getThreadTask(Thread.currentThread());
        if (task == null) {
            this.fireMessageLogged(this, output, isError ? 0 : 2);
        } else if (isError) {
            task.handleErrorFlush(output);
        } else {
            task.handleFlush(output);
        }
    }

    public void executeTarget(String targetName) throws BuildException {
        if (targetName == null) {
            String msg = "No target specified";
            throw new BuildException("No target specified");
        }
        this.executeSortedTargets(this.topoSort(targetName, this.targets, false));
    }

    public void executeSortedTargets(Vector<Target> sortedTargets) throws BuildException {
        HashSet<String> succeededTargets = new HashSet<String>();
        BuildException buildException = null;
        for (Target curtarget : sortedTargets) {
            boolean canExecute = true;
            for (String dependencyName : Collections.list(curtarget.getDependencies())) {
                if (succeededTargets.contains(dependencyName)) continue;
                canExecute = false;
                this.log(curtarget, "Cannot execute '" + curtarget.getName() + "' - '" + dependencyName + "' failed or was not executed.", 0);
                break;
            }
            if (!canExecute) continue;
            Throwable thrownException = null;
            try {
                curtarget.performTasks();
                succeededTargets.add(curtarget.getName());
            }
            catch (RuntimeException ex) {
                if (!this.keepGoingMode) {
                    throw ex;
                }
                thrownException = ex;
            }
            catch (Throwable ex) {
                if (!this.keepGoingMode) {
                    throw new BuildException(ex);
                }
                thrownException = ex;
            }
            if (thrownException == null) continue;
            if (thrownException instanceof BuildException) {
                this.log(curtarget, "Target '" + curtarget.getName() + "' failed with message '" + thrownException.getMessage() + "'.", 0);
                if (buildException != null) continue;
                buildException = (BuildException)thrownException;
                continue;
            }
            this.log(curtarget, "Target '" + curtarget.getName() + "' failed with message '" + thrownException.getMessage() + "'.", 0);
            thrownException.printStackTrace(System.err);
            if (buildException != null) continue;
            buildException = new BuildException(thrownException);
        }
        if (buildException != null) {
            throw buildException;
        }
    }

    @Deprecated
    public File resolveFile(String fileName, File rootDir) {
        return FILE_UTILS.resolveFile(rootDir, fileName);
    }

    public File resolveFile(String fileName) {
        return FILE_UTILS.resolveFile(this.baseDir, fileName);
    }

    @Deprecated
    public static String translatePath(String toProcess) {
        return FileUtils.translatePath(toProcess);
    }

    @Deprecated
    public void copyFile(String sourceFile, String destFile) throws IOException {
        FILE_UTILS.copyFile(sourceFile, destFile);
    }

    @Deprecated
    public void copyFile(String sourceFile, String destFile, boolean filtering) throws IOException {
        FILE_UTILS.copyFile(sourceFile, destFile, filtering ? this.globalFilters : null);
    }

    @Deprecated
    public void copyFile(String sourceFile, String destFile, boolean filtering, boolean overwrite) throws IOException {
        FILE_UTILS.copyFile(sourceFile, destFile, filtering ? this.globalFilters : null, overwrite);
    }

    @Deprecated
    public void copyFile(String sourceFile, String destFile, boolean filtering, boolean overwrite, boolean preserveLastModified) throws IOException {
        FILE_UTILS.copyFile(sourceFile, destFile, filtering ? this.globalFilters : null, overwrite, preserveLastModified);
    }

    @Deprecated
    public void copyFile(File sourceFile, File destFile) throws IOException {
        FILE_UTILS.copyFile(sourceFile, destFile);
    }

    @Deprecated
    public void copyFile(File sourceFile, File destFile, boolean filtering) throws IOException {
        FILE_UTILS.copyFile(sourceFile, destFile, filtering ? this.globalFilters : null);
    }

    @Deprecated
    public void copyFile(File sourceFile, File destFile, boolean filtering, boolean overwrite) throws IOException {
        FILE_UTILS.copyFile(sourceFile, destFile, filtering ? this.globalFilters : null, overwrite);
    }

    @Deprecated
    public void copyFile(File sourceFile, File destFile, boolean filtering, boolean overwrite, boolean preserveLastModified) throws IOException {
        FILE_UTILS.copyFile(sourceFile, destFile, filtering ? this.globalFilters : null, overwrite, preserveLastModified);
    }

    @Deprecated
    public void setFileLastModified(File file, long time) throws BuildException {
        FILE_UTILS.setFileLastModified(file, time);
        this.log("Setting modification time for " + file, 3);
    }

    public static boolean toBoolean(String s) {
        return "on".equalsIgnoreCase(s) || "true".equalsIgnoreCase(s) || "yes".equalsIgnoreCase(s);
    }

    public static Project getProject(Object o) {
        if (o instanceof ProjectComponent) {
            return ((ProjectComponent)o).getProject();
        }
        try {
            Method m = o.getClass().getMethod("getProject", new Class[0]);
            if (Project.class.equals(m.getReturnType())) {
                return (Project)m.invoke(o, new Object[0]);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return null;
    }

    public final Vector<Target> topoSort(String root, Hashtable<String, Target> targetTable) throws BuildException {
        return this.topoSort(new String[]{root}, targetTable, true);
    }

    public final Vector<Target> topoSort(String root, Hashtable<String, Target> targetTable, boolean returnAll) throws BuildException {
        return this.topoSort(new String[]{root}, targetTable, returnAll);
    }

    public final Vector<Target> topoSort(String[] roots, Hashtable<String, Target> targetTable, boolean returnAll) throws BuildException {
        VectorSet<Target> ret = new VectorSet<Target>();
        Hashtable<String, String> state = new Hashtable<String, String>();
        Stack<String> visiting = new Stack<String>();
        for (String root2 : roots) {
            String st = (String)state.get(root2);
            if (st == null) {
                this.tsort(root2, targetTable, state, visiting, ret);
                continue;
            }
            if (st != VISITING) continue;
            throw new BuildException("Unexpected node in visiting state: " + root2);
        }
        this.log("Build sequence for target(s)" + Arrays.stream(roots).map(root -> String.format(" `%s'", root)).collect(Collectors.joining(",")) + " is " + ret, 3);
        Vector complete = returnAll ? ret : new Vector<Target>(ret);
        for (String curTarget : targetTable.keySet()) {
            String st = state.get(curTarget);
            if (st == null) {
                this.tsort(curTarget, targetTable, state, visiting, complete);
                continue;
            }
            if (st != VISITING) continue;
            throw new BuildException("Unexpected node in visiting state: " + curTarget);
        }
        this.log("Complete build sequence is " + complete, 3);
        return ret;
    }

    private void tsort(String root, Hashtable<String, Target> targetTable, Hashtable<String, String> state, Stack<String> visiting, Vector<Target> ret) throws BuildException {
        state.put(root, VISITING);
        visiting.push(root);
        Target target = targetTable.get(root);
        if (target == null) {
            StringBuilder sb = new StringBuilder("Target \"");
            sb.append(root);
            sb.append("\" does not exist in the project \"");
            sb.append(this.name);
            sb.append("\". ");
            visiting.pop();
            if (!visiting.empty()) {
                String parent = visiting.peek();
                sb.append("It is used from target \"");
                sb.append(parent);
                sb.append("\".");
            }
            throw new BuildException(new String(sb));
        }
        for (String cur : Collections.list(target.getDependencies())) {
            String m = state.get(cur);
            if (m == null) {
                this.tsort(cur, targetTable, state, visiting, ret);
                continue;
            }
            if (m != VISITING) continue;
            throw Project.makeCircularException(cur, visiting);
        }
        String p = visiting.pop();
        if (root != p) {
            throw new BuildException("Unexpected internal error: expected to pop " + root + " but got " + p);
        }
        state.put(root, VISITED);
        ret.addElement(target);
    }

    private static BuildException makeCircularException(String end, Stack<String> stk) {
        String c;
        StringBuilder sb = new StringBuilder("Circular dependency: ");
        sb.append(end);
        do {
            c = stk.pop();
            sb.append(" <- ");
            sb.append(c);
        } while (!c.equals(end));
        return new BuildException(sb.toString());
    }

    public void inheritIDReferences(Project parent) {
    }

    public void addIdReference(String id, Object value) {
        this.idReferences.put(id, value);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addReference(String referenceName, Object value) {
        Object object = this.referencesLock;
        synchronized (object) {
            Object old = ((AntRefTable)this.references).getReal(referenceName);
            if (old == value) {
                return;
            }
            if (old != null && !(old instanceof UnknownElement)) {
                this.log("Overriding previous definition of reference to " + referenceName, 3);
            }
            this.log("Adding reference: " + referenceName, 4);
            this.references.put(referenceName, value);
        }
    }

    public Hashtable<String, Object> getReferences() {
        return this.references;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean hasReference(String key) {
        Object object = this.referencesLock;
        synchronized (object) {
            return this.references.containsKey(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Map<String, Object> getCopyOfReferences() {
        Object object = this.referencesLock;
        synchronized (object) {
            return new HashMap<String, Object>(this.references);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public <T> T getReference(String key) {
        Object object = this.referencesLock;
        synchronized (object) {
            Object ret = this.references.get(key);
            if (ret != null) {
                return (T)ret;
            }
        }
        if (!key.equals("ant.PropertyHelper")) {
            try {
                if (PropertyHelper.getPropertyHelper(this).containsProperties(key)) {
                    this.log("Unresolvable reference " + key + " might be a misuse of property expansion syntax.", 1);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return null;
    }

    public String getElementName(Object element) {
        return ComponentHelper.getComponentHelper(this).getElementName(element);
    }

    public void fireBuildStarted() {
        BuildEvent event = new BuildEvent(this);
        for (BuildListener currListener : this.listeners) {
            currListener.buildStarted(event);
        }
    }

    public void fireBuildFinished(Throwable exception) {
        BuildEvent event = new BuildEvent(this);
        event.setException(exception);
        for (BuildListener currListener : this.listeners) {
            currListener.buildFinished(event);
        }
        IntrospectionHelper.clearCache();
    }

    public void fireSubBuildStarted() {
        BuildEvent event = new BuildEvent(this);
        for (BuildListener currListener : this.listeners) {
            if (!(currListener instanceof SubBuildListener)) continue;
            ((SubBuildListener)currListener).subBuildStarted(event);
        }
    }

    public void fireSubBuildFinished(Throwable exception) {
        BuildEvent event = new BuildEvent(this);
        event.setException(exception);
        for (BuildListener currListener : this.listeners) {
            if (!(currListener instanceof SubBuildListener)) continue;
            ((SubBuildListener)currListener).subBuildFinished(event);
        }
    }

    protected void fireTargetStarted(Target target) {
        BuildEvent event = new BuildEvent(target);
        for (BuildListener currListener : this.listeners) {
            currListener.targetStarted(event);
        }
    }

    protected void fireTargetFinished(Target target, Throwable exception) {
        BuildEvent event = new BuildEvent(target);
        event.setException(exception);
        for (BuildListener currListener : this.listeners) {
            currListener.targetFinished(event);
        }
    }

    protected void fireTaskStarted(Task task) {
        this.registerThreadTask(Thread.currentThread(), task);
        BuildEvent event = new BuildEvent(task);
        for (BuildListener currListener : this.listeners) {
            currListener.taskStarted(event);
        }
    }

    protected void fireTaskFinished(Task task, Throwable exception) {
        this.registerThreadTask(Thread.currentThread(), null);
        System.out.flush();
        System.err.flush();
        BuildEvent event = new BuildEvent(task);
        event.setException(exception);
        for (BuildListener currListener : this.listeners) {
            currListener.taskFinished(event);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void fireMessageLoggedEvent(BuildEvent event, String message, int priority) {
        if (message == null) {
            message = String.valueOf(message);
        }
        if (message.endsWith(System.lineSeparator())) {
            int endIndex = message.length() - System.lineSeparator().length();
            event.setMessage(message.substring(0, endIndex), priority);
        } else {
            event.setMessage(message, priority);
        }
        if (this.isLoggingMessage.get() != Boolean.FALSE) {
            return;
        }
        try {
            this.isLoggingMessage.set(Boolean.TRUE);
            for (BuildListener currListener : this.listeners) {
                currListener.messageLogged(event);
            }
        }
        finally {
            this.isLoggingMessage.set(Boolean.FALSE);
        }
    }

    protected void fireMessageLogged(Project project, String message, int priority) {
        this.fireMessageLogged(project, message, null, priority);
    }

    protected void fireMessageLogged(Project project, String message, Throwable throwable, int priority) {
        BuildEvent event = new BuildEvent(project);
        event.setException(throwable);
        this.fireMessageLoggedEvent(event, message, priority);
    }

    protected void fireMessageLogged(Target target, String message, int priority) {
        this.fireMessageLogged(target, message, null, priority);
    }

    protected void fireMessageLogged(Target target, String message, Throwable throwable, int priority) {
        BuildEvent event = new BuildEvent(target);
        event.setException(throwable);
        this.fireMessageLoggedEvent(event, message, priority);
    }

    protected void fireMessageLogged(Task task, String message, int priority) {
        this.fireMessageLogged(task, message, null, priority);
    }

    protected void fireMessageLogged(Task task, String message, Throwable throwable, int priority) {
        BuildEvent event = new BuildEvent(task);
        event.setException(throwable);
        this.fireMessageLoggedEvent(event, message, priority);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void registerThreadTask(Thread thread, Task task) {
        Map<Thread, Task> map = this.threadTasks;
        synchronized (map) {
            if (task != null) {
                this.threadTasks.put(thread, task);
                this.threadGroupTasks.put(thread.getThreadGroup(), task);
            } else {
                this.threadTasks.remove(thread);
                this.threadGroupTasks.remove(thread.getThreadGroup());
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Task getThreadTask(Thread thread) {
        Map<Thread, Task> map = this.threadTasks;
        synchronized (map) {
            Task task = this.threadTasks.get(thread);
            if (task == null) {
                for (ThreadGroup group = thread.getThreadGroup(); task == null && group != null; group = group.getParent()) {
                    task = this.threadGroupTasks.get(group);
                }
            }
            return task;
        }
    }

    public final void setProjectReference(Object obj) {
        if (obj instanceof ProjectComponent) {
            ((ProjectComponent)obj).setProject(this);
            return;
        }
        try {
            Method method = obj.getClass().getMethod("setProject", Project.class);
            if (method != null) {
                method.invoke(obj, this);
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    @Override
    public Resource getResource(String name) {
        return new FileResource(this.getBaseDir(), name);
    }

    private static class AntRefTable
    extends Hashtable<String, Object> {
        private static final long serialVersionUID = 1L;

        AntRefTable() {
        }

        private Object getReal(Object key) {
            return super.get(key);
        }

        @Override
        public Object get(Object key) {
            Object o = this.getReal(key);
            if (o instanceof UnknownElement) {
                UnknownElement ue = (UnknownElement)o;
                ue.maybeConfigure();
                o = ue.getRealThing();
            }
            return o;
        }
    }
}

