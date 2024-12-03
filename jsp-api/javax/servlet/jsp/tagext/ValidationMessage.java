/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp.tagext;

public class ValidationMessage {
    private final String id;
    private final String message;

    public ValidationMessage(String id, String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return this.id;
    }

    public String getMessage() {
        return this.message;
    }
}

