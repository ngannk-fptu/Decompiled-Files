/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;

public class PDDeviceNProcess {
    private final COSDictionary dictionary;

    public PDDeviceNProcess() {
        this.dictionary = new COSDictionary();
    }

    public PDDeviceNProcess(COSDictionary attributes) {
        this.dictionary = attributes;
    }

    public COSDictionary getCOSDictionary() {
        return this.dictionary;
    }

    public PDColorSpace getColorSpace() throws IOException {
        COSBase cosColorSpace = this.dictionary.getDictionaryObject(COSName.COLORSPACE);
        if (cosColorSpace == null) {
            return null;
        }
        return PDColorSpace.create(cosColorSpace);
    }

    public List<String> getComponents() {
        COSArray cosComponents = this.dictionary.getCOSArray(COSName.COMPONENTS);
        if (cosComponents == null) {
            return new ArrayList<String>(0);
        }
        ArrayList<String> components = new ArrayList<String>(cosComponents.size());
        for (COSBase name : cosComponents) {
            components.add(((COSName)name).getName());
        }
        return components;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Process{");
        try {
            sb.append(this.getColorSpace());
            for (String component : this.getComponents()) {
                sb.append(" \"");
                sb.append(component);
                sb.append('\"');
            }
        }
        catch (IOException e) {
            sb.append("ERROR");
        }
        sb.append('}');
        return sb.toString();
    }
}

