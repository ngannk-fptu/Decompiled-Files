/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.services.secretsmanager.endpoints.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public class InnerParseError
extends RuntimeException {
    private static final long serialVersionUID = -7808901449079805477L;

    public InnerParseError(String message) {
        super(message);
    }
}

