/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bandana.BandanaContext
 */
package com.atlassian.confluence.extra.flyingpdf.config;

import com.atlassian.bandana.BandanaContext;

public interface PdfExportSettingsManager {
    public String getStyle(BandanaContext var1);

    public void setStyle(BandanaContext var1, String var2);

    public String getTitlePage(BandanaContext var1);

    public void setTitlePage(BandanaContext var1, String var2);

    public String getHeader(BandanaContext var1);

    public void setHeader(BandanaContext var1, String var2);

    public String getFooter(BandanaContext var1);

    public void setFooter(BandanaContext var1, String var2);
}

