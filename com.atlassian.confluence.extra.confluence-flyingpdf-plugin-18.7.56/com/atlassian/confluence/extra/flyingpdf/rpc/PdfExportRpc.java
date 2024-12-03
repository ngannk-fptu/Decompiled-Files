/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.rpc.RemoteException
 *  com.atlassian.confluence.rpc.SecureRpc
 */
package com.atlassian.confluence.extra.flyingpdf.rpc;

import com.atlassian.confluence.rpc.RemoteException;
import com.atlassian.confluence.rpc.SecureRpc;

public interface PdfExportRpc
extends SecureRpc {
    public String exportSpace(String var1, String var2) throws RemoteException;
}

