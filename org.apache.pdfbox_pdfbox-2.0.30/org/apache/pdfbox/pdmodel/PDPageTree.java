/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.ResourceCache;
import org.apache.pdfbox.pdmodel.common.COSObjectable;

public class PDPageTree
implements COSObjectable,
Iterable<PDPage> {
    private static final Log LOG = LogFactory.getLog(PDPageTree.class);
    private final COSDictionary root;
    private final PDDocument document;
    private final Set<COSDictionary> pageSet = new HashSet<COSDictionary>();

    public PDPageTree() {
        this.root = new COSDictionary();
        this.root.setItem(COSName.TYPE, (COSBase)COSName.PAGES);
        this.root.setItem(COSName.KIDS, (COSBase)new COSArray());
        this.root.setItem(COSName.COUNT, (COSBase)COSInteger.ZERO);
        this.document = null;
    }

    public PDPageTree(COSDictionary root) {
        this(root, null);
    }

    PDPageTree(COSDictionary root, PDDocument document) {
        if (root == null) {
            throw new IllegalArgumentException("page tree root cannot be null");
        }
        if (COSName.PAGE.equals(root.getCOSName(COSName.TYPE))) {
            COSArray kids = new COSArray();
            kids.add(root);
            this.root = new COSDictionary();
            this.root.setItem(COSName.KIDS, (COSBase)kids);
            this.root.setInt(COSName.COUNT, 1);
        } else {
            this.root = root;
        }
        this.document = document;
    }

    public static COSBase getInheritableAttribute(COSDictionary node, COSName key) {
        COSDictionary parent;
        COSBase value = node.getDictionaryObject(key);
        if (value != null) {
            return value;
        }
        COSBase base = node.getDictionaryObject(COSName.PARENT, COSName.P);
        if (base instanceof COSDictionary && COSName.PAGES.equals((parent = (COSDictionary)base).getDictionaryObject(COSName.TYPE))) {
            return PDPageTree.getInheritableAttribute(parent, key);
        }
        return null;
    }

    @Override
    public Iterator<PDPage> iterator() {
        return new PageIterator(this.root);
    }

    private List<COSDictionary> getKids(COSDictionary node) {
        ArrayList<COSDictionary> result = new ArrayList<COSDictionary>();
        COSArray kids = node.getCOSArray(COSName.KIDS);
        if (kids == null) {
            return result;
        }
        int size = kids.size();
        for (int i = 0; i < size; ++i) {
            COSBase base = kids.getObject(i);
            if (base instanceof COSDictionary) {
                result.add((COSDictionary)base);
                continue;
            }
            if (base == null) {
                LOG.warn((Object)"replaced null entry with an empty page");
                COSDictionary emptyPage = new COSDictionary();
                emptyPage.setItem(COSName.TYPE, (COSBase)COSName.PAGE);
                kids.set(i, emptyPage);
                result.add(emptyPage);
                continue;
            }
            LOG.warn((Object)("COSDictionary expected, but got " + (base == null ? "null" : base.getClass().getSimpleName())));
        }
        return result;
    }

    public PDPage get(int index) {
        COSDictionary dict = this.get(index + 1, this.root, 0);
        PDPageTree.sanitizeType(dict);
        ResourceCache resourceCache = this.document != null ? this.document.getResourceCache() : null;
        return new PDPage(dict, resourceCache);
    }

    private static void sanitizeType(COSDictionary dictionary) {
        COSName type = dictionary.getCOSName(COSName.TYPE);
        if (type == null) {
            dictionary.setItem(COSName.TYPE, (COSBase)COSName.PAGE);
            return;
        }
        if (!COSName.PAGE.equals(type)) {
            throw new IllegalStateException("Expected 'Page' but found " + type);
        }
    }

    private COSDictionary get(int pageNum, COSDictionary node, int encountered) {
        if (pageNum < 1) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + pageNum);
        }
        if (this.pageSet.contains(node)) {
            this.pageSet.clear();
            throw new IllegalStateException("Possible recursion found when searching for page " + pageNum);
        }
        this.pageSet.add(node);
        if (this.isPageTreeNode(node)) {
            int count = node.getInt(COSName.COUNT, 0);
            if (pageNum <= encountered + count) {
                for (COSDictionary kid : this.getKids(node)) {
                    if (this.isPageTreeNode(kid)) {
                        int kidCount = kid.getInt(COSName.COUNT, 0);
                        if (pageNum <= encountered + kidCount) {
                            return this.get(pageNum, kid, encountered);
                        }
                        encountered += kidCount;
                        continue;
                    }
                    if (pageNum != ++encountered) continue;
                    return this.get(pageNum, kid, encountered);
                }
                throw new IllegalStateException("1-based index not found: " + pageNum);
            }
            throw new IndexOutOfBoundsException("1-based index out of bounds: " + pageNum);
        }
        if (encountered == pageNum) {
            this.pageSet.clear();
            return node;
        }
        throw new IllegalStateException("1-based index not found: " + pageNum);
    }

    private boolean isPageTreeNode(COSDictionary node) {
        return node != null && (node.getCOSName(COSName.TYPE) == COSName.PAGES || node.containsKey(COSName.KIDS));
    }

    public int indexOf(PDPage page) {
        SearchContext context = new SearchContext(page);
        if (this.findPage(context, this.root)) {
            return context.index;
        }
        return -1;
    }

    private boolean findPage(SearchContext context, COSDictionary node) {
        for (COSDictionary kid : this.getKids(node)) {
            if (context.found) break;
            if (this.isPageTreeNode(kid)) {
                this.findPage(context, kid);
                continue;
            }
            context.visitPage(kid);
        }
        return context.found;
    }

    public int getCount() {
        return this.root.getInt(COSName.COUNT, 0);
    }

    @Override
    public COSDictionary getCOSObject() {
        return this.root;
    }

    public void remove(int index) {
        COSDictionary node = this.get(index + 1, this.root, 0);
        this.remove(node);
    }

    public void remove(PDPage page) {
        this.remove(page.getCOSObject());
    }

    private void remove(COSDictionary node) {
        COSDictionary parent = (COSDictionary)node.getDictionaryObject(COSName.PARENT, COSName.P);
        COSArray kids = (COSArray)parent.getDictionaryObject(COSName.KIDS);
        if (kids.removeObject(node)) {
            do {
                if ((node = (COSDictionary)node.getDictionaryObject(COSName.PARENT, COSName.P)) == null) continue;
                node.setInt(COSName.COUNT, node.getInt(COSName.COUNT) - 1);
            } while (node != null);
        }
    }

    public void add(PDPage page) {
        COSDictionary node = page.getCOSObject();
        node.setItem(COSName.PARENT, (COSBase)this.root);
        COSArray kids = (COSArray)this.root.getDictionaryObject(COSName.KIDS);
        kids.add(node);
        do {
            if ((node = (COSDictionary)node.getDictionaryObject(COSName.PARENT, COSName.P)) == null) continue;
            node.setInt(COSName.COUNT, node.getInt(COSName.COUNT) + 1);
        } while (node != null);
    }

    public void insertBefore(PDPage newPage, PDPage nextPage) {
        COSDictionary nextPageDict = nextPage.getCOSObject();
        COSDictionary parentDict = (COSDictionary)nextPageDict.getDictionaryObject(COSName.PARENT);
        COSArray kids = (COSArray)parentDict.getDictionaryObject(COSName.KIDS);
        boolean found = false;
        for (int i = 0; i < kids.size(); ++i) {
            COSDictionary pageDict = (COSDictionary)kids.getObject(i);
            if (!pageDict.equals(nextPage.getCOSObject())) continue;
            kids.add(i, newPage.getCOSObject());
            newPage.getCOSObject().setItem(COSName.PARENT, (COSBase)parentDict);
            found = true;
            break;
        }
        if (!found) {
            throw new IllegalArgumentException("attempted to insert before orphan page");
        }
        this.increaseParents(parentDict);
    }

    public void insertAfter(PDPage newPage, PDPage prevPage) {
        COSDictionary prevPageDict = prevPage.getCOSObject();
        COSDictionary parentDict = (COSDictionary)prevPageDict.getDictionaryObject(COSName.PARENT);
        COSArray kids = (COSArray)parentDict.getDictionaryObject(COSName.KIDS);
        boolean found = false;
        for (int i = 0; i < kids.size(); ++i) {
            COSDictionary pageDict = (COSDictionary)kids.getObject(i);
            if (!pageDict.equals(prevPage.getCOSObject())) continue;
            kids.add(i + 1, newPage.getCOSObject());
            newPage.getCOSObject().setItem(COSName.PARENT, (COSBase)parentDict);
            found = true;
            break;
        }
        if (!found) {
            throw new IllegalArgumentException("attempted to insert before orphan page");
        }
        this.increaseParents(parentDict);
    }

    private void increaseParents(COSDictionary parentDict) {
        do {
            int cnt = parentDict.getInt(COSName.COUNT);
            parentDict.setInt(COSName.COUNT, cnt + 1);
        } while ((parentDict = (COSDictionary)parentDict.getDictionaryObject(COSName.PARENT)) != null);
    }

    private static final class SearchContext {
        private final COSDictionary searched;
        private int index = -1;
        private boolean found;

        private SearchContext(PDPage page) {
            this.searched = page.getCOSObject();
        }

        private void visitPage(COSDictionary current) {
            ++this.index;
            this.found = this.searched == current;
        }
    }

    private final class PageIterator
    implements Iterator<PDPage> {
        private final Queue<COSDictionary> queue = new ArrayDeque<COSDictionary>();
        private Set<COSDictionary> set = new HashSet<COSDictionary>();

        private PageIterator(COSDictionary node) {
            this.enqueueKids(node);
            this.set = null;
        }

        private void enqueueKids(COSDictionary node) {
            if (PDPageTree.this.isPageTreeNode(node)) {
                List kids = PDPageTree.this.getKids(node);
                for (COSDictionary kid : kids) {
                    if (this.set.contains(kid)) {
                        LOG.error((Object)"This page tree node has already been visited");
                        continue;
                    }
                    if (kid.containsKey(COSName.KIDS)) {
                        this.set.add(kid);
                    }
                    this.enqueueKids(kid);
                }
            } else if (COSName.PAGE.equals(node.getCOSName(COSName.TYPE))) {
                this.queue.add(node);
            } else {
                LOG.error((Object)("Page skipped due to an invalid or missing type " + node.getCOSName(COSName.TYPE)));
            }
        }

        @Override
        public boolean hasNext() {
            return !this.queue.isEmpty();
        }

        @Override
        public PDPage next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            COSDictionary next = this.queue.poll();
            PDPageTree.sanitizeType(next);
            ResourceCache resourceCache = PDPageTree.this.document != null ? PDPageTree.this.document.getResourceCache() : null;
            return new PDPage(next, resourceCache);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

