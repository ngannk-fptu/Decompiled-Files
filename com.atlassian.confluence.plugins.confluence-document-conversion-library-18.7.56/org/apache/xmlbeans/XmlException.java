/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlRuntimeException;

public class XmlException
extends Exception {
    private static final long serialVersionUID = 1L;
    private List<XmlError> _errors;

    public XmlException(String m) {
        super(m);
    }

    public XmlException(String m, Throwable t) {
        super(m, t);
    }

    public XmlException(Throwable t) {
        super(t);
    }

    public XmlException(XmlError error) {
        this(error.toString(), null, error);
    }

    public XmlException(String m, Throwable t, XmlError error) {
        this(m, t, Collections.singletonList(error));
    }

    public XmlException(String m, Throwable t, Collection<XmlError> errors) {
        super(m, t);
        if (errors != null) {
            this._errors = Collections.unmodifiableList(new ArrayList<XmlError>(errors));
        }
    }

    public XmlException(XmlRuntimeException xmlRuntimeException) {
        super(xmlRuntimeException.getMessage(), xmlRuntimeException.getCause());
        Collection<XmlError> errors = xmlRuntimeException.getErrors();
        if (errors != null) {
            this._errors = Collections.unmodifiableList(new ArrayList<XmlError>(errors));
        }
    }

    public XmlError getError() {
        if (this._errors == null || this._errors.size() == 0) {
            return null;
        }
        return this._errors.get(0);
    }

    public Collection<XmlError> getErrors() {
        return this._errors;
    }
}

