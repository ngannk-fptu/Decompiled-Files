/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 *  com.atlassian.mywork.model.Registration
 *  com.atlassian.mywork.model.Registration$RegistrationId
 */
package com.atlassian.mywork.host.dao;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.mywork.model.Registration;
import java.util.Date;
import java.util.List;

@Transactional
public interface RegistrationDao {
    public Registration get(Registration.RegistrationId var1);

    public void set(Registration var1);

    public List<Registration> getAll();

    public Date getMostRecentUpdate();
}

