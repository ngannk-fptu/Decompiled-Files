/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.jdom2.Content;
import org.jdom2.IllegalDataException;
import org.jdom2.IllegalTargetException;
import org.jdom2.Parent;
import org.jdom2.Verifier;
import org.jdom2.output.XMLOutputter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ProcessingInstruction
extends Content {
    private static final long serialVersionUID = 200L;
    protected String target;
    protected String rawData;
    protected transient Map<String, String> mapData = null;

    protected ProcessingInstruction() {
        super(Content.CType.ProcessingInstruction);
    }

    public ProcessingInstruction(String target) {
        this(target, "");
    }

    public ProcessingInstruction(String target, Map<String, String> data) {
        super(Content.CType.ProcessingInstruction);
        this.setTarget(target);
        this.setData(data);
    }

    public ProcessingInstruction(String target, String data) {
        super(Content.CType.ProcessingInstruction);
        this.setTarget(target);
        this.setData(data);
    }

    public ProcessingInstruction setTarget(String newTarget) {
        String reason = Verifier.checkProcessingInstructionTarget(newTarget);
        if (reason != null) {
            throw new IllegalTargetException(newTarget, reason);
        }
        this.target = newTarget;
        return this;
    }

    @Override
    public String getValue() {
        return this.rawData;
    }

    public String getTarget() {
        return this.target;
    }

    public String getData() {
        return this.rawData;
    }

    public List<String> getPseudoAttributeNames() {
        return new ArrayList<String>(this.mapData.keySet());
    }

    public ProcessingInstruction setData(String data) {
        String reason = Verifier.checkProcessingInstructionData(data);
        if (reason != null) {
            throw new IllegalDataException(data, reason);
        }
        this.rawData = data;
        this.mapData = this.parseData(data);
        return this;
    }

    public ProcessingInstruction setData(Map<String, String> data) {
        String temp = ProcessingInstruction.toString(data);
        String reason = Verifier.checkProcessingInstructionData(temp);
        if (reason != null) {
            throw new IllegalDataException(temp, reason);
        }
        this.rawData = temp;
        this.mapData = new LinkedHashMap<String, String>(data);
        return this;
    }

    public String getPseudoAttributeValue(String name) {
        return this.mapData.get(name);
    }

    public ProcessingInstruction setPseudoAttribute(String name, String value) {
        String reason = Verifier.checkProcessingInstructionData(name);
        if (reason != null) {
            throw new IllegalDataException(name, reason);
        }
        reason = Verifier.checkProcessingInstructionData(value);
        if (reason != null) {
            throw new IllegalDataException(value, reason);
        }
        this.mapData.put(name, value);
        this.rawData = ProcessingInstruction.toString(this.mapData);
        return this;
    }

    public boolean removePseudoAttribute(String name) {
        if (this.mapData.remove(name) != null) {
            this.rawData = ProcessingInstruction.toString(this.mapData);
            return true;
        }
        return false;
    }

    private static final String toString(Map<String, String> pmapData) {
        StringBuilder stringData = new StringBuilder();
        for (Map.Entry<String, String> me : pmapData.entrySet()) {
            stringData.append(me.getKey()).append("=\"").append(me.getValue()).append("\" ");
        }
        if (stringData.length() > 0) {
            stringData.setLength(stringData.length() - 1);
        }
        return stringData.toString();
    }

    private Map<String, String> parseData(String prawData) {
        LinkedHashMap<String, String> data = new LinkedHashMap<String, String>();
        String inputData = prawData.trim();
        while (!inputData.trim().equals("")) {
            int pos;
            String name = "";
            String value = "";
            int startName = 0;
            char previousChar = inputData.charAt(startName);
            for (pos = 1; pos < inputData.length(); ++pos) {
                char currentChar = inputData.charAt(pos);
                if (currentChar == '=') {
                    name = inputData.substring(startName, pos).trim();
                    int[] bounds = ProcessingInstruction.extractQuotedString(inputData.substring(pos + 1));
                    if (bounds == null) {
                        return Collections.emptyMap();
                    }
                    value = inputData.substring(bounds[0] + pos + 1, bounds[1] + pos + 1);
                    pos += bounds[1] + 1;
                    break;
                }
                if (Character.isWhitespace(previousChar) && !Character.isWhitespace(currentChar)) {
                    startName = pos;
                }
                previousChar = currentChar;
            }
            inputData = inputData.substring(pos);
            if (name.length() <= 0) continue;
            data.put(name, value);
        }
        return data;
    }

    private static int[] extractQuotedString(String rawData) {
        boolean inQuotes = false;
        char quoteChar = '\"';
        int start = 0;
        for (int pos = 0; pos < rawData.length(); ++pos) {
            char currentChar = rawData.charAt(pos);
            if (currentChar != '\"' && currentChar != '\'') continue;
            if (!inQuotes) {
                quoteChar = currentChar;
                inQuotes = true;
                start = pos + 1;
                continue;
            }
            if (quoteChar != currentChar) continue;
            inQuotes = false;
            return new int[]{start, pos};
        }
        return null;
    }

    public String toString() {
        return "[ProcessingInstruction: " + new XMLOutputter().outputString(this) + "]";
    }

    @Override
    public ProcessingInstruction clone() {
        ProcessingInstruction pi = (ProcessingInstruction)super.clone();
        pi.mapData = this.parseData(this.rawData);
        return pi;
    }

    @Override
    public ProcessingInstruction detach() {
        return (ProcessingInstruction)super.detach();
    }

    @Override
    protected ProcessingInstruction setParent(Parent parent) {
        return (ProcessingInstruction)super.setParent(parent);
    }
}

