/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config;

import com.typesafe.config.ConfigIncludeContext;
import com.typesafe.config.ConfigObject;
import java.io.File;

public interface ConfigIncluderFile {
    public ConfigObject includeFile(ConfigIncludeContext var1, File var2);
}

