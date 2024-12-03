/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.launch;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.tools.ant.launch.AntMain;
import org.apache.tools.ant.launch.LaunchException;
import org.apache.tools.ant.launch.Locator;

public class Launcher {
    public static final String ANTHOME_PROPERTY = "ant.home";
    public static final String ANTLIBDIR_PROPERTY = "ant.library.dir";
    public static final String ANT_PRIVATEDIR = ".ant";
    public static final String ANT_PRIVATELIB = "lib";
    public static final String USER_LIBDIR = ".ant" + File.separatorChar + "lib";
    public static final String MAIN_CLASS = "org.apache.tools.ant.Main";
    public static final String USER_HOMEDIR = "user.home";
    private static final String JAVA_CLASS_PATH = "java.class.path";
    protected static final int EXIT_CODE_ERROR = 2;
    public boolean launchDiag = false;

    public static void main(String[] args) {
        int exitCode;
        boolean launchDiag = false;
        try {
            Launcher launcher = new Launcher();
            exitCode = launcher.run(args);
            launchDiag = launcher.launchDiag;
        }
        catch (LaunchException e) {
            exitCode = 2;
            System.err.println(e.getMessage());
        }
        catch (Throwable t) {
            exitCode = 2;
            t.printStackTrace(System.err);
        }
        if (exitCode != 0) {
            if (launchDiag) {
                System.out.println("Exit code: " + exitCode);
            }
            System.exit(exitCode);
        }
    }

    private Launcher() {
    }

    private void addPath(String path, boolean getJars, List<URL> libPathURLs) throws MalformedURLException {
        StringTokenizer tokenizer = new StringTokenizer(path, File.pathSeparator);
        while (tokenizer.hasMoreElements()) {
            String elementName = tokenizer.nextToken();
            File element = new File(elementName);
            if (elementName.contains("%") && !element.exists()) continue;
            if (getJars && element.isDirectory()) {
                for (URL dirURL : Locator.getLocationURLs(element)) {
                    if (this.launchDiag) {
                        System.out.println("adding library JAR: " + dirURL);
                    }
                    libPathURLs.add(dirURL);
                }
            }
            URL url = new URL(element.toURI().toASCIIString());
            if (this.launchDiag) {
                System.out.println("adding library URL: " + url);
            }
            libPathURLs.add(url);
        }
    }

    private int run(String[] args) throws LaunchException, MalformedURLException {
        String antHomeProperty = System.getProperty(ANTHOME_PROPERTY);
        File antHome = null;
        File sourceJar = Locator.getClassSource(this.getClass());
        File jarDir = sourceJar.getParentFile();
        String mainClassname = MAIN_CLASS;
        if (antHomeProperty != null) {
            antHome = new File(antHomeProperty);
        }
        if (antHome == null || !antHome.exists()) {
            antHome = jarDir.getParentFile();
            this.setProperty(ANTHOME_PROPERTY, antHome.getAbsolutePath());
        }
        if (!antHome.exists()) {
            throw new LaunchException("Ant home is set incorrectly or ant could not be located (estimated value=" + antHome.getAbsolutePath() + ")");
        }
        ArrayList<String> libPaths = new ArrayList<String>();
        String cpString = null;
        ArrayList<String> argList = new ArrayList<String>();
        boolean noUserLib = false;
        boolean noClassPath = false;
        for (int i = 0; i < args.length; ++i) {
            if ("-lib".equals(args[i])) {
                if (i == args.length - 1) {
                    throw new LaunchException("The -lib argument must be followed by a library location");
                }
                libPaths.add(args[++i]);
                continue;
            }
            if ("-cp".equals(args[i])) {
                if (i == args.length - 1) {
                    throw new LaunchException("The -cp argument must be followed by a classpath expression");
                }
                if (cpString != null) {
                    throw new LaunchException("The -cp argument must not be repeated");
                }
                cpString = args[++i];
                continue;
            }
            if ("--nouserlib".equals(args[i]) || "-nouserlib".equals(args[i])) {
                noUserLib = true;
                continue;
            }
            if ("--launchdiag".equals(args[i])) {
                this.launchDiag = true;
                continue;
            }
            if ("--noclasspath".equals(args[i]) || "-noclasspath".equals(args[i])) {
                noClassPath = true;
                continue;
            }
            if ("-main".equals(args[i])) {
                if (i == args.length - 1) {
                    throw new LaunchException("The -main argument must be followed by a library location");
                }
                mainClassname = args[++i];
                continue;
            }
            argList.add(args[i]);
        }
        this.logPath("Launcher JAR", sourceJar);
        this.logPath("Launcher JAR directory", sourceJar.getParentFile());
        this.logPath("java.home", new File(System.getProperty("java.home")));
        String[] newArgs = argList.size() == args.length ? args : argList.toArray(new String[0]);
        URL[] libURLs = this.getLibPathURLs(noClassPath ? null : cpString, libPaths);
        URL[] systemURLs = this.getSystemURLs(jarDir);
        URL[] userURLs = noUserLib ? new URL[]{} : this.getUserURLs();
        File toolsJAR = Locator.getToolsJar();
        this.logPath("tools.jar", toolsJAR);
        URL[] jars = this.getJarArray(libURLs, userURLs, systemURLs, toolsJAR);
        StringBuilder baseClassPath = new StringBuilder(System.getProperty(JAVA_CLASS_PATH));
        if (baseClassPath.charAt(baseClassPath.length() - 1) == File.pathSeparatorChar) {
            baseClassPath.setLength(baseClassPath.length() - 1);
        }
        for (URL jar : jars) {
            baseClassPath.append(File.pathSeparatorChar);
            baseClassPath.append(Locator.fromURI(jar.toString()));
        }
        this.setProperty(JAVA_CLASS_PATH, baseClassPath.toString());
        URLClassLoader loader = new URLClassLoader(jars, Launcher.class.getClassLoader());
        Thread.currentThread().setContextClassLoader(loader);
        Class<AntMain> mainClass = null;
        int exitCode = 0;
        Throwable thrown = null;
        try {
            mainClass = loader.loadClass(mainClassname).asSubclass(AntMain.class);
            AntMain main = mainClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            main.startAnt(newArgs, null, null);
        }
        catch (InstantiationException ex) {
            System.err.println("Incompatible version of " + mainClassname + " detected");
            File mainJar = Locator.getClassSource(mainClass);
            System.err.println("Location of this class " + mainJar);
            thrown = ex;
        }
        catch (ClassNotFoundException cnfe) {
            System.err.println("Failed to locate" + mainClassname);
            thrown = cnfe;
        }
        catch (Throwable t) {
            t.printStackTrace(System.err);
            thrown = t;
        }
        if (thrown != null) {
            System.err.println("ant.home: " + antHome.getAbsolutePath());
            System.err.println("Classpath: " + baseClassPath.toString());
            System.err.println("Launcher JAR: " + sourceJar.getAbsolutePath());
            System.err.println("Launcher Directory: " + jarDir.getAbsolutePath());
            exitCode = 2;
        }
        return exitCode;
    }

    private URL[] getLibPathURLs(String cpString, List<String> libPaths) throws MalformedURLException {
        ArrayList<URL> libPathURLs = new ArrayList<URL>();
        if (cpString != null) {
            this.addPath(cpString, false, libPathURLs);
        }
        for (String libPath : libPaths) {
            this.addPath(libPath, true, libPathURLs);
        }
        return libPathURLs.toArray(new URL[0]);
    }

    private URL[] getSystemURLs(File antLauncherDir) throws MalformedURLException {
        File antLibDir = null;
        String antLibDirProperty = System.getProperty(ANTLIBDIR_PROPERTY);
        if (antLibDirProperty != null) {
            antLibDir = new File(antLibDirProperty);
        }
        if (antLibDir == null || !antLibDir.exists()) {
            antLibDir = antLauncherDir;
            this.setProperty(ANTLIBDIR_PROPERTY, antLibDir.getAbsolutePath());
        }
        return Locator.getLocationURLs(antLibDir);
    }

    private URL[] getUserURLs() throws MalformedURLException {
        File userLibDir = new File(System.getProperty(USER_HOMEDIR), USER_LIBDIR);
        return Locator.getLocationURLs(userLibDir);
    }

    private URL[] getJarArray(URL[] libJars, URL[] userJars, URL[] systemJars, File toolsJar) throws MalformedURLException {
        int numJars = libJars.length + userJars.length + systemJars.length;
        if (toolsJar != null) {
            ++numJars;
        }
        URL[] jars = new URL[numJars];
        System.arraycopy(libJars, 0, jars, 0, libJars.length);
        System.arraycopy(userJars, 0, jars, libJars.length, userJars.length);
        System.arraycopy(systemJars, 0, jars, userJars.length + libJars.length, systemJars.length);
        if (toolsJar != null) {
            jars[jars.length - 1] = new URL(toolsJar.toURI().toASCIIString());
        }
        return jars;
    }

    private void setProperty(String name, String value) {
        if (this.launchDiag) {
            System.out.println("Setting \"" + name + "\" to \"" + value + "\"");
        }
        System.setProperty(name, value);
    }

    private void logPath(String name, File path) {
        if (this.launchDiag) {
            System.out.println(name + "= \"" + path + "\"");
        }
    }
}

