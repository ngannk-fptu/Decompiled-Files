/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ReadOnlyApplicationLink
 */
package com.atlassian.jpos.confluencemacro;

import com.atlassian.applinks.api.ReadOnlyApplicationLink;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlanHtmlTransform
implements UnaryOperator<String> {
    private static final Pattern SHORTCUTS_JS_PATTERN = Pattern.compile("src=\"(.*)/rest/api/1.0/shortcuts");
    private static final String SHORTCUT_JS_LINK_FORMAT = "src=\"%s/rest/api/1.0/shortcuts";
    private static final Pattern CONTEXT_PATH_PATTERN = Pattern.compile("\"com.atlassian.plugins.atlassian-plugins-webresource-plugin:context-path.context-path\"]=\"(.*)\"");
    private static final String CONTEXT_PATH_FORMAT = "\"com.atlassian.plugins.atlassian-plugins-webresource-plugin:context-path.context-path\"]=\"\\\"%s\\\"\"";
    private static final String APP_LINK_PROXY_URL_BASE_FORMAT = "{0}/plugins/servlet/arj-proxy?appId={1}&path=";
    private final String jiraBaseUrl;
    private final String appLinkedUrl;

    public PlanHtmlTransform(ReadOnlyApplicationLink jiraApplicationLink, String confluenceBaseUrl) {
        this.jiraBaseUrl = jiraApplicationLink.getRpcUrl().toString();
        this.appLinkedUrl = MessageFormat.format(APP_LINK_PROXY_URL_BASE_FORMAT, confluenceBaseUrl, jiraApplicationLink.getId().get());
    }

    @Override
    public String apply(String planHtml) {
        String transformed = this.setJiraBaseUrl(planHtml);
        transformed = this.rerouteAJSContextPathToProxy(transformed);
        transformed = this.rerouteShortcutsJsToProxy(transformed);
        transformed = this.rerouteJpoRestUrlToProxy(transformed);
        return transformed;
    }

    private String setJiraBaseUrl(String planHtml) {
        String headElement = "<head>";
        int headElementStartIndex = planHtml.lastIndexOf(headElement);
        if (headElementStartIndex > 0) {
            int headElementEndIndex = planHtml.lastIndexOf(headElement) + headElement.length();
            return planHtml.substring(0, headElementEndIndex) + "\n<base href=\"" + this.jiraBaseUrl + "\" />\n" + planHtml.substring(headElementEndIndex);
        }
        return planHtml;
    }

    private String rerouteShortcutsJsToProxy(String planHtml) {
        return SHORTCUTS_JS_PATTERN.matcher(planHtml).replaceAll(Matcher.quoteReplacement(String.format(SHORTCUT_JS_LINK_FORMAT, this.appLinkedUrl)));
    }

    private String rerouteAJSContextPathToProxy(String planHtml) {
        return CONTEXT_PATH_PATTERN.matcher(planHtml).replaceAll(Matcher.quoteReplacement(String.format(CONTEXT_PATH_FORMAT, this.appLinkedUrl)));
    }

    private String rerouteJpoRestUrlToProxy(String planHtml) {
        Optional<String> restUrl;
        Optional<String> baseUrl = this.getPropertyFromPlanHtml(planHtml, "\\\"baseUrl\\\":\\\"");
        if (baseUrl.isPresent() && (restUrl = this.getPropertyFromPlanHtml(planHtml, "\\\"restUrl\\\":\\\"")).isPresent()) {
            String restContext = restUrl.get().replace(baseUrl.get(), "");
            String proxyUrl = this.appLinkedUrl + restContext;
            return planHtml.replace(restUrl.get(), proxyUrl);
        }
        return planHtml;
    }

    private Optional<String> getPropertyFromPlanHtml(String content, String searchKey) {
        int startIndex;
        int endIndex;
        int searchIndex = content.indexOf(searchKey);
        if (searchIndex > 0 && (endIndex = content.indexOf("\"", startIndex = searchIndex + searchKey.length())) > startIndex) {
            return Optional.of(content.substring(startIndex, endIndex - 1));
        }
        return Optional.empty();
    }
}

