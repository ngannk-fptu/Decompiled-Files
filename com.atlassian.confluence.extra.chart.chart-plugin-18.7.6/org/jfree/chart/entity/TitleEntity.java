/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.entity;

import java.awt.Shape;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jfree.chart.HashUtilities;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.title.Title;
import org.jfree.io.SerialUtilities;
import org.jfree.util.ObjectUtilities;

public class TitleEntity
extends ChartEntity {
    private static final long serialVersionUID = -4445994133561919083L;
    private Title title;

    public TitleEntity(Shape area, Title title) {
        this(area, title, null);
    }

    public TitleEntity(Shape area, Title title, String toolTipText) {
        this(area, title, toolTipText, null);
    }

    public TitleEntity(Shape area, Title title, String toolTipText, String urlText) {
        super(area, toolTipText, urlText);
        if (title == null) {
            throw new IllegalArgumentException("Null 'title' argument.");
        }
        this.title = title;
    }

    public Title getTitle() {
        return this.title;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer("TitleEntity: ");
        buf.append("tooltip = ");
        buf.append(this.getToolTipText());
        return buf.toString();
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof TitleEntity)) {
            return false;
        }
        TitleEntity that = (TitleEntity)obj;
        if (!this.getArea().equals(that.getArea())) {
            return false;
        }
        if (!ObjectUtilities.equal(this.getToolTipText(), that.getToolTipText())) {
            return false;
        }
        if (!ObjectUtilities.equal(this.getURLText(), that.getURLText())) {
            return false;
        }
        return this.title.equals(that.title);
    }

    public int hashCode() {
        int result = 41;
        result = HashUtilities.hashCode(result, this.getToolTipText());
        result = HashUtilities.hashCode(result, this.getURLText());
        return result;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
        SerialUtilities.writeShape(this.getArea(), stream);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
        this.setArea(SerialUtilities.readShape(stream));
    }
}

