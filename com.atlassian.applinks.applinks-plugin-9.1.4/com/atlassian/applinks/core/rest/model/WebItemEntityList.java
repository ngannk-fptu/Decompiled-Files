/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.applinks.core.rest.model;

import com.atlassian.applinks.core.rest.model.WebItemEntity;
import java.util.List;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="webItems")
public class WebItemEntityList {
    @XmlElement(name="webItem")
    private List<WebItemEntity> items;

    public WebItemEntityList() {
    }

    public WebItemEntityList(List<WebItemEntity> items) {
        this.items = items;
    }

    @Nullable
    public List<WebItemEntity> getItems() {
        return this.items;
    }
}

