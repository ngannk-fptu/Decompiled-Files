/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.usermodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.poi.hwpf.model.BookmarksTables;
import org.apache.poi.hwpf.model.GenericPropertyNode;
import org.apache.poi.hwpf.model.PropertyNode;
import org.apache.poi.hwpf.usermodel.Bookmark;
import org.apache.poi.hwpf.usermodel.Bookmarks;

public class BookmarksImpl
implements Bookmarks {
    private final BookmarksTables bookmarksTables;
    private Map<Integer, List<GenericPropertyNode>> sortedDescriptors;
    private int[] sortedStartPositions;

    public BookmarksImpl(BookmarksTables bookmarksTables) {
        this.bookmarksTables = bookmarksTables;
        this.reset();
    }

    void afterDelete(int startCp, int length) {
        this.bookmarksTables.afterDelete(startCp, length);
        this.reset();
    }

    void afterInsert(int startCp, int length) {
        this.bookmarksTables.afterInsert(startCp, length);
        this.reset();
    }

    private Bookmark getBookmark(GenericPropertyNode first) {
        return new BookmarkImpl(first);
    }

    @Override
    public Bookmark getBookmark(int index) {
        GenericPropertyNode first = this.bookmarksTables.getDescriptorFirst(index);
        return this.getBookmark(first);
    }

    public List<Bookmark> getBookmarksAt(int startCp) {
        this.updateSortedDescriptors();
        List<GenericPropertyNode> nodes = this.sortedDescriptors.get(startCp);
        if (nodes == null || nodes.isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<Bookmark> result = new ArrayList<Bookmark>(nodes.size());
        for (GenericPropertyNode node : nodes) {
            result.add(this.getBookmark(node));
        }
        return Collections.unmodifiableList(result);
    }

    @Override
    public int getBookmarksCount() {
        return this.bookmarksTables.getDescriptorsFirstCount();
    }

    @Override
    public Map<Integer, List<Bookmark>> getBookmarksStartedBetween(int startInclusive, int endExclusive) {
        int endLookupIndex;
        this.updateSortedDescriptors();
        int startLookupIndex = Arrays.binarySearch(this.sortedStartPositions, startInclusive);
        if (startLookupIndex < 0) {
            startLookupIndex = -(startLookupIndex + 1);
        }
        if ((endLookupIndex = Arrays.binarySearch(this.sortedStartPositions, endExclusive)) < 0) {
            endLookupIndex = -(endLookupIndex + 1);
        }
        LinkedHashMap<Integer, List<Bookmark>> result = new LinkedHashMap<Integer, List<Bookmark>>();
        for (int lookupIndex = startLookupIndex; lookupIndex < endLookupIndex; ++lookupIndex) {
            int s = this.sortedStartPositions[lookupIndex];
            if (s < startInclusive) continue;
            if (s >= endExclusive) break;
            List<Bookmark> startedAt = this.getBookmarksAt(s);
            if (startedAt == null) continue;
            result.put(s, startedAt);
        }
        return Collections.unmodifiableMap(result);
    }

    @Override
    public void remove(int index) {
        this.bookmarksTables.remove(index);
    }

    private void reset() {
        this.sortedDescriptors = null;
        this.sortedStartPositions = null;
    }

    private void updateSortedDescriptors() {
        if (this.sortedDescriptors != null) {
            return;
        }
        HashMap<Integer, List<GenericPropertyNode>> result = new HashMap<Integer, List<GenericPropertyNode>>();
        for (int b = 0; b < this.bookmarksTables.getDescriptorsFirstCount(); ++b) {
            GenericPropertyNode property = this.bookmarksTables.getDescriptorFirst(b);
            Integer positionKey = property.getStart();
            List list = result.computeIfAbsent(positionKey, k -> new LinkedList());
            list.add(property);
        }
        int counter = 0;
        int[] indices = new int[result.size()];
        for (Map.Entry entry : result.entrySet()) {
            indices[counter++] = (Integer)entry.getKey();
            ArrayList updated = new ArrayList((Collection)entry.getValue());
            updated.sort(PropertyNode.EndComparator);
            entry.setValue(updated);
        }
        Arrays.sort(indices);
        this.sortedDescriptors = result;
        this.sortedStartPositions = indices;
    }

    private final class BookmarkImpl
    implements Bookmark {
        private final GenericPropertyNode first;

        private BookmarkImpl(GenericPropertyNode first) {
            this.first = first;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }
            BookmarkImpl other = (BookmarkImpl)obj;
            return !(this.first == null ? other.first != null : !this.first.equals(other.first));
        }

        @Override
        public int getEnd() {
            int currentIndex = BookmarksImpl.this.bookmarksTables.getDescriptorFirstIndex(this.first);
            try {
                GenericPropertyNode descriptorLim = BookmarksImpl.this.bookmarksTables.getDescriptorLim(currentIndex);
                return descriptorLim.getStart();
            }
            catch (IndexOutOfBoundsException exc) {
                return this.first.getEnd();
            }
        }

        @Override
        public String getName() {
            int currentIndex = BookmarksImpl.this.bookmarksTables.getDescriptorFirstIndex(this.first);
            try {
                return BookmarksImpl.this.bookmarksTables.getName(currentIndex);
            }
            catch (ArrayIndexOutOfBoundsException exc) {
                return "";
            }
        }

        @Override
        public int getStart() {
            return this.first.getStart();
        }

        public int hashCode() {
            return Objects.hash(this.first);
        }

        @Override
        public void setName(String name) {
            int currentIndex = BookmarksImpl.this.bookmarksTables.getDescriptorFirstIndex(this.first);
            BookmarksImpl.this.bookmarksTables.setName(currentIndex, name);
        }

        public String toString() {
            return "Bookmark [" + this.getStart() + "; " + this.getEnd() + "): name: " + this.getName();
        }
    }
}

