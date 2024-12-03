/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import org.apache.abdera.model.Div;
import org.apache.abdera.model.Element;

public interface Text
extends Element {
    public Type getTextType();

    public Text setTextType(Type var1);

    public Div getValueElement();

    public Text setValueElement(Div var1);

    public String getValue();

    public Text setValue(String var1);

    public String getWrappedValue();

    public Text setWrappedValue(String var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Type {
        TEXT,
        HTML,
        XHTML;


        public static Type typeFromString(String val) {
            Type type = TEXT;
            if (val != null) {
                type = val.equalsIgnoreCase("text") ? TEXT : (val.equalsIgnoreCase("html") ? HTML : (val.equalsIgnoreCase("xhtml") ? XHTML : null));
            }
            return type;
        }
    }
}

