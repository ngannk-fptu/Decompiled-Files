/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.confluence.renderer.embedded.EmbeddedObject
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.util.UrlUtils
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.confluence.velocity.htmlsafe.HtmlFragment
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.core.filters.ServletContextThreadLocal
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.xwork.XsrfTokenGenerator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.macros.multimedia.renderer;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.macros.multimedia.renderer.AbstractEmbeddedResourceRenderer;
import com.atlassian.confluence.renderer.embedded.EmbeddedObject;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.UrlUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.confluence.velocity.htmlsafe.HtmlFragment;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.core.filters.ServletContextThreadLocal;
import com.atlassian.renderer.RenderContext;
import com.atlassian.xwork.XsrfTokenGenerator;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddedFlashRenderer
extends AbstractEmbeddedResourceRenderer {
    private static final Logger log = LoggerFactory.getLogger(EmbeddedFlashRenderer.class);
    public static final String RESOURCE_TYPE = "application/x-shockwave-flash";

    private String getUrlWithXsrfToken(String givenUrl) {
        URL url;
        URL requestUrl;
        XsrfTokenGenerator xsrfTokenGenerator = this.getXsrfTokenGenerator();
        if (xsrfTokenGenerator == null) {
            return givenUrl;
        }
        try {
            requestUrl = new URL(ServletContextThreadLocal.getRequest().getRequestURL().toString());
            url = new URL(requestUrl, givenUrl);
        }
        catch (MalformedURLException e) {
            return givenUrl;
        }
        if (UrlUtils.isSameOrigin((URL)url, (URL)requestUrl)) {
            String token = xsrfTokenGenerator.getToken(ServletContextThreadLocal.getRequest(), true);
            UrlBuilder uBuilder = new UrlBuilder(url.toString());
            uBuilder.add(xsrfTokenGenerator.getXsrfTokenName(), token);
            return uBuilder.toString();
        }
        return givenUrl;
    }

    @Override
    protected Map<String, Object> refineParams(Attachment attachment, Map<String, Object> properties) {
        if (!properties.containsKey("classid")) {
            properties.put("classid", "clsid:D27CDB6E-AE6D-11cf-96B8-444553540000");
        }
        if (!properties.containsKey("codebase")) {
            properties.put("codebase", "https://download.macromedia.com/pub/shockwave/cabs/flash/swflash.cab#version=6,0,0,0");
        }
        if (!properties.containsKey("pluginspage")) {
            properties.put("pluginspage", "https://www.macromedia.com/shockwave/download/index.cgi?P1_Prod_Version=ShockwaveFlash");
        }
        if (!properties.containsKey("type")) {
            properties.put("type", RESOURCE_TYPE);
        }
        if (!properties.containsKey("quality")) {
            properties.put("quality", "high");
        }
        if (!properties.containsKey("loop")) {
            properties.put("loop", "false");
        }
        if (!properties.containsKey("menu")) {
            properties.put("menu", "false");
        }
        if (!properties.containsKey("scale")) {
            properties.put("scale", "exactfit");
        }
        if (!properties.containsKey("wmode")) {
            properties.put("wmode", "transparent");
        }
        if (properties.containsKey("autostart")) {
            properties.put("play", properties.get("autostart"));
        }
        if (properties.get("movie") == null) {
            properties.put("movie", properties.get("object"));
        }
        properties.put("AllowScriptAccess", "never");
        for (String urlKey : Arrays.asList("data", "movie", "src")) {
            if (!properties.containsKey(urlKey)) continue;
            String url = this.getUrlWithXsrfToken(properties.get(urlKey).toString());
            properties.put(urlKey, url);
        }
        return properties;
    }

    @Override
    public String renderResource(EmbeddedObject resource, RenderContext context) {
        Map<String, Object> contextMap = this.setupObjectProperties(resource, context);
        Map wrapperContextMap = MacroUtils.defaultVelocityContext();
        wrapperContextMap.put("properties", contextMap);
        wrapperContextMap.put("validObjectTags", validObjectTags);
        wrapperContextMap.put("validEmbedTags", validEmbedTags);
        wrapperContextMap.put("validParamTags", validParamTags);
        wrapperContextMap.put("attachmentDownloadPath", resource.getAttachment().getDownloadPath());
        wrapperContextMap.put("constantObjectTags", this.getConstantTags());
        wrapperContextMap.put("constantParamTags", this.getConstantTags());
        wrapperContextMap.put("constantEmbedTags", this.getConstantTags());
        String renderedObjectHtml = VelocityUtils.getRenderedTemplate((String)"templates/embeddedobject.vm", (Map)wrapperContextMap);
        wrapperContextMap.put("embeddedObject", new HtmlFragment((Object)renderedObjectHtml));
        return VelocityUtils.getRenderedTemplate((String)"templates/embeddedobject-wrapper.vm", (Map)wrapperContextMap);
    }

    public boolean matchesType(EmbeddedObject resource) {
        return resource.getContentType().startsWith(RESOURCE_TYPE);
    }

    private Map<String, String> getConstantTags() {
        HashMap<String, String> constantTags = new HashMap<String, String>();
        constantTags.put("AllowScriptAccess", "never");
        constantTags.put("allowNetworking", "none");
        return Collections.unmodifiableMap(constantTags);
    }
}

