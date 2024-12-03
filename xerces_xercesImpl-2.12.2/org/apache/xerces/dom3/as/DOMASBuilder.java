/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom3.as;

import org.apache.xerces.dom3.as.ASModel;
import org.apache.xerces.dom3.as.DOMASException;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;

public interface DOMASBuilder
extends LSParser {
    public ASModel getAbstractSchema();

    public void setAbstractSchema(ASModel var1);

    public ASModel parseASURI(String var1) throws DOMASException, Exception;

    public ASModel parseASInputSource(LSInput var1) throws DOMASException, Exception;
}

