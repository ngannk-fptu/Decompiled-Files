/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bouncycastle.asn1.tsp.PartialHashtree
 *  org.bouncycastle.util.Arrays
 */
package org.bouncycastle.tsp.ers;

import java.util.ArrayList;
import java.util.List;
import org.bouncycastle.asn1.tsp.PartialHashtree;
import org.bouncycastle.operator.DigestCalculator;
import org.bouncycastle.tsp.ers.ERSRootNodeCalculator;
import org.bouncycastle.tsp.ers.ERSUtil;
import org.bouncycastle.tsp.ers.SortedHashList;
import org.bouncycastle.util.Arrays;

public class BinaryTreeRootCalculator
implements ERSRootNodeCalculator {
    private List<List<byte[]>> tree;

    @Override
    public byte[] computeRootHash(DigestCalculator digCalc, PartialHashtree[] nodes) {
        SortedHashList hashes = new SortedHashList();
        for (int i = 0; i < nodes.length; ++i) {
            byte[] left = ERSUtil.computeNodeHash(digCalc, nodes[i]);
            hashes.add(left);
        }
        List<byte[]> hashValues = hashes.toList();
        this.tree = new ArrayList<List<byte[]>>();
        this.tree.add(hashValues);
        if (hashValues.size() > 1) {
            ArrayList<byte[]> newHashes;
            do {
                newHashes = new ArrayList<byte[]>(hashValues.size() / 2 + 1);
                for (int i = 0; i <= hashValues.size() - 2; i += 2) {
                    newHashes.add(ERSUtil.calculateBranchHash(digCalc, hashValues.get(i), hashValues.get(i + 1)));
                }
                if (hashValues.size() % 2 == 1) {
                    newHashes.add(hashValues.get(hashValues.size() - 1));
                }
                this.tree.add(newHashes);
            } while ((hashValues = newHashes).size() > 1);
        }
        return hashValues.get(0);
    }

    @Override
    public PartialHashtree[] computePathToRoot(DigestCalculator digCalc, PartialHashtree node, int index) {
        ArrayList<PartialHashtree> path = new ArrayList<PartialHashtree>();
        byte[] nodeHash = ERSUtil.computeNodeHash(digCalc, node);
        path.add(node);
        for (int row = 0; row < this.tree.size() - 1; ++row) {
            if (index == this.tree.get(row).size() - 1) {
                List<byte[]> hashes;
                while (Arrays.areEqual((byte[])nodeHash, (byte[])(hashes = this.tree.get(row + 1)).get(hashes.size() - 1))) {
                    index = this.tree.get(++row).size() - 1;
                }
            }
            byte[] neighborHash = (index & 1) == 0 ? this.tree.get(row).get(index + 1) : this.tree.get(row).get(index - 1);
            path.add(new PartialHashtree(neighborHash));
            nodeHash = ERSUtil.calculateBranchHash(digCalc, nodeHash, neighborHash);
            index /= 2;
        }
        return path.toArray(new PartialHashtree[0]);
    }

    @Override
    public byte[] recoverRootHash(DigestCalculator digCalc, PartialHashtree[] nodes) {
        byte[] baseHash = ERSUtil.computeNodeHash(digCalc, nodes[0]);
        for (int i = 1; i < nodes.length; ++i) {
            baseHash = ERSUtil.calculateBranchHash(digCalc, baseHash, ERSUtil.computeNodeHash(digCalc, nodes[i]));
        }
        return baseHash;
    }
}

