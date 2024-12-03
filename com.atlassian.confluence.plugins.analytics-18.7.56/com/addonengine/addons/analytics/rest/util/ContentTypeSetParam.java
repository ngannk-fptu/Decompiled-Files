/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.rest.util;

import com.addonengine.addons.analytics.rest.util.CaseInsensitiveEnumParam;
import com.addonengine.addons.analytics.rest.util.CommaSeparatedEnumParam;
import com.addonengine.addons.analytics.service.model.ContentType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\"\n\u0002\b\u0003\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u00a2\u0006\u0002\u0010\u0005R\u001a\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00020\u0007X\u0096\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t\u00a8\u0006\n"}, d2={"Lcom/addonengine/addons/analytics/rest/util/ContentTypeSetParam;", "Lcom/addonengine/addons/analytics/rest/util/CommaSeparatedEnumParam;", "Lcom/addonengine/addons/analytics/service/model/ContentType;", "param", "", "(Ljava/lang/String;)V", "value", "", "getValue", "()Ljava/util/Set;", "analytics"})
@SourceDebugExtension(value={"SMAP\nCustomParams.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CustomParams.kt\ncom/addonengine/addons/analytics/rest/util/ContentTypeSetParam\n+ 2 CustomParams.kt\ncom/addonengine/addons/analytics/rest/util/CommaSeparatedEnumParam\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 CustomParams.kt\ncom/addonengine/addons/analytics/rest/util/CaseInsensitiveEnumParam\n*L\n1#1,83:1\n27#2:84\n28#2:90\n1549#3:85\n1620#3,2:86\n1622#3:89\n21#4:88\n*S KotlinDebug\n*F\n+ 1 CustomParams.kt\ncom/addonengine/addons/analytics/rest/util/ContentTypeSetParam\n*L\n53#1:84\n53#1:90\n53#1:85\n53#1:86,2\n53#1:89\n53#1:88\n*E\n"})
public final class ContentTypeSetParam
extends CommaSeparatedEnumParam<ContentType> {
    @NotNull
    private final Set<ContentType> value;

    /*
     * WARNING - void declaration
     */
    public ContentTypeSetParam(@NotNull String param) {
        void $this$mapTo$iv$iv$iv;
        Intrinsics.checkNotNullParameter((Object)param, (String)"param");
        CommaSeparatedEnumParam commaSeparatedEnumParam = this;
        ContentTypeSetParam contentTypeSetParam = this;
        boolean $i$f$stringToEnumSet = false;
        char[] cArray = new char[]{','};
        Iterable $this$map$iv$iv = StringsKt.split$default((CharSequence)param, (char[])cArray, (boolean)false, (int)0, (int)6, null);
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv$iv;
        Collection destination$iv$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv$iv : $this$mapTo$iv$iv$iv) {
            void p0$iv;
            void this_$iv;
            String string = (String)item$iv$iv$iv;
            Collection collection = destination$iv$iv$iv;
            boolean bl = false;
            CaseInsensitiveEnumParam this_$iv$iv = (CaseInsensitiveEnumParam)this_$iv;
            boolean $i$f$stringToEnum = false;
            String string2 = p0$iv.toUpperCase();
            Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"this as java.lang.String).toUpperCase()");
            collection.add(ContentType.valueOf(string2));
        }
        List enumList$iv = (List)destination$iv$iv$iv;
        contentTypeSetParam.value = CollectionsKt.toSet((Iterable)enumList$iv);
    }

    @Override
    @NotNull
    public Set<ContentType> getValue() {
        return this.value;
    }
}

