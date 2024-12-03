/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.FileSize
 *  com.atlassian.spring.container.ContainerManager
 *  com.atlassian.xwork.FileUploadUtils$FileUploadException
 *  com.opensymphony.xwork2.TextProvider
 *  com.opensymphony.xwork2.TextProviderFactory
 *  org.apache.struts2.dispatcher.Dispatcher
 *  org.apache.struts2.dispatcher.LocalizedMessage
 *  org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
 */
package com.atlassian.confluence.util;

import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.core.util.FileSize;
import com.atlassian.spring.container.ContainerManager;
import com.atlassian.xwork.FileUploadUtils;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.struts2.dispatcher.Dispatcher;
import org.apache.struts2.dispatcher.LocalizedMessage;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;

public class StrutsUtil {
    private static final String STRUTS_SIZE_LIMIT_KEY = "struts.messages.upload.error.SizeLimitExceededException";
    private static final String STRUTS_FILE_SIZE_LIMIT_KEY = "struts.messages.upload.error.FileSizeLimitExceededException";
    private static final String STRUTS_FILE_COUNT_LIMIT_KEY = "struts.messages.upload.error.FileCountLimitExceededException";
    private static final String STRUTS_GENERIC_UPLOAD_KEY = "struts.messages.error.uploading";
    private static final String I18N_FILE_SIZE_LIMIT_KEY = "upload.size.limit.exceeded";
    private static final String I18N_REQ_SIZE_LIMIT_KEY = "upload.request.size.limit.exceeded";
    private static final String I18N_PART_LIMIT_KEY = "upload.part.limit.exceeded";
    private static final String I18N_UNEXPECTED_KEY = "upload.unexpected.error";

    private StrutsUtil() {
    }

    public static List<String> localizeMultipartErrorMessages(FileUploadUtils.FileUploadException e) {
        return StrutsUtil.localizeMultipartErrorMessages(e.getErrorMsgs());
    }

    public static List<String> localizeMultipartErrorMessages(MultiPartRequestWrapper multiPartRequestWrapper) {
        return StrutsUtil.localizeMultipartErrorMessages(multiPartRequestWrapper.getErrors());
    }

    private static List<String> localizeMultipartErrorMessages(Collection<LocalizedMessage> msgErrors) {
        ArrayList<String> strErrors = new ArrayList<String>();
        for (LocalizedMessage msgError : msgErrors) {
            String strError;
            Object[] i18nArgs;
            Object[] originalArgs;
            if (STRUTS_SIZE_LIMIT_KEY.equals(msgError.getTextKey())) {
                originalArgs = msgError.getArgs();
                i18nArgs = new Object[]{FileSize.format((Long)((Long)originalArgs[1])), FileSize.format((Long)((Long)originalArgs[0]))};
                strError = GeneralUtil.getI18n().getText(I18N_REQ_SIZE_LIMIT_KEY, i18nArgs);
            } else if (STRUTS_FILE_SIZE_LIMIT_KEY.equals(msgError.getTextKey())) {
                originalArgs = msgError.getArgs();
                i18nArgs = new Object[]{FileSize.format((Long)((Long)originalArgs[2])), FileSize.format((Long)((Long)originalArgs[1]))};
                strError = GeneralUtil.getI18n().getText(I18N_FILE_SIZE_LIMIT_KEY, i18nArgs);
            } else {
                strError = STRUTS_FILE_COUNT_LIMIT_KEY.equals(msgError.getTextKey()) ? GeneralUtil.getI18n().getText(I18N_PART_LIMIT_KEY, msgError.getArgs()) : (STRUTS_GENERIC_UPLOAD_KEY.equals(msgError.getTextKey()) ? GeneralUtil.getI18n().getText(I18N_UNEXPECTED_KEY, msgError.getArgs()) : (StrutsUtil.getStrutsTextProvider().hasKey(msgError.getTextKey()) ? StrutsUtil.getStrutsTextMessage(msgError.getTextKey(), Arrays.asList(msgError.getArgs())) : GeneralUtil.getI18n().getText(I18N_UNEXPECTED_KEY, Collections.singletonList(msgError.getDefaultMessage()))));
            }
            strErrors.add(HtmlUtil.htmlEncode(strError));
        }
        return strErrors;
    }

    public static String getStrutsTextMessage(String messageKey, List<?> args) {
        return StrutsUtil.getStrutsTextProvider().getText(messageKey, args);
    }

    public static TextProvider getStrutsTextProvider() {
        TextProviderFactory tpf = (TextProviderFactory)((Dispatcher)ContainerManager.getComponent((String)"strutsDispatcher")).getContainer().getInstance(TextProviderFactory.class);
        return tpf.createInstance(StrutsUtil.class);
    }
}

