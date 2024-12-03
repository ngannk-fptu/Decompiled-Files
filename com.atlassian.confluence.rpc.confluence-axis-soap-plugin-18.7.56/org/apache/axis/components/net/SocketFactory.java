/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.components.net;

import java.net.Socket;
import org.apache.axis.components.net.BooleanHolder;

public interface SocketFactory {
    public Socket create(String var1, int var2, StringBuffer var3, BooleanHolder var4) throws Exception;
}

