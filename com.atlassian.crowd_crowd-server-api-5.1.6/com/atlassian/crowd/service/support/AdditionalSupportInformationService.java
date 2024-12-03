/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.support.SupportInformationBuilder
 */
package com.atlassian.crowd.service.support;

import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.support.SupportInformationBuilder;

public interface AdditionalSupportInformationService {
    public void extendSupportInformation(SupportInformationBuilder var1);

    default public void extendSupportInformation(SupportInformationBuilder supportInformationBuilder, Application application, int applicationIndex) {
    }
}

