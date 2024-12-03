/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.synd;

import com.rometools.rome.feed.CopyFrom;
import com.rometools.rome.feed.impl.CloneableBean;
import com.rometools.rome.feed.impl.CopyFromHelper;
import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import com.rometools.rome.feed.synd.SyndImage;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SyndImageImpl
implements Serializable,
SyndImage {
    private static final long serialVersionUID = 1L;
    private static final CopyFromHelper COPY_FROM_HELPER;
    private String title;
    private String url;
    private Integer width;
    private Integer height;
    private String link;
    private String description;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, Collections.<String>emptySet());
    }

    public boolean equals(Object other) {
        return EqualsBean.beanEquals(SyndImage.class, this, other);
    }

    public int hashCode() {
        return EqualsBean.beanHashCode(this);
    }

    public String toString() {
        return ToStringBean.toString(SyndImage.class, this);
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public Integer getWidth() {
        return this.width;
    }

    @Override
    public void setWidth(Integer width) {
        this.width = width;
    }

    @Override
    public Integer getHeight() {
        return this.height;
    }

    @Override
    public void setHeight(Integer height) {
        this.height = height;
    }

    @Override
    public String getLink() {
        return this.link;
    }

    @Override
    public void setLink(String link) {
        this.link = link;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public Class<SyndImage> getInterface() {
        return SyndImage.class;
    }

    @Override
    public void copyFrom(CopyFrom syndImage) {
        COPY_FROM_HELPER.copy(this, syndImage);
    }

    static {
        HashMap basePropInterfaceMap = new HashMap();
        basePropInterfaceMap.put("title", String.class);
        basePropInterfaceMap.put("url", String.class);
        basePropInterfaceMap.put("link", String.class);
        basePropInterfaceMap.put("width", Integer.class);
        basePropInterfaceMap.put("height", Integer.class);
        basePropInterfaceMap.put("description", String.class);
        Map<Class<? extends CopyFrom>, Class<?>> basePropClassImplMap = Collections.emptyMap();
        COPY_FROM_HELPER = new CopyFromHelper(SyndImage.class, basePropInterfaceMap, basePropClassImplMap);
    }
}

