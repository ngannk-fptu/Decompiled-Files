/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.io.CharStreams
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.service;

import com.atlassian.confluence.plugins.hipchat.emoticons.rest.AtlaskitEmoticonModel;
import com.google.common.io.CharStreams;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CachingTwitterEmoticonServiceHelper {
    private static final Logger log = LoggerFactory.getLogger(CachingTwitterEmoticonServiceHelper.class);
    private final Collection<AtlaskitEmoticonModel> twEmojisList;
    private final Map<String, AtlaskitEmoticonModel> twEmojisSkinsMap;
    private final Map<String, AtlaskitEmoticonModel> twEmojisMap;
    private final ConcurrentMap<String, String> twEmojisContentMap;

    CachingTwitterEmoticonServiceHelper(Collection<AtlaskitEmoticonModel> twEmojisList) {
        this.twEmojisList = Collections.unmodifiableCollection(twEmojisList);
        this.twEmojisSkinsMap = new HashMap<String, AtlaskitEmoticonModel>();
        this.twEmojisMap = new HashMap<String, AtlaskitEmoticonModel>();
        this.twEmojisContentMap = new ConcurrentHashMap<String, String>();
        for (AtlaskitEmoticonModel atlaskitEmoticonModel : twEmojisList) {
            this.twEmojisMap.put(atlaskitEmoticonModel.getId(), atlaskitEmoticonModel);
            if (atlaskitEmoticonModel.getSkinVariations() == null) continue;
            for (AtlaskitEmoticonModel skinVariation : atlaskitEmoticonModel.getSkinVariations()) {
                this.twEmojisSkinsMap.put(skinVariation.getId(), skinVariation);
            }
        }
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private String readImageFileContentFromDisk(AtlaskitEmoticonModel model) {
        Object filename = "";
        try {
            filename = "svg/" + model.getId() + ".svg";
            try (InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream((String)filename);){
                String string;
                try (InputStreamReader reader = new InputStreamReader(inputStream);){
                    String text;
                    string = text = CharStreams.toString((Readable)reader);
                }
                return string;
            }
        }
        catch (Exception ex) {
            log.error("Error retrieving content from '" + (String)filename + "'", (Throwable)ex);
            return "";
        }
    }

    public String getSvgFileContent(AtlaskitEmoticonModel model) {
        return this.twEmojisContentMap.computeIfAbsent(model.getId(), id -> this.readImageFileContentFromDisk(model));
    }

    public AtlaskitEmoticonModel findById(String id) {
        if (this.twEmojisMap.containsKey(id)) {
            return this.twEmojisMap.get(id);
        }
        return this.twEmojisSkinsMap.get(id);
    }

    public Collection<AtlaskitEmoticonModel> list() {
        return this.twEmojisList;
    }
}

