/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.spi;

import org.apache.jackrabbit.spi.NodeId;
import org.apache.jackrabbit.spi.QValue;

public interface QueryResultRow {
    public NodeId getNodeId(String var1);

    public double getScore(String var1);

    public QValue[] getValues();
}

