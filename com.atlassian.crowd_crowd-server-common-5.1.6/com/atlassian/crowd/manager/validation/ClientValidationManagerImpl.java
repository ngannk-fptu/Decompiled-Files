/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.util.I18nHelper
 *  com.google.common.base.Preconditions
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.lang3.Validate
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.validation;

import com.atlassian.crowd.manager.application.InternalApplicationAttributes;
import com.atlassian.crowd.manager.proxy.TrustedProxyManager;
import com.atlassian.crowd.manager.validation.ApplicationRemoteAddressValidator;
import com.atlassian.crowd.manager.validation.ClientValidationException;
import com.atlassian.crowd.manager.validation.ClientValidationManager;
import com.atlassian.crowd.manager.validation.XForwardedForUtil;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.util.I18nHelper;
import com.google.common.base.Preconditions;
import java.net.InetAddress;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientValidationManagerImpl
implements ClientValidationManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ClientValidationManagerImpl.class);
    private final TrustedProxyManager trustedProxyManager;
    private final I18nHelper i18nHelper;
    private final ApplicationRemoteAddressValidator applicationRemoteAddressValidator;

    public ClientValidationManagerImpl(TrustedProxyManager trustedProxyManager, I18nHelper i18nHelper, ApplicationRemoteAddressValidator applicationRemoteAddressValidator) {
        this.trustedProxyManager = trustedProxyManager;
        this.i18nHelper = i18nHelper;
        this.applicationRemoteAddressValidator = (ApplicationRemoteAddressValidator)Preconditions.checkNotNull((Object)applicationRemoteAddressValidator);
    }

    @Override
    public void validate(Application application, HttpServletRequest request) throws ClientValidationException {
        Validate.notNull((Object)application);
        Validate.notNull((Object)request);
        this.validateApplicationActive(application);
        this.validateApplicationAccess(application);
        this.validateRemoteAddress(application, request);
    }

    private void validateApplicationActive(Application application) throws ClientValidationException {
        if (!application.isActive()) {
            throw new ClientValidationException(this.i18nHelper.getText("application.inactive.error", application.getName()));
        }
    }

    private void validateApplicationAccess(Application application) throws ClientValidationException {
        String accessDenied = (String)application.getAttributes().get(InternalApplicationAttributes.ACCESS_DENIED.getAttribute());
        if (Boolean.parseBoolean(accessDenied)) {
            throw new ClientValidationException(this.i18nHelper.getText("application.access.error", application.getName()));
        }
    }

    private void validateRemoteAddress(Application application, HttpServletRequest request) throws ClientValidationException {
        InetAddress clientAddress = XForwardedForUtil.getTrustedAddress(this.trustedProxyManager, request);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Client address: " + clientAddress.getHostAddress());
        }
        if (!this.applicationRemoteAddressValidator.validate(application, clientAddress)) {
            LOGGER.info("Client with address '{}' is forbidden from making requests to application '{}'", (Object)clientAddress.getHostAddress(), (Object)application.getName());
            String errorMsg = this.i18nHelper.getText("client.forbidden.exception", clientAddress.getHostAddress(), application.getName());
            throw new ClientValidationException(errorMsg);
        }
    }
}

