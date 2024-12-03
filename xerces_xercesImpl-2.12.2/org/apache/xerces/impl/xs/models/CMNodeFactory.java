/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.models;

import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.dtd.models.CMNode;
import org.apache.xerces.impl.xs.models.XSCMBinOp;
import org.apache.xerces.impl.xs.models.XSCMLeaf;
import org.apache.xerces.impl.xs.models.XSCMRepeatingLeaf;
import org.apache.xerces.impl.xs.models.XSCMUniOp;
import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;

public class CMNodeFactory {
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final boolean DEBUG = false;
    private static final int MULTIPLICITY = 1;
    private int nodeCount = 0;
    private int maxNodeLimit;
    private XMLErrorReporter fErrorReporter;
    private SecurityManager fSecurityManager = null;

    public void reset(XMLComponentManager xMLComponentManager) {
        this.fErrorReporter = (XMLErrorReporter)xMLComponentManager.getProperty(ERROR_REPORTER);
        try {
            this.fSecurityManager = (SecurityManager)xMLComponentManager.getProperty(SECURITY_MANAGER);
            this.reset();
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fSecurityManager = null;
        }
    }

    public void reset() {
        if (this.fSecurityManager != null) {
            this.maxNodeLimit = this.fSecurityManager.getMaxOccurNodeLimit() * 1;
        }
    }

    public CMNode getCMLeafNode(int n, Object object, int n2, int n3) {
        this.nodeCountCheck();
        return new XSCMLeaf(n, object, n2, n3);
    }

    public CMNode getCMRepeatingLeafNode(int n, Object object, int n2, int n3, int n4, int n5) {
        this.nodeCountCheck();
        return new XSCMRepeatingLeaf(n, object, n2, n3, n4, n5);
    }

    public CMNode getCMUniOpNode(int n, CMNode cMNode) {
        this.nodeCountCheck();
        return new XSCMUniOp(n, cMNode);
    }

    public CMNode getCMBinOpNode(int n, CMNode cMNode, CMNode cMNode2) {
        this.nodeCountCheck();
        return new XSCMBinOp(n, cMNode, cMNode2);
    }

    public void nodeCountCheck() {
        if (this.fSecurityManager != null && this.nodeCount++ > this.maxNodeLimit) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "maxOccurLimit", new Object[]{new Integer(this.maxNodeLimit)}, (short)2);
            this.nodeCount = 0;
        }
    }

    public void resetNodeCount() {
        this.nodeCount = 0;
    }

    public void setProperty(String string, Object object) throws XMLConfigurationException {
        if (string.startsWith("http://apache.org/xml/properties/")) {
            int n = string.length() - "http://apache.org/xml/properties/".length();
            if (n == "security-manager".length() && string.endsWith("security-manager")) {
                this.fSecurityManager = (SecurityManager)object;
                this.maxNodeLimit = this.fSecurityManager != null ? this.fSecurityManager.getMaxOccurNodeLimit() * 1 : 0;
                return;
            }
            if (n == "internal/error-reporter".length() && string.endsWith("internal/error-reporter")) {
                this.fErrorReporter = (XMLErrorReporter)object;
                return;
            }
        }
    }
}

