/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom3.as;

import org.apache.xerces.dom3.as.NodeEditAS;

public interface CharacterDataEditAS
extends NodeEditAS {
    public boolean getIsWhitespaceOnly();

    public boolean canSetData(int var1, int var2);

    public boolean canAppendData(String var1);

    public boolean canReplaceData(int var1, int var2, String var3);

    public boolean canInsertData(int var1, String var2);

    public boolean canDeleteData(int var1, int var2);
}

