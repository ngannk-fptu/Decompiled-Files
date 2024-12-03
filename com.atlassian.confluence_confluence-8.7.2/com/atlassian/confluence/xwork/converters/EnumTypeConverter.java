/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.struts2.util.StrutsTypeConverter
 */
package com.atlassian.confluence.xwork.converters;

import java.util.Map;
import org.apache.struts2.util.StrutsTypeConverter;

public class EnumTypeConverter
extends StrutsTypeConverter {
    public Object convertFromString(Map context, String[] values, Class toClass) {
        return Enum.valueOf(toClass, values[0]);
    }

    public String convertToString(Map context, Object o) {
        return o.toString();
    }
}

