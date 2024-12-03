/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.pages.persistence.dao;

import com.atlassian.confluence.pages.persistence.dao.FileLocationResolver;
import java.io.File;

public class SimpleFileLocationResolver
implements FileLocationResolver {
    File locationDir;

    public SimpleFileLocationResolver(File locationFile) {
        this.locationDir = locationFile;
    }

    public SimpleFileLocationResolver(String location) {
        this.locationDir = new File(location);
    }

    @Override
    public File getFileLocation() {
        return this.locationDir;
    }
}

