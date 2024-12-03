/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.words.IImageSavingCallback
 *  com.aspose.words.ImageSavingArgs
 */
package com.atlassian.plugins.conversion.convert.html.word;

import com.aspose.words.IImageSavingCallback;
import com.aspose.words.ImageSavingArgs;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ImageSavingEventHandler
implements IImageSavingCallback {
    private final Map<String, ByteArrayOutputStream> imageOutputStreams = new HashMap<String, ByteArrayOutputStream>();
    private int imageCounter = 1;

    public Map<String, ByteArrayOutputStream> getImageOutputStreams() {
        return Collections.unmodifiableMap(this.imageOutputStreams);
    }

    public void imageSaving(ImageSavingArgs args) throws Exception {
        String originalFileName = args.getImageFileName();
        int idxDot = originalFileName.lastIndexOf(46);
        String extension = idxDot > -1 ? originalFileName.substring(idxDot) : "";
        String filename = "image-" + this.imageCounter++ + extension;
        args.setImageFileName(filename);
        ByteArrayOutputStream imageOutputStream = new ByteArrayOutputStream();
        this.imageOutputStreams.put(filename, imageOutputStream);
        args.setImageStream((OutputStream)imageOutputStream);
    }
}

