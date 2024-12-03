/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.User
 */
package com.atlassian.crowd.support;

import com.atlassian.crowd.embedded.api.User;
import java.util.Map;

public interface SupportInformationService {
    public String getSupportInformation(User var1);

    public Map<String, String> getSupportInformationMap(User var1);
}

