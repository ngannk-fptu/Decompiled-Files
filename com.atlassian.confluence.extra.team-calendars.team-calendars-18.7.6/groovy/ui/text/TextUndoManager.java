/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui.text;

import java.beans.PropertyChangeListener;
import javax.swing.event.SwingPropertyChangeSupport;
import javax.swing.event.UndoableEditEvent;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoManager;
import javax.swing.undo.UndoableEdit;

public class TextUndoManager
extends UndoManager {
    private SwingPropertyChangeSupport propChangeSupport = new SwingPropertyChangeSupport(this);
    private StructuredEdit compoundEdit = new StructuredEdit();
    private long firstModified;
    private UndoableEdit modificationMarker = this.editToBeUndone();

    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        this.propChangeSupport.addPropertyChangeListener(pcl);
    }

    @Override
    public void die() {
        boolean undoable = this.canUndo();
        super.die();
        this.firePropertyChangeEvent("Undo", undoable, this.canUndo());
    }

    @Override
    public void discardAllEdits() {
        boolean undoable = this.canUndo();
        boolean redoable = this.canRedo();
        boolean changed = this.hasChanged();
        super.discardAllEdits();
        this.modificationMarker = this.editToBeUndone();
        this.firePropertyChangeEvent("Undo", undoable, this.canUndo());
        this.firePropertyChangeEvent("Undo", redoable, this.canRedo());
    }

    protected void firePropertyChangeEvent(String name, boolean oldValue, boolean newValue) {
        this.propChangeSupport.firePropertyChange(name, oldValue, newValue);
    }

    public boolean hasChanged() {
        return this.modificationMarker != this.editToBeUndone();
    }

    @Override
    public void redo() throws CannotRedoException {
        this.compoundEdit.end();
        if (this.firstModified == 0L) {
            this.firstModified = ((StructuredEdit)this.editToBeRedone()).editedTime();
        }
        boolean undoable = this.canUndo();
        boolean changed = this.hasChanged();
        super.redo();
        this.firePropertyChangeEvent("Undo", undoable, this.canUndo());
    }

    @Override
    protected void redoTo(UndoableEdit edit) {
        this.compoundEdit.end();
        if (this.firstModified == 0L) {
            this.firstModified = ((StructuredEdit)this.editToBeRedone()).editedTime();
        }
        boolean undoable = this.canUndo();
        boolean changed = this.hasChanged();
        super.redoTo(edit);
        this.firePropertyChangeEvent("Undo", undoable, this.canUndo());
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl) {
        this.propChangeSupport.removePropertyChangeListener(pcl);
    }

    public void reset() {
        boolean changed;
        boolean bl = changed = this.modificationMarker != this.editToBeUndone();
        if (changed) {
            this.modificationMarker = this.editToBeUndone();
        }
    }

    @Override
    protected void trimEdits(int from, int to) {
        boolean undoable = this.canUndo();
        boolean redoable = this.canRedo();
        boolean changed = this.hasChanged();
        super.trimEdits(from, to);
        this.firePropertyChangeEvent("Undo", undoable, this.canUndo());
        this.firePropertyChangeEvent("Redo", redoable, this.canRedo());
    }

    @Override
    public void undo() throws CannotUndoException {
        this.compoundEdit.end();
        UndoableEdit edit = this.editToBeUndone();
        if (((StructuredEdit)this.editToBeUndone()).editedTime() == this.firstModified) {
            this.firstModified = 0L;
        } else if (this.firstModified == 0L) {
            this.firstModified = ((StructuredEdit)this.editToBeUndone()).editedTime();
        }
        boolean redoable = this.canRedo();
        boolean changed = this.hasChanged();
        super.undo();
        this.firePropertyChangeEvent("Redo", redoable, this.canRedo());
    }

    @Override
    public void undoableEditHappened(UndoableEditEvent uee) {
        UndoableEdit edit = uee.getEdit();
        boolean undoable = this.canUndo();
        long editTime = System.currentTimeMillis();
        if (this.firstModified == 0L || editTime - this.compoundEdit.editedTime() > 700L) {
            this.compoundEdit.end();
            this.compoundEdit = new StructuredEdit();
        }
        this.compoundEdit.addEdit(edit);
        long l = this.firstModified = this.firstModified == 0L ? this.compoundEdit.editedTime() : this.firstModified;
        if (this.lastEdit() != this.compoundEdit) {
            boolean changed = this.hasChanged();
            this.addEdit(this.compoundEdit);
            this.firePropertyChangeEvent("Undo", undoable, this.canUndo());
        }
    }

    private static class StructuredEdit
    extends CompoundEdit {
        private long editedTime;

        private StructuredEdit() {
        }

        @Override
        public boolean addEdit(UndoableEdit edit) {
            boolean result = super.addEdit(edit);
            if (result && this.editedTime == 0L) {
                this.editedTime = System.currentTimeMillis();
            }
            return result;
        }

        @Override
        public boolean canUndo() {
            return !this.edits.isEmpty();
        }

        protected long editedTime() {
            return this.editedTime;
        }

        @Override
        public boolean isInProgress() {
            return false;
        }
    }
}

