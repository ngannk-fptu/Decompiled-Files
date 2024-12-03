/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.launch.Launcher
 */
package org.apache.tools.ant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;
import org.apache.tools.ant.AntTypeDefinition;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DefaultDefinitions;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskAdapter;
import org.apache.tools.ant.UnknownElement;
import org.apache.tools.ant.launch.Launcher;
import org.apache.tools.ant.taskdefs.Definer;
import org.apache.tools.ant.taskdefs.Property;
import org.apache.tools.ant.taskdefs.Typedef;

public class ComponentHelper {
    private final Map<String, List<AntTypeDefinition>> restrictedDefinitions = new HashMap<String, List<AntTypeDefinition>>();
    private final Hashtable<String, AntTypeDefinition> antTypeTable = new Hashtable();
    private final Hashtable<String, Class<?>> taskClassDefinitions = new Hashtable();
    private boolean rebuildTaskClassDefinitions = true;
    private final Hashtable<String, Class<?>> typeClassDefinitions = new Hashtable();
    private boolean rebuildTypeClassDefinitions = true;
    private final HashSet<String> checkedNamespaces = new HashSet();
    private Stack<String> antLibStack = new Stack();
    private String antLibCurrentUri = null;
    private ComponentHelper next;
    private Project project;
    private static final String ERROR_NO_TASK_LIST_LOAD = "Can't load default task list";
    private static final String ERROR_NO_TYPE_LIST_LOAD = "Can't load default type list";
    public static final String COMPONENT_HELPER_REFERENCE = "ant.ComponentHelper";
    private static final String BUILD_SYSCLASSPATH_ONLY = "only";
    private static final String ANT_PROPERTY_TASK = "property";
    private static Properties[] defaultDefinitions = new Properties[2];

    public Project getProject() {
        return this.project;
    }

    public static ComponentHelper getComponentHelper(Project project) {
        if (project == null) {
            return null;
        }
        ComponentHelper ph = (ComponentHelper)project.getReference(COMPONENT_HELPER_REFERENCE);
        if (ph != null) {
            return ph;
        }
        ph = new ComponentHelper();
        ph.setProject(project);
        project.addReference(COMPONENT_HELPER_REFERENCE, ph);
        return ph;
    }

    protected ComponentHelper() {
    }

    public void setNext(ComponentHelper next) {
        this.next = next;
    }

    public ComponentHelper getNext() {
        return this.next;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    private synchronized Set<String> getCheckedNamespace() {
        Set result = (Set)this.checkedNamespaces.clone();
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Map<String, List<AntTypeDefinition>> getRestrictedDefinition() {
        HashMap<String, List<AntTypeDefinition>> result = new HashMap<String, List<AntTypeDefinition>>();
        Map<String, List<AntTypeDefinition>> map = this.restrictedDefinitions;
        synchronized (map) {
            for (Map.Entry<String, List<AntTypeDefinition>> entry : this.restrictedDefinitions.entrySet()) {
                List<AntTypeDefinition> entryVal;
                List<AntTypeDefinition> list = entryVal = entry.getValue();
                synchronized (list) {
                    entryVal = new ArrayList<AntTypeDefinition>(entryVal);
                }
                result.put(entry.getKey(), entryVal);
            }
        }
        return result;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void initSubProject(ComponentHelper helper) {
        Hashtable typeTable = (Hashtable)helper.antTypeTable.clone();
        Hashtable<String, AntTypeDefinition> hashtable = this.antTypeTable;
        synchronized (hashtable) {
            for (AntTypeDefinition def : typeTable.values()) {
                this.antTypeTable.put(def.getName(), def);
            }
        }
        Set<String> inheritedCheckedNamespace = helper.getCheckedNamespace();
        ComponentHelper componentHelper = this;
        synchronized (componentHelper) {
            this.checkedNamespaces.addAll(inheritedCheckedNamespace);
        }
        Map<String, List<AntTypeDefinition>> inheritedRestrictedDef = helper.getRestrictedDefinition();
        Map<String, List<AntTypeDefinition>> map = this.restrictedDefinitions;
        synchronized (map) {
            this.restrictedDefinitions.putAll(inheritedRestrictedDef);
        }
    }

    public Object createComponent(UnknownElement ue, String ns, String componentType) throws BuildException {
        Object component = this.createComponent(componentType);
        if (component instanceof Task) {
            Task task = (Task)component;
            task.setLocation(ue.getLocation());
            task.setTaskType(componentType);
            task.setTaskName(ue.getTaskName());
            task.setOwningTarget(ue.getOwningTarget());
            task.init();
        }
        return component;
    }

    public Object createComponent(String componentName) {
        AntTypeDefinition def = this.getDefinition(componentName);
        return def == null ? null : def.create(this.project);
    }

    public Class<?> getComponentClass(String componentName) {
        AntTypeDefinition def = this.getDefinition(componentName);
        return def == null ? null : def.getExposedClass(this.project);
    }

    public AntTypeDefinition getDefinition(String componentName) {
        this.checkNamespace(componentName);
        return this.antTypeTable.get(componentName);
    }

    public void initDefaultDefinitions() {
        this.initTasks();
        this.initTypes();
        new DefaultDefinitions(this).execute();
    }

    public void addTaskDefinition(String taskName, Class<?> taskClass) {
        this.checkTaskClass(taskClass);
        AntTypeDefinition def = new AntTypeDefinition();
        def.setName(taskName);
        def.setClassLoader(taskClass.getClassLoader());
        def.setClass(taskClass);
        def.setAdapterClass(TaskAdapter.class);
        def.setClassName(taskClass.getName());
        def.setAdaptToClass(Task.class);
        this.updateDataTypeDefinition(def);
    }

    public void checkTaskClass(Class<?> taskClass) throws BuildException {
        if (!Modifier.isPublic(taskClass.getModifiers())) {
            String message = taskClass + " is not public";
            this.project.log(message, 0);
            throw new BuildException(message);
        }
        if (Modifier.isAbstract(taskClass.getModifiers())) {
            String message = taskClass + " is abstract";
            this.project.log(message, 0);
            throw new BuildException(message);
        }
        try {
            taskClass.getConstructor(null);
        }
        catch (NoSuchMethodException e) {
            String message = "No public no-arg constructor in " + taskClass;
            this.project.log(message, 0);
            throw new BuildException(message);
        }
        if (!Task.class.isAssignableFrom(taskClass)) {
            TaskAdapter.checkTaskClass(taskClass, this.project);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Hashtable<String, Class<?>> getTaskDefinitions() {
        Hashtable<String, Class<?>> hashtable = this.taskClassDefinitions;
        synchronized (hashtable) {
            Hashtable<String, AntTypeDefinition> hashtable2 = this.antTypeTable;
            synchronized (hashtable2) {
                if (this.rebuildTaskClassDefinitions) {
                    this.taskClassDefinitions.clear();
                    this.antTypeTable.entrySet().stream().filter(e -> ((AntTypeDefinition)e.getValue()).getExposedClass(this.project) != null && Task.class.isAssignableFrom(((AntTypeDefinition)e.getValue()).getExposedClass(this.project))).forEach(e -> this.taskClassDefinitions.put((String)e.getKey(), ((AntTypeDefinition)e.getValue()).getTypeClass(this.project)));
                    this.rebuildTaskClassDefinitions = false;
                }
            }
        }
        return this.taskClassDefinitions;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Hashtable<String, Class<?>> getDataTypeDefinitions() {
        Hashtable<String, Class<?>> hashtable = this.typeClassDefinitions;
        synchronized (hashtable) {
            Hashtable<String, AntTypeDefinition> hashtable2 = this.antTypeTable;
            synchronized (hashtable2) {
                if (this.rebuildTypeClassDefinitions) {
                    this.typeClassDefinitions.clear();
                    this.antTypeTable.entrySet().stream().filter(e -> ((AntTypeDefinition)e.getValue()).getExposedClass(this.project) != null && !Task.class.isAssignableFrom(((AntTypeDefinition)e.getValue()).getExposedClass(this.project))).forEach(e -> this.typeClassDefinitions.put((String)e.getKey(), ((AntTypeDefinition)e.getValue()).getTypeClass(this.project)));
                    this.rebuildTypeClassDefinitions = false;
                }
            }
        }
        return this.typeClassDefinitions;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<AntTypeDefinition> getRestrictedDefinitions(String componentName) {
        Map<String, List<AntTypeDefinition>> map = this.restrictedDefinitions;
        synchronized (map) {
            return this.restrictedDefinitions.get(componentName);
        }
    }

    public void addDataTypeDefinition(String typeName, Class<?> typeClass) {
        AntTypeDefinition def = new AntTypeDefinition();
        def.setName(typeName);
        def.setClass(typeClass);
        this.updateDataTypeDefinition(def);
        this.project.log(" +User datatype: " + typeName + "     " + typeClass.getName(), 4);
    }

    public void addDataTypeDefinition(AntTypeDefinition def) {
        if (!def.isRestrict()) {
            this.updateDataTypeDefinition(def);
        } else {
            this.updateRestrictedDefinition(def);
        }
    }

    public Hashtable<String, AntTypeDefinition> getAntTypeTable() {
        return this.antTypeTable;
    }

    public Task createTask(String taskType) throws BuildException {
        Task task = this.createNewTask(taskType);
        if (task == null && taskType.equals(ANT_PROPERTY_TASK)) {
            this.addTaskDefinition(ANT_PROPERTY_TASK, Property.class);
            task = this.createNewTask(taskType);
        }
        return task;
    }

    private Task createNewTask(String taskType) throws BuildException {
        Class<?> c = this.getComponentClass(taskType);
        if (c == null || !Task.class.isAssignableFrom(c)) {
            return null;
        }
        Object obj = this.createComponent(taskType);
        if (obj == null) {
            return null;
        }
        if (!(obj instanceof Task)) {
            throw new BuildException("Expected a Task from '" + taskType + "' but got an instance of " + obj.getClass().getName() + " instead");
        }
        Task task = (Task)obj;
        task.setTaskType(taskType);
        task.setTaskName(taskType);
        this.project.log("   +Task: " + taskType, 4);
        return task;
    }

    public Object createDataType(String typeName) throws BuildException {
        return this.createComponent(typeName);
    }

    public String getElementName(Object element) {
        return this.getElementName(element, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String getElementName(Object o, boolean brief) {
        Class<?> elementClass = o.getClass();
        String elementClassname = elementClass.getName();
        Hashtable<String, AntTypeDefinition> hashtable = this.antTypeTable;
        synchronized (hashtable) {
            for (AntTypeDefinition def : this.antTypeTable.values()) {
                if (!elementClassname.equals(def.getClassName()) || elementClass != def.getExposedClass(this.project)) continue;
                String name = def.getName();
                return brief ? name : "The <" + name + "> type";
            }
        }
        return ComponentHelper.getUnmappedElementName(o.getClass(), brief);
    }

    public static String getElementName(Project p, Object o, boolean brief) {
        if (p == null) {
            p = Project.getProject(o);
        }
        return p == null ? ComponentHelper.getUnmappedElementName(o.getClass(), brief) : ComponentHelper.getComponentHelper(p).getElementName(o, brief);
    }

    private static String getUnmappedElementName(Class<?> c, boolean brief) {
        if (brief) {
            String name = c.getName();
            return name.substring(name.lastIndexOf(46) + 1);
        }
        return c.toString();
    }

    private boolean validDefinition(AntTypeDefinition def) {
        return def.getTypeClass(this.project) != null && def.getExposedClass(this.project) != null;
    }

    private boolean sameDefinition(AntTypeDefinition def, AntTypeDefinition old) {
        boolean defValid = this.validDefinition(def);
        boolean sameValidity = defValid == this.validDefinition(old);
        return sameValidity && (!defValid || def.sameDefinition(old, this.project));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateRestrictedDefinition(AntTypeDefinition def) {
        String name = def.getName();
        List list = null;
        Object object = this.restrictedDefinitions;
        synchronized (object) {
            list = this.restrictedDefinitions.computeIfAbsent(name, k -> new ArrayList());
        }
        object = list;
        synchronized (object) {
            Iterator i = list.iterator();
            while (i.hasNext()) {
                AntTypeDefinition current = (AntTypeDefinition)i.next();
                if (!current.getClassName().equals(def.getClassName())) continue;
                i.remove();
                break;
            }
            list.add(def);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void updateDataTypeDefinition(AntTypeDefinition def) {
        String name = def.getName();
        Hashtable<String, AntTypeDefinition> hashtable = this.antTypeTable;
        synchronized (hashtable) {
            this.rebuildTaskClassDefinitions = true;
            this.rebuildTypeClassDefinitions = true;
            AntTypeDefinition old = this.antTypeTable.get(name);
            if (old != null) {
                if (this.sameDefinition(def, old)) {
                    return;
                }
                Class<?> oldClass = old.getExposedClass(this.project);
                boolean isTask = oldClass != null && Task.class.isAssignableFrom(oldClass);
                this.project.log("Trying to override old definition of " + (isTask ? "task " : "datatype ") + name, def.similarDefinition(old, this.project) ? 3 : 1);
            }
            this.project.log(" +Datatype " + name + " " + def.getClassName(), 4);
            this.antTypeTable.put(name, def);
        }
    }

    public void enterAntLib(String uri) {
        this.antLibCurrentUri = uri;
        this.antLibStack.push(uri);
    }

    public String getCurrentAntlibUri() {
        return this.antLibCurrentUri;
    }

    public void exitAntLib() {
        this.antLibStack.pop();
        this.antLibCurrentUri = this.antLibStack.isEmpty() ? null : this.antLibStack.peek();
    }

    private void initTasks() {
        ClassLoader classLoader = this.getClassLoader(null);
        Properties props = ComponentHelper.getDefaultDefinitions(false);
        for (String name : props.stringPropertyNames()) {
            AntTypeDefinition def = new AntTypeDefinition();
            def.setName(name);
            def.setClassName(props.getProperty(name));
            def.setClassLoader(classLoader);
            def.setAdaptToClass(Task.class);
            def.setAdapterClass(TaskAdapter.class);
            this.antTypeTable.put(name, def);
        }
    }

    private ClassLoader getClassLoader(ClassLoader classLoader) {
        String buildSysclasspath = this.project.getProperty("build.sysclasspath");
        if (this.project.getCoreLoader() != null && !BUILD_SYSCLASSPATH_ONLY.equals(buildSysclasspath)) {
            classLoader = this.project.getCoreLoader();
        }
        return classLoader;
    }

    private static synchronized Properties getDefaultDefinitions(boolean type) throws BuildException {
        int idx;
        int n = idx = type ? 1 : 0;
        if (defaultDefinitions[idx] == null) {
            String resource = type ? "/org/apache/tools/ant/types/defaults.properties" : "/org/apache/tools/ant/taskdefs/defaults.properties";
            String errorString = type ? ERROR_NO_TYPE_LIST_LOAD : ERROR_NO_TASK_LIST_LOAD;
            try (InputStream in = ComponentHelper.class.getResourceAsStream(resource);){
                if (in == null) {
                    throw new BuildException(errorString);
                }
                Properties p = new Properties();
                p.load(in);
                ComponentHelper.defaultDefinitions[idx] = p;
            }
            catch (IOException e) {
                throw new BuildException(errorString, e);
            }
        }
        return defaultDefinitions[idx];
    }

    private void initTypes() {
        ClassLoader classLoader = this.getClassLoader(null);
        Properties props = ComponentHelper.getDefaultDefinitions(true);
        for (String name : props.stringPropertyNames()) {
            AntTypeDefinition def = new AntTypeDefinition();
            def.setName(name);
            def.setClassName(props.getProperty(name));
            def.setClassLoader(classLoader);
            this.antTypeTable.put(name, def);
        }
    }

    private synchronized void checkNamespace(String componentName) {
        String uri = ProjectHelper.extractUriFromComponentName(componentName);
        if (uri.isEmpty()) {
            uri = "antlib:org.apache.tools.ant";
        }
        if (!uri.startsWith("antlib:")) {
            return;
        }
        if (this.checkedNamespaces.contains(uri)) {
            return;
        }
        this.checkedNamespaces.add(uri);
        if (this.antTypeTable.isEmpty()) {
            this.initDefaultDefinitions();
        }
        Typedef definer = new Typedef();
        definer.setProject(this.project);
        definer.init();
        definer.setURI(uri);
        definer.setTaskName(uri);
        definer.setResource(Definer.makeResourceFromURI(uri));
        definer.setOnError(new Definer.OnError("ignore"));
        definer.execute();
    }

    public String diagnoseCreationFailure(String componentName, String type) {
        String antHomeLib;
        StringWriter errorText = new StringWriter();
        PrintWriter out = new PrintWriter(errorText);
        out.println("Problem: failed to create " + type + " " + componentName);
        boolean lowlevel = false;
        boolean jars = false;
        boolean definitions = false;
        String home = System.getProperty("user.home");
        File libDir = new File(home, Launcher.USER_LIBDIR);
        boolean probablyIDE = false;
        String anthome = System.getProperty("ant.home");
        if (anthome != null) {
            File antHomeLibDir = new File(anthome, "lib");
            antHomeLib = antHomeLibDir.getAbsolutePath();
        } else {
            probablyIDE = true;
            antHomeLib = "ANT_HOME" + File.separatorChar + "lib";
        }
        StringBuilder dirListingText = new StringBuilder();
        String tab = "        -";
        dirListingText.append("        -");
        dirListingText.append(antHomeLib);
        dirListingText.append('\n');
        if (probablyIDE) {
            dirListingText.append("        -");
            dirListingText.append("the IDE Ant configuration dialogs");
        } else {
            dirListingText.append("        -");
            dirListingText.append(libDir);
            dirListingText.append('\n');
            dirListingText.append("        -");
            dirListingText.append("a directory added on the command line with the -lib argument");
        }
        String dirListing = dirListingText.toString();
        AntTypeDefinition def = this.getDefinition(componentName);
        if (def == null) {
            this.printUnknownDefinition(out, componentName, dirListing);
            definitions = true;
        } else {
            String classname = def.getClassName();
            boolean antTask = classname.startsWith("org.apache.tools.ant.");
            boolean optional = classname.startsWith("org.apache.tools.ant.types.optional") || classname.startsWith("org.apache.tools.ant.taskdefs.optional");
            Class<?> clazz = null;
            try {
                clazz = def.innerGetTypeClass();
            }
            catch (ClassNotFoundException e) {
                jars = true;
                if (!optional) {
                    definitions = true;
                }
                this.printClassNotFound(out, classname, optional, dirListing);
            }
            catch (NoClassDefFoundError ncdfe) {
                jars = true;
                this.printNotLoadDependentClass(out, optional, ncdfe, dirListing);
            }
            if (clazz != null) {
                try {
                    def.innerCreateAndSet(clazz, this.project);
                    out.println("The component could be instantiated.");
                }
                catch (NoSuchMethodException e) {
                    lowlevel = true;
                    out.println("Cause: The class " + classname + " has no compatible constructor.");
                }
                catch (InstantiationException e) {
                    lowlevel = true;
                    out.println("Cause: The class " + classname + " is abstract and cannot be instantiated.");
                }
                catch (IllegalAccessException e) {
                    lowlevel = true;
                    out.println("Cause: The constructor for " + classname + " is private and cannot be invoked.");
                }
                catch (InvocationTargetException ex) {
                    lowlevel = true;
                    Throwable t = ex.getTargetException();
                    out.println("Cause: The constructor threw the exception");
                    out.println(t.toString());
                    t.printStackTrace(out);
                }
                catch (NoClassDefFoundError ncdfe) {
                    jars = true;
                    out.println("Cause:  A class needed by class " + classname + " cannot be found: ");
                    out.println("       " + ncdfe.getMessage());
                    out.println("Action: Determine what extra JAR files are needed, and place them in:");
                    out.println(dirListing);
                }
            }
            out.println();
            out.println("Do not panic, this is a common problem.");
            if (definitions) {
                out.println("It may just be a typographical error in the build file or the task/type declaration.");
            }
            if (jars) {
                out.println("The commonest cause is a missing JAR.");
            }
            if (lowlevel) {
                out.println("This is quite a low level problem, which may need consultation with the author of the task.");
                if (antTask) {
                    out.println("This may be the Ant team. Please file a defect or contact the developer team.");
                } else {
                    out.println("This does not appear to be a task bundled with Ant.");
                    out.println("Please take it up with the supplier of the third-party " + type + ".");
                    out.println("If you have written it yourself, you probably have a bug to fix.");
                }
            } else {
                out.println();
                out.println("This is not a bug; it is a configuration problem");
            }
        }
        out.flush();
        out.close();
        return errorText.toString();
    }

    private void printUnknownDefinition(PrintWriter out, String componentName, String dirListing) {
        boolean isAntlib = componentName.startsWith("antlib:");
        String uri = ProjectHelper.extractUriFromComponentName(componentName);
        out.println("Cause: The name is undefined.");
        out.println("Action: Check the spelling.");
        out.println("Action: Check that any custom tasks/types have been declared.");
        out.println("Action: Check that any <presetdef>/<macrodef> declarations have taken place.");
        if (!uri.isEmpty()) {
            List<AntTypeDefinition> matches = this.findTypeMatches(uri);
            if (matches.isEmpty()) {
                out.println("No types or tasks have been defined in this namespace yet");
                if (isAntlib) {
                    out.println();
                    out.println("This appears to be an antlib declaration. ");
                    out.println("Action: Check that the implementing library exists in one of:");
                    out.println(dirListing);
                }
            } else {
                out.println();
                out.println("The definitions in the namespace " + uri + " are:");
                for (AntTypeDefinition def : matches) {
                    String local = ProjectHelper.extractNameFromComponentName(def.getName());
                    out.println("    " + local);
                }
            }
        }
    }

    private void printClassNotFound(PrintWriter out, String classname, boolean optional, String dirListing) {
        out.println("Cause: the class " + classname + " was not found.");
        if (optional) {
            out.println("        This looks like one of Ant's optional components.");
            out.println("Action: Check that the appropriate optional JAR exists in");
            out.println(dirListing);
        } else {
            out.println("Action: Check that the component has been correctly declared");
            out.println("        and that the implementing JAR is in one of:");
            out.println(dirListing);
        }
    }

    private void printNotLoadDependentClass(PrintWriter out, boolean optional, NoClassDefFoundError ncdfe, String dirListing) {
        out.println("Cause: Could not load a dependent class " + ncdfe.getMessage());
        if (optional) {
            out.println("       It is not enough to have Ant's optional JARs");
            out.println("       you need the JAR files that the optional tasks depend upon.");
            out.println("       Ant's optional task dependencies are listed in the manual.");
        } else {
            out.println("       This class may be in a separate JAR that is not installed.");
        }
        out.println("Action: Determine what extra JAR files are needed, and place them in one of:");
        out.println(dirListing);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private List<AntTypeDefinition> findTypeMatches(String prefix) {
        Hashtable<String, AntTypeDefinition> hashtable = this.antTypeTable;
        synchronized (hashtable) {
            return this.antTypeTable.values().stream().filter(def -> def.getName().startsWith(prefix)).collect(Collectors.toList());
        }
    }
}

