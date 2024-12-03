/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.output.support;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.jdom2.Content;
import org.jdom2.output.support.Walker;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class WalkerPRESERVE
implements Walker {
    private static final Iterator<Content> EMPTYIT = new Iterator<Content>(){

        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public Content next() {
            throw new NoSuchElementException("Cannot call next() on an empty iterator.");
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Cannot remove from an empty iterator.");
        }
    };
    private final Iterator<? extends Content> iter;
    private final boolean alltext;

    public WalkerPRESERVE(List<? extends Content> content) {
        if (content.isEmpty()) {
            this.alltext = true;
            this.iter = EMPTYIT;
        } else {
            this.iter = content.iterator();
            this.alltext = false;
        }
    }

    @Override
    public boolean isAllText() {
        return this.alltext;
    }

    @Override
    public boolean hasNext() {
        return this.iter.hasNext();
    }

    @Override
    public Content next() {
        return this.iter.next();
    }

    @Override
    public String text() {
        return null;
    }

    @Override
    public boolean isCDATA() {
        return false;
    }

    @Override
    public boolean isAllWhitespace() {
        return this.alltext;
    }
}

