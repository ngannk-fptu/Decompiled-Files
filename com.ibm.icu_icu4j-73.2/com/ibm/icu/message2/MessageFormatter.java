/*
 * Decompiled with CFR 0.152.
 */
package com.ibm.icu.message2;

import com.ibm.icu.message2.FormattedMessage;
import com.ibm.icu.message2.Mf2DataModel;
import com.ibm.icu.message2.Mf2DataModelFormatter;
import com.ibm.icu.message2.Mf2FunctionRegistry;
import com.ibm.icu.message2.Mf2Parser;
import com.ibm.icu.message2.Mf2Serializer;
import java.util.Locale;
import java.util.Map;

@Deprecated
public class MessageFormatter {
    private final Locale locale;
    private final String pattern;
    private final Mf2FunctionRegistry functionRegistry;
    private final Mf2DataModel dataModel;
    private final Mf2DataModelFormatter modelFormatter;

    private MessageFormatter(Builder builder) {
        this.locale = builder.locale;
        this.functionRegistry = builder.functionRegistry;
        if (builder.pattern == null && builder.dataModel == null || builder.pattern != null && builder.dataModel != null) {
            throw new IllegalArgumentException("You need to set either a pattern, or a dataModel, but not both.");
        }
        if (builder.dataModel != null) {
            this.dataModel = builder.dataModel;
            this.pattern = Mf2Serializer.dataModelToString(this.dataModel);
        } else {
            this.pattern = builder.pattern;
            Mf2Serializer tree = new Mf2Serializer();
            Mf2Parser parser = new Mf2Parser(this.pattern, tree);
            try {
                parser.parse_Message();
                this.dataModel = tree.build();
            }
            catch (Mf2Parser.ParseException pe) {
                throw new IllegalArgumentException("Parse error:\nMessage: <<" + this.pattern + ">>\nError:" + parser.getErrorMessage(pe) + "\n");
            }
        }
        this.modelFormatter = new Mf2DataModelFormatter(this.dataModel, this.locale, this.functionRegistry);
    }

    @Deprecated
    public static Builder builder() {
        return new Builder();
    }

    @Deprecated
    public Locale getLocale() {
        return this.locale;
    }

    @Deprecated
    public String getPattern() {
        return this.pattern;
    }

    @Deprecated
    public Mf2DataModel getDataModel() {
        return this.dataModel;
    }

    @Deprecated
    public String formatToString(Map<String, Object> arguments) {
        return this.modelFormatter.format(arguments);
    }

    @Deprecated
    public FormattedMessage format(Map<String, Object> arguments) {
        throw new RuntimeException("Not yet implemented.");
    }

    @Deprecated
    public static class Builder {
        private Locale locale = Locale.getDefault(Locale.Category.FORMAT);
        private String pattern = null;
        private Mf2FunctionRegistry functionRegistry = Mf2FunctionRegistry.builder().build();
        private Mf2DataModel dataModel = null;

        private Builder() {
        }

        @Deprecated
        public Builder setLocale(Locale locale) {
            this.locale = locale;
            return this;
        }

        @Deprecated
        public Builder setPattern(String pattern) {
            this.pattern = pattern;
            this.dataModel = null;
            return this;
        }

        @Deprecated
        public Builder setFunctionRegistry(Mf2FunctionRegistry functionRegistry) {
            this.functionRegistry = functionRegistry;
            return this;
        }

        @Deprecated
        public Builder setDataModel(Mf2DataModel dataModel) {
            this.dataModel = dataModel;
            this.pattern = null;
            return this;
        }

        @Deprecated
        public MessageFormatter build() {
            return new MessageFormatter(this);
        }
    }
}

