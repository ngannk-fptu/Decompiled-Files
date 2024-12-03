/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkedContentReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDObjectReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;

public abstract class PDStructureNode
implements COSObjectable {
    private final COSDictionary dictionary;

    protected PDStructureNode(String type) {
        this.dictionary = new COSDictionary();
        this.dictionary.setName(COSName.TYPE, type);
    }

    protected PDStructureNode(COSDictionary dictionary) {
        this.dictionary = dictionary;
    }

    public static PDStructureNode create(COSDictionary node) {
        String type = node.getNameAsString(COSName.TYPE);
        if ("StructTreeRoot".equals(type)) {
            return new PDStructureTreeRoot(node);
        }
        if (type == null || "StructElem".equals(type)) {
            return new PDStructureElement(node);
        }
        throw new IllegalArgumentException("Dictionary must not include a Type entry with a value that is neither StructTreeRoot nor StructElem.");
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public String getType() {
        return this.getCOSObject().getNameAsString(COSName.TYPE);
    }

    public List<Object> getKids() {
        ArrayList<Object> kidObjects = new ArrayList<Object>();
        COSBase k = this.getCOSObject().getDictionaryObject(COSName.K);
        if (k instanceof COSArray) {
            for (COSBase kid : (COSArray)k) {
                Object kidObject = this.createObject(kid);
                if (kidObject == null) continue;
                kidObjects.add(kidObject);
            }
        } else {
            Object kidObject = this.createObject(k);
            if (kidObject != null) {
                kidObjects.add(kidObject);
            }
        }
        return kidObjects;
    }

    public void setKids(List<Object> kids) {
        this.getCOSObject().setItem(COSName.K, (COSBase)COSArrayList.converterToCOSArray(kids));
    }

    public void appendKid(PDStructureElement structureElement) {
        this.appendObjectableKid(structureElement);
        structureElement.setParent(this);
    }

    protected void appendObjectableKid(COSObjectable objectable) {
        if (objectable == null) {
            return;
        }
        this.appendKid(objectable.getCOSObject());
    }

    protected void appendKid(COSBase object) {
        if (object == null) {
            return;
        }
        COSBase k = this.getCOSObject().getDictionaryObject(COSName.K);
        if (k == null) {
            this.getCOSObject().setItem(COSName.K, object);
        } else if (k instanceof COSArray) {
            COSArray array = (COSArray)k;
            array.add(object);
        } else {
            COSArray array = new COSArray();
            array.add(k);
            array.add(object);
            this.getCOSObject().setItem(COSName.K, (COSBase)array);
        }
    }

    public void insertBefore(PDStructureElement newKid, Object refKid) {
        this.insertObjectableBefore(newKid, refKid);
    }

    protected void insertObjectableBefore(COSObjectable newKid, Object refKid) {
        if (newKid == null) {
            return;
        }
        this.insertBefore(newKid.getCOSObject(), refKid);
    }

    protected void insertBefore(COSBase newKid, Object refKid) {
        if (newKid == null || refKid == null) {
            return;
        }
        COSBase k = this.getCOSObject().getDictionaryObject(COSName.K);
        if (k == null) {
            return;
        }
        COSBase refKidBase = null;
        if (refKid instanceof COSObjectable) {
            refKidBase = ((COSObjectable)refKid).getCOSObject();
        }
        if (k instanceof COSArray) {
            COSArray array = (COSArray)k;
            int refIndex = array.indexOfObject(refKidBase);
            array.add(refIndex, newKid.getCOSObject());
        } else {
            boolean onlyKid = k.equals(refKidBase);
            if (!onlyKid && k instanceof COSObject) {
                COSBase kObj = ((COSObject)k).getObject();
                onlyKid = kObj.equals(refKidBase);
            }
            if (onlyKid) {
                COSArray array = new COSArray();
                array.add(newKid);
                array.add(refKidBase);
                this.getCOSObject().setItem(COSName.K, (COSBase)array);
            }
        }
    }

    public boolean removeKid(PDStructureElement structureElement) {
        boolean removed = this.removeObjectableKid(structureElement);
        if (removed) {
            structureElement.setParent(null);
        }
        return removed;
    }

    protected boolean removeObjectableKid(COSObjectable objectable) {
        if (objectable == null) {
            return false;
        }
        return this.removeKid(objectable.getCOSObject());
    }

    protected boolean removeKid(COSBase object) {
        if (object == null) {
            return false;
        }
        COSBase k = this.getCOSObject().getDictionaryObject(COSName.K);
        if (k == null) {
            return false;
        }
        if (k instanceof COSArray) {
            COSArray array = (COSArray)k;
            boolean removed = array.removeObject(object);
            if (array.size() == 1) {
                this.getCOSObject().setItem(COSName.K, array.getObject(0));
            }
            return removed;
        }
        boolean onlyKid = k.equals(object);
        if (!onlyKid && k instanceof COSObject) {
            COSBase kObj = ((COSObject)k).getObject();
            onlyKid = kObj.equals(object);
        }
        if (onlyKid) {
            this.getCOSObject().setItem(COSName.K, null);
            return true;
        }
        return false;
    }

    protected Object createObject(COSBase kid) {
        COSBase base;
        COSDictionary kidDic = null;
        if (kid instanceof COSDictionary) {
            kidDic = (COSDictionary)kid;
        } else if (kid instanceof COSObject && (base = ((COSObject)kid).getObject()) instanceof COSDictionary) {
            kidDic = (COSDictionary)base;
        }
        if (kidDic != null) {
            return this.createObjectFromDic(kidDic);
        }
        if (kid instanceof COSInteger) {
            COSInteger mcid = (COSInteger)kid;
            return mcid.intValue();
        }
        return null;
    }

    private COSObjectable createObjectFromDic(COSDictionary kidDic) {
        String type = kidDic.getNameAsString(COSName.TYPE);
        if (type == null || "StructElem".equals(type)) {
            return new PDStructureElement(kidDic);
        }
        if ("OBJR".equals(type)) {
            return new PDObjectReference(kidDic);
        }
        if ("MCR".equals(type)) {
            return new PDMarkedContentReference(kidDic);
        }
        return null;
    }
}

