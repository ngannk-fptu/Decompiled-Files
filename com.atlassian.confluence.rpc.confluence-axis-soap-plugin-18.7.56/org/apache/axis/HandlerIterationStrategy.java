/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis;

import org.apache.axis.AxisFault;
import org.apache.axis.Handler;
import org.apache.axis.MessageContext;

public interface HandlerIterationStrategy {
    public void visit(Handler var1, MessageContext var2) throws AxisFault;
}

