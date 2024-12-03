/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.extractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import org.apache.poi.extractor.POIOLE2TextExtractor;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.Entry;
import org.apache.poi.poifs.filesystem.FileMagic;

public interface ExtractorProvider {
    public boolean accepts(FileMagic var1);

    public POITextExtractor create(File var1, String var2) throws IOException;

    public POITextExtractor create(InputStream var1, String var2) throws IOException;

    public POITextExtractor create(DirectoryNode var1, String var2) throws IOException;

    default public void identifyEmbeddedResources(POIOLE2TextExtractor ext, List<Entry> dirs, List<InputStream> nonPOIFS) throws IOException {
        throw new IllegalArgumentException("Error checking for Scratchpad embedded resources");
    }
}

