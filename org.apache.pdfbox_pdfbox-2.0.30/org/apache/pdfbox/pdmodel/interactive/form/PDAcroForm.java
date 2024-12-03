/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.fdf.FDFCatalog;
import org.apache.pdfbox.pdmodel.fdf.FDFDictionary;
import org.apache.pdfbox.pdmodel.fdf.FDFDocument;
import org.apache.pdfbox.pdmodel.fdf.FDFField;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAppearanceStream;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDFieldTree;
import org.apache.pdfbox.pdmodel.interactive.form.PDTerminalField;
import org.apache.pdfbox.pdmodel.interactive.form.PDXFAResource;
import org.apache.pdfbox.pdmodel.interactive.form.ScriptingHandler;
import org.apache.pdfbox.util.Matrix;

public final class PDAcroForm
implements COSObjectable {
    private static final Log LOG = LogFactory.getLog(PDAcroForm.class);
    private static final int FLAG_SIGNATURES_EXIST = 1;
    private static final int FLAG_APPEND_ONLY = 2;
    private final PDDocument document;
    private final COSDictionary dictionary;
    private Map<String, PDField> fieldCache;
    private ScriptingHandler scriptingHandler;
    private final Map<COSName, SoftReference<PDFont>> directFontCache = new HashMap<COSName, SoftReference<PDFont>>();

    public PDAcroForm(PDDocument doc) {
        this.document = doc;
        this.dictionary = new COSDictionary();
        this.dictionary.setItem(COSName.FIELDS, (COSBase)new COSArray());
    }

    public PDAcroForm(PDDocument doc, COSDictionary form) {
        this.document = doc;
        this.dictionary = form;
    }

    PDDocument getDocument() {
        return this.document;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public void importFDF(FDFDocument fdf) throws IOException {
        List<FDFField> fields = fdf.getCatalog().getFDF().getFields();
        if (fields != null) {
            for (FDFField field : fields) {
                FDFField fdfField = field;
                PDField docField = this.getField(fdfField.getPartialFieldName());
                if (docField == null) continue;
                docField.importFDF(fdfField);
            }
        }
    }

    public FDFDocument exportFDF() throws IOException {
        FDFDocument fdf = new FDFDocument();
        FDFCatalog catalog = fdf.getCatalog();
        FDFDictionary fdfDict = new FDFDictionary();
        catalog.setFDF(fdfDict);
        List<PDField> fields = this.getFields();
        ArrayList<FDFField> fdfFields = new ArrayList<FDFField>(fields.size());
        for (PDField field : fields) {
            fdfFields.add(field.exportFDF());
        }
        fdfDict.setID(this.document.getDocument().getDocumentID());
        if (!fdfFields.isEmpty()) {
            fdfDict.setFields(fdfFields);
        }
        return fdf;
    }

    public void flatten() throws IOException {
        if (this.xfaIsDynamic()) {
            LOG.warn((Object)"Flatten for a dynamix XFA form is not supported");
            return;
        }
        ArrayList<PDField> fields = new ArrayList<PDField>();
        for (PDField field : this.getFieldTree()) {
            fields.add(field);
        }
        this.flatten(fields, false);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void flatten(List<PDField> fields, boolean refreshAppearances) throws IOException {
        if (fields.isEmpty()) {
            return;
        }
        if (!refreshAppearances && this.getNeedAppearances()) {
            LOG.warn((Object)"acroForm.getNeedAppearances() returns true, visual field appearances may not have been set");
            LOG.warn((Object)"call acroForm.refreshAppearances() or use the flatten() method with refreshAppearances parameter");
        }
        if (this.xfaIsDynamic()) {
            LOG.warn((Object)"Flatten for a dynamix XFA form is not supported");
            return;
        }
        if (refreshAppearances) {
            this.refreshAppearances(fields);
        }
        PDPageTree pages = this.document.getPages();
        Map<COSDictionary, Set<COSDictionary>> pagesWidgetsMap = this.buildPagesWidgetsMap(fields, pages);
        for (PDPage page : pages) {
            Set<COSDictionary> widgetsForPageMap = pagesWidgetsMap.get(page.getCOSObject());
            boolean isContentStreamWrapped = false;
            ArrayList<PDAnnotation> annotations = new ArrayList<PDAnnotation>();
            for (PDAnnotation annotation : page.getAnnotations()) {
                if (widgetsForPageMap == null || !widgetsForPageMap.contains(annotation.getCOSObject())) {
                    annotations.add(annotation);
                    continue;
                }
                if (!this.isVisibleAnnotation(annotation)) continue;
                PDPageContentStream contentStream = new PDPageContentStream(this.document, page, PDPageContentStream.AppendMode.APPEND, true, !isContentStreamWrapped);
                try {
                    isContentStreamWrapped = true;
                    PDAppearanceStream appearanceStream = annotation.getNormalAppearanceStream();
                    PDFormXObject fieldObject = new PDFormXObject(appearanceStream.getCOSObject());
                    contentStream.saveGraphicsState();
                    Matrix transformationMatrix = this.resolveTransformationMatrix(annotation, appearanceStream);
                    contentStream.transform(transformationMatrix);
                    contentStream.drawForm(fieldObject);
                    contentStream.restoreGraphicsState();
                }
                finally {
                    contentStream.close();
                }
            }
            page.setAnnotations(annotations);
        }
        this.removeFields(fields);
        this.dictionary.removeItem(COSName.XFA);
        if (this.document.getSignatureDictionaries().isEmpty()) {
            this.getCOSObject().removeItem(COSName.SIG_FLAGS);
        }
    }

    private boolean isVisibleAnnotation(PDAnnotation annotation) {
        if (annotation.isInvisible() || annotation.isHidden()) {
            return false;
        }
        PDAppearanceStream normalAppearanceStream = annotation.getNormalAppearanceStream();
        if (normalAppearanceStream == null) {
            return false;
        }
        PDRectangle bbox = normalAppearanceStream.getBBox();
        return bbox != null && bbox.getWidth() > 0.0f && bbox.getHeight() > 0.0f;
    }

    public void refreshAppearances() throws IOException {
        for (PDField field : this.getFieldTree()) {
            if (!(field instanceof PDTerminalField)) continue;
            ((PDTerminalField)field).constructAppearances();
        }
    }

    public void refreshAppearances(List<PDField> fields) throws IOException {
        for (PDField field : fields) {
            if (!(field instanceof PDTerminalField)) continue;
            ((PDTerminalField)field).constructAppearances();
        }
    }

    public List<PDField> getFields() {
        COSArray cosFields = this.dictionary.getCOSArray(COSName.FIELDS);
        if (cosFields == null) {
            return Collections.emptyList();
        }
        ArrayList<PDField> pdFields = new ArrayList<PDField>();
        for (int i = 0; i < cosFields.size(); ++i) {
            PDField field;
            COSBase element = cosFields.getObject(i);
            if (!(element instanceof COSDictionary) || (field = PDField.fromDictionary(this, (COSDictionary)element, null)) == null) continue;
            pdFields.add(field);
        }
        return new COSArrayList<PDField>(pdFields, cosFields);
    }

    public void setFields(List<PDField> fields) {
        this.dictionary.setItem(COSName.FIELDS, (COSBase)COSArrayList.converterToCOSArray(fields));
    }

    public Iterator<PDField> getFieldIterator() {
        return new PDFieldTree(this).iterator();
    }

    public PDFieldTree getFieldTree() {
        return new PDFieldTree(this);
    }

    public void setCacheFields(boolean cache) {
        if (cache) {
            this.fieldCache = new HashMap<String, PDField>();
            for (PDField field : this.getFieldTree()) {
                this.fieldCache.put(field.getFullyQualifiedName(), field);
            }
        } else {
            this.fieldCache = null;
        }
    }

    public boolean isCachingFields() {
        return this.fieldCache != null;
    }

    public PDField getField(String fullyQualifiedName) {
        if (this.fieldCache != null) {
            return this.fieldCache.get(fullyQualifiedName);
        }
        for (PDField field : this.getFieldTree()) {
            if (!field.getFullyQualifiedName().equals(fullyQualifiedName)) continue;
            return field;
        }
        return null;
    }

    public String getDefaultAppearance() {
        return this.dictionary.getString(COSName.DA, "");
    }

    public void setDefaultAppearance(String daValue) {
        this.dictionary.setString(COSName.DA, daValue);
    }

    public boolean getNeedAppearances() {
        return this.dictionary.getBoolean(COSName.NEED_APPEARANCES, false);
    }

    public void setNeedAppearances(Boolean value) {
        this.dictionary.setBoolean(COSName.NEED_APPEARANCES, (boolean)value);
    }

    public PDResources getDefaultResources() {
        PDResources retval = null;
        COSBase base = this.dictionary.getDictionaryObject(COSName.DR);
        if (base instanceof COSDictionary) {
            retval = new PDResources((COSDictionary)base, this.document.getResourceCache(), this.directFontCache);
        }
        return retval;
    }

    public void setDefaultResources(PDResources dr) {
        this.dictionary.setItem(COSName.DR, (COSObjectable)dr);
    }

    public boolean hasXFA() {
        return this.dictionary.containsKey(COSName.XFA);
    }

    public boolean xfaIsDynamic() {
        return this.hasXFA() && this.getFields().isEmpty();
    }

    public PDXFAResource getXFA() {
        PDXFAResource xfa = null;
        COSBase base = this.dictionary.getDictionaryObject(COSName.XFA);
        if (base != null) {
            xfa = new PDXFAResource(base);
        }
        return xfa;
    }

    public void setXFA(PDXFAResource xfa) {
        this.dictionary.setItem(COSName.XFA, (COSObjectable)xfa);
    }

    public int getQ() {
        int retval = 0;
        COSNumber number = (COSNumber)this.dictionary.getDictionaryObject(COSName.Q);
        if (number != null) {
            retval = number.intValue();
        }
        return retval;
    }

    public void setQ(int q) {
        this.dictionary.setInt(COSName.Q, q);
    }

    public boolean isSignaturesExist() {
        return this.dictionary.getFlag(COSName.SIG_FLAGS, 1);
    }

    public void setSignaturesExist(boolean signaturesExist) {
        this.dictionary.setFlag(COSName.SIG_FLAGS, 1, signaturesExist);
    }

    public boolean isAppendOnly() {
        return this.dictionary.getFlag(COSName.SIG_FLAGS, 2);
    }

    public void setAppendOnly(boolean appendOnly) {
        this.dictionary.setFlag(COSName.SIG_FLAGS, 2, appendOnly);
    }

    public List<PDField> getCalcOrder() {
        COSArray co = this.dictionary.getCOSArray(COSName.CO);
        if (co == null) {
            return Collections.emptyList();
        }
        Iterable<PDField> fields = this.isCachingFields() ? this.fieldCache.values() : this.getFieldTree();
        ArrayList<PDField> actuals = new ArrayList<PDField>();
        block0: for (int i = 0; i < co.size(); ++i) {
            COSBase item = co.getObject(i);
            for (PDField field : fields) {
                if (field.getCOSObject() != item) continue;
                actuals.add(field);
                continue block0;
            }
        }
        return actuals;
    }

    public void setCalcOrder(List<PDField> fields) {
        COSArray array = new COSArray();
        for (PDField field : fields) {
            array.add(field);
        }
        this.dictionary.setItem(COSName.CO, (COSBase)array);
    }

    public ScriptingHandler getScriptingHandler() {
        return this.scriptingHandler;
    }

    public void setScriptingHandler(ScriptingHandler scriptingHandler) {
        this.scriptingHandler = scriptingHandler;
    }

    private Matrix resolveTransformationMatrix(PDAnnotation annotation, PDAppearanceStream appearanceStream) {
        Rectangle2D transformedAppearanceBox = this.getTransformedAppearanceBBox(appearanceStream);
        PDRectangle annotationRect = annotation.getRectangle();
        Matrix transformationMatrix = new Matrix();
        transformationMatrix.translate((float)((double)annotationRect.getLowerLeftX() - transformedAppearanceBox.getX()), (float)((double)annotationRect.getLowerLeftY() - transformedAppearanceBox.getY()));
        transformationMatrix.scale((float)((double)annotationRect.getWidth() / transformedAppearanceBox.getWidth()), (float)((double)annotationRect.getHeight() / transformedAppearanceBox.getHeight()));
        return transformationMatrix;
    }

    private Rectangle2D getTransformedAppearanceBBox(PDAppearanceStream appearanceStream) {
        Matrix appearanceStreamMatrix = appearanceStream.getMatrix();
        PDRectangle appearanceStreamBBox = appearanceStream.getBBox();
        GeneralPath transformedAppearanceBox = appearanceStreamBBox.transform(appearanceStreamMatrix);
        return transformedAppearanceBox.getBounds2D();
    }

    private Map<COSDictionary, Set<COSDictionary>> buildPagesWidgetsMap(List<PDField> fields, PDPageTree pages) throws IOException {
        HashMap<COSDictionary, Set<COSDictionary>> pagesAnnotationsMap = new HashMap<COSDictionary, Set<COSDictionary>>();
        boolean hasMissingPageRef = false;
        for (PDField field : fields) {
            List<PDAnnotationWidget> widgets = field.getWidgets();
            for (PDAnnotationWidget widget : widgets) {
                PDPage page = widget.getPage();
                if (page != null) {
                    this.fillPagesAnnotationMap(pagesAnnotationsMap, page, widget);
                    continue;
                }
                hasMissingPageRef = true;
            }
        }
        if (!hasMissingPageRef) {
            return pagesAnnotationsMap;
        }
        LOG.warn((Object)"There has been a widget with a missing page reference, will check all page annotations");
        for (PDPage page : pages) {
            for (PDAnnotation annotation : page.getAnnotations()) {
                if (!(annotation instanceof PDAnnotationWidget)) continue;
                this.fillPagesAnnotationMap(pagesAnnotationsMap, page, (PDAnnotationWidget)annotation);
            }
        }
        return pagesAnnotationsMap;
    }

    private void fillPagesAnnotationMap(Map<COSDictionary, Set<COSDictionary>> pagesAnnotationsMap, PDPage page, PDAnnotationWidget widget) {
        Set<COSDictionary> widgetsForPage = pagesAnnotationsMap.get(page.getCOSObject());
        if (widgetsForPage == null) {
            widgetsForPage = new HashSet<COSDictionary>();
            widgetsForPage.add(widget.getCOSObject());
            pagesAnnotationsMap.put(page.getCOSObject(), widgetsForPage);
        } else {
            widgetsForPage.add(widget.getCOSObject());
        }
    }

    private void removeFields(List<PDField> fields) {
        for (PDField field : fields) {
            COSArray array = field.getParent() == null ? (COSArray)this.dictionary.getDictionaryObject(COSName.FIELDS) : (COSArray)field.getParent().getCOSObject().getDictionaryObject(COSName.KIDS);
            array.removeObject(field.getCOSObject());
        }
    }
}

