/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.service;

import com.atlassian.mywork.model.Registration;
import com.atlassian.mywork.service.RegistrationProvider;

public interface ClientRegistrationService {
    public Iterable<Registration> createRegistrations();

    public Registration createRegistration(RegistrationProvider var1);
}

