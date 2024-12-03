/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.PublishedApi
 *  kotlin.TypeCastException
 *  kotlin.Unit
 *  kotlin.collections.ArraysKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Reflection
 *  kotlin.reflect.KDeclarationContainer
 *  org.jetbrains.annotations.NotNull
 */
package org.springframework.context.support;

import java.util.ArrayList;
import java.util.function.Supplier;
import kotlin.Metadata;
import kotlin.PublishedApi;
import kotlin.TypeCastException;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KDeclarationContainer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.BeanDefinitionDsl;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

@Metadata(mv={1, 1, 10}, bv={1, 0, 2}, k=1, d1={"\u0000Z\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\n\b\u0016\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0003)*+B\u001b\u0012\u0014\b\u0002\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004\u00a2\u0006\u0002\u0010\u0007J`\u0010\u0013\u001a\u00020\u0010\"\n\b\u0000\u0010\u0014\u0018\u0001*\u00020\u00152\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u00172\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u00192\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\u00062\b\b\u0002\u0010\u001c\u001a\u00020\u001d2\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u0006H\u0086\b\u00a2\u0006\u0002\u0010\u001fJ\u007f\u0010\u0013\u001a\u00020\u0010\"\n\b\u0000\u0010\u0014\u0018\u0001*\u00020\u00152\n\b\u0002\u0010\u0016\u001a\u0004\u0018\u00010\u00172\n\b\u0002\u0010\u0018\u001a\u0004\u0018\u00010\u00192\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\u00062\b\b\u0002\u0010\u001c\u001a\u00020\u001d2\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u00062\u001d\b\u0004\u0010 \u001a\u0017\u0012\b\u0012\u00060!R\u00020\u0000\u0012\u0004\u0012\u0002H\u00140\u0004\u00a2\u0006\u0002\b\"H\u0086\b\u00a2\u0006\u0002\u0010#J8\u0010$\u001a\u00020\u00002\u0017\u0010\u0003\u001a\u0013\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004\u00a2\u0006\u0002\b\"2\u0017\u0010%\u001a\u0013\u0012\u0004\u0012\u00020\u0000\u0012\u0004\u0012\u00020\u00100\u0004\u00a2\u0006\u0002\b\"J\u0010\u0010&\u001a\u00020\u00102\u0006\u0010'\u001a\u00020\u0002H\u0016J'\u0010(\u001a\u00020\u00002\u0006\u0010(\u001a\u00020\u00172\u0017\u0010%\u001a\u0013\u0012\u0004\u0012\u00020\u0000\u0012\u0004\u0012\u00020\u00100\u0004\u00a2\u0006\u0002\b\"R,\u0010\b\u001a\u0012\u0012\u0004\u0012\u00020\u00000\tj\b\u0012\u0004\u0012\u00020\u0000`\n8\u0000X\u0081\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u000b\u0010\f\u001a\u0004\b\r\u0010\u000eR\u001a\u0010\u0003\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00060\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000RD\u0010\u000f\u001a*\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00100\u00040\tj\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00100\u0004`\n8\u0000X\u0081\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u0011\u0010\f\u001a\u0004\b\u0012\u0010\u000e\u00a8\u0006,"}, d2={"Lorg/springframework/context/support/BeanDefinitionDsl;", "Lorg/springframework/context/ApplicationContextInitializer;", "Lorg/springframework/context/support/GenericApplicationContext;", "condition", "Lkotlin/Function1;", "Lorg/springframework/core/env/ConfigurableEnvironment;", "", "(Lkotlin/jvm/functions/Function1;)V", "children", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "children$annotations", "()V", "getChildren", "()Ljava/util/ArrayList;", "registrations", "", "registrations$annotations", "getRegistrations", "bean", "T", "", "name", "", "scope", "Lorg/springframework/context/support/BeanDefinitionDsl$Scope;", "isLazyInit", "isPrimary", "autowireMode", "Lorg/springframework/context/support/BeanDefinitionDsl$Autowire;", "isAutowireCandidate", "(Ljava/lang/String;Lorg/springframework/context/support/BeanDefinitionDsl$Scope;Ljava/lang/Boolean;Ljava/lang/Boolean;Lorg/springframework/context/support/BeanDefinitionDsl$Autowire;Ljava/lang/Boolean;)V", "function", "Lorg/springframework/context/support/BeanDefinitionDsl$BeanDefinitionContext;", "Lkotlin/ExtensionFunctionType;", "(Ljava/lang/String;Lorg/springframework/context/support/BeanDefinitionDsl$Scope;Ljava/lang/Boolean;Ljava/lang/Boolean;Lorg/springframework/context/support/BeanDefinitionDsl$Autowire;Ljava/lang/Boolean;Lkotlin/jvm/functions/Function1;)V", "environment", "init", "initialize", "context", "profile", "Autowire", "BeanDefinitionContext", "Scope", "spring-context"})
public class BeanDefinitionDsl
implements ApplicationContextInitializer<GenericApplicationContext> {
    @NotNull
    private final ArrayList<Function1<GenericApplicationContext, Unit>> registrations;
    @NotNull
    private final ArrayList<BeanDefinitionDsl> children;
    private final Function1<ConfigurableEnvironment, Boolean> condition;

    @PublishedApi
    public static /* synthetic */ void registrations$annotations() {
    }

    @NotNull
    public final ArrayList<Function1<GenericApplicationContext, Unit>> getRegistrations() {
        return this.registrations;
    }

    @PublishedApi
    public static /* synthetic */ void children$annotations() {
    }

    @NotNull
    public final ArrayList<BeanDefinitionDsl> getChildren() {
        return this.children;
    }

    private final <T> void bean(String name, Scope scope, Boolean isLazyInit, Boolean isPrimary, Autowire autowireMode, Boolean isAutowireCandidate) {
        ArrayList<Function1<GenericApplicationContext, Unit>> arrayList = this.getRegistrations();
        Intrinsics.needClassReification();
        arrayList.add(new Function1<GenericApplicationContext, Unit>(scope, isLazyInit, isPrimary, isAutowireCandidate, autowireMode, name){
            final /* synthetic */ Scope $scope;
            final /* synthetic */ Boolean $isLazyInit;
            final /* synthetic */ Boolean $isPrimary;
            final /* synthetic */ Boolean $isAutowireCandidate;
            final /* synthetic */ Autowire $autowireMode;
            final /* synthetic */ String $name;

            public final void invoke(@NotNull GenericApplicationContext it) {
                Intrinsics.checkParameterIsNotNull((Object)it, (String)"it");
                BeanDefinitionCustomizer customizer2 = new BeanDefinitionCustomizer(this){
                    final /* synthetic */ bean.1 this$0;

                    public final void customize(@NotNull BeanDefinition bd) {
                        Object object;
                        Intrinsics.checkParameterIsNotNull((Object)bd, (String)"bd");
                        Scope scope = this.this$0.$scope;
                        if (scope != null) {
                            object = scope;
                            Scope it = object;
                            String string = this.this$0.$scope.name();
                            BeanDefinition beanDefinition = bd;
                            String string2 = string;
                            if (string2 == null) {
                                throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
                            }
                            String string3 = string2.toLowerCase();
                            Intrinsics.checkExpressionValueIsNotNull((Object)string3, (String)"(this as java.lang.String).toLowerCase()");
                            String string4 = string3;
                            beanDefinition.setScope(string4);
                        }
                        Boolean bl = this.this$0.$isLazyInit;
                        if (bl != null) {
                            object = bl;
                            boolean it = (Boolean)object;
                            bd.setLazyInit(this.this$0.$isLazyInit);
                        }
                        Boolean bl2 = this.this$0.$isPrimary;
                        if (bl2 != null) {
                            object = bl2;
                            boolean it = (Boolean)object;
                            bd.setPrimary(this.this$0.$isPrimary);
                        }
                        Boolean bl3 = this.this$0.$isAutowireCandidate;
                        if (bl3 != null) {
                            object = bl3;
                            boolean it = (Boolean)object;
                            bd.setAutowireCandidate(this.this$0.$isAutowireCandidate);
                        }
                        if (bd instanceof AbstractBeanDefinition) {
                            ((AbstractBeanDefinition)bd).setAutowireMode(this.this$0.$autowireMode.ordinal());
                        }
                    }
                    {
                        this.this$0 = var1_1;
                    }
                };
                String string = this.$name;
                if (string == null) {
                    Intrinsics.reifiedOperationMarker((int)4, (String)"T");
                    it.registerBean(Object.class, customizer2);
                } else {
                    Intrinsics.reifiedOperationMarker((int)4, (String)"T");
                    it.registerBean(this.$name, Object.class, customizer2);
                }
            }
            {
                this.$scope = scope;
                this.$isLazyInit = bl;
                this.$isPrimary = bl2;
                this.$isAutowireCandidate = bl3;
                this.$autowireMode = autowire;
                this.$name = string;
                super(1);
            }
        });
    }

    static /* bridge */ /* synthetic */ void bean$default(BeanDefinitionDsl this_, String name, Scope scope, Boolean isLazyInit, Boolean isPrimary, Autowire autowireMode, Boolean isAutowireCandidate, int n, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: bean");
        }
        if ((n & 1) != 0) {
            name = null;
        }
        if ((n & 2) != 0) {
            scope = null;
        }
        if ((n & 4) != 0) {
            isLazyInit = null;
        }
        if ((n & 8) != 0) {
            isPrimary = null;
        }
        if ((n & 0x10) != 0) {
            autowireMode = Autowire.CONSTRUCTOR;
        }
        if ((n & 0x20) != 0) {
            isAutowireCandidate = null;
        }
        ArrayList<Function1<GenericApplicationContext, Unit>> arrayList = this_.getRegistrations();
        Intrinsics.needClassReification();
        arrayList.add(new /* invalid duplicate definition of identical inner class */);
    }

    private final <T> void bean(String name, Scope scope, Boolean isLazyInit, Boolean isPrimary, Autowire autowireMode, Boolean isAutowireCandidate, Function1<? super BeanDefinitionContext, ? extends T> function) {
        BeanDefinitionCustomizer customizer2 = new BeanDefinitionCustomizer(scope, isLazyInit, isPrimary, isAutowireCandidate, autowireMode){
            final /* synthetic */ Scope $scope;
            final /* synthetic */ Boolean $isLazyInit;
            final /* synthetic */ Boolean $isPrimary;
            final /* synthetic */ Boolean $isAutowireCandidate;
            final /* synthetic */ Autowire $autowireMode;

            public final void customize(@NotNull BeanDefinition bd) {
                Object object;
                Intrinsics.checkParameterIsNotNull((Object)bd, (String)"bd");
                Scope scope = this.$scope;
                if (scope != null) {
                    object = scope;
                    Scope it = object;
                    String string = this.$scope.name();
                    BeanDefinition beanDefinition = bd;
                    String string2 = string;
                    if (string2 == null) {
                        throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
                    }
                    String string3 = string2.toLowerCase();
                    Intrinsics.checkExpressionValueIsNotNull((Object)string3, (String)"(this as java.lang.String).toLowerCase()");
                    String string4 = string3;
                    beanDefinition.setScope(string4);
                }
                Boolean bl = this.$isLazyInit;
                if (bl != null) {
                    object = bl;
                    boolean it = (Boolean)object;
                    bd.setLazyInit(this.$isLazyInit);
                }
                Boolean bl2 = this.$isPrimary;
                if (bl2 != null) {
                    object = bl2;
                    boolean it = (Boolean)object;
                    bd.setPrimary(this.$isPrimary);
                }
                Boolean bl3 = this.$isAutowireCandidate;
                if (bl3 != null) {
                    object = bl3;
                    boolean it = (Boolean)object;
                    bd.setAutowireCandidate(this.$isAutowireCandidate);
                }
                if (bd instanceof AbstractBeanDefinition) {
                    ((AbstractBeanDefinition)bd).setAutowireMode(this.$autowireMode.ordinal());
                }
            }
            {
                this.$scope = scope;
                this.$isLazyInit = bl;
                this.$isPrimary = bl2;
                this.$isAutowireCandidate = bl3;
                this.$autowireMode = autowire;
            }
        };
        ArrayList<Function1<GenericApplicationContext, Unit>> arrayList = this.getRegistrations();
        Intrinsics.needClassReification();
        arrayList.add(new Function1<GenericApplicationContext, Unit>(this, name, function, customizer2){
            final /* synthetic */ BeanDefinitionDsl this$0;
            final /* synthetic */ String $name;
            final /* synthetic */ Function1 $function;
            final /* synthetic */ BeanDefinitionCustomizer $customizer;

            public final void invoke(@NotNull GenericApplicationContext it) {
                Intrinsics.checkParameterIsNotNull((Object)it, (String)"it");
                BeanDefinitionContext beanContext = this.this$0.new BeanDefinitionContext(it);
                String string = this.$name;
                if (string == null) {
                    Intrinsics.reifiedOperationMarker((int)4, (String)"T");
                    it.registerBean(Object.class, new Supplier<T>(this, beanContext){
                        final /* synthetic */ bean.2 this$0;
                        final /* synthetic */ BeanDefinitionContext $beanContext;

                        @NotNull
                        public final T get() {
                            return (T)this.this$0.$function.invoke((Object)this.$beanContext);
                        }
                        {
                            this.this$0 = var1_1;
                            this.$beanContext = beanDefinitionContext;
                        }
                    }, this.$customizer);
                } else {
                    Intrinsics.reifiedOperationMarker((int)4, (String)"T");
                    it.registerBean(this.$name, Object.class, new Supplier<T>(this, beanContext){
                        final /* synthetic */ bean.2 this$0;
                        final /* synthetic */ BeanDefinitionContext $beanContext;

                        @NotNull
                        public final T get() {
                            return (T)this.this$0.$function.invoke((Object)this.$beanContext);
                        }
                        {
                            this.this$0 = var1_1;
                            this.$beanContext = beanDefinitionContext;
                        }
                    }, this.$customizer);
                }
            }
            {
                this.this$0 = beanDefinitionDsl;
                this.$name = string;
                this.$function = function1;
                this.$customizer = beanDefinitionCustomizer;
                super(1);
            }
        });
    }

    static /* bridge */ /* synthetic */ void bean$default(BeanDefinitionDsl this_, String name, Scope scope, Boolean isLazyInit, Boolean isPrimary, Autowire autowireMode, Boolean isAutowireCandidate, Function1 function, int n, Object object) {
        if (object != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: bean");
        }
        if ((n & 1) != 0) {
            name = null;
        }
        if ((n & 2) != 0) {
            scope = null;
        }
        if ((n & 4) != 0) {
            isLazyInit = null;
        }
        if ((n & 8) != 0) {
            isPrimary = null;
        }
        if ((n & 0x10) != 0) {
            autowireMode = Autowire.NO;
        }
        if ((n & 0x20) != 0) {
            isAutowireCandidate = null;
        }
        BeanDefinitionCustomizer customizer2 = new /* invalid duplicate definition of identical inner class */;
        ArrayList<Function1<GenericApplicationContext, Unit>> arrayList = this_.getRegistrations();
        Intrinsics.needClassReification();
        arrayList.add(new /* invalid duplicate definition of identical inner class */);
    }

    @NotNull
    public final BeanDefinitionDsl profile(@NotNull String profile2, @NotNull Function1<? super BeanDefinitionDsl, Unit> init) {
        Intrinsics.checkParameterIsNotNull((Object)profile2, (String)"profile");
        Intrinsics.checkParameterIsNotNull(init, (String)"init");
        BeanDefinitionDsl beans2 = new BeanDefinitionDsl((Function1<? super ConfigurableEnvironment, Boolean>)((Function1)new Function1<ConfigurableEnvironment, Boolean>(profile2){
            final /* synthetic */ String $profile;

            public final boolean invoke(@NotNull ConfigurableEnvironment it) {
                Intrinsics.checkParameterIsNotNull((Object)it, (String)"it");
                return ArraysKt.contains((Object[])it.getActiveProfiles(), (Object)this.$profile);
            }
            {
                this.$profile = string;
                super(1);
            }
        }));
        init.invoke((Object)beans2);
        this.children.add(beans2);
        return beans2;
    }

    @NotNull
    public final BeanDefinitionDsl environment(@NotNull Function1<? super ConfigurableEnvironment, Boolean> condition, @NotNull Function1<? super BeanDefinitionDsl, Unit> init) {
        Intrinsics.checkParameterIsNotNull(condition, (String)"condition");
        Intrinsics.checkParameterIsNotNull(init, (String)"init");
        BeanDefinitionDsl beans2 = new BeanDefinitionDsl((Function1<? super ConfigurableEnvironment, Boolean>)((Function1)new Function1<ConfigurableEnvironment, Boolean>(condition){

            public final boolean invoke(@NotNull ConfigurableEnvironment p1) {
                Intrinsics.checkParameterIsNotNull((Object)p1, (String)"p1");
                return (Boolean)((Function1)this.receiver).invoke((Object)p1);
            }

            public final KDeclarationContainer getOwner() {
                return Reflection.getOrCreateKotlinClass(Function1.class);
            }

            public final String getName() {
                return "invoke";
            }

            public final String getSignature() {
                return "invoke(Ljava/lang/Object;)Ljava/lang/Object;";
            }
        }));
        init.invoke((Object)beans2);
        this.children.add(beans2);
        return beans2;
    }

    @Override
    public void initialize(@NotNull GenericApplicationContext context) {
        Intrinsics.checkParameterIsNotNull((Object)context, (String)"context");
        for (Function1<GenericApplicationContext, Unit> registration : this.registrations) {
            ConfigurableEnvironment configurableEnvironment = context.getEnvironment();
            Intrinsics.checkExpressionValueIsNotNull((Object)configurableEnvironment, (String)"context.environment");
            if (!((Boolean)this.condition.invoke((Object)configurableEnvironment)).booleanValue()) continue;
            registration.invoke((Object)context);
        }
        for (BeanDefinitionDsl child : this.children) {
            child.initialize(context);
        }
    }

    public BeanDefinitionDsl(@NotNull Function1<? super ConfigurableEnvironment, Boolean> condition) {
        Intrinsics.checkParameterIsNotNull(condition, (String)"condition");
        this.condition = condition;
        BeanDefinitionDsl beanDefinitionDsl = this;
        ArrayList arrayList = new ArrayList();
        beanDefinitionDsl.registrations = arrayList;
        beanDefinitionDsl = this;
        arrayList = new ArrayList();
        beanDefinitionDsl.children = arrayList;
    }

    public /* synthetic */ BeanDefinitionDsl(Function1 function1, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 1) != 0) {
            function1 = 1.INSTANCE;
        }
        this((Function1<? super ConfigurableEnvironment, Boolean>)function1);
    }

    public BeanDefinitionDsl() {
        this(null, 1, null);
    }

    @Metadata(mv={1, 1, 10}, bv={1, 0, 2}, k=1, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0004\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004\u00a8\u0006\u0005"}, d2={"Lorg/springframework/context/support/BeanDefinitionDsl$Scope;", "", "(Ljava/lang/String;I)V", "SINGLETON", "PROTOTYPE", "spring-context"})
    public static final class Scope
    extends Enum<Scope> {
        public static final /* enum */ Scope SINGLETON;
        public static final /* enum */ Scope PROTOTYPE;
        private static final /* synthetic */ Scope[] $VALUES;

        static {
            Scope[] scopeArray = new Scope[2];
            Scope[] scopeArray2 = scopeArray;
            scopeArray[0] = SINGLETON = new Scope();
            scopeArray[1] = PROTOTYPE = new Scope();
            $VALUES = scopeArray;
        }

        public static Scope[] values() {
            return (Scope[])$VALUES.clone();
        }

        public static Scope valueOf(String string) {
            return Enum.valueOf(Scope.class, string);
        }
    }

    @Metadata(mv={1, 1, 10}, bv={1, 0, 2}, k=1, d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0001\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002j\u0002\b\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2={"Lorg/springframework/context/support/BeanDefinitionDsl$Autowire;", "", "(Ljava/lang/String;I)V", "NO", "BY_NAME", "BY_TYPE", "CONSTRUCTOR", "spring-context"})
    public static final class Autowire
    extends Enum<Autowire> {
        public static final /* enum */ Autowire NO;
        public static final /* enum */ Autowire BY_NAME;
        public static final /* enum */ Autowire BY_TYPE;
        public static final /* enum */ Autowire CONSTRUCTOR;
        private static final /* synthetic */ Autowire[] $VALUES;

        static {
            Autowire[] autowireArray = new Autowire[4];
            Autowire[] autowireArray2 = autowireArray;
            autowireArray[0] = NO = new Autowire();
            autowireArray[1] = BY_NAME = new Autowire();
            autowireArray[2] = BY_TYPE = new Autowire();
            autowireArray[3] = CONSTRUCTOR = new Autowire();
            $VALUES = autowireArray;
        }

        public static Autowire[] values() {
            return (Autowire[])$VALUES.clone();
        }

        public static Autowire valueOf(String string) {
            return Enum.valueOf(Autowire.class, string);
        }
    }

    @Metadata(mv={1, 1, 10}, bv={1, 0, 2}, k=1, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0004\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J&\u0010\r\u001a\u0002H\u000e\"\n\b\u0000\u0010\u000e\u0018\u0001*\u00020\u00012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0086\b\u00a2\u0006\u0002\u0010\u0011R\u001c\u0010\u0002\u001a\u00020\u00038\u0000X\u0081\u0004\u00a2\u0006\u000e\n\u0000\u0012\u0004\b\u0005\u0010\u0006\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\n8F\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\f\u00a8\u0006\u0012"}, d2={"Lorg/springframework/context/support/BeanDefinitionDsl$BeanDefinitionContext;", "", "context", "Lorg/springframework/context/support/GenericApplicationContext;", "(Lorg/springframework/context/support/BeanDefinitionDsl;Lorg/springframework/context/support/GenericApplicationContext;)V", "context$annotations", "()V", "getContext", "()Lorg/springframework/context/support/GenericApplicationContext;", "env", "Lorg/springframework/core/env/ConfigurableEnvironment;", "getEnv", "()Lorg/springframework/core/env/ConfigurableEnvironment;", "ref", "T", "name", "", "(Ljava/lang/String;)Ljava/lang/Object;", "spring-context"})
    public final class BeanDefinitionContext {
        @NotNull
        private final GenericApplicationContext context;

        private final <T> T ref(String name) {
            Object object;
            String string = name;
            if (string == null) {
                GenericApplicationContext genericApplicationContext = this.getContext();
                Intrinsics.reifiedOperationMarker((int)4, (String)"T");
                Object object2 = genericApplicationContext.getBean(Object.class);
                object = object2;
                Intrinsics.checkExpressionValueIsNotNull((Object)object2, (String)"context.getBean(T::class.java)");
            } else {
                GenericApplicationContext genericApplicationContext = this.getContext();
                Intrinsics.reifiedOperationMarker((int)4, (String)"T");
                Object object3 = genericApplicationContext.getBean(name, Object.class);
                object = object3;
                Intrinsics.checkExpressionValueIsNotNull((Object)object3, (String)"context.getBean(name, T::class.java)");
            }
            return (T)object;
        }

        static /* bridge */ /* synthetic */ Object ref$default(BeanDefinitionContext this_, String name, int n, Object object) {
            Object object2;
            if ((n & 1) != 0) {
                name = null;
            }
            if ((object = name) == null) {
                GenericApplicationContext genericApplicationContext = this_.getContext();
                Intrinsics.reifiedOperationMarker((int)4, (String)"T");
                Object object3 = genericApplicationContext.getBean(Object.class);
                object2 = object3;
                Intrinsics.checkExpressionValueIsNotNull((Object)object3, (String)"context.getBean(T::class.java)");
            } else {
                GenericApplicationContext genericApplicationContext = this_.getContext();
                Intrinsics.reifiedOperationMarker((int)4, (String)"T");
                Object object4 = genericApplicationContext.getBean(name, Object.class);
                object2 = object4;
                Intrinsics.checkExpressionValueIsNotNull((Object)object4, (String)"context.getBean(name, T::class.java)");
            }
            return object2;
        }

        @NotNull
        public final ConfigurableEnvironment getEnv() {
            ConfigurableEnvironment configurableEnvironment = this.context.getEnvironment();
            Intrinsics.checkExpressionValueIsNotNull((Object)configurableEnvironment, (String)"context.environment");
            return configurableEnvironment;
        }

        @PublishedApi
        public static /* synthetic */ void context$annotations() {
        }

        @NotNull
        public final GenericApplicationContext getContext() {
            return this.context;
        }

        public BeanDefinitionContext(GenericApplicationContext context) {
            Intrinsics.checkParameterIsNotNull((Object)context, (String)"context");
            this.context = context;
        }
    }
}

