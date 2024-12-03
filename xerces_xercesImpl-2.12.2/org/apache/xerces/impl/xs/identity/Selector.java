/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.identity;

import org.apache.xerces.impl.xpath.XPath;
import org.apache.xerces.impl.xpath.XPathException;
import org.apache.xerces.impl.xs.identity.Field;
import org.apache.xerces.impl.xs.identity.FieldActivator;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.xs.identity.XPathMatcher;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSTypeDefinition;

public class Selector {
    protected final XPath fXPath;
    protected final IdentityConstraint fIdentityConstraint;
    protected IdentityConstraint fIDConstraint;

    public Selector(XPath xPath, IdentityConstraint identityConstraint) {
        this.fXPath = xPath;
        this.fIdentityConstraint = identityConstraint;
    }

    public org.apache.xerces.impl.xpath.XPath getXPath() {
        return this.fXPath;
    }

    public IdentityConstraint getIDConstraint() {
        return this.fIdentityConstraint;
    }

    public XPathMatcher createMatcher(FieldActivator fieldActivator, int n) {
        return new Matcher(this.fXPath, fieldActivator, n);
    }

    public String toString() {
        return this.fXPath.toString();
    }

    public class Matcher
    extends XPathMatcher {
        protected final FieldActivator fFieldActivator;
        protected final int fInitialDepth;
        protected int fElementDepth;
        protected int fMatchedDepth;

        public Matcher(XPath xPath, FieldActivator fieldActivator, int n) {
            super(xPath);
            this.fFieldActivator = fieldActivator;
            this.fInitialDepth = n;
        }

        @Override
        public void startDocumentFragment() {
            super.startDocumentFragment();
            this.fElementDepth = 0;
            this.fMatchedDepth = -1;
        }

        @Override
        public void startElement(QName qName, XMLAttributes xMLAttributes) {
            super.startElement(qName, xMLAttributes);
            ++this.fElementDepth;
            if (this.isMatched()) {
                this.fMatchedDepth = this.fElementDepth;
                this.fFieldActivator.startValueScopeFor(Selector.this.fIdentityConstraint, this.fInitialDepth);
                int n = Selector.this.fIdentityConstraint.getFieldCount();
                for (int i = 0; i < n; ++i) {
                    Field field = Selector.this.fIdentityConstraint.getFieldAt(i);
                    XPathMatcher xPathMatcher = this.fFieldActivator.activateField(field, this.fInitialDepth);
                    xPathMatcher.startElement(qName, xMLAttributes);
                }
            }
        }

        @Override
        public void endElement(QName qName, XSTypeDefinition xSTypeDefinition, boolean bl, Object object, short s, ShortList shortList) {
            super.endElement(qName, xSTypeDefinition, bl, object, s, shortList);
            if (this.fElementDepth-- == this.fMatchedDepth) {
                this.fMatchedDepth = -1;
                this.fFieldActivator.endValueScopeFor(Selector.this.fIdentityConstraint, this.fInitialDepth);
            }
        }

        public IdentityConstraint getIdentityConstraint() {
            return Selector.this.fIdentityConstraint;
        }

        public int getInitialDepth() {
            return this.fInitialDepth;
        }
    }

    public static class XPath
    extends org.apache.xerces.impl.xpath.XPath {
        public XPath(String string, SymbolTable symbolTable, NamespaceContext namespaceContext) throws XPathException {
            super(XPath.normalize(string), symbolTable, namespaceContext);
            for (int i = 0; i < this.fLocationPaths.length; ++i) {
                XPath.Axis axis = this.fLocationPaths[i].steps[this.fLocationPaths[i].steps.length - 1].axis;
                if (axis.type != 2) continue;
                throw new XPathException("c-selector-xpath");
            }
        }

        private static String normalize(String string) {
            StringBuffer stringBuffer = new StringBuffer(string.length() + 5);
            int n = -1;
            while (true) {
                if (!XMLChar.trim(string).startsWith("/") && !XMLChar.trim(string).startsWith(".")) {
                    stringBuffer.append("./");
                }
                if ((n = string.indexOf(124)) == -1) break;
                stringBuffer.append(string.substring(0, n + 1));
                string = string.substring(n + 1, string.length());
            }
            stringBuffer.append(string);
            return stringBuffer.toString();
        }
    }
}

