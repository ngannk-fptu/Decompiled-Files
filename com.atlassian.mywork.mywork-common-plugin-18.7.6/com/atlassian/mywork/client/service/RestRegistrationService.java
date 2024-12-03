/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.client.service;

import com.atlassian.mywork.model.Registration;
import com.atlassian.mywork.service.HostService;
import com.atlassian.mywork.service.RegistrationService;

public class RestRegistrationService
implements RegistrationService {
    private final HostService hostService;

    public RestRegistrationService(HostService hostService) {
        this.hostService = hostService;
    }

    @Override
    public void register(Iterable<Registration> registrations) {
        this.hostService.disable();
        this.hostService.enable();
    }
}

