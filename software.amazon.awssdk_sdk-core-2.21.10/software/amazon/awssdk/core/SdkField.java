/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 */
package software.amazon.awssdk.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.core.SdkPojo;
import software.amazon.awssdk.core.protocol.MarshallLocation;
import software.amazon.awssdk.core.protocol.MarshallingType;
import software.amazon.awssdk.core.traits.DefaultValueTrait;
import software.amazon.awssdk.core.traits.LocationTrait;
import software.amazon.awssdk.core.traits.Trait;

@SdkProtectedApi
public final class SdkField<TypeT> {
    private final String memberName;
    private final MarshallingType<? super TypeT> marshallingType;
    private final MarshallLocation location;
    private final String locationName;
    private final String unmarshallLocationName;
    private final Supplier<SdkPojo> constructor;
    private final BiConsumer<Object, TypeT> setter;
    private final Function<Object, TypeT> getter;
    private final Map<Class<? extends Trait>, Trait> traits;

    private SdkField(Builder<TypeT> builder) {
        this.memberName = ((Builder)builder).memberName;
        this.marshallingType = ((Builder)builder).marshallingType;
        this.traits = new HashMap<Class<? extends Trait>, Trait>(((Builder)builder).traits);
        this.constructor = ((Builder)builder).constructor;
        this.setter = ((Builder)builder).setter;
        this.getter = ((Builder)builder).getter;
        LocationTrait locationTrait = this.getTrait(LocationTrait.class);
        this.location = locationTrait.location();
        this.locationName = locationTrait.locationName();
        this.unmarshallLocationName = locationTrait.unmarshallLocationName();
    }

    public String memberName() {
        return this.memberName;
    }

    public MarshallingType<? super TypeT> marshallingType() {
        return this.marshallingType;
    }

    public MarshallLocation location() {
        return this.location;
    }

    public String locationName() {
        return this.locationName;
    }

    public String unmarshallLocationName() {
        return this.unmarshallLocationName;
    }

    public Supplier<SdkPojo> constructor() {
        return this.constructor;
    }

    public <T extends Trait> T getTrait(Class<T> clzz) {
        return (T)this.traits.get(clzz);
    }

    public <T extends Trait> Optional<T> getOptionalTrait(Class<T> clzz) {
        return Optional.ofNullable(this.traits.get(clzz));
    }

    public <T extends Trait> T getRequiredTrait(Class<T> clzz) throws IllegalStateException {
        Trait trait = this.traits.get(clzz);
        if (trait == null) {
            throw new IllegalStateException(this.memberName + " member is missing " + clzz.getSimpleName());
        }
        return (T)trait;
    }

    public boolean containsTrait(Class<? extends Trait> clzz) {
        return this.traits.containsKey(clzz);
    }

    private TypeT get(Object pojo) {
        return this.getter.apply(pojo);
    }

    public TypeT getValueOrDefault(Object pojo) {
        TypeT val = this.get(pojo);
        DefaultValueTrait trait = this.getTrait(DefaultValueTrait.class);
        return (TypeT)(trait == null ? val : trait.resolveValue(val));
    }

    public void set(Object pojo, Object val) {
        this.setter.accept(pojo, val);
    }

    public static <TypeT> Builder<TypeT> builder(MarshallingType<? super TypeT> marshallingType) {
        return new Builder(marshallingType);
    }

    public static final class Builder<TypeT> {
        private final MarshallingType<? super TypeT> marshallingType;
        private String memberName;
        private Supplier<SdkPojo> constructor;
        private BiConsumer<Object, TypeT> setter;
        private Function<Object, TypeT> getter;
        private final Map<Class<? extends Trait>, Trait> traits = new HashMap<Class<? extends Trait>, Trait>();

        private Builder(MarshallingType<? super TypeT> marshallingType) {
            this.marshallingType = marshallingType;
        }

        public Builder<TypeT> memberName(String memberName) {
            this.memberName = memberName;
            return this;
        }

        public Builder<TypeT> constructor(Supplier<SdkPojo> constructor) {
            this.constructor = constructor;
            return this;
        }

        public Builder<TypeT> setter(BiConsumer<Object, TypeT> setter) {
            this.setter = setter;
            return this;
        }

        public Builder<TypeT> getter(Function<Object, TypeT> getter) {
            this.getter = getter;
            return this;
        }

        public Builder<TypeT> traits(Trait ... traits) {
            Arrays.stream(traits).forEach(t -> this.traits.put((Class<? extends Trait>)t.getClass(), (Trait)t));
            return this;
        }

        public SdkField<TypeT> build() {
            return new SdkField(this);
        }
    }
}

