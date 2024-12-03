/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai.remote;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import javax.media.jai.ParameterList;
import javax.media.jai.ParameterListDescriptor;
import javax.media.jai.ParameterListDescriptorImpl;
import javax.media.jai.ParameterListImpl;
import javax.media.jai.remote.JaiI18N;
import javax.media.jai.remote.Negotiable;
import javax.media.jai.util.CaselessStringKey;

public class NegotiableCapability
extends ParameterListImpl
implements Serializable {
    private String category;
    private String capabilityName;
    private List generators;
    private boolean isPreference = false;
    static /* synthetic */ Class class$javax$media$jai$remote$Negotiable;

    public NegotiableCapability(String category, String capabilityName, List generators, ParameterListDescriptor descriptor, boolean isPreference) {
        super(descriptor);
        if (category == null) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability0"));
        }
        if (capabilityName == null) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability1"));
        }
        ParameterListDescriptor desc = this.getParameterListDescriptor();
        int numParams = desc.getNumParameters();
        String[] names = desc.getParamNames();
        Class[] classes = desc.getParamClasses();
        Object[] defaults = desc.getParamDefaults();
        for (int i = 0; i < numParams; ++i) {
            if (!(class$javax$media$jai$remote$Negotiable == null ? NegotiableCapability.class$("javax.media.jai.remote.Negotiable") : class$javax$media$jai$remote$Negotiable).isAssignableFrom(classes[i])) {
                throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability4"));
            }
            if (defaults[i] != ParameterListDescriptor.NO_PARAMETER_DEFAULT) continue;
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability5"));
        }
        this.category = category;
        this.capabilityName = capabilityName;
        this.generators = generators;
        this.isPreference = isPreference;
    }

    public String getCategory() {
        return this.category;
    }

    public String getCapabilityName() {
        return this.capabilityName;
    }

    public List getGenerators() {
        return this.generators;
    }

    public void setGenerators(List generators) {
        this.generators = generators;
    }

    public boolean isPreference() {
        return this.isPreference;
    }

    public Object getNegotiatedValue(String parameterName) {
        Negotiable value = (Negotiable)this.getObjectParameter(parameterName);
        if (value == null) {
            return null;
        }
        return value.getNegotiatedValue();
    }

    public NegotiableCapability negotiate(NegotiableCapability capability) {
        String[] otherNames;
        if (capability == null) {
            return null;
        }
        if (!capability.getCategory().equalsIgnoreCase(this.category) || !capability.getCapabilityName().equalsIgnoreCase(this.capabilityName)) {
            return null;
        }
        if (!this.areParameterListDescriptorsCompatible(capability)) {
            return null;
        }
        int negStatus = capability.isPreference() ? (this.isPreference ? 0 : 1) : (this.isPreference ? 2 : 3);
        ParameterListDescriptor pld = this.getParameterListDescriptor();
        ParameterListDescriptor otherPld = capability.getParameterListDescriptor();
        String[] thisNames = pld.getParamNames();
        if (thisNames == null) {
            thisNames = new String[]{};
        }
        if ((otherNames = otherPld.getParamNames()) == null) {
            otherNames = new String[]{};
        }
        Hashtable thisHash = this.hashNames(thisNames);
        Hashtable otherHash = this.hashNames(otherNames);
        Class[] thisClasses = pld.getParamClasses();
        Class[] otherClasses = otherPld.getParamClasses();
        Object[] thisDefaults = pld.getParamDefaults();
        Object[] otherDefaults = otherPld.getParamDefaults();
        NegotiableCapability result = null;
        ArrayList resultGenerators = new ArrayList();
        if (this.generators != null) {
            resultGenerators.addAll(this.generators);
        }
        if (capability.getGenerators() != null) {
            resultGenerators.addAll(capability.getGenerators());
        }
        switch (negStatus) {
            case 0: {
                String currParam;
                int i;
                int i2;
                String name;
                Vector commonNames = this.commonElements(thisHash, otherHash);
                Hashtable commonHash = this.hashNames(commonNames);
                Vector thisExtras = this.removeAll(thisHash, commonHash);
                Vector otherExtras = this.removeAll(otherHash, commonHash);
                int thisExtraLength = thisExtras.size();
                int otherExtraLength = otherExtras.size();
                Vector resultParams = new Vector(commonNames);
                resultParams.addAll(thisExtras);
                resultParams.addAll(otherExtras);
                int resultLength = resultParams.size();
                String[] resultNames = new String[resultLength];
                for (int i3 = 0; i3 < resultLength; ++i3) {
                    resultNames[i3] = (String)resultParams.elementAt(i3);
                }
                Class[] resultClasses = new Class[resultLength];
                Object[] resultDefaults = new Object[resultLength];
                Object[] resultValidValues = new Object[resultLength];
                for (int count = 0; count < commonNames.size(); ++count) {
                    name = (String)commonNames.elementAt(count);
                    resultClasses[count] = thisClasses[this.getIndex(thisHash, name)];
                    resultDefaults[count] = thisDefaults[this.getIndex(thisHash, name)];
                    resultValidValues[count] = pld.getParamValueRange(name);
                }
                for (i2 = 0; i2 < thisExtraLength; ++i2) {
                    name = (String)thisExtras.elementAt(i2);
                    resultClasses[count + i2] = thisClasses[this.getIndex(thisHash, name)];
                    resultDefaults[count + i2] = thisDefaults[this.getIndex(thisHash, name)];
                    resultValidValues[count + i2] = pld.getParamValueRange(name);
                }
                count += thisExtraLength;
                for (i2 = 0; i2 < otherExtraLength; ++i2) {
                    name = (String)otherExtras.elementAt(i2);
                    resultClasses[i2 + count] = otherClasses[this.getIndex(otherHash, name)];
                    resultDefaults[i2 + count] = otherDefaults[this.getIndex(otherHash, name)];
                    resultValidValues[i2 + count] = otherPld.getParamValueRange(name);
                }
                ParameterListDescriptorImpl resultPLD = new ParameterListDescriptorImpl(null, resultNames, resultClasses, resultDefaults, resultValidValues);
                result = new NegotiableCapability(this.category, this.capabilityName, resultGenerators, resultPLD, true);
                for (i = 0; i < commonNames.size(); ++i) {
                    currParam = (String)commonNames.elementAt(i);
                    Negotiable thisValue = (Negotiable)this.getObjectParameter(currParam);
                    Negotiable otherValue = (Negotiable)capability.getObjectParameter(currParam);
                    if (thisValue == null) {
                        result.setParameter(currParam, otherValue);
                        continue;
                    }
                    if (otherValue == null) {
                        result.setParameter(currParam, thisValue);
                        continue;
                    }
                    Negotiable resultValue = thisValue.negotiate(otherValue);
                    if (resultValue == null) {
                        return null;
                    }
                    result.setParameter(currParam, resultValue);
                }
                for (i = 0; i < thisExtraLength; ++i) {
                    currParam = (String)thisExtras.elementAt(i);
                    result.setParameter(currParam, (Negotiable)this.getObjectParameter(currParam));
                }
                for (i = 0; i < otherExtraLength; ++i) {
                    currParam = (String)otherExtras.elementAt(i);
                    result.setParameter(currParam, (Negotiable)capability.getObjectParameter(currParam));
                }
                break;
            }
            case 1: {
                Negotiable resultValue;
                String currParam;
                String name;
                int i;
                Vector commonNames = this.commonElements(thisHash, otherHash);
                Hashtable commonHash = this.hashNames(commonNames);
                Vector thisExtras = this.removeAll(thisHash, commonHash);
                Vector resultParams = new Vector(commonNames);
                resultParams.addAll(thisExtras);
                int resultLength = resultParams.size();
                String[] resultNames = new String[resultLength];
                for (i = 0; i < resultLength; ++i) {
                    resultNames[i] = (String)resultParams.elementAt(i);
                }
                Class[] resultClasses = new Class[resultLength];
                Object[] resultDefaults = new Object[resultLength];
                Object[] resultValidValues = new Object[resultLength];
                int count = 0;
                for (count = 0; count < commonNames.size(); ++count) {
                    name = (String)commonNames.elementAt(count);
                    resultClasses[count] = thisClasses[this.getIndex(thisHash, name)];
                    resultDefaults[count] = thisDefaults[this.getIndex(thisHash, name)];
                    resultValidValues[count] = pld.getParamValueRange(name);
                }
                for (i = 0; i < thisExtras.size(); ++i) {
                    name = (String)thisExtras.elementAt(i);
                    resultClasses[i + count] = thisClasses[this.getIndex(thisHash, name)];
                    resultDefaults[i + count] = thisDefaults[this.getIndex(thisHash, name)];
                    resultValidValues[i + count] = pld.getParamValueRange(name);
                }
                ParameterListDescriptorImpl resultPLD = new ParameterListDescriptorImpl(null, resultNames, resultClasses, resultDefaults, resultValidValues);
                result = new NegotiableCapability(this.category, this.capabilityName, resultGenerators, resultPLD, false);
                for (i = 0; i < commonNames.size(); ++i) {
                    currParam = (String)commonNames.elementAt(i);
                    Negotiable thisValue = (Negotiable)this.getObjectParameter(currParam);
                    Negotiable otherValue = (Negotiable)capability.getObjectParameter(currParam);
                    if (thisValue == null) {
                        return null;
                    }
                    if (otherValue == null) {
                        result.setParameter(currParam, thisValue);
                        continue;
                    }
                    resultValue = thisValue.negotiate(otherValue);
                    if (resultValue == null) {
                        return null;
                    }
                    result.setParameter(currParam, resultValue);
                }
                for (i = 0; i < thisExtras.size(); ++i) {
                    currParam = (String)thisExtras.elementAt(i);
                    resultValue = (Negotiable)this.getObjectParameter(currParam);
                    if (resultValue == null) {
                        return null;
                    }
                    result.setParameter(currParam, resultValue);
                }
                break;
            }
            case 2: {
                Negotiable resultValue;
                String currParam;
                String name;
                int i;
                Vector commonNames = this.commonElements(thisHash, otherHash);
                Hashtable commonHash = this.hashNames(commonNames);
                Vector otherExtras = this.removeAll(otherHash, commonHash);
                Vector resultParams = new Vector(commonNames);
                resultParams.addAll(otherExtras);
                int resultLength = resultParams.size();
                String[] resultNames = new String[resultLength];
                for (i = 0; i < resultLength; ++i) {
                    resultNames[i] = (String)resultParams.elementAt(i);
                }
                Class[] resultClasses = new Class[resultLength];
                Object[] resultDefaults = new Object[resultLength];
                Object[] resultValidValues = new Object[resultLength];
                int count = 0;
                for (count = 0; count < commonNames.size(); ++count) {
                    name = (String)commonNames.elementAt(count);
                    resultClasses[count] = thisClasses[this.getIndex(thisHash, name)];
                    resultDefaults[count] = thisDefaults[this.getIndex(thisHash, name)];
                    resultValidValues[count] = pld.getParamValueRange(name);
                }
                for (i = 0; i < otherExtras.size(); ++i) {
                    name = (String)otherExtras.elementAt(i);
                    resultClasses[i + count] = otherClasses[this.getIndex(otherHash, name)];
                    resultDefaults[i + count] = otherDefaults[this.getIndex(otherHash, name)];
                    resultValidValues[i + count] = otherPld.getParamValueRange(name);
                }
                ParameterListDescriptorImpl resultPLD = new ParameterListDescriptorImpl(null, resultNames, resultClasses, resultDefaults, resultValidValues);
                result = new NegotiableCapability(this.category, this.capabilityName, resultGenerators, resultPLD, false);
                for (i = 0; i < commonNames.size(); ++i) {
                    currParam = (String)commonNames.elementAt(i);
                    Negotiable thisValue = (Negotiable)this.getObjectParameter(currParam);
                    Negotiable otherValue = (Negotiable)capability.getObjectParameter(currParam);
                    if (otherValue == null) {
                        return null;
                    }
                    if (thisValue == null) {
                        result.setParameter(currParam, otherValue);
                        continue;
                    }
                    resultValue = otherValue.negotiate(thisValue);
                    if (resultValue == null) {
                        return null;
                    }
                    result.setParameter(currParam, resultValue);
                }
                for (i = 0; i < otherExtras.size(); ++i) {
                    currParam = (String)otherExtras.elementAt(i);
                    resultValue = (Negotiable)capability.getObjectParameter(currParam);
                    if (resultValue == null) {
                        return null;
                    }
                    result.setParameter(currParam, resultValue);
                }
                break;
            }
            case 3: {
                result = new NegotiableCapability(this.category, this.capabilityName, resultGenerators, pld, false);
                for (int i = 0; i < thisNames.length; ++i) {
                    String currParam = thisNames[i];
                    Negotiable thisValue = (Negotiable)this.getObjectParameter(currParam);
                    Negotiable otherValue = (Negotiable)capability.getObjectParameter(currParam);
                    if (thisValue == null || otherValue == null) {
                        return null;
                    }
                    Negotiable resultValue = thisValue.negotiate(otherValue);
                    if (resultValue == null) {
                        return null;
                    }
                    result.setParameter(currParam, resultValue);
                }
                break;
            }
        }
        return result;
    }

    public boolean areParameterListDescriptorsCompatible(NegotiableCapability other) {
        String[] otherNames;
        if (other == null) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability6"));
        }
        ParameterListDescriptor thisDesc = this.getParameterListDescriptor();
        ParameterListDescriptor otherDesc = other.getParameterListDescriptor();
        String[] thisNames = thisDesc.getParamNames();
        if (thisNames == null) {
            thisNames = new String[]{};
        }
        if ((otherNames = otherDesc.getParamNames()) == null) {
            otherNames = new String[]{};
        }
        Hashtable thisHash = this.hashNames(thisNames);
        Hashtable otherHash = this.hashNames(otherNames);
        if (!this.isPreference && !other.isPreference()) {
            if (thisDesc.getNumParameters() != otherDesc.getNumParameters()) {
                return false;
            }
            if (!this.containsAll(thisHash, otherHash)) {
                return false;
            }
            Class[] thisParamClasses = thisDesc.getParamClasses();
            Class[] otherParamClasses = otherDesc.getParamClasses();
            for (int i = 0; i < thisNames.length; ++i) {
                if (thisParamClasses[i] == otherParamClasses[this.getIndex(otherHash, thisNames[i])]) continue;
                return false;
            }
            return true;
        }
        Vector commonNames = this.commonElements(thisHash, otherHash);
        Class[] thisParamClasses = thisDesc.getParamClasses();
        Class[] otherParamClasses = otherDesc.getParamClasses();
        for (int i = 0; i < commonNames.size(); ++i) {
            String currName = (String)commonNames.elementAt(i);
            if (thisParamClasses[this.getIndex(thisHash, currName)] == otherParamClasses[this.getIndex(otherHash, currName)]) continue;
            return false;
        }
        return true;
    }

    private boolean containsAll(Hashtable thisHash, Hashtable otherHash) {
        Enumeration i = thisHash.keys();
        while (i.hasMoreElements()) {
            CaselessStringKey thisNameKey = (CaselessStringKey)i.nextElement();
            if (otherHash.containsKey(thisNameKey)) continue;
            return false;
        }
        return true;
    }

    private Vector removeAll(Hashtable thisHash, Hashtable otherHash) {
        Vector<String> v = new Vector<String>();
        Enumeration i = thisHash.keys();
        while (i.hasMoreElements()) {
            CaselessStringKey thisNameKey = (CaselessStringKey)i.nextElement();
            if (otherHash.containsKey(thisNameKey)) continue;
            v.add(thisNameKey.toString());
        }
        return v;
    }

    private int getIndex(Hashtable h, String s) {
        return (Integer)h.get(new CaselessStringKey(s));
    }

    private Vector commonElements(Hashtable thisHash, Hashtable otherHash) {
        Vector<String> v = new Vector<String>();
        Enumeration i = thisHash.keys();
        while (i.hasMoreElements()) {
            CaselessStringKey thisNameKey = (CaselessStringKey)i.nextElement();
            if (!otherHash.containsKey(thisNameKey)) continue;
            v.add(thisNameKey.toString());
        }
        return v;
    }

    private Hashtable hashNames(String[] paramNames) {
        Hashtable<CaselessStringKey, Integer> h = new Hashtable<CaselessStringKey, Integer>();
        if (paramNames != null) {
            for (int i = 0; i < paramNames.length; ++i) {
                h.put(new CaselessStringKey(paramNames[i]), new Integer(i));
            }
        }
        return h;
    }

    private Hashtable hashNames(Vector paramNames) {
        Hashtable<CaselessStringKey, Integer> h = new Hashtable<CaselessStringKey, Integer>();
        if (paramNames != null) {
            for (int i = 0; i < paramNames.size(); ++i) {
                h.put(new CaselessStringKey((String)paramNames.elementAt(i)), new Integer(i));
            }
        }
        return h;
    }

    public ParameterList setParameter(String paramName, byte b) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
    }

    public ParameterList setParameter(String paramName, boolean b) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
    }

    public ParameterList setParameter(String paramName, char c) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
    }

    public ParameterList setParameter(String paramName, short s) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
    }

    public ParameterList setParameter(String paramName, int i) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
    }

    public ParameterList setParameter(String paramName, long l) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
    }

    public ParameterList setParameter(String paramName, float f) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
    }

    public ParameterList setParameter(String paramName, double d) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
    }

    public ParameterList setParameter(String paramName, Object obj) {
        if (obj != null && !(obj instanceof Negotiable)) {
            throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability2"));
        }
        super.setParameter(paramName, obj);
        return this;
    }

    public byte getByteParameter(String paramName) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability3"));
    }

    public boolean getBooleanParameter(String paramName) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability3"));
    }

    public char getCharParameter(String paramName) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability3"));
    }

    public short getShortParameter(String paramName) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability3"));
    }

    public int getIntParameter(String paramName) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability3"));
    }

    public long getLongParameter(String paramName) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability3"));
    }

    public float getFloatParameter(String paramName) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability3"));
    }

    public double getDoubleParameter(String paramName) {
        throw new IllegalArgumentException(JaiI18N.getString("NegotiableCapability3"));
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

