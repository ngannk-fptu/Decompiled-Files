/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Option
 *  com.atlassian.fugue.Pair
 *  com.atlassian.mywork.model.Registration
 *  com.atlassian.mywork.model.Registration$RegistrationId
 *  com.atlassian.mywork.service.RegistrationService
 */
package com.atlassian.mywork.host.service;

import com.atlassian.fugue.Option;
import com.atlassian.fugue.Pair;
import com.atlassian.mywork.model.Registration;
import com.atlassian.mywork.service.RegistrationService;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public interface LocalRegistrationService
extends RegistrationService {
    public Registration get(Registration.RegistrationId var1);

    public Option<Pair<List<Registration>, Date>> getAll(Date var1);

    public Date getLastModified();

    public String getCacheValue(Locale var1);
}

