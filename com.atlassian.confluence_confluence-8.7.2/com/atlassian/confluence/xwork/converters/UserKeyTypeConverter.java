/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.apache.struts2.util.StrutsTypeConverter
 */
package com.atlassian.confluence.xwork.converters;

import com.atlassian.sal.api.user.UserKey;
import java.util.Map;
import org.apache.struts2.util.StrutsTypeConverter;

public class UserKeyTypeConverter
extends StrutsTypeConverter {
    public Object convertFromString(Map context, String[] values, Class toClass) {
        return new UserKey(values[0]);
    }

    public String convertToString(Map context, Object o) {
        return o.toString();
    }
}

