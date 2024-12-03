/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.identity;

import org.apache.xerces.impl.xpath.XPath;
import org.apache.xerces.util.IntStack;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.XSTypeDefinition;

public class XPathMatcher {
    protected static final boolean DEBUG_ALL = false;
    protected static final boolean DEBUG_METHODS = false;
    protected static final boolean DEBUG_METHODS2 = false;
    protected static final boolean DEBUG_METHODS3 = false;
    protected static final boolean DEBUG_MATCH = false;
    protected static final boolean DEBUG_STACK = false;
    protected static final boolean DEBUG_ANY = false;
    protected static final int MATCHED = 1;
    protected static final int MATCHED_ATTRIBUTE = 3;
    protected static final int MATCHED_DESCENDANT = 5;
    protected static final int MATCHED_DESCENDANT_PREVIOUS = 13;
    private final XPath.LocationPath[] fLocationPaths;
    private final int[] fMatched;
    protected Object fMatchedString;
    private final IntStack[] fStepIndexes;
    private final int[] fCurrentStep;
    private final int[] fNoMatchDepth;
    final QName fQName = new QName();

    public XPathMatcher(XPath xPath) {
        this.fLocationPaths = xPath.getLocationPaths();
        this.fStepIndexes = new IntStack[this.fLocationPaths.length];
        for (int i = 0; i < this.fStepIndexes.length; ++i) {
            this.fStepIndexes[i] = new IntStack();
        }
        this.fCurrentStep = new int[this.fLocationPaths.length];
        this.fNoMatchDepth = new int[this.fLocationPaths.length];
        this.fMatched = new int[this.fLocationPaths.length];
    }

    public boolean isMatched() {
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            if ((this.fMatched[i] & 1) != 1 || (this.fMatched[i] & 0xD) == 13 || this.fNoMatchDepth[i] != 0 && (this.fMatched[i] & 5) != 5) continue;
            return true;
        }
        return false;
    }

    protected void handleContent(XSTypeDefinition xSTypeDefinition, boolean bl, Object object, short s, ShortList shortList) {
    }

    protected void matched(Object object, short s, ShortList shortList, boolean bl) {
    }

    public void startDocumentFragment() {
        this.fMatchedString = null;
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            this.fStepIndexes[i].clear();
            this.fCurrentStep[i] = 0;
            this.fNoMatchDepth[i] = 0;
            this.fMatched[i] = 0;
        }
    }

    public void startElement(QName qName, XMLAttributes xMLAttributes) {
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            XPath.NodeTest nodeTest;
            boolean bl;
            int n = this.fCurrentStep[i];
            this.fStepIndexes[i].push(n);
            if ((this.fMatched[i] & 5) == 1 || this.fNoMatchDepth[i] > 0) {
                int n2 = i;
                this.fNoMatchDepth[n2] = this.fNoMatchDepth[n2] + 1;
                continue;
            }
            if ((this.fMatched[i] & 5) == 5) {
                this.fMatched[i] = 13;
            }
            XPath.Step[] stepArray = this.fLocationPaths[i].steps;
            while (this.fCurrentStep[i] < stepArray.length && stepArray[this.fCurrentStep[i]].axis.type == 3) {
                int n3 = i;
                this.fCurrentStep[n3] = this.fCurrentStep[n3] + 1;
            }
            if (this.fCurrentStep[i] == stepArray.length) {
                this.fMatched[i] = 1;
                continue;
            }
            int n4 = this.fCurrentStep[i];
            while (this.fCurrentStep[i] < stepArray.length && stepArray[this.fCurrentStep[i]].axis.type == 4) {
                int n5 = i;
                this.fCurrentStep[n5] = this.fCurrentStep[n5] + 1;
            }
            boolean bl2 = bl = this.fCurrentStep[i] > n4;
            if (this.fCurrentStep[i] == stepArray.length) {
                int n6 = i;
                this.fNoMatchDepth[n6] = this.fNoMatchDepth[n6] + 1;
                continue;
            }
            if ((this.fCurrentStep[i] == n || this.fCurrentStep[i] > n4) && stepArray[this.fCurrentStep[i]].axis.type == 1) {
                XPath.Step step = stepArray[this.fCurrentStep[i]];
                nodeTest = step.nodeTest;
                if (!XPathMatcher.matches(nodeTest, qName)) {
                    if (this.fCurrentStep[i] > n4) {
                        this.fCurrentStep[i] = n4;
                        continue;
                    }
                    int n7 = i;
                    this.fNoMatchDepth[n7] = this.fNoMatchDepth[n7] + 1;
                    continue;
                }
                int n8 = i;
                this.fCurrentStep[n8] = this.fCurrentStep[n8] + 1;
            }
            if (this.fCurrentStep[i] == stepArray.length) {
                if (bl) {
                    this.fCurrentStep[i] = n4;
                    this.fMatched[i] = 5;
                    continue;
                }
                this.fMatched[i] = 1;
                continue;
            }
            if (this.fCurrentStep[i] >= stepArray.length || stepArray[this.fCurrentStep[i]].axis.type != 2) continue;
            int n9 = xMLAttributes.getLength();
            if (n9 > 0) {
                nodeTest = stepArray[this.fCurrentStep[i]].nodeTest;
                for (int j = 0; j < n9; ++j) {
                    int n10;
                    xMLAttributes.getName(j, this.fQName);
                    if (!XPathMatcher.matches(nodeTest, this.fQName)) continue;
                    int n11 = i;
                    this.fCurrentStep[n11] = this.fCurrentStep[n11] + 1;
                    if (this.fCurrentStep[i] != stepArray.length) break;
                    this.fMatched[i] = 3;
                    for (n10 = 0; n10 < i && (this.fMatched[n10] & 1) != 1; ++n10) {
                    }
                    if (n10 != i) break;
                    AttributePSVI attributePSVI = (AttributePSVI)xMLAttributes.getAugmentations(j).getItem("ATTRIBUTE_PSVI");
                    this.fMatchedString = attributePSVI.getActualNormalizedValue();
                    this.matched(this.fMatchedString, attributePSVI.getActualNormalizedValueType(), attributePSVI.getItemValueTypes(), false);
                    break;
                }
            }
            if ((this.fMatched[i] & 1) == 1) continue;
            if (this.fCurrentStep[i] > n4) {
                this.fCurrentStep[i] = n4;
                continue;
            }
            int n12 = i;
            this.fNoMatchDepth[n12] = this.fNoMatchDepth[n12] + 1;
        }
    }

    public void endElement(QName qName, XSTypeDefinition xSTypeDefinition, boolean bl, Object object, short s, ShortList shortList) {
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            int n;
            this.fCurrentStep[i] = this.fStepIndexes[i].pop();
            if (this.fNoMatchDepth[i] > 0) {
                int n2 = i;
                this.fNoMatchDepth[n2] = this.fNoMatchDepth[n2] - 1;
                continue;
            }
            for (n = 0; n < i && (this.fMatched[n] & 1) != 1; ++n) {
            }
            if (n < i || this.fMatched[n] == 0) continue;
            if ((this.fMatched[n] & 3) == 3) {
                this.fMatched[i] = 0;
                continue;
            }
            this.handleContent(xSTypeDefinition, bl, object, s, shortList);
            this.fMatched[i] = 0;
        }
    }

    public String toString() {
        StringBuffer stringBuffer = new StringBuffer();
        String string = super.toString();
        int n = string.lastIndexOf(46);
        if (n != -1) {
            string = string.substring(n + 1);
        }
        stringBuffer.append(string);
        for (int i = 0; i < this.fLocationPaths.length; ++i) {
            stringBuffer.append('[');
            XPath.Step[] stepArray = this.fLocationPaths[i].steps;
            for (int j = 0; j < stepArray.length; ++j) {
                if (j == this.fCurrentStep[i]) {
                    stringBuffer.append('^');
                }
                stringBuffer.append(stepArray[j].toString());
                if (j >= stepArray.length - 1) continue;
                stringBuffer.append('/');
            }
            if (this.fCurrentStep[i] == stepArray.length) {
                stringBuffer.append('^');
            }
            stringBuffer.append(']');
            stringBuffer.append(',');
        }
        return stringBuffer.toString();
    }

    private String normalize(String string) {
        StringBuffer stringBuffer = new StringBuffer();
        int n = string.length();
        block3: for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            switch (c) {
                case '\n': {
                    stringBuffer.append("\\n");
                    continue block3;
                }
                default: {
                    stringBuffer.append(c);
                }
            }
        }
        return stringBuffer.toString();
    }

    private static boolean matches(XPath.NodeTest nodeTest, QName qName) {
        if (nodeTest.type == 1) {
            return nodeTest.name.equals(qName);
        }
        if (nodeTest.type == 4) {
            return nodeTest.name.uri == qName.uri;
        }
        return true;
    }
}

