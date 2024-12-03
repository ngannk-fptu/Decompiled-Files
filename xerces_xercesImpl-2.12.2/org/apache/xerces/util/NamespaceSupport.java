/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.NamespaceContext;

public class NamespaceSupport
implements NamespaceContext {
    protected String[] fNamespace = new String[32];
    protected int fNamespaceSize;
    protected int[] fContext = new int[8];
    protected int fCurrentContext;
    protected String[] fPrefixes = new String[16];

    public NamespaceSupport() {
    }

    public NamespaceSupport(NamespaceContext namespaceContext) {
        this.pushContext();
        Enumeration enumeration = namespaceContext.getAllPrefixes();
        while (enumeration.hasMoreElements()) {
            String string = (String)enumeration.nextElement();
            String string2 = namespaceContext.getURI(string);
            this.declarePrefix(string, string2);
        }
    }

    @Override
    public void reset() {
        this.fNamespaceSize = 0;
        this.fCurrentContext = 0;
        this.fContext[this.fCurrentContext] = this.fNamespaceSize;
        this.fNamespace[this.fNamespaceSize++] = XMLSymbols.PREFIX_XML;
        this.fNamespace[this.fNamespaceSize++] = NamespaceContext.XML_URI;
        this.fNamespace[this.fNamespaceSize++] = XMLSymbols.PREFIX_XMLNS;
        this.fNamespace[this.fNamespaceSize++] = NamespaceContext.XMLNS_URI;
        ++this.fCurrentContext;
    }

    @Override
    public void pushContext() {
        if (this.fCurrentContext + 1 == this.fContext.length) {
            int[] nArray = new int[this.fContext.length * 2];
            System.arraycopy(this.fContext, 0, nArray, 0, this.fContext.length);
            this.fContext = nArray;
        }
        this.fContext[++this.fCurrentContext] = this.fNamespaceSize;
    }

    @Override
    public void popContext() {
        this.fNamespaceSize = this.fContext[this.fCurrentContext--];
    }

    @Override
    public boolean declarePrefix(String string, String string2) {
        if (string == XMLSymbols.PREFIX_XML || string == XMLSymbols.PREFIX_XMLNS) {
            return false;
        }
        for (int i = this.fNamespaceSize; i > this.fContext[this.fCurrentContext]; i -= 2) {
            if (this.fNamespace[i - 2] != string) continue;
            this.fNamespace[i - 1] = string2;
            return true;
        }
        if (this.fNamespaceSize == this.fNamespace.length) {
            String[] stringArray = new String[this.fNamespaceSize * 2];
            System.arraycopy(this.fNamespace, 0, stringArray, 0, this.fNamespaceSize);
            this.fNamespace = stringArray;
        }
        this.fNamespace[this.fNamespaceSize++] = string;
        this.fNamespace[this.fNamespaceSize++] = string2;
        return true;
    }

    @Override
    public String getURI(String string) {
        for (int i = this.fNamespaceSize; i > 0; i -= 2) {
            if (this.fNamespace[i - 2] != string) continue;
            return this.fNamespace[i - 1];
        }
        return null;
    }

    @Override
    public String getPrefix(String string) {
        for (int i = this.fNamespaceSize; i > 0; i -= 2) {
            if (this.fNamespace[i - 1] != string || this.getURI(this.fNamespace[i - 2]) != string) continue;
            return this.fNamespace[i - 2];
        }
        return null;
    }

    @Override
    public int getDeclaredPrefixCount() {
        return (this.fNamespaceSize - this.fContext[this.fCurrentContext]) / 2;
    }

    @Override
    public String getDeclaredPrefixAt(int n) {
        return this.fNamespace[this.fContext[this.fCurrentContext] + n * 2];
    }

    @Override
    public Enumeration getAllPrefixes() {
        Object object;
        int n = 0;
        if (this.fPrefixes.length < this.fNamespace.length / 2) {
            object = new String[this.fNamespaceSize];
            this.fPrefixes = object;
        }
        object = null;
        boolean bl = true;
        for (int i = 2; i < this.fNamespaceSize - 2; i += 2) {
            object = this.fNamespace[i + 2];
            for (int j = 0; j < n; ++j) {
                if (this.fPrefixes[j] != object) continue;
                bl = false;
                break;
            }
            if (bl) {
                this.fPrefixes[n++] = object;
            }
            bl = true;
        }
        return new Prefixes(this.fPrefixes, n);
    }

    public boolean containsPrefix(String string) {
        for (int i = this.fNamespaceSize; i > 0; i -= 2) {
            if (this.fNamespace[i - 2] != string) continue;
            return true;
        }
        return false;
    }

    protected final class Prefixes
    implements Enumeration {
        private String[] prefixes;
        private int counter = 0;
        private int size = 0;

        public Prefixes(String[] stringArray, int n) {
            this.prefixes = stringArray;
            this.size = n;
        }

        @Override
        public boolean hasMoreElements() {
            return this.counter < this.size;
        }

        public Object nextElement() {
            if (this.counter < this.size) {
                return NamespaceSupport.this.fPrefixes[this.counter++];
            }
            throw new NoSuchElementException("Illegal access to Namespace prefixes enumeration.");
        }

        public String toString() {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < this.size; ++i) {
                stringBuffer.append(this.prefixes[i]);
                stringBuffer.append(' ');
            }
            return stringBuffer.toString();
        }
    }
}

