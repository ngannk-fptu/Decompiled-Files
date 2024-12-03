/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.application.Application
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.crowd.manager.validation;

import com.atlassian.crowd.manager.validation.ClientValidationException;
import com.atlassian.crowd.model.application.Application;
import javax.servlet.http.HttpServletRequest;

public interface ClientValidationManager {
    public void validate(Application var1, HttpServletRequest var2) throws ClientValidationException;
}

