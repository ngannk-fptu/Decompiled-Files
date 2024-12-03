/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 */
package com.addonengine.addons.analytics.rest.util;

import com.addonengine.addons.analytics.rest.util.CustomParam;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b&\u0018\u0000*\u0006\b\u0000\u0010\u0001 \u00012\b\u0012\u0004\u0012\u0002H\u00010\u0002B\u0005\u00a2\u0006\u0002\u0010\u0003J(\u0010\u0004\u001a\u0002H\u0001\"\u0010\b\u0001\u0010\u0001\u0018\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0084\b\u00a2\u0006\u0002\u0010\b\u00a8\u0006\t"}, d2={"Lcom/addonengine/addons/analytics/rest/util/CaseInsensitiveEnumParam;", "T", "Lcom/addonengine/addons/analytics/rest/util/CustomParam;", "()V", "stringToEnum", "", "stringValue", "", "(Ljava/lang/String;)Ljava/lang/Enum;", "analytics"})
public abstract class CaseInsensitiveEnumParam<T>
implements CustomParam<T> {
    protected final /* synthetic */ <T extends Enum<T>> T stringToEnum(String stringValue) {
        Intrinsics.checkNotNullParameter((Object)stringValue, (String)"stringValue");
        boolean $i$f$stringToEnum = false;
        String string = stringValue.toUpperCase();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"this as java.lang.String).toUpperCase()");
        String string2 = string;
        Intrinsics.reifiedOperationMarker((int)5, (String)"T");
        return Enum.valueOf(null, string2);
    }
}

