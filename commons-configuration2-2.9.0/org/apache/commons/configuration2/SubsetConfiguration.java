/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package org.apache.commons.configuration2;

import java.util.Iterator;
import org.apache.commons.configuration2.AbstractConfiguration;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.convert.ListDelimiterHandler;
import org.apache.commons.lang3.StringUtils;

public class SubsetConfiguration
extends AbstractConfiguration {
    protected Configuration parent;
    protected String prefix;
    protected String delimiter;

    public SubsetConfiguration(Configuration parent, String prefix) {
        this(parent, prefix, null);
    }

    public SubsetConfiguration(Configuration parent, String prefix, String delimiter) {
        if (parent == null) {
            throw new IllegalArgumentException("Parent configuration must not be null!");
        }
        this.parent = parent;
        this.prefix = prefix;
        this.delimiter = delimiter;
        this.initInterpolator();
    }

    protected String getParentKey(String key) {
        if (StringUtils.isEmpty((CharSequence)key)) {
            return this.prefix;
        }
        return this.delimiter == null ? this.prefix + key : this.prefix + this.delimiter + key;
    }

    protected String getChildKey(String key) {
        if (!key.startsWith(this.prefix)) {
            throw new IllegalArgumentException("The parent key '" + key + "' is not in the subset.");
        }
        String modifiedKey = null;
        if (key.length() == this.prefix.length()) {
            modifiedKey = "";
        } else {
            int i = this.prefix.length() + (this.delimiter != null ? this.delimiter.length() : 0);
            modifiedKey = key.substring(i);
        }
        return modifiedKey;
    }

    public Configuration getParent() {
        return this.parent;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Configuration subset(String prefix) {
        return this.parent.subset(this.getParentKey(prefix));
    }

    @Override
    protected boolean isEmptyInternal() {
        return !this.getKeysInternal().hasNext();
    }

    @Override
    protected boolean containsKeyInternal(String key) {
        return this.parent.containsKey(this.getParentKey(key));
    }

    @Override
    public void addPropertyDirect(String key, Object value) {
        this.parent.addProperty(this.getParentKey(key), value);
    }

    @Override
    protected void clearPropertyDirect(String key) {
        this.parent.clearProperty(this.getParentKey(key));
    }

    @Override
    protected Object getPropertyInternal(String key) {
        return this.parent.getProperty(this.getParentKey(key));
    }

    @Override
    protected Iterator<String> getKeysInternal(String prefix) {
        return new SubsetIterator(this.parent.getKeys(this.getParentKey(prefix)));
    }

    @Override
    protected Iterator<String> getKeysInternal() {
        return new SubsetIterator(this.parent.getKeys(this.prefix));
    }

    @Override
    public void setThrowExceptionOnMissing(boolean throwExceptionOnMissing) {
        if (this.parent instanceof AbstractConfiguration) {
            ((AbstractConfiguration)this.parent).setThrowExceptionOnMissing(throwExceptionOnMissing);
        } else {
            super.setThrowExceptionOnMissing(throwExceptionOnMissing);
        }
    }

    @Override
    public boolean isThrowExceptionOnMissing() {
        if (this.parent instanceof AbstractConfiguration) {
            return ((AbstractConfiguration)this.parent).isThrowExceptionOnMissing();
        }
        return super.isThrowExceptionOnMissing();
    }

    @Override
    public ListDelimiterHandler getListDelimiterHandler() {
        return this.parent instanceof AbstractConfiguration ? ((AbstractConfiguration)this.parent).getListDelimiterHandler() : super.getListDelimiterHandler();
    }

    @Override
    public void setListDelimiterHandler(ListDelimiterHandler listDelimiterHandler) {
        if (this.parent instanceof AbstractConfiguration) {
            ((AbstractConfiguration)this.parent).setListDelimiterHandler(listDelimiterHandler);
        } else {
            super.setListDelimiterHandler(listDelimiterHandler);
        }
    }

    private void initInterpolator() {
        this.getInterpolator().setParentInterpolator(this.getParent().getInterpolator());
    }

    private class SubsetIterator
    implements Iterator<String> {
        private final Iterator<String> parentIterator;

        public SubsetIterator(Iterator<String> it) {
            this.parentIterator = it;
        }

        @Override
        public boolean hasNext() {
            return this.parentIterator.hasNext();
        }

        @Override
        public String next() {
            return SubsetConfiguration.this.getChildKey(this.parentIterator.next());
        }

        @Override
        public void remove() {
            this.parentIterator.remove();
        }
    }
}

