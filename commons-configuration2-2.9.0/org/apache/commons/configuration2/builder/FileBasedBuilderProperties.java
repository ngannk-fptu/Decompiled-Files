/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import java.io.File;
import java.net.URL;
import org.apache.commons.configuration2.builder.ReloadingDetectorFactory;
import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.FileSystem;
import org.apache.commons.configuration2.io.URLConnectionOptions;

public interface FileBasedBuilderProperties<T> {
    public T setBasePath(String var1);

    public T setEncoding(String var1);

    public T setFile(File var1);

    public T setFileName(String var1);

    public T setFileSystem(FileSystem var1);

    public T setLocationStrategy(FileLocationStrategy var1);

    public T setPath(String var1);

    public T setReloadingDetectorFactory(ReloadingDetectorFactory var1);

    public T setReloadingRefreshDelay(Long var1);

    public T setURL(URL var1);

    default public T setURL(URL url, URLConnectionOptions urlConnectionOptions) {
        return this.setURL(url);
    }
}

