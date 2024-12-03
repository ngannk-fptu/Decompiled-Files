/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaPersister
 *  com.atlassian.core.util.FileUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.setup.bandana;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaPersister;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaConfigMap;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.xstream.ConfluenceXStreamManager;
import com.atlassian.core.util.FileUtils;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public class ConfluenceBandanaPersister
implements BandanaPersister {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceBandanaPersister.class);
    public static final String CONFIG_DIRECTORY_NAME = "config";
    public static final String GLOBAL_CONFIG_FILE_NAME = "confluence-global.bandana.xml";
    public static final String SPACE_CONFIG_FILE_NAME = "confluence-space.bandana.xml";
    private ConfluenceXStreamManager xStreamManager;
    private BootstrapManager bootstrapManager;

    public Object retrieve(BandanaContext context, String key) {
        ConfluenceBandanaContext cContext = (ConfluenceBandanaContext)context;
        if (this.hasExistingConfigMap(cContext)) {
            return this.loadBandanaMap(cContext).get(key);
        }
        return null;
    }

    public Map retrieve(BandanaContext context) {
        return this.loadBandanaMap((ConfluenceBandanaContext)context).getValues();
    }

    public Iterable<String> retrieveKeys(BandanaContext bandanaContext) {
        return this.loadBandanaMap((ConfluenceBandanaContext)bandanaContext).getValues().keySet();
    }

    public void store(BandanaContext context, String key, Object configuration) {
        ConfluenceBandanaConfigMap map;
        ConfluenceBandanaContext cContext = (ConfluenceBandanaContext)context;
        if (this.hasExistingConfigMap(cContext)) {
            map = this.loadBandanaMap(cContext);
        } else {
            map = new ConfluenceBandanaConfigMap();
            map.setContext(cContext);
        }
        map.put(key, configuration);
        this.saveBandanaMap(map);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private ConfluenceBandanaConfigMap loadBandanaMap(ConfluenceBandanaContext cContext) {
        try {
            File configFile = this.getConfigFile(cContext);
            if (!configFile.exists()) {
                throw new IllegalArgumentException("Configuration for context: " + cContext + " does not exist?");
            }
            try (FileInputStream fis = new FileInputStream(configFile);){
                ConfluenceBandanaConfigMap confluenceBandanaConfigMap;
                try (BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream)fis, this.getEncoding()));){
                    confluenceBandanaConfigMap = (ConfluenceBandanaConfigMap)this.xStreamManager.getConfluenceXStream().fromXML(reader);
                }
                return confluenceBandanaConfigMap;
            }
        }
        catch (Throwable e) {
            log.error("Error loading stream for context : " + cContext, e);
            ConfluenceBandanaConfigMap newMap = new ConfluenceBandanaConfigMap();
            newMap.setContext(cContext);
            return newMap;
        }
    }

    private synchronized void saveBandanaMap(ConfluenceBandanaConfigMap map) {
        try {
            File configFile = this.getConfigFile(map.getContext());
            FileUtils.ensureFileAndPathExist((File)configFile);
            try (OutputStreamWriter writer = new OutputStreamWriter((OutputStream)new FileOutputStream(configFile), this.getEncoding());){
                this.xStreamManager.getConfluenceXStream().toXML(map, writer);
            }
        }
        catch (IOException e) {
            log.error("Error writing bandana map for context: " + map.getContext(), (Throwable)e);
        }
    }

    private String getEncoding() {
        return "UTF-8";
    }

    private File getConfigFile(ConfluenceBandanaContext cContext) {
        String path = this.getBasePath();
        if (cContext.getSpaceKey() != null) {
            return new File(new File(this.getBasePath(), cContext.getSpaceKey()), SPACE_CONFIG_FILE_NAME);
        }
        return new File(path, GLOBAL_CONFIG_FILE_NAME);
    }

    protected String getBasePath() {
        return this.bootstrapManager.getConfluenceHome() + File.separator + CONFIG_DIRECTORY_NAME + File.separator;
    }

    private boolean hasExistingConfigMap(ConfluenceBandanaContext cContext) {
        return this.getConfigFile(cContext).exists();
    }

    public void setxStreamManager(ConfluenceXStreamManager xStreamManager) {
        this.xStreamManager = xStreamManager;
    }

    public void setBootstrapManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    public void flushCaches() {
        throw new UnsupportedOperationException();
    }

    public void remove(BandanaContext bandanaContext) {
        ConfluenceBandanaContext confluenceBandanaContext = (ConfluenceBandanaContext)bandanaContext;
        String spaceKey = confluenceBandanaContext.getSpaceKey();
        File configFile = this.getConfigFile(confluenceBandanaContext);
        if (StringUtils.isNotBlank((CharSequence)spaceKey)) {
            FileUtils.deleteDir((File)configFile.getParentFile());
        } else {
            configFile.delete();
        }
    }

    public void remove(BandanaContext context, String key) {
        ConfluenceBandanaConfigMap map = this.loadBandanaMap((ConfluenceBandanaContext)context);
        map.getValues().remove(key);
        this.saveBandanaMap(map);
    }
}

