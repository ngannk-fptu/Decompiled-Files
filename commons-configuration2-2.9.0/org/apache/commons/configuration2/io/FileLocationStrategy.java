/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.io;

import java.net.URL;
import org.apache.commons.configuration2.io.FileLocator;
import org.apache.commons.configuration2.io.FileSystem;

public interface FileLocationStrategy {
    public URL locate(FileSystem var1, FileLocator var2);
}

