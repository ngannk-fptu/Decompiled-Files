/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 */
package com.atlassian.plugins.rest.common.multipart;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.io.Serializable;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

public class FileSizeLimitExceededException
extends WebApplicationException {
    private static final int PAYLOAD_TOO_LARGE = 413;
    private static final int NOT_FOUND = Response.Status.NOT_FOUND.getStatusCode();
    public static final String LEGACY_MODE_KEY = "atlassian.rest.filesize.exceeded.statuscode.legacy.enabled";
    @Deprecated
    @VisibleForTesting
    static final Supplier<Boolean> legacyMode = Suppliers.memoize((Supplier)new LegacyModeSupplier());

    private static int getStatusCode() {
        return (Boolean)legacyMode.get() != false ? NOT_FOUND : 413;
    }

    public FileSizeLimitExceededException(String message) {
        super(Response.status(FileSizeLimitExceededException.getStatusCode()).entity(message).build());
    }

    private static final class LegacyModeSupplier
    implements Supplier<Boolean>,
    Serializable {
        private LegacyModeSupplier() {
        }

        public Boolean get() {
            return Boolean.getBoolean(FileSizeLimitExceededException.LEGACY_MODE_KEY);
        }
    }
}

