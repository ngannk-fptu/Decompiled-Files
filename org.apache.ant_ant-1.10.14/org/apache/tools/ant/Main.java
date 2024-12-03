/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tools.ant.launch.AntMain
 */
package org.apache.tools.ant;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;
import java.util.stream.Collectors;
import org.apache.tools.ant.ArgumentProcessor;
import org.apache.tools.ant.ArgumentProcessorRegistry;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.BuildLogger;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.DemuxInputStream;
import org.apache.tools.ant.DemuxOutputStream;
import org.apache.tools.ant.Diagnostics;
import org.apache.tools.ant.ExitStatusException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.apache.tools.ant.ProjectHelperRepository;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.Target;
import org.apache.tools.ant.input.DefaultInputHandler;
import org.apache.tools.ant.input.InputHandler;
import org.apache.tools.ant.launch.AntMain;
import org.apache.tools.ant.listener.SilentLogger;
import org.apache.tools.ant.property.GetProperty;
import org.apache.tools.ant.property.ResolvePropertyMap;
import org.apache.tools.ant.util.ClasspathUtils;
import org.apache.tools.ant.util.FileUtils;
import org.apache.tools.ant.util.ProxySetup;
import org.apache.tools.ant.util.StreamUtils;

public class Main
implements AntMain {
    private static final Set<String> LAUNCH_COMMANDS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList("-lib", "-cp", "-noclasspath", "--noclasspath", "-nouserlib", "-main")));
    public static final String DEFAULT_BUILD_FILENAME = "build.xml";
    private int msgOutputLevel = 2;
    private File buildFile;
    private PrintStream out = System.out;
    private PrintStream err = System.err;
    private final Vector<String> targets = new Vector();
    private final Properties definedProps = new Properties();
    private final Vector<String> listeners = new Vector(1);
    private final Vector<String> propertyFiles = new Vector(1);
    private boolean allowInput = true;
    private boolean keepGoingMode = false;
    private String loggerClassname = null;
    private String inputHandlerClassname = null;
    private boolean emacsMode = false;
    private boolean silent = false;
    private boolean readyToRun = false;
    private boolean projectHelp = false;
    private boolean isLogFileUsed = false;
    private Integer threadPriority = null;
    private boolean proxy = false;
    private final Map<Class<?>, List<String>> extraArguments = new HashMap();
    private static final GetProperty NOPROPERTIES = aName -> null;
    private static String antVersion = null;
    private static String shortAntVersion = null;

    private static void printMessage(Throwable t) {
        String message = t.getMessage();
        if (message != null) {
            System.err.println(message);
        }
    }

    public static void start(String[] args, Properties additionalUserProperties, ClassLoader coreLoader) {
        Main m = new Main();
        m.startAnt(args, additionalUserProperties, coreLoader);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void startAnt(String[] args, Properties additionalUserProperties, ClassLoader coreLoader) {
        try {
            this.processArgs(args);
        }
        catch (Throwable exc) {
            this.handleLogfile();
            Main.printMessage(exc);
            this.exit(1);
            return;
        }
        if (additionalUserProperties != null) {
            additionalUserProperties.stringPropertyNames().forEach(key -> this.definedProps.put(key, additionalUserProperties.getProperty((String)key)));
        }
        int exitCode = 1;
        try {
            try {
                this.runBuild(coreLoader);
                exitCode = 0;
            }
            catch (ExitStatusException ese) {
                exitCode = ese.getStatus();
                if (exitCode != 0) {
                    throw ese;
                }
            }
        }
        catch (BuildException be) {
            if (this.err != System.err) {
                Main.printMessage(be);
            }
        }
        catch (Throwable exc) {
            exc.printStackTrace();
            Main.printMessage(exc);
        }
        finally {
            this.handleLogfile();
        }
        this.exit(exitCode);
    }

    protected void exit(int exitCode) {
        System.exit(exitCode);
    }

    private void handleLogfile() {
        if (this.isLogFileUsed) {
            FileUtils.close(this.out);
            FileUtils.close(this.err);
        }
    }

    public static void main(String[] args) {
        Main.start(args, null, null);
    }

    public Main() {
    }

    @Deprecated
    protected Main(String[] args) throws BuildException {
        this.processArgs(args);
    }

    private void processArgs(String[] args) {
        String searchForThis = null;
        boolean searchForFile = false;
        PrintStream logTo = null;
        boolean justPrintUsage = false;
        boolean justPrintVersion = false;
        boolean justPrintDiagnostics = false;
        ArgumentProcessorRegistry processorRegistry = ArgumentProcessorRegistry.getInstance();
        for (int i = 0; i < args.length; ++i) {
            Object msg;
            String arg = args[i];
            if (arg.equals("-help") || arg.equals("-h")) {
                justPrintUsage = true;
                continue;
            }
            if (arg.equals("-version")) {
                justPrintVersion = true;
                continue;
            }
            if (arg.equals("-diagnostics")) {
                justPrintDiagnostics = true;
                continue;
            }
            if (arg.equals("-quiet") || arg.equals("-q")) {
                this.msgOutputLevel = 1;
                continue;
            }
            if (arg.equals("-verbose") || arg.equals("-v")) {
                this.msgOutputLevel = 3;
                continue;
            }
            if (arg.equals("-debug") || arg.equals("-d")) {
                this.msgOutputLevel = 4;
                continue;
            }
            if (arg.equals("-silent") || arg.equals("-S")) {
                this.silent = true;
                continue;
            }
            if (arg.equals("-noinput")) {
                this.allowInput = false;
                continue;
            }
            if (arg.equals("-logfile") || arg.equals("-l")) {
                try {
                    File logFile = new File(args[i + 1]);
                    ++i;
                    logTo = new PrintStream(Files.newOutputStream(logFile.toPath(), new OpenOption[0]));
                    this.isLogFileUsed = true;
                    continue;
                }
                catch (IOException ioe) {
                    msg = "Cannot write on the specified log file. Make sure the path exists and you have write permissions.";
                    throw new BuildException("Cannot write on the specified log file. Make sure the path exists and you have write permissions.");
                }
                catch (ArrayIndexOutOfBoundsException aioobe) {
                    msg = "You must specify a log file when using the -log argument";
                    throw new BuildException("You must specify a log file when using the -log argument");
                }
            }
            if (arg.equals("-buildfile") || arg.equals("-file") || arg.equals("-f")) {
                i = this.handleArgBuildFile(args, i);
                continue;
            }
            if (arg.equals("-listener")) {
                i = this.handleArgListener(args, i);
                continue;
            }
            if (arg.startsWith("-D")) {
                i = this.handleArgDefine(args, i);
                continue;
            }
            if (arg.equals("-logger")) {
                i = this.handleArgLogger(args, i);
                continue;
            }
            if (arg.equals("-inputhandler")) {
                i = this.handleArgInputHandler(args, i);
                continue;
            }
            if (arg.equals("-emacs") || arg.equals("-e")) {
                this.emacsMode = true;
                continue;
            }
            if (arg.equals("-projecthelp") || arg.equals("-p")) {
                this.projectHelp = true;
                continue;
            }
            if (arg.equals("-find") || arg.equals("-s")) {
                searchForFile = true;
                if (i >= args.length - 1) continue;
                searchForThis = args[++i];
                continue;
            }
            if (arg.startsWith("-propertyfile")) {
                i = this.handleArgPropertyFile(args, i);
                continue;
            }
            if (arg.equals("-k") || arg.equals("-keep-going")) {
                this.keepGoingMode = true;
                continue;
            }
            if (arg.equals("-nice")) {
                i = this.handleArgNice(args, i);
                continue;
            }
            if (LAUNCH_COMMANDS.contains(arg)) {
                String msg2 = "Ant's Main method is being handed an option " + arg + " that is only for the launcher class.\nThis can be caused by a version mismatch between the ant script/.bat file and Ant itself.";
                throw new BuildException(msg2);
            }
            if (arg.equals("-autoproxy")) {
                this.proxy = true;
                continue;
            }
            if (arg.startsWith("-")) {
                boolean processed = false;
                for (ArgumentProcessor processor : processorRegistry.getProcessors()) {
                    int newI = processor.readArguments(args, i);
                    if (newI == -1) continue;
                    List extraArgs = this.extraArguments.computeIfAbsent(processor.getClass(), k -> new ArrayList());
                    extraArgs.addAll(Arrays.asList(args).subList(newI, args.length));
                    processed = true;
                    break;
                }
                if (processed) continue;
                msg = "Unknown argument: " + arg;
                System.err.println((String)msg);
                Main.printUsage();
                throw new BuildException("");
            }
            this.targets.addElement(arg);
        }
        if (this.msgOutputLevel >= 3 || justPrintVersion) {
            Main.printVersion(this.msgOutputLevel);
        }
        if (justPrintUsage || justPrintVersion || justPrintDiagnostics) {
            if (justPrintUsage) {
                Main.printUsage();
            }
            if (justPrintDiagnostics) {
                Diagnostics.doReport(System.out, this.msgOutputLevel);
            }
            return;
        }
        if (this.buildFile == null) {
            ProjectHelper helper;
            if (searchForFile) {
                if (searchForThis != null) {
                    this.buildFile = this.findBuildFile(System.getProperty("user.dir"), searchForThis);
                } else {
                    Iterator<ProjectHelper> it = ProjectHelperRepository.getInstance().getHelpers();
                    do {
                        helper = it.next();
                        searchForThis = helper.getDefaultBuildFile();
                        if (this.msgOutputLevel >= 3) {
                            System.out.println("Searching the default build file: " + searchForThis);
                        }
                        this.buildFile = this.findBuildFile(System.getProperty("user.dir"), searchForThis);
                    } while (this.buildFile == null && it.hasNext());
                }
                if (this.buildFile == null) {
                    throw new BuildException("Could not locate a build file!");
                }
            } else {
                Iterator<ProjectHelper> it = ProjectHelperRepository.getInstance().getHelpers();
                do {
                    helper = it.next();
                    this.buildFile = new File(helper.getDefaultBuildFile());
                    if (this.msgOutputLevel < 3) continue;
                    System.out.println("Trying the default build file: " + this.buildFile);
                } while (!this.buildFile.exists() && it.hasNext());
            }
        }
        if (!this.buildFile.exists()) {
            System.out.println("Buildfile: " + this.buildFile + " does not exist!");
            throw new BuildException("Build failed");
        }
        if (this.buildFile.isDirectory()) {
            File whatYouMeant = new File(this.buildFile, DEFAULT_BUILD_FILENAME);
            if (whatYouMeant.isFile()) {
                this.buildFile = whatYouMeant;
            } else {
                System.out.println("What? Buildfile: " + this.buildFile + " is a dir!");
                throw new BuildException("Build failed");
            }
        }
        this.buildFile = FileUtils.getFileUtils().normalize(this.buildFile.getAbsolutePath());
        this.loadPropertyFiles();
        if (this.msgOutputLevel >= 2) {
            System.out.println("Buildfile: " + this.buildFile);
        }
        if (logTo != null) {
            this.out = logTo;
            this.err = logTo;
            System.setOut(this.out);
            System.setErr(this.err);
        }
        this.readyToRun = true;
    }

    private int handleArgBuildFile(String[] args, int pos) {
        try {
            this.buildFile = new File(args[++pos].replace('/', File.separatorChar));
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {
            throw new BuildException("You must specify a buildfile when using the -buildfile argument");
        }
        return pos;
    }

    private int handleArgListener(String[] args, int pos) {
        try {
            this.listeners.addElement(args[pos + 1]);
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {
            String msg = "You must specify a classname when using the -listener argument";
            throw new BuildException("You must specify a classname when using the -listener argument");
        }
        return ++pos;
    }

    private int handleArgDefine(String[] args, int argPos) {
        String value;
        String arg = args[argPos];
        String name = arg.substring(2);
        int posEq = name.indexOf(61);
        if (posEq > 0) {
            value = name.substring(posEq + 1);
            name = name.substring(0, posEq);
        } else if (argPos < args.length - 1) {
            value = args[++argPos];
        } else {
            throw new BuildException("Missing value for property " + name);
        }
        this.definedProps.put(name, value);
        return argPos;
    }

    private int handleArgLogger(String[] args, int pos) {
        if (this.loggerClassname != null) {
            throw new BuildException("Only one logger class may be specified.");
        }
        try {
            this.loggerClassname = args[++pos];
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {
            throw new BuildException("You must specify a classname when using the -logger argument");
        }
        return pos;
    }

    private int handleArgInputHandler(String[] args, int pos) {
        if (this.inputHandlerClassname != null) {
            throw new BuildException("Only one input handler class may be specified.");
        }
        try {
            this.inputHandlerClassname = args[++pos];
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {
            throw new BuildException("You must specify a classname when using the -inputhandler argument");
        }
        return pos;
    }

    private int handleArgPropertyFile(String[] args, int pos) {
        try {
            this.propertyFiles.addElement(args[++pos]);
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {
            String msg = "You must specify a property filename when using the -propertyfile argument";
            throw new BuildException("You must specify a property filename when using the -propertyfile argument");
        }
        return pos;
    }

    private int handleArgNice(String[] args, int pos) {
        try {
            this.threadPriority = Integer.decode(args[++pos]);
        }
        catch (ArrayIndexOutOfBoundsException aioobe) {
            throw new BuildException("You must supply a niceness value (1-10) after the -nice option");
        }
        catch (NumberFormatException e) {
            throw new BuildException("Unrecognized niceness value: " + args[pos]);
        }
        if (this.threadPriority < 1 || this.threadPriority > 10) {
            throw new BuildException("Niceness value is out of the range 1-10");
        }
        return pos;
    }

    private void loadPropertyFiles() {
        for (String filename : this.propertyFiles) {
            Properties props = new Properties();
            try (InputStream fis = Files.newInputStream(Paths.get(filename, new String[0]), new OpenOption[0]);){
                props.load(fis);
            }
            catch (IOException e) {
                System.out.println("Could not load property file " + filename + ": " + e.getMessage());
            }
            props.stringPropertyNames().stream().filter(name -> this.definedProps.getProperty((String)name) == null).forEach(name -> this.definedProps.put(name, props.getProperty((String)name)));
        }
    }

    @Deprecated
    private File getParentFile(File file) {
        File parent = file.getParentFile();
        if (parent != null && this.msgOutputLevel >= 3) {
            System.out.println("Searching in " + parent.getAbsolutePath());
        }
        return parent;
    }

    private File findBuildFile(String start, String suffix) {
        if (this.msgOutputLevel >= 2) {
            System.out.println("Searching for " + suffix + " ...");
        }
        File parent = new File(new File(start).getAbsolutePath());
        File file = new File(parent, suffix);
        while (!file.exists()) {
            if ((parent = this.getParentFile(parent)) == null) {
                return null;
            }
            file = new File(parent, suffix);
        }
        return file;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private void runBuild(ClassLoader coreLoader) throws BuildException {
        if (!this.readyToRun) {
            return;
        }
        ArgumentProcessorRegistry processorRegistry = ArgumentProcessorRegistry.getInstance();
        for (ArgumentProcessor processor : processorRegistry.getProcessors()) {
            List<String> extraArgs = this.extraArguments.get(processor.getClass());
            if (extraArgs == null || !processor.handleArg(extraArgs)) continue;
            return;
        }
        Project project = new Project();
        project.setCoreLoader(coreLoader);
        Throwable error = null;
        try {
            this.addBuildListeners(project);
            this.addInputHandler(project);
            PrintStream savedErr = System.err;
            PrintStream savedOut = System.out;
            InputStream savedIn = System.in;
            try {
                List<String> extraArgs;
                if (this.allowInput) {
                    project.setDefaultInputStream(System.in);
                }
                System.setIn(new DemuxInputStream(project));
                System.setOut(new PrintStream(new DemuxOutputStream(project, false)));
                System.setErr(new PrintStream(new DemuxOutputStream(project, true)));
                if (!this.projectHelp) {
                    project.fireBuildStarted();
                }
                if (this.threadPriority != null) {
                    try {
                        project.log("Setting Ant's thread priority to " + this.threadPriority, 3);
                        Thread.currentThread().setPriority(this.threadPriority);
                    }
                    catch (SecurityException swallowed) {
                        project.log("A security manager refused to set the -nice value");
                    }
                }
                this.setProperties(project);
                project.setKeepGoingMode(this.keepGoingMode);
                if (this.proxy) {
                    Iterator<ArgumentProcessor> proxySetup = new ProxySetup(project);
                    ((ProxySetup)((Object)proxySetup)).enableProxies();
                }
                for (ArgumentProcessor processor : processorRegistry.getProcessors()) {
                    extraArgs = this.extraArguments.get(processor.getClass());
                    if (extraArgs == null) continue;
                    processor.prepareConfigure(project, extraArgs);
                }
                ProjectHelper.configureProject(project, this.buildFile);
                for (ArgumentProcessor processor : processorRegistry.getProcessors()) {
                    extraArgs = this.extraArguments.get(processor.getClass());
                    if (extraArgs == null || !processor.handleArg(project, extraArgs)) continue;
                    return;
                }
                if (this.projectHelp) {
                    Main.printDescription(project);
                    Main.printTargets(project, this.msgOutputLevel > 2, this.msgOutputLevel > 3);
                    return;
                }
                if (this.targets.isEmpty() && project.getDefaultTarget() != null) {
                    this.targets.addElement(project.getDefaultTarget());
                }
                project.executeTargets(this.targets);
                return;
            }
            finally {
                System.setOut(savedOut);
                System.setErr(savedErr);
                System.setIn(savedIn);
            }
        }
        catch (Error | RuntimeException exc) {
            error = exc;
            throw exc;
        }
        finally {
            if (!this.projectHelp) {
                try {
                    project.fireBuildFinished(error);
                }
                catch (Throwable t) {
                    System.err.println("Caught an exception while logging the end of the build.  Exception was:");
                    t.printStackTrace();
                    if (error == null) throw new BuildException(t);
                    System.err.println("There has been an error prior to that:");
                    error.printStackTrace();
                    throw new BuildException(t);
                }
            } else if (error != null) {
                project.log(error.toString(), 0);
            }
        }
    }

    private void setProperties(Project project) {
        HashMap<Object, Object> raw;
        project.init();
        PropertyHelper propertyHelper = PropertyHelper.getPropertyHelper(project);
        HashMap<Object, Object> props = raw = new HashMap<Object, Object>(this.definedProps);
        ResolvePropertyMap resolver = new ResolvePropertyMap(project, NOPROPERTIES, propertyHelper.getExpanders());
        resolver.resolveAllProperties(props, null, false);
        props.forEach((arg, value) -> project.setUserProperty((String)arg, String.valueOf(value)));
        project.setUserProperty("ant.file", this.buildFile.getAbsolutePath());
        project.setUserProperty("ant.file.type", "file");
        project.setUserProperty("ant.project.invoked-targets", String.join((CharSequence)",", this.targets));
    }

    protected void addBuildListeners(Project project) {
        project.addBuildListener(this.createLogger());
        int count = this.listeners.size();
        for (int i = 0; i < count; ++i) {
            String className = this.listeners.elementAt(i);
            BuildListener listener = ClasspathUtils.newInstance(className, Main.class.getClassLoader(), BuildListener.class);
            project.setProjectReference(listener);
            project.addBuildListener(listener);
        }
    }

    private void addInputHandler(Project project) throws BuildException {
        InputHandler handler = null;
        if (this.inputHandlerClassname == null) {
            handler = new DefaultInputHandler();
        } else {
            handler = ClasspathUtils.newInstance(this.inputHandlerClassname, Main.class.getClassLoader(), InputHandler.class);
            project.setProjectReference(handler);
        }
        project.setInputHandler(handler);
    }

    private BuildLogger createLogger() {
        BuildLogger logger = null;
        if (this.silent) {
            logger = new SilentLogger();
            this.msgOutputLevel = 1;
            this.emacsMode = true;
        } else if (this.loggerClassname != null) {
            try {
                logger = ClasspathUtils.newInstance(this.loggerClassname, Main.class.getClassLoader(), BuildLogger.class);
            }
            catch (BuildException e) {
                System.err.println("The specified logger class " + this.loggerClassname + " could not be used because " + e.getMessage());
                throw e;
            }
        } else {
            logger = new DefaultLogger();
        }
        logger.setMessageOutputLevel(this.msgOutputLevel);
        logger.setOutputPrintStream(this.out);
        logger.setErrorPrintStream(this.err);
        logger.setEmacsMode(this.emacsMode);
        return logger;
    }

    private static void printUsage() {
        System.out.println("ant [options] [target [target2 [target3] ...]]");
        System.out.println("Options: ");
        System.out.println("  -help, -h              print this message and exit");
        System.out.println("  -projecthelp, -p       print project help information and exit");
        System.out.println("  -version               print the version information and exit");
        System.out.println("  -diagnostics           print information that might be helpful to");
        System.out.println("                         diagnose or report problems and exit");
        System.out.println("  -quiet, -q             be extra quiet");
        System.out.println("  -silent, -S            print nothing but task outputs and build failures");
        System.out.println("  -verbose, -v           be extra verbose");
        System.out.println("  -debug, -d             print debugging information");
        System.out.println("  -emacs, -e             produce logging information without adornments");
        System.out.println("  -lib <path>            specifies a path to search for jars and classes");
        System.out.println("  -logfile <file>        use given file for log");
        System.out.println("    -l     <file>                ''");
        System.out.println("  -logger <classname>    the class which is to perform logging");
        System.out.println("  -listener <classname>  add an instance of class as a project listener");
        System.out.println("  -noinput               do not allow interactive input");
        System.out.println("  -buildfile <file>      use given buildfile");
        System.out.println("    -file    <file>              ''");
        System.out.println("    -f       <file>              ''");
        System.out.println("  -D<property>=<value>   use value for given property");
        System.out.println("  -keep-going, -k        execute all targets that do not depend");
        System.out.println("                         on failed target(s)");
        System.out.println("  -propertyfile <name>   load all properties from file with -D");
        System.out.println("                         properties taking precedence");
        System.out.println("  -inputhandler <class>  the class which will handle input requests");
        System.out.println("  -find <file>           (s)earch for buildfile towards the root of");
        System.out.println("    -s  <file>           the filesystem and use it");
        System.out.println("  -nice  number          A niceness value for the main thread:");
        System.out.println("                         1 (lowest) to 10 (highest); 5 is the default");
        System.out.println("  -nouserlib             Run ant without using the jar files from");
        System.out.println("                         ${user.home}/.ant/lib");
        System.out.println("  -noclasspath           Run ant without using CLASSPATH");
        System.out.println("  -autoproxy             Java1.5+: use the OS proxy settings");
        System.out.println("  -main <class>          override Ant's normal entry point");
        for (ArgumentProcessor processor : ArgumentProcessorRegistry.getInstance().getProcessors()) {
            processor.printUsage(System.out);
        }
    }

    private static void printVersion(int logLevel) throws BuildException {
        System.out.println(Main.getAntVersion());
    }

    public static synchronized String getAntVersion() throws BuildException {
        if (antVersion == null) {
            try {
                Properties props = new Properties();
                InputStream in = Main.class.getResourceAsStream("/org/apache/tools/ant/version.txt");
                props.load(in);
                in.close();
                shortAntVersion = props.getProperty("VERSION");
                antVersion = "Apache Ant(TM) version " + shortAntVersion + " compiled on " + props.getProperty("DATE");
            }
            catch (IOException ioe) {
                throw new BuildException("Could not load the version information:" + ioe.getMessage());
            }
            catch (NullPointerException npe) {
                throw new BuildException("Could not load the version information.");
            }
        }
        return antVersion;
    }

    public static String getShortAntVersion() throws BuildException {
        if (shortAntVersion == null) {
            Main.getAntVersion();
        }
        return shortAntVersion;
    }

    private static void printDescription(Project project) {
        if (project.getDescription() != null) {
            project.log(project.getDescription());
        }
    }

    private static Map<String, Target> removeDuplicateTargets(Map<String, Target> targets) {
        HashMap locationMap = new HashMap();
        targets.forEach((name, target) -> {
            Target otherTarget = (Target)locationMap.get(target.getLocation());
            if (otherTarget == null || otherTarget.getName().length() > name.length()) {
                locationMap.put(target.getLocation(), target);
            }
        });
        return locationMap.values().stream().collect(Collectors.toMap(Target::getName, target -> target, (a, b) -> b));
    }

    private static void printTargets(Project project, boolean printSubTargets, boolean printDependencies) {
        String defaultTarget;
        int maxLength = 0;
        Map<String, Target> ptargets = Main.removeDuplicateTargets(project.getTargets());
        Vector<String> topNames = new Vector<String>();
        Vector<String> topDescriptions = new Vector<String>();
        Vector<Enumeration<String>> topDependencies = new Vector<Enumeration<String>>();
        Vector<String> subNames = new Vector<String>();
        Vector<Enumeration<String>> subDependencies = new Vector<Enumeration<String>>();
        for (Target currentTarget : ptargets.values()) {
            int pos;
            String targetName = currentTarget.getName();
            if (targetName.isEmpty()) continue;
            String targetDescription = currentTarget.getDescription();
            if (targetDescription == null) {
                pos = Main.findTargetPosition(subNames, targetName);
                subNames.insertElementAt(targetName, pos);
                if (!printDependencies) continue;
                subDependencies.insertElementAt(currentTarget.getDependencies(), pos);
                continue;
            }
            pos = Main.findTargetPosition(topNames, targetName);
            topNames.insertElementAt(targetName, pos);
            topDescriptions.insertElementAt(targetDescription, pos);
            if (targetName.length() > maxLength) {
                maxLength = targetName.length();
            }
            if (!printDependencies) continue;
            topDependencies.insertElementAt(currentTarget.getDependencies(), pos);
        }
        Main.printTargets(project, topNames, topDescriptions, topDependencies, "Main targets:", maxLength);
        if (topNames.isEmpty()) {
            printSubTargets = true;
        }
        if (printSubTargets) {
            Main.printTargets(project, subNames, null, subDependencies, "Other targets:", 0);
        }
        if ((defaultTarget = project.getDefaultTarget()) != null && !defaultTarget.isEmpty()) {
            project.log("Default target: " + defaultTarget);
        }
    }

    private static int findTargetPosition(Vector<String> names, String name) {
        int size;
        int res = size = names.size();
        for (int i = 0; i < size && res == size; ++i) {
            if (name.compareTo(names.elementAt(i)) >= 0) continue;
            res = i;
        }
        return res;
    }

    private static void printTargets(Project project, Vector<String> names, Vector<String> descriptions, Vector<Enumeration<String>> dependencies, String heading, int maxlen) {
        String eol = System.lineSeparator();
        StringBuilder spaces = new StringBuilder("    ");
        while (spaces.length() <= maxlen) {
            spaces.append((CharSequence)spaces);
        }
        StringBuilder msg = new StringBuilder();
        msg.append(heading).append(eol).append(eol);
        int size = names.size();
        for (int i = 0; i < size; ++i) {
            msg.append(" ");
            msg.append(names.elementAt(i));
            if (descriptions != null) {
                msg.append(spaces.substring(0, maxlen - names.elementAt(i).length() + 2));
                msg.append(descriptions.elementAt(i));
            }
            msg.append(eol);
            if (dependencies.isEmpty() || !dependencies.elementAt(i).hasMoreElements()) continue;
            msg.append(StreamUtils.enumerationAsStream(dependencies.elementAt(i)).collect(Collectors.joining(", ", "   depends on: ", eol)));
        }
        project.log(msg.toString(), 1);
    }
}

