/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 *  org.bouncycastle.asn1.ASN1Integer
 */
package org.bouncycastle.oer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.oer.Element;
import org.bouncycastle.oer.ElementSupplier;
import org.bouncycastle.oer.Switch;

public class OERDefinition {
    static final BigInteger[] uIntMax = new BigInteger[]{new BigInteger("256"), new BigInteger("65536"), new BigInteger("4294967296"), new BigInteger("18446744073709551616")};
    static final BigInteger[][] sIntRange = new BigInteger[][]{{new BigInteger("-128"), new BigInteger("127")}, {new BigInteger("-32768"), new BigInteger("32767")}, {new BigInteger("-2147483648"), new BigInteger("2147483647")}, {new BigInteger("-9223372036854775808"), new BigInteger("9223372036854775807")}};

    public static Builder bool() {
        return new Builder(BaseType.BOOLEAN);
    }

    public static Builder integer() {
        return new Builder(BaseType.INT);
    }

    public static Builder integer(long val) {
        return new Builder(BaseType.INT).defaultValue((ASN1Encodable)new ASN1Integer(val));
    }

    public static Builder bitString(long len) {
        return new Builder(BaseType.BIT_STRING).fixedSize(len);
    }

    public static Builder integer(BigInteger lower, BigInteger upper) {
        return new Builder(BaseType.INT).range(lower, upper);
    }

    public static Builder integer(long lower, long upper) {
        return new Builder(BaseType.INT).range(BigInteger.valueOf(lower), BigInteger.valueOf(upper));
    }

    public static Builder integer(long lower, long upper, ASN1Encodable defaultValue) {
        return new Builder(BaseType.INT).range(lower, upper, defaultValue);
    }

    public static Builder nullValue() {
        return new Builder(BaseType.NULL);
    }

    public static Builder seq() {
        return new Builder(BaseType.SEQ);
    }

    public static Builder seq(Object ... items) {
        return new Builder(BaseType.SEQ).items(items);
    }

    public static Builder aSwitch(Switch aSwitch) {
        return new Builder(BaseType.Switch).decodeSwitch(aSwitch);
    }

    public static Builder enumItem(String label) {
        return new Builder(BaseType.ENUM_ITEM).label(label);
    }

    public static Builder enumItem(String label, BigInteger value) {
        return new Builder(BaseType.ENUM_ITEM).enumValue(value).label(label);
    }

    public static Builder enumeration(Object ... items) {
        return new Builder(BaseType.ENUM).items(items);
    }

    public static Builder choice(Object ... items) {
        return new Builder(BaseType.CHOICE).items(items);
    }

    public static Builder placeholder() {
        return new Builder(null);
    }

    public static Builder seqof(Object ... items) {
        return new Builder(BaseType.SEQ_OF).items(items);
    }

    public static Builder octets() {
        return new Builder(BaseType.OCTET_STRING).unbounded();
    }

    public static Builder octets(int size) {
        return new Builder(BaseType.OCTET_STRING).fixedSize(size);
    }

    public static Builder octets(int lowerBound, int upperBound) {
        return new Builder(BaseType.OCTET_STRING).range(BigInteger.valueOf(lowerBound), BigInteger.valueOf(upperBound));
    }

    public static Builder ia5String() {
        return new Builder(BaseType.IA5String);
    }

    public static Builder utf8String() {
        return new Builder(BaseType.UTF8_STRING);
    }

    public static Builder utf8String(int size) {
        return new Builder(BaseType.UTF8_STRING).rangeToMAXFrom(size);
    }

    public static Builder utf8String(int lowerBound, int upperBound) {
        return new Builder(BaseType.UTF8_STRING).range(BigInteger.valueOf(lowerBound), BigInteger.valueOf(upperBound));
    }

    public static Builder opaque() {
        return new Builder(BaseType.OPAQUE);
    }

    public static List<Object> optional(Object ... items) {
        return new OptionalList(Arrays.asList(items));
    }

    public static ExtensionList extension(Object ... items) {
        return new ExtensionList(1, Arrays.asList(items));
    }

    public static ExtensionList extension(int block, Object ... items) {
        return new ExtensionList(block, Arrays.asList(items));
    }

    public static Builder deferred(ElementSupplier elementSupplier) {
        return new Builder(BaseType.Supplier).elementSupplier(elementSupplier);
    }

    public static enum BaseType {
        SEQ,
        SEQ_OF,
        CHOICE,
        ENUM,
        INT,
        OCTET_STRING,
        OPAQUE,
        UTF8_STRING,
        BIT_STRING,
        NULL,
        EXTENSION,
        ENUM_ITEM,
        BOOLEAN,
        IS0646String,
        PrintableString,
        NumericString,
        BMPString,
        UniversalString,
        IA5String,
        VisibleString,
        Switch,
        Supplier;

    }

    public static class Builder {
        protected final BaseType baseType;
        protected ArrayList<Builder> children = new ArrayList();
        protected boolean explicit = true;
        protected String typeName;
        protected String label;
        protected BigInteger upperBound;
        protected BigInteger lowerBound;
        protected BigInteger enumValue;
        protected ASN1Encodable defaultValue;
        protected Builder placeholderValue;
        protected Boolean inScope;
        protected Switch aSwitch;
        protected ArrayList<ASN1Encodable> validSwitchValues = new ArrayList();
        protected ElementSupplier elementSupplier;
        protected boolean mayRecurse;
        protected Map<String, ElementSupplier> supplierMap = new HashMap<String, ElementSupplier>();
        protected int block;
        private final ItemProvider defaultItemProvider = new ItemProvider(){

            @Override
            public Builder existingChild(int index, Builder existingChild) {
                return existingChild.copy(defaultItemProvider);
            }
        };

        public Builder(BaseType baseType) {
            this.baseType = baseType;
        }

        private Builder copy(ItemProvider provider) {
            Builder b = new Builder(this.baseType);
            int t = 0;
            for (Builder child : this.children) {
                b.children.add(provider.existingChild(t++, child));
            }
            b.explicit = this.explicit;
            b.label = this.label;
            b.upperBound = this.upperBound;
            b.lowerBound = this.lowerBound;
            b.defaultValue = this.defaultValue;
            b.enumValue = this.enumValue;
            b.inScope = this.inScope;
            b.aSwitch = this.aSwitch;
            b.validSwitchValues = new ArrayList<ASN1Encodable>(this.validSwitchValues);
            b.elementSupplier = this.elementSupplier;
            b.mayRecurse = this.mayRecurse;
            b.typeName = this.typeName;
            b.supplierMap = new HashMap<String, ElementSupplier>(this.supplierMap);
            b.block = this.block;
            return b;
        }

        protected Builder block(int block) {
            Builder b = this.copy();
            b.block = block;
            return b;
        }

        public Builder copy() {
            return this.copy(this.defaultItemProvider);
        }

        public Builder elementSupplier(ElementSupplier elementSupplier) {
            Builder b = this.copy();
            b.elementSupplier = elementSupplier;
            return b;
        }

        public Builder validSwitchValue(ASN1Encodable ... values) {
            Builder b = this.copy();
            b.validSwitchValues.addAll(Arrays.asList(values));
            return b;
        }

        public Builder inScope(boolean scope) {
            Builder b = this.copy();
            b.inScope = scope;
            return b;
        }

        public Builder limitScopeTo(String ... label) {
            Builder b = this.copy();
            HashSet<String> labels = new HashSet<String>();
            labels.addAll(Arrays.asList(label));
            ArrayList<Builder> scopeLimited = new ArrayList<Builder>();
            for (Builder child : this.children) {
                scopeLimited.add(child.copy().inScope(labels.contains(child.label)));
            }
            b.children = scopeLimited;
            return b;
        }

        public Builder typeName(String name) {
            Builder b = this.copy();
            b.typeName = name;
            if (b.label == null) {
                b.label = name;
            }
            return b;
        }

        public Builder unbounded() {
            Builder b = this.copy();
            b.lowerBound = null;
            b.upperBound = null;
            return b;
        }

        public Builder decodeSwitch(Switch aSwitch) {
            Builder cpy = this.copy();
            cpy.aSwitch = aSwitch;
            return cpy;
        }

        public Builder labelPrefix(String prefix) {
            Builder cpy = this.copy();
            cpy.label = prefix + " " + this.label;
            return cpy;
        }

        public Builder explicit(boolean explicit) {
            Builder b = this.copy();
            b.explicit = explicit;
            return b;
        }

        public Builder defaultValue(ASN1Encodable defaultValue) {
            Builder b = this.copy();
            b.defaultValue = defaultValue;
            return b;
        }

        protected Builder wrap(boolean explicit, Object item) {
            if (item instanceof Builder) {
                return ((Builder)item).explicit(explicit);
            }
            if (item instanceof BaseType) {
                return new Builder((BaseType)((Object)item)).explicit(explicit);
            }
            if (item instanceof String) {
                return OERDefinition.enumItem((String)item);
            }
            throw new IllegalStateException("Unable to wrap item in builder");
        }

        protected void addExtensions(Builder b, ExtensionList extensionList) {
            if (extensionList.isEmpty()) {
                Builder stub = new Builder(BaseType.EXTENSION);
                stub.block = extensionList.block;
                b.children.add(stub);
                return;
            }
            for (Object item : extensionList) {
                if (item instanceof OptionalList) {
                    this.addOptionals(b, extensionList.block, (OptionalList)item);
                    continue;
                }
                Builder wrapped = this.wrap(true, item);
                wrapped.block = extensionList.block;
                b.children.add(wrapped);
            }
        }

        protected void addOptionals(Builder b, int block, OptionalList optionalList) {
            for (Object o : optionalList) {
                if (o instanceof ExtensionList) {
                    this.addExtensions(b, (ExtensionList)o);
                    continue;
                }
                Builder wrapped = this.wrap(false, o);
                wrapped.block = block;
                b.children.add(wrapped);
            }
        }

        public Builder items(Object ... items) {
            Builder b = this.copy();
            for (int i = 0; i != items.length; ++i) {
                Object item = items[i];
                if (item instanceof ExtensionList) {
                    this.addExtensions(b, (ExtensionList)item);
                    continue;
                }
                if (item instanceof OptionalList) {
                    this.addOptionals(b, b.block, (OptionalList)item);
                    continue;
                }
                if (item.getClass().isArray()) {
                    for (int t = 0; t < ((Object[])item).length; ++t) {
                        b.children.add(this.wrap(true, ((Object[])item)[t]));
                    }
                    continue;
                }
                b.children.add(this.wrap(true, item));
            }
            return b;
        }

        public Builder label(String label) {
            Builder newBuilder = this.copy();
            newBuilder.label = label;
            return newBuilder;
        }

        public Builder mayRecurse(boolean val) {
            Builder b = this.copy();
            b.mayRecurse = val;
            return b;
        }

        public Element build() {
            ArrayList<Element> children = new ArrayList<Element>();
            boolean hasExtensions = false;
            if (this.baseType == BaseType.ENUM) {
                int ordinal = 0;
                HashSet<BigInteger> dupCheck = new HashSet<BigInteger>();
                for (int t = 0; t < this.children.size(); ++t) {
                    Builder b = this.children.get(t);
                    if (b.enumValue == null) {
                        b.enumValue = BigInteger.valueOf(ordinal);
                        ++ordinal;
                    }
                    if (dupCheck.contains(b.enumValue)) {
                        throw new IllegalStateException("duplicate enum value at index " + t);
                    }
                    dupCheck.add(b.enumValue);
                }
            }
            int optionals = 0;
            boolean defaultValuesInChildren = false;
            for (Builder child : this.children) {
                if (!hasExtensions && child.block > 0) {
                    hasExtensions = true;
                }
                if (!child.explicit) {
                    ++optionals;
                }
                if (!defaultValuesInChildren && child.defaultValue != null) {
                    defaultValuesInChildren = true;
                }
                children.add(child.build());
            }
            return new Element(this.baseType, children, this.defaultValue == null && this.explicit, this.label, this.lowerBound, this.upperBound, hasExtensions, this.enumValue, this.defaultValue, this.aSwitch, this.validSwitchValues.isEmpty() ? null : this.validSwitchValues, this.elementSupplier, this.mayRecurse, this.typeName, this.supplierMap.isEmpty() ? null : this.supplierMap, this.block, optionals, defaultValuesInChildren);
        }

        public Builder range(BigInteger lower, BigInteger upper) {
            Builder newBuilder = this.copy();
            newBuilder.lowerBound = lower;
            newBuilder.upperBound = upper;
            return newBuilder;
        }

        public Builder rangeToMAXFrom(long from) {
            Builder b = this.copy();
            b.lowerBound = BigInteger.valueOf(from);
            b.upperBound = null;
            return b;
        }

        public Builder rangeZeroTo(long max) {
            Builder b = this.copy();
            b.upperBound = BigInteger.valueOf(max);
            b.lowerBound = BigInteger.ZERO;
            return b;
        }

        public Builder fixedSize(long size) {
            Builder b = this.copy();
            b.upperBound = BigInteger.valueOf(size);
            b.lowerBound = BigInteger.valueOf(size);
            return b;
        }

        public Builder range(long lower, long upper, ASN1Encodable defaultIntValue) {
            Builder b = this.copy();
            b.lowerBound = BigInteger.valueOf(lower);
            b.upperBound = BigInteger.valueOf(upper);
            b.defaultValue = defaultIntValue;
            return b;
        }

        public Builder enumValue(BigInteger value) {
            Builder b = this.copy();
            this.enumValue = value;
            return b;
        }

        public Builder replaceChild(final int index, final Builder newItem) {
            return this.copy(new ItemProvider(){

                @Override
                public Builder existingChild(int _index, Builder existingChild) {
                    return index == _index ? newItem : existingChild;
                }
            });
        }
    }

    private static class ExtensionList
    extends ArrayList<Object> {
        protected final int block;

        public ExtensionList(int block, List<Object> asList) {
            this.block = block;
            this.addAll(asList);
        }
    }

    public static interface ItemProvider {
        public Builder existingChild(int var1, Builder var2);
    }

    public static class MutableBuilder
    extends Builder {
        private boolean frozen = false;

        public MutableBuilder(BaseType baseType) {
            super(baseType);
        }

        @Override
        public MutableBuilder label(String label) {
            this.label = label;
            return this;
        }

        public MutableBuilder addItemsAndFreeze(Builder ... items) {
            if (this.frozen) {
                throw new IllegalStateException("build cannot be modified and must be copied only");
            }
            for (int i = 0; i != items.length; ++i) {
                Builder item = items[i];
                if (item instanceof OptionalList) {
                    Iterator it = ((List)((Object)item)).iterator();
                    while (it.hasNext()) {
                        this.children.add(this.wrap(false, it.next()));
                    }
                    continue;
                }
                if (item.getClass().isArray()) {
                    for (Object o : (Object[])item) {
                        this.children.add(this.wrap(true, o));
                    }
                    continue;
                }
                this.children.add(this.wrap(true, item));
            }
            this.frozen = true;
            return this;
        }
    }

    private static class OptionalList
    extends ArrayList<Object> {
        public OptionalList(List<Object> asList) {
            this.addAll(asList);
        }
    }
}

