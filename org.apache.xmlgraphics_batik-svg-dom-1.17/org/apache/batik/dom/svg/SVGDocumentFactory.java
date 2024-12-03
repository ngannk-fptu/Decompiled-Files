/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.dom.util.DocumentFactory
 *  org.w3c.dom.svg.SVGDocument
 */
package org.apache.batik.dom.svg;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.apache.batik.dom.util.DocumentFactory;
import org.w3c.dom.svg.SVGDocument;

public interface SVGDocumentFactory
extends DocumentFactory {
    public SVGDocument createSVGDocument(String var1) throws IOException;

    public SVGDocument createSVGDocument(String var1, InputStream var2) throws IOException;

    public SVGDocument createSVGDocument(String var1, Reader var2) throws IOException;
}

