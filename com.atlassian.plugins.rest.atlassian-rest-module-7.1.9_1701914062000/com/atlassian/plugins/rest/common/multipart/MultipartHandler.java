/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package com.atlassian.plugins.rest.common.multipart;

import com.atlassian.plugins.rest.common.multipart.FilePart;
import com.atlassian.plugins.rest.common.multipart.MultipartForm;
import javax.servlet.http.HttpServletRequest;

public interface MultipartHandler {
    public FilePart getFilePart(HttpServletRequest var1, String var2);

    public MultipartForm getForm(HttpServletRequest var1);
}

