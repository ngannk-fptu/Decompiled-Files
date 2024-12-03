/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.WebApplicationException
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.calendar3.rest.validators.event;

import com.atlassian.confluence.extra.calendar3.rest.param.UpdateEventParam;
import com.atlassian.confluence.extra.calendar3.rest.validators.event.EventValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.WebApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventChainValidator
implements EventValidator {
    private final List<EventValidator> validators;

    @Autowired
    public EventChainValidator(List<EventValidator> validators) {
        this.validators = validators == null ? new ArrayList() : validators;
    }

    @Override
    public boolean isValid(UpdateEventParam param, Map<String, List<String>> fieldErrors) throws WebApplicationException {
        for (EventValidator validator : this.validators) {
            validator.isValid(param, fieldErrors);
        }
        return !fieldErrors.isEmpty();
    }
}

