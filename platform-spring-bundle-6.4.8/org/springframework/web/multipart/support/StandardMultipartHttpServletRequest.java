/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.mail.internet.MimeUtility
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.Part
 */
package org.springframework.web.multipart.support;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.mail.internet.MimeUtility;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;

public class StandardMultipartHttpServletRequest
extends AbstractMultipartHttpServletRequest {
    @Nullable
    private Set<String> multipartParameterNames;

    public StandardMultipartHttpServletRequest(HttpServletRequest request) throws MultipartException {
        this(request, false);
    }

    public StandardMultipartHttpServletRequest(HttpServletRequest request, boolean lazyParsing) throws MultipartException {
        super(request);
        if (!lazyParsing) {
            this.parseRequest(request);
        }
    }

    private void parseRequest(HttpServletRequest request) {
        try {
            Collection parts = request.getParts();
            this.multipartParameterNames = new LinkedHashSet<String>(parts.size());
            LinkedMultiValueMap<String, MultipartFile> files = new LinkedMultiValueMap<String, MultipartFile>(parts.size());
            for (Part part : parts) {
                String headerValue = part.getHeader("Content-Disposition");
                ContentDisposition disposition = ContentDisposition.parse(headerValue);
                String filename = disposition.getFilename();
                if (filename != null) {
                    if (filename.startsWith("=?") && filename.endsWith("?=")) {
                        filename = MimeDelegate.decode(filename);
                    }
                    files.add(part.getName(), new StandardMultipartFile(part, filename));
                    continue;
                }
                this.multipartParameterNames.add(part.getName());
            }
            this.setMultipartFiles(files);
        }
        catch (Throwable ex) {
            this.handleParseFailure(ex);
        }
    }

    protected void handleParseFailure(Throwable ex) {
        String msg = ex.getMessage();
        if (msg != null && (msg = msg.toLowerCase()).contains("size") && msg.contains("exceed")) {
            throw new MaxUploadSizeExceededException(-1L, ex);
        }
        throw new MultipartException("Failed to parse multipart servlet request", ex);
    }

    @Override
    protected void initializeMultipart() {
        this.parseRequest(this.getRequest());
    }

    public Enumeration<String> getParameterNames() {
        if (this.multipartParameterNames == null) {
            this.initializeMultipart();
        }
        if (this.multipartParameterNames.isEmpty()) {
            return super.getParameterNames();
        }
        LinkedHashSet paramNames = new LinkedHashSet();
        Enumeration paramEnum = super.getParameterNames();
        while (paramEnum.hasMoreElements()) {
            paramNames.add(paramEnum.nextElement());
        }
        paramNames.addAll(this.multipartParameterNames);
        return Collections.enumeration(paramNames);
    }

    public Map<String, String[]> getParameterMap() {
        if (this.multipartParameterNames == null) {
            this.initializeMultipart();
        }
        if (this.multipartParameterNames.isEmpty()) {
            return super.getParameterMap();
        }
        LinkedHashMap<String, String[]> paramMap = new LinkedHashMap<String, String[]>(super.getParameterMap());
        for (String paramName : this.multipartParameterNames) {
            if (paramMap.containsKey(paramName)) continue;
            paramMap.put(paramName, this.getParameterValues(paramName));
        }
        return paramMap;
    }

    @Override
    public String getMultipartContentType(String paramOrFileName) {
        try {
            Part part = this.getPart(paramOrFileName);
            return part != null ? part.getContentType() : null;
        }
        catch (Throwable ex) {
            throw new MultipartException("Could not access multipart servlet request", ex);
        }
    }

    @Override
    public HttpHeaders getMultipartHeaders(String paramOrFileName) {
        try {
            Part part = this.getPart(paramOrFileName);
            if (part != null) {
                HttpHeaders headers = new HttpHeaders();
                for (String headerName : part.getHeaderNames()) {
                    headers.put(headerName, (List<String>)new ArrayList<String>(part.getHeaders(headerName)));
                }
                return headers;
            }
            return null;
        }
        catch (Throwable ex) {
            throw new MultipartException("Could not access multipart servlet request", ex);
        }
    }

    private static class MimeDelegate {
        private MimeDelegate() {
        }

        public static String decode(String value) {
            try {
                return MimeUtility.decodeText((String)value);
            }
            catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    private static class StandardMultipartFile
    implements MultipartFile,
    Serializable {
        private final Part part;
        private final String filename;

        public StandardMultipartFile(Part part, String filename) {
            this.part = part;
            this.filename = filename;
        }

        @Override
        public String getName() {
            return this.part.getName();
        }

        @Override
        public String getOriginalFilename() {
            return this.filename;
        }

        @Override
        public String getContentType() {
            return this.part.getContentType();
        }

        @Override
        public boolean isEmpty() {
            return this.part.getSize() == 0L;
        }

        @Override
        public long getSize() {
            return this.part.getSize();
        }

        @Override
        public byte[] getBytes() throws IOException {
            return FileCopyUtils.copyToByteArray(this.part.getInputStream());
        }

        @Override
        public InputStream getInputStream() throws IOException {
            return this.part.getInputStream();
        }

        @Override
        public void transferTo(File dest) throws IOException, IllegalStateException {
            this.part.write(dest.getPath());
            if (dest.isAbsolute() && !dest.exists()) {
                FileCopyUtils.copy(this.part.getInputStream(), Files.newOutputStream(dest.toPath(), new OpenOption[0]));
            }
        }

        @Override
        public void transferTo(Path dest) throws IOException, IllegalStateException {
            FileCopyUtils.copy(this.part.getInputStream(), Files.newOutputStream(dest, new OpenOption[0]));
        }
    }
}

