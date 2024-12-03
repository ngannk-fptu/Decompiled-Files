/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.IconManager
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.components.phrase.EmoticonRendererComponent
 */
package com.atlassian.confluence.content.render.xhtml.migration;

import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.content.render.xhtml.model.inline.Emoticon;
import com.atlassian.confluence.content.render.xhtml.storage.inline.StorageEmoticonMarshaller;
import com.atlassian.renderer.IconManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.components.phrase.EmoticonRendererComponent;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

public class XhtmlEmoticonRendererComponent
extends EmoticonRendererComponent {
    private static final Map<String, Emoticon> wikiEmoticons = new HashMap<String, Emoticon>(25);
    private final StorageEmoticonMarshaller marshaller;

    public XhtmlEmoticonRendererComponent(StorageEmoticonMarshaller marshaller, IconManager iconManager) {
        super(iconManager);
        this.marshaller = marshaller;
    }

    public void appendSubstitution(StringBuffer buffer, RenderContext context, Matcher matcher) {
        String match = matcher.group(1);
        if (match.startsWith("\\")) {
            buffer.append(match.substring(1));
            return;
        }
        Emoticon emoticon = wikiEmoticons.get(match);
        if (emoticon == null) {
            buffer.append(match);
            return;
        }
        try {
            buffer.append(Streamables.writeToString(this.marshaller.marshal(emoticon, null)));
        }
        catch (XhtmlException ex) {
            throw new RuntimeException(ex);
        }
    }

    static {
        wikiEmoticons.put(":-)", Emoticon.SMILE);
        wikiEmoticons.put(":)", Emoticon.SMILE);
        wikiEmoticons.put(":P", Emoticon.CHEEKY);
        wikiEmoticons.put(":p", Emoticon.CHEEKY);
        wikiEmoticons.put(";-)", Emoticon.WINK);
        wikiEmoticons.put(";)", Emoticon.WINK);
        wikiEmoticons.put(":D", Emoticon.LAUGH);
        wikiEmoticons.put(":-(", Emoticon.SAD);
        wikiEmoticons.put(":(", Emoticon.SAD);
        wikiEmoticons.put("(y)", Emoticon.THUMBS_UP);
        wikiEmoticons.put("(n)", Emoticon.THUMBS_DOWN);
        wikiEmoticons.put("(i)", Emoticon.INFORMATION);
        wikiEmoticons.put("(/)", Emoticon.TICK);
        wikiEmoticons.put("(x)", Emoticon.CROSS);
        wikiEmoticons.put("(+)", Emoticon.PLUS);
        wikiEmoticons.put("(-)", Emoticon.MINUS);
        wikiEmoticons.put("(!)", Emoticon.WARNING);
        wikiEmoticons.put("(?)", Emoticon.QUESTION);
        wikiEmoticons.put("(on)", Emoticon.LIGHT_ON);
        wikiEmoticons.put("(off)", Emoticon.LIGHT_OFF);
        wikiEmoticons.put("(*)", Emoticon.YELLOW_STAR);
        wikiEmoticons.put("(*b)", Emoticon.BLUE_STAR);
        wikiEmoticons.put("(*y)", Emoticon.YELLOW_STAR);
        wikiEmoticons.put("(*g)", Emoticon.GREEN_STAR);
        wikiEmoticons.put("(*r)", Emoticon.RED_STAR);
    }
}

