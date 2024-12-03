/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.forge.ForgeEnvironmentType
 *  com.atlassian.migration.app.jira.JiraAppCloudMigrationListenerV1
 *  com.atlassian.migration.app.listener.DiscoverableListener
 *  kotlin.Metadata
 *  kotlin.TuplesKt
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.AccessScope;
import com.atlassian.migration.app.AppCloudMigrationGateway;
import com.atlassian.migration.app.AppCloudMigrationGatewayHandler;
import com.atlassian.migration.app.BaseAppCloudMigrationListener;
import com.atlassian.migration.app.MigrationDetailsV1;
import com.atlassian.migration.app.ServerAppCustomField;
import com.atlassian.migration.app.forge.ForgeEnvironmentType;
import com.atlassian.migration.app.jira.JiraAppCloudMigrationListenerV1;
import com.atlassian.migration.app.listener.DiscoverableListener;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000l\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 +2\u00020\u00012\u00020\u0002:\u0001+B\u0015\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\u0007J$\u0010\b\u001a\u00020\t2\n\u0010\n\u001a\u0006\u0012\u0002\b\u00030\u000b2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00060\rH\u0002J$\u0010\u000e\u001a\u00020\t2\n\u0010\n\u001a\u0006\u0012\u0002\b\u00030\u000b2\u000e\b\u0002\u0010\f\u001a\b\u0012\u0004\u0012\u00020\u00060\rH\u0002J\b\u0010\u000f\u001a\u00020\u0006H\u0016J\u000e\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00120\u0011H\u0016J\u0006\u0010\u0013\u001a\u00020\u0014J\u0006\u0010\u0015\u001a\u00020\u0016J\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018J\b\u0010\u0019\u001a\u00020\u0006H\u0016J\u0006\u0010\u001a\u001a\u00020\tJ&\u0010\u001b\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010\u001f\u001a\u00020\u00062\u0006\u0010 \u001a\u00020\u00062\u0006\u0010!\u001a\u00020\"J\u001e\u0010#\u001a\u00020\u001c2\u0006\u0010\u001d\u001a\u00020\u001e2\u0006\u0010 \u001a\u00020\u00062\u0006\u0010!\u001a\u00020\"J\u001e\u0010$\u001a\u0004\u0018\u00010\u00042\n\u0010%\u001a\u0006\u0012\u0002\b\u00030\u000b2\u0006\u0010!\u001a\u00020\"H\u0002J\"\u0010&\u001a\u00020\u001c2\u0006\u0010'\u001a\u00020(2\u0006\u0010)\u001a\u00020\u00042\b\u0010*\u001a\u0004\u0018\u00010\u0004H\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006,"}, d2={"Lcom/atlassian/migration/app/DiscoverableListenerProxy;", "Lcom/atlassian/migration/app/listener/DiscoverableListener;", "Lcom/atlassian/migration/app/BaseAppCloudMigrationListener;", "service", "", "bundleKey", "", "(Ljava/lang/Object;Ljava/lang/String;)V", "classImplementsForgeListener", "", "clazz", "Ljava/lang/Class;", "checkedClasses", "", "classImplementsJiraListener", "getCloudAppKey", "getDataAccessScopes", "", "Lcom/atlassian/migration/app/AccessScope;", "getForgeAppId", "Ljava/util/UUID;", "getForgeEnvironmentType", "Lcom/atlassian/migration/app/forge/ForgeEnvironmentType;", "getJiraListener", "Lcom/atlassian/migration/app/jira/JiraAppCloudMigrationListenerV1;", "getServerAppKey", "isForge", "onRerunAppMigration", "", "migrationGateway", "Lcom/atlassian/migration/app/AppCloudMigrationGateway;", "originalTransferId", "transferId", "migrationDetails", "Lcom/atlassian/migration/app/MigrationDetailsV1;", "onStartAppMigration", "proxiedMigrationDetails", "vendorDetailsClass", "setField", "field", "Ljava/lang/reflect/Field;", "instance", "value", "Companion", "app-migration-assistant"})
public final class DiscoverableListenerProxy
implements DiscoverableListener,
BaseAppCloudMigrationListener {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final Object service;
    @NotNull
    private final String bundleKey;
    @NotNull
    public static final String JIRA_LISTENER = "com.atlassian.migration.app.jira.JiraAppCloudMigrationListenerV1";
    @NotNull
    public static final String FORGE_LISTENER = "com.atlassian.migration.app.listener.DiscoverableForgeListener";

    public DiscoverableListenerProxy(@NotNull Object service, @NotNull String bundleKey) {
        Intrinsics.checkNotNullParameter((Object)service, (String)"service");
        Intrinsics.checkNotNullParameter((Object)bundleKey, (String)"bundleKey");
        this.service = service;
        this.bundleKey = bundleKey;
    }

    @Nullable
    public final JiraAppCloudMigrationListenerV1 getJiraListener() {
        if (DiscoverableListenerProxy.classImplementsJiraListener$default(this, this.service.getClass(), null, 2, null)) {
            return new JiraAppCloudMigrationListenerV1(this){
                final /* synthetic */ DiscoverableListenerProxy this$0;
                {
                    this.this$0 = $receiver;
                }

                @NotNull
                public Map<String, String> getSupportedWorkflowRuleMappings() {
                    Object object = DiscoverableListenerProxy.access$getService$p(this.this$0).getClass().getMethod("getSupportedWorkflowRuleMappings", new Class[0]).invoke(DiscoverableListenerProxy.access$getService$p(this.this$0), new Object[0]);
                    Intrinsics.checkNotNull((Object)object, (String)"null cannot be cast to non-null type kotlin.collections.Map<kotlin.String, kotlin.String>");
                    return (Map)object;
                }

                /*
                 * WARNING - void declaration
                 */
                @NotNull
                public Map<ServerAppCustomField, String> getSupportedCustomFieldMappings() {
                    void $this$mapTo$iv$iv;
                    Map mapFromVendor;
                    Object object = DiscoverableListenerProxy.access$getService$p(this.this$0).getClass().getMethod("getSupportedCustomFieldMappings", new Class[0]).invoke(DiscoverableListenerProxy.access$getService$p(this.this$0), new Object[0]);
                    Intrinsics.checkNotNull((Object)object, (String)"null cannot be cast to non-null type kotlin.collections.Map<kotlin.Any, kotlin.String>");
                    Map $this$map$iv = mapFromVendor = (Map)object;
                    boolean $i$f$map = false;
                    Map map = $this$map$iv;
                    Collection destination$iv$iv = new ArrayList<E>($this$map$iv.size());
                    boolean $i$f$mapTo = false;
                    Iterator<Map.Entry<K, V>> iterator = $this$mapTo$iv$iv.entrySet().iterator();
                    while (iterator.hasNext()) {
                        void it;
                        Map.Entry<K, V> item$iv$iv;
                        Map.Entry<K, V> entry = item$iv$iv = iterator.next();
                        Collection collection = destination$iv$iv;
                        boolean bl = false;
                        collection.add(TuplesKt.to((Object)this.convertServerAppCustomField(it.getKey()), it.getValue()));
                    }
                    return MapsKt.toMap((Iterable)((List)destination$iv$iv));
                }

                private final ServerAppCustomField convertServerAppCustomField(Object key) {
                    return new ServerAppCustomField((String)key.getClass().getMethod("getFieldName", new Class[0]).invoke(key, new Object[0]), (String)key.getClass().getMethod("getFieldTypeKey", new Class[0]).invoke(key, new Object[0]));
                }
            };
        }
        return null;
    }

    @Override
    @NotNull
    public String getCloudAppKey() {
        Object element$iv2;
        block2: {
            Method[] methodArray = this.service.getClass().getMethods();
            Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"service.javaClass.methods");
            Object[] $this$first$iv = methodArray;
            boolean $i$f$first = false;
            for (Object element$iv2 : $this$first$iv) {
                Method it = (Method)element$iv2;
                boolean bl = false;
                if (!Intrinsics.areEqual((Object)it.getName(), (Object)"getCloudAppKey")) {
                    continue;
                }
                break block2;
            }
            throw new NoSuchElementException("Array contains no element matching the predicate.");
        }
        Object object = ((Method)element$iv2).invoke(this.service, new Object[0]);
        Intrinsics.checkNotNull((Object)object, (String)"null cannot be cast to non-null type kotlin.String");
        return (String)object;
    }

    @NotNull
    public final UUID getForgeAppId() {
        Object element$iv2;
        block2: {
            Method[] methodArray = this.service.getClass().getMethods();
            Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"service.javaClass.methods");
            Object[] $this$first$iv = methodArray;
            boolean $i$f$first = false;
            for (Object element$iv2 : $this$first$iv) {
                Method it = (Method)element$iv2;
                boolean bl = false;
                if (!Intrinsics.areEqual((Object)it.getName(), (Object)"getForgeAppId")) {
                    continue;
                }
                break block2;
            }
            throw new NoSuchElementException("Array contains no element matching the predicate.");
        }
        Object object = ((Method)element$iv2).invoke(this.service, new Object[0]);
        Intrinsics.checkNotNull((Object)object, (String)"null cannot be cast to non-null type java.util.UUID");
        return (UUID)object;
    }

    @NotNull
    public final ForgeEnvironmentType getForgeEnvironmentType() {
        Object element$iv2;
        block2: {
            Method[] methodArray = this.service.getClass().getMethods();
            Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"service.javaClass.methods");
            Object[] $this$first$iv = methodArray;
            boolean $i$f$first = false;
            for (Object element$iv2 : $this$first$iv) {
                Method it = (Method)element$iv2;
                boolean bl = false;
                if (!Intrinsics.areEqual((Object)it.getName(), (Object)"getForgeEnvironmentType")) {
                    continue;
                }
                break block2;
            }
            throw new NoSuchElementException("Array contains no element matching the predicate.");
        }
        return ForgeEnvironmentType.valueOf((String)((Method)element$iv2).invoke(this.service, new Object[0]).toString());
    }

    @Override
    @NotNull
    public String getServerAppKey() {
        Object element$iv2;
        block3: {
            Method[] methodArray = this.service.getClass().getMethods();
            Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"service.javaClass.methods");
            Object[] $this$first$iv = methodArray;
            boolean $i$f$first = false;
            for (Object element$iv2 : $this$first$iv) {
                Method it = (Method)element$iv2;
                boolean bl = false;
                if (!Intrinsics.areEqual((Object)it.getName(), (Object)"getServerAppKey")) {
                    continue;
                }
                break block3;
            }
            throw new NoSuchElementException("Array contains no element matching the predicate.");
        }
        String string = (String)((Method)element$iv2).invoke(this.service, new Object[0]);
        if (string == null) {
            string = this.bundleKey;
        }
        return string;
    }

    /*
     * WARNING - void declaration
     */
    @Override
    @NotNull
    public Set<AccessScope> getDataAccessScopes() {
        void $this$mapTo$iv$iv;
        Object element$iv2;
        block3: {
            Method[] methodArray = this.service.getClass().getMethods();
            Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"service.javaClass.methods");
            Object[] $this$first$iv = methodArray;
            boolean $i$f$first = false;
            for (Object element$iv2 : $this$first$iv) {
                Method it = (Method)element$iv2;
                boolean bl = false;
                if (!Intrinsics.areEqual((Object)it.getName(), (Object)"getDataAccessScopes")) {
                    continue;
                }
                break block3;
            }
            throw new NoSuchElementException("Array contains no element matching the predicate.");
        }
        Object object = ((Method)element$iv2).invoke(this.service, new Object[0]);
        Intrinsics.checkNotNull((Object)object, (String)"null cannot be cast to non-null type kotlin.collections.Set<kotlin.Any>");
        Set accessScopes = (Set)object;
        Iterable $this$map$iv = accessScopes;
        boolean $i$f$map = false;
        Iterable $i$f$first = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        Iterator iterator = $this$mapTo$iv$iv.iterator();
        while (iterator.hasNext()) {
            void it;
            Object item$iv$iv;
            Object bl = item$iv$iv = iterator.next();
            Collection collection = destination$iv$iv;
            boolean bl2 = false;
            collection.add(AccessScope.valueOf(it.toString()));
        }
        return CollectionsKt.toSet((Iterable)((List)destination$iv$iv));
    }

    public final boolean isForge() {
        return DiscoverableListenerProxy.classImplementsForgeListener$default(this, this.service.getClass(), null, 2, null);
    }

    public final void onStartAppMigration(@NotNull AppCloudMigrationGateway migrationGateway, @NotNull String transferId, @NotNull MigrationDetailsV1 migrationDetails) {
        Object element$iv2;
        block2: {
            Intrinsics.checkNotNullParameter((Object)migrationGateway, (String)"migrationGateway");
            Intrinsics.checkNotNullParameter((Object)transferId, (String)"transferId");
            Intrinsics.checkNotNullParameter((Object)migrationDetails, (String)"migrationDetails");
            Method[] methodArray = this.service.getClass().getMethods();
            Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"service.javaClass.methods");
            Object[] $this$first$iv = methodArray;
            boolean $i$f$first = false;
            for (Object element$iv2 : $this$first$iv) {
                Method it = (Method)element$iv2;
                boolean bl = false;
                if (!(Intrinsics.areEqual((Object)it.getName(), (Object)"onStartAppMigration") && it.getParameterCount() == 3)) {
                    continue;
                }
                break block2;
            }
            throw new NoSuchElementException("Array contains no element matching the predicate.");
        }
        Method method = (Method)element$iv2;
        Class<?> vendorClass = method.getParameterTypes()[0];
        ClassLoader classLoader = vendorClass.getClassLoader();
        Class[] classArray = new Class[1];
        Intrinsics.checkNotNullExpressionValue(vendorClass, (String)"vendorClass");
        classArray[0] = vendorClass;
        Object appCloudMigrationGatewayProxy = Proxy.newProxyInstance(classLoader, classArray, (InvocationHandler)new AppCloudMigrationGatewayHandler(migrationGateway));
        Class<?> vendorDetailsClass = method.getParameterTypes()[2];
        Intrinsics.checkNotNullExpressionValue(vendorDetailsClass, (String)"vendorDetailsClass");
        Object proxiedMigrationDetails = this.proxiedMigrationDetails(vendorDetailsClass, migrationDetails);
        Object[] objectArray = new Object[]{appCloudMigrationGatewayProxy, transferId, proxiedMigrationDetails};
        method.invoke(this.service, objectArray);
    }

    public final void onRerunAppMigration(@NotNull AppCloudMigrationGateway migrationGateway, @NotNull String originalTransferId, @NotNull String transferId, @NotNull MigrationDetailsV1 migrationDetails) {
        Object object;
        block3: {
            Intrinsics.checkNotNullParameter((Object)migrationGateway, (String)"migrationGateway");
            Intrinsics.checkNotNullParameter((Object)originalTransferId, (String)"originalTransferId");
            Intrinsics.checkNotNullParameter((Object)transferId, (String)"transferId");
            Intrinsics.checkNotNullParameter((Object)migrationDetails, (String)"migrationDetails");
            Method[] methodArray = this.service.getClass().getMethods();
            Intrinsics.checkNotNullExpressionValue((Object)methodArray, (String)"service.javaClass.methods");
            Object[] $this$firstOrNull$iv = methodArray;
            boolean $i$f$firstOrNull = false;
            for (Object element$iv : $this$firstOrNull$iv) {
                Method it = (Method)element$iv;
                boolean bl = false;
                if (!(Intrinsics.areEqual((Object)it.getName(), (Object)"onRerunAppMigration") && it.getParameterCount() == 4)) continue;
                object = element$iv;
                break block3;
            }
            object = null;
        }
        Method method = (Method)object;
        if (method != null) {
            Class<?> vendorClass = method.getParameterTypes()[0];
            ClassLoader classLoader = vendorClass.getClassLoader();
            Class[] classArray = new Class[1];
            Intrinsics.checkNotNullExpressionValue(vendorClass, (String)"vendorClass");
            classArray[0] = vendorClass;
            Object appCloudMigrationGatewayProxy = Proxy.newProxyInstance(classLoader, classArray, (InvocationHandler)new AppCloudMigrationGatewayHandler(migrationGateway));
            Class<?> vendorDetailsClass = method.getParameterTypes()[3];
            Intrinsics.checkNotNullExpressionValue(vendorDetailsClass, (String)"vendorDetailsClass");
            Object proxiedMigrationDetails = this.proxiedMigrationDetails(vendorDetailsClass, migrationDetails);
            Object[] objectArray = new Object[]{appCloudMigrationGatewayProxy, originalTransferId, transferId, proxiedMigrationDetails};
            method.invoke(this.service, objectArray);
        } else {
            this.onStartAppMigration(migrationGateway, transferId, migrationDetails);
        }
    }

    private final Object proxiedMigrationDetails(Class<?> vendorDetailsClass, MigrationDetailsV1 migrationDetails) {
        Object newInstance = vendorDetailsClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        Field field = vendorDetailsClass.getDeclaredField("migrationId");
        Intrinsics.checkNotNullExpressionValue((Object)field, (String)"vendorDetailsClass.getDeclaredField(\"migrationId\")");
        Intrinsics.checkNotNullExpressionValue(newInstance, (String)"newInstance");
        this.setField(field, newInstance, migrationDetails.getMigrationId());
        Field field2 = vendorDetailsClass.getDeclaredField("migrationScopeId");
        Intrinsics.checkNotNullExpressionValue((Object)field2, (String)"vendorDetailsClass.getDe\u2026Field(\"migrationScopeId\")");
        this.setField(field2, newInstance, migrationDetails.getMigrationScopeId());
        Field field3 = vendorDetailsClass.getDeclaredField("name");
        Intrinsics.checkNotNullExpressionValue((Object)field3, (String)"vendorDetailsClass.getDeclaredField(\"name\")");
        this.setField(field3, newInstance, migrationDetails.getName());
        Field field4 = vendorDetailsClass.getDeclaredField("createdAt");
        Intrinsics.checkNotNullExpressionValue((Object)field4, (String)"vendorDetailsClass.getDeclaredField(\"createdAt\")");
        this.setField(field4, newInstance, migrationDetails.getCreatedAt());
        Field field5 = vendorDetailsClass.getDeclaredField("jiraClientKey");
        Intrinsics.checkNotNullExpressionValue((Object)field5, (String)"vendorDetailsClass.getDe\u2026redField(\"jiraClientKey\")");
        this.setField(field5, newInstance, migrationDetails.getJiraClientKey());
        Field field6 = vendorDetailsClass.getDeclaredField("confluenceClientKey");
        Intrinsics.checkNotNullExpressionValue((Object)field6, (String)"vendorDetailsClass.getDe\u2026ld(\"confluenceClientKey\")");
        this.setField(field6, newInstance, migrationDetails.getConfluenceClientKey());
        Field field7 = vendorDetailsClass.getDeclaredField("cloudUrl");
        Intrinsics.checkNotNullExpressionValue((Object)field7, (String)"vendorDetailsClass.getDeclaredField(\"cloudUrl\")");
        this.setField(field7, newInstance, migrationDetails.getCloudUrl());
        return newInstance;
    }

    private final void setField(Field field, Object instance, Object value) {
        field.setAccessible(true);
        field.set(instance, value);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final boolean classImplementsJiraListener(Class<?> clazz, Set<String> checkedClasses) {
        Class it;
        String string = clazz.getName();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"clazz.name");
        if (!checkedClasses.add(string)) return false;
        Class<?>[] classArray = clazz.getInterfaces();
        Intrinsics.checkNotNullExpressionValue(classArray, (String)"clazz.interfaces");
        Object[] $this$any$iv = classArray;
        boolean $i$f$any = false;
        for (Object element$iv : $this$any$iv) {
            it = (Class)element$iv;
            boolean bl = false;
            if (!it.getName().equals(JIRA_LISTENER)) continue;
            return true;
        }
        boolean bl = false;
        if (bl) return true;
        Class<?>[] classArray2 = clazz.getInterfaces();
        Intrinsics.checkNotNullExpressionValue(classArray2, (String)"clazz.interfaces");
        $this$any$iv = classArray2;
        $i$f$any = false;
        for (Object element$iv : $this$any$iv) {
            it = (Class)element$iv;
            boolean bl2 = false;
            Intrinsics.checkNotNullExpressionValue((Object)it, (String)"it");
            if (!this.classImplementsJiraListener(it, checkedClasses)) continue;
            return true;
        }
        boolean bl3 = false;
        if (bl3) return true;
        Class<?> clazz2 = clazz.getSuperclass();
        if (clazz2 == null) return false;
        Class<?> it2 = clazz2;
        boolean bl4 = false;
        boolean bl5 = this.classImplementsJiraListener(it2, checkedClasses);
        if (!bl5) return false;
        return true;
    }

    static /* synthetic */ boolean classImplementsJiraListener$default(DiscoverableListenerProxy discoverableListenerProxy, Class clazz, Set set, int n, Object object) {
        if ((n & 2) != 0) {
            set = new HashSet();
        }
        return discoverableListenerProxy.classImplementsJiraListener(clazz, set);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private final boolean classImplementsForgeListener(Class<?> clazz, Set<String> checkedClasses) {
        Class it;
        String string = clazz.getName();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"clazz.name");
        if (!checkedClasses.add(string)) return false;
        Class<?>[] classArray = clazz.getInterfaces();
        Intrinsics.checkNotNullExpressionValue(classArray, (String)"clazz.interfaces");
        Object[] $this$any$iv = classArray;
        boolean $i$f$any = false;
        for (Object element$iv : $this$any$iv) {
            it = (Class)element$iv;
            boolean bl = false;
            if (!it.getName().equals(FORGE_LISTENER)) continue;
            return true;
        }
        boolean bl = false;
        if (bl) return true;
        Class<?>[] classArray2 = clazz.getInterfaces();
        Intrinsics.checkNotNullExpressionValue(classArray2, (String)"clazz.interfaces");
        $this$any$iv = classArray2;
        $i$f$any = false;
        for (Object element$iv : $this$any$iv) {
            it = (Class)element$iv;
            boolean bl2 = false;
            Intrinsics.checkNotNullExpressionValue((Object)it, (String)"it");
            if (!this.classImplementsForgeListener(it, checkedClasses)) continue;
            return true;
        }
        boolean bl3 = false;
        if (bl3) return true;
        Class<?> clazz2 = clazz.getSuperclass();
        if (clazz2 == null) return false;
        Class<?> it2 = clazz2;
        boolean bl4 = false;
        boolean bl5 = this.classImplementsForgeListener(it2, checkedClasses);
        if (!bl5) return false;
        return true;
    }

    static /* synthetic */ boolean classImplementsForgeListener$default(DiscoverableListenerProxy discoverableListenerProxy, Class clazz, Set set, int n, Object object) {
        if ((n & 2) != 0) {
            set = new HashSet();
        }
        return discoverableListenerProxy.classImplementsForgeListener(clazz, set);
    }

    public static final /* synthetic */ Object access$getService$p(DiscoverableListenerProxy $this) {
        return $this.service;
    }

    @Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2={"Lcom/atlassian/migration/app/DiscoverableListenerProxy$Companion;", "", "()V", "FORGE_LISTENER", "", "JIRA_LISTENER", "app-migration-assistant"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

