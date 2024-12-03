/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sr;

import com.ctc.wstx.sr.InputElementStack;
import javax.xml.stream.XMLStreamException;

public interface NsDefaultProvider {
    public boolean mayHaveNsDefaults(String var1, String var2);

    public void checkNsDefaults(InputElementStack var1) throws XMLStreamException;
}

