/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws.soap;

import javax.xml.ws.WebServiceFeature;

public final class AddressingFeature
extends WebServiceFeature {
    public static final String ID = "http://www.w3.org/2005/08/addressing/module";
    protected boolean required;
    private final Responses responses;

    public AddressingFeature() {
        this(true, false, Responses.ALL);
    }

    public AddressingFeature(boolean enabled) {
        this(enabled, false, Responses.ALL);
    }

    public AddressingFeature(boolean enabled, boolean required) {
        this(enabled, required, Responses.ALL);
    }

    public AddressingFeature(boolean enabled, boolean required, Responses responses) {
        this.enabled = enabled;
        this.required = required;
        this.responses = responses;
    }

    @Override
    public String getID() {
        return ID;
    }

    public boolean isRequired() {
        return this.required;
    }

    public Responses getResponses() {
        return this.responses;
    }

    public static enum Responses {
        ANONYMOUS,
        NON_ANONYMOUS,
        ALL;

    }
}

