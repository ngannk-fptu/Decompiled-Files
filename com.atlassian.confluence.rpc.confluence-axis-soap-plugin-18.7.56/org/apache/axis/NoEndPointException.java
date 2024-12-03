/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis;

import org.apache.axis.AxisFault;
import org.apache.axis.utils.Messages;

public class NoEndPointException
extends AxisFault {
    public NoEndPointException() {
        super("Server.NoEndpoint", Messages.getMessage("noEndpoint"), null, null);
    }
}

