/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom3.as;

import java.io.OutputStream;
import org.apache.xerces.dom3.as.ASModel;
import org.w3c.dom.ls.LSSerializer;

public interface DOMASWriter
extends LSSerializer {
    public void writeASModel(OutputStream var1, ASModel var2) throws Exception;
}

