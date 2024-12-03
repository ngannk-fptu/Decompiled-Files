/*
 * Decompiled with CFR 0.152.
 */
package com.sun.pdfview.action;

import com.sun.pdfview.PDFObject;
import com.sun.pdfview.PDFParseException;
import com.sun.pdfview.action.PDFAction;
import com.sun.pdfview.action.PdfObjectParseUtil;
import java.io.IOException;

public class LaunchAction
extends PDFAction {
    public static final String SOLIDUS = "/";
    private FileSpec file;
    private boolean newWindow = false;
    private PDFObject unixParam;
    private PDFObject macParam;
    private WinLaunchParam winParam;

    public LaunchAction(PDFObject obj, PDFObject root) throws IOException {
        super("Launch");
        PDFObject fileObj = obj.getDictRef("F");
        this.file = this.parseFileSpecification(fileObj);
        PDFObject newWinObj = obj.getDictRef("NewWindow");
        if (newWinObj != null) {
            this.newWindow = newWinObj.getBooleanValue();
        }
        this.winParam = this.parseWinDict(obj.getDictRef("Win"));
        this.unixParam = obj.getDictRef("Unix");
        this.macParam = obj.getDictRef("Mac");
        if (this.file == null && this.winParam == null && this.unixParam == null && this.macParam == null) {
            throw new PDFParseException("Could not parse launch action (file or OS specific launch parameters are missing): " + obj.toString());
        }
    }

    public static boolean isAbsolute(String fileName) {
        return fileName.startsWith(SOLIDUS);
    }

    private FileSpec parseFileSpecification(PDFObject fileObj) throws PDFParseException, IOException {
        FileSpec file = null;
        if (fileObj != null) {
            file = new FileSpec();
            if (fileObj.getType() == 6) {
                file.setFileSystem(PdfObjectParseUtil.parseStringFromDict("FS", fileObj, false));
                file.setFileName(PdfObjectParseUtil.parseStringFromDict("F", fileObj, false));
                file.setUnicode(PdfObjectParseUtil.parseStringFromDict("UF", fileObj, false));
                file.setDosFileName(PdfObjectParseUtil.parseStringFromDict("DOS", fileObj, false));
                file.setMacFileName(PdfObjectParseUtil.parseStringFromDict("Mac", fileObj, false));
                file.setUnixFileName(PdfObjectParseUtil.parseStringFromDict("Unix", fileObj, false));
                file.setVolatileFile(PdfObjectParseUtil.parseBooleanFromDict("V", fileObj, false));
                file.setDescription(PdfObjectParseUtil.parseStringFromDict("Desc", fileObj, false));
                file.setId(fileObj.getDictRef("ID"));
                file.setEmbeddedFile(fileObj.getDictRef("EF"));
                file.setRelatedFile(fileObj.getDictRef("RF"));
                file.setCollectionItem(fileObj.getDictRef("CI"));
            } else if (fileObj.getType() == 3) {
                file.setFileName(fileObj.getStringValue());
            } else {
                throw new PDFParseException("File specification could not be parsed (should be of type 'Dictionary' or 'String'): " + fileObj.toString());
            }
        }
        return file;
    }

    private WinLaunchParam parseWinDict(PDFObject winDict) throws IOException {
        if (winDict == null) {
            return null;
        }
        WinLaunchParam param = new WinLaunchParam();
        param.setFileName(PdfObjectParseUtil.parseStringFromDict("F", winDict, true));
        param.setDirectory(PdfObjectParseUtil.parseStringFromDict("D", winDict, false));
        param.setOperation(PdfObjectParseUtil.parseStringFromDict("O", winDict, false));
        param.setParameter(PdfObjectParseUtil.parseStringFromDict("P", winDict, false));
        return param;
    }

    public FileSpec getFileSpecification() {
        return this.file;
    }

    public boolean isNewWindow() {
        return this.newWindow;
    }

    public PDFObject getUnixParam() {
        return this.unixParam;
    }

    public PDFObject getMacParam() {
        return this.macParam;
    }

    public WinLaunchParam getWinParam() {
        return this.winParam;
    }

    public static class FileSpec {
        private String fileSystem;
        private String fileName;
        private String dosFileName;
        private String unixFileName;
        private String macFileName;
        private String unicode;
        private PDFObject id;
        private boolean volatileFile;
        private PDFObject embeddedFile;
        private PDFObject relatedFile;
        private String description;
        private PDFObject collectionItem;

        public String getFileSystem() {
            return this.fileSystem;
        }

        public void setFileSystem(String fileSystem) {
            this.fileSystem = fileSystem;
        }

        public String getFileName() {
            String system = System.getProperty("os.name");
            if (system.startsWith("Windows")) {
                if (this.dosFileName != null) {
                    return this.dosFileName;
                }
            } else if (system.startsWith("mac os x")) {
                if (this.macFileName != null) {
                    return this.macFileName;
                }
            } else if (this.unixFileName != null) {
                return this.unixFileName;
            }
            return this.fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getDosFileName() {
            return this.dosFileName;
        }

        public void setDosFileName(String dosFileName) {
            this.dosFileName = dosFileName;
        }

        public String getUnixFileName() {
            return this.unixFileName;
        }

        public void setUnixFileName(String unixFileName) {
            this.unixFileName = unixFileName;
        }

        public String getMacFileName() {
            return this.macFileName;
        }

        public void setMacFileName(String macFileName) {
            this.macFileName = macFileName;
        }

        public String getUnicode() {
            return this.unicode;
        }

        public void setUnicode(String unicode) {
            this.unicode = unicode;
        }

        public PDFObject getId() {
            return this.id;
        }

        public void setId(PDFObject id) {
            this.id = id;
        }

        public boolean isVolatileFile() {
            return this.volatileFile;
        }

        public void setVolatileFile(boolean volatileFile) {
            this.volatileFile = volatileFile;
        }

        public PDFObject getEmbeddedFile() {
            return this.embeddedFile;
        }

        public void setEmbeddedFile(PDFObject embeddedFile) {
            this.embeddedFile = embeddedFile;
        }

        public PDFObject getRelatedFile() {
            return this.relatedFile;
        }

        public void setRelatedFile(PDFObject relatedFile) {
            this.relatedFile = relatedFile;
        }

        public String getDescription() {
            return this.description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public PDFObject getCollectionItem() {
            return this.collectionItem;
        }

        public void setCollectionItem(PDFObject collectionItem) {
            this.collectionItem = collectionItem;
        }
    }

    public class WinLaunchParam {
        private String fileName;
        private String directory;
        private String operation = "open";
        private String parameter;

        public String getFileName() {
            return this.fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getDirectory() {
            return this.directory;
        }

        public void setDirectory(String directory) {
            this.directory = directory;
        }

        public String getOperation() {
            return this.operation;
        }

        public void setOperation(String operation) {
            this.operation = operation;
        }

        public String getParameter() {
            return this.parameter;
        }

        public void setParameter(String parameter) {
            this.parameter = parameter;
        }
    }
}

