/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.PrettyOutput;
import cz.vutbr.web.css.Rule;
import cz.vutbr.web.css.Term;
import java.net.URL;

public interface Declaration
extends Rule<Term<?>>,
PrettyOutput,
Comparable<Declaration> {
    public boolean isImportant();

    public void setImportant(boolean var1);

    public String getProperty();

    public void setProperty(String var1);

    public Source getSource();

    public void setSource(Source var1);

    public static class Source {
        private URL url;
        private int line;
        private int position;

        public Source(URL url, int line, int position) {
            this.url = url;
            this.line = line;
            this.position = position;
        }

        public Source(Source other) {
            this.url = other.url;
            this.line = other.line;
            this.position = other.position;
        }

        public URL getUrl() {
            return this.url;
        }

        public void setUrl(URL url) {
            this.url = url;
        }

        public int getLine() {
            return this.line;
        }

        public void setLine(int line) {
            this.line = line;
        }

        public int getPosition() {
            return this.position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public String toString() {
            return (this.url == null ? "<internal>" : this.url.toString()) + ":" + this.line + ":" + this.position;
        }
    }
}

