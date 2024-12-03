/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

public class RelType
extends Parameter {
    private static final long serialVersionUID = 5346030888832899016L;
    private static final String VALUE_PARENT = "PARENT";
    private static final String VALUE_CHILD = "CHILD";
    private static final String VALUE_SIBLING = "SIBLING";
    public static final RelType PARENT = new RelType("PARENT");
    public static final RelType CHILD = new RelType("CHILD");
    public static final RelType SIBLING = new RelType("SIBLING");
    private String value;

    public RelType(String aValue) {
        super("RELTYPE", new Factory());
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
            super("RELTYPE");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            RelType parameter = new RelType(value);
            if (PARENT.equals(parameter)) {
                parameter = PARENT;
            } else if (CHILD.equals(parameter)) {
                parameter = CHILD;
            }
            if (SIBLING.equals(parameter)) {
                parameter = SIBLING;
            }
            return parameter;
        }
    }
}

