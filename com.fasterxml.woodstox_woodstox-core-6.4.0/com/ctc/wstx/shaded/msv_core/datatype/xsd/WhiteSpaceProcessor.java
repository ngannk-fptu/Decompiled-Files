/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.DatatypeException;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatypeImpl;
import java.io.InvalidObjectException;
import java.io.Serializable;

public abstract class WhiteSpaceProcessor
implements Serializable {
    public static final WhiteSpaceProcessor thePreserve = new Preserve();
    public static final WhiteSpaceProcessor theReplace = new Replace();
    public static final WhiteSpaceProcessor theCollapse = new Collapse();
    private static final WhiteSpaceProcessor backwardCompatibiliyHook1 = new WhiteSpaceProcessor(){

        public String process(String text) {
            throw new UnsupportedOperationException();
        }

        int tightness() {
            return 0;
        }

        public String getName() {
            return "preserve";
        }
    };
    private static final WhiteSpaceProcessor backwardCompatibiliyHook2 = new WhiteSpaceProcessor(){

        public String process(String text) {
            throw new UnsupportedOperationException();
        }

        int tightness() {
            return 2;
        }

        public String getName() {
            return "collapse";
        }
    };
    private static final WhiteSpaceProcessor backwardCompatibiliyHook3 = new WhiteSpaceProcessor(){

        public String process(String text) {
            throw new UnsupportedOperationException();
        }

        int tightness() {
            return 1;
        }

        public String getName() {
            return "replace";
        }
    };
    private static final long serialVersionUID = 1L;

    public abstract String process(String var1);

    abstract int tightness();

    public abstract String getName();

    protected static WhiteSpaceProcessor get(String name) throws DatatypeException {
        if ((name = theCollapse.process(name)).equals("preserve")) {
            return thePreserve;
        }
        if (name.equals("collapse")) {
            return theCollapse;
        }
        if (name.equals("replace")) {
            return theReplace;
        }
        throw new DatatypeException(XSDatatypeImpl.localize("WhiteSpaceProcessor.InvalidWhiteSpaceValue", name));
    }

    protected static final boolean isWhiteSpace(char ch) {
        return ch == '\t' || ch == '\n' || ch == '\r' || ch == ' ';
    }

    protected Object readResolve() throws InvalidObjectException {
        try {
            if (this.getClass() == backwardCompatibiliyHook1.getClass()) {
                return thePreserve;
            }
            if (this.getClass() == backwardCompatibiliyHook2.getClass()) {
                return theCollapse;
            }
            if (this.getClass() == backwardCompatibiliyHook3.getClass()) {
                return theReplace;
            }
            return WhiteSpaceProcessor.get(this.getName());
        }
        catch (DatatypeException bte) {
            throw new InvalidObjectException("Unknown Processing Mode");
        }
    }

    public static String replace(String str) {
        return theReplace.process(str);
    }

    public static String collapse(String str) {
        return theCollapse.process(str);
    }

    private static class Collapse
    extends WhiteSpaceProcessor {
        private static final long serialVersionUID = 1L;

        private Collapse() {
        }

        public String process(String text) {
            int len = text.length();
            StringBuffer result = new StringBuffer(len);
            boolean inStripMode = true;
            for (int i = 0; i < len; ++i) {
                char ch = text.charAt(i);
                boolean b = WhiteSpaceProcessor.isWhiteSpace(ch);
                if (inStripMode && b) continue;
                inStripMode = b;
                if (inStripMode) {
                    result.append(' ');
                    continue;
                }
                result.append(ch);
            }
            len = result.length();
            if (len > 0 && result.charAt(len - 1) == ' ') {
                result.setLength(len - 1);
            }
            return result.toString();
        }

        int tightness() {
            return 2;
        }

        public String getName() {
            return "collapse";
        }
    }

    private static class Replace
    extends WhiteSpaceProcessor {
        private static final long serialVersionUID = 1L;

        private Replace() {
        }

        public String process(String text) {
            int len = text.length();
            StringBuffer result = new StringBuffer(len);
            for (int i = 0; i < len; ++i) {
                char ch = text.charAt(i);
                if (WhiteSpaceProcessor.isWhiteSpace(ch)) {
                    result.append(' ');
                    continue;
                }
                result.append(ch);
            }
            return result.toString();
        }

        int tightness() {
            return 1;
        }

        public String getName() {
            return "replace";
        }
    }

    private static class Preserve
    extends WhiteSpaceProcessor {
        private static final long serialVersionUID = 1L;

        private Preserve() {
        }

        public String process(String text) {
            return text;
        }

        int tightness() {
            return 0;
        }

        public String getName() {
            return "preserve";
        }
    }
}

