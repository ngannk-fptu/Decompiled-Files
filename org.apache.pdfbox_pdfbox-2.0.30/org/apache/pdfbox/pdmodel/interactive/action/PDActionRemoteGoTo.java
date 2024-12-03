/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.action;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;
import org.apache.pdfbox.pdmodel.interactive.action.OpenMode;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;

public class PDActionRemoteGoTo
extends PDAction {
    public static final String SUB_TYPE = "GoToR";

    public PDActionRemoteGoTo() {
        this.setSubType(SUB_TYPE);
    }

    public PDActionRemoteGoTo(COSDictionary a) {
        super(a);
    }

    @Deprecated
    public String getS() {
        return this.action.getNameAsString(COSName.S);
    }

    @Deprecated
    public void setS(String s) {
        this.action.setName(COSName.S, s);
    }

    public PDFileSpecification getFile() throws IOException {
        return PDFileSpecification.createFS(this.action.getDictionaryObject(COSName.F));
    }

    public void setFile(PDFileSpecification fs) {
        this.action.setItem(COSName.F, (COSObjectable)fs);
    }

    public COSBase getD() {
        return this.action.getDictionaryObject(COSName.D);
    }

    public void setD(COSBase d) {
        this.action.setItem(COSName.D, d);
    }

    @Deprecated
    public boolean shouldOpenInNewWindow() {
        return this.action.getBoolean(COSName.NEW_WINDOW, true);
    }

    @Deprecated
    public void setOpenInNewWindow(boolean value) {
        this.action.setBoolean(COSName.NEW_WINDOW, value);
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
}

