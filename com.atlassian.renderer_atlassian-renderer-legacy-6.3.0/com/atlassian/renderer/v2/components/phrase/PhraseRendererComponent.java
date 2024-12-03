/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components.phrase;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.Replacer;
import com.atlassian.renderer.v2.components.RendererComponent;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PhraseRendererComponent
implements RendererComponent {
    static final String NO_LETTERS_OR_DIGITS_BEFORE = "(?<![\\p{L}\\p{Nd}\\\\])";
    static final String NO_LETTERS_OR_DIGITS_AFTERWARDS = "(?![\\p{L}\\p{Nd}])";
    static final String INLINE_TOKENS_BEFORE = "(?<=" + TokenType.INLINE.getTokenMarker() + ")";
    static final String INLINE_TOKENS_AFTERWARDS = "(?=" + TokenType.INLINE.getTokenMarker() + ")";
    public static final String VALID_START = "((?<![\\p{L}\\p{Nd}\\\\])|" + INLINE_TOKENS_BEFORE + ")";
    public static final String VALID_END = "((?![\\p{L}\\p{Nd}])|" + INLINE_TOKENS_AFTERWARDS + ")";
    private static Map<String, PhraseRendererComponent> heresOneWePreparedEarlier = new HashMap<String, PhraseRendererComponent>();
    private Replacer replacer;

    public static PhraseRendererComponent getDefaultRenderer(String name) {
        return heresOneWePreparedEarlier.get(name);
    }

    public PhraseRendererComponent(String delimiter, String tagName) {
        this(delimiter, delimiter, "<" + tagName + ">", "</" + tagName + ">");
    }

    public PhraseRendererComponent(String startDelimiter, String endDelimiter, String tagName) {
        this(startDelimiter, endDelimiter, "<" + tagName + ">", "</" + tagName + ">");
    }

    public PhraseRendererComponent(String startDelimiter, String endDelimiter, String startTag, String endTag) {
        this.replacer = new Replacer(PhraseRendererComponent.makePattern(startDelimiter, endDelimiter), startTag + "$2" + endTag, new String[]{startDelimiter.replaceAll("\\\\", ""), endDelimiter.replaceAll("\\\\", "")});
    }

    @Override
    public String render(String wiki, RenderContext context) {
        String html = this.replacer.replaceAll(wiki);
        if (context.isRenderingForWysiwyg()) {
            html = html.replaceAll("<ins>", "<u>").replaceAll("</ins>", "</u>");
        }
        return html;
    }

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderPhrases();
    }

    static Pattern makePattern(String startDelimiter, String endDelimiter) {
        String startDelimiter2 = "\\{" + startDelimiter + "\\}";
        String endDelimiter2 = "\\{" + endDelimiter + "\\}";
        String phrase_content = "[^\\s" + startDelimiter + "]((?!" + endDelimiter + ")[\\p{L}\\p{Nd}\\p{Z}\\p{S}\\p{M}\\p{P}]*?[^\\s" + endDelimiter + "])??";
        return Pattern.compile("(?:(?:" + VALID_START + startDelimiter + ")|" + startDelimiter2 + ")(" + phrase_content + ")(?<!\\\\)(?:(?:" + endDelimiter + VALID_END + ")|" + endDelimiter2 + ")");
    }

    static {
        heresOneWePreparedEarlier.put("citation", new PhraseRendererComponent("\\?\\?", "cite"));
        heresOneWePreparedEarlier.put("strong", new PhraseRendererComponent("\\*", "strong"));
        heresOneWePreparedEarlier.put("superscript", new PhraseRendererComponent("\\^", "sup"));
        heresOneWePreparedEarlier.put("subscript", new PhraseRendererComponent("~", "sub"));
        heresOneWePreparedEarlier.put("emphasis", new PhraseRendererComponent("_", "em"));
        heresOneWePreparedEarlier.put("deleted", new PhraseRendererComponent("-", "-", "<span style=\"text-decoration: line-through; \">", "</span>"));
        heresOneWePreparedEarlier.put("inserted", new PhraseRendererComponent("\\+", "\\+", "<span style=\"text-decoration: underline; \">", "</span>"));
        heresOneWePreparedEarlier.put("monospaced", new PhraseRendererComponent("\\{\\{", "\\}\\}", "code"));
    }
}

