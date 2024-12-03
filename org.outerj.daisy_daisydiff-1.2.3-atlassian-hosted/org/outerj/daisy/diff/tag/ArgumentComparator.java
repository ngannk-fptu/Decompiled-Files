/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.tag;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.outerj.daisy.diff.tag.Atom;
import org.outerj.daisy.diff.tag.DelimiterAtom;
import org.outerj.daisy.diff.tag.IAtomSplitter;
import org.outerj.daisy.diff.tag.TextAtom;

public class ArgumentComparator
implements IAtomSplitter {
    private List<Atom> atoms = new ArrayList<Atom>(5);

    public ArgumentComparator(String s) {
        this.generateAtoms(s);
    }

    private void generateAtoms(String s) {
        if (this.atoms.size() > 0) {
            throw new IllegalStateException("Atoms can only be generated once");
        }
        StringBuilder currentWord = new StringBuilder(30);
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '<' || c == '>') {
                if (currentWord.length() > 0) {
                    this.atoms.add(new TextAtom(currentWord.toString()));
                    currentWord.setLength(0);
                }
                this.atoms.add(new TextAtom("" + c));
                currentWord.setLength(0);
                continue;
            }
            if (DelimiterAtom.isValidDelimiter("" + c)) {
                if (currentWord.length() > 0) {
                    this.atoms.add(new TextAtom(currentWord.toString()));
                    currentWord.setLength(0);
                }
                this.atoms.add(new DelimiterAtom(c));
                continue;
            }
            currentWord.append(c);
        }
        if (currentWord.length() > 0) {
            this.atoms.add(new TextAtom(currentWord.toString()));
            currentWord.setLength(0);
        }
    }

    @Override
    public Atom getAtom(int i) {
        if (i < 0 || i >= this.atoms.size()) {
            throw new IndexOutOfBoundsException("There is no Atom with index " + i);
        }
        return this.atoms.get(i);
    }

    @Override
    public int getRangeCount() {
        return this.atoms.size();
    }

    @Override
    public boolean rangesEqual(int thisIndex, IRangeComparator other, int otherIndex) {
        ArgumentComparator tc2;
        try {
            tc2 = (ArgumentComparator)other;
        }
        catch (ClassCastException e) {
            return false;
        }
        return tc2.getAtom(otherIndex).equalsIdentifier(this.getAtom(thisIndex));
    }

    @Override
    public boolean skipRangeComparison(int length, int maxLength, IRangeComparator other) {
        return false;
    }

    @Override
    public String substring(int startAtom, int endAtom) {
        if (startAtom == endAtom) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        for (int i = startAtom; i < endAtom; ++i) {
            result.append(this.atoms.get(i).getFullText());
        }
        return result.toString();
    }

    @Override
    public String substring(int startAtom) {
        return this.substring(startAtom, this.atoms.size());
    }
}

