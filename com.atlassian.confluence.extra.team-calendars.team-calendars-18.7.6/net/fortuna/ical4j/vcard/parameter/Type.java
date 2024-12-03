/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.vcard.parameter;

import java.util.ArrayList;
import java.util.Arrays;
import net.fortuna.ical4j.vcard.Parameter;
import net.fortuna.ical4j.vcard.ParameterFactory;

public final class Type
extends Parameter {
    private static final long serialVersionUID = -3644362129355908795L;
    public static final Type HOME = new Type("home");
    public static final Type WORK = new Type("work");
    public static final Type PREF = new Type("pref");
    public static final ParameterFactory<Type> FACTORY = new Factory();
    private final String[] types;

    public Type(String value) {
        super(Parameter.Id.TYPE);
        this.types = value.split(",");
    }

    public Type(String ... types) {
        super(Parameter.Id.TYPE);
        this.types = types;
    }

    public Type(Type ... types) {
        super(Parameter.Id.TYPE);
        ArrayList<String> typeList = new ArrayList<String>();
        for (Type type : types) {
            typeList.addAll(Arrays.asList(type.getTypes()));
        }
        this.types = typeList.toArray(new String[typeList.size()]);
    }

    public String[] getTypes() {
        return this.types;
    }

    public String getValue() {
        StringBuilder b = new StringBuilder();
        for (int i = 0; i < this.types.length; ++i) {
            b.append(this.types[i]);
            if (i >= this.types.length - 1) continue;
            b.append(',');
        }
        return b.toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class Factory
    implements ParameterFactory<Type> {
        private Factory() {
        }

        @Override
        public Type createParameter(String value) {
            Type parameter = null;
            parameter = HOME.getValue().equals(value) ? HOME : (PREF.getValue().equals(value) ? PREF : (WORK.getValue().equals(value) ? WORK : new Type(value)));
            return parameter;
        }
    }
}

