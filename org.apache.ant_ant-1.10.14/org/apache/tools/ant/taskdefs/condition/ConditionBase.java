/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs.condition;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import org.apache.tools.ant.ProjectComponent;
import org.apache.tools.ant.taskdefs.Available;
import org.apache.tools.ant.taskdefs.Checksum;
import org.apache.tools.ant.taskdefs.UpToDate;
import org.apache.tools.ant.taskdefs.condition.And;
import org.apache.tools.ant.taskdefs.condition.Condition;
import org.apache.tools.ant.taskdefs.condition.Contains;
import org.apache.tools.ant.taskdefs.condition.Equals;
import org.apache.tools.ant.taskdefs.condition.FilesMatch;
import org.apache.tools.ant.taskdefs.condition.Http;
import org.apache.tools.ant.taskdefs.condition.IsFalse;
import org.apache.tools.ant.taskdefs.condition.IsFileSelected;
import org.apache.tools.ant.taskdefs.condition.IsReference;
import org.apache.tools.ant.taskdefs.condition.IsSet;
import org.apache.tools.ant.taskdefs.condition.IsTrue;
import org.apache.tools.ant.taskdefs.condition.Not;
import org.apache.tools.ant.taskdefs.condition.Or;
import org.apache.tools.ant.taskdefs.condition.Os;
import org.apache.tools.ant.taskdefs.condition.Socket;

public abstract class ConditionBase
extends ProjectComponent {
    private String taskName = "condition";
    private List<Condition> conditions = new Vector<Condition>();

    protected ConditionBase() {
        this.taskName = "component";
    }

    protected ConditionBase(String taskName) {
        this.taskName = taskName;
    }

    protected int countConditions() {
        return this.conditions.size();
    }

    protected final Enumeration<Condition> getConditions() {
        return Collections.enumeration(this.conditions);
    }

    public void setTaskName(String name) {
        this.taskName = name;
    }

    public String getTaskName() {
        return this.taskName;
    }

    public void addAvailable(Available a) {
        this.conditions.add(a);
    }

    public void addChecksum(Checksum c) {
        this.conditions.add(c);
    }

    public void addUptodate(UpToDate u) {
        this.conditions.add(u);
    }

    public void addNot(Not n) {
        this.conditions.add(n);
    }

    public void addAnd(And a) {
        this.conditions.add(a);
    }

    public void addOr(Or o) {
        this.conditions.add(o);
    }

    public void addEquals(Equals e) {
        this.conditions.add(e);
    }

    public void addOs(Os o) {
        this.conditions.add(o);
    }

    public void addIsSet(IsSet i) {
        this.conditions.add(i);
    }

    public void addHttp(Http h) {
        this.conditions.add(h);
    }

    public void addSocket(Socket s) {
        this.conditions.add(s);
    }

    public void addFilesMatch(FilesMatch test) {
        this.conditions.add(test);
    }

    public void addContains(Contains test) {
        this.conditions.add(test);
    }

    public void addIsTrue(IsTrue test) {
        this.conditions.add(test);
    }

    public void addIsFalse(IsFalse test) {
        this.conditions.add(test);
    }

    public void addIsReference(IsReference i) {
        this.conditions.add(i);
    }

    public void addIsFileSelected(IsFileSelected test) {
        this.conditions.add(test);
    }

    public void add(Condition c) {
        this.conditions.add(c);
    }
}

