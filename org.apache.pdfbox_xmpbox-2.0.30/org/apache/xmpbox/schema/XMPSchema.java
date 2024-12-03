/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.schema;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractComplexProperty;
import org.apache.xmpbox.type.AbstractField;
import org.apache.xmpbox.type.AbstractSimpleProperty;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.Attribute;
import org.apache.xmpbox.type.BadFieldValueException;
import org.apache.xmpbox.type.BooleanType;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.ComplexPropertyContainer;
import org.apache.xmpbox.type.DateType;
import org.apache.xmpbox.type.IntegerType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.type.TypeMapping;
import org.apache.xmpbox.type.Types;

public class XMPSchema
extends AbstractStructuredType {
    public XMPSchema(XMPMetadata metadata, String namespaceURI, String prefix, String name) {
        super(metadata, namespaceURI, prefix, name);
        this.addNamespace(this.getNamespace(), this.getPrefix());
    }

    public XMPSchema(XMPMetadata metadata) {
        this(metadata, null, null, null);
    }

    public XMPSchema(XMPMetadata metadata, String prefix) {
        this(metadata, null, prefix, null);
    }

    public XMPSchema(XMPMetadata metadata, String namespaceURI, String prefix) {
        this(metadata, namespaceURI, prefix, null);
    }

    public AbstractField getAbstractProperty(String qualifiedName) {
        for (AbstractField child : this.getContainer().getAllProperties()) {
            if (!child.getPropertyName().equals(qualifiedName)) continue;
            return child;
        }
        return null;
    }

    public Attribute getAboutAttribute() {
        return this.getAttribute("about");
    }

    public String getAboutValue() {
        Attribute prop = this.getAttribute("about");
        if (prop != null) {
            return prop.getValue();
        }
        return "";
    }

    public void setAbout(Attribute about) throws BadFieldValueException {
        if ("http://www.w3.org/1999/02/22-rdf-syntax-ns#".equals(about.getNamespace()) && "about".equals(about.getName())) {
            this.setAttribute(about);
            return;
        }
        throw new BadFieldValueException("Attribute 'about' must be named 'rdf:about' or 'about'");
    }

    public void setAboutAsSimple(String about) {
        if (about == null) {
            this.removeAttribute("about");
        } else {
            this.setAttribute(new Attribute("http://www.w3.org/1999/02/22-rdf-syntax-ns#", "about", about));
        }
    }

    private void setSpecifiedSimpleTypeProperty(Types type, String qualifiedName, Object propertyValue) {
        if (propertyValue == null) {
            for (AbstractField child : this.getContainer().getAllProperties()) {
                if (!child.getPropertyName().equals(qualifiedName)) continue;
                this.getContainer().removeProperty(child);
                return;
            }
        } else {
            AbstractSimpleProperty specifiedTypeProperty;
            try {
                TypeMapping tm = this.getMetadata().getTypeMapping();
                specifiedTypeProperty = tm.instanciateSimpleProperty(null, this.getPrefix(), qualifiedName, propertyValue, type);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Failed to create property with the specified type given in parameters", e);
            }
            for (AbstractField child : this.getAllProperties()) {
                if (!child.getPropertyName().equals(qualifiedName)) continue;
                this.removeProperty(child);
                this.addProperty(specifiedTypeProperty);
                return;
            }
            this.addProperty(specifiedTypeProperty);
        }
    }

    private void setSpecifiedSimpleTypeProperty(AbstractSimpleProperty prop) {
        for (AbstractField child : this.getAllProperties()) {
            if (!child.getPropertyName().equals(prop.getPropertyName())) continue;
            this.removeProperty(child);
            this.addProperty(prop);
            return;
        }
        this.addProperty(prop);
    }

    public void setTextProperty(TextType prop) {
        this.setSpecifiedSimpleTypeProperty(prop);
    }

    public void setTextPropertyValue(String qualifiedName, String propertyValue) {
        this.setSpecifiedSimpleTypeProperty(Types.Text, qualifiedName, propertyValue);
    }

    public void setTextPropertyValueAsSimple(String simpleName, String propertyValue) {
        this.setTextPropertyValue(simpleName, propertyValue);
    }

    public TextType getUnqualifiedTextProperty(String name) {
        AbstractField prop = this.getAbstractProperty(name);
        if (prop != null) {
            if (prop instanceof TextType) {
                return (TextType)prop;
            }
            throw new IllegalArgumentException("Property asked is not a Text Property");
        }
        return null;
    }

    public String getUnqualifiedTextPropertyValue(String name) {
        TextType tt = this.getUnqualifiedTextProperty(name);
        return tt == null ? null : tt.getStringValue();
    }

    public DateType getDateProperty(String qualifiedName) {
        AbstractField prop = this.getAbstractProperty(qualifiedName);
        if (prop != null) {
            if (prop instanceof DateType) {
                return (DateType)prop;
            }
            throw new IllegalArgumentException("Property asked is not a Date Property");
        }
        return null;
    }

    public Calendar getDatePropertyValueAsSimple(String simpleName) {
        return this.getDatePropertyValue(simpleName);
    }

    public Calendar getDatePropertyValue(String qualifiedName) {
        AbstractField prop = this.getAbstractProperty(qualifiedName);
        if (prop != null) {
            if (prop instanceof DateType) {
                return ((DateType)prop).getValue();
            }
            throw new IllegalArgumentException("Property asked is not a Date Property");
        }
        return null;
    }

    public void setDateProperty(DateType date) {
        this.setSpecifiedSimpleTypeProperty(date);
    }

    public void setDatePropertyValueAsSimple(String simpleName, Calendar date) {
        this.setDatePropertyValue(simpleName, date);
    }

    public void setDatePropertyValue(String qualifiedName, Calendar date) {
        this.setSpecifiedSimpleTypeProperty(Types.Date, qualifiedName, date);
    }

    public BooleanType getBooleanProperty(String qualifiedName) {
        AbstractField prop = this.getAbstractProperty(qualifiedName);
        if (prop != null) {
            if (prop instanceof BooleanType) {
                return (BooleanType)prop;
            }
            throw new IllegalArgumentException("Property asked is not a Boolean Property");
        }
        return null;
    }

    public Boolean getBooleanPropertyValueAsSimple(String simpleName) {
        return this.getBooleanPropertyValue(simpleName);
    }

    public Boolean getBooleanPropertyValue(String qualifiedName) {
        AbstractField prop = this.getAbstractProperty(qualifiedName);
        if (prop != null) {
            if (prop instanceof BooleanType) {
                return ((BooleanType)prop).getValue();
            }
            throw new IllegalArgumentException("Property asked is not a Boolean Property");
        }
        return null;
    }

    public void setBooleanProperty(BooleanType bool) {
        this.setSpecifiedSimpleTypeProperty(bool);
    }

    public void setBooleanPropertyValueAsSimple(String simpleName, Boolean bool) {
        this.setBooleanPropertyValue(simpleName, bool);
    }

    public void setBooleanPropertyValue(String qualifiedName, Boolean bool) {
        this.setSpecifiedSimpleTypeProperty(Types.Boolean, qualifiedName, bool);
    }

    public IntegerType getIntegerProperty(String qualifiedName) {
        AbstractField prop = this.getAbstractProperty(qualifiedName);
        if (prop != null) {
            if (prop instanceof IntegerType) {
                return (IntegerType)prop;
            }
            throw new IllegalArgumentException("Property asked is not an Integer Property");
        }
        return null;
    }

    public Integer getIntegerPropertyValueAsSimple(String simpleName) {
        return this.getIntegerPropertyValue(simpleName);
    }

    public Integer getIntegerPropertyValue(String qualifiedName) {
        AbstractField prop = this.getAbstractProperty(qualifiedName);
        if (prop != null) {
            if (prop instanceof IntegerType) {
                return ((IntegerType)prop).getValue();
            }
            throw new IllegalArgumentException("Property asked is not an Integer Property");
        }
        return null;
    }

    public void setIntegerProperty(IntegerType prop) {
        this.setSpecifiedSimpleTypeProperty(prop);
    }

    public void setIntegerPropertyValueAsSimple(String simpleName, Integer intValue) {
        this.setIntegerPropertyValue(simpleName, intValue);
    }

    public void setIntegerPropertyValue(String qualifiedName, Integer intValue) {
        this.setSpecifiedSimpleTypeProperty(Types.Integer, qualifiedName, intValue);
    }

    private void removeUnqualifiedArrayValue(String arrayName, String fieldValue) {
        ArrayProperty array = (ArrayProperty)this.getAbstractProperty(arrayName);
        if (array != null) {
            ArrayList<AbstractSimpleProperty> toDelete = new ArrayList<AbstractSimpleProperty>();
            for (AbstractField abstractField : array.getContainer().getAllProperties()) {
                AbstractSimpleProperty tmp = (AbstractSimpleProperty)abstractField;
                if (!tmp.getStringValue().equals(fieldValue)) continue;
                toDelete.add(tmp);
            }
            for (AbstractField abstractField : toDelete) {
                array.getContainer().removeProperty(abstractField);
            }
        }
    }

    public void removeUnqualifiedBagValue(String bagName, String bagValue) {
        this.removeUnqualifiedArrayValue(bagName, bagValue);
    }

    public void addBagValueAsSimple(String simpleName, String bagValue) {
        this.internalAddBagValue(simpleName, bagValue);
    }

    private void internalAddBagValue(String qualifiedBagName, String bagValue) {
        ArrayProperty bag = (ArrayProperty)this.getAbstractProperty(qualifiedBagName);
        TextType li = this.createTextType("li", bagValue);
        if (bag != null) {
            bag.getContainer().addProperty(li);
        } else {
            ArrayProperty newBag = this.createArrayProperty(qualifiedBagName, Cardinality.Bag);
            newBag.getContainer().addProperty(li);
            this.addProperty(newBag);
        }
    }

    public void addQualifiedBagValue(String simpleName, String bagValue) {
        this.internalAddBagValue(simpleName, bagValue);
    }

    public List<String> getUnqualifiedBagValueList(String bagName) {
        ArrayProperty array = (ArrayProperty)this.getAbstractProperty(bagName);
        if (array != null) {
            return array.getElementsAsString();
        }
        return null;
    }

    public void removeUnqualifiedSequenceValue(String qualifiedSeqName, String seqValue) {
        this.removeUnqualifiedArrayValue(qualifiedSeqName, seqValue);
    }

    public void removeUnqualifiedArrayValue(String arrayName, AbstractField fieldValue) {
        ArrayProperty array = (ArrayProperty)this.getAbstractProperty(arrayName);
        if (array != null) {
            ArrayList<AbstractSimpleProperty> toDelete = new ArrayList<AbstractSimpleProperty>();
            for (AbstractField abstractField : array.getContainer().getAllProperties()) {
                AbstractSimpleProperty tmp = (AbstractSimpleProperty)abstractField;
                if (!tmp.equals(fieldValue)) continue;
                toDelete.add(tmp);
            }
            for (AbstractField abstractField : toDelete) {
                array.getContainer().removeProperty(abstractField);
            }
        }
    }

    public void removeUnqualifiedSequenceValue(String qualifiedSeqName, AbstractField seqValue) {
        this.removeUnqualifiedArrayValue(qualifiedSeqName, seqValue);
    }

    public void addUnqualifiedSequenceValue(String simpleSeqName, String seqValue) {
        ArrayProperty seq = (ArrayProperty)this.getAbstractProperty(simpleSeqName);
        TextType li = this.createTextType("li", seqValue);
        if (seq != null) {
            seq.getContainer().addProperty(li);
        } else {
            ArrayProperty newSeq = this.createArrayProperty(simpleSeqName, Cardinality.Seq);
            newSeq.getContainer().addProperty(li);
            this.addProperty(newSeq);
        }
    }

    public void addBagValue(String qualifiedSeqName, AbstractField seqValue) {
        ArrayProperty bag = (ArrayProperty)this.getAbstractProperty(qualifiedSeqName);
        if (bag != null) {
            bag.getContainer().addProperty(seqValue);
        } else {
            ArrayProperty newBag = this.createArrayProperty(qualifiedSeqName, Cardinality.Bag);
            newBag.getContainer().addProperty(seqValue);
            this.addProperty(newBag);
        }
    }

    public void addUnqualifiedSequenceValue(String seqName, AbstractField seqValue) {
        ArrayProperty seq = (ArrayProperty)this.getAbstractProperty(seqName);
        if (seq != null) {
            seq.getContainer().addProperty(seqValue);
        } else {
            ArrayProperty newSeq = this.createArrayProperty(seqName, Cardinality.Seq);
            newSeq.getContainer().addProperty(seqValue);
            this.addProperty(newSeq);
        }
    }

    public List<String> getUnqualifiedSequenceValueList(String seqName) {
        ArrayProperty array = (ArrayProperty)this.getAbstractProperty(seqName);
        if (array != null) {
            return array.getElementsAsString();
        }
        return null;
    }

    public void removeUnqualifiedSequenceDateValue(String seqName, Calendar date) {
        ArrayProperty seq = (ArrayProperty)this.getAbstractProperty(seqName);
        if (seq != null) {
            ArrayList<AbstractField> toDelete = new ArrayList<AbstractField>();
            for (AbstractField tmp : seq.getContainer().getAllProperties()) {
                if (!(tmp instanceof DateType) || !((DateType)tmp).getValue().equals(date)) continue;
                toDelete.add(tmp);
            }
            for (AbstractField aToDelete : toDelete) {
                seq.getContainer().removeProperty(aToDelete);
            }
        }
    }

    public void addSequenceDateValueAsSimple(String simpleName, Calendar date) {
        this.addUnqualifiedSequenceDateValue(simpleName, date);
    }

    public void addUnqualifiedSequenceDateValue(String seqName, Calendar date) {
        this.addUnqualifiedSequenceValue(seqName, this.getMetadata().getTypeMapping().createDate(null, "RDF", "li", date));
    }

    public List<Calendar> getUnqualifiedSequenceDateValueList(String seqName) {
        ArrayList<Calendar> retval = null;
        ArrayProperty seq = (ArrayProperty)this.getAbstractProperty(seqName);
        if (seq != null) {
            retval = new ArrayList<Calendar>();
            for (AbstractField child : seq.getContainer().getAllProperties()) {
                if (!(child instanceof DateType)) continue;
                retval.add(((DateType)child).getValue());
            }
        }
        return retval;
    }

    public void reorganizeAltOrder(ComplexPropertyContainer alt) {
        Iterator<AbstractField> it = alt.getAllProperties().iterator();
        AbstractField xdefault = null;
        boolean xdefaultFound = false;
        if (it.hasNext() && it.next().getAttribute("lang").getValue().equals("x-default")) {
            return;
        }
        while (it.hasNext() && !xdefaultFound) {
            xdefault = it.next();
            if (!xdefault.getAttribute("lang").getValue().equals("x-default")) continue;
            alt.removeProperty(xdefault);
            xdefaultFound = true;
        }
        if (xdefaultFound) {
            it = alt.getAllProperties().iterator();
            ArrayList<AbstractField> reordered = new ArrayList<AbstractField>();
            ArrayList<AbstractField> toDelete = new ArrayList<AbstractField>();
            reordered.add(xdefault);
            while (it.hasNext()) {
                AbstractField tmp = it.next();
                reordered.add(tmp);
                toDelete.add(tmp);
            }
            for (AbstractField aToDelete : toDelete) {
                alt.removeProperty(aToDelete);
            }
            it = reordered.iterator();
            while (it.hasNext()) {
                alt.addProperty(it.next());
            }
        }
    }

    public void setUnqualifiedLanguagePropertyValue(String name, String language, String value) {
        AbstractField property;
        if (language == null || language.isEmpty()) {
            language = "x-default";
        }
        if ((property = this.getAbstractProperty(name)) != null) {
            if (property instanceof ArrayProperty) {
                ArrayProperty arrayProp = (ArrayProperty)property;
                for (AbstractField child : arrayProp.getContainer().getAllProperties()) {
                    if (!child.getAttribute("lang").getValue().equals(language)) continue;
                    arrayProp.getContainer().removeProperty(child);
                    if (value != null) {
                        TextType langValue = this.createTextType("li", value);
                        langValue.setAttribute(new Attribute("http://www.w3.org/XML/1998/namespace", "lang", language));
                        arrayProp.getContainer().addProperty(langValue);
                    }
                    this.reorganizeAltOrder(arrayProp.getContainer());
                    return;
                }
                TextType langValue = this.createTextType("li", value);
                langValue.setAttribute(new Attribute("http://www.w3.org/XML/1998/namespace", "lang", language));
                arrayProp.getContainer().addProperty(langValue);
                this.reorganizeAltOrder(arrayProp.getContainer());
            }
        } else {
            ArrayProperty arrayProp = this.createArrayProperty(name, Cardinality.Alt);
            TextType langValue = this.createTextType("li", value);
            langValue.setAttribute(new Attribute("http://www.w3.org/XML/1998/namespace", "lang", language));
            arrayProp.getContainer().addProperty(langValue);
            this.addProperty(arrayProp);
        }
    }

    public String getUnqualifiedLanguagePropertyValue(String name, String expectedLanguage) {
        String language = expectedLanguage != null ? expectedLanguage : "x-default";
        AbstractField property = this.getAbstractProperty(name);
        if (property != null) {
            if (property instanceof ArrayProperty) {
                ArrayProperty arrayProp = (ArrayProperty)property;
                for (AbstractField child : arrayProp.getContainer().getAllProperties()) {
                    Attribute text = child.getAttribute("lang");
                    if (text == null || !text.getValue().equals(language)) continue;
                    return ((TextType)child).getStringValue();
                }
                return null;
            }
            throw new IllegalArgumentException("The property '" + name + "' is not of Lang Alt type");
        }
        return null;
    }

    public List<String> getUnqualifiedLanguagePropertyLanguagesValue(String name) {
        AbstractField property = this.getAbstractProperty(name);
        if (property != null) {
            if (property instanceof ArrayProperty) {
                ArrayProperty arrayProp = (ArrayProperty)property;
                List<AbstractField> allProperties = arrayProp.getContainer().getAllProperties();
                ArrayList<String> retval = new ArrayList<String>(allProperties.size());
                for (AbstractField child : allProperties) {
                    Attribute text = child.getAttribute("lang");
                    retval.add(text != null ? text.getValue() : "x-default");
                }
                return retval;
            }
            throw new IllegalArgumentException("The property '" + name + "' is not of Lang Alt type");
        }
        return null;
    }

    public void merge(XMPSchema xmpSchema) throws IOException {
        if (!xmpSchema.getClass().equals(this.getClass())) {
            throw new IOException("Can only merge schemas of the same type.");
        }
        for (Attribute att : xmpSchema.getAllAttributes()) {
            if (!att.getNamespace().equals(this.getNamespace())) continue;
            this.setAttribute(att);
        }
        for (AbstractField child : xmpSchema.getContainer().getAllProperties()) {
            if (!child.getPrefix().equals(this.getPrefix())) continue;
            if (child instanceof ArrayProperty) {
                String analyzedPropQualifiedName = child.getPropertyName();
                for (AbstractField tmpEmbeddedProperty : this.getAllProperties()) {
                    Iterator<AbstractField> itNewValues;
                    if (!(tmpEmbeddedProperty instanceof ArrayProperty) || !tmpEmbeddedProperty.getPropertyName().equals(analyzedPropQualifiedName) || !this.mergeComplexProperty(itNewValues = ((ArrayProperty)child).getContainer().getAllProperties().iterator(), (ArrayProperty)tmpEmbeddedProperty)) continue;
                    return;
                }
                continue;
            }
            this.addProperty(child);
        }
    }

    private boolean mergeComplexProperty(Iterator<AbstractField> itNewValues, ArrayProperty arrayProperty) {
        while (itNewValues.hasNext()) {
            TextType tmpNewValue = (TextType)itNewValues.next();
            for (AbstractField abstractField : arrayProperty.getContainer().getAllProperties()) {
                TextType tmpOldValue = (TextType)abstractField;
                if (!tmpOldValue.getStringValue().equals(tmpNewValue.getStringValue())) continue;
                return true;
            }
            arrayProperty.getContainer().addProperty(tmpNewValue);
        }
        return false;
    }

    public List<AbstractField> getUnqualifiedArrayList(String name) throws BadFieldValueException {
        AbstractComplexProperty array = null;
        for (AbstractField child : this.getAllProperties()) {
            if (!child.getPropertyName().equals(name)) continue;
            if (child instanceof ArrayProperty) {
                array = (ArrayProperty)child;
                break;
            }
            throw new BadFieldValueException("Property asked is not an array");
        }
        if (array != null) {
            return new ArrayList<AbstractField>(array.getContainer().getAllProperties());
        }
        return null;
    }

    protected AbstractSimpleProperty instanciateSimple(String propertyName, Object value) {
        TypeMapping tm = this.getMetadata().getTypeMapping();
        return tm.instanciateSimpleField(this.getClass(), null, this.getPrefix(), propertyName, value);
    }
}

