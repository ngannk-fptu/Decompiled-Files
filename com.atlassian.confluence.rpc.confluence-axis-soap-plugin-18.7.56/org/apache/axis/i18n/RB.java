/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.i18n;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;

public class RB {
    static Hashtable propertyCache = new Hashtable();
    public static final String BASE_NAME = "resource";
    public static final String PROPERTY_EXT = ".properties";
    protected String basePropertyFileName;
    protected Properties resourceProperties;

    public RB(String name) throws MissingResourceException {
        this(null, name, null);
    }

    public RB(Object caller, String name) throws MissingResourceException {
        this(caller, name, null);
    }

    public RB(Object caller, String name, Locale locale) throws MissingResourceException {
        ClassLoader cl = null;
        if (caller != null) {
            String fullName;
            int pos;
            Class<?> c = caller instanceof Class ? (Class<?>)caller : caller.getClass();
            cl = c.getClassLoader();
            if (name.indexOf("/") == -1 && (pos = (fullName = c.getName()).lastIndexOf(".")) > 0) {
                name = fullName.substring(0, pos + 1).replace('.', '/') + name;
            }
        } else if (name.indexOf("/") == -1) {
            name = "org/apache/axis/default-resource";
        }
        Locale defaultLocale = Locale.getDefault();
        if (locale != null && locale.equals(defaultLocale)) {
            locale = null;
        }
        this.loadProperties(name, cl, locale, defaultLocale);
    }

    public String getString(String key) throws MissingResourceException {
        return this.getString(key, null);
    }

    public String getString(String key, Object arg0) throws MissingResourceException {
        Object[] o = new Object[]{arg0};
        return this.getString(key, o);
    }

    public String getString(String key, Object arg0, Object arg1) throws MissingResourceException {
        Object[] o = new Object[]{arg0, arg1};
        return this.getString(key, o);
    }

    public String getString(String key, Object arg0, Object arg1, Object arg2) throws MissingResourceException {
        Object[] o = new Object[]{arg0, arg1, arg2};
        return this.getString(key, o);
    }

    public String getString(String key, Object[] array) throws MissingResourceException {
        String msg = null;
        if (this.resourceProperties != null) {
            msg = this.resourceProperties.getProperty(key);
        }
        if (msg == null) {
            throw new MissingResourceException("Cannot find resource key \"" + key + "\" in base name " + this.basePropertyFileName, this.basePropertyFileName, key);
        }
        msg = MessageFormat.format(msg, array);
        return msg;
    }

    protected void loadProperties(String basename, ClassLoader loader, Locale locale, Locale defaultLocale) throws MissingResourceException {
        String loaderName = "";
        if (loader != null) {
            loaderName = ":" + loader.hashCode();
        }
        String cacheKey = basename + ":" + locale + ":" + defaultLocale + loaderName;
        Properties p = (Properties)propertyCache.get(cacheKey);
        this.basePropertyFileName = basename + PROPERTY_EXT;
        if (p == null) {
            if (locale != null) {
                p = this.loadProperties(basename, loader, locale, p);
            }
            if (defaultLocale != null) {
                p = this.loadProperties(basename, loader, defaultLocale, p);
            }
            if ((p = this.merge(p, this.loadProperties(this.basePropertyFileName, loader))) == null) {
                throw new MissingResourceException("Cannot find resource for base name " + this.basePropertyFileName, this.basePropertyFileName, "");
            }
            propertyCache.put(cacheKey, p);
        }
        this.resourceProperties = p;
    }

    protected Properties loadProperties(String basename, ClassLoader loader, Locale locale, Properties props) {
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        if (variant != null && variant.trim().length() == 0) {
            variant = null;
        }
        if (language != null) {
            if (country != null) {
                if (variant != null) {
                    props = this.merge(props, this.loadProperties(basename + "_" + language + "_" + country + "_" + variant + PROPERTY_EXT, loader));
                }
                props = this.merge(props, this.loadProperties(basename + "_" + language + "_" + country + PROPERTY_EXT, loader));
            }
            props = this.merge(props, this.loadProperties(basename + "_" + language + PROPERTY_EXT, loader));
        }
        return props;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected Properties loadProperties(String resname, ClassLoader loader) {
        Properties props = null;
        InputStream in = null;
        try {
            if (loader != null) {
                in = loader.getResourceAsStream(resname);
            }
            if (in == null) {
                in = ClassLoader.getSystemResourceAsStream(resname);
            }
            if (in != null) {
                props = new Properties();
                try {
                    props.load(in);
                }
                catch (IOException ex) {
                    props = null;
                }
            }
            Object var7_6 = null;
            if (in == null) return props;
        }
        catch (Throwable throwable) {
            Object var7_7 = null;
            if (in == null) throw throwable;
            try {
                in.close();
                throw throwable;
            }
            catch (Exception ex) {
                // empty catch block
            }
            throw throwable;
        }
        try {
            in.close();
            return props;
        }
        catch (Exception ex) {}
        return props;
    }

    protected Properties merge(Properties p1, Properties p2) {
        if (p1 == null && p2 == null) {
            return null;
        }
        if (p1 == null) {
            return p2;
        }
        if (p2 == null) {
            return p1;
        }
        Enumeration<Object> enumeration = p2.keys();
        while (enumeration.hasMoreElements()) {
            String key = (String)enumeration.nextElement();
            if (p1.getProperty(key) != null) continue;
            p1.put(key, p2.getProperty(key));
        }
        return p1;
    }

    public Properties getProperties() {
        return this.resourceProperties;
    }

    public static String getString(Object caller, String key) throws MissingResourceException {
        return RB.getMessage(caller, BASE_NAME, null, key, null);
    }

    public static String getString(Object caller, String key, Object arg0) throws MissingResourceException {
        Object[] o = new Object[]{arg0};
        return RB.getMessage(caller, BASE_NAME, null, key, o);
    }

    public static String getString(Object caller, String key, Object arg0, Object arg1) throws MissingResourceException {
        Object[] o = new Object[]{arg0, arg1};
        return RB.getMessage(caller, BASE_NAME, null, key, o);
    }

    public static String getString(Object caller, String key, Object arg0, Object arg1, Object arg2) throws MissingResourceException {
        Object[] o = new Object[]{arg0, arg1, arg2};
        return RB.getMessage(caller, BASE_NAME, null, key, o);
    }

    public static String getString(Object caller, String key, Object arg0, Object arg1, Object arg2, Object arg3) throws MissingResourceException {
        Object[] o = new Object[]{arg0, arg1, arg2, arg3};
        return RB.getMessage(caller, BASE_NAME, null, key, o);
    }

    public static String getString(Object caller, String key, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) throws MissingResourceException {
        Object[] o = new Object[]{arg0, arg1, arg2, arg3, arg4};
        return RB.getMessage(caller, BASE_NAME, null, key, o);
    }

    public static String getString(Object caller, String key, Object[] args) throws MissingResourceException {
        return RB.getMessage(caller, BASE_NAME, null, key, args);
    }

    public static String getString(Object caller, Locale locale, String key) throws MissingResourceException {
        return RB.getMessage(caller, BASE_NAME, locale, key, null);
    }

    public static String getString(Object caller, Locale locale, String key, Object arg0) throws MissingResourceException {
        Object[] o = new Object[]{arg0};
        return RB.getMessage(caller, BASE_NAME, locale, key, o);
    }

    public static String getString(Object caller, Locale locale, String key, Object arg0, Object arg1) throws MissingResourceException {
        Object[] o = new Object[]{arg0, arg1};
        return RB.getMessage(caller, BASE_NAME, locale, key, o);
    }

    public static String getString(Object caller, Locale locale, String key, Object arg0, Object arg1, Object arg2) throws MissingResourceException {
        Object[] o = new Object[]{arg0, arg1, arg2};
        return RB.getMessage(caller, BASE_NAME, locale, key, o);
    }

    public static String getString(Object caller, Locale locale, String key, Object arg0, Object arg1, Object arg2, Object arg3) throws MissingResourceException {
        Object[] o = new Object[]{arg0, arg1, arg2, arg3};
        return RB.getMessage(caller, BASE_NAME, locale, key, o);
    }

    public static String getString(Object caller, Locale locale, String key, Object arg0, Object arg1, Object arg2, Object arg3, Object arg4) throws MissingResourceException {
        Object[] o = new Object[]{arg0, arg1, arg2, arg3, arg4};
        return RB.getMessage(caller, BASE_NAME, locale, key, o);
    }

    public static String getString(Object caller, Locale locale, String key, Object[] args) throws MissingResourceException {
        return RB.getMessage(caller, BASE_NAME, locale, key, args);
    }

    public static String getMessage(Object caller, String basename, Locale locale, String key, Object[] args) throws MissingResourceException {
        String msg = null;
        MissingResourceException firstEx = null;
        String fullName = null;
        Class<?> curClass = null;
        boolean didNull = false;
        if (caller != null) {
            curClass = caller instanceof Class ? (Class<?>)caller : caller.getClass();
        }
        while (msg == null) {
            String pkgName;
            int pos;
            fullName = curClass != null ? ((pos = (pkgName = curClass.getName()).lastIndexOf(".")) > 0 ? pkgName.substring(0, pos + 1).replace('.', '/') + basename : basename) : basename;
            try {
                RB rb = new RB(caller, fullName, locale);
                msg = rb.getString(key, args);
            }
            catch (MissingResourceException ex) {
                if (curClass == null) {
                    throw ex;
                }
                if (firstEx == null) {
                    firstEx = ex;
                }
                if ((curClass = curClass.getSuperclass()) == null) {
                    if (didNull) {
                        throw firstEx;
                    }
                    didNull = true;
                    caller = null;
                    continue;
                }
                String cname = curClass.getName();
                if (!cname.startsWith("java.") && !cname.startsWith("javax.")) continue;
                if (didNull) {
                    throw firstEx;
                }
                didNull = true;
                caller = null;
                curClass = null;
            }
        }
        return msg;
    }

    public static void clearCache() {
        propertyCache.clear();
    }
}

