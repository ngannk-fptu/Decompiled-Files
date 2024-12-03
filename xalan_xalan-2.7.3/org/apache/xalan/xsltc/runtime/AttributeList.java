/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.runtime;

import java.util.Vector;
import org.apache.xalan.xsltc.runtime.Hashtable;
import org.xml.sax.Attributes;

public class AttributeList
implements Attributes {
    private static final String EMPTYSTRING = "";
    private static final String CDATASTRING = "CDATA";
    private Hashtable _attributes;
    private Vector _names;
    private Vector _qnames;
    private Vector _values;
    private Vector _uris;
    private int _length = 0;

    public AttributeList() {
    }

    public AttributeList(Attributes attributes) {
        this();
        if (attributes != null) {
            int count = attributes.getLength();
            for (int i = 0; i < count; ++i) {
                this.add(attributes.getQName(i), attributes.getValue(i));
            }
        }
    }

    private void alloc() {
        this._attributes = new Hashtable();
        this._names = new Vector();
        this._values = new Vector();
        this._qnames = new Vector();
        this._uris = new Vector();
    }

    @Override
    public int getLength() {
        return this._length;
    }

    @Override
    public String getURI(int index) {
        if (index < this._length) {
            return (String)this._uris.elementAt(index);
        }
        return null;
    }

    @Override
    public String getLocalName(int index) {
        if (index < this._length) {
            return (String)this._names.elementAt(index);
        }
        return null;
    }

    @Override
    public String getQName(int pos) {
        if (pos < this._length) {
            return (String)this._qnames.elementAt(pos);
        }
        return null;
    }

    @Override
    public String getType(int index) {
        return CDATASTRING;
    }

    @Override
    public int getIndex(String namespaceURI, String localPart) {
        return -1;
    }

    @Override
    public int getIndex(String qname) {
        return -1;
    }

    @Override
    public String getType(String uri, String localName) {
        return CDATASTRING;
    }

    @Override
    public String getType(String qname) {
        return CDATASTRING;
    }

    @Override
    public String getValue(int pos) {
        if (pos < this._length) {
            return (String)this._values.elementAt(pos);
        }
        return null;
    }

    @Override
    public String getValue(String qname) {
        if (this._attributes != null) {
            Integer obj = (Integer)this._attributes.get(qname);
            if (obj == null) {
                return null;
            }
            return this.getValue(obj);
        }
        return null;
    }

    @Override
    public String getValue(String uri, String localName) {
        return this.getValue(uri + ':' + localName);
    }

    public void add(String qname, String value) {
        Integer obj;
        if (this._attributes == null) {
            this.alloc();
        }
        if ((obj = (Integer)this._attributes.get(qname)) == null) {
            obj = new Integer(this._length++);
            this._attributes.put(qname, obj);
            this._qnames.addElement(qname);
            this._values.addElement(value);
            int col = qname.lastIndexOf(58);
            if (col > -1) {
                this._uris.addElement(qname.substring(0, col));
                this._names.addElement(qname.substring(col + 1));
            } else {
                this._uris.addElement(EMPTYSTRING);
                this._names.addElement(qname);
            }
        } else {
            int index = obj;
            this._values.set(index, value);
        }
    }

    public void clear() {
        this._length = 0;
        if (this._attributes != null) {
            this._attributes.clear();
            this._names.removeAllElements();
            this._values.removeAllElements();
            this._qnames.removeAllElements();
            this._uris.removeAllElements();
        }
    }
}

