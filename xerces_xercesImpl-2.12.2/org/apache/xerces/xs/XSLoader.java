/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xs;

import org.apache.xerces.xs.LSInputList;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSModel;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.ls.LSInput;

public interface XSLoader {
    public DOMConfiguration getConfig();

    public XSModel loadURIList(StringList var1);

    public XSModel loadInputList(LSInputList var1);

    public XSModel loadURI(String var1);

    public XSModel load(LSInput var1);
}

