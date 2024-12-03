/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.tika.pipes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Set;
import org.apache.tika.exception.TikaConfigException;
import org.apache.tika.pipes.PipesClient;
import org.apache.tika.pipes.PipesConfigBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipesConfig
extends PipesConfigBase {
    private static final Logger LOG = LoggerFactory.getLogger(PipesClient.class);
    private long maxWaitForClientMillis = 60000L;

    public static PipesConfig load(Path tikaConfig) throws IOException, TikaConfigException {
        PipesConfig pipesConfig = new PipesConfig();
        try (InputStream is = Files.newInputStream(tikaConfig, new OpenOption[0]);){
            Set<String> set = pipesConfig.configure("pipes", is);
        }
        if (pipesConfig.getTikaConfig() == null) {
            LOG.debug("A separate tikaConfig was not specified in the <pipes/> element in the  config file; will use {} for pipes", (Object)tikaConfig);
            pipesConfig.setTikaConfig(tikaConfig);
        }
        return pipesConfig;
    }

    private PipesConfig() {
    }

    public long getMaxWaitForClientMillis() {
        return this.maxWaitForClientMillis;
    }

    public void setMaxWaitForClientMillis(long maxWaitForClientMillis) {
        this.maxWaitForClientMillis = maxWaitForClientMillis;
    }
}

