/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.action;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;
import org.apache.pdfbox.pdmodel.interactive.action.OpenMode;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDTargetDirectory;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;

public class PDActionEmbeddedGoTo
extends PDAction {
    public static final String SUB_TYPE = "GoToE";

    public PDActionEmbeddedGoTo() {
        this.setSubType(SUB_TYPE);
    }

    public PDActionEmbeddedGoTo(COSDictionary a) {
        super(a);
    }

    public PDDestination getDestination() throws IOException {
        return PDDestination.create(this.getCOSObject().getDictionaryObject(COSName.D));
    }

    public void setDestination(PDDestination d) {
        COSBase page;
        PDPageDestination pageDest;
        COSArray destArray;
        if (d instanceof PDPageDestination && (destArray = (pageDest = (PDPageDestination)d).getCOSObject()).size() >= 1 && !((page = destArray.getObject(0)) instanceof COSInteger)) {
            throw new IllegalArgumentException("Destination of a GoToE action must be an integer");
        }
        this.getCOSObject().setItem(COSName.D, (COSObjectable)d);
    }

    public PDFileSpecification getFile() throws IOException {
        return PDFileSpecification.createFS(this.getCOSObject().getDictionaryObject(COSName.F));
    }

    public void setFile(PDFileSpecification fs) {
        this.getCOSObject().setItem(COSName.F, (COSObjectable)fs);
    }

    public OpenMode getOpenInNewWindow() {
        if (this.getCOSObject().getDictionaryObject(COSName.NEW_WINDOW) instanceof COSBoolean) {
            COSBoolean b = (COSBoolean)this.getCOSObject().getDictionaryObject(COSName.NEW_WINDOW);
            return b.getValue() ? OpenMode.NEW_WINDOW : OpenMode.SAME_WINDOW;
        }
        return OpenMode.USER_PREFERENCE;
    }

    public void setOpenInNewWindow(OpenMode value) {
        if (null == value) {
            this.getCOSObject().removeItem(COSName.NEW_WINDOW);
            return;
        }
        switch (value) {
            case USER_PREFERENCE: {
                this.getCOSObject().removeItem(COSName.NEW_WINDOW);
                break;
            }
            case SAME_WINDOW: {
                this.getCOSObject().setBoolean(COSName.NEW_WINDOW, false);
                break;
            }
            case NEW_WINDOW: {
                this.getCOSObject().setBoolean(COSName.NEW_WINDOW, true);
                break;
            }
        }
    }

    public PDTargetDirectory getTargetDirectory() {
        COSBase base = this.getCOSObject().getDictionaryObject(COSName.T);
        if (base instanceof COSDictionary) {
            return new PDTargetDirectory((COSDictionary)base);
        }
        return null;
    }

    public void setTargetDirectory(PDTargetDirectory targetDirectory) {
        this.getCOSObject().setItem(COSName.T, (COSObjectable)targetDirectory);
    }
}

