/*
 * Decompiled with CFR 0.152.
 */
package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.spi.ContextAwareBase;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ContextUtil
extends ContextAwareBase {
    static final String GROOVY_RUNTIME_PACKAGE = "org.codehaus.groovy.runtime";

    public ContextUtil(Context context) {
        this.setContext(context);
    }

    public void addProperties(Properties props) {
        if (props == null) {
            return;
        }
        for (Map.Entry<Object, Object> e : props.entrySet()) {
            String key = (String)e.getKey();
            this.context.putProperty(key, (String)e.getValue());
        }
    }

    public static Map<String, String> getFilenameCollisionMap(Context context) {
        if (context == null) {
            return null;
        }
        Map map = (Map)context.getObject("FA_FILENAMES_MAP");
        return map;
    }

    public static Map<String, FileNamePattern> getFilenamePatternCollisionMap(Context context) {
        if (context == null) {
            return null;
        }
        Map map = (Map)context.getObject("RFA_FILENAME_PATTERN_COLLISION_MAP");
        return map;
    }

    public void addGroovyPackages(List<String> frameworkPackages) {
        this.addFrameworkPackage(frameworkPackages, GROOVY_RUNTIME_PACKAGE);
    }

    public void addFrameworkPackage(List<String> frameworkPackages, String packageName) {
        if (!frameworkPackages.contains(packageName)) {
            frameworkPackages.add(packageName);
        }
    }
}

