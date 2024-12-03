/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.filters;

import java.io.IOException;
import java.io.Reader;
import java.util.Vector;
import org.apache.tools.ant.filters.BaseParamFilterReader;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.types.Parameter;

public final class StripLineComments
extends BaseParamFilterReader
implements ChainableReader {
    private static final String COMMENTS_KEY = "comment";
    private Vector<String> comments = new Vector();
    private String line = null;

    public StripLineComments() {
    }

    public StripLineComments(Reader in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        if (!this.getInitialized()) {
            this.initialize();
            this.setInitialized(true);
        }
        int ch = -1;
        if (this.line != null) {
            ch = this.line.charAt(0);
            this.line = this.line.length() == 1 ? null : this.line.substring(1);
        } else {
            this.line = this.readLine();
            int commentsSize = this.comments.size();
            while (this.line != null) {
                for (int i = 0; i < commentsSize; ++i) {
                    String comment = this.comments.elementAt(i);
                    if (!this.line.startsWith(comment)) continue;
                    this.line = null;
                    break;
                }
                if (this.line != null) break;
                this.line = this.readLine();
            }
            if (this.line != null) {
                return this.read();
            }
        }
        return ch;
    }

    public void addConfiguredComment(Comment comment) {
        this.comments.addElement(comment.getValue());
    }

    private void setComments(Vector<String> comments) {
        this.comments = comments;
    }

    private Vector<String> getComments() {
        return this.comments;
    }

    @Override
    public Reader chain(Reader rdr) {
        StripLineComments newFilter = new StripLineComments(rdr);
        newFilter.setComments(this.getComments());
        newFilter.setInitialized(true);
        return newFilter;
    }

    private void initialize() {
        Parameter[] params = this.getParameters();
        if (params != null) {
            for (Parameter param : params) {
                if (!COMMENTS_KEY.equals(param.getType())) continue;
                this.comments.addElement(param.getValue());
            }
        }
    }

    public static class Comment {
        private String value;

        public final void setValue(String comment) {
            if (this.value != null) {
                throw new IllegalStateException("Comment value already set.");
            }
            this.value = comment;
        }

        public final String getValue() {
            return this.value;
        }

        public void addText(String comment) {
            this.setValue(comment);
        }
    }
}

