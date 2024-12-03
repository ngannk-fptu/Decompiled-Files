/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.io.IOException;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSSerializerFilter;

public interface DOM3Serializer {
    public void serializeDOM3(Node var1) throws IOException;

    public void setErrorHandler(DOMErrorHandler var1);

    public DOMErrorHandler getErrorHandler();

    public void setNodeFilter(LSSerializerFilter var1);

    public LSSerializerFilter getNodeFilter();

    public void setNewLine(char[] var1);
}

