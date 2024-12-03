/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.action;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBoolean;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.filespecification.PDFileSpecification;
import org.apache.pdfbox.pdmodel.interactive.action.OpenMode;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDWindowsLaunchParams;

public class PDActionLaunch
extends PDAction {
    public static final String SUB_TYPE = "Launch";

    public PDActionLaunch() {
        this.setSubType(SUB_TYPE);
    }

    public PDActionLaunch(COSDictionary a) {
        super(a);
    }

    public PDFileSpecification getFile() throws IOException {
        return PDFileSpecification.createFS(this.getCOSObject().getDictionaryObject(COSName.F));
    }

    public void setFile(PDFileSpecification fs) {
        this.getCOSObject().setItem(COSName.F, (COSObjectable)fs);
    }

    public PDWindowsLaunchParams getWinLaunchParams() {
        COSDictionary win = (COSDictionary)this.action.getDictionaryObject("Win");
        PDWindowsLaunchParams retval = null;
        if (win != null) {
            retval = new PDWindowsLaunchParams(win);
        }
        return retval;
    }

    public void setWinLaunchParams(PDWindowsLaunchParams win) {
        this.action.setItem("Win", (COSObjectable)win);
    }

    public String getF() {
        return this.action.getString(COSName.F);
    }

    public void setF(String f) {
        this.action.setString(COSName.F, f);
    }

    public String getD() {
        return this.action.getString(COSName.D);
    }

    public void setD(String d) {
        this.action.setString(COSName.D, d);
    }

    public String getO() {
        return this.action.getString(COSName.O);
    }

    public void setO(String o) {
        this.action.setString(COSName.O, o);
    }

    public String getP() {
        return this.action.getString(COSName.P);
    }

    public void setP(String p) {
        this.action.setString(COSName.P, p);
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

