/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

public class CuType
extends Parameter {
    private static final long serialVersionUID = -3134064324693983052L;
    private static final String VALUE_INDIVIDUAL = "INDIVIDUAL";
    private static final String VALUE_GROUP = "GROUP";
    private static final String VALUE_RESOURCE = "RESOURCE";
    private static final String VALUE_ROOM = "ROOM";
    private static final String VALUE_UNKNOWN = "UNKNOWN";
    public static final CuType INDIVIDUAL = new CuType("INDIVIDUAL");
    public static final CuType GROUP = new CuType("GROUP");
    public static final CuType RESOURCE = new CuType("RESOURCE");
    public static final CuType ROOM = new CuType("ROOM");
    public static final CuType UNKNOWN = new CuType("UNKNOWN");
    private String value;

    public CuType(String aValue) {
        super("CUTYPE", new Factory());
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
            super("CUTYPE");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            CuType parameter = new CuType(value);
            if (INDIVIDUAL.equals(parameter)) {
                parameter = INDIVIDUAL;
            } else if (GROUP.equals(parameter)) {
                parameter = GROUP;
            } else if (RESOURCE.equals(parameter)) {
                parameter = RESOURCE;
            } else if (ROOM.equals(parameter)) {
                parameter = ROOM;
            } else if (UNKNOWN.equals(parameter)) {
                parameter = UNKNOWN;
            }
            return parameter;
        }
    }
}

