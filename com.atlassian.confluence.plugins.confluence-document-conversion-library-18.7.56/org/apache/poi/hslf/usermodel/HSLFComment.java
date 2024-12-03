/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.usermodel;

import java.awt.geom.Point2D;
import java.util.Date;
import org.apache.poi.hslf.record.Comment2000;
import org.apache.poi.sl.usermodel.Comment;
import org.apache.poi.util.Units;

public final class HSLFComment
implements Comment {
    private final Comment2000 _comment2000;

    public HSLFComment(Comment2000 comment2000) {
        this._comment2000 = comment2000;
    }

    protected Comment2000 getComment2000() {
        return this._comment2000;
    }

    @Override
    public String getAuthor() {
        return this._comment2000.getAuthor();
    }

    @Override
    public void setAuthor(String author) {
        this._comment2000.setAuthor(author);
    }

    @Override
    public String getAuthorInitials() {
        return this._comment2000.getAuthorInitials();
    }

    @Override
    public void setAuthorInitials(String initials) {
        this._comment2000.setAuthorInitials(initials);
    }

    @Override
    public String getText() {
        return this._comment2000.getText();
    }

    @Override
    public void setText(String text) {
        this._comment2000.setText(text);
    }

    @Override
    public Date getDate() {
        return this._comment2000.getComment2000Atom().getDate();
    }

    @Override
    public void setDate(Date date) {
        this._comment2000.getComment2000Atom().setDate(date);
    }

    @Override
    public Point2D getOffset() {
        double x = Units.masterToPoints(this._comment2000.getComment2000Atom().getXOffset());
        double y = Units.masterToPoints(this._comment2000.getComment2000Atom().getYOffset());
        return new Point2D.Double(x, y);
    }

    @Override
    public void setOffset(Point2D offset) {
        int x = Units.pointsToMaster(offset.getX());
        int y = Units.pointsToMaster(offset.getY());
        this._comment2000.getComment2000Atom().setXOffset(x);
        this._comment2000.getComment2000Atom().setYOffset(y);
    }
}

