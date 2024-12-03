/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import javax.media.jai.EnumeratedParameter;
import javax.media.jai.util.Range;

public interface ParameterListDescriptor {
    public static final Object NO_PARAMETER_DEFAULT = 1.class$javax$media$jai$ParameterNoDefault == null ? (1.class$javax$media$jai$ParameterNoDefault = 1.class$("javax.media.jai.ParameterNoDefault")) : 1.class$javax$media$jai$ParameterNoDefault;

    public int getNumParameters();

    public Class[] getParamClasses();

    public String[] getParamNames();

    public Object[] getParamDefaults();

    public Object getParamDefaultValue(String var1);

    public Range getParamValueRange(String var1);

    public String[] getEnumeratedParameterNames();

    public EnumeratedParameter[] getEnumeratedParameterValues(String var1);

    public boolean isParameterValueValid(String var1, Object var2);

    static class 1 {
        static /* synthetic */ Class class$javax$media$jai$ParameterNoDefault;

        static /* synthetic */ Class class$(String x0) {
            try {
                return Class.forName(x0);
            }
            catch (ClassNotFoundException x1) {
                throw new NoClassDefFoundError(x1.getMessage());
            }
        }
    }
}

