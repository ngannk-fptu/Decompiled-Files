/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.catalina.startup;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.catalina.security.SecurityClassLoad;
import org.apache.catalina.startup.CatalinaProperties;
import org.apache.catalina.startup.ClassLoaderFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;

public final class Bootstrap {
    private static final Log log;
    private static final Object daemonLock;
    private static volatile Bootstrap daemon;
    private static final File catalinaBaseFile;
    private static final File catalinaHomeFile;
    private static final Pattern PATH_PATTERN;
    private Object catalinaDaemon = null;
    ClassLoader commonLoader = null;
    ClassLoader catalinaLoader = null;
    ClassLoader sharedLoader = null;

    private void initClassLoaders() {
        try {
            this.commonLoader = this.createClassLoader("common", null);
            if (this.commonLoader == null) {
                this.commonLoader = this.getClass().getClassLoader();
            }
            this.catalinaLoader = this.createClassLoader("server", this.commonLoader);
            this.sharedLoader = this.createClassLoader("shared", this.commonLoader);
        }
        catch (Throwable t) {
            Bootstrap.handleThrowable(t);
            log.error((Object)"Class loader creation threw exception", t);
            System.exit(1);
        }
    }

    private ClassLoader createClassLoader(String name, ClassLoader parent) throws Exception {
        String[] repositoryPaths;
        String value = CatalinaProperties.getProperty(name + ".loader");
        if (value == null || value.equals("")) {
            return parent;
        }
        value = this.replace(value);
        ArrayList<ClassLoaderFactory.Repository> repositories = new ArrayList<ClassLoaderFactory.Repository>();
        for (String repository : repositoryPaths = Bootstrap.getPaths(value)) {
            try {
                URI uri = new URI(repository);
                URL url = uri.toURL();
                repositories.add(new ClassLoaderFactory.Repository(repository, ClassLoaderFactory.RepositoryType.URL));
            }
            catch (IllegalArgumentException | MalformedURLException | URISyntaxException exception) {
                if (repository.endsWith("*.jar")) {
                    repository = repository.substring(0, repository.length() - "*.jar".length());
                    repositories.add(new ClassLoaderFactory.Repository(repository, ClassLoaderFactory.RepositoryType.GLOB));
                    continue;
                }
                if (repository.endsWith(".jar")) {
                    repositories.add(new ClassLoaderFactory.Repository(repository, ClassLoaderFactory.RepositoryType.JAR));
                    continue;
                }
                repositories.add(new ClassLoaderFactory.Repository(repository, ClassLoaderFactory.RepositoryType.DIR));
            }
        }
        return ClassLoaderFactory.createClassLoader(repositories, parent);
    }

    protected String replace(String str) {
        String result = str;
        int pos_start = str.indexOf("${");
        if (pos_start >= 0) {
            StringBuilder builder = new StringBuilder();
            int pos_end = -1;
            while (pos_start >= 0) {
                builder.append(str, pos_end + 1, pos_start);
                pos_end = str.indexOf(125, pos_start + 2);
                if (pos_end < 0) {
                    pos_end = pos_start - 1;
                    break;
                }
                String propName = str.substring(pos_start + 2, pos_end);
                String replacement = propName.length() == 0 ? null : ("catalina.home".equals(propName) ? Bootstrap.getCatalinaHome() : ("catalina.base".equals(propName) ? Bootstrap.getCatalinaBase() : System.getProperty(propName)));
                if (replacement != null) {
                    builder.append(replacement);
                } else {
                    builder.append(str, pos_start, pos_end + 1);
                }
                pos_start = str.indexOf("${", pos_end + 1);
            }
            builder.append(str, pos_end + 1, str.length());
            result = builder.toString();
        }
        return result;
    }

    public void init() throws Exception {
        this.initClassLoaders();
        Thread.currentThread().setContextClassLoader(this.catalinaLoader);
        SecurityClassLoad.securityClassLoad(this.catalinaLoader);
        if (log.isDebugEnabled()) {
            log.debug((Object)"Loading startup class");
        }
        Class<?> startupClass = this.catalinaLoader.loadClass("org.apache.catalina.startup.Catalina");
        Object startupInstance = startupClass.getConstructor(new Class[0]).newInstance(new Object[0]);
        if (log.isDebugEnabled()) {
            log.debug((Object)"Setting startup class properties");
        }
        String methodName = "setParentClassLoader";
        Class[] paramTypes = new Class[]{Class.forName("java.lang.ClassLoader")};
        Object[] paramValues = new Object[]{this.sharedLoader};
        Method method = startupInstance.getClass().getMethod(methodName, paramTypes);
        method.invoke(startupInstance, paramValues);
        this.catalinaDaemon = startupInstance;
    }

    private void load(String[] arguments) throws Exception {
        Object[] param;
        Class[] paramTypes;
        String methodName = "load";
        if (arguments == null || arguments.length == 0) {
            paramTypes = null;
            param = null;
        } else {
            paramTypes = new Class[]{arguments.getClass()};
            param = new Object[]{arguments};
        }
        Method method = this.catalinaDaemon.getClass().getMethod(methodName, paramTypes);
        if (log.isDebugEnabled()) {
            log.debug((Object)("Calling startup class " + method));
        }
        method.invoke(this.catalinaDaemon, param);
    }

    private Object getServer() throws Exception {
        String methodName = "getServer";
        Method method = this.catalinaDaemon.getClass().getMethod(methodName, new Class[0]);
        return method.invoke(this.catalinaDaemon, new Object[0]);
    }

    public void init(String[] arguments) throws Exception {
        this.init();
        this.load(arguments);
    }

    public void start() throws Exception {
        if (this.catalinaDaemon == null) {
            this.init();
        }
        Method method = this.catalinaDaemon.getClass().getMethod("start", null);
        method.invoke(this.catalinaDaemon, (Object[])null);
    }

    public void stop() throws Exception {
        Method method = this.catalinaDaemon.getClass().getMethod("stop", null);
        method.invoke(this.catalinaDaemon, (Object[])null);
    }

    public void stopServer() throws Exception {
        Method method = this.catalinaDaemon.getClass().getMethod("stopServer", null);
        method.invoke(this.catalinaDaemon, (Object[])null);
    }

    public void stopServer(String[] arguments) throws Exception {
        Object[] param;
        Class[] paramTypes;
        if (arguments == null || arguments.length == 0) {
            paramTypes = null;
            param = null;
        } else {
            paramTypes = new Class[]{arguments.getClass()};
            param = new Object[]{arguments};
        }
        Method method = this.catalinaDaemon.getClass().getMethod("stopServer", paramTypes);
        method.invoke(this.catalinaDaemon, param);
    }

    public void setAwait(boolean await) throws Exception {
        Class[] paramTypes = new Class[]{Boolean.TYPE};
        Object[] paramValues = new Object[]{await};
        Method method = this.catalinaDaemon.getClass().getMethod("setAwait", paramTypes);
        method.invoke(this.catalinaDaemon, paramValues);
    }

    public boolean getAwait() throws Exception {
        Class[] paramTypes = new Class[]{};
        Object[] paramValues = new Object[]{};
        Method method = this.catalinaDaemon.getClass().getMethod("getAwait", paramTypes);
        Boolean b = (Boolean)method.invoke(this.catalinaDaemon, paramValues);
        return b;
    }

    public void destroy() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void main(String[] args) {
        Object object = daemonLock;
        synchronized (object) {
            if (daemon == null) {
                Bootstrap bootstrap = new Bootstrap();
                try {
                    bootstrap.init();
                }
                catch (Throwable t) {
                    Bootstrap.handleThrowable(t);
                    log.error((Object)"Init exception", t);
                    return;
                }
                daemon = bootstrap;
            } else {
                Thread.currentThread().setContextClassLoader(Bootstrap.daemon.catalinaLoader);
            }
        }
        try {
            String command = "start";
            if (args.length > 0) {
                command = args[args.length - 1];
            }
            if (command.equals("startd")) {
                args[args.length - 1] = "start";
                daemon.load(args);
                daemon.start();
            } else if (command.equals("stopd")) {
                args[args.length - 1] = "stop";
                daemon.stop();
            } else if (command.equals("start")) {
                daemon.setAwait(true);
                daemon.load(args);
                daemon.start();
                if (null == daemon.getServer()) {
                    System.exit(1);
                }
            } else if (command.equals("stop")) {
                daemon.stopServer(args);
            } else if (command.equals("configtest")) {
                daemon.load(args);
                if (null == daemon.getServer()) {
                    System.exit(1);
                }
                System.exit(0);
            } else {
                log.warn((Object)("Bootstrap: command \"" + command + "\" does not exist."));
            }
        }
        catch (Throwable t) {
            if (t instanceof InvocationTargetException && t.getCause() != null) {
                t = t.getCause();
            }
            Bootstrap.handleThrowable(t);
            log.error((Object)"Error running command", t);
            System.exit(1);
        }
    }

    public static String getCatalinaHome() {
        return catalinaHomeFile.getPath();
    }

    public static String getCatalinaBase() {
        return catalinaBaseFile.getPath();
    }

    public static File getCatalinaHomeFile() {
        return catalinaHomeFile;
    }

    public static File getCatalinaBaseFile() {
        return catalinaBaseFile;
    }

    static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath)t;
        }
        if (t instanceof StackOverflowError) {
            return;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError)t;
        }
    }

    static Throwable unwrapInvocationTargetException(Throwable t) {
        if (t instanceof InvocationTargetException && t.getCause() != null) {
            return t.getCause();
        }
        return t;
    }

    protected static String[] getPaths(String value) {
        ArrayList<String> result = new ArrayList<String>();
        Matcher matcher = PATH_PATTERN.matcher(value);
        while (matcher.find()) {
            String path = value.substring(matcher.start(), matcher.end());
            if ((path = path.trim()).length() == 0) continue;
            char first = path.charAt(0);
            char last = path.charAt(path.length() - 1);
            if (first == '\"' && last == '\"' && path.length() > 1) {
                path = path.substring(1, path.length() - 1);
                if ((path = path.trim()).length() == 0) {
                    continue;
                }
            } else if (path.contains("\"")) {
                throw new IllegalArgumentException("The double quote [\"] character can only be used to quote paths. It must not appear in a path. This loader path is not valid: [" + value + "]");
            }
            result.add(path);
        }
        return result.toArray(new String[0]);
    }

    static {
        File bootstrapJar;
        File f;
        log = LogFactory.getLog(Bootstrap.class);
        daemonLock = new Object();
        daemon = null;
        PATH_PATTERN = Pattern.compile("(\"[^\"]*\")|(([^,])*)");
        String userDir = System.getProperty("user.dir");
        String home = System.getProperty("catalina.home");
        File homeFile = null;
        if (home != null) {
            f = new File(home);
            try {
                homeFile = f.getCanonicalFile();
            }
            catch (IOException ioe) {
                homeFile = f.getAbsoluteFile();
            }
        }
        if (homeFile == null && (bootstrapJar = new File(userDir, "bootstrap.jar")).exists()) {
            File f2 = new File(userDir, "..");
            try {
                homeFile = f2.getCanonicalFile();
            }
            catch (IOException ioe) {
                homeFile = f2.getAbsoluteFile();
            }
        }
        if (homeFile == null) {
            f = new File(userDir);
            try {
                homeFile = f.getCanonicalFile();
            }
            catch (IOException ioe) {
                homeFile = f.getAbsoluteFile();
            }
        }
        catalinaHomeFile = homeFile;
        System.setProperty("catalina.home", catalinaHomeFile.getPath());
        String base = System.getProperty("catalina.base");
        if (base == null) {
            catalinaBaseFile = catalinaHomeFile;
        } else {
            File baseFile = new File(base);
            try {
                baseFile = baseFile.getCanonicalFile();
            }
            catch (IOException ioe) {
                baseFile = baseFile.getAbsoluteFile();
            }
            catalinaBaseFile = baseFile;
        }
        System.setProperty("catalina.base", catalinaBaseFile.getPath());
    }
}

