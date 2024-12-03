/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2.io;

import java.io.File;
import java.net.URL;
import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.apache.commons.configuration2.io.FileSystem;
import org.apache.commons.lang3.StringUtils;

public class BasePathLocationStrategy
implements FileLocationStrategy {
    @Override
    public URL locate(FileSystem fileSystem, FileLocator locator) {
        File file;
        if (StringUtils.isNotEmpty((CharSequence)locator.getFileName()) && (file = FileLocatorUtils.constructFile(locator.getBasePath(), locator.getFileName())).isFile()) {
            return FileLocatorUtils.convertFileToURL(file);
        }
        return null;
    }
}

