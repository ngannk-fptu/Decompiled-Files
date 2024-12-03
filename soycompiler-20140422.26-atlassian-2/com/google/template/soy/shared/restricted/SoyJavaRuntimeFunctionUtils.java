/*
 * Decompiled with CFR 0.152.
 */
package com.google.template.soy.shared.restricted;

import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.StringData;

@Deprecated
public class SoyJavaRuntimeFunctionUtils {
    private SoyJavaRuntimeFunctionUtils() {
    }

    public static SoyData toSoyData(boolean value) {
        return BooleanData.forValue(value);
    }

    public static SoyData toSoyData(int value) {
        return IntegerData.forValue(value);
    }

    public static SoyData toSoyData(double value) {
        return FloatData.forValue(value);
    }

    public static SoyData toSoyData(String value) {
        return StringData.forValue(value);
    }
}

