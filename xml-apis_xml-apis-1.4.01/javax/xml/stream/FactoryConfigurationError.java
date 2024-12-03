/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.stream;

public class FactoryConfigurationError
extends Error {
    private static final long serialVersionUID = -2994412584589975744L;
    private Exception nested;

    public FactoryConfigurationError() {
    }

    public FactoryConfigurationError(Exception exception) {
        this.nested = exception;
    }

    public FactoryConfigurationError(Exception exception, String string) {
        super(string);
        this.nested = exception;
    }

    public FactoryConfigurationError(String string) {
        super(string);
    }

    public FactoryConfigurationError(String string, Exception exception) {
        super(string);
        this.nested = exception;
    }

    public Exception getException() {
        return this.nested;
    }

    public String getMessage() {
        String string = super.getMessage();
        if (string != null) {
            return string;
        }
        if (this.nested != null && (string = this.nested.getMessage()) == null) {
            string = this.nested.getClass().toString();
        }
        return string;
    }
}

