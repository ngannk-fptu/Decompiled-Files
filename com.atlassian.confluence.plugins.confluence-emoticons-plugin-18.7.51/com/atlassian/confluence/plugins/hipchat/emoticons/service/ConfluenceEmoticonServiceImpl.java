/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.editor.inline.EmoticonDisplayMapper
 *  com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableMap
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.service;

import com.atlassian.confluence.content.render.xhtml.editor.inline.EmoticonDisplayMapper;
import com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon;
import com.atlassian.confluence.plugins.hipchat.emoticons.rest.AtlaskitEmoticonModel;
import com.atlassian.confluence.plugins.hipchat.emoticons.rest.EmoticonModel;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.ConfluenceEmoticonService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConfluenceEmoticonServiceImpl
implements ConfluenceEmoticonService {
    private static final Logger log = LoggerFactory.getLogger(ConfluenceEmoticonServiceImpl.class);
    private final EmoticonDisplayMapper emoticonDisplayMapper;
    private static final Map<Emoticon, String> SHORTCUT_MAP = ImmutableMap.builder().put((Object)Emoticon.INFORMATION, (Object)"(i)").put((Object)Emoticon.TICK, (Object)"(/)").put((Object)Emoticon.CROSS, (Object)"(x)").put((Object)Emoticon.WARNING, (Object)"(!)").put((Object)Emoticon.PLUS, (Object)"(+)").put((Object)Emoticon.MINUS, (Object)"(-)").put((Object)Emoticon.QUESTION, (Object)"(?)").put((Object)Emoticon.LIGHT_ON, (Object)"(on)").put((Object)Emoticon.LIGHT_OFF, (Object)"(off)").put((Object)Emoticon.RED_STAR, (Object)"(*r)").put((Object)Emoticon.GREEN_STAR, (Object)"(*g)").put((Object)Emoticon.BLUE_STAR, (Object)"(*b)").build();
    private static final List<Emoticon> SHORTCUT_ORDERED_LIST = ImmutableList.builder().add((Object)Emoticon.LIGHT_ON).add((Object)Emoticon.LIGHT_OFF).add((Object)Emoticon.WARNING).add((Object)Emoticon.RED_STAR).add((Object)Emoticon.GREEN_STAR).add((Object)Emoticon.BLUE_STAR).add((Object)Emoticon.TICK).add((Object)Emoticon.CROSS).add((Object)Emoticon.INFORMATION).add((Object)Emoticon.QUESTION).add((Object)Emoticon.PLUS).add((Object)Emoticon.MINUS).build();

    public ConfluenceEmoticonServiceImpl(@ComponentImport EmoticonDisplayMapper emoticonDisplayMapper) {
        this.emoticonDisplayMapper = emoticonDisplayMapper;
    }

    @Override
    public Map<Emoticon, String> list() {
        return SHORTCUT_MAP;
    }

    @Override
    public List<AtlaskitEmoticonModel> orderedList() {
        ArrayList<AtlaskitEmoticonModel> atlaskitEmoticonModels = new ArrayList<AtlaskitEmoticonModel>();
        long order = 0L;
        for (Emoticon emoticon : SHORTCUT_ORDERED_LIST) {
            try {
                EmoticonModel emoticonModel = EmoticonModel.fromConfluenceEmoticon(this.emoticonDisplayMapper, this).apply(emoticon);
                AtlaskitEmoticonModel atlaskitEmoticonModel = new AtlaskitEmoticonModel(emoticonModel.getShortcut(), emoticonModel.getName(), emoticonModel.getShortcut(), emoticonModel.getShortcut(), "ATLASSIAN", "ATLASSIAN", order++, new AtlaskitEmoticonModel.ImageRepresentation(64, 64, emoticonModel.getUrl()), true, null, null);
                atlaskitEmoticonModels.add(atlaskitEmoticonModel);
            }
            catch (Exception exception) {
                log.error("Couldn't parse confluence emoticon", (Object)emoticon, (Object)exception);
            }
        }
        return atlaskitEmoticonModels;
    }
}

