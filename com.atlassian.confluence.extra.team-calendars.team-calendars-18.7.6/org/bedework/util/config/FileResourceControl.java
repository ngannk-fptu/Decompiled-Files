/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import org.bedework.util.config.ConfigException;
import org.bedework.util.misc.Util;

public class FileResourceControl
extends ResourceBundle.Control {
    private final String dirPath;

    FileResourceControl(String dirPath) throws ConfigException {
        try {
            File f = new File(dirPath);
            if (!f.isDirectory()) {
                throw new ConfigException(dirPath + " is not a directory");
            }
            this.dirPath = f.getCanonicalPath() + File.separator;
        }
        catch (ConfigException ce) {
            throw ce;
        }
        catch (Throwable t) {
            throw new ConfigException(t);
        }
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        PropertyResourceBundle bundle;
        if (!format.equals("java.properties")) {
            throw new IllegalArgumentException("unknown format: " + format);
        }
        String bundleName = this.toBundleName(baseName, locale);
        String resourceName = this.toResourceName(bundleName, "properties");
        try (FileInputStream stream = new FileInputStream(Util.buildPath(false, this.dirPath, "/", resourceName));){
            bundle = new PropertyResourceBundle(stream);
        }
        return bundle;
    }
}

