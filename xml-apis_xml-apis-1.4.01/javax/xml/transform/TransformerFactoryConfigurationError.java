/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.transform;

public class TransformerFactoryConfigurationError
extends Error {
    private Exception exception;

    public TransformerFactoryConfigurationError() {
        this.exception = null;
    }

    public TransformerFactoryConfigurationError(String string) {
        super(string);
        this.exception = null;
    }

    public TransformerFactoryConfigurationError(Exception exception) {
        super(exception.toString());
        this.exception = exception;
    }

    public TransformerFactoryConfigurationError(Exception exception, String string) {
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

