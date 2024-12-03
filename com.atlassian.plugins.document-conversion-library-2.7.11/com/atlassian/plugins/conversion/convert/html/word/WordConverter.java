/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.words.Document
 *  com.aspose.words.HtmlSaveOptions
 *  com.aspose.words.IImageSavingCallback
 *  com.aspose.words.IResourceLoadingCallback
 *  com.aspose.words.LoadOptions
 *  com.aspose.words.SaveOptions
 */
package com.atlassian.plugins.conversion.convert.html.word;

import com.aspose.words.Document;
import com.aspose.words.HtmlSaveOptions;
import com.aspose.words.IImageSavingCallback;
import com.aspose.words.IResourceLoadingCallback;
import com.aspose.words.LoadOptions;
import com.aspose.words.SaveOptions;
import com.atlassian.plugins.conversion.AsposeAware;
import com.atlassian.plugins.conversion.convert.html.HtmlConversionData;
import com.atlassian.plugins.conversion.convert.html.word.ImageSavingEventHandler;
import com.atlassian.plugins.conversion.convert.html.word.RestrictiveResourceLoadingCallback;
import com.atlassian.plugins.conversion.convert.html.word.XHtmlBodyExtractor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.xml.sax.InputSource;

public class WordConverter
extends AsposeAware {
    public static HtmlConversionData convertToHtml(InputStream inputStream, String imagePath) {
        try {
            HtmlConversionData data = new HtmlConversionData();
            LoadOptions restrictiveLoadOptions = new LoadOptions();
            restrictiveLoadOptions.setResourceLoadingCallback((IResourceLoadingCallback)new RestrictiveResourceLoadingCallback());
            Document document = new Document(inputStream, restrictiveLoadOptions);
            document.joinRunsWithSameFormatting();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            HtmlSaveOptions saveOptions = new HtmlSaveOptions(50);
            saveOptions.setEncoding(StandardCharsets.UTF_8);
            saveOptions.setImagesFolderAlias(imagePath);
            saveOptions.setExportHeadersFootersMode(0);
            ImageSavingEventHandler imageSavingEventHandler = new ImageSavingEventHandler();
            saveOptions.setImageSavingCallback((IImageSavingCallback)imageSavingEventHandler);
            saveOptions.setScaleImageToShapeSize(false);
            saveOptions.setImageResolution(96);
            saveOptions.setExportRoundtripInformation(false);
            document.save((OutputStream)output, (SaveOptions)saveOptions);
            for (Map.Entry<String, ByteArrayOutputStream> imageEntry : imageSavingEventHandler.getImageOutputStreams().entrySet()) {
                data.addImage(imageEntry.getKey(), imageEntry.getValue().toByteArray());
            }
            InputSource inputSource = new InputSource(new ByteArrayInputStream(output.toByteArray()));
            data.setHtml(new XHtmlBodyExtractor().extract(inputSource));
            return data;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

