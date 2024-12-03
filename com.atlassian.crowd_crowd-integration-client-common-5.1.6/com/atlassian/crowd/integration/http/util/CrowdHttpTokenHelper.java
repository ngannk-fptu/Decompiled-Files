/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.model.authentication.CookieConfiguration
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 */
package com.atlassian.crowd.integration.http.util;

import com.atlassian.crowd.integration.http.util.CrowdHttpValidationFactorExtractor;
import com.atlassian.crowd.model.authentication.CookieConfiguration;
import com.atlassian.crowd.model.authentication.UserAuthenticationContext;
import com.atlassian.crowd.service.client.ClientProperties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface CrowdHttpTokenHelper {
    public String getCrowdToken(HttpServletRequest var1, String var2);

    public void removeCrowdToken(HttpServletRequest var1, HttpServletResponse var2, ClientProperties var3, CookieConfiguration var4);

    public void setCrowdToken(HttpServletRequest var1, HttpServletResponse var2, String var3, ClientProperties var4, CookieConfiguration var5);

    public UserAuthenticationContext getUserAuthenticationContext(HttpServletRequest var1, String var2, String var3, ClientProperties var4);

    public CrowdHttpValidationFactorExtractor getValidationFactorExtractor();
}

