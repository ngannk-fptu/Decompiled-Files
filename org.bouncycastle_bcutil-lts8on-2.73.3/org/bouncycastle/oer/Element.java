/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.ASN1Encodable
 */
package org.bouncycastle.oer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.oer.ElementSupplier;
import org.bouncycastle.oer.OERDefinition;
import org.bouncycastle.oer.Switch;

public class Element {
    private final OERDefinition.BaseType baseType;
    private final List<Element> children;
    private final boolean explicit;
    private final String label;
    private final BigInteger lowerBound;
    private final BigInteger upperBound;
    private final boolean extensionsInDefinition;
    private final BigInteger enumValue;
    private final ASN1Encodable defaultValue;
    private final Switch aSwitch;
    private final boolean defaultValuesInChildren;
    private List<Element> optionalChildrenInOrder;
    private List<ASN1Encodable> validSwitchValues;
    private final ElementSupplier elementSupplier;
    private final boolean mayRecurse;
    private final String typeName;
    private final Map<String, ElementSupplier> supplierMap;
    private Element parent;
    private final int optionals;
    private final int block;

    public Element(OERDefinition.BaseType baseType, List<Element> children, boolean explicit, String label, BigInteger lowerBound, BigInteger upperBound, boolean extensionsInDefinition, BigInteger enumValue, ASN1Encodable defaultValue, Switch aSwitch, List<ASN1Encodable> switchValues, ElementSupplier elementSupplier, boolean mayRecurse, String typeName, Map<String, ElementSupplier> supplierMap, int block, int optionals, boolean defaultValuesInChildren) {
        this.baseType = baseType;
        this.children = children;
        this.explicit = explicit;
        this.label = label;
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.extensionsInDefinition = extensionsInDefinition;
        this.enumValue = enumValue;
        this.defaultValue = defaultValue;
        this.aSwitch = aSwitch;
        this.validSwitchValues = switchValues != null ? Collections.unmodifiableList(switchValues) : null;
        this.elementSupplier = elementSupplier;
        this.mayRecurse = mayRecurse;
        this.typeName = typeName;
        this.block = block;
        this.optionals = optionals;
        this.defaultValuesInChildren = defaultValuesInChildren;
        this.supplierMap = supplierMap == null ? Collections.emptyMap() : supplierMap;
        for (Element e : children) {
            e.parent = this;
        }
    }

    public Element(Element element, Element parent) {
        this.baseType = element.baseType;
        this.children = new ArrayList<Element>(element.children);
        this.explicit = element.explicit;
        this.label = element.label;
        this.lowerBound = element.lowerBound;
        this.upperBound = element.upperBound;
        this.extensionsInDefinition = element.extensionsInDefinition;
        this.enumValue = element.enumValue;
        this.defaultValue = element.defaultValue;
        this.aSwitch = element.aSwitch;
        this.validSwitchValues = element.validSwitchValues;
        this.elementSupplier = element.elementSupplier;
        this.mayRecurse = element.mayRecurse;
        this.typeName = element.typeName;
        this.supplierMap = element.supplierMap;
        this.parent = parent;
        this.block = element.block;
        this.optionals = element.optionals;
        this.defaultValuesInChildren = element.defaultValuesInChildren;
        for (Element e : this.children) {
            e.parent = this;
        }
    }

    public static Element expandDeferredDefinition(Element e, Element parent) {
        if (e.elementSupplier != null && (e = e.elementSupplier.build()).getParent() != parent) {
            e = new Element(e, parent);
        }
        return e;
    }

    public String rangeExpression() {
        return "(" + (this.getLowerBound() != null ? this.getLowerBound().toString() : "MIN") + " ... " + (this.getUpperBound() != null ? this.getUpperBound().toString() : "MAX") + ")";
    }

    public String appendLabel(String s) {
        return "[" + (this.getLabel() == null ? "" : this.getLabel()) + (this.isExplicit() ? " (E)" : "") + "] " + s;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public List<Element> optionalOrDefaultChildrenInOrder() {
        Element element = this;
        synchronized (element) {
            if (this.getOptionalChildrenInOrder() == null) {
                ArrayList<Element> optList = new ArrayList<Element>();
                for (Element e : this.getChildren()) {
                    if (e.isExplicit() && e.getDefaultValue() == null) continue;
                    optList.add(e);
                }
                this.optionalChildrenInOrder = Collections.unmodifiableList(optList);
            }
            return this.getOptionalChildrenInOrder();
        }
    }

    public boolean isUnbounded() {
        return this.getUpperBound() == null && this.getLowerBound() == null;
    }

    public boolean isLowerRangeZero() {
        return BigInteger.ZERO.equals(this.getLowerBound());
    }

    public boolean isUnsignedWithRange() {
        return this.isLowerRangeZero() && this.getUpperBound() != null && BigInteger.ZERO.compareTo(this.getUpperBound()) < 0;
    }

    public boolean canBeNegative() {
        return this.getLowerBound() != null && BigInteger.ZERO.compareTo(this.getLowerBound()) > 0;
    }

    public int intBytesForRange() {
        block6: {
            if (this.getLowerBound() == null || this.getUpperBound() == null) break block6;
            if (BigInteger.ZERO.equals(this.getLowerBound())) {
                int i = 0;
                int j = 1;
                while (i < OERDefinition.uIntMax.length) {
                    if (this.getUpperBound().compareTo(OERDefinition.uIntMax[i]) < 0) {
                        return j;
                    }
                    ++i;
                    j *= 2;
                }
            } else {
                int i = 0;
                int j = 1;
                while (i < OERDefinition.sIntRange.length) {
                    if (this.getLowerBound().compareTo(OERDefinition.sIntRange[i][0]) >= 0 && this.getUpperBound().compareTo(OERDefinition.sIntRange[i][1]) < 0) {
                        return -j;
                    }
                    ++i;
                    j *= 2;
                }
            }
        }
        return 0;
    }

    public boolean hasPopulatedExtension() {
        return this.extensionsInDefinition;
    }

    public boolean hasDefaultChildren() {
        return this.defaultValuesInChildren;
    }

    public ASN1Encodable getDefaultValue() {
        return this.defaultValue;
    }

    public Element getFirstChid() {
        return this.getChildren().get(0);
    }

    public boolean isFixedLength() {
        return this.getLowerBound() != null && this.getLowerBound().equals(this.getUpperBound());
    }

    public String toString() {
        return "[" + this.typeName + " " + this.baseType.name() + " '" + this.getLabel() + "']";
    }

    public OERDefinition.BaseType getBaseType() {
        return this.baseType;
    }

    public List<Element> getChildren() {
        return this.children;
    }

    public boolean isExplicit() {
        return this.explicit;
    }

    public String getLabel() {
        return this.label;
    }

    public BigInteger getLowerBound() {
        return this.lowerBound;
    }

    public BigInteger getUpperBound() {
        return this.upperBound;
    }

    public boolean isExtensionsInDefinition() {
        return this.extensionsInDefinition;
    }

    public BigInteger getEnumValue() {
        return this.enumValue;
    }

    public Switch getaSwitch() {
        return this.aSwitch;
    }

    public List<Element> getOptionalChildrenInOrder() {
        return this.optionalChildrenInOrder;
    }

    public List<ASN1Encodable> getValidSwitchValues() {
        return this.validSwitchValues;
    }

    public ElementSupplier getElementSupplier() {
        return this.elementSupplier;
    }

    public boolean isMayRecurse() {
        return this.mayRecurse;
    }

    public String getTypeName() {
        return this.typeName;
    }

    public int getOptionals() {
        return this.optionals;
    }

    public int getBlock() {
        return this.block;
    }

    public String getDerivedTypeName() {
        if (this.typeName != null) {
            return this.typeName;
        }
        return this.baseType.name();
    }

    public ElementSupplier resolveSupplier() {
        if (this.supplierMap.containsKey(this.label)) {
            return this.supplierMap.get(this.label);
        }
        if (this.parent != null) {
            return this.parent.resolveSupplier(this.label);
        }
        throw new IllegalStateException("unable to resolve: " + this.label);
    }

    protected ElementSupplier resolveSupplier(String name) {
        name = this.label + "." + name;
        if (this.supplierMap.containsKey(name)) {
            return this.supplierMap.get(name);
        }
        if (this.parent != null) {
            return this.parent.resolveSupplier(name);
        }
        throw new IllegalStateException("unable to resolve: " + name);
    }

    public Element getParent() {
        return this.parent;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Element element = (Element)o;
        if (this.explicit != element.explicit) {
            return false;
        }
        if (this.extensionsInDefinition != element.extensionsInDefinition) {
            return false;
        }
        if (this.defaultValuesInChildren != element.defaultValuesInChildren) {
            return false;
        }
        if (this.mayRecurse != element.mayRecurse) {
            return false;
        }
        if (this.optionals != element.optionals) {
            return false;
        }
        if (this.block != element.block) {
            return false;
        }
        if (this.baseType != element.baseType) {
            return false;
        }
        if (this.children != null ? !this.children.equals(element.children) : element.children != null) {
            return false;
        }
        if (this.label != null ? !this.label.equals(element.label) : element.label != null) {
            return false;
        }
        if (this.lowerBound != null ? !this.lowerBound.equals(element.lowerBound) : element.lowerBound != null) {
            return false;
        }
        if (this.upperBound != null ? !this.upperBound.equals(element.upperBound) : element.upperBound != null) {
            return false;
        }
        if (this.enumValue != null ? !this.enumValue.equals(element.enumValue) : element.enumValue != null) {
            return false;
        }
        if (this.defaultValue != null ? !this.defaultValue.equals(element.defaultValue) : element.defaultValue != null) {
            return false;
        }
        if (this.aSwitch != null ? !this.aSwitch.equals(element.aSwitch) : element.aSwitch != null) {
            return false;
        }
        if (this.optionalChildrenInOrder != null ? !this.optionalChildrenInOrder.equals(element.optionalChildrenInOrder) : element.optionalChildrenInOrder != null) {
            return false;
        }
        if (this.validSwitchValues != null ? !this.validSwitchValues.equals(element.validSwitchValues) : element.validSwitchValues != null) {
            return false;
        }
        if (this.elementSupplier != null ? !this.elementSupplier.equals(element.elementSupplier) : element.elementSupplier != null) {
            return false;
        }
        if (this.typeName != null ? !this.typeName.equals(element.typeName) : element.typeName != null) {
            return false;
        }
        return this.supplierMap != null ? !this.supplierMap.equals(element.supplierMap) : element.supplierMap != null;
    }

    public int hashCode() {
        int result = this.baseType != null ? this.baseType.hashCode() : 0;
        result = 31 * result + (this.children != null ? this.children.hashCode() : 0);
        result = 31 * result + (this.explicit ? 1 : 0);
        result = 31 * result + (this.label != null ? this.label.hashCode() : 0);
        result = 31 * result + (this.lowerBound != null ? this.lowerBound.hashCode() : 0);
        result = 31 * result + (this.upperBound != null ? this.upperBound.hashCode() : 0);
        result = 31 * result + (this.extensionsInDefinition ? 1 : 0);
        result = 31 * result + (this.enumValue != null ? this.enumValue.hashCode() : 0);
        result = 31 * result + (this.defaultValue != null ? this.defaultValue.hashCode() : 0);
        result = 31 * result + (this.aSwitch != null ? this.aSwitch.hashCode() : 0);
        result = 31 * result + (this.defaultValuesInChildren ? 1 : 0);
        result = 31 * result + (this.optionalChildrenInOrder != null ? this.optionalChildrenInOrder.hashCode() : 0);
        result = 31 * result + (this.validSwitchValues != null ? this.validSwitchValues.hashCode() : 0);
        result = 31 * result + (this.elementSupplier != null ? this.elementSupplier.hashCode() : 0);
        result = 31 * result + (this.mayRecurse ? 1 : 0);
        result = 31 * result + (this.typeName != null ? this.typeName.hashCode() : 0);
        result = 31 * result + (this.supplierMap != null ? this.supplierMap.hashCode() : 0);
        result = 31 * result + this.optionals;
        result = 31 * result + this.block;
        return result;
    }
}

