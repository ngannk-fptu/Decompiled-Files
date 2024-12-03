/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.mywork.service.ServiceSelector
 *  org.springframework.stereotype.Component
 */
package com.atlassian.mywork.host.delegator;

import com.atlassian.mywork.service.ServiceSelector;
import org.springframework.stereotype.Component;

@Component
public class ServiceSelectorWrapper {
    private static ServiceSelector serviceSelector;

    public ServiceSelectorWrapper(ServiceSelector serviceSelector) {
        ServiceSelectorWrapper.setServiceSelector(serviceSelector);
    }

    private static void setServiceSelector(ServiceSelector serviceSelector) {
        ServiceSelectorWrapper.serviceSelector = serviceSelector;
    }

    public static ServiceSelector getServiceSelector() {
        return serviceSelector;
    }
}

