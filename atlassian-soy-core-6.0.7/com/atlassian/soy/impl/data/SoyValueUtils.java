/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.soy.renderer.SanitizationType
 *  com.atlassian.soy.renderer.SanitizedString
 *  com.google.common.base.Function
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.template.soy.data.SanitizedContent
 *  com.google.template.soy.data.SanitizedContent$ContentKind
 *  com.google.template.soy.data.SoyDict
 *  com.google.template.soy.data.SoyList
 *  com.google.template.soy.data.SoyValue
 *  com.google.template.soy.data.restricted.BooleanData
 *  com.google.template.soy.data.restricted.FloatData
 *  com.google.template.soy.data.restricted.IntegerData
 *  com.google.template.soy.data.restricted.NullData
 *  com.google.template.soy.data.restricted.StringData
 *  com.google.template.soy.data.restricted.UndefinedData
 */
package com.atlassian.soy.impl.data;

import com.atlassian.soy.impl.data.EnumSoyValue;
import com.atlassian.soy.impl.data.JavaBeanSoyDict;
import com.atlassian.soy.renderer.SanitizationType;
import com.atlassian.soy.renderer.SanitizedString;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyDict;
import com.google.template.soy.data.SoyList;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.NullData;
import com.google.template.soy.data.restricted.StringData;
import com.google.template.soy.data.restricted.UndefinedData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SoyValueUtils {
    private SoyValueUtils() {
        throw new UnsupportedOperationException();
    }

    public static Object fromSoyValue(SoyValue data) {
        if (data == NullData.INSTANCE || data == UndefinedData.INSTANCE) {
            return null;
        }
        if (data instanceof JavaBeanSoyDict) {
            return ((JavaBeanSoyDict)data).getDelegate();
        }
        if (data instanceof EnumSoyValue) {
            return ((EnumSoyValue)data).getValue();
        }
        if (data instanceof SoyDict) {
            return new HashMap(Maps.transformValues((Map)((SoyDict)data).asResolvedJavaStringMap(), (Function)new Function<SoyValue, Object>(){

                public Object apply(SoyValue from) {
                    return SoyValueUtils.fromSoyValue(from);
                }
            }));
        }
        if (data instanceof SoyList) {
            return new ArrayList(Lists.transform((List)((SoyList)data).asResolvedJavaList(), (Function)new Function<SoyValue, Object>(){

                public Object apply(SoyValue from) {
                    return SoyValueUtils.fromSoyValue(from);
                }
            }));
        }
        if (data instanceof StringData) {
            return data.stringValue();
        }
        if (data instanceof SanitizedContent) {
            SanitizedContent sanitizedContent = (SanitizedContent)data;
            return new SanitizedString(sanitizedContent.getContent(), SoyValueUtils.toSanitizationType(sanitizedContent.getContentKind()));
        }
        if (data instanceof IntegerData) {
            return ((IntegerData)data).getValue();
        }
        if (data instanceof BooleanData) {
            return ((BooleanData)data).getValue();
        }
        if (data instanceof FloatData) {
            return ((FloatData)data).getValue();
        }
        return data.stringValue();
    }

    private static SanitizationType toSanitizationType(SanitizedContent.ContentKind kind) {
        switch (kind) {
            case CSS: {
                return SanitizationType.CSS;
            }
            case JS: {
                return SanitizationType.JS;
            }
            case JS_STR_CHARS: {
                return SanitizationType.JS_STRING;
            }
            case HTML: {
                return SanitizationType.HTML;
            }
            case ATTRIBUTES: {
                return SanitizationType.HTML_ATTRIBUTE;
            }
            case TEXT: {
                return SanitizationType.TEXT;
            }
            case URI: {
                return SanitizationType.URI;
            }
        }
        throw new UnsupportedOperationException("Unsupported kind " + kind);
    }
}

