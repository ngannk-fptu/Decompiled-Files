/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.models;

import org.apache.xerces.impl.dtd.models.CMNode;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSDeclarationPool;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.models.CMNodeFactory;
import org.apache.xerces.impl.xs.models.XSAllCM;
import org.apache.xerces.impl.xs.models.XSCMBinOp;
import org.apache.xerces.impl.xs.models.XSCMLeaf;
import org.apache.xerces.impl.xs.models.XSCMUniOp;
import org.apache.xerces.impl.xs.models.XSCMValidator;
import org.apache.xerces.impl.xs.models.XSDFACM;
import org.apache.xerces.impl.xs.models.XSEmptyCM;

public class CMBuilder {
    private XSDeclarationPool fDeclPool = null;
    private static final XSEmptyCM fEmptyCM = new XSEmptyCM();
    private int fLeafCount;
    private int fParticleCount;
    private final CMNodeFactory fNodeFactory;

    public CMBuilder(CMNodeFactory cMNodeFactory) {
        this.fNodeFactory = cMNodeFactory;
    }

    public void setDeclPool(XSDeclarationPool xSDeclarationPool) {
        this.fDeclPool = xSDeclarationPool;
    }

    public XSCMValidator getContentModel(XSComplexTypeDecl xSComplexTypeDecl, boolean bl) {
        short s = xSComplexTypeDecl.getContentType();
        if (s == 1 || s == 0) {
            return null;
        }
        XSParticleDecl xSParticleDecl = (XSParticleDecl)xSComplexTypeDecl.getParticle();
        if (xSParticleDecl == null) {
            return fEmptyCM;
        }
        XSCMValidator xSCMValidator = null;
        xSCMValidator = xSParticleDecl.fType == 3 && ((XSModelGroupImpl)xSParticleDecl.fValue).fCompositor == 103 ? this.createAllCM(xSParticleDecl) : this.createDFACM(xSParticleDecl, bl);
        this.fNodeFactory.resetNodeCount();
        if (xSCMValidator == null) {
            xSCMValidator = fEmptyCM;
        }
        return xSCMValidator;
    }

    XSCMValidator createAllCM(XSParticleDecl xSParticleDecl) {
        if (xSParticleDecl.fMaxOccurs == 0) {
            return null;
        }
        XSModelGroupImpl xSModelGroupImpl = (XSModelGroupImpl)xSParticleDecl.fValue;
        XSAllCM xSAllCM = new XSAllCM(xSParticleDecl.fMinOccurs == 0, xSModelGroupImpl.fParticleCount);
        for (int i = 0; i < xSModelGroupImpl.fParticleCount; ++i) {
            xSAllCM.addElement((XSElementDecl)xSModelGroupImpl.fParticles[i].fValue, xSModelGroupImpl.fParticles[i].fMinOccurs == 0);
        }
        return xSAllCM;
    }

    XSCMValidator createDFACM(XSParticleDecl xSParticleDecl, boolean bl) {
        CMNode cMNode;
        this.fLeafCount = 0;
        this.fParticleCount = 0;
        CMNode cMNode2 = cMNode = this.useRepeatingLeafNodes(xSParticleDecl) ? this.buildCompactSyntaxTree(xSParticleDecl) : this.buildSyntaxTree(xSParticleDecl, bl);
        if (cMNode == null) {
            return null;
        }
        return new XSDFACM(cMNode, this.fLeafCount);
    }

    private CMNode buildSyntaxTree(XSParticleDecl xSParticleDecl, boolean bl) {
        int n = xSParticleDecl.fMaxOccurs;
        int n2 = xSParticleDecl.fMinOccurs;
        boolean bl2 = false;
        if (bl) {
            if (n2 > 1) {
                if (n > n2 || xSParticleDecl.getMaxOccursUnbounded()) {
                    n2 = 1;
                    bl2 = true;
                } else {
                    n2 = 2;
                    bl2 = true;
                }
            }
            if (n > 1) {
                n = 2;
                bl2 = true;
            }
        }
        short s = xSParticleDecl.fType;
        CMNode cMNode = null;
        if (s == 2 || s == 1) {
            cMNode = this.fNodeFactory.getCMLeafNode(xSParticleDecl.fType, xSParticleDecl.fValue, this.fParticleCount++, this.fLeafCount++);
            if ((cMNode = this.expandContentModel(cMNode, n2, n)) != null) {
                cMNode.setIsCompactUPAModel(bl2);
            }
        } else if (s == 3) {
            XSModelGroupImpl xSModelGroupImpl = (XSModelGroupImpl)xSParticleDecl.fValue;
            CMNode cMNode2 = null;
            int n3 = 0;
            for (int i = 0; i < xSModelGroupImpl.fParticleCount; ++i) {
                cMNode2 = this.buildSyntaxTree(xSModelGroupImpl.fParticles[i], bl);
                if (cMNode2 == null) continue;
                bl2 |= cMNode2.isCompactedForUPA();
                ++n3;
                cMNode = cMNode == null ? cMNode2 : this.fNodeFactory.getCMBinOpNode(xSModelGroupImpl.fCompositor, cMNode, cMNode2);
            }
            if (cMNode != null) {
                if (xSModelGroupImpl.fCompositor == 101 && n3 < xSModelGroupImpl.fParticleCount) {
                    cMNode = this.fNodeFactory.getCMUniOpNode(5, cMNode);
                }
                cMNode = this.expandContentModel(cMNode, n2, n);
                cMNode.setIsCompactUPAModel(bl2);
            }
        }
        return cMNode;
    }

    private CMNode expandContentModel(CMNode cMNode, int n, int n2) {
        CMNode cMNode2 = null;
        if (n == 1 && n2 == 1) {
            cMNode2 = cMNode;
        } else if (n == 0 && n2 == 1) {
            cMNode2 = this.fNodeFactory.getCMUniOpNode(5, cMNode);
        } else if (n == 0 && n2 == -1) {
            cMNode2 = this.fNodeFactory.getCMUniOpNode(4, cMNode);
        } else if (n == 1 && n2 == -1) {
            cMNode2 = this.fNodeFactory.getCMUniOpNode(6, cMNode);
        } else if (n2 == -1) {
            cMNode2 = this.fNodeFactory.getCMUniOpNode(6, cMNode);
            cMNode2 = this.fNodeFactory.getCMBinOpNode(102, this.multiNodes(cMNode, n - 1, true), cMNode2);
        } else {
            if (n > 0) {
                cMNode2 = this.multiNodes(cMNode, n, false);
            }
            if (n2 > n) {
                cMNode = this.fNodeFactory.getCMUniOpNode(5, cMNode);
                cMNode2 = cMNode2 == null ? this.multiNodes(cMNode, n2 - n, false) : this.fNodeFactory.getCMBinOpNode(102, cMNode2, this.multiNodes(cMNode, n2 - n, true));
            }
        }
        return cMNode2;
    }

    private CMNode multiNodes(CMNode cMNode, int n, boolean bl) {
        if (n == 0) {
            return null;
        }
        if (n == 1) {
            return bl ? this.copyNode(cMNode) : cMNode;
        }
        int n2 = n / 2;
        return this.fNodeFactory.getCMBinOpNode(102, this.multiNodes(cMNode, n2, bl), this.multiNodes(cMNode, n - n2, true));
    }

    private CMNode copyNode(CMNode cMNode) {
        int n = cMNode.type();
        if (n == 101 || n == 102) {
            XSCMBinOp xSCMBinOp = (XSCMBinOp)cMNode;
            cMNode = this.fNodeFactory.getCMBinOpNode(n, this.copyNode(xSCMBinOp.getLeft()), this.copyNode(xSCMBinOp.getRight()));
        } else if (n == 4 || n == 6 || n == 5) {
            XSCMUniOp xSCMUniOp = (XSCMUniOp)cMNode;
            cMNode = this.fNodeFactory.getCMUniOpNode(n, this.copyNode(xSCMUniOp.getChild()));
        } else if (n == 1 || n == 2) {
            XSCMLeaf xSCMLeaf = (XSCMLeaf)cMNode;
            cMNode = this.fNodeFactory.getCMLeafNode(xSCMLeaf.type(), xSCMLeaf.getLeaf(), xSCMLeaf.getParticleId(), this.fLeafCount++);
        }
        return cMNode;
    }

    private CMNode buildCompactSyntaxTree(XSParticleDecl xSParticleDecl) {
        int n = xSParticleDecl.fMaxOccurs;
        int n2 = xSParticleDecl.fMinOccurs;
        short s = xSParticleDecl.fType;
        CMNode cMNode = null;
        if (s == 2 || s == 1) {
            return this.buildCompactSyntaxTree2(xSParticleDecl, n2, n);
        }
        if (s == 3) {
            XSModelGroupImpl xSModelGroupImpl = (XSModelGroupImpl)xSParticleDecl.fValue;
            if (xSModelGroupImpl.fParticleCount == 1 && (n2 != 1 || n != 1)) {
                return this.buildCompactSyntaxTree2(xSModelGroupImpl.fParticles[0], n2, n);
            }
            CMNode cMNode2 = null;
            int n3 = 0;
            for (int i = 0; i < xSModelGroupImpl.fParticleCount; ++i) {
                cMNode2 = this.buildCompactSyntaxTree(xSModelGroupImpl.fParticles[i]);
                if (cMNode2 == null) continue;
                ++n3;
                cMNode = cMNode == null ? cMNode2 : this.fNodeFactory.getCMBinOpNode(xSModelGroupImpl.fCompositor, cMNode, cMNode2);
            }
            if (cMNode != null && xSModelGroupImpl.fCompositor == 101 && n3 < xSModelGroupImpl.fParticleCount) {
                cMNode = this.fNodeFactory.getCMUniOpNode(5, cMNode);
            }
        }
        return cMNode;
    }

    private CMNode buildCompactSyntaxTree2(XSParticleDecl xSParticleDecl, int n, int n2) {
        CMNode cMNode = null;
        if (n == 1 && n2 == 1) {
            cMNode = this.fNodeFactory.getCMLeafNode(xSParticleDecl.fType, xSParticleDecl.fValue, this.fParticleCount++, this.fLeafCount++);
        } else if (n == 0 && n2 == 1) {
            cMNode = this.fNodeFactory.getCMLeafNode(xSParticleDecl.fType, xSParticleDecl.fValue, this.fParticleCount++, this.fLeafCount++);
            cMNode = this.fNodeFactory.getCMUniOpNode(5, cMNode);
        } else if (n == 0 && n2 == -1) {
            cMNode = this.fNodeFactory.getCMLeafNode(xSParticleDecl.fType, xSParticleDecl.fValue, this.fParticleCount++, this.fLeafCount++);
            cMNode = this.fNodeFactory.getCMUniOpNode(4, cMNode);
        } else if (n == 1 && n2 == -1) {
            cMNode = this.fNodeFactory.getCMLeafNode(xSParticleDecl.fType, xSParticleDecl.fValue, this.fParticleCount++, this.fLeafCount++);
            cMNode = this.fNodeFactory.getCMUniOpNode(6, cMNode);
        } else {
            cMNode = this.fNodeFactory.getCMRepeatingLeafNode(xSParticleDecl.fType, xSParticleDecl.fValue, n, n2, this.fParticleCount++, this.fLeafCount++);
            cMNode = n == 0 ? this.fNodeFactory.getCMUniOpNode(4, cMNode) : this.fNodeFactory.getCMUniOpNode(6, cMNode);
        }
        return cMNode;
    }

    private boolean useRepeatingLeafNodes(XSParticleDecl xSParticleDecl) {
        int n = xSParticleDecl.fMaxOccurs;
        int n2 = xSParticleDecl.fMinOccurs;
        short s = xSParticleDecl.fType;
        if (s == 3) {
            XSModelGroupImpl xSModelGroupImpl = (XSModelGroupImpl)xSParticleDecl.fValue;
            if (n2 != 1 || n != 1) {
                if (xSModelGroupImpl.fParticleCount == 1) {
                    XSParticleDecl xSParticleDecl2 = xSModelGroupImpl.fParticles[0];
                    short s2 = xSParticleDecl2.fType;
                    return (s2 == 1 || s2 == 2) && xSParticleDecl2.fMinOccurs == 1 && xSParticleDecl2.fMaxOccurs == 1;
                }
                return xSModelGroupImpl.fParticleCount == 0;
            }
            for (int i = 0; i < xSModelGroupImpl.fParticleCount; ++i) {
                if (this.useRepeatingLeafNodes(xSModelGroupImpl.fParticles[i])) continue;
                return false;
            }
        }
        return true;
    }
}

