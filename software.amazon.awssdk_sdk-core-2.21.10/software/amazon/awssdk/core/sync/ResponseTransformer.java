/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkPublicApi
 *  software.amazon.awssdk.http.AbortableInputStream
 *  software.amazon.awssdk.utils.IoUtils
 *  software.amazon.awssdk.utils.Logger
 */
package software.amazon.awssdk.core.sync;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import software.amazon.awssdk.annotations.SdkPublicApi;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.RetryableException;
import software.amazon.awssdk.core.internal.http.InterruptMonitor;
import software.amazon.awssdk.http.AbortableInputStream;
import software.amazon.awssdk.utils.IoUtils;
import software.amazon.awssdk.utils.Logger;

@FunctionalInterface
@SdkPublicApi
public interface ResponseTransformer<ResponseT, ReturnT> {
    public ReturnT transform(ResponseT var1, AbortableInputStream var2) throws Exception;

    default public boolean needsConnectionLeftOpen() {
        return false;
    }

    public static <ResponseT> ResponseTransformer<ResponseT, ResponseT> toFile(Path path) {
        return (resp, in) -> {
            try {
                InterruptMonitor.checkInterrupted();
                Files.copy((InputStream)in, path, new CopyOption[0]);
                return resp;
            }
            catch (IOException copyException) {
                String copyError = "Failed to read response into file: " + path;
                if (copyException instanceof FileAlreadyExistsException || copyException instanceof DirectoryNotEmptyException) {
                    throw new IOException(copyError, copyException);
                }
                try {
                    Files.deleteIfExists(path);
                }
                catch (IOException deletionException) {
                    Logger.loggerFor(ResponseTransformer.class).error(() -> "Failed to delete destination file '" + path + "' after reading the service response failed.", (Throwable)deletionException);
                    throw new IOException(copyError + ". Additionally, the file could not be cleaned up (" + deletionException.getMessage() + "), so the request will not be retried.", copyException);
                }
                throw RetryableException.builder().message(copyError).cause(copyException).build();
            }
        };
    }

    public static <ResponseT> ResponseTransformer<ResponseT, ResponseT> toFile(File file) {
        return ResponseTransformer.toFile(file.toPath());
    }

    public static <ResponseT> ResponseTransformer<ResponseT, ResponseT> toOutputStream(OutputStream outputStream) {
        return (resp, in) -> {
            InterruptMonitor.checkInterrupted();
            IoUtils.copy((InputStream)in, (OutputStream)outputStream);
            return resp;
        };
    }

    public static <ResponseT> ResponseTransformer<ResponseT, ResponseBytes<ResponseT>> toBytes() {
        return (response, inputStream) -> {
            try {
                InterruptMonitor.checkInterrupted();
                return ResponseBytes.fromByteArrayUnsafe(response, IoUtils.toByteArray((InputStream)inputStream));
            }
            catch (IOException e) {
                throw RetryableException.builder().message("Failed to read response.").cause(e).build();
            }
        };
    }

    public static <ResponseT> ResponseTransformer<ResponseT, ResponseInputStream<ResponseT>> toInputStream() {
        return ResponseTransformer.unmanaged(ResponseInputStream::new);
    }

    public static <ResponseT, ReturnT> ResponseTransformer<ResponseT, ReturnT> unmanaged(final ResponseTransformer<ResponseT, ReturnT> transformer) {
        return new ResponseTransformer<ResponseT, ReturnT>(){

            @Override
            public ReturnT transform(ResponseT response, AbortableInputStream inputStream) throws Exception {
                InterruptMonitor.checkInterrupted();
                return transformer.transform(response, inputStream);
            }

            @Override
            public boolean needsConnectionLeftOpen() {
                return true;
            }
        };
    }
}

