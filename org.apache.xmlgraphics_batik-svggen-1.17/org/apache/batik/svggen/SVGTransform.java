/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.g2d.GraphicContext
 *  org.apache.batik.ext.awt.g2d.TransformStackElement
 */
package org.apache.batik.svggen;

import java.util.Stack;
import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.ext.awt.g2d.TransformStackElement;
import org.apache.batik.svggen.AbstractSVGConverter;
import org.apache.batik.svggen.SVGDescriptor;
import org.apache.batik.svggen.SVGGeneratorContext;
import org.apache.batik.svggen.SVGTransformDescriptor;

public class SVGTransform
extends AbstractSVGConverter {
    private static double radiansToDegrees = 57.29577951308232;

    public SVGTransform(SVGGeneratorContext generatorContext) {
        super(generatorContext);
    }

    @Override
    public SVGDescriptor toSVG(GraphicContext gc) {
        return new SVGTransformDescriptor(this.toSVGTransform(gc));
    }

    public final String toSVGTransform(GraphicContext gc) {
        return this.toSVGTransform(gc.getTransformStack());
    }

    public final String toSVGTransform(TransformStackElement[] transformStack) {
        int nTransforms = transformStack.length;
        Stack presentation = new Stack(){

            public Object push(Object o) {
                Object element;
                if (((TransformStackElement)o).isIdentity()) {
                    element = this.pop();
                } else {
                    super.push(o);
                    element = null;
                }
                return element;
            }

            public Object pop() {
                Object element = null;
                if (!super.empty()) {
                    element = super.pop();
                }
                return element;
            }
        };
        boolean canConcatenate = false;
        int i = 0;
        int j = 0;
        int next = 0;
        TransformStackElement element = null;
        while (i < nTransforms) {
            next = i;
            if (element == null) {
                element = (TransformStackElement)transformStack[i].clone();
            }
            canConcatenate = true;
            for (j = ++next; j < nTransforms && (canConcatenate = element.concatenate(transformStack[j])); ++j) {
            }
            i = j;
            element = presentation.push(element);
        }
        if (element != null) {
            presentation.push(element);
        }
        int nPresentations = presentation.size();
        StringBuffer transformStackBuffer = new StringBuffer(nPresentations * 8);
        for (i = 0; i < nPresentations; ++i) {
            transformStackBuffer.append(this.convertTransform((TransformStackElement)presentation.get(i)));
            transformStackBuffer.append(" ");
        }
        String transformValue = transformStackBuffer.toString().trim();
        return transformValue;
    }

    final String convertTransform(TransformStackElement transformElement) {
        StringBuffer transformString = new StringBuffer();
        double[] transformParameters = transformElement.getTransformParameters();
        switch (transformElement.getType().toInt()) {
            case 0: {
                if (transformElement.isIdentity()) break;
                transformString.append("translate");
                transformString.append("(");
                transformString.append(this.doubleString(transformParameters[0]));
                transformString.append(",");
                transformString.append(this.doubleString(transformParameters[1]));
                transformString.append(")");
                break;
            }
            case 1: {
                if (transformElement.isIdentity()) break;
                transformString.append("rotate");
                transformString.append("(");
                transformString.append(this.doubleString(radiansToDegrees * transformParameters[0]));
                transformString.append(")");
                break;
            }
            case 2: {
                if (transformElement.isIdentity()) break;
                transformString.append("scale");
                transformString.append("(");
                transformString.append(this.doubleString(transformParameters[0]));
                transformString.append(",");
                transformString.append(this.doubleString(transformParameters[1]));
                transformString.append(")");
                break;
            }
            case 3: {
                if (transformElement.isIdentity()) break;
                transformString.append("matrix");
                transformString.append("(");
                transformString.append(1);
                transformString.append(",");
                transformString.append(this.doubleString(transformParameters[1]));
                transformString.append(",");
                transformString.append(this.doubleString(transformParameters[0]));
                transformString.append(",");
                transformString.append(1);
                transformString.append(",");
                transformString.append(0);
                transformString.append(",");
                transformString.append(0);
                transformString.append(")");
                break;
            }
            case 4: {
                if (transformElement.isIdentity()) break;
                transformString.append("matrix");
                transformString.append("(");
                transformString.append(this.doubleString(transformParameters[0]));
                transformString.append(",");
                transformString.append(this.doubleString(transformParameters[1]));
                transformString.append(",");
                transformString.append(this.doubleString(transformParameters[2]));
                transformString.append(",");
                transformString.append(this.doubleString(transformParameters[3]));
                transformString.append(",");
                transformString.append(this.doubleString(transformParameters[4]));
                transformString.append(",");
                transformString.append(this.doubleString(transformParameters[5]));
                transformString.append(")");
                break;
            }
            default: {
                throw new RuntimeException();
            }
        }
        return transformString.toString();
    }
}

