/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;

public abstract class PDPageDestination
extends PDDestination {
    protected COSArray array;

    protected PDPageDestination() {
        this.array = new COSArray();
    }

    protected PDPageDestination(COSArray arr) {
        this.array = arr;
    }

    public PDPage getPage() {
        COSBase page;
        PDPage retval = null;
        if (this.array.size() > 0 && (page = this.array.getObject(0)) instanceof COSDictionary) {
            retval = new PDPage((COSDictionary)page);
        }
        return retval;
    }

    public void setPage(PDPage page) {
        this.array.set(0, page);
    }

    public int getPageNumber() {
        COSBase page;
        int retval = -1;
        if (this.array.size() > 0 && (page = this.array.getObject(0)) instanceof COSNumber) {
            retval = ((COSNumber)page).intValue();
        }
        return retval;
    }

    @Deprecated
    public int findPageNumber() {
        int retval = -1;
        if (this.array.size() > 0) {
            COSBase page = this.array.getObject(0);
            if (page instanceof COSNumber) {
                retval = ((COSNumber)page).intValue();
            } else if (page instanceof COSDictionary) {
                COSBase parent = page;
                while (((COSDictionary)parent).getDictionaryObject(COSName.PARENT, COSName.P) != null) {
                    parent = ((COSDictionary)parent).getDictionaryObject(COSName.PARENT, COSName.P);
                }
                PDPageTree pages = new PDPageTree((COSDictionary)parent);
                return pages.indexOf(new PDPage((COSDictionary)page)) + 1;
            }
        }
        return retval;
    }

    public int retrievePageNumber() {
        int retval = -1;
        if (this.array.size() > 0) {
            COSBase page = this.array.getObject(0);
            if (page instanceof COSNumber) {
                retval = ((COSNumber)page).intValue();
            } else if (page instanceof COSDictionary) {
                return this.indexOfPageTree((COSDictionary)page);
            }
        }
        return retval;
    }

    private int indexOfPageTree(COSDictionary pageDict) {
        COSDictionary parent = pageDict;
        while (parent.getDictionaryObject(COSName.PARENT, COSName.P) instanceof COSDictionary) {
            parent = (COSDictionary)parent.getDictionaryObject(COSName.PARENT, COSName.P);
        }
        if (parent.containsKey(COSName.KIDS) && COSName.PAGES.equals(parent.getItem(COSName.TYPE))) {
            PDPageTree pages = new PDPageTree(parent);
            return pages.indexOf(new PDPage(pageDict));
        }
        return -1;
    }

    public void setPageNumber(int pageNumber) {
        this.array.set(0, pageNumber);
    }

    @Override
    public COSArray getCOSObject() {
        return this.array;
    }
}

