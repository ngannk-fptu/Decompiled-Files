/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.identity;

import org.apache.xerces.impl.xpath.XPath;
import org.apache.xerces.impl.xpath.XPathException;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.xs.identity.ValueStore;
import org.apache.xerces.impl.xs.identity.XPathMatcher;
import org.apache.xerces.impl.xs.util.ShortListImpl;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSComplexTypeDefinition;
import org.apache.xerces.xs.XSTypeDefinition;

public class Field {
    protected final XPath fXPath;
    protected final IdentityConstraint fIdentityConstraint;

    public Field(XPath xPath, IdentityConstraint identityConstraint) {
        this.fXPath = xPath;
        this.fIdentityConstraint = identityConstraint;
    }

    public org.apache.xerces.impl.xpath.XPath getXPath() {
        return this.fXPath;
    }

    public IdentityConstraint getIdentityConstraint() {
        return this.fIdentityConstraint;
    }

    public XPathMatcher createMatcher(ValueStore valueStore) {
        return new Matcher(this.fXPath, valueStore);
    }

    public String toString() {
        return this.fXPath.toString();
    }

    protected class Matcher
    extends XPathMatcher {
        protected final ValueStore fStore;
        protected boolean fMayMatch;

        public Matcher(XPath xPath, ValueStore valueStore) {
            super(xPath);
            this.fMayMatch = true;
            this.fStore = valueStore;
        }

        @Override
        protected void matched(Object object, short s, ShortList shortList, boolean bl) {
            super.matched(object, s, shortList, bl);
            if (bl && Field.this.fIdentityConstraint.getCategory() == 1) {
                String string = "KeyMatchesNillable";
                this.fStore.reportError(string, new Object[]{Field.this.fIdentityConstraint.getElementName(), Field.this.fIdentityConstraint.getIdentityConstraintName()});
            }
            this.fStore.addValue(Field.this, this.fMayMatch, object, this.convertToPrimitiveKind(s), this.convertToPrimitiveKind(shortList));
            this.fMayMatch = false;
        }

        private short convertToPrimitiveKind(short s) {
            if (s <= 20) {
                return s;
            }
            if (s <= 29) {
                return 2;
            }
            if (s <= 42) {
                return 4;
            }
            return s;
        }

        private ShortList convertToPrimitiveKind(ShortList shortList) {
            if (shortList != null) {
                short s;
                int n;
                int n2 = shortList.getLength();
                for (n = 0; n < n2 && (s = shortList.item(n)) == this.convertToPrimitiveKind(s); ++n) {
                }
                if (n != n2) {
                    short[] sArray = new short[n2];
                    for (int i = 0; i < n; ++i) {
                        sArray[i] = shortList.item(i);
                    }
                    while (n < n2) {
                        sArray[n] = this.convertToPrimitiveKind(shortList.item(n));
                        ++n;
                    }
                    return new ShortListImpl(sArray, sArray.length);
                }
            }
            return shortList;
        }

        @Override
        protected void handleContent(XSTypeDefinition xSTypeDefinition, boolean bl, Object object, short s, ShortList shortList) {
            if (xSTypeDefinition == null || xSTypeDefinition.getTypeCategory() == 15 && ((XSComplexTypeDefinition)xSTypeDefinition).getContentType() != 1) {
                this.fStore.reportError("cvc-id.3", new Object[]{Field.this.fIdentityConstraint.getName(), Field.this.fIdentityConstraint.getElementName()});
            }
            this.fMatchedString = object;
            this.matched(this.fMatchedString, s, shortList, bl);
        }
    }

    public static class XPath
    extends org.apache.xerces.impl.xpath.XPath {
        public XPath(String string, SymbolTable symbolTable, NamespaceContext namespaceContext) throws XPathException {
            super(XPath.fixupXPath(string), symbolTable, namespaceContext);
            for (int i = 0; i < this.fLocationPaths.length; ++i) {
                for (int j = 0; j < this.fLocationPaths[i].steps.length; ++j) {
                    XPath.Axis axis = this.fLocationPaths[i].steps[j].axis;
                    if (axis.type != 2 || j >= this.fLocationPaths[i].steps.length - 1) continue;
                    throw new XPathException("c-fields-xpaths");
                }
            }
        }

        private static String fixupXPath(String string) {
            int n = string.length();
            boolean bl = true;
            for (int i = 0; i < n; ++i) {
                char c = string.charAt(i);
                if (bl) {
                    if (XMLChar.isSpace(c)) continue;
                    if (c == '.' || c == '/') {
                        bl = false;
                        continue;
                    }
                    if (c == '|') continue;
                    return XPath.fixupXPath2(string, i, n);
                }
                if (c != '|') continue;
                bl = true;
            }
            return string;
        }

        private static String fixupXPath2(String string, int n, int n2) {
            int n3;
            StringBuffer stringBuffer = new StringBuffer(n2 + 2);
            for (n3 = 0; n3 < n; ++n3) {
                stringBuffer.append(string.charAt(n3));
            }
            stringBuffer.append("./");
            n3 = 0;
            while (n < n2) {
                char c = string.charAt(n);
                if (n3 != 0) {
                    if (!XMLChar.isSpace(c)) {
                        if (c == '.' || c == '/') {
                            n3 = 0;
                        } else if (c != '|') {
                            stringBuffer.append("./");
                            n3 = 0;
                        }
                    }
                } else if (c == '|') {
                    n3 = 1;
                }
                stringBuffer.append(c);
                ++n;
            }
            return stringBuffer.toString();
        }
    }
}

