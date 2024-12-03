/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.expand.Expandable
 *  com.atlassian.plugins.rest.common.expand.parameter.DefaultExpandParameter
 *  com.atlassian.plugins.rest.common.expand.parameter.ExpandParameter
 *  javax.servlet.http.HttpServletRequest
 *  javax.xml.bind.annotation.XmlElement
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.Validate
 */
package com.atlassian.crowd.plugin.rest.util;

import com.atlassian.plugins.rest.common.expand.Expandable;
import com.atlassian.plugins.rest.common.expand.parameter.DefaultExpandParameter;
import com.atlassian.plugins.rest.common.expand.parameter.ExpandParameter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

public class EntityExpansionUtil {
    public static final String EXPAND_PARAM = "expand";

    private EntityExpansionUtil() {
    }

    public static boolean shouldExpandField(Class clazz, String fieldName, HttpServletRequest request) {
        Validate.notNull((Object)clazz);
        Validate.notNull((Object)fieldName);
        Validate.notNull((Object)request);
        ExpandParameter expandParameter = EntityExpansionUtil.getExpandParameter(request);
        return EntityExpansionUtil.shouldExpandField(clazz, fieldName, expandParameter);
    }

    public static boolean shouldExpandField(Class clazz, String fieldName, ExpandParameter expandParameter) {
        Validate.notNull((Object)clazz);
        Validate.notNull((Object)fieldName);
        Validate.notNull((Object)expandParameter);
        try {
            Field attrField = clazz.getDeclaredField(fieldName);
            Expandable attrExpandable = EntityExpansionUtil.getExpandable(attrField);
            return expandParameter.shouldExpand(attrExpandable);
        }
        catch (NoSuchFieldException e) {
            throw new IllegalArgumentException(String.format("Could not find field %s in class %s", fieldName, clazz.getCanonicalName()), e);
        }
    }

    public static ExpandParameter getExpandParameter(HttpServletRequest request) {
        Validate.notNull((Object)request);
        String[] expandValues = request.getParameterValues(EXPAND_PARAM);
        return new DefaultExpandParameter(expandValues != null ? Arrays.asList(expandValues) : Collections.emptyList());
    }

    private static Expandable getExpandable(Field field) {
        if (field == null) {
            return null;
        }
        Expandable expandable = field.getAnnotation(Expandable.class);
        if (expandable == null) {
            return null;
        }
        if (StringUtils.isNotEmpty((CharSequence)expandable.value())) {
            return expandable;
        }
        XmlElement xmlElement = field.getAnnotation(XmlElement.class);
        if (xmlElement != null && StringUtils.isNotEmpty((CharSequence)xmlElement.name()) && !StringUtils.equals((CharSequence)"##default", (CharSequence)xmlElement.name())) {
            return new ExpandableWithValue(xmlElement.name());
        }
        return new ExpandableWithValue(field.getName());
    }

    private static class ExpandableWithValue
    implements Expandable {
        private final String value;

        public ExpandableWithValue(String value) {
            this.value = value;
        }

        public String value() {
            return this.value;
        }

        public Class<? extends Annotation> annotationType() {
            return Expandable.class;
        }
    }
}

