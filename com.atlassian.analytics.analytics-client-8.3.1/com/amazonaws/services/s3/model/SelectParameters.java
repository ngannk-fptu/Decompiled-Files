/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.ExpressionType;
import com.amazonaws.services.s3.model.InputSerialization;
import com.amazonaws.services.s3.model.OutputSerialization;
import com.amazonaws.services.s3.model.ScanRange;
import java.io.Serializable;

public class SelectParameters
implements Serializable,
Cloneable {
    private InputSerialization inputSerialization;
    private String expressionType;
    private String expression;
    private ScanRange scanRange;
    private OutputSerialization outputSerialization;

    public InputSerialization getInputSerialization() {
        return this.inputSerialization;
    }

    public void setInputSerialization(InputSerialization inputSerialization) {
        this.inputSerialization = inputSerialization;
    }

    public SelectParameters withInputSerialization(InputSerialization inputSerialization) {
        this.setInputSerialization(inputSerialization);
        return this;
    }

    public String getExpressionType() {
        return this.expressionType;
    }

    public void setExpressionType(String expressionType) {
        this.expressionType = expressionType;
    }

    public SelectParameters withExpressionType(String expressionType) {
        this.setExpressionType(expressionType);
        return this;
    }

    public SelectParameters withExpressionType(ExpressionType expressionType) {
        this.setExpressionType(expressionType == null ? null : expressionType.toString());
        return this;
    }

    public String getExpression() {
        return this.expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public SelectParameters withExpression(String expression) {
        this.setExpression(expression);
        return this;
    }

    public ScanRange getScanRange() {
        return this.scanRange;
    }

    public void setScanRange(ScanRange scanRange) {
        this.scanRange = scanRange;
    }

    public SelectParameters withRange(ScanRange scanRange) {
        this.setScanRange(scanRange);
        return this;
    }

    public OutputSerialization getOutputSerialization() {
        return this.outputSerialization;
    }

    public void setOutputSerialization(OutputSerialization outputSerialization) {
        this.outputSerialization = outputSerialization;
    }

    public SelectParameters withOutputSerialization(OutputSerialization outputSerialization) {
        this.setOutputSerialization(outputSerialization);
        return this;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof SelectParameters)) {
            return false;
        }
        SelectParameters other = (SelectParameters)obj;
        if (other.getInputSerialization() == null ^ this.getInputSerialization() == null) {
            return false;
        }
        if (other.getInputSerialization() != null && !other.getInputSerialization().equals(this.getInputSerialization())) {
            return false;
        }
        if (other.getExpressionType() == null ^ this.getExpressionType() == null) {
            return false;
        }
        if (other.getExpressionType() != null && !other.getExpressionType().equals(this.getExpressionType())) {
            return false;
        }
        if (other.getExpression() == null ^ this.getExpression() == null) {
            return false;
        }
        if (other.getExpression() != null && !other.getExpression().equals(this.getExpression())) {
            return false;
        }
        if (other.getOutputSerialization() == null ^ this.getOutputSerialization() == null) {
            return false;
        }
        return other.getOutputSerialization() == null || other.getOutputSerialization().equals(this.getOutputSerialization());
    }

    public int hashCode() {
        int prime = 31;
        int hashCode = 1;
        hashCode = 31 * hashCode + (this.getInputSerialization() == null ? 0 : this.getInputSerialization().hashCode());
        hashCode = 31 * hashCode + (this.getExpressionType() == null ? 0 : this.getExpressionType().hashCode());
        hashCode = 31 * hashCode + (this.getExpression() == null ? 0 : this.getExpression().hashCode());
        hashCode = 31 * hashCode + (this.getOutputSerialization() == null ? 0 : this.getOutputSerialization().hashCode());
        return hashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (this.getInputSerialization() != null) {
            sb.append("InputSerialization: ").append(this.getInputSerialization()).append(",");
        }
        if (this.getExpressionType() != null) {
            sb.append("ExpressionType: ").append(this.getExpressionType()).append(",");
        }
        if (this.getExpression() != null) {
            sb.append("Expression: ").append(this.getExpression()).append(",");
        }
        if (this.getOutputSerialization() != null) {
            sb.append("OutputSerialization: ").append(this.getOutputSerialization());
        }
        sb.append("}");
        return sb.toString();
    }

    public SelectParameters clone() {
        try {
            return (SelectParameters)super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Got a CloneNotSupportedException from Object.clone() even though we're Cloneable!", e);
        }
    }
}

