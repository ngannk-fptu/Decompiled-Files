/*
 * Decompiled with CFR 0.152.
 */
package org.jdom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jdom.Content;
import org.jdom.IllegalDataException;
import org.jdom.IllegalTargetException;
import org.jdom.Verifier;
import org.jdom.output.XMLOutputter;

public class ProcessingInstruction
extends Content {
    private static final String CVS_ID = "@(#) $RCSfile: ProcessingInstruction.java,v $ $Revision: 1.47 $ $Date: 2007/11/10 05:28:59 $ $Name:  $";
    protected String target;
    protected String rawData;
    protected Map mapData;

    protected ProcessingInstruction() {
    }

    public ProcessingInstruction(String target, Map data) {
        this.setTarget(target);
        this.setData(data);
    }

    public ProcessingInstruction(String target, String data) {
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

    public List getPseudoAttributeNames() {
        Set mapDataSet = this.mapData.entrySet();
        ArrayList<String> nameList = new ArrayList<String>();
        Iterator i = mapDataSet.iterator();
        while (i.hasNext()) {
            String wholeSet = i.next().toString();
            String attrName = wholeSet.substring(0, wholeSet.indexOf("="));
            nameList.add(attrName);
        }
        return nameList;
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

    public ProcessingInstruction setData(Map data) {
        String temp = this.toString(data);
        String reason = Verifier.checkProcessingInstructionData(temp);
        if (reason != null) {
            throw new IllegalDataException(temp, reason);
        }
        this.rawData = temp;
        this.mapData = new HashMap(data);
        return this;
    }

    public String getPseudoAttributeValue(String name) {
        return (String)this.mapData.get(name);
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
        this.rawData = this.toString(this.mapData);
        return this;
    }

    public boolean removePseudoAttribute(String name) {
        if (this.mapData.remove(name) != null) {
            this.rawData = this.toString(this.mapData);
            return true;
        }
        return false;
    }

    private String toString(Map mapData) {
        StringBuffer rawData = new StringBuffer();
        for (String name : mapData.keySet()) {
            String value = (String)mapData.get(name);
            rawData.append(name).append("=\"").append(value).append("\" ");
        }
        if (rawData.length() > 0) {
            rawData.setLength(rawData.length() - 1);
        }
        return rawData.toString();
    }

    private Map parseData(String rawData) {
        HashMap<String, String> data = new HashMap<String, String>();
        String inputData = rawData.trim();
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
                        return new HashMap();
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
            if (name.length() <= 0 || value == null) continue;
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
        return new StringBuffer().append("[ProcessingInstruction: ").append(new XMLOutputter().outputString(this)).append("]").toString();
    }

    @Override
    public Object clone() {
        ProcessingInstruction pi = (ProcessingInstruction)super.clone();
        if (this.mapData != null) {
            pi.mapData = this.parseData(this.rawData);
        }
        return pi;
    }
}

