/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import java.lang.reflect.Member;
import java.util.Map;
import ognl.OgnlOps;
import ognl.TypeConverter;

public class DefaultTypeConverter
implements TypeConverter {
    public Object convertValue(Map context, Object value, Class toType) {
        return OgnlOps.convertValue(value, toType);
    }

    @Override
    public Object convertValue(Map context, Object target, Member member, String propertyName, Object value, Class toType) {
        return this.convertValue(context, value, toType);
    }
}

