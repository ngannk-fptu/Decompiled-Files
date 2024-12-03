/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.models;

import java.util.HashMap;
import java.util.Vector;
import org.apache.xerces.impl.dtd.models.CMNode;
import org.apache.xerces.impl.dtd.models.CMStateSet;
import org.apache.xerces.impl.xs.SubstitutionGroupHandler;
import org.apache.xerces.impl.xs.XMLSchemaException;
import org.apache.xerces.impl.xs.XSConstraints;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSWildcardDecl;
import org.apache.xerces.impl.xs.models.XSCMBinOp;
import org.apache.xerces.impl.xs.models.XSCMLeaf;
import org.apache.xerces.impl.xs.models.XSCMRepeatingLeaf;
import org.apache.xerces.impl.xs.models.XSCMUniOp;
import org.apache.xerces.impl.xs.models.XSCMValidator;
import org.apache.xerces.xni.QName;

public class XSDFACM
implements XSCMValidator {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_VALIDATE_CONTENT = false;
    private Object[] fElemMap = null;
    private int[] fElemMapType = null;
    private int[] fElemMapId = null;
    private int fElemMapSize = 0;
    private boolean[] fFinalStateFlags = null;
    private CMStateSet[] fFollowList = null;
    private CMNode fHeadNode = null;
    private int fLeafCount = 0;
    private XSCMLeaf[] fLeafList = null;
    private int[] fLeafListType = null;
    private int[][] fTransTable = null;
    private Occurence[] fCountingStates = null;
    private int fTransTableSize = 0;
    private boolean fIsCompactedForUPA;
    private static long time = 0L;

    public XSDFACM(CMNode cMNode, int n) {
        this.fLeafCount = n;
        this.fIsCompactedForUPA = cMNode.isCompactedForUPA();
        this.buildDFA(cMNode);
    }

    public boolean isFinalState(int n) {
        return n < 0 ? false : this.fFinalStateFlags[n];
    }

    @Override
    public Object oneTransition(QName qName, int[] nArray, SubstitutionGroupHandler substitutionGroupHandler) {
        int n;
        int n2 = nArray[0];
        if (n2 == -1 || n2 == -2) {
            if (n2 == -1) {
                nArray[0] = -2;
            }
            return this.findMatchingDecl(qName, substitutionGroupHandler);
        }
        int n3 = 0;
        Object object = null;
        for (n = 0; n < this.fElemMapSize; ++n) {
            n3 = this.fTransTable[n2][n];
            if (n3 == -1) continue;
            int n4 = this.fElemMapType[n];
            if (n4 == 1) {
                object = substitutionGroupHandler.getMatchingElemDecl(qName, (XSElementDecl)this.fElemMap[n]);
                if (object == null) continue;
                break;
            }
            if (n4 != 2 || !((XSWildcardDecl)this.fElemMap[n]).allowNamespace(qName.uri)) continue;
            object = this.fElemMap[n];
            break;
        }
        if (n == this.fElemMapSize) {
            nArray[1] = nArray[0];
            nArray[0] = -1;
            return this.findMatchingDecl(qName, substitutionGroupHandler);
        }
        if (this.fCountingStates != null) {
            Occurence occurence = this.fCountingStates[n2];
            if (occurence != null) {
                if (n2 == n3) {
                    nArray[2] = nArray[2] + 1;
                    if (nArray[2] > occurence.maxOccurs && occurence.maxOccurs != -1) {
                        return this.findMatchingDecl(qName, nArray, substitutionGroupHandler, n);
                    }
                } else {
                    if (nArray[2] < occurence.minOccurs) {
                        nArray[1] = nArray[0];
                        nArray[0] = -1;
                        return this.findMatchingDecl(qName, substitutionGroupHandler);
                    }
                    occurence = this.fCountingStates[n3];
                    if (occurence != null) {
                        nArray[2] = n == occurence.elemIndex ? 1 : 0;
                    }
                }
            } else {
                occurence = this.fCountingStates[n3];
                if (occurence != null) {
                    nArray[2] = n == occurence.elemIndex ? 1 : 0;
                }
            }
        }
        nArray[0] = n3;
        return object;
    }

    Object findMatchingDecl(QName qName, SubstitutionGroupHandler substitutionGroupHandler) {
        XSElementDecl xSElementDecl = null;
        for (int i = 0; i < this.fElemMapSize; ++i) {
            int n = this.fElemMapType[i];
            if (n == 1) {
                xSElementDecl = substitutionGroupHandler.getMatchingElemDecl(qName, (XSElementDecl)this.fElemMap[i]);
                if (xSElementDecl == null) continue;
                return xSElementDecl;
            }
            if (n != 2 || !((XSWildcardDecl)this.fElemMap[i]).allowNamespace(qName.uri)) continue;
            return this.fElemMap[i];
        }
        return null;
    }

    Object findMatchingDecl(QName qName, int[] nArray, SubstitutionGroupHandler substitutionGroupHandler, int n) {
        int n2 = nArray[0];
        int n3 = 0;
        Object object = null;
        while (++n < this.fElemMapSize) {
            n3 = this.fTransTable[n2][n];
            if (n3 == -1) continue;
            int n4 = this.fElemMapType[n];
            if (n4 == 1) {
                object = substitutionGroupHandler.getMatchingElemDecl(qName, (XSElementDecl)this.fElemMap[n]);
                if (object == null) continue;
                break;
            }
            if (n4 != 2 || !((XSWildcardDecl)this.fElemMap[n]).allowNamespace(qName.uri)) continue;
            object = this.fElemMap[n];
            break;
        }
        if (n == this.fElemMapSize) {
            nArray[1] = nArray[0];
            nArray[0] = -1;
            return this.findMatchingDecl(qName, substitutionGroupHandler);
        }
        nArray[0] = n3;
        Occurence occurence = this.fCountingStates[n3];
        if (occurence != null) {
            nArray[2] = n == occurence.elemIndex ? 1 : 0;
        }
        return object;
    }

    @Override
    public int[] startContentModel() {
        return new int[3];
    }

    @Override
    public boolean endContentModel(int[] nArray) {
        int n = nArray[0];
        if (this.fFinalStateFlags[n]) {
            Occurence occurence;
            return this.fCountingStates == null || (occurence = this.fCountingStates[n]) == null || nArray[2] >= occurence.minOccurs;
        }
        return false;
    }

    private void buildDFA(CMNode cMNode) {
        int n;
        Object object;
        int n2;
        int n3;
        int n4 = this.fLeafCount;
        XSCMLeaf xSCMLeaf = new XSCMLeaf(1, null, -1, this.fLeafCount++);
        this.fHeadNode = new XSCMBinOp(102, cMNode, xSCMLeaf);
        this.fLeafList = new XSCMLeaf[this.fLeafCount];
        this.fLeafListType = new int[this.fLeafCount];
        this.postTreeBuildInit(this.fHeadNode);
        this.fFollowList = new CMStateSet[this.fLeafCount];
        for (int i = 0; i < this.fLeafCount; ++i) {
            this.fFollowList[i] = new CMStateSet(this.fLeafCount);
        }
        this.calcFollowList(this.fHeadNode);
        this.fElemMap = new Object[this.fLeafCount];
        this.fElemMapType = new int[this.fLeafCount];
        this.fElemMapId = new int[this.fLeafCount];
        this.fElemMapSize = 0;
        Occurence[] occurenceArray = null;
        for (int i = 0; i < this.fLeafCount; ++i) {
            this.fElemMap[i] = null;
            n3 = this.fLeafList[i].getParticleId();
            for (n2 = 0; n2 < this.fElemMapSize && n3 != this.fElemMapId[n2]; ++n2) {
            }
            if (n2 != this.fElemMapSize) continue;
            XSCMLeaf xSCMLeaf2 = this.fLeafList[i];
            this.fElemMap[this.fElemMapSize] = xSCMLeaf2.getLeaf();
            if (xSCMLeaf2 instanceof XSCMRepeatingLeaf) {
                if (occurenceArray == null) {
                    occurenceArray = new Occurence[this.fLeafCount];
                }
                occurenceArray[this.fElemMapSize] = new Occurence((XSCMRepeatingLeaf)xSCMLeaf2, this.fElemMapSize);
            }
            this.fElemMapType[this.fElemMapSize] = this.fLeafListType[i];
            this.fElemMapId[this.fElemMapSize] = n3;
            ++this.fElemMapSize;
        }
        --this.fElemMapSize;
        int[] nArray = new int[this.fLeafCount + this.fElemMapSize];
        n2 = 0;
        for (n3 = 0; n3 < this.fElemMapSize; ++n3) {
            int n5 = this.fElemMapId[n3];
            for (int i = 0; i < this.fLeafCount; ++i) {
                if (n5 != this.fLeafList[i].getParticleId()) continue;
                nArray[n2++] = i;
            }
            nArray[n2++] = -1;
        }
        n3 = this.fLeafCount * 4;
        CMStateSet[] cMStateSetArray = new CMStateSet[n3];
        this.fFinalStateFlags = new boolean[n3];
        this.fTransTable = new int[n3][];
        CMStateSet cMStateSet = this.fHeadNode.firstPos();
        int n6 = 0;
        int n7 = 0;
        this.fTransTable[n7] = this.makeDefStateList();
        cMStateSetArray[n7] = cMStateSet;
        ++n7;
        HashMap<Object, Integer> hashMap = new HashMap<Object, Integer>();
        while (n6 < n7) {
            cMStateSet = cMStateSetArray[n6];
            int[] nArray2 = this.fTransTable[n6];
            this.fFinalStateFlags[n6] = cMStateSet.getBit(n4);
            ++n6;
            object = null;
            n = 0;
            for (int i = 0; i < this.fElemMapSize; ++i) {
                int n8;
                if (object == null) {
                    object = new CMStateSet(this.fLeafCount);
                } else {
                    ((CMStateSet)object).zeroBits();
                }
                int n9 = nArray[n++];
                while (n9 != -1) {
                    if (cMStateSet.getBit(n9)) {
                        ((CMStateSet)object).union(this.fFollowList[n9]);
                    }
                    n9 = nArray[n++];
                }
                if (((CMStateSet)object).isEmpty()) continue;
                Integer n10 = (Integer)hashMap.get(object);
                int n11 = n8 = n10 == null ? n7 : n10;
                if (n8 == n7) {
                    cMStateSetArray[n7] = object;
                    this.fTransTable[n7] = this.makeDefStateList();
                    hashMap.put(object, new Integer(n7));
                    ++n7;
                    object = null;
                }
                nArray2[i] = n8;
                if (n7 != n3) continue;
                int n12 = (int)((double)n3 * 1.5);
                CMStateSet[] cMStateSetArray2 = new CMStateSet[n12];
                boolean[] blArray = new boolean[n12];
                int[][] nArrayArray = new int[n12][];
                System.arraycopy(cMStateSetArray, 0, cMStateSetArray2, 0, n3);
                System.arraycopy(this.fFinalStateFlags, 0, blArray, 0, n3);
                System.arraycopy(this.fTransTable, 0, nArrayArray, 0, n3);
                n3 = n12;
                cMStateSetArray = cMStateSetArray2;
                this.fFinalStateFlags = blArray;
                this.fTransTable = nArrayArray;
            }
        }
        if (occurenceArray != null) {
            this.fCountingStates = new Occurence[n7];
            block8: for (int i = 0; i < n7; ++i) {
                object = this.fTransTable[i];
                for (n = 0; n < ((int[])object).length; ++n) {
                    if (i != object[n]) continue;
                    this.fCountingStates[i] = occurenceArray[n];
                    continue block8;
                }
            }
        }
        this.fHeadNode = null;
        this.fLeafList = null;
        this.fFollowList = null;
        this.fLeafListType = null;
        this.fElemMapId = null;
    }

    private void calcFollowList(CMNode cMNode) {
        if (cMNode.type() == 101) {
            this.calcFollowList(((XSCMBinOp)cMNode).getLeft());
            this.calcFollowList(((XSCMBinOp)cMNode).getRight());
        } else if (cMNode.type() == 102) {
            this.calcFollowList(((XSCMBinOp)cMNode).getLeft());
            this.calcFollowList(((XSCMBinOp)cMNode).getRight());
            CMStateSet cMStateSet = ((XSCMBinOp)cMNode).getLeft().lastPos();
            CMStateSet cMStateSet2 = ((XSCMBinOp)cMNode).getRight().firstPos();
            for (int i = 0; i < this.fLeafCount; ++i) {
                if (!cMStateSet.getBit(i)) continue;
                this.fFollowList[i].union(cMStateSet2);
            }
        } else if (cMNode.type() == 4 || cMNode.type() == 6) {
            this.calcFollowList(((XSCMUniOp)cMNode).getChild());
            CMStateSet cMStateSet = cMNode.firstPos();
            CMStateSet cMStateSet3 = cMNode.lastPos();
            for (int i = 0; i < this.fLeafCount; ++i) {
                if (!cMStateSet3.getBit(i)) continue;
                this.fFollowList[i].union(cMStateSet);
            }
        } else if (cMNode.type() == 5) {
            this.calcFollowList(((XSCMUniOp)cMNode).getChild());
        }
    }

    private void dumpTree(CMNode cMNode, int n) {
        int n2;
        for (n2 = 0; n2 < n; ++n2) {
            System.out.print("   ");
        }
        n2 = cMNode.type();
        switch (n2) {
            case 101: 
            case 102: {
                if (n2 == 101) {
                    System.out.print("Choice Node ");
                } else {
                    System.out.print("Seq Node ");
                }
                if (cMNode.isNullable()) {
                    System.out.print("Nullable ");
                }
                System.out.print("firstPos=");
                System.out.print(cMNode.firstPos().toString());
                System.out.print(" lastPos=");
                System.out.println(cMNode.lastPos().toString());
                this.dumpTree(((XSCMBinOp)cMNode).getLeft(), n + 1);
                this.dumpTree(((XSCMBinOp)cMNode).getRight(), n + 1);
                break;
            }
            case 4: 
            case 5: 
            case 6: {
                System.out.print("Rep Node ");
                if (cMNode.isNullable()) {
                    System.out.print("Nullable ");
                }
                System.out.print("firstPos=");
                System.out.print(cMNode.firstPos().toString());
                System.out.print(" lastPos=");
                System.out.println(cMNode.lastPos().toString());
                this.dumpTree(((XSCMUniOp)cMNode).getChild(), n + 1);
                break;
            }
            case 1: {
                System.out.print("Leaf: (pos=" + ((XSCMLeaf)cMNode).getPosition() + "), " + "(elemIndex=" + ((XSCMLeaf)cMNode).getLeaf() + ") ");
                if (cMNode.isNullable()) {
                    System.out.print(" Nullable ");
                }
                System.out.print("firstPos=");
                System.out.print(cMNode.firstPos().toString());
                System.out.print(" lastPos=");
                System.out.println(cMNode.lastPos().toString());
                break;
            }
            case 2: {
                System.out.print("Any Node: ");
                System.out.print("firstPos=");
                System.out.print(cMNode.firstPos().toString());
                System.out.print(" lastPos=");
                System.out.println(cMNode.lastPos().toString());
                break;
            }
            default: {
                throw new RuntimeException("ImplementationMessages.VAL_NIICM");
            }
        }
    }

    private int[] makeDefStateList() {
        int[] nArray = new int[this.fElemMapSize];
        for (int i = 0; i < this.fElemMapSize; ++i) {
            nArray[i] = -1;
        }
        return nArray;
    }

    private void postTreeBuildInit(CMNode cMNode) throws RuntimeException {
        cMNode.setMaxStates(this.fLeafCount);
        XSCMLeaf xSCMLeaf = null;
        int n = 0;
        if (cMNode.type() == 2) {
            xSCMLeaf = (XSCMLeaf)cMNode;
            n = xSCMLeaf.getPosition();
            this.fLeafList[n] = xSCMLeaf;
            this.fLeafListType[n] = 2;
        } else if (cMNode.type() == 101 || cMNode.type() == 102) {
            this.postTreeBuildInit(((XSCMBinOp)cMNode).getLeft());
            this.postTreeBuildInit(((XSCMBinOp)cMNode).getRight());
        } else if (cMNode.type() == 4 || cMNode.type() == 6 || cMNode.type() == 5) {
            this.postTreeBuildInit(((XSCMUniOp)cMNode).getChild());
        } else if (cMNode.type() == 1) {
            xSCMLeaf = (XSCMLeaf)cMNode;
            n = xSCMLeaf.getPosition();
            this.fLeafList[n] = xSCMLeaf;
            this.fLeafListType[n] = 1;
        } else {
            throw new RuntimeException("ImplementationMessages.VAL_NIICM");
        }
    }

    @Override
    public boolean checkUniqueParticleAttribution(SubstitutionGroupHandler substitutionGroupHandler) throws XMLSchemaException {
        int n;
        int n2;
        byte[][] byArray = new byte[this.fElemMapSize][this.fElemMapSize];
        for (n2 = 0; n2 < this.fTransTable.length && this.fTransTable[n2] != null; ++n2) {
            for (n = 0; n < this.fElemMapSize; ++n) {
                for (int i = n + 1; i < this.fElemMapSize; ++i) {
                    if (this.fTransTable[n2][n] == -1 || this.fTransTable[n2][i] == -1 || byArray[n][i] != 0) continue;
                    if (XSConstraints.overlapUPA(this.fElemMap[n], this.fElemMap[i], substitutionGroupHandler)) {
                        Occurence occurence;
                        if (this.fCountingStates != null && (occurence = this.fCountingStates[n2]) != null && this.fTransTable[n2][n] == n2 ^ this.fTransTable[n2][i] == n2 && occurence.minOccurs == occurence.maxOccurs) {
                            byArray[n][i] = -1;
                            continue;
                        }
                        byArray[n][i] = 1;
                        continue;
                    }
                    byArray[n][i] = -1;
                }
            }
        }
        for (n2 = 0; n2 < this.fElemMapSize; ++n2) {
            for (n = 0; n < this.fElemMapSize; ++n) {
                if (byArray[n2][n] != 1) continue;
                throw new XMLSchemaException("cos-nonambig", new Object[]{this.fElemMap[n2].toString(), this.fElemMap[n].toString()});
            }
        }
        for (n2 = 0; n2 < this.fElemMapSize; ++n2) {
            if (this.fElemMapType[n2] != 2) continue;
            XSWildcardDecl xSWildcardDecl = (XSWildcardDecl)this.fElemMap[n2];
            if (xSWildcardDecl.fType != 3 && xSWildcardDecl.fType != 2) continue;
            return true;
        }
        return false;
    }

    @Override
    public Vector whatCanGoHere(int[] nArray) {
        int n = nArray[0];
        if (n < 0) {
            n = nArray[1];
        }
        Occurence occurence = this.fCountingStates != null ? this.fCountingStates[n] : null;
        int n2 = nArray[2];
        Vector<Object> vector = new Vector<Object>();
        for (int i = 0; i < this.fElemMapSize; ++i) {
            int n3 = this.fTransTable[n][i];
            if (n3 == -1 || occurence != null && (n == n3 ? n2 >= occurence.maxOccurs && occurence.maxOccurs != -1 : n2 < occurence.minOccurs)) continue;
            vector.addElement(this.fElemMap[i]);
        }
        return vector;
    }

    @Override
    public int[] occurenceInfo(int[] nArray) {
        if (this.fCountingStates != null) {
            Occurence occurence;
            int n = nArray[0];
            if (n < 0) {
                n = nArray[1];
            }
            if ((occurence = this.fCountingStates[n]) != null) {
                int[] nArray2 = new int[]{occurence.minOccurs, occurence.maxOccurs, nArray[2], occurence.elemIndex};
                return nArray2;
            }
        }
        return null;
    }

    @Override
    public String getTermName(int n) {
        Object object = this.fElemMap[n];
        return object != null ? object.toString() : null;
    }

    @Override
    public boolean isCompactedForUPA() {
        return this.fIsCompactedForUPA;
    }

    static final class Occurence {
        final int minOccurs;
        final int maxOccurs;
        final int elemIndex;

        public Occurence(XSCMRepeatingLeaf xSCMRepeatingLeaf, int n) {
            this.minOccurs = xSCMRepeatingLeaf.getMinOccurs();
            this.maxOccurs = xSCMRepeatingLeaf.getMaxOccurs();
            this.elemIndex = n;
        }

        public String toString() {
            return "minOccurs=" + this.minOccurs + ";maxOccurs=" + (this.maxOccurs != -1 ? Integer.toString(this.maxOccurs) : "unbounded");
        }
    }
}

