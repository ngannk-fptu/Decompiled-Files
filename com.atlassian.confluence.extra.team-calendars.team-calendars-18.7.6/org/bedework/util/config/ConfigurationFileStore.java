/*
 * Decompiled with CFR 0.152.
 */
package org.bedework.util.config;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import org.bedework.util.config.ConfigBase;
import org.bedework.util.config.ConfigException;
import org.bedework.util.config.ConfigurationStore;
import org.bedework.util.config.FileResourceControl;
import org.bedework.util.misc.Util;

public class ConfigurationFileStore
implements ConfigurationStore {
    private String dirPath;
    private ResourceBundle.Control resourceControl;

    public ConfigurationFileStore(String dirPath) throws ConfigException {
        try {
            this.dirPath = dirPath;
            File f = new File(dirPath);
            if (!f.exists() && !f.mkdir()) {
                throw new ConfigException("Unable to create directory " + dirPath);
            }
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
    public boolean readOnly() {
        return false;
    }

    @Override
    public String getLocation() throws ConfigException {
        return this.dirPath;
    }

    @Override
    public void saveConfiguration(ConfigBase config) throws ConfigException {
        try {
            File f = new File(this.dirPath + config.getName() + ".xml");
            FileWriter fw = new FileWriter(f);
            config.toXml(fw);
            fw.close();
        }
        catch (ConfigException ce) {
            throw ce;
        }
        catch (Throwable t) {
            throw new ConfigException(t);
        }
    }

    @Override
    public ConfigBase getConfig(String name) throws ConfigException {
        return this.getConfig(name, null);
    }

    @Override
    public ConfigBase getConfig(String name, Class cl) throws ConfigException {
        ConfigBase configBase;
        FileInputStream fis = null;
        try {
            ConfigBase config;
            File f = new File(this.dirPath + name + ".xml");
            if (!f.exists()) {
                ConfigBase configBase2 = null;
                return configBase2;
            }
            fis = new FileInputStream(f);
            configBase = config = new ConfigBase().fromXml(fis, cl);
        }
        catch (ConfigException ce) {
            throw ce;
        }
        catch (Throwable t) {
            throw new ConfigException(t);
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (Throwable throwable) {}
            }
        }
        return configBase;
    }

    @Override
    public List<String> getConfigs() throws ConfigException {
        try {
            File dir = new File(this.dirPath);
            File[] files = dir.listFiles(new FilesOnly());
            ArrayList<String> names = new ArrayList<String>();
            for (File f : files) {
                String nm = f.getName();
                names.add(nm.substring(0, nm.indexOf(".xml")));
            }
            return names;
        }
        catch (Throwable t) {
            throw new ConfigException(t);
        }
    }

    @Override
    public ConfigurationStore getStore(String name) throws ConfigException {
        try {
            File[] files;
            File dir = new File(this.dirPath);
            String newPath = this.dirPath + name;
            for (File f : files = dir.listFiles(new DirsOnly())) {
                if (!f.getName().equals(name)) continue;
                return new ConfigurationFileStore(newPath);
            }
            File newDir = new File(newPath);
            if (!newDir.mkdir()) {
                throw new ConfigException("Unable to create directory " + newPath);
            }
            return new ConfigurationFileStore(newPath);
        }
        catch (Throwable t) {
            throw new ConfigException(t);
        }
    }

    @Override
    public ResourceBundle getResource(String name, String locale) throws ConfigException {
        try {
            if (this.resourceControl == null) {
                this.resourceControl = new FileResourceControl(this.dirPath);
            }
            Locale loc = locale == null ? Locale.getDefault() : Util.makeLocale(locale);
            return ResourceBundle.getBundle(name, loc, this.resourceControl);
        }
        catch (ConfigException ce) {
            throw ce;
        }
        catch (Throwable t) {
            throw new ConfigException(t);
        }
    }

    private static class DirsOnly
    implements FileFilter {
        private DirsOnly() {
        }

        @Override
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    }

    private static class FilesOnly
    implements FileFilter {
        private FilesOnly() {
        }

        @Override
        public boolean accept(File pathname) {
            if (!pathname.isFile()) {
                return false;
            }
            return pathname.getName().endsWith(".xml");
        }
    }
}

