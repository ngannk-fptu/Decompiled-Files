/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.ClassUtils
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2.builder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.XMLPropertiesConfiguration;
import org.apache.commons.configuration2.builder.AutoSaveListener;
import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.BuilderParameters;
import org.apache.commons.configuration2.builder.FileBasedBuilderParametersImpl;
import org.apache.commons.configuration2.event.ConfigurationEvent;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.io.FileBased;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

public class FileBasedConfigurationBuilder<T extends FileBasedConfiguration>
extends BasicConfigurationBuilder<T> {
    private static final Map<Class<?>, String> DEFAULT_ENCODINGS = FileBasedConfigurationBuilder.initializeDefaultEncodings();
    private FileHandler currentFileHandler;
    private AutoSaveListener autoSaveListener;
    private boolean resetParameters;

    public FileBasedConfigurationBuilder(Class<? extends T> resCls) {
        super(resCls);
    }

    public FileBasedConfigurationBuilder(Class<? extends T> resCls, Map<String, Object> params) {
        super(resCls, params);
    }

    public FileBasedConfigurationBuilder(Class<? extends T> resCls, Map<String, Object> params, boolean allowFailOnInit) {
        super(resCls, params, allowFailOnInit);
    }

    public static String getDefaultEncoding(Class<?> configClass) {
        String enc = DEFAULT_ENCODINGS.get(configClass);
        if (enc != null || configClass == null) {
            return enc;
        }
        for (Class cls : ClassUtils.getAllSuperclasses(configClass)) {
            enc = DEFAULT_ENCODINGS.get(cls);
            if (enc == null) continue;
            return enc;
        }
        for (Class cls : ClassUtils.getAllInterfaces(configClass)) {
            enc = DEFAULT_ENCODINGS.get(cls);
            if (enc == null) continue;
            return enc;
        }
        return null;
    }

    public static void setDefaultEncoding(Class<?> configClass, String encoding) {
        if (configClass == null) {
            throw new IllegalArgumentException("Configuration class must not be null!");
        }
        if (encoding == null) {
            DEFAULT_ENCODINGS.remove(configClass);
        } else {
            DEFAULT_ENCODINGS.put(configClass, encoding);
        }
    }

    @Override
    public FileBasedConfigurationBuilder<T> configure(BuilderParameters ... params) {
        super.configure(params);
        return this;
    }

    public synchronized FileHandler getFileHandler() {
        return this.currentFileHandler != null ? this.currentFileHandler : this.fetchFileHandlerFromParameters();
    }

    @Override
    public synchronized BasicConfigurationBuilder<T> setParameters(Map<String, Object> params) {
        super.setParameters(params);
        this.resetParameters = true;
        return this;
    }

    public void save() throws ConfigurationException {
        this.getFileHandler().save();
    }

    public synchronized boolean isAutoSave() {
        return this.autoSaveListener != null;
    }

    public synchronized void setAutoSave(boolean enabled) {
        if (enabled) {
            this.installAutoSaveListener();
        } else {
            this.removeAutoSaveListener();
        }
    }

    @Override
    protected void initResultInstance(T obj) throws ConfigurationException {
        super.initResultInstance(obj);
        FileHandler srcHandler = this.currentFileHandler != null && !this.resetParameters ? this.currentFileHandler : this.fetchFileHandlerFromParameters();
        this.currentFileHandler = new FileHandler((FileBased)obj, srcHandler);
        if (this.autoSaveListener != null) {
            this.autoSaveListener.updateFileHandler(this.currentFileHandler);
        }
        this.initFileHandler(this.currentFileHandler);
        this.resetParameters = false;
    }

    protected void initFileHandler(FileHandler handler) throws ConfigurationException {
        this.initEncoding(handler);
        if (handler.isLocationDefined()) {
            handler.locate();
            handler.load();
        }
    }

    private FileHandler fetchFileHandlerFromParameters() {
        FileBasedBuilderParametersImpl fileParams = FileBasedBuilderParametersImpl.fromParameters(this.getParameters(), false);
        if (fileParams == null) {
            fileParams = new FileBasedBuilderParametersImpl();
            this.addParameters(fileParams.getParameters());
        }
        return fileParams.getFileHandler();
    }

    private void installAutoSaveListener() {
        if (this.autoSaveListener == null) {
            this.autoSaveListener = new AutoSaveListener(this);
            this.addEventListener(ConfigurationEvent.ANY, this.autoSaveListener);
            this.autoSaveListener.updateFileHandler(this.getFileHandler());
        }
    }

    private void removeAutoSaveListener() {
        if (this.autoSaveListener != null) {
            this.removeEventListener(ConfigurationEvent.ANY, this.autoSaveListener);
            this.autoSaveListener.updateFileHandler(null);
            this.autoSaveListener = null;
        }
    }

    private void initEncoding(FileHandler handler) {
        String encoding;
        if (StringUtils.isEmpty((CharSequence)handler.getEncoding()) && (encoding = FileBasedConfigurationBuilder.getDefaultEncoding(this.getResultClass())) != null) {
            handler.setEncoding(encoding);
        }
    }

    private static Map<Class<?>, String> initializeDefaultEncodings() {
        ConcurrentHashMap enc = new ConcurrentHashMap();
        enc.put(PropertiesConfiguration.class, "ISO-8859-1");
        enc.put(XMLPropertiesConfiguration.class, "UTF-8");
        return enc;
    }
}

