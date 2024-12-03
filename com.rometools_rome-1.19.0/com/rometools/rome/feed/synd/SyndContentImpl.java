/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.synd;

import com.rometools.rome.feed.CopyFrom;
import com.rometools.rome.feed.impl.CloneableBean;
import com.rometools.rome.feed.impl.CopyFromHelper;
import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import com.rometools.rome.feed.synd.SyndContent;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SyndContentImpl
implements Serializable,
SyndContent {
    private static final long serialVersionUID = 1L;
    private static final CopyFromHelper COPY_FROM_HELPER;
    private String type;
    private String value;
    private String mode;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, Collections.<String>emptySet());
    }

    public boolean equals(Object other) {
        return EqualsBean.beanEquals(SyndContent.class, this, other);
    }

    public int hashCode() {
        return EqualsBean.beanHashCode(this);
    }

    public String toString() {
        return ToStringBean.toString(SyndContent.class, this);
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getMode() {
        return this.mode;
    }

    @Override
    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    public Class<SyndContent> getInterface() {
        return SyndContent.class;
    }

    @Override
    public void copyFrom(CopyFrom obj) {
        COPY_FROM_HELPER.copy(this, obj);
    }

    static {
        HashMap basePropInterfaceMap = new HashMap();
        basePropInterfaceMap.put("type", String.class);
        basePropInterfaceMap.put("value", String.class);
        Map<Class<? extends CopyFrom>, Class<?>> basePropClassImplMap = Collections.emptyMap();
        COPY_FROM_HELPER = new CopyFromHelper(SyndContent.class, basePropInterfaceMap, basePropClassImplMap);
    }
}

