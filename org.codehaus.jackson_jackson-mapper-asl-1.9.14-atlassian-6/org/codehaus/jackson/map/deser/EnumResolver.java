/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser;

import java.util.HashMap;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Deprecated
public final class EnumResolver<T extends Enum<T>>
extends org.codehaus.jackson.map.util.EnumResolver<T> {
    private EnumResolver(Class<T> enumClass, T[] enums, HashMap<String, T> map) {
        super(enumClass, enums, map);
    }
}

