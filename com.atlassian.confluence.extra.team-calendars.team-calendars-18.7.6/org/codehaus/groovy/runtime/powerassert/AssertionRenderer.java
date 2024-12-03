/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime.powerassert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.powerassert.Value;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;

public final class AssertionRenderer {
    private final String text;
    private final ValueRecorder recorder;
    private final List<StringBuilder> lines = new ArrayList<StringBuilder>();
    private final List<Integer> startColumns = new ArrayList<Integer>();

    private AssertionRenderer(String text, ValueRecorder recorder) {
        if (text.contains("\n")) {
            throw new IllegalArgumentException("source text may not contain line breaks");
        }
        this.text = text;
        this.recorder = recorder;
    }

    public static String render(String text, ValueRecorder recorder) {
        return new AssertionRenderer(text, recorder).render();
    }

    private String render() {
        this.renderText();
        this.sortValues();
        this.renderValues();
        return this.linesToString();
    }

    private void renderText() {
        this.lines.add(new StringBuilder(this.text));
        this.startColumns.add(0);
        this.lines.add(new StringBuilder());
        this.startColumns.add(0);
    }

    private void sortValues() {
        Collections.sort(this.recorder.getValues(), new Comparator<Value>(){

            @Override
            public int compare(Value v1, Value v2) {
                return v2.getColumn() - v1.getColumn();
            }
        });
    }

    private void renderValues() {
        List<Value> values = this.recorder.getValues();
        int valuesSize = values.size();
        block0: for (int i = 0; i < valuesSize; ++i) {
            String str;
            Value next;
            Value value = values.get(i);
            int startColumn = value.getColumn();
            if (startColumn < 1) continue;
            Value value2 = next = i + 1 < valuesSize ? values.get(i + 1) : null;
            if (next != null && next.getColumn() == startColumn || (str = AssertionRenderer.valueToString(value.getValue())) == null) continue;
            String[] strs = str.split("\r\n|\r|\n");
            int endColumn = strs.length == 1 ? startColumn + str.length() : Integer.MAX_VALUE;
            for (int j = 1; j < this.lines.size(); ++j) {
                if (endColumn < this.startColumns.get(j)) {
                    AssertionRenderer.placeString(this.lines.get(j), str, startColumn);
                    this.startColumns.set(j, startColumn);
                    continue block0;
                }
                AssertionRenderer.placeString(this.lines.get(j), "|", startColumn);
                if (j <= 1) continue;
                this.startColumns.set(j, startColumn + 1);
            }
            for (String s : strs) {
                StringBuilder newLine = new StringBuilder();
                this.lines.add(newLine);
                AssertionRenderer.placeString(newLine, s, startColumn);
                this.startColumns.add(startColumn);
            }
        }
    }

    private String linesToString() {
        StringBuilder firstLine = this.lines.get(0);
        for (int i = 1; i < this.lines.size(); ++i) {
            firstLine.append('\n').append(this.lines.get(i).toString());
        }
        return firstLine.toString();
    }

    private static void placeString(StringBuilder line, String str, int column) {
        while (line.length() < column) {
            line.append(' ');
        }
        line.replace(column - 1, column - 1 + str.length(), str);
    }

    private static String valueToString(Object value) {
        String toString;
        try {
            toString = DefaultGroovyMethods.toString(value);
        }
        catch (Exception e) {
            return String.format("%s (toString() threw %s)", AssertionRenderer.javaLangObjectToString(value), e.getClass().getName());
        }
        if (toString == null) {
            return String.format("%s (toString() == null)", AssertionRenderer.javaLangObjectToString(value));
        }
        if (toString.equals("")) {
            if (AssertionRenderer.hasStringLikeType(value)) {
                return "\"\"";
            }
            return String.format("%s (toString() == \"\")", AssertionRenderer.javaLangObjectToString(value));
        }
        return toString;
    }

    private static boolean hasStringLikeType(Object value) {
        Class<?> clazz = value.getClass();
        return clazz == String.class || clazz == StringBuffer.class || clazz == StringBuilder.class;
    }

    private static String javaLangObjectToString(Object value) {
        String hash = Integer.toHexString(System.identityHashCode(value));
        return value.getClass().getName() + "@" + hash;
    }
}

