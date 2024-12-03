/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.scanner.dtd;

import com.ctc.wstx.shaded.msv_core.scanner.dtd.InputEntity;
import java.util.EventListener;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public interface DTDEventListener
extends EventListener {
    public static final short CONTENT_MODEL_EMPTY = 0;
    public static final short CONTENT_MODEL_ANY = 1;
    public static final short CONTENT_MODEL_MIXED = 2;
    public static final short CONTENT_MODEL_CHILDREN = 3;
    public static final short USE_NORMAL = 0;
    public static final short USE_IMPLIED = 1;
    public static final short USE_FIXED = 2;
    public static final short USE_REQUIRED = 3;
    public static final short CHOICE = 0;
    public static final short SEQUENCE = 1;
    public static final short OCCURENCE_ZERO_OR_MORE = 0;
    public static final short OCCURENCE_ONE_OR_MORE = 1;
    public static final short OCCURENCE_ZERO_OR_ONE = 2;
    public static final short OCCURENCE_ONCE = 3;

    public void setDocumentLocator(Locator var1);

    public void processingInstruction(String var1, String var2) throws SAXException;

    public void notationDecl(String var1, String var2, String var3) throws SAXException;

    public void unparsedEntityDecl(String var1, String var2, String var3, String var4) throws SAXException;

    public void internalGeneralEntityDecl(String var1, String var2) throws SAXException;

    public void externalGeneralEntityDecl(String var1, String var2, String var3) throws SAXException;

    public void internalParameterEntityDecl(String var1, String var2) throws SAXException;

    public void externalParameterEntityDecl(String var1, String var2, String var3) throws SAXException;

    public void startDTD(InputEntity var1) throws SAXException;

    public void endDTD() throws SAXException;

    public void comment(String var1) throws SAXException;

    public void characters(char[] var1, int var2, int var3) throws SAXException;

    public void ignorableWhitespace(char[] var1, int var2, int var3) throws SAXException;

    public void startCDATA() throws SAXException;

    public void endCDATA() throws SAXException;

    public void fatalError(SAXParseException var1) throws SAXException;

    public void error(SAXParseException var1) throws SAXException;

    public void warning(SAXParseException var1) throws SAXException;

    public void startContentModel(String var1, short var2) throws SAXException;

    public void endContentModel(String var1, short var2) throws SAXException;

    public void attributeDecl(String var1, String var2, String var3, String[] var4, short var5, String var6) throws SAXException;

    public void childElement(String var1, short var2) throws SAXException;

    public void mixedElement(String var1) throws SAXException;

    public void startModelGroup() throws SAXException;

    public void endModelGroup(short var1) throws SAXException;

    public void connector(short var1) throws SAXException;
}

