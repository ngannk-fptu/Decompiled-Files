/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.embedded.EmbeddedResource;
import com.atlassian.renderer.embedded.EmbeddedResourceRenderer;
import com.atlassian.renderer.embedded.EmbeddedResourceResolver;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.AbstractRegexRendererComponent;
import com.atlassian.renderer.v2.components.phrase.PhraseRendererComponent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmbeddedRendererComponent
extends AbstractRegexRendererComponent {
    static final Pattern IMAGE_PATTERN = Pattern.compile(EmbeddedRendererComponent.buildPhraseRegExp("\\!", "\\!"));

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderImages();
    }

    @Override
    public String render(String wiki, RenderContext context) {
        if (wiki.indexOf("!") == -1) {
            return wiki;
        }
        return this.regexRender(wiki, context, IMAGE_PATTERN);
    }

    @Override
    public void appendSubstitution(StringBuffer buffer, RenderContext context, Matcher matcher) {
        String matchStr = matcher.group(2);
        if (matchStr.length() < 5 || matchStr.charAt(0) == ')') {
            buffer.append("!").append(matchStr).append("!");
            return;
        }
        EmbeddedResource r = EmbeddedResourceResolver.create(matchStr);
        String renderedResource = this.renderResource(context, r, matchStr);
        buffer.append(context.getRenderedContentStore().addInline(renderedResource));
    }

    protected String renderResource(RenderContext context, EmbeddedResource r, String matchStr) {
        EmbeddedResourceRenderer renderer = context.getEmbeddedResourceRenderer();
        return renderer.renderResource(r, context);
    }

    private static String buildPhraseRegExp(String phrase_start_sign, String phrase_end_sign) {
        String phrase_content = "[^\\s" + phrase_start_sign + "]((?!" + phrase_end_sign + ")[\\p{L}\\p{Nd}\\p{Z}\\p{S}\\p{M}\\p{P}]*?[^\\s" + phrase_end_sign + "])?";
        return PhraseRendererComponent.VALID_START + phrase_start_sign + "(" + phrase_content + ")(?<!\\\\)" + phrase_end_sign + PhraseRendererComponent.VALID_END;
    }
}

