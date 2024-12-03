/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import com.sun.media.jai.util.CaselessStringArrayTable;
import java.awt.image.renderable.ParameterBlock;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;
import javax.media.jai.DeferredData;
import javax.media.jai.JAI;
import javax.media.jai.JaiI18N;
import javax.media.jai.OperationDescriptor;
import javax.media.jai.ParameterList;
import javax.media.jai.ParameterListDescriptor;

public class ParameterBlockJAI
extends ParameterBlock
implements ParameterList {
    private transient OperationDescriptor odesc;
    private String modeName;
    private ParameterListDescriptor pld;
    private CaselessStringArrayTable paramIndices;
    private CaselessStringArrayTable sourceIndices;
    private int numParameters;
    private String[] paramNames;
    private Class[] paramClasses;
    private Class[] sourceClasses;
    static /* synthetic */ Class class$javax$media$jai$OperationDescriptor;

    private static String getDefaultMode(OperationDescriptor odesc) {
        if (odesc == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        return odesc.getSupportedModes()[0];
    }

    public ParameterBlockJAI(OperationDescriptor odesc) {
        this(odesc, ParameterBlockJAI.getDefaultMode(odesc));
    }

    public ParameterBlockJAI(String operationName) {
        this((OperationDescriptor)JAI.getDefaultInstance().getOperationRegistry().getDescriptor(class$javax$media$jai$OperationDescriptor == null ? (class$javax$media$jai$OperationDescriptor = ParameterBlockJAI.class$("javax.media.jai.OperationDescriptor")) : class$javax$media$jai$OperationDescriptor, operationName));
    }

    public ParameterBlockJAI(OperationDescriptor odesc, String modeName) {
        if (odesc == null || modeName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        this.odesc = odesc;
        this.modeName = modeName;
        this.pld = odesc.getParameterListDescriptor(modeName);
        this.numParameters = this.pld.getNumParameters();
        this.paramNames = this.pld.getParamNames();
        this.paramIndices = new CaselessStringArrayTable(this.pld.getParamNames());
        this.sourceIndices = new CaselessStringArrayTable(odesc.getSourceNames());
        this.paramClasses = this.pld.getParamClasses();
        this.sourceClasses = odesc.getSourceClasses(modeName);
        Object[] defaults = this.pld.getParamDefaults();
        this.parameters = new Vector(this.numParameters);
        for (int i = 0; i < this.numParameters; ++i) {
            this.parameters.addElement(defaults[i]);
        }
    }

    public ParameterBlockJAI(String operationName, String modeName) {
        this((OperationDescriptor)JAI.getDefaultInstance().getOperationRegistry().getDescriptor(modeName, operationName), modeName);
    }

    public int indexOfSource(String sourceName) {
        return this.sourceIndices.indexOf(sourceName);
    }

    public int indexOfParam(String paramName) {
        return this.paramIndices.indexOf(paramName);
    }

    public OperationDescriptor getOperationDescriptor() {
        return this.odesc;
    }

    public ParameterListDescriptor getParameterListDescriptor() {
        return this.pld;
    }

    public String getMode() {
        return this.modeName;
    }

    public ParameterBlockJAI setSource(String sourceName, Object source) {
        if (source == null || sourceName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("Generic0"));
        }
        int index = this.indexOfSource(sourceName);
        if (!this.sourceClasses[index].isInstance(source)) {
            throw new IllegalArgumentException(JaiI18N.getString("ParameterBlockJAI4"));
        }
        if (index >= this.odesc.getNumSources()) {
            this.addSource(source);
        } else {
            this.setSource(source, index);
        }
        return this;
    }

    public Class[] getParamClasses() {
        return this.paramClasses;
    }

    private Object getObjectParameter0(String paramName) {
        Object obj = this.getObjectParameter(this.indexOfParam(paramName));
        if (obj == ParameterListDescriptor.NO_PARAMETER_DEFAULT) {
            throw new IllegalStateException(paramName + ":" + JaiI18N.getString("ParameterBlockJAI6"));
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

    private int checkParameter(String paramName, Object obj) {
        int index = this.indexOfParam(paramName);
        if (obj != null) {
            if (obj == ParameterListDescriptor.NO_PARAMETER_DEFAULT) {
                throw new IllegalArgumentException(paramName + ":" + JaiI18N.getString("ParameterBlockJAI8"));
            }
            if (obj instanceof DeferredData) {
                DeferredData dd = (DeferredData)obj;
                if (!this.paramClasses[index].isAssignableFrom(dd.getDataClass())) {
                    throw new IllegalArgumentException(paramName + ":" + JaiI18N.getString("ParameterBlockJAI0"));
                }
                if (dd.isValid() && !this.pld.isParameterValueValid(paramName, dd.getData())) {
                    throw new IllegalArgumentException(paramName + ":" + JaiI18N.getString("ParameterBlockJAI2"));
                }
            } else if (!this.paramClasses[index].isInstance(obj)) {
                throw new IllegalArgumentException(paramName + ":" + JaiI18N.getString("ParameterBlockJAI0"));
            }
        }
        if (!(obj != null && obj instanceof DeferredData || this.pld.isParameterValueValid(paramName, obj))) {
            throw new IllegalArgumentException(paramName + ":" + JaiI18N.getString("ParameterBlockJAI2"));
        }
        return index;
    }

    private ParameterList setParameter0(String paramName, Object obj) {
        int index = this.checkParameter(paramName, obj);
        this.parameters.setElementAt(obj, index);
        return this;
    }

    public ParameterBlock add(Object obj) {
        throw new IllegalStateException(JaiI18N.getString("ParameterBlockJAI5"));
    }

    public ParameterBlock set(Object obj, int index) {
        if (index < 0 || index >= this.pld.getNumParameters()) {
            throw new ArrayIndexOutOfBoundsException();
        }
        this.setParameter0(this.paramNames[index], obj);
        return this;
    }

    public void setParameters(Vector parameters) {
        if (parameters == null || parameters.size() != this.numParameters) {
            throw new IllegalArgumentException(JaiI18N.getString("ParameterBlockJAI7"));
        }
        for (int i = 0; i < this.numParameters; ++i) {
            this.checkParameter(this.paramNames[i], parameters.get(i));
        }
        this.parameters = parameters;
    }

    public int indexOf(String paramName) {
        return this.indexOfParam(paramName);
    }

    public ParameterBlock set(byte b, String paramName) {
        return this.set((Object)new Byte(b), paramName);
    }

    public ParameterBlock set(char c, String paramName) {
        return this.set((Object)new Character(c), paramName);
    }

    public ParameterBlock set(short s, String paramName) {
        return this.set((Object)new Short(s), paramName);
    }

    public ParameterBlock set(int i, String paramName) {
        return this.set((Object)new Integer(i), paramName);
    }

    public ParameterBlock set(long l, String paramName) {
        return this.set((Object)new Long(l), paramName);
    }

    public ParameterBlock set(float f, String paramName) {
        return this.set((Object)new Float(f), paramName);
    }

    public ParameterBlock set(double d, String paramName) {
        return this.set((Object)new Double(d), paramName);
    }

    public ParameterBlock set(Object obj, String paramName) {
        this.setParameter0(paramName, obj);
        return this;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.odesc.getName());
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        String operationName = (String)in.readObject();
        this.odesc = (OperationDescriptor)JAI.getDefaultInstance().getOperationRegistry().getDescriptor(this.modeName, operationName);
        if (this.odesc == null) {
            throw new NotSerializableException(operationName + " " + JaiI18N.getString("ParameterBlockJAI1"));
        }
    }

    public Object clone() {
        ParameterBlockJAI theClone = (ParameterBlockJAI)this.shallowClone();
        if (this.sources != null) {
            theClone.setSources((Vector)this.sources.clone());
        }
        if (this.parameters != null) {
            theClone.parameters = (Vector)this.parameters.clone();
        }
        return theClone;
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

