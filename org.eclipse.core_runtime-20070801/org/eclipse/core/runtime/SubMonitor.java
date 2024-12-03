/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.core.runtime;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IProgressMonitorWithBlocking;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;

public final class SubMonitor
implements IProgressMonitorWithBlocking {
    private static final int MINIMUM_RESOLUTION = 1000;
    private int totalParent;
    private int usedForParent = 0;
    private double usedForChildren = 0.0;
    private int totalForChildren;
    private IProgressMonitor lastSubMonitor = null;
    private final RootInfo root;
    private final int flags;
    public static final int SUPPRESS_SUBTASK = 1;
    public static final int SUPPRESS_BEGINTASK = 2;
    public static final int SUPPRESS_SETTASKNAME = 4;
    public static final int SUPPRESS_ALL_LABELS = 7;
    public static final int SUPPRESS_NONE = 0;

    private SubMonitor(RootInfo rootInfo, int totalWork, int availableToChildren, int flags) {
        this.root = rootInfo;
        this.totalParent = totalWork > 0 ? totalWork : 0;
        this.totalForChildren = availableToChildren;
        this.flags = flags;
    }

    public static SubMonitor convert(IProgressMonitor monitor) {
        return SubMonitor.convert(monitor, "", 0);
    }

    public static SubMonitor convert(IProgressMonitor monitor, int work) {
        return SubMonitor.convert(monitor, "", work);
    }

    public static SubMonitor convert(IProgressMonitor monitor, String taskName, int work) {
        if (monitor == null) {
            monitor = new NullProgressMonitor();
        }
        if (monitor instanceof SubMonitor) {
            monitor.beginTask(taskName, work);
            return (SubMonitor)monitor;
        }
        monitor.beginTask(taskName, 1000);
        return new SubMonitor(new RootInfo(monitor), 1000, work, 0);
    }

    public SubMonitor setWorkRemaining(int workRemaining) {
        workRemaining = Math.max(0, workRemaining);
        if (this.totalForChildren > 0 && this.totalParent > this.usedForParent) {
            double remainForParent = (double)this.totalParent * (1.0 - this.usedForChildren / (double)this.totalForChildren);
            this.usedForChildren = (double)workRemaining * (1.0 - remainForParent / (double)(this.totalParent - this.usedForParent));
        } else {
            this.usedForChildren = 0.0;
        }
        this.totalParent -= this.usedForParent;
        this.usedForParent = 0;
        this.totalForChildren = workRemaining;
        return this;
    }

    private int consume(double ticks) {
        if (this.totalParent == 0 || this.totalForChildren == 0) {
            return 0;
        }
        this.usedForChildren += ticks;
        if (this.usedForChildren > (double)this.totalForChildren) {
            this.usedForChildren = this.totalForChildren;
        } else if (this.usedForChildren < 0.0) {
            this.usedForChildren = 0.0;
        }
        int parentPosition = (int)((double)this.totalParent * this.usedForChildren / (double)this.totalForChildren);
        int delta = parentPosition - this.usedForParent;
        this.usedForParent = parentPosition;
        return delta;
    }

    public boolean isCanceled() {
        return this.root.isCanceled();
    }

    public void setTaskName(String name) {
        if ((this.flags & 4) == 0) {
            this.root.setTaskName(name);
        }
    }

    public void beginTask(String name, int totalWork) {
        if ((this.flags & 2) == 0 && name != null) {
            this.root.setTaskName(name);
        }
        this.setWorkRemaining(totalWork);
    }

    public void done() {
        this.cleanupActiveChild();
        int delta = this.totalParent - this.usedForParent;
        if (delta > 0) {
            this.root.worked(delta);
        }
        this.totalParent = 0;
        this.usedForParent = 0;
        this.totalForChildren = 0;
        this.usedForChildren = 0.0;
    }

    public void internalWorked(double work) {
        int delta = this.consume(work > 0.0 ? work : 0.0);
        if (delta != 0) {
            this.root.worked(delta);
        }
    }

    public void subTask(String name) {
        if ((this.flags & 1) == 0) {
            this.root.subTask(name);
        }
    }

    public void worked(int work) {
        this.internalWorked(work);
    }

    public void setCanceled(boolean b) {
        this.root.setCanceled(b);
    }

    public SubMonitor newChild(int totalWork) {
        return this.newChild(totalWork, 2);
    }

    public SubMonitor newChild(int totalWork, int suppressFlags) {
        double totalWorkDouble = totalWork > 0 ? (double)totalWork : 0.0;
        totalWorkDouble = Math.min(totalWorkDouble, (double)this.totalForChildren - this.usedForChildren);
        this.cleanupActiveChild();
        int childFlags = 0;
        if ((this.flags & 4) != 0) {
            childFlags |= 6;
        }
        if ((this.flags & 1) != 0) {
            childFlags |= 1;
        }
        SubMonitor result = new SubMonitor(this.root, this.consume(totalWorkDouble), 0, childFlags |= suppressFlags);
        this.lastSubMonitor = result;
        return result;
    }

    private void cleanupActiveChild() {
        if (this.lastSubMonitor == null) {
            return;
        }
        IProgressMonitor child = this.lastSubMonitor;
        this.lastSubMonitor = null;
        child.done();
    }

    public void clearBlocked() {
        this.root.clearBlocked();
    }

    public void setBlocked(IStatus reason) {
        this.root.setBlocked(reason);
    }

    protected static boolean eq(Object o1, Object o2) {
        if (o1 == null) {
            return o2 == null;
        }
        if (o2 == null) {
            return false;
        }
        return o1.equals(o2);
    }

    private static final class RootInfo {
        private final IProgressMonitor root;
        private String taskName = null;
        private String subTask = null;

        public RootInfo(IProgressMonitor root) {
            this.root = root;
        }

        public boolean isCanceled() {
            return this.root.isCanceled();
        }

        public void setCanceled(boolean value) {
            this.root.setCanceled(value);
        }

        public void setTaskName(String taskName) {
            if (SubMonitor.eq(taskName, this.taskName)) {
                return;
            }
            this.taskName = taskName;
            this.root.setTaskName(taskName);
        }

        public void subTask(String name) {
            if (SubMonitor.eq(this.subTask, name)) {
                return;
            }
            this.subTask = name;
            this.root.subTask(name);
        }

        public void worked(int i) {
            this.root.worked(i);
        }

        public void clearBlocked() {
            if (this.root instanceof IProgressMonitorWithBlocking) {
                ((IProgressMonitorWithBlocking)this.root).clearBlocked();
            }
        }

        public void setBlocked(IStatus reason) {
            if (this.root instanceof IProgressMonitorWithBlocking) {
                ((IProgressMonitorWithBlocking)this.root).setBlocked(reason);
            }
        }
    }
}

