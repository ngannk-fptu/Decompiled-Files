/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.v2.components.HtmlEscaper
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 */
package com.atlassian.confluence.admin.actions.lookandfeel;

import com.atlassian.confluence.admin.actions.LookAndFeel;
import com.atlassian.confluence.admin.actions.lookandfeel.AbstractDecoratorAction;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.io.BufferedReader;
import java.io.StringReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewDefaultDecoratorAction
extends AbstractDecoratorAction
implements LookAndFeel {
    private static final String HTML_QUOTE = "&quot;";
    private static final String HTML_SINGLE_QUOTE = "&#39;";

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() throws Exception {
        String s;
        String template = this.readDefaultTemplate();
        if (template == null) {
            this.addActionError("template.not.found", this.decoratorName);
            this.setContent("");
            return "success";
        }
        BufferedReader br = new BufferedReader(new StringReader(template));
        StringBuilder decoratorSource = new StringBuilder();
        while ((s = br.readLine()) != null) {
            this.addIndent(s, decoratorSource);
            decoratorSource.append(HtmlUtil.htmlEncode(s)).append("<br/>");
        }
        this.setContent(this.linkIncludes(decoratorSource));
        return "success";
    }

    private void addIndent(String s, StringBuilder buffer) {
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) != ' ') {
                return;
            }
            buffer.append("&nbsp;");
        }
    }

    private String linkIncludes(CharSequence source) {
        Pattern p = Pattern.compile("#parse[ ]*\\(.*?\\)");
        Matcher m = p.matcher(source);
        StringBuffer sb = new StringBuffer();
        boolean result = m.find();
        while (result) {
            String match = m.group();
            boolean hasHtmlQuote = match.contains(HTML_QUOTE);
            if (hasHtmlQuote || match.contains(HTML_SINGLE_QUOTE)) {
                String delimiter = hasHtmlQuote ? HTML_QUOTE : HTML_SINGLE_QUOTE;
                int includePathStart = match.indexOf(delimiter) + delimiter.length();
                int includePathEnd = match.lastIndexOf(delimiter);
                String includePath = match.substring(includePathStart, includePathEnd);
                m.appendReplacement(sb, "#parse (&quot;<a href='viewdefaultdecorator.action?decoratorName=" + includePath + (String)(this.key == null ? "" : "&key=" + HtmlEscaper.escapeAll((String)this.key, (boolean)true)) + "'>" + includePath + "</a>&quot;)");
            }
            result = m.find();
        }
        m.appendTail(sb);
        return sb.toString();
    }

    public String getFileName() {
        int i = this.decoratorName.lastIndexOf("/");
        if (i != -1) {
            return this.decoratorName.substring(i + 1);
        }
        return this.decoratorName;
    }
}

