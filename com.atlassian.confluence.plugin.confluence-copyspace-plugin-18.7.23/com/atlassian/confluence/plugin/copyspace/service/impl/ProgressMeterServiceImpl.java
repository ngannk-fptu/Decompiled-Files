/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.ProgressMeter
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugin.copyspace.service.impl;

import com.atlassian.confluence.plugin.copyspace.service.I18NBeanProvider;
import com.atlassian.confluence.plugin.copyspace.service.ProgressMeterService;
import com.atlassian.confluence.plugin.copyspace.util.DateFormatUtils;
import com.atlassian.core.util.ProgressMeter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component(value="progressMeterServiceImpl")
public class ProgressMeterServiceImpl
implements ProgressMeterService {
    private final I18NBeanProvider i18NBeanProvider;
    private static final Logger log = LoggerFactory.getLogger(ProgressMeterServiceImpl.class);

    public ProgressMeterServiceImpl(I18NBeanProvider i18NBeanProvider) {
        this.i18NBeanProvider = i18NBeanProvider;
    }

    @Override
    public void incrementProgressMeterCount(ProgressMeter progressMeter) {
        int currentCount = progressMeter.getCurrentCount();
        progressMeter.setCurrentCount(currentCount + 1);
    }

    @Override
    public void setStatusMessage(String message, ProgressMeter progressMeter) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("message", this.i18NBeanProvider.getI18NBean().getText(message));
        progressMeter.setStatus(this.getStatusString(map));
    }

    @Override
    public void setTimeTaken(long elapsedTime, ProgressMeter progressMeter) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("timeTaken", DateFormatUtils.prettyTime(elapsedTime, this.i18NBeanProvider.getI18NBean()));
        progressMeter.setStatus(this.getStatusString(map));
    }

    @Override
    public void setAttachmentErrorMessage(ProgressMeter progressMeter) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("errorMessage", this.i18NBeanProvider.getI18NBean().getText("copyspace.copy.attachments.error"));
        progressMeter.setStatus(this.getStatusString(map));
    }

    private String getStatusString(Map<String, Object> innerMap) {
        HashMap<String, Map<String, Object>> map = new HashMap<String, Map<String, Object>>();
        map.put("status", innerMap);
        String message = "";
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            message = objectMapper.writeValueAsString(map);
        }
        catch (IOException e) {
            log.error("Could not serialize JSON status", (Throwable)e);
        }
        return message;
    }
}

