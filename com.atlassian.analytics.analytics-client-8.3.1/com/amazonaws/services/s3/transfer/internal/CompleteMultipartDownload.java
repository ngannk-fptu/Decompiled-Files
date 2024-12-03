/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.transfer.internal;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.services.s3.internal.FileLocks;
import com.amazonaws.services.s3.transfer.Transfer;
import com.amazonaws.services.s3.transfer.internal.DownloadImpl;
import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@SdkInternalApi
public class CompleteMultipartDownload
implements Callable<File> {
    private final List<Future<Long>> partFiles;
    private final File destinationFile;
    private final DownloadImpl download;
    private Integer currentPartNumber;

    public CompleteMultipartDownload(List<Future<Long>> files, File destinationFile, DownloadImpl download, Integer currentPartNumber) {
        this.partFiles = files;
        this.destinationFile = destinationFile;
        this.download = download;
        this.currentPartNumber = currentPartNumber;
    }

    @Override
    public File call() throws Exception {
        try {
            for (Future<Long> file : this.partFiles) {
                long filePosition = file.get();
                Integer n = this.currentPartNumber;
                Integer n2 = this.currentPartNumber = Integer.valueOf(this.currentPartNumber + 1);
                this.download.updatePersistableTransfer(n, filePosition);
            }
            this.download.setState(Transfer.TransferState.Completed);
        }
        catch (Exception exception) {
            this.cleanUpAfterException();
            throw new SdkClientException("Unable to complete multipart download. Individual part download failed.", exception);
        }
        finally {
            FileLocks.unlock(this.destinationFile);
        }
        return this.destinationFile;
    }

    private void cleanUpAfterException() {
        for (Future<Long> file : this.partFiles) {
            file.cancel(false);
        }
        this.download.setState(Transfer.TransferState.Failed);
    }
}

