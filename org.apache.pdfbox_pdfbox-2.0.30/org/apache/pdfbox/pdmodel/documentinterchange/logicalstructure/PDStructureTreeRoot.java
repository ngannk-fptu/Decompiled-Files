/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDStructureElementNameTreeNode;
import org.apache.pdfbox.pdmodel.common.COSDictionaryMap;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.PDNumberTreeNode;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDParentTreeValue;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureNode;

public class PDStructureTreeRoot
extends PDStructureNode {
    private static final Log LOG = LogFactory.getLog(PDStructureTreeRoot.class);
    private static final String TYPE = "StructTreeRoot";

    public PDStructureTreeRoot() {
        super(TYPE);
    }

    public PDStructureTreeRoot(COSDictionary dic) {
        super(dic);
    }

    @Deprecated
    public COSArray getKArray() {
        COSDictionary kdict;
        COSBase k = this.getCOSObject().getDictionaryObject(COSName.K);
        if (k instanceof COSDictionary ? (k = (kdict = (COSDictionary)k).getDictionaryObject(COSName.K)) instanceof COSArray : k instanceof COSArray) {
            return (COSArray)k;
        }
        return null;
    }

    public COSBase getK() {
        return this.getCOSObject().getDictionaryObject(COSName.K);
    }

    public void setK(COSBase k) {
        this.getCOSObject().setItem(COSName.K, k);
    }

    public PDNameTreeNode<PDStructureElement> getIDTree() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.ID_TREE);
        if (base instanceof COSDictionary) {
            return new PDStructureElementNameTreeNode((COSDictionary)base);
        }
        return null;
    }

    public void setIDTree(PDNameTreeNode<PDStructureElement> idTree) {
        this.getCOSObject().setItem(COSName.ID_TREE, idTree);
    }

    public PDNumberTreeNode getParentTree() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.PARENT_TREE);
        if (base instanceof COSDictionary) {
            return new PDNumberTreeNode((COSDictionary)base, PDParentTreeValue.class);
        }
        return null;
    }

    public void setParentTree(PDNumberTreeNode parentTree) {
        this.getCOSObject().setItem(COSName.PARENT_TREE, (COSObjectable)parentTree);
    }

    public int getParentTreeNextKey() {
        return this.getCOSObject().getInt(COSName.PARENT_TREE_NEXT_KEY);
    }

    public void setParentTreeNextKey(int parentTreeNextkey) {
        this.getCOSObject().setInt(COSName.PARENT_TREE_NEXT_KEY, parentTreeNextkey);
    }

    public Map<String, Object> getRoleMap() {
        COSBase rm = this.getCOSObject().getDictionaryObject(COSName.ROLE_MAP);
        if (rm instanceof COSDictionary) {
            try {
                return COSDictionaryMap.convertBasicTypesToMap((COSDictionary)rm);
            }
            catch (IOException e) {
                LOG.error((Object)e, (Throwable)e);
            }
        }
        return new HashMap<String, Object>();
    }

    public void setRoleMap(Map<String, String> roleMap) {
        COSDictionary rmDic = new COSDictionary();
        for (Map.Entry<String, String> entry : roleMap.entrySet()) {
            rmDic.setName(entry.getKey(), entry.getValue());
        }
        this.getCOSObject().setItem(COSName.ROLE_MAP, (COSBase)rmDic);
    }
}

