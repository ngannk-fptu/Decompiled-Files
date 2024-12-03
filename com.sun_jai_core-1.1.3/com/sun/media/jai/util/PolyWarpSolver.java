/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import java.util.Random;

public class PolyWarpSolver {
    private static Random myRandom = new Random(0L);
    private static double[] c0 = new double[6];
    private static double[] c1 = new double[6];
    private static double noise = 0.0;

    private static double sign(double a, double b) {
        a = Math.abs(a);
        if (b >= 0.0) {
            return a;
        }
        return -a;
    }

    private static final double square(double x) {
        return x * x;
    }

    private static final double sqrt(double x) {
        return Math.sqrt(x);
    }

    private static final double hypot(double x, double y) {
        double yabs;
        double xabs = Math.abs(x);
        if (xabs > (yabs = Math.abs(y))) {
            return xabs * PolyWarpSolver.sqrt(PolyWarpSolver.square(yabs / xabs) + 1.0);
        }
        if (yabs != 0.0) {
            return yabs * PolyWarpSolver.sqrt(PolyWarpSolver.square(xabs / yabs) + 1.0);
        }
        return xabs;
    }

    public static double[][] matmul_t(double[][] A, double[][] B) {
        int rowsA = A.length;
        int colsA = A[0].length;
        int rowsB = B[0].length;
        int colsB = B.length;
        double[][] out = new double[rowsA][colsB];
        for (int i = 0; i < rowsA; ++i) {
            double[] outi = out[i];
            double[] Ai = A[i];
            for (int j = 0; j < colsB; ++j) {
                double tmp = 0.0;
                for (int k = 0; k < colsA; ++k) {
                    tmp += Ai[k] * B[j][k];
                }
                outi[j] = tmp;
            }
        }
        return out;
    }

    private static boolean SVD(double[][] a, double[] w, double[][] u, double[][] v) {
        double h;
        double f;
        int k;
        double s;
        int j;
        int i;
        int l = 0;
        int l1 = 0;
        int m = a.length;
        int n = a[0].length;
        double[] rv1 = new double[n];
        for (i = 0; i < m; ++i) {
            for (j = 0; j < n; ++j) {
                u[i][j] = a[i][j];
            }
        }
        double g = 0.0;
        double scale = 0.0;
        double x = 0.0;
        for (i = 0; i < n; ++i) {
            l = i + 1;
            rv1[i] = scale * g;
            g = 0.0;
            s = 0.0;
            scale = 0.0;
            if (i < m) {
                for (k = i; k < m; ++k) {
                    scale += Math.abs(u[k][i]);
                }
                if (scale != 0.0) {
                    for (k = i; k < m; ++k) {
                        double[] dArray = u[k];
                        int n2 = i;
                        dArray[n2] = dArray[n2] / scale;
                        s += PolyWarpSolver.square(u[k][i]);
                    }
                    f = u[i][i];
                    g = -PolyWarpSolver.sign(PolyWarpSolver.sqrt(s), f);
                    h = f * g - s;
                    u[i][i] = f - g;
                    for (j = l; j < n; ++j) {
                        s = 0.0;
                        for (k = i; k < m; ++k) {
                            s += u[k][i] * u[k][j];
                        }
                        f = s / h;
                        for (k = i; k < m; ++k) {
                            double[] dArray = u[k];
                            int n3 = j;
                            dArray[n3] = dArray[n3] + f * u[k][i];
                        }
                    }
                    for (k = i; k < m; ++k) {
                        double[] dArray = u[k];
                        int n4 = i;
                        dArray[n4] = dArray[n4] * scale;
                    }
                }
            }
            w[i] = scale * g;
            g = 0.0;
            s = 0.0;
            scale = 0.0;
            if (i < m && i != n - 1) {
                for (k = l; k < n; ++k) {
                    scale += Math.abs(u[i][k]);
                }
                if (scale != 0.0) {
                    for (k = l; k < n; ++k) {
                        double[] dArray = u[i];
                        int n5 = k;
                        dArray[n5] = dArray[n5] / scale;
                        s += PolyWarpSolver.square(u[i][k]);
                    }
                    f = u[i][l];
                    g = -PolyWarpSolver.sign(PolyWarpSolver.sqrt(s), f);
                    h = f * g - s;
                    u[i][l] = f - g;
                    for (k = l; k < n; ++k) {
                        rv1[k] = u[i][k] / h;
                    }
                    for (j = l; j < m; ++j) {
                        s = 0.0;
                        for (k = l; k < n; ++k) {
                            s += u[j][k] * u[i][k];
                        }
                        for (k = l; k < n; ++k) {
                            double[] dArray = u[j];
                            int n6 = k;
                            dArray[n6] = dArray[n6] + s * rv1[k];
                        }
                    }
                    k = l;
                    while (k < n) {
                        double[] dArray = u[i];
                        int n7 = k++;
                        dArray[n7] = dArray[n7] * scale;
                    }
                }
            }
            x = Math.max(x, Math.abs(w[i]) + Math.abs(rv1[i]));
        }
        i = n - 1;
        while (i >= 0) {
            if (i != n - 1) {
                if (g != 0.0) {
                    for (j = l; j < n; ++j) {
                        v[j][i] = u[i][j] / u[i][l] / g;
                    }
                    for (j = l; j < n; ++j) {
                        s = 0.0;
                        for (k = l; k < n; ++k) {
                            s += u[i][k] * v[k][j];
                        }
                        for (k = l; k < n; ++k) {
                            double[] dArray = v[k];
                            int n8 = j;
                            dArray[n8] = dArray[n8] + s * v[k][i];
                        }
                    }
                }
                for (j = l; j < n; ++j) {
                    v[j][i] = 0.0;
                    v[i][j] = 0.0;
                }
            }
            v[i][i] = 1.0;
            g = rv1[i];
            l = i--;
        }
        int mn = Math.min(m, n);
        i = mn - 1;
        while (i >= 0) {
            l = i + 1;
            g = w[i];
            if (i != n - 1) {
                for (j = l; j < n; ++j) {
                    u[i][j] = 0.0;
                }
            }
            if (g != 0.0) {
                if (i != mn - 1) {
                    for (j = l; j < n; ++j) {
                        s = 0.0;
                        for (k = l; k < m; ++k) {
                            s += u[k][i] * u[k][j];
                        }
                        f = s / u[i][i] / g;
                        for (k = i; k < m; ++k) {
                            double[] dArray = u[k];
                            int n9 = j;
                            dArray[n9] = dArray[n9] + f * u[k][i];
                        }
                    }
                }
                for (j = i; j < m; ++j) {
                    double[] dArray = u[j];
                    int n10 = i;
                    dArray[n10] = dArray[n10] / g;
                }
            } else {
                for (j = i; j < m; ++j) {
                    u[j][i] = 0.0;
                }
            }
            double[] dArray = u[i];
            int n11 = i--;
            dArray[n11] = dArray[n11] + 1.0;
        }
        double tst1 = x;
        block29: for (k = n - 1; k >= 0; --k) {
            int k1 = k - 1;
            int its = 0;
            while (true) {
                double z;
                double y;
                double c;
                double tst2;
                boolean flag = true;
                for (l = k; l >= 0; --l) {
                    l1 = l - 1;
                    tst2 = tst1 + Math.abs(rv1[l]);
                    if (tst2 == tst1) {
                        flag = false;
                        break;
                    }
                    tst2 = tst1 + Math.abs(w[l1]);
                    if (tst2 != tst1) continue;
                    flag = true;
                    break;
                }
                if (flag) {
                    c = 0.0;
                    s = 1.0;
                    for (i = l; i < k + 1; ++i) {
                        f = s * rv1[i];
                        int n12 = i;
                        rv1[n12] = rv1[n12] * c;
                        tst2 = tst1 + Math.abs(f);
                        if (tst2 == tst1) continue;
                        g = w[i];
                        w[i] = h = PolyWarpSolver.hypot(f, g);
                        c = g / h;
                        s = -f / h;
                        for (j = 0; j < m; ++j) {
                            y = u[j][l1];
                            z = u[j][i];
                            u[j][l1] = y * c + z * s;
                            u[j][i] = -y * s + z * c;
                        }
                    }
                }
                z = w[k];
                if (l == k) {
                    if (!(z < 0.0)) continue block29;
                    w[k] = -z;
                    for (j = 0; j < n; ++j) {
                        v[j][k] = -v[j][k];
                    }
                    continue block29;
                }
                if (its == 30) {
                    return false;
                }
                ++its;
                x = w[l];
                y = w[k1];
                g = rv1[k1];
                h = rv1[k];
                f = 0.5 * ((g + z) / h * ((g - z) / y) + y / h - h / y);
                g = PolyWarpSolver.hypot(f, 1.0);
                f = x - z / x * z + h / x * (y / (f + PolyWarpSolver.sign(g, f)) - h);
                c = 1.0;
                s = 1.0;
                for (int i1 = l; i1 <= k1; ++i1) {
                    i = i1 + 1;
                    g = rv1[i];
                    y = w[i];
                    h = s * g;
                    g = c * g;
                    rv1[i1] = z = PolyWarpSolver.hypot(f, h);
                    c = f / z;
                    s = h / z;
                    f = x * c + g * s;
                    g = -x * s + g * c;
                    h = y * s;
                    y *= c;
                    for (j = 0; j < n; ++j) {
                        x = v[j][i1];
                        z = v[j][i];
                        v[j][i1] = x * c + z * s;
                        v[j][i] = -x * s + z * c;
                    }
                    w[i1] = z = PolyWarpSolver.hypot(f, h);
                    if (z != 0.0) {
                        c = f / z;
                        s = h / z;
                    }
                    f = c * g + s * y;
                    x = -s * g + c * y;
                    for (j = 0; j < m; ++j) {
                        y = u[j][i1];
                        z = u[j][i];
                        u[j][i1] = y * c + z * s;
                        u[j][i] = -y * s + z * c;
                    }
                }
                rv1[l] = 0.0;
                rv1[k] = f;
                w[k] = x;
            }
        }
        return true;
    }

    public static float[] getCoeffs(float[] sourceCoords, int sourceOffset, float[] destCoords, int destOffset, int numCoords, float preScaleX, float preScaleY, float postScaleX, float postScaleY, int degree) {
        int j;
        int i;
        int equations = numCoords / 2;
        int unknowns = (degree + 1) * (degree + 2) / 2;
        float[] out = new float[2 * unknowns];
        if (degree == 1 && numCoords == 3) {
            double x0 = sourceCoords[0] * preScaleX;
            double y0 = sourceCoords[1] * preScaleY;
            double x1 = sourceCoords[2] * preScaleX;
            double y1 = sourceCoords[3] * preScaleY;
            double x2 = sourceCoords[4] * preScaleY;
            double y2 = sourceCoords[5] * preScaleY;
            double u0 = destCoords[0] / postScaleX;
            double v0 = destCoords[1] / postScaleY;
            double u1 = destCoords[2] / postScaleX;
            double v1 = destCoords[3] / postScaleY;
            double u2 = destCoords[4] / postScaleX;
            double v2 = destCoords[5] / postScaleY;
            double v0mv1 = v0 - v1;
            double v1mv2 = v1 - v2;
            double v2mv0 = v2 - v0;
            double u1mu0 = u1 - u0;
            double u2mu1 = u2 - u1;
            double u0mu2 = u0 - u2;
            double u1v2mu2v1 = u1 * v2 - u2 * v1;
            double u2v0mu0v2 = u2 * v0 - u0 * v2;
            double u0v1mu1v0 = u0 * v1 - u1 * v0;
            double invdet = 1.0 / (u0 * v1mv2 + v0 * u2mu1 + u1v2mu2v1);
            out[0] = (float)((v1mv2 * x0 + v2mv0 * x1 + v0mv1 * x2) * invdet);
            out[1] = (float)((u2mu1 * x0 + u0mu2 * x1 + u1mu0 * x2) * invdet);
            out[2] = (float)((u1v2mu2v1 * x0 + u2v0mu0v2 * x1 + u0v1mu1v0 * x2) * invdet);
            out[3] = (float)((v1mv2 * y0 + v2mv0 * y1 + v0mv1 * y2) * invdet);
            out[4] = (float)((u2mu1 * y0 + u0mu2 * y1 + u1mu0 * y2) * invdet);
            out[5] = (float)((u1v2mu2v1 * y0 + u2v0mu0v2 * y1 + u0v1mu1v0 * y2) * invdet);
            return out;
        }
        double[][] A = new double[equations][unknowns];
        double[] xpow = new double[degree + 1];
        double[] ypow = new double[degree + 1];
        for (i = 0; i < equations; ++i) {
            double[] Ai = A[i];
            double x = destCoords[2 * i + destOffset] / postScaleX;
            double y = destCoords[2 * i + 1 + destOffset] / postScaleY;
            double xtmp = 1.0;
            double ytmp = 1.0;
            for (int d = 0; d <= degree; ++d) {
                xpow[d] = xtmp;
                ypow[d] = ytmp;
                xtmp *= x;
                ytmp *= y;
            }
            int index = 0;
            for (int deg = 0; deg <= degree; ++deg) {
                for (int ydeg = 0; ydeg <= deg; ++ydeg) {
                    Ai[index++] = xpow[deg - ydeg] * ypow[ydeg];
                }
            }
        }
        double[][] V = new double[unknowns][unknowns];
        double[] W = new double[unknowns];
        double[][] U = new double[equations][unknowns];
        PolyWarpSolver.SVD(A, W, U, V);
        for (j = 0; j < unknowns; ++j) {
            double winv = W[j];
            if (winv != 0.0) {
                winv = 1.0 / winv;
            }
            for (i = 0; i < unknowns; ++i) {
                double[] dArray = V[i];
                int n = j;
                dArray[n] = dArray[n] * winv;
            }
        }
        double[][] VWINVUT = PolyWarpSolver.matmul_t(V, U);
        for (i = 0; i < unknowns; ++i) {
            double tmp0 = 0.0;
            double tmp1 = 0.0;
            for (j = 0; j < equations; ++j) {
                double val = VWINVUT[i][j];
                tmp0 += val * (double)sourceCoords[2 * j + sourceOffset] * (double)preScaleX;
                tmp1 += val * (double)sourceCoords[2 * j + 1 + sourceOffset] * (double)preScaleY;
            }
            out[i] = (float)tmp0;
            out[i + unknowns] = (float)tmp1;
        }
        return out;
    }

    private static float xpoly(float x, float y) {
        return (float)(c0[0] + c0[1] * (double)x + c0[2] * (double)y + c0[3] * (double)x * (double)x + c0[4] * (double)x * (double)y + c0[5] * (double)y * (double)y + myRandom.nextDouble() * noise);
    }

    private static float ypoly(float x, float y) {
        return (float)(c1[0] + c1[1] * (double)x + c1[2] * (double)y + c1[3] * (double)x * (double)x + c1[4] * (double)x * (double)y + c1[5] * (double)y * (double)y + myRandom.nextDouble() * noise);
    }

    private static void doTest(int equations, boolean print) {
        for (int i = 0; i < 6; ++i) {
            PolyWarpSolver.c0[i] = myRandom.nextDouble() * 100.0;
            PolyWarpSolver.c1[i] = myRandom.nextDouble() * 100.0;
        }
        float[] destCoords = new float[2 * equations];
        for (int i = 0; i < 2 * equations; ++i) {
            destCoords[i] = myRandom.nextFloat() * 100.0f;
        }
        float[] sourceCoords = new float[2 * equations];
        for (int i = 0; i < equations; ++i) {
            sourceCoords[2 * i] = PolyWarpSolver.xpoly(destCoords[2 * i], destCoords[2 * i + 1]);
            sourceCoords[2 * i + 1] = PolyWarpSolver.ypoly(destCoords[2 * i], destCoords[2 * i + 1]);
        }
        float[] coeffs = PolyWarpSolver.getCoeffs(sourceCoords, 0, destCoords, 0, sourceCoords.length, 0.5f, 0.5f, 2.0f, 2.0f, 2);
        if (print) {
            System.out.println("Using " + equations + " equations:");
            for (int i = 0; i < 6; ++i) {
                System.out.println("c0[" + i + "] = " + c0[i] + ", recovered as " + coeffs[i] + " (ratio = " + c0[i] / (double)coeffs[i] + ")");
                System.out.println("c1[" + i + "] = " + c1[i] + ", recovered as " + coeffs[i + 6] + " (ratio = " + c1[i] / (double)coeffs[i + 6] + ")");
            }
        }
    }

    public static void main(String[] args) {
        for (int times = 0; times < 3; ++times) {
            PolyWarpSolver.doTest(6 + 50 * times, true);
            System.out.println();
        }
        int trials = 10000;
        int points = 6;
        long startTime = System.currentTimeMillis();
        for (int times = 0; times < trials; ++times) {
            PolyWarpSolver.doTest(points, false);
        }
        long endTime = System.currentTimeMillis();
        System.out.println("Did " + trials + " " + points + "-point solutions in " + (float)(endTime - startTime) / 1000.0f + " seconds.");
        System.out.println("Rate = " + (float)trials * 1000.0f / (float)(endTime - startTime) + " trials/second");
    }
}

