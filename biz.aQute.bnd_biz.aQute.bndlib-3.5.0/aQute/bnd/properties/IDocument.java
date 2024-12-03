/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.properties;

import aQute.bnd.properties.BadLocationException;
import aQute.bnd.properties.IRegion;

public interface IDocument {
    public int getNumberOfLines();

    public IRegion getLineInformation(int var1) throws BadLocationException;

    public String get();

    public String get(int var1, int var2) throws BadLocationException;

    public String getLineDelimiter(int var1) throws BadLocationException;

    public int getLength();

    public void replace(int var1, int var2, String var3) throws BadLocationException;

    public char getChar(int var1) throws BadLocationException;
}

