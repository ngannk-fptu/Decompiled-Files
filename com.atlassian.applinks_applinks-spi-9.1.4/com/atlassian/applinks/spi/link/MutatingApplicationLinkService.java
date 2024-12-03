/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.ApplicationType
 *  com.atlassian.applinks.api.CredentialsRequiredException
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.sal.api.net.ResponseException
 */
package com.atlassian.applinks.spi.link;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.ApplicationType;
import com.atlassian.applinks.api.CredentialsRequiredException;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.spi.auth.AuthenticationConfigurationException;
import com.atlassian.applinks.spi.auth.AuthenticationScenario;
import com.atlassian.applinks.spi.link.ApplicationLinkDetails;
import com.atlassian.applinks.spi.link.MutableApplicationLink;
import com.atlassian.applinks.spi.link.ReciprocalActionException;
import com.atlassian.applinks.spi.manifest.ManifestNotFoundException;
import com.atlassian.sal.api.net.ResponseException;
import java.net.URI;

public interface MutatingApplicationLinkService
extends ApplicationLinkService {
    public MutableApplicationLink addApplicationLink(ApplicationId var1, ApplicationType var2, ApplicationLinkDetails var3);

    public void deleteReciprocatedApplicationLink(ApplicationLink var1) throws ReciprocalActionException, CredentialsRequiredException;

    public void deleteApplicationLink(ApplicationLink var1);

    public MutableApplicationLink getApplicationLink(ApplicationId var1) throws TypeNotInstalledException;

    public void makePrimary(ApplicationId var1) throws TypeNotInstalledException;

    public void setSystem(ApplicationId var1, boolean var2) throws TypeNotInstalledException;

    public void changeApplicationId(ApplicationId var1, ApplicationId var2) throws TypeNotInstalledException;

    public ApplicationLink createApplicationLink(ApplicationType var1, ApplicationLinkDetails var2) throws ManifestNotFoundException;

    @Deprecated
    public void createReciprocalLink(URI var1, URI var2, String var3, String var4) throws ReciprocalActionException;

    public boolean isAdminUserInRemoteApplication(URI var1, String var2, String var3) throws ResponseException;

    public void configureAuthenticationForApplicationLink(ApplicationLink var1, AuthenticationScenario var2, String var3, String var4) throws AuthenticationConfigurationException;

    public URI createSelfLinkFor(ApplicationId var1);

    public boolean isNameInUse(String var1, ApplicationId var2);
}

