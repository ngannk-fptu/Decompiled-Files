/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jettison.mapped;

import java.util.Map;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.codehaus.jettison.AbstractXMLInputFactory;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONTokener;
import org.codehaus.jettison.mapped.Configuration;
import org.codehaus.jettison.mapped.MappedNamespaceConvention;
import org.codehaus.jettison.mapped.MappedXMLStreamReader;

public class MappedXMLInputFactory
extends AbstractXMLInputFactory {
    private MappedNamespaceConvention convention;

    public MappedXMLInputFactory(Map nstojns) {
        this(new Configuration(nstojns));
    }

    public MappedXMLInputFactory(Configuration config) {
        this.convention = new MappedNamespaceConvention(config);
    }

    @Override
    public XMLStreamReader createXMLStreamReader(JSONTokener tokener) throws XMLStreamException {
        try {
            JSONObject root = this.createJSONObject(tokener);
            return new MappedXMLStreamReader(root, this.convention);
        }
        catch (JSONException e) {
            int column = e.getColumn();
            if (column == -1) {
                throw new XMLStreamException(e);
            }
            throw new XMLStreamException(e.getMessage(), new ErrorLocation(e.getLine(), e.getColumn()), e);
        }
    }

    protected JSONObject createJSONObject(JSONTokener tokener) throws JSONException {
        return new JSONObject(tokener);
    }

    private static class ErrorLocation
    implements Location {
        private int line = -1;
        private int column = -1;

        public ErrorLocation(int line, int column) {
            this.line = line;
            this.column = column;
        }

        @Override
        public int getCharacterOffset() {
            return 0;
        }

        @Override
        public int getColumnNumber() {
            return this.column;
        }

        @Override
        public int getLineNumber() {
            return this.line;
        }

        @Override
        public String getPublicId() {
            return null;
        }

        @Override
        public String getSystemId() {
            return null;
        }
    }
}

