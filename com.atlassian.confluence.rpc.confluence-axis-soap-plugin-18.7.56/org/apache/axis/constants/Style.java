/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.constants;

import java.io.ObjectStreamException;
import javax.xml.namespace.QName;
import org.apache.axis.constants.Enum;
import org.apache.axis.deployment.wsdd.WSDDConstants;

public class Style
extends Enum {
    private static final Type type = new Type();
    public static final String RPC_STR = "rpc";
    public static final String DOCUMENT_STR = "document";
    public static final String WRAPPED_STR = "wrapped";
    public static final String MESSAGE_STR = "message";
    public static final Style RPC = type.getStyle("rpc");
    public static final Style DOCUMENT = type.getStyle("document");
    public static final Style WRAPPED = type.getStyle("wrapped");
    public static final Style MESSAGE = type.getStyle("message");
    public static final Style DEFAULT = RPC;
    private QName provider;

    public static Style getDefault() {
        return (Style)type.getDefault();
    }

    public final QName getProvider() {
        return this.provider;
    }

    public static final Style getStyle(int style) {
        return type.getStyle(style);
    }

    public static final Style getStyle(String style) {
        return type.getStyle(style);
    }

    public static final Style getStyle(String style, Style dephault) {
        return type.getStyle(style, dephault);
    }

    public static final boolean isValid(String style) {
        return type.isValid(style);
    }

    public static final int size() {
        return type.size();
    }

    public static final String[] getStyles() {
        return type.getEnumNames();
    }

    private Object readResolve() throws ObjectStreamException {
        return type.getStyle(this.value);
    }

    private Style(int value, String name, QName provider) {
        super(type, value, name);
        this.provider = provider;
    }

    protected Style() {
        super(type, DEFAULT.getValue(), DEFAULT.getName());
        this.provider = DEFAULT.getProvider();
    }

    static {
        type.setDefault(DEFAULT);
    }

    public static class Type
    extends Enum.Type {
        private Type() {
            super("style", new Enum[]{new Style(0, Style.RPC_STR, WSDDConstants.QNAME_JAVARPC_PROVIDER), new Style(1, Style.DOCUMENT_STR, WSDDConstants.QNAME_JAVARPC_PROVIDER), new Style(2, Style.WRAPPED_STR, WSDDConstants.QNAME_JAVARPC_PROVIDER), new Style(3, Style.MESSAGE_STR, WSDDConstants.QNAME_JAVAMSG_PROVIDER)});
        }

        public final Style getStyle(int style) {
            return (Style)this.getEnum(style);
        }

        public final Style getStyle(String style) {
            return (Style)this.getEnum(style);
        }

        public final Style getStyle(String style, Style dephault) {
            return (Style)this.getEnum(style, dephault);
        }
    }
}

