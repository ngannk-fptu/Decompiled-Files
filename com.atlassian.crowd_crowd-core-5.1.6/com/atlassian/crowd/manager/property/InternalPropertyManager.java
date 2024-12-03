/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.property.PropertyManager
 *  com.atlassian.crowd.manager.property.PropertyManagerException
 *  com.atlassian.crowd.manager.rememberme.CrowdSpecificRememberMeSettings
 */
package com.atlassian.crowd.manager.property;

import com.atlassian.crowd.manager.property.PropertyManager;
import com.atlassian.crowd.manager.property.PropertyManagerException;
import com.atlassian.crowd.manager.rememberme.CrowdSpecificRememberMeSettings;
import java.util.Collection;
import java.util.List;

public interface InternalPropertyManager
extends PropertyManager {
    public Collection<String> getNotificationEmails() throws PropertyManagerException;

    public void setNotificationEmails(List<String> var1);

    public void setRememberMeConfiguration(CrowdSpecificRememberMeSettings var1);

    public CrowdSpecificRememberMeSettings getRememberMeConfiguration();
}

