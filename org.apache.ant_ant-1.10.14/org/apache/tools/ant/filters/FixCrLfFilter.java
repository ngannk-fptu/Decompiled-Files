/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.filters.BaseParamFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.types.EnumeratedAttribute;

public final class FixCrLfFilter
extends BaseParamFilterReader
implements ChainableReader {
    private static final int DEFAULT_TAB_LENGTH = 8;
    private static final int MIN_TAB_LENGTH = 2;
    private static final int MAX_TAB_LENGTH = 80;
    private static final char CTRLZ = '\u001a';
    private int tabLength = 8;
    private CrLf eol;
    private AddAsisRemove ctrlz;
    private AddAsisRemove tabs = AddAsisRemove.access$000();
    private boolean javafiles = false;
    private boolean fixlast = true;
    private boolean initialized = false;

    public FixCrLfFilter() {
        if (Os.isFamily("mac") && !Os.isFamily("unix")) {
            this.ctrlz = AddAsisRemove.REMOVE;
            this.setEol(CrLf.MAC);
        } else if (Os.isFamily("dos")) {
            this.ctrlz = AddAsisRemove.ASIS;
            this.setEol(CrLf.DOS);
        } else {
            this.ctrlz = AddAsisRemove.REMOVE;
            this.setEol(CrLf.UNIX);
        }
    }

    public FixCrLfFilter(Reader in) throws IOException {
        super(in);
        if (Os.isFamily("mac") && !Os.isFamily("unix")) {
            this.ctrlz = AddAsisRemove.REMOVE;
            this.setEol(CrLf.MAC);
        } else if (Os.isFamily("dos")) {
            this.ctrlz = AddAsisRemove.ASIS;
            this.setEol(CrLf.DOS);
        } else {
            this.ctrlz = AddAsisRemove.REMOVE;
            this.setEol(CrLf.UNIX);
        }
    }

    @Override
    public Reader chain(Reader rdr) {
        try {
            FixCrLfFilter newFilter = new FixCrLfFilter(rdr);
            newFilter.setJavafiles(this.getJavafiles());
            newFilter.setEol(this.getEol());
            newFilter.setTab(this.getTab());
            newFilter.setTablength(this.getTablength());
            newFilter.setEof(this.getEof());
            newFilter.setFixlast(this.getFixlast());
            newFilter.initInternalFilters();
            return newFilter;
        }
        catch (IOException e) {
            throw new BuildException(e);
        }
    }

    public AddAsisRemove getEof() {
        return this.ctrlz.newInstance();
    }

    public CrLf getEol() {
        return this.eol.newInstance();
    }

    public boolean getFixlast() {
        return this.fixlast;
    }

    public boolean getJavafiles() {
        return this.javafiles;
    }

    public AddAsisRemove getTab() {
        return this.tabs.newInstance();
    }

    public int getTablength() {
        return this.tabLength;
    }

    private static String calculateEolString(CrLf eol) {
        if (eol == CrLf.CR || eol == CrLf.MAC) {
            return "\r";
        }
        if (eol == CrLf.CRLF || eol == CrLf.DOS) {
            return "\r\n";
        }
        return "\n";
    }

    private void initInternalFilters() {
        Reader reader = this.in = this.ctrlz == AddAsisRemove.REMOVE ? new RemoveEofFilter(this.in) : this.in;
        if (this.eol != CrLf.ASIS) {
            this.in = new NormalizeEolFilter(this.in, FixCrLfFilter.calculateEolString(this.eol), this.getFixlast());
        }
        if (this.tabs != AddAsisRemove.ASIS) {
            if (this.getJavafiles()) {
                this.in = new MaskJavaTabLiteralsFilter(this.in);
            }
            this.in = this.tabs == AddAsisRemove.ADD ? new AddTabFilter(this.in, this.getTablength()) : new RemoveTabFilter(this.in, this.getTablength());
        }
        this.in = this.ctrlz == AddAsisRemove.ADD ? new AddEofFilter(this.in) : this.in;
        this.initialized = true;
    }

    @Override
    public synchronized int read() throws IOException {
        if (!this.initialized) {
            this.initInternalFilters();
        }
        return this.in.read();
    }

    public void setEof(AddAsisRemove attr) {
        this.ctrlz = attr.resolve();
    }

    public void setEol(CrLf attr) {
        this.eol = attr.resolve();
    }

    public void setFixlast(boolean fixlast) {
        this.fixlast = fixlast;
    }

    public void setJavafiles(boolean javafiles) {
        this.javafiles = javafiles;
    }

    public void setTab(AddAsisRemove attr) {
        this.tabs = attr.resolve();
    }

    public void setTablength(int tabLength) throws IOException {
        if (tabLength < 2 || tabLength > 80) {
            throw new IOException("tablength must be between 2 and 80");
        }
        this.tabLength = tabLength;
    }

    public static class AddAsisRemove
    extends EnumeratedAttribute {
        private static final AddAsisRemove ASIS = AddAsisRemove.newInstance("asis");
        private static final AddAsisRemove ADD = AddAsisRemove.newInstance("add");
        private static final AddAsisRemove REMOVE = AddAsisRemove.newInstance("remove");

        @Override
        public String[] getValues() {
            return new String[]{"add", "asis", "remove"};
        }

        public boolean equals(Object other) {
            return other instanceof AddAsisRemove && this.getIndex() == ((AddAsisRemove)other).getIndex();
        }

        public int hashCode() {
            return this.getIndex();
        }

        AddAsisRemove resolve() throws IllegalStateException {
            if (this.equals(ASIS)) {
                return ASIS;
            }
            if (this.equals(ADD)) {
                return ADD;
            }
            if (this.equals(REMOVE)) {
                return REMOVE;
            }
            throw new IllegalStateException("No replacement for " + this);
        }

        private AddAsisRemove newInstance() {
            return AddAsisRemove.newInstance(this.getValue());
        }

        public static AddAsisRemove newInstance(String value) {
            AddAsisRemove a = new AddAsisRemove();
            a.setValue(value);
            return a;
        }
    }

    public static class CrLf
    extends EnumeratedAttribute {
        private static final CrLf ASIS = CrLf.newInstance("asis");
        private static final CrLf CR = CrLf.newInstance("cr");
        private static final CrLf CRLF = CrLf.newInstance("crlf");
        private static final CrLf DOS = CrLf.newInstance("dos");
        private static final CrLf LF = CrLf.newInstance("lf");
        private static final CrLf MAC = CrLf.newInstance("mac");
        private static final CrLf UNIX = CrLf.newInstance("unix");

        @Override
        public String[] getValues() {
            return new String[]{"asis", "cr", "lf", "crlf", "mac", "unix", "dos"};
        }

        public boolean equals(Object other) {
            return other instanceof CrLf && this.getIndex() == ((CrLf)other).getIndex();
        }

        public int hashCode() {
            return this.getIndex();
        }

        CrLf resolve() {
            if (this.equals(ASIS)) {
                return ASIS;
            }
            if (this.equals(CR) || this.equals(MAC)) {
                return CR;
            }
            if (this.equals(CRLF) || this.equals(DOS)) {
                return CRLF;
            }
            if (this.equals(LF) || this.equals(UNIX)) {
                return LF;
            }
            throw new IllegalStateException("No replacement for " + this);
        }

        private CrLf newInstance() {
            return CrLf.newInstance(this.getValue());
        }

        public static CrLf newInstance(String value) {
            CrLf c = new CrLf();
            c.setValue(value);
            return c;
        }
    }

    private static class RemoveEofFilter
    extends SimpleFilterReader {
        private int lookAhead = -1;

        public RemoveEofFilter(Reader in) {
            super(in);
            try {
                this.lookAhead = in.read();
            }
            catch (IOException e) {
                this.lookAhead = -1;
            }
        }

        @Override
        public int read() throws IOException {
            int lookAhead2 = super.read();
            if (lookAhead2 == -1 && this.lookAhead == 26) {
                return -1;
            }
            int i = this.lookAhead;
            this.lookAhead = lookAhead2;
            return i;
        }
    }

    private static class NormalizeEolFilter
    extends SimpleFilterReader {
        private boolean previousWasEOL;
        private boolean fixLast;
        private int normalizedEOL = 0;
        private char[] eol = null;

        public NormalizeEolFilter(Reader in, String eolString, boolean fixLast) {
            super(in);
            this.eol = eolString.toCharArray();
            this.fixLast = fixLast;
        }

        @Override
        public int read() throws IOException {
            int thisChar = super.read();
            if (this.normalizedEOL == 0) {
                int numEOL = 0;
                boolean atEnd = false;
                switch (thisChar) {
                    case 26: {
                        int c = super.read();
                        if (c == -1) {
                            atEnd = true;
                            if (!this.fixLast || this.previousWasEOL) break;
                            numEOL = 1;
                            this.push(thisChar);
                            break;
                        }
                        this.push(c);
                        break;
                    }
                    case -1: {
                        atEnd = true;
                        if (!this.fixLast || this.previousWasEOL) break;
                        numEOL = 1;
                        break;
                    }
                    case 10: {
                        numEOL = 1;
                        break;
                    }
                    case 13: {
                        numEOL = 1;
                        int c1 = super.read();
                        int c2 = super.read();
                        if (c1 == 13 && c2 == 10) break;
                        if (c1 == 13) {
                            numEOL = 2;
                            this.push(c2);
                            break;
                        }
                        if (c1 == 10) {
                            this.push(c2);
                            break;
                        }
                        this.push(c2);
                        this.push(c1);
                    }
                }
                if (numEOL > 0) {
                    while (numEOL-- > 0) {
                        this.push(this.eol);
                        this.normalizedEOL += this.eol.length;
                    }
                    this.previousWasEOL = true;
                    thisChar = this.read();
                } else if (!atEnd) {
                    this.previousWasEOL = false;
                }
            } else {
                --this.normalizedEOL;
            }
            return thisChar;
        }
    }

    private static class MaskJavaTabLiteralsFilter
    extends SimpleFilterReader {
        private boolean editsBlocked = false;
        private static final int JAVA = 1;
        private static final int IN_CHAR_CONST = 2;
        private static final int IN_STR_CONST = 3;
        private static final int IN_SINGLE_COMMENT = 4;
        private static final int IN_MULTI_COMMENT = 5;
        private static final int TRANS_TO_COMMENT = 6;
        private static final int TRANS_FROM_MULTI = 8;
        private int state = 1;

        public MaskJavaTabLiteralsFilter(Reader in) {
            super(in);
        }

        @Override
        public boolean editsBlocked() {
            return this.editsBlocked || super.editsBlocked();
        }

        @Override
        public int read() throws IOException {
            int thisChar = super.read();
            this.editsBlocked = this.state == 2 || this.state == 3;
            block0 : switch (this.state) {
                case 1: {
                    switch (thisChar) {
                        case 39: {
                            this.state = 2;
                            break block0;
                        }
                        case 34: {
                            this.state = 3;
                            break block0;
                        }
                        case 47: {
                            this.state = 6;
                            break block0;
                        }
                    }
                    break;
                }
                case 2: {
                    switch (thisChar) {
                        case 39: {
                            this.state = 1;
                            break block0;
                        }
                    }
                    break;
                }
                case 3: {
                    switch (thisChar) {
                        case 34: {
                            this.state = 1;
                            break block0;
                        }
                    }
                    break;
                }
                case 4: {
                    switch (thisChar) {
                        case 10: 
                        case 13: {
                            this.state = 1;
                            break block0;
                        }
                    }
                    break;
                }
                case 5: {
                    switch (thisChar) {
                        case 42: {
                            this.state = 8;
                            break block0;
                        }
                    }
                    break;
                }
                case 6: {
                    switch (thisChar) {
                        case 42: {
                            this.state = 5;
                            break block0;
                        }
                        case 47: {
                            this.state = 4;
                            break block0;
                        }
                        case 39: {
                            this.state = 2;
                            break block0;
                        }
                        case 34: {
                            this.state = 3;
                            break block0;
                        }
                    }
                    this.state = 1;
                    break;
                }
                case 8: {
                    switch (thisChar) {
                        case 47: {
                            this.state = 1;
                            break block0;
                        }
                    }
                    break;
                }
            }
            return thisChar;
        }
    }

    private static class AddTabFilter
    extends SimpleFilterReader {
        private int columnNumber = 0;
        private int tabLength = 0;

        public AddTabFilter(Reader in, int tabLength) {
            super(in);
            this.tabLength = tabLength;
        }

        @Override
        public int read() throws IOException {
            int c = super.read();
            block0 : switch (c) {
                case 10: 
                case 13: {
                    this.columnNumber = 0;
                    break;
                }
                case 32: {
                    ++this.columnNumber;
                    if (this.editsBlocked()) break;
                    int colNextTab = (this.columnNumber + this.tabLength - 1) / this.tabLength * this.tabLength;
                    int countSpaces = 1;
                    int numTabs = 0;
                    block13: while ((c = super.read()) != -1) {
                        switch (c) {
                            case 32: {
                                if (++this.columnNumber == colNextTab) {
                                    ++numTabs;
                                    countSpaces = 0;
                                    colNextTab += this.tabLength;
                                    continue block13;
                                }
                                ++countSpaces;
                                continue block13;
                            }
                            case 9: {
                                this.columnNumber = colNextTab;
                                ++numTabs;
                                countSpaces = 0;
                                colNextTab += this.tabLength;
                                continue block13;
                            }
                        }
                        this.push(c);
                        break;
                    }
                    while (countSpaces-- > 0) {
                        this.push(' ');
                        --this.columnNumber;
                    }
                    while (numTabs-- > 0) {
                        this.push('\t');
                        this.columnNumber -= this.tabLength;
                    }
                    c = super.read();
                    switch (c) {
                        case 32: {
                            ++this.columnNumber;
                            break block0;
                        }
                        case 9: {
                            this.columnNumber += this.tabLength;
                            break block0;
                        }
                    }
                    break;
                }
                case 9: {
                    this.columnNumber = (this.columnNumber + this.tabLength - 1) / this.tabLength * this.tabLength;
                    break;
                }
                default: {
                    ++this.columnNumber;
                }
            }
            return c;
        }
    }

    private static class RemoveTabFilter
    extends SimpleFilterReader {
        private int columnNumber = 0;
        private int tabLength = 0;

        public RemoveTabFilter(Reader in, int tabLength) {
            super(in);
            this.tabLength = tabLength;
        }

        @Override
        public int read() throws IOException {
            int c = super.read();
            switch (c) {
                case 10: 
                case 13: {
                    this.columnNumber = 0;
                    break;
                }
                case 9: {
                    int width;
                    if (!this.editsBlocked()) {
                        for (width = this.tabLength - this.columnNumber % this.tabLength; width > 1; --width) {
                            this.push(' ');
                        }
                        c = 32;
                    }
                    this.columnNumber += width;
                    break;
                }
                default: {
                    ++this.columnNumber;
                }
            }
            return c;
        }
    }

    private static class AddEofFilter
    extends SimpleFilterReader {
        private int lastChar = -1;

        public AddEofFilter(Reader in) {
            super(in);
        }

        @Override
        public int read() throws IOException {
            int thisChar = super.read();
            if (thisChar == -1) {
                if (this.lastChar != 26) {
                    this.lastChar = 26;
                    return this.lastChar;
                }
            } else {
                this.lastChar = thisChar;
            }
            return thisChar;
        }
    }

    private static class SimpleFilterReader
    extends Reader {
        private static final int PREEMPT_BUFFER_LENGTH = 16;
        private Reader in;
        private int[] preempt = new int[16];
        private int preemptIndex = 0;

        public SimpleFilterReader(Reader in) {
            this.in = in;
        }

        public void push(char c) {
            this.push((int)c);
        }

        public void push(int c) {
            try {
                this.preempt[this.preemptIndex++] = c;
            }
            catch (ArrayIndexOutOfBoundsException e) {
                int[] p2 = new int[this.preempt.length * 2];
                System.arraycopy(this.preempt, 0, p2, 0, this.preempt.length);
                this.preempt = p2;
                this.push(c);
            }
        }

        public void push(char[] cs, int start, int length) {
            int i = start + length - 1;
            while (i >= start) {
                this.push(cs[i--]);
            }
        }

        public void push(char[] cs) {
            this.push(cs, 0, cs.length);
        }

        public boolean editsBlocked() {
            return this.in instanceof SimpleFilterReader && ((SimpleFilterReader)this.in).editsBlocked();
        }

        @Override
        public int read() throws IOException {
            return this.preemptIndex > 0 ? this.preempt[--this.preemptIndex] : this.in.read();
        }

        @Override
        public void close() throws IOException {
            this.in.close();
        }

        @Override
        public void reset() throws IOException {
            this.in.reset();
        }

        @Override
        public boolean markSupported() {
            return this.in.markSupported();
        }

        @Override
        public boolean ready() throws IOException {
            return this.in.ready();
        }

        @Override
        public void mark(int i) throws IOException {
            this.in.mark(i);
        }

        @Override
        public long skip(long i) throws IOException {
            return this.in.skip(i);
        }

        @Override
        public int read(char[] buf) throws IOException {
            return this.read(buf, 0, buf.length);
        }

        @Override
        public int read(char[] buf, int start, int length) throws IOException {
            int count = 0;
            int c = 0;
            while (length-- > 0 && (c = this.read()) != -1) {
                buf[start++] = (char)c;
                ++count;
            }
            return count == 0 && c == -1 ? -1 : count;
        }
    }
}

