/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarcommonscli;

import groovyjarjarcommonscli.Option;
import groovyjarjarcommonscli.ParseException;

public class MissingArgumentException
extends ParseException {
    private Option option;

    public MissingArgumentException(String message) {
        super(message);
    }

    public MissingArgumentException(Option option) {
        this("Missing argument for option: " + option.getKey());
        this.option = option;
    }

    public Option getOption() {
        return this.option;
    }
}

