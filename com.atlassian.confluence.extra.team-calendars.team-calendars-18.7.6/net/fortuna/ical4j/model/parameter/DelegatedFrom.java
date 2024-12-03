/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URISyntaxException;
import net.fortuna.ical4j.model.AddressList;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

public class DelegatedFrom
extends Parameter {
    private static final long serialVersionUID = -795956139235258568L;
    private AddressList delegators;

    public DelegatedFrom(String aValue) throws URISyntaxException {
        this(new AddressList(Strings.unquote(aValue)));
    }

    public DelegatedFrom(AddressList aList) {
        super("DELEGATED-FROM", new Factory());
        this.delegators = aList;
    }

    public final AddressList getDelegators() {
        return this.delegators;
    }

    @Override
    public final String getValue() {
        return this.getDelegators().toString();
    }

    @Override
    protected boolean isQuotable() {
        return false;
    }

    public static class Factory
    extends Content.Factory
    implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("DELEGATED-FROM");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            return new DelegatedFrom(value);
        }
    }
}

