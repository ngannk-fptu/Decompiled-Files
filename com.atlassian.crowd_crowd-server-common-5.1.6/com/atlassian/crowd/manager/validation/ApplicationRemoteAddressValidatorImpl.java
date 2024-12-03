/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.application.RemoteAddress
 *  com.atlassian.ip.IPMatcher
 *  com.atlassian.ip.IPMatcher$Builder
 */
package com.atlassian.crowd.manager.validation;

import com.atlassian.crowd.manager.validation.ApplicationRemoteAddressValidator;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.RemoteAddress;
import com.atlassian.ip.IPMatcher;
import java.net.InetAddress;

public class ApplicationRemoteAddressValidatorImpl
implements ApplicationRemoteAddressValidator {
    @Override
    public boolean validate(Application application, InetAddress clientAddress) {
        IPMatcher.Builder ipMatcherBuilder = IPMatcher.builder();
        for (RemoteAddress allowedAddress : application.getRemoteAddresses()) {
            ipMatcherBuilder.addPatternOrHost(allowedAddress.getAddress());
        }
        return ipMatcherBuilder.build().matches(clientAddress);
    }
}

