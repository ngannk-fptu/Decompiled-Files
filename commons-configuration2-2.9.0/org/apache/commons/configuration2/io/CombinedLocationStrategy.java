/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.io;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileSystem;

public class CombinedLocationStrategy
implements FileLocationStrategy {
    private final Collection<FileLocationStrategy> subStrategies;

    public CombinedLocationStrategy(Collection<? extends FileLocationStrategy> subs) {
        if (subs == null) {
            throw new IllegalArgumentException("Collection with sub strategies must not be null!");
        }
        this.subStrategies = Collections.unmodifiableCollection(new ArrayList<FileLocationStrategy>(subs));
        if (this.subStrategies.contains(null)) {
            throw new IllegalArgumentException("Collection with sub strategies contains null entry!");
        }
    }

    public Collection<FileLocationStrategy> getSubStrategies() {
        return this.subStrategies;
    }

    @Override
    public URL locate(FileSystem fileSystem, FileLocator locator) {
        for (FileLocationStrategy sub : this.getSubStrategies()) {
            URL url = sub.locate(fileSystem, locator);
            if (url == null) continue;
            return url;
        }
        return null;
    }
}

