/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.exception.InfrastructureException
 */
package com.atlassian.confluence.setup;

import com.atlassian.confluence.plugin.PluginParentDirectoryLocator;
import com.atlassian.core.exception.InfrastructureException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileAttribute;

public class TempPluginParentDirectoryLocator
implements PluginParentDirectoryLocator {
    private final File tempDirectory;

    public TempPluginParentDirectoryLocator() {
        try {
            this.tempDirectory = this.createDirectory();
        }
        catch (IOException e) {
            throw new InfrastructureException("Unable to create temporary directory during setup. Please check that the directory " + System.getProperty("java.io.tmpdir") + " can be written to by Confluence." + e.getMessage(), (Throwable)e);
        }
    }

    private File createDirectory() throws IOException {
        return Files.createTempDirectory(null, new FileAttribute[0]).toFile();
    }

    @Override
    public File getDirectory() {
        return this.tempDirectory;
    }
}

