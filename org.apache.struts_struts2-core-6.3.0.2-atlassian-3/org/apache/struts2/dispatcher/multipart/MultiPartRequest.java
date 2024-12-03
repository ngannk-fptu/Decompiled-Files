/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.struts2.dispatcher.multipart;

import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.struts2.dispatcher.LocalizedMessage;
import org.apache.struts2.dispatcher.multipart.UploadedFile;

public interface MultiPartRequest {
    public void parse(HttpServletRequest var1, String var2) throws IOException;

    public Enumeration<String> getFileParameterNames();

    public String[] getContentType(String var1);

    public UploadedFile[] getFile(String var1);

    public String[] getFileNames(String var1);

    public String[] getFilesystemName(String var1);

    public String getParameter(String var1);

    public Enumeration<String> getParameterNames();

    public String[] getParameterValues(String var1);

    public List<LocalizedMessage> getErrors();

    public void cleanUp();
}

