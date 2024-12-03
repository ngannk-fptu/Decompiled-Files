/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ooxml.extractor;

import java.io.File;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.extractor.POITextExtractor;

public final class CommandLineTextExtractor {
    public static final String DIVIDER = "=======================";

    private CommandLineTextExtractor() {
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("   CommandLineTextExtractor <filename> [filename] [filename]");
            System.exit(1);
        }
        for (String arg : args) {
            System.out.println(DIVIDER);
            File f = new File(arg);
            System.out.println(f);
            try (POITextExtractor extractor = ExtractorFactory.createExtractor(f);){
                POITextExtractor metadataExtractor = extractor.getMetadataTextExtractor();
                System.out.println("   =======================");
                String metaData = metadataExtractor.getText();
                System.out.println(metaData);
                System.out.println("   =======================");
                String text = extractor.getText();
                System.out.println(text);
                System.out.println(DIVIDER);
                System.out.println("Had " + metaData.length() + " characters of metadata and " + text.length() + " characters of text");
            }
        }
    }
}

