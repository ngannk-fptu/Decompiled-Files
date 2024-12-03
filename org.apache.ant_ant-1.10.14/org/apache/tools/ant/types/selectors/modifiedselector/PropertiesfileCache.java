/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors.modifiedselector;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.Iterator;
import java.util.Properties;
import org.apache.tools.ant.types.selectors.modifiedselector.Cache;

public class PropertiesfileCache
implements Cache {
    private File cachefile = null;
    private Properties cache = new Properties();
    private boolean cacheLoaded = false;
    private boolean cacheDirty = true;

    public PropertiesfileCache() {
    }

    public PropertiesfileCache(File cachefile) {
        this.cachefile = cachefile;
    }

    public void setCachefile(File file) {
        this.cachefile = file;
    }

    public File getCachefile() {
        return this.cachefile;
    }

    @Override
    public boolean isValid() {
        return this.cachefile != null;
    }

    @Override
    public void load() {
        if (this.cachefile != null && this.cachefile.isFile() && this.cachefile.canRead()) {
            try (BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(this.cachefile.toPath(), new OpenOption[0]));){
                this.cache.load(bis);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.cacheLoaded = true;
        this.cacheDirty = false;
    }

    @Override
    public void save() {
        if (!this.cacheDirty) {
            return;
        }
        if (this.cachefile != null && this.cache.propertyNames().hasMoreElements()) {
            try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(this.cachefile.toPath(), new OpenOption[0]));){
                this.cache.store(bos, null);
                bos.flush();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        this.cacheDirty = false;
    }

    @Override
    public void delete() {
        this.cache = new Properties();
        this.cachefile.delete();
        this.cacheLoaded = true;
        this.cacheDirty = false;
    }

    @Override
    public Object get(Object key) {
        if (!this.cacheLoaded) {
            this.load();
        }
        try {
            return this.cache.getProperty(String.valueOf(key));
        }
        catch (ClassCastException e) {
            return null;
        }
    }

    @Override
    public void put(Object key, Object value) {
        this.cache.put(String.valueOf(key), String.valueOf(value));
        this.cacheDirty = true;
    }

    @Override
    public Iterator<String> iterator() {
        return this.cache.stringPropertyNames().iterator();
    }

    public String toString() {
        return String.format("<PropertiesfileCache:cachefile=%s;noOfEntries=%d>", this.cachefile, this.cache.size());
    }
}

