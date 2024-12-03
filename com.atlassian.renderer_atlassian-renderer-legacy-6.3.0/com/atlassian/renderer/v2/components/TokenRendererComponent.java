/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.renderer.v2.components;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.RenderedContentStore;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.escaper.RenderEscaper;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.Renderable;
import com.atlassian.renderer.v2.SubRenderer;
import com.atlassian.renderer.v2.components.AbstractRegexRendererComponent;
import java.util.regex.Matcher;
import org.apache.commons.lang.StringUtils;

public class TokenRendererComponent
extends AbstractRegexRendererComponent {
    public static final String BLOCK_TOKEN_PATTERN_STR = TokenType.BLOCK.getTokenPatternString();
    public static final String INLINE_TOKEN_PATTERN_STR = TokenType.INLINE.getTokenPatternString();
    private final SubRenderer subRenderer;

    public TokenRendererComponent(SubRenderer subRenderer) {
        this.subRenderer = subRenderer;
    }

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.resolveTokens() && renderMode.tokenizes();
    }

    @Override
    public String render(String wiki, RenderContext context) {
        String wikiBeforeRendering;
        do {
            wikiBeforeRendering = wiki;
            for (TokenType tokenType : TokenType.values()) {
                wiki = this.regexRender(wiki, context, tokenType.getTokenPattern());
            }
        } while (!StringUtils.equals((String)wiki, (String)wikiBeforeRendering));
        return wikiBeforeRendering;
    }

    @Override
    public void appendSubstitution(StringBuffer buffer, RenderContext context, Matcher matcher) {
        String token = matcher.group(0);
        RenderedContentStore renderedContentStore = context.getRenderedContentStore();
        Object obj = renderedContentStore.get(token);
        RenderEscaper escaper = context.getRenderedContentStore().getEscaper(token);
        if (obj instanceof Renderable) {
            context.setEscaper(escaper);
            int contentStart = buffer.length();
            Renderable renderable = (Renderable)obj;
            renderable.render(this.subRenderer, context, buffer);
            this.handleRecursion(buffer.substring(contentStart), token, renderedContentStore);
        } else if (obj instanceof String) {
            String content = (String)obj;
            buffer.append(escaper.escape(content, context.getCharacterEncoding()));
            this.handleRecursion(content, token, renderedContentStore);
        } else {
            throw new RuntimeException("Found object " + obj + " in token store?");
        }
    }

    private void handleRecursion(String content, String token, RenderedContentStore renderedContentStore) {
        if (content.contains(token)) {
            renderedContentStore.set(token, token);
        }
    }
}

