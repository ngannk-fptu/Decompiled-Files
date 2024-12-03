/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.core.SdkField
 *  software.amazon.awssdk.core.SdkPojo
 *  software.amazon.awssdk.core.protocol.MarshallLocation
 *  software.amazon.awssdk.core.protocol.MarshallingType
 *  software.amazon.awssdk.core.traits.LocationTrait
 *  software.amazon.awssdk.core.traits.RequiredTrait
 *  software.amazon.awssdk.core.traits.Trait
 *  software.amazon.awssdk.utils.ToString
 *  software.amazon.awssdk.utils.builder.CopyableBuilder
 *  software.amazon.awssdk.utils.builder.ToCopyableBuilder
 */
package software.amazon.awssdk.services.s3.model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import software.amazon.awssdk.core.SdkField;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.RequiredTrait;
import software.amazon.awssdk.core.traits.Trait;
import software.amazon.awssdk.services.s3.model.ExpressionType;
import software.amazon.awssdk.services.s3.model.InputSerialization;
import software.amazon.awssdk.services.s3.model.OutputSerialization;
import software.amazon.awssdk.utils.ToString;
import software.amazon.awssdk.utils.builder.CopyableBuilder;
import software.amazon.awssdk.utils.builder.ToCopyableBuilder;

public final class SelectParameters
implements SdkPojo,
Serializable,
ToCopyableBuilder<Builder, SelectParameters> {
    private static final SdkField<InputSerialization> INPUT_SERIALIZATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("InputSerialization").getter(SelectParameters.getter(SelectParameters::inputSerialization)).setter(SelectParameters.setter(Builder::inputSerialization)).constructor(InputSerialization::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("InputSerialization").unmarshallLocationName("InputSerialization").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> EXPRESSION_TYPE_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("ExpressionType").getter(SelectParameters.getter(SelectParameters::expressionTypeAsString)).setter(SelectParameters.setter(Builder::expressionType)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("ExpressionType").unmarshallLocationName("ExpressionType").build(), RequiredTrait.create()}).build();
    private static final SdkField<String> EXPRESSION_FIELD = SdkField.builder((MarshallingType)MarshallingType.STRING).memberName("Expression").getter(SelectParameters.getter(SelectParameters::expression)).setter(SelectParameters.setter(Builder::expression)).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("Expression").unmarshallLocationName("Expression").build(), RequiredTrait.create()}).build();
    private static final SdkField<OutputSerialization> OUTPUT_SERIALIZATION_FIELD = SdkField.builder((MarshallingType)MarshallingType.SDK_POJO).memberName("OutputSerialization").getter(SelectParameters.getter(SelectParameters::outputSerialization)).setter(SelectParameters.setter(Builder::outputSerialization)).constructor(OutputSerialization::builder).traits(new Trait[]{LocationTrait.builder().location(MarshallLocation.PAYLOAD).locationName("OutputSerialization").unmarshallLocationName("OutputSerialization").build(), RequiredTrait.create()}).build();
    private static final List<SdkField<?>> SDK_FIELDS = Collections.unmodifiableList(Arrays.asList(INPUT_SERIALIZATION_FIELD, EXPRESSION_TYPE_FIELD, EXPRESSION_FIELD, OUTPUT_SERIALIZATION_FIELD));
    private static final long serialVersionUID = 1L;
    private final InputSerialization inputSerialization;
    private final String expressionType;
    private final String expression;
    private final OutputSerialization outputSerialization;

    private SelectParameters(BuilderImpl builder) {
        this.inputSerialization = builder.inputSerialization;
        this.expressionType = builder.expressionType;
        this.expression = builder.expression;
        this.outputSerialization = builder.outputSerialization;
    }

    public final InputSerialization inputSerialization() {
        return this.inputSerialization;
    }

    public final ExpressionType expressionType() {
        return ExpressionType.fromValue(this.expressionType);
    }

    public final String expressionTypeAsString() {
        return this.expressionType;
    }

    public final String expression() {
        return this.expression;
    }

    public final OutputSerialization outputSerialization() {
        return this.outputSerialization;
    }

    public Builder toBuilder() {
        return new BuilderImpl(this);
    }

    public static Builder builder() {
        return new BuilderImpl();
    }

    public static Class<? extends Builder> serializableBuilderClass() {
        return BuilderImpl.class;
    }

    public final int hashCode() {
        int hashCode = 1;
        hashCode = 31 * hashCode + Objects.hashCode(this.inputSerialization());
        hashCode = 31 * hashCode + Objects.hashCode(this.expressionTypeAsString());
        hashCode = 31 * hashCode + Objects.hashCode(this.expression());
        hashCode = 31 * hashCode + Objects.hashCode(this.outputSerialization());
        return hashCode;
    }

    public final boolean equals(Object obj) {
        return this.equalsBySdkFields(obj);
    }

    public final boolean equalsBySdkFields(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SelectParameters)) {
            return false;
        }
        SelectParameters other = (SelectParameters)obj;
        return Objects.equals(this.inputSerialization(), other.inputSerialization()) && Objects.equals(this.expressionTypeAsString(), other.expressionTypeAsString()) && Objects.equals(this.expression(), other.expression()) && Objects.equals(this.outputSerialization(), other.outputSerialization());
    }

    public final String toString() {
        return ToString.builder((String)"SelectParameters").add("InputSerialization", (Object)this.inputSerialization()).add("ExpressionType", (Object)this.expressionTypeAsString()).add("Expression", (Object)this.expression()).add("OutputSerialization", (Object)this.outputSerialization()).build();
    }

    public final <T> Optional<T> getValueForField(String fieldName, Class<T> clazz) {
        switch (fieldName) {
            case "InputSerialization": {
                return Optional.ofNullable(clazz.cast(this.inputSerialization()));
            }
            case "ExpressionType": {
                return Optional.ofNullable(clazz.cast(this.expressionTypeAsString()));
            }
            case "Expression": {
                return Optional.ofNullable(clazz.cast(this.expression()));
            }
            case "OutputSerialization": {
                return Optional.ofNullable(clazz.cast(this.outputSerialization()));
            }
        }
        return Optional.empty();
    }

    public final List<SdkField<?>> sdkFields() {
        return SDK_FIELDS;
    }

    private static <T> Function<Object, T> getter(Function<SelectParameters, T> g) {
        return obj -> g.apply((SelectParameters)obj);
    }

    private static <T> BiConsumer<Object, T> setter(BiConsumer<Builder, T> s) {
        return (obj, val) -> s.accept((Builder)obj, val);
    }

    static final class BuilderImpl
    implements Builder {
        private InputSerialization inputSerialization;
        private String expressionType;
        private String expression;
        private OutputSerialization outputSerialization;

        private BuilderImpl() {
        }

        private BuilderImpl(SelectParameters model) {
            this.inputSerialization(model.inputSerialization);
            this.expressionType(model.expressionType);
            this.expression(model.expression);
            this.outputSerialization(model.outputSerialization);
        }

        public final InputSerialization.Builder getInputSerialization() {
            return this.inputSerialization != null ? this.inputSerialization.toBuilder() : null;
        }

        public final void setInputSerialization(InputSerialization.BuilderImpl inputSerialization) {
            this.inputSerialization = inputSerialization != null ? inputSerialization.build() : null;
        }

        @Override
        public final Builder inputSerialization(InputSerialization inputSerialization) {
            this.inputSerialization = inputSerialization;
            return this;
        }

        public final String getExpressionType() {
            return this.expressionType;
        }

        public final void setExpressionType(String expressionType) {
            this.expressionType = expressionType;
        }

        @Override
        public final Builder expressionType(String expressionType) {
            this.expressionType = expressionType;
            return this;
        }

        @Override
        public final Builder expressionType(ExpressionType expressionType) {
            this.expressionType(expressionType == null ? null : expressionType.toString());
            return this;
        }

        public final String getExpression() {
            return this.expression;
        }

        public final void setExpression(String expression) {
            this.expression = expression;
        }

        @Override
        public final Builder expression(String expression) {
            this.expression = expression;
            return this;
        }

        public final OutputSerialization.Builder getOutputSerialization() {
            return this.outputSerialization != null ? this.outputSerialization.toBuilder() : null;
        }

        public final void setOutputSerialization(OutputSerialization.BuilderImpl outputSerialization) {
            this.outputSerialization = outputSerialization != null ? outputSerialization.build() : null;
        }

        @Override
        public final Builder outputSerialization(OutputSerialization outputSerialization) {
            this.outputSerialization = outputSerialization;
            return this;
        }

        public SelectParameters build() {
            return new SelectParameters(this);
        }

        public List<SdkField<?>> sdkFields() {
            return SDK_FIELDS;
        }
    }

    public static interface Builder
    extends SdkPojo,
    CopyableBuilder<Builder, SelectParameters> {
        public Builder inputSerialization(InputSerialization var1);

        default public Builder inputSerialization(Consumer<InputSerialization.Builder> inputSerialization) {
            return this.inputSerialization((InputSerialization)((InputSerialization.Builder)InputSerialization.builder().applyMutation(inputSerialization)).build());
        }

        public Builder expressionType(String var1);

        public Builder expressionType(ExpressionType var1);

        public Builder expression(String var1);

        public Builder outputSerialization(OutputSerialization var1);

        default public Builder outputSerialization(Consumer<OutputSerialization.Builder> outputSerialization) {
            return this.outputSerialization((OutputSerialization)((OutputSerialization.Builder)OutputSerialization.builder().applyMutation(outputSerialization)).build());
        }
    }
}

