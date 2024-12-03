/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.trace;

import org.apache.xalan.trace.ExtensionEvent;
import org.apache.xalan.trace.TraceListenerEx2;

public interface TraceListenerEx3
extends TraceListenerEx2 {
    public void extension(ExtensionEvent var1);

    public void extensionEnd(ExtensionEvent var1);
}

