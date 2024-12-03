/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.rest.util;

import com.addonengine.addons.analytics.rest.util.CaseInsensitiveEnumParam;
import com.addonengine.addons.analytics.service.model.ContentSortField;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0005R\u0014\u0010\u0006\u001a\u00020\u0002X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b\u00a8\u0006\t"}, d2={"Lcom/addonengine/addons/analytics/rest/util/ContentSortFieldParam;", "Lcom/addonengine/addons/analytics/rest/util/CaseInsensitiveEnumParam;", "Lcom/addonengine/addons/analytics/service/model/ContentSortField;", "param", "", "(Ljava/lang/String;)V", "value", "getValue", "()Lcom/addonengine/addons/analytics/service/model/ContentSortField;", "analytics"})
@SourceDebugExtension(value={"SMAP\nCustomParams.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CustomParams.kt\ncom/addonengine/addons/analytics/rest/util/ContentSortFieldParam\n+ 2 CustomParams.kt\ncom/addonengine/addons/analytics/rest/util/CaseInsensitiveEnumParam\n*L\n1#1,83:1\n21#2:84\n*S KotlinDebug\n*F\n+ 1 CustomParams.kt\ncom/addonengine/addons/analytics/rest/util/ContentSortFieldParam\n*L\n65#1:84\n*E\n"})
public final class ContentSortFieldParam
extends CaseInsensitiveEnumParam<ContentSortField> {
    @NotNull
    private final ContentSortField value;

    public ContentSortFieldParam(@NotNull String param) {
        Intrinsics.checkNotNullParameter((Object)param, (String)"param");
        CaseInsensitiveEnumParam this_$iv = this;
        boolean $i$f$stringToEnum = false;
        String string = param.toUpperCase();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"this as java.lang.String).toUpperCase()");
        this.value = ContentSortField.valueOf(string);
    }

    @Override
    @NotNull
    public ContentSortField getValue() {
        return this.value;
    }
}

