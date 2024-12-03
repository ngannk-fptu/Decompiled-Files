/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 */
package org.apache.jackrabbit.server.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import javax.servlet.http.HttpServletRequest;
import org.apache.jackrabbit.server.util.HttpMultipartPost;

public class RequestData {
    private final HttpServletRequest request;
    private final HttpMultipartPost mpReq;

    public RequestData(HttpServletRequest request, File tmpDir) throws IOException {
        this.request = request;
        this.mpReq = new HttpMultipartPost(request, tmpDir);
    }

    public void dispose() {
        this.mpReq.dispose();
    }

    public Iterator<String> getParameterNames() {
        HashSet names = new HashSet(this.request.getParameterMap().keySet());
        names.addAll(this.mpReq.getParameterNames());
        return names.iterator();
    }

    public String getParameter(String name) {
        String ret = this.mpReq.getParameter(name);
        return ret == null ? this.request.getParameter(name) : ret;
    }

    public String[] getParameterTypes(String name) {
        String[] types = this.mpReq.getParameterTypes(name);
        return types == null ? null : types;
    }

    public String[] getParameterValues(String name) {
        String[] ret = this.mpReq.getParameterValues(name);
        return ret == null ? this.request.getParameterValues(name) : ret;
    }

    public InputStream[] getFileParameters(String name) throws IOException {
        return this.mpReq.getFileParameterValues(name);
    }
}

