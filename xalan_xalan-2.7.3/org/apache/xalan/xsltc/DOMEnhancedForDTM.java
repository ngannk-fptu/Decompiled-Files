/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc;

import org.apache.xalan.xsltc.DOM;

public interface DOMEnhancedForDTM
extends DOM {
    public short[] getMapping(String[] var1, String[] var2, int[] var3);

    public int[] getReverseMapping(String[] var1, String[] var2, int[] var3);

    public short[] getNamespaceMapping(String[] var1);

    public short[] getReverseNamespaceMapping(String[] var1);

    public String getDocumentURI();

    public void setDocumentURI(String var1);

    public int getExpandedTypeID2(int var1);

    public boolean hasDOMSource();

    public int getElementById(String var1);
}

