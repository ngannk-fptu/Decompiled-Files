/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.core.pagination.sync.SdkIterable
 */
package software.amazon.awssdk.services.s3.internal.crt;

import java.util.Iterator;
import java.util.NoSuchElementException;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.services.s3.internal.multipart.SdkPojoConversionUtils;
import software.amazon.awssdk.services.s3.model.CopyObjectRequest;
import software.amazon.awssdk.services.s3.model.UploadPartCopyRequest;

@SdkInternalApi
public final class UploadPartCopyRequestIterable
implements SdkIterable<UploadPartCopyRequest> {
    private final String uploadId;
    private final long optimalPartSize;
    private final CopyObjectRequest copyObjectRequest;
    private long remainingBytes;
    private int partNumber = 1;
    private long offset = 0L;

    public UploadPartCopyRequestIterable(String uploadId, long partSize, CopyObjectRequest copyObjectRequest, long remainingBytes) {
        this.uploadId = uploadId;
        this.optimalPartSize = partSize;
        this.copyObjectRequest = copyObjectRequest;
        this.remainingBytes = remainingBytes;
    }

    public Iterator<UploadPartCopyRequest> iterator() {
        return new UploadPartCopyRequestIterator();
    }

    private class UploadPartCopyRequestIterator
    implements Iterator<UploadPartCopyRequest> {
        private UploadPartCopyRequestIterator() {
        }

        @Override
        public boolean hasNext() {
            return UploadPartCopyRequestIterable.this.remainingBytes > 0L;
        }

        @Override
        public UploadPartCopyRequest next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException("No UploadPartCopyRequest available");
            }
            long partSize = Math.min(UploadPartCopyRequestIterable.this.optimalPartSize, UploadPartCopyRequestIterable.this.remainingBytes);
            String range = this.range(partSize);
            UploadPartCopyRequest uploadPartCopyRequest = SdkPojoConversionUtils.toUploadPartCopyRequest(UploadPartCopyRequestIterable.this.copyObjectRequest, UploadPartCopyRequestIterable.this.partNumber, UploadPartCopyRequestIterable.this.uploadId, range);
            UploadPartCopyRequestIterable.this.partNumber++;
            UploadPartCopyRequestIterable.this.offset = UploadPartCopyRequestIterable.this.offset + partSize;
            UploadPartCopyRequestIterable.this.remainingBytes = UploadPartCopyRequestIterable.this.remainingBytes - partSize;
            return uploadPartCopyRequest;
        }

        private String range(long partSize) {
            return "bytes=" + UploadPartCopyRequestIterable.this.offset + "-" + (UploadPartCopyRequestIterable.this.offset + partSize - 1L);
        }
    }
}

