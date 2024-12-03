/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.exception;

import org.apache.velocity.exception.ExtendedParseException;
import org.apache.velocity.exception.VelocityException;
import org.apache.velocity.runtime.log.Log;

public class MethodInvocationException
extends VelocityException
implements ExtendedParseException {
    private static final long serialVersionUID = 7305685093478106342L;
    private String referenceName = "";
    private final String methodName;
    private final int lineNumber;
    private final int columnNumber;
    private final String templateName;

    public MethodInvocationException(String message, Throwable e, String methodName, String templateName, int lineNumber, int columnNumber) {
        super(message, e);
        this.methodName = methodName;
        this.templateName = templateName;
        this.lineNumber = lineNumber;
        this.columnNumber = columnNumber;
    }

    public String getMethodName() {
        return this.methodName;
    }

    public void setReferenceName(String ref) {
        this.referenceName = ref;
    }

    public String getReferenceName() {
        return this.referenceName;
    }

    @Override
    public int getColumnNumber() {
        return this.columnNumber;
    }

    @Override
    public int getLineNumber() {
        return this.lineNumber;
    }

    @Override
    public String getTemplateName() {
        return this.templateName;
    }

    @Override
    public String getMessage() {
        StringBuffer message = new StringBuffer();
        message.append(super.getMessage());
        message.append(" at ");
        message.append(Log.formatFileString(this.templateName, this.lineNumber, this.columnNumber));
        return message.toString();
    }
}

