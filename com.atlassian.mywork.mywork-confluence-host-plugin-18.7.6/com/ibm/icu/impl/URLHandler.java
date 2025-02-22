/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.impl;

import com.ibm.icu.impl.ClassLoaderUtil;
import com.ibm.icu.impl.ICUDebug;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract class URLHandler {
    public static final String PROPNAME = "urlhandler.props";
    private static final Map<String, Method> handlers;
    private static final boolean DEBUG;

    public static URLHandler get(URL url) {
        block9: {
            Method m;
            if (url == null) {
                return null;
            }
            String protocol = url.getProtocol();
            if (handlers != null && (m = handlers.get(protocol)) != null) {
                try {
                    URLHandler handler = (URLHandler)m.invoke(null, url);
                    if (handler != null) {
                        return handler;
                    }
                }
                catch (IllegalAccessException e) {
                    if (DEBUG) {
                        System.err.println(e);
                    }
                }
                catch (IllegalArgumentException e) {
                    if (DEBUG) {
                        System.err.println(e);
                    }
                }
                catch (InvocationTargetException e) {
                    if (!DEBUG) break block9;
                    System.err.println(e);
                }
            }
        }
        return URLHandler.getDefault(url);
    }

    protected static URLHandler getDefault(URL url) {
        URLHandler handler = null;
        String protocol = url.getProtocol();
        try {
            if (protocol.equals("file")) {
                handler = new FileURLHandler(url);
            } else if (protocol.equals("jar") || protocol.equals("wsjar")) {
                handler = new JarURLHandler(url);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        return handler;
    }

    public void guide(URLVisitor visitor, boolean recurse) {
        this.guide(visitor, recurse, true);
    }

    public abstract void guide(URLVisitor var1, boolean var2, boolean var3);

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static {
        HashMap<String, Method> h;
        block23: {
            DEBUG = ICUDebug.enabled("URLHandler");
            h = null;
            BufferedReader br = null;
            try {
                ClassLoader loader = ClassLoaderUtil.getClassLoader(URLHandler.class);
                InputStream is = loader.getResourceAsStream(PROPNAME);
                if (is == null) break block23;
                Class[] params = new Class[]{URL.class};
                br = new BufferedReader(new InputStreamReader(is));
                String line = br.readLine();
                while (line != null) {
                    block24: {
                        if ((line = line.trim()).length() != 0 && line.charAt(0) != '#') {
                            int ix = line.indexOf(61);
                            if (ix == -1) {
                                if (!DEBUG) break;
                                System.err.println("bad urlhandler line: '" + line + "'");
                                break;
                            }
                            String key = line.substring(0, ix).trim();
                            String value = line.substring(ix + 1).trim();
                            try {
                                Class<?> cl = Class.forName(value);
                                Method m = cl.getDeclaredMethod("get", params);
                                if (h == null) {
                                    h = new HashMap<String, Method>();
                                }
                                h.put(key, m);
                            }
                            catch (ClassNotFoundException e) {
                                if (DEBUG) {
                                    System.err.println(e);
                                }
                            }
                            catch (NoSuchMethodException e) {
                                if (DEBUG) {
                                    System.err.println(e);
                                }
                            }
                            catch (SecurityException e) {
                                if (!DEBUG) break block24;
                                System.err.println(e);
                            }
                        }
                    }
                    line = br.readLine();
                }
                br.close();
            }
            catch (Throwable t) {
                if (DEBUG) {
                    System.err.println(t);
                }
            }
            finally {
                if (br != null) {
                    try {
                        br.close();
                    }
                    catch (IOException loader) {}
                }
            }
        }
        handlers = h;
    }

    public static interface URLVisitor {
        public void visit(String var1);
    }

    private static class JarURLHandler
    extends URLHandler {
        JarFile jarFile;
        String prefix;

        JarURLHandler(URL url) {
            try {
                String urlStr;
                int idx;
                String protocol;
                this.prefix = url.getPath();
                int ix = this.prefix.lastIndexOf("!/");
                if (ix >= 0) {
                    this.prefix = this.prefix.substring(ix + 2);
                }
                if (!(protocol = url.getProtocol()).equals("jar") && (idx = (urlStr = url.toString()).indexOf(":")) != -1) {
                    url = new URL("jar" + urlStr.substring(idx));
                }
                JarURLConnection conn = (JarURLConnection)url.openConnection();
                this.jarFile = conn.getJarFile();
            }
            catch (Exception e) {
                if (DEBUG) {
                    System.err.println("icurb jar error: " + e);
                }
                throw new IllegalArgumentException("jar error: " + e.getMessage());
            }
        }

        @Override
        public void guide(URLVisitor v, boolean recurse, boolean strip) {
            block4: {
                try {
                    Enumeration<JarEntry> entries = this.jarFile.entries();
                    while (entries.hasMoreElements()) {
                        int ix;
                        String name;
                        JarEntry entry = entries.nextElement();
                        if (entry.isDirectory() || !(name = entry.getName()).startsWith(this.prefix) || (ix = (name = name.substring(this.prefix.length())).lastIndexOf(47)) > 0 && !recurse) continue;
                        if (strip && ix != -1) {
                            name = name.substring(ix + 1);
                        }
                        v.visit(name);
                    }
                }
                catch (Exception e) {
                    if (!DEBUG) break block4;
                    System.err.println("icurb jar error: " + e);
                }
            }
        }
    }

    private static class FileURLHandler
    extends URLHandler {
        File file;

        FileURLHandler(URL url) {
            try {
                this.file = new File(url.toURI());
            }
            catch (URISyntaxException uRISyntaxException) {
                // empty catch block
            }
            if (this.file == null || !this.file.exists()) {
                if (DEBUG) {
                    System.err.println("file does not exist - " + url.toString());
                }
                throw new IllegalArgumentException();
            }
        }

        @Override
        public void guide(URLVisitor v, boolean recurse, boolean strip) {
            if (this.file.isDirectory()) {
                this.process(v, recurse, strip, "/", this.file.listFiles());
            } else {
                v.visit(this.file.getName());
            }
        }

        private void process(URLVisitor v, boolean recurse, boolean strip, String path, File[] files) {
            if (files != null) {
                for (int i = 0; i < files.length; ++i) {
                    File f = files[i];
                    if (f.isDirectory()) {
                        if (!recurse) continue;
                        this.process(v, recurse, strip, path + f.getName() + '/', f.listFiles());
                        continue;
                    }
                    v.visit(strip ? f.getName() : path + f.getName());
                }
            }
        }
    }
}

