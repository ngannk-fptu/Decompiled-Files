/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.reloading;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.commons.configuration2.io.FileHandler;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.apache.commons.configuration2.reloading.ReloadingDetector;

public class FileHandlerReloadingDetector
implements ReloadingDetector {
    private static final String JAR_PROTOCOL = "jar";
    private static final int DEFAULT_REFRESH_DELAY_MILLIS = 5000;
    private final FileHandler fileHandler;
    private final long refreshDelayMillis;
    private long lastModifiedMillis;
    private long lastCheckedMillis;

    public FileHandlerReloadingDetector(FileHandler handler, long refreshDelayMillis) {
        this.fileHandler = handler != null ? handler : new FileHandler();
        this.refreshDelayMillis = refreshDelayMillis;
    }

    public FileHandlerReloadingDetector(FileHandler handler) {
        this(handler, 5000L);
    }

    public FileHandlerReloadingDetector() {
        this(null);
    }

    public FileHandler getFileHandler() {
        return this.fileHandler;
    }

    public long getRefreshDelay() {
        return this.refreshDelayMillis;
    }

    @Override
    public boolean isReloadingRequired() {
        long nowMillis = System.currentTimeMillis();
        if (nowMillis >= this.lastCheckedMillis + this.getRefreshDelay()) {
            this.lastCheckedMillis = nowMillis;
            long modifiedMillis = this.getLastModificationDate();
            if (modifiedMillis > 0L) {
                if (this.lastModifiedMillis != 0L) {
                    return modifiedMillis != this.lastModifiedMillis;
                }
                this.updateLastModified(modifiedMillis);
            }
        }
        return false;
    }

    @Override
    public void reloadingPerformed() {
        this.updateLastModified(this.getLastModificationDate());
    }

    public void refresh() {
        this.updateLastModified(this.getLastModificationDate());
    }

    protected long getLastModificationDate() {
        File file = this.getExistingFile();
        return file != null ? file.lastModified() : 0L;
    }

    protected void updateLastModified(long timeMillis) {
        this.lastModifiedMillis = timeMillis;
    }

    protected File getFile() {
        URL url = this.getFileHandler().getURL();
        return url != null ? FileHandlerReloadingDetector.fileFromURL(url) : this.getFileHandler().getFile();
    }

    private File getExistingFile() {
        File file = this.getFile();
        if (file != null && !file.exists()) {
            file = null;
        }
        return file;
    }

    private static File fileFromURL(URL url) {
        if (JAR_PROTOCOL.equals(url.getProtocol())) {
            String path = url.getPath();
            try {
                return FileLocatorUtils.fileFromURL(new URL(path.substring(0, path.indexOf(33))));
            }
            catch (MalformedURLException mex) {
                return null;
            }
        }
        return FileLocatorUtils.fileFromURL(url);
    }
}

