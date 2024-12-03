/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline;

import java.awt.Color;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.graphics.color.PDColor;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.color.PDDeviceRGB;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionFactory;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDNamedDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageXYZDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineNode;

public final class PDOutlineItem
extends PDOutlineNode {
    private static final int ITALIC_FLAG = 1;
    private static final int BOLD_FLAG = 2;

    public PDOutlineItem() {
    }

    public PDOutlineItem(COSDictionary dic) {
        super(dic);
    }

    public void insertSiblingAfter(PDOutlineItem newSibling) {
        this.requireSingleNode(newSibling);
        PDOutlineNode parent = this.getParent();
        newSibling.setParent(parent);
        PDOutlineItem next = this.getNextSibling();
        this.setNextSibling(newSibling);
        newSibling.setPreviousSibling(this);
        if (next != null) {
            newSibling.setNextSibling(next);
            next.setPreviousSibling(newSibling);
        } else if (parent != null) {
            this.getParent().setLastChild(newSibling);
        }
        this.updateParentOpenCountForAddedChild(newSibling);
    }

    public void insertSiblingBefore(PDOutlineItem newSibling) {
        this.requireSingleNode(newSibling);
        PDOutlineNode parent = this.getParent();
        newSibling.setParent(parent);
        PDOutlineItem previous = this.getPreviousSibling();
        this.setPreviousSibling(newSibling);
        newSibling.setNextSibling(this);
        if (previous != null) {
            previous.setNextSibling(newSibling);
            newSibling.setPreviousSibling(previous);
        } else if (parent != null) {
            this.getParent().setFirstChild(newSibling);
        }
        this.updateParentOpenCountForAddedChild(newSibling);
    }

    public PDOutlineItem getPreviousSibling() {
        return this.getOutlineItem(COSName.PREV);
    }

    void setPreviousSibling(PDOutlineNode outlineNode) {
        this.getCOSObject().setItem(COSName.PREV, (COSObjectable)outlineNode);
    }

    public PDOutlineItem getNextSibling() {
        return this.getOutlineItem(COSName.NEXT);
    }

    void setNextSibling(PDOutlineNode outlineNode) {
        this.getCOSObject().setItem(COSName.NEXT, (COSObjectable)outlineNode);
    }

    public String getTitle() {
        return this.getCOSObject().getString(COSName.TITLE);
    }

    public void setTitle(String title) {
        this.getCOSObject().setString(COSName.TITLE, title);
    }

    public PDDestination getDestination() throws IOException {
        return PDDestination.create(this.getCOSObject().getDictionaryObject(COSName.DEST));
    }

    public void setDestination(PDDestination dest) {
        this.getCOSObject().setItem(COSName.DEST, (COSObjectable)dest);
    }

    public void setDestination(PDPage page) {
        PDPageXYZDestination dest = null;
        if (page != null) {
            dest = new PDPageXYZDestination();
            dest.setPage(page);
        }
        this.setDestination(dest);
    }

    public PDPage findDestinationPage(PDDocument doc) throws IOException {
        int pageNumber;
        PDPage page;
        PDAction outlineAction;
        PDDestination dest = this.getDestination();
        if (dest == null && (outlineAction = this.getAction()) instanceof PDActionGoTo) {
            dest = ((PDActionGoTo)outlineAction).getDestination();
        }
        if (dest == null) {
            return null;
        }
        PDPageDestination pageDestination = null;
        if (dest instanceof PDNamedDestination) {
            pageDestination = doc.getDocumentCatalog().findNamedDestinationPage((PDNamedDestination)dest);
            if (pageDestination == null) {
                return null;
            }
        } else if (dest instanceof PDPageDestination) {
            pageDestination = (PDPageDestination)dest;
        } else {
            throw new IOException("Error: Unknown destination type " + dest);
        }
        if ((page = pageDestination.getPage()) == null && (pageNumber = pageDestination.getPageNumber()) != -1) {
            page = doc.getPage(pageNumber);
        }
        return page;
    }

    public PDAction getAction() {
        return PDActionFactory.createAction((COSDictionary)this.getCOSObject().getDictionaryObject(COSName.A));
    }

    public void setAction(PDAction action) {
        this.getCOSObject().setItem(COSName.A, (COSObjectable)action);
    }

    public PDStructureElement getStructureElement() {
        PDStructureElement se = null;
        COSDictionary dic = (COSDictionary)this.getCOSObject().getDictionaryObject(COSName.SE);
        if (dic != null) {
            se = new PDStructureElement(dic);
        }
        return se;
    }

    public void setStructureElement(PDStructureElement structureElement) {
        this.getCOSObject().setItem(COSName.SE, (COSObjectable)structureElement);
    }

    public PDColor getTextColor() {
        COSArray csValues = (COSArray)this.getCOSObject().getDictionaryObject(COSName.C);
        if (csValues == null) {
            csValues = new COSArray();
            csValues.growToSize(3, new COSFloat(0.0f));
            this.getCOSObject().setItem(COSName.C, (COSBase)csValues);
        }
        return new PDColor(csValues, (PDColorSpace)PDDeviceRGB.INSTANCE);
    }

    public void setTextColor(PDColor textColor) {
        this.getCOSObject().setItem(COSName.C, (COSBase)textColor.toCOSArray());
    }

    public void setTextColor(Color textColor) {
        COSArray array = new COSArray();
        array.add(new COSFloat((float)textColor.getRed() / 255.0f));
        array.add(new COSFloat((float)textColor.getGreen() / 255.0f));
        array.add(new COSFloat((float)textColor.getBlue() / 255.0f));
        this.getCOSObject().setItem(COSName.C, (COSBase)array);
    }

    public boolean isItalic() {
        return this.getCOSObject().getFlag(COSName.F, 1);
    }

    public void setItalic(boolean italic) {
        this.getCOSObject().setFlag(COSName.F, 1, italic);
    }

    public boolean isBold() {
        return this.getCOSObject().getFlag(COSName.F, 2);
    }

    public void setBold(boolean bold) {
        this.getCOSObject().setFlag(COSName.F, 2, bold);
    }
}

