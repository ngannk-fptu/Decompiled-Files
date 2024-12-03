/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.macro.BaseMacro
 *  com.atlassian.user.User
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.StringUtils
 *  org.radeox.macro.parameter.MacroParameter
 */
package com.atlassian.confluence.renderer.radeox.macros;

import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.renderer.WikiRendererContextKeys;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.renderer.macro.BaseMacro;
import com.atlassian.user.User;
import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.radeox.macro.parameter.MacroParameter;

public abstract class AbstractHtmlGeneratingMacro
extends BaseMacro {
    public final void execute(Writer writer, MacroParameter macroParameter) throws IllegalArgumentException, IOException {
        String content = this.getHtml(macroParameter);
        if (StringUtils.isNotEmpty((CharSequence)content)) {
            writer.write(content);
        }
    }

    protected abstract String getHtml(MacroParameter var1) throws IllegalArgumentException, IOException;

    protected String errorContent(String message) {
        return "<div class=\"error\">" + message + "</div>";
    }

    protected String parseParameterForKey(MacroParameter parameter, String key) {
        Map paramMap = parameter.getParams();
        String result = (String)paramMap.get(key);
        if (result == null) {
            result = (String)paramMap.get(" " + key);
        }
        if (result == null) {
            result = (String)paramMap.get(key + " ");
        }
        if (result == null) {
            result = (String)paramMap.get(" " + key + " ");
        }
        return result;
    }

    protected PageContext getPageContext(MacroParameter macroParameter) {
        Map contextParams = macroParameter.getContext().getParameters();
        PageContext context = WikiRendererContextKeys.getPageContext(contextParams);
        return context;
    }

    protected User getRemoteUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    protected String buildBaseUrl(HttpServletRequest request, List ignoredKeys) {
        if (request == null) {
            return null;
        }
        StringBuilder baseurl = new StringBuilder(GeneralUtil.appendAmpersandOrQuestionMark(request.getRequestURI()));
        Enumeration keys = request.getParameterNames();
        while (keys.hasMoreElements()) {
            String key = (String)keys.nextElement();
            if (ignoredKeys.contains(key)) continue;
            baseurl.append(HtmlUtil.urlEncode(key)).append("=").append(HtmlUtil.urlEncode(request.getParameter(key))).append("&");
        }
        return baseurl.toString();
    }

    protected boolean hasLoneParameter(MacroParameter macroParameter, String loneParameter) {
        for (String parameter : macroParameter.getParams().keySet()) {
            try {
                Integer.parseInt(parameter);
                if (!StringUtils.defaultString((String)loneParameter).equals(macroParameter.getParams().get(parameter))) continue;
                return true;
            }
            catch (NumberFormatException numberFormatException) {
            }
        }
        return false;
    }
}

