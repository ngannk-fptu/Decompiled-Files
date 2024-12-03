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

public class Member
extends Parameter {
    private static final long serialVersionUID = 287348849443687499L;
    private AddressList groups;

    public Member(String aValue) throws URISyntaxException {
        this(new AddressList(Strings.unquote(aValue)));
    }

    public Member(AddressList aList) {
        super("MEMBER", new Factory());
        this.groups = aList;
    }

    public final AddressList getGroups() {
        return this.groups;
    }

    @Override
    public final String getValue() {
        return this.getGroups().toString();
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
            super("MEMBER");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            return new Member(value);
        }
    }
}

