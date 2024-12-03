/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.collections.ArraysKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.SetsKt
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.ConfluenceSpaceContainerV1;
import com.atlassian.migration.app.ContainerV1;
import com.atlassian.migration.app.JiraProjectContainerV1;
import com.atlassian.migration.app.PaginatedContainers;
import com.atlassian.migration.app.SiteContainerV1;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000P\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u001a\u0010\u0007\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\nH\u0002J\u001a\u0010\u000b\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\t\u001a\u00020\fH\u0002J\u001a\u0010\r\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fH\u0002J2\u0010\u0010\u001a\u00020\b2\b\u0010\u0011\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0012\u001a\u00020\u00132\u0010\u0010\u0014\u001a\f\u0012\u0006\b\u0001\u0012\u00020\b\u0018\u00010\u0015H\u0096\u0002\u00a2\u0006\u0002\u0010\u0016J\u001a\u0010\u0017\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0018\u001a\u00020\u0019H\u0002J&\u0010\u001a\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u001b2\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00190\u001bH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001d"}, d2={"Lcom/atlassian/migration/app/PaginatedContainersHandler;", "Ljava/lang/reflect/InvocationHandler;", "paginatedContainers", "Lcom/atlassian/migration/app/PaginatedContainers;", "classLoader", "Ljava/lang/ClassLoader;", "(Lcom/atlassian/migration/app/PaginatedContainers;Ljava/lang/ClassLoader;)V", "createConfluenceSpaceContainer", "", "sourceContainer", "Lcom/atlassian/migration/app/ConfluenceSpaceContainerV1;", "createJiraContainer", "Lcom/atlassian/migration/app/JiraProjectContainerV1;", "createSiteContainer", "siteContainerV1", "Lcom/atlassian/migration/app/SiteContainerV1;", "invoke", "proxy", "method", "Ljava/lang/reflect/Method;", "args", "", "(Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;)Ljava/lang/Object;", "recreateContainer", "it", "Lcom/atlassian/migration/app/ContainerV1;", "recreateContainers", "", "containers", "app-migration-assistant"})
final class PaginatedContainersHandler
implements InvocationHandler {
    @NotNull
    private final PaginatedContainers paginatedContainers;
    @NotNull
    private final ClassLoader classLoader;

    public PaginatedContainersHandler(@NotNull PaginatedContainers paginatedContainers, @NotNull ClassLoader classLoader) {
        Intrinsics.checkNotNullParameter((Object)paginatedContainers, (String)"paginatedContainers");
        Intrinsics.checkNotNullParameter((Object)classLoader, (String)"classLoader");
        this.paginatedContainers = paginatedContainers;
        this.classLoader = classLoader;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    @NotNull
    public Object invoke(@Nullable Object proxy, @NotNull Method method, @Nullable Object[] args) {
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
            case -152503288: {
                if (string.equals("getContainers")) {
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
                List<Object> list2 = this.paginatedContainers.next();
                return list2;
            }
            case 2: {
                List<ContainerV1> list = this.paginatedContainers.getContainers();
                Intrinsics.checkNotNullExpressionValue(list, (String)"paginatedContainers.containers");
                List<Object> list2 = this.recreateContainers(this.classLoader, list);
                return list2;
            }
            case 3: {
                List<Object> list2 = this.paginatedContainers.toString();
                return list2;
            }
            default: {
                throw new UnsupportedOperationException("Couldn't map method " + method.getName());
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    private final List<Object> recreateContainers(ClassLoader classLoader, List<? extends ContainerV1> containers) {
        void $this$mapTo$iv$iv;
        Iterable $this$map$iv = containers;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            ContainerV1 containerV1 = (ContainerV1)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(this.recreateContainer(classLoader, (ContainerV1)it));
        }
        return (List)destination$iv$iv;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final Object recreateContainer(ClassLoader classLoader, ContainerV1 it) {
        String className = it.getClass().getName();
        if (className == null) throw new IllegalArgumentException("Cant create container for " + className);
        int n = -1;
        switch (className.hashCode()) {
            case 1628034283: {
                if (className.equals("com.atlassian.migration.app.JiraProjectContainerV1")) {
                    n = 1;
                }
                break;
            }
            case 1565661115: {
                if (className.equals("com.atlassian.migration.app.SiteContainerV1")) {
                    n = 2;
                }
                break;
            }
            case -1016601688: {
                if (className.equals("com.atlassian.migration.app.ConfluenceSpaceContainerV1")) {
                    n = 3;
                }
                break;
            }
        }
        switch (n) {
            case 2: {
                Intrinsics.checkNotNull((Object)it, (String)"null cannot be cast to non-null type com.atlassian.migration.app.SiteContainerV1");
                Object object = this.createSiteContainer(classLoader, (SiteContainerV1)it);
                return object;
            }
            case 1: {
                Intrinsics.checkNotNull((Object)it, (String)"null cannot be cast to non-null type com.atlassian.migration.app.JiraProjectContainerV1");
                Object object = this.createJiraContainer(classLoader, (JiraProjectContainerV1)it);
                return object;
            }
            case 3: {
                Intrinsics.checkNotNull((Object)it, (String)"null cannot be cast to non-null type com.atlassian.migration.app.ConfluenceSpaceContainerV1");
                Object object = this.createConfluenceSpaceContainer(classLoader, (ConfluenceSpaceContainerV1)it);
                return object;
            }
            default: {
                throw new IllegalArgumentException("Cant create container for " + className);
            }
        }
    }

    private final Object createConfluenceSpaceContainer(ClassLoader classLoader, ConfluenceSpaceContainerV1 sourceContainer) {
        Constructor<?>[] constructorArray = classLoader.loadClass("com.atlassian.migration.app.ConfluenceSpaceContainerV1").getConstructors();
        Intrinsics.checkNotNullExpressionValue(constructorArray, (String)"classLoader.loadClass(\"c\u2026ontainerV1\").constructors");
        Object[] objectArray = new Object[]{sourceContainer.getSourceId(), sourceContainer.getKey()};
        return ((Constructor)ArraysKt.first((Object[])constructorArray)).newInstance(objectArray);
    }

    private final Object createJiraContainer(ClassLoader classLoader, JiraProjectContainerV1 sourceContainer) {
        Constructor<?>[] constructorArray = classLoader.loadClass("com.atlassian.migration.app.JiraProjectContainerV1").getConstructors();
        Intrinsics.checkNotNullExpressionValue(constructorArray, (String)"classLoader.loadClass(\"c\u2026ontainerV1\").constructors");
        Object[] objectArray = new Object[]{sourceContainer.getSourceId(), sourceContainer.getKey()};
        return ((Constructor)ArraysKt.first((Object[])constructorArray)).newInstance(objectArray);
    }

    private final Object createSiteContainer(ClassLoader classLoader, SiteContainerV1 siteContainerV1) {
        Object[] objectArray;
        Set set;
        if (siteContainerV1.getSelections().isEmpty()) {
            set = SetsKt.emptySet();
        } else {
            objectArray = new Class[]{String.class};
            Method method = classLoader.loadClass("com.atlassian.migration.app.SiteSelection").getMethod("valueOf", (Class<?>[])objectArray);
            objectArray = new Object[]{"USERS"};
            set = SetsKt.setOf((Object)method.invoke(null, objectArray));
        }
        Set selections = set;
        Constructor<?>[] constructorArray = classLoader.loadClass("com.atlassian.migration.app.SiteContainerV1").getConstructors();
        Intrinsics.checkNotNullExpressionValue(constructorArray, (String)"classLoader.loadClass(\"c\u2026ontainerV1\").constructors");
        objectArray = new Object[]{selections};
        return ((Constructor)ArraysKt.first((Object[])constructorArray)).newInstance(objectArray);
    }
}

