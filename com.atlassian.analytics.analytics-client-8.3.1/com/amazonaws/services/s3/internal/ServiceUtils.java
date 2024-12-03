/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.services.s3.internal;

import com.amazonaws.Request;
import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.internal.FileLocks;
import com.amazonaws.services.s3.internal.RequestCopyUtils;
import com.amazonaws.services.s3.internal.SkipMd5CheckStrategy;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.exception.FileLockException;
import com.amazonaws.util.BinaryUtils;
import com.amazonaws.util.DateUtils;
import com.amazonaws.util.IOUtils;
import com.amazonaws.util.Md5Utils;
import com.amazonaws.util.SdkHttpUtils;
import com.amazonaws.util.StringUtils;
import com.amazonaws.util.ValidationUtils;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.net.ssl.SSLProtocolException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ServiceUtils {
    private static final Log LOG = LogFactory.getLog(ServiceUtils.class);
    public static final boolean APPEND_MODE = true;
    public static final boolean OVERWRITE_MODE = false;
    private static final SkipMd5CheckStrategy skipMd5CheckStrategy = SkipMd5CheckStrategy.INSTANCE;
    @Deprecated
    protected static final DateUtils dateUtils = new DateUtils();

    public static Date parseIso8601Date(String dateString) {
        return DateUtils.parseISO8601Date(dateString);
    }

    public static String formatIso8601Date(Date date) {
        return DateUtils.formatISO8601Date(date);
    }

    public static Date parseRfc822Date(String dateString) {
        if (StringUtils.isNullOrEmpty(dateString)) {
            return null;
        }
        return DateUtils.parseRFC822Date(dateString);
    }

    public static String formatRfc822Date(Date date) {
        return DateUtils.formatRFC822Date(date);
    }

    public static byte[] toByteArray(String s) {
        return s.getBytes(StringUtils.UTF8);
    }

    public static String removeQuotes(String s) {
        if (s == null) {
            return null;
        }
        if ((s = s.trim()).startsWith("\"")) {
            s = s.substring(1);
        }
        if (s.endsWith("\"")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }

    @Deprecated
    public static URL convertRequestToUrl(Request<?> request) {
        return ServiceUtils.convertRequestToUrl(request, false);
    }

    @Deprecated
    public static URL convertRequestToUrl(Request<?> request, boolean removeLeadingSlashInResourcePath) {
        return ServiceUtils.convertRequestToUrl(request, removeLeadingSlashInResourcePath, true);
    }

    public static URL convertRequestToUrl(Request<?> request, boolean removeLeadingSlashInResourcePath, boolean urlEncode) {
        String resourcePath;
        String string = resourcePath = urlEncode ? SdkHttpUtils.urlEncode(request.getResourcePath(), true) : request.getResourcePath();
        if (removeLeadingSlashInResourcePath && resourcePath.startsWith("/")) {
            resourcePath = resourcePath.substring(1);
        }
        String urlPath = "/" + resourcePath;
        urlPath = urlPath.replaceAll("(?<=/)/", "%2F");
        StringBuilder url = new StringBuilder(request.getEndpoint().toString());
        url.append(urlPath);
        StringBuilder queryParams = new StringBuilder();
        Map<String, List<String>> requestParams = request.getParameters();
        for (Map.Entry<String, List<String>> entry : requestParams.entrySet()) {
            for (String value : entry.getValue()) {
                queryParams = queryParams.length() > 0 ? queryParams.append("&") : queryParams.append("?");
                queryParams.append(entry.getKey()).append("=").append(SdkHttpUtils.urlEncode(value, false));
            }
        }
        url.append(queryParams.toString());
        try {
            return new URL(url.toString());
        }
        catch (MalformedURLException e) {
            throw new SdkClientException("Unable to convert request to well formed URL: " + e.getMessage(), e);
        }
    }

    public static String join(List<String> strings) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (String s : strings) {
            if (!first) {
                result.append(", ");
            }
            result.append(s);
            first = false;
        }
        return result.toString();
    }

    public static void downloadObjectToFile(S3Object s3Object, File destinationFile, boolean performIntegrityCheck, boolean appendData) {
        ServiceUtils.downloadToFile(s3Object, destinationFile, performIntegrityCheck, appendData, -1L);
    }

    public static void downloadToFile(S3Object s3Object, File dstfile, boolean performIntegrityCheck, boolean appendData, long expectedFileLength) {
        ServiceUtils.createParentDirectoryIfNecessary(dstfile);
        if (!FileLocks.lock(dstfile)) {
            throw new FileLockException("Fail to lock " + dstfile + " for appendData=" + appendData);
        }
        BufferedOutputStream outputStream = null;
        try {
            int bytesRead;
            long actualLen = dstfile.length();
            if (appendData && actualLen != expectedFileLength) {
                throw new IllegalStateException("Expected file length to append is " + expectedFileLength + " but actual length is " + actualLen + " for file " + dstfile);
            }
            outputStream = new BufferedOutputStream(new FileOutputStream(dstfile, appendData));
            byte[] buffer = new byte[10240];
            while ((bytesRead = s3Object.getObjectContent().read(buffer)) > -1) {
                ((OutputStream)outputStream).write(buffer, 0, bytesRead);
            }
        }
        catch (IOException e) {
            try {
                s3Object.getObjectContent().abort();
                throw new SdkClientException("Unable to store object contents to disk: " + e.getMessage(), e);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(outputStream, LOG);
                FileLocks.unlock(dstfile);
                IOUtils.closeQuietly(s3Object.getObjectContent(), LOG);
                throw throwable;
            }
        }
        IOUtils.closeQuietly(outputStream, LOG);
        FileLocks.unlock(dstfile);
        IOUtils.closeQuietly(s3Object.getObjectContent(), LOG);
        if (performIntegrityCheck) {
            byte[] clientSideHash = null;
            byte[] serverSideHash = null;
            try {
                ObjectMetadata metadata = s3Object.getObjectMetadata();
                if (!skipMd5CheckStrategy.skipClientSideValidationPerGetResponse(metadata)) {
                    clientSideHash = Md5Utils.computeMD5Hash(new FileInputStream(dstfile));
                    serverSideHash = BinaryUtils.fromHex(metadata.getETag());
                }
            }
            catch (Exception e) {
                LOG.warn((Object)("Unable to calculate MD5 hash to validate download: " + e.getMessage()), (Throwable)e);
            }
            if (clientSideHash != null && serverSideHash != null && !Arrays.equals(clientSideHash, serverSideHash)) {
                throw new SdkClientException("Unable to verify integrity of data download.  Client calculated content hash didn't match hash calculated by Amazon S3.  The data stored in '" + dstfile.getAbsolutePath() + "' may be corrupt.");
            }
        }
    }

    public static void createParentDirectoryIfNecessary(File file) {
        File parentDirectory = file.getParentFile();
        if (parentDirectory == null || parentDirectory.mkdirs() || parentDirectory.exists()) {
            return;
        }
        throw new SdkClientException("Unable to create directory in the path: " + parentDirectory.getAbsolutePath());
    }

    public static S3Object retryableDownloadS3ObjectToFile(File file, RetryableS3DownloadTask retryableS3DownloadTask, boolean appendData) {
        S3Object s3Object;
        boolean needRetry;
        boolean hasRetried = false;
        do {
            needRetry = false;
            s3Object = retryableS3DownloadTask.getS3ObjectStream();
            if (s3Object == null) {
                return null;
            }
            try {
                ServiceUtils.downloadObjectToFile(s3Object, file, retryableS3DownloadTask.needIntegrityCheck(), appendData);
            }
            catch (SdkClientException ace) {
                if (!ace.isRetryable()) {
                    s3Object.getObjectContent().abort();
                    throw ace;
                }
                if (ace.getCause() instanceof SocketException || ace.getCause() instanceof SSLProtocolException) {
                    throw ace;
                }
                needRetry = true;
                if (hasRetried) {
                    s3Object.getObjectContent().abort();
                    throw ace;
                }
                LOG.info((Object)("Retry the download of object " + s3Object.getKey() + " (bucket " + s3Object.getBucketName() + ")"), (Throwable)ace);
                hasRetried = true;
            }
        } while (needRetry);
        return s3Object;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static void appendFile(File sourceFile, File destinationFile) {
        ValidationUtils.assertNotNull(destinationFile, "destFile");
        ValidationUtils.assertNotNull(sourceFile, "sourceFile");
        if (!FileLocks.lock(sourceFile)) {
            throw new FileLockException("Fail to lock " + sourceFile);
        }
        if (!FileLocks.lock(destinationFile)) {
            throw new FileLockException("Fail to lock " + destinationFile);
        }
        FileChannel in = null;
        FileChannel out = null;
        try {
            in = new FileInputStream(sourceFile).getChannel();
            out = new FileOutputStream(destinationFile, true).getChannel();
            long size = in.size();
            long count = 0x2000000L;
            for (long position = 0L; position < size; position += in.transferTo(position, 0x2000000L, out)) {
            }
        }
        catch (IOException e) {
            try {
                throw new SdkClientException("Unable to append file " + sourceFile.getAbsolutePath() + "to destination file " + destinationFile.getAbsolutePath() + "\n" + e.getMessage(), e);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(out, LOG);
                IOUtils.closeQuietly(in, LOG);
                FileLocks.unlock(sourceFile);
                FileLocks.unlock(destinationFile);
                try {
                    if (sourceFile.delete()) throw throwable;
                    LOG.warn((Object)("Failed to delete file " + sourceFile.getAbsolutePath()));
                    throw throwable;
                }
                catch (SecurityException exception) {
                    LOG.warn((Object)("Security manager denied delete access to file " + sourceFile.getAbsolutePath()));
                }
                throw throwable;
            }
        }
        IOUtils.closeQuietly(out, LOG);
        IOUtils.closeQuietly(in, LOG);
        FileLocks.unlock(sourceFile);
        FileLocks.unlock(destinationFile);
        try {
            if (sourceFile.delete()) return;
            LOG.warn((Object)("Failed to delete file " + sourceFile.getAbsolutePath()));
            return;
        }
        catch (SecurityException exception) {
            LOG.warn((Object)("Security manager denied delete access to file " + sourceFile.getAbsolutePath()));
            return;
        }
    }

    public static boolean isS3USStandardEndpoint(String endpoint) {
        return endpoint.endsWith("s3.amazonaws.com");
    }

    public static boolean isS3USEastEndpiont(String endpoint) {
        return ServiceUtils.isS3USStandardEndpoint(endpoint) || endpoint.endsWith("s3-external-1.amazonaws.com");
    }

    public static boolean isS3AccelerateEndpoint(String endpoint) {
        return endpoint.endsWith("s3-accelerate.amazonaws.com") || endpoint.endsWith("s3-accelerate.dualstack.amazonaws.com");
    }

    public static Integer getPartCount(GetObjectRequest getObjectRequest, AmazonS3 s3) {
        ValidationUtils.assertNotNull(s3, "S3 client");
        ValidationUtils.assertNotNull(getObjectRequest, "GetObjectRequest");
        GetObjectMetadataRequest getObjectMetadataRequest = RequestCopyUtils.createGetObjectMetadataRequestFrom(getObjectRequest).withPartNumber(1);
        return s3.getObjectMetadata(getObjectMetadataRequest).getPartCount();
    }

    @SdkInternalApi
    public static long getPartSize(GetObjectRequest getObjectRequest, AmazonS3 s3, int partNumber) {
        ValidationUtils.assertNotNull(s3, "S3 client");
        ValidationUtils.assertNotNull(getObjectRequest, "GetObjectRequest");
        GetObjectMetadataRequest getObjectMetadataRequest = RequestCopyUtils.createGetObjectMetadataRequestFrom(getObjectRequest).withPartNumber(partNumber);
        return s3.getObjectMetadata(getObjectMetadataRequest).getContentLength();
    }

    public static long getLastByteInPart(AmazonS3 s3, GetObjectRequest getObjectRequest, Integer partNumber) {
        ValidationUtils.assertNotNull(s3, "S3 client");
        ValidationUtils.assertNotNull(getObjectRequest, "GetObjectRequest");
        ValidationUtils.assertNotNull(partNumber, "partNumber");
        GetObjectMetadataRequest getObjectMetadataRequest = RequestCopyUtils.createGetObjectMetadataRequestFrom(getObjectRequest).withPartNumber(partNumber);
        ObjectMetadata metadata = s3.getObjectMetadata(getObjectMetadataRequest);
        return metadata.getContentRange()[1];
    }

    public static interface RetryableS3DownloadTask {
        public S3Object getS3ObjectStream();

        public boolean needIntegrityCheck();
    }
}

