/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.springframework.web.multipart;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;

public interface MultipartResolver {
    public boolean isMultipart(HttpServletRequest var1);

    public MultipartHttpServletRequest resolveMultipart(HttpServletRequest var1) throws MultipartException;

    public void cleanupMultipart(MultipartHttpServletRequest var1);
}

