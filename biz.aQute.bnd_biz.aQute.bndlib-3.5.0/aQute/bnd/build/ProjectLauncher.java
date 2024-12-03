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
import aQute.bnd.build.RunSession;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.service.Strategy;
import aQute.libg.command.Command;
import aQute.libg.generics.Create;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ProjectLauncher
extends Processor {
    private static final Logger logger = LoggerFactory.getLogger(ProjectLauncher.class);
    private final Project project;
    private long timeout = 0L;
    private final List<String> classpath = new ArrayList<String>();
    private List<String> runbundles = Create.list();
    private final List<String> runvm = new ArrayList<String>();
    private final List<String> runprogramargs = new ArrayList<String>();
    private Map<String, String> runproperties;
    private Command java;
    private Parameters runsystempackages;
    private Parameters runsystemcapabilities;
    private final List<String> activators = Create.list();
    private File storageDir;
    private boolean trace;
    private boolean keep;
    private int framework;
    private File cwd;
    private Collection<String> agents = new ArrayList<String>();
    private Map<NotificationListener, Boolean> listeners = new IdentityHashMap<NotificationListener, Boolean>();
    protected Appendable out = System.out;
    protected Appendable err = System.err;
    protected InputStream in = System.in;
    public static final int SERVICES = 10111;
    public static final int NONE = 20123;
    public static final int OK = 0;
    public static final int WARNING = -1;
    public static final int ERROR = -2;
    public static final int TIMEDOUT = -3;
    public static final int UPDATE_NEEDED = -4;
    public static final int CANCELED = -5;
    public static final int DUPLICATE_BUNDLE = -6;
    public static final int RESOLVE_ERROR = -7;
    public static final int ACTIVATOR_ERROR = -8;
    public static final int CUSTOM_LAUNCHER = -128;
    public static final String EMBEDDED_ACTIVATOR = "Embedded-Activator";
    static Pattern IGNORE = Pattern.compile("org(/|\\.)osgi(/|\\.).resource.*");

    public ProjectLauncher(Project project) throws Exception {
        this.project = project;
        this.updateFromProject();
    }

    protected void updateFromProject() throws Exception {
        File[] builds;
        this.setCwd(this.project.getBase());
        this.runbundles.clear();
        Collection<Container> run = this.project.getRunbundles();
        for (Container container : run) {
            File file = container.getFile();
            if (file != null && (file.isFile() || file.isDirectory())) {
                this.runbundles.add(file.getAbsolutePath());
                continue;
            }
            this.project.error("Bundle file \"%s\" does not exist, given error is %s", file, container.getError());
        }
        if (this.project.getRunBuilds() && (builds = this.project.getBuildFiles(true)) != null) {
            for (File file : builds) {
                this.runbundles.add(file.getAbsolutePath());
            }
        }
        Collection<Container> runpath = this.project.getRunpath();
        this.runsystempackages = new Parameters(this.project.mergeProperties("-runsystempackages"), this.project);
        this.runsystemcapabilities = new Parameters(this.project.mergeProperties("-runsystemcapabilities"), this.project);
        this.framework = this.getRunframework(this.project.getProperty("-runframework"));
        this.timeout = Processor.getDuration(this.project.getProperty("-runtimeout"), 0L);
        this.trace = Processor.isTrue(this.project.getProperty("-runtrace"));
        runpath.addAll(this.project.getRunFw());
        for (Container c : runpath) {
            this.addClasspath(c);
        }
        this.runvm.addAll(this.project.getRunVM());
        this.runprogramargs.addAll(this.project.getRunProgramArgs());
        this.runproperties = this.project.getRunProperties();
        this.storageDir = this.project.getRunStorage();
        this.setKeep(this.project.getRunKeep());
    }

    private int getRunframework(String property) {
        if ("none".equalsIgnoreCase(property)) {
            return 20123;
        }
        if ("services".equalsIgnoreCase(property)) {
            return 10111;
        }
        return 10111;
    }

    public void addClasspath(Container container) throws Exception {
        if (container.getError() != null) {
            this.project.error("Cannot launch because %s has reported %s", container.getProject(), container.getError());
        } else {
            List<Container> members = container.getMembers();
            for (Container m : members) {
                String path = m.getFile().getAbsolutePath();
                if (this.classpath.contains(path)) continue;
                Manifest manifest = m.getManifest();
                if (manifest != null) {
                    String agentClassName = manifest.getMainAttributes().getValue("Premain-Class");
                    if (agentClassName != null) {
                        String agent = path;
                        if (container.getAttributes().get("agent") != null) {
                            agent = agent + "=" + container.getAttributes().get("agent");
                        }
                        this.agents.add(path);
                    }
                    Parameters exports = this.project.parseHeader(manifest.getMainAttributes().getValue("Export-Package"));
                    for (Map.Entry<String, Attrs> e : exports.entrySet()) {
                        if (this.runsystempackages.containsKey(e.getKey())) continue;
                        this.runsystempackages.put(e.getKey(), e.getValue());
                    }
                    String activator = manifest.getMainAttributes().getValue(EMBEDDED_ACTIVATOR);
                    if (activator != null) {
                        this.activators.add(activator);
                    }
                }
                this.classpath.add(path);
            }
        }
    }

    protected void addClasspath(Collection<Container> path) throws Exception {
        for (Container c : Container.flatten(path)) {
            this.addClasspath(c);
        }
    }

    public void addRunBundle(String f) {
        this.runbundles.add(f);
    }

    public Collection<String> getRunBundles() {
        return this.runbundles;
    }

    public void addRunVM(String arg) {
        this.runvm.add(arg);
    }

    public void addRunProgramArgs(String arg) {
        this.runprogramargs.add(arg);
    }

    public List<String> getRunpath() {
        return this.classpath;
    }

    public Collection<String> getClasspath() {
        return this.classpath;
    }

    public Collection<String> getRunVM() {
        return this.runvm;
    }

    @Deprecated
    public Collection<String> getArguments() {
        return this.getRunProgramArgs();
    }

    public Collection<String> getRunProgramArgs() {
        return this.runprogramargs;
    }

    public Map<String, String> getRunProperties() {
        return this.runproperties;
    }

    public File getStorageDir() {
        return this.storageDir;
    }

    public abstract String getMainTypeName();

    public abstract void update() throws Exception;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public int launch() throws Exception {
        File cwd;
        String jdb;
        this.prepare();
        this.java = new Command();
        Map<String, String> env = this.getRunEnv();
        for (Map.Entry<String, String> e : env.entrySet()) {
            this.java.var(e.getKey(), e.getValue());
        }
        this.java.add(this.project.getProperty("java", this.getJavaExecutable()));
        String javaagent = this.project.getProperty("-javaagent");
        if (Processor.isTrue(javaagent)) {
            for (String agent : this.agents) {
                this.java.add("-javaagent:" + agent);
            }
        }
        if ((jdb = this.getRunJdb()) != null) {
            int port = 1044;
            try {
                port = Integer.parseInt(this.project.getProperty("-runjdb"));
            }
            catch (Exception e) {
                // empty catch block
            }
            String suspend = port > 0 ? "y" : "n";
            this.java.add("-Xrunjdwp:server=y,transport=dt_socket,address=" + Math.abs(port) + ",suspend=" + suspend);
        }
        this.java.add("-cp");
        this.java.add(Processor.join(this.getClasspath(), File.pathSeparator));
        this.java.addAll(this.getRunVM());
        this.java.add(this.getMainTypeName());
        this.java.addAll(this.getRunProgramArgs());
        if (this.timeout != 0L) {
            this.java.setTimeout(this.timeout + 1000L, TimeUnit.MILLISECONDS);
        }
        if ((cwd = this.getCwd()) != null) {
            this.java.setCwd(cwd);
        }
        logger.debug("cmd line {}", (Object)this.java);
        try {
            int result = this.java.execute(this.in, this.out, this.err);
            if (result == Integer.MIN_VALUE) {
                int n = -3;
                return n;
            }
            this.reportResult(result);
            int n = result;
            return n;
        }
        finally {
            this.cleanup();
            this.listeners.clear();
        }
    }

    private String getJavaExecutable() {
        String javaHome = System.getProperty("java.home");
        if (javaHome == null) {
            return "java";
        }
        File java = new File(javaHome, "bin/java");
        return java.getAbsolutePath();
    }

    public int start(ClassLoader parent) throws Exception {
        this.prepare();
        ClassLoader fcl = new ClassLoader(parent){

            @Override
            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                if (IGNORE.matcher(name).matches()) {
                    throw new ClassNotFoundException();
                }
                return super.loadClass(name, resolve);
            }
        };
        ArrayList<URL> cp = new ArrayList<URL>();
        for (String path : this.getClasspath()) {
            cp.add(new File(path).toURI().toURL());
        }
        URLClassLoader cl = new URLClassLoader(cp.toArray(new URL[0]), fcl);
        String[] args = this.getRunProgramArgs().toArray(new String[0]);
        Class<?> main = cl.loadClass(this.getMainTypeName());
        return this.invoke(main, args);
    }

    protected int invoke(Class<?> main, String[] args) throws Exception {
        throw new UnsupportedOperationException();
    }

    public void cleanup() {
    }

    protected void reportResult(int result) {
        switch (result) {
            case 0: {
                logger.debug("Command terminated normal {}", (Object)this.java);
                break;
            }
            case -3: {
                this.project.error("Launch timedout: %s", this.java);
                break;
            }
            case -2: {
                this.project.error("Launch errored: %s", this.java);
                break;
            }
            case -1: {
                this.project.warning("Launch had a warning %s", this.java);
                break;
            }
            default: {
                this.project.error("Exit code remote process %d: %s", result, this.java);
            }
        }
    }

    public void setTimeout(long timeout, TimeUnit unit) {
        this.timeout = unit.convert(timeout, TimeUnit.MILLISECONDS);
    }

    public long getTimeout() {
        return this.timeout;
    }

    public void cancel() throws Exception {
        this.java.cancel();
    }

    public Map<String, ? extends Map<String, String>> getSystemPackages() {
        return this.runsystempackages.asMapMap();
    }

    public String getSystemCapabilities() {
        return this.runsystemcapabilities.isEmpty() ? null : this.runsystemcapabilities.toString();
    }

    public Parameters getSystemCapabilitiesParameters() {
        return this.runsystemcapabilities;
    }

    public void setKeep(boolean keep) {
        this.keep = keep;
    }

    public boolean isKeep() {
        return this.keep;
    }

    @Override
    public void setTrace(boolean level) {
        this.trace = level;
    }

    public boolean getTrace() {
        return this.trace;
    }

    public abstract void prepare() throws Exception;

    public Project getProject() {
        return this.project;
    }

    public boolean addActivator(String e) {
        return this.activators.add(e);
    }

    public Collection<String> getActivators() {
        return Collections.unmodifiableCollection(this.activators);
    }

    public int getRunFramework() {
        return this.framework;
    }

    public void setRunFramework(int n) {
        assert (n == 20123 || n == 10111);
        this.framework = n;
    }

    public void addDefault(String defaultSpec) throws Exception {
        List<Container> deflts = this.project.getBundles(Strategy.HIGHEST, defaultSpec, null);
        for (Container c : deflts) {
            this.addClasspath(c);
        }
    }

    public Jar executable() throws Exception {
        throw new UnsupportedOperationException();
    }

    public File getCwd() {
        return this.cwd;
    }

    public void setCwd(File cwd) {
        this.cwd = cwd;
    }

    public String getRunJdb() {
        return this.project.getProperty("-runjdb");
    }

    public Map<String, String> getRunEnv() {
        String runenv = this.project.getProperty("-runenv");
        if (runenv != null) {
            return OSGiHeader.parseProperties(runenv);
        }
        return Collections.emptyMap();
    }

    public void registerForNotifications(NotificationListener listener) {
        this.listeners.put(listener, Boolean.TRUE);
    }

    public Set<NotificationListener> getNotificationListeners() {
        return Collections.unmodifiableSet(this.listeners.keySet());
    }

    public void setStreams(Appendable out, Appendable err) {
        this.out = out;
        this.err = err;
    }

    public void write(String text) throws Exception {
    }

    public List<? extends RunSession> getRunSessions() throws Exception {
        return null;
    }

    public void calculatedProperties(Map<String, Object> properties) throws Exception {
        if (!this.keep) {
            properties.put("org.osgi.framework.storage.clean", "onFirstInit");
        }
        if (!this.runsystemcapabilities.isEmpty()) {
            properties.put("org.osgi.framework.system.capabilities.extra", this.runsystemcapabilities.toString());
        }
        if (!this.runsystempackages.isEmpty()) {
            properties.put("org.osgi.framework.system.packages.extra", this.runsystempackages.toString());
        }
    }

    public static enum NotificationType {
        ERROR,
        WARNING,
        INFO;

    }

    public static interface NotificationListener {
        public void notify(NotificationType var1, String var2);
    }
}

