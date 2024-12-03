/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.parsers;

public class FactoryConfigurationError
extends Error {
    private Exception exception;

    public FactoryConfigurationError() {
        this.exception = null;
    }

    public FactoryConfigurationError(String string) {
        super(string);
        this.exception = null;
    }

    public FactoryConfigurationError(Exception exception) {
        super(exception.toString());
        this.exception = exception;
    }

    public FactoryConfigurationError(Exception exception, String string) {
        super(string);
        this.exception = exception;
    }

    public String getMessage() {
        String string = super.getMessage();
        if (string == null && this.exception != null) {
            return this.exception.getMessage();
        }
        return string;
    }

    public Exception getException() {
        return this.exception;
    }
}

