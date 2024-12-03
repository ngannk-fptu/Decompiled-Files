/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.pluginsettings.PluginSettings
 *  com.atlassian.sal.api.pluginsettings.PluginSettingsFactory
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.upm.api.license.PluginLicenseManager
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.addonengine.addons.analytics.service.confluence.server;

import com.addonengine.addons.analytics.service.confluence.LicenseRemovalService;
import com.atlassian.plugin.spring.scanner.annotation.component.ConfluenceComponent;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.upm.api.license.PluginLicenseManager;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@ConfluenceComponent
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 \u00112\u00020\u0001:\u0001\u0011B%\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\b\u0010\u000f\u001a\u00020\u0010H\u0016R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u0012"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/server/PluginSettingsLicenseRemovalService;", "Lcom/addonengine/addons/analytics/service/confluence/LicenseRemovalService;", "pluginLicenseManager", "Lcom/atlassian/upm/api/license/PluginLicenseManager;", "pluginSettingsFactory", "Lcom/atlassian/sal/api/pluginsettings/PluginSettingsFactory;", "transactionTemplate", "Lcom/atlassian/sal/api/transaction/TransactionTemplate;", "(Lcom/atlassian/upm/api/license/PluginLicenseManager;Lcom/atlassian/sal/api/pluginsettings/PluginSettingsFactory;Lcom/atlassian/sal/api/transaction/TransactionTemplate;)V", "getPluginLicenseManager", "()Lcom/atlassian/upm/api/license/PluginLicenseManager;", "getPluginSettingsFactory", "()Lcom/atlassian/sal/api/pluginsettings/PluginSettingsFactory;", "getTransactionTemplate", "()Lcom/atlassian/sal/api/transaction/TransactionTemplate;", "removeIfExist", "", "Companion", "analytics"})
@SourceDebugExtension(value={"SMAP\nPluginSettingsLicenseRemovalService.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PluginSettingsLicenseRemovalService.kt\ncom/addonengine/addons/analytics/service/confluence/server/PluginSettingsLicenseRemovalService\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,66:1\n766#2:67\n857#2,2:68\n*S KotlinDebug\n*F\n+ 1 PluginSettingsLicenseRemovalService.kt\ncom/addonengine/addons/analytics/service/confluence/server/PluginSettingsLicenseRemovalService\n*L\n57#1:67\n57#1:68,2\n*E\n"})
public final class PluginSettingsLicenseRemovalService
implements LicenseRemovalService {
    @NotNull
    public static final Companion Companion = new Companion(null);
    @NotNull
    private final PluginLicenseManager pluginLicenseManager;
    @NotNull
    private final PluginSettingsFactory pluginSettingsFactory;
    @NotNull
    private final TransactionTemplate transactionTemplate;
    private static final Logger log = LoggerFactory.getLogger((String)"atlassian.plugin");
    @NotNull
    public static final String AFC_LICENSE_HASHED_KEY = "com.atlassian.upm.license.internal.impl.PluginSettingsPluginLicenseR60cc0445599bfd4bce5ebfda566214e8";
    @NotNull
    public static final String STORED_APP_LICENSES_HASHED_KEY = "com.atlassian.upm.license.internal.impl.PluginSettingsPluginLicenseRfdbfad06c2f1036a60cb955d7e285d69";

    @Autowired
    public PluginSettingsLicenseRemovalService(@ComponentImport @NotNull PluginLicenseManager pluginLicenseManager, @ComponentImport @NotNull PluginSettingsFactory pluginSettingsFactory, @ComponentImport @NotNull TransactionTemplate transactionTemplate) {
        Intrinsics.checkNotNullParameter((Object)pluginLicenseManager, (String)"pluginLicenseManager");
        Intrinsics.checkNotNullParameter((Object)pluginSettingsFactory, (String)"pluginSettingsFactory");
        Intrinsics.checkNotNullParameter((Object)transactionTemplate, (String)"transactionTemplate");
        this.pluginLicenseManager = pluginLicenseManager;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.transactionTemplate = transactionTemplate;
    }

    @NotNull
    public final PluginLicenseManager getPluginLicenseManager() {
        return this.pluginLicenseManager;
    }

    @NotNull
    public final PluginSettingsFactory getPluginSettingsFactory() {
        return this.pluginSettingsFactory;
    }

    @NotNull
    public final TransactionTemplate getTransactionTemplate() {
        return this.transactionTemplate;
    }

    @Override
    public void removeIfExist() {
        if (!this.pluginLicenseManager.getLicense().isDefined()) {
            log.info("There is no legacy licenses for AfC. Skipping");
            return;
        }
        log.info("Removing legacy AfC license");
        this.transactionTemplate.execute(() -> PluginSettingsLicenseRemovalService.removeIfExist$lambda$1(this));
        log.info("Legacy AfC license has been removed");
    }

    /*
     * WARNING - void declaration
     */
    private static final Unit removeIfExist$lambda$1(PluginSettingsLicenseRemovalService this$0) {
        Intrinsics.checkNotNullParameter((Object)this$0, (String)"this$0");
        PluginSettings pluginSettings = this$0.pluginSettingsFactory.createGlobalSettings();
        log.debug("Removing license for AfC using key {}", (Object)AFC_LICENSE_HASHED_KEY);
        pluginSettings.remove(AFC_LICENSE_HASHED_KEY);
        log.debug("AfC license has been removed");
        Object object = pluginSettings.get(STORED_APP_LICENSES_HASHED_KEY);
        if (object == null) {
            object = CollectionsKt.emptyList();
        }
        Intrinsics.checkNotNull((Object)object, (String)"null cannot be cast to non-null type kotlin.collections.List<kotlin.String>");
        List storedAppLicenses = (List)object;
        if (storedAppLicenses.contains("com.addonengine.analytics")) {
            void $this$filterTo$iv$iv;
            log.debug("Found stored license for AfC. Removing it");
            Iterable $this$filter$iv = storedAppLicenses;
            boolean $i$f$filter = false;
            Iterable iterable = $this$filter$iv;
            Collection destination$iv$iv = new ArrayList();
            boolean $i$f$filterTo = false;
            for (Object element$iv$iv : $this$filterTo$iv$iv) {
                String it = (String)element$iv$iv;
                boolean bl = false;
                if (!(!Intrinsics.areEqual((Object)it, (Object)"com.addonengine.analytics"))) continue;
                destination$iv$iv.add(element$iv$iv);
            }
            ArrayList purgedStoredAppLicenses = (ArrayList)CollectionsKt.toCollection((Iterable)((List)destination$iv$iv), (Collection)new ArrayList());
            pluginSettings.put(STORED_APP_LICENSES_HASHED_KEY, (Object)purgedStoredAppLicenses);
            log.debug("Stored license for AfC has been removed");
        }
        return Unit.INSTANCE;
    }

    @Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0006\u001a\n \b*\u0004\u0018\u00010\u00070\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\t"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/server/PluginSettingsLicenseRemovalService$Companion;", "", "()V", "AFC_LICENSE_HASHED_KEY", "", "STORED_APP_LICENSES_HASHED_KEY", "log", "Lorg/slf4j/Logger;", "kotlin.jvm.PlatformType", "analytics"})
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker $constructor_marker) {
            this();
        }
    }
}

