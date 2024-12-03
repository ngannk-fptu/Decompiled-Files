/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.util;

import com.opensymphony.xwork2.conversion.impl.DefaultTypeConverter;
import java.util.Map;

public abstract class StrutsTypeConverter
extends DefaultTypeConverter {
    public Object convertValue(Map context, Object o, Class toClass) {
        if (toClass.equals(String.class)) {
            return this.convertToString(context, o);
        }
        if (o instanceof String[]) {
            return this.convertFromString(context, (String[])o, toClass);
        }
        if (o instanceof String) {
            return this.convertFromString(context, new String[]{(String)o}, toClass);
        }
        return this.performFallbackConversion(context, o, toClass);
    }

    protected Object performFallbackConversion(Map context, Object o, Class toClass) {
        return super.convertValue(context, o, toClass);
    }

    public abstract Object convertFromString(Map var1, String[] var2, Class var3);

    public abstract String convertToString(Map var1, Object var2);
}

