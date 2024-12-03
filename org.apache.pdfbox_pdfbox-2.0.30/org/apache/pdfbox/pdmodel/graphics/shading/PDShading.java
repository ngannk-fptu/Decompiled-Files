/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.graphics.shading;

import java.awt.Paint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;
import org.apache.pdfbox.pdmodel.graphics.color.PDColorSpace;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType1;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType2;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType3;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType4;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType5;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType6;
import org.apache.pdfbox.pdmodel.graphics.shading.PDShadingType7;
import org.apache.pdfbox.util.Matrix;

public abstract class PDShading
implements COSObjectable {
    private final COSDictionary dictionary;
    private COSArray background = null;
    private PDRectangle bBox = null;
    private PDColorSpace colorSpace = null;
    private PDFunction function = null;
    private PDFunction[] functionArray = null;
    public static final int SHADING_TYPE1 = 1;
    public static final int SHADING_TYPE2 = 2;
    public static final int SHADING_TYPE3 = 3;
    public static final int SHADING_TYPE4 = 4;
    public static final int SHADING_TYPE5 = 5;
    public static final int SHADING_TYPE6 = 6;
    public static final int SHADING_TYPE7 = 7;

    public PDShading() {
        this.dictionary = new COSDictionary();
    }

    public PDShading(COSDictionary shadingDictionary) {
        this.dictionary = shadingDictionary;
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.dictionary;
    }

    public String getType() {
        return COSName.SHADING.getName();
    }

    public void setShadingType(int shadingType) {
        this.dictionary.setInt(COSName.SHADING_TYPE, shadingType);
    }

    public abstract int getShadingType();

    public void setBackground(COSArray newBackground) {
        this.background = newBackground;
        this.dictionary.setItem(COSName.BACKGROUND, (COSBase)newBackground);
    }

    public COSArray getBackground() {
        if (this.background == null) {
            this.background = (COSArray)this.dictionary.getDictionaryObject(COSName.BACKGROUND);
        }
        return this.background;
    }

    public PDRectangle getBBox() {
        COSArray array;
        if (this.bBox == null && (array = (COSArray)this.dictionary.getDictionaryObject(COSName.BBOX)) != null) {
            this.bBox = new PDRectangle(array);
        }
        return this.bBox;
    }

    public void setBBox(PDRectangle newBBox) {
        this.bBox = newBBox;
        if (this.bBox == null) {
            this.dictionary.removeItem(COSName.BBOX);
        } else {
            this.dictionary.setItem(COSName.BBOX, (COSBase)this.bBox.getCOSArray());
        }
    }

    public Rectangle2D getBounds(AffineTransform xform, Matrix matrix) throws IOException {
        return null;
    }

    public void setAntiAlias(boolean antiAlias) {
        this.dictionary.setBoolean(COSName.ANTI_ALIAS, antiAlias);
    }

    public boolean getAntiAlias() {
        return this.dictionary.getBoolean(COSName.ANTI_ALIAS, false);
    }

    public PDColorSpace getColorSpace() throws IOException {
        if (this.colorSpace == null) {
            COSBase colorSpaceDictionary = this.dictionary.getDictionaryObject(COSName.CS, COSName.COLORSPACE);
            this.colorSpace = PDColorSpace.create(colorSpaceDictionary);
        }
        return this.colorSpace;
    }

    public void setColorSpace(PDColorSpace colorSpace) {
        this.colorSpace = colorSpace;
        if (colorSpace != null) {
            this.dictionary.setItem(COSName.COLORSPACE, colorSpace.getCOSObject());
        } else {
            this.dictionary.removeItem(COSName.COLORSPACE);
        }
    }

    public static PDShading create(COSDictionary shadingDictionary) throws IOException {
        PDShading shading = null;
        int shadingType = shadingDictionary.getInt(COSName.SHADING_TYPE, 0);
        switch (shadingType) {
            case 1: {
                shading = new PDShadingType1(shadingDictionary);
                break;
            }
            case 2: {
                shading = new PDShadingType2(shadingDictionary);
                break;
            }
            case 3: {
                shading = new PDShadingType3(shadingDictionary);
                break;
            }
            case 4: {
                shading = new PDShadingType4(shadingDictionary);
                break;
            }
            case 5: {
                shading = new PDShadingType5(shadingDictionary);
                break;
            }
            case 6: {
                shading = new PDShadingType6(shadingDictionary);
                break;
            }
            case 7: {
                shading = new PDShadingType7(shadingDictionary);
                break;
            }
            default: {
                throw new IOException("Error: Unknown shading type " + shadingType);
            }
        }
        return shading;
    }

    public void setFunction(PDFunction newFunction) {
        this.functionArray = null;
        this.function = newFunction;
        this.getCOSObject().setItem(COSName.FUNCTION, (COSObjectable)newFunction);
    }

    public void setFunction(COSArray newFunctions) {
        this.functionArray = null;
        this.function = null;
        this.getCOSObject().setItem(COSName.FUNCTION, (COSBase)newFunctions);
    }

    public PDFunction getFunction() throws IOException {
        COSBase dictionaryFunctionObject;
        if (this.function == null && (dictionaryFunctionObject = this.getCOSObject().getDictionaryObject(COSName.FUNCTION)) != null) {
            this.function = PDFunction.create(dictionaryFunctionObject);
        }
        return this.function;
    }

    private PDFunction[] getFunctionsArray() throws IOException {
        if (this.functionArray == null) {
            COSBase functionObject = this.getCOSObject().getDictionaryObject(COSName.FUNCTION);
            if (functionObject instanceof COSDictionary) {
                this.functionArray = new PDFunction[1];
                this.functionArray[0] = PDFunction.create(functionObject);
            } else if (functionObject instanceof COSArray) {
                COSArray functionCOSArray = (COSArray)functionObject;
                int numberOfFunctions = functionCOSArray.size();
                this.functionArray = new PDFunction[numberOfFunctions];
                for (int i = 0; i < numberOfFunctions; ++i) {
                    this.functionArray[i] = PDFunction.create(functionCOSArray.get(i));
                }
            } else {
                throw new IOException("mandatory /Function element must be a dictionary or an array");
            }
        }
        return this.functionArray;
    }

    public float[] evalFunction(float inputValue) throws IOException {
        return this.evalFunction(new float[]{inputValue});
    }

    public float[] evalFunction(float[] input) throws IOException {
        int i;
        float[] returnValues;
        PDFunction[] functions = this.getFunctionsArray();
        int numberOfFunctions = functions.length;
        if (numberOfFunctions == 1) {
            returnValues = functions[0].eval(input);
        } else {
            returnValues = new float[numberOfFunctions];
            for (i = 0; i < numberOfFunctions; ++i) {
                float[] newValue = functions[i].eval(input);
                returnValues[i] = newValue[0];
            }
        }
        for (i = 0; i < returnValues.length; ++i) {
            if (returnValues[i] < 0.0f) {
                returnValues[i] = 0.0f;
                continue;
            }
            if (!(returnValues[i] > 1.0f)) continue;
            returnValues[i] = 1.0f;
        }
        return returnValues;
    }

    public abstract Paint toPaint(Matrix var1);
}

