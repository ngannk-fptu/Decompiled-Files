/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.fileupload.FileItem
 *  org.apache.commons.fileupload.FileItemFactory
 *  org.apache.commons.fileupload.FileUploadException
 *  org.apache.commons.fileupload.disk.DiskFileItemFactory
 *  org.apache.commons.fileupload.servlet.ServletFileUpload
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.server.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class HttpMultipartPost {
    private static final Logger log = LoggerFactory.getLogger(HttpMultipartPost.class);
    private final Map<String, List<FileItem>> nameToItems = new LinkedHashMap<String, List<FileItem>>();
    private final Set<String> fileParamNames = new HashSet<String>();
    private boolean initialized;

    HttpMultipartPost(HttpServletRequest request, File tmpDir) throws IOException {
        this.extractMultipart(request, tmpDir);
        this.initialized = true;
    }

    private static FileItemFactory getFileItemFactory(File tmpDir) {
        DiskFileItemFactory fiFactory = new DiskFileItemFactory(10240, tmpDir);
        return fiFactory;
    }

    private void extractMultipart(HttpServletRequest request, File tmpDir) throws IOException {
        if (!ServletFileUpload.isMultipartContent((HttpServletRequest)request)) {
            log.debug("Request does not contain multipart content -> ignoring.");
            return;
        }
        ServletFileUpload upload = new ServletFileUpload(HttpMultipartPost.getFileItemFactory(tmpDir));
        if (request.getCharacterEncoding() == null) {
            upload.setHeaderEncoding("UTF-8");
        }
        try {
            List fileItems = upload.parseRequest(request);
            for (FileItem fileItem : fileItems) {
                this.addItem(fileItem);
            }
        }
        catch (FileUploadException e) {
            log.error("Error while processing multipart.", (Throwable)e);
            throw new IOException(e.toString());
        }
    }

    private void addItem(FileItem item) {
        String name = item.getFieldName();
        List<FileItem> l = this.nameToItems.get(item.getFieldName());
        if (l == null) {
            l = new ArrayList<FileItem>();
            this.nameToItems.put(name, l);
        }
        l.add(item);
        if (!item.isFormField()) {
            this.fileParamNames.add(name);
        }
    }

    private void checkInitialized() {
        if (!this.initialized) {
            throw new IllegalStateException("HttpMultipartPost not initialized (or already disposed).");
        }
    }

    synchronized void dispose() {
        this.checkInitialized();
        for (List<FileItem> fileItems : this.nameToItems.values()) {
            for (FileItem fileItem : fileItems) {
                fileItem.delete();
            }
        }
        this.nameToItems.clear();
        this.fileParamNames.clear();
        this.initialized = false;
    }

    Set<String> getParameterNames() {
        this.checkInitialized();
        return this.nameToItems.keySet();
    }

    String[] getParameterTypes(String name) {
        this.checkInitialized();
        String[] cts = null;
        List<FileItem> l = this.nameToItems.get(name);
        if (l != null && !l.isEmpty()) {
            cts = new String[l.size()];
            for (int i = 0; i < cts.length; ++i) {
                cts[i] = l.get(i).getContentType();
            }
        }
        return cts;
    }

    String getParameter(String name) {
        this.checkInitialized();
        List<FileItem> l = this.nameToItems.get(name);
        if (l == null || l.isEmpty()) {
            return null;
        }
        FileItem item = l.get(0);
        if (item.isFormField()) {
            return item.getString();
        }
        return item.getName();
    }

    String[] getParameterValues(String name) {
        this.checkInitialized();
        List<FileItem> l = this.nameToItems.get(name);
        if (l == null || l.isEmpty()) {
            return null;
        }
        String[] values = new String[l.size()];
        for (int i = 0; i < values.length; ++i) {
            FileItem item = l.get(i);
            values[i] = item.isFormField() ? item.getString() : item.getName();
        }
        return values;
    }

    Set<String> getFileParameterNames() {
        this.checkInitialized();
        return this.fileParamNames;
    }

    InputStream[] getFileParameterValues(String name) throws IOException {
        List<FileItem> l;
        this.checkInitialized();
        InputStream[] values = null;
        if (this.fileParamNames.contains(name) && (l = this.nameToItems.get(name)) != null && !l.isEmpty()) {
            ArrayList<InputStream> ins = new ArrayList<InputStream>(l.size());
            for (FileItem item : l) {
                if (item.isFormField()) continue;
                ins.add(item.getInputStream());
            }
            values = ins.toArray(new InputStream[ins.size()]);
        }
        return values;
    }
}

