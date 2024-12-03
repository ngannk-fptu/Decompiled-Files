/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.action;

import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.PDDestinationOrAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionFactory;

public abstract class PDAction
implements PDDestinationOrAction {
    public static final String TYPE = "Action";
    protected COSDictionary action;

    public PDAction() {
        this.action = new COSDictionary();
        this.setType(TYPE);
    }

    public PDAction(COSDictionary a) {
        this.action = a;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.action;
    }

    public String getType() {
        return this.action.getNameAsString(COSName.TYPE);
    }

    public final void setType(String type) {
        this.action.setName(COSName.TYPE, type);
    }

    public String getSubType() {
        return this.action.getNameAsString(COSName.S);
    }

    public void setSubType(String s) {
        this.action.setName(COSName.S, s);
    }

    public List<PDAction> getNext() {
        COSArrayList<PDAction> retval = null;
        COSBase next = this.action.getDictionaryObject(COSName.NEXT);
        if (next instanceof COSDictionary) {
            PDAction pdAction = PDActionFactory.createAction((COSDictionary)next);
            retval = new COSArrayList<PDAction>(pdAction, next, this.action, COSName.NEXT);
        } else if (next instanceof COSArray) {
            COSArray array = (COSArray)next;
            ArrayList<PDAction> actions = new ArrayList<PDAction>(array.size());
            for (int i = 0; i < array.size(); ++i) {
                actions.add(PDActionFactory.createAction((COSDictionary)array.getObject(i)));
            }
            retval = new COSArrayList(actions, array);
        }
        return retval;
    }

    public void setNext(List<?> next) {
        this.action.setItem(COSName.NEXT, (COSBase)COSArrayList.converterToCOSArray(next));
    }
}

