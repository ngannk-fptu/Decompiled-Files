/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.ext;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import org.apache.felix.framework.util.SecureAction;

public interface ClassPathExtenderFactory {
    public ClassPathExtender getExtender(ClassLoader var1);

    public static final class DefaultClassLoaderExtender
    implements ClassPathExtenderFactory,
    ClassPathExtender {
        private static final Method m_append;
        private static final ClassLoader m_app;
        private static final Method m_addURL;
        private final ClassLoader m_loader;

        public DefaultClassLoaderExtender() {
            this(null);
        }

        private DefaultClassLoaderExtender(ClassLoader loader) {
            this.m_loader = loader;
        }

        @Override
        public ClassPathExtender getExtender(ClassLoader loader) {
            if (m_append != null) {
                for (ClassLoader current = ClassLoader.getSystemClassLoader(); current != null; current = current.getParent()) {
                    if (loader != current) continue;
                    return this;
                }
            }
            if (m_addURL != null && loader instanceof URLClassLoader) {
                return new DefaultClassLoaderExtender(loader);
            }
            return null;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void add(File file) throws Exception {
            ClassLoader classLoader;
            ClassLoader loader;
            if (this.m_loader != null) {
                loader = this.m_loader;
                classLoader = this.m_loader;
                synchronized (classLoader) {
                    m_addURL.invoke((Object)this.m_loader, file.getCanonicalFile().toURI().toURL());
                }
            }
            loader = m_app;
            classLoader = m_app;
            synchronized (classLoader) {
                m_append.invoke((Object)m_app, file.getCanonicalFile().getPath());
            }
            try {
                for (int i = 0; i < 1000; ++i) {
                    loader.loadClass("flushFelixExtensionSubsystem" + i + ".class");
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }

        static {
            Method addURL;
            ClassLoader app = ClassLoader.getSystemClassLoader();
            Method append = null;
            while (app != null) {
                try {
                    append = app.getClass().getDeclaredMethod("appendToClassPathForInstrumentation", String.class);
                    new SecureAction().setAccesssible(append);
                    break;
                }
                catch (Throwable e) {
                    append = null;
                    try {
                        app = app.getParent();
                    }
                    catch (Exception ex) {
                        app = null;
                    }
                }
            }
            m_append = append;
            m_app = app;
            try {
                addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
                new SecureAction().setAccesssible(addURL);
            }
            catch (Throwable e) {
                addURL = null;
            }
            m_addURL = addURL;
        }
    }

    public static interface ClassPathExtender {
        public void add(File var1) throws Exception;
    }
}

