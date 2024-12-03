/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.apache.fontbox.ttf.TrueTypeFont
 */
package org.apache.pdfbox.pdmodel.fixup.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.fixup.processor.AbstractProcessor;
import org.apache.pdfbox.pdmodel.font.FontMapper;
import org.apache.pdfbox.pdmodel.font.FontMappers;
import org.apache.pdfbox.pdmodel.font.FontMapping;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDFieldFactory;
import org.apache.pdfbox.pdmodel.interactive.form.PDVariableText;

public class AcroFormOrphanWidgetsProcessor
extends AbstractProcessor {
    private static final Log LOG = LogFactory.getLog(AcroFormOrphanWidgetsProcessor.class);

    public AcroFormOrphanWidgetsProcessor(PDDocument document) {
        super(document);
    }

    @Override
    public void process() {
        PDAcroForm acroForm = this.document.getDocumentCatalog().getAcroForm(null);
        if (acroForm != null) {
            this.resolveFieldsFromWidgets(acroForm);
        }
    }

    private void resolveFieldsFromWidgets(PDAcroForm acroForm) {
        LOG.debug((Object)"rebuilding fields from widgets");
        PDResources resources = acroForm.getDefaultResources();
        if (resources == null) {
            LOG.debug((Object)"AcroForm default resources is null");
            return;
        }
        ArrayList<PDField> fields = new ArrayList<PDField>();
        HashMap<String, PDField> nonTerminalFieldsMap = new HashMap<String, PDField>();
        for (PDPage page : this.document.getPages()) {
            try {
                this.handleAnnotations(acroForm, resources, fields, page.getAnnotations(), nonTerminalFieldsMap);
            }
            catch (IOException ioe) {
                LOG.debug((Object)("couldn't read annotations for page " + ioe.getMessage()));
            }
        }
        acroForm.setFields(fields);
        for (PDField field : acroForm.getFieldTree()) {
            if (!(field instanceof PDVariableText)) continue;
            this.ensureFontResources(resources, (PDVariableText)field);
        }
    }

    private void handleAnnotations(PDAcroForm acroForm, PDResources acroFormResources, List<PDField> fields, List<PDAnnotation> annotations, Map<String, PDField> nonTerminalFieldsMap) {
        for (PDAnnotation annot : annotations) {
            if (!(annot instanceof PDAnnotationWidget)) continue;
            this.addFontFromWidget(acroFormResources, annot);
            COSDictionary parent = annot.getCOSObject().getCOSDictionary(COSName.PARENT);
            if (parent != null) {
                PDField resolvedField = this.resolveNonRootField(acroForm, parent, nonTerminalFieldsMap);
                if (resolvedField == null) continue;
                fields.add(resolvedField);
                continue;
            }
            fields.add(PDFieldFactory.createField(acroForm, annot.getCOSObject(), null));
        }
    }

    private void addFontFromWidget(PDResources acroFormResources, PDAnnotation annotation) {
        PDAppearanceStream normalAppearanceStream = annotation.getNormalAppearanceStream();
        if (normalAppearanceStream == null) {
            return;
        }
        PDResources widgetResources = normalAppearanceStream.getResources();
        if (widgetResources == null) {
            return;
        }
        for (COSName fontName : widgetResources.getFontNames()) {
            if (!fontName.getName().startsWith("+")) {
                try {
                    if (acroFormResources.getFont(fontName) != null) continue;
                    acroFormResources.put(fontName, widgetResources.getFont(fontName));
                    LOG.debug((Object)("added font resource to AcroForm from widget for font name " + fontName.getName()));
                }
                catch (IOException ioe) {
                    LOG.debug((Object)("unable to add font to AcroForm for font name " + fontName.getName()));
                }
                continue;
            }
            LOG.debug((Object)("font resource for widget was a subsetted font - ignored: " + fontName.getName()));
        }
    }

    private PDField resolveNonRootField(PDAcroForm acroForm, COSDictionary parent, Map<String, PDField> nonTerminalFieldsMap) {
        while (parent.containsKey(COSName.PARENT)) {
            if ((parent = parent.getCOSDictionary(COSName.PARENT)) != null) continue;
            return null;
        }
        if (nonTerminalFieldsMap.get(parent.getString(COSName.T)) == null) {
            PDField field = PDFieldFactory.createField(acroForm, parent, null);
            if (field != null) {
                nonTerminalFieldsMap.put(field.getFullyQualifiedName(), field);
            }
            return field;
        }
        return null;
    }

    private void ensureFontResources(PDResources defaultResources, PDVariableText field) {
        String daString = field.getDefaultAppearance();
        if (daString.startsWith("/") && daString.length() > 1) {
            COSName fontName = COSName.getPDFName(daString.substring(1, daString.indexOf(" ")));
            try {
                if (defaultResources.getFont(fontName) == null) {
                    LOG.debug((Object)("trying to add missing font resource for field " + field.getFullyQualifiedName()));
                    FontMapper mapper = FontMappers.instance();
                    FontMapping<TrueTypeFont> fontMapping = mapper.getTrueTypeFont(fontName.getName(), null);
                    if (fontMapping != null) {
                        PDType0Font pdFont = PDType0Font.load(this.document, fontMapping.getFont(), false);
                        LOG.debug((Object)("looked up font for " + fontName.getName() + " - found " + fontMapping.getFont().getName()));
                        defaultResources.put(fontName, pdFont);
                    } else {
                        LOG.debug((Object)("no suitable font found for field " + field.getFullyQualifiedName() + " for font name " + fontName.getName()));
                    }
                }
            }
            catch (IOException ioe) {
                LOG.debug((Object)("Unable to handle font resources for field " + field.getFullyQualifiedName() + ": " + ioe.getMessage()));
            }
        }
    }
}

