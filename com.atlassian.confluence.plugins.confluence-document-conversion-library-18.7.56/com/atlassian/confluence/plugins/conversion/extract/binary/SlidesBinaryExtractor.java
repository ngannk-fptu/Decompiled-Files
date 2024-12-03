/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.conversion.extract.binary;

import com.atlassian.confluence.plugins.conversion.extract.binary.AbstractBinaryExtractor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.hslf.usermodel.HSLFShape;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFSlideShowFactory;
import org.apache.poi.hslf.usermodel.HSLFTextParagraph;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.sl.extractor.SlideShowExtractor;

public class SlidesBinaryExtractor
extends AbstractBinaryExtractor {
    public static String extractText(InputStream inputStream) throws IOException {
        String string;
        POIFSFileSystem system = new POIFSFileSystem(inputStream);
        try {
            DirectoryNode root = system.getRoot();
            if (root.hasEntry("Pictures")) {
                root.getEntry("Pictures").delete();
                root.createDocument("Pictures", new ByteArrayInputStream(new byte[0]));
            }
            HSLFSlideShow slideShow = HSLFSlideShowFactory.createSlideShow(system);
            string = new SlideShowExtractor<HSLFShape, HSLFTextParagraph>(slideShow).getText();
        }
        catch (Throwable throwable) {
            try {
                try {
                    system.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (Exception e) {
                throw new IOException("Error reading content of PowerPoint document: " + e.getMessage(), e);
            }
        }
        system.close();
        return string;
    }
}

