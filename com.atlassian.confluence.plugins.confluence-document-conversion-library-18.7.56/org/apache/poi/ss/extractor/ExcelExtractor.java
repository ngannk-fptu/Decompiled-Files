/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.extractor;

public interface ExcelExtractor {
    public void setIncludeSheetNames(boolean var1);

    public void setFormulasNotResults(boolean var1);

    public void setIncludeHeadersFooters(boolean var1);

    public void setIncludeCellComments(boolean var1);

    public String getText();
}

