/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv.relaxng_datatype.helpers;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibrary;
import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibraryFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import java.util.Vector;

public class DatatypeLibraryLoader
implements DatatypeLibraryFactory {
    private final Service service = new Service(class$org$relaxng$datatype$DatatypeLibraryFactory == null ? (class$org$relaxng$datatype$DatatypeLibraryFactory = DatatypeLibraryLoader.class$("com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeLibraryFactory")) : class$org$relaxng$datatype$DatatypeLibraryFactory);
    static /* synthetic */ Class class$org$relaxng$datatype$DatatypeLibraryFactory;

    public DatatypeLibrary createDatatypeLibrary(String string) {
        Enumeration enumeration = this.service.getProviders();
        while (enumeration.hasMoreElements()) {
            DatatypeLibraryFactory datatypeLibraryFactory = (DatatypeLibraryFactory)enumeration.nextElement();
            DatatypeLibrary datatypeLibrary = datatypeLibraryFactory.createDatatypeLibrary(string);
            if (datatypeLibrary == null) continue;
            return datatypeLibrary;
        }
        return null;
    }

    static /* synthetic */ Class class$(String string) {
        try {
            return Class.forName(string);
        }
        catch (ClassNotFoundException classNotFoundException) {
            throw new NoClassDefFoundError(classNotFoundException.getMessage());
        }
    }

    private static class Service {
        private final Class serviceClass;
        private final Enumeration configFiles;
        private Enumeration classNames = null;
        private final Vector providers = new Vector();
        private Loader loader;
        private static final int START = 0;
        private static final int IN_NAME = 1;
        private static final int IN_COMMENT = 2;

        public Service(Class clazz) {
            try {
                this.loader = new Loader2();
            }
            catch (NoSuchMethodError noSuchMethodError) {
                this.loader = new Loader();
            }
            this.serviceClass = clazz;
            String string = "META-INF/services/" + this.serviceClass.getName();
            this.configFiles = this.loader.getResources(string);
        }

        public Enumeration getProviders() {
            return new ProviderEnumeration();
        }

        private synchronized boolean moreProviders() {
            while (true) {
                if (this.classNames == null) {
                    if (!this.configFiles.hasMoreElements()) {
                        return false;
                    }
                    this.classNames = Service.parseConfigFile((URL)this.configFiles.nextElement());
                    continue;
                }
                while (this.classNames.hasMoreElements()) {
                    String string = (String)this.classNames.nextElement();
                    try {
                        Class clazz = this.loader.loadClass(string);
                        Object t = clazz.newInstance();
                        if (!this.serviceClass.isInstance(t)) continue;
                        this.providers.addElement(t);
                        return true;
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                    }
                    catch (InstantiationException instantiationException) {
                    }
                    catch (IllegalAccessException illegalAccessException) {
                    }
                    catch (LinkageError linkageError) {
                        // empty catch block
                    }
                }
                this.classNames = null;
            }
        }

        private static Enumeration parseConfigFile(URL uRL) {
            try {
                int n;
                Reader reader;
                InputStream inputStream = uRL.openStream();
                try {
                    reader = new InputStreamReader(inputStream, "UTF-8");
                }
                catch (UnsupportedEncodingException unsupportedEncodingException) {
                    reader = new InputStreamReader(inputStream, "UTF8");
                }
                reader = new BufferedReader(reader);
                Vector<String> vector = new Vector<String>();
                StringBuffer stringBuffer = new StringBuffer();
                int n2 = 0;
                while ((n = reader.read()) >= 0) {
                    char c = (char)n;
                    switch (c) {
                        case '\n': 
                        case '\r': {
                            n2 = 0;
                            break;
                        }
                        case '\t': 
                        case ' ': {
                            break;
                        }
                        case '#': {
                            n2 = 2;
                            break;
                        }
                        default: {
                            if (n2 == 2) break;
                            n2 = 1;
                            stringBuffer.append(c);
                        }
                    }
                    if (stringBuffer.length() == 0 || n2 == 1) continue;
                    vector.addElement(stringBuffer.toString());
                    stringBuffer.setLength(0);
                }
                if (stringBuffer.length() != 0) {
                    vector.addElement(stringBuffer.toString());
                }
                return vector.elements();
            }
            catch (IOException iOException) {
                return null;
            }
        }

        private static class Loader2
        extends Loader {
            private ClassLoader cl = (class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader2 == null ? (class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader2 = Loader2.class$("com.ctc.wstx.shaded.msv.relaxng_datatype.helpers.DatatypeLibraryLoader$Service$Loader2")) : class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader2).getClassLoader();
            static /* synthetic */ Class class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader2;

            Loader2() {
                ClassLoader classLoader;
                ClassLoader classLoader2 = classLoader = Thread.currentThread().getContextClassLoader();
                while (classLoader2 != null) {
                    if (classLoader2 == this.cl) {
                        this.cl = classLoader;
                        break;
                    }
                    classLoader2 = classLoader2.getParent();
                }
            }

            Enumeration getResources(String string) {
                try {
                    return this.cl.getResources(string);
                }
                catch (IOException iOException) {
                    return new Singleton(null);
                }
            }

            Class loadClass(String string) throws ClassNotFoundException {
                return Class.forName(string, true, this.cl);
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

        private static class Loader {
            static /* synthetic */ Class class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader;

            private Loader() {
            }

            Enumeration getResources(String string) {
                ClassLoader classLoader = (class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader == null ? (class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader = Loader.class$("com.ctc.wstx.shaded.msv.relaxng_datatype.helpers.DatatypeLibraryLoader$Service$Loader")) : class$org$relaxng$datatype$helpers$DatatypeLibraryLoader$Service$Loader).getClassLoader();
                URL uRL = classLoader == null ? ClassLoader.getSystemResource(string) : classLoader.getResource(string);
                return new Singleton(uRL);
            }

            Class loadClass(String string) throws ClassNotFoundException {
                return Class.forName(string);
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

        private static class Singleton
        implements Enumeration {
            private Object obj;

            private Singleton(Object object) {
                this.obj = object;
            }

            public boolean hasMoreElements() {
                return this.obj != null;
            }

            public Object nextElement() {
                if (this.obj == null) {
                    throw new NoSuchElementException();
                }
                Object object = this.obj;
                this.obj = null;
                return object;
            }
        }

        private class ProviderEnumeration
        implements Enumeration {
            private int nextIndex = 0;

            private ProviderEnumeration() {
            }

            public boolean hasMoreElements() {
                return this.nextIndex < Service.this.providers.size() || Service.this.moreProviders();
            }

            public Object nextElement() {
                try {
                    return Service.this.providers.elementAt(this.nextIndex++);
                }
                catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                    throw new NoSuchElementException();
                }
            }
        }
    }
}

