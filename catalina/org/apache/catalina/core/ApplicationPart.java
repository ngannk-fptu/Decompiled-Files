/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.Part
 *  org.apache.tomcat.util.http.fileupload.FileItem
 *  org.apache.tomcat.util.http.fileupload.ParameterParser
 *  org.apache.tomcat.util.http.fileupload.disk.DiskFileItem
 *  org.apache.tomcat.util.http.parser.HttpParser
 */
package org.apache.catalina.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import javax.servlet.http.Part;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.ParameterParser;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.apache.tomcat.util.http.parser.HttpParser;

public class ApplicationPart
implements Part {
    private final FileItem fileItem;
    private final File location;

    public ApplicationPart(FileItem fileItem, File location) {
        this.fileItem = fileItem;
        this.location = location;
    }

    public void delete() throws IOException {
        this.fileItem.delete();
    }

    public String getContentType() {
        return this.fileItem.getContentType();
    }

    public String getHeader(String name) {
        if (this.fileItem instanceof DiskFileItem) {
            return this.fileItem.getHeaders().getHeader(name);
        }
        return null;
    }

    public Collection<String> getHeaderNames() {
        if (this.fileItem instanceof DiskFileItem) {
            LinkedHashSet<String> headerNames = new LinkedHashSet<String>();
            Iterator iter = this.fileItem.getHeaders().getHeaderNames();
            while (iter.hasNext()) {
                headerNames.add((String)iter.next());
            }
            return headerNames;
        }
        return Collections.emptyList();
    }

    public Collection<String> getHeaders(String name) {
        if (this.fileItem instanceof DiskFileItem) {
            LinkedHashSet<String> headers = new LinkedHashSet<String>();
            Iterator iter = this.fileItem.getHeaders().getHeaders(name);
            while (iter.hasNext()) {
                headers.add((String)iter.next());
            }
            return headers;
        }
        return Collections.emptyList();
    }

    public InputStream getInputStream() throws IOException {
        return this.fileItem.getInputStream();
    }

    public String getName() {
        return this.fileItem.getFieldName();
    }

    public long getSize() {
        return this.fileItem.getSize();
    }

    public void write(String fileName) throws IOException {
        File file = new File(fileName);
        if (!file.isAbsolute()) {
            file = new File(this.location, fileName);
        }
        try {
            this.fileItem.write(file);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

    public String getString(String encoding) throws UnsupportedEncodingException, IOException {
        return this.fileItem.getString(encoding);
    }

    public String getSubmittedFileName() {
        String cdl;
        String fileName = null;
        String cd = this.getHeader("Content-Disposition");
        if (cd != null && ((cdl = cd.toLowerCase(Locale.ENGLISH)).startsWith("form-data") || cdl.startsWith("attachment"))) {
            ParameterParser paramParser = new ParameterParser();
            paramParser.setLowerCaseNames(true);
            Map params = paramParser.parse(cd, ';');
            if (params.containsKey("filename")) {
                fileName = (String)params.get("filename");
                fileName = fileName != null ? (fileName.indexOf(92) > -1 ? HttpParser.unquote((String)fileName.trim()) : fileName.trim()) : "";
            }
        }
        return fileName;
    }
}

