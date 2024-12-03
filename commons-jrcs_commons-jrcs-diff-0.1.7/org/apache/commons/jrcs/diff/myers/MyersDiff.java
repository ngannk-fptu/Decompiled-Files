/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.jrcs.diff.myers;

import org.apache.commons.jrcs.diff.Chunk;
import org.apache.commons.jrcs.diff.Delta;
import org.apache.commons.jrcs.diff.DiffAlgorithm;
import org.apache.commons.jrcs.diff.DifferentiationFailedException;
import org.apache.commons.jrcs.diff.Revision;
import org.apache.commons.jrcs.diff.myers.DiffNode;
import org.apache.commons.jrcs.diff.myers.PathNode;
import org.apache.commons.jrcs.diff.myers.Snake;

public class MyersDiff
implements DiffAlgorithm {
    public Revision diff(Object[] orig, Object[] rev) throws DifferentiationFailedException {
        PathNode path = MyersDiff.buildPath(orig, rev);
        return MyersDiff.buildRevision(path, orig, rev);
    }

    public static PathNode buildPath(Object[] orig, Object[] rev) throws DifferentiationFailedException {
        if (orig == null) {
            throw new IllegalArgumentException("original sequence is null");
        }
        if (rev == null) {
            throw new IllegalArgumentException("revised sequence is null");
        }
        int N = orig.length;
        int M = rev.length;
        int MAX = N + M + 1;
        int size = 1 + 2 * MAX;
        int middle = (size + 1) / 2;
        PathNode[] diagonal = new PathNode[size];
        Object path = null;
        diagonal[middle + 1] = new Snake(0, -1, null);
        int d = 0;
        while (d < MAX) {
            int k = -d;
            while (k <= d) {
                int i;
                int kmiddle = middle + k;
                int kplus = kmiddle + 1;
                int kminus = kmiddle - 1;
                PathNode prev = null;
                if (k == -d || k != d && diagonal[kminus].i < diagonal[kplus].i) {
                    i = diagonal[kplus].i;
                    prev = diagonal[kplus];
                } else {
                    i = diagonal[kminus].i + 1;
                    prev = diagonal[kminus];
                }
                diagonal[kminus] = null;
                int j = i - k;
                PathNode node = new DiffNode(i, j, prev);
                while (i < N && j < M && orig[i].equals(rev[j])) {
                    ++i;
                    ++j;
                }
                if (i > node.i) {
                    node = new Snake(i, j, node);
                }
                diagonal[kmiddle] = node;
                if (i >= N && j >= M) {
                    return diagonal[kmiddle];
                }
                k += 2;
            }
            diagonal[middle + d - 1] = null;
            ++d;
        }
        throw new DifferentiationFailedException("could not find a diff path");
    }

    public static Revision buildRevision(PathNode path, Object[] orig, Object[] rev) {
        if (path == null) {
            throw new IllegalArgumentException("path is null");
        }
        if (orig == null) {
            throw new IllegalArgumentException("original sequence is null");
        }
        if (rev == null) {
            throw new IllegalArgumentException("revised sequence is null");
        }
        Revision revision = new Revision();
        if (path.isSnake()) {
            path = path.prev;
        }
        while (path != null && path.prev != null && path.prev.j >= 0) {
            if (path.isSnake()) {
                throw new IllegalStateException("bad diffpath: found snake when looking for diff");
            }
            int i = path.i;
            int j = path.j;
            path = path.prev;
            int ianchor = path.i;
            int janchor = path.j;
            Delta delta = Delta.newDelta(new Chunk(orig, ianchor, i - ianchor), new Chunk(rev, janchor, j - janchor));
            revision.insertDelta(delta);
            if (!path.isSnake()) continue;
            path = path.prev;
        }
        return revision;
    }
}

