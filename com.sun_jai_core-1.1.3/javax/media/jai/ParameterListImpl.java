/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.CaselessStringArrayTable;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Locale;
import javax.media.jai.JaiI18N;
import javax.media.jai.ParameterList;
import javax.media.jai.ParameterListDescriptor;

public class ParameterListImpl
implements ParameterList,
Serializable {
    private ParameterListDescriptor pld;
    private CaselessStringArrayTable paramIndices;
    private Object[] paramValues;
    private Class[] paramClasses;

    public ParameterListImpl(ParameterListDescriptor descriptor) {
        if (descriptor == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.pld = descriptor;
        int numParams = this.pld.getNumParameters();
        if (numParams > 0) {
            Object[] paramDefaults = this.pld.getParamDefaults();
            this.paramClasses = this.pld.getParamClasses();
            this.paramIndices = new CaselessStringArrayTable(this.pld.getParamNames());
            this.paramValues = new Object[numParams];
            for (int i = 0; i < numParams; ++i) {
                this.paramValues[i] = paramDefaults[i];
            }
        } else {
            this.paramClasses = null;
            this.paramIndices = null;
            this.paramValues = null;
        }
    }

    public ParameterListDescriptor getParameterListDescriptor() {
        return this.pld;
    }

    private ParameterList setParameter0(String paramName, Object obj) {
        int index = this.paramIndices.indexOf(paramName);
        if (obj != null && !this.paramClasses[index].isInstance(obj)) {
            throw new IllegalArgumentException(this.formatMsg(JaiI18N.getString("ParameterListImpl0"), new Object[]{obj.getClass().getName(), this.paramClasses[index].getName(), paramName}));
        }
        if (!this.pld.isParameterValueValid(paramName, obj)) {
            throw new IllegalArgumentException(paramName + ":" + JaiI18N.getString("ParameterListImpl1"));
        }
        this.paramValues[index] = obj;
        return this;
    }

    public ParameterList setParameter(String paramName, byte b) {
        return this.setParameter0(paramName, new Byte(b));
    }

    public ParameterList setParameter(String paramName, boolean b) {
        return this.setParameter0(paramName, new Boolean(b));
    }

    public ParameterList setParameter(String paramName, char c) {
        return this.setParameter0(paramName, new Character(c));
    }

    public ParameterList setParameter(String paramName, short s) {
        return this.setParameter0(paramName, new Short(s));
    }

    public ParameterList setParameter(String paramName, int i) {
        return this.setParameter0(paramName, new Integer(i));
    }

    public ParameterList setParameter(String paramName, long l) {
        return this.setParameter0(paramName, new Long(l));
    }

    public ParameterList setParameter(String paramName, float f) {
        return this.setParameter0(paramName, new Float(f));
    }

    public ParameterList setParameter(String paramName, double d) {
        return this.setParameter0(paramName, new Double(d));
    }

    public ParameterList setParameter(String paramName, Object obj) {
        return this.setParameter0(paramName, obj);
    }

    private Object getObjectParameter0(String paramName) {
        Object obj = this.paramValues[this.paramIndices.indexOf(paramName)];
        if (obj == ParameterListDescriptor.NO_PARAMETER_DEFAULT) {
            throw new IllegalStateException(paramName + ":" + JaiI18N.getString("ParameterListImpl2"));
        }
        return obj;
    }

    public Object getObjectParameter(String paramName) {
        return this.getObjectParameter0(paramName);
    }

    public byte getByteParameter(String paramName) {
        return (Byte)this.getObjectParameter0(paramName);
    }

    public boolean getBooleanParameter(String paramName) {
        return (Boolean)this.getObjectParameter0(paramName);
    }

    public char getCharParameter(String paramName) {
        return ((Character)this.getObjectParameter0(paramName)).charValue();
    }

    public short getShortParameter(String paramName) {
        return (Short)this.getObjectParameter0(paramName);
    }

    public int getIntParameter(String paramName) {
        return (Integer)this.getObjectParameter0(paramName);
    }

    public long getLongParameter(String paramName) {
        return (Long)this.getObjectParameter0(paramName);
    }

    public float getFloatParameter(String paramName) {
        return ((Float)this.getObjectParameter0(paramName)).floatValue();
    }

    public double getDoubleParameter(String paramName) {
        return (Double)this.getObjectParameter0(paramName);
    }

    private String formatMsg(String key, Object[] args) {
        MessageFormat mf = new MessageFormat(key);
        mf.setLocale(Locale.getDefault());
        return mf.format(args);
    }
}

