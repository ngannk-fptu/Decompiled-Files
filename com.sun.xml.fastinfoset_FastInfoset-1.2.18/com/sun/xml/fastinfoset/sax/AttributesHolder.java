/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.sax;

import com.sun.xml.fastinfoset.CommonResourceBundle;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.algorithm.BuiltInEncodingAlgorithmFactory;
import java.io.IOException;
import java.util.Map;
import org.jvnet.fastinfoset.EncodingAlgorithm;
import org.jvnet.fastinfoset.EncodingAlgorithmException;
import org.jvnet.fastinfoset.FastInfosetException;
import org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;

public class AttributesHolder
implements EncodingAlgorithmAttributes {
    private static final int DEFAULT_CAPACITY = 8;
    private Map _registeredEncodingAlgorithms;
    private int _attributeCount;
    private QualifiedName[] _names = new QualifiedName[8];
    private String[] _values = new String[8];
    private String[] _algorithmURIs = new String[8];
    private int[] _algorithmIds = new int[8];
    private Object[] _algorithmData = new Object[8];

    public AttributesHolder() {
    }

    public AttributesHolder(Map registeredEncodingAlgorithms) {
        this();
        this._registeredEncodingAlgorithms = registeredEncodingAlgorithms;
    }

    @Override
    public final int getLength() {
        return this._attributeCount;
    }

    @Override
    public final String getLocalName(int index) {
        return this._names[index].localName;
    }

    @Override
    public final String getQName(int index) {
        return this._names[index].getQNameString();
    }

    @Override
    public final String getType(int index) {
        return "CDATA";
    }

    @Override
    public final String getURI(int index) {
        return this._names[index].namespaceName;
    }

    @Override
    public final String getValue(int index) {
        String value = this._values[index];
        if (value != null) {
            return value;
        }
        if (this._algorithmData[index] == null || this._algorithmIds[index] >= 32 && this._registeredEncodingAlgorithms == null) {
            return null;
        }
        try {
            this._values[index] = this.convertEncodingAlgorithmDataToString(this._algorithmIds[index], this._algorithmURIs[index], this._algorithmData[index]).toString();
            return this._values[index];
        }
        catch (IOException e) {
            return null;
        }
        catch (FastInfosetException e) {
            return null;
        }
    }

    @Override
    public final int getIndex(String qName) {
        int i = qName.indexOf(58);
        String prefix = "";
        String localName = qName;
        if (i >= 0) {
            prefix = qName.substring(0, i);
            localName = qName.substring(i + 1);
        }
        for (i = 0; i < this._attributeCount; ++i) {
            QualifiedName name = this._names[i];
            if (!localName.equals(name.localName) || !prefix.equals(name.prefix)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public final String getType(String qName) {
        int index = this.getIndex(qName);
        if (index >= 0) {
            return "CDATA";
        }
        return null;
    }

    @Override
    public final String getValue(String qName) {
        int index = this.getIndex(qName);
        if (index >= 0) {
            return this._values[index];
        }
        return null;
    }

    @Override
    public final int getIndex(String uri, String localName) {
        for (int i = 0; i < this._attributeCount; ++i) {
            QualifiedName name = this._names[i];
            if (!localName.equals(name.localName) || !uri.equals(name.namespaceName)) continue;
            return i;
        }
        return -1;
    }

    @Override
    public final String getType(String uri, String localName) {
        int index = this.getIndex(uri, localName);
        if (index >= 0) {
            return "CDATA";
        }
        return null;
    }

    @Override
    public final String getValue(String uri, String localName) {
        int index = this.getIndex(uri, localName);
        if (index >= 0) {
            return this._values[index];
        }
        return null;
    }

    public final void clear() {
        for (int i = 0; i < this._attributeCount; ++i) {
            this._values[i] = null;
            this._algorithmData[i] = null;
        }
        this._attributeCount = 0;
    }

    @Override
    public final String getAlgorithmURI(int index) {
        return this._algorithmURIs[index];
    }

    @Override
    public final int getAlgorithmIndex(int index) {
        return this._algorithmIds[index];
    }

    @Override
    public final Object getAlgorithmData(int index) {
        return this._algorithmData[index];
    }

    @Override
    public String getAlpababet(int index) {
        return null;
    }

    @Override
    public boolean getToIndex(int index) {
        return false;
    }

    public final void addAttribute(QualifiedName name, String value) {
        if (this._attributeCount == this._names.length) {
            this.resize();
        }
        this._names[this._attributeCount] = name;
        this._values[this._attributeCount++] = value;
    }

    public final void addAttributeWithAlgorithmData(QualifiedName name, String URI2, int id, Object data) {
        if (this._attributeCount == this._names.length) {
            this.resize();
        }
        this._names[this._attributeCount] = name;
        this._values[this._attributeCount] = null;
        this._algorithmURIs[this._attributeCount] = URI2;
        this._algorithmIds[this._attributeCount] = id;
        this._algorithmData[this._attributeCount++] = data;
    }

    public final QualifiedName getQualifiedName(int index) {
        return this._names[index];
    }

    public final String getPrefix(int index) {
        return this._names[index].prefix;
    }

    private final void resize() {
        int newLength = this._attributeCount * 3 / 2 + 1;
        QualifiedName[] names = new QualifiedName[newLength];
        String[] values = new String[newLength];
        String[] algorithmURIs = new String[newLength];
        int[] algorithmIds = new int[newLength];
        Object[] algorithmData = new Object[newLength];
        System.arraycopy(this._names, 0, names, 0, this._attributeCount);
        System.arraycopy(this._values, 0, values, 0, this._attributeCount);
        System.arraycopy(this._algorithmURIs, 0, algorithmURIs, 0, this._attributeCount);
        System.arraycopy(this._algorithmIds, 0, algorithmIds, 0, this._attributeCount);
        System.arraycopy(this._algorithmData, 0, algorithmData, 0, this._attributeCount);
        this._names = names;
        this._values = values;
        this._algorithmURIs = algorithmURIs;
        this._algorithmIds = algorithmIds;
        this._algorithmData = algorithmData;
    }

    private final StringBuffer convertEncodingAlgorithmDataToString(int identifier, String URI2, Object data) throws FastInfosetException, IOException {
        EncodingAlgorithm ea = null;
        if (identifier < 9) {
            ea = BuiltInEncodingAlgorithmFactory.getAlgorithm(identifier);
        } else {
            if (identifier == 9) {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.CDATAAlgorithmNotSupported"));
            }
            if (identifier >= 32) {
                if (URI2 == null) {
                    throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.URINotPresent") + identifier);
                }
                ea = (EncodingAlgorithm)this._registeredEncodingAlgorithms.get(URI2);
                if (ea == null) {
                    throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.algorithmNotRegistered") + URI2);
                }
            } else {
                throw new EncodingAlgorithmException(CommonResourceBundle.getInstance().getString("message.identifiers10to31Reserved"));
            }
        }
        StringBuffer sb = new StringBuffer();
        ea.convertToCharacters(data, sb);
        return sb;
    }
}

