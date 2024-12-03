/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 */
package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.springframework.beans.factory.ListableBeanFactory;

@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=2, d1={"\u00004\n\u0002\b\u0002\n\u0002\u0010\u001b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010$\n\u0002\b\u0002\u001a(\u0010\u0000\u001a\u0004\u0018\u0001H\u0001\"\n\b\u0000\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0086\b\u00a2\u0006\u0002\u0010\u0006\u001a&\u0010\u0007\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00050\b\"\n\b\u0000\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u0003H\u0086\b\u00a2\u0006\u0002\u0010\t\u001a:\u0010\n\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00050\b\"\n\b\u0000\u0010\u0001\u0018\u0001*\u00020\u000b*\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\rH\u0086\b\u00a2\u0006\u0002\u0010\u000f\u001a9\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u0002H\u00010\u0011\"\n\b\u0000\u0010\u0001\u0018\u0001*\u00020\u000b*\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\r2\b\b\u0002\u0010\u000e\u001a\u00020\rH\u0086\b\u001a%\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000b0\u0011\"\n\b\u0000\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u0003H\u0086\b\u00a8\u0006\u0013"}, d2={"findAnnotationOnBean", "T", "", "Lorg/springframework/beans/factory/ListableBeanFactory;", "beanName", "", "(Lorg/springframework/beans/factory/ListableBeanFactory;Ljava/lang/String;)Ljava/lang/annotation/Annotation;", "getBeanNamesForAnnotation", "", "(Lorg/springframework/beans/factory/ListableBeanFactory;)[Ljava/lang/String;", "getBeanNamesForType", "", "includeNonSingletons", "", "allowEagerInit", "(Lorg/springframework/beans/factory/ListableBeanFactory;ZZ)[Ljava/lang/String;", "getBeansOfType", "", "getBeansWithAnnotation", "spring-beans"})
public final class ListableBeanFactoryExtensionsKt {
    public static final /* synthetic */ <T> String[] getBeanNamesForType(ListableBeanFactory $this$getBeanNamesForType, boolean includeNonSingletons, boolean allowEagerInit) {
        int $i$f$getBeanNamesForType = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$getBeanNamesForType, (String)"$this$getBeanNamesForType");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        String[] stringArray = $this$getBeanNamesForType.getBeanNamesForType(Object.class, includeNonSingletons, allowEagerInit);
        Intrinsics.checkExpressionValueIsNotNull((Object)stringArray, (String)"getBeanNamesForType(T::c\u2026ngletons, allowEagerInit)");
        return stringArray;
    }

    public static /* synthetic */ String[] getBeanNamesForType$default(ListableBeanFactory $this$getBeanNamesForType, boolean includeNonSingletons, boolean allowEagerInit, int n, Object object) {
        if ((n & 1) != 0) {
            includeNonSingletons = true;
        }
        if ((n & 2) != 0) {
            allowEagerInit = true;
        }
        boolean $i$f$getBeanNamesForType = false;
        Intrinsics.checkParameterIsNotNull((Object)$this$getBeanNamesForType, (String)"$this$getBeanNamesForType");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        String[] stringArray = $this$getBeanNamesForType.getBeanNamesForType(Object.class, includeNonSingletons, allowEagerInit);
        Intrinsics.checkExpressionValueIsNotNull((Object)stringArray, (String)"getBeanNamesForType(T::c\u2026ngletons, allowEagerInit)");
        return stringArray;
    }

    public static final /* synthetic */ <T> Map<String, T> getBeansOfType(ListableBeanFactory $this$getBeansOfType, boolean includeNonSingletons, boolean allowEagerInit) {
        int $i$f$getBeansOfType = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$getBeansOfType, (String)"$this$getBeansOfType");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Map<String, Object> map = $this$getBeansOfType.getBeansOfType(Object.class, includeNonSingletons, allowEagerInit);
        Intrinsics.checkExpressionValueIsNotNull(map, (String)"getBeansOfType(T::class.\u2026ngletons, allowEagerInit)");
        return map;
    }

    public static /* synthetic */ Map getBeansOfType$default(ListableBeanFactory $this$getBeansOfType, boolean includeNonSingletons, boolean allowEagerInit, int n, Object object) {
        if ((n & 1) != 0) {
            includeNonSingletons = true;
        }
        if ((n & 2) != 0) {
            allowEagerInit = true;
        }
        boolean $i$f$getBeansOfType = false;
        Intrinsics.checkParameterIsNotNull((Object)$this$getBeansOfType, (String)"$this$getBeansOfType");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Map<String, Object> map = $this$getBeansOfType.getBeansOfType(Object.class, includeNonSingletons, allowEagerInit);
        Intrinsics.checkExpressionValueIsNotNull(map, (String)"getBeansOfType(T::class.\u2026ngletons, allowEagerInit)");
        return map;
    }

    public static final /* synthetic */ <T extends Annotation> String[] getBeanNamesForAnnotation(ListableBeanFactory $this$getBeanNamesForAnnotation) {
        int $i$f$getBeanNamesForAnnotation = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$getBeanNamesForAnnotation, (String)"$this$getBeanNamesForAnnotation");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        String[] stringArray = $this$getBeanNamesForAnnotation.getBeanNamesForAnnotation(Annotation.class);
        Intrinsics.checkExpressionValueIsNotNull((Object)stringArray, (String)"getBeanNamesForAnnotation(T::class.java)");
        return stringArray;
    }

    public static final /* synthetic */ <T extends Annotation> Map<String, Object> getBeansWithAnnotation(ListableBeanFactory $this$getBeansWithAnnotation) {
        int $i$f$getBeansWithAnnotation = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$getBeansWithAnnotation, (String)"$this$getBeansWithAnnotation");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Map<String, Object> map = $this$getBeansWithAnnotation.getBeansWithAnnotation(Annotation.class);
        Intrinsics.checkExpressionValueIsNotNull(map, (String)"getBeansWithAnnotation(T::class.java)");
        return map;
    }

    public static final /* synthetic */ <T extends Annotation> T findAnnotationOnBean(ListableBeanFactory $this$findAnnotationOnBean, String beanName) {
        int $i$f$findAnnotationOnBean = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$findAnnotationOnBean, (String)"$this$findAnnotationOnBean");
        Intrinsics.checkParameterIsNotNull((Object)beanName, (String)"beanName");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        return (T)$this$findAnnotationOnBean.findAnnotationOnBean(beanName, Annotation.class);
    }
}

