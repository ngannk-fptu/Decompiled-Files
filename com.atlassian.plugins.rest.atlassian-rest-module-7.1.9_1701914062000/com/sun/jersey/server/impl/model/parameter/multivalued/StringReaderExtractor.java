/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.model.parameter.multivalued;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.server.impl.model.parameter.multivalued.AbstractStringReaderExtractor;
import com.sun.jersey.server.impl.model.parameter.multivalued.ExtractorContainerException;
import com.sun.jersey.spi.StringReader;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;

final class StringReaderExtractor
extends AbstractStringReaderExtractor {
    public StringReaderExtractor(StringReader sr, String parameter, String defaultStringValue) {
        super(sr, parameter, defaultStringValue);
    }

    @Override
    public Object extract(MultivaluedMap<String, String> parameters) {
        String v = parameters.getFirst(this.parameter);
        Object result = null;
        if (v != null) {
            try {
                result = this.sr.fromString(v);
            }
            catch (WebApplicationException ex) {
                throw ex;
            }
            catch (ContainerException ex) {
                throw ex;
            }
            catch (Exception ex) {
                throw new ExtractorContainerException(ex);
            }
        }
        if (result == null && this.defaultStringValue != null) {
            result = this.sr.fromString(this.defaultStringValue);
        }
        return result;
    }
}

