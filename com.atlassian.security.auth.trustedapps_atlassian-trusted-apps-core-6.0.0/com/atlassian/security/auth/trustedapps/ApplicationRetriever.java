/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.security.auth.trustedapps;

import com.atlassian.security.auth.trustedapps.Application;

@Deprecated
public interface ApplicationRetriever {
    public Application getApplication() throws RetrievalException;

    public static class RemoteSystemNotFoundException
    extends RetrievalException {
        RemoteSystemNotFoundException(Exception cause) {
            super(cause);
        }
    }

    public static class InvalidApplicationDetailsException
    extends RetrievalException {
        InvalidApplicationDetailsException(Exception cause) {
            super(cause);
        }
    }

    public static class ApplicationNotFoundException
    extends RetrievalException {
        ApplicationNotFoundException(String message) {
            super(message);
        }

        ApplicationNotFoundException(Exception cause) {
            super(cause);
        }
    }

    public static abstract class RetrievalException
    extends Exception {
        RetrievalException(String message) {
            super(message);
        }

        RetrievalException(Exception cause) {
            super(cause);
        }
    }
}

