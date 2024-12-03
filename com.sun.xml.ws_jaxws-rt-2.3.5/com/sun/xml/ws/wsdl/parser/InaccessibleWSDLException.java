/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.wsdl.parser;

import com.sun.xml.ws.wsdl.parser.ErrorHandler;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.xml.ws.WebServiceException;

public class InaccessibleWSDLException
extends WebServiceException {
    private final List<Throwable> errors;
    private static final long serialVersionUID = 1L;

    public InaccessibleWSDLException(List<Throwable> errors) {
        super(errors.size() + " counts of InaccessibleWSDLException.\n");
        assert (!errors.isEmpty()) : "there must be at least one error";
        this.errors = Collections.unmodifiableList(new ArrayList<Throwable>(errors));
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append('\n');
        for (Throwable error : this.errors) {
            sb.append(error.toString()).append('\n');
        }
        return sb.toString();
    }

    public List<Throwable> getErrors() {
        return this.errors;
    }

    public static class Builder
    implements ErrorHandler {
        private final List<Throwable> list = new ArrayList<Throwable>();

        @Override
        public void error(Throwable e) {
            this.list.add(e);
        }

        public void check() throws InaccessibleWSDLException {
            if (this.list.isEmpty()) {
                return;
            }
            throw new InaccessibleWSDLException(this.list);
        }
    }
}

