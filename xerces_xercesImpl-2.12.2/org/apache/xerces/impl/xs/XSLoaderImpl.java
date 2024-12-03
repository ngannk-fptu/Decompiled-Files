/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.impl.xs.XSAnnotationImpl;
import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.XSAttributeGroupDecl;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSGroupDecl;
import org.apache.xerces.impl.xs.XSNotationDecl;
import org.apache.xerces.impl.xs.util.XSGrammarPool;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.XSGrammar;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xs.LSInputList;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.ls.LSInput;

public final class XSLoaderImpl
implements XSLoader,
DOMConfiguration {
    private final XSGrammarPool fGrammarPool = new XSGrammarMerger();
    private final XMLSchemaLoader fSchemaLoader = new XMLSchemaLoader();

    public XSLoaderImpl() {
        this.fSchemaLoader.setProperty("http://apache.org/xml/properties/internal/grammar-pool", this.fGrammarPool);
    }

    @Override
    public DOMConfiguration getConfig() {
        return this;
    }

    @Override
    public XSModel loadURIList(StringList stringList) {
        int n = stringList.getLength();
        try {
            this.fGrammarPool.clear();
            for (int i = 0; i < n; ++i) {
                this.fSchemaLoader.loadGrammar(new XMLInputSource(null, stringList.item(i), null));
            }
            return this.fGrammarPool.toXSModel();
        }
        catch (Exception exception) {
            this.fSchemaLoader.reportDOMFatalError(exception);
            return null;
        }
    }

    @Override
    public XSModel loadInputList(LSInputList lSInputList) {
        int n = lSInputList.getLength();
        try {
            this.fGrammarPool.clear();
            for (int i = 0; i < n; ++i) {
                this.fSchemaLoader.loadGrammar(this.fSchemaLoader.dom2xmlInputSource(lSInputList.item(i)));
            }
            return this.fGrammarPool.toXSModel();
        }
        catch (Exception exception) {
            this.fSchemaLoader.reportDOMFatalError(exception);
            return null;
        }
    }

    @Override
    public XSModel loadURI(String string) {
        try {
            this.fGrammarPool.clear();
            return ((XSGrammar)this.fSchemaLoader.loadGrammar(new XMLInputSource(null, string, null))).toXSModel();
        }
        catch (Exception exception) {
            this.fSchemaLoader.reportDOMFatalError(exception);
            return null;
        }
    }

    @Override
    public XSModel load(LSInput lSInput) {
        try {
            this.fGrammarPool.clear();
            return ((XSGrammar)this.fSchemaLoader.loadGrammar(this.fSchemaLoader.dom2xmlInputSource(lSInput))).toXSModel();
        }
        catch (Exception exception) {
            this.fSchemaLoader.reportDOMFatalError(exception);
            return null;
        }
    }

    @Override
    public void setParameter(String string, Object object) throws DOMException {
        this.fSchemaLoader.setParameter(string, object);
    }

    @Override
    public Object getParameter(String string) throws DOMException {
        return this.fSchemaLoader.getParameter(string);
    }

    @Override
    public boolean canSetParameter(String string, Object object) {
        return this.fSchemaLoader.canSetParameter(string, object);
    }

    @Override
    public DOMStringList getParameterNames() {
        return this.fSchemaLoader.getParameterNames();
    }

    private static final class XSGrammarMerger
    extends XSGrammarPool {
        @Override
        public void putGrammar(Grammar grammar) {
            SchemaGrammar schemaGrammar = this.toSchemaGrammar(super.getGrammar(grammar.getGrammarDescription()));
            if (schemaGrammar != null) {
                SchemaGrammar schemaGrammar2 = this.toSchemaGrammar(grammar);
                if (schemaGrammar2 != null) {
                    this.mergeSchemaGrammars(schemaGrammar, schemaGrammar2);
                }
            } else {
                super.putGrammar(grammar);
            }
        }

        private SchemaGrammar toSchemaGrammar(Grammar grammar) {
            return grammar instanceof SchemaGrammar ? (SchemaGrammar)grammar : null;
        }

        private void mergeSchemaGrammars(SchemaGrammar schemaGrammar, SchemaGrammar schemaGrammar2) {
            XSObject xSObject;
            int n;
            XSNamedMap xSNamedMap = schemaGrammar2.getComponents((short)2);
            int n2 = xSNamedMap.getLength();
            for (n = 0; n < n2; ++n) {
                xSObject = (XSElementDecl)xSNamedMap.item(n);
                if (schemaGrammar.getGlobalElementDecl(((XSElementDecl)xSObject).getName()) != null) continue;
                schemaGrammar.addGlobalElementDecl((XSElementDecl)xSObject);
            }
            xSNamedMap = schemaGrammar2.getComponents((short)1);
            n2 = xSNamedMap.getLength();
            for (n = 0; n < n2; ++n) {
                xSObject = (XSAttributeDecl)xSNamedMap.item(n);
                if (schemaGrammar.getGlobalAttributeDecl(((XSAttributeDecl)xSObject).getName()) != null) continue;
                schemaGrammar.addGlobalAttributeDecl((XSAttributeDecl)xSObject);
            }
            xSNamedMap = schemaGrammar2.getComponents((short)3);
            n2 = xSNamedMap.getLength();
            for (n = 0; n < n2; ++n) {
                xSObject = (XSTypeDefinition)xSNamedMap.item(n);
                if (schemaGrammar.getGlobalTypeDecl(xSObject.getName()) != null) continue;
                schemaGrammar.addGlobalTypeDecl((XSTypeDefinition)xSObject);
            }
            xSNamedMap = schemaGrammar2.getComponents((short)5);
            n2 = xSNamedMap.getLength();
            for (n = 0; n < n2; ++n) {
                xSObject = (XSAttributeGroupDecl)xSNamedMap.item(n);
                if (schemaGrammar.getGlobalAttributeGroupDecl(((XSAttributeGroupDecl)xSObject).getName()) != null) continue;
                schemaGrammar.addGlobalAttributeGroupDecl((XSAttributeGroupDecl)xSObject);
            }
            xSNamedMap = schemaGrammar2.getComponents((short)7);
            n2 = xSNamedMap.getLength();
            for (n = 0; n < n2; ++n) {
                xSObject = (XSGroupDecl)xSNamedMap.item(n);
                if (schemaGrammar.getGlobalGroupDecl(((XSGroupDecl)xSObject).getName()) != null) continue;
                schemaGrammar.addGlobalGroupDecl((XSGroupDecl)xSObject);
            }
            xSNamedMap = schemaGrammar2.getComponents((short)11);
            n2 = xSNamedMap.getLength();
            for (n = 0; n < n2; ++n) {
                xSObject = (XSNotationDecl)xSNamedMap.item(n);
                if (schemaGrammar.getGlobalNotationDecl(((XSNotationDecl)xSObject).getName()) != null) continue;
                schemaGrammar.addGlobalNotationDecl((XSNotationDecl)xSObject);
            }
            XSObjectList xSObjectList = schemaGrammar2.getAnnotations();
            n2 = xSObjectList.getLength();
            for (int i = 0; i < n2; ++i) {
                schemaGrammar.addAnnotation((XSAnnotationImpl)xSObjectList.item(i));
            }
        }

        @Override
        public boolean containsGrammar(XMLGrammarDescription xMLGrammarDescription) {
            return false;
        }

        @Override
        public Grammar getGrammar(XMLGrammarDescription xMLGrammarDescription) {
            return null;
        }

        @Override
        public Grammar retrieveGrammar(XMLGrammarDescription xMLGrammarDescription) {
            return null;
        }

        @Override
        public Grammar[] retrieveInitialGrammarSet(String string) {
            return new Grammar[0];
        }
    }
}

