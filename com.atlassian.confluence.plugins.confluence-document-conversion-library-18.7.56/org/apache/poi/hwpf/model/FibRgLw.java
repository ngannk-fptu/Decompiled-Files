/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import org.apache.poi.hwpf.model.SubdocumentType;

public interface FibRgLw {
    public int getCbMac();

    public int getSubdocumentTextStreamLength(SubdocumentType var1);

    public void setCbMac(int var1);

    public void setSubdocumentTextStreamLength(SubdocumentType var1, int var2);
}

