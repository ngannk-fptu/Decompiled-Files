/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.apache.http.client.utils.URLEncodedUtils
 *  org.apache.http.message.BasicNameValuePair
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service.confluence.server;

import com.addonengine.addons.analytics.service.confluence.UrlBuilder;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.jetbrains.annotations.NotNull;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010$\n\u0000\n\u0002\u0010\u000b\n\u0002\b\n\b\u0007\u0018\u00002\u00020\u0001B\u0011\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J,\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\b0\n2\u0006\u0010\u000b\u001a\u00020\fH\u0002J\u0010\u0010\r\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\bH\u0016J\u0010\u0010\u000e\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00020\bH\u0016J(\u0010\u0010\u001a\u00020\b2\u0006\u0010\u0011\u001a\u00020\b2\u0006\u0010\u0012\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\u0013\u001a\u00020\fH\u0002J\b\u0010\u0014\u001a\u00020\bH\u0016J\b\u0010\u0015\u001a\u00020\bH\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/server/UrlBuilderServerImpl;", "Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;", "applicationProperties", "Lcom/atlassian/sal/api/ApplicationProperties;", "(Lcom/atlassian/sal/api/ApplicationProperties;)V", "buildHostActionUrl", "Ljava/net/URL;", "actionName", "", "queryString", "", "administration", "", "buildHostAdminActionUrl", "buildHostCanonicalUri", "path", "buildUrl", "baseUrl", "addonKey", "adminLink", "getAnonymousUserProfilePictureUrl", "getBaseUrl", "analytics"})
@SourceDebugExtension(value={"SMAP\nUrlBuilderServerImpl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UrlBuilderServerImpl.kt\ncom/addonengine/addons/analytics/service/confluence/server/UrlBuilderServerImpl\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,48:1\n125#2:49\n152#2,3:50\n*S KotlinDebug\n*F\n+ 1 UrlBuilderServerImpl.kt\ncom/addonengine/addons/analytics/service/confluence/server/UrlBuilderServerImpl\n*L\n39#1:49\n39#1:50,3\n*E\n"})
public final class UrlBuilderServerImpl
implements UrlBuilder {
    @NotNull
    private final ApplicationProperties applicationProperties;

    @Inject
    public UrlBuilderServerImpl(@ComponentImport @NotNull ApplicationProperties applicationProperties) {
        Intrinsics.checkNotNullParameter((Object)applicationProperties, (String)"applicationProperties");
        this.applicationProperties = applicationProperties;
    }

    @Override
    @NotNull
    public URL buildHostCanonicalUri(@NotNull String path) {
        Intrinsics.checkNotNullParameter((Object)path, (String)"path");
        return new URL(this.getBaseUrl() + path);
    }

    private final String buildUrl(String baseUrl, String addonKey, String actionName, boolean adminLink) {
        return adminLink ? baseUrl + "/admin/plugins/" + addonKey + '/' + actionName + ".action" : baseUrl + "/plugins/" + addonKey + '/' + actionName + ".action";
    }

    @Override
    @NotNull
    public URL buildHostAdminActionUrl(@NotNull String actionName) {
        Intrinsics.checkNotNullParameter((Object)actionName, (String)"actionName");
        return this.buildHostActionUrl(actionName, MapsKt.emptyMap(), true);
    }

    @Override
    @NotNull
    public URL buildHostActionUrl(@NotNull String actionName) {
        Intrinsics.checkNotNullParameter((Object)actionName, (String)"actionName");
        return this.buildHostActionUrl(actionName, MapsKt.emptyMap(), false);
    }

    /*
     * WARNING - void declaration
     */
    private final URL buildHostActionUrl(String actionName, Map<String, String> queryString, boolean administration) {
        void $this$mapTo$iv$iv;
        String baseUrl = this.getBaseUrl();
        String addonPath = "confanalytics";
        Map<String, String> $this$map$iv = queryString;
        boolean $i$f$map = false;
        Map<String, String> map = $this$map$iv;
        Collection destination$iv$iv = new ArrayList($this$map$iv.size());
        boolean $i$f$mapTo = false;
        Iterator iterator = $this$mapTo$iv$iv.entrySet().iterator();
        while (iterator.hasNext()) {
            void it;
            Map.Entry item$iv$iv;
            Map.Entry entry = item$iv$iv = iterator.next();
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new BasicNameValuePair((String)it.getKey(), (String)it.getValue()));
        }
        String queryString2 = URLEncodedUtils.format((Iterable)((List)destination$iv$iv), (Charset)StandardCharsets.UTF_8);
        String url = this.buildUrl(baseUrl, addonPath, actionName, administration);
        Intrinsics.checkNotNull((Object)queryString2);
        return new URL(((CharSequence)queryString2).length() == 0 ? url : url + '?' + queryString2);
    }

    @Override
    @NotNull
    public String getBaseUrl() {
        String string = this.applicationProperties.getBaseUrl(UrlMode.CANONICAL);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getBaseUrl(...)");
        String string2 = string.toLowerCase();
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"this as java.lang.String).toLowerCase()");
        return string2;
    }

    @Override
    @NotNull
    public String getAnonymousUserProfilePictureUrl() {
        String string = this.buildHostCanonicalUri("/images/icons/profilepics/anonymous.png").toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        return string;
    }
}

