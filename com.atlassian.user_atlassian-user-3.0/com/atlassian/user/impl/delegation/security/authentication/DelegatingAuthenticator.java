/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.profiling.UtilTimerStack
 *  org.apache.log4j.Logger
 */
package com.atlassian.user.impl.delegation.security.authentication;

import com.atlassian.user.EntityException;
import com.atlassian.user.UserManager;
import com.atlassian.user.impl.delegation.repository.DelegatingRepository;
import com.atlassian.user.repository.RepositoryIdentifier;
import com.atlassian.user.security.authentication.Authenticator;
import com.atlassian.util.profiling.UtilTimerStack;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DelegatingAuthenticator
implements Authenticator {
    private static final Logger log = Logger.getLogger(DelegatingAuthenticator.class);
    private final List<Authenticator> authenticators;
    private final UserManager userManager;

    public DelegatingAuthenticator(UserManager userManager, List<Authenticator> authenticators) {
        this.userManager = userManager;
        this.authenticators = authenticators;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean authenticate(String username, String password) throws EntityException {
        if (UtilTimerStack.isActive()) {
            UtilTimerStack.push((String)(this.getClass().getName() + "_delegating_authenticate(" + username + ")"));
        }
        try {
            RepositoryIdentifier repository = this.userManager.getRepository(this.userManager.getUser(username));
            List<Authenticator> authenticators = this.getAuthenticatorsForRepository(repository);
            if (authenticators.isEmpty()) {
                log.error((Object)("Failed to find authenticator for user " + username + " from repository " + repository));
                boolean bl = false;
                return bl;
            }
            for (Authenticator authenticator : authenticators) {
                if (!this.authenticate(authenticator, username, password)) continue;
                boolean bl = true;
                return bl;
            }
        }
        finally {
            if (UtilTimerStack.isActive()) {
                UtilTimerStack.pop((String)(this.getClass().getName() + "_delegating_authenticate(" + username + ")"));
            }
        }
        return false;
    }

    private boolean authenticate(Authenticator authenticator, String username, String password) {
        try {
            return authenticator.authenticate(username, password);
        }
        catch (EntityException e) {
            log.error((Object)(authenticator.getRepository() + ": " + e.getMessage()));
            return false;
        }
    }

    private List<Authenticator> getAuthenticatorsForRepository(RepositoryIdentifier repository) {
        LinkedList<Authenticator> result = new LinkedList<Authenticator>();
        for (Authenticator authenticator : this.authenticators) {
            if (!authenticator.getRepository().equals(repository)) continue;
            result.add(authenticator);
        }
        return result;
    }

    @Override
    public RepositoryIdentifier getRepository() {
        ArrayList<RepositoryIdentifier> repositories = new ArrayList<RepositoryIdentifier>(this.authenticators.size());
        for (Authenticator authenticator : this.authenticators) {
            repositories.add(authenticator.getRepository());
        }
        return new DelegatingRepository(repositories);
    }

    public List getAuthenticators() {
        return this.authenticators;
    }
}

