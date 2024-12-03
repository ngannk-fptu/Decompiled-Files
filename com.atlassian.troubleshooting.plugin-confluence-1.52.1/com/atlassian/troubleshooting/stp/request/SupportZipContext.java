/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.troubleshooting.stp.request;

import com.atlassian.troubleshooting.stp.request.SupportZipCreationRequest;
import java.util.Optional;
import java.util.concurrent.Callable;

public final class SupportZipContext {
    private static final ThreadLocal<SupportZipCreationRequest> ZIP_CREATION_REQUEST = new ThreadLocal();

    private SupportZipContext() {
    }

    public static <T> Callable<T> wrap(SupportZipCreationRequest request, Callable<T> callable) throws Exception {
        return () -> {
            if (ZIP_CREATION_REQUEST.get() != null) {
                throw new IllegalStateException("Support zip creation request context is already initialized and can't be overridden");
            }
            try {
                ZIP_CREATION_REQUEST.set(request);
                Object v = callable.call();
                return v;
            }
            finally {
                ZIP_CREATION_REQUEST.remove();
            }
        };
    }

    public static Optional<SupportZipCreationRequest> getSupportZipRequest() {
        return Optional.ofNullable(ZIP_CREATION_REQUEST.get());
    }
}

