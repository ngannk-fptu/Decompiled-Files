/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonTypeInfo$Id
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.jsontype;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface TypeIdResolver {
    public void init(JavaType var1);

    public String idFromValue(Object var1);

    public String idFromValueAndType(Object var1, Class<?> var2);

    public JavaType typeFromId(String var1);

    public JsonTypeInfo.Id getMechanism();
}

