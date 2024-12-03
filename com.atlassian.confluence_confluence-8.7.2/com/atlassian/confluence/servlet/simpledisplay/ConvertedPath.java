/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.velocity.VelocityContext
 *  org.apache.velocity.context.Context
 */
package com.atlassian.confluence.servlet.simpledisplay;

import com.atlassian.confluence.servlet.simpledisplay.PathConversionAction;
import com.atlassian.confluence.servlet.simpledisplay.VelocityEngineResolver;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.web.UrlBuilder;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.context.Context;

public class ConvertedPath {
    private final String url;
    private final PathConversionAction action;
    private final Map<String, String> parameters;
    private final boolean encodeAnchor;
    private final VelocityEngineResolver resolver;

    public ConvertedPath(String url) {
        this(url, PathConversionAction.FORWARD);
    }

    public ConvertedPath(String url, PathConversionAction action) {
        this(url, action, true);
    }

    public ConvertedPath(String url, PathConversionAction action, boolean encodeAnchor) {
        this(url, action, encodeAnchor, null);
    }

    @Deprecated
    public ConvertedPath(String pathTemplate, VelocityEngineResolver resolver) {
        this(pathTemplate, PathConversionAction.FORWARD, resolver);
    }

    private ConvertedPath(String url, PathConversionAction action, VelocityEngineResolver resolver) {
        this(url, action, true, resolver);
    }

    private ConvertedPath(String url, PathConversionAction action, boolean encodeAnchor, VelocityEngineResolver resolver) {
        this.url = url;
        this.action = action;
        this.encodeAnchor = encodeAnchor;
        this.parameters = new HashMap<String, String>();
        this.resolver = resolver;
    }

    public void addParameter(String name, String value) {
        this.parameters.put(name, value);
    }

    public String getUrl() {
        return this.url;
    }

    public PathConversionAction getAction() {
        return this.action;
    }

    public String getPath() {
        return this.getPath(Collections.emptyMap());
    }

    @Deprecated
    public String getPath(Map<String, String[]> queryParameters) {
        if (queryParameters == null) {
            queryParameters = Collections.emptyMap();
        }
        if (this.resolver != null) {
            return this.velocityRender(queryParameters);
        }
        UrlBuilder builder = new UrlBuilder(this.url);
        HashMap<String, Object> combinedParameters = new HashMap<String, Object>(this.parameters.size() + queryParameters.size());
        combinedParameters.putAll(queryParameters);
        for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
            combinedParameters.put(entry.getKey(), new String[]{entry.getValue()});
        }
        for (Map.Entry<String, String> entry : combinedParameters.entrySet()) {
            builder.add(entry.getKey(), (String[])entry.getValue());
        }
        return builder.toUrl(this.encodeAnchor);
    }

    public Map getParameters() {
        HashMap<String, String> encodedParameters = new HashMap<String, String>(this.parameters.size());
        for (Map.Entry<String, String> entry : this.parameters.entrySet()) {
            encodedParameters.put(entry.getKey(), HtmlUtil.urlEncode(entry.getValue()));
        }
        return encodedParameters;
    }

    @Deprecated
    private String velocityRender(Map<String, String[]> queryParameters) {
        String url = this.renderUrlTemplate();
        return ConvertedPath.combineQueryParameters(url, queryParameters, this.encodeAnchor);
    }

    @Deprecated
    private String renderUrlTemplate() {
        VelocityContext context = new VelocityContext(this.getParameters());
        try {
            StringWriter stringWriter = new StringWriter();
            this.resolver.getVelocityEngine().evaluate((Context)context, (Writer)stringWriter, "CONVERTER", this.url);
            return stringWriter.toString();
        }
        catch (Exception e) {
            throw new IllegalStateException("Could not read template from string: " + e.getMessage(), e);
        }
    }

    @Deprecated
    static String combineQueryParameters(String url, Map<String, String[]> queryParameters, boolean encodeAnchor) {
        if (queryParameters == null || queryParameters.isEmpty()) {
            return url;
        }
        HashMap<String, String[]> paramCopy = new HashMap<String, String[]>(queryParameters);
        int queryPos = url.indexOf(63);
        if (queryPos != -1 && queryPos != url.length() - 1) {
            String[] urlParameters;
            for (String urlParam : urlParameters = StringUtils.split((String)url.substring(queryPos + 1), (String)"&")) {
                String[] nameValue = StringUtils.split((String)urlParam, (char)'=');
                if (nameValue.length <= 0) continue;
                paramCopy.remove(nameValue[0]);
            }
        }
        if (paramCopy.isEmpty()) {
            return url;
        }
        UrlBuilder builder = new UrlBuilder(url);
        paramCopy.forEach(builder::add);
        return builder.toUrl(encodeAnchor);
    }
}

