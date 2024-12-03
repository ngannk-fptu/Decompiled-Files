/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  org.apache.commons.io.FileUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.conversion.impl.runnable;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.conversion.impl.FileSystemConversionState;
import com.atlassian.confluence.plugins.conversion.impl.runnable.CouldNotReserveMemoryForConversionException;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class ConversionRunnable
implements Runnable {
    protected static final Logger log = LoggerFactory.getLogger(ConversionRunnable.class);
    protected final File tempFile;
    protected final File file;
    protected final FileSystemConversionState conversionState;
    protected final Attachment attachment;

    protected ConversionRunnable(FileSystemConversionState conversionState, Attachment attachment) {
        this.conversionState = conversionState;
        this.attachment = attachment;
        this.tempFile = conversionState.getTempFile();
        this.file = conversionState.getConvertedFile();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        try (FileOutputStream fos = new FileOutputStream(this.tempFile);){
            this.doWork(fos);
            fos.close();
            Files.move(this.tempFile.toPath(), this.file.toPath(), StandardCopyOption.ATOMIC_MOVE);
        }
        catch (CouldNotReserveMemoryForConversionException e) {
            log.warn("Could not reserve enough memory to get the thumbnail from pdf.");
        }
        catch (Exception e) {
            this.conversionState.markAsError();
            log.error("Cannot create conversion.", (Throwable)e);
        }
        finally {
            FileUtils.deleteQuietly((File)this.tempFile);
        }
    }

    protected abstract void doWork(FileOutputStream var1) throws Exception;
}

