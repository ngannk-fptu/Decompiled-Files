/*
 * Decompiled with CFR 0.152.
 */
package freemarker.cache;

import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;

final class TemplateLoaderUtils {
    private TemplateLoaderUtils() {
    }

    public static String getClassNameForToString(TemplateLoader templateLoader) {
        Class<?> tlClass = templateLoader.getClass();
        Package tlPackage = tlClass.getPackage();
        return tlPackage == Configuration.class.getPackage() || tlPackage == TemplateLoader.class.getPackage() ? tlClass.getSimpleName() : tlClass.getName();
    }
}

