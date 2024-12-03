/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.constants;

import java.io.ObjectStreamException;
import org.apache.axis.Constants;
import org.apache.axis.constants.Enum;

public class Use
extends Enum {
    private static final Type type = new Type();
    public static final String ENCODED_STR = "encoded";
    public static final String LITERAL_STR = "literal";
    public static final Use ENCODED = type.getUse("encoded");
    public static final Use LITERAL = type.getUse("literal");
    public static final Use DEFAULT = ENCODED;
    private String encoding;

    public static Use getDefault() {
        return (Use)type.getDefault();
    }

    public final String getEncoding() {
        return this.encoding;
    }

    public static final Use getUse(int style) {
        return type.getUse(style);
    }

    public static final Use getUse(String style) {
        return type.getUse(style);
    }

    public static final Use getUse(String style, Use dephault) {
        return type.getUse(style, dephault);
    }

    public static final boolean isValid(String style) {
        return type.isValid(style);
    }

    public static final int size() {
        return type.size();
    }

    public static final String[] getUses() {
        return type.getEnumNames();
    }

    private Object readResolve() throws ObjectStreamException {
        return type.getUse(this.value);
    }

    private Use(int value, String name, String encoding) {
        super(type, value, name);
        this.encoding = encoding;
    }

    protected Use() {
        super(type, DEFAULT.getValue(), DEFAULT.getName());
        this.encoding = DEFAULT.getEncoding();
    }

    static {
        type.setDefault(DEFAULT);
    }

    public static class Type
    extends Enum.Type {
        private Type() {
            super("style", new Enum[]{new Use(0, Use.ENCODED_STR, Constants.URI_DEFAULT_SOAP_ENC), new Use(1, Use.LITERAL_STR, "")});
        }

        public final Use getUse(int style) {
            return (Use)this.getEnum(style);
        }

        public final Use getUse(String style) {
            return (Use)this.getEnum(style);
        }

        public final Use getUse(String style, Use dephault) {
            return (Use)this.getEnum(style, dephault);
        }
    }
}

