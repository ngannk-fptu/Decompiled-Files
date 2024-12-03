/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlList
 *  javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter
 */
package com.sun.xml.bind.v2.model.core;

import com.sun.xml.bind.v2.model.annotation.AnnotationReader;
import com.sun.xml.bind.v2.model.core.Adapter;
import com.sun.xml.bind.v2.model.impl.ModelBuilderI;
import com.sun.xml.bind.v2.model.nav.Navigator;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

public final class Ref<T, C> {
    public final T type;
    public final Adapter<T, C> adapter;
    public final boolean valueList;

    public Ref(T type) {
        this(type, null, false);
    }

    public Ref(T type, Adapter<T, C> adapter, boolean valueList) {
        this.adapter = adapter;
        if (adapter != null) {
            type = adapter.defaultType;
        }
        this.type = type;
        this.valueList = valueList;
    }

    public Ref(ModelBuilderI<T, C, ?, ?> builder, T type, XmlJavaTypeAdapter xjta, XmlList xl) {
        this(builder.getReader(), builder.getNavigator(), type, xjta, xl);
    }

    public Ref(AnnotationReader<T, C, ?, ?> reader, Navigator<T, C, ?, ?> nav, T type, XmlJavaTypeAdapter xjta, XmlList xl) {
        Adapter<T, C> adapter = null;
        if (xjta != null) {
            adapter = new Adapter<T, C>(xjta, reader, nav);
            type = adapter.defaultType;
        }
        this.type = type;
        this.adapter = adapter;
        this.valueList = xl != null;
    }
}

