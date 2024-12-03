/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.apache.axis.i18n;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.apache.axis.components.logger.LogFactory;
import org.apache.commons.logging.Log;

public class ProjectResourceBundle
extends ResourceBundle {
    protected static Log log = LogFactory.getLog((class$org$apache$axis$i18n$ProjectResourceBundle == null ? (class$org$apache$axis$i18n$ProjectResourceBundle = ProjectResourceBundle.class$("org.apache.axis.i18n.ProjectResourceBundle")) : class$org$apache$axis$i18n$ProjectResourceBundle).getName());
    private static final Hashtable bundleCache = new Hashtable();
    private static final Locale defaultLocale = Locale.getDefault();
    private final ResourceBundle resourceBundle;
    private final String resourceName;
    static /* synthetic */ Class class$org$apache$axis$i18n$ProjectResourceBundle;

    protected Object handleGetObject(String key) throws MissingResourceException {
        Object obj;
        if (log.isDebugEnabled()) {
            log.debug((Object)(this.toString() + "::handleGetObject(" + key + ")"));
        }
        try {
            obj = this.resourceBundle.getObject(key);
        }
        catch (MissingResourceException e) {
            obj = null;
        }
        return obj;
    }

    public Enumeration getKeys() {
        Enumeration<String> myKeys = this.resourceBundle.getKeys();
        if (this.parent == null) {
            return myKeys;
        }
        final HashSet<String> set = new HashSet<String>();
        while (myKeys.hasMoreElements()) {
            set.add(myKeys.nextElement());
        }
        Enumeration<String> pKeys = this.parent.getKeys();
        while (pKeys.hasMoreElements()) {
            set.add(pKeys.nextElement());
        }
        return new Enumeration(){
            private Iterator it;
            {
                this.it = set.iterator();
            }

            public boolean hasMoreElements() {
                return this.it.hasNext();
            }

            public Object nextElement() {
                return this.it.next();
            }
        };
    }

    public static ProjectResourceBundle getBundle(String projectName, String packageName, String resourceName) throws MissingResourceException {
        return ProjectResourceBundle.getBundle(projectName, packageName, resourceName, null, null, null);
    }

    public static ProjectResourceBundle getBundle(String projectName, Class caller, String resourceName, Locale locale) throws MissingResourceException {
        return ProjectResourceBundle.getBundle(projectName, caller, resourceName, locale, null);
    }

    public static ProjectResourceBundle getBundle(String projectName, String packageName, String resourceName, Locale locale, ClassLoader loader) throws MissingResourceException {
        return ProjectResourceBundle.getBundle(projectName, packageName, resourceName, locale, loader, null);
    }

    public static ProjectResourceBundle getBundle(String projectName, Class caller, String resourceName, Locale locale, ResourceBundle extendsBundle) throws MissingResourceException {
        return ProjectResourceBundle.getBundle(projectName, ProjectResourceBundle.getPackage(caller.getClass().getName()), resourceName, locale, caller.getClass().getClassLoader(), extendsBundle);
    }

    public static ProjectResourceBundle getBundle(String projectName, String packageName, String resourceName, Locale locale, ClassLoader loader, ResourceBundle extendsBundle) throws MissingResourceException {
        if (log.isDebugEnabled()) {
            log.debug((Object)("getBundle(" + projectName + "," + packageName + "," + resourceName + "," + String.valueOf(locale) + ",...)"));
        }
        Context context = new Context();
        context.setLocale(locale);
        context.setLoader(loader);
        context.setProjectName(projectName);
        context.setResourceName(resourceName);
        context.setParentBundle(extendsBundle);
        packageName = context.validate(packageName);
        ProjectResourceBundle bundle = null;
        try {
            bundle = ProjectResourceBundle.getBundle(context, packageName);
        }
        catch (RuntimeException e) {
            log.debug((Object)"Exception: ", (Throwable)e);
            throw e;
        }
        if (bundle == null) {
            throw new MissingResourceException("Cannot find resource '" + packageName + '.' + resourceName + "'", resourceName, "");
        }
        return bundle;
    }

    private static synchronized ProjectResourceBundle getBundle(Context context, String packageName) throws MissingResourceException {
        String cacheKey = context.getCacheKey(packageName);
        ProjectResourceBundle prb = (ProjectResourceBundle)bundleCache.get(cacheKey);
        if (prb == null) {
            String name = packageName + '.' + context.getResourceName();
            ResourceBundle rb = context.loadBundle(packageName);
            ResourceBundle parent = context.getParentBundle(packageName);
            if (rb != null) {
                prb = new ProjectResourceBundle(name, rb);
                prb.setParent(parent);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Created " + prb + ", linked to parent " + String.valueOf(parent)));
                }
            } else if (parent != null) {
                prb = parent instanceof ProjectResourceBundle ? (ProjectResourceBundle)parent : new ProjectResourceBundle(name, parent);
                if (log.isDebugEnabled()) {
                    log.debug((Object)("Root package not found, cross link to " + parent));
                }
            }
            if (prb != null) {
                bundleCache.put(cacheKey, prb);
            }
        }
        return prb;
    }

    private static final String getPackage(String name) {
        return name.substring(0, name.lastIndexOf(46)).intern();
    }

    private ProjectResourceBundle(String name, ResourceBundle bundle) throws MissingResourceException {
        this.resourceBundle = bundle;
        this.resourceName = name;
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public static void clearCache() {
        bundleCache.clear();
    }

    public String toString() {
        return this.resourceName;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    private static class Context {
        private Locale _locale;
        private ClassLoader _loader;
        private String _projectName;
        private String _resourceName;
        private ResourceBundle _parent;

        private Context() {
        }

        void setLocale(Locale l) {
            this._locale = l == null ? defaultLocale : l;
        }

        void setLoader(ClassLoader l) {
            ClassLoader classLoader = this._loader = l != null ? l : this.getClass().getClassLoader();
            if (this._loader == null) {
                this._loader = ClassLoader.getSystemClassLoader();
            }
        }

        void setProjectName(String name) {
            this._projectName = name.intern();
        }

        void setResourceName(String name) {
            this._resourceName = name.intern();
        }

        void setParentBundle(ResourceBundle b) {
            this._parent = b;
        }

        Locale getLocale() {
            return this._locale;
        }

        ClassLoader getLoader() {
            return this._loader;
        }

        String getProjectName() {
            return this._projectName;
        }

        String getResourceName() {
            return this._resourceName;
        }

        ResourceBundle getParentBundle() {
            return this._parent;
        }

        String getCacheKey(String packageName) {
            String loaderName = this._loader == null ? "" : ":" + this._loader.hashCode();
            return packageName + "." + this._resourceName + ":" + this._locale + ":" + defaultLocale + loaderName;
        }

        ResourceBundle loadBundle(String packageName) {
            try {
                return ResourceBundle.getBundle(packageName + '.' + this._resourceName, this._locale, this._loader);
            }
            catch (MissingResourceException e) {
                log.debug((Object)("loadBundle: Ignoring MissingResourceException: " + e.getMessage()));
                return null;
            }
        }

        ResourceBundle getParentBundle(String packageName) {
            ResourceBundle p;
            if (packageName != this._projectName) {
                p = ProjectResourceBundle.getBundle(this, ProjectResourceBundle.getPackage(packageName));
            } else {
                p = this._parent;
                this._parent = null;
            }
            return p;
        }

        String validate(String packageName) throws MissingResourceException {
            if (this._projectName == null || this._projectName.length() == 0) {
                log.debug((Object)"Project name not specified");
                throw new MissingResourceException("Project name not specified", "", "");
            }
            if (packageName == null || packageName.length() == 0) {
                log.debug((Object)"Package name not specified");
                throw new MissingResourceException("Package not specified", packageName, "");
            }
            if ((packageName = packageName.intern()) != this._projectName && !packageName.startsWith(this._projectName + '.')) {
                log.debug((Object)"Project not a prefix of Package");
                throw new MissingResourceException("Project '" + this._projectName + "' must be a prefix of Package '" + packageName + "'", packageName + '.' + this._resourceName, "");
            }
            return packageName;
        }
    }
}

