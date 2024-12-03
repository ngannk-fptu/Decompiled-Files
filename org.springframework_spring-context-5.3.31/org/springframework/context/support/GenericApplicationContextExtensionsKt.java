/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Deprecated
 *  kotlin.Metadata
 *  kotlin.ReplaceWith
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.springframework.beans.factory.config.BeanDefinitionCustomizer
 */
package org.springframework.context.support;

import java.util.Arrays;
import java.util.function.Supplier;
import kotlin.Deprecated;
import kotlin.Metadata;
import kotlin.ReplaceWith;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

@Metadata(mv={1, 1, 18}, bv={1, 0, 3}, k=2, d1={"\u0000:\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0004\u001a!\u0010\u0000\u001a\u00020\u00012\u0017\u0010\u0002\u001a\u0013\u0012\u0004\u0012\u00020\u0001\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\u0002\b\u0005H\u0007\u001a2\u0010\u0006\u001a\u00020\u0004\"\n\b\u0000\u0010\u0007\u0018\u0001*\u00020\b*\u00020\u00012\u0012\u0010\t\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u000b0\n\"\u00020\u000bH\u0086\b\u00a2\u0006\u0002\u0010\f\u001aH\u0010\u0006\u001a\u00020\u0004\"\n\b\u0000\u0010\u0007\u0018\u0001*\u00020\b*\u00020\u00012\u0012\u0010\t\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u000b0\n\"\u00020\u000b2\u0014\b\u0004\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u0002H\u00070\u0003H\u0086\b\u00a2\u0006\u0002\u0010\u000f\u001a:\u0010\u0006\u001a\u00020\u0004\"\n\b\u0000\u0010\u0007\u0018\u0001*\u00020\b*\u00020\u00012\u0006\u0010\u0010\u001a\u00020\u00112\u0012\u0010\t\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u000b0\n\"\u00020\u000bH\u0086\b\u00a2\u0006\u0002\u0010\u0012\u001aP\u0010\u0006\u001a\u00020\u0004\"\n\b\u0000\u0010\u0007\u0018\u0001*\u00020\b*\u00020\u00012\u0006\u0010\u0013\u001a\u00020\u00112\u0012\u0010\t\u001a\n\u0012\u0006\b\u0001\u0012\u00020\u000b0\n\"\u00020\u000b2\u0014\b\u0004\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u0002H\u00070\u0003H\u0086\b\u00a2\u0006\u0002\u0010\u0014\u00a8\u0006\u0015"}, d2={"GenericApplicationContext", "Lorg/springframework/context/support/GenericApplicationContext;", "configure", "Lkotlin/Function1;", "", "Lkotlin/ExtensionFunctionType;", "registerBean", "T", "", "customizers", "", "Lorg/springframework/beans/factory/config/BeanDefinitionCustomizer;", "(Lorg/springframework/context/support/GenericApplicationContext;[Lorg/springframework/beans/factory/config/BeanDefinitionCustomizer;)V", "function", "Lorg/springframework/context/ApplicationContext;", "(Lorg/springframework/context/support/GenericApplicationContext;[Lorg/springframework/beans/factory/config/BeanDefinitionCustomizer;Lkotlin/jvm/functions/Function1;)V", "beanName", "", "(Lorg/springframework/context/support/GenericApplicationContext;Ljava/lang/String;[Lorg/springframework/beans/factory/config/BeanDefinitionCustomizer;)V", "name", "(Lorg/springframework/context/support/GenericApplicationContext;Ljava/lang/String;[Lorg/springframework/beans/factory/config/BeanDefinitionCustomizer;Lkotlin/jvm/functions/Function1;)V", "spring-context"})
public final class GenericApplicationContextExtensionsKt {
    public static final /* synthetic */ <T> void registerBean(GenericApplicationContext $this$registerBean, BeanDefinitionCustomizer ... customizers) {
        int $i$f$registerBean = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$registerBean, (String)"$this$registerBean");
        Intrinsics.checkParameterIsNotNull((Object)customizers, (String)"customizers");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        $this$registerBean.registerBean(Object.class, Arrays.copyOf(customizers, customizers.length));
    }

    public static final /* synthetic */ <T> void registerBean(GenericApplicationContext $this$registerBean, String beanName, BeanDefinitionCustomizer ... customizers) {
        int $i$f$registerBean = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$registerBean, (String)"$this$registerBean");
        Intrinsics.checkParameterIsNotNull((Object)beanName, (String)"beanName");
        Intrinsics.checkParameterIsNotNull((Object)customizers, (String)"customizers");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        $this$registerBean.registerBean(beanName, Object.class, Arrays.copyOf(customizers, customizers.length));
    }

    public static final /* synthetic */ <T> void registerBean(GenericApplicationContext $this$registerBean, BeanDefinitionCustomizer[] customizers, Function1<? super ApplicationContext, ? extends T> function) {
        int $i$f$registerBean = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$registerBean, (String)"$this$registerBean");
        Intrinsics.checkParameterIsNotNull((Object)customizers, (String)"customizers");
        Intrinsics.checkParameterIsNotNull(function, (String)"function");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        $this$registerBean.registerBean(Object.class, new Supplier($this$registerBean, function){
            final /* synthetic */ GenericApplicationContext $this_registerBean;
            final /* synthetic */ Function1 $function;

            @NotNull
            public final T get() {
                return (T)this.$function.invoke((Object)this.$this_registerBean);
            }
            {
                this.$this_registerBean = genericApplicationContext;
                this.$function = function1;
            }
        }, Arrays.copyOf(customizers, customizers.length));
    }

    public static final /* synthetic */ <T> void registerBean(GenericApplicationContext $this$registerBean, String name, BeanDefinitionCustomizer[] customizers, Function1<? super ApplicationContext, ? extends T> function) {
        int $i$f$registerBean = 0;
        Intrinsics.checkParameterIsNotNull((Object)$this$registerBean, (String)"$this$registerBean");
        Intrinsics.checkParameterIsNotNull((Object)name, (String)"name");
        Intrinsics.checkParameterIsNotNull((Object)customizers, (String)"customizers");
        Intrinsics.checkParameterIsNotNull(function, (String)"function");
        Intrinsics.reifiedOperationMarker((int)4, (String)"T");
        $this$registerBean.registerBean(name, Object.class, new Supplier($this$registerBean, function){
            final /* synthetic */ GenericApplicationContext $this_registerBean;
            final /* synthetic */ Function1 $function;

            @NotNull
            public final T get() {
                return (T)this.$function.invoke((Object)this.$this_registerBean);
            }
            {
                this.$this_registerBean = genericApplicationContext;
                this.$function = function1;
            }
        }, Arrays.copyOf(customizers, customizers.length));
    }

    @Deprecated(message="Use regular apply method instead.", replaceWith=@ReplaceWith(imports={}, expression="GenericApplicationContext().apply(configure)"))
    @NotNull
    public static final GenericApplicationContext GenericApplicationContext(@NotNull Function1<? super GenericApplicationContext, Unit> configure) {
        Intrinsics.checkParameterIsNotNull(configure, (String)"configure");
        GenericApplicationContext genericApplicationContext = new GenericApplicationContext();
        boolean bl = false;
        boolean bl2 = false;
        configure.invoke((Object)genericApplicationContext);
        return genericApplicationContext;
    }
}

