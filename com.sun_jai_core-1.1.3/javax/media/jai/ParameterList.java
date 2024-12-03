/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import javax.media.jai.ParameterListDescriptor;

public interface ParameterList {
    public ParameterListDescriptor getParameterListDescriptor();

    public ParameterList setParameter(String var1, byte var2);

    public ParameterList setParameter(String var1, boolean var2);

    public ParameterList setParameter(String var1, char var2);

    public ParameterList setParameter(String var1, short var2);

    public ParameterList setParameter(String var1, int var2);

    public ParameterList setParameter(String var1, long var2);

    public ParameterList setParameter(String var1, float var2);

    public ParameterList setParameter(String var1, double var2);

    public ParameterList setParameter(String var1, Object var2);

    public Object getObjectParameter(String var1);

    public byte getByteParameter(String var1);

    public boolean getBooleanParameter(String var1);

    public char getCharParameter(String var1);

    public short getShortParameter(String var1);

    public int getIntParameter(String var1);

    public long getLongParameter(String var1);

    public float getFloatParameter(String var1);

    public double getDoubleParameter(String var1);
}

