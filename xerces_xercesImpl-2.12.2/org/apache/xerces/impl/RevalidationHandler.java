/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl;

import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.parser.XMLDocumentFilter;

public interface RevalidationHandler
extends XMLDocumentFilter {
    public boolean characterData(String var1, Augmentations var2);
}

