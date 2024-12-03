/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.parser;

import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.Counter;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.Rect;
import org.xhtmlrenderer.css.constants.IdentValue;
import org.xhtmlrenderer.css.parser.FSColor;
import org.xhtmlrenderer.css.parser.FSFunction;
import org.xhtmlrenderer.css.parser.Token;
import org.xhtmlrenderer.util.ArrayUtil;

public class PropertyValue
implements CSSPrimitiveValue {
    public static final short VALUE_TYPE_NUMBER = 1;
    public static final short VALUE_TYPE_LENGTH = 2;
    public static final short VALUE_TYPE_COLOR = 3;
    public static final short VALUE_TYPE_IDENT = 4;
    public static final short VALUE_TYPE_STRING = 5;
    public static final short VALUE_TYPE_LIST = 6;
    public static final short VALUE_TYPE_FUNCTION = 7;
    private short _type;
    private short _cssValueType;
    private String _stringValue;
    private float _floatValue;
    private String[] _stringArrayValue;
    private String _cssText;
    private FSColor _FSColor;
    private IdentValue _identValue;
    private short _propertyValueType;
    private Token _operator;
    private List _values;
    private FSFunction _function;

    public PropertyValue(short type, float floatValue, String cssText) {
        this._type = type;
        this._floatValue = floatValue;
        this._cssValueType = 1;
        this._cssText = cssText;
        this._propertyValueType = type == 1 && floatValue != 0.0f ? (short)1 : (short)2;
    }

    public PropertyValue(FSColor color) {
        this._type = (short)25;
        this._cssValueType = 1;
        this._cssText = color.toString();
        this._FSColor = color;
        this._propertyValueType = (short)3;
    }

    public PropertyValue(short type, String stringValue, String cssText) {
        this._type = type;
        this._stringValue = stringValue;
        this._cssValueType = this._stringValue.equalsIgnoreCase("inherit") ? (short)0 : 1;
        this._cssText = cssText;
        this._propertyValueType = type == 21 ? (short)4 : (short)5;
    }

    public PropertyValue(IdentValue ident) {
        this._type = (short)21;
        this._stringValue = ident.toString();
        this._cssValueType = this._stringValue.equals("inherit") ? (short)0 : 1;
        this._cssText = ident.toString();
        this._propertyValueType = (short)4;
        this._identValue = ident;
    }

    public PropertyValue(List values) {
        this._type = 0;
        this._cssValueType = (short)3;
        this._cssText = values.toString();
        this._values = values;
        this._propertyValueType = (short)6;
    }

    public PropertyValue(FSFunction function) {
        this._type = 0;
        this._cssValueType = (short)3;
        this._cssText = function.toString();
        this._function = function;
        this._propertyValueType = (short)7;
    }

    @Override
    public Counter getCounterValue() throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getFloatValue(short unitType) throws DOMException {
        return this._floatValue;
    }

    public float getFloatValue() {
        return this._floatValue;
    }

    @Override
    public short getPrimitiveType() {
        return this._type;
    }

    @Override
    public RGBColor getRGBColorValue() throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Rect getRectValue() throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getStringValue() throws DOMException {
        return this._stringValue;
    }

    @Override
    public void setFloatValue(short unitType, float floatValue) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStringValue(short stringType, String stringValue) throws DOMException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getCssText() {
        return this._cssText;
    }

    @Override
    public short getCssValueType() {
        return this._cssValueType;
    }

    @Override
    public void setCssText(String cssText) throws DOMException {
        throw new UnsupportedOperationException();
    }

    public FSColor getFSColor() {
        return this._FSColor;
    }

    public IdentValue getIdentValue() {
        return this._identValue;
    }

    public void setIdentValue(IdentValue identValue) {
        this._identValue = identValue;
    }

    public short getPropertyValueType() {
        return this._propertyValueType;
    }

    public Token getOperator() {
        return this._operator;
    }

    public void setOperator(Token operator) {
        this._operator = operator;
    }

    public String[] getStringArrayValue() {
        return ArrayUtil.cloneOrEmpty(this._stringArrayValue);
    }

    public void setStringArrayValue(String[] stringArrayValue) {
        this._stringArrayValue = ArrayUtil.cloneOrEmpty(stringArrayValue);
    }

    public String toString() {
        return this._cssText;
    }

    public List getValues() {
        return new ArrayList(this._values);
    }

    public FSFunction getFunction() {
        return this._function;
    }

    public String getFingerprint() {
        if (this.getPropertyValueType() == 4) {
            if (this._identValue == null) {
                this._identValue = IdentValue.getByIdentString(this.getStringValue());
            }
            return "I" + this._identValue.FS_ID;
        }
        return this.getCssText();
    }
}

