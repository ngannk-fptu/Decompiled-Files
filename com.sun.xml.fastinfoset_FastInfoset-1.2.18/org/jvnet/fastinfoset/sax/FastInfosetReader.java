/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.fastinfoset.sax;

import java.io.IOException;
import java.io.InputStream;
import org.jvnet.fastinfoset.FastInfosetException;
import org.jvnet.fastinfoset.FastInfosetParser;
import org.jvnet.fastinfoset.sax.EncodingAlgorithmContentHandler;
import org.jvnet.fastinfoset.sax.PrimitiveTypeContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public interface FastInfosetReader
extends XMLReader,
FastInfosetParser {
    public static final String ENCODING_ALGORITHM_CONTENT_HANDLER_PROPERTY = "http://jvnet.org/fastinfoset/sax/properties/encoding-algorithm-content-handler";
    public static final String PRIMITIVE_TYPE_CONTENT_HANDLER_PROPERTY = "http://jvnet.org/fastinfoset/sax/properties/primitive-type-content-handler";

    public void parse(InputStream var1) throws IOException, FastInfosetException, SAXException;

    public void setLexicalHandler(LexicalHandler var1);

    public LexicalHandler getLexicalHandler();

    public void setDeclHandler(DeclHandler var1);

    public DeclHandler getDeclHandler();

    public void setEncodingAlgorithmContentHandler(EncodingAlgorithmContentHandler var1);

    public EncodingAlgorithmContentHandler getEncodingAlgorithmContentHandler();

    public void setPrimitiveTypeContentHandler(PrimitiveTypeContentHandler var1);

    public PrimitiveTypeContentHandler getPrimitiveTypeContentHandler();
}

