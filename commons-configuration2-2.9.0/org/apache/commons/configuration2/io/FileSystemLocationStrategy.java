/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.io;

import java.net.URL;
import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileSystem;

public class FileSystemLocationStrategy
implements FileLocationStrategy {
    @Override
    public URL locate(FileSystem fileSystem, FileLocator locator) {
        return fileSystem.locateFromURL(locator.getBasePath(), locator.getFileName());
    }
}

