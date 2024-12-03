/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;

public class XmlRuntimeException
extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private List<XmlError> _errors;

    public XmlRuntimeException(String m) {
        super(m);
    }

    public XmlRuntimeException(String m, Throwable t) {
        super(m, t);
    }

    public XmlRuntimeException(Throwable t) {
        super(t);
    }

    public XmlRuntimeException(String m, Throwable t, Collection<XmlError> errors) {
        super(m, t);
        if (errors != null) {
            this._errors = Collections.unmodifiableList(new ArrayList<XmlError>(errors));
        }
    }

    public XmlRuntimeException(XmlError error) {
        this(error.toString(), null, error);
    }

    public XmlRuntimeException(String m, Throwable t, XmlError error) {
        this(m, t, Collections.singletonList(error));
    }

    public XmlRuntimeException(XmlException xmlException) {
        super(xmlException.getMessage(), xmlException.getCause());
        Collection<XmlError> errors = xmlException.getErrors();
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

