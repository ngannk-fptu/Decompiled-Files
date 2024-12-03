/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import org.apache.abdera.util.MultiIterator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class Discover {
    private Discover() {
    }

    public static <T> T locate(String id, String defaultImpl, Object ... args) {
        return Discover.locate(id, defaultImpl, Discover.getLoader(), args);
    }

    public static <T> T locate(String id, String defaultImpl, ClassLoader loader, Object ... args) {
        try {
            T instance = null;
            Iterable<T> items = Discover.locate(id, loader, args);
            Iterator<T> i$ = items.iterator();
            if (i$.hasNext()) {
                T i = i$.next();
                instance = i;
            }
            if (instance == null) {
                instance = Discover.load(loader, defaultImpl, false, args);
            }
            return instance;
        }
        catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    private static ClassLoader getLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static <T> Iterable<T> locate(String id, ClassLoader cl, Object ... args) {
        return Discover.locate(id, false, cl, args);
    }

    public static <T> Iterable<T> locate(String id, boolean classesonly, ClassLoader cl, Object ... args) {
        return Discover.locate(id, new DefaultLoader(id, classesonly, args, cl));
    }

    public static <T> Iterable<T> locate(String id, Object ... args) {
        return Discover.locate(id, false, args);
    }

    public static <T> Iterable<T> locate(String id, boolean classesonly) {
        return Discover.locate(id, new DefaultLoader(id, classesonly, null));
    }

    public static <T> Iterable<T> locate(String id, boolean classesonly, Object ... args) {
        return Discover.locate(id, new DefaultLoader(id, classesonly, args));
    }

    public static <T> Iterable<T> locate(String id, Iterable<T> loader) {
        List<T> impls = Collections.synchronizedList(new ArrayList());
        try {
            for (T instance : loader) {
                if (instance == null) continue;
                impls.add(instance);
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return impls;
    }

    private static <T> T load(ClassLoader loader, String spec, boolean classesonly, Object[] args) throws Exception {
        if (classesonly) {
            return (T)Discover.getClass(loader, spec);
        }
        Class<T> _class = Discover.getClass(loader, spec);
        Class[] types = new Class[args != null ? args.length : 0];
        if (args != null) {
            for (int n = 0; n < args.length; ++n) {
                types[n] = args[n].getClass();
            }
            return _class.getConstructor(types).newInstance(args);
        }
        return _class.newInstance();
    }

    private static <T> Class<T> getClass(ClassLoader loader, String spec) {
        Class<?> c = null;
        try {
            c = loader.loadClass(spec);
        }
        catch (ClassNotFoundException e) {
            try {
                c = Discover.class.getClassLoader().loadClass(spec);
            }
            catch (ClassNotFoundException e1) {
                throw new RuntimeException(e);
            }
        }
        return c;
    }

    public static URL locateResource(String id, ClassLoader loader, Class<?> callingClass) {
        URL url = loader.getResource(id);
        if (url == null && id.startsWith("/")) {
            url = loader.getResource(id.substring(1));
        }
        if (url == null) {
            url = Discover.locateResource(id, Discover.class.getClassLoader(), callingClass);
        }
        if (url == null && callingClass != null) {
            url = Discover.locateResource(id, callingClass.getClassLoader(), null);
        }
        if (url == null) {
            url = callingClass.getResource(id);
        }
        if (url == null && id.startsWith("/")) {
            url = callingClass.getResource(id.substring(1));
        }
        return url;
    }

    public static Enumeration<URL> locateResources(String id, ClassLoader loader, Class<?> callingClass) throws IOException {
        Enumeration<URL> urls = loader.getResources(id);
        if (urls == null && id.startsWith("/")) {
            urls = loader.getResources(id.substring(1));
        }
        if (urls == null) {
            urls = Discover.locateResources(id, Discover.class.getClassLoader(), callingClass);
        }
        if (urls == null) {
            urls = Discover.locateResources(id, callingClass.getClassLoader(), callingClass);
        }
        return urls;
    }

    public static InputStream locateResourceAsStream(String resourceName, ClassLoader loader, Class<?> callingClass) {
        URL url = Discover.locateResource(resourceName, loader, callingClass);
        try {
            return url != null ? url.openStream() : null;
        }
        catch (IOException e) {
            return null;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class LoaderIterator<T>
    implements Iterator<T> {
        protected final ClassLoader cl;

        protected LoaderIterator(ClassLoader cl) {
            this.cl = cl;
        }

        @Override
        public void remove() {
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static abstract class LineReaderLoaderIterator<T>
    extends LoaderIterator<T> {
        private BufferedReader buf = null;
        private String line = null;
        protected final Object[] args;
        protected final boolean classesonly;

        protected LineReaderLoaderIterator(ClassLoader cl, InputStream in, boolean classesonly, Object[] args) {
            super(cl);
            this.args = args;
            this.classesonly = classesonly;
            try {
                InputStreamReader reader = new InputStreamReader(in, "UTF-8");
                this.buf = new BufferedReader(reader);
                this.line = this.readNext();
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        @Override
        public boolean hasNext() {
            return this.line != null;
        }

        protected String readNext() {
            try {
                String line = null;
                while ((line = this.buf.readLine()) != null && (line = line.trim()).startsWith("#")) {
                }
                return line;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        protected String read() {
            String val = this.line;
            this.line = this.readNext();
            return val;
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class DefaultLoaderIterator<T>
    extends LineReaderLoaderIterator<T> {
        public DefaultLoaderIterator(ClassLoader cl, InputStream in, boolean classesonly, Object[] args) {
            super(cl, in, classesonly, args);
        }

        @Override
        public T next() {
            try {
                if (!this.hasNext()) {
                    return null;
                }
                return this.create(this.read(), this.args);
            }
            catch (Throwable t) {
                return null;
            }
        }

        protected T create(String spec, Object[] args) {
            try {
                return (T)Discover.load(this.cl, spec, this.classesonly, args);
            }
            catch (RuntimeException e) {
                throw e;
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class DefaultLoader<T>
    implements Iterable<T> {
        protected final ClassLoader loader;
        protected final String id;
        protected final Iterator<T> iterator;
        protected final Object[] args;

        public DefaultLoader(String id, boolean classesonly, Object[] args) {
            this(id, classesonly, args, Discover.getLoader());
        }

        public DefaultLoader(String id, boolean classesonly, Object[] args, ClassLoader loader) {
            this.loader = loader != null ? loader : Discover.getLoader();
            this.id = id;
            this.iterator = this.init(classesonly);
            this.args = args;
        }

        private Iterator<T> init(boolean classesonly) {
            try {
                ArrayList list = new ArrayList();
                Enumeration<URL> e = Discover.locateResources("META-INF/services/" + this.id, this.loader, Discover.class);
                while (e.hasMoreElements()) {
                    DefaultLoaderIterator i = new DefaultLoaderIterator(this.loader, e.nextElement().openStream(), classesonly, this.args);
                    list.add(i);
                }
                return new MultiIterator(list);
            }
            catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }

        @Override
        public Iterator<T> iterator() {
            return this.iterator;
        }
    }
}

