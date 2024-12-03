/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.core.internal;

import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.interceptor.ExecutionAttribute;
import software.amazon.awssdk.core.interceptor.SdkExecutionAttribute;

@SdkInternalApi
public final class InternalCoreExecutionAttribute
extends SdkExecutionAttribute {
    public static final ExecutionAttribute<Integer> EXECUTION_ATTEMPT = new ExecutionAttribute("SdkInternalExecutionAttempt");

    private InternalCoreExecutionAttribute() {
    }
}

