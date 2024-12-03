/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.security.auth.trustedapps.TransportErrorMessage
 *  com.atlassian.security.auth.trustedapps.TrustedApplicationUtils
 */
package com.atlassian.confluence.util.http.trust;

import com.atlassian.security.auth.trustedapps.TransportErrorMessage;
import com.atlassian.security.auth.trustedapps.TrustedApplicationUtils;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

@Deprecated(forRemoval=true)
public class TrustedConnectionStatus {
    public static final TrustedConnectionStatus UNSUPPORTED = new TrustedConnectionStatus(false, false, false, Collections.emptyList(), false);
    public static final TrustedConnectionStatus SUCCESS = new TrustedConnectionStatus(true, true, false, Collections.emptyList(), true);
    private final boolean userRecognized;
    private final boolean appRecognized;
    private final boolean trustedConnectionError;
    private final List<String> trustedConnectionErrors;
    private final boolean trustSupported;

    public TrustedConnectionStatus(boolean userRecognized, boolean appRecognized, boolean trustedConnectionError, List<String> trustedConnectionErrors, boolean trustSupported) {
        this.userRecognized = userRecognized;
        this.appRecognized = appRecognized;
        this.trustedConnectionError = trustedConnectionError;
        this.trustedConnectionErrors = Collections.unmodifiableList(new LinkedList<String>(trustedConnectionErrors));
        this.trustSupported = trustSupported;
    }

    public boolean isUserRecognized() {
        return this.userRecognized;
    }

    public boolean isAppRecognized() {
        return this.appRecognized;
    }

    public boolean isTrustedConnectionError() {
        return this.trustedConnectionError;
    }

    public List<TransportErrorMessage> getTrustedTransportErrorMessages() {
        return this.trustedConnectionErrors.stream().map(TrustedApplicationUtils::parseError).collect(Collectors.toList());
    }

    public boolean isTrustSupported() {
        return this.trustSupported;
    }

    public String toString() {
        if (!this.trustSupported) {
            return "Trusted connection not supported";
        }
        if (!this.trustedConnectionError) {
            return "Trusted connection successful";
        }
        return "Trusted connection errors: " + this.trustedConnectionErrors;
    }
}

