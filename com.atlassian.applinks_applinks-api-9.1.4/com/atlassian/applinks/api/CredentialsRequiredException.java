/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.applinks.api;

import com.atlassian.applinks.api.AuthorisationURIGenerator;
import java.net.URI;

public class CredentialsRequiredException
extends Exception
implements AuthorisationURIGenerator {
    private final AuthorisationURIGenerator authorisationURIGenerator;

    public CredentialsRequiredException(AuthorisationURIGenerator authorisationURIGenerator, String message) {
        super(message);
        this.authorisationURIGenerator = authorisationURIGenerator;
    }

    @Override
    public URI getAuthorisationURI() {
        return this.authorisationURIGenerator.getAuthorisationURI();
    }

    @Override
    public URI getAuthorisationURI(URI callback) {
        return this.authorisationURIGenerator.getAuthorisationURI(callback);
    }
}

