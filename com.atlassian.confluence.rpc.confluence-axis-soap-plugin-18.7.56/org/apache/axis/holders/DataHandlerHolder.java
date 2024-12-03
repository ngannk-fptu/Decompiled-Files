/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axis.holders;

import javax.activation.DataHandler;
import javax.xml.rpc.holders.Holder;

public final class DataHandlerHolder
implements Holder {
    public DataHandler value;

    public DataHandlerHolder() {
    }

    public DataHandlerHolder(DataHandler value) {
        this.value = value;
    }
}

