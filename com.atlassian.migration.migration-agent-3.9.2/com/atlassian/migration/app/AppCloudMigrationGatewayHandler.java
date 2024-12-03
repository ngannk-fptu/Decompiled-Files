/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.TypeIntrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.AppCloudMigrationGateway;
import com.atlassian.migration.app.ContainerType;
import com.atlassian.migration.app.PaginatedContainers;
import com.atlassian.migration.app.PaginatedContainersHandler;
import com.atlassian.migration.app.PaginatedMapping;
import com.atlassian.migration.app.PaginatedMappingHandler;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.TypeIntrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J2\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\u00062\u0006\u0010\b\u001a\u00020\t2\u0010\u0010\n\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u0006\u0018\u00010\u000bH\u0096\u0002\u00a2\u0006\u0002\u0010\fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2={"Lcom/atlassian/migration/app/AppCloudMigrationGatewayHandler;", "Ljava/lang/reflect/InvocationHandler;", "migrationGateway", "Lcom/atlassian/migration/app/AppCloudMigrationGateway;", "(Lcom/atlassian/migration/app/AppCloudMigrationGateway;)V", "invoke", "", "proxy", "method", "Ljava/lang/reflect/Method;", "args", "", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", "app-migration-assistant"})
final class AppCloudMigrationGatewayHandler
implements InvocationHandler {
    @NotNull
    private final AppCloudMigrationGateway migrationGateway;

    public AppCloudMigrationGatewayHandler(@NotNull AppCloudMigrationGateway migrationGateway) {
        Intrinsics.checkNotNullParameter((Object)migrationGateway, (String)"migrationGateway");
        this.migrationGateway = migrationGateway;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    @NotNull
    public Object invoke(@Nullable Object proxy, @NotNull Method method, @Nullable Object[] args) {
        Map<String, String> map;
        Intrinsics.checkNotNullParameter((Object)method, (String)"method");
        String string = method.getName();
        if (string == null) throw new UnsupportedOperationException("Couldn't map method " + method.getName());
        int n = -1;
        switch (string.hashCode()) {
            case 1452083407: {
                if (string.equals("createAppData")) {
                    n = 1;
                }
                break;
            }
            case 707040439: {
                if (string.equals("getPaginatedMapping")) {
                    n = 2;
                }
                break;
            }
            case -1788986342: {
                if (string.equals("getCloudFeedbackIfPresent")) {
                    n = 3;
                }
                break;
            }
            case 1357047076: {
                if (string.equals("getCloudFeedback")) {
                    n = 4;
                }
                break;
            }
            case -1821566838: {
                if (string.equals("getMappingById")) {
                    n = 5;
                }
                break;
            }
            case -1111239799: {
                if (string.equals("getPaginatedContainers")) {
                    n = 6;
                }
                break;
            }
            case -1776922004: {
                if (string.equals("toString")) {
                    n = 7;
                }
                break;
            }
        }
        block9 : switch (n) {
            case 1: {
                switch (method.getParameterCount()) {
                    case 1: {
                        Intrinsics.checkNotNull((Object)args);
                        map = this.migrationGateway.createAppData((String)args[0]);
                        break block9;
                    }
                    case 2: {
                        Intrinsics.checkNotNull((Object)args);
                        map = this.migrationGateway.createAppData((String)args[0], (String)args[1]);
                        break block9;
                    }
                }
                throw new UnsupportedOperationException("Couldn't map method " + method.getName());
            }
            case 5: {
                Intrinsics.checkNotNull((Object)args);
                map = this.migrationGateway.getMappingById((String)args[0], (String)args[1], TypeIntrinsics.asMutableSet((Object)args[2]));
                break;
            }
            case 2: {
                Class<?> returnType = method.getReturnType();
                Intrinsics.checkNotNull((Object)args);
                String string2 = (String)args[0];
                String string3 = (String)args[1];
                Object object = args[2];
                Intrinsics.checkNotNull((Object)object, (String)"null cannot be cast to non-null type kotlin.Int");
                PaginatedMapping paginatedMapping = this.migrationGateway.getPaginatedMapping(string2, string3, (Integer)object);
                ClassLoader classLoader = returnType.getClassLoader();
                Class[] classArray = new Class[1];
                Intrinsics.checkNotNullExpressionValue(returnType, (String)"returnType");
                classArray[0] = returnType;
                Intrinsics.checkNotNullExpressionValue((Object)paginatedMapping, (String)"paginatedMapping");
                Object object2 = Proxy.newProxyInstance(classLoader, classArray, (InvocationHandler)new PaginatedMappingHandler(paginatedMapping));
                Intrinsics.checkNotNullExpressionValue((Object)object2, (String)"newProxyInstance(\n      \u2026andler(paginatedMapping))");
                return object2;
            }
            case 6: {
                Class<?> returnType = method.getReturnType();
                Intrinsics.checkNotNull((Object)args);
                String string4 = (String)args[0];
                Object object = args[1];
                Intrinsics.checkNotNull((Object)object, (String)"null cannot be cast to non-null type kotlin.Enum<*>");
                ContainerType containerType = ContainerType.valueOf(((Enum)object).name());
                Object object3 = args[2];
                Intrinsics.checkNotNull((Object)object3, (String)"null cannot be cast to non-null type kotlin.Int");
                PaginatedContainers paginatedContainers = this.migrationGateway.getPaginatedContainers(string4, containerType, (Integer)object3);
                ClassLoader classLoader = returnType.getClassLoader();
                Class[] classArray = new Class[1];
                Intrinsics.checkNotNullExpressionValue(returnType, (String)"returnType");
                classArray[0] = returnType;
                Intrinsics.checkNotNullExpressionValue((Object)paginatedContainers, (String)"paginatedContainers");
                ClassLoader classLoader2 = returnType.getClassLoader();
                Intrinsics.checkNotNullExpressionValue((Object)classLoader2, (String)"returnType.classLoader");
                Object object4 = Proxy.newProxyInstance(classLoader, classArray, (InvocationHandler)new PaginatedContainersHandler(paginatedContainers, classLoader2));
                Intrinsics.checkNotNullExpressionValue((Object)object4, (String)"newProxyInstance(\n      \u2026 returnType.classLoader))");
                return object4;
            }
            case 4: {
                Intrinsics.checkNotNull((Object)args);
                map = this.migrationGateway.getCloudFeedback((String)args[0]);
                break;
            }
            case 3: {
                Intrinsics.checkNotNull((Object)args);
                map = this.migrationGateway.getCloudFeedbackIfPresent((String)args[0]);
                break;
            }
            case 7: {
                map = this.migrationGateway.toString();
                break;
            }
            default: {
                throw new UnsupportedOperationException("Couldn't map method " + method.getName());
            }
        }
        String string5 = map;
        Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"when (method.name) {\n   \u2026{method.name}\")\n        }");
        return string5;
    }
}

