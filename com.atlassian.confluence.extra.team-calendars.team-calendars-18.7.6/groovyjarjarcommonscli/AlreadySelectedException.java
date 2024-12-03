/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarcommonscli;

import groovyjarjarcommonscli.Option;
import groovyjarjarcommonscli.OptionGroup;
import groovyjarjarcommonscli.ParseException;

public class AlreadySelectedException
extends ParseException {
    private OptionGroup group;
    private Option option;

    public AlreadySelectedException(String message) {
        super(message);
    }

    public AlreadySelectedException(OptionGroup group, Option option) {
        this("The option '" + option.getKey() + "' was specified but an option from this group " + "has already been selected: '" + group.getSelected() + "'");
        this.group = group;
        this.option = option;
    }

    public OptionGroup getOptionGroup() {
        return this.group;
    }

    public Option getOption() {
        return this.option;
    }
}

