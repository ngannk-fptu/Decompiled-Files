/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.mapped;

import java.io.IOException;
import java.io.Writer;
import java.util.Stack;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import org.codehaus.jettison.AbstractXMLStreamWriter;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;

public class MappedXMLStreamWriter
extends AbstractXMLStreamWriter {
    private static final String MIXED_CONTENT_VALUE_KEY = "$";
    private MappedNamespaceConvention convention;
    protected Writer writer;
    private NamespaceContext namespaceContext;
    private String valueKey = "$";
    private Stack<JSONProperty> stack = new Stack();
    private JSONProperty current;

    public MappedXMLStreamWriter(MappedNamespaceConvention convention, Writer writer) {
        this.convention = convention;
        this.writer = writer;
        this.namespaceContext = convention;
    }

    private String getPropertyArrayKey(JSONProperty property) {
        return this.isArrayKeysWithSlashAvailable() ? property.getTreeKey() : property.getKey();
    }

    @Override
    public NamespaceContext getNamespaceContext() {
        return this.namespaceContext;
    }

    @Override
    public void setNamespaceContext(NamespaceContext context) throws XMLStreamException {
        this.namespaceContext = context;
    }

    public String getTextKey() {
        return this.valueKey;
    }

    public void setValueKey(String valueKey) {
        this.valueKey = valueKey;
    }

    @Override
    public void writeStartDocument() throws XMLStreamException {
        this.current = new JSONPropertyObject(null, null, new JSONObject(this.convention.isDropRootElement(), this.convention.getIgnoredElements(), this.convention.isWriteNullAsString(), this.convention.isEscapeForwardSlashAlways()));
        this.stack.clear();
    }

    @Override
    public void writeStartElement(String prefix, String local, String ns) throws XMLStreamException {
        if (this.current == null) {
            this.writeStartDocument();
        }
        String parentKey = this.current.getTreeKey();
        this.stack.push(this.current);
        String key = this.convention.createKey(prefix, ns, local);
        this.current = new JSONPropertyString(key, parentKey);
    }

    @Override
    public void writeAttribute(String prefix, String ns, String local, String value) throws XMLStreamException {
        String key = this.convention.isElement(prefix, ns, local) ? this.convention.createKey(prefix, ns, local) : this.convention.createAttributeKey(prefix, ns, local);
        JSONPropertyString prop = new JSONPropertyString(key, null);
        prop.addText(value);
        this.current = this.current.withProperty(prop, false);
    }

    @Override
    public void writeAttribute(String ns, String local, String value) throws XMLStreamException {
        this.writeAttribute(null, ns, local, value);
    }

    @Override
    public void writeAttribute(String local, String value) throws XMLStreamException {
        this.writeAttribute(null, local, value);
    }

    @Override
    public void writeCharacters(String text) throws XMLStreamException {
        this.current.addText(text);
    }

    @Override
    public void writeEndElement() throws XMLStreamException {
        if (this.stack.isEmpty()) {
            throw new XMLStreamException("Too many closing tags.");
        }
        this.current = this.stack.pop().withProperty(this.current);
    }

    @Override
    public void writeEndDocument() throws XMLStreamException {
        if (!this.stack.isEmpty()) {
            throw new XMLStreamException("Missing some closing tags.");
        }
        this.writeJSONObject((JSONObject)this.current.getValue());
        try {
            this.writer.flush();
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    protected void writeJSONObject(JSONObject root) throws XMLStreamException {
        try {
            if (root == null) {
                this.writer.write("null");
            } else {
                root.write(this.writer);
            }
        }
        catch (JSONException e) {
            throw new XMLStreamException(e);
        }
        catch (IOException e) {
            throw new XMLStreamException(e);
        }
    }

    @Override
    public void close() throws XMLStreamException {
    }

    @Override
    public void flush() throws XMLStreamException {
    }

    @Override
    public String getPrefix(String arg0) throws XMLStreamException {
        return null;
    }

    @Override
    public Object getProperty(String arg0) throws IllegalArgumentException {
        return null;
    }

    @Override
    public void setDefaultNamespace(String arg0) throws XMLStreamException {
    }

    @Override
    public void setPrefix(String arg0, String arg1) throws XMLStreamException {
    }

    @Override
    public void writeDefaultNamespace(String arg0) throws XMLStreamException {
    }

    @Override
    public void writeEntityRef(String arg0) throws XMLStreamException {
    }

    @Override
    public void writeNamespace(String arg0, String arg1) throws XMLStreamException {
    }

    @Override
    public void writeProcessingInstruction(String arg0) throws XMLStreamException {
    }

    @Override
    public void writeProcessingInstruction(String arg0, String arg1) throws XMLStreamException {
    }

    public MappedNamespaceConvention getConvention() {
        return this.convention;
    }

    private final class JSONPropertyObject
    extends JSONProperty {
        private JSONObject object;

        public JSONPropertyObject(String key, String parentKey, JSONObject object) {
            super(key, parentKey);
            this.object = object;
        }

        @Override
        public Object getValue() {
            return this.object;
        }

        @Override
        public void addText(String text) {
            if (MappedXMLStreamWriter.MIXED_CONTENT_VALUE_KEY == MappedXMLStreamWriter.this.valueKey && (text = text.trim()).length() == 0) {
                return;
            }
            try {
                text = this.object.getString(MappedXMLStreamWriter.this.valueKey) + text;
            }
            catch (JSONException jSONException) {
                // empty catch block
            }
            try {
                if (MappedXMLStreamWriter.this.valueKey != null) {
                    this.object.put(MappedXMLStreamWriter.this.valueKey, text);
                }
            }
            catch (JSONException e) {
                throw new AssertionError((Object)e);
            }
        }

        @Override
        public JSONPropertyObject withProperty(JSONProperty property, boolean add) {
            Object value = property.getValue();
            if (value instanceof String && !((String)value).isEmpty()) {
                value = MappedXMLStreamWriter.this.convention.convertToJSONPrimitive((String)value);
            }
            Object old = this.object.opt(property.getKey());
            try {
                if (old != null) {
                    JSONArray values;
                    if (old instanceof JSONArray) {
                        values = (JSONArray)old;
                    } else {
                        values = new JSONArray();
                        values.put(old);
                    }
                    values.put(value);
                    this.object.put(property.getKey(), values);
                } else if (MappedXMLStreamWriter.this.getSerializedAsArrays().contains(MappedXMLStreamWriter.this.getPropertyArrayKey(property))) {
                    boolean emptyString;
                    JSONArray values = new JSONArray();
                    boolean bl = emptyString = value instanceof String && ((String)value).isEmpty();
                    if (!MappedXMLStreamWriter.this.convention.isIgnoreEmptyArrayValues() || !emptyString && value != null) {
                        values.put(value);
                    }
                    this.object.put(property.getKey(), values);
                } else {
                    this.object.put(property.getKey(), value);
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            return this;
        }
    }

    private final class JSONPropertyString
    extends JSONProperty {
        private StringBuilder object;

        public JSONPropertyString(String key, String parentKey) {
            super(key, parentKey);
            this.object = new StringBuilder();
        }

        @Override
        public Object getValue() {
            return this.object.toString();
        }

        @Override
        public void addText(String text) {
            this.object.append(text);
        }

        @Override
        public JSONPropertyObject withProperty(JSONProperty property, boolean add) {
            JSONObject jo = new JSONObject(false, MappedXMLStreamWriter.this.convention.getIgnoredElements(), MappedXMLStreamWriter.this.convention.isWriteNullAsString(), MappedXMLStreamWriter.this.convention.isEscapeForwardSlashAlways());
            try {
                Object value;
                boolean emptyString;
                String strValue = this.getValue().toString();
                if (MappedXMLStreamWriter.MIXED_CONTENT_VALUE_KEY == MappedXMLStreamWriter.this.valueKey) {
                    strValue = strValue.trim();
                }
                if (strValue.length() > 0) {
                    jo.put(MappedXMLStreamWriter.this.valueKey, strValue);
                }
                boolean bl = emptyString = (value = property.getValue()) instanceof String && ((String)value).isEmpty();
                if (value instanceof String && !emptyString) {
                    value = MappedXMLStreamWriter.this.convention.convertToJSONPrimitive((String)value);
                }
                if (MappedXMLStreamWriter.this.getSerializedAsArrays().contains(MappedXMLStreamWriter.this.getPropertyArrayKey(property))) {
                    JSONArray values = new JSONArray();
                    if (!MappedXMLStreamWriter.this.convention.isIgnoreEmptyArrayValues() || !emptyString && value != null) {
                        values.put(value);
                    }
                    value = values;
                }
                jo.put(property.getKey(), value);
            }
            catch (JSONException e) {
                throw new AssertionError((Object)e);
            }
            return new JSONPropertyObject(this.getKey(), this.getParentKey(), jo);
        }
    }

    private abstract class JSONProperty {
        private String key;
        private String parentKey;

        public JSONProperty(String key, String parentKey) {
            this.key = key;
            this.parentKey = parentKey;
        }

        public String getKey() {
            return this.key;
        }

        public String getParentKey() {
            return this.parentKey;
        }

        public String getTreeKey() {
            return this.parentKey == null ? this.key : this.parentKey + "/" + this.key;
        }

        public abstract Object getValue();

        public abstract void addText(String var1);

        public abstract JSONPropertyObject withProperty(JSONProperty var1, boolean var2);

        public JSONPropertyObject withProperty(JSONProperty property) {
            return this.withProperty(property, true);
        }
    }
}

