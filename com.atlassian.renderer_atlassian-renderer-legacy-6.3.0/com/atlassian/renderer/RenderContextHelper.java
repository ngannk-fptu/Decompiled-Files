/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.RenderedContentStore;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.escaper.RenderEscaper;
import com.atlassian.renderer.escaper.RenderEscapers;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang.StringUtils;

public class RenderContextHelper {
    public static String storeEscaperAndCreateTokensIfNeeded(String wiki, RenderContext context, RenderEscaper escaper) {
        HashMap<String, String> tokenMap = new HashMap<String, String>();
        RenderedContentStore renderedContentStore = context.getRenderedContentStore();
        for (TokenType tokenType : TokenType.values()) {
            Pattern pattern = tokenType.getTokenPattern();
            if (wiki == null || wiki.length() == 0) break;
            Matcher matcher = pattern.matcher(wiki);
            while (matcher.find()) {
                String token = matcher.group(0);
                RenderEscaper currentEscaper = renderedContentStore.getEscaper(token);
                if (currentEscaper == escaper) continue;
                if (currentEscaper == RenderEscapers.NONE_RENDERER_ESCAPER) {
                    renderedContentStore.putEscaper(token, escaper);
                    continue;
                }
                Object o = renderedContentStore.get(token);
                String newToken = renderedContentStore.addContent(o, RenderContextHelper.getTokenType(token));
                renderedContentStore.putEscaper(newToken, escaper);
                tokenMap.put(token, newToken);
            }
        }
        for (Map.Entry entry : tokenMap.entrySet()) {
            String token = (String)entry.getKey();
            String newToken = (String)entry.getValue();
            wiki = StringUtils.replace((String)wiki, (String)token, (String)newToken);
        }
        return wiki;
    }

    private static TokenType getTokenType(String token) {
        for (TokenType type : TokenType.values()) {
            if (!token.startsWith(type.getTokenMarker())) continue;
            return type;
        }
        throw new IllegalArgumentException("Unknown token format");
    }
}

