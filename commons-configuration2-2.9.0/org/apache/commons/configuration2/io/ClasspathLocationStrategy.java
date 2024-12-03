/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2.io;

import java.net.URL;
import org.apache.commons.configuration2.io.FileLocationStrategy;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileLocatorUtils;
import org.apache.commons.configuration2.io.FileSystem;
import org.apache.commons.lang3.StringUtils;

public class ClasspathLocationStrategy
implements FileLocationStrategy {
    @Override
    public URL locate(FileSystem fileSystem, FileLocator locator) {
        return StringUtils.isEmpty((CharSequence)locator.getFileName()) ? null : FileLocatorUtils.getClasspathResource(locator.getFileName());
    }
}

