/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import org.apache.poi.sl.draw.geom.AdjustHandle;
import org.apache.poi.sl.draw.geom.AdjustPoint;
import org.apache.poi.sl.draw.geom.AdjustValueIf;
import org.apache.poi.sl.draw.geom.ClosePathCommand;
import org.apache.poi.sl.draw.geom.ConnectionSiteIf;
import org.apache.poi.sl.draw.geom.GuideIf;
import org.apache.poi.sl.draw.geom.LineToCommand;
import org.apache.poi.sl.draw.geom.MoveToCommand;
import org.apache.poi.sl.draw.geom.Path;
import org.apache.poi.sl.draw.geom.PathIf;

public final class CustomGeometry
implements Iterable<PathIf> {
    final List<AdjustValueIf> adjusts = new ArrayList<AdjustValueIf>();
    final List<GuideIf> guides = new ArrayList<GuideIf>();
    final List<PathIf> paths = new ArrayList<PathIf>();
    final List<AdjustHandle> handles = new ArrayList<AdjustHandle>();
    final List<ConnectionSiteIf> connections = new ArrayList<ConnectionSiteIf>();
    Path textBounds;

    public void addAdjustGuide(AdjustValueIf guide) {
        this.adjusts.add(guide);
    }

    public void addGeomGuide(GuideIf guide) {
        this.guides.add(guide);
    }

    public void addAdjustHandle(AdjustHandle handle) {
        this.handles.add(handle);
    }

    public void addConnectionSite(ConnectionSiteIf connection) {
        this.connections.add(connection);
    }

    public void addPath(PathIf path) {
        this.paths.add(path);
    }

    public void setTextBounds(String left, String top, String right, String bottom) {
        this.textBounds = new Path();
        this.textBounds.addCommand(CustomGeometry.moveTo(left, top));
        this.textBounds.addCommand(CustomGeometry.lineTo(right, top));
        this.textBounds.addCommand(CustomGeometry.lineTo(right, bottom));
        this.textBounds.addCommand(CustomGeometry.lineTo(left, bottom));
        this.textBounds.addCommand(new ClosePathCommand());
    }

    private static MoveToCommand moveTo(String x, String y) {
        AdjustPoint pt = new AdjustPoint();
        pt.setX(x);
        pt.setY(y);
        MoveToCommand cmd = new MoveToCommand();
        cmd.setPt(pt);
        return cmd;
    }

    private static LineToCommand lineTo(String x, String y) {
        AdjustPoint pt = new AdjustPoint();
        pt.setX(x);
        pt.setY(y);
        LineToCommand cmd = new LineToCommand();
        cmd.setPt(pt);
        return cmd;
    }

    @Override
    public Iterator<PathIf> iterator() {
        return this.paths.iterator();
    }

    @Override
    public Spliterator<PathIf> spliterator() {
        return this.paths.spliterator();
    }

    public Path getTextBounds() {
        return this.textBounds;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomGeometry)) {
            return false;
        }
        CustomGeometry that = (CustomGeometry)o;
        return Objects.equals(this.adjusts, that.adjusts) && Objects.equals(this.guides, that.guides) && Objects.equals(this.handles, that.handles) && Objects.equals(this.connections, that.connections) && Objects.equals(this.textBounds, that.textBounds) && Objects.equals(this.paths, that.paths);
    }

    public int hashCode() {
        return Objects.hash(this.adjusts, this.guides, this.handles, this.connections, this.textBounds, this.paths);
    }
}

