/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.dom;

import java.util.ArrayList;
import org.apache.batik.css.dom.CSSOMValue;
import org.apache.batik.css.engine.value.FloatValue;
import org.apache.batik.css.engine.value.Value;
import org.apache.batik.css.engine.value.svg.ICCColor;
import org.w3c.dom.DOMException;
import org.w3c.dom.css.CSSPrimitiveValue;
import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.Counter;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.css.Rect;
import org.w3c.dom.svg.SVGColor;
import org.w3c.dom.svg.SVGICCColor;
import org.w3c.dom.svg.SVGNumber;
import org.w3c.dom.svg.SVGNumberList;

public class CSSOMSVGColor
implements SVGColor,
RGBColor,
SVGICCColor,
SVGNumberList {
    protected ValueProvider valueProvider;
    protected ModificationHandler handler;
    protected RedComponent redComponent;
    protected GreenComponent greenComponent;
    protected BlueComponent blueComponent;
    protected ArrayList iccColors;

    public CSSOMSVGColor(ValueProvider vp) {
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
        this.iccColors = null;
        this.handler.textChanged(cssText);
    }

    @Override
    public short getCssValueType() {
        return 3;
    }

    @Override
    public short getColorType() {
        Value value = this.valueProvider.getValue();
        short cssValueType = value.getCssValueType();
        switch (cssValueType) {
            case 1: {
                short primitiveType = value.getPrimitiveType();
                switch (primitiveType) {
                    case 21: {
                        if (value.getStringValue().equalsIgnoreCase("currentcolor")) {
                            return 3;
                        }
                        return 1;
                    }
                    case 25: {
                        return 1;
                    }
                }
                throw new IllegalStateException("Found unexpected PrimitiveType:" + primitiveType);
            }
            case 2: {
                return 2;
            }
        }
        throw new IllegalStateException("Found unexpected CssValueType:" + cssValueType);
    }

    @Override
    public RGBColor getRGBColor() {
        return this;
    }

    public RGBColor getRgbColor() {
        return this;
    }

    @Override
    public void setRGBColor(String color) {
        if (this.handler == null) {
            throw new DOMException(7, "");
        }
        this.handler.rgbColorChanged(color);
    }

    @Override
    public SVGICCColor getICCColor() {
        return this;
    }

    public SVGICCColor getIccColor() {
        return this;
    }

    @Override
    public void setRGBColorICCColor(String rgb, String icc) {
        if (this.handler == null) {
            throw new DOMException(7, "");
        }
        this.iccColors = null;
        this.handler.rgbColorICCColorChanged(rgb, icc);
    }

    @Override
    public void setColor(short type, String rgb, String icc) {
        if (this.handler == null) {
            throw new DOMException(7, "");
        }
        this.iccColors = null;
        this.handler.colorChanged(type, rgb, icc);
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

    @Override
    public String getColorProfile() {
        if (this.getColorType() != 2) {
            throw new DOMException(12, "");
        }
        Value value = this.valueProvider.getValue();
        return ((ICCColor)value.item(1)).getColorProfile();
    }

    @Override
    public void setColorProfile(String colorProfile) throws DOMException {
        if (this.handler == null) {
            throw new DOMException(7, "");
        }
        this.handler.colorProfileChanged(colorProfile);
    }

    @Override
    public SVGNumberList getColors() {
        return this;
    }

    @Override
    public int getNumberOfItems() {
        if (this.getColorType() != 2) {
            throw new DOMException(12, "");
        }
        Value value = this.valueProvider.getValue();
        return ((ICCColor)value.item(1)).getNumberOfColors();
    }

    @Override
    public void clear() throws DOMException {
        if (this.handler == null) {
            throw new DOMException(7, "");
        }
        this.iccColors = null;
        this.handler.colorsCleared();
    }

    @Override
    public SVGNumber initialize(SVGNumber newItem) throws DOMException {
        if (this.handler == null) {
            throw new DOMException(7, "");
        }
        float f = newItem.getValue();
        this.iccColors = new ArrayList();
        ColorNumber result = new ColorNumber(f);
        this.iccColors.add(result);
        this.handler.colorsInitialized(f);
        return result;
    }

    @Override
    public SVGNumber getItem(int index) throws DOMException {
        if (this.getColorType() != 2) {
            throw new DOMException(1, "");
        }
        int n = this.getNumberOfItems();
        if (index < 0 || index >= n) {
            throw new DOMException(1, "");
        }
        if (this.iccColors == null) {
            this.iccColors = new ArrayList(n);
            for (int i = this.iccColors.size(); i < n; ++i) {
                this.iccColors.add(null);
            }
        }
        Value value = this.valueProvider.getValue().item(1);
        float f = ((ICCColor)value).getColor(index);
        ColorNumber result = new ColorNumber(f);
        this.iccColors.set(index, result);
        return result;
    }

    @Override
    public SVGNumber insertItemBefore(SVGNumber newItem, int index) throws DOMException {
        if (this.handler == null) {
            throw new DOMException(7, "");
        }
        int n = this.getNumberOfItems();
        if (index < 0 || index > n) {
            throw new DOMException(1, "");
        }
        if (this.iccColors == null) {
            this.iccColors = new ArrayList(n);
            for (int i = this.iccColors.size(); i < n; ++i) {
                this.iccColors.add(null);
            }
        }
        float f = newItem.getValue();
        ColorNumber result = new ColorNumber(f);
        this.iccColors.add(index, result);
        this.handler.colorInsertedBefore(f, index);
        return result;
    }

    @Override
    public SVGNumber replaceItem(SVGNumber newItem, int index) throws DOMException {
        if (this.handler == null) {
            throw new DOMException(7, "");
        }
        int n = this.getNumberOfItems();
        if (index < 0 || index >= n) {
            throw new DOMException(1, "");
        }
        if (this.iccColors == null) {
            this.iccColors = new ArrayList(n);
            for (int i = this.iccColors.size(); i < n; ++i) {
                this.iccColors.add(null);
            }
        }
        float f = newItem.getValue();
        ColorNumber result = new ColorNumber(f);
        this.iccColors.set(index, result);
        this.handler.colorReplaced(f, index);
        return result;
    }

    @Override
    public SVGNumber removeItem(int index) throws DOMException {
        if (this.handler == null) {
            throw new DOMException(7, "");
        }
        int n = this.getNumberOfItems();
        if (index < 0 || index >= n) {
            throw new DOMException(1, "");
        }
        ColorNumber result = null;
        if (this.iccColors != null) {
            result = (ColorNumber)this.iccColors.get(index);
        }
        if (result == null) {
            Value value = this.valueProvider.getValue().item(1);
            result = new ColorNumber(((ICCColor)value).getColor(index));
        }
        this.handler.colorRemoved(index);
        return result;
    }

    @Override
    public SVGNumber appendItem(SVGNumber newItem) throws DOMException {
        if (this.handler == null) {
            throw new DOMException(7, "");
        }
        if (this.iccColors == null) {
            int n = this.getNumberOfItems();
            this.iccColors = new ArrayList(n);
            for (int i = 0; i < n; ++i) {
                this.iccColors.add(null);
            }
        }
        float f = newItem.getValue();
        ColorNumber result = new ColorNumber(f);
        this.iccColors.add(result);
        this.handler.colorAppend(f);
        return result;
    }

    protected class BlueComponent
    extends FloatComponent {
        protected BlueComponent() {
        }

        @Override
        protected Value getValue() {
            return CSSOMSVGColor.this.valueProvider.getValue().getBlue();
        }

        @Override
        public void setCssText(String cssText) throws DOMException {
            if (CSSOMSVGColor.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMSVGColor.this.handler.blueTextChanged(cssText);
        }

        @Override
        public void setFloatValue(short unitType, float floatValue) throws DOMException {
            if (CSSOMSVGColor.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMSVGColor.this.handler.blueFloatValueChanged(unitType, floatValue);
        }
    }

    protected class GreenComponent
    extends FloatComponent {
        protected GreenComponent() {
        }

        @Override
        protected Value getValue() {
            return CSSOMSVGColor.this.valueProvider.getValue().getGreen();
        }

        @Override
        public void setCssText(String cssText) throws DOMException {
            if (CSSOMSVGColor.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMSVGColor.this.handler.greenTextChanged(cssText);
        }

        @Override
        public void setFloatValue(short unitType, float floatValue) throws DOMException {
            if (CSSOMSVGColor.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMSVGColor.this.handler.greenFloatValueChanged(unitType, floatValue);
        }
    }

    protected class RedComponent
    extends FloatComponent {
        protected RedComponent() {
        }

        @Override
        protected Value getValue() {
            return CSSOMSVGColor.this.valueProvider.getValue().getRed();
        }

        @Override
        public void setCssText(String cssText) throws DOMException {
            if (CSSOMSVGColor.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMSVGColor.this.handler.redTextChanged(cssText);
        }

        @Override
        public void setFloatValue(short unitType, float floatValue) throws DOMException {
            if (CSSOMSVGColor.this.handler == null) {
                throw new DOMException(7, "");
            }
            this.getValue();
            CSSOMSVGColor.this.handler.redFloatValueChanged(unitType, floatValue);
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
            return CSSOMSVGColor.this.valueProvider.getValue().getStringValue();
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

    public abstract class AbstractModificationHandler
    implements ModificationHandler {
        protected abstract Value getValue();

        @Override
        public void redTextChanged(String text) throws DOMException {
            StringBuffer sb = new StringBuffer(40);
            Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 1: {
                    sb.append("rgb(");
                    sb.append(text);
                    sb.append(',');
                    sb.append(value.getGreen().getCssText());
                    sb.append(',');
                    sb.append(value.getBlue().getCssText());
                    sb.append(')');
                    break;
                }
                case 2: {
                    sb.append("rgb(");
                    sb.append(text);
                    sb.append(',');
                    sb.append(value.item(0).getGreen().getCssText());
                    sb.append(',');
                    sb.append(value.item(0).getBlue().getCssText());
                    sb.append(')');
                    sb.append(value.item(1).getCssText());
                    break;
                }
                default: {
                    throw new DOMException(7, "");
                }
            }
            this.textChanged(sb.toString());
        }

        @Override
        public void redFloatValueChanged(short unit, float fValue) throws DOMException {
            StringBuffer sb = new StringBuffer(40);
            Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 1: {
                    sb.append("rgb(");
                    sb.append(FloatValue.getCssText(unit, fValue));
                    sb.append(',');
                    sb.append(value.getGreen().getCssText());
                    sb.append(',');
                    sb.append(value.getBlue().getCssText());
                    sb.append(')');
                    break;
                }
                case 2: {
                    sb.append("rgb(");
                    sb.append(FloatValue.getCssText(unit, fValue));
                    sb.append(',');
                    sb.append(value.item(0).getGreen().getCssText());
                    sb.append(',');
                    sb.append(value.item(0).getBlue().getCssText());
                    sb.append(')');
                    sb.append(value.item(1).getCssText());
                    break;
                }
                default: {
                    throw new DOMException(7, "");
                }
            }
            this.textChanged(sb.toString());
        }

        @Override
        public void greenTextChanged(String text) throws DOMException {
            StringBuffer sb = new StringBuffer(40);
            Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 1: {
                    sb.append("rgb(");
                    sb.append(value.getRed().getCssText());
                    sb.append(',');
                    sb.append(text);
                    sb.append(',');
                    sb.append(value.getBlue().getCssText());
                    sb.append(')');
                    break;
                }
                case 2: {
                    sb.append("rgb(");
                    sb.append(value.item(0).getRed().getCssText());
                    sb.append(',');
                    sb.append(text);
                    sb.append(',');
                    sb.append(value.item(0).getBlue().getCssText());
                    sb.append(')');
                    sb.append(value.item(1).getCssText());
                    break;
                }
                default: {
                    throw new DOMException(7, "");
                }
            }
            this.textChanged(sb.toString());
        }

        @Override
        public void greenFloatValueChanged(short unit, float fValue) throws DOMException {
            StringBuffer sb = new StringBuffer(40);
            Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 1: {
                    sb.append("rgb(");
                    sb.append(value.getRed().getCssText());
                    sb.append(',');
                    sb.append(FloatValue.getCssText(unit, fValue));
                    sb.append(',');
                    sb.append(value.getBlue().getCssText());
                    sb.append(')');
                    break;
                }
                case 2: {
                    sb.append("rgb(");
                    sb.append(value.item(0).getRed().getCssText());
                    sb.append(',');
                    sb.append(FloatValue.getCssText(unit, fValue));
                    sb.append(',');
                    sb.append(value.item(0).getBlue().getCssText());
                    sb.append(')');
                    sb.append(value.item(1).getCssText());
                    break;
                }
                default: {
                    throw new DOMException(7, "");
                }
            }
            this.textChanged(sb.toString());
        }

        @Override
        public void blueTextChanged(String text) throws DOMException {
            StringBuffer sb = new StringBuffer(40);
            Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 1: {
                    sb.append("rgb(");
                    sb.append(value.getRed().getCssText());
                    sb.append(',');
                    sb.append(value.getGreen().getCssText());
                    sb.append(',');
                    sb.append(text);
                    sb.append(')');
                    break;
                }
                case 2: {
                    sb.append("rgb(");
                    sb.append(value.item(0).getRed().getCssText());
                    sb.append(',');
                    sb.append(value.item(0).getGreen().getCssText());
                    sb.append(',');
                    sb.append(text);
                    sb.append(')');
                    sb.append(value.item(1).getCssText());
                    break;
                }
                default: {
                    throw new DOMException(7, "");
                }
            }
            this.textChanged(sb.toString());
        }

        @Override
        public void blueFloatValueChanged(short unit, float fValue) throws DOMException {
            StringBuffer sb = new StringBuffer(40);
            Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 1: {
                    sb.append("rgb(");
                    sb.append(value.getRed().getCssText());
                    sb.append(',');
                    sb.append(value.getGreen().getCssText());
                    sb.append(',');
                    sb.append(FloatValue.getCssText(unit, fValue));
                    sb.append(')');
                    break;
                }
                case 2: {
                    sb.append("rgb(");
                    sb.append(value.item(0).getRed().getCssText());
                    sb.append(',');
                    sb.append(value.item(0).getGreen().getCssText());
                    sb.append(',');
                    sb.append(FloatValue.getCssText(unit, fValue));
                    sb.append(')');
                    sb.append(value.item(1).getCssText());
                    break;
                }
                default: {
                    throw new DOMException(7, "");
                }
            }
            this.textChanged(sb.toString());
        }

        @Override
        public void rgbColorChanged(String text) throws DOMException {
            switch (CSSOMSVGColor.this.getColorType()) {
                case 1: {
                    break;
                }
                case 2: {
                    text = text + this.getValue().item(1).getCssText();
                    break;
                }
                default: {
                    throw new DOMException(7, "");
                }
            }
            this.textChanged(text);
        }

        @Override
        public void rgbColorICCColorChanged(String rgb, String icc) throws DOMException {
            switch (CSSOMSVGColor.this.getColorType()) {
                case 2: {
                    this.textChanged(rgb + ' ' + icc);
                    break;
                }
                default: {
                    throw new DOMException(7, "");
                }
            }
        }

        @Override
        public void colorChanged(short type, String rgb, String icc) throws DOMException {
            switch (type) {
                case 3: {
                    this.textChanged("currentcolor");
                    break;
                }
                case 1: {
                    this.textChanged(rgb);
                    break;
                }
                case 2: {
                    this.textChanged(rgb + ' ' + icc);
                    break;
                }
                default: {
                    throw new DOMException(9, "");
                }
            }
        }

        @Override
        public void colorProfileChanged(String cp) throws DOMException {
            Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 2: {
                    StringBuffer sb = new StringBuffer(value.item(0).getCssText());
                    sb.append(" icc-color(");
                    sb.append(cp);
                    ICCColor iccc = (ICCColor)value.item(1);
                    for (int i = 0; i < iccc.getLength(); ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(')');
                    this.textChanged(sb.toString());
                    break;
                }
                default: {
                    throw new DOMException(7, "");
                }
            }
        }

        @Override
        public void colorsCleared() throws DOMException {
            Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 2: {
                    StringBuffer sb = new StringBuffer(value.item(0).getCssText());
                    sb.append(" icc-color(");
                    ICCColor iccc = (ICCColor)value.item(1);
                    sb.append(iccc.getColorProfile());
                    sb.append(')');
                    this.textChanged(sb.toString());
                    break;
                }
                default: {
                    throw new DOMException(7, "");
                }
            }
        }

        @Override
        public void colorsInitialized(float f) throws DOMException {
            Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 2: {
                    StringBuffer sb = new StringBuffer(value.item(0).getCssText());
                    sb.append(" icc-color(");
                    ICCColor iccc = (ICCColor)value.item(1);
                    sb.append(iccc.getColorProfile());
                    sb.append(',');
                    sb.append(f);
                    sb.append(')');
                    this.textChanged(sb.toString());
                    break;
                }
                default: {
                    throw new DOMException(7, "");
                }
            }
        }

        @Override
        public void colorInsertedBefore(float f, int idx) throws DOMException {
            Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 2: {
                    int i;
                    StringBuffer sb = new StringBuffer(value.item(0).getCssText());
                    sb.append(" icc-color(");
                    ICCColor iccc = (ICCColor)value.item(1);
                    sb.append(iccc.getColorProfile());
                    for (i = 0; i < idx; ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(',');
                    sb.append(f);
                    for (i = idx; i < iccc.getLength(); ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(')');
                    this.textChanged(sb.toString());
                    break;
                }
                default: {
                    throw new DOMException(7, "");
                }
            }
        }

        @Override
        public void colorReplaced(float f, int idx) throws DOMException {
            Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 2: {
                    int i;
                    StringBuffer sb = new StringBuffer(value.item(0).getCssText());
                    sb.append(" icc-color(");
                    ICCColor iccc = (ICCColor)value.item(1);
                    sb.append(iccc.getColorProfile());
                    for (i = 0; i < idx; ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(',');
                    sb.append(f);
                    for (i = idx + 1; i < iccc.getLength(); ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(')');
                    this.textChanged(sb.toString());
                    break;
                }
                default: {
                    throw new DOMException(7, "");
                }
            }
        }

        @Override
        public void colorRemoved(int idx) throws DOMException {
            Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 2: {
                    int i;
                    StringBuffer sb = new StringBuffer(value.item(0).getCssText());
                    sb.append(" icc-color(");
                    ICCColor iccc = (ICCColor)value.item(1);
                    sb.append(iccc.getColorProfile());
                    for (i = 0; i < idx; ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    for (i = idx + 1; i < iccc.getLength(); ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(')');
                    this.textChanged(sb.toString());
                    break;
                }
                default: {
                    throw new DOMException(7, "");
                }
            }
        }

        @Override
        public void colorAppend(float f) throws DOMException {
            Value value = this.getValue();
            switch (CSSOMSVGColor.this.getColorType()) {
                case 2: {
                    StringBuffer sb = new StringBuffer(value.item(0).getCssText());
                    sb.append(" icc-color(");
                    ICCColor iccc = (ICCColor)value.item(1);
                    sb.append(iccc.getColorProfile());
                    for (int i = 0; i < iccc.getLength(); ++i) {
                        sb.append(',');
                        sb.append(iccc.getColor(i));
                    }
                    sb.append(',');
                    sb.append(f);
                    sb.append(')');
                    this.textChanged(sb.toString());
                    break;
                }
                default: {
                    throw new DOMException(7, "");
                }
            }
        }
    }

    public static interface ModificationHandler {
        public void textChanged(String var1) throws DOMException;

        public void redTextChanged(String var1) throws DOMException;

        public void redFloatValueChanged(short var1, float var2) throws DOMException;

        public void greenTextChanged(String var1) throws DOMException;

        public void greenFloatValueChanged(short var1, float var2) throws DOMException;

        public void blueTextChanged(String var1) throws DOMException;

        public void blueFloatValueChanged(short var1, float var2) throws DOMException;

        public void rgbColorChanged(String var1) throws DOMException;

        public void rgbColorICCColorChanged(String var1, String var2) throws DOMException;

        public void colorChanged(short var1, String var2, String var3) throws DOMException;

        public void colorProfileChanged(String var1) throws DOMException;

        public void colorsCleared() throws DOMException;

        public void colorsInitialized(float var1) throws DOMException;

        public void colorInsertedBefore(float var1, int var2) throws DOMException;

        public void colorReplaced(float var1, int var2) throws DOMException;

        public void colorRemoved(int var1) throws DOMException;

        public void colorAppend(float var1) throws DOMException;
    }

    public static interface ValueProvider {
        public Value getValue();
    }

    protected class ColorNumber
    implements SVGNumber {
        protected float value;

        public ColorNumber(float f) {
            this.value = f;
        }

        @Override
        public float getValue() {
            if (CSSOMSVGColor.this.iccColors == null) {
                return this.value;
            }
            int idx = CSSOMSVGColor.this.iccColors.indexOf(this);
            if (idx == -1) {
                return this.value;
            }
            Value value = CSSOMSVGColor.this.valueProvider.getValue().item(1);
            return ((ICCColor)value).getColor(idx);
        }

        @Override
        public void setValue(float f) {
            this.value = f;
            if (CSSOMSVGColor.this.iccColors == null) {
                return;
            }
            int idx = CSSOMSVGColor.this.iccColors.indexOf(this);
            if (idx == -1) {
                return;
            }
            if (CSSOMSVGColor.this.handler == null) {
                throw new DOMException(7, "");
            }
            CSSOMSVGColor.this.handler.colorReplaced(f, idx);
        }
    }
}

