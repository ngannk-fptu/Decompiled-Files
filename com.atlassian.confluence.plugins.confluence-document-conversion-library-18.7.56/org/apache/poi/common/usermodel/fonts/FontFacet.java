/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.common.usermodel.fonts;

public interface FontFacet {
    default public int getWeight() {
        return 400;
    }

    default public void setWeight(int weight) {
        throw new UnsupportedOperationException("FontFacet is read-only.");
    }

    default public boolean isItalic() {
        return false;
    }

    default public void setItalic(boolean italic) {
        throw new UnsupportedOperationException("FontFacet is read-only.");
    }

    default public Object getFontData() {
        return null;
    }
}

