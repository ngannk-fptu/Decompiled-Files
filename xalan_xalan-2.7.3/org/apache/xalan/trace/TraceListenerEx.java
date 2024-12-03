/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.trace;

import javax.xml.transform.TransformerException;
import org.apache.xalan.trace.EndSelectionEvent;
import org.apache.xalan.trace.TraceListener;

public interface TraceListenerEx
extends TraceListener {
    public void selectEnd(EndSelectionEvent var1) throws TransformerException;
}

