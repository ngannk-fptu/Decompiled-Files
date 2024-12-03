/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.core.runtime.OperationCanceledException
 *  org.eclipse.core.runtime.SubMonitor
 */
package org.eclipse.compare.internal;

import org.eclipse.compare.internal.LCSSettings;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.SubMonitor;

public abstract class LCS {
    private int max_differences;
    private int length;

    public void longestCommonSubsequence(SubMonitor subMonitor, LCSSettings settings) {
        int backBoundL2;
        int forwardBound;
        int length1 = this.getLength1();
        int length2 = this.getLength2();
        if (length1 == 0 || length2 == 0) {
            this.length = 0;
            return;
        }
        this.max_differences = (length1 + length2 + 1) / 2;
        if ((double)length1 * (double)length2 > settings.getTooLong()) {
            this.max_differences = (int)Math.pow(this.max_differences, settings.getPowLimit() - 1.0);
        }
        this.initializeLcs(length1);
        subMonitor.beginTask(null, length1);
        int max = Math.min(length1, length2);
        for (forwardBound = 0; forwardBound < max && this.isRangeEqual(forwardBound, forwardBound); ++forwardBound) {
            this.setLcs(forwardBound, forwardBound);
            this.worked(subMonitor, 1);
        }
        int backBoundL1 = length1 - 1;
        for (backBoundL2 = length2 - 1; backBoundL1 >= forwardBound && backBoundL2 >= forwardBound && this.isRangeEqual(backBoundL1, backBoundL2); --backBoundL1, --backBoundL2) {
            this.setLcs(backBoundL1, backBoundL2);
            this.worked(subMonitor, 1);
        }
        this.length = forwardBound + length1 - backBoundL1 - 1 + this.lcs_rec(forwardBound, backBoundL1, forwardBound, backBoundL2, new int[2][length1 + length2 + 1], new int[3], subMonitor);
    }

    private int lcs_rec(int bottoml1, int topl1, int bottoml2, int topl2, int[][] V, int[] snake, SubMonitor subMonitor) {
        if (bottoml1 > topl1 || bottoml2 > topl2) {
            return 0;
        }
        int d = this.find_middle_snake(bottoml1, topl1, bottoml2, topl2, V, snake);
        int len = snake[2];
        int startx = snake[0];
        int starty = snake[1];
        for (int i = 0; i < len; ++i) {
            this.setLcs(startx + i, starty + i);
            this.worked(subMonitor, 1);
        }
        if (d > 1) {
            return len + this.lcs_rec(bottoml1, startx - 1, bottoml2, starty - 1, V, snake, subMonitor) + this.lcs_rec(startx + len, topl1, starty + len, topl2, V, snake, subMonitor);
        }
        if (d == 1) {
            int max = Math.min(startx - bottoml1, starty - bottoml2);
            for (int i = 0; i < max; ++i) {
                this.setLcs(bottoml1 + i, bottoml2 + i);
                this.worked(subMonitor, 1);
            }
            return max + len;
        }
        return len;
    }

    private void worked(SubMonitor subMonitor, int work) {
        if (subMonitor.isCanceled()) {
            throw new OperationCanceledException();
        }
        subMonitor.worked(work);
    }

    private int find_middle_snake(int bottoml1, int topl1, int bottoml2, int topl2, int[][] V, int[] snake) {
        int N = topl1 - bottoml1 + 1;
        int M = topl2 - bottoml2 + 1;
        int delta = N - M;
        boolean isEven = (delta & 1) != 1;
        int limit = Math.min(this.max_differences, (N + M + 1) / 2);
        int value_to_add_forward = (M & 1) == 1 ? 1 : 0;
        int value_to_add_backward = (N & 1) == 1 ? 1 : 0;
        int start_forward = -M;
        int end_forward = N;
        int start_backward = -N;
        int end_backward = M;
        V[0][limit + 1] = 0;
        V[1][limit - 1] = N;
        for (int d = 0; d <= limit; ++d) {
            int y;
            int x;
            int k;
            int start_diag = Math.max(value_to_add_forward + start_forward, -d);
            int end_diag = Math.min(end_forward, d);
            value_to_add_forward = 1 - value_to_add_forward;
            for (k = start_diag; k <= end_diag; k += 2) {
                x = k == -d || k < d && V[0][limit + k - 1] < V[0][limit + k + 1] ? V[0][limit + k + 1] : V[0][limit + k - 1] + 1;
                snake[0] = x + bottoml1;
                snake[1] = y + bottoml2;
                snake[2] = 0;
                for (y = x - k; x < N && y < M && this.isRangeEqual(x + bottoml1, y + bottoml2); ++x, ++y) {
                    snake[2] = snake[2] + 1;
                }
                V[0][limit + k] = x;
                if (!isEven && k >= delta - d + 1 && k <= delta + d - 1 && x >= V[1][limit + k - delta]) {
                    return 2 * d - 1;
                }
                if (x >= N && end_forward > k - 1) {
                    end_forward = k - 1;
                    continue;
                }
                if (y < M) continue;
                start_forward = k + 1;
                value_to_add_forward = 0;
            }
            start_diag = Math.max(value_to_add_backward + start_backward, -d);
            end_diag = Math.min(end_backward, d);
            value_to_add_backward = 1 - value_to_add_backward;
            for (k = start_diag; k <= end_diag; k += 2) {
                x = k == d || k != -d && V[1][limit + k - 1] < V[1][limit + k + 1] ? V[1][limit + k - 1] : V[1][limit + k + 1] - 1;
                snake[2] = 0;
                for (y = x - k - delta; x > 0 && y > 0 && this.isRangeEqual(x - 1 + bottoml1, y - 1 + bottoml2); --x, --y) {
                    snake[2] = snake[2] + 1;
                }
                V[1][limit + k] = x;
                if (isEven && k >= -delta - d && k <= d - delta && x <= V[0][limit + k + delta]) {
                    snake[0] = bottoml1 + x;
                    snake[1] = bottoml2 + y;
                    return 2 * d;
                }
                if (x <= 0) {
                    start_backward = k + 1;
                    value_to_add_backward = 0;
                    continue;
                }
                if (y > 0 || end_backward <= k - 1) continue;
                end_backward = k - 1;
            }
        }
        int[] most_progress = LCS.findMostProgress(M, N, limit, V);
        snake[0] = bottoml1 + most_progress[0];
        snake[1] = bottoml2 + most_progress[1];
        snake[2] = 0;
        return 5;
    }

    private static int[] findMostProgress(int M, int N, int limit, int[][] V) {
        int delta = N - M;
        int forward_start_diag = (M & 1) == (limit & 1) ? Math.max(-M, -limit) : Math.max(1 - M, -limit);
        int forward_end_diag = Math.min(N, limit);
        int backward_start_diag = (N & 1) == (limit & 1) ? Math.max(-N, -limit) : Math.max(1 - N, -limit);
        int backward_end_diag = Math.min(M, limit);
        int[][] max_progress = new int[Math.max(forward_end_diag - forward_start_diag, backward_end_diag - backward_start_diag) / 2 + 1][3];
        int num_progress = 0;
        for (int k = forward_start_diag; k <= forward_end_diag; k += 2) {
            int x = V[0][limit + k];
            int y = x - k;
            if (x > N || y > M) continue;
            int progress = x + y;
            if (progress > max_progress[0][2]) {
                num_progress = 0;
                max_progress[0][0] = x;
                max_progress[0][1] = y;
                max_progress[0][2] = progress;
                continue;
            }
            if (progress != max_progress[0][2]) continue;
            max_progress[++num_progress][0] = x;
            max_progress[num_progress][1] = y;
            max_progress[num_progress][2] = progress;
        }
        boolean max_progress_forward = true;
        for (int k = backward_start_diag; k <= backward_end_diag; k += 2) {
            int x = V[1][limit + k];
            int y = x - k - delta;
            if (x < 0 || y < 0) continue;
            int progress = N - x + M - y;
            if (progress > max_progress[0][2]) {
                num_progress = 0;
                max_progress_forward = false;
                max_progress[0][0] = x;
                max_progress[0][1] = y;
                max_progress[0][2] = progress;
                continue;
            }
            if (progress != max_progress[0][2] || max_progress_forward) continue;
            max_progress[++num_progress][0] = x;
            max_progress[num_progress][1] = y;
            max_progress[num_progress][2] = progress;
        }
        return max_progress[num_progress / 2];
    }

    protected abstract int getLength2();

    protected abstract int getLength1();

    protected abstract boolean isRangeEqual(int var1, int var2);

    protected abstract void setLcs(int var1, int var2);

    protected abstract void initializeLcs(int var1);

    public int getLength() {
        return this.length;
    }
}

