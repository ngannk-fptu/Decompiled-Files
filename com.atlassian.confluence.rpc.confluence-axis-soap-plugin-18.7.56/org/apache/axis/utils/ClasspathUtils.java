/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.StringTokenizer;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import org.apache.axis.AxisProperties;
import org.apache.axis.MessageContext;
import org.apache.axis.transport.http.HTTPConstants;

public class ClasspathUtils {
    public static String expandDirs(String dirPaths) {
        StringTokenizer st = new StringTokenizer(dirPaths, File.pathSeparator);
        StringBuffer buffer = new StringBuffer();
        while (st.hasMoreTokens()) {
            String d = st.nextToken();
            File dir = new File(d);
            if (!dir.isDirectory()) continue;
            File[] files = dir.listFiles(new JavaArchiveFilter());
            for (int i = 0; i < files.length; ++i) {
                buffer.append(files[i]).append(File.pathSeparator);
            }
        }
        return buffer.toString();
    }

    public static boolean isJar(InputStream is) {
        try {
            JarInputStream jis = new JarInputStream(is);
            if (jis.getNextEntry() != null) {
                return true;
            }
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return false;
    }

    public static String getDefaultClasspath(MessageContext msgContext) {
        StringBuffer classpath = new StringBuffer();
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        ClasspathUtils.fillClassPath(cl, classpath);
        String webBase = (String)msgContext.getProperty(HTTPConstants.MC_HTTP_SERVLETLOCATION);
        if (webBase != null) {
            classpath.append(webBase + File.separatorChar + "classes" + File.pathSeparatorChar);
            try {
                String libBase = webBase + File.separatorChar + "lib";
                File libDir = new File(libBase);
                String[] jarFiles = libDir.list();
                for (int i = 0; i < jarFiles.length; ++i) {
                    String jarFile = jarFiles[i];
                    if (!jarFile.endsWith(".jar")) continue;
                    classpath.append(libBase + File.separatorChar + jarFile + File.pathSeparatorChar);
                }
            }
            catch (Exception e) {
                // empty catch block
            }
        }
        ClasspathUtils.getClassPathFromDirectoryProperty(classpath, "axis.ext.dirs");
        ClasspathUtils.getClassPathFromProperty(classpath, "org.apache.catalina.jsp_classpath");
        ClasspathUtils.getClassPathFromProperty(classpath, "ws.ext.dirs");
        ClasspathUtils.getClassPathFromProperty(classpath, "com.ibm.websphere.servlet.application.classpath");
        ClasspathUtils.getClassPathFromProperty(classpath, "java.class.path");
        ClasspathUtils.getClassPathFromDirectoryProperty(classpath, "java.ext.dirs");
        ClasspathUtils.getClassPathFromProperty(classpath, "sun.boot.class.path");
        return classpath.toString();
    }

    private static void getClassPathFromDirectoryProperty(StringBuffer classpath, String property) {
        String dirs = AxisProperties.getProperty(property);
        String path = null;
        try {
            path = ClasspathUtils.expandDirs(dirs);
        }
        catch (Exception e) {
            // empty catch block
        }
        if (path != null) {
            classpath.append(path);
            classpath.append(File.pathSeparatorChar);
        }
    }

    private static void getClassPathFromProperty(StringBuffer classpath, String property) {
        String path = AxisProperties.getProperty(property);
        if (path != null) {
            classpath.append(path);
            classpath.append(File.pathSeparatorChar);
        }
    }

    private static void fillClassPath(ClassLoader cl, StringBuffer classpath) {
        while (cl != null) {
            if (cl instanceof URLClassLoader) {
                URL[] urls = ((URLClassLoader)cl).getURLs();
                for (int i = 0; urls != null && i < urls.length; ++i) {
                    String path = urls[i].getPath();
                    if (path.length() >= 3 && path.charAt(0) == '/' && path.charAt(2) == ':') {
                        path = path.substring(1);
                    }
                    classpath.append(URLDecoder.decode(path));
                    classpath.append(File.pathSeparatorChar);
                    File file = new File(urls[i].getFile());
                    if (!file.isFile()) continue;
                    FileInputStream fis = null;
                    try {
                        Attributes attributes;
                        JarFile jar;
                        Manifest manifest;
                        fis = new FileInputStream(file);
                        if (!ClasspathUtils.isJar(fis) || (manifest = (jar = new JarFile(file)).getManifest()) == null || (attributes = manifest.getMainAttributes()) == null) continue;
                        String s = attributes.getValue(Attributes.Name.CLASS_PATH);
                        String base = file.getParent();
                        if (s == null) continue;
                        StringTokenizer st = new StringTokenizer(s, " ");
                        while (st.hasMoreTokens()) {
                            String t = st.nextToken();
                            classpath.append(base + File.separatorChar + t);
                            classpath.append(File.pathSeparatorChar);
                        }
                        continue;
                    }
                    catch (IOException ioe) {
                        if (fis == null) continue;
                        try {
                            fis.close();
                            continue;
                        }
                        catch (IOException ioe2) {
                            // empty catch block
                        }
                    }
                }
            }
            cl = cl.getParent();
        }
    }

    private static class JavaArchiveFilter
    implements FileFilter {
        private JavaArchiveFilter() {
        }

        public boolean accept(File file) {
            String name = file.getName().toLowerCase();
            return name.endsWith(".jar") || name.endsWith(".zip");
        }
    }
}

