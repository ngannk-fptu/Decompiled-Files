/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

public class Role
extends Parameter {
    private static final long serialVersionUID = 1438225631470825963L;
    private static final String VALUE_CHAIR = "CHAIR";
    private static final String VALUE_REQ_PARTICIPANT = "REQ-PARTICIPANT";
    private static final String VALUE_OPT_PARTICIPANT = "OPT-PARTICIPANT";
    private static final String VALUE_NON_PARTICIPANT = "NON-PARTICIPANT";
    public static final Role CHAIR = new Role("CHAIR");
    public static final Role REQ_PARTICIPANT = new Role("REQ-PARTICIPANT");
    public static final Role OPT_PARTICIPANT = new Role("OPT-PARTICIPANT");
    public static final Role NON_PARTICIPANT = new Role("NON-PARTICIPANT");
    private String value;

    public Role(String aValue) {
        super("ROLE", new Factory());
        this.value = Strings.unquote(aValue);
    }

    @Override
    public final String getValue() {
        return this.value;
    }

    public static class Factory
    extends Content.Factory
    implements ParameterFactory {
        private static final long serialVersionUID = 1L;

        public Factory() {
            super("ROLE");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            Role parameter = new Role(value);
            if (CHAIR.equals(parameter)) {
                parameter = CHAIR;
            } else if (REQ_PARTICIPANT.equals(parameter)) {
                parameter = REQ_PARTICIPANT;
            } else if (OPT_PARTICIPANT.equals(parameter)) {
                parameter = OPT_PARTICIPANT;
            } else if (NON_PARTICIPANT.equals(parameter)) {
                parameter = NON_PARTICIPANT;
            }
            return parameter;
        }
    }
}

