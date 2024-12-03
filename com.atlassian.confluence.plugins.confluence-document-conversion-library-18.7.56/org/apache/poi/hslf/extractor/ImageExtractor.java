/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.extractor;

import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.poi.hslf.usermodel.HSLFPictureData;
import org.apache.poi.hslf.usermodel.HSLFSlideShow;
import org.apache.poi.hslf.usermodel.HSLFSlideShowImpl;
import org.apache.poi.sl.usermodel.PictureData;

public final class ImageExtractor {
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage:");
            System.err.println("\tImageExtractor <file>");
            return;
        }
        try (HSLFSlideShow ppt = new HSLFSlideShow(new HSLFSlideShowImpl(args[0]));){
            int i = 0;
            for (HSLFPictureData pict : ppt.getPictureData()) {
                byte[] data = pict.getData();
                PictureData.PictureType type = pict.getType();
                FileOutputStream out = new FileOutputStream("pict_" + i++ + type.extension);
                Throwable throwable = null;
                try {
                    out.write(data);
                }
                catch (Throwable throwable2) {
                    throwable = throwable2;
                    throw throwable2;
                }
                finally {
                    if (out == null) continue;
                    if (throwable != null) {
                        try {
                            out.close();
                        }
                        catch (Throwable throwable3) {
                            throwable.addSuppressed(throwable3);
                        }
                        continue;
                    }
                    out.close();
                }
            }
        }
    }
}

