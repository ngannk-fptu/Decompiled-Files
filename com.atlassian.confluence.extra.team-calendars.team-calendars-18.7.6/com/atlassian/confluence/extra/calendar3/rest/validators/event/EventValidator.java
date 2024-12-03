/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.WebApplicationException
 */
package com.atlassian.confluence.extra.calendar3.rest.validators.event;

import com.atlassian.confluence.extra.calendar3.rest.param.UpdateEventParam;
import java.util.List;
import java.util.Map;
import javax.ws.rs.WebApplicationException;

public interface EventValidator {
    public boolean isValid(UpdateEventParam var1, Map<String, List<String>> var2) throws WebApplicationException;
}

