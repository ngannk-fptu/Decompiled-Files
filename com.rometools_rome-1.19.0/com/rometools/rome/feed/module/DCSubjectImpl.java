/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.module;

import com.rometools.rome.feed.CopyFrom;
import com.rometools.rome.feed.impl.CloneableBean;
import com.rometools.rome.feed.impl.CopyFromHelper;
import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import com.rometools.rome.feed.module.DCSubject;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DCSubjectImpl
implements Cloneable,
Serializable,
DCSubject {
    private static final long serialVersionUID = 1L;
    private static final CopyFromHelper COPY_FROM_HELPER;
    private String taxonomyUri;
    private String value;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, Collections.<String>emptySet());
    }

    public boolean equals(Object other) {
        if (!(other instanceof DCSubjectImpl)) {
            return false;
        }
        return EqualsBean.beanEquals(this.getClass(), this, other);
    }

    public int hashCode() {
        return EqualsBean.beanHashCode(this);
    }

    public String toString() {
        return ToStringBean.toString(this.getClass(), this);
    }

    @Override
    public String getTaxonomyUri() {
        return this.taxonomyUri;
    }

    @Override
    public void setTaxonomyUri(String taxonomyUri) {
        this.taxonomyUri = taxonomyUri;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public void setValue(String value) {
        this.value = value;
    }

    public Class<DCSubject> getInterface() {
        return DCSubject.class;
    }

    @Override
    public void copyFrom(CopyFrom obj) {
        COPY_FROM_HELPER.copy(this, obj);
    }

    static {
        HashMap basePropInterfaceMap = new HashMap();
        basePropInterfaceMap.put("taxonomyUri", String.class);
        basePropInterfaceMap.put("value", String.class);
        Map<Class<? extends CopyFrom>, Class<?>> basePropClassImplMap = Collections.emptyMap();
        COPY_FROM_HELPER = new CopyFromHelper(DCSubject.class, basePropInterfaceMap, basePropClassImplMap);
    }
}

