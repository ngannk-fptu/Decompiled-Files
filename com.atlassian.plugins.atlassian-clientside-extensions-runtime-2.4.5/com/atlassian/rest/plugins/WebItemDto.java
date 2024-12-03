/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.api.WebItem
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.rest.plugins;

import com.atlassian.plugin.web.api.WebItem;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="webitem")
public class WebItemDto {
    private static String[] DEFAULT_ALLOWED_PARAMS = new String[]{"glyph", "tooltip"};
    @XmlElement
    private String key;
    @XmlElement
    private String location;
    @XmlElement
    private String url;
    @XmlElement
    private String accessKey;
    @XmlElement
    private String label;
    @XmlElement
    private String title;
    @XmlElement
    private String styleClass;
    @XmlElement
    private String linkId;
    @XmlElement
    private Map<String, String> params;
    @XmlElement
    private int weight;
    @XmlElement
    private Map<String, String> attributes;

    public WebItemDto() {
    }

    public WebItemDto(WebItem item, @Nonnull String contextPath) {
        if (null != item) {
            this.key = item.getCompleteKey();
            this.location = item.getSection();
            this.accessKey = item.getAccessKey();
            this.styleClass = item.getStyleClass();
            this.linkId = item.getId();
            this.params = item.getParams();
            this.weight = item.getWeight();
            this.title = item.getTitle();
            this.url = WebItemDto.addContextPathToUrlIfNeeded(item.getUrl(), contextPath);
            this.attributes = new HashMap<String, String>();
            this.attributes.put("url", this.url);
            this.attributes.put("label", item.getLabel());
            this.attributes.put("title", this.title);
            this.attributes.values().removeIf(Objects::isNull);
            this.attributes.putAll(WebItemDto.extendByParams(item.getParams()));
        }
    }

    public String getKey() {
        return this.key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccessKey() {
        return this.accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStyleClass() {
        return this.styleClass;
    }

    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    public String getLinkId() {
        return this.linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public Map<String, String> getParams() {
        return this.params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public int getWeight() {
        return this.weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public Map<String, String> getAttributes() {
        return this.attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    private static Map<String, String> extendByParams(Map<String, String> params) {
        if (params == null) {
            return new HashMap<String, String>();
        }
        return params.entrySet().stream().filter(map -> Arrays.asList(DEFAULT_ALLOWED_PARAMS).contains(map.getKey()) && map.getValue() != null).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Nullable
    private static String addContextPathToUrlIfNeeded(@Nullable String url, @Nonnull String contextPath) {
        if (!Objects.isNull(url) && url.startsWith("/")) {
            return contextPath + url;
        }
        return url;
    }
}

