/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd;

import org.apache.xerces.impl.dtd.XMLDTDValidator;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XNIException;

public class XMLNSDTDValidator
extends XMLDTDValidator {
    private final QName fAttributeQName = new QName();

    @Override
    protected final void startNamespaceScope(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        int n;
        String string;
        String string2;
        this.fNamespaceContext.pushContext();
        if (qName.prefix == XMLSymbols.PREFIX_XMLNS) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementXMLNSPrefix", new Object[]{qName.rawname}, (short)2);
        }
        int n2 = xMLAttributes.getLength();
        for (int i = 0; i < n2; ++i) {
            String string3 = xMLAttributes.getLocalName(i);
            string2 = xMLAttributes.getPrefix(i);
            if (string2 != XMLSymbols.PREFIX_XMLNS && (string2 != XMLSymbols.EMPTY_STRING || string3 != XMLSymbols.PREFIX_XMLNS)) continue;
            string = this.fSymbolTable.addSymbol(xMLAttributes.getValue(i));
            if (string2 == XMLSymbols.PREFIX_XMLNS && string3 == XMLSymbols.PREFIX_XMLNS) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[]{xMLAttributes.getQName(i)}, (short)2);
            }
            if (string == NamespaceContext.XMLNS_URI) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXMLNS", new Object[]{xMLAttributes.getQName(i)}, (short)2);
            }
            if (string3 == XMLSymbols.PREFIX_XML) {
                if (string != NamespaceContext.XML_URI) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[]{xMLAttributes.getQName(i)}, (short)2);
                }
            } else if (string == NamespaceContext.XML_URI) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "CantBindXML", new Object[]{xMLAttributes.getQName(i)}, (short)2);
            }
            String string4 = string2 = string3 != XMLSymbols.PREFIX_XMLNS ? string3 : XMLSymbols.EMPTY_STRING;
            if (string == XMLSymbols.EMPTY_STRING && string3 != XMLSymbols.PREFIX_XMLNS) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "EmptyPrefixedAttName", new Object[]{xMLAttributes.getQName(i)}, (short)2);
                continue;
            }
            this.fNamespaceContext.declarePrefix(string2, string.length() != 0 ? string : null);
        }
        String string5 = qName.prefix != null ? qName.prefix : XMLSymbols.EMPTY_STRING;
        qName.uri = this.fNamespaceContext.getURI(string5);
        if (qName.prefix == null && qName.uri != null) {
            qName.prefix = XMLSymbols.EMPTY_STRING;
        }
        if (qName.prefix != null && qName.uri == null) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "ElementPrefixUnbound", new Object[]{qName.prefix, qName.rawname}, (short)2);
        }
        for (n = 0; n < n2; ++n) {
            xMLAttributes.getName(n, this.fAttributeQName);
            string2 = this.fAttributeQName.prefix != null ? this.fAttributeQName.prefix : XMLSymbols.EMPTY_STRING;
            string = this.fAttributeQName.rawname;
            if (string == XMLSymbols.PREFIX_XMLNS) {
                this.fAttributeQName.uri = this.fNamespaceContext.getURI(XMLSymbols.PREFIX_XMLNS);
                xMLAttributes.setName(n, this.fAttributeQName);
                continue;
            }
            if (string2 == XMLSymbols.EMPTY_STRING) continue;
            this.fAttributeQName.uri = this.fNamespaceContext.getURI(string2);
            if (this.fAttributeQName.uri == null) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributePrefixUnbound", new Object[]{qName.rawname, string, string2}, (short)2);
            }
            xMLAttributes.setName(n, this.fAttributeQName);
        }
        n = xMLAttributes.getLength();
        for (int i = 0; i < n - 1; ++i) {
            string = xMLAttributes.getURI(i);
            if (string == null || string == NamespaceContext.XMLNS_URI) continue;
            String string6 = xMLAttributes.getLocalName(i);
            for (int j = i + 1; j < n; ++j) {
                String string7 = xMLAttributes.getLocalName(j);
                String string8 = xMLAttributes.getURI(j);
                if (string6 != string7 || string != string8) continue;
                this.fErrorReporter.reportError("http://www.w3.org/TR/1999/REC-xml-names-19990114", "AttributeNSNotUnique", new Object[]{qName.rawname, string6, string}, (short)2);
            }
        }
    }

    @Override
    protected void endNamespaceScope(QName qName, Augmentations augmentations, boolean bl) throws XNIException {
        String string = qName.prefix != null ? qName.prefix : XMLSymbols.EMPTY_STRING;
        qName.uri = this.fNamespaceContext.getURI(string);
        if (qName.uri != null) {
            qName.prefix = string;
        }
        if (this.fDocumentHandler != null && !bl) {
            this.fDocumentHandler.endElement(qName, augmentations);
        }
        this.fNamespaceContext.popContext();
    }
}

