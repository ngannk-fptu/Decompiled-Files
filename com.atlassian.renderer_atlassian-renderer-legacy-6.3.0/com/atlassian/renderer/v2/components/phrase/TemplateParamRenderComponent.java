/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.v2.components.phrase;

import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.components.AbstractRegexRendererComponent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TemplateParamRenderComponent
extends AbstractRegexRendererComponent {
    public static final Pattern VARIABLE_PATTERN = Pattern.compile("@([\\p{L}\\p{N}_|\\(\\),]+)@");

    @Override
    public boolean shouldRender(RenderMode renderMode) {
        return renderMode.renderTemplate();
    }

    @Override
    public String render(String wiki, RenderContext context) {
        if (wiki.indexOf("@") == -1) {
            return wiki;
        }
        return this.regexRender(wiki, context, VARIABLE_PATTERN);
    }

    @Override
    public void appendSubstitution(StringBuffer buffer, RenderContext context, Matcher matcher) {
        buffer.append(context.getRenderedContentStore().addInline(this.makeFormElement(matcher)));
    }

    private String makeFormElement(Matcher match) {
        String paramName;
        String resultStr = match.group(1);
        StringBuffer result = new StringBuffer();
        if (resultStr.indexOf(124) < 0) {
            paramName = resultStr;
            result.append("<input type=\"text\" name=\"variableValues." + paramName + "\" size=\"12\" onkeyup=\"updateOthers(this)\" />");
        } else {
            paramName = resultStr.substring(0, resultStr.indexOf(124));
            String paramType = resultStr.substring(resultStr.indexOf(124) + 1);
            if (paramType.toLowerCase().startsWith("textarea")) {
                this.handleTextArea(result, paramName, paramType);
            } else if (paramType.toLowerCase().startsWith("list")) {
                this.handleList(result, paramName, paramType);
            }
        }
        result.append("&nbsp;<span class=\"templateparameter\">(" + paramName + ")</span>");
        return result.toString();
    }

    private void handleList(StringBuffer stringBuffer, String paramName, String paramType) {
        List paramParameters = this.getParameters(paramType);
        stringBuffer.append("<select name=\"variableValues." + paramName + "\">");
        for (String param : paramParameters) {
            stringBuffer.append("<option value=\"" + param + "\">" + param + "</option>");
        }
        stringBuffer.append("</select>");
    }

    private void handleTextArea(StringBuffer stringBuffer, String paramName, String paramType) {
        String rows = "4";
        String cols = "40";
        List paramParameters = this.getParameters(paramType);
        if (paramParameters.size() > 0) {
            rows = (String)paramParameters.get(0);
        }
        if (paramParameters.size() > 1) {
            cols = (String)paramParameters.get(1);
        }
        stringBuffer.append("<textarea name=\"variableValues." + paramName + "\" rows=\"" + rows + "\" cols=\"" + cols + "\"></textarea>");
    }

    private List getParameters(String paramType) {
        int firstBrace = paramType.indexOf(40);
        if (firstBrace < 0) {
            return Collections.EMPTY_LIST;
        }
        int lastBrace = paramType.lastIndexOf(41);
        if (lastBrace < 0) {
            return Collections.EMPTY_LIST;
        }
        StringTokenizer tokens = new StringTokenizer(paramType.substring(firstBrace + 1, lastBrace), ",");
        ArrayList<String> result = new ArrayList<String>(tokens.countTokens());
        while (tokens.hasMoreTokens()) {
            result.add(tokens.nextToken());
        }
        return result;
    }
}

