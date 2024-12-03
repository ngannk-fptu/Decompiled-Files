/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.action.UploadedFilesAware;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.apache.struts2.interceptor.AbstractFileUploadInterceptor;

public class ActionFileUploadInterceptor
extends AbstractFileUploadInterceptor {
    protected static final Logger LOG = LogManager.getLogger(ActionFileUploadInterceptor.class);

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = invocation.getInvocationContext().getServletRequest();
        if (!(request instanceof MultiPartRequestWrapper)) {
            if (LOG.isDebugEnabled()) {
                ActionProxy proxy = invocation.getProxy();
                LOG.debug(this.getTextMessage("struts.messages.bypass.request", new String[]{proxy.getNamespace(), proxy.getActionName()}));
            }
            return invocation.invoke();
        }
        MultiPartRequestWrapper multiWrapper = (MultiPartRequestWrapper)request;
        if (!(invocation.getAction() instanceof UploadedFilesAware)) {
            LOG.debug("Action: {} doesn't implement: {}, ignoring file upload", (Object)invocation.getProxy().getActionName(), (Object)UploadedFilesAware.class.getSimpleName());
            return invocation.invoke();
        }
        UploadedFilesAware action = (UploadedFilesAware)invocation.getAction();
        this.applyValidation(action, multiWrapper);
        Enumeration<String> fileParameterNames = multiWrapper.getFileParameterNames();
        ArrayList<UploadedFile> acceptedFiles = new ArrayList<UploadedFile>();
        while (fileParameterNames != null && fileParameterNames.hasMoreElements()) {
            String inputName = fileParameterNames.nextElement();
            UploadedFile[] uploadedFiles = multiWrapper.getFiles(inputName);
            if (uploadedFiles == null || uploadedFiles.length == 0) {
                if (!LOG.isWarnEnabled()) continue;
                LOG.warn(this.getTextMessage(action, "struts.messages.invalid.file", new String[]{inputName}));
                continue;
            }
            for (UploadedFile uploadedFile : uploadedFiles) {
                if (!this.acceptFile(action, uploadedFile, uploadedFile.getOriginalName(), uploadedFile.getContentType(), inputName)) continue;
                acceptedFiles.add(uploadedFile);
            }
        }
        if (acceptedFiles.isEmpty()) {
            LOG.debug("No files have been uploaded/accepted");
        } else {
            LOG.debug("Passing: {} uploaded file(s) to action", (Object)acceptedFiles.size());
            action.withUploadedFiles(acceptedFiles);
        }
        return invocation.invoke();
    }
}

