/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis;

import org.apache.axis.Chain;
import org.apache.axis.Handler;

public interface TargetedChain
extends Chain {
    public Handler getRequestHandler();

    public Handler getPivotHandler();

    public Handler getResponseHandler();
}

