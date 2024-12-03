/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Message
 */
package org.apache.axis.wsdl.symbolTable;

import javax.wsdl.Message;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;

public class MessageEntry
extends SymTabEntry {
    private Message message;

    public MessageEntry(Message message) {
        super(message.getQName());
        this.message = message;
    }

    public Message getMessage() {
        return this.message;
    }
}

