/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.strategies;

import org.apache.axis.AxisFault;
import org.apache.axis.Handler;
import org.apache.axis.HandlerIterationStrategy;
import org.apache.axis.MessageContext;

public class WSDLGenStrategy
implements HandlerIterationStrategy {
    public void visit(Handler handler, MessageContext msgContext) throws AxisFault {
        handler.generateWSDL(msgContext);
    }
}

