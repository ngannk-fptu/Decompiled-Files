/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.util.http.trust;

import com.atlassian.confluence.util.http.HttpResponse;
import com.atlassian.confluence.util.http.trust.TrustedConnectionStatus;
import com.atlassian.confluence.util.http.trust.TrustedConnectionStatusBuilder;
import java.util.Arrays;
import java.util.LinkedList;

@Deprecated(forRemoval=true)
public class DefaultTrustedConnectionStatusBuilder
implements TrustedConnectionStatusBuilder {
    private static final String UNRECOGNIZED_APP = "Unrecognized application";
    private static final String UNRECOGNIZED_USER = "Unrecognized user";

    @Override
    public TrustedConnectionStatus getTrustedConnectionStatus(HttpResponse httpResponse) {
        boolean trustSupported;
        if (httpResponse == null) {
            throw new IllegalArgumentException("httpResponse must not be null");
        }
        String[] statusHeaders = httpResponse.getHeaders("X-Seraph-Trusted-App-Status");
        boolean bl = trustSupported = statusHeaders != null && statusHeaders.length != 0;
        if (!trustSupported) {
            return TrustedConnectionStatus.UNSUPPORTED;
        }
        String[] headers = httpResponse.getHeaders("X-Seraph-Trusted-App-Error");
        if (headers == null || headers.length == 0) {
            return TrustedConnectionStatus.SUCCESS;
        }
        LinkedList<String> trustedConnectionErrors = new LinkedList<String>();
        trustedConnectionErrors.addAll(Arrays.asList(headers));
        boolean appRecognized = !trustedConnectionErrors.contains(UNRECOGNIZED_APP);
        boolean userRecognized = appRecognized && !trustedConnectionErrors.contains(UNRECOGNIZED_USER);
        return new TrustedConnectionStatus(userRecognized, appRecognized, true, trustedConnectionErrors, true);
    }
}

