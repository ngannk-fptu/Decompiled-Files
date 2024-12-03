/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.multipdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSNumber;
import org.apache.pdfbox.cos.COSObject;
import org.apache.pdfbox.cos.COSStream;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFCloneUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDDocumentNameDestinationDictionary;
import org.apache.pdfbox.pdmodel.PDDocumentNameDictionary;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageTree;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.PDStructureElementNameTreeNode;
import org.apache.pdfbox.pdmodel.PageMode;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDDestinationOrAction;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDNameTreeNode;
import org.apache.pdfbox.pdmodel.common.PDNumberTreeNode;
import org.apache.pdfbox.pdmodel.common.PDStream;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDMarkInfo;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDParentTreeValue;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureElement;
import org.apache.pdfbox.pdmodel.documentinterchange.logicalstructure.PDStructureTreeRoot;
import org.apache.pdfbox.pdmodel.graphics.color.PDOutputIntent;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionGoTo;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;
import org.apache.pdfbox.pdmodel.interactive.viewerpreferences.PDViewerPreferences;

public class PDFMergerUtility {
    private static final Log LOG = LogFactory.getLog(PDFMergerUtility.class);
    private final List<Object> sources;
    private String destinationFileName;
    private OutputStream destinationStream;
    private boolean ignoreAcroFormErrors = false;
    private PDDocumentInformation destinationDocumentInformation = null;
    private PDMetadata destinationMetadata = null;
    private DocumentMergeMode documentMergeMode = DocumentMergeMode.PDFBOX_LEGACY_MODE;
    private AcroFormMergeMode acroFormMergeMode = AcroFormMergeMode.PDFBOX_LEGACY_MODE;
    private int nextFieldNum = 1;

    public PDFMergerUtility() {
        this.sources = new ArrayList<Object>();
    }

    public AcroFormMergeMode getAcroFormMergeMode() {
        return this.acroFormMergeMode;
    }

    public void setAcroFormMergeMode(AcroFormMergeMode theAcroFormMergeMode) {
        this.acroFormMergeMode = theAcroFormMergeMode;
    }

    public void setDocumentMergeMode(DocumentMergeMode theDocumentMergeMode) {
        this.documentMergeMode = theDocumentMergeMode;
    }

    public DocumentMergeMode getDocumentMergeMode() {
        return this.documentMergeMode;
    }

    public String getDestinationFileName() {
        return this.destinationFileName;
    }

    public void setDestinationFileName(String destination) {
        this.destinationFileName = destination;
    }

    public OutputStream getDestinationStream() {
        return this.destinationStream;
    }

    public void setDestinationStream(OutputStream destStream) {
        this.destinationStream = destStream;
    }

    public PDDocumentInformation getDestinationDocumentInformation() {
        return this.destinationDocumentInformation;
    }

    public void setDestinationDocumentInformation(PDDocumentInformation info) {
        this.destinationDocumentInformation = info;
    }

    public PDMetadata getDestinationMetadata() {
        return this.destinationMetadata;
    }

    public void setDestinationMetadata(PDMetadata meta) {
        this.destinationMetadata = meta;
    }

    public void addSource(String source) throws FileNotFoundException {
        this.addSource(new File(source));
    }

    public void addSource(File source) throws FileNotFoundException {
        this.sources.add(source);
    }

    public void addSource(InputStream source) {
        this.sources.add(source);
    }

    public void addSources(List<InputStream> sourcesList) {
        this.sources.addAll(sourcesList);
    }

    @Deprecated
    public void mergeDocuments() throws IOException {
        this.mergeDocuments(MemoryUsageSetting.setupMainMemoryOnly());
    }

    public void mergeDocuments(MemoryUsageSetting memUsageSetting) throws IOException {
        if (this.documentMergeMode == DocumentMergeMode.PDFBOX_LEGACY_MODE) {
            this.legacyMergeDocuments(memUsageSetting);
        } else if (this.documentMergeMode == DocumentMergeMode.OPTIMIZE_RESOURCES_MODE) {
            this.optimizedMergeDocuments(memUsageSetting);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void optimizedMergeDocuments(MemoryUsageSetting memUsageSetting) throws IOException {
        PDDocument destination;
        block9: {
            destination = null;
            try {
                destination = new PDDocument(memUsageSetting);
                PDFCloneUtility cloner = new PDFCloneUtility(destination);
                PDPageTree destinationPageTree = destination.getPages();
                for (Object sourceObject : this.sources) {
                    PDDocument sourceDoc = null;
                    try {
                        sourceDoc = sourceObject instanceof File ? PDDocument.load((File)sourceObject, memUsageSetting) : PDDocument.load((InputStream)sourceObject, memUsageSetting);
                        for (PDPage page : sourceDoc.getPages()) {
                            PDPage newPage = new PDPage((COSDictionary)cloner.cloneForNewDocument(page.getCOSObject()));
                            newPage.setCropBox(page.getCropBox());
                            newPage.setMediaBox(page.getMediaBox());
                            newPage.setRotation(page.getRotation());
                            PDResources resources = page.getResources();
                            if (resources != null) {
                                newPage.setResources(new PDResources((COSDictionary)cloner.cloneForNewDocument(resources)));
                            } else {
                                newPage.setResources(new PDResources());
                            }
                            destinationPageTree.add(newPage);
                        }
                    }
                    catch (Throwable throwable) {
                        IOUtils.closeQuietly(sourceDoc);
                        throw throwable;
                    }
                    IOUtils.closeQuietly(sourceDoc);
                }
                if (this.destinationStream == null) {
                    destination.save(this.destinationFileName);
                    break block9;
                }
                destination.save(this.destinationStream);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(destination);
                throw throwable;
            }
        }
        IOUtils.closeQuietly(destination);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void legacyMergeDocuments(MemoryUsageSetting memUsageSetting) throws IOException {
        PDDocument destination = null;
        if (this.sources.size() > 0) {
            ArrayList<PDDocument> tobeclosed = new ArrayList<PDDocument>(this.sources.size());
            try {
                MemoryUsageSetting partitionedMemSetting = memUsageSetting != null ? memUsageSetting.getPartitionedCopy(this.sources.size() + 1) : MemoryUsageSetting.setupMainMemoryOnly();
                destination = new PDDocument(partitionedMemSetting);
                for (Object sourceObject : this.sources) {
                    PDDocument sourceDoc = null;
                    sourceDoc = sourceObject instanceof File ? PDDocument.load((File)sourceObject, partitionedMemSetting) : PDDocument.load((InputStream)sourceObject, partitionedMemSetting);
                    tobeclosed.add(sourceDoc);
                    this.appendDocument(destination, sourceDoc);
                }
                if (this.destinationDocumentInformation != null) {
                    destination.setDocumentInformation(this.destinationDocumentInformation);
                }
                if (this.destinationMetadata != null) {
                    destination.getDocumentCatalog().setMetadata(this.destinationMetadata);
                }
                if (this.destinationStream == null) {
                    destination.save(this.destinationFileName);
                } else {
                    destination.save(this.destinationStream);
                }
            }
            finally {
                if (destination != null) {
                    IOUtils.closeAndLogException(destination, LOG, "PDDocument", null);
                }
                for (PDDocument doc : tobeclosed) {
                    IOUtils.closeAndLogException(doc, LOG, "PDDocument", null);
                }
            }
        }
    }

    public void appendDocument(PDDocument destination, PDDocument source) throws IOException {
        COSDictionary srcLabels;
        PageMode destPageMode;
        PDDocumentOutline srcOutline;
        PDDocumentNameDestinationDictionary srcDests;
        if (source.getDocument().isClosed()) {
            throw new IOException("Error: source PDF is closed.");
        }
        if (destination.getDocument().isClosed()) {
            throw new IOException("Error: destination PDF is closed.");
        }
        PDDocumentCatalog srcCatalog = source.getDocumentCatalog();
        if (this.isDynamicXfa(srcCatalog.getAcroForm())) {
            throw new IOException("Error: can't merge source document containing dynamic XFA form content.");
        }
        PDDocumentInformation destInfo = destination.getDocumentInformation();
        PDDocumentInformation srcInfo = source.getDocumentInformation();
        this.mergeInto(srcInfo.getCOSObject(), destInfo.getCOSObject(), Collections.<COSName>emptySet());
        float destVersion = destination.getVersion();
        float srcVersion = source.getVersion();
        if (destVersion < srcVersion) {
            destination.setVersion(srcVersion);
        }
        int pageIndexOpenActionDest = -1;
        PDDocumentCatalog destCatalog = destination.getDocumentCatalog();
        if (destCatalog.getOpenAction() == null) {
            PDPage page;
            PDDestinationOrAction openAction = null;
            try {
                openAction = srcCatalog.getOpenAction();
            }
            catch (IOException ex) {
                LOG.error((Object)"Invalid OpenAction ignored", (Throwable)ex);
            }
            PDDestination openActionDestination = null;
            if (openAction instanceof PDActionGoTo) {
                openActionDestination = ((PDActionGoTo)openAction).getDestination();
            } else if (openAction instanceof PDDestination) {
                openActionDestination = (PDDestination)openAction;
            }
            if (openActionDestination instanceof PDPageDestination && (page = ((PDPageDestination)openActionDestination).getPage()) != null) {
                pageIndexOpenActionDest = srcCatalog.getPages().indexOf(page);
            }
            destCatalog.setOpenAction(openAction);
        }
        PDFCloneUtility cloner = new PDFCloneUtility(destination);
        this.mergeAcroForm(cloner, destCatalog, srcCatalog);
        COSArray destThreads = (COSArray)destCatalog.getCOSObject().getDictionaryObject(COSName.THREADS);
        COSArray srcThreads = (COSArray)cloner.cloneForNewDocument(destCatalog.getCOSObject().getDictionaryObject(COSName.THREADS));
        if (destThreads == null) {
            destCatalog.getCOSObject().setItem(COSName.THREADS, (COSBase)srcThreads);
        } else {
            destThreads.addAll(srcThreads);
        }
        PDDocumentNameDictionary destNames = destCatalog.getNames();
        PDDocumentNameDictionary srcNames = srcCatalog.getNames();
        if (srcNames != null) {
            if (destNames == null) {
                destCatalog.getCOSObject().setItem(COSName.NAMES, cloner.cloneForNewDocument(srcNames));
            } else {
                cloner.cloneMerge(srcNames, destNames);
            }
        }
        if (destNames != null && destNames.getCOSObject().containsKey(COSName.ID_TREE)) {
            destNames.getCOSObject().removeItem(COSName.ID_TREE);
            LOG.warn((Object)"Removed /IDTree from /Names dictionary, doesn't belong there");
        }
        if ((srcDests = srcCatalog.getDests()) != null) {
            PDDocumentNameDestinationDictionary destDests = destCatalog.getDests();
            if (destDests == null) {
                destCatalog.getCOSObject().setItem(COSName.DESTS, cloner.cloneForNewDocument(srcDests));
            } else {
                cloner.cloneMerge(srcDests, destDests);
            }
        }
        if ((srcOutline = srcCatalog.getDocumentOutline()) != null) {
            PDDocumentOutline destOutline = destCatalog.getDocumentOutline();
            if (destOutline == null || destOutline.getFirstChild() == null) {
                PDDocumentOutline cloned = new PDDocumentOutline((COSDictionary)cloner.cloneForNewDocument(srcOutline));
                destCatalog.setDocumentOutline(cloned);
            } else {
                Object outlineItem;
                PDOutlineItem destLastOutlineItem = destOutline.getFirstChild();
                while ((outlineItem = destLastOutlineItem.getNextSibling()) != null) {
                    destLastOutlineItem = outlineItem;
                }
                for (PDOutlineItem item : srcOutline.children()) {
                    COSDictionary clonedDict = (COSDictionary)cloner.cloneForNewDocument(item);
                    clonedDict.removeItem(COSName.PREV);
                    clonedDict.removeItem(COSName.NEXT);
                    PDOutlineItem clonedItem = new PDOutlineItem(clonedDict);
                    destLastOutlineItem.insertSiblingAfter(clonedItem);
                    destLastOutlineItem = destLastOutlineItem.getNextSibling();
                }
            }
        }
        if ((destPageMode = destCatalog.getPageMode()) == null) {
            PageMode srcPageMode = srcCatalog.getPageMode();
            destCatalog.setPageMode(srcPageMode);
        }
        if ((srcLabels = srcCatalog.getCOSObject().getCOSDictionary(COSName.PAGE_LABELS)) != null) {
            COSArray destNums;
            int destPageCount = destination.getNumberOfPages();
            COSDictionary destLabels = destCatalog.getCOSObject().getCOSDictionary(COSName.PAGE_LABELS);
            if (destLabels == null) {
                destLabels = new COSDictionary();
                destNums = new COSArray();
                destLabels.setItem(COSName.NUMS, (COSBase)destNums);
                destCatalog.getCOSObject().setItem(COSName.PAGE_LABELS, (COSBase)destLabels);
            } else {
                destNums = (COSArray)destLabels.getDictionaryObject(COSName.NUMS);
            }
            COSArray srcNums = (COSArray)srcLabels.getDictionaryObject(COSName.NUMS);
            if (srcNums != null) {
                int startSize = destNums.size();
                for (int i = 0; i < srcNums.size(); i += 2) {
                    COSBase base = srcNums.getObject(i);
                    if (!(base instanceof COSNumber)) {
                        LOG.error((Object)("page labels ignored, index " + i + " should be a number, but is " + base));
                        while (destNums.size() > startSize) {
                            destNums.remove(startSize);
                        }
                        break;
                    }
                    COSNumber labelIndex = (COSNumber)base;
                    long labelIndexValue = labelIndex.intValue();
                    destNums.add(COSInteger.get(labelIndexValue + (long)destPageCount));
                    destNums.add(cloner.cloneForNewDocument(srcNums.getObject(i + 1)));
                }
            }
        }
        COSStream destMetadata = destCatalog.getCOSObject().getCOSStream(COSName.METADATA);
        COSStream srcMetadata = srcCatalog.getCOSObject().getCOSStream(COSName.METADATA);
        if (destMetadata == null && srcMetadata != null) {
            try {
                PDStream newStream = new PDStream(destination, (InputStream)srcMetadata.createInputStream(), (COSName)null);
                this.mergeInto(srcMetadata, newStream.getCOSObject(), new HashSet<COSName>(Arrays.asList(COSName.FILTER, COSName.LENGTH)));
                destCatalog.getCOSObject().setItem(COSName.METADATA, (COSObjectable)newStream);
            }
            catch (IOException ex) {
                LOG.error((Object)"Metadata skipped because it could not be read", (Throwable)ex);
            }
        }
        COSDictionary destOCP = destCatalog.getCOSObject().getCOSDictionary(COSName.OCPROPERTIES);
        COSDictionary srcOCP = srcCatalog.getCOSObject().getCOSDictionary(COSName.OCPROPERTIES);
        if (destOCP == null && srcOCP != null) {
            destCatalog.getCOSObject().setItem(COSName.OCPROPERTIES, cloner.cloneForNewDocument(srcOCP));
        } else if (destOCP != null && srcOCP != null) {
            cloner.cloneMerge(srcOCP, destOCP);
        }
        this.mergeOutputIntents(cloner, srcCatalog, destCatalog);
        boolean mergeStructTree = false;
        int destParentTreeNextKey = -1;
        Map<Integer, COSObjectable> srcNumberTreeAsMap = null;
        Map<Integer, COSObjectable> destNumberTreeAsMap = null;
        PDStructureTreeRoot srcStructTree = srcCatalog.getStructureTreeRoot();
        PDStructureTreeRoot destStructTree = destCatalog.getStructureTreeRoot();
        if (destStructTree == null && srcStructTree != null) {
            destStructTree = new PDStructureTreeRoot();
            destCatalog.setStructureTreeRoot(destStructTree);
            destStructTree.setParentTree(new PDNumberTreeNode(PDParentTreeValue.class));
            for (PDPage page : destCatalog.getPages()) {
                page.getCOSObject().removeItem(COSName.STRUCT_PARENTS);
                for (PDAnnotation pDAnnotation : page.getAnnotations()) {
                    pDAnnotation.getCOSObject().removeItem(COSName.STRUCT_PARENT);
                }
            }
        }
        if (destStructTree != null) {
            PDNumberTreeNode destParentTree = destStructTree.getParentTree();
            destParentTreeNextKey = destStructTree.getParentTreeNextKey();
            if (destParentTree != null) {
                PDNumberTreeNode srcParentTree;
                destNumberTreeAsMap = PDFMergerUtility.getNumberTreeAsMap(destParentTree);
                if (destParentTreeNextKey < 0) {
                    destParentTreeNextKey = destNumberTreeAsMap.isEmpty() ? 0 : Collections.max(destNumberTreeAsMap.keySet()) + 1;
                }
                if (destParentTreeNextKey >= 0 && srcStructTree != null && (srcParentTree = srcStructTree.getParentTree()) != null && !(srcNumberTreeAsMap = PDFMergerUtility.getNumberTreeAsMap(srcParentTree)).isEmpty()) {
                    mergeStructTree = true;
                }
            }
        }
        HashMap<COSDictionary, COSDictionary> objMapping = new HashMap<COSDictionary, COSDictionary>();
        int pageIndex = 0;
        PDPageTree destinationPageTree = destination.getPages();
        for (Object page : srcCatalog.getPages()) {
            PDPage pDPage = new PDPage((COSDictionary)cloner.cloneForNewDocument(((PDPage)page).getCOSObject()));
            if (!mergeStructTree) {
                pDPage.getCOSObject().removeItem(COSName.STRUCT_PARENTS);
                for (PDAnnotation ann : pDPage.getAnnotations()) {
                    ann.getCOSObject().removeItem(COSName.STRUCT_PARENT);
                }
            }
            pDPage.setCropBox(((PDPage)page).getCropBox());
            pDPage.setMediaBox(((PDPage)page).getMediaBox());
            pDPage.setRotation(((PDPage)page).getRotation());
            PDResources resources = ((PDPage)page).getResources();
            if (resources != null) {
                pDPage.setResources(new PDResources((COSDictionary)cloner.cloneForNewDocument(resources)));
            } else {
                pDPage.setResources(new PDResources());
            }
            if (mergeStructTree) {
                this.updateStructParentEntries(pDPage, destParentTreeNextKey);
                objMapping.put(((PDPage)page).getCOSObject(), pDPage.getCOSObject());
                List<PDAnnotation> oldAnnots = ((PDPage)page).getAnnotations();
                List<PDAnnotation> newAnnots = pDPage.getAnnotations();
                for (int i = 0; i < oldAnnots.size(); ++i) {
                    objMapping.put(oldAnnots.get(i).getCOSObject(), newAnnots.get(i).getCOSObject());
                }
            }
            destinationPageTree.add(pDPage);
            if (pageIndex == pageIndexOpenActionDest) {
                PDDestinationOrAction openAction = destCatalog.getOpenAction();
                PDPageDestination pageDestination = openAction instanceof PDActionGoTo ? (PDPageDestination)((PDActionGoTo)openAction).getDestination() : (PDPageDestination)openAction;
                pageDestination.setPage(pDPage);
            }
            ++pageIndex;
        }
        if (mergeStructTree) {
            int n;
            this.updatePageReferences(cloner, srcNumberTreeAsMap, objMapping);
            int n2 = -1;
            for (Map.Entry entry : srcNumberTreeAsMap.entrySet()) {
                int srcKey = (Integer)entry.getKey();
                n = Math.max(srcKey, n);
                destNumberTreeAsMap.put(destParentTreeNextKey + srcKey, cloner.cloneForNewDocument(entry.getValue()));
            }
            PDNumberTreeNode newParentTreeNode = new PDNumberTreeNode(PDParentTreeValue.class);
            newParentTreeNode.setNumbers(destNumberTreeAsMap);
            destStructTree.setParentTree(newParentTreeNode);
            destStructTree.setParentTreeNextKey(destParentTreeNextKey += n + true);
            this.mergeKEntries(cloner, srcStructTree, destStructTree);
            this.mergeRoleMap(srcStructTree, destStructTree);
            this.mergeIDTree(cloner, srcStructTree, destStructTree);
            this.mergeMarkInfo(destCatalog, srcCatalog);
            this.mergeLanguage(destCatalog, srcCatalog);
            this.mergeViewerPreferences(destCatalog, srcCatalog);
        }
    }

    private void mergeViewerPreferences(PDDocumentCatalog destCatalog, PDDocumentCatalog srcCatalog) {
        PDViewerPreferences srcViewerPreferences = srcCatalog.getViewerPreferences();
        if (srcViewerPreferences == null) {
            return;
        }
        PDViewerPreferences destViewerPreferences = destCatalog.getViewerPreferences();
        if (destViewerPreferences == null) {
            destViewerPreferences = new PDViewerPreferences(new COSDictionary());
            destCatalog.setViewerPreferences(destViewerPreferences);
        }
        this.mergeInto(srcViewerPreferences.getCOSObject(), destViewerPreferences.getCOSObject(), Collections.<COSName>emptySet());
        if (srcViewerPreferences.hideToolbar() || destViewerPreferences.hideToolbar()) {
            destViewerPreferences.setHideToolbar(true);
        }
        if (srcViewerPreferences.hideMenubar() || destViewerPreferences.hideMenubar()) {
            destViewerPreferences.setHideMenubar(true);
        }
        if (srcViewerPreferences.hideWindowUI() || destViewerPreferences.hideWindowUI()) {
            destViewerPreferences.setHideWindowUI(true);
        }
        if (srcViewerPreferences.fitWindow() || destViewerPreferences.fitWindow()) {
            destViewerPreferences.setFitWindow(true);
        }
        if (srcViewerPreferences.centerWindow() || destViewerPreferences.centerWindow()) {
            destViewerPreferences.setCenterWindow(true);
        }
        if (srcViewerPreferences.displayDocTitle() || destViewerPreferences.displayDocTitle()) {
            destViewerPreferences.setDisplayDocTitle(true);
        }
    }

    private void mergeLanguage(PDDocumentCatalog destCatalog, PDDocumentCatalog srcCatalog) {
        String srcLanguage;
        if (destCatalog.getLanguage() == null && (srcLanguage = srcCatalog.getLanguage()) != null) {
            destCatalog.setLanguage(srcLanguage);
        }
    }

    private void mergeMarkInfo(PDDocumentCatalog destCatalog, PDDocumentCatalog srcCatalog) {
        PDMarkInfo destMark = destCatalog.getMarkInfo();
        PDMarkInfo srcMark = srcCatalog.getMarkInfo();
        if (destMark == null) {
            destMark = new PDMarkInfo();
        }
        if (srcMark == null) {
            srcMark = new PDMarkInfo();
        }
        destMark.setMarked(true);
        destMark.setSuspect(srcMark.isSuspect() || destMark.isSuspect());
        destMark.setSuspect(srcMark.usesUserProperties() || destMark.usesUserProperties());
        destCatalog.setMarkInfo(destMark);
    }

    private void mergeKEntries(PDFCloneUtility cloner, PDStructureTreeRoot srcStructTree, PDStructureTreeRoot destStructTree) throws IOException {
        boolean onlyDocuments;
        COSArray kLevelOneArray;
        COSDictionary topKDict;
        COSBase srcKEntry = srcStructTree.getK();
        COSArray srcKArray = new COSArray();
        COSBase clonedSrcKEntry = cloner.cloneForNewDocument(srcKEntry);
        if (clonedSrcKEntry instanceof COSArray) {
            srcKArray.addAll((COSArray)clonedSrcKEntry);
        } else if (clonedSrcKEntry instanceof COSDictionary) {
            srcKArray.add(clonedSrcKEntry);
        }
        if (srcKArray.size() == 0) {
            return;
        }
        COSArray dstKArray = new COSArray();
        COSBase dstKEntry = destStructTree.getK();
        if (dstKEntry instanceof COSArray) {
            dstKArray.addAll((COSArray)dstKEntry);
        } else if (dstKEntry instanceof COSDictionary) {
            dstKArray.add(dstKEntry);
        }
        if (dstKArray.size() == 1 && dstKArray.getObject(0) instanceof COSDictionary && COSName.DOCUMENT.equals((topKDict = (COSDictionary)dstKArray.getObject(0)).getCOSName(COSName.S)) && (kLevelOneArray = topKDict.getCOSArray(COSName.K)) != null && (onlyDocuments = this.hasOnlyDocumentsOrParts(kLevelOneArray))) {
            kLevelOneArray.addAll(srcKArray);
            this.updateParentEntry(kLevelOneArray, topKDict, COSName.PART);
            return;
        }
        if (dstKArray.size() == 0) {
            this.updateParentEntry(srcKArray, destStructTree.getCOSObject(), null);
            destStructTree.setK(srcKArray);
            return;
        }
        dstKArray.addAll(srcKArray);
        COSDictionary kLevelZeroDict = new COSDictionary();
        COSName newStructureType = this.hasOnlyDocumentsOrParts(dstKArray) ? COSName.PART : null;
        this.updateParentEntry(dstKArray, kLevelZeroDict, newStructureType);
        kLevelZeroDict.setItem(COSName.K, (COSBase)dstKArray);
        kLevelZeroDict.setItem(COSName.P, (COSObjectable)destStructTree);
        kLevelZeroDict.setItem(COSName.S, (COSBase)COSName.DOCUMENT);
        destStructTree.setK(kLevelZeroDict);
    }

    private boolean hasOnlyDocumentsOrParts(COSArray kLevelOneArray) {
        for (int i = 0; i < kLevelOneArray.size(); ++i) {
            COSBase base = kLevelOneArray.getObject(i);
            if (!(base instanceof COSDictionary)) {
                return false;
            }
            COSDictionary dict = (COSDictionary)base;
            COSName sEntry = dict.getCOSName(COSName.S);
            if (COSName.DOCUMENT.equals(sEntry) || COSName.PART.equals(sEntry)) continue;
            return false;
        }
        return true;
    }

    private void updateParentEntry(COSArray kArray, COSDictionary newParent, COSName newStructureType) {
        for (int i = 0; i < kArray.size(); ++i) {
            COSBase subEntry = kArray.getObject(i);
            if (!(subEntry instanceof COSDictionary)) continue;
            COSDictionary dictEntry = (COSDictionary)subEntry;
            dictEntry.setItem(COSName.P, (COSBase)newParent);
            if (newStructureType == null) continue;
            dictEntry.setItem(COSName.S, (COSBase)newStructureType);
        }
    }

    private void mergeIDTree(PDFCloneUtility cloner, PDStructureTreeRoot srcStructTree, PDStructureTreeRoot destStructTree) throws IOException {
        PDNameTreeNode<PDStructureElement> srcIDTree = srcStructTree.getIDTree();
        if (srcIDTree == null) {
            return;
        }
        PDStructureElementNameTreeNode destIDTree = destStructTree.getIDTree();
        if (destIDTree == null) {
            destIDTree = new PDStructureElementNameTreeNode();
        }
        Map<String, PDStructureElement> srcNames = PDFMergerUtility.getIDTreeAsMap(srcIDTree);
        Map<String, PDStructureElement> destNames = PDFMergerUtility.getIDTreeAsMap(destIDTree);
        for (Map.Entry<String, PDStructureElement> entry : srcNames.entrySet()) {
            if (destNames.containsKey(entry.getKey())) {
                LOG.warn((Object)("key " + entry.getKey() + " already exists in destination IDTree"));
                continue;
            }
            destNames.put(entry.getKey(), new PDStructureElement((COSDictionary)cloner.cloneForNewDocument(entry.getValue().getCOSObject())));
        }
        destIDTree = new PDStructureElementNameTreeNode();
        destIDTree.setNames(destNames);
        destStructTree.setIDTree(destIDTree);
    }

    static Map<String, PDStructureElement> getIDTreeAsMap(PDNameTreeNode<PDStructureElement> idTree) throws IOException {
        Map<String, PDStructureElement> names = idTree.getNames();
        names = names == null ? new LinkedHashMap<String, PDStructureElement>() : new LinkedHashMap<String, PDStructureElement>(names);
        List<PDNameTreeNode<PDStructureElement>> kids = idTree.getKids();
        if (kids != null) {
            for (PDNameTreeNode<PDStructureElement> kid : kids) {
                names.putAll(PDFMergerUtility.getIDTreeAsMap(kid));
            }
        }
        return names;
    }

    static Map<Integer, COSObjectable> getNumberTreeAsMap(PDNumberTreeNode tree) throws IOException {
        Map<Integer, COSObjectable> numbers = tree.getNumbers();
        numbers = numbers == null ? new LinkedHashMap<Integer, COSObjectable>() : new LinkedHashMap<Integer, COSObjectable>(numbers);
        List<PDNumberTreeNode> kids = tree.getKids();
        if (kids != null) {
            for (PDNumberTreeNode kid : kids) {
                numbers.putAll(PDFMergerUtility.getNumberTreeAsMap(kid));
            }
        }
        return numbers;
    }

    private void mergeRoleMap(PDStructureTreeRoot srcStructTree, PDStructureTreeRoot destStructTree) {
        COSDictionary srcDict = srcStructTree.getCOSObject().getCOSDictionary(COSName.ROLE_MAP);
        if (srcDict == null) {
            return;
        }
        COSDictionary destDict = destStructTree.getCOSObject().getCOSDictionary(COSName.ROLE_MAP);
        if (destDict == null) {
            destStructTree.getCOSObject().setItem(COSName.ROLE_MAP, (COSBase)srcDict);
            return;
        }
        for (Map.Entry<COSName, COSBase> entry : srcDict.entrySet()) {
            COSBase destValue = destDict.getDictionaryObject(entry.getKey());
            if (destValue != null && destValue.equals(entry.getValue())) continue;
            if (destDict.containsKey(entry.getKey())) {
                LOG.warn((Object)("key " + entry.getKey() + " already exists in destination RoleMap"));
                continue;
            }
            destDict.setItem(entry.getKey(), entry.getValue());
        }
    }

    private void mergeOutputIntents(PDFCloneUtility cloner, PDDocumentCatalog srcCatalog, PDDocumentCatalog destCatalog) throws IOException {
        List<PDOutputIntent> srcOutputIntents = srcCatalog.getOutputIntents();
        List<PDOutputIntent> dstOutputIntents = destCatalog.getOutputIntents();
        for (PDOutputIntent srcOI : srcOutputIntents) {
            String srcOCI = srcOI.getOutputConditionIdentifier();
            if (srcOCI != null && !"Custom".equals(srcOCI)) {
                boolean skip = false;
                for (PDOutputIntent dstOI : dstOutputIntents) {
                    if (!dstOI.getOutputConditionIdentifier().equals(srcOCI)) continue;
                    skip = true;
                    break;
                }
                if (skip) continue;
            }
            destCatalog.addOutputIntent(new PDOutputIntent((COSDictionary)cloner.cloneForNewDocument(srcOI)));
            dstOutputIntents.add(srcOI);
        }
    }

    private void mergeAcroForm(PDFCloneUtility cloner, PDDocumentCatalog destCatalog, PDDocumentCatalog srcCatalog) throws IOException {
        block8: {
            try {
                PDAcroForm destAcroForm = destCatalog.getAcroForm();
                PDAcroForm srcAcroForm = srcCatalog.getAcroForm();
                if (destAcroForm == null && srcAcroForm != null) {
                    destCatalog.getCOSObject().setItem(COSName.ACRO_FORM, cloner.cloneForNewDocument(srcAcroForm.getCOSObject()));
                } else if (srcAcroForm != null) {
                    if (this.acroFormMergeMode == AcroFormMergeMode.PDFBOX_LEGACY_MODE) {
                        this.acroFormLegacyMode(cloner, destAcroForm, srcAcroForm);
                    } else if (this.acroFormMergeMode == AcroFormMergeMode.JOIN_FORM_FIELDS_MODE) {
                        this.acroFormJoinFieldsMode(cloner, destAcroForm, srcAcroForm);
                    }
                }
            }
            catch (IOException e) {
                if (this.ignoreAcroFormErrors) break block8;
                throw new IOException(e);
            }
        }
    }

    private void acroFormJoinFieldsMode(PDFCloneUtility cloner, PDAcroForm destAcroForm, PDAcroForm srcAcroForm) throws IOException {
        List<PDField> srcFields = srcAcroForm.getFields();
        if (!srcFields.isEmpty()) {
            COSBase base = destAcroForm.getCOSObject().getItem(COSName.FIELDS);
            COSArray destFields = base instanceof COSArray ? (COSArray)base : new COSArray();
            for (PDField srcField : srcAcroForm.getFieldTree()) {
                PDField destinationField = destAcroForm.getField(srcField.getFullyQualifiedName());
                if (destinationField == null) {
                    COSDictionary importedField = (COSDictionary)cloner.cloneForNewDocument(srcField.getCOSObject());
                    destFields.add(importedField);
                    continue;
                }
                this.mergeFields(cloner, destinationField, srcField);
            }
            destAcroForm.getCOSObject().setItem(COSName.FIELDS, (COSBase)destFields);
        }
    }

    private void mergeFields(PDFCloneUtility cloner, PDField destField, PDField srcField) {
        if (destField instanceof PDNonTerminalField && srcField instanceof PDNonTerminalField) {
            LOG.info((Object)("Skipping non terminal field " + srcField.getFullyQualifiedName()));
            return;
        }
        if ("Tx".equals(srcField.getFieldType()) && "Tx".equals(destField.getFieldType())) {
            if (destField.getCOSObject().containsKey(COSName.KIDS)) {
                COSArray widgets = destField.getCOSObject().getCOSArray(COSName.KIDS);
                for (PDAnnotationWidget srcWidget : srcField.getWidgets()) {
                    try {
                        widgets.add(cloner.cloneForNewDocument(srcWidget.getCOSObject()));
                    }
                    catch (IOException ioe) {
                        LOG.warn((Object)("Unable to clone widget for source field " + srcField.getFullyQualifiedName()));
                    }
                }
            } else {
                COSArray widgets = new COSArray();
                try {
                    COSDictionary widgetAsCOS = (COSDictionary)cloner.cloneForNewDocument(destField.getWidgets().get(0));
                    this.cleanupWidgetCOSDictionary(widgetAsCOS, true);
                    widgetAsCOS.setItem(COSName.PARENT, (COSObjectable)destField);
                    widgets.add(widgetAsCOS);
                    for (PDAnnotationWidget srcWidget : srcField.getWidgets()) {
                        try {
                            widgetAsCOS = (COSDictionary)cloner.cloneForNewDocument(srcWidget.getCOSObject());
                            this.cleanupWidgetCOSDictionary(widgetAsCOS, false);
                            widgetAsCOS.setItem(COSName.PARENT, (COSObjectable)destField);
                            widgets.add(widgetAsCOS);
                        }
                        catch (IOException ioe) {
                            LOG.warn((Object)("Unable to clone widget for source field " + srcField.getFullyQualifiedName()));
                        }
                    }
                    destField.getCOSObject().setItem(COSName.KIDS, (COSBase)widgets);
                    this.cleanupFieldCOSDictionary(destField.getCOSObject());
                }
                catch (IOException ioe) {
                    LOG.warn((Object)("Unable to clone widget for destination field " + destField.getFullyQualifiedName()));
                }
            }
        } else {
            LOG.info((Object)"Only merging two text fields is currently supported");
            LOG.info((Object)("Skipping merging of " + srcField.getFullyQualifiedName() + " into " + destField.getFullyQualifiedName()));
        }
    }

    private void cleanupFieldCOSDictionary(COSDictionary fieldCos) {
        fieldCos.removeItem(COSName.F);
        fieldCos.removeItem(COSName.MK);
        fieldCos.removeItem(COSName.P);
        fieldCos.removeItem(COSName.RECT);
        fieldCos.removeItem(COSName.SUBTYPE);
        fieldCos.removeItem(COSName.TYPE);
    }

    private void cleanupWidgetCOSDictionary(COSDictionary widgetCos, boolean removeDAEntry) {
        if (removeDAEntry) {
            widgetCos.removeItem(COSName.DA);
        }
        widgetCos.removeItem(COSName.FT);
        widgetCos.removeItem(COSName.T);
        widgetCos.removeItem(COSName.V);
    }

    private void acroFormLegacyMode(PDFCloneUtility cloner, PDAcroForm destAcroForm, PDAcroForm srcAcroForm) throws IOException {
        List<PDField> srcFields = srcAcroForm.getFields();
        if (!srcFields.isEmpty()) {
            String prefix = "dummyFieldName";
            int prefixLength = "dummyFieldName".length();
            for (PDField destField : destAcroForm.getFieldTree()) {
                String suffix;
                String fieldName = destField.getPartialName();
                if (fieldName == null || !fieldName.startsWith("dummyFieldName") || !(suffix = fieldName.substring(prefixLength)).matches("\\d+")) continue;
                this.nextFieldNum = Math.max(this.nextFieldNum, Integer.parseInt(suffix) + 1);
            }
            COSBase base = destAcroForm.getCOSObject().getItem(COSName.FIELDS);
            COSArray destFields = base instanceof COSArray ? (COSArray)base : new COSArray();
            for (PDField srcField : srcAcroForm.getFields()) {
                COSDictionary dstField = (COSDictionary)cloner.cloneForNewDocument(srcField.getCOSObject());
                if (destAcroForm.getField(srcField.getFullyQualifiedName()) != null) {
                    dstField.setString(COSName.T, "dummyFieldName" + this.nextFieldNum++);
                }
                destFields.add(dstField);
            }
            destAcroForm.getCOSObject().setItem(COSName.FIELDS, (COSBase)destFields);
        }
    }

    public boolean isIgnoreAcroFormErrors() {
        return this.ignoreAcroFormErrors;
    }

    public void setIgnoreAcroFormErrors(boolean ignoreAcroFormErrorsValue) {
        this.ignoreAcroFormErrors = ignoreAcroFormErrorsValue;
    }

    private void updatePageReferences(PDFCloneUtility cloner, Map<Integer, COSObjectable> numberTreeAsMap, Map<COSDictionary, COSDictionary> objMapping) throws IOException {
        for (COSObjectable obj : numberTreeAsMap.values()) {
            if (obj == null) continue;
            PDParentTreeValue val = (PDParentTreeValue)obj;
            COSBase base = val.getCOSObject();
            if (base instanceof COSArray) {
                this.updatePageReferences(cloner, (COSArray)base, objMapping);
                continue;
            }
            this.updatePageReferences(cloner, (COSDictionary)base, objMapping);
        }
    }

    private void updatePageReferences(PDFCloneUtility cloner, COSDictionary parentTreeEntry, Map<COSDictionary, COSDictionary> objMapping) throws IOException {
        COSBase kSubEntry;
        COSBase obj;
        COSDictionary pageDict = parentTreeEntry.getCOSDictionary(COSName.PG);
        if (objMapping.containsKey(pageDict)) {
            parentTreeEntry.setItem(COSName.PG, (COSBase)objMapping.get(pageDict));
        }
        if ((obj = parentTreeEntry.getDictionaryObject(COSName.OBJ)) instanceof COSDictionary) {
            COSDictionary objDict = (COSDictionary)obj;
            if (objMapping.containsKey(objDict)) {
                parentTreeEntry.setItem(COSName.OBJ, (COSBase)objMapping.get(objDict));
            } else {
                COSBase item = parentTreeEntry.getItem(COSName.OBJ);
                if (item instanceof COSObject) {
                    LOG.debug((Object)("clone potential orphan object in structure tree: " + item + ", Type: " + objDict.getNameAsString(COSName.TYPE) + ", Subtype: " + objDict.getNameAsString(COSName.SUBTYPE) + ", T: " + objDict.getNameAsString(COSName.T)));
                } else {
                    LOG.debug((Object)("clone potential orphan object in structure tree, Type: " + objDict.getNameAsString(COSName.TYPE) + ", Subtype: " + objDict.getNameAsString(COSName.SUBTYPE) + ", T: " + objDict.getNameAsString(COSName.T)));
                }
                parentTreeEntry.setItem(COSName.OBJ, cloner.cloneForNewDocument(obj));
            }
        }
        if ((kSubEntry = parentTreeEntry.getDictionaryObject(COSName.K)) instanceof COSArray) {
            this.updatePageReferences(cloner, (COSArray)kSubEntry, objMapping);
        } else if (kSubEntry instanceof COSDictionary) {
            this.updatePageReferences(cloner, (COSDictionary)kSubEntry, objMapping);
        }
    }

    private void updatePageReferences(PDFCloneUtility cloner, COSArray parentTreeEntry, Map<COSDictionary, COSDictionary> objMapping) throws IOException {
        for (int i = 0; i < parentTreeEntry.size(); ++i) {
            COSBase subEntry = parentTreeEntry.getObject(i);
            if (subEntry instanceof COSArray) {
                this.updatePageReferences(cloner, (COSArray)subEntry, objMapping);
                continue;
            }
            if (!(subEntry instanceof COSDictionary)) continue;
            this.updatePageReferences(cloner, (COSDictionary)subEntry, objMapping);
        }
    }

    private void updateStructParentEntries(PDPage page, int structParentOffset) throws IOException {
        int structParents = page.getStructParents();
        if (structParents >= 0) {
            page.setStructParents(structParents + structParentOffset);
        }
        List<PDAnnotation> annots = page.getAnnotations();
        ArrayList<PDAnnotation> newannots = new ArrayList<PDAnnotation>(annots.size());
        for (PDAnnotation annot : annots) {
            int structParent = annot.getStructParent();
            if (structParent >= 0) {
                annot.setStructParent(structParent + structParentOffset);
            }
            newannots.add(annot);
        }
        page.setAnnotations(newannots);
    }

    private boolean isDynamicXfa(PDAcroForm acroForm) {
        return acroForm != null && acroForm.xfaIsDynamic();
    }

    private void mergeInto(COSDictionary src, COSDictionary dst, Set<COSName> exclude) {
        for (Map.Entry<COSName, COSBase> entry : src.entrySet()) {
            if (exclude.contains(entry.getKey()) || dst.containsKey(entry.getKey())) continue;
            dst.setItem(entry.getKey(), entry.getValue());
        }
    }

    public static enum AcroFormMergeMode {
        JOIN_FORM_FIELDS_MODE,
        PDFBOX_LEGACY_MODE;

    }

    public static enum DocumentMergeMode {
        OPTIMIZE_RESOURCES_MODE,
        PDFBOX_LEGACY_MODE;

    }
}

