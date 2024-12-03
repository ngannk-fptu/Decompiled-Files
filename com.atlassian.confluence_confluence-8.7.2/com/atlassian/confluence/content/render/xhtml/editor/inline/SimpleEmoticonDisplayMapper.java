/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.webresource.UrlMode
 *  com.atlassian.plugin.webresource.WebResourceUrlProvider
 */
package com.atlassian.confluence.content.render.xhtml.editor.inline;

import com.atlassian.confluence.content.render.xhtml.editor.inline.EmoticonDisplayMapper;
import com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon;
import com.atlassian.plugin.webresource.UrlMode;
import com.atlassian.plugin.webresource.WebResourceUrlProvider;
import java.util.HashMap;
import java.util.Map;

public class SimpleEmoticonDisplayMapper
implements EmoticonDisplayMapper {
    private final WebResourceUrlProvider webResourceUrlProvider;
    private Map<Emoticon, String> emoticonImages;

    public SimpleEmoticonDisplayMapper(WebResourceUrlProvider webResourceUrlProvider) {
        this.webResourceUrlProvider = webResourceUrlProvider;
        this.emoticonImages = new HashMap<Emoticon, String>(Emoticon.values().length);
        this.emoticonImages.put(Emoticon.SMILE, "smile");
        this.emoticonImages.put(Emoticon.SAD, "sad");
        this.emoticonImages.put(Emoticon.CHEEKY, "tongue");
        this.emoticonImages.put(Emoticon.LAUGH, "biggrin");
        this.emoticonImages.put(Emoticon.WINK, "wink");
        this.emoticonImages.put(Emoticon.THUMBS_UP, "thumbs_up");
        this.emoticonImages.put(Emoticon.THUMBS_DOWN, "thumbs_down");
        this.emoticonImages.put(Emoticon.INFORMATION, "information");
        this.emoticonImages.put(Emoticon.TICK, "check");
        this.emoticonImages.put(Emoticon.CROSS, "error");
        this.emoticonImages.put(Emoticon.WARNING, "warning");
        this.emoticonImages.put(Emoticon.PLUS, "add");
        this.emoticonImages.put(Emoticon.MINUS, "forbidden");
        this.emoticonImages.put(Emoticon.QUESTION, "help_16");
        this.emoticonImages.put(Emoticon.LIGHT_ON, "lightbulb_on");
        this.emoticonImages.put(Emoticon.LIGHT_OFF, "lightbulb");
        this.emoticonImages.put(Emoticon.YELLOW_STAR, "star_yellow");
        this.emoticonImages.put(Emoticon.RED_STAR, "star_red");
        this.emoticonImages.put(Emoticon.GREEN_STAR, "star_green");
        this.emoticonImages.put(Emoticon.BLUE_STAR, "star_blue");
        this.emoticonImages.put(Emoticon.HEART, "heart");
        this.emoticonImages.put(Emoticon.BROKEN_HEART, "broken_heart");
    }

    @Override
    public String getRelativeImageUrl(Emoticon emoticon) {
        return this.getImageUrl(emoticon, UrlMode.RELATIVE);
    }

    @Override
    public String getAbsoluteImageUrl(Emoticon emoticon) {
        return this.getImageUrl(emoticon, UrlMode.ABSOLUTE);
    }

    private String getImageUrl(Emoticon emoticon, UrlMode urlMode) {
        String image = this.getImageName(emoticon);
        if (image == null) {
            return null;
        }
        return this.webResourceUrlProvider.getStaticResourcePrefix(urlMode) + "/images/icons/emoticons/" + image + ".svg";
    }

    @Override
    public String getImageName(Emoticon emoticon) {
        return this.emoticonImages.get((Object)emoticon);
    }
}

