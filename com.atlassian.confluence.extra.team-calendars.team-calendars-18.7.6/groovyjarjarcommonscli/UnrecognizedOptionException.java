/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarcommonscli;

import groovyjarjarcommonscli.ParseException;

public class UnrecognizedOptionException
extends ParseException {
    private String option;

    public UnrecognizedOptionException(String message) {
        super(message);
    }

    public UnrecognizedOptionException(String message, String option) {
        this(message);
        this.option = option;
    }

    public String getOption() {
        return this.option;
    }
}

