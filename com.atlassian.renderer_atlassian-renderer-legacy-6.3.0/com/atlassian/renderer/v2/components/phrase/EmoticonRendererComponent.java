/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  com.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.renderer.v2.components.phrase;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.renderer.IconManager;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.util.RegExpUtil;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.AbstractRegexRendererComponent;
import com.atlassian.renderer.v2.components.TextConverter;
import com.atlassian.renderer.v2.components.phrase.PhraseRendererComponent;
import com.atlassian.util.concurrent.LazyReference;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmoticonRendererComponent
extends AbstractRegexRendererComponent
implements TextConverter {
    private final IconManager iconManager;
    @TenantAware(value=TenancyScope.TENANTLESS, comment="All tenants share same set of emoticons")
    private final LazyReference<Emoticons> emoticons;

    public EmoticonRendererComponent(final IconManager iconManager) {
        this.iconManager = iconManager;
        this.emoticons = new LazyReference<Emoticons>(){

            protected Emoticons create() throws Exception {
                return new Emoticons(iconManager.getEmoticonSymbols());
            }
        };
    }

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderPhrases();
    }

    @Override
    public String render(String wiki, RenderContext context) {
        return ((Emoticons)this.emoticons.get()).render(wiki, context);
    }

    @Override
    public String convertToWikiMarkup(String text) {
        return ((Emoticons)this.emoticons.get()).toWikiMarkup(text);
    }

    @Override
    public void appendSubstitution(StringBuffer buffer, RenderContext context, Matcher matcher) {
        String match = matcher.group(1);
        if (match.startsWith("\\")) {
            buffer.append(match.substring(1));
        } else {
            buffer.append(this.iconManager.getEmoticon(match).toHtml(context.getImagePath()));
        }
    }

    private class Emoticon {
        private final String symbol;
        private final Pattern pattern;

        private Emoticon(Pattern pattern, String symbol) {
            this.pattern = pattern;
            this.symbol = symbol;
        }

        public String getSymbol() {
            return this.symbol;
        }

        public String render(String wiki, RenderContext context) {
            if (wiki.indexOf(this.getSymbol()) != -1) {
                return EmoticonRendererComponent.this.regexRender(wiki, context, this.pattern);
            }
            return wiki;
        }

        public String toWiki(String text) {
            if (text.contains(this.getSymbol())) {
                Matcher matcher = this.pattern.matcher(text);
                return matcher.replaceAll("\\\\$1");
            }
            return text;
        }
    }

    private class Emoticons {
        private final List<Emoticon> emoticons;

        public Emoticons(String[] symbols) {
            this.emoticons = new ArrayList<Emoticon>(symbols.length);
            for (String symbol : symbols) {
                String optionalBackslash = "\\\\?";
                String patternString = "(" + optionalBackslash + RegExpUtil.convertToRegularExpression(symbol) + ")" + PhraseRendererComponent.VALID_END;
                this.emoticons.add(new Emoticon(Pattern.compile(patternString), symbol));
            }
        }

        public String render(String wiki, RenderContext context) {
            for (Emoticon emoticon : this.emoticons) {
                wiki = emoticon.render(wiki, context);
            }
            return wiki;
        }

        public String toWikiMarkup(String text) {
            for (Emoticon emoticon : this.emoticons) {
                text = emoticon.toWiki(text);
            }
            return text;
        }
    }
}

