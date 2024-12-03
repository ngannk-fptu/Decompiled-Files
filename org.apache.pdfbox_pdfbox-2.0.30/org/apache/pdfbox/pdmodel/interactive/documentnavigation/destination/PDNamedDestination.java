/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination;

import java.io.IOException;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;

public class PDNamedDestination
extends PDDestination {
    private COSBase namedDestination;

    public PDNamedDestination(COSString dest) {
        this.namedDestination = dest;
    }

    public PDNamedDestination(COSName dest) {
        this.namedDestination = dest;
    }

    public PDNamedDestination() {
    }

    public PDNamedDestination(String dest) {
        this.namedDestination = new COSString(dest);
    }

    @Override
    public COSBase getCOSObject() {
        return this.namedDestination;
    }

    public String getNamedDestination() {
        String retval = null;
        if (this.namedDestination instanceof COSString) {
            retval = ((COSString)this.namedDestination).getString();
        } else if (this.namedDestination instanceof COSName) {
            retval = ((COSName)this.namedDestination).getName();
        }
        return retval;
    }

    public void setNamedDestination(String dest) throws IOException {
        this.namedDestination = dest == null ? null : new COSString(dest);
    }
}

