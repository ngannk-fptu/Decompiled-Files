/*
 * Decompiled with CFR 0.152.
 */
package org.radeox.macro.parameter;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import org.radeox.api.engine.context.RenderContext;
import org.radeox.macro.parameter.MacroParameter;

public class BaseMacroParameter
implements MacroParameter {
    private String content;
    protected Map params;
    private int size;
    protected RenderContext context;
    private int start;
    private int end;
    private int contentStart;
    private int contentEnd;

    public BaseMacroParameter() {
    }

    public BaseMacroParameter(RenderContext context) {
        this.context = context;
    }

    public void setParams(String stringParams) {
        this.params = this.split(stringParams, "|");
        this.size = this.params.size();
    }

    public RenderContext getContext() {
        return this.context;
    }

    public Map getParams() {
        return this.params;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLength() {
        return this.size;
    }

    public String get(String index, int idx) {
        String result = this.get(index);
        if (result == null) {
            result = this.get(idx);
        }
        return result;
    }

    public String get(String index) {
        return (String)this.params.get(index);
    }

    public String get(int index) {
        return this.get("" + index);
    }

    private Map split(String aString, String delimiter) {
        HashMap<String, String> result = new HashMap<String, String>();
        if (null != aString) {
            StringTokenizer st = new StringTokenizer(aString, delimiter);
            int i = 0;
            while (st.hasMoreTokens()) {
                String value = st.nextToken();
                String key = "" + i;
                if (value.indexOf("=") != -1) {
                    result.put(key, this.insertValue(value));
                    int index = value.indexOf("=");
                    key = value.substring(0, index);
                    value = value.substring(index + 1);
                    result.put(key, this.insertValue(value));
                } else {
                    result.put(key, this.insertValue(value));
                }
                ++i;
            }
        }
        return result;
    }

    private String insertValue(String s) {
        int idx = s.indexOf(36);
        if (idx != -1) {
            StringBuffer tmp = new StringBuffer();
            Map globals = this.context.getParameters();
            String var = s.substring(idx + 1);
            if (idx > 0) {
                tmp.append(s.substring(0, idx));
            }
            if (globals.containsKey(var)) {
                tmp.append(globals.get(var));
            }
            return tmp.toString();
        }
        return s;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public int getContentStart() {
        return this.contentStart;
    }

    public void setContentStart(int contentStart) {
        this.contentStart = contentStart;
    }

    public int getContentEnd() {
        return this.contentEnd;
    }

    public void setContentEnd(int contentEnd) {
        this.contentEnd = contentEnd;
    }
}

