/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.cells.CellsHelper
 *  com.aspose.cells.ImageOrPrintOptions
 *  com.aspose.cells.InterruptMonitor
 *  com.aspose.cells.LoadOptions
 *  com.aspose.cells.Range
 *  com.aspose.cells.SheetRender
 *  com.aspose.cells.Workbook
 *  com.aspose.cells.Worksheet
 *  com.google.common.util.concurrent.ThreadFactoryBuilder
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.conversion.convert.image;

import com.aspose.cells.CellsHelper;
import com.aspose.cells.ImageOrPrintOptions;
import com.aspose.cells.InterruptMonitor;
import com.aspose.cells.LoadOptions;
import com.aspose.cells.Range;
import com.aspose.cells.SheetRender;
import com.aspose.cells.Workbook;
import com.aspose.cells.Worksheet;
import com.atlassian.plugins.conversion.convert.ConversionException;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.bean.BeanFile;
import com.atlassian.plugins.conversion.convert.bean.BeanResult;
import com.atlassian.plugins.conversion.convert.image.AbstractConverter;
import com.atlassian.plugins.conversion.convert.store.ConversionStore;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CellsConverter
extends AbstractConverter {
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder().setDaemon(true).setNameFormat("conversion-cell-interrupter-thread-%d").setPriority(1).build());
    private static final Integer MAXIMUM_CELL_NUMBERS = Integer.getInteger("confluence.document.conversion.maximum.cells", 1000000);
    private static final Integer SAVE_TIMEOUT_IN_SECONDS = Integer.getInteger("confluence.document.conversion.spreadsheet.convert.timeout", 30);
    private static final Integer MAXIMUM_COLUMNS = 128;
    private static final Logger logger = LoggerFactory.getLogger(CellsConverter.class);

    @Override
    public BeanResult convert(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, ConversionStore conversionStore, String fileName, Collection<Integer> pageNumbers) throws Exception {
        Workbook doc = this.buildWorkbook(inStream);
        String onlyName = CellsConverter.getOnlyName(fileName);
        BeanResult result = new BeanResult();
        switch (outFileFormat) {
            case PDF: {
                UUID uuid = UUID.randomUUID();
                OutputStream outputStream = conversionStore.createFile(uuid);
                this.convertToPDF(doc, outputStream);
                result.result = Collections.singletonList(new BeanFile(uuid, -1, onlyName, outFileFormat));
                break;
            }
            case JPG: 
            case PNG: {
                int pageIdx = 0;
                int j = 0;
                result.result = new ArrayList<BeanFile>();
                String formatName = outFileFormat.name().toLowerCase();
                this.assertNotTooManyCells(doc);
                for (Object o : doc.getWorksheets()) {
                    Worksheet sheet = (Worksheet)o;
                    ImageOrPrintOptions imgOptions = new ImageOrPrintOptions();
                    imgOptions.setImageType(outFileFormat == FileFormat.PNG ? 6 : 5);
                    SheetRender sheetRender = new SheetRender(sheet, imgOptions);
                    for (int k = 0; k < sheetRender.getPageCount(); ++k) {
                        UUID uuid = UUID.randomUUID();
                        OutputStream outputStream = conversionStore.createFile(uuid);
                        sheetRender.toImage(k, outputStream);
                        result.result.add(new BeanFile(uuid, pageIdx++, onlyName + "-" + j + "." + formatName, outFileFormat));
                    }
                    ++j;
                }
                result.numPages = pageIdx;
                break;
            }
            default: {
                throw new ConversionException("Unknown format");
            }
        }
        return result;
    }

    @Override
    public void convertDocDirect(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, OutputStream outStream) throws Exception {
        if (outFileFormat != FileFormat.PDF) {
            throw new ConversionException("Unknown format");
        }
        Workbook doc = this.buildWorkbook(inStream);
        this.convertToPDF(doc, outStream);
    }

    private Workbook buildWorkbook(InputStream inputStream) throws Exception {
        LoadOptions loadOptions = new LoadOptions();
        loadOptions.setMemorySetting(1);
        Workbook workbook = new Workbook(inputStream, loadOptions);
        this.setAutoFitRow(workbook);
        return workbook;
    }

    private void convertToPDF(Workbook workbook, OutputStream outputStream) throws Exception {
        this.assertNotTooManyCells(workbook);
        InterruptMonitor monitor = new InterruptMonitor();
        workbook.setInterruptMonitor(monitor);
        ScheduledFuture<Boolean> scheduledFuture = scheduler.schedule(() -> {
            monitor.interrupt();
            return true;
        }, (long)SAVE_TIMEOUT_IN_SECONDS.intValue(), TimeUnit.SECONDS);
        workbook.save(outputStream, 13);
        scheduledFuture.cancel(true);
    }

    @Override
    public void generateThumbnailDirect(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, OutputStream outStream, int pageNumber, double maxWidth, double maxHeight) throws ConversionException {
        try {
            int imageType;
            Workbook doc = this.buildWorkbook(inStream);
            ImageOrPrintOptions imgOptions = new ImageOrPrintOptions();
            switch (outFileFormat) {
                case PNG: {
                    imageType = 6;
                    break;
                }
                case JPG: {
                    imageType = 5;
                    break;
                }
                default: {
                    throw new ConversionException("Unsupported image format (" + (Object)((Object)outFileFormat) + ")");
                }
            }
            imgOptions.setImageType(imageType);
            boolean firstWorkSheet = false;
            Worksheet worksheet = doc.getWorksheets().get(0);
            this.setupRangeWhenHavingTooManyCells(worksheet);
            SheetRender sheetRender = new SheetRender(worksheet, imgOptions);
            if (pageNumber < 1 || pageNumber > sheetRender.getPageCount()) {
                throw new ConversionException("Only rendering of the first page of the first sheet supported for now");
            }
            int newPageNum = pageNumber - 1;
            float[] pageSize = sheetRender.getPageSize(newPageNum);
            double ratio = CellsConverter.findRatio(pageSize[0], pageSize[1], maxWidth, maxHeight);
            imgOptions.setHorizontalResolution((int)Math.floor((double)imgOptions.getHorizontalResolution() * ratio));
            imgOptions.setVerticalResolution((int)Math.floor((double)imgOptions.getVerticalResolution() * ratio));
            sheetRender.toImage(newPageNum, outStream);
        }
        catch (Exception e) {
            logger.warn("Could not generate thumbnail", (Throwable)e);
            throw new ConversionException("Unknown error: " + e.getMessage());
        }
    }

    @Override
    public boolean handlesFileFormat(FileFormat inFileFormat) {
        return inFileFormat == FileFormat.XLS || inFileFormat == FileFormat.XLSX;
    }

    @Override
    public FileFormat getBestOutputFormat(FileFormat inFileFormat) {
        return this.handlesFileFormat(inFileFormat) ? FileFormat.PDF : null;
    }

    private void setAutoFitRow(Workbook workbook) throws Exception {
        for (Object obj : workbook.getWorksheets()) {
            Worksheet sheet = (Worksheet)obj;
            sheet.autoFitRows();
        }
    }

    private void setupRangeWhenHavingTooManyCells(Worksheet worksheet) {
        Range range = worksheet.getCells().getMaxDisplayRange();
        BigInteger cellCount = BigInteger.valueOf(range.getColumnCount()).multiply(BigInteger.valueOf(range.getRowCount()));
        if (cellCount.compareTo(BigInteger.valueOf(MAXIMUM_CELL_NUMBERS.intValue())) > 0) {
            logger.debug("Maximum cell count is exceeded, hence the range is updated to generate the thumbnail.");
            int columnIndex = Math.min(range.getColumnCount(), MAXIMUM_COLUMNS);
            int rowNumber = MAXIMUM_CELL_NUMBERS / columnIndex;
            worksheet.getPageSetup().setPrintArea("A1:" + CellsHelper.columnIndexToName((int)(columnIndex - 1)) + CellsHelper.rowIndexToName((int)(rowNumber - 1)));
        }
    }

    private void assertNotTooManyCells(Workbook workbook) throws ConversionException {
        BigInteger cellCount = BigInteger.ZERO;
        int worksheetCounts = workbook.getWorksheets().getCount();
        for (int index = 0; index < worksheetCounts; ++index) {
            Range range = workbook.getWorksheets().get(index).getCells().getMaxDisplayRange();
            cellCount = cellCount.add(BigInteger.valueOf(range.getColumnCount()).multiply(BigInteger.valueOf(range.getRowCount())));
        }
        if (cellCount.compareTo(BigInteger.valueOf(MAXIMUM_CELL_NUMBERS.intValue())) > 0) {
            logger.debug("Could not convert the spreadsheet as the total cell count exceeds the limit");
            throw new ConversionException("Cannot convert the spreadsheet as there are too many cells!");
        }
    }
}

