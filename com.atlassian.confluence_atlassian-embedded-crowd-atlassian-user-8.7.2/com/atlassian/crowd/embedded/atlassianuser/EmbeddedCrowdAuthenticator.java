/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.CrowdService
 *  com.atlassian.crowd.exception.ExpiredCredentialException
 *  com.atlassian.crowd.exception.FailedAuthenticationException
 *  com.atlassian.crowd.exception.InactiveAccountException
 *  com.atlassian.crowd.exception.runtime.UserNotFoundException
 *  com.atlassian.user.repository.RepositoryIdentifier
 *  com.atlassian.user.security.authentication.Authenticator
 *  com.atlassian.user.util.Assert
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.embedded.atlassianuser;

import com.atlassian.crowd.embedded.api.CrowdService;
import com.atlassian.crowd.exception.ExpiredCredentialException;
import com.atlassian.crowd.exception.FailedAuthenticationException;
import com.atlassian.crowd.exception.InactiveAccountException;
import com.atlassian.crowd.exception.runtime.UserNotFoundException;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.security.authentication.Authenticator;
import com.atlassian.user.util.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
public final class EmbeddedCrowdAuthenticator
implements Authenticator {
    private static final Logger log = LoggerFactory.getLogger(EmbeddedCrowdAuthenticator.class);
    private final RepositoryIdentifier repositoryIdentifier;
    private final CrowdService crowdService;

    public EmbeddedCrowdAuthenticator(RepositoryIdentifier repositoryIdentifier, CrowdService crowdService) {
        this.repositoryIdentifier = repositoryIdentifier;
        this.crowdService = crowdService;
    }

    public boolean authenticate(String username, String password) {
        log.debug("Authenticating user '{}' by password", (Object)username);
        try {
            boolean result = this.crowdService.authenticate(username, password) != null;
            Assert.isTrue((boolean)result, (String)"Result must always be true if an exception was not thrown");
            log.debug("Authentication successful for user '{}'", (Object)username);
            return result;
        }
        catch (UserNotFoundException e) {
            log.debug("Authentication failed for username '{}' because user could not be found.", (Object)username);
            return false;
        }
        catch (InactiveAccountException e) {
            log.debug("Authentication failed for username '{}' because user is inactive.", (Object)username);
            return false;
        }
        catch (ExpiredCredentialException e) {
            log.debug("Authentication failed for username '{}' because the credentials have expired.", (Object)username);
            return false;
        }
        catch (FailedAuthenticationException e) {
            log.debug("Authentication failed for username '{}' because the password was incorrect.", (Object)username);
            return false;
        }
    }

    public RepositoryIdentifier getRepository() {
        return this.repositoryIdentifier;
    }
}

