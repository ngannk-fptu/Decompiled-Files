/*
 * Decompiled with CFR 0.152.
 */
package org.outerj.daisy.diff.tag;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.compare.rangedifferencer.IRangeComparator;
import org.outerj.daisy.diff.tag.Atom;
import org.outerj.daisy.diff.tag.DelimiterAtom;
import org.outerj.daisy.diff.tag.IAtomSplitter;
import org.outerj.daisy.diff.tag.TagAtom;
import org.outerj.daisy.diff.tag.TextAtom;

public class TagComparator
implements IAtomSplitter {
    private List<Atom> atoms = new ArrayList<Atom>(50);

    public TagComparator(String s) {
        this.generateAtoms(s);
    }

    public TagComparator(StringBuilder s) {
        this.generateAtoms(s.toString());
    }

    public TagComparator(BufferedReader in) throws IOException {
        StringBuilder sb = new StringBuilder();
        boolean allRead = false;
        while (!allRead) {
            int result = in.read();
            if (result >= 0) {
                sb.append((char)result);
                continue;
            }
            this.generateAtoms(sb.toString());
            allRead = true;
        }
    }

    public List<Atom> getAtoms() {
        return new ArrayList<Atom>(this.atoms);
    }

    private void generateAtoms(String s) {
        if (this.atoms.size() > 0) {
            throw new IllegalStateException("Atoms can only be generated once");
        }
        StringBuilder currentWord = new StringBuilder(100);
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '<' && TagAtom.isValidTag(s.substring(i, s.indexOf(62, i) + 1))) {
                if (currentWord.length() > 0) {
                    this.atoms.add(new TextAtom(currentWord.toString()));
                    currentWord.setLength(0);
                }
                int end = s.indexOf(62, i);
                this.atoms.add(new TagAtom(s.substring(i, end + 1)));
                i = end;
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
        TagComparator tc2;
        try {
            tc2 = (TagComparator)other;
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
}

