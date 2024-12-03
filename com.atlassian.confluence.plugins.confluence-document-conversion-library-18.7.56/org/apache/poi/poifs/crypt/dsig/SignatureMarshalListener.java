/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.poifs.crypt.dsig;

import org.apache.poi.poifs.crypt.dsig.SignatureInfo;
import org.w3c.dom.Document;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

public interface SignatureMarshalListener {
    public void handleElement(SignatureInfo var1, Document var2, EventTarget var3, EventListener var4);
}

