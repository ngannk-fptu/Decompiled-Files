/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.gvt.text;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.batik.gvt.text.GVTAttributedCharacterIterator;

public class GVTACIImpl
implements GVTAttributedCharacterIterator {
    private String simpleString;
    private Set allAttributes;
    private ArrayList mapList;
    private static int START_RUN = 2;
    private static int END_RUN = 3;
    private static int MID_RUN = 1;
    private static int SINGLETON = 0;
    private int[] charInRun;
    private CharacterIterator iter = null;
    private int currentIndex = -1;

    public GVTACIImpl() {
        this.simpleString = "";
        this.buildAttributeTables();
    }

    public GVTACIImpl(AttributedCharacterIterator aci) {
        this.buildAttributeTables(aci);
    }

    @Override
    public void setString(String s) {
        this.simpleString = s;
        this.iter = new StringCharacterIterator(this.simpleString);
        this.buildAttributeTables();
    }

    @Override
    public void setString(AttributedString s) {
        this.iter = s.getIterator();
        this.buildAttributeTables((AttributedCharacterIterator)this.iter);
    }

    @Override
    public void setAttributeArray(GVTAttributedCharacterIterator.TextAttribute attr, Object[] attValues, int beginIndex, int endIndex) {
        beginIndex = Math.max(beginIndex, 0);
        endIndex = Math.min(endIndex, this.simpleString.length());
        if (this.charInRun[beginIndex] == END_RUN) {
            this.charInRun[beginIndex - 1] = this.charInRun[beginIndex - 1] == MID_RUN ? END_RUN : SINGLETON;
        }
        if (this.charInRun[endIndex + 1] == END_RUN) {
            this.charInRun[endIndex + 1] = SINGLETON;
        } else if (this.charInRun[endIndex + 1] == MID_RUN) {
            this.charInRun[endIndex + 1] = START_RUN;
        }
        for (int i = beginIndex; i <= endIndex; ++i) {
            this.charInRun[i] = SINGLETON;
            int n = Math.min(i, attValues.length - 1);
            ((Map)this.mapList.get(i)).put(attr, attValues[n]);
        }
    }

    @Override
    public Set getAllAttributeKeys() {
        return this.allAttributes;
    }

    @Override
    public Object getAttribute(AttributedCharacterIterator.Attribute attribute) {
        return this.getAttributes().get(attribute);
    }

    @Override
    public Map getAttributes() {
        return (Map)this.mapList.get(this.currentIndex);
    }

    @Override
    public int getRunLimit() {
        int ndx = this.currentIndex;
        while (this.charInRun[++ndx] == MID_RUN) {
        }
        return ndx;
    }

    @Override
    public int getRunLimit(AttributedCharacterIterator.Attribute attribute) {
        int ndx = this.currentIndex;
        Object value = this.getAttributes().get(attribute);
        if (value == null) {
            while (((Map)this.mapList.get(++ndx)).get(attribute) == null) {
            }
        } else {
            while (value.equals(((Map)this.mapList.get(++ndx)).get(attribute))) {
            }
        }
        return ndx;
    }

    @Override
    public int getRunLimit(Set attributes) {
        int ndx = this.currentIndex;
        while (attributes.equals(this.mapList.get(++ndx))) {
        }
        return ndx;
    }

    @Override
    public int getRunStart() {
        int ndx = this.currentIndex;
        while (this.charInRun[ndx] == MID_RUN) {
            --ndx;
        }
        return ndx;
    }

    @Override
    public int getRunStart(AttributedCharacterIterator.Attribute attribute) {
        int ndx = this.currentIndex - 1;
        Object value = this.getAttributes().get(attribute);
        try {
            if (value == null) {
                while (((Map)this.mapList.get(ndx - 1)).get(attribute) == null) {
                    --ndx;
                }
            } else {
                while (value.equals(((Map)this.mapList.get(ndx - 1)).get(attribute))) {
                    --ndx;
                }
            }
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            // empty catch block
        }
        return ndx;
    }

    @Override
    public int getRunStart(Set attributes) {
        int ndx = this.currentIndex;
        try {
            while (attributes.equals(this.mapList.get(ndx - 1))) {
                --ndx;
            }
        }
        catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            // empty catch block
        }
        return ndx;
    }

    @Override
    public Object clone() {
        GVTACIImpl cloneACI = new GVTACIImpl(this);
        return cloneACI;
    }

    @Override
    public char current() {
        return this.iter.current();
    }

    @Override
    public char first() {
        return this.iter.first();
    }

    @Override
    public int getBeginIndex() {
        return this.iter.getBeginIndex();
    }

    @Override
    public int getEndIndex() {
        return this.iter.getEndIndex();
    }

    @Override
    public int getIndex() {
        return this.iter.getIndex();
    }

    @Override
    public char last() {
        return this.iter.last();
    }

    @Override
    public char next() {
        return this.iter.next();
    }

    @Override
    public char previous() {
        return this.iter.previous();
    }

    @Override
    public char setIndex(int position) {
        return this.iter.setIndex(position);
    }

    private void buildAttributeTables() {
        this.allAttributes = new HashSet();
        this.mapList = new ArrayList(this.simpleString.length());
        this.charInRun = new int[this.simpleString.length()];
        for (int i = 0; i < this.charInRun.length; ++i) {
            this.charInRun[i] = SINGLETON;
            this.mapList.set(i, new HashMap());
        }
    }

    private void buildAttributeTables(AttributedCharacterIterator aci) {
        this.allAttributes = aci.getAllAttributeKeys();
        int length = aci.getEndIndex() - aci.getBeginIndex();
        this.mapList = new ArrayList(length);
        this.charInRun = new int[length];
        char c = aci.first();
        char[] chars = new char[length];
        for (int i = 0; i < length; ++i) {
            chars[i] = c;
            this.charInRun[i] = SINGLETON;
            this.mapList.set(i, new HashMap<AttributedCharacterIterator.Attribute, Object>(aci.getAttributes()));
            c = aci.next();
        }
        this.simpleString = new String(chars);
    }

    public static class TransformAttributeFilter
    implements GVTAttributedCharacterIterator.AttributeFilter {
        @Override
        public AttributedCharacterIterator mutateAttributes(AttributedCharacterIterator aci) {
            return aci;
        }
    }
}

