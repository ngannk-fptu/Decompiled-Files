/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  org.codehaus.jackson.map.ObjectMapper
 */
package com.atlassian.confluence.plugins.licensebanner.provider;

import com.atlassian.confluence.plugins.licensebanner.provider.LicenseDetailsModel;
import com.atlassian.confluence.plugins.licensebanner.support.LicenseBannerService;
import com.atlassian.confluence.plugins.licensebanner.support.LicenseDetails;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import java.io.IOException;
import java.io.Writer;
import org.codehaus.jackson.map.ObjectMapper;

public class LicenseDetailsProvider
implements WebResourceDataProvider {
    private final UserManager userManager;
    private final LicenseBannerService licenseBannerService;

    public LicenseDetailsProvider(@ComponentImport UserManager userManager, LicenseBannerService licenseBannerService) {
        this.userManager = userManager;
        this.licenseBannerService = licenseBannerService;
    }

    public Jsonable get() {
        LicenseDetailsModel licenseDetailsModel;
        UserKey remoteUserKey = this.userManager.getRemoteUserKey();
        if (remoteUserKey == null || !this.userManager.isSystemAdmin(remoteUserKey)) {
            licenseDetailsModel = new LicenseDetailsModel();
        } else {
            LicenseDetails details = this.licenseBannerService.retrieveLicenseDetails(remoteUserKey);
            licenseDetailsModel = new LicenseDetailsModel(details);
        }
        return new Jsonable(){
            private final ObjectMapper objectMapper = new ObjectMapper();

            public void write(Writer writer) throws IOException {
                this.objectMapper.writeValue(writer, (Object)licenseDetailsModel);
            }
        };
    }
}

