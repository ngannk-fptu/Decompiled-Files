/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.renderer.embedded;

import com.atlassian.renderer.util.FileTypeUtil;
import com.atlassian.renderer.util.UrlUtil;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

public class EmbeddedResourceParser {
    private String originalText;
    private String resource;
    private String filename;
    private String page;
    private String space;
    private Properties properties;
    private boolean isExternal;
    private String type;
    protected static final String UNKNOWN_MIME_TYPE = "application/octet-stream";
    protected static final String UNKNOWN_IMAGE_MIME_TYPE = "image/unknown";

    public EmbeddedResourceParser(String originalText) {
        this.originalText = originalText;
        this.parse(this.originalText);
    }

    private void parse(String s) {
        String propertiesString;
        String resourceString;
        int index = s.indexOf(124);
        if (index == -1) {
            resourceString = s;
            propertiesString = "";
        } else {
            resourceString = s.substring(0, index);
            propertiesString = s.substring(index + 1);
        }
        this.parseResource(resourceString);
        this.properties = this.parseProperties(propertiesString);
    }

    private void parseResource(String resourceString) {
        this.resource = resourceString;
        if (UrlUtil.startsWithUrl(resourceString)) {
            this.isExternal = true;
            String tempResourceString = resourceString.substring(resourceString.lastIndexOf(47) + 1);
            if (tempResourceString.indexOf("?") > -1) {
                tempResourceString = tempResourceString.substring(0, tempResourceString.indexOf("?"));
            }
            this.filename = tempResourceString;
        } else {
            if (resourceString.indexOf(58) != -1) {
                this.space = resourceString.substring(0, resourceString.indexOf(58));
                resourceString = resourceString.substring(resourceString.indexOf(58) + 1);
            }
            if (resourceString.indexOf(94) != -1) {
                this.page = resourceString.substring(0, resourceString.indexOf(94));
                resourceString = resourceString.substring(resourceString.indexOf(94) + 1);
            }
            this.filename = resourceString;
        }
        this.type = FileTypeUtil.getContentType(this.filename);
        if (this.isExternal && UNKNOWN_MIME_TYPE.equals(this.type)) {
            this.type = UNKNOWN_IMAGE_MIME_TYPE;
        }
    }

    private Properties parseProperties(String parameterString) {
        Properties props = new Properties();
        StringTokenizer st = new StringTokenizer(parameterString, ",");
        while (st.hasMoreTokens()) {
            String paramPair = st.nextToken();
            if (paramPair.indexOf(61) > 0) {
                String paramName = paramPair.substring(0, paramPair.indexOf(61)).trim();
                String paramValue = paramPair.substring(paramPair.indexOf(61) + 1).trim();
                if (paramValue.startsWith("\"") && paramValue.endsWith("\"")) {
                    paramValue = paramValue.substring(1, paramValue.length() - 1);
                }
                props.put(paramName, paramValue);
                if (!"type".equals(paramName)) continue;
                this.type = paramValue;
                continue;
            }
            props.put(paramPair, "");
        }
        return props;
    }

    public String getOriginalText() {
        return this.originalText;
    }

    public String getResource() {
        return this.resource;
    }

    public String getPage() {
        return this.page;
    }

    public String getSpace() {
        return this.space;
    }

    public Properties getProperties() {
        Properties newProps = new Properties();
        newProps.putAll((Map<?, ?>)this.properties);
        return newProps;
    }

    public boolean isExternal() {
        return this.isExternal;
    }

    public String getFilename() {
        return this.filename;
    }

    public String getType() {
        return this.type;
    }
}

