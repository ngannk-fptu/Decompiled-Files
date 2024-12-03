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

public class DelegatedTo
extends Parameter {
    private static final long serialVersionUID = 567577003350648021L;
    private AddressList delegatees;

    public DelegatedTo(String aValue) throws URISyntaxException {
        this(new AddressList(Strings.unquote(aValue)));
    }

    public DelegatedTo(AddressList aList) {
        super("DELEGATED-TO", new Factory());
        this.delegatees = aList;
    }

    public final AddressList getDelegatees() {
        return this.delegatees;
    }

    @Override
    public final String getValue() {
        return this.getDelegatees().toString();
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
            super("DELEGATED-TO");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            return new DelegatedTo(value);
        }
    }
}

