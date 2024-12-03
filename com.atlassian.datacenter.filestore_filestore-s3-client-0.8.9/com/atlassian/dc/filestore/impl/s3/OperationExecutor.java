/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.dc.filestore.impl.s3;

import com.atlassian.dc.filestore.impl.s3.ClientOperation;
import java.io.IOException;

public interface OperationExecutor {
    public <T> T performOperation(ClientOperation<T> var1) throws IOException;
}

