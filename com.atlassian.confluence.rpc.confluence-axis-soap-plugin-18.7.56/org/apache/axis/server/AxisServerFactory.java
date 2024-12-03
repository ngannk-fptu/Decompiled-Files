/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.server;

import java.util.Map;
import org.apache.axis.AxisFault;
import org.apache.axis.server.AxisServer;

public interface AxisServerFactory {
    public AxisServer getServer(Map var1) throws AxisFault;
}

