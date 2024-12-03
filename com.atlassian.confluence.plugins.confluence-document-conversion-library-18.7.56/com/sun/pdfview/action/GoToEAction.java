/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.action;

import com.sun.pdfview.PDFDestination;
import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.action.PDFAction;
import com.sun.pdfview.action.PdfObjectParseUtil;
import java.io.IOException;
import java.util.ArrayList;

public class GoToEAction
extends PDFAction {
    private PDFDestination destination;
    private String file = null;
    private boolean newWindow = false;
    private GoToETarget target;

    public GoToEAction(PDFObject obj, PDFObject root) throws IOException {
        super("GoToE");
        this.destination = PdfObjectParseUtil.parseDestination("D", obj, root, true);
        this.file = PdfObjectParseUtil.parseStringFromDict("F", obj, false);
        this.newWindow = PdfObjectParseUtil.parseBooleanFromDict("NewWindow", obj, false);
        PDFObject targetObj = obj.getDictRef("T");
        ArrayList<GoToETarget> list = new ArrayList<GoToETarget>();
        this.target = this.parseTargetDistionary(targetObj, list);
    }

    private GoToETarget parseTargetDistionary(PDFObject targetObj, ArrayList<GoToETarget> list) throws IOException {
        GoToETarget target = null;
        if (targetObj != null) {
            target = new GoToETarget();
            target.setRelation(PdfObjectParseUtil.parseStringFromDict("R", targetObj, true));
            target.setNameInTree(PdfObjectParseUtil.parseStringFromDict("N", targetObj, false));
            String page = PdfObjectParseUtil.parseStringFromDict("P", targetObj, false);
            if (page == null) {
                page = "" + PdfObjectParseUtil.parseIntegerFromDict("P", targetObj, false);
            }
            target.setPageNo(page);
            String annot = PdfObjectParseUtil.parseStringFromDict("A", targetObj, false);
            if (annot == null) {
                annot = "" + PdfObjectParseUtil.parseIntegerFromDict("A", targetObj, false);
            }
            target.setAnnotNo(annot);
            PDFObject subTargetObj = targetObj.getDictRef("T");
            if (subTargetObj != null && !list.contains(target)) {
                list.add(target);
                GoToETarget subTargetDictionary = this.parseTargetDistionary(subTargetObj, list);
                target.setTargetDictionary(subTargetDictionary);
            }
        } else if (this.file == null) {
            throw new PDFParseException("No target dictionary in GoToE action " + targetObj);
        }
        return target;
    }

    public GoToEAction(PDFDestination dest, String file, boolean newWindow) {
        super("GoToR");
        this.file = file;
        this.destination = dest;
        this.newWindow = newWindow;
    }

    public PDFDestination getDestination() {
        return this.destination;
    }

    public String getFile() {
        return this.file;
    }

    public boolean isNewWindow() {
        return this.newWindow;
    }

    public GoToETarget getTarget() {
        return this.target;
    }

    public static class GoToETarget {
        private String relation;
        private String nameInTree;
        private String pageNo;
        private String annotNo;
        private GoToETarget targetDictionary;

        public String getRelation() {
            return this.relation;
        }

        public void setRelation(String relation) {
            this.relation = relation;
        }

        public String getNameInTree() {
            return this.nameInTree;
        }

        public void setNameInTree(String nameInTree) {
            this.nameInTree = nameInTree;
        }

        public String getPageNo() {
            return this.pageNo;
        }

        public void setPageNo(String pageNo) {
            this.pageNo = pageNo;
        }

        public String getAnnotNo() {
            return this.annotNo;
        }

        public void setAnnotNo(String annotNo) {
            this.annotNo = annotNo;
        }

        public GoToETarget getTargetDictionary() {
            return this.targetDictionary;
        }

        public void setTargetDictionary(GoToETarget targetDictionary) {
            this.targetDictionary = targetDictionary;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof GoToETarget)) {
                return false;
            }
            if (super.equals(obj)) {
                return true;
            }
            GoToETarget that = (GoToETarget)obj;
            return String.valueOf(this.annotNo).equals(String.valueOf(that.annotNo)) && String.valueOf(this.nameInTree).equals(String.valueOf(that.nameInTree)) && String.valueOf(this.pageNo).equals(String.valueOf(that.pageNo)) && String.valueOf(this.relation).equals(String.valueOf(that.relation));
        }
    }
}

