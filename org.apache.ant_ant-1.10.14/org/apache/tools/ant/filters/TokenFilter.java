/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.filters.BaseFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.RegularExpression;
import org.apache.tools.ant.types.Substitution;
import org.apache.tools.ant.util.LineTokenizer;
import org.apache.tools.ant.util.StringUtils;
import org.apache.tools.ant.util.Tokenizer;
import org.apache.tools.ant.util.regexp.Regexp;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public class TokenFilter
extends BaseFilterReader
implements ChainableReader {
    private Vector<Filter> filters = new Vector();
    private Tokenizer tokenizer = null;
    private String delimOutput = null;
    private String line = null;
    private int linePos = 0;

    public TokenFilter() {
    }

    public TokenFilter(Reader in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        if (this.tokenizer == null) {
            this.tokenizer = new LineTokenizer();
        }
        while (this.line == null || this.line.isEmpty()) {
            this.line = this.tokenizer.getToken(this.in);
            if (this.line == null) {
                return -1;
            }
            for (Filter filter : this.filters) {
                this.line = filter.filter(this.line);
                if (this.line != null) continue;
                break;
            }
            this.linePos = 0;
            if (this.line == null || this.tokenizer.getPostToken().isEmpty()) continue;
            if (this.delimOutput != null) {
                this.line = this.line + this.delimOutput;
                continue;
            }
            this.line = this.line + this.tokenizer.getPostToken();
        }
        char ch = this.line.charAt(this.linePos);
        ++this.linePos;
        if (this.linePos == this.line.length()) {
            this.line = null;
        }
        return ch;
    }

    @Override
    public final Reader chain(Reader reader) {
        TokenFilter newFilter = new TokenFilter(reader);
        newFilter.filters = this.filters;
        newFilter.tokenizer = this.tokenizer;
        newFilter.delimOutput = this.delimOutput;
        newFilter.setProject(this.getProject());
        return newFilter;
    }

    public void setDelimOutput(String delimOutput) {
        this.delimOutput = TokenFilter.resolveBackSlash(delimOutput);
    }

    public void addLineTokenizer(LineTokenizer tokenizer) {
        this.add(tokenizer);
    }

    public void addStringTokenizer(StringTokenizer tokenizer) {
        this.add(tokenizer);
    }

    public void addFileTokenizer(FileTokenizer tokenizer) {
        this.add(tokenizer);
    }

    public void add(Tokenizer tokenizer) {
        if (this.tokenizer != null) {
            throw new BuildException("Only one tokenizer allowed");
        }
        this.tokenizer = tokenizer;
    }

    public void addReplaceString(ReplaceString filter) {
        this.filters.addElement(filter);
    }

    public void addContainsString(ContainsString filter) {
        this.filters.addElement(filter);
    }

    public void addReplaceRegex(ReplaceRegex filter) {
        this.filters.addElement(filter);
    }

    public void addContainsRegex(ContainsRegex filter) {
        this.filters.addElement(filter);
    }

    public void addTrim(Trim filter) {
        this.filters.addElement(filter);
    }

    public void addIgnoreBlank(IgnoreBlank filter) {
        this.filters.addElement(filter);
    }

    public void addDeleteCharacters(DeleteCharacters filter) {
        this.filters.addElement(filter);
    }

    public void add(Filter filter) {
        this.filters.addElement(filter);
    }

    public static String resolveBackSlash(String input) {
        return StringUtils.resolveBackSlash(input);
    }

    public static int convertRegexOptions(String flags) {
        return RegexpUtil.asOptions(flags);
    }

    public static interface Filter {
        public String filter(String var1);
    }

    public static class DeleteCharacters
    extends ProjectComponent
    implements Filter,
    ChainableReader {
        private String deleteChars = "";

        public void setChars(String deleteChars) {
            this.deleteChars = TokenFilter.resolveBackSlash(deleteChars);
        }

        @Override
        public String filter(String string) {
            StringBuilder output = new StringBuilder(string.length());
            for (int i = 0; i < string.length(); ++i) {
                char ch = string.charAt(i);
                if (this.isDeleteCharacter(ch)) continue;
                output.append(ch);
            }
            return output.toString();
        }

        @Override
        public Reader chain(Reader reader) {
            return new BaseFilterReader(reader){

                @Override
                public int read() throws IOException {
                    int c;
                    do {
                        if ((c = this.in.read()) != -1) continue;
                        return c;
                    } while (this.isDeleteCharacter((char)c));
                    return c;
                }
            };
        }

        private boolean isDeleteCharacter(char c) {
            for (int d = 0; d < this.deleteChars.length(); ++d) {
                if (this.deleteChars.charAt(d) != c) continue;
                return true;
            }
            return false;
        }
    }

    public static class IgnoreBlank
    extends ChainableReaderFilter {
        @Override
        public String filter(String line) {
            if (line.trim().isEmpty()) {
                return null;
            }
            return line;
        }
    }

    public static class Trim
    extends ChainableReaderFilter {
        @Override
        public String filter(String line) {
            return line.trim();
        }
    }

    public static class ContainsRegex
    extends ChainableReaderFilter {
        private String from;
        private String to;
        private RegularExpression regularExpression;
        private Substitution substitution;
        private boolean initialized = false;
        private String flags = "";
        private int options;
        private Regexp regexp;

        public void setPattern(String from) {
            this.from = from;
        }

        public void setReplace(String to) {
            this.to = to;
        }

        public void setFlags(String flags) {
            this.flags = flags;
        }

        private void initialize() {
            if (this.initialized) {
                return;
            }
            this.options = TokenFilter.convertRegexOptions(this.flags);
            if (this.from == null) {
                throw new BuildException("Missing from in containsregex");
            }
            this.regularExpression = new RegularExpression();
            this.regularExpression.setPattern(this.from);
            this.regexp = this.regularExpression.getRegexp(this.getProject());
            if (this.to == null) {
                return;
            }
            this.substitution = new Substitution();
            this.substitution.setExpression(this.to);
        }

        @Override
        public String filter(String string) {
            this.initialize();
            if (!this.regexp.matches(string, this.options)) {
                return null;
            }
            if (this.substitution == null) {
                return string;
            }
            return this.regexp.substitute(string, this.substitution.getExpression(this.getProject()), this.options);
        }
    }

    public static class ReplaceRegex
    extends ChainableReaderFilter {
        private String from;
        private String to;
        private RegularExpression regularExpression;
        private Substitution substitution;
        private boolean initialized = false;
        private String flags = "";
        private int options;
        private Regexp regexp;

        public void setPattern(String from) {
            this.from = from;
        }

        public void setReplace(String to) {
            this.to = to;
        }

        public void setFlags(String flags) {
            this.flags = flags;
        }

        private void initialize() {
            if (this.initialized) {
                return;
            }
            this.options = TokenFilter.convertRegexOptions(this.flags);
            if (this.from == null) {
                throw new BuildException("Missing pattern in replaceregex");
            }
            this.regularExpression = new RegularExpression();
            this.regularExpression.setPattern(this.from);
            this.regexp = this.regularExpression.getRegexp(this.getProject());
            if (this.to == null) {
                this.to = "";
            }
            this.substitution = new Substitution();
            this.substitution.setExpression(this.to);
        }

        @Override
        public String filter(String line) {
            this.initialize();
            if (!this.regexp.matches(line, this.options)) {
                return line;
            }
            return this.regexp.substitute(line, this.substitution.getExpression(this.getProject()), this.options);
        }
    }

    public static class ContainsString
    extends ProjectComponent
    implements Filter {
        private String contains;

        public void setContains(String contains) {
            this.contains = contains;
        }

        @Override
        public String filter(String string) {
            if (this.contains == null) {
                throw new BuildException("Missing contains in containsstring");
            }
            if (string.contains(this.contains)) {
                return string;
            }
            return null;
        }
    }

    public static class ReplaceString
    extends ChainableReaderFilter {
        private String from;
        private String to;

        public void setFrom(String from) {
            this.from = from;
        }

        public void setTo(String to) {
            this.to = to;
        }

        @Override
        public String filter(String line) {
            if (this.from == null) {
                throw new BuildException("Missing from in stringreplace");
            }
            StringBuilder ret = new StringBuilder();
            int start = 0;
            int found = line.indexOf(this.from);
            while (found >= 0) {
                if (found > start) {
                    ret.append(line, start, found);
                }
                if (this.to != null) {
                    ret.append(this.to);
                }
                start = found + this.from.length();
                found = line.indexOf(this.from, start);
            }
            if (line.length() > start) {
                ret.append(line, start, line.length());
            }
            return ret.toString();
        }
    }

    public static abstract class ChainableReaderFilter
    extends ProjectComponent
    implements ChainableReader,
    Filter {
        private boolean byLine = true;

        public void setByLine(boolean byLine) {
            this.byLine = byLine;
        }

        @Override
        public Reader chain(Reader reader) {
            TokenFilter tokenFilter = new TokenFilter(reader);
            if (!this.byLine) {
                tokenFilter.add(new FileTokenizer());
            }
            tokenFilter.add(this);
            return tokenFilter;
        }
    }

    public static class StringTokenizer
    extends org.apache.tools.ant.util.StringTokenizer {
    }

    public static class FileTokenizer
    extends org.apache.tools.ant.util.FileTokenizer {
    }
}

