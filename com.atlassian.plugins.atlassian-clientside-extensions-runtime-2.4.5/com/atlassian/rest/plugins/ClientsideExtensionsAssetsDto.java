/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.web.api.WebItem
 *  javax.annotation.Nonnull
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.rest.plugins;

import com.atlassian.plugin.web.api.WebItem;
import com.atlassian.rest.plugins.WebItemDto;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="assets")
public class ClientsideExtensionsAssetsDto {
    @XmlElement
    private List<WebItemDto> webItems;

    public ClientsideExtensionsAssetsDto() {
        this(Collections.emptyList(), "");
    }

    public ClientsideExtensionsAssetsDto(List<WebItem> webItems, @Nonnull String contextPath) {
        this.webItems = webItems.stream().map(webItem -> new WebItemDto((WebItem)webItem, contextPath)).sorted(Comparator.comparing(WebItemDto::getWeight)).collect(Collectors.toList());
    }

    public List<WebItemDto> getWebItems() {
        return this.webItems;
    }

    public void setWebItems(List<WebItemDto> webItems) {
        this.webItems = webItems;
    }
}

