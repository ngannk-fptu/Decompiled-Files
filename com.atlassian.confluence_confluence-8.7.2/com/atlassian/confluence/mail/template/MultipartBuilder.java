/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.io.ByteStreams
 *  com.google.common.io.Files
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 *  javax.activation.FileDataSource
 *  javax.mail.BodyPart
 *  javax.mail.MessagingException
 *  javax.mail.Multipart
 *  javax.mail.internet.MimeBodyPart
 *  javax.mail.internet.MimeMultipart
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.FileSystemResource
 *  org.springframework.core.io.Resource
 */
package com.atlassian.confluence.mail.template;

import com.atlassian.confluence.core.DefaultMimetypesExtensionTranslationMapFactory;
import com.atlassian.confluence.core.MimetypesExtensionTranslationMapFactory;
import com.atlassian.confluence.jmx.CurrentTimeFacade;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class MultipartBuilder {
    private static final Logger log = LoggerFactory.getLogger(MultipartBuilder.class);
    public static final MultipartBuilder INSTANCE = new MultipartBuilder(new DefaultMimetypesExtensionTranslationMapFactory());
    @VisibleForTesting
    static final String CONTENT_ID = "Content-ID";
    @VisibleForTesting
    static final String CONTENT_DISPOSITION = "Content-Disposition";
    private final Map<String, String> extMap;
    private static final Comparator<Path> MOST_RECENTLY_MODIFIED_FIRST = (file1, file2) -> {
        try {
            return Files.getLastModifiedTime(file2, new LinkOption[0]).compareTo(Files.getLastModifiedTime(file1, new LinkOption[0]));
        }
        catch (IOException e) {
            log.warn("Failed to read last modified time for {} or {}: {}", new Object[]{file2, file1, e.getMessage()});
            return 0;
        }
    };

    private MultipartBuilder(MimetypesExtensionTranslationMapFactory extMapFactory) {
        this.extMap = extMapFactory.getMimetypeExtensionTranslationMap();
    }

    public MimeMultipart makeMultipart(File file) throws MessagingException, IOException {
        MimeBodyPart filePart = new MimeBodyPart();
        filePart.setDataHandler(new DataHandler((DataSource)new FileDataSource(file)));
        filePart.setFileName(file.getName());
        MimeMultipart multipart = this.getMultipart();
        multipart.addBodyPart((BodyPart)filePart);
        return multipart;
    }

    public MimeMultipart makeMultipart(ByteArrayInputStream bytezIn) throws MessagingException, IOException {
        MimeBodyPart filePart = new MimeBodyPart();
        filePart.setDataHandler(new DataHandler((DataSource)new ByteArrayDataSource(bytezIn, "application/octet-stream")));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        filePart.setFileName("logs-" + format.format(CurrentTimeFacade.getCurrentTime()) + ".zip");
        MimeMultipart multipart = this.getMultipart();
        multipart.addBodyPart((BodyPart)filePart);
        return multipart;
    }

    private MimeMultipart getMultipart() throws MessagingException {
        MimeMultipart multipart = new MimeMultipart();
        MimeBodyPart messagePart = new MimeBodyPart();
        messagePart.setText("Log file(s) zip :");
        multipart.addBodyPart((BodyPart)messagePart);
        return multipart;
    }

    public Multipart buildMartipart(String location) throws IOException, MessagingException {
        List<Resource> list = this.getResourcesFromPath(location);
        if (list.isEmpty()) {
            throw new IOException("Unable to read logs or no logs present at location  :  " + location);
        }
        ByteArrayOutputStream myout = this.extractZippedBytes(list);
        ByteArrayInputStream bytezIn = new ByteArrayInputStream(myout.toByteArray());
        return this.makeMultipart(bytezIn);
    }

    @VisibleForTesting
    ByteArrayOutputStream extractZippedBytes(List<Resource> resources) throws IOException {
        ByteArrayOutputStream myout = new ByteArrayOutputStream();
        ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(myout));
        for (Resource resource : resources) {
            String filename = resource.getFilename();
            if (filename == null) continue;
            ZipEntry entry = new ZipEntry(filename);
            out.putNextEntry(entry);
            ByteStreams.copy((InputStream)resource.getInputStream(), (OutputStream)out);
        }
        out.close();
        return myout;
    }

    public List<Resource> getResourcesFromPath(String location) {
        try {
            Path dir = Paths.get(location, new String[0]);
            return MultipartBuilder.findMostRecentLogFilesInDir(dir, 2).collect(Collectors.toList());
        }
        catch (IOException e) {
            log.error("Failed to read log resources from {}: {}", (Object)location, (Object)e.getMessage());
            return Collections.emptyList();
        }
    }

    private static Stream<FileSystemResource> findMostRecentLogFilesInDir(Path dir, int max) throws IOException {
        return Files.list(dir).filter(file -> file.endsWith("log")).sorted(MOST_RECENTLY_MODIFIED_FIRST).limit(max).map(Path::toFile).map(FileSystemResource::new);
    }

    public Multipart makeMultipart(Collection<DataSource> dataSources) {
        if (dataSources == null || dataSources.isEmpty()) {
            return null;
        }
        MimeMultipart multipart = new MimeMultipart("related");
        for (DataSource dataSource : dataSources) {
            try {
                MimeBodyPart mimeBodyPart = this.createMimeBodyPart(dataSource.getName(), new DataHandler(dataSource));
                multipart.addBodyPart((BodyPart)mimeBodyPart);
            }
            catch (MessagingException e) {
                throw new RuntimeException(e);
            }
        }
        return multipart;
    }

    private MimeBodyPart createMimeBodyPart(String contentId, DataHandler dataHandler) throws MessagingException {
        String contentType = dataHandler.getContentType();
        String filename = this.addExtension(contentId, this.extMap.get(contentType));
        return this.createMimeBodyPart(contentId, dataHandler, filename);
    }

    private MimeBodyPart createMimeBodyPart(String contentId, DataHandler dataHandler, String fileName) throws MessagingException {
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setDataHandler(dataHandler);
        mimeBodyPart.setHeader(CONTENT_ID, "<" + contentId + ">");
        mimeBodyPart.setHeader(CONTENT_DISPOSITION, "inline");
        if (!StringUtils.isBlank((CharSequence)fileName)) {
            mimeBodyPart.setFileName(fileName);
        }
        return mimeBodyPart;
    }

    private String addExtension(String contentId, String ext) {
        String currentExt = com.google.common.io.Files.getFileExtension((String)contentId);
        if (StringUtils.isBlank((CharSequence)ext) || ext.equals(currentExt)) {
            return contentId;
        }
        return contentId + "." + ext;
    }

    private static class ByteArrayDataSource
    implements DataSource {
        private ByteArrayOutputStream baos = null;
        private String type = "application/octet-stream";

        public ByteArrayDataSource(InputStream aIs, String type) throws IOException {
            this.byteArrayDataSource(aIs, type);
        }

        private void byteArrayDataSource(InputStream aIs, String type) throws IOException {
            this.type = type;
            this.baos = new ByteArrayOutputStream();
            ByteStreams.copy((InputStream)aIs, (OutputStream)this.baos);
            this.baos.close();
        }

        public String getContentType() {
            return this.type == null ? "application/octet-stream" : this.type;
        }

        public InputStream getInputStream() throws IOException {
            if (this.baos == null) {
                throw new IOException("no data");
            }
            return new ByteArrayInputStream(this.baos.toByteArray());
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            this.baos = new ByteArrayOutputStream();
            return this.baos;
        }
    }
}

