/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.text.StringsKt
 */
package com.addonengine.addons.analytics.rest.util;

import com.addonengine.addons.analytics.rest.util.CaseInsensitiveEnumParam;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\"\n\u0002\b\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\b&\u0018\u0000*\u0006\b\u0000\u0010\u0001 \u00012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u0002H\u00010\u00030\u0002B\u0005\u00a2\u0006\u0002\u0010\u0004J)\u0010\u0005\u001a\b\u0012\u0004\u0012\u0002H\u00010\u0003\"\u0010\b\u0001\u0010\u0001\u0018\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0084\b\u00a8\u0006\t"}, d2={"Lcom/addonengine/addons/analytics/rest/util/CommaSeparatedEnumParam;", "T", "Lcom/addonengine/addons/analytics/rest/util/CaseInsensitiveEnumParam;", "", "()V", "stringToEnumSet", "", "stringValue", "", "analytics"})
@SourceDebugExtension(value={"SMAP\nCustomParams.kt\nKotlin\n*S Kotlin\n*F\n+ 1 CustomParams.kt\ncom/addonengine/addons/analytics/rest/util/CommaSeparatedEnumParam\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 CustomParams.kt\ncom/addonengine/addons/analytics/rest/util/CaseInsensitiveEnumParam\n*L\n1#1,83:1\n1549#2:84\n1620#2,2:85\n1622#2:88\n21#3:87\n*S KotlinDebug\n*F\n+ 1 CustomParams.kt\ncom/addonengine/addons/analytics/rest/util/CommaSeparatedEnumParam\n*L\n27#1:84\n27#1:85,2\n27#1:88\n27#1:87\n*E\n"})
public abstract class CommaSeparatedEnumParam<T>
extends CaseInsensitiveEnumParam<Set<? extends T>> {
    /*
     * WARNING - void declaration
     */
    protected final /* synthetic */ <T extends Enum<T>> Set<T> stringToEnumSet(String stringValue) {
        void $this$mapTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)stringValue, (String)"stringValue");
        boolean $i$f$stringToEnumSet = false;
        char[] cArray = new char[]{','};
        Iterable $this$map$iv = StringsKt.split$default((CharSequence)stringValue, (char[])cArray, (boolean)false, (int)0, (int)6, null);
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            String string;
            void p0;
            String string2 = (String)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            CaseInsensitiveEnumParam this_$iv = this;
            boolean $i$f$stringToEnum = false;
            Intrinsics.checkNotNullExpressionValue((Object)p0.toUpperCase(), (String)"this as java.lang.String).toUpperCase()");
            Intrinsics.reifiedOperationMarker((int)5, (String)"T");
            collection.add(Enum.valueOf(null, string));
        }
        List enumList = (List)destination$iv$iv;
        return CollectionsKt.toSet((Iterable)enumList);
    }
}

