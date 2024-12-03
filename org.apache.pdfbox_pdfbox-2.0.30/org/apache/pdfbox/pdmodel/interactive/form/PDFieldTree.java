/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.pdfbox.pdmodel.interactive.form;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.apache.pdfbox.pdmodel.interactive.form.PDField;
import org.apache.pdfbox.pdmodel.interactive.form.PDNonTerminalField;

public class PDFieldTree
implements Iterable<PDField> {
    private static final Log LOG = LogFactory.getLog(PDFieldTree.class);
    private final PDAcroForm acroForm;

    public PDFieldTree(PDAcroForm acroForm) {
        if (acroForm == null) {
            throw new IllegalArgumentException("root cannot be null");
        }
        this.acroForm = acroForm;
    }

    @Override
    public Iterator<PDField> iterator() {
        return new FieldIterator(this.acroForm);
    }

    private static final class FieldIterator
    implements Iterator<PDField> {
        private final Queue<PDField> queue = new ArrayDeque<PDField>();
        private final Set<COSDictionary> set = Collections.newSetFromMap(new IdentityHashMap());

        private FieldIterator(PDAcroForm form) {
            List<PDField> fields = form.getFields();
            for (PDField field : fields) {
                this.enqueueKids(field);
            }
        }

        @Override
        public boolean hasNext() {
            return !this.queue.isEmpty();
        }

        @Override
        public PDField next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.queue.poll();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void enqueueKids(PDField node) {
            this.queue.add(node);
            this.set.add(node.getCOSObject());
            if (node instanceof PDNonTerminalField) {
                List<PDField> kids = ((PDNonTerminalField)node).getChildren();
                for (PDField kid : kids) {
                    if (this.set.contains(kid.getCOSObject())) {
                        LOG.error((Object)("Child of field '" + node.getFullyQualifiedName() + "' already exists elsewhere, ignored to avoid recursion"));
                        continue;
                    }
                    this.enqueueKids(kid);
                }
            }
        }
    }
}

