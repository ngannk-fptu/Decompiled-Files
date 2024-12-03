/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.model.rest;

import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
public class GeneralResponseEntity
implements JsonSerializable {
    private static final Logger LOG = LoggerFactory.getLogger(GeneralResponseEntity.class);
    @XmlElement
    private boolean success;
    @XmlElement
    private List<FieldError> fieldErrors;

    public GeneralResponseEntity() {
        this.setSuccess(true);
    }

    public boolean isSuccess() {
        return this.success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<FieldError> getFieldErrors() {
        return this.fieldErrors;
    }

    public void setFieldErrors(List<FieldError> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObj = new JSONObject();
        try {
            thisObj.put("success", this.isSuccess());
            List<FieldError> fieldErrors = this.getFieldErrors();
            if (null != fieldErrors && !fieldErrors.isEmpty()) {
                JSONArray fieldErrArray = new JSONArray();
                for (FieldError fieldError : fieldErrors) {
                    if (null == fieldError.field) continue;
                    JSONObject fieldErrorObj = new JSONObject();
                    fieldErrorObj.put("field", (Object)fieldError.field);
                    List<String> fieldErrorMessages = fieldError.errorMessages;
                    if (null != fieldErrorMessages && !fieldErrorMessages.isEmpty()) {
                        JSONArray fieldErrorMsgArray = new JSONArray();
                        for (String fieldErrorMessage : fieldErrorMessages) {
                            if (null == fieldErrorMessage) continue;
                            fieldErrorMsgArray.put((Object)fieldErrorMessage);
                        }
                        fieldErrorObj.put("errorMessages", (Object)fieldErrorMsgArray);
                    }
                    fieldErrArray.put((Object)fieldErrorObj);
                }
                if (fieldErrArray.length() > 0) {
                    thisObj.put("fieldErrors", (Object)fieldErrArray);
                }
            }
        }
        catch (JSONException jsonE) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)jsonE);
        }
        return thisObj;
    }

    @XmlRootElement
    public static class FieldError {
        @XmlElement
        private String field;
        @XmlElement
        private List<String> errorMessages;

        public FieldError(String field, List<String> errorMessages) {
            this.field = field;
            this.errorMessages = errorMessages;
        }
    }
}

