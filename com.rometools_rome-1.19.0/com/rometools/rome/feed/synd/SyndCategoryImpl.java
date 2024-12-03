/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.synd;

import com.rometools.rome.feed.CopyFrom;
import com.rometools.rome.feed.impl.CloneableBean;
import com.rometools.rome.feed.impl.CopyFromHelper;
import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import com.rometools.rome.feed.module.DCSubject;
import com.rometools.rome.feed.module.DCSubjectImpl;
import com.rometools.rome.feed.synd.SyndCategory;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SyndCategoryImpl
implements Serializable,
SyndCategory {
    private static final long serialVersionUID = 1L;
    private static final CopyFromHelper COPY_FROM_HELPER;
    private final DCSubject subject;
    private String label;

    SyndCategoryImpl(DCSubject subject) {
        this.subject = subject;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, Collections.<String>emptySet());
    }

    public boolean equals(Object other) {
        if (!(other instanceof SyndCategoryImpl)) {
            return false;
        }
        return EqualsBean.beanEquals(SyndCategory.class, this, other);
    }

    public int hashCode() {
        return EqualsBean.beanHashCode(this);
    }

    public String toString() {
        return ToStringBean.toString(SyndCategory.class, this);
    }

    DCSubject getSubject() {
        return this.subject;
    }

    public SyndCategoryImpl() {
        this(new DCSubjectImpl());
    }

    @Override
    public String getName() {
        return this.subject.getValue();
    }

    @Override
    public void setName(String name) {
        this.subject.setValue(name);
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public String getTaxonomyUri() {
        return this.subject.getTaxonomyUri();
    }

    @Override
    public void setTaxonomyUri(String taxonomyUri) {
        this.subject.setTaxonomyUri(taxonomyUri);
    }

    @Override
    public Class<? extends CopyFrom> getInterface() {
        return SyndCategory.class;
    }

    @Override
    public void copyFrom(CopyFrom obj) {
        COPY_FROM_HELPER.copy(this, obj);
    }

    static {
        HashMap basePropInterfaceMap = new HashMap();
        basePropInterfaceMap.put("name", String.class);
        basePropInterfaceMap.put("taxonomyUri", String.class);
        basePropInterfaceMap.put("label", String.class);
        Map<Class<? extends CopyFrom>, Class<?>> basePropClassImplMap = Collections.emptyMap();
        COPY_FROM_HELPER = new CopyFromHelper(SyndCategory.class, basePropInterfaceMap, basePropClassImplMap);
    }
}

