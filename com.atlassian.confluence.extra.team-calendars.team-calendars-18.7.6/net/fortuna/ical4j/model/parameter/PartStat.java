/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import java.net.URISyntaxException;
import net.fortuna.ical4j.model.Content;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterFactory;
import net.fortuna.ical4j.util.Strings;

public class PartStat
extends Parameter {
    private static final long serialVersionUID = -7856347127343842441L;
    private static final String VALUE_NEEDS_ACTION = "NEEDS-ACTION";
    private static final String VALUE_ACCEPTED = "ACCEPTED";
    private static final String VALUE_DECLINED = "DECLINED";
    private static final String VALUE_TENTATIVE = "TENTATIVE";
    private static final String VALUE_DELEGATED = "DELEGATED";
    private static final String VALUE_COMPLETED = "COMPLETED";
    private static final String VALUE_IN_PROCESS = "IN-PROCESS";
    public static final PartStat NEEDS_ACTION = new PartStat("NEEDS-ACTION");
    public static final PartStat ACCEPTED = new PartStat("ACCEPTED");
    public static final PartStat DECLINED = new PartStat("DECLINED");
    public static final PartStat TENTATIVE = new PartStat("TENTATIVE");
    public static final PartStat DELEGATED = new PartStat("DELEGATED");
    public static final PartStat COMPLETED = new PartStat("COMPLETED");
    public static final PartStat IN_PROCESS = new PartStat("IN-PROCESS");
    private String value;

    public PartStat(String aValue) {
        super("PARTSTAT", new Factory());
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
            super("PARTSTAT");
        }

        public Parameter createParameter(String value) throws URISyntaxException {
            PartStat parameter = new PartStat(value);
            if (NEEDS_ACTION.equals(parameter)) {
                parameter = NEEDS_ACTION;
            } else if (ACCEPTED.equals(parameter)) {
                parameter = ACCEPTED;
            } else if (DECLINED.equals(parameter)) {
                parameter = DECLINED;
            } else if (TENTATIVE.equals(parameter)) {
                parameter = TENTATIVE;
            } else if (DELEGATED.equals(parameter)) {
                parameter = DELEGATED;
            } else if (COMPLETED.equals(parameter)) {
                parameter = COMPLETED;
            } else if (IN_PROCESS.equals(parameter)) {
                parameter = IN_PROCESS;
            }
            return parameter;
        }
    }
}

