/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.json.marshal.Jsonable$JsonMappingException
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.sal.api.user.UserProfile
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  com.atlassian.whisper.plugin.api.MessagesService
 *  javax.inject.Inject
 *  org.codehaus.jettison.json.JSONException
 *  org.codehaus.jettison.json.JSONObject
 */
package com.atlassian.whisper.plugin.impl;

import com.atlassian.json.marshal.Jsonable;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.atlassian.whisper.plugin.api.MessagesService;
import com.atlassian.whisper.plugin.impl.LicenseProductsInfoProvider;
import java.time.Instant;
import java.util.Optional;
import javax.inject.Inject;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class WhisperDataProvider
implements WebResourceDataProvider {
    private final MessagesService messagesService;
    private final UserManager userManager;
    private final ApplicationProperties applicationProperties;
    private final LicenseProductsInfoProvider licenseProductsInfoProvider;

    @Inject
    public WhisperDataProvider(MessagesService messagesService, UserManager userManager, ApplicationProperties applicationProperties, LicenseProductsInfoProvider licenseProductsInfoProvider) {
        this.messagesService = messagesService;
        this.userManager = userManager;
        this.applicationProperties = applicationProperties;
        this.licenseProductsInfoProvider = licenseProductsInfoProvider;
    }

    public Jsonable get() {
        return writer -> {
            try {
                this.getJsonData().write(writer);
            }
            catch (JSONException e) {
                throw new Jsonable.JsonMappingException((Throwable)e);
            }
        };
    }

    private JSONObject getJsonData() throws JSONException {
        JSONObject json = new JSONObject();
        UserProfile currentUser = this.userManager.getRemoteUser();
        json.put("hasMessages", this.messagesService.hasMessagesForCurrentUser());
        json.put("syncInit", this.messagesService.hasOverride(currentUser, "whisper-sync-init"));
        json.put("userEmail", Optional.ofNullable(currentUser).map(UserProfile::getEmail).orElse(null));
        json.put("baseUrl", (Object)this.applicationProperties.getBaseUrl(UrlMode.ABSOLUTE));
        if (currentUser != null && this.userManager.isAdmin(currentUser.getUserKey())) {
            JSONObject products = new JSONObject();
            this.appendProductInfoJson("jira-core", products);
            this.appendProductInfoJson("jira-software", products);
            this.appendProductInfoJson("jira-servicedesk", products);
            this.appendProductInfoJson("conf", products);
            json.put("products", (Object)products);
        }
        return json;
    }

    private void appendProductInfoJson(String productKey, JSONObject json) throws JSONException {
        JSONObject productInfo = this.getProductInfoJson(productKey);
        if (productInfo != null) {
            json.put(productKey, (Object)productInfo);
        }
    }

    private JSONObject getProductInfoJson(String productKey) throws JSONException {
        if (!this.licenseProductsInfoProvider.isProductLicensed(productKey)) {
            return null;
        }
        JSONObject productInfo = new JSONObject();
        productInfo.put("sen", (Object)this.licenseProductsInfoProvider.getProductSEN(productKey));
        productInfo.put("maintenanceExpiry", Optional.ofNullable(this.licenseProductsInfoProvider.getMaintenanceExpiryDate(productKey)).map(Instant::toEpochMilli).orElse(null));
        productInfo.put("licenseExpiry", Optional.ofNullable(this.licenseProductsInfoProvider.getLicenseExpiryDate(productKey)).map(Instant::toEpochMilli).orElse(null));
        return productInfo;
    }
}

