/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.interceptor;

import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.LocaleProviderFactory;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.TextProviderFactory;
import com.opensymphony.xwork2.inject.Container;
import com.opensymphony.xwork2.inject.Inject;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.opensymphony.xwork2.interceptor.ValidationAware;
import com.opensymphony.xwork2.util.TextParseUtil;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.LocalizedMessage;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.apache.struts2.dispatcher.multipart.UploadedFile;
import org.apache.struts2.util.ContentTypeMatcher;

public abstract class AbstractFileUploadInterceptor
extends AbstractInterceptor {
    private static final Logger LOG = LogManager.getLogger(AbstractFileUploadInterceptor.class);
    public static final String STRUTS_MESSAGES_BYPASS_REQUEST_KEY = "struts.messages.bypass.request";
    public static final String STRUTS_MESSAGES_ERROR_UPLOADING_KEY = "struts.messages.error.uploading";
    public static final String STRUTS_MESSAGES_ERROR_FILE_TOO_LARGE_KEY = "struts.messages.error.file.too.large";
    public static final String STRUTS_MESSAGES_INVALID_FILE_KEY = "struts.messages.invalid.file";
    public static final String STRUTS_MESSAGES_INVALID_CONTENT_TYPE_KEY = "struts.messages.invalid.content.type";
    public static final String STRUTS_MESSAGES_ERROR_CONTENT_TYPE_NOT_ALLOWED_KEY = "struts.messages.error.content.type.not.allowed";
    public static final String STRUTS_MESSAGES_ERROR_FILE_EXTENSION_NOT_ALLOWED_KEY = "struts.messages.error.file.extension.not.allowed";
    private Long maximumSize;
    private Set<String> allowedTypesSet = Collections.emptySet();
    private Set<String> allowedExtensionsSet = Collections.emptySet();
    private ContentTypeMatcher<Object> matcher;
    private Container container;

    @Inject
    public void setMatcher(ContentTypeMatcher<Object> matcher) {
        this.matcher = matcher;
    }

    @Inject
    public void setContainer(Container container) {
        this.container = container;
    }

    public void setAllowedExtensions(String allowedExtensions) {
        this.allowedExtensionsSet = TextParseUtil.commaDelimitedStringToSet(allowedExtensions);
    }

    public void setAllowedTypes(String allowedTypes) {
        this.allowedTypesSet = TextParseUtil.commaDelimitedStringToSet(allowedTypes);
    }

    public void setMaximumSize(Long maximumSize) {
        this.maximumSize = maximumSize;
    }

    protected boolean acceptFile(Object action, UploadedFile file, String originalFilename, String contentType, String inputName) {
        String errMsg;
        HashSet<String> errorMessages = new HashSet<String>();
        ValidationAware validation = null;
        if (action instanceof ValidationAware) {
            validation = (ValidationAware)action;
        }
        if (file == null) {
            String errMsg2 = this.getTextMessage(action, STRUTS_MESSAGES_ERROR_UPLOADING_KEY, new String[]{inputName});
            if (validation != null) {
                validation.addFieldError(inputName, errMsg2);
            }
            LOG.warn(errMsg2);
            return false;
        }
        if (file.getContent() == null) {
            errMsg = this.getTextMessage(action, STRUTS_MESSAGES_ERROR_UPLOADING_KEY, new String[]{originalFilename});
            errorMessages.add(errMsg);
            LOG.warn(errMsg);
        }
        if (this.maximumSize != null && this.maximumSize < file.length()) {
            errMsg = this.getTextMessage(action, STRUTS_MESSAGES_ERROR_FILE_TOO_LARGE_KEY, new String[]{inputName, originalFilename, file.getName(), "" + file.length(), this.getMaximumSizeStr(action)});
            errorMessages.add(errMsg);
            LOG.warn(errMsg);
        }
        if (!this.allowedTypesSet.isEmpty() && !this.containsItem(this.allowedTypesSet, contentType)) {
            errMsg = this.getTextMessage(action, STRUTS_MESSAGES_ERROR_CONTENT_TYPE_NOT_ALLOWED_KEY, new String[]{inputName, originalFilename, file.getName(), contentType});
            errorMessages.add(errMsg);
            LOG.warn(errMsg);
        }
        if (!this.allowedExtensionsSet.isEmpty() && !this.hasAllowedExtension(this.allowedExtensionsSet, originalFilename)) {
            errMsg = this.getTextMessage(action, STRUTS_MESSAGES_ERROR_FILE_EXTENSION_NOT_ALLOWED_KEY, new String[]{inputName, originalFilename, file.getName(), contentType});
            errorMessages.add(errMsg);
            LOG.warn(errMsg);
        }
        if (validation != null) {
            for (String errorMsg : errorMessages) {
                validation.addFieldError(inputName, errorMsg);
            }
        }
        return errorMessages.isEmpty();
    }

    private String getMaximumSizeStr(Object action) {
        return NumberFormat.getNumberInstance(this.getLocaleProvider(action).getLocale()).format(this.maximumSize);
    }

    private boolean hasAllowedExtension(Collection<String> extensionCollection, String filename) {
        if (filename == null) {
            return false;
        }
        String lowercaseFilename = filename.toLowerCase();
        for (String extension : extensionCollection) {
            if (!lowercaseFilename.endsWith(extension)) continue;
            return true;
        }
        return false;
    }

    private boolean containsItem(Collection<String> itemCollection, String item) {
        for (String pattern : itemCollection) {
            if (!this.matchesWildcard(pattern, item)) continue;
            return true;
        }
        return false;
    }

    private boolean matchesWildcard(String pattern, String text) {
        Object o = this.matcher.compilePattern(pattern);
        return this.matcher.match(new HashMap<String, String>(), text, o);
    }

    protected boolean isNonEmpty(Object[] objArray) {
        boolean result = false;
        for (Object o : objArray) {
            if (o == null) continue;
            result = true;
            break;
        }
        return result;
    }

    protected String getTextMessage(String messageKey, String[] args) {
        return this.getTextMessage(this, messageKey, args);
    }

    protected String getTextMessage(Object action, String messageKey, String[] args) {
        if (action instanceof TextProvider) {
            return ((TextProvider)action).getText(messageKey, args);
        }
        return this.getTextProvider(action).getText(messageKey, args);
    }

    protected TextProvider getTextProvider(Object action) {
        TextProviderFactory tpf = this.container.getInstance(TextProviderFactory.class);
        return tpf.createInstance(action.getClass());
    }

    private LocaleProvider getLocaleProvider(Object action) {
        LocaleProvider localeProvider;
        if (action instanceof LocaleProvider) {
            localeProvider = (LocaleProvider)action;
        } else {
            LocaleProviderFactory localeProviderFactory = this.container.getInstance(LocaleProviderFactory.class);
            localeProvider = localeProviderFactory.createLocaleProvider();
        }
        return localeProvider;
    }

    protected void applyValidation(Object action, MultiPartRequestWrapper multiWrapper) {
        ValidationAware validation = null;
        if (action instanceof ValidationAware) {
            validation = (ValidationAware)action;
        }
        if (multiWrapper.hasErrors() && validation != null) {
            TextProvider textProvider = this.getTextProvider(action);
            for (LocalizedMessage error : multiWrapper.getErrors()) {
                String errorMessage = textProvider.hasKey(error.getTextKey()) ? textProvider.getText(error.getTextKey(), Arrays.asList(error.getArgs())) : textProvider.getText(STRUTS_MESSAGES_ERROR_UPLOADING_KEY, error.getDefaultMessage());
                validation.addActionError(errorMessage);
            }
        }
    }
}

