/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.Converter;

@Deprecated
public abstract class AbstractArrayConverter
implements Converter {
    public static final Object NO_DEFAULT = new Object();
    protected static String[] strings = new String[0];
    protected Object defaultValue = null;
    protected boolean useDefault = true;

    public AbstractArrayConverter() {
        this.defaultValue = null;
        this.useDefault = false;
    }

    public AbstractArrayConverter(Object defaultValue) {
        if (defaultValue == NO_DEFAULT) {
            this.useDefault = false;
        } else {
            this.defaultValue = defaultValue;
            this.useDefault = true;
        }
    }

    public abstract Object convert(Class var1, Object var2);

    protected List parseElements(String svalue) {
        if (svalue == null) {
            throw new NullPointerException();
        }
        if ((svalue = svalue.trim()).startsWith("{") && svalue.endsWith("}")) {
            svalue = svalue.substring(1, svalue.length() - 1);
        }
        try {
            int ttype;
            StreamTokenizer st = new StreamTokenizer(new StringReader(svalue));
            st.whitespaceChars(44, 44);
            st.ordinaryChars(48, 57);
            st.ordinaryChars(46, 46);
            st.ordinaryChars(45, 45);
            st.wordChars(48, 57);
            st.wordChars(46, 46);
            st.wordChars(45, 45);
            ArrayList<String> list = new ArrayList<String>();
            while ((ttype = st.nextToken()) == -3 || ttype > 0) {
                list.add(st.sval);
            }
            if (ttype != -1) {
                throw new ConversionException("Encountered token of type " + ttype);
            }
            return list;
        }
        catch (IOException e) {
            throw new ConversionException(e);
        }
    }
}

