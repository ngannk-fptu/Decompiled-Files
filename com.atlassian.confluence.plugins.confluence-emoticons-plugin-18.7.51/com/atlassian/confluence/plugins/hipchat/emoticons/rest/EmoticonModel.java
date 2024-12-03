/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.editor.inline.EmoticonDisplayMapper
 *  com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon
 *  org.codehaus.jackson.annotate.JsonProperty
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.rest;

import com.atlassian.confluence.content.render.xhtml.editor.inline.EmoticonDisplayMapper;
import com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon;
import com.atlassian.confluence.plugins.hipchat.emoticons.content.entity.CustomEmoticon;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.ConfluenceEmoticonService;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.codehaus.jackson.annotate.JsonProperty;

public class EmoticonModel
implements Serializable {
    @JsonProperty
    private String url;
    @JsonProperty
    private String shortcut;
    @JsonProperty
    private String name;
    @JsonProperty
    private String creatorUserId;
    @JsonProperty
    private Date createdDate;

    public EmoticonModel(String url, String shortcut, String name) {
        this(url, shortcut, name, null, null);
    }

    public EmoticonModel(String url, String shortcut, String name, String creatorUserId, Date createdDate) {
        this.url = url;
        this.shortcut = shortcut;
        this.name = name;
        this.creatorUserId = creatorUserId;
        this.createdDate = createdDate;
    }

    public String getCreatorUserId() {
        return this.creatorUserId;
    }

    public Date getCreatedDate() {
        return this.createdDate;
    }

    public String getUrl() {
        return this.url;
    }

    public String getShortcut() {
        return this.shortcut;
    }

    public String getName() {
        return this.name;
    }

    public static Function<Map.Entry<Emoticon, String>, EmoticonModel> fromConfluenceEmoticon(EmoticonDisplayMapper emoticonDisplayMapper) {
        return input -> new EmoticonModel(emoticonDisplayMapper.getRelativeImageUrl((Emoticon)input.getKey()), (String)input.getValue(), ((Emoticon)input.getKey()).getType());
    }

    public static Function<Emoticon, EmoticonModel> fromConfluenceEmoticon(EmoticonDisplayMapper emoticonDisplayMapper, ConfluenceEmoticonService confluenceEmoticonService) {
        return input -> new EmoticonModel(emoticonDisplayMapper.getRelativeImageUrl(input), confluenceEmoticonService.list().get(input), input.getType());
    }

    public static Function<CustomEmoticon, EmoticonModel> fromCustomEmoticon() {
        return customEmoticon -> new EmoticonModel(customEmoticon.getURL(), customEmoticon.getShortcut(), customEmoticon.getName(), customEmoticon.getCreatorUserId(), customEmoticon.getCreatedDate());
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof EmoticonModel)) {
            return false;
        }
        EmoticonModel oo = (EmoticonModel)o;
        return Objects.equals(oo.getName(), this.getName()) && Objects.equals(oo.getShortcut(), this.getShortcut()) && Objects.equals(oo.getUrl(), this.getUrl());
    }
}

