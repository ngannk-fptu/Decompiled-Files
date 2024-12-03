/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.action;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.action.GoToAction;
import com.sun.pdfview.action.GoToEAction;
import com.sun.pdfview.action.GoToRAction;
import com.sun.pdfview.action.LaunchAction;
import com.sun.pdfview.action.UriAction;
import java.io.IOException;

public class PDFAction {
    private String type;
    private PDFObject next;

    public PDFAction(String type) {
        this.type = type;
    }

    public static PDFAction getAction(PDFObject obj, PDFObject root) throws IOException {
        PDFObject typeObj = obj.getDictRef("S");
        if (typeObj == null) {
            throw new PDFParseException("No action type in object: " + obj);
        }
        PDFAction action = null;
        String type = typeObj.getStringValue();
        if (type.equals("GoTo")) {
            action = new GoToAction(obj, root);
        } else if (type.equals("GoToE")) {
            action = new GoToEAction(obj, root);
        } else if (type.equals("GoToR")) {
            action = new GoToRAction(obj, root);
        } else if (type.equals("URI")) {
            action = new UriAction(obj, root);
        } else if (type.equals("Launch")) {
            action = new LaunchAction(obj, root);
        } else {
            throw new PDFParseException("Unknown Action type: " + type);
        }
        PDFObject nextObj = obj.getDictRef("Next");
        if (nextObj != null) {
            action.setNext(nextObj);
        }
        return action;
    }

    public String getType() {
        return this.type;
    }

    public PDFObject getNext() {
        return this.next;
    }

    public void setNext(PDFObject next) {
        this.next = next;
    }
}

