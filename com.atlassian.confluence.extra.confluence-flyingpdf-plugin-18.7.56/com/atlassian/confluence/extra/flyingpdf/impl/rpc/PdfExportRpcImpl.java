/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rpc.RemoteException
 */
package com.atlassian.confluence.extra.flyingpdf.impl.rpc;

import com.atlassian.confluence.extra.flyingpdf.impl.rpc.PdfExportRpcDelegatorImpl;
import com.atlassian.confluence.extra.flyingpdf.rpc.PdfExportRpc;
import com.atlassian.confluence.rpc.RemoteException;

public class PdfExportRpcImpl
implements PdfExportRpc {
    private final PdfExportRpcDelegatorImpl data;

    public PdfExportRpcImpl(PdfExportRpcDelegatorImpl data) {
        this.data = data;
    }

    @Override
    public String exportSpace(String token, String spaceKey) throws RemoteException {
        return this.data.exportSpace(spaceKey);
    }

    public String login(String username, String password) {
        throw new UnsupportedOperationException("Should be handled in an interceptor.");
    }

    public boolean logout(String token) {
        throw new UnsupportedOperationException("Should be handled in an interceptor.");
    }
}

