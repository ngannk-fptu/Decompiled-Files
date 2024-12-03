/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.dispatcher.multipart;

import com.opensymphony.xwork2.LocaleProvider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.LocalizedMessage;
import org.apache.struts2.dispatcher.StrutsRequestWrapper;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;
import org.apache.struts2.dispatcher.multipart.UploadedFile;

public class MultiPartRequestWrapper
extends StrutsRequestWrapper {
    protected static final Logger LOG = LogManager.getLogger(MultiPartRequestWrapper.class);
    private Collection<LocalizedMessage> errors;
    private MultiPartRequest multi;
    private Locale defaultLocale = Locale.ENGLISH;

    public MultiPartRequestWrapper(MultiPartRequest multiPartRequest, HttpServletRequest request, String saveDir, LocaleProvider provider, boolean disableRequestAttributeValueStackLookup) {
        super(request, disableRequestAttributeValueStackLookup);
        this.errors = new ArrayList<LocalizedMessage>();
        this.multi = multiPartRequest;
        this.defaultLocale = provider.getLocale();
        this.setLocale(request);
        try {
            this.multi.parse(request, saveDir);
            for (LocalizedMessage error : this.multi.getErrors()) {
                this.addError(error);
            }
        }
        catch (IOException e) {
            LOG.warn(e.getMessage(), (Throwable)e);
            this.addError(this.buildErrorMessage(e, new Object[]{e.getMessage()}));
        }
    }

    public MultiPartRequestWrapper(MultiPartRequest multiPartRequest, HttpServletRequest request, String saveDir, LocaleProvider provider) {
        this(multiPartRequest, request, saveDir, provider, false);
    }

    protected void setLocale(HttpServletRequest request) {
        if (this.defaultLocale == null) {
            this.defaultLocale = request.getLocale();
        }
    }

    protected LocalizedMessage buildErrorMessage(Throwable e, Object[] args) {
        String errorKey = "struts.messages.upload.error." + e.getClass().getSimpleName();
        LOG.debug("Preparing error message for key: [{}]", (Object)errorKey);
        return new LocalizedMessage(((Object)((Object)this)).getClass(), errorKey, e.getMessage(), args);
    }

    public Enumeration<String> getFileParameterNames() {
        if (this.multi == null) {
            return null;
        }
        return this.multi.getFileParameterNames();
    }

    public String[] getContentTypes(String name) {
        if (this.multi == null) {
            return null;
        }
        return this.multi.getContentType(name);
    }

    public UploadedFile[] getFiles(String fieldName) {
        if (this.multi == null) {
            return null;
        }
        return this.multi.getFile(fieldName);
    }

    public String[] getFileNames(String fieldName) {
        if (this.multi == null) {
            return null;
        }
        return this.multi.getFileNames(fieldName);
    }

    public String[] getFileSystemNames(String fieldName) {
        if (this.multi == null) {
            return null;
        }
        return this.multi.getFilesystemName(fieldName);
    }

    public String getParameter(String name) {
        return this.multi == null || this.multi.getParameter(name) == null ? super.getParameter(name) : this.multi.getParameter(name);
    }

    public Map getParameterMap() {
        HashMap<String, String[]> map = new HashMap<String, String[]>();
        Enumeration enumeration = this.getParameterNames();
        while (enumeration.hasMoreElements()) {
            String name = (String)enumeration.nextElement();
            map.put(name, this.getParameterValues(name));
        }
        return map;
    }

    public Enumeration getParameterNames() {
        if (this.multi == null) {
            return super.getParameterNames();
        }
        return this.mergeParams(this.multi.getParameterNames(), super.getParameterNames());
    }

    public String[] getParameterValues(String name) {
        return this.multi == null || this.multi.getParameterValues(name) == null ? super.getParameterValues(name) : this.multi.getParameterValues(name);
    }

    public boolean hasErrors() {
        return !this.errors.isEmpty();
    }

    public Collection<LocalizedMessage> getErrors() {
        return this.errors;
    }

    protected void addError(LocalizedMessage anErrorMessage) {
        if (!this.errors.contains(anErrorMessage)) {
            this.errors.add(anErrorMessage);
        }
    }

    protected Enumeration mergeParams(Enumeration params1, Enumeration params2) {
        Vector temp = new Vector();
        while (params1.hasMoreElements()) {
            temp.add(params1.nextElement());
        }
        while (params2.hasMoreElements()) {
            temp.add(params2.nextElement());
        }
        return temp.elements();
    }

    public void cleanUp() {
        if (this.multi != null) {
            this.multi.cleanUp();
        }
    }
}

