/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.optionalcontent;

import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.documentinterchange.markedcontent.PDPropertyList;
import org.apache.pdfbox.rendering.RenderDestination;

public class PDOptionalContentGroup
extends PDPropertyList {
    public PDOptionalContentGroup(String name) {
        this.dict.setItem(COSName.TYPE, (COSBase)COSName.OCG);
        this.setName(name);
    }

    public PDOptionalContentGroup(COSDictionary dict) {
        super(dict);
        if (!dict.getItem(COSName.TYPE).equals(COSName.OCG)) {
            throw new IllegalArgumentException("Provided dictionary is not of type '" + COSName.OCG + "'");
        }
    }

    public String getName() {
        return this.dict.getString(COSName.NAME);
    }

    public void setName(String name) {
        this.dict.setString(COSName.NAME, name);
    }

    public RenderState getRenderState(RenderDestination destination) {
        COSName state = null;
        COSDictionary usage = (COSDictionary)this.dict.getDictionaryObject("Usage");
        if (usage != null) {
            if (RenderDestination.PRINT.equals((Object)destination)) {
                COSDictionary print = (COSDictionary)usage.getDictionaryObject("Print");
                state = print == null ? null : (COSName)print.getDictionaryObject("PrintState");
            } else if (RenderDestination.VIEW.equals((Object)destination)) {
                COSDictionary view = (COSDictionary)usage.getDictionaryObject("View");
                COSName cOSName = state = view == null ? null : (COSName)view.getDictionaryObject("ViewState");
            }
            if (state == null) {
                COSDictionary export = (COSDictionary)usage.getDictionaryObject("Export");
                state = export == null ? null : (COSName)export.getDictionaryObject("ExportState");
            }
        }
        return state == null ? null : RenderState.valueOf(state);
    }

    public String toString() {
        return super.toString() + " (" + this.getName() + ")";
    }

    public static enum RenderState {
        ON(COSName.ON),
        OFF(COSName.OFF);

        private final COSName name;

        private RenderState(COSName value) {
            this.name = value;
        }

        public static RenderState valueOf(COSName state) {
            if (state == null) {
                return null;
            }
            return RenderState.valueOf(state.getName().toUpperCase());
        }

        public COSName getName() {
            return this.name;
        }
    }
}

