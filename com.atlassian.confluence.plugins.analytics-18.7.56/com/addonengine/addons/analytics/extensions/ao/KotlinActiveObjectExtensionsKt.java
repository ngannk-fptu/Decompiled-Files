/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  kotlin.reflect.KProperty1
 *  net.java.ao.DBParam
 *  net.java.ao.Query
 *  net.java.ao.RawEntity
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.extensions.ao;

import com.atlassian.activeobjects.external.ActiveObjects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty1;
import net.java.ao.DBParam;
import net.java.ao.Query;
import net.java.ao.RawEntity;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=2, xi=48, d1={"\u0000D\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\u001a\u001f\u0010\u0000\u001a\u00020\u0001\"\u0010\b\u0000\u0010\u0002\u0018\u0001*\b\u0012\u0004\u0012\u00020\u00040\u0003*\u00020\u0005H\u0086\b\u001a,\u0010\u0006\u001a\n \u0007*\u0004\u0018\u0001H\u0002H\u0002\"\u0010\b\u0000\u0010\u0002\u0018\u0001*\b\u0012\u0004\u0012\u00020\u00040\u0003*\u00020\u0005H\u0086\b\u00a2\u0006\u0002\u0010\b\u001aJ\u0010\t\u001a\u0002H\u0002\"\u0010\b\u0000\u0010\u0002\u0018\u0001*\b\u0012\u0004\u0012\u00020\u00040\u0003*\u00020\u00052$\u0010\n\u001a \u0012\u0014\u0012\u0012\u0012\u0004\u0012\u0002H\u0002\u0012\b\b\u0001\u0012\u0004\u0018\u00010\r0\f\u0012\u0006\u0012\u0004\u0018\u00010\r0\u000bH\u0086\b\u00a2\u0006\u0002\u0010\u000e\u001aJ\u0010\u000f\u001a(\u0012\f\u0012\n \u0007*\u0004\u0018\u0001H\u0002H\u0002 \u0007*\u0014\u0012\u000e\b\u0001\u0012\n \u0007*\u0004\u0018\u0001H\u0002H\u0002\u0018\u00010\u00100\u0010\"\u0010\b\u0000\u0010\u0002\u0018\u0001*\b\u0012\u0004\u0012\u00020\u00040\u0003*\u00020\u0005H\u0086\b\u00a2\u0006\u0002\u0010\u0011\u001aR\u0010\u000f\u001a(\u0012\f\u0012\n \u0007*\u0004\u0018\u0001H\u0002H\u0002 \u0007*\u0014\u0012\u000e\b\u0001\u0012\n \u0007*\u0004\u0018\u0001H\u0002H\u0002\u0018\u00010\u00100\u0010\"\u0010\b\u0000\u0010\u0002\u0018\u0001*\b\u0012\u0004\u0012\u00020\u00040\u0003*\u00020\u00052\u0006\u0010\u0012\u001a\u00020\u0013H\u0086\b\u00a2\u0006\u0002\u0010\u0014\u001a2\u0010\u0015\u001a\u0002H\u0002\"\u0010\b\u0000\u0010\u0002\u0018\u0001*\b\u0012\u0004\u0012\u0002H\u00160\u0003\"\u0004\b\u0001\u0010\u0016*\u00020\u00052\u0006\u0010\u0017\u001a\u0002H\u0016H\u0086\b\u00a2\u0006\u0002\u0010\u0018\u001a\u001f\u0010\u0019\u001a\u00020\u001a\"\u0010\b\u0000\u0010\u0002\u0018\u0001*\b\u0012\u0004\u0012\u00020\u00040\u0003*\u00020\u0005H\u0086\b\u001a\n\u0010\u001b\u001a\u00020\u001c*\u00020\u001c\u00a8\u0006\u001d"}, d2={"count", "", "T", "Lnet/java/ao/RawEntity;", "", "Lcom/atlassian/activeobjects/external/ActiveObjects;", "create", "kotlin.jvm.PlatformType", "(Lcom/atlassian/activeobjects/external/ActiveObjects;)Lnet/java/ao/RawEntity;", "createWithProps", "entityProperties", "", "Lkotlin/reflect/KProperty1;", "", "(Lcom/atlassian/activeobjects/external/ActiveObjects;Ljava/util/Map;)Lnet/java/ao/RawEntity;", "find", "", "(Lcom/atlassian/activeobjects/external/ActiveObjects;)[Lnet/java/ao/RawEntity;", "query", "Lnet/java/ao/Query;", "(Lcom/atlassian/activeobjects/external/ActiveObjects;Lnet/java/ao/Query;)[Lnet/java/ao/RawEntity;", "get", "K", "id", "(Lcom/atlassian/activeobjects/external/ActiveObjects;Ljava/lang/Object;)Lnet/java/ao/RawEntity;", "migrate", "", "toDBParamFieldName", "", "analytics"})
@SourceDebugExtension(value={"SMAP\nKotlinActiveObjectExtensions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 KotlinActiveObjectExtensions.kt\ncom/addonengine/addons/analytics/extensions/ao/KotlinActiveObjectExtensionsKt\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n+ 4 _Strings.kt\nkotlin/text/StringsKt___StringsKt\n*L\n1#1,54:1\n125#2:55\n152#2,3:56\n37#3,2:59\n970#4:61\n1041#4,3:62\n*S KotlinDebug\n*F\n+ 1 KotlinActiveObjectExtensions.kt\ncom/addonengine/addons/analytics/extensions/ao/KotlinActiveObjectExtensionsKt\n*L\n16#1:55\n16#1:56,3\n16#1:59,2\n51#1:61\n51#1:62,3\n*E\n"})
public final class KotlinActiveObjectExtensionsKt {
    /*
     * WARNING - void declaration
     */
    public static final /* synthetic */ <T extends RawEntity<Long>> T createWithProps(ActiveObjects $this$createWithProps, Map<KProperty1<T, Object>, ? extends Object> entityProperties) {
        void $this$toTypedArray$iv;
        void $this$mapTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)$this$createWithProps, (String)"<this>");
        Intrinsics.checkNotNullParameter(entityProperties, (String)"entityProperties");
        boolean $i$f$createWithProps = false;
        Object $this$map$iv = entityProperties;
        boolean $i$f$map = false;
        Map<KProperty1<T, Object>, Object> map = $this$map$iv;
        Collection destination$iv$iv = new ArrayList($this$map$iv.size());
        boolean $i$f$mapTo = false;
        Iterator iterator = $this$mapTo$iv$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry item$iv$iv;
            Map.Entry entry = item$iv$iv = iterator.next();
            Collection collection = destination$iv$iv;
            boolean bl = false;
            KProperty1 prop = (KProperty1)entry.getKey();
            Object value = entry.getValue();
            collection.add(new DBParam(KotlinActiveObjectExtensionsKt.toDBParamFieldName(prop.getName()), value));
        }
        $this$map$iv = (List)destination$iv$iv;
        boolean $i$f$toTypedArray = false;
        Collection thisCollection$iv = (Collection)$this$toTypedArray$iv;
        DBParam[] params = thisCollection$iv.toArray(new DBParam[0]);
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        RawEntity rawEntity = $this$createWithProps.create(RawEntity.class, Arrays.copyOf(params, params.length));
        Intrinsics.checkNotNullExpressionValue((Object)rawEntity, (String)"create(...)");
        return (T)rawEntity;
    }

    public static final /* synthetic */ <T extends RawEntity<Long>> T create(ActiveObjects $this$create) {
        Intrinsics.checkNotNullParameter((Object)$this$create, (String)"<this>");
        boolean $i$f$create = false;
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        return (T)$this$create.create(RawEntity.class, new DBParam[0]);
    }

    public static final /* synthetic */ <T extends RawEntity<K>, K> T get(ActiveObjects $this$get, K id) {
        Intrinsics.checkNotNullParameter((Object)$this$get, (String)"<this>");
        boolean $i$f$get = false;
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        RawEntity rawEntity = $this$get.get(RawEntity.class, id);
        Intrinsics.checkNotNullExpressionValue((Object)rawEntity, (String)"get(...)");
        return (T)rawEntity;
    }

    public static final /* synthetic */ <T extends RawEntity<Long>> int count(ActiveObjects $this$count) {
        Intrinsics.checkNotNullParameter((Object)$this$count, (String)"<this>");
        boolean $i$f$count = false;
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        return $this$count.count(RawEntity.class);
    }

    public static final /* synthetic */ <T extends RawEntity<Long>> void migrate(ActiveObjects $this$migrate) {
        Intrinsics.checkNotNullParameter((Object)$this$migrate, (String)"<this>");
        boolean $i$f$migrate = false;
        Class[] classArray = new Class[1];
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        classArray[0] = RawEntity.class;
        $this$migrate.migrate(classArray);
    }

    public static final /* synthetic */ <T extends RawEntity<Long>> T[] find(ActiveObjects $this$find) {
        Intrinsics.checkNotNullParameter((Object)$this$find, (String)"<this>");
        boolean $i$f$find = false;
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        return $this$find.find(RawEntity.class);
    }

    public static final /* synthetic */ <T extends RawEntity<Long>> T[] find(ActiveObjects $this$find, Query query) {
        Intrinsics.checkNotNullParameter((Object)$this$find, (String)"<this>");
        Intrinsics.checkNotNullParameter((Object)query, (String)"query");
        boolean $i$f$find = false;
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        return $this$find.find(RawEntity.class, query);
    }

    /*
     * WARNING - void declaration
     */
    @NotNull
    public static final String toDBParamFieldName(@NotNull String $this$toDBParamFieldName) {
        void $this$mapTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)$this$toDBParamFieldName, (String)"<this>");
        CharSequence $this$map$iv = $this$toDBParamFieldName;
        boolean $i$f$map = false;
        CharSequence charSequence = $this$map$iv;
        Collection destination$iv$iv = new ArrayList($this$map$iv.length());
        boolean $i$f$mapTo = false;
        for (int i = 0; i < $this$mapTo$iv$iv.length(); ++i) {
            void it;
            char item$iv$iv;
            char c = item$iv$iv = $this$mapTo$iv$iv.charAt(i);
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(Character.isUpperCase((char)it) ? "" + '_' + (char)it : Character.valueOf((char)it));
        }
        String underscored = CollectionsKt.joinToString$default((Iterable)((List)destination$iv$iv), (CharSequence)"", null, null, (int)0, null, null, (int)62, null);
        String string = underscored.toUpperCase();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"this as java.lang.String).toUpperCase()");
        return string;
    }
}

