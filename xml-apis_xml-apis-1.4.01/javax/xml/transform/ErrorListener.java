/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.transform;

import javax.xml.transform.TransformerException;

public interface ErrorListener {
    public void warning(TransformerException var1) throws TransformerException;

    public void error(TransformerException var1) throws TransformerException;

    public void fatalError(TransformerException var1) throws TransformerException;
}

