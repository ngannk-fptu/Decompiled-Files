/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.bridge;

import java.util.List;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.IMessageHandler;

public interface IMessageHolder
extends IMessageHandler {
    public static final boolean ORGREATER = true;
    public static final boolean EQUAL = false;

    public boolean hasAnyMessage(IMessage.Kind var1, boolean var2);

    public int numMessages(IMessage.Kind var1, boolean var2);

    public IMessage[] getMessages(IMessage.Kind var1, boolean var2);

    public List<IMessage> getUnmodifiableListView();

    public void clearMessages() throws UnsupportedOperationException;
}

