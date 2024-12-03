/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.profile.path.config;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.profile.path.AwsDirectoryBasePathProvider;
import java.io.File;

@SdkInternalApi
public class SharedConfigDefaultLocationProvider
extends AwsDirectoryBasePathProvider {
    private static final String DEFAULT_CONFIG_FILE_NAME = "config";

    @Override
    public File getLocation() {
        File file = new File(this.getAwsDirectory(), DEFAULT_CONFIG_FILE_NAME);
        if (file.exists() && file.isFile()) {
            return file;
        }
        return null;
    }
}

