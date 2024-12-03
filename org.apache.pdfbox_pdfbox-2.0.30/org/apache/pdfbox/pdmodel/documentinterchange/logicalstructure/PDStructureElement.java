/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure;

import java.util.Iterator;
import java.util.Map;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDAttributeObject;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkedContentReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDObjectReference;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureNode;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.Revisions;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDMarkedContent;

public class PDStructureElement
extends PDStructureNode {
    public static final String TYPE = "StructElem";

    public PDStructureElement(String structureType, PDStructureNode parent) {
        super(TYPE);
        this.setStructureType(structureType);
        this.setParent(parent);
    }

    public PDStructureElement(COSDictionary dic) {
        super(dic);
    }

    public String getStructureType() {
        return this.getCOSObject().getNameAsString(COSName.S);
    }

    public final void setStructureType(String structureType) {
        this.getCOSObject().setName(COSName.S, structureType);
    }

    public PDStructureNode getParent() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.P);
        if (base instanceof COSDictionary) {
            return PDStructureNode.create((COSDictionary)base);
        }
        return null;
    }

    public final void setParent(PDStructureNode structureNode) {
        this.getCOSObject().setItem(COSName.P, (COSObjectable)structureNode);
    }

    public String getElementIdentifier() {
        return this.getCOSObject().getString(COSName.ID);
    }

    public void setElementIdentifier(String id) {
        this.getCOSObject().setString(COSName.ID, id);
    }

    public PDPage getPage() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.PG);
        if (base instanceof COSDictionary) {
            return new PDPage((COSDictionary)base);
        }
        return null;
    }

    public void setPage(PDPage page) {
        this.getCOSObject().setItem(COSName.PG, (COSObjectable)page);
    }

    public Revisions<PDAttributeObject> getAttributes() {
        Revisions<PDAttributeObject> attributes = new Revisions<PDAttributeObject>();
        COSBase a = this.getCOSObject().getDictionaryObject(COSName.A);
        if (a instanceof COSArray) {
            COSArray aa = (COSArray)a;
            Iterator<COSBase> it = aa.iterator();
            PDAttributeObject ao = null;
            while (it.hasNext()) {
                COSBase item = it.next();
                if (item instanceof COSObject) {
                    item = ((COSObject)item).getObject();
                }
                if (item instanceof COSDictionary) {
                    ao = PDAttributeObject.create((COSDictionary)item);
                    ao.setStructureElement(this);
                    attributes.addObject(ao, 0);
                    continue;
                }
                if (!(item instanceof COSInteger)) continue;
                attributes.setRevisionNumber(ao, ((COSNumber)item).intValue());
            }
        }
        if (a instanceof COSDictionary) {
            PDAttributeObject ao = PDAttributeObject.create((COSDictionary)a);
            ao.setStructureElement(this);
            attributes.addObject(ao, 0);
        }
        return attributes;
    }

    public void setAttributes(Revisions<PDAttributeObject> attributes) {
        COSName key = COSName.A;
        if (attributes.size() == 1 && attributes.getRevisionNumber(0) == 0) {
            PDAttributeObject attributeObject = attributes.getObject(0);
            attributeObject.setStructureElement(this);
            this.getCOSObject().setItem(key, (COSObjectable)attributeObject);
            return;
        }
        COSArray array = new COSArray();
        for (int i = 0; i < attributes.size(); ++i) {
            PDAttributeObject attributeObject = attributes.getObject(i);
            attributeObject.setStructureElement(this);
            int revisionNumber = attributes.getRevisionNumber(i);
            if (revisionNumber < 0) {
                throw new IllegalArgumentException("The revision number shall be > -1");
            }
            array.add(attributeObject);
            array.add(COSInteger.get(revisionNumber));
        }
        this.getCOSObject().setItem(key, (COSBase)array);
    }

    public void addAttribute(PDAttributeObject attributeObject) {
        COSArray array;
        COSName key = COSName.A;
        attributeObject.setStructureElement(this);
        COSBase a = this.getCOSObject().getDictionaryObject(key);
        if (a instanceof COSArray) {
            array = (COSArray)a;
        } else {
            array = new COSArray();
            if (a != null) {
                array.add(a);
                array.add(COSInteger.get(0L));
            }
        }
        this.getCOSObject().setItem(key, (COSBase)array);
        array.add(attributeObject);
        array.add(COSInteger.get(this.getRevisionNumber()));
    }

    public void removeAttribute(PDAttributeObject attributeObject) {
        COSName key = COSName.A;
        COSBase a = this.getCOSObject().getDictionaryObject(key);
        if (a instanceof COSArray) {
            COSArray array = (COSArray)a;
            array.remove(attributeObject.getCOSObject());
            if (array.size() == 2 && array.getInt(1) == 0) {
                this.getCOSObject().setItem(key, array.getObject(0));
            }
        } else {
            COSBase directA = a;
            if (a instanceof COSObject) {
                directA = ((COSObject)a).getObject();
            }
            if (attributeObject.getCOSObject().equals(directA)) {
                this.getCOSObject().setItem(key, null);
            }
        }
        attributeObject.setStructureElement(null);
    }

    public void attributeChanged(PDAttributeObject attributeObject) {
        COSName key = COSName.A;
        COSBase a = this.getCOSObject().getDictionaryObject(key);
        if (a instanceof COSArray) {
            COSArray array = (COSArray)a;
            for (int i = 0; i < array.size(); ++i) {
                COSBase next;
                COSBase entry = array.getObject(i);
                if (!entry.equals(attributeObject.getCOSObject()) || !((next = array.get(i + 1)) instanceof COSInteger)) continue;
                array.set(i + 1, COSInteger.get(this.getRevisionNumber()));
            }
        } else {
            COSArray array = new COSArray();
            array.add(a);
            array.add(COSInteger.get(this.getRevisionNumber()));
            this.getCOSObject().setItem(key, (COSBase)array);
        }
    }

    public Revisions<String> getClassNames() {
        COSName key = COSName.C;
        Revisions<String> classNames = new Revisions<String>();
        COSBase c = this.getCOSObject().getDictionaryObject(key);
        if (c instanceof COSName) {
            classNames.addObject(((COSName)c).getName(), 0);
        }
        if (c instanceof COSArray) {
            COSArray array = (COSArray)c;
            Iterator<COSBase> it = array.iterator();
            String className = null;
            while (it.hasNext()) {
                COSBase item = it.next();
                if (item instanceof COSObject) {
                    item = ((COSObject)item).getObject();
                }
                if (item instanceof COSName) {
                    className = ((COSName)item).getName();
                    classNames.addObject(className, 0);
                    continue;
                }
                if (!(item instanceof COSInteger)) continue;
                classNames.setRevisionNumber(className, ((COSInteger)item).intValue());
            }
        }
        return classNames;
    }

    public void setClassNames(Revisions<String> classNames) {
        if (classNames == null) {
            return;
        }
        COSName key = COSName.C;
        if (classNames.size() == 1 && classNames.getRevisionNumber(0) == 0) {
            String className = classNames.getObject(0);
            this.getCOSObject().setName(key, className);
            return;
        }
        COSArray array = new COSArray();
        for (int i = 0; i < classNames.size(); ++i) {
            String className = classNames.getObject(i);
            int revisionNumber = classNames.getRevisionNumber(i);
            if (revisionNumber < 0) {
                throw new IllegalArgumentException("The revision number shall be > -1");
            }
            array.add(COSName.getPDFName(className));
            array.add(COSInteger.get(revisionNumber));
        }
        this.getCOSObject().setItem(key, (COSBase)array);
    }

    public void addClassName(String className) {
        COSArray array;
        if (className == null) {
            return;
        }
        COSName key = COSName.C;
        COSBase c = this.getCOSObject().getDictionaryObject(key);
        if (c instanceof COSArray) {
            array = (COSArray)c;
        } else {
            array = new COSArray();
            if (c != null) {
                array.add(c);
                array.add(COSInteger.get(0L));
            }
        }
        this.getCOSObject().setItem(key, (COSBase)array);
        array.add(COSName.getPDFName(className));
        array.add(COSInteger.get(this.getRevisionNumber()));
    }

    public void removeClassName(String className) {
        if (className == null) {
            return;
        }
        COSName key = COSName.C;
        COSBase c = this.getCOSObject().getDictionaryObject(key);
        COSName name = COSName.getPDFName(className);
        if (c instanceof COSArray) {
            COSArray array = (COSArray)c;
            array.remove(name);
            if (array.size() == 2 && array.getInt(1) == 0) {
                this.getCOSObject().setItem(key, array.getObject(0));
            }
        } else {
            COSBase directC = c;
            if (c instanceof COSObject) {
                directC = ((COSObject)c).getObject();
            }
            if (name.equals(directC)) {
                this.getCOSObject().setItem(key, null);
            }
        }
    }

    public int getRevisionNumber() {
        return this.getCOSObject().getInt(COSName.R, 0);
    }

    public void setRevisionNumber(int revisionNumber) {
        if (revisionNumber < 0) {
            throw new IllegalArgumentException("The revision number shall be > -1");
        }
        this.getCOSObject().setInt(COSName.R, revisionNumber);
    }

    public void incrementRevisionNumber() {
        this.setRevisionNumber(this.getRevisionNumber() + 1);
    }

    public String getTitle() {
        return this.getCOSObject().getString(COSName.T);
    }

    public void setTitle(String title) {
        this.getCOSObject().setString(COSName.T, title);
    }

    public String getLanguage() {
        return this.getCOSObject().getString(COSName.LANG);
    }

    public void setLanguage(String language) {
        this.getCOSObject().setString(COSName.LANG, language);
    }

    public String getAlternateDescription() {
        return this.getCOSObject().getString(COSName.ALT);
    }

    public void setAlternateDescription(String alternateDescription) {
        this.getCOSObject().setString(COSName.ALT, alternateDescription);
    }

    public String getExpandedForm() {
        return this.getCOSObject().getString(COSName.E);
    }

    public void setExpandedForm(String expandedForm) {
        this.getCOSObject().setString(COSName.E, expandedForm);
    }

    public String getActualText() {
        return this.getCOSObject().getString(COSName.ACTUAL_TEXT);
    }

    public void setActualText(String actualText) {
        this.getCOSObject().setString(COSName.ACTUAL_TEXT, actualText);
    }

    public String getStandardStructureType() {
        Object mappedValue;
        String type = this.getStructureType();
        Map<String, Object> roleMap = this.getRoleMap();
        if (roleMap.containsKey(type) && (mappedValue = this.getRoleMap().get(type)) instanceof String) {
            type = (String)mappedValue;
        }
        return type;
    }

    public void appendKid(PDMarkedContent markedContent) {
        if (markedContent == null) {
            return;
        }
        this.appendKid(COSInteger.get(markedContent.getMCID()));
    }

    public void appendKid(PDMarkedContentReference markedContentReference) {
        this.appendObjectableKid(markedContentReference);
    }

    public void appendKid(PDObjectReference objectReference) {
        this.appendObjectableKid(objectReference);
    }

    public void insertBefore(COSInteger markedContentIdentifier, Object refKid) {
        this.insertBefore((COSBase)markedContentIdentifier, refKid);
    }

    public void insertBefore(PDMarkedContentReference markedContentReference, Object refKid) {
        this.insertObjectableBefore(markedContentReference, refKid);
    }

    public void insertBefore(PDObjectReference objectReference, Object refKid) {
        this.insertObjectableBefore(objectReference, refKid);
    }

    public void removeKid(COSInteger markedContentIdentifier) {
        this.removeKid((COSBase)markedContentIdentifier);
    }

    public void removeKid(PDMarkedContentReference markedContentReference) {
        this.removeObjectableKid(markedContentReference);
    }

    public void removeKid(PDObjectReference objectReference) {
        this.removeObjectableKid(objectReference);
    }

    private PDStructureTreeRoot getStructureTreeRoot() {
        PDStructureNode parent = this.getParent();
        while (parent instanceof PDStructureElement) {
            parent = ((PDStructureElement)parent).getParent();
        }
        if (parent instanceof PDStructureTreeRoot) {
            return (PDStructureTreeRoot)parent;
        }
        return null;
    }

    private Map<String, Object> getRoleMap() {
        PDStructureTreeRoot root = this.getStructureTreeRoot();
        if (root != null) {
            return root.getRoleMap();
        }
        return null;
    }
}

