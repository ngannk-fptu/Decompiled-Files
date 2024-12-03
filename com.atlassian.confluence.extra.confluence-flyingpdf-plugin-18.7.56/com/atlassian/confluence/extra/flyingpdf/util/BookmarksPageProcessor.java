/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.extra.flyingpdf.util;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.extra.flyingpdf.util.PdfPageProcessor;
import com.atlassian.confluence.extra.flyingpdf.util.UrlUtils;
import com.lowagie.text.pdf.PdfAction;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfDestination;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;
import com.lowagie.text.pdf.PdfString;
import java.util.Map;
import java.util.regex.Matcher;

@Internal
public class BookmarksPageProcessor
implements PdfPageProcessor {
    private final String spaceKey;
    private final Map<String, Integer> locationByTitle;
    private final String baseUrl;

    public BookmarksPageProcessor(String spaceKey, Map<String, Integer> locationByTitle, String baseUrl) {
        this.spaceKey = spaceKey;
        this.locationByTitle = locationByTitle;
        this.baseUrl = baseUrl;
    }

    @Override
    public void processPage(PdfReader reader, PdfStamper stamper, int page) {
        PdfDictionary pageDict = reader.getPageN(page);
        PdfArray annotations = pageDict.getAsArray(PdfName.ANNOTS);
        if (annotations == null || annotations.length() == 0) {
            return;
        }
        for (PdfObject annotation : annotations.getArrayList()) {
            Matcher matcher;
            PdfString currentUri;
            boolean isOnThisServer;
            PdfDictionary action;
            PdfDictionary annotationDictionary = (PdfDictionary)PdfReader.getPdfObject(annotation);
            if (!annotationDictionary.get(PdfName.SUBTYPE).equals(PdfName.LINK) || annotationDictionary.get(PdfName.A) == null || !(action = (PdfDictionary)annotationDictionary.get(PdfName.A)).get(PdfName.S).equals(PdfName.URI) || !(isOnThisServer = (currentUri = (PdfString)action.get(PdfName.URI)).toString().trim().startsWith("/") || currentUri.toString().trim().startsWith(this.baseUrl)) || !(matcher = UrlUtils.pageDisplayUrlPattern.matcher(currentUri.toUnicodeString())).find()) continue;
            String spaceKey = matcher.group(1);
            String pageTitle = UrlUtils.decodeTitle(matcher.group(2));
            if (!this.spaceKey.equals(spaceKey) || !this.locationByTitle.containsKey(pageTitle)) continue;
            Integer location = this.locationByTitle.get(pageTitle);
            PdfAction newAction = PdfAction.gotoLocalPage(location, new PdfDestination(0), stamper.getWriter());
            annotationDictionary.put(PdfName.A, newAction);
        }
    }
}

