/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.bnd.build;

import aQute.bnd.annotation.plugin.BndPlugin;
import aQute.bnd.build.DownloadBlocker;
import aQute.bnd.build.LoggingProgressPlugin;
import aQute.bnd.build.Project;
import aQute.bnd.build.Run;
import aQute.bnd.build.WorkspaceLayout;
import aQute.bnd.build.WorkspaceRepository;
import aQute.bnd.connection.settings.ConnectionSettings;
import aQute.bnd.exporter.subsystem.SubsystemExporter;
import aQute.bnd.header.Attrs;
import aQute.bnd.header.OSGiHeader;
import aQute.bnd.header.Parameters;
import aQute.bnd.http.HttpClient;
import aQute.bnd.maven.support.Maven;
import aQute.bnd.osgi.About;
import aQute.bnd.osgi.Macro;
import aQute.bnd.osgi.Processor;
import aQute.bnd.osgi.Verifier;
import aQute.bnd.resource.repository.ResourceRepositoryImpl;
import aQute.bnd.service.BndListener;
import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.service.action.Action;
import aQute.bnd.service.extension.ExtensionActivator;
import aQute.bnd.service.lifecycle.LifeCyclePlugin;
import aQute.bnd.service.repository.Prepare;
import aQute.bnd.service.repository.RepositoryDigest;
import aQute.bnd.service.repository.SearchableRepository;
import aQute.bnd.url.MultiURLConnectionHandler;
import aQute.bnd.version.Version;
import aQute.bnd.version.VersionRange;
import aQute.lib.deployer.FileRepo;
import aQute.lib.hex.Hex;
import aQute.lib.io.IO;
import aQute.lib.settings.Settings;
import aQute.lib.strings.Strings;
import aQute.lib.utf8properties.UTF8Properties;
import aQute.lib.zip.ZipUtil;
import aQute.libg.uri.URIUtil;
import aQute.service.reporter.Reporter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.naming.TimeLimitExceededException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Workspace
extends Processor {
    private static final Logger logger = LoggerFactory.getLogger(Workspace.class);
    public static final File BND_DEFAULT_WS = IO.getFile("~/.bnd/default-ws");
    public static final String BND_CACHE_REPONAME = "bnd-cache";
    public static final String EXT = "ext";
    public static final String BUILDFILE = "build.bnd";
    public static final String CNFDIR = "cnf";
    public static final String BNDDIR = "bnd";
    public static final String CACHEDIR = "cache/" + About.CURRENT;
    public static final String STANDALONE_REPO_CLASS = "aQute.bnd.repository.osgi.OSGiRepository";
    static final int BUFFER_SIZE = 65536;
    private static final String PLUGIN_STANDALONE = "-plugin.standalone_";
    private final Pattern EMBEDDED_REPO_TESTING_PATTERN = Pattern.compile(".*biz\\.aQute\\.bnd\\.embedded-repo(-.*)?\\.jar");
    private static final Map<File, WeakReference<Workspace>> cache = Workspace.newHashMap();
    static Processor defaults = null;
    final Map<String, Project> models = Workspace.newHashMap();
    private final Set<String> modelsUnderConstruction = this.newSet();
    final Map<String, Action> commands = Workspace.newMap();
    final Maven maven = new Maven(Processor.getExecutor());
    private final AtomicBoolean offline = new AtomicBoolean();
    Settings settings = new Settings();
    WorkspaceRepository workspaceRepo = new WorkspaceRepository(this);
    static String overallDriver = "unset";
    static Parameters overallGestalt = new Parameters();
    final ThreadLocal<Reporter> signalBusy = new ThreadLocal();
    ResourceRepositoryImpl resourceRepositoryImpl;
    private Parameters gestalt;
    private String driver;
    private final WorkspaceLayout layout;
    final Set<Project> trail = Collections.newSetFromMap(new ConcurrentHashMap());
    private WorkspaceData data = new WorkspaceData();
    private File buildDir;
    static Pattern ESCAPE_P = Pattern.compile("(\"|')(.*)\u0001");

    public static Project getProject(File projectDir) throws Exception {
        projectDir = projectDir.getAbsoluteFile();
        assert (projectDir.isDirectory());
        Workspace ws = Workspace.getWorkspace(projectDir.getParentFile());
        return ws.getProject(projectDir.getName());
    }

    public static synchronized Processor getDefaults() {
        if (defaults != null) {
            return defaults;
        }
        UTF8Properties props = new UTF8Properties();
        try (InputStream propStream = Workspace.class.getResourceAsStream("defaults.bnd");){
            if (propStream != null) {
                props.load(propStream);
            } else {
                System.err.println("Cannot load defaults");
            }
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Unable to load bnd defaults.", e);
        }
        defaults = new Processor(props, false);
        return defaults;
    }

    public static Workspace createDefaultWorkspace() throws Exception {
        Workspace ws = new Workspace(BND_DEFAULT_WS, CNFDIR);
        return ws;
    }

    public static Workspace getWorkspace(File workspaceDir) throws Exception {
        return Workspace.getWorkspace(workspaceDir, CNFDIR);
    }

    public static Workspace getWorkspaceWithoutException(File workspaceDir) throws Exception {
        try {
            return Workspace.getWorkspace(workspaceDir);
        }
        catch (IllegalArgumentException e) {
            return null;
        }
    }

    public static Workspace findWorkspace(File base) throws Exception {
        for (File rover = base; rover != null; rover = rover.getParentFile()) {
            File file = IO.getFile(rover, "cnf/build.bnd");
            if (!file.isFile()) continue;
            return Workspace.getWorkspace(rover);
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static Workspace getWorkspace(File workspaceDir, String bndDir) throws Exception {
        workspaceDir = workspaceDir.getAbsoluteFile();
        while (workspaceDir.isDirectory()) {
            File test = new File(workspaceDir, CNFDIR);
            if (!test.exists()) {
                test = new File(workspaceDir, bndDir);
            }
            if (test.isDirectory()) break;
            if (test.isFile()) {
                String redirect = IO.collect(test).trim();
                workspaceDir = test = Workspace.getFile(test.getParentFile(), redirect).getAbsoluteFile();
            }
            if (test.exists()) continue;
            throw new IllegalArgumentException("No Workspace found from: " + workspaceDir);
        }
        Map<File, WeakReference<Workspace>> map = cache;
        synchronized (map) {
            Workspace ws;
            WeakReference<Workspace> wsr = cache.get(workspaceDir);
            if (wsr == null || (ws = (Workspace)wsr.get()) == null) {
                ws = new Workspace(workspaceDir, bndDir);
                cache.put(workspaceDir, new WeakReference<Workspace>(ws));
            }
            return ws;
        }
    }

    public Workspace(File workspaceDir) throws Exception {
        this(workspaceDir, CNFDIR);
    }

    public Workspace(File workspaceDir, String bndDir) throws Exception {
        super(Workspace.getDefaults());
        workspaceDir = workspaceDir.getAbsoluteFile();
        this.setBase(workspaceDir);
        this.layout = WorkspaceLayout.BND;
        this.addBasicPlugin(new LoggingProgressPlugin());
        this.setFileSystem(workspaceDir, bndDir);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void setFileSystem(File workspaceDir, String bndDir) throws Exception {
        workspaceDir = workspaceDir.getAbsoluteFile();
        IO.mkdirs(workspaceDir);
        assert (workspaceDir.isDirectory());
        Map<File, WeakReference<Workspace>> map = cache;
        synchronized (map) {
            WeakReference<Workspace> wsr = cache.get(this.getBase());
            if (wsr != null && wsr.get() == this) {
                cache.remove(this.getBase());
                cache.put(workspaceDir, wsr);
            }
        }
        File buildDir = new File(workspaceDir, bndDir).getAbsoluteFile();
        if (!buildDir.isDirectory()) {
            buildDir = new File(workspaceDir, CNFDIR).getAbsoluteFile();
        }
        this.setBuildDir(buildDir);
        File buildFile = new File(buildDir, BUILDFILE).getAbsoluteFile();
        if (!buildFile.isFile()) {
            this.warning("No Build File in %s", workspaceDir);
        }
        this.setProperties(buildFile, workspaceDir);
        this.propertiesChanged();
        Attrs sysProps = OSGiHeader.parseProperties(this.mergeProperties("-systemproperties"));
        for (Map.Entry<String, String> e : sysProps.entrySet()) {
            System.setProperty(e.getKey(), e.getValue());
        }
    }

    private Workspace(WorkspaceLayout layout) throws Exception {
        super(Workspace.getDefaults());
        this.layout = layout;
        this.setBuildDir(IO.getFile(BND_DEFAULT_WS, CNFDIR));
    }

    public Project getProjectFromFile(File projectDir) throws Exception {
        projectDir = projectDir.getAbsoluteFile();
        assert (projectDir.isDirectory());
        if (this.getBase().equals(projectDir.getParentFile())) {
            return this.getProject(projectDir.getName());
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Project getProject(String bsn) throws Exception {
        Map<String, Project> map = this.models;
        synchronized (map) {
            Project project = this.models.get(bsn);
            if (project != null) {
                return project;
            }
            if (this.modelsUnderConstruction.add(bsn)) {
                try {
                    File projectDir = this.getFile(bsn);
                    project = new Project(this, projectDir);
                    if (!project.isValid()) {
                        Project project2 = null;
                        return project2;
                    }
                    this.models.put(bsn, project);
                }
                finally {
                    this.modelsUnderConstruction.remove(bsn);
                }
            }
            return project;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void removeProject(Project p) throws Exception {
        if (p.isCnf()) {
            return;
        }
        Map<String, Project> map = this.models;
        synchronized (map) {
            this.models.remove(p.getName());
        }
        for (LifeCyclePlugin lp : this.getPlugins(LifeCyclePlugin.class)) {
            lp.delete(p);
        }
    }

    public boolean isPresent(String name) {
        return this.models.containsKey(name);
    }

    public Collection<Project> getCurrentProjects() {
        return this.models.values();
    }

    @Override
    public boolean refresh() {
        this.data = new WorkspaceData();
        if (super.refresh()) {
            for (Project project : this.getCurrentProjects()) {
                project.propertiesChanged();
            }
            return true;
        }
        return false;
    }

    @Override
    public void propertiesChanged() {
        this.data = new WorkspaceData();
        File extDir = new File(this.getBuildDir(), EXT);
        File[] extensions = extDir.listFiles();
        if (extensions != null) {
            for (File extension : extensions) {
                String extensionName = extension.getName();
                if (!extensionName.endsWith(".bnd")) continue;
                extensionName = extensionName.substring(0, extensionName.length() - ".bnd".length());
                try {
                    this.doIncludeFile(extension, false, this.getProperties(), "ext." + extensionName);
                }
                catch (Exception e) {
                    this.exception(e, "PropertiesChanged: %s", e);
                }
            }
        }
        super.propertiesChanged();
    }

    public String _workspace(String[] args) {
        return this.getBase().getAbsolutePath();
    }

    public void addCommand(String menu, Action action) {
        this.commands.put(menu, action);
    }

    public void removeCommand(String menu) {
        this.commands.remove(menu);
    }

    public void fillActions(Map<String, Action> all) {
        all.putAll(this.commands);
    }

    public Collection<Project> getAllProjects() throws Exception {
        ArrayList<Project> projects = new ArrayList<Project>();
        for (File file : this.getBase().listFiles()) {
            Project p;
            if (!new File(file, "bnd.bnd").isFile() || (p = this.getProject(file.getAbsoluteFile().getName())) == null) continue;
            projects.add(p);
        }
        return projects;
    }

    public void changedFile(File f) {
        List<BndListener> listeners = this.getPlugins(BndListener.class);
        for (BndListener l : listeners) {
            try {
                l.changed(f);
            }
            catch (Exception e) {
                logger.debug("Exception in a BndListener changedFile method call", (Throwable)e);
            }
        }
    }

    public void bracket(boolean begin) {
        List<BndListener> listeners = this.getPlugins(BndListener.class);
        for (BndListener l : listeners) {
            try {
                if (begin) {
                    l.begin();
                    continue;
                }
                l.end();
            }
            catch (Exception e) {
                if (begin) {
                    logger.debug("Exception in a BndListener begin method call", (Throwable)e);
                    continue;
                }
                logger.debug("Exception in a BndListener end method call", (Throwable)e);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void signal(Reporter reporter) {
        if (this.signalBusy.get() != null) {
            return;
        }
        this.signalBusy.set(reporter);
        try {
            List<BndListener> listeners = this.getPlugins(BndListener.class);
            for (BndListener l : listeners) {
                try {
                    l.signal(this);
                }
                catch (Exception e) {
                    logger.debug("Exception in a BndListener signal method call", (Throwable)e);
                }
            }
        }
        catch (Exception exception) {
        }
        finally {
            this.signalBusy.set(null);
        }
    }

    @Override
    public void signal() {
        this.signal(this);
    }

    public void syncCache() throws Exception {
        CachedFileRepo cf = new CachedFileRepo();
        cf.init();
        cf.close();
    }

    public List<RepositoryPlugin> getRepositories() throws Exception {
        if (this.data.repositories == null) {
            this.data.repositories = this.getPlugins(RepositoryPlugin.class);
            for (RepositoryPlugin repo : this.data.repositories) {
                if (!(repo instanceof Prepare)) continue;
                ((Prepare)((Object)repo)).prepare();
            }
        }
        return this.data.repositories;
    }

    public Collection<Project> getBuildOrder() throws Exception {
        ArrayList<Project> result = new ArrayList<Project>();
        for (Project project : this.getAllProjects()) {
            Collection<Project> dependsOn = project.getDependson();
            this.getBuildOrder(dependsOn, result);
            if (result.contains(project)) continue;
            result.add(project);
        }
        return result;
    }

    private void getBuildOrder(Collection<Project> dependsOn, List<Project> result) throws Exception {
        for (Project project : dependsOn) {
            Collection<Project> subProjects = project.getDependson();
            for (Project subProject : subProjects) {
                if (result.contains(subProject)) continue;
                result.add(subProject);
            }
            if (result.contains(project)) continue;
            result.add(project);
        }
    }

    public static Workspace getWorkspace(String path) throws Exception {
        File file = IO.getFile(new File(""), path);
        return Workspace.getWorkspace(file);
    }

    public Maven getMaven() {
        return this.maven;
    }

    @Override
    protected void setTypeSpecificPlugins(Set<Object> list) {
        try {
            super.setTypeSpecificPlugins(list);
            list.add(this);
            list.add(this.maven);
            list.add(this.settings);
            if (!Workspace.isTrue(this.getProperty("-nobuildincache"))) {
                CachedFileRepo repo = new CachedFileRepo();
                list.add(repo);
            }
            this.resourceRepositoryImpl = new ResourceRepositoryImpl();
            this.resourceRepositoryImpl.setCache(IO.getFile(this.getProperty(CACHEDIR, "~/.bnd/caches/shas")));
            this.resourceRepositoryImpl.setExecutor(Workspace.getExecutor());
            this.resourceRepositoryImpl.setIndexFile(Workspace.getFile(this.getBuildDir(), "repo.json"));
            this.resourceRepositoryImpl.setURLConnector(new MultiURLConnectionHandler(this));
            this.customize(this.resourceRepositoryImpl, null);
            list.add(this.resourceRepositoryImpl);
            list.add(new SubsystemExporter());
            try {
                HttpClient client = new HttpClient();
                client.setOffline(this.getOffline());
                client.setRegistry(this);
                try (ConnectionSettings cs = new ConnectionSettings(this, client);){
                    cs.readSettings();
                }
                list.add(client);
            }
            catch (Exception e) {
                this.exception(e, "Failed to load the communication settings", new Object[0]);
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void addExtensions(Set<Object> list) {
        Parameters extensions = this.getMergedParameters("-extension");
        HashMap<DownloadBlocker, Attrs> blockers = new HashMap<DownloadBlocker, Attrs>();
        for (Map.Entry<String, Attrs> entry : extensions.entrySet()) {
            String bsn = Workspace.removeDuplicateMarker(entry.getKey());
            String stringRange = entry.getValue().get("version");
            logger.debug("Adding extension {}-{}", (Object)bsn, (Object)stringRange);
            if (stringRange == null) {
                stringRange = Version.LOWEST.toString();
            } else if (!VersionRange.isVersionRange(stringRange)) {
                this.error("Invalid version range %s on extension %s", stringRange, bsn);
                continue;
            }
            try {
                SortedSet<SearchableRepository.ResourceDescriptor> matches = this.resourceRepositoryImpl.find(null, bsn, new VersionRange(stringRange));
                if (matches.isEmpty()) {
                    this.error("Extension %s;version=%s not found in base repo", bsn, stringRange);
                    continue;
                }
                DownloadBlocker blocker = new DownloadBlocker(this);
                blockers.put(blocker, entry.getValue());
                this.resourceRepositoryImpl.getResource(matches.last().id, blocker);
            }
            catch (Exception e) {
                this.error("Failed to load extension %s-%s, %s", bsn, stringRange, e);
            }
        }
        logger.debug("Found extensions {}", blockers);
        for (Map.Entry<String, Attrs> entry : blockers.entrySet()) {
            try {
                String reason = ((DownloadBlocker)((Object)entry.getKey())).getReason();
                if (reason != null) {
                    this.error("Extension load failed: %s", reason);
                    continue;
                }
                URLClassLoader cl = new URLClassLoader(new URL[]{((DownloadBlocker)((Object)entry.getKey())).getFile().toURI().toURL()}, this.getClass().getClassLoader());
                Enumeration<URL> manifests = cl.getResources("META-INF/MANIFEST.MF");
                while (manifests.hasMoreElements()) {
                    InputStream is = manifests.nextElement().openStream();
                    Throwable throwable = null;
                    try {
                        Manifest m = new Manifest(is);
                        Parameters activators = new Parameters(m.getMainAttributes().getValue("Extension-Activator"), this);
                        for (Map.Entry<String, Attrs> e : activators.entrySet()) {
                            try {
                                Class<?> c = cl.loadClass(e.getKey());
                                ExtensionActivator extensionActivator = (ExtensionActivator)c.getConstructor(new Class[0]).newInstance(new Object[0]);
                                this.customize(extensionActivator, entry.getValue());
                                List<?> plugins = extensionActivator.activate(this, entry.getValue());
                                list.add(extensionActivator);
                                if (plugins == null) continue;
                                for (Object plugin : plugins) {
                                    list.add(plugin);
                                }
                            }
                            catch (ClassNotFoundException cnfe) {
                                this.error("Loading extension %s, extension activator missing: %s (ignored)", entry, e.getKey());
                            }
                        }
                    }
                    catch (Throwable throwable2) {
                        throwable = throwable2;
                        throw throwable2;
                    }
                    finally {
                        if (is == null) continue;
                        if (throwable != null) {
                            try {
                                is.close();
                            }
                            catch (Throwable x2) {
                                throwable.addSuppressed(x2);
                            }
                            continue;
                        }
                        is.close();
                    }
                }
            }
            catch (Exception e) {
                this.error("failed to install extension %s due to %s", entry, e);
            }
        }
    }

    public boolean isOffline() {
        return this.offline.get();
    }

    public AtomicBoolean getOffline() {
        return this.offline;
    }

    public Workspace setOffline(boolean on) {
        this.offline.set(on);
        return this;
    }

    public String _global(String[] args) throws Exception {
        Macro.verifyCommand(args, "${global;<name>[;<default>]}, get a global setting from ~/.bnd/settings.json", null, 2, 3);
        String key = args[1];
        if (key.equals("key.public")) {
            return Hex.toHexString(this.settings.getPublicKey());
        }
        if (key.equals("key.private")) {
            return Hex.toHexString(this.settings.getPrivateKey());
        }
        String s = this.settings.get(key);
        if (s != null) {
            return s;
        }
        if (args.length == 3) {
            return args[2];
        }
        return null;
    }

    public String _user(String[] args) throws Exception {
        return this._global(args);
    }

    public Object _repodigests(String[] args) throws Exception {
        Macro.verifyCommand(args, "${repodigests;[;<repo names>]...}, get the repository digests", null, 1, 10000);
        List<RepositoryPlugin> repos = this.getRepositories();
        if (args.length > 1) {
            Iterator<RepositoryPlugin> it = repos.iterator();
            block2: while (it.hasNext()) {
                String name = it.next().getName();
                for (int i = 1; i < args.length; ++i) {
                    if (name.equals(args[i])) continue block2;
                }
                it.remove();
            }
        }
        ArrayList<String> digests = new ArrayList<String>();
        for (RepositoryPlugin repo : repos) {
            try {
                if (repo instanceof RepositoryDigest) {
                    byte[] digest = ((RepositoryDigest)((Object)repo)).getDigest();
                    digests.add(Hex.toHexString(digest));
                    continue;
                }
                if (args.length == 1) continue;
                this.error("Specified repo %s for ${repodigests} was named but it is not found", repo.getName());
            }
            catch (Exception e) {
                if (args.length == 1) continue;
                this.error("Specified repo %s for digests is not found", repo.getName());
            }
        }
        return Workspace.join(digests, ",");
    }

    public static Run getRun(File file) throws Exception {
        if (!file.isFile()) {
            return null;
        }
        File projectDir = file.getParentFile();
        File workspaceDir = projectDir.getParentFile();
        if (!workspaceDir.isDirectory()) {
            return null;
        }
        Workspace ws = Workspace.getWorkspaceWithoutException(workspaceDir);
        if (ws == null) {
            return null;
        }
        return new Run(ws, projectDir, file);
    }

    @Override
    public void report(Map<String, Object> table) throws Exception {
        super.report(table);
        table.put("Workspace", this.toString());
        table.put("Plugins", this.getPlugins(Object.class));
        table.put("Repos", this.getRepositories());
        table.put("Projects in build order", this.getBuildOrder());
    }

    public File getCache(String name) {
        return Workspace.getFile(this.buildDir, CACHEDIR + "/" + name);
    }

    public WorkspaceRepository getWorkspaceRepository() {
        return this.workspaceRepo;
    }

    public void checkStructure() {
        if (!this.getBuildDir().isDirectory()) {
            this.error("No directory for cnf %s", this.getBuildDir());
        } else {
            File build = IO.getFile(this.getBuildDir(), BUILDFILE);
            if (build.isFile()) {
                this.error("No %s file in %s", BUILDFILE, this.getBuildDir());
            }
        }
    }

    public File getBuildDir() {
        return this.buildDir;
    }

    public void setBuildDir(File buildDir) {
        this.buildDir = buildDir;
    }

    public boolean isValid() {
        return IO.getFile(this.getBuildDir(), BUILDFILE).isFile();
    }

    public RepositoryPlugin getRepository(String repo) throws Exception {
        for (RepositoryPlugin r : this.getRepositories()) {
            if (!repo.equals(r.getName())) continue;
            return r;
        }
        return null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void close() {
        Map<File, WeakReference<Workspace>> map = cache;
        synchronized (map) {
            WeakReference<Workspace> wsr = cache.get(this.getBase());
            if (wsr != null && wsr.get() == this) {
                cache.remove(this.getBase());
            }
        }
        try {
            super.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }

    public String getDriver() {
        if (this.driver == null) {
            this.driver = this.getProperty("-bnd-driver", null);
            if (this.driver != null) {
                this.driver = this.driver.trim();
            }
        }
        if (this.driver != null) {
            return this.driver;
        }
        return overallDriver;
    }

    public static void setDriver(String driver) {
        overallDriver = driver;
    }

    public String _driver(String[] args) {
        if (args.length == 1) {
            return this.getDriver();
        }
        String driver = this.getDriver();
        if (driver == null) {
            driver = this.getProperty("-bnd-driver");
        }
        if (driver != null) {
            for (int i = 1; i < args.length; ++i) {
                if (!args[i].equalsIgnoreCase(driver)) continue;
                return driver;
            }
        }
        return "";
    }

    public static void addGestalt(String part, Attrs attrs) {
        Attrs already = overallGestalt.get(part);
        if (attrs == null) {
            attrs = new Attrs();
        }
        if (already != null) {
            already.putAll(attrs);
        } else {
            already = attrs;
        }
        overallGestalt.put(part, already);
    }

    public Attrs getGestalt(String part) {
        return this.getGestalt().get(part);
    }

    public Parameters getGestalt() {
        if (this.gestalt == null) {
            this.gestalt = this.getMergedParameters("-gestalt");
            this.gestalt.mergeWith(overallGestalt, false);
        }
        return this.gestalt;
    }

    public WorkspaceLayout getLayout() {
        return this.layout;
    }

    public String _gestalt(String[] args) {
        if (args.length >= 2) {
            Attrs attrs = this.getGestalt(args[1]);
            if (attrs == null) {
                return "";
            }
            if (args.length == 2) {
                return args[1];
            }
            String s = attrs.get(args[2]);
            if (args.length == 3) {
                if (s == null) {
                    s = "";
                }
                return s;
            }
            if (args.length == 4) {
                if (args[3].equals(s)) {
                    return s;
                }
                return "";
            }
        }
        throw new IllegalArgumentException("${gestalt;<part>[;key[;<value>]]} has too many arguments");
    }

    @Override
    public String toString() {
        return "Workspace [" + this.getBase().getName() + "]";
    }

    public Project createProject(String name) throws Exception {
        if (!Verifier.SYMBOLICNAME.matcher(name).matches()) {
            this.error("A project name is a Bundle Symbolic Name, this must therefore consist of only letters, digits and dots", new Object[0]);
            return null;
        }
        File pdir = this.getFile(name);
        IO.mkdirs(pdir);
        IO.store((Object)("#\n#   " + name.toUpperCase().replace('.', ' ') + "\n#\n"), Workspace.getFile(pdir, "bnd.bnd"));
        Project p = new Project(this, pdir);
        IO.mkdirs(p.getTarget());
        IO.mkdirs(p.getOutput());
        IO.mkdirs(p.getTestOutput());
        for (File dir : p.getSourcePath()) {
            IO.mkdirs(dir);
        }
        IO.mkdirs(p.getTestSrc());
        for (LifeCyclePlugin l : this.getPlugins(LifeCyclePlugin.class)) {
            l.created(p);
        }
        if (!p.isValid()) {
            this.error("project %s is not valid", p);
        }
        return p;
    }

    public static Workspace createWorkspace(File wsdir) throws Exception {
        if (wsdir.exists()) {
            return null;
        }
        IO.mkdirs(wsdir);
        File cnf = IO.getFile(wsdir, CNFDIR);
        IO.mkdirs(cnf);
        IO.store((Object)"", new File(cnf, BUILDFILE));
        IO.store((Object)"-nobundles: true\n", new File(cnf, "bnd.bnd"));
        File ext = new File(cnf, EXT);
        IO.mkdirs(ext);
        Workspace ws = Workspace.getWorkspace(wsdir);
        return ws;
    }

    public boolean addPlugin(Class<?> plugin, String alias, Map<String, String> parameters, boolean force) throws Exception {
        BndPlugin ann = plugin.getAnnotation(BndPlugin.class);
        if (alias == null) {
            if (ann != null) {
                alias = ann.name();
            } else {
                alias = Strings.getLastSegment(plugin.getName()).toLowerCase();
                if (alias.endsWith("plugin")) {
                    alias = alias.substring(0, alias.length() - "plugin".length());
                }
            }
        }
        if (!Verifier.isBsn(alias)) {
            this.error("Not a valid plugin name %s", alias);
        }
        File ext = this.getFile("cnf/ext");
        IO.mkdirs(ext);
        File f = new File(ext, alias + ".bnd");
        if (!force) {
            if (f.exists()) {
                this.error("Plugin %s already exists", alias);
                return false;
            }
        } else {
            IO.delete(f);
        }
        Object l = plugin.getConstructor(new Class[0]).newInstance(new Object[0]);
        try (Formatter setup = new Formatter();){
            setup.format("#\n# Plugin %s setup\n#\n", alias);
            setup.format("-plugin.%s = %s", alias, plugin.getName());
            for (Map.Entry<String, String> e : parameters.entrySet()) {
                setup.format("; \\\n \t%s = '%s'", e.getKey(), this.escaped(e.getValue()));
            }
            setup.format("\n\n", new Object[0]);
            String out = setup.toString();
            if (l instanceof LifeCyclePlugin) {
                out = ((LifeCyclePlugin)l).augmentSetup(out, alias, parameters);
                ((LifeCyclePlugin)l).init(this);
            }
            logger.debug("setup {}", (Object)out);
            IO.store((Object)out, f);
        }
        this.refresh();
        for (LifeCyclePlugin lp : this.getPlugins(LifeCyclePlugin.class)) {
            lp.addedPlugin(this, plugin.getName(), alias, parameters);
        }
        return true;
    }

    private Object escaped(String value) {
        Matcher matcher = ESCAPE_P.matcher(value);
        if (matcher.matches()) {
            value = matcher.group(2);
        }
        return value.replaceAll("'", "\\'");
    }

    public boolean removePlugin(String alias) {
        File ext = this.getFile("cnf/ext");
        File f = new File(ext, alias + ".bnd");
        if (!f.exists()) {
            this.error("No such plugin %s", alias);
            return false;
        }
        IO.delete(f);
        this.refresh();
        return true;
    }

    public static Workspace createStandaloneWorkspace(Processor run, URI base) throws Exception {
        Workspace ws = new Workspace(WorkspaceLayout.STANDALONE);
        for (Map.Entry<Object, Object> entry : run.getProperties().entrySet()) {
            String key = (String)entry.getKey();
            if (key.startsWith(PLUGIN_STANDALONE)) continue;
            ws.getProperties().put(key, entry.getValue());
        }
        Parameters standalone = new Parameters(ws.getProperty("-standalone"), ws);
        StringBuilder sb = new StringBuilder();
        try (Formatter f = new Formatter(sb, Locale.US);){
            int counter = 1;
            for (Map.Entry<String, Attrs> e : standalone.entrySet()) {
                String locationStr = e.getKey();
                if ("true".equalsIgnoreCase(locationStr)) {
                    break;
                }
                URI resolvedLocation = URIUtil.resolve(base, locationStr);
                String key = f.format("%s%02d", PLUGIN_STANDALONE, counter).toString();
                sb.setLength(0);
                Attrs attrs = e.getValue();
                String name = attrs.get("name");
                if (name == null) {
                    name = String.format("repo%02d", counter);
                }
                f.format("%s; name='%s'; locations='%s'", STANDALONE_REPO_CLASS, name, resolvedLocation);
                for (Map.Entry<String, String> attribEntry : attrs.entrySet()) {
                    if ("name".equals(attribEntry.getKey())) continue;
                    f.format("; %s='%s'", attribEntry.getKey(), attribEntry.getValue());
                }
                String value = f.toString();
                sb.setLength(0);
                ws.setProperty(key, value);
                ++counter;
            }
        }
        return ws;
    }

    public boolean isDefaultWorkspace() {
        return BND_DEFAULT_WS.equals(this.getBase());
    }

    class CachedFileRepo
    extends FileRepo {
        final Lock lock;
        boolean inited;

        CachedFileRepo() {
            super(Workspace.BND_CACHE_REPONAME, Workspace.this.getCache(Workspace.BND_CACHE_REPONAME), false);
            this.lock = new ReentrantLock();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        protected boolean init() throws Exception {
            if (!this.lock.tryLock(50L, TimeUnit.SECONDS)) {
                throw new TimeLimitExceededException("Cached File Repo is locked and can't acquire it");
            }
            try {
                if (super.init()) {
                    this.inited = true;
                    IO.mkdirs(this.root);
                    if (!this.root.isDirectory()) {
                        throw new IllegalArgumentException("Cache directory " + this.root + " not a directory");
                    }
                    try (InputStream in = this.getClass().getResourceAsStream("/embedded-repo.jar");){
                        if (in != null) {
                            this.unzip(in, this.root);
                            boolean bl = true;
                            return bl;
                        }
                    }
                    StringTokenizer classPathTokenizer = new StringTokenizer(System.getProperty("java.class.path", ""), File.pathSeparator);
                    while (classPathTokenizer.hasMoreTokens()) {
                        String classPathEntry = classPathTokenizer.nextToken().trim();
                        if (!Workspace.this.EMBEDDED_REPO_TESTING_PATTERN.matcher(classPathEntry).matches()) continue;
                        try (InputStream in = IO.stream(Paths.get(classPathEntry, new String[0]));){
                            this.unzip(in, this.root);
                            boolean bl = true;
                            return bl;
                        }
                    }
                    Workspace.this.error("Couldn't find biz.aQute.bnd.embedded-repo on the classpath", new Object[0]);
                    boolean bl = false;
                    return bl;
                }
                boolean bl = false;
                return bl;
            }
            finally {
                this.lock.unlock();
            }
        }

        private void unzip(InputStream in, File dir) throws Exception {
            try (JarInputStream jin = new JarInputStream(in);){
                byte[] data = new byte[65536];
                JarEntry jentry = jin.getNextJarEntry();
                while (jentry != null) {
                    String jentryName;
                    if (!jentry.isDirectory() && !(jentryName = jentry.getName()).startsWith("META-INF/")) {
                        File dest = Processor.getFile(dir, jentryName);
                        long modifiedTime = ZipUtil.getModifiedTime(jentry);
                        if (!dest.isFile() || dest.lastModified() < modifiedTime || modifiedTime <= 0L) {
                            File dp = dest.getParentFile();
                            IO.mkdirs(dp);
                            try (OutputStream out = IO.outputStream(dest);){
                                int size = jin.read(data);
                                while (size > 0) {
                                    out.write(data, 0, size);
                                    size = jin.read(data);
                                }
                            }
                        }
                    }
                    jentry = jin.getNextJarEntry();
                }
            }
        }
    }

    static class WorkspaceData {
        List<RepositoryPlugin> repositories;

        WorkspaceData() {
        }
    }
}

