/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.dom;

import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.ListValue;
import org.apache.batik.css.engine.value.StringValue;
import org.apache.batik.css.engine.value.Value;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.CSSValueList;
import org.w3c.dom.css.Counter;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.Rect;

public class CSSOMValue
implements CSSPrimitiveValue,
CSSValueList,
Counter,
Rect,
RGBColor {
    protected ValueProvider valueProvider;
    protected ModificationHandler handler;
    protected LeftComponent leftComponent;
    protected RightComponent rightComponent;
    protected BottomComponent bottomComponent;
    protected TopComponent topComponent;
    protected RedComponent redComponent;
    protected GreenComponent greenComponent;
    protected BlueComponent blueComponent;
    protected CSSValue[] items;

    public CSSOMValue(ValueProvider vp) {
        this.valueProvider = vp;
    }

    public void setModificationHandler(ModificationHandler h) {
        this.handler = h;
    }

    @Override
    public String getCssText() {
        return this.valueProvider.getValue().getCssText();
    }

    @Override
    public void setCssText(String cssText) throws DOMException {
        if (this.handler == null) {
            throw new DOMException(7, "");
        }
        this.handler.textChanged(cssText);
    }

    @Override
    public short getCssValueType() {
        return this.valueProvider.getValue().getCssValueType();
    }

    @Override
    public short getPrimitiveType() {
        return this.valueProvider.getValue().getPrimitiveType();
    }

    @Override
    public void setFloatValue(short unitType, float floatValue) throws DOMException {
        if (this.handler == null) {
            throw new DOMException(7, "");
        }
        this.handler.floatValueChanged(unitType, floatValue);
    }

    @Override
    public float getFloatValue(short unitType) throws DOMException {
        return CSSOMValue.convertFloatValue(unitType, this.valueProvider.getValue());
    }

    public static float convertFloatValue(short unitType, Value value) {
        switch (unitType) {
            case 1: 
            case 2: 
            case 3: 
            case 4: 
            case 5: 
            case 18: {
                if (value.getPrimitiveType() != unitType) break;
                return value.getFloatValue();
            }
            case 6: {
                return CSSOMValue.toCentimeters(value);
            }
            case 7: {
                return CSSOMValue.toMillimeters(value);
            }
            case 8: {
                return CSSOMValue.toInches(value);
            }
            case 9: {
                return CSSOMValue.toPoints(value);
            }
            case 10: {
                return CSSOMValue.toPicas(value);
            }
            case 11: {
                return CSSOMValue.toDegrees(value);
            }
            case 12: {
                return CSSOMValue.toRadians(value);
            }
            case 13: {
                return CSSOMValue.toGradians(value);
            }
            case 14: {
                return CSSOMValue.toMilliseconds(value);
            }
            case 15: {
                return CSSOMValue.toSeconds(value);
            }
            case 16: {
                return CSSOMValue.toHertz(value);
            }
            case 17: {
                return CSSOMValue.tokHertz(value);
            }
        }
        throw new DOMException(15, "");
    }

    protected static float toCentimeters(Value value) {
        switch (value.getPrimitiveType()) {
            case 6: {
                return value.getFloatValue();
            }
            case 7: {
                return value.getFloatValue() / 10.0f;
            }
            case 8: {
                return value.getFloatValue() * 2.54f;
            }
            case 9: {
                return value.getFloatValue() * 2.54f / 72.0f;
            }
            case 10: {
                return value.getFloatValue() * 2.54f / 6.0f;
            }
        }
        throw new DOMException(15, "");
    }

    protected static float toInches(Value value) {
        switch (value.getPrimitiveType()) {
            case 6: {
                return value.getFloatValue() / 2.54f;
            }
            case 7: {
                return value.getFloatValue() / 25.4f;
            }
            case 8: {
                return value.getFloatValue();
            }
            case 9: {
                return value.getFloatValue() / 72.0f;
            }
            case 10: {
                return value.getFloatValue() / 6.0f;
            }
        }
        throw new DOMException(15, "");
    }

    protected static float toMillimeters(Value value) {
        switch (value.getPrimitiveType()) {
            case 6: {
                return value.getFloatValue() * 10.0f;
            }
            case 7: {
                return value.getFloatValue();
            }
            case 8: {
                return value.getFloatValue() * 25.4f;
            }
            case 9: {
                return value.getFloatValue() * 25.4f / 72.0f;
            }
            case 10: {
                return value.getFloatValue() * 25.4f / 6.0f;
            }
        }
        throw new DOMException(15, "");
    }

    protected static float toPoints(Value value) {
        switch (value.getPrimitiveType()) {
            case 6: {
                return value.getFloatValue() * 72.0f / 2.54f;
            }
            case 7: {
                return value.getFloatValue() * 72.0f / 25.4f;
            }
            case 8: {
                return value.getFloatValue() * 72.0f;
            }
            case 9: {
                return value.getFloatValue();
            }
            case 10: {
                return value.getFloatValue() * 12.0f;
            }
        }
        throw new DOMException(15, "");
    }

    protected static float toPicas(Value value) {
        switch (value.getPrimitiveType()) {
            case 6: {
                return value.getFloatValue() * 6.0f / 2.54f;
            }
            case 7: {
                return value.getFloatValue() * 6.0f / 25.4f;
            }
            case 8: {
                return value.getFloatValue() * 6.0f;
            }
            case 9: {
                return value.getFloatValue() / 12.0f;
            }
            case 10: {
                return value.getFloatValue();
            }
        }
        throw new DOMException(15, "");
    }

    protected static float toDegrees(Value value) {
        switch (value.getPrimitiveType()) {
            case 11: {
                return value.getFloatValue();
            }
            case 12: {
                return (float)Math.toDegrees(value.getFloatValue());
            }
            case 13: {
                return value.getFloatValue() * 9.0f / 5.0f;
            }
        }
        throw new DOMException(15, "");
    }

    protected static float toRadians(Value value) {
        switch (value.getPrimitiveType()) {
            case 11: {
                return value.getFloatValue() * 5.0f / 9.0f;
            }
            case 12: {
                return value.getFloatValue();
            }
            case 13: {
                return (float)((double)(value.getFloatValue() * 100.0f) / Math.PI);
            }
        }
        throw new DOMException(15, "");
    }

    protected static float toGradians(Value value) {
        switch (value.getPrimitiveType()) {
            case 11: {
                return (float)((double)value.getFloatValue() * Math.PI / 180.0);
            }
            case 12: {
                return (float)((double)value.getFloatValue() * Math.PI / 100.0);
            }
            case 13: {
                return value.getFloatValue();
            }
        }
        throw new DOMException(15, "");
    }

    protected static float toMilliseconds(Value value) {
        switch (value.getPrimitiveType()) {
            case 14: {
                return value.getFloatValue();
            }
            case 15: {
                return value.getFloatValue() * 1000.0f;
            }
        }
        throw new DOMException(15, "");
    }

    protected static float toSeconds(Value value) {
        switch (value.getPrimitiveType()) {
            case 14: {
                return value.getFloatValue() / 1000.0f;
            }
            case 15: {
                return value.getFloatValue();
            }
        }
        throw new DOMException(15, "");
    }

    protected static float toHertz(Value value) {
        switch (value.getPrimitiveType()) {
            case 16: {
                return value.getFloatValue();
            }
            case 17: {
                return value.getFloatValue() / 1000.0f;
            }
        }
        throw new DOMException(15, "");
    }

    protected static float tokHertz(Value value) {
        switch (value.getPrimitiveType()) {
            case 16: {
                return value.getFloatValue() * 1000.0f;
            }
            case 17: {
                return value.getFloatValue();
            }
        }
        throw new DOMException(15, "");
    }

    @Override
    public void setStringValue(short stringType, String stringValue) throws DOMException {
        if (this.handler == null) {
            throw new DOMException(7, "");
        }
        this.handler.stringValueChanged(stringType, stringValue);
    }

    @Override
    public String getStringValue() throws DOMException {
        return this.valueProvider.getValue().getStringValue();
    }

    @Override
    public Counter getCounterValue() throws DOMException {
        return this;
    }

    @Override
    public Rect getRectValue() throws DOMException {
        return this;
    }

    @Override
    public RGBColor getRGBColorValue() throws DOMException {
        return this;
    }

    @Override
    public int getLength() {
        return this.valueProvider.getValue().getLength();
    }

    @Override
    public CSSValue item(int index) {
        int len = this.valueProvider.getValue().getLength();
        if (index < 0 || index >= len) {
            return null;
        }
        if (this.items == null) {
            this.items = new CSSValue[this.valueProvider.getValue().getLength()];
        } else if (this.items.length < len) {
            CSSValue[] nitems = new CSSValue[len];
            System.arraycopy(this.items, 0, nitems, 0, this.items.length);
            this.items = nitems;
        }
        CSSValue result = this.items[index];
        if (result == null) {
            this.items[index] = result = new ListComponent(index);
        }
        return result;
    }

    @Override
    public String getIdentifier() {
        return this.valueProvider.getValue().getIdentifier();
    }

    @Override
    public String getListStyle() {
        return this.valueProvider.getValue().getListStyle();
    }

    @Override
    public String getSeparator() {
        return this.valueProvider.getValue().getSeparator();
    }

    @Override
    public CSSPrimitiveValue getTop() {
        this.valueProvider.getValue().getTop();
        if (this.topComponent == null) {
            this.topComponent = new TopComponent();
        }
        return this.topComponent;
    }

    @Override
    public CSSPrimitiveValue getRight() {
        this.valueProvider.getValue().getRight();
        if (this.rightComponent == null) {
            this.rightComponent = new RightComponent();
        }
        return this.rightComponent;
    }

    @Override
    public CSSPrimitiveValue getBottom() {
        this.valueProvider.getValue().getBottom();
        if (this.bottomComponent == null) {
            this.bottomComponent = new BottomComponent();
        }
        return this.bottomComponent;
    }

    @Override
    public CSSPrimitiveValue getLeft() {
        this.valueProvider.getValue().getLeft();
        if (this.leftComponent == null) {
            this.leftComponent = new LeftComponent();
        }
        return this.leftComponent;
    }

    @Override
    public CSSPrimitiveValue getRed() {
        this.valueProvider.getValue().getRed();
        if (this.redComponent == null) {
            this.redComponent = new RedComponent();
        }
        return this.redComponent;
    }

    @Override
    public CSSPrimitiveValue getGreen() {
        this.valueProvider.getValue().getGreen();
        if (this.greenComponent == null) {
            this.greenComponent = new GreenComponent();
        }
        return this.greenComponent;
    }

    @Override
    public CSSPrimitiveValue getBlue() {
        this.valueProvider.getValue().getBlue();
        if (this.blueComponent == null) {
            this.blueComponent = new BlueComponent();
        }
        return this.blueComponent;
    }

    protected class ListComponent
    extends AbstractComponent {
        protected int index;

        public ListComponent(int idx) {
            this.index = idx;
        }

        @Override
        protected Value getValue() {
            if (this.index >= CSSOMValue.this.valueProvider.getValue().getLength()) {
                throw new DOMException(7, "");
            }
            return CSSOMValue.this.valueProvider.getValue().item(this.index);
        }

        @Override
        public void setCssText(String cssText) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.listTextChanged(this.index, cssText);
        }

        @Override
        public void setFloatValue(short unitType, float floatValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.listFloatValueChanged(this.index, unitType, floatValue);
        }

        @Override
        public void setStringValue(short stringType, String stringValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.listStringValueChanged(this.index, stringType, stringValue);
        }
    }

    protected class BlueComponent
    extends FloatComponent {
        protected BlueComponent() {
        }

        @Override
        protected Value getValue() {
            return CSSOMValue.this.valueProvider.getValue().getBlue();
        }

        @Override
        public void setCssText(String cssText) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.blueTextChanged(cssText);
        }

        @Override
        public void setFloatValue(short unitType, float floatValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.blueFloatValueChanged(unitType, floatValue);
        }
    }

    protected class GreenComponent
    extends FloatComponent {
        protected GreenComponent() {
        }

        @Override
        protected Value getValue() {
            return CSSOMValue.this.valueProvider.getValue().getGreen();
        }

        @Override
        public void setCssText(String cssText) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.greenTextChanged(cssText);
        }

        @Override
        public void setFloatValue(short unitType, float floatValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.greenFloatValueChanged(unitType, floatValue);
        }
    }

    protected class RedComponent
    extends FloatComponent {
        protected RedComponent() {
        }

        @Override
        protected Value getValue() {
            return CSSOMValue.this.valueProvider.getValue().getRed();
        }

        @Override
        public void setCssText(String cssText) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.redTextChanged(cssText);
        }

        @Override
        public void setFloatValue(short unitType, float floatValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.redFloatValueChanged(unitType, floatValue);
        }
    }

    protected class BottomComponent
    extends FloatComponent {
        protected BottomComponent() {
        }

        @Override
        protected Value getValue() {
            return CSSOMValue.this.valueProvider.getValue().getBottom();
        }

        @Override
        public void setCssText(String cssText) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.bottomTextChanged(cssText);
        }

        @Override
        public void setFloatValue(short unitType, float floatValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.bottomFloatValueChanged(unitType, floatValue);
        }
    }

    protected class RightComponent
    extends FloatComponent {
        protected RightComponent() {
        }

        @Override
        protected Value getValue() {
            return CSSOMValue.this.valueProvider.getValue().getRight();
        }

        @Override
        public void setCssText(String cssText) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.rightTextChanged(cssText);
        }

        @Override
        public void setFloatValue(short unitType, float floatValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.rightFloatValueChanged(unitType, floatValue);
        }
    }

    protected class TopComponent
    extends FloatComponent {
        protected TopComponent() {
        }

        @Override
        protected Value getValue() {
            return CSSOMValue.this.valueProvider.getValue().getTop();
        }

        @Override
        public void setCssText(String cssText) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.topTextChanged(cssText);
        }

        @Override
        public void setFloatValue(short unitType, float floatValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.topFloatValueChanged(unitType, floatValue);
        }
    }

    protected class LeftComponent
    extends FloatComponent {
        protected LeftComponent() {
        }

        @Override
        protected Value getValue() {
            return CSSOMValue.this.valueProvider.getValue().getLeft();
        }

        @Override
        public void setCssText(String cssText) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.leftTextChanged(cssText);
        }

        @Override
        public void setFloatValue(short unitType, float floatValue) throws DOMException {
            if (CSSOMValue.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMValue.this.handler.leftFloatValueChanged(unitType, floatValue);
        }
    }

    protected abstract class FloatComponent
    extends AbstractComponent {
        protected FloatComponent() {
        }

        @Override
        public void setStringValue(short stringType, String stringValue) throws DOMException {
            throw new DOMException(15, "");
        }
    }

    protected abstract class AbstractComponent
    implements CSSPrimitiveValue {
        protected AbstractComponent() {
        }

        protected abstract Value getValue();

        @Override
        public String getCssText() {
            return this.getValue().getCssText();
        }

        @Override
        public short getCssValueType() {
            return this.getValue().getCssValueType();
        }

        @Override
        public short getPrimitiveType() {
            return this.getValue().getPrimitiveType();
        }

        @Override
        public float getFloatValue(short unitType) throws DOMException {
            return CSSOMValue.convertFloatValue(unitType, this.getValue());
        }

        @Override
        public String getStringValue() throws DOMException {
            return CSSOMValue.this.valueProvider.getValue().getStringValue();
        }

        @Override
        public Counter getCounterValue() throws DOMException {
            throw new DOMException(15, "");
        }

        @Override
        public Rect getRectValue() throws DOMException {
            throw new DOMException(15, "");
        }

        @Override
        public RGBColor getRGBColorValue() throws DOMException {
            throw new DOMException(15, "");
        }

        public int getLength() {
            throw new DOMException(15, "");
        }

        public CSSValue item(int index) {
            throw new DOMException(15, "");
        }
    }

    public static abstract class AbstractModificationHandler
    implements ModificationHandler {
        protected abstract Value getValue();

        @Override
        public void floatValueChanged(short unit, float value) throws DOMException {
            this.textChanged(FloatValue.getCssText(unit, value));
        }

        @Override
        public void stringValueChanged(short type, String value) throws DOMException {
            this.textChanged(StringValue.getCssText(type, value));
        }

        @Override
        public void leftTextChanged(String text) throws DOMException {
            Value val = this.getValue();
            text = "rect(" + val.getTop().getCssText() + ", " + val.getRight().getCssText() + ", " + val.getBottom().getCssText() + ", " + text + ')';
            this.textChanged(text);
        }

        @Override
        public void leftFloatValueChanged(short unit, float value) throws DOMException {
            Value val = this.getValue();
            String text = "rect(" + val.getTop().getCssText() + ", " + val.getRight().getCssText() + ", " + val.getBottom().getCssText() + ", " + FloatValue.getCssText(unit, value) + ')';
            this.textChanged(text);
        }

        @Override
        public void topTextChanged(String text) throws DOMException {
            Value val = this.getValue();
            text = "rect(" + text + ", " + val.getRight().getCssText() + ", " + val.getBottom().getCssText() + ", " + val.getLeft().getCssText() + ')';
            this.textChanged(text);
        }

        @Override
        public void topFloatValueChanged(short unit, float value) throws DOMException {
            Value val = this.getValue();
            String text = "rect(" + FloatValue.getCssText(unit, value) + ", " + val.getRight().getCssText() + ", " + val.getBottom().getCssText() + ", " + val.getLeft().getCssText() + ')';
            this.textChanged(text);
        }

        @Override
        public void rightTextChanged(String text) throws DOMException {
            Value val = this.getValue();
            text = "rect(" + val.getTop().getCssText() + ", " + text + ", " + val.getBottom().getCssText() + ", " + val.getLeft().getCssText() + ')';
            this.textChanged(text);
        }

        @Override
        public void rightFloatValueChanged(short unit, float value) throws DOMException {
            Value val = this.getValue();
            String text = "rect(" + val.getTop().getCssText() + ", " + FloatValue.getCssText(unit, value) + ", " + val.getBottom().getCssText() + ", " + val.getLeft().getCssText() + ')';
            this.textChanged(text);
        }

        @Override
        public void bottomTextChanged(String text) throws DOMException {
            Value val = this.getValue();
            text = "rect(" + val.getTop().getCssText() + ", " + val.getRight().getCssText() + ", " + text + ", " + val.getLeft().getCssText() + ')';
            this.textChanged(text);
        }

        @Override
        public void bottomFloatValueChanged(short unit, float value) throws DOMException {
            Value val = this.getValue();
            String text = "rect(" + val.getTop().getCssText() + ", " + val.getRight().getCssText() + ", " + FloatValue.getCssText(unit, value) + ", " + val.getLeft().getCssText() + ')';
            this.textChanged(text);
        }

        @Override
        public void redTextChanged(String text) throws DOMException {
            Value val = this.getValue();
            text = "rgb(" + text + ", " + val.getGreen().getCssText() + ", " + val.getBlue().getCssText() + ')';
            this.textChanged(text);
        }

        @Override
        public void redFloatValueChanged(short unit, float value) throws DOMException {
            Value val = this.getValue();
            String text = "rgb(" + FloatValue.getCssText(unit, value) + ", " + val.getGreen().getCssText() + ", " + val.getBlue().getCssText() + ')';
            this.textChanged(text);
        }

        @Override
        public void greenTextChanged(String text) throws DOMException {
            Value val = this.getValue();
            text = "rgb(" + val.getRed().getCssText() + ", " + text + ", " + val.getBlue().getCssText() + ')';
            this.textChanged(text);
        }

        @Override
        public void greenFloatValueChanged(short unit, float value) throws DOMException {
            Value val = this.getValue();
            String text = "rgb(" + val.getRed().getCssText() + ", " + FloatValue.getCssText(unit, value) + ", " + val.getBlue().getCssText() + ')';
            this.textChanged(text);
        }

        @Override
        public void blueTextChanged(String text) throws DOMException {
            Value val = this.getValue();
            text = "rgb(" + val.getRed().getCssText() + ", " + val.getGreen().getCssText() + ", " + text + ')';
            this.textChanged(text);
        }

        @Override
        public void blueFloatValueChanged(short unit, float value) throws DOMException {
            Value val = this.getValue();
            String text = "rgb(" + val.getRed().getCssText() + ", " + val.getGreen().getCssText() + ", " + FloatValue.getCssText(unit, value) + ')';
            this.textChanged(text);
        }

        @Override
        public void listTextChanged(int idx, String text) throws DOMException {
            int i;
            ListValue lv = (ListValue)this.getValue();
            int len = lv.getLength();
            StringBuffer sb = new StringBuffer(len * 8);
            for (i = 0; i < idx; ++i) {
                sb.append(lv.item(i).getCssText());
                sb.append(lv.getSeparatorChar());
            }
            sb.append(text);
            for (i = idx + 1; i < len; ++i) {
                sb.append(lv.getSeparatorChar());
                sb.append(lv.item(i).getCssText());
            }
            text = sb.toString();
            this.textChanged(text);
        }

        @Override
        public void listFloatValueChanged(int idx, short unit, float value) throws DOMException {
            int i;
            ListValue lv = (ListValue)this.getValue();
            int len = lv.getLength();
            StringBuffer sb = new StringBuffer(len * 8);
            for (i = 0; i < idx; ++i) {
                sb.append(lv.item(i).getCssText());
                sb.append(lv.getSeparatorChar());
            }
            sb.append(FloatValue.getCssText(unit, value));
            for (i = idx + 1; i < len; ++i) {
                sb.append(lv.getSeparatorChar());
                sb.append(lv.item(i).getCssText());
            }
            this.textChanged(sb.toString());
        }

        @Override
        public void listStringValueChanged(int idx, short unit, String value) throws DOMException {
            int i;
            ListValue lv = (ListValue)this.getValue();
            int len = lv.getLength();
            StringBuffer sb = new StringBuffer(len * 8);
            for (i = 0; i < idx; ++i) {
                sb.append(lv.item(i).getCssText());
                sb.append(lv.getSeparatorChar());
            }
            sb.append(StringValue.getCssText(unit, value));
            for (i = idx + 1; i < len; ++i) {
                sb.append(lv.getSeparatorChar());
                sb.append(lv.item(i).getCssText());
            }
            this.textChanged(sb.toString());
        }
    }

    public static interface ModificationHandler {
        public void textChanged(String var1) throws DOMException;

        public void floatValueChanged(short var1, float var2) throws DOMException;

        public void stringValueChanged(short var1, String var2) throws DOMException;

        public void leftTextChanged(String var1) throws DOMException;

        public void leftFloatValueChanged(short var1, float var2) throws DOMException;

        public void topTextChanged(String var1) throws DOMException;

        public void topFloatValueChanged(short var1, float var2) throws DOMException;

        public void rightTextChanged(String var1) throws DOMException;

        public void rightFloatValueChanged(short var1, float var2) throws DOMException;

        public void bottomTextChanged(String var1) throws DOMException;

        public void bottomFloatValueChanged(short var1, float var2) throws DOMException;

        public void redTextChanged(String var1) throws DOMException;

        public void redFloatValueChanged(short var1, float var2) throws DOMException;

        public void greenTextChanged(String var1) throws DOMException;

        public void greenFloatValueChanged(short var1, float var2) throws DOMException;

        public void blueTextChanged(String var1) throws DOMException;

        public void blueFloatValueChanged(short var1, float var2) throws DOMException;

        public void listTextChanged(int var1, String var2) throws DOMException;

        public void listFloatValueChanged(int var1, short var2, float var3) throws DOMException;

        public void listStringValueChanged(int var1, short var2, String var3) throws DOMException;
    }

    public static interface ValueProvider {
        public Value getValue();
    }
}

