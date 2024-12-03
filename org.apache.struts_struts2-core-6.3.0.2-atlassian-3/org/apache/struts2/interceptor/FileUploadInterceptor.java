/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionProxy;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.Parameter;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.apache.struts2.interceptor.AbstractFileUploadInterceptor;

@Deprecated
public class FileUploadInterceptor
extends AbstractFileUploadInterceptor {
    private static final long serialVersionUID = -4764627478894962478L;
    protected static final Logger LOG = LogManager.getLogger(FileUploadInterceptor.class);

    @Override
    public String intercept(ActionInvocation invocation) throws Exception {
        ActionContext ac = invocation.getInvocationContext();
        HttpServletRequest request = ac.getServletRequest();
        if (!(request instanceof MultiPartRequestWrapper)) {
            if (LOG.isDebugEnabled()) {
                ActionProxy proxy = invocation.getProxy();
                LOG.debug(this.getTextMessage("struts.messages.bypass.request", new String[]{proxy.getNamespace(), proxy.getActionName()}));
            }
            return invocation.invoke();
        }
        Object action = invocation.getAction();
        MultiPartRequestWrapper multiWrapper = (MultiPartRequestWrapper)request;
        this.applyValidation(action, multiWrapper);
        Enumeration<String> fileParameterNames = multiWrapper.getFileParameterNames();
        while (fileParameterNames != null && fileParameterNames.hasMoreElements()) {
            String inputName = fileParameterNames.nextElement();
            Object[] contentType = multiWrapper.getContentTypes(inputName);
            if (this.isNonEmpty(contentType)) {
                Object[] fileName = multiWrapper.getFileNames(inputName);
                if (this.isNonEmpty(fileName)) {
                    UploadedFile[] files = multiWrapper.getFiles(inputName);
                    if (files == null || files.length <= 0) continue;
                    ArrayList<UploadedFile> acceptedFiles = new ArrayList<UploadedFile>(files.length);
                    ArrayList<Object> acceptedContentTypes = new ArrayList<Object>(files.length);
                    ArrayList<Object> acceptedFileNames = new ArrayList<Object>(files.length);
                    String contentTypeName = inputName + "ContentType";
                    String fileNameName = inputName + "FileName";
                    for (int index = 0; index < files.length; ++index) {
                        if (!this.acceptFile(action, files[index], (String)fileName[index], (String)contentType[index], inputName)) continue;
                        acceptedFiles.add(files[index]);
                        acceptedContentTypes.add(contentType[index]);
                        acceptedFileNames.add(fileName[index]);
                    }
                    if (acceptedFiles.isEmpty()) continue;
                    HashMap<String, Parameter> newParams = new HashMap<String, Parameter>();
                    newParams.put(inputName, new Parameter.File(inputName, acceptedFiles.toArray(new UploadedFile[0])));
                    newParams.put(contentTypeName, new Parameter.File(contentTypeName, acceptedContentTypes.toArray(new String[0])));
                    newParams.put(fileNameName, new Parameter.File(fileNameName, acceptedFileNames.toArray(new String[0])));
                    ac.getParameters().appendAll(newParams);
                    continue;
                }
                if (!LOG.isWarnEnabled()) continue;
                LOG.warn(this.getTextMessage(action, "struts.messages.invalid.file", new String[]{inputName}));
                continue;
            }
            if (!LOG.isWarnEnabled()) continue;
            LOG.warn(this.getTextMessage(action, "struts.messages.invalid.content.type", new String[]{inputName}));
        }
        return invocation.invoke();
    }
}

