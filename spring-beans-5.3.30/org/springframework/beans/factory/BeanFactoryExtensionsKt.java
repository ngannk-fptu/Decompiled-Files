/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.springframework.core.ParameterizedTypeReference
 *  org.springframework.core.ResolvableType
 */
package org.springframework.beans.factory;

import java.lang.reflect.Type;
import java.util.Arrays;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;

@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=2, d1={"\u0000&\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a\u001e\u0010\u0000\u001a\u0002H\u0001\"\n\b\u0000\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u0003H\u0086\b\u00a2\u0006\u0002\u0010\u0004\u001a2\u0010\u0000\u001a\u0002H\u0001\"\n\b\u0000\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u00032\u0012\u0010\u0005\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u00020\u0006\"\u00020\u0002H\u0086\b\u00a2\u0006\u0002\u0010\u0007\u001a&\u0010\u0000\u001a\u0002H\u0001\"\n\b\u0000\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u00032\u0006\u0010\b\u001a\u00020\tH\u0086\b\u00a2\u0006\u0002\u0010\n\u001a\u001f\u0010\u000b\u001a\b\u0012\u0004\u0012\u0002H\u00010\f\"\n\b\u0000\u0010\u0001\u0018\u0001*\u00020\u0002*\u00020\u0003H\u0086\b\u00a8\u0006\r"}, d2={"getBean", "T", "", "Lorg/springframework/beans/factory/BeanFactory;", "(Lorg/springframework/beans/factory/BeanFactory;)Ljava/lang/Object;", "args", "", "(Lorg/springframework/beans/factory/BeanFactory;[Ljava/lang/Object;)Ljava/lang/Object;", "name", "", "(Lorg/springframework/beans/factory/BeanFactory;Ljava/lang/String;)Ljava/lang/Object;", "getBeanProvider", "Lorg/springframework/beans/factory/ObjectProvider;", "spring-beans"})
public final class BeanFactoryExtensionsKt {
    public static final /* synthetic */ <T> T getBean(BeanFactory $this$getBean) {
        int $i$f$getBean = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$getBean, (String)"$this$getBean");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object = $this$getBean.getBean(Object.class);
        Intrinsics.checkExpressionValueIsNotNull((Object)object, (String)"getBean(T::class.java)");
        return (T)object;
    }

    public static final /* synthetic */ <T> T getBean(BeanFactory $this$getBean, String name) {
        int $i$f$getBean = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$getBean, (String)"$this$getBean");
        Intrinsics.checkParameterIsNotNull((Object)name, (String)"name");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object = $this$getBean.getBean(name, Object.class);
        Intrinsics.checkExpressionValueIsNotNull((Object)object, (String)"getBean(name, T::class.java)");
        return (T)object;
    }

    public static final /* synthetic */ <T> T getBean(BeanFactory $this$getBean, Object ... args) {
        int $i$f$getBean = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$getBean, (String)"$this$getBean");
        Intrinsics.checkParameterIsNotNull((Object)args, (String)"args");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        Object object = $this$getBean.getBean(Object.class, Arrays.copyOf(args, args.length));
        Intrinsics.checkExpressionValueIsNotNull((Object)object, (String)"getBean(T::class.java, *args)");
        return (T)object;
    }

    public static final /* synthetic */ <T> ObjectProvider<T> getBeanProvider(BeanFactory $this$getBeanProvider) {
        int $i$f$getBeanProvider = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$getBeanProvider, (String)"$this$getBeanProvider");
        Intrinsics.needClassReification();
        ObjectProvider objectProvider = $this$getBeanProvider.getBeanProvider(ResolvableType.forType((Type)new ParameterizedTypeReference<T>(){}.getType()));
        Intrinsics.checkExpressionValueIsNotNull(objectProvider, (String)"getBeanProvider(Resolvab\u2026Reference<T>() {}).type))");
        return objectProvider;
    }
}

