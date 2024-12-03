/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.parameter;

import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.ParameterFactory;

public final class Pid
extends Parameter {
    private static final long serialVersionUID = -6324011073580375538L;
    public static final ParameterFactory<Pid> FACTORY = new Factory();
    private final Integer pid;

    public Pid(String value) {
        this(Integer.valueOf(value));
    }

    public Pid(Integer pid) {
        super(Parameter.Id.PID);
        this.pid = pid;
    }

    public Integer getPid() {
        return this.pid;
    }

    public String getValue() {
        return this.pid.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements ParameterFactory<Pid> {
        private Factory() {
        }

        @Override
        public Pid createParameter(String value) {
            return new Pid(Integer.valueOf(value));
        }
    }
}

