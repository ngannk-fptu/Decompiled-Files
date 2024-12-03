/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.PaginatedMapping;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\b\u0002\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J2\u0010\u0005\u001a\u00020\u00062\b\u0010\u0007\u001a\u0004\u0018\u00010\u00062\u0006\u0010\b\u001a\u00020\t2\u0010\u0010\n\u001a\f\u0012\u0006\b\u0001\u0012\u00020\u0006\u0018\u00010\u000bH\u0096\u0002\u00a2\u0006\u0002\u0010\fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2={"Lcom/atlassian/migration/app/PaginatedMappingHandler;", "Ljava/lang/reflect/InvocationHandler;", "paginatedMapping", "Lcom/atlassian/migration/app/PaginatedMapping;", "(Lcom/atlassian/migration/app/PaginatedMapping;)V", "invoke", "", "proxy", "method", "Ljava/lang/reflect/Method;", "args", "", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", "app-migration-assistant"})
final class PaginatedMappingHandler
implements InvocationHandler {
    @NotNull
    private final PaginatedMapping paginatedMapping;

    public PaginatedMappingHandler(@NotNull PaginatedMapping paginatedMapping) {
        Intrinsics.checkNotNullParameter((Object)paginatedMapping, (String)"paginatedMapping");
        this.paginatedMapping = paginatedMapping;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    @NotNull
    public Object invoke(@Nullable Object proxy, @NotNull Method method, @Nullable Object[] args) {
        Object object;
        Intrinsics.checkNotNullParameter((Object)method, (String)"method");
        String string = method.getName();
        if (string == null) throw new UnsupportedOperationException("Couldn't map method " + method.getName());
        int n = -1;
        switch (string.hashCode()) {
            case 3377907: {
                if (string.equals("next")) {
                    n = 1;
                }
                break;
            }
            case 1874416792: {
                if (string.equals("getMapping")) {
                    n = 2;
                }
                break;
            }
            case -1776922004: {
                if (string.equals("toString")) {
                    n = 3;
                }
                break;
            }
        }
        switch (n) {
            case 1: {
                object = this.paginatedMapping.next();
                break;
            }
            case 2: {
                object = this.paginatedMapping.getMapping();
                break;
            }
            case 3: {
                object = this.paginatedMapping.toString();
                break;
            }
            default: {
                throw new UnsupportedOperationException("Couldn't map method " + method.getName());
            }
        }
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"when (method.name) {\n   \u2026{method.name}\")\n        }");
        return object;
    }
}

