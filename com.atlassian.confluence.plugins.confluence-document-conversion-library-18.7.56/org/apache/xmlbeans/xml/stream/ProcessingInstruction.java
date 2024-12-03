/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.xml.stream;

import org.apache.xmlbeans.xml.stream.XMLEvent;

public interface ProcessingInstruction
extends XMLEvent {
    public String getTarget();

    public String getData();
}

