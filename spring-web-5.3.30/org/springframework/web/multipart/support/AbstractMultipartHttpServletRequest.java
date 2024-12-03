/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletRequestWrapper
 *  org.springframework.lang.Nullable
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 */
package org.springframework.web.multipart.support;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public abstract class AbstractMultipartHttpServletRequest
extends HttpServletRequestWrapper
implements MultipartHttpServletRequest {
    @Nullable
    private MultiValueMap<String, MultipartFile> multipartFiles;

    protected AbstractMultipartHttpServletRequest(HttpServletRequest request) {
        super(request);
    }

    public HttpServletRequest getRequest() {
        return (HttpServletRequest)super.getRequest();
    }

    @Override
    public HttpMethod getRequestMethod() {
        return HttpMethod.resolve(this.getRequest().getMethod());
    }

    @Override
    public HttpHeaders getRequestHeaders() {
        HttpHeaders headers = new HttpHeaders();
        Enumeration headerNames = this.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = (String)headerNames.nextElement();
            headers.put(headerName, Collections.list(this.getHeaders(headerName)));
        }
        return headers;
    }

    @Override
    public Iterator<String> getFileNames() {
        return this.getMultipartFiles().keySet().iterator();
    }

    @Override
    public MultipartFile getFile(String name) {
        return (MultipartFile)this.getMultipartFiles().getFirst((Object)name);
    }

    @Override
    public List<MultipartFile> getFiles(String name) {
        List multipartFiles = (List)this.getMultipartFiles().get((Object)name);
        if (multipartFiles != null) {
            return multipartFiles;
        }
        return Collections.emptyList();
    }

    @Override
    public Map<String, MultipartFile> getFileMap() {
        return this.getMultipartFiles().toSingleValueMap();
    }

    @Override
    public MultiValueMap<String, MultipartFile> getMultiFileMap() {
        return this.getMultipartFiles();
    }

    public boolean isResolved() {
        return this.multipartFiles != null;
    }

    protected final void setMultipartFiles(MultiValueMap<String, MultipartFile> multipartFiles) {
        this.multipartFiles = new LinkedMultiValueMap(Collections.unmodifiableMap(multipartFiles));
    }

    protected MultiValueMap<String, MultipartFile> getMultipartFiles() {
        if (this.multipartFiles == null) {
            this.initializeMultipart();
        }
        return this.multipartFiles;
    }

    protected void initializeMultipart() {
        throw new IllegalStateException("Multipart request not initialized");
    }
}

