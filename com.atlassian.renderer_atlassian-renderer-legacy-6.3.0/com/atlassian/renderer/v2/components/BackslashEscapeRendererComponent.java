/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.AbstractRegexRendererComponent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackslashEscapeRendererComponent
extends AbstractRegexRendererComponent {
    private static final char[] ESCAPABLE_WIKI_CHARS = new char[]{'!', '[', ']', '^', '~', '+', '?', '%', '{', '}', '(', ')', '*', '_', '-', '|', '@'};
    private static final Pattern ESCAPING_PATTERN = Pattern.compile("(^|(?<!\\\\))\\\\([\\-\\#\\*\\_\\+\\?\\^\\~\\|\\%\\{\\}\\[\\]\\(\\)\\!\\@])");

    public static String escapeWiki(String str) {
        StringBuffer buf = new StringBuffer(str.length() + 10);
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            for (char escapableWikiChar : ESCAPABLE_WIKI_CHARS) {
                if (c != escapableWikiChar) continue;
                buf.append('\\');
            }
            buf.append(c);
        }
        return buf.toString();
    }

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.backslashEscape();
    }

    @Override
    public String render(String wiki, RenderContext context) {
        if (wiki.indexOf("\\") == -1) {
            return wiki;
        }
        return this.regexRender(wiki, context, ESCAPING_PATTERN);
    }

    @Override
    public void appendSubstitution(StringBuffer buffer, RenderContext context, Matcher matcher) {
        char c = matcher.group(2).charAt(0);
        if (c == '{' || c == '}' || c == '[' || c == ']') {
            if (context.isRenderingForWysiwyg() && (c == '{' || c == '}')) {
                buffer.append(context.getRenderedContentStore().addInline("\\" + c));
            } else {
                buffer.append(c);
            }
        } else {
            buffer.append("&#").append((int)c).append(";");
        }
    }
}

