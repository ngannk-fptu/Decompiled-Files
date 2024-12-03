/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.resource;

import org.xml.sax.InputSource;

public interface Resource {
    public InputSource getResourceInputSource();

    public long getResourceLoadTimeStamp();
}

