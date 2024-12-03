/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf;

import com.lowagie.text.error_messages.MessageLocalization;
import com.lowagie.text.pdf.FieldReader;
import com.lowagie.text.xml.simpleparser.SimpleXMLDocHandler;
import com.lowagie.text.xml.simpleparser.SimpleXMLParser;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class XfdfReader
implements SimpleXMLDocHandler,
FieldReader {
    private boolean foundRoot = false;
    private Stack<String> fieldNames = new Stack();
    private Stack<String> fieldValues = new Stack();
    private Map<String, String> fields;
    private Map<String, List<String>> listFields;
    private String fileSpec;

    public XfdfReader(String filename) throws IOException {
        try (FileInputStream fin = new FileInputStream(filename);){
            SimpleXMLParser.parse((SimpleXMLDocHandler)this, fin);
        }
    }

    public XfdfReader(byte[] xfdfIn) throws IOException {
        SimpleXMLParser.parse((SimpleXMLDocHandler)this, new ByteArrayInputStream(xfdfIn));
    }

    @Override
    @Deprecated
    public HashMap<String, String> getFields() {
        return (HashMap)this.fields;
    }

    @Override
    public Map<String, String> getAllFields() {
        return this.fields;
    }

    public String getField(String name) {
        return this.fields.get(name);
    }

    @Override
    public String getFieldValue(String name) {
        return this.fields.get(name);
    }

    @Override
    public List<String> getListValues(String name) {
        return this.listFields.get(name);
    }

    public String getFileSpec() {
        return this.fileSpec;
    }

    @Override
    @Deprecated
    public void startElement(String tag, HashMap h) {
        this.startElement(tag, (Map<String, String>)h);
    }

    @Override
    public void startElement(String tag, Map<String, String> h) {
        if (!this.foundRoot) {
            if (!tag.equals("xfdf")) {
                throw new RuntimeException(MessageLocalization.getComposedMessage("root.element.is.not.xfdf.1", tag));
            }
            this.foundRoot = true;
        }
        switch (tag) {
            case "xfdf": {
                break;
            }
            case "f": {
                this.fileSpec = h.get("href");
                break;
            }
            case "fields": {
                this.fields = new HashMap<String, String>();
                this.listFields = new HashMap<String, List<String>>();
                break;
            }
            case "field": {
                String fName = h.get("name");
                this.fieldNames.push(fName);
                break;
            }
            case "value": {
                this.fieldValues.push("");
            }
        }
    }

    @Override
    public void endElement(String tag) {
        if (tag.equals("value")) {
            StringBuilder fName = new StringBuilder();
            for (int k = 0; k < this.fieldNames.size(); ++k) {
                fName.append(".").append((String)this.fieldNames.elementAt(k));
            }
            if (fName.toString().startsWith(".")) {
                fName = new StringBuilder(fName.substring(1));
            }
            String fVal = this.fieldValues.pop();
            String old = this.fields.put(fName.toString(), fVal);
            if (old != null) {
                List<String> l = this.listFields.get(fName.toString());
                if (l == null) {
                    l = new ArrayList<String>();
                    l.add(old);
                }
                l.add(fVal);
                this.listFields.put(fName.toString(), l);
            }
        } else if (tag.equals("field") && !this.fieldNames.isEmpty()) {
            this.fieldNames.pop();
        }
    }

    @Override
    public void startDocument() {
        this.fileSpec = "";
    }

    @Override
    public void endDocument() {
    }

    @Override
    public void text(String str) {
        if (this.fieldNames.isEmpty() || this.fieldValues.isEmpty()) {
            return;
        }
        String val = this.fieldValues.pop();
        val = val + str;
        this.fieldValues.push(val);
    }
}

