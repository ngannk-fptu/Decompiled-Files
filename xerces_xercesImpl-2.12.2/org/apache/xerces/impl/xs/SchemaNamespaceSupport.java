/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.xs.opti.ElementImpl;
import org.apache.xerces.util.NamespaceSupport;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class SchemaNamespaceSupport
extends NamespaceSupport {
    private SchemaRootContext fSchemaRootContext = null;

    public SchemaNamespaceSupport(Element element, SymbolTable symbolTable) {
        Document document;
        if (element != null && !(element instanceof ElementImpl) && (document = element.getOwnerDocument()) != null && element != document.getDocumentElement()) {
            this.fSchemaRootContext = new SchemaRootContext(element, symbolTable);
        }
    }

    public SchemaNamespaceSupport(SchemaNamespaceSupport schemaNamespaceSupport) {
        this.fSchemaRootContext = schemaNamespaceSupport.fSchemaRootContext;
        this.fNamespaceSize = schemaNamespaceSupport.fNamespaceSize;
        if (this.fNamespace.length < this.fNamespaceSize) {
            this.fNamespace = new String[this.fNamespaceSize];
        }
        System.arraycopy(schemaNamespaceSupport.fNamespace, 0, this.fNamespace, 0, this.fNamespaceSize);
        this.fCurrentContext = schemaNamespaceSupport.fCurrentContext;
        if (this.fContext.length <= this.fCurrentContext) {
            this.fContext = new int[this.fCurrentContext + 1];
        }
        System.arraycopy(schemaNamespaceSupport.fContext, 0, this.fContext, 0, this.fCurrentContext + 1);
    }

    public void setEffectiveContext(String[] stringArray) {
        if (stringArray == null || stringArray.length == 0) {
            return;
        }
        this.pushContext();
        int n = this.fNamespaceSize + stringArray.length;
        if (this.fNamespace.length < n) {
            String[] stringArray2 = new String[n];
            System.arraycopy(this.fNamespace, 0, stringArray2, 0, this.fNamespace.length);
            this.fNamespace = stringArray2;
        }
        System.arraycopy(stringArray, 0, this.fNamespace, this.fNamespaceSize, stringArray.length);
        this.fNamespaceSize = n;
    }

    public String[] getEffectiveLocalContext() {
        int n;
        int n2;
        String[] stringArray = null;
        if (this.fCurrentContext >= 3 && (n2 = this.fNamespaceSize - (n = this.fContext[3])) > 0) {
            stringArray = new String[n2];
            System.arraycopy(this.fNamespace, n, stringArray, 0, n2);
        }
        return stringArray;
    }

    public void makeGlobal() {
        if (this.fCurrentContext >= 3) {
            this.fCurrentContext = 3;
            this.fNamespaceSize = this.fContext[3];
        }
    }

    @Override
    public String getURI(String string) {
        String string2 = super.getURI(string);
        if (string2 == null && this.fSchemaRootContext != null) {
            if (!this.fSchemaRootContext.fDOMContextBuilt) {
                this.fSchemaRootContext.fillNamespaceContext();
                this.fSchemaRootContext.fDOMContextBuilt = true;
            }
            if (this.fSchemaRootContext.fNamespaceSize > 0 && !this.containsPrefix(string)) {
                string2 = this.fSchemaRootContext.getURI(string);
            }
        }
        return string2;
    }

    static final class SchemaRootContext {
        String[] fNamespace = new String[32];
        int fNamespaceSize = 0;
        boolean fDOMContextBuilt = false;
        private final Element fSchemaRoot;
        private final SymbolTable fSymbolTable;
        private final QName fAttributeQName = new QName();

        SchemaRootContext(Element element, SymbolTable symbolTable) {
            this.fSchemaRoot = element;
            this.fSymbolTable = symbolTable;
        }

        void fillNamespaceContext() {
            if (this.fSchemaRoot != null) {
                for (Node node = this.fSchemaRoot.getParentNode(); node != null; node = node.getParentNode()) {
                    if (1 != node.getNodeType()) continue;
                    NamedNodeMap namedNodeMap = node.getAttributes();
                    int n = namedNodeMap.getLength();
                    for (int i = 0; i < n; ++i) {
                        Attr attr = (Attr)namedNodeMap.item(i);
                        String string = attr.getValue();
                        if (string == null) {
                            string = XMLSymbols.EMPTY_STRING;
                        }
                        this.fillQName(this.fAttributeQName, attr);
                        if (this.fAttributeQName.uri != NamespaceContext.XMLNS_URI) continue;
                        if (this.fAttributeQName.prefix == XMLSymbols.PREFIX_XMLNS) {
                            this.declarePrefix(this.fAttributeQName.localpart, string.length() != 0 ? this.fSymbolTable.addSymbol(string) : null);
                            continue;
                        }
                        this.declarePrefix(XMLSymbols.EMPTY_STRING, string.length() != 0 ? this.fSymbolTable.addSymbol(string) : null);
                    }
                }
            }
        }

        String getURI(String string) {
            for (int i = 0; i < this.fNamespaceSize; i += 2) {
                if (this.fNamespace[i] != string) continue;
                return this.fNamespace[i + 1];
            }
            return null;
        }

        private void declarePrefix(String string, String string2) {
            if (this.fNamespaceSize == this.fNamespace.length) {
                String[] stringArray = new String[this.fNamespaceSize * 2];
                System.arraycopy(this.fNamespace, 0, stringArray, 0, this.fNamespaceSize);
                this.fNamespace = stringArray;
            }
            this.fNamespace[this.fNamespaceSize++] = string;
            this.fNamespace[this.fNamespaceSize++] = string2;
        }

        private void fillQName(QName qName, Node node) {
            String string = node.getPrefix();
            String string2 = node.getLocalName();
            String string3 = node.getNodeName();
            String string4 = node.getNamespaceURI();
            qName.prefix = string != null ? this.fSymbolTable.addSymbol(string) : XMLSymbols.EMPTY_STRING;
            qName.localpart = string2 != null ? this.fSymbolTable.addSymbol(string2) : XMLSymbols.EMPTY_STRING;
            qName.rawname = string3 != null ? this.fSymbolTable.addSymbol(string3) : XMLSymbols.EMPTY_STRING;
            qName.uri = string4 != null && string4.length() > 0 ? this.fSymbolTable.addSymbol(string4) : null;
        }
    }
}

