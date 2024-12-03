/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.octo.captcha.service.CaptchaServiceException
 */
package com.octo.captcha.module.config;

import com.octo.captcha.module.CaptchaModuleException;
import com.octo.captcha.service.CaptchaServiceException;
import java.util.ResourceBundle;

public class CaptchaModuleConfig {
    private static CaptchaModuleConfig instance = new CaptchaModuleConfig();
    public static final String MESSAGE_TYPE_BUNDLE = "bundle";
    public static final String ID_GENERATED = "generated";
    public static final String MESSAGE_TYPE_TEXT = "text";
    public static final String ID_SESSION = "session";
    public static final String JMX_REGISTERING_NAME = "com.octo.captcha.module.struts:object=CaptchaServicePlugin";
    private Boolean registerToMbean = Boolean.FALSE;
    private String responseKey = "jcaptcha_response";
    private String serviceClass = "com.octo.captcha.service.image.DefaultManageableImageCaptchaService";
    private String messageType = "text";
    private String messageValue = "You failed the jcaptcha test";
    private String messageKey = "jcaptcha_fail";
    private String idType = "session";
    private String idKey = "jcaptcha_id";

    public static CaptchaModuleConfig getInstance() {
        return instance;
    }

    private CaptchaModuleConfig() {
    }

    public String getIdKey() {
        return this.idKey;
    }

    public void setIdKey(String idKey) {
        this.idKey = idKey;
    }

    public String getMessageType() {
        return this.messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getMessageValue() {
        return this.messageValue;
    }

    public void setMessageValue(String messageValue) {
        this.messageValue = messageValue;
    }

    public String getMessageKey() {
        return this.messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    public String getIdType() {
        return this.idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getServiceClass() {
        return this.serviceClass;
    }

    public void setServiceClass(String serviceClass) {
        this.serviceClass = serviceClass;
    }

    public String getResponseKey() {
        return this.responseKey;
    }

    public void setResponseKey(String responseKey) {
        this.responseKey = responseKey;
    }

    public Boolean getRegisterToMbean() {
        return this.registerToMbean;
    }

    public void setRegisterToMbean(Boolean registerToMbean) {
        this.registerToMbean = registerToMbean;
    }

    public void validate() {
        if (!MESSAGE_TYPE_TEXT.equals(this.messageType) && !MESSAGE_TYPE_BUNDLE.equals(this.messageType)) {
            throw new CaptchaServiceException("messageType can only be set to 'text' or 'bundle'");
        }
        if (!ID_SESSION.equals(this.idType) && !ID_GENERATED.equals(this.idType)) {
            throw new CaptchaServiceException("idType can only be set to 'session' or 'generated'");
        }
        if (this.messageValue == null) {
            throw new CaptchaModuleException("messageValue cannot be null");
        }
        if (this.messageKey == null || "".equals(this.messageKey)) {
            throw new CaptchaModuleException("messageKey cannot be null or empty");
        }
        if (this.responseKey == null || "".equals(this.responseKey)) {
            throw new CaptchaModuleException("responseKey cannot be null or empty");
        }
        if (this.idType.equals(ID_GENERATED) && (this.idKey == null || "".equals(this.idKey))) {
            throw new CaptchaServiceException("idKey cannot be null or empty when id is generated (ie idType='generated'");
        }
        if (this.messageType.equals(MESSAGE_TYPE_BUNDLE)) {
            ResourceBundle bundle = ResourceBundle.getBundle(this.getMessageValue());
            if (bundle == null) {
                throw new CaptchaModuleException("can't initialize module config with a unfound bundle : resource bundle " + this.getMessageValue() + " has  not been found");
            }
            if (bundle.getString(this.getMessageKey()) == null) {
                throw new CaptchaModuleException("can't initialize module config with a unfound message : resource bundle " + this.getMessageValue() + " has  no key named :" + this.getMessageKey());
            }
        }
        try {
            Class.forName(this.serviceClass).newInstance();
        }
        catch (Throwable e) {
            e.printStackTrace();
            throw new CaptchaModuleException("Error during Service Class initialization", e);
        }
    }
}

