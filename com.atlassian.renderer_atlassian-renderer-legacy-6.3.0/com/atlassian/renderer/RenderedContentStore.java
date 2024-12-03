/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.renderer;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.escaper.RenderEscaper;
import com.atlassian.renderer.escaper.RenderEscapers;
import com.atlassian.renderer.v2.Renderable;
import com.atlassian.renderer.v2.Replacer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RenderedContentStore {
    private static final Logger log = LoggerFactory.getLogger(RenderedContentStore.class);
    public static final String BLOCK_TOKEN = TokenType.BLOCK.getTokenMarker();
    public static final String INLINE_TOKEN = TokenType.INLINE.getTokenMarker();
    public static final String MAP_KEY = "RenderedContentStore";
    private List store = new ArrayList();
    private Map<String, RenderEscaper> tokenEscapers = new HashMap<String, RenderEscaper>();

    public static RenderedContentStore getFromRenderContext(RenderContext renderContext) {
        return renderContext.getRenderedContentStore();
    }

    public String addContent(Object content, TokenType type) {
        if (!(content instanceof String) && !(content instanceof Renderable)) {
            throw new RuntimeException("You can only store String and Renderable objects.");
        }
        this.store.add(content);
        return type.getTokenMarker() + (this.store.size() - 1) + type.getTokenMarker();
    }

    public String addBlock(Object content) {
        return this.addContent(content, TokenType.BLOCK);
    }

    public String addInline(Object content) {
        return this.addContent(content, TokenType.INLINE);
    }

    public Object get(String token) {
        try {
            return this.validToken(token) ? this.store.get(this.getId(token)) : token;
        }
        catch (Exception e) {
            log.warn("Could not find stored token. A filter or macro may be broken. Exception: " + e.getMessage());
            return token;
        }
    }

    public void set(String token, Object content) {
        try {
            this.store.set(this.getId(token), content);
        }
        catch (Exception e) {
            log.warn("Could not find stored token. A filter or macro may be broken. Exception: " + e.getMessage());
        }
    }

    private int getId(String token) {
        return Integer.parseInt(token.substring(BLOCK_TOKEN.length(), token.length() - BLOCK_TOKEN.length()));
    }

    private boolean validToken(String token) {
        if (token == null || token.length() < 3) {
            log.warn("Could not find stored token: the token was null or too short. A filter or macro may be broken.");
            return false;
        }
        return true;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RenderedContentStore)) {
            return false;
        }
        RenderedContentStore renderedContentStore = (RenderedContentStore)o;
        return !(this.store != null ? !this.store.equals(renderedContentStore.store) : renderedContentStore.store != null);
    }

    public int hashCode() {
        return this.store != null ? this.store.hashCode() : 0;
    }

    public static String stripTokens(String text) {
        for (TokenType tokenType : TokenType.values()) {
            text = new Replacer(tokenType.getTokenPattern(), "", new String[0]).replace(text);
        }
        return text;
    }

    public void putEscaper(String token, RenderEscaper escaper) {
        this.tokenEscapers.put(token, escaper);
    }

    public RenderEscaper getEscaper(String token) {
        return this.tokenEscapers.containsKey(token) ? this.tokenEscapers.get(token) : RenderEscapers.NONE_RENDERER_ESCAPER;
    }
}

