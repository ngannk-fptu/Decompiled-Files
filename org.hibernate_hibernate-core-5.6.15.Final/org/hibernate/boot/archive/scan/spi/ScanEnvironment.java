/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.archive.scan.spi;

import java.net.URL;
import java.util.List;

public interface ScanEnvironment {
    public URL getRootUrl();

    public List<URL> getNonRootUrls();

    public List<String> getExplicitlyListedClassNames();

    public List<String> getExplicitlyListedMappingFiles();
}

