/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.json.JSONObject
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.calendar3.model;

import com.atlassian.confluence.extra.calendar3.model.JsonSerializable;
import java.util.Set;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.NONE)
public class JqlValidationResult
implements JsonSerializable {
    private static final Logger LOG = LoggerFactory.getLogger(JqlValidationResult.class);
    private Set<String> warningMessages;
    private Set<String> errorMessages;

    @XmlElement
    public Set<String> getWarningMessages() {
        return this.warningMessages;
    }

    public void setWarningMessages(Set<String> warningMessages) {
        this.warningMessages = warningMessages;
    }

    @XmlElement
    public Set<String> getErrorMessages() {
        return this.errorMessages;
    }

    public void setErrorMessages(Set<String> errorMessages) {
        this.errorMessages = errorMessages;
    }

    @XmlElement
    public boolean isValid() {
        return !(null != this.getWarningMessages() && !this.getWarningMessages().isEmpty() || null != this.getErrorMessages() && !this.getErrorMessages().isEmpty());
    }

    @Override
    public JSONObject toJson() {
        JSONObject thisObj = new JSONObject();
        try {
            Set<String> warningMessages = this.getWarningMessages();
            if (null != warningMessages && !warningMessages.isEmpty()) {
                JSONArray warningMessagesArray = new JSONArray();
                for (String warningMessage : warningMessages) {
                    warningMessagesArray.put((Object)warningMessage);
                }
                thisObj.put("warningMessages", (Object)warningMessagesArray);
            }
            Set<String> errorMessages = this.getErrorMessages();
            if (null != warningMessages && !warningMessages.isEmpty()) {
                JSONArray errorMessagesArray = new JSONArray();
                for (String errorMessage : errorMessages) {
                    errorMessagesArray.put((Object)errorMessage);
                }
                thisObj.put("errorMessages", (Object)errorMessagesArray);
            }
            thisObj.put("valid", this.isValid());
        }
        catch (JSONException json) {
            LOG.error("Unable to create a JSON object based on this object", (Throwable)json);
        }
        return thisObj;
    }
}

