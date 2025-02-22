/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.transformer;

import javax.xml.transform.TransformerException;
import org.apache.xalan.serialize.SerializerUtils;
import org.apache.xml.dtm.DTM;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.XMLString;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class ClonerToResultTree {
    public static void cloneToResultTree(int node, int nodeType, DTM dtm, SerializationHandler rth, boolean shouldCloneAttributes) throws TransformerException {
        try {
            switch (nodeType) {
                case 3: {
                    dtm.dispatchCharactersEvents(node, (ContentHandler)rth, false);
                    break;
                }
                case 9: 
                case 11: {
                    break;
                }
                case 1: {
                    String ns = dtm.getNamespaceURI(node);
                    if (ns == null) {
                        ns = "";
                    }
                    String localName = dtm.getLocalName(node);
                    rth.startElement(ns, localName, dtm.getNodeNameX(node));
                    if (shouldCloneAttributes) {
                        SerializerUtils.addAttributes(rth, node);
                        SerializerUtils.processNSDecls(rth, node, nodeType, dtm);
                    }
                    break;
                }
                case 4: {
                    rth.startCDATA();
                    dtm.dispatchCharactersEvents(node, (ContentHandler)rth, false);
                    rth.endCDATA();
                    break;
                }
                case 2: {
                    SerializerUtils.addAttribute(rth, node);
                    break;
                }
                case 13: {
                    SerializerUtils.processNSDecls(rth, node, 13, dtm);
                    break;
                }
                case 8: {
                    XMLString xstr = dtm.getStringValue(node);
                    xstr.dispatchAsComment((LexicalHandler)rth);
                    break;
                }
                case 5: {
                    rth.entityReference(dtm.getNodeNameX(node));
                    break;
                }
                case 7: {
                    rth.processingInstruction(dtm.getNodeNameX(node), dtm.getNodeValue(node));
                    break;
                }
                default: {
                    throw new TransformerException("Can't clone node: " + dtm.getNodeName(node));
                }
            }
        }
        catch (SAXException se) {
            throw new TransformerException(se);
        }
    }
}

