/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import java.io.File;
import java.net.URL;
import java.util.Map;
import org.apache.commons.configuration2.builder.BasicBuilderParameters;
import org.apache.commons.configuration2.builder.FileBasedBuilderProperties;
import org.apache.commons.configuration2.builder.ReloadingDetectorFactory;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.FileSystem;
import org.apache.commons.configuration2.io.URLConnectionOptions;

public class FileBasedBuilderParametersImpl
extends BasicBuilderParameters
implements FileBasedBuilderProperties<FileBasedBuilderParametersImpl> {
    private static final String PARAM_KEY = "config-fileBased";
    private static final String PROP_REFRESH_DELAY = "reloadingRefreshDelay";
    private static final String PROP_DETECTOR_FACTORY = "reloadingDetectorFactory";
    private FileHandler fileHandler;
    private ReloadingDetectorFactory reloadingDetectorFactory;
    private Long reloadingRefreshDelay;

    public FileBasedBuilderParametersImpl() {
        this(null);
    }

    public FileBasedBuilderParametersImpl(FileHandler handler) {
        this.fileHandler = handler != null ? handler : new FileHandler();
    }

    public static FileBasedBuilderParametersImpl fromMap(Map<String, ?> map) {
        FileBasedBuilderParametersImpl params = new FileBasedBuilderParametersImpl(FileHandler.fromMap(map));
        if (map != null) {
            params.setReloadingRefreshDelay((Long)map.get(PROP_REFRESH_DELAY));
            params.setReloadingDetectorFactory((ReloadingDetectorFactory)map.get(PROP_DETECTOR_FACTORY));
        }
        return params;
    }

    public static FileBasedBuilderParametersImpl fromParameters(Map<String, ?> params) {
        return FileBasedBuilderParametersImpl.fromParameters(params, false);
    }

    public static FileBasedBuilderParametersImpl fromParameters(Map<String, ?> params, boolean createIfMissing) {
        if (params == null) {
            throw new IllegalArgumentException("Parameters map must not be null!");
        }
        FileBasedBuilderParametersImpl instance = (FileBasedBuilderParametersImpl)params.get(PARAM_KEY);
        if (instance == null && createIfMissing) {
            instance = new FileBasedBuilderParametersImpl();
        }
        return instance;
    }

    @Override
    public FileBasedBuilderParametersImpl clone() {
        FileBasedBuilderParametersImpl copy = (FileBasedBuilderParametersImpl)super.clone();
        copy.fileHandler = new FileHandler(this.fileHandler.getContent(), this.fileHandler);
        return copy;
    }

    public FileHandler getFileHandler() {
        return this.fileHandler;
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = super.getParameters();
        params.put(PARAM_KEY, this);
        return params;
    }

    public ReloadingDetectorFactory getReloadingDetectorFactory() {
        return this.reloadingDetectorFactory;
    }

    public Long getReloadingRefreshDelay() {
        return this.reloadingRefreshDelay;
    }

    @Override
    public void inheritFrom(Map<String, ?> source) {
        super.inheritFrom(source);
        FileBasedBuilderParametersImpl srcParams = FileBasedBuilderParametersImpl.fromParameters(source);
        if (srcParams != null) {
            this.setFileSystem(srcParams.getFileHandler().getFileSystem());
            this.setLocationStrategy(srcParams.getFileHandler().getLocationStrategy());
            if (srcParams.getFileHandler().getEncoding() != null) {
                this.setEncoding(srcParams.getFileHandler().getEncoding());
            }
            if (srcParams.getReloadingDetectorFactory() != null) {
                this.setReloadingDetectorFactory(srcParams.getReloadingDetectorFactory());
            }
            if (srcParams.getReloadingRefreshDelay() != null) {
                this.setReloadingRefreshDelay(srcParams.getReloadingRefreshDelay());
            }
        }
    }

    @Override
    public FileBasedBuilderParametersImpl setBasePath(String path) {
        this.getFileHandler().setBasePath(path);
        return this;
    }

    @Override
    public FileBasedBuilderParametersImpl setEncoding(String enc) {
        this.getFileHandler().setEncoding(enc);
        return this;
    }

    @Override
    public FileBasedBuilderParametersImpl setFile(File file) {
        this.getFileHandler().setFile(file);
        return this;
    }

    @Override
    public FileBasedBuilderParametersImpl setFileName(String name) {
        this.getFileHandler().setFileName(name);
        return this;
    }

    @Override
    public FileBasedBuilderParametersImpl setFileSystem(FileSystem fs) {
        this.getFileHandler().setFileSystem(fs);
        return this;
    }

    @Override
    public FileBasedBuilderParametersImpl setLocationStrategy(FileLocationStrategy strategy) {
        this.getFileHandler().setLocationStrategy(strategy);
        return this;
    }

    @Override
    public FileBasedBuilderParametersImpl setPath(String path) {
        this.getFileHandler().setPath(path);
        return this;
    }

    @Override
    public FileBasedBuilderParametersImpl setReloadingDetectorFactory(ReloadingDetectorFactory reloadingDetectorFactory) {
        this.reloadingDetectorFactory = reloadingDetectorFactory;
        return this;
    }

    @Override
    public FileBasedBuilderParametersImpl setReloadingRefreshDelay(Long reloadingRefreshDelay) {
        this.reloadingRefreshDelay = reloadingRefreshDelay;
        return this;
    }

    @Override
    public FileBasedBuilderParametersImpl setURL(URL url) {
        this.getFileHandler().setURL(url);
        return this;
    }

    @Override
    public FileBasedBuilderParametersImpl setURL(URL url, URLConnectionOptions urlConnectionOptions) {
        this.getFileHandler().setURL(url, urlConnectionOptions);
        return this;
    }
}

