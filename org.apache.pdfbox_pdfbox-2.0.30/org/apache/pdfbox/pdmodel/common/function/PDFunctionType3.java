/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.pdmodel.common.function;

import java.io.IOException;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.common.PDRange;
import org.apache.pdfbox.pdmodel.common.function.PDFunction;

public class PDFunctionType3
extends PDFunction {
    private COSArray functions = null;
    private COSArray encode = null;
    private COSArray bounds = null;
    private PDFunction[] functionsArray = null;
    private float[] boundsValues = null;

    public PDFunctionType3(COSBase functionStream) {
        super(functionStream);
    }

    @Override
    public int getFunctionType() {
        return 3;
    }

    @Override
    public float[] eval(float[] input) throws IOException {
        PDFunction function = null;
        float x = input[0];
        PDRange domain = this.getDomainForInput(0);
        x = this.clipToRange(x, domain.getMin(), domain.getMax());
        if (this.functionsArray == null) {
            COSArray ar = this.getFunctions();
            this.functionsArray = new PDFunction[ar.size()];
            for (int i = 0; i < ar.size(); ++i) {
                this.functionsArray[i] = PDFunction.create(ar.getObject(i));
            }
        }
        if (this.functionsArray.length == 1) {
            function = this.functionsArray[0];
            PDRange encRange = this.getEncodeForParameter(0);
            x = this.interpolate(x, domain.getMin(), domain.getMax(), encRange.getMin(), encRange.getMax());
        } else {
            if (this.boundsValues == null) {
                this.boundsValues = this.getBounds().toFloatArray();
            }
            int boundsSize = this.boundsValues.length;
            float[] partitionValues = new float[boundsSize + 2];
            int partitionValuesSize = partitionValues.length;
            partitionValues[0] = domain.getMin();
            partitionValues[partitionValuesSize - 1] = domain.getMax();
            System.arraycopy(this.boundsValues, 0, partitionValues, 1, boundsSize);
            for (int i = 0; i < partitionValuesSize - 1; ++i) {
                if (!(x >= partitionValues[i]) || !(x < partitionValues[i + 1]) && (i != partitionValuesSize - 2 || x != partitionValues[i + 1])) continue;
                function = this.functionsArray[i];
                PDRange encRange = this.getEncodeForParameter(i);
                x = this.interpolate(x, partitionValues[i], partitionValues[i + 1], encRange.getMin(), encRange.getMax());
                break;
            }
        }
        if (function == null) {
            throw new IOException("partition not found in type 3 function");
        }
        float[] functionValues = new float[]{x};
        float[] functionResult = function.eval(functionValues);
        return this.clipToRange(functionResult);
    }

    public COSArray getFunctions() {
        if (this.functions == null) {
            this.functions = (COSArray)this.getCOSObject().getDictionaryObject(COSName.FUNCTIONS);
        }
        return this.functions;
    }

    public COSArray getBounds() {
        if (this.bounds == null) {
            this.bounds = (COSArray)this.getCOSObject().getDictionaryObject(COSName.BOUNDS);
        }
        return this.bounds;
    }

    public COSArray getEncode() {
        if (this.encode == null) {
            this.encode = (COSArray)this.getCOSObject().getDictionaryObject(COSName.ENCODE);
        }
        return this.encode;
    }

    private PDRange getEncodeForParameter(int n) {
        COSArray encodeValues = this.getEncode();
        return new PDRange(encodeValues, n);
    }
}

