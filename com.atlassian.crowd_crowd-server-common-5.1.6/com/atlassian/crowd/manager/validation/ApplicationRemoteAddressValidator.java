/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 */
package com.atlassian.crowd.manager.validation;

import com.atlassian.crowd.model.application.Application;
import java.net.InetAddress;

public interface ApplicationRemoteAddressValidator {
    public boolean validate(Application var1, InetAddress var2);
}

