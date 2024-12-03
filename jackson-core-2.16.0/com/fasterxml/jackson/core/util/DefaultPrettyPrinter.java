/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.PrettyPrinter;
import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.Instantiatable;
import com.fasterxml.jackson.core.util.Separators;
import java.io.IOException;
import java.io.Serializable;

public class DefaultPrettyPrinter
implements PrettyPrinter,
Instantiatable<DefaultPrettyPrinter>,
Serializable {
    private static final long serialVersionUID = 1L;
    @Deprecated
    public static final SerializedString DEFAULT_ROOT_VALUE_SEPARATOR = new SerializedString(" ");
    protected Indenter _arrayIndenter = FixedSpaceIndenter.instance;
    protected Indenter _objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
    protected SerializableString _rootSeparator;
    @Deprecated
    protected boolean _spacesInObjectEntries = true;
    protected transient int _nesting;
    protected Separators _separators;
    protected String _objectFieldValueSeparatorWithSpaces;
    protected String _objectEntrySeparator;
    protected String _arrayValueSeparator;

    public DefaultPrettyPrinter() {
        this(DEFAULT_SEPARATORS);
    }

    @Deprecated
    public DefaultPrettyPrinter(String rootSeparator) {
        this(rootSeparator == null ? null : new SerializedString(rootSeparator));
    }

    @Deprecated
    public DefaultPrettyPrinter(SerializableString rootSeparator) {
        this(DEFAULT_SEPARATORS.withRootSeparator(rootSeparator.getValue()));
    }

    @Deprecated
    public DefaultPrettyPrinter(DefaultPrettyPrinter base, SerializableString rootSeparator) {
        this._arrayIndenter = base._arrayIndenter;
        this._objectIndenter = base._objectIndenter;
        this._spacesInObjectEntries = base._spacesInObjectEntries;
        this._nesting = base._nesting;
        this._separators = base._separators;
        this._objectFieldValueSeparatorWithSpaces = base._objectFieldValueSeparatorWithSpaces;
        this._objectEntrySeparator = base._objectEntrySeparator;
        this._arrayValueSeparator = base._arrayValueSeparator;
        this._rootSeparator = rootSeparator;
    }

    public DefaultPrettyPrinter(Separators separators) {
        this._separators = separators;
        this._rootSeparator = separators.getRootSeparator() == null ? null : new SerializedString(separators.getRootSeparator());
        this._objectFieldValueSeparatorWithSpaces = separators.getObjectFieldValueSpacing().apply(separators.getObjectFieldValueSeparator());
        this._objectEntrySeparator = separators.getObjectEntrySpacing().apply(separators.getObjectEntrySeparator());
        this._arrayValueSeparator = separators.getArrayValueSpacing().apply(separators.getArrayValueSeparator());
    }

    public DefaultPrettyPrinter(DefaultPrettyPrinter base) {
        this._rootSeparator = base._rootSeparator;
        this._arrayIndenter = base._arrayIndenter;
        this._objectIndenter = base._objectIndenter;
        this._spacesInObjectEntries = base._spacesInObjectEntries;
        this._nesting = base._nesting;
        this._separators = base._separators;
        this._objectFieldValueSeparatorWithSpaces = base._objectFieldValueSeparatorWithSpaces;
        this._objectEntrySeparator = base._objectEntrySeparator;
        this._arrayValueSeparator = base._arrayValueSeparator;
    }

    @Deprecated
    public DefaultPrettyPrinter withRootSeparator(SerializableString rootSeparator) {
        if (this._rootSeparator == rootSeparator || rootSeparator != null && rootSeparator.equals(this._rootSeparator)) {
            return this;
        }
        Separators separators = this._separators.withRootSeparator(rootSeparator == null ? null : rootSeparator.getValue());
        return new DefaultPrettyPrinter(this).withSeparators(separators);
    }

    @Deprecated
    public DefaultPrettyPrinter withRootSeparator(String rootSeparator) {
        return this.withRootSeparator(rootSeparator == null ? null : new SerializedString(rootSeparator));
    }

    public void indentArraysWith(Indenter i) {
        this._arrayIndenter = i == null ? NopIndenter.instance : i;
    }

    public void indentObjectsWith(Indenter i) {
        this._objectIndenter = i == null ? NopIndenter.instance : i;
    }

    public DefaultPrettyPrinter withArrayIndenter(Indenter i) {
        if (i == null) {
            i = NopIndenter.instance;
        }
        if (this._arrayIndenter == i) {
            return this;
        }
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
        pp._arrayIndenter = i;
        return pp;
    }

    public DefaultPrettyPrinter withObjectIndenter(Indenter i) {
        if (i == null) {
            i = NopIndenter.instance;
        }
        if (this._objectIndenter == i) {
            return this;
        }
        DefaultPrettyPrinter pp = new DefaultPrettyPrinter(this);
        pp._objectIndenter = i;
        return pp;
    }

    @Deprecated
    public DefaultPrettyPrinter withSpacesInObjectEntries() {
        return this._withSpaces(true);
    }

    @Deprecated
    public DefaultPrettyPrinter withoutSpacesInObjectEntries() {
        return this._withSpaces(false);
    }

    protected DefaultPrettyPrinter _withSpaces(boolean state) {
        if (this._spacesInObjectEntries == state) {
            return this;
        }
        Separators copy = this._separators.withObjectFieldValueSpacing(state ? Separators.Spacing.BOTH : Separators.Spacing.NONE);
        DefaultPrettyPrinter pp = this.withSeparators(copy);
        pp._spacesInObjectEntries = state;
        return pp;
    }

    public DefaultPrettyPrinter withSeparators(Separators separators) {
        DefaultPrettyPrinter result = new DefaultPrettyPrinter(this);
        result._separators = separators;
        result._rootSeparator = separators.getRootSeparator() == null ? null : new SerializedString(separators.getRootSeparator());
        result._objectFieldValueSeparatorWithSpaces = separators.getObjectFieldValueSpacing().apply(separators.getObjectFieldValueSeparator());
        result._objectEntrySeparator = separators.getObjectEntrySpacing().apply(separators.getObjectEntrySeparator());
        result._arrayValueSeparator = separators.getArrayValueSpacing().apply(separators.getArrayValueSeparator());
        return result;
    }

    @Override
    public DefaultPrettyPrinter createInstance() {
        if (this.getClass() != DefaultPrettyPrinter.class) {
            throw new IllegalStateException("Failed `createInstance()`: " + this.getClass().getName() + " does not override method; it has to");
        }
        return new DefaultPrettyPrinter(this);
    }

    @Override
    public void writeRootValueSeparator(JsonGenerator g) throws IOException {
        if (this._rootSeparator != null) {
            g.writeRaw(this._rootSeparator);
        }
    }

    @Override
    public void writeStartObject(JsonGenerator g) throws IOException {
        g.writeRaw('{');
        if (!this._objectIndenter.isInline()) {
            ++this._nesting;
        }
    }

    @Override
    public void beforeObjectEntries(JsonGenerator g) throws IOException {
        this._objectIndenter.writeIndentation(g, this._nesting);
    }

    @Override
    public void writeObjectFieldValueSeparator(JsonGenerator g) throws IOException {
        g.writeRaw(this._objectFieldValueSeparatorWithSpaces);
    }

    @Override
    public void writeObjectEntrySeparator(JsonGenerator g) throws IOException {
        g.writeRaw(this._objectEntrySeparator);
        this._objectIndenter.writeIndentation(g, this._nesting);
    }

    @Override
    public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
        if (!this._objectIndenter.isInline()) {
            --this._nesting;
        }
        if (nrOfEntries > 0) {
            this._objectIndenter.writeIndentation(g, this._nesting);
        } else {
            g.writeRaw(' ');
        }
        g.writeRaw('}');
    }

    @Override
    public void writeStartArray(JsonGenerator g) throws IOException {
        if (!this._arrayIndenter.isInline()) {
            ++this._nesting;
        }
        g.writeRaw('[');
    }

    @Override
    public void beforeArrayValues(JsonGenerator g) throws IOException {
        this._arrayIndenter.writeIndentation(g, this._nesting);
    }

    @Override
    public void writeArrayValueSeparator(JsonGenerator g) throws IOException {
        g.writeRaw(this._arrayValueSeparator);
        this._arrayIndenter.writeIndentation(g, this._nesting);
    }

    @Override
    public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
        if (!this._arrayIndenter.isInline()) {
            --this._nesting;
        }
        if (nrOfValues > 0) {
            this._arrayIndenter.writeIndentation(g, this._nesting);
        } else {
            g.writeRaw(' ');
        }
        g.writeRaw(']');
    }

    public static class FixedSpaceIndenter
    extends NopIndenter {
        public static final FixedSpaceIndenter instance = new FixedSpaceIndenter();

        @Override
        public void writeIndentation(JsonGenerator g, int level) throws IOException {
            g.writeRaw(' ');
        }

        @Override
        public boolean isInline() {
            return true;
        }
    }

    public static class NopIndenter
    implements Indenter,
    Serializable {
        public static final NopIndenter instance = new NopIndenter();

        @Override
        public void writeIndentation(JsonGenerator g, int level) throws IOException {
        }

        @Override
        public boolean isInline() {
            return true;
        }
    }

    public static interface Indenter {
        public void writeIndentation(JsonGenerator var1, int var2) throws IOException;

        public boolean isInline();
    }
}

