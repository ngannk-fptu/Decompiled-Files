/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import com.sun.media.jai.util.ServiceConfigurationError;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

public final class Service {
    private static final String prefix = "META-INF/services/";

    private Service() {
    }

    private static void fail(Class service, String msg) throws ServiceConfigurationError {
        throw new ServiceConfigurationError(service.getName() + ": " + msg);
    }

    private static void fail(Class service, URL u, int line, String msg) throws ServiceConfigurationError {
        Service.fail(service, u + ":" + line + ": " + msg);
    }

    private static int parseLine(Class service, URL u, BufferedReader r, int lc, List names, Set returned) throws IOException, ServiceConfigurationError {
        int n;
        String ln = r.readLine();
        if (ln == null) {
            return -1;
        }
        int ci = ln.indexOf(35);
        if (ci >= 0) {
            ln = ln.substring(0, ci);
        }
        if ((n = (ln = ln.trim()).length()) != 0) {
            if (ln.indexOf(32) >= 0 || ln.indexOf(9) >= 0) {
                Service.fail(service, u, lc, "Illegal configuration-file syntax");
            }
            if (!Character.isJavaIdentifierStart(ln.charAt(0))) {
                Service.fail(service, u, lc, "Illegal provider-class name: " + ln);
            }
            for (int i = 1; i < n; ++i) {
                char c = ln.charAt(i);
                if (Character.isJavaIdentifierPart(c) || c == '.') continue;
                Service.fail(service, u, lc, "Illegal provider-class name: " + ln);
            }
            if (!returned.contains(ln)) {
                names.add(ln);
                returned.add(ln);
            }
        }
        return lc + 1;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Loose catch block
     */
    private static Iterator parse(Class service, URL u, Set returned) throws ServiceConfigurationError {
        ArrayList names;
        block15: {
            InputStream in = null;
            BufferedReader r = null;
            names = new ArrayList();
            in = u.openStream();
            r = new BufferedReader(new InputStreamReader(in, "utf-8"));
            int lc = 1;
            while ((lc = Service.parseLine(service, u, r, lc, names, returned)) >= 0) {
            }
            Object var8_8 = null;
            try {
                if (r != null) {
                    r.close();
                }
                if (in != null) {
                    in.close();
                }
                break block15;
            }
            catch (IOException y) {
                Service.fail(service, ": " + y);
            }
            break block15;
            {
                catch (IOException x) {
                    Service.fail(service, ": " + x);
                    Object var8_9 = null;
                    try {
                        if (r != null) {
                            r.close();
                        }
                        if (in != null) {
                            in.close();
                        }
                        break block15;
                    }
                    catch (IOException y) {
                        Service.fail(service, ": " + y);
                    }
                }
            }
            catch (Throwable throwable) {
                Object var8_10 = null;
                try {
                    if (r != null) {
                        r.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                }
                catch (IOException y) {
                    Service.fail(service, ": " + y);
                }
                throw throwable;
            }
        }
        return names.iterator();
    }

    public static Iterator providers(Class service, ClassLoader loader) throws ServiceConfigurationError {
        return new LazyIterator(service, loader);
    }

    public static Iterator providers(Class service) throws ServiceConfigurationError {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        return Service.providers(service, cl);
    }

    public static Iterator installedProviders(Class service) throws ServiceConfigurationError {
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        if (cl != null) {
            cl = cl.getParent();
        }
        return Service.providers(service, cl);
    }

    private static class LazyIterator
    implements Iterator {
        Class service;
        ClassLoader loader;
        Enumeration configs = null;
        Iterator pending = null;
        Set returned = new TreeSet();
        String nextName = null;

        private LazyIterator(Class service, ClassLoader loader) {
            this.service = service;
            this.loader = loader;
        }

        public boolean hasNext() throws ServiceConfigurationError {
            if (this.nextName != null) {
                return true;
            }
            if (this.configs == null) {
                try {
                    String fullName = Service.prefix + this.service.getName();
                    this.configs = this.loader == null ? ClassLoader.getSystemResources(fullName) : this.loader.getResources(fullName);
                }
                catch (IOException x) {
                    Service.fail(this.service, ": " + x);
                }
            }
            while (this.pending == null || !this.pending.hasNext()) {
                if (!this.configs.hasMoreElements()) {
                    return false;
                }
                this.pending = Service.parse(this.service, (URL)this.configs.nextElement(), this.returned);
            }
            this.nextName = (String)this.pending.next();
            return true;
        }

        public Object next() throws ServiceConfigurationError {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            String cn = this.nextName;
            this.nextName = null;
            try {
                return Class.forName(cn, true, this.loader).newInstance();
            }
            catch (ClassNotFoundException x) {
                Service.fail(this.service, "Provider " + cn + " not found");
            }
            catch (Exception x) {
                Service.fail(this.service, "Provider " + cn + " could not be instantiated: " + x);
            }
            return null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

