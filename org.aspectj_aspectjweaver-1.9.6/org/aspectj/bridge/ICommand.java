/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.bridge;

import org.aspectj.bridge.IMessageHandler;

public interface ICommand {
    public boolean runCommand(String[] var1, IMessageHandler var2);

    public boolean repeatCommand(IMessageHandler var1);
}

