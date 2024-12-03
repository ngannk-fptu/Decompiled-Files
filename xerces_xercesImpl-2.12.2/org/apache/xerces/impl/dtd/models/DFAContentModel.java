/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd.models;

import java.util.HashMap;
import org.apache.xerces.impl.dtd.models.CMAny;
import org.apache.xerces.impl.dtd.models.CMBinOp;
import org.apache.xerces.impl.dtd.models.CMLeaf;
import org.apache.xerces.impl.dtd.models.CMNode;
import org.apache.xerces.impl.dtd.models.CMStateSet;
import org.apache.xerces.impl.dtd.models.CMUniOp;
import org.apache.xerces.impl.dtd.models.ContentModelValidator;
import org.apache.xerces.xni.QName;

public class DFAContentModel
implements ContentModelValidator {
    private static String fEpsilonString = "<<CMNODE_EPSILON>>";
    private static String fEOCString = "<<CMNODE_EOC>>";
    private static final boolean DEBUG_VALIDATE_CONTENT = false;
    private QName[] fElemMap = null;
    private int[] fElemMapType = null;
    private int fElemMapSize = 0;
    private boolean fMixed;
    private int fEOCPos = 0;
    private boolean[] fFinalStateFlags = null;
    private CMStateSet[] fFollowList = null;
    private CMNode fHeadNode = null;
    private int fLeafCount = 0;
    private CMLeaf[] fLeafList = null;
    private int[] fLeafListType = null;
    private int[][] fTransTable = null;
    private int fTransTableSize = 0;
    private boolean fEmptyContentIsValid = false;
    private final QName fQName = new QName();

    public DFAContentModel(CMNode cMNode, int n, boolean bl) {
        this.fLeafCount = n;
        this.fMixed = bl;
        this.buildDFA(cMNode);
    }

    @Override
    public int validate(QName[] qNameArray, int n, int n2) {
        if (n2 == 0) {
            return this.fEmptyContentIsValid ? -1 : 0;
        }
        int n3 = 0;
        for (int i = 0; i < n2; ++i) {
            String string;
            int n4;
            int n5;
            QName qName = qNameArray[n + i];
            if (this.fMixed && qName.localpart == null) continue;
            for (n5 = 0; n5 < this.fElemMapSize && !((n4 = this.fElemMapType[n5] & 0xF) == 0 ? this.fElemMap[n5].rawname == qName.rawname : (n4 == 6 ? (string = this.fElemMap[n5].uri) == null || string == qName.uri : (n4 == 8 ? qName.uri == null : n4 == 7 && this.fElemMap[n5].uri != qName.uri))); ++n5) {
            }
            if (n5 == this.fElemMapSize) {
                return i;
            }
            if ((n3 = this.fTransTable[n3][n5]) != -1) continue;
            return i;
        }
        if (!this.fFinalStateFlags[n3]) {
            return n2;
        }
        return -1;
    }

    private void buildDFA(CMNode cMNode) {
        Object object;
        int n;
        int n2;
        this.fQName.setValues(null, fEOCString, fEOCString, null);
        CMLeaf cMLeaf = new CMLeaf(this.fQName);
        this.fHeadNode = new CMBinOp(5, cMNode, cMLeaf);
        this.fEOCPos = this.fLeafCount;
        cMLeaf.setPosition(this.fLeafCount++);
        this.fLeafList = new CMLeaf[this.fLeafCount];
        this.fLeafListType = new int[this.fLeafCount];
        this.postTreeBuildInit(this.fHeadNode, 0);
        this.fFollowList = new CMStateSet[this.fLeafCount];
        for (n2 = 0; n2 < this.fLeafCount; ++n2) {
            this.fFollowList[n2] = new CMStateSet(this.fLeafCount);
        }
        this.calcFollowList(this.fHeadNode);
        this.fElemMap = new QName[this.fLeafCount];
        this.fElemMapType = new int[this.fLeafCount];
        this.fElemMapSize = 0;
        for (n2 = 0; n2 < this.fLeafCount; ++n2) {
            this.fElemMap[n2] = new QName();
            QName qName = this.fLeafList[n2].getElement();
            for (n = 0; n < this.fElemMapSize && this.fElemMap[n].rawname != qName.rawname; ++n) {
            }
            if (n != this.fElemMapSize) continue;
            this.fElemMap[this.fElemMapSize].setValues(qName);
            this.fElemMapType[this.fElemMapSize] = this.fLeafListType[n2];
            ++this.fElemMapSize;
        }
        int[] nArray = new int[this.fLeafCount + this.fElemMapSize];
        int n3 = 0;
        for (n = 0; n < this.fElemMapSize; ++n) {
            for (int i = 0; i < this.fLeafCount; ++i) {
                object = this.fLeafList[i].getElement();
                QName qName = this.fElemMap[n];
                if (((QName)object).rawname != qName.rawname) continue;
                nArray[n3++] = i;
            }
            nArray[n3++] = -1;
        }
        n = this.fLeafCount * 4;
        CMStateSet[] cMStateSetArray = new CMStateSet[n];
        this.fFinalStateFlags = new boolean[n];
        this.fTransTable = new int[n][];
        object = this.fHeadNode.firstPos();
        int n4 = 0;
        int n5 = 0;
        this.fTransTable[n5] = this.makeDefStateList();
        cMStateSetArray[n5] = object;
        ++n5;
        HashMap<CMStateSet, Integer> hashMap = new HashMap<CMStateSet, Integer>();
        while (n4 < n5) {
            object = cMStateSetArray[n4];
            int[] nArray2 = this.fTransTable[n4];
            this.fFinalStateFlags[n4] = ((CMStateSet)object).getBit(this.fEOCPos);
            ++n4;
            CMStateSet cMStateSet = null;
            int n6 = 0;
            for (int i = 0; i < this.fElemMapSize; ++i) {
                int n7;
                if (cMStateSet == null) {
                    cMStateSet = new CMStateSet(this.fLeafCount);
                } else {
                    cMStateSet.zeroBits();
                }
                int n8 = nArray[n6++];
                while (n8 != -1) {
                    if (((CMStateSet)object).getBit(n8)) {
                        cMStateSet.union(this.fFollowList[n8]);
                    }
                    n8 = nArray[n6++];
                }
                if (cMStateSet.isEmpty()) continue;
                Integer n9 = (Integer)hashMap.get(cMStateSet);
                int n10 = n7 = n9 == null ? n5 : n9;
                if (n7 == n5) {
                    cMStateSetArray[n5] = cMStateSet;
                    this.fTransTable[n5] = this.makeDefStateList();
                    hashMap.put(cMStateSet, new Integer(n5));
                    ++n5;
                    cMStateSet = null;
                }
                nArray2[i] = n7;
                if (n5 != n) continue;
                int n11 = (int)((double)n * 1.5);
                CMStateSet[] cMStateSetArray2 = new CMStateSet[n11];
                boolean[] blArray = new boolean[n11];
                int[][] nArrayArray = new int[n11][];
                System.arraycopy(cMStateSetArray, 0, cMStateSetArray2, 0, n);
                System.arraycopy(this.fFinalStateFlags, 0, blArray, 0, n);
                System.arraycopy(this.fTransTable, 0, nArrayArray, 0, n);
                n = n11;
                cMStateSetArray = cMStateSetArray2;
                this.fFinalStateFlags = blArray;
                this.fTransTable = nArrayArray;
            }
        }
        this.fEmptyContentIsValid = ((CMBinOp)this.fHeadNode).getLeft().isNullable();
        this.fHeadNode = null;
        this.fLeafList = null;
        this.fFollowList = null;
    }

    private void calcFollowList(CMNode cMNode) {
        if (cMNode.type() == 4) {
            this.calcFollowList(((CMBinOp)cMNode).getLeft());
            this.calcFollowList(((CMBinOp)cMNode).getRight());
        } else if (cMNode.type() == 5) {
            this.calcFollowList(((CMBinOp)cMNode).getLeft());
            this.calcFollowList(((CMBinOp)cMNode).getRight());
            CMStateSet cMStateSet = ((CMBinOp)cMNode).getLeft().lastPos();
            CMStateSet cMStateSet2 = ((CMBinOp)cMNode).getRight().firstPos();
            for (int i = 0; i < this.fLeafCount; ++i) {
                if (!cMStateSet.getBit(i)) continue;
                this.fFollowList[i].union(cMStateSet2);
            }
        } else if (cMNode.type() == 2 || cMNode.type() == 3) {
            this.calcFollowList(((CMUniOp)cMNode).getChild());
            CMStateSet cMStateSet = cMNode.firstPos();
            CMStateSet cMStateSet3 = cMNode.lastPos();
            for (int i = 0; i < this.fLeafCount; ++i) {
                if (!cMStateSet3.getBit(i)) continue;
                this.fFollowList[i].union(cMStateSet);
            }
        } else if (cMNode.type() == 1) {
            this.calcFollowList(((CMUniOp)cMNode).getChild());
        }
    }

    private void dumpTree(CMNode cMNode, int n) {
        int n2;
        for (n2 = 0; n2 < n; ++n2) {
            System.out.print("   ");
        }
        n2 = cMNode.type();
        if (n2 == 4 || n2 == 5) {
            if (n2 == 4) {
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
            this.dumpTree(((CMBinOp)cMNode).getLeft(), n + 1);
            this.dumpTree(((CMBinOp)cMNode).getRight(), n + 1);
        } else if (cMNode.type() == 2) {
            System.out.print("Rep Node ");
            if (cMNode.isNullable()) {
                System.out.print("Nullable ");
            }
            System.out.print("firstPos=");
            System.out.print(cMNode.firstPos().toString());
            System.out.print(" lastPos=");
            System.out.println(cMNode.lastPos().toString());
            this.dumpTree(((CMUniOp)cMNode).getChild(), n + 1);
        } else if (cMNode.type() == 0) {
            System.out.print("Leaf: (pos=" + ((CMLeaf)cMNode).getPosition() + "), " + ((CMLeaf)cMNode).getElement() + "(elemIndex=" + ((CMLeaf)cMNode).getElement() + ") ");
            if (cMNode.isNullable()) {
                System.out.print(" Nullable ");
            }
            System.out.print("firstPos=");
            System.out.print(cMNode.firstPos().toString());
            System.out.print(" lastPos=");
            System.out.println(cMNode.lastPos().toString());
        } else {
            throw new RuntimeException("ImplementationMessages.VAL_NIICM");
        }
    }

    private int[] makeDefStateList() {
        int[] nArray = new int[this.fElemMapSize];
        for (int i = 0; i < this.fElemMapSize; ++i) {
            nArray[i] = -1;
        }
        return nArray;
    }

    private int postTreeBuildInit(CMNode cMNode, int n) {
        cMNode.setMaxStates(this.fLeafCount);
        if ((cMNode.type() & 0xF) == 6 || (cMNode.type() & 0xF) == 8 || (cMNode.type() & 0xF) == 7) {
            QName qName = new QName(null, null, null, ((CMAny)cMNode).getURI());
            this.fLeafList[n] = new CMLeaf(qName, ((CMAny)cMNode).getPosition());
            this.fLeafListType[n] = cMNode.type();
            ++n;
        } else if (cMNode.type() == 4 || cMNode.type() == 5) {
            n = this.postTreeBuildInit(((CMBinOp)cMNode).getLeft(), n);
            n = this.postTreeBuildInit(((CMBinOp)cMNode).getRight(), n);
        } else if (cMNode.type() == 2 || cMNode.type() == 3 || cMNode.type() == 1) {
            n = this.postTreeBuildInit(((CMUniOp)cMNode).getChild(), n);
        } else if (cMNode.type() == 0) {
            QName qName = ((CMLeaf)cMNode).getElement();
            if (qName.localpart != fEpsilonString) {
                this.fLeafList[n] = (CMLeaf)cMNode;
                this.fLeafListType[n] = 0;
                ++n;
            }
        } else {
            throw new RuntimeException("ImplementationMessages.VAL_NIICM: type=" + cMNode.type());
        }
        return n;
    }

    static {
        fEpsilonString = fEpsilonString.intern();
        fEOCString = fEOCString.intern();
    }
}

