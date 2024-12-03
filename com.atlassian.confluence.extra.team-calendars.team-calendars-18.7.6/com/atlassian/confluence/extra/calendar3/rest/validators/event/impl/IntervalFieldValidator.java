/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.languages.LocaleManager
 *  com.atlassian.confluence.util.i18n.I18NBeanFactory
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.ws.rs.WebApplicationException
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.rest.validators.event.impl;

import com.atlassian.confluence.extra.calendar3.rest.param.UpdateEventParam;
import com.atlassian.confluence.extra.calendar3.rest.validators.event.AbstractEventValidator;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import java.util.List;
import java.util.Map;
import javax.ws.rs.WebApplicationException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IntervalFieldValidator
extends AbstractEventValidator {
    @Autowired
    public IntervalFieldValidator(@ComponentImport LocaleManager localeManager, @ComponentImport I18NBeanFactory i18NBeanFactory) {
        super(localeManager, i18NBeanFactory);
    }

    @Override
    public boolean isValid(UpdateEventParam param, Map<String, List<String>> fieldErrors) throws WebApplicationException {
        String freq = param.getFreq();
        String interval = param.getInterval();
        if (StringUtils.isNotBlank(freq)) {
            try {
                if (0 >= Integer.parseInt(StringUtils.trim(StringUtils.defaultString(interval)))) {
                    this.addFieldError(fieldErrors, "interval", this.getText("calendar3.error.repeatintervallesserthanone"));
                }
            }
            catch (NumberFormatException invalidInterval) {
                this.addFieldError(fieldErrors, "interval", this.getText("calendar3.error.invalidrepeatinterval"));
            }
        }
        return true;
    }
}

