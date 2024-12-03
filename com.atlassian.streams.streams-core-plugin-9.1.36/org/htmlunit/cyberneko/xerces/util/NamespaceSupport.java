/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.util;

import java.util.Objects;
import org.htmlunit.cyberneko.xerces.xni.NamespaceContext;

public class NamespaceSupport
implements NamespaceContext {
    private String[] fNamespace_ = new String[32];
    private int fNamespaceSize_;
    private int[] fContext_ = new int[8];
    private int fCurrentContext_;

    @Override
    public void reset() {
        this.fNamespaceSize_ = 0;
        this.fCurrentContext_ = 0;
        this.fContext_[this.fCurrentContext_] = this.fNamespaceSize_;
        ++this.fCurrentContext_;
    }

    @Override
    public void pushContext() {
        if (this.fCurrentContext_ + 1 == this.fContext_.length) {
            int[] contextarray = new int[this.fContext_.length * 2];
            System.arraycopy(this.fContext_, 0, contextarray, 0, this.fContext_.length);
            this.fContext_ = contextarray;
        }
        this.fContext_[++this.fCurrentContext_] = this.fNamespaceSize_;
    }

    @Override
    public void popContext() {
        this.fNamespaceSize_ = this.fContext_[this.fCurrentContext_--];
    }

    @Override
    public boolean declarePrefix(String prefix, String uri) {
        for (int i = this.fNamespaceSize_; i > this.fContext_[this.fCurrentContext_]; i -= 2) {
            if (!Objects.equals(prefix, this.fNamespace_[i - 2])) continue;
            this.fNamespace_[i - 1] = uri;
            return true;
        }
        if (this.fNamespaceSize_ == this.fNamespace_.length) {
            String[] namespacearray = new String[this.fNamespaceSize_ * 2];
            System.arraycopy(this.fNamespace_, 0, namespacearray, 0, this.fNamespaceSize_);
            this.fNamespace_ = namespacearray;
        }
        this.fNamespace_[this.fNamespaceSize_++] = prefix;
        this.fNamespace_[this.fNamespaceSize_++] = uri;
        return true;
    }

    @Override
    public String getURI(String prefix) {
        for (int i = this.fNamespaceSize_; i > 0; i -= 2) {
            if (!Objects.equals(prefix, this.fNamespace_[i - 2])) continue;
            return this.fNamespace_[i - 1];
        }
        return null;
    }

    @Override
    public int getDeclaredPrefixCount() {
        return (this.fNamespaceSize_ - this.fContext_[this.fCurrentContext_]) / 2;
    }

    @Override
    public String getDeclaredPrefixAt(int index) {
        return this.fNamespace_[this.fContext_[this.fCurrentContext_] + index * 2];
    }
}

