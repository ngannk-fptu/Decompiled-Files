/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;

public interface XMLEntityHandler {
    public void startEntity(String var1, XMLResourceIdentifier var2, String var3, Augmentations var4) throws XNIException;

    public void endEntity(String var1, Augmentations var2) throws XNIException;
}

