/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.ssi;

import java.io.PrintWriter;
import org.apache.catalina.ssi.SSIMediator;
import org.apache.catalina.ssi.SSIStopProcessingException;

public interface SSICommand {
    public long process(SSIMediator var1, String var2, String[] var3, String[] var4, PrintWriter var5) throws SSIStopProcessingException;
}

