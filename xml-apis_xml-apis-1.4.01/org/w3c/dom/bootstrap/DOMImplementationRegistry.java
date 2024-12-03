/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.bootstrap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.StringTokenizer;
import java.util.Vector;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DOMImplementationList;
import org.w3c.dom.DOMImplementationSource;

public final class DOMImplementationRegistry {
    public static final String PROPERTY = "org.w3c.dom.DOMImplementationSourceList";
    private static final int DEFAULT_LINE_LENGTH = 80;
    private static final String DEFAULT_DOM_IMPLEMENTATION_SOURCE = "org.apache.xerces.dom.DOMXSImplementationSourceImpl";
    private Vector sources;
    static /* synthetic */ Class class$org$w3c$dom$bootstrap$DOMImplementationRegistry;

    private DOMImplementationRegistry(Vector vector) {
        this.sources = vector;
    }

    public static DOMImplementationRegistry newInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException, ClassCastException {
        Vector<DOMImplementationSource> vector = new Vector<DOMImplementationSource>();
        ClassLoader classLoader = DOMImplementationRegistry.getClassLoader();
        String string = DOMImplementationRegistry.getSystemProperty(PROPERTY);
        if (string == null || string.length() == 0) {
            string = DOMImplementationRegistry.getServiceValue(classLoader);
        }
        if (string == null) {
            string = DEFAULT_DOM_IMPLEMENTATION_SOURCE;
        }
        if (string != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(string);
            while (stringTokenizer.hasMoreTokens()) {
                String string2 = stringTokenizer.nextToken();
                Class<?> clazz = null;
                clazz = classLoader != null ? classLoader.loadClass(string2) : Class.forName(string2);
                DOMImplementationSource dOMImplementationSource = (DOMImplementationSource)clazz.newInstance();
                vector.addElement(dOMImplementationSource);
            }
        }
        return new DOMImplementationRegistry(vector);
    }

    public DOMImplementation getDOMImplementation(String string) {
        int n = this.sources.size();
        Object var3_3 = null;
        int n2 = 0;
        while (n2 < n) {
            DOMImplementationSource dOMImplementationSource = (DOMImplementationSource)this.sources.elementAt(n2);
            DOMImplementation dOMImplementation = dOMImplementationSource.getDOMImplementation(string);
            if (dOMImplementation != null) {
                return dOMImplementation;
            }
            ++n2;
        }
        return null;
    }

    public DOMImplementationList getDOMImplementationList(String string) {
        final Vector<DOMImplementation> vector = new Vector<DOMImplementation>();
        int n = this.sources.size();
        int n2 = 0;
        while (n2 < n) {
            DOMImplementationSource dOMImplementationSource = (DOMImplementationSource)this.sources.elementAt(n2);
            DOMImplementationList dOMImplementationList = dOMImplementationSource.getDOMImplementationList(string);
            int n3 = 0;
            while (n3 < dOMImplementationList.getLength()) {
                DOMImplementation dOMImplementation = dOMImplementationList.item(n3);
                vector.addElement(dOMImplementation);
                ++n3;
            }
            ++n2;
        }
        return new DOMImplementationList(){

            public DOMImplementation item(int n) {
                if (n >= 0 && n < vector.size()) {
                    try {
                        return (DOMImplementation)vector.elementAt(n);
                    }
                    catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                        return null;
                    }
                }
                return null;
            }

            public int getLength() {
                return vector.size();
            }
        };
    }

    public void addSource(DOMImplementationSource dOMImplementationSource) {
        if (dOMImplementationSource == null) {
            throw new NullPointerException();
        }
        if (!this.sources.contains(dOMImplementationSource)) {
            this.sources.addElement(dOMImplementationSource);
        }
    }

    private static ClassLoader getClassLoader() {
        try {
            ClassLoader classLoader = DOMImplementationRegistry.getContextClassLoader();
            if (classLoader != null) {
                return classLoader;
            }
        }
        catch (Exception exception) {
            return (class$org$w3c$dom$bootstrap$DOMImplementationRegistry == null ? (class$org$w3c$dom$bootstrap$DOMImplementationRegistry = DOMImplementationRegistry.class$("org.w3c.dom.bootstrap.DOMImplementationRegistry")) : class$org$w3c$dom$bootstrap$DOMImplementationRegistry).getClassLoader();
        }
        return (class$org$w3c$dom$bootstrap$DOMImplementationRegistry == null ? (class$org$w3c$dom$bootstrap$DOMImplementationRegistry = DOMImplementationRegistry.class$("org.w3c.dom.bootstrap.DOMImplementationRegistry")) : class$org$w3c$dom$bootstrap$DOMImplementationRegistry).getClassLoader();
    }

    private static String getServiceValue(ClassLoader classLoader) {
        block7: {
            String string = "META-INF/services/org.w3c.dom.DOMImplementationSourceList";
            try {
                BufferedReader bufferedReader;
                InputStream inputStream = DOMImplementationRegistry.getResourceAsStream(classLoader, string);
                if (inputStream == null) break block7;
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 80);
                }
                catch (UnsupportedEncodingException unsupportedEncodingException) {
                    bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 80);
                }
                String string2 = null;
                try {
                    string2 = bufferedReader.readLine();
                    Object var6_7 = null;
                }
                catch (Throwable throwable) {
                    Object var6_8 = null;
                    bufferedReader.close();
                    throw throwable;
                }
                bufferedReader.close();
                if (string2 != null && string2.length() > 0) {
                    return string2;
                }
            }
            catch (Exception exception) {
                return null;
            }
        }
        return null;
    }

    private static boolean isJRE11() {
        try {
            Class<?> clazz = Class.forName("java.security.AccessController");
            return false;
        }
        catch (Exception exception) {
            return true;
        }
    }

    private static ClassLoader getContextClassLoader() {
        return DOMImplementationRegistry.isJRE11() ? null : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                ClassLoader classLoader = null;
                try {
                    classLoader = Thread.currentThread().getContextClassLoader();
                }
                catch (SecurityException securityException) {
                    // empty catch block
                }
                return classLoader;
            }
        });
    }

    private static String getSystemProperty(final String string) {
        return DOMImplementationRegistry.isJRE11() ? System.getProperty(string) : (String)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                return System.getProperty(string);
            }
        });
    }

    private static InputStream getResourceAsStream(final ClassLoader classLoader, final String string) {
        if (DOMImplementationRegistry.isJRE11()) {
            InputStream inputStream = classLoader == null ? ClassLoader.getSystemResourceAsStream(string) : classLoader.getResourceAsStream(string);
            return inputStream;
        }
        return (InputStream)AccessController.doPrivileged(new PrivilegedAction(){

            public Object run() {
                InputStream inputStream = classLoader == null ? ClassLoader.getSystemResourceAsStream(string) : classLoader.getResourceAsStream(string);
                return inputStream;
            }
        });
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }
}

