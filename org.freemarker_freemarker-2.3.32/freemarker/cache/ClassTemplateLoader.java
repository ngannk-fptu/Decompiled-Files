/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.TemplateLoaderUtils;
import freemarker.cache.URLTemplateLoader;
import freemarker.template.utility.NullArgumentException;
import freemarker.template.utility.StringUtil;
import java.net.URL;

public class ClassTemplateLoader
extends URLTemplateLoader {
    private final Class<?> resourceLoaderClass;
    private final ClassLoader classLoader;
    private final String basePackagePath;

    @Deprecated
    public ClassTemplateLoader() {
        this(null, true, null, "/");
    }

    @Deprecated
    public ClassTemplateLoader(Class<?> resourceLoaderClass) {
        this(resourceLoaderClass, "");
    }

    public ClassTemplateLoader(Class<?> resourceLoaderClass, String basePackagePath) {
        this(resourceLoaderClass, false, null, basePackagePath);
    }

    public ClassTemplateLoader(ClassLoader classLoader, String basePackagePath) {
        this(null, true, classLoader, basePackagePath);
    }

    private ClassTemplateLoader(Class<?> resourceLoaderClass, boolean allowNullResourceLoaderClass, ClassLoader classLoader, String basePackagePath) {
        if (!allowNullResourceLoaderClass) {
            NullArgumentException.check("resourceLoaderClass", resourceLoaderClass);
        }
        NullArgumentException.check("basePackagePath", basePackagePath);
        Class<?> clazz = classLoader == null ? (resourceLoaderClass == null ? this.getClass() : resourceLoaderClass) : (this.resourceLoaderClass = null);
        if (this.resourceLoaderClass == null && classLoader == null) {
            throw new NullArgumentException("classLoader");
        }
        this.classLoader = classLoader;
        String canonBasePackagePath = ClassTemplateLoader.canonicalizePrefix(basePackagePath);
        if (this.classLoader != null && canonBasePackagePath.startsWith("/")) {
            canonBasePackagePath = canonBasePackagePath.substring(1);
        }
        this.basePackagePath = canonBasePackagePath;
    }

    @Override
    protected URL getURL(String name) {
        String fullPath = this.basePackagePath + name;
        if (this.basePackagePath.equals("/") && !ClassTemplateLoader.isSchemeless(fullPath)) {
            return null;
        }
        return this.resourceLoaderClass != null ? this.resourceLoaderClass.getResource(fullPath) : this.classLoader.getResource(fullPath);
    }

    private static boolean isSchemeless(String fullPath) {
        int i = 0;
        int ln = fullPath.length();
        if (i < ln && fullPath.charAt(i) == '/') {
            ++i;
        }
        while (i < ln) {
            char c = fullPath.charAt(i);
            if (c == '/') {
                return true;
            }
            if (c == ':') {
                return false;
            }
            ++i;
        }
        return true;
    }

    public String toString() {
        return TemplateLoaderUtils.getClassNameForToString(this) + "(" + (this.resourceLoaderClass != null ? "resourceLoaderClass=" + this.resourceLoaderClass.getName() : "classLoader=" + StringUtil.jQuote(this.classLoader)) + ", basePackagePath=" + StringUtil.jQuote(this.basePackagePath) + (this.resourceLoaderClass != null ? (this.basePackagePath.startsWith("/") ? "" : " /* relatively to resourceLoaderClass pkg */") : "") + ")";
    }

    public Class getResourceLoaderClass() {
        return this.resourceLoaderClass;
    }

    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    public String getBasePackagePath() {
        return this.basePackagePath;
    }
}

