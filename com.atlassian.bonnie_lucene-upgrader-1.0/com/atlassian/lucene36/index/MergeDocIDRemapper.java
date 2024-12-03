/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.index;

import com.atlassian.lucene36.index.MergePolicy;
import com.atlassian.lucene36.index.SegmentInfo;
import com.atlassian.lucene36.index.SegmentInfos;

final class MergeDocIDRemapper {
    int[] starts;
    int[] newStarts;
    int[][] docMaps;
    int minDocID;
    int maxDocID;
    int docShift;

    public MergeDocIDRemapper(SegmentInfos infos, int[][] docMaps, int[] delCounts, MergePolicy.OneMerge merge, int mergedDocCount) {
        SegmentInfo info;
        this.docMaps = docMaps;
        SegmentInfo firstSegment = merge.segments.get(0);
        int i = 0;
        while (!(info = infos.info(i)).equals(firstSegment)) {
            this.minDocID += info.docCount;
            ++i;
        }
        int numDocs = 0;
        for (int j = 0; j < docMaps.length; ++j) {
            numDocs += infos.info((int)i).docCount;
            assert (infos.info(i).equals(merge.segments.get(j)));
            ++i;
        }
        this.maxDocID = this.minDocID + numDocs;
        this.starts = new int[docMaps.length];
        this.newStarts = new int[docMaps.length];
        this.starts[0] = this.minDocID;
        this.newStarts[0] = this.minDocID;
        for (i = 1; i < docMaps.length; ++i) {
            int lastDocCount = merge.segments.get((int)(i - 1)).docCount;
            this.starts[i] = this.starts[i - 1] + lastDocCount;
            this.newStarts[i] = this.newStarts[i - 1] + lastDocCount - delCounts[i - 1];
        }
        this.docShift = numDocs - mergedDocCount;
        assert (this.docShift == this.maxDocID - (this.newStarts[docMaps.length - 1] + merge.segments.get((int)(docMaps.length - 1)).docCount - delCounts[docMaps.length - 1]));
    }

    public int remap(int oldDocID) {
        if (oldDocID < this.minDocID) {
            return oldDocID;
        }
        if (oldDocID >= this.maxDocID) {
            return oldDocID - this.docShift;
        }
        int lo = 0;
        int hi = this.docMaps.length - 1;
        while (hi >= lo) {
            int mid = lo + hi >>> 1;
            int midValue = this.starts[mid];
            if (oldDocID < midValue) {
                hi = mid - 1;
                continue;
            }
            if (oldDocID > midValue) {
                lo = mid + 1;
                continue;
            }
            while (mid + 1 < this.docMaps.length && this.starts[mid + 1] == midValue) {
                ++mid;
            }
            if (this.docMaps[mid] != null) {
                return this.newStarts[mid] + this.docMaps[mid][oldDocID - this.starts[mid]];
            }
            return this.newStarts[mid] + oldDocID - this.starts[mid];
        }
        if (this.docMaps[hi] != null) {
            return this.newStarts[hi] + this.docMaps[hi][oldDocID - this.starts[hi]];
        }
        return this.newStarts[hi] + oldDocID - this.starts[hi];
    }
}

