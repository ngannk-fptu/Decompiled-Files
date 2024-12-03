/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 *  com.atlassian.bandana.BandanaPersister
 *  com.atlassian.config.util.BootstrapUtils
 *  com.atlassian.core.util.FileUtils
 */
package com.atlassian.confluence.util;

import com.atlassian.bandana.BandanaContext;
import com.atlassian.bandana.BandanaPersister;
import com.atlassian.config.util.BootstrapUtils;
import com.atlassian.confluence.setup.bandana.ConfluenceBandanaContext;
import com.atlassian.confluence.setup.bandana.FileBandanaXmlParser;
import com.atlassian.confluence.setup.xstream.ConfluenceXStreamManager;
import com.atlassian.core.util.FileUtils;
import java.io.File;
import java.util.Map;

public class BandanaConfigMigrator {
    private ConfluenceXStreamManager confluenceXStreamManager;
    private BandanaPersister persister;

    public void run() {
        this.run(new File(BootstrapUtils.getBootstrapManager().getApplicationHome() + "/config/"));
    }

    public void run(File baseConfigPath) {
        if (baseConfigPath == null || !baseConfigPath.exists()) {
            return;
        }
        FileBandanaXmlParser bandanaXmlParser = new FileBandanaXmlParser(baseConfigPath);
        Map globalBandanaEntries = bandanaXmlParser.getEntries(ConfluenceBandanaContext.GLOBAL_CONTEXT);
        for (Map.Entry entry : globalBandanaEntries.entrySet()) {
            String xmlValue = (String)entry.getValue();
            Object value = this.confluenceXStreamManager.getConfluenceXStream().fromXML(xmlValue);
            this.persister.store((BandanaContext)ConfluenceBandanaContext.GLOBAL_CONTEXT, (String)entry.getKey(), value);
        }
        File globalConfigFile = new File(baseConfigPath, "confluence-global.bandana.xml");
        globalConfigFile.delete();
        File[] subDirs = baseConfigPath.listFiles(File::isDirectory);
        if (subDirs != null) {
            for (File subDir : subDirs) {
                String spaceKey = subDir.getName();
                ConfluenceBandanaContext context = new ConfluenceBandanaContext(spaceKey);
                Map spaceBandanaEntries = bandanaXmlParser.getEntries(context);
                for (Map.Entry entry : spaceBandanaEntries.entrySet()) {
                    String xmlValue = (String)entry.getValue();
                    Object value = this.confluenceXStreamManager.getConfluenceXStream().fromXML(xmlValue);
                    this.persister.store((BandanaContext)context, (String)entry.getKey(), value);
                }
                FileUtils.deleteDir((File)subDir);
            }
        }
    }

    public void setBandanaPersister(BandanaPersister persister) {
        this.persister = persister;
    }

    public void setxStreamManager(ConfluenceXStreamManager confluenceXStreamManager) {
        this.confluenceXStreamManager = confluenceXStreamManager;
    }
}

