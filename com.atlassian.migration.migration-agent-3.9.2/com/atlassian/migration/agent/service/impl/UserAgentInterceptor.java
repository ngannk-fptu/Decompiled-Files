/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.status.service.SystemInformationService
 *  com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo
 *  com.atlassian.confluence.status.service.systeminfo.DatabaseInfo
 *  javax.annotation.ParametersAreNonnullByDefault
 *  okhttp3.Interceptor
 *  okhttp3.Interceptor$Chain
 *  okhttp3.Request
 *  okhttp3.Response
 */
package com.atlassian.migration.agent.service.impl;

import com.atlassian.confluence.status.service.SystemInformationService;
import com.atlassian.confluence.status.service.systeminfo.ConfluenceInfo;
import com.atlassian.confluence.status.service.systeminfo.DatabaseInfo;
import com.atlassian.migration.agent.service.impl.SENSupplier;
import com.atlassian.migration.agent.service.version.PluginVersionManager;
import java.io.IOException;
import javax.annotation.ParametersAreNonnullByDefault;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

@ParametersAreNonnullByDefault
public class UserAgentInterceptor
implements Interceptor {
    private static final String X_SEN_HEADER = "X-ASEN";
    private final String userAgent;
    private final String sen;

    public UserAgentInterceptor(PluginVersionManager pluginVersionManager, SystemInformationService systemInformationService, SENSupplier senProvider) {
        ConfluenceInfo confluenceInfo = systemInformationService.getConfluenceInfo();
        DatabaseInfo databaseInfo = systemInformationService.getDatabaseInfo();
        this.sen = senProvider.get();
        StringBuilder version = new StringBuilder(databaseInfo.getVersion());
        int length = version.length();
        for (int i = 0; i < length; ++i) {
            char c = version.charAt(i);
            version.setCharAt(i, '\u001f' < c && c < '\u007f' ? c : (char)' ');
        }
        this.userAgent = String.format("MigrationPlugin/%s (Confluence/%s.%s; %s/%s)", pluginVersionManager.getPluginVersion(), confluenceInfo.getVersion(), confluenceInfo.getBuildNumber(), databaseInfo.getName(), version);
    }

    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        request = request.newBuilder().addHeader("User-Agent", this.userAgent).addHeader(X_SEN_HEADER, this.sen).build();
        return chain.proceed(request);
    }
}

