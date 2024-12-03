/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.detect;

import org.apache.tika.detect.TrainedModel;

public class NNTrainedModel
extends TrainedModel {
    private int numOfInputs;
    private int numOfHidden;
    private int numOfOutputs;
    private float[][] Theta1;
    private float[][] Theta2;

    public NNTrainedModel(int nInput, int nHidden, int nOutput, float[] nn_params) {
        this.numOfInputs = nInput;
        this.numOfHidden = nHidden;
        this.numOfOutputs = nOutput;
        this.Theta1 = new float[this.numOfHidden][this.numOfInputs + 1];
        this.Theta2 = new float[this.numOfOutputs][this.numOfHidden + 1];
        this.populateThetas(nn_params);
    }

    private void populateThetas(float[] nn_params) {
        int j;
        int i;
        int m = this.Theta1.length;
        int n = this.Theta1[0].length;
        int k = 0;
        for (i = 0; i < n; ++i) {
            for (j = 0; j < m; ++j) {
                this.Theta1[j][i] = nn_params[k];
                ++k;
            }
        }
        m = this.Theta2.length;
        n = this.Theta2[0].length;
        for (i = 0; i < n; ++i) {
            for (j = 0; j < m; ++j) {
                this.Theta2[j][i] = nn_params[k];
                ++k;
            }
        }
    }

    @Override
    public double predict(double[] unseen) {
        return 0.0;
    }

    @Override
    public float predict(float[] unseen) {
        int j;
        int i;
        int m = this.Theta1.length;
        int n = this.Theta1[0].length;
        float[] hh = new float[m + 1];
        hh[0] = 1.0f;
        for (i = 0; i < m; ++i) {
            double h = 0.0;
            for (j = 0; j < n; ++j) {
                h += (double)(this.Theta1[i][j] * unseen[j]);
            }
            h = 1.0 / (1.0 + Math.exp(-h));
            hh[i + 1] = (float)h;
        }
        m = this.Theta2.length;
        n = this.Theta2[0].length;
        float[] oo = new float[m];
        for (i = 0; i < m; ++i) {
            double o = 0.0;
            for (j = 0; j < n; ++j) {
                o += (double)(this.Theta2[i][j] * hh[j]);
            }
            o = 1.0 / (1.0 + Math.exp(-o));
            oo[i] = (float)o;
        }
        return oo[0];
    }
}

