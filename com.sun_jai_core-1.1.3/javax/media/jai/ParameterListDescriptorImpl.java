/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.CaselessStringArrayTable;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.JaiI18N;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.util.Range;

public class ParameterListDescriptorImpl
implements ParameterListDescriptor,
Serializable {
    private int numParams;
    private String[] paramNames;
    private Class[] paramClasses;
    private Object[] paramDefaults;
    private Object[] validParamValues;
    private CaselessStringArrayTable paramIndices;
    private Object descriptor;
    private boolean validParamsInitialized = false;
    static /* synthetic */ Class class$javax$media$jai$EnumeratedParameter;

    public static Set getEnumeratedValues(Object descriptor, Class paramClass) {
        if (descriptor == null || paramClass == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        if (!(class$javax$media$jai$EnumeratedParameter == null ? (class$javax$media$jai$EnumeratedParameter = ParameterListDescriptorImpl.class$("javax.media.jai.EnumeratedParameter")) : class$javax$media$jai$EnumeratedParameter).isAssignableFrom(paramClass)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("ParameterListDescriptorImpl10", new Object[]{paramClass.getName()}));
        }
        Field[] fields = descriptor.getClass().getDeclaredFields();
        if (fields == null) {
            return null;
        }
        int numFields = fields.length;
        HashSet<Object> valueSet = null;
        for (int j = 0; j < numFields; ++j) {
            Field field = fields[j];
            int modifiers = field.getModifiers();
            if (!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) || !Modifier.isFinal(modifiers)) continue;
            Object fieldValue = null;
            try {
                fieldValue = field.get(null);
            }
            catch (Exception e) {
                // empty catch block
            }
            if (!paramClass.isInstance(fieldValue)) continue;
            if (valueSet == null) {
                valueSet = new HashSet<Object>();
            }
            if (valueSet.contains(fieldValue)) {
                throw new UnsupportedOperationException(JaiI18N.getString("ParameterListDescriptorImpl0"));
            }
            valueSet.add(fieldValue);
        }
        return valueSet;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object getValidParamValue(int index) {
        if (this.validParamsInitialized) {
            return this.validParamValues[index];
        }
        ParameterListDescriptorImpl parameterListDescriptorImpl = this;
        synchronized (parameterListDescriptorImpl) {
            if (this.validParamValues == null) {
                this.validParamValues = new Object[this.numParams];
            }
            Class enumeratedClass = class$javax$media$jai$EnumeratedParameter == null ? (class$javax$media$jai$EnumeratedParameter = ParameterListDescriptorImpl.class$("javax.media.jai.EnumeratedParameter")) : class$javax$media$jai$EnumeratedParameter;
            for (int i = 0; i < this.numParams; ++i) {
                if (this.validParamValues[i] != null || !enumeratedClass.isAssignableFrom(this.paramClasses[i])) continue;
                this.validParamValues[i] = ParameterListDescriptorImpl.getEnumeratedValues(this.descriptor, this.paramClasses[i]);
            }
        }
        this.validParamsInitialized = true;
        return this.validParamValues[index];
    }

    public ParameterListDescriptorImpl() {
        this.numParams = 0;
        this.paramNames = null;
        this.paramClasses = null;
        this.paramDefaults = null;
        this.paramIndices = new CaselessStringArrayTable();
        this.validParamValues = null;
    }

    public ParameterListDescriptorImpl(Object descriptor, String[] paramNames, Class[] paramClasses, Object[] paramDefaults, Object[] validParamValues) {
        int numParams;
        int n = numParams = paramNames == null ? 0 : paramNames.length;
        if (paramDefaults != null && paramDefaults.length != numParams) {
            throw new IllegalArgumentException("paramDefaults" + JaiI18N.getString("ParameterListDescriptorImpl1"));
        }
        if (validParamValues != null && validParamValues.length != numParams) {
            throw new IllegalArgumentException("validParamValues" + JaiI18N.getString("ParameterListDescriptorImpl2"));
        }
        this.descriptor = descriptor;
        if (numParams == 0) {
            if (paramClasses != null && paramClasses.length != 0) {
                throw new IllegalArgumentException("paramClasses" + JaiI18N.getString("ParameterListDescriptorImpl3"));
            }
            this.numParams = 0;
            this.paramNames = null;
            this.paramClasses = null;
            this.paramDefaults = null;
            this.paramIndices = new CaselessStringArrayTable();
            this.validParamValues = null;
        } else {
            int i;
            if (paramClasses == null || paramClasses.length != numParams) {
                throw new IllegalArgumentException("paramClasses" + JaiI18N.getString("ParameterListDescriptorImpl3"));
            }
            this.numParams = numParams;
            this.paramNames = paramNames;
            this.paramClasses = paramClasses;
            this.validParamValues = validParamValues;
            if (paramDefaults == null) {
                this.paramDefaults = new Object[numParams];
                for (i = 0; i < numParams; ++i) {
                    this.paramDefaults[i] = ParameterListDescriptor.NO_PARAMETER_DEFAULT;
                }
            } else {
                this.paramDefaults = paramDefaults;
                for (i = 0; i < numParams; ++i) {
                    if (paramDefaults[i] == null || paramDefaults[i] == ParameterListDescriptor.NO_PARAMETER_DEFAULT || paramClasses[i].isInstance(paramDefaults[i])) continue;
                    throw new IllegalArgumentException(JaiI18N.formatMsg("ParameterListDescriptorImpl4", new Object[]{paramDefaults[i].getClass().getName(), paramClasses[i].getName(), paramNames[i]}));
                }
            }
            if (validParamValues != null) {
                Class enumeratedClass = class$javax$media$jai$EnumeratedParameter == null ? (class$javax$media$jai$EnumeratedParameter = ParameterListDescriptorImpl.class$("javax.media.jai.EnumeratedParameter")) : class$javax$media$jai$EnumeratedParameter;
                for (int i2 = 0; i2 < numParams; ++i2) {
                    if (validParamValues[i2] == null) continue;
                    if (enumeratedClass.isAssignableFrom(paramClasses[i2])) {
                        if (validParamValues[i2] instanceof Set) continue;
                        throw new IllegalArgumentException(JaiI18N.formatMsg("ParameterListDescriptorImpl5", new Object[]{paramNames[i2]}));
                    }
                    if (validParamValues[i2] instanceof Range) {
                        Range range = (Range)validParamValues[i2];
                        if (paramClasses[i2].isAssignableFrom(range.getElementClass())) continue;
                        throw new IllegalArgumentException(JaiI18N.formatMsg("ParameterListDescriptorImpl6", new Object[]{range.getElementClass().getName(), paramClasses[i2].getName(), paramNames[i2]}));
                    }
                    if (paramClasses[i2].isInstance(validParamValues[i2])) continue;
                    throw new IllegalArgumentException(JaiI18N.formatMsg("ParameterListDescriptorImpl7", new Object[]{validParamValues[i2].getClass().getName(), paramClasses[i2].getName(), paramNames[i2]}));
                }
            }
            this.paramIndices = new CaselessStringArrayTable(paramNames);
        }
    }

    public int getNumParameters() {
        return this.numParams;
    }

    public Class[] getParamClasses() {
        return this.paramClasses;
    }

    public String[] getParamNames() {
        return this.paramNames;
    }

    public Object[] getParamDefaults() {
        return this.paramDefaults;
    }

    public Object getParamDefaultValue(String parameterName) {
        return this.paramDefaults[this.paramIndices.indexOf(parameterName)];
    }

    public Range getParamValueRange(String parameterName) {
        Object values = this.getValidParamValue(this.paramIndices.indexOf(parameterName));
        if (values == null || values instanceof Range) {
            return (Range)values;
        }
        return null;
    }

    public String[] getEnumeratedParameterNames() {
        Vector<String> v = new Vector<String>();
        for (int i = 0; i < this.numParams; ++i) {
            if (!(class$javax$media$jai$EnumeratedParameter == null ? ParameterListDescriptorImpl.class$("javax.media.jai.EnumeratedParameter") : class$javax$media$jai$EnumeratedParameter).isAssignableFrom(this.paramClasses[i])) continue;
            v.add(this.paramNames[i]);
        }
        if (v.size() <= 0) {
            return null;
        }
        return v.toArray(new String[0]);
    }

    public EnumeratedParameter[] getEnumeratedParameterValues(String parameterName) {
        int i;
        if (!(class$javax$media$jai$EnumeratedParameter == null ? (class$javax$media$jai$EnumeratedParameter = ParameterListDescriptorImpl.class$("javax.media.jai.EnumeratedParameter")) : class$javax$media$jai$EnumeratedParameter).isAssignableFrom(this.paramClasses[i = this.paramIndices.indexOf(parameterName)])) {
            throw new IllegalArgumentException(parameterName + ":" + JaiI18N.getString("ParameterListDescriptorImpl8"));
        }
        Set enumSet = (Set)this.getValidParamValue(i);
        if (enumSet == null) {
            return null;
        }
        return enumSet.toArray(new EnumeratedParameter[0]);
    }

    public boolean isParameterValueValid(String parameterName, Object value) {
        int index = this.paramIndices.indexOf(parameterName);
        if (value == null && this.paramDefaults[index] == null) {
            return true;
        }
        if (value != null && !this.paramClasses[index].isInstance(value)) {
            throw new IllegalArgumentException(JaiI18N.formatMsg("ParameterListDescriptorImpl9", new Object[]{value.getClass().getName(), this.paramClasses[index].getName(), parameterName}));
        }
        Object validValues = this.getValidParamValue(index);
        if (validValues == null) {
            return true;
        }
        if (validValues instanceof Range) {
            return ((Range)validValues).contains((Comparable)value);
        }
        if (validValues instanceof Set) {
            return ((Set)validValues).contains(value);
        }
        return value == validValues;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

