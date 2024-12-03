/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.sax;

import org.xml.sax.ContentHandler;

public interface ContentFilter
extends ContentHandler {
    public void setParent(ContentHandler var1);

    public ContentHandler getParent();
}

