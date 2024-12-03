/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.renderer.util.UrlUtil
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.flyingpdf.html;

import com.atlassian.confluence.extra.flyingpdf.util.UrlUtils;
import com.atlassian.renderer.util.UrlUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import org.apache.commons.lang3.StringUtils;

public class LinkFixer {
    private final HashMap<String, String> pageTitleById = new HashMap();
    private final HashSet<String> pageTitles = new HashSet();
    private final String baseUrl;
    private final String spaceKey;
    private final InternalPageStrategy pageStrategy;

    public LinkFixer(String spaceKey, String baseUrl, InternalPageStrategy pageStrategy) {
        this.baseUrl = LinkFixer.trimEndingSlash(baseUrl);
        this.spaceKey = spaceKey;
        this.pageStrategy = pageStrategy;
    }

    void addPage(String pageId, String pageTitle) {
        this.pageTitleById.put(pageId, pageTitle);
        this.pageTitles.add(pageTitle);
    }

    String convertLink(String url) {
        boolean isOnThisServer;
        if (StringUtils.isBlank((CharSequence)url)) {
            return null;
        }
        boolean bl = isOnThisServer = url.trim().startsWith("/") || url.trim().startsWith(this.baseUrl);
        if (!isOnThisServer) {
            return null;
        }
        Matcher matcher = UrlUtils.pageDisplayUrlPattern.matcher(url);
        if (matcher.find()) {
            String spaceKey = matcher.group(1);
            String pageTitle = UrlUtils.decodeTitle(matcher.group(2));
            if (this.spaceKey.equalsIgnoreCase(spaceKey) && this.pageTitles.contains(pageTitle)) {
                return this.pageStrategy.generate(this.baseUrl, spaceKey, pageTitle);
            }
        } else if (url.contains("/pages/viewpage.action?")) {
            Map queryParams = UrlUtil.getQueryParameters((String)url);
            String spaceKey = (String)queryParams.get("spaceKey");
            String pageId = (String)queryParams.get("pageId");
            String title = UrlUtils.decodeTitle((String)queryParams.get("pageTitle"));
            if (pageId != null) {
                title = this.pageTitleById.get(pageId);
                if (title != null) {
                    return this.pageStrategy.generate(this.baseUrl, this.spaceKey, title);
                }
            } else if (this.spaceKey.equalsIgnoreCase(spaceKey) && this.pageTitles.contains(title)) {
                return this.pageStrategy.generate(this.baseUrl, spaceKey, title);
            }
        }
        return null;
    }

    private static String trimEndingSlash(String baseUrl) {
        return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
    }

    public static enum InternalPageStrategy {
        ANCHOR{

            @Override
            String generate(String baseUrl, String spaceKey, String title) {
                return "#" + title;
            }
        }
        ,
        NORMALISE{

            @Override
            String generate(String baseUrl, String spaceKey, String title) {
                return baseUrl + "/display/" + spaceKey + "/" + UrlUtils.encodeTitle(title);
            }
        };


        abstract String generate(String var1, String var2, String var3);
    }
}

