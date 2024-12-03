/*
 * Decompiled with CFR 0.152.
 */
package org.apache.fontbox.cff;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.fontbox.cff.CharStringCommand;
import org.apache.fontbox.cff.CharStringHandler;
import org.apache.fontbox.cff.Type1CharString;
import org.apache.fontbox.type1.Type1CharStringReader;

public class Type2CharString
extends Type1CharString {
    private float defWidthX = 0.0f;
    private float nominalWidthX = 0.0f;
    private int pathCount = 0;
    private final List<Object> type2sequence;
    private final int gid;

    public Type2CharString(Type1CharStringReader font, String fontName, String glyphName, int gid, List<Object> sequence, int defaultWidthX, int nomWidthX) {
        super(font, fontName, glyphName);
        this.gid = gid;
        this.type2sequence = sequence;
        this.defWidthX = defaultWidthX;
        this.nominalWidthX = nomWidthX;
        this.convertType1ToType2(sequence);
    }

    public int getGID() {
        return this.gid;
    }

    public List<Object> getType2Sequence() {
        return this.type2sequence;
    }

    private void convertType1ToType2(List<Object> sequence) {
        this.type1Sequence = new ArrayList();
        this.pathCount = 0;
        CharStringHandler handler = new CharStringHandler(){

            @Override
            public List<Number> handleCommand(List<Number> numbers, CharStringCommand command) {
                return Type2CharString.this.handleCommand(numbers, command);
            }
        };
        handler.handleSequence(sequence);
    }

    private List<Number> handleCommand(List<Number> numbers, CharStringCommand command) {
        ++this.commandCount;
        String name = CharStringCommand.TYPE2_VOCABULARY.get(command.getKey());
        if ("hstem".equals(name)) {
            numbers = this.clearStack(numbers, numbers.size() % 2 != 0);
            this.expandStemHints(numbers, true);
        } else if ("vstem".equals(name)) {
            numbers = this.clearStack(numbers, numbers.size() % 2 != 0);
            this.expandStemHints(numbers, false);
        } else if ("vmoveto".equals(name)) {
            numbers = this.clearStack(numbers, numbers.size() > 1);
            this.markPath();
            this.addCommand(numbers, command);
        } else if ("rlineto".equals(name)) {
            this.addCommandList(Type2CharString.split(numbers, 2), command);
        } else if ("hlineto".equals(name)) {
            this.drawAlternatingLine(numbers, true);
        } else if ("vlineto".equals(name)) {
            this.drawAlternatingLine(numbers, false);
        } else if ("rrcurveto".equals(name)) {
            this.addCommandList(Type2CharString.split(numbers, 6), command);
        } else if ("endchar".equals(name)) {
            numbers = this.clearStack(numbers, numbers.size() == 5 || numbers.size() == 1);
            this.closeCharString2Path();
            if (numbers.size() == 4) {
                numbers.add(0, 0);
                this.addCommand(numbers, new CharStringCommand(12, 6));
            } else {
                this.addCommand(numbers, command);
            }
        } else if ("rmoveto".equals(name)) {
            numbers = this.clearStack(numbers, numbers.size() > 2);
            this.markPath();
            this.addCommand(numbers, command);
        } else if ("hmoveto".equals(name)) {
            numbers = this.clearStack(numbers, numbers.size() > 1);
            this.markPath();
            this.addCommand(numbers, command);
        } else if ("vhcurveto".equals(name)) {
            this.drawAlternatingCurve(numbers, false);
        } else if ("hvcurveto".equals(name)) {
            this.drawAlternatingCurve(numbers, true);
        } else if ("hflex".equals(name)) {
            if (numbers.size() >= 7) {
                List<Number> first = Arrays.asList(numbers.get(0), 0, numbers.get(1), numbers.get(2), numbers.get(3), 0);
                List<Number> second = Arrays.asList(numbers.get(4), 0, numbers.get(5), Float.valueOf(-numbers.get(2).floatValue()), numbers.get(6), 0);
                this.addCommandList(Arrays.asList(first, second), new CharStringCommand(8));
            }
        } else if ("flex".equals(name)) {
            List<Number> first = numbers.subList(0, 6);
            List<Number> second = numbers.subList(6, 12);
            this.addCommandList(Arrays.asList(first, second), new CharStringCommand(8));
        } else if ("hflex1".equals(name)) {
            if (numbers.size() >= 9) {
                List<Number> first = Arrays.asList(numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3), numbers.get(4), 0);
                List<Number> second = Arrays.asList(numbers.get(5), 0, numbers.get(6), numbers.get(7), numbers.get(8), 0);
                this.addCommandList(Arrays.asList(first, second), new CharStringCommand(8));
            }
        } else if ("flex1".equals(name)) {
            int dx = 0;
            int dy = 0;
            for (int i = 0; i < 5; ++i) {
                dx += numbers.get(i * 2).intValue();
                dy += numbers.get(i * 2 + 1).intValue();
            }
            List<Number> first = numbers.subList(0, 6);
            boolean dxIsBigger = Math.abs(dx) > Math.abs(dy);
            List<Number> second = Arrays.asList(numbers.get(6), numbers.get(7), numbers.get(8), numbers.get(9), dxIsBigger ? (Number)numbers.get(10) : (Number)(-dx), dxIsBigger ? (Number)(-dy) : (Number)numbers.get(10));
            this.addCommandList(Arrays.asList(first, second), new CharStringCommand(8));
        } else if ("hstemhm".equals(name)) {
            numbers = this.clearStack(numbers, numbers.size() % 2 != 0);
            this.expandStemHints(numbers, true);
        } else if ("hintmask".equals(name) || "cntrmask".equals(name)) {
            if (!(numbers = this.clearStack(numbers, numbers.size() % 2 != 0)).isEmpty()) {
                this.expandStemHints(numbers, false);
            }
        } else if ("vstemhm".equals(name)) {
            numbers = this.clearStack(numbers, numbers.size() % 2 != 0);
            this.expandStemHints(numbers, false);
        } else if ("rcurveline".equals(name)) {
            if (numbers.size() >= 2) {
                this.addCommandList(Type2CharString.split(numbers.subList(0, numbers.size() - 2), 6), new CharStringCommand(8));
                this.addCommand(numbers.subList(numbers.size() - 2, numbers.size()), new CharStringCommand(5));
            }
        } else if ("rlinecurve".equals(name)) {
            if (numbers.size() >= 6) {
                this.addCommandList(Type2CharString.split(numbers.subList(0, numbers.size() - 6), 2), new CharStringCommand(5));
                this.addCommand(numbers.subList(numbers.size() - 6, numbers.size()), new CharStringCommand(8));
            }
        } else if ("vvcurveto".equals(name)) {
            this.drawCurve(numbers, false);
        } else if ("hhcurveto".equals(name)) {
            this.drawCurve(numbers, true);
        } else {
            this.addCommand(numbers, command);
        }
        return null;
    }

    private List<Number> clearStack(List<Number> numbers, boolean flag) {
        if (this.type1Sequence.isEmpty()) {
            if (flag) {
                this.addCommand(Arrays.asList(Float.valueOf(0.0f), Float.valueOf(numbers.get(0).floatValue() + this.nominalWidthX)), new CharStringCommand(13));
                numbers = numbers.subList(1, numbers.size());
            } else {
                this.addCommand(Arrays.asList(Float.valueOf(0.0f), Float.valueOf(this.defWidthX)), new CharStringCommand(13));
            }
        }
        return numbers;
    }

    private void expandStemHints(List<Number> numbers, boolean horizontal) {
    }

    private void markPath() {
        if (this.pathCount > 0) {
            this.closeCharString2Path();
        }
        ++this.pathCount;
    }

    private void closeCharString2Path() {
        CharStringCommand command = this.pathCount > 0 ? (CharStringCommand)this.type1Sequence.get(this.type1Sequence.size() - 1) : null;
        CharStringCommand closepathCommand = new CharStringCommand(9);
        if (command != null && !closepathCommand.equals(command)) {
            this.addCommand(Collections.<Number>emptyList(), closepathCommand);
        }
    }

    private void drawAlternatingLine(List<Number> numbers, boolean horizontal) {
        while (!numbers.isEmpty()) {
            this.addCommand(numbers.subList(0, 1), new CharStringCommand(horizontal ? 6 : 7));
            numbers = numbers.subList(1, numbers.size());
            horizontal = !horizontal;
        }
    }

    private void drawAlternatingCurve(List<Number> numbers, boolean horizontal) {
        while (numbers.size() >= 4) {
            boolean last;
            boolean bl = last = numbers.size() == 5;
            if (horizontal) {
                this.addCommand(Arrays.asList(numbers.get(0), 0, numbers.get(1), numbers.get(2), last ? (Number)numbers.get(4) : (Number)0, numbers.get(3)), new CharStringCommand(8));
            } else {
                this.addCommand(Arrays.asList(0, numbers.get(0), numbers.get(1), numbers.get(2), numbers.get(3), last ? (Number)numbers.get(4) : (Number)0), new CharStringCommand(8));
            }
            numbers = numbers.subList(last ? 5 : 4, numbers.size());
            horizontal = !horizontal;
        }
    }

    private void drawCurve(List<Number> numbers, boolean horizontal) {
        while (numbers.size() >= 4) {
            boolean first;
            boolean bl = first = numbers.size() % 4 == 1;
            if (horizontal) {
                this.addCommand(Arrays.asList(numbers.get(first ? 1 : 0), first ? (Number)numbers.get(0) : (Number)0, numbers.get(first ? 2 : 1), numbers.get(first ? 3 : 2), numbers.get(first ? 4 : 3), 0), new CharStringCommand(8));
            } else {
                this.addCommand(Arrays.asList(first ? (Number)numbers.get(0) : (Number)0, numbers.get(first ? 1 : 0), numbers.get(first ? 2 : 1), numbers.get(first ? 3 : 2), 0, numbers.get(first ? 4 : 3)), new CharStringCommand(8));
            }
            numbers = numbers.subList(first ? 5 : 4, numbers.size());
        }
    }

    private void addCommandList(List<List<Number>> numbers, CharStringCommand command) {
        for (List<Number> ns : numbers) {
            this.addCommand(ns, command);
        }
    }

    private void addCommand(List<Number> numbers, CharStringCommand command) {
        this.type1Sequence.addAll(numbers);
        this.type1Sequence.add(command);
    }

    private static <E> List<List<E>> split(List<E> list, int size) {
        int listSize = list.size() / size;
        ArrayList<List<List<E>>> result = new ArrayList<List<List<E>>>(listSize);
        for (int i = 0; i < listSize; ++i) {
            result.add(list.subList(i * size, (i + 1) * size));
        }
        return result;
    }
}

