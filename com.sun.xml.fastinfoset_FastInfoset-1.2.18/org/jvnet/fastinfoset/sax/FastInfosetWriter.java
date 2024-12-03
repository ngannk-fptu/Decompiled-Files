/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset.sax;

import org.jvnet.fastinfoset.FastInfosetSerializer;
import org.jvnet.fastinfoset.sax.EncodingAlgorithmContentHandler;
import org.jvnet.fastinfoset.sax.ExtendedContentHandler;
import org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler;
import org.jvnet.fastinfoset.sax.RestrictedAlphabetContentHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public interface FastInfosetWriter
extends ContentHandler,
LexicalHandler,
EncodingAlgorithmContentHandler,
PrimitiveTypeContentHandler,
RestrictedAlphabetContentHandler,
ExtendedContentHandler,
FastInfosetSerializer {
}

