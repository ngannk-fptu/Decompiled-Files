/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.transfer.internal;

import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.concurrent.Callable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@SdkInternalApi
public class DownloadS3ObjectCallable
implements Callable<Long> {
    private static final Log LOG = LogFactory.getLog(DownloadS3ObjectCallable.class);
    private static final int BUFFER_SIZE = 0x200000;
    private final Callable<S3Object> serviceCall;
    private final File destinationFile;
    private final long position;

    public DownloadS3ObjectCallable(Callable<S3Object> serviceCall, File destinationFile, long position) {
        this.serviceCall = serviceCall;
        this.destinationFile = destinationFile;
        this.position = position;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Long call() throws Exception {
        long filePosition;
        RandomAccessFile randomAccessFile = new RandomAccessFile(this.destinationFile, "rw");
        FileChannel channel = randomAccessFile.getChannel();
        channel.position(this.position);
        S3ObjectInputStream objectContent = null;
        try {
            int bytesRead;
            S3Object object = this.serviceCall.call();
            objectContent = object.getObjectContent();
            byte[] buffer = new byte[0x200000];
            ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
            while ((bytesRead = objectContent.read(buffer)) > -1) {
                byteBuffer.limit(bytesRead);
                while (byteBuffer.hasRemaining()) {
                    channel.write(byteBuffer);
                }
                byteBuffer.clear();
            }
            filePosition = channel.position();
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(objectContent, LOG);
            IOUtils.closeQuietly(randomAccessFile, LOG);
            IOUtils.closeQuietly(channel, LOG);
            throw throwable;
        }
        IOUtils.closeQuietly(objectContent, LOG);
        IOUtils.closeQuietly(randomAccessFile, LOG);
        IOUtils.closeQuietly(channel, LOG);
        return filePosition;
    }
}

