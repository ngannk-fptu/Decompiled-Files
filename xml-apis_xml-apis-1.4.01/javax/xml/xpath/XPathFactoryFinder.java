/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.xpath;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Properties;
import javax.xml.xpath.SecuritySupport;
import javax.xml.xpath.XPathFactory;

final class XPathFactoryFinder {
    private static boolean debug = false;
    private static final int DEFAULT_LINE_LENGTH = 80;
    private static Properties cacheProps;
    private static boolean firstTime;
    private final ClassLoader classLoader;
    private static final Class SERVICE_CLASS;
    private static final String SERVICE_ID;
    static /* synthetic */ Class class$javax$xml$xpath$XPathFactory;

    private static void debugPrintln(String string) {
        if (debug) {
            System.err.println("JAXP: " + string);
        }
    }

    public XPathFactoryFinder(ClassLoader classLoader) {
        this.classLoader = classLoader;
        if (debug) {
            this.debugDisplayClassLoader();
        }
    }

    private void debugDisplayClassLoader() {
        try {
            if (this.classLoader == SecuritySupport.getContextClassLoader()) {
                XPathFactoryFinder.debugPrintln("using thread context class loader (" + this.classLoader + ") for search");
                return;
            }
        }
        catch (VirtualMachineError virtualMachineError) {
            throw virtualMachineError;
        }
        catch (ThreadDeath threadDeath) {
            throw threadDeath;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        if (this.classLoader == ClassLoader.getSystemClassLoader()) {
            XPathFactoryFinder.debugPrintln("using system class loader (" + this.classLoader + ") for search");
            return;
        }
        XPathFactoryFinder.debugPrintln("using class loader (" + this.classLoader + ") for search");
    }

    public XPathFactory newFactory(String string) {
        if (string == null) {
            throw new NullPointerException();
        }
        XPathFactory xPathFactory = this._newFactory(string);
        if (debug) {
            if (xPathFactory != null) {
                XPathFactoryFinder.debugPrintln("factory '" + xPathFactory.getClass().getName() + "' was found for " + string);
            } else {
                XPathFactoryFinder.debugPrintln("unable to find a factory for " + string);
            }
        }
        return xPathFactory;
    }

    private XPathFactory _newFactory(String string) {
        Serializable serializable;
        Object object;
        XPathFactory xPathFactory;
        block29: {
            String string2;
            String string3;
            block28: {
                string3 = SERVICE_CLASS.getName() + ":" + string;
                try {
                    if (debug) {
                        XPathFactoryFinder.debugPrintln("Looking up system property '" + string3 + "'");
                    }
                    if ((string2 = SecuritySupport.getSystemProperty(string3)) != null && string2.length() > 0) {
                        if (debug) {
                            XPathFactoryFinder.debugPrintln("The value is '" + string2 + "'");
                        }
                        if ((xPathFactory = this.createInstance(string2)) != null) {
                            return xPathFactory;
                        }
                    } else if (debug) {
                        XPathFactoryFinder.debugPrintln("The property is undefined.");
                    }
                }
                catch (VirtualMachineError virtualMachineError) {
                    throw virtualMachineError;
                }
                catch (ThreadDeath threadDeath) {
                    throw threadDeath;
                }
                catch (Throwable throwable) {
                    if (!debug) break block28;
                    XPathFactoryFinder.debugPrintln("failed to look up system property '" + string3 + "'");
                    throwable.printStackTrace();
                }
            }
            string2 = SecuritySupport.getSystemProperty("java.home");
            String string4 = string2 + File.separator + "lib" + File.separator + "jaxp.properties";
            String string5 = null;
            try {
                if (firstTime) {
                    object = cacheProps;
                    synchronized (object) {
                        if (firstTime) {
                            serializable = new File(string4);
                            firstTime = false;
                            if (SecuritySupport.doesFileExist((File)serializable)) {
                                if (debug) {
                                    XPathFactoryFinder.debugPrintln("Read properties file " + serializable);
                                }
                                cacheProps.load(SecuritySupport.getFileInputStream((File)serializable));
                            }
                        }
                    }
                }
                string5 = cacheProps.getProperty(string3);
                if (debug) {
                    XPathFactoryFinder.debugPrintln("found " + string5 + " in $java.home/jaxp.properties");
                }
                if (string5 != null && (xPathFactory = this.createInstance(string5)) != null) {
                    return xPathFactory;
                }
            }
            catch (Exception exception) {
                if (!debug) break block29;
                exception.printStackTrace();
            }
        }
        object = this.createServiceFileIterator();
        while (object.hasNext()) {
            serializable = (URL)object.next();
            if (debug) {
                XPathFactoryFinder.debugPrintln("looking into " + serializable);
            }
            try {
                xPathFactory = this.loadFromServicesFile(string, ((URL)serializable).toExternalForm(), SecuritySupport.getURLInputStream((URL)serializable));
                if (xPathFactory == null) continue;
                return xPathFactory;
            }
            catch (IOException iOException) {
                if (!debug) continue;
                XPathFactoryFinder.debugPrintln("failed to read " + serializable);
                iOException.printStackTrace();
            }
        }
        if (string.equals("http://java.sun.com/jaxp/xpath/dom")) {
            if (debug) {
                XPathFactoryFinder.debugPrintln("attempting to use the platform default W3C DOM XPath lib");
            }
            return this.createInstance("org.apache.xpath.jaxp.XPathFactoryImpl");
        }
        if (debug) {
            XPathFactoryFinder.debugPrintln("all things were tried, but none was found. bailing out.");
        }
        return null;
    }

    XPathFactory createInstance(String string) {
        block8: {
            try {
                Object obj;
                if (debug) {
                    XPathFactoryFinder.debugPrintln("instanciating " + string);
                }
                Class<?> clazz = this.classLoader != null ? this.classLoader.loadClass(string) : Class.forName(string);
                if (debug) {
                    XPathFactoryFinder.debugPrintln("loaded it from " + XPathFactoryFinder.which(clazz));
                }
                if ((obj = clazz.newInstance()) instanceof XPathFactory) {
                    return (XPathFactory)obj;
                }
                if (debug) {
                    XPathFactoryFinder.debugPrintln(string + " is not assignable to " + SERVICE_CLASS.getName());
                }
            }
            catch (VirtualMachineError virtualMachineError) {
                throw virtualMachineError;
            }
            catch (ThreadDeath threadDeath) {
                throw threadDeath;
            }
            catch (Throwable throwable) {
                if (!debug) break block8;
                XPathFactoryFinder.debugPrintln("failed to instanciate " + string);
                throwable.printStackTrace();
            }
        }
        return null;
    }

    private XPathFactory loadFromServicesFile(String string, String string2, InputStream inputStream) {
        BufferedReader bufferedReader;
        if (debug) {
            XPathFactoryFinder.debugPrintln("Reading " + string2);
        }
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 80);
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 80);
        }
        String string3 = null;
        XPathFactory xPathFactory = null;
        while (true) {
            try {
                string3 = bufferedReader.readLine();
            }
            catch (IOException iOException) {
                break;
            }
            if (string3 == null) break;
            int n = string3.indexOf(35);
            if (n != -1) {
                string3 = string3.substring(0, n);
            }
            if ((string3 = string3.trim()).length() == 0) continue;
            try {
                XPathFactory xPathFactory2 = this.createInstance(string3);
                if (!xPathFactory2.isObjectModelSupported(string)) continue;
                xPathFactory = xPathFactory2;
            }
            catch (Exception exception) {
                continue;
            }
            break;
        }
        try {
            bufferedReader.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return xPathFactory;
    }

    private Iterator createServiceFileIterator() {
        if (this.classLoader == null) {
            return new SingleIterator(){
                static /* synthetic */ Class class$javax$xml$xpath$XPathFactoryFinder;

                protected Object value() {
                    ClassLoader classLoader = (class$javax$xml$xpath$XPathFactoryFinder == null ? (class$javax$xml$xpath$XPathFactoryFinder = 1.class$("javax.xml.xpath.XPathFactoryFinder")) : class$javax$xml$xpath$XPathFactoryFinder).getClassLoader();
                    return SecuritySupport.getResourceAsURL(classLoader, SERVICE_ID);
                }

                static /* synthetic */ Class class$(String string) {
                    try {
                        return Class.forName(string);
                    }
                    catch (ClassNotFoundException classNotFoundException) {
                        throw new NoClassDefFoundError(classNotFoundException.getMessage());
                    }
                }
            };
        }
        try {
            final Enumeration enumeration = SecuritySupport.getResources(this.classLoader, SERVICE_ID);
            if (debug && !enumeration.hasMoreElements()) {
                XPathFactoryFinder.debugPrintln("no " + SERVICE_ID + " file was found");
            }
            return new Iterator(){

                public void remove() {
                    throw new UnsupportedOperationException();
                }

                public boolean hasNext() {
                    return enumeration.hasMoreElements();
                }

                public Object next() {
                    return enumeration.nextElement();
                }
            };
        }
        catch (IOException iOException) {
            if (debug) {
                XPathFactoryFinder.debugPrintln("failed to enumerate resources " + SERVICE_ID);
                iOException.printStackTrace();
            }
            return ((AbstractList)new ArrayList()).iterator();
        }
    }

    private static String which(Class clazz) {
        return XPathFactoryFinder.which(clazz.getName(), clazz.getClassLoader());
    }

    private static String which(String string, ClassLoader classLoader) {
        URL uRL;
        String string2 = string.replace('.', '/') + ".class";
        if (classLoader == null) {
            classLoader = ClassLoader.getSystemClassLoader();
        }
        if ((uRL = SecuritySupport.getResourceAsURL(classLoader, string2)) != null) {
            return uRL.toString();
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

    static {
        try {
            String string = SecuritySupport.getSystemProperty("jaxp.debug");
            debug = string != null && !"false".equals(string);
        }
        catch (Exception exception) {
            debug = false;
        }
        cacheProps = new Properties();
        firstTime = true;
        SERVICE_CLASS = class$javax$xml$xpath$XPathFactory == null ? (class$javax$xml$xpath$XPathFactory = XPathFactoryFinder.class$("javax.xml.xpath.XPathFactory")) : class$javax$xml$xpath$XPathFactory;
        SERVICE_ID = "META-INF/services/" + SERVICE_CLASS.getName();
    }

    private static abstract class SingleIterator
    implements Iterator {
        private boolean seen = false;

        private SingleIterator() {
        }

        public final void remove() {
            throw new UnsupportedOperationException();
        }

        public final boolean hasNext() {
            return !this.seen;
        }

        public final Object next() {
            if (this.seen) {
                throw new NoSuchElementException();
            }
            this.seen = true;
            return this.value();
        }

        protected abstract Object value();
    }
}

