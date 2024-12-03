/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package aQute.lib.deployer;

import aQute.bnd.annotation.plugin.BndPlugin;
import aQute.bnd.osgi.Instruction;
import aQute.bnd.osgi.Jar;
import aQute.bnd.osgi.Processor;
import aQute.bnd.service.Actionable;
import aQute.bnd.service.Plugin;
import aQute.bnd.service.Refreshable;
import aQute.bnd.service.Registry;
import aQute.bnd.service.RegistryPlugin;
import aQute.bnd.service.RepositoryListenerPlugin;
import aQute.bnd.service.RepositoryPlugin;
import aQute.bnd.service.repository.SearchableRepository;
import aQute.bnd.version.Version;
import aQute.lib.collections.SortedList;
import aQute.lib.hex.Hex;
import aQute.lib.io.IO;
import aQute.lib.json.JSONCodec;
import aQute.lib.persistentmap.PersistentMap;
import aQute.libg.command.Command;
import aQute.libg.cryptography.SHA1;
import aQute.libg.cryptography.SHA256;
import aQute.libg.reporter.ReporterAdapter;
import aQute.service.reporter.Reporter;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.jar.Manifest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@BndPlugin(name="filerepo", parameters=Config.class)
public class FileRepo
implements Plugin,
RepositoryPlugin,
Refreshable,
RegistryPlugin,
Actionable,
Closeable {
    private static final Logger logger = LoggerFactory.getLogger(FileRepo.class);
    public static final String TRACE = "trace";
    public static final String LOCATION = "location";
    public static final String READONLY = "readonly";
    public static final String LATEST_OPTION = "latest";
    public static final String NAME = "name";
    public static final String INDEX = "index";
    public static final String CMD_PATH = "cmd.path";
    public static final String CMD_SHELL = "cmd.shell";
    public static final String CMD_INIT = "cmd.init";
    public static final String CMD_OPEN = "cmd.open";
    public static final String CMD_AFTER_PUT = "cmd.after.put";
    public static final String CMD_REFRESH = "cmd.refresh";
    public static final String CMD_BEFORE_PUT = "cmd.before.put";
    public static final String CMD_ABORT_PUT = "cmd.abort.put";
    public static final String CMD_CLOSE = "cmd.close";
    public static final String CMD_AFTER_ACTION = "cmd.after.action";
    public static final String CMD_BEFORE_GET = "cmd.before.get";
    static final RepositoryPlugin.PutOptions DEFAULTOPTIONS = new RepositoryPlugin.PutOptions();
    public static final int MAX_MAJOR = 999999999;
    private static final String LATEST_POSTFIX = "-latest.jar";
    public static final Version LATEST_VERSION = new Version(999999999, 0, 0);
    private static final SortedSet<Version> LATEST_SET = new TreeSet<Version>(Collections.singleton(LATEST_VERSION));
    static final JSONCodec codec = new JSONCodec();
    String shell;
    String path;
    String init;
    String open;
    String refresh;
    String beforePut;
    String afterPut;
    String abortPut;
    String beforeGet;
    String close;
    String action;
    File[] EMPTY_FILES = new File[0];
    protected File root;
    Registry registry;
    boolean createLatest = true;
    boolean canWrite = true;
    Pattern REPO_FILE = Pattern.compile("(?:([-a-zA-z0-9_\\.]+)-)((\\d{1,9})(\\.(\\d{1,9})(\\.(\\d{1,9})(\\.([-_\\da-zA-Z]+))?)?)?|latest)\\.(jar|lib)");
    Reporter reporter;
    boolean dirty = true;
    String name;
    boolean inited;
    boolean trace;
    PersistentMap<SearchableRepository.ResourceDescriptor> index;
    private boolean hasIndex;
    private static String[] names = new String[]{"bytes", "Kb", "Mb", "Gb"};

    public FileRepo() {
    }

    public FileRepo(String name, File location, boolean canWrite) {
        this.name = name;
        this.root = location;
        this.canWrite = canWrite;
    }

    protected boolean init() throws Exception {
        if (this.inited) {
            return false;
        }
        this.inited = true;
        if (this.reporter == null) {
            ReporterAdapter reporter = this.trace ? new ReporterAdapter(System.out) : new ReporterAdapter();
            reporter.setTrace(this.trace);
            reporter.setExceptions(this.trace);
            this.reporter = reporter;
        }
        logger.debug("init");
        if (!this.root.isDirectory()) {
            IO.mkdirs(this.root);
            if (!this.root.isDirectory()) {
                throw new IllegalArgumentException("Location cannot be turned into a directory " + this.root);
            }
            this.exec(this.init, this.root.getAbsolutePath());
        }
        if (this.hasIndex) {
            this.index = new PersistentMap<SearchableRepository.ResourceDescriptor>(new File(this.root, ".index"), SearchableRepository.ResourceDescriptor.class);
        }
        this.open();
        return true;
    }

    @Override
    public void setProperties(Map<String, String> map) {
        String createLatest;
        String location = map.get(LOCATION);
        if (location == null) {
            throw new IllegalArgumentException("Location must be set on a FileRepo plugin");
        }
        this.root = IO.getFile(IO.home, location);
        String readonly = map.get(READONLY);
        if (readonly != null) {
            boolean bl = this.canWrite = Boolean.valueOf(readonly) == false;
        }
        if ((createLatest = map.get(LATEST_OPTION)) != null) {
            this.createLatest = Boolean.valueOf(createLatest);
        }
        this.hasIndex = Processor.isTrue(map.get(INDEX));
        this.name = map.get(NAME);
        this.path = map.get(CMD_PATH);
        this.shell = map.get(CMD_SHELL);
        this.init = map.get(CMD_INIT);
        this.open = map.get(CMD_OPEN);
        this.refresh = map.get(CMD_REFRESH);
        this.beforePut = map.get(CMD_BEFORE_PUT);
        this.abortPut = map.get(CMD_ABORT_PUT);
        this.afterPut = map.get(CMD_AFTER_PUT);
        this.beforeGet = map.get(CMD_BEFORE_GET);
        this.close = map.get(CMD_CLOSE);
        this.action = map.get(CMD_AFTER_ACTION);
        this.trace = map.get(TRACE) != null && Boolean.parseBoolean(map.get(TRACE));
    }

    @Override
    public boolean canWrite() {
        return this.canWrite;
    }

    protected File putArtifact(File tmpFile, byte[] digest) throws Exception {
        return this.putArtifact(tmpFile, null, digest);
    }

    protected File putArtifact(File tmpFile, RepositoryPlugin.PutOptions options, byte[] digest) throws Exception {
        assert (tmpFile != null);
        try (Jar tmpJar = new Jar(tmpFile);){
            String bsn = null;
            bsn = options != null && options.bsn != null ? options.bsn : tmpJar.getBsn();
            if (bsn == null) {
                throw new IllegalArgumentException("No bsn set in jar: " + tmpFile);
            }
            Version version = null;
            if (options != null && options.version != null) {
                version = options.version;
            } else {
                try {
                    version = new Version(tmpJar.getVersion());
                }
                catch (Exception e) {
                    throw new IllegalArgumentException("Incorrect version in : " + tmpFile + " " + tmpJar.getVersion());
                }
            }
            if (version == null) {
                version = Version.LOWEST;
            }
            logger.debug("bsn={} version={}", (Object)bsn, (Object)version);
            File dir = new File(this.root, bsn);
            IO.mkdirs(dir);
            if (!dir.isDirectory()) {
                throw new IOException("Could not create directory " + dir);
            }
            String fName = bsn + "-" + version.getWithoutQualifier() + ".jar";
            File file = new File(dir, fName);
            logger.debug("updating {}", (Object)file.getAbsolutePath());
            if (this.hasIndex) {
                this.index.put(bsn + "-" + version.getWithoutQualifier(), this.buildDescriptor(tmpFile, tmpJar, digest, bsn, version));
            }
            tmpJar.close();
            this.dirty = true;
            if (file.isFile() && !file.canWrite()) {
                file.setWritable(true);
            }
            IO.rename(tmpFile, file);
            this.fireBundleAdded(file);
            this.afterPut(file, bsn, version, Hex.toHexString(digest));
            if (this.createLatest) {
                File latest = new File(dir, bsn + LATEST_POSTFIX);
                IO.copy(file, latest);
            }
            logger.debug("updated {}", (Object)file.getAbsolutePath());
            File file2 = file;
            return file2;
        }
    }

    /*
     * Exception decompiling
     */
    @Override
    public RepositoryPlugin.PutResult put(InputStream stream, RepositoryPlugin.PutOptions options) throws Exception {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public void setLocation(String string) {
        this.root = IO.getFile(string);
    }

    @Override
    public void setReporter(Reporter reporter) {
        this.reporter = reporter;
    }

    @Override
    public List<String> list(String regex) throws Exception {
        this.init();
        Instruction pattern = null;
        if (regex != null) {
            pattern = new Instruction(regex);
        }
        ArrayList<String> result = new ArrayList<String>();
        if (this.root == null) {
            if (this.reporter != null) {
                this.reporter.error("FileRepo root directory is not set.", new Object[0]);
            }
        } else {
            File[] list = this.root.listFiles();
            if (list != null) {
                for (File f : list) {
                    String fileName;
                    if (!f.isDirectory() || (fileName = f.getName()).charAt(0) == '.' || pattern != null && !pattern.matches(fileName)) continue;
                    result.add(fileName);
                }
            } else if (this.reporter != null) {
                this.reporter.error("FileRepo root directory (%s) does not exist", this.root);
            }
        }
        return result;
    }

    @Override
    public SortedSet<Version> versions(String bsn) throws Exception {
        this.init();
        File dir = new File(this.root, bsn);
        boolean latest = false;
        if (dir.isDirectory()) {
            String[] versions = dir.list();
            ArrayList<Version> list = new ArrayList<Version>();
            for (String v : versions) {
                Matcher m = this.REPO_FILE.matcher(v);
                if (!m.matches()) continue;
                String version = m.group(2);
                if (!version.equals(LATEST_OPTION)) {
                    list.add(new Version(version));
                    continue;
                }
                latest = true;
            }
            if (list.isEmpty() && latest) {
                return LATEST_SET;
            }
            return new SortedList<Version>(list);
        }
        return SortedList.empty();
    }

    public String toString() {
        return String.format("%s [%-40s r/w=%s]", this.getName(), this.getRoot().getAbsolutePath(), this.canWrite());
    }

    @Override
    public File getRoot() {
        return this.root;
    }

    @Override
    public boolean refresh() throws Exception {
        this.init();
        this.exec(this.refresh, this.root);
        this.rebuildIndex();
        return true;
    }

    @Override
    public String getName() {
        if (this.name == null) {
            return this.getLocation();
        }
        return this.name;
    }

    @Override
    public File get(String bsn, Version version, Map<String, String> properties, RepositoryPlugin.DownloadListener ... listeners) throws Exception {
        this.init();
        this.beforeGet(bsn, version);
        File file = this.getLocal(bsn, version, properties);
        if (file.exists()) {
            for (RepositoryPlugin.DownloadListener l : listeners) {
                try {
                    l.success(file);
                }
                catch (Exception e) {
                    this.reporter.exception(e, "Download listener for %s", file);
                }
            }
            return file;
        }
        return null;
    }

    @Override
    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    @Override
    public String getLocation() {
        return this.root.toString();
    }

    @Override
    public Map<String, Runnable> actions(Object ... target) throws Exception {
        if (target == null || target.length == 0) {
            LinkedHashMap<String, Runnable> actions = new LinkedHashMap<String, Runnable>();
            actions.put("Rebuild Resource Index", new Runnable(){

                @Override
                public void run() {
                    try {
                        FileRepo.this.refresh();
                    }
                    catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return actions;
        }
        try {
            String bsn = (String)target[0];
            Version version = (Version)target[1];
            final File f = this.get(bsn, version, null, new RepositoryPlugin.DownloadListener[0]);
            if (f == null) {
                return null;
            }
            HashMap<String, Runnable> actions = new HashMap<String, Runnable>();
            actions.put("Delete " + bsn + "-" + this.status(bsn, version), new Runnable(){

                @Override
                public void run() {
                    IO.delete(f);
                    if (f.getParentFile().list().length == 0) {
                        IO.delete(f.getParentFile());
                    }
                    FileRepo.this.afterAction(f, "delete");
                }
            });
            return actions;
        }
        catch (Exception e) {
            return null;
        }
    }

    protected void afterAction(File f, String key) {
        this.exec(this.action, this.root, f, key);
    }

    @Override
    public String tooltip(Object ... target) throws Exception {
        if (target == null || target.length == 0) {
            return String.format("%s\n%s", this.getName(), this.root);
        }
        try {
            String bsn = (String)target[0];
            Version version = (Version)target[1];
            Map map = null;
            if (target.length > 2) {
                map = (Map)target[2];
            }
            File f = this.getLocal(bsn, version, map);
            String s = "";
            SearchableRepository.ResourceDescriptor descriptor = this.getDescriptor(bsn, version);
            if (descriptor != null && descriptor.description != null) {
                s = descriptor.description + "\n";
            }
            s = s + String.format("Path: %s\nSize: %s\nSHA1: %s", f.getAbsolutePath(), this.readable(f.length(), 0), SHA1.digest(f).asHex());
            if (f.getName().endsWith(".lib") && f.isFile()) {
                s = s + "\n" + IO.collect(f);
            }
            return s;
        }
        catch (Exception e) {
            return null;
        }
    }

    @Override
    public String title(Object ... target) throws Exception {
        if (target == null || target.length == 0) {
            return this.getName();
        }
        if (target.length == 1 && target[0] instanceof String) {
            return (String)target[0];
        }
        if (target.length == 2 && target[0] instanceof String && target[1] instanceof Version) {
            return this.status((String)target[0], (Version)target[1]);
        }
        return null;
    }

    protected File getLocal(String bsn, Version version, Map<String, String> properties) {
        File fjar;
        File dir = new File(this.root, bsn);
        if (LATEST_VERSION.equals(version) && (fjar = new File(dir, bsn + LATEST_POSTFIX)).isFile()) {
            return fjar.getAbsoluteFile();
        }
        fjar = new File(dir, bsn + "-" + version.getWithoutQualifier() + ".jar");
        if (fjar.isFile()) {
            return fjar.getAbsoluteFile();
        }
        File sfjar = new File(dir, version.getWithoutQualifier() + ".jar");
        if (sfjar.isFile()) {
            return sfjar.getAbsoluteFile();
        }
        File flib = new File(dir, bsn + "-" + version.getWithoutQualifier() + ".lib");
        if (flib.isFile()) {
            return flib.getAbsoluteFile();
        }
        File sflib = new File(dir, version.getWithoutQualifier() + ".lib");
        if (sflib.isFile()) {
            return sflib.getAbsoluteFile();
        }
        return fjar.getAbsoluteFile();
    }

    protected String status(String bsn, Version version) {
        File file = this.getLocal(bsn, version, null);
        String vs = LATEST_VERSION.equals(version) ? LATEST_OPTION : version.toString();
        StringBuilder sb = new StringBuilder(vs);
        String del = " [";
        if (file.getName().endsWith(".lib")) {
            sb.append(del).append("L");
            del = "";
        } else if (!file.getName().endsWith(".jar")) {
            sb.append(del).append("?");
            del = "";
        }
        if (!file.isFile()) {
            sb.append(del).append("X");
            del = "";
        }
        if (file.length() == 0L) {
            sb.append(del).append("0");
            del = "";
        }
        if (del.equals("")) {
            sb.append("]");
        }
        return sb.toString();
    }

    private Object readable(long length, int n) {
        if (length < 0L) {
            return "<invalid>";
        }
        if (length < 1024L || n >= names.length) {
            return length + names[n];
        }
        return this.readable(length / 1024L, n + 1);
    }

    @Override
    public void close() throws IOException {
        if (this.inited) {
            this.exec(this.close, this.root.getAbsolutePath());
            if (this.hasIndex) {
                this.index.close();
            }
        }
    }

    protected void open() {
        this.exec(this.open, this.root.getAbsolutePath());
    }

    protected void beforePut(File tmp) {
        this.exec(this.beforePut, this.root.getAbsolutePath(), tmp.getAbsolutePath());
    }

    protected void afterPut(File file, String bsn, Version version, String sha) {
        this.exec(this.afterPut, this.root.getAbsolutePath(), file.getAbsolutePath(), sha);
    }

    protected void abortPut(File tmpFile) {
        this.exec(this.abortPut, this.root.getAbsolutePath(), tmpFile.getAbsolutePath());
    }

    protected void beforeGet(String bsn, Version version) {
        this.exec(this.beforeGet, this.root.getAbsolutePath(), bsn, version);
    }

    protected void fireBundleAdded(File file) throws Exception {
        if (this.registry == null) {
            return;
        }
        List<RepositoryListenerPlugin> listeners = this.registry.getPlugins(RepositoryListenerPlugin.class);
        if (listeners.isEmpty()) {
            return;
        }
        try (Jar jar = new Jar(file);){
            for (RepositoryListenerPlugin listener : listeners) {
                try {
                    listener.bundleAdded(this, jar, file);
                }
                catch (Exception e) {
                    if (this.reporter == null) continue;
                    this.reporter.warning("Repository listener threw an unexpected exception: %s", e);
                }
            }
        }
    }

    void exec(String line, Object ... args) {
        if (line == null) {
            logger.debug("Line is empty, args={}", args == null ? new Object[]{} : args);
            return;
        }
        logger.debug("exec {}", (Object)line);
        try {
            if (args != null) {
                for (int i = 0; i < args.length; ++i) {
                    if (i == 0) {
                        line = line.replaceAll("\\$\\{@\\}", args[0].toString().replaceAll("\\\\", "\\\\\\\\"));
                    }
                    line = line.replaceAll("\\$" + i, args[i].toString().replaceAll("\\\\", "\\\\\\\\"));
                }
            }
            line = line.replaceAll("\\s*\\$[0-9]\\s*", "");
            int result = 0;
            StringBuilder stdout = new StringBuilder();
            StringBuilder stderr = new StringBuilder();
            if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
                Command cmd = new Command("cmd.exe /C " + line);
                cmd.setCwd(this.getRoot());
                result = cmd.execute(stdout, stderr);
            } else {
                if (this.shell == null) {
                    this.shell = "sh";
                }
                Command cmd = new Command(this.shell);
                cmd.setCwd(this.getRoot());
                if (this.path != null) {
                    cmd.inherit();
                    String oldpath = cmd.var("PATH");
                    this.path = this.path.replaceAll("\\s*,\\s*", File.pathSeparator);
                    this.path = this.path.replaceAll("\\$\\{@\\}", oldpath);
                    cmd.var("PATH", this.path);
                }
                result = cmd.execute(line, (Appendable)stdout, (Appendable)stderr);
            }
            if (result != 0) {
                this.reporter.error("Command %s failed with %s %s %s", line, result, stdout, stderr);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            this.reporter.exception(e, "%s", e);
        }
    }

    public void setDir(File repoDir) {
        this.root = repoDir;
    }

    public void delete(String bsn, Version version) throws Exception {
        this.init();
        assert (bsn != null);
        SortedSet<Version> versions = version == null ? this.versions(bsn) : new SortedList<Version>(version);
        for (Version v : versions) {
            File f = this.getLocal(bsn, version, null);
            if (!f.isFile()) {
                this.reporter.error("No artifact found for %s:%s", bsn, version);
                continue;
            }
            IO.delete(f);
        }
        if (this.versions(bsn).isEmpty()) {
            IO.delete(new File(this.root, bsn));
        }
        this.index.remove(bsn + "-" + version);
    }

    public SearchableRepository.ResourceDescriptor getDescriptor(String bsn, Version version) throws Exception {
        this.init();
        if (this.hasIndex) {
            SearchableRepository.ResourceDescriptor resourceDescriptor = (SearchableRepository.ResourceDescriptor)this.index.get(bsn + "-" + version);
            if (resourceDescriptor == null) {
                System.out.println("Keys " + this.index.keySet());
            }
            return resourceDescriptor;
        }
        return null;
    }

    public SortedSet<SearchableRepository.ResourceDescriptor> getResources() throws Exception {
        this.init();
        if (this.hasIndex) {
            TreeSet<SearchableRepository.ResourceDescriptor> resources = new TreeSet<SearchableRepository.ResourceDescriptor>(new Comparator<SearchableRepository.ResourceDescriptor>(){

                @Override
                public int compare(SearchableRepository.ResourceDescriptor a, SearchableRepository.ResourceDescriptor b) {
                    if (a == b) {
                        return 0;
                    }
                    int r = a.bsn.compareTo(b.bsn);
                    if (r != 0) {
                        return r;
                    }
                    if (a.version != b.version) {
                        if (a.version == null) {
                            return 1;
                        }
                        if (b.version == null) {
                            return -1;
                        }
                        r = a.version.compareTo(b.version);
                        if (r != 0) {
                            return r;
                        }
                    }
                    if (a.id.length > b.id.length) {
                        return 1;
                    }
                    if (a.id.length < b.id.length) {
                        return -1;
                    }
                    for (int i = 0; i < a.id.length; ++i) {
                        if (a.id[i] > b.id[i]) {
                            return 1;
                        }
                        if (a.id[i] >= b.id[i]) continue;
                        return 1;
                    }
                    return 0;
                }
            });
            for (SearchableRepository.ResourceDescriptor rd : this.index.values()) {
                resources.add(rd);
            }
            return resources;
        }
        return null;
    }

    public SearchableRepository.ResourceDescriptor getResource(byte[] sha) throws Exception {
        this.init();
        if (this.hasIndex) {
            for (SearchableRepository.ResourceDescriptor rd : this.index.values()) {
                if (!Arrays.equals(rd.id, sha)) continue;
                return rd;
            }
        }
        return null;
    }

    void rebuildIndex() throws Exception {
        this.init();
        if (!this.hasIndex || !this.dirty) {
            return;
        }
        this.index.clear();
        for (String bsn : this.list(null)) {
            for (Version version : this.versions(bsn)) {
                File f = this.get(bsn, version, null, new RepositoryPlugin.DownloadListener[0]);
                this.index.put(bsn + "-" + version, this.buildDescriptor(f, null, null, bsn, version));
            }
        }
        this.dirty = false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SearchableRepository.ResourceDescriptor buildDescriptor(File f, Jar jar, byte[] digest, String bsn, Version version) throws NoSuchAlgorithmException, Exception {
        this.init();
        Jar tmpjar = jar;
        if (jar == null) {
            tmpjar = new Jar(f);
        }
        try {
            Manifest m = tmpjar.getManifest();
            SearchableRepository.ResourceDescriptor rd = new SearchableRepository.ResourceDescriptor();
            rd.bsn = bsn;
            rd.version = version;
            rd.description = m.getMainAttributes().getValue("Bundle-Description");
            rd.id = digest;
            if (rd.id == null) {
                rd.id = SHA1.digest(f).digest();
            }
            rd.sha256 = SHA256.digest(f).digest();
            rd.url = f.toURI();
            SearchableRepository.ResourceDescriptor resourceDescriptor = rd;
            return resourceDescriptor;
        }
        finally {
            if (tmpjar != null) {
                tmpjar.close();
            }
        }
    }

    public void setIndex(boolean b) {
        this.hasIndex = b;
    }

    static interface Config {
        public String name();

        public String location();

        public boolean readonly();

        public boolean trace();

        public boolean index();

        public String cmd_path();

        public String cmd_shell();

        public String cmd_init();

        public String cmd_open();

        public String cmd_after_put();

        public String cmd_before_put();

        public String cmd_abort_put();

        public String cmd_before_get();

        public String cmd_after_action();

        public String cmd_refresh();

        public String cmd_close();
    }
}

