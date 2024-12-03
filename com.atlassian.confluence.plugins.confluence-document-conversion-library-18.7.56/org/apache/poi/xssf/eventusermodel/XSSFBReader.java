/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.eventusermodel;

import com.zaxxer.sparsebits.SparseBitSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.util.LittleEndian;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.binary.XSSFBCommentsTable;
import org.apache.poi.xssf.binary.XSSFBParseException;
import org.apache.poi.xssf.binary.XSSFBParser;
import org.apache.poi.xssf.binary.XSSFBRecordType;
import org.apache.poi.xssf.binary.XSSFBRelation;
import org.apache.poi.xssf.binary.XSSFBStylesTable;
import org.apache.poi.xssf.binary.XSSFBUtils;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.usermodel.XSSFRelation;

public class XSSFBReader
extends XSSFReader {
    private static final Logger LOGGER = LogManager.getLogger(XSSFBReader.class);
    private static final Set<String> WORKSHEET_RELS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(XSSFRelation.WORKSHEET.getRelation(), XSSFRelation.CHARTSHEET.getRelation(), XSSFRelation.MACRO_SHEET_BIN.getRelation(), XSSFRelation.INTL_MACRO_SHEET_BIN.getRelation(), XSSFRelation.DIALOG_SHEET_BIN.getRelation())));

    public XSSFBReader(OPCPackage pkg) throws IOException, OpenXML4JException {
        super(pkg);
    }

    public String getAbsPathMetadata() throws IOException {
        try (InputStream is = this.workbookPart.getInputStream();){
            PathExtractor p = new PathExtractor(is);
            p.parse();
            String string = p.getPath();
            return string;
        }
    }

    @Override
    public Iterator<InputStream> getSheetsData() throws IOException, InvalidFormatException {
        return new SheetIterator(this.workbookPart);
    }

    public XSSFBStylesTable getXSSFBStylesTable() throws IOException {
        ArrayList<PackagePart> parts = this.pkg.getPartsByContentType(XSSFBRelation.STYLES_BINARY.getContentType());
        if (parts.isEmpty()) {
            return null;
        }
        try (InputStream stream = parts.get(0).getInputStream();){
            XSSFBStylesTable xSSFBStylesTable = new XSSFBStylesTable(stream);
            return xSSFBStylesTable;
        }
    }

    private static class SheetRefLoader
    extends XSSFBParser {
        List<XSSFReader.XSSFSheetRef> sheets = new LinkedList<XSSFReader.XSSFSheetRef>();

        private SheetRefLoader(InputStream is) {
            super(is);
        }

        @Override
        public void handleRecord(int recordType, byte[] data) throws XSSFBParseException {
            if (recordType == XSSFBRecordType.BrtBundleSh.getId()) {
                this.addWorksheet(data);
            }
        }

        private void addWorksheet(byte[] data) {
            try {
                this.tryToAddWorksheet(data);
            }
            catch (XSSFBParseException e) {
                if (this.tryOldFormat(data)) {
                    LOGGER.atWarn().log("This file was written with a beta version of Excel. POI will try to parse the file as a regular xlsb.");
                }
                throw e;
            }
        }

        private void tryToAddWorksheet(byte[] data) throws XSSFBParseException {
            int offset = 0;
            LittleEndian.getUInt(data, offset);
            long iTabID = LittleEndian.getUInt(data, offset += 4);
            offset += 4;
            if (iTabID < 1L || iTabID > 65535L) {
                throw new XSSFBParseException("table id out of range: " + iTabID);
            }
            StringBuilder sb = new StringBuilder();
            offset += XSSFBUtils.readXLWideString(data, offset, sb);
            String relId = sb.toString();
            sb.setLength(0);
            XSSFBUtils.readXLWideString(data, offset, sb);
            String name = sb.toString();
            if (StringUtil.isNotBlank(relId)) {
                this.sheets.add(new XSSFReader.XSSFSheetRef(relId, name));
            }
        }

        private boolean tryOldFormat(byte[] data) throws XSSFBParseException {
            int offset = 8;
            long iTabID = LittleEndian.getUInt(data, offset);
            offset += 4;
            if (iTabID < 1L || iTabID > 65535L) {
                throw new XSSFBParseException("table id out of range: " + iTabID);
            }
            StringBuilder sb = new StringBuilder();
            offset += XSSFBUtils.readXLWideString(data, offset, sb);
            String relId = sb.toString();
            sb.setLength(0);
            offset += XSSFBUtils.readXLWideString(data, offset, sb);
            String name = sb.toString();
            if (StringUtil.isNotBlank(relId)) {
                this.sheets.add(new XSSFReader.XSSFSheetRef(relId, name));
            }
            return offset == data.length;
        }

        List<XSSFReader.XSSFSheetRef> getSheets() {
            return this.sheets;
        }
    }

    private static class PathExtractor
    extends XSSFBParser {
        private static SparseBitSet RECORDS = new SparseBitSet();
        private String path;

        public PathExtractor(InputStream is) {
            super(is, RECORDS);
        }

        @Override
        public void handleRecord(int recordType, byte[] data) throws XSSFBParseException {
            if (recordType != XSSFBRecordType.BrtAbsPath15.getId()) {
                return;
            }
            StringBuilder sb = new StringBuilder();
            XSSFBUtils.readXLWideString(data, 0, sb);
            this.path = sb.toString();
        }

        String getPath() {
            return this.path;
        }

        static {
            RECORDS.set(XSSFBRecordType.BrtAbsPath15.getId());
        }
    }

    public static class SheetIterator
    extends XSSFReader.SheetIterator {
        private SheetIterator(PackagePart wb) throws IOException {
            super(wb);
        }

        @Override
        protected Set<String> getSheetRelationships() {
            return WORKSHEET_RELS;
        }

        @Override
        protected Iterator<XSSFReader.XSSFSheetRef> createSheetIteratorFromWB(PackagePart wb) throws IOException {
            try (InputStream stream = wb.getInputStream();){
                SheetRefLoader sheetRefLoader = new SheetRefLoader(stream);
                sheetRefLoader.parse();
                Iterator<XSSFReader.XSSFSheetRef> iterator = sheetRefLoader.getSheets().iterator();
                return iterator;
            }
        }

        @Override
        public CommentsTable getSheetComments() {
            throw new IllegalArgumentException("Please use getXSSFBSheetComments");
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public XSSFBCommentsTable getXSSFBSheetComments() {
            PackagePart sheetPkg = this.getSheetPart();
            try {
                PackageRelationshipCollection commentsList = sheetPkg.getRelationshipsByType(XSSFRelation.SHEET_COMMENTS.getRelation());
                if (commentsList.isEmpty()) return null;
                PackageRelationship comments = commentsList.getRelationship(0);
                if (comments == null) return null;
                if (comments.getTargetURI() == null) {
                    return null;
                }
                PackagePartName commentsName = PackagingURIHelper.createPartName(comments.getTargetURI());
                PackagePart commentsPart = sheetPkg.getPackage().getPart(commentsName);
                try (InputStream stream = commentsPart.getInputStream();){
                    XSSFBCommentsTable xSSFBCommentsTable = new XSSFBCommentsTable(stream);
                    return xSSFBCommentsTable;
                }
            }
            catch (IOException | InvalidFormatException e) {
                return null;
            }
        }
    }
}

