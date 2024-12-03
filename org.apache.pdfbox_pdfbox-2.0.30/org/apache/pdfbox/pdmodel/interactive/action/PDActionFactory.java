/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.action;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionEmbeddedGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionHide;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionImportData;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionJavaScript;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionLaunch;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionMovie;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionNamed;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionRemoteGoTo;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionResetForm;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionSound;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionSubmitForm;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionThread;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;

public final class PDActionFactory {
    private PDActionFactory() {
    }

    public static PDAction createAction(COSDictionary action) {
        PDAction retval = null;
        if (action != null) {
            String type = action.getNameAsString(COSName.S);
            if ("JavaScript".equals(type)) {
                retval = new PDActionJavaScript(action);
            } else if ("GoTo".equals(type)) {
                retval = new PDActionGoTo(action);
            } else if ("Launch".equals(type)) {
                retval = new PDActionLaunch(action);
            } else if ("GoToR".equals(type)) {
                retval = new PDActionRemoteGoTo(action);
            } else if ("URI".equals(type)) {
                retval = new PDActionURI(action);
            } else if ("Named".equals(type)) {
                retval = new PDActionNamed(action);
            } else if ("Sound".equals(type)) {
                retval = new PDActionSound(action);
            } else if ("Movie".equals(type)) {
                retval = new PDActionMovie(action);
            } else if ("ImportData".equals(type)) {
                retval = new PDActionImportData(action);
            } else if ("ResetForm".equals(type)) {
                retval = new PDActionResetForm(action);
            } else if ("Hide".equals(type)) {
                retval = new PDActionHide(action);
            } else if ("SubmitForm".equals(type)) {
                retval = new PDActionSubmitForm(action);
            } else if ("Thread".equals(type)) {
                retval = new PDActionThread(action);
            } else if ("GoToE".equals(type)) {
                retval = new PDActionEmbeddedGoTo(action);
            }
        }
        return retval;
    }
}

