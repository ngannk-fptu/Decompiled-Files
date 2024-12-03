/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.Geometry;
import com.microsoft.sqlserver.jdbc.JDBCType;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import com.microsoft.sqlserver.jdbc.spatialdatatypes.Figure;
import com.microsoft.sqlserver.jdbc.spatialdatatypes.Point;
import com.microsoft.sqlserver.jdbc.spatialdatatypes.Segment;
import com.microsoft.sqlserver.jdbc.spatialdatatypes.Shape;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

abstract class SQLServerSpatialDatatype {
    ByteBuffer buffer;
    InternalSpatialDatatype internalType;
    String wkt;
    String wktNoZM;
    byte[] clr;
    byte[] clrNoZM;
    int srid;
    byte version = 1;
    int numberOfPoints;
    int numberOfFigures;
    int numberOfShapes;
    int numberOfSegments;
    StringBuffer wktSb;
    StringBuffer wktSbNoZM;
    int currentPointIndex = 0;
    int currentFigureIndex = 0;
    int currentSegmentIndex = 0;
    int currentShapeIndex = 0;
    int currentWKBPointIndex = 0;
    int currentWKBFigureIndex = 0;
    int currentWKBSegmentIndex = 0;
    int currentWKBShapeIndex = 0;
    double[] xValues;
    double[] yValues;
    double[] zValues;
    double[] mValues;
    Figure[] figures = new Figure[0];
    Shape[] shapes = new Shape[0];
    Segment[] segments = new Segment[0];
    byte[] wkb;
    byte endian = 1;
    int wkbType;
    private static final int WKB_POINT_SIZE = 16;
    private static final int BYTE_ORDER_SIZE = 1;
    private static final int INTERNAL_TYPE_SIZE = 4;
    private static final int NUMBER_OF_SHAPES_SIZE = 4;
    private static final int LINEAR_RING_HEADER_SIZE = 4;
    private static final int WKB_POINT_HEADER_SIZE = 5;
    private static final int WKB_HEADER_SIZE = 9;
    private static final int WKB_FULLGLOBE_CODE = 126;
    boolean hasZvalues = false;
    boolean hasMvalues = false;
    boolean isValid = true;
    boolean isSinglePoint = false;
    boolean isSingleLineSegment = false;
    boolean isLargerThanHemisphere = false;
    boolean isNull = true;
    static final byte FA_INTERIOR_RING = 0;
    static final byte FA_STROKE = 1;
    static final byte FA_EXTERIOR_RING = 2;
    static final byte FA_POINT = 0;
    static final byte FA_LINE = 1;
    static final byte FA_ARC = 2;
    static final byte FA_COMPOSITE_CURVE = 3;
    int currentWktPos = 0;
    List<Point> pointList = new ArrayList<Point>();
    List<Figure> figureList = new ArrayList<Figure>();
    List<Shape> shapeList = new ArrayList<Shape>();
    List<Segment> segmentList = new ArrayList<Segment>();
    byte serializationProperties = 0;
    private static final byte SEGMENT_LINE = 0;
    private static final byte SEGMENT_ARC = 1;
    private static final byte SEGMENT_FIRST_LINE = 2;
    private static final byte SEGMENT_FIRST_ARC = 3;
    private static final byte HAS_ZVALUES_MASK = 1;
    private static final byte HAS_MVALUES_MASK = 2;
    private static final byte IS_VALID_MASK = 4;
    private static final byte IS_SINGLE_POINT_MASK = 8;
    private static final byte IS_SINGLE_LINE_SEGMENT_MASK = 16;
    private static final byte IS_LARGER_THAN_HEMISPHERE_MASK = 32;
    private List<Integer> versionOneShapeIndexes = new ArrayList<Integer>();
    private static final String EMPTY_STR = "EMPTY";
    private static final String POINT_STR = "POINT";
    private static final String LINESTRING_STR = "LINESTRING";
    private static final String POLYGON_STR = "POLYGON";
    private static final String MULTIPOINT_STR = "MULTIPOINT";
    private static final String MULTILINESTRING_STR = "MULTILINESTRING";
    private static final String MULTIPOLYGON_STR = "MULTIPOLYGON";
    private static final String GEOMETRYCOLLECTION_STR = "GEOMETRYCOLLECTION";
    private static final String CIRCULARSTRING_STR = "CIRCULARSTRING";
    private static final String COMPOUNDCURVE_STR = "COMPOUNDCURVE";
    private static final String CURVEPOLYGON_STR = "CURVEPOLYGON";
    private static final String FULLGLOBE_STR = "FULLGLOBE";

    SQLServerSpatialDatatype() {
    }

    void serializeToClr(boolean excludeZMFromCLR, SQLServerSpatialDatatype type) {
        int i;
        ByteBuffer buf = ByteBuffer.allocate(this.determineClrCapacity(excludeZMFromCLR));
        this.createSerializationProperties();
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(this.srid);
        buf.put(this.version);
        if (excludeZMFromCLR) {
            byte serializationPropertiesNoZM = this.serializationProperties;
            if (this.hasZvalues) {
                serializationPropertiesNoZM = (byte)(serializationPropertiesNoZM - 1);
            }
            if (this.hasMvalues) {
                serializationPropertiesNoZM = (byte)(serializationPropertiesNoZM - 2);
            }
            buf.put(serializationPropertiesNoZM);
        } else {
            buf.put(this.serializationProperties);
        }
        if (!this.isSinglePoint && !this.isSingleLineSegment) {
            buf.putInt(this.numberOfPoints);
        }
        if (type instanceof Geometry) {
            for (i = 0; i < this.numberOfPoints; ++i) {
                buf.putDouble(this.xValues[i]);
                buf.putDouble(this.yValues[i]);
            }
        } else {
            for (i = 0; i < this.numberOfPoints; ++i) {
                buf.putDouble(this.yValues[i]);
                buf.putDouble(this.xValues[i]);
            }
        }
        if (!excludeZMFromCLR) {
            if (this.hasZvalues) {
                for (i = 0; i < this.numberOfPoints; ++i) {
                    buf.putDouble(this.zValues[i]);
                }
            }
            if (this.hasMvalues) {
                for (i = 0; i < this.numberOfPoints; ++i) {
                    buf.putDouble(this.mValues[i]);
                }
            }
        }
        if (this.isSinglePoint || this.isSingleLineSegment) {
            if (excludeZMFromCLR) {
                this.clrNoZM = buf.array();
            } else {
                this.clr = buf.array();
            }
            return;
        }
        buf.putInt(this.numberOfFigures);
        for (i = 0; i < this.numberOfFigures; ++i) {
            buf.put(this.figures[i].getFiguresAttribute());
            buf.putInt(this.figures[i].getPointOffset());
        }
        buf.putInt(this.numberOfShapes);
        for (i = 0; i < this.numberOfShapes; ++i) {
            buf.putInt(this.shapes[i].getParentOffset());
            buf.putInt(this.shapes[i].getFigureOffset());
            buf.put(this.shapes[i].getOpenGISType());
        }
        if (this.version == 2 && null != this.segments) {
            buf.putInt(this.numberOfSegments);
            for (i = 0; i < this.numberOfSegments; ++i) {
                buf.put(this.segments[i].getSegmentType());
            }
        }
        if (excludeZMFromCLR) {
            this.clrNoZM = buf.array();
        } else {
            this.clr = buf.array();
        }
    }

    void serializeToWkb(SQLServerSpatialDatatype type) {
        ByteBuffer buf = ByteBuffer.allocate(this.determineWkbCapacity());
        buf.order(ByteOrder.LITTLE_ENDIAN);
        switch (this.internalType) {
            case POINT: {
                this.addPointToBuffer(buf, this.numberOfPoints);
                break;
            }
            case LINESTRING: {
                this.addLineStringToBuffer(buf, this.numberOfPoints);
                break;
            }
            case POLYGON: {
                this.addPolygonToBuffer(buf, this.numberOfFigures);
                break;
            }
            case MULTIPOINT: {
                this.addMultiPointToBuffer(buf, this.numberOfFigures);
                break;
            }
            case MULTILINESTRING: {
                this.addMultiLineStringToBuffer(buf, this.numberOfFigures);
                break;
            }
            case MULTIPOLYGON: {
                this.addMultiPolygonToBuffer(buf, this.numberOfShapes - 1);
                break;
            }
            case GEOMETRYCOLLECTION: {
                this.addGeometryCollectionToBuffer(buf, this.calculateNumShapesInThisGeometryCollection());
                break;
            }
            case CIRCULARSTRING: {
                this.addCircularStringToBuffer(buf, this.numberOfPoints);
                break;
            }
            case COMPOUNDCURVE: {
                this.addCompoundCurveToBuffer(buf, this.calculateNumCurvesInThisFigure());
                break;
            }
            case CURVEPOLYGON: {
                this.addCurvePolygonToBuffer(buf, this.numberOfFigures);
                break;
            }
            case FULLGLOBE: {
                this.addFullGlobeToBuffer(buf);
                break;
            }
        }
        this.wkb = buf.array();
    }

    private void addPointToBuffer(ByteBuffer buf, int numberOfPoints) {
        buf.put(this.endian);
        if (numberOfPoints == 0) {
            buf.putInt(InternalSpatialDatatype.MULTIPOINT.getTypeCode());
            buf.putInt(numberOfPoints);
        } else {
            buf.putInt(InternalSpatialDatatype.POINT.getTypeCode());
            this.addCoordinateToBuffer(buf, numberOfPoints);
            ++this.currentWKBFigureIndex;
        }
    }

    private void addLineStringToBuffer(ByteBuffer buf, int numberOfPoints) {
        buf.put(this.endian);
        buf.putInt(InternalSpatialDatatype.LINESTRING.getTypeCode());
        buf.putInt(numberOfPoints);
        this.addCoordinateToBuffer(buf, numberOfPoints);
        if (numberOfPoints > 0) {
            ++this.currentWKBFigureIndex;
        }
    }

    private void addPolygonToBuffer(ByteBuffer buf, int numberOfFigures) {
        buf.put(this.endian);
        buf.putInt(InternalSpatialDatatype.POLYGON.getTypeCode());
        buf.putInt(numberOfFigures);
        this.addStructureToBuffer(buf, numberOfFigures, InternalSpatialDatatype.POLYGON);
    }

    private void addMultiPointToBuffer(ByteBuffer buf, int numberOfFigures) {
        buf.put(this.endian);
        buf.putInt(InternalSpatialDatatype.MULTIPOINT.getTypeCode());
        buf.putInt(numberOfFigures);
        this.addStructureToBuffer(buf, numberOfFigures, InternalSpatialDatatype.MULTIPOINT);
    }

    private void addMultiLineStringToBuffer(ByteBuffer buf, int numberOfFigures) {
        buf.put(this.endian);
        buf.putInt(InternalSpatialDatatype.MULTILINESTRING.getTypeCode());
        buf.putInt(numberOfFigures);
        this.addStructureToBuffer(buf, numberOfFigures, InternalSpatialDatatype.MULTILINESTRING);
    }

    private void addMultiPolygonToBuffer(ByteBuffer buf, int numberOfShapes) {
        buf.put(this.endian);
        buf.putInt(InternalSpatialDatatype.MULTIPOLYGON.getTypeCode());
        buf.putInt(numberOfShapes);
        ++this.currentWKBShapeIndex;
        this.addStructureToBuffer(buf, numberOfShapes, InternalSpatialDatatype.MULTIPOLYGON);
    }

    private void addCircularStringToBuffer(ByteBuffer buf, int numberOfPoints) {
        buf.put(this.endian);
        buf.putInt(InternalSpatialDatatype.CIRCULARSTRING.getTypeCode());
        buf.putInt(numberOfPoints);
        this.addCoordinateToBuffer(buf, numberOfPoints);
        if (numberOfPoints > 0) {
            ++this.currentWKBFigureIndex;
        }
    }

    private void addCompoundCurveToBuffer(ByteBuffer buf, int numberOfCurves) {
        buf.put(this.endian);
        buf.putInt(InternalSpatialDatatype.COMPOUNDCURVE.getTypeCode());
        buf.putInt(numberOfCurves);
        this.addStructureToBuffer(buf, numberOfCurves, InternalSpatialDatatype.COMPOUNDCURVE);
        if (numberOfCurves > 0) {
            ++this.currentWKBFigureIndex;
        }
    }

    private void addCurvePolygonToBuffer(ByteBuffer buf, int numberOfFigures) {
        buf.put(this.endian);
        buf.putInt(InternalSpatialDatatype.CURVEPOLYGON.getTypeCode());
        buf.putInt(numberOfFigures);
        block5: for (int i = 0; i < numberOfFigures; ++i) {
            switch (this.figures[this.currentWKBFigureIndex].getFiguresAttribute()) {
                case 1: {
                    this.addStructureToBuffer(buf, 1, InternalSpatialDatatype.LINESTRING);
                    continue block5;
                }
                case 2: {
                    this.addStructureToBuffer(buf, 1, InternalSpatialDatatype.CIRCULARSTRING);
                    continue block5;
                }
                case 3: {
                    int numCurvesInThisFigure = this.calculateNumCurvesInThisFigure();
                    buf.put(this.endian);
                    buf.putInt(InternalSpatialDatatype.COMPOUNDCURVE.getTypeCode());
                    buf.putInt(numCurvesInThisFigure);
                    this.addStructureToBuffer(buf, numCurvesInThisFigure, InternalSpatialDatatype.COMPOUNDCURVE);
                    ++this.currentWKBFigureIndex;
                    continue block5;
                }
            }
        }
    }

    private void addGeometryCollectionToBuffer(ByteBuffer buf, int numberOfRemainingGeometries) {
        buf.put(this.endian);
        buf.putInt(this.internalType.getTypeCode());
        buf.putInt(numberOfRemainingGeometries);
        ++this.currentWKBShapeIndex;
        while (numberOfRemainingGeometries > 0) {
            switch (InternalSpatialDatatype.valueOf(this.shapes[this.currentWKBShapeIndex].getOpenGISType())) {
                case POINT: {
                    if (this.shapes[this.currentWKBShapeIndex].getFigureOffset() == -1) {
                        this.addPointToBuffer(buf, 0);
                    } else {
                        this.addPointToBuffer(buf, this.calculateNumPointsInThisFigure());
                    }
                    ++this.currentWKBShapeIndex;
                    break;
                }
                case LINESTRING: {
                    if (this.shapes[this.currentWKBShapeIndex].getFigureOffset() == -1) {
                        this.addLineStringToBuffer(buf, 0);
                    } else {
                        this.addLineStringToBuffer(buf, this.calculateNumPointsInThisFigure());
                    }
                    ++this.currentWKBShapeIndex;
                    break;
                }
                case POLYGON: {
                    if (this.shapes[this.currentWKBShapeIndex].getFigureOffset() == -1) {
                        this.addPolygonToBuffer(buf, 0);
                    } else {
                        this.addPolygonToBuffer(buf, this.calculateNumFiguresInThisShape(false));
                    }
                    ++this.currentWKBShapeIndex;
                    break;
                }
                case MULTIPOINT: {
                    if (this.shapes[this.currentWKBShapeIndex].getFigureOffset() == -1) {
                        this.addMultiPointToBuffer(buf, 0);
                    } else {
                        this.addMultiPointToBuffer(buf, this.calculateNumFiguresInThisShape(true));
                    }
                    ++this.currentWKBShapeIndex;
                    break;
                }
                case MULTILINESTRING: {
                    if (this.shapes[this.currentWKBShapeIndex].getFigureOffset() == -1) {
                        this.addMultiLineStringToBuffer(buf, 0);
                    } else {
                        this.addMultiLineStringToBuffer(buf, this.calculateNumFiguresInThisShape(true));
                    }
                    ++this.currentWKBShapeIndex;
                    break;
                }
                case MULTIPOLYGON: {
                    if (this.shapes[this.currentWKBShapeIndex].getFigureOffset() == -1) {
                        this.addMultiPolygonToBuffer(buf, 0);
                        break;
                    }
                    this.addMultiPolygonToBuffer(buf, this.calculateNumShapesInThisMultiPolygon());
                    break;
                }
                case GEOMETRYCOLLECTION: {
                    if (this.shapes[this.currentWKBShapeIndex].getFigureOffset() == -1) {
                        this.addGeometryCollectionToBuffer(buf, 0);
                        break;
                    }
                    this.addGeometryCollectionToBuffer(buf, this.calculateNumShapesInThisGeometryCollection());
                    break;
                }
                case CIRCULARSTRING: {
                    if (this.shapes[this.currentWKBShapeIndex].getFigureOffset() == -1) {
                        this.addCircularStringToBuffer(buf, 0);
                    } else {
                        this.addCircularStringToBuffer(buf, this.calculateNumPointsInThisFigure());
                    }
                    ++this.currentWKBShapeIndex;
                    break;
                }
                case COMPOUNDCURVE: {
                    if (this.shapes[this.currentWKBShapeIndex].getFigureOffset() == -1) {
                        this.addCompoundCurveToBuffer(buf, 0);
                    } else {
                        this.addCompoundCurveToBuffer(buf, this.calculateNumCurvesInThisFigure());
                    }
                    ++this.currentWKBShapeIndex;
                    break;
                }
                case CURVEPOLYGON: {
                    if (this.shapes[this.currentWKBShapeIndex].getFigureOffset() == -1) {
                        this.addCurvePolygonToBuffer(buf, 0);
                    } else {
                        this.addCurvePolygonToBuffer(buf, this.calculateNumFiguresInThisShape(false));
                    }
                    ++this.currentWKBShapeIndex;
                    break;
                }
            }
            --numberOfRemainingGeometries;
        }
    }

    private void addFullGlobeToBuffer(ByteBuffer buf) {
        buf.put(this.endian);
        buf.putInt(126);
    }

    private void addCoordinateToBuffer(ByteBuffer buf, int numPoint) {
        while (numPoint > 0) {
            buf.putDouble(this.xValues[this.currentWKBPointIndex]);
            buf.putDouble(this.yValues[this.currentWKBPointIndex]);
            ++this.currentWKBPointIndex;
            --numPoint;
        }
    }

    private void addStructureToBuffer(ByteBuffer buf, int remainingStructureCount, InternalSpatialDatatype internalParentType) {
        int originalRemainingStructureCount = remainingStructureCount;
        while (remainingStructureCount > 0) {
            int numPointsInThisFigure = this.calculateNumPointsInThisFigure();
            switch (internalParentType) {
                case LINESTRING: {
                    buf.put(this.endian);
                    buf.putInt(InternalSpatialDatatype.LINESTRING.getTypeCode());
                    buf.putInt(numPointsInThisFigure);
                    this.addCoordinateToBuffer(buf, numPointsInThisFigure);
                    ++this.currentWKBFigureIndex;
                    break;
                }
                case POLYGON: {
                    buf.putInt(numPointsInThisFigure);
                    this.addCoordinateToBuffer(buf, numPointsInThisFigure);
                    ++this.currentWKBFigureIndex;
                    break;
                }
                case MULTIPOINT: {
                    buf.put(this.endian);
                    buf.putInt(InternalSpatialDatatype.POINT.getTypeCode());
                    this.addCoordinateToBuffer(buf, 1);
                    ++this.currentWKBFigureIndex;
                    ++this.currentWKBShapeIndex;
                    break;
                }
                case MULTILINESTRING: {
                    buf.put(this.endian);
                    buf.putInt(InternalSpatialDatatype.LINESTRING.getTypeCode());
                    buf.putInt(numPointsInThisFigure);
                    this.addCoordinateToBuffer(buf, numPointsInThisFigure);
                    ++this.currentWKBFigureIndex;
                    ++this.currentWKBShapeIndex;
                    break;
                }
                case MULTIPOLYGON: {
                    int numFiguresInThisShape = this.calculateNumFiguresInThisShape(false);
                    buf.put(this.endian);
                    buf.putInt(InternalSpatialDatatype.POLYGON.getTypeCode());
                    buf.putInt(numFiguresInThisShape);
                    this.addStructureToBuffer(buf, numFiguresInThisShape, InternalSpatialDatatype.POLYGON);
                    ++this.currentWKBShapeIndex;
                    break;
                }
                case CIRCULARSTRING: {
                    buf.put(this.endian);
                    buf.putInt(InternalSpatialDatatype.CIRCULARSTRING.getTypeCode());
                    buf.putInt(numPointsInThisFigure);
                    this.addCoordinateToBuffer(buf, numPointsInThisFigure);
                    ++this.currentWKBFigureIndex;
                    break;
                }
                case COMPOUNDCURVE: {
                    int numberOfPointsInStructure;
                    if (this.segments[this.currentWKBSegmentIndex].getSegmentType() == 3) {
                        numberOfPointsInStructure = 3;
                        ++this.currentWKBSegmentIndex;
                        while (this.currentWKBSegmentIndex < this.segments.length && this.segments[this.currentWKBSegmentIndex].getSegmentType() != 3 && this.segments[this.currentWKBSegmentIndex].getSegmentType() != 2) {
                            numberOfPointsInStructure += 2;
                            ++this.currentWKBSegmentIndex;
                        }
                        buf.put(this.endian);
                        buf.putInt(InternalSpatialDatatype.CIRCULARSTRING.getTypeCode());
                        buf.putInt(numberOfPointsInStructure);
                        if (originalRemainingStructureCount != remainingStructureCount) {
                            --this.currentWKBPointIndex;
                        }
                        this.addCoordinateToBuffer(buf, numberOfPointsInStructure);
                        break;
                    }
                    if (this.segments[this.currentWKBSegmentIndex].getSegmentType() != 2) break;
                    numberOfPointsInStructure = 2;
                    ++this.currentWKBSegmentIndex;
                    while (this.currentWKBSegmentIndex < this.segments.length && this.segments[this.currentWKBSegmentIndex].getSegmentType() != 3 && this.segments[this.currentWKBSegmentIndex].getSegmentType() != 2) {
                        ++numberOfPointsInStructure;
                        ++this.currentWKBSegmentIndex;
                    }
                    buf.put(this.endian);
                    buf.putInt(InternalSpatialDatatype.LINESTRING.getTypeCode());
                    buf.putInt(numberOfPointsInStructure);
                    if (originalRemainingStructureCount != remainingStructureCount) {
                        --this.currentWKBPointIndex;
                    }
                    this.addCoordinateToBuffer(buf, numberOfPointsInStructure);
                    break;
                }
            }
            --remainingStructureCount;
        }
    }

    private int calculateNumPointsInThisFigure() {
        if (this.figures.length == 0) {
            return 0;
        }
        return this.currentWKBFigureIndex == this.figures.length - 1 ? this.numberOfPoints - this.figures[this.currentWKBFigureIndex].getPointOffset() : this.figures[this.currentWKBFigureIndex + 1].getPointOffset() - this.figures[this.currentWKBFigureIndex].getPointOffset();
    }

    private int calculateNumCurvesInThisFigure() {
        int numPointsInThisFigure = this.calculateNumPointsInThisFigure();
        int numCurvesInThisFigure = 0;
        int tempCurrentWKBSegmentIndex = this.currentWKBSegmentIndex;
        boolean isFirstSegment = true;
        while (numPointsInThisFigure > 0) {
            switch (this.segments[tempCurrentWKBSegmentIndex].getSegmentType()) {
                case 0: {
                    --numPointsInThisFigure;
                    break;
                }
                case 1: {
                    numPointsInThisFigure -= 2;
                    break;
                }
                case 2: {
                    numPointsInThisFigure = isFirstSegment ? (numPointsInThisFigure -= 2) : --numPointsInThisFigure;
                    ++numCurvesInThisFigure;
                    break;
                }
                case 3: {
                    numPointsInThisFigure = isFirstSegment ? (numPointsInThisFigure -= 3) : (numPointsInThisFigure -= 2);
                    ++numCurvesInThisFigure;
                    break;
                }
            }
            isFirstSegment = false;
            ++tempCurrentWKBSegmentIndex;
        }
        return numCurvesInThisFigure;
    }

    private int calculateNumFiguresInThisShape(boolean containsInnerStructures) {
        if (this.shapes.length == 0) {
            return 0;
        }
        if (containsInnerStructures) {
            int nextNonInnerShapeIndex;
            for (nextNonInnerShapeIndex = this.currentWKBShapeIndex + 1; nextNonInnerShapeIndex < this.shapes.length && this.shapes[nextNonInnerShapeIndex].getParentOffset() == this.currentWKBShapeIndex; ++nextNonInnerShapeIndex) {
            }
            if (nextNonInnerShapeIndex == this.shapes.length) {
                return this.numberOfFigures - this.shapes[this.currentWKBShapeIndex].getFigureOffset();
            }
            int figureIndexEnd = -1;
            for (int localCurrentShapeIndex = nextNonInnerShapeIndex; figureIndexEnd == -1 && localCurrentShapeIndex < this.shapes.length - 1; ++localCurrentShapeIndex) {
                figureIndexEnd = this.shapes[localCurrentShapeIndex + 1].getFigureOffset();
            }
            if (figureIndexEnd == -1) {
                figureIndexEnd = this.numberOfFigures;
            }
            return figureIndexEnd - this.shapes[this.currentWKBShapeIndex].getFigureOffset();
        }
        if (this.currentWKBShapeIndex == this.shapes.length - 1) {
            return this.numberOfFigures - this.shapes[this.currentWKBShapeIndex].getFigureOffset();
        }
        int figureIndexEnd = -1;
        for (int localCurrentShapeIndex = this.currentWKBShapeIndex; figureIndexEnd == -1 && localCurrentShapeIndex < this.shapes.length - 1; ++localCurrentShapeIndex) {
            figureIndexEnd = this.shapes[localCurrentShapeIndex + 1].getFigureOffset();
        }
        if (figureIndexEnd == -1) {
            figureIndexEnd = this.numberOfFigures;
        }
        return figureIndexEnd - this.shapes[this.currentWKBShapeIndex].getFigureOffset();
    }

    private int calculateNumShapesInThisMultiPolygon() {
        int nextNonInnerShapeIndex;
        if (this.shapes.length == 0) {
            return 0;
        }
        for (nextNonInnerShapeIndex = this.currentWKBShapeIndex + 1; nextNonInnerShapeIndex < this.shapes.length && this.shapes[nextNonInnerShapeIndex].getParentOffset() == this.currentWKBShapeIndex; ++nextNonInnerShapeIndex) {
        }
        return nextNonInnerShapeIndex - this.currentWKBShapeIndex - 1;
    }

    private int calculateNumShapesInThisGeometryCollection() {
        int numberOfGeometries = 0;
        for (int i = 0; i < this.shapes.length; ++i) {
            if (this.shapes[i].getParentOffset() != this.currentWKBShapeIndex) continue;
            ++numberOfGeometries;
        }
        return numberOfGeometries;
    }

    void parseClr(SQLServerSpatialDatatype type) throws SQLServerException {
        this.srid = this.readInt();
        this.version = this.readByte();
        this.serializationProperties = this.readByte();
        this.interpretSerializationPropBytes();
        this.readNumberOfPoints();
        this.readPoints(type);
        if (this.hasZvalues) {
            this.readZvalues();
        }
        if (this.hasMvalues) {
            this.readMvalues();
        }
        if (!this.isSinglePoint && !this.isSingleLineSegment) {
            this.readNumberOfFigures();
            this.readFigures();
            this.readNumberOfShapes();
            this.readShapes();
        }
        this.determineInternalType();
        if (this.buffer.hasRemaining() && this.version == 2 && this.internalType.getTypeCode() != 8 && this.internalType.getTypeCode() != 11) {
            this.readNumberOfSegments();
            this.readSegments();
        }
    }

    void constructWKT(SQLServerSpatialDatatype sd, InternalSpatialDatatype isd, int pointIndexEnd, int figureIndexEnd, int segmentIndexEnd, int shapeIndexEnd) throws SQLServerException {
        if (this.numberOfPoints == 0) {
            if (isd.getTypeCode() == 11) {
                if (sd instanceof Geometry) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_illegalTypeForGeometry"));
                    throw new SQLServerException(form.format(new Object[]{FULLGLOBE_STR}), null, 0, null);
                }
                this.appendToWKTBuffers(FULLGLOBE_STR);
                return;
            }
            if (isd.getTypeCode() == 7 && this.currentShapeIndex != shapeIndexEnd - 1) {
                ++this.currentShapeIndex;
                this.appendToWKTBuffers(isd.getTypeName() + "(");
                this.constructWKT(this, InternalSpatialDatatype.valueOf(this.shapes[this.currentShapeIndex].getOpenGISType()), this.numberOfPoints, this.numberOfFigures, this.numberOfSegments, this.numberOfShapes);
                this.appendToWKTBuffers(")");
                return;
            }
            this.appendToWKTBuffers(isd.getTypeName() + " EMPTY");
            return;
        }
        if (figureIndexEnd == -1) {
            this.appendToWKTBuffers(isd.getTypeName() + " EMPTY");
            return;
        }
        this.appendToWKTBuffers(isd.getTypeName());
        this.appendToWKTBuffers("(");
        switch (isd) {
            case POINT: {
                this.constructPointWKT(this.currentPointIndex);
                break;
            }
            case LINESTRING: 
            case CIRCULARSTRING: {
                this.constructLineWKT(this.currentPointIndex, pointIndexEnd);
                break;
            }
            case POLYGON: {
                this.constructShapeWKT(this.currentFigureIndex, figureIndexEnd);
                break;
            }
            case MULTIPOINT: 
            case MULTILINESTRING: {
                this.constructMultiShapeWKT(this.currentShapeIndex, shapeIndexEnd);
                break;
            }
            case COMPOUNDCURVE: {
                this.constructCompoundcurveWKT(this.currentSegmentIndex, segmentIndexEnd, pointIndexEnd);
                break;
            }
            case MULTIPOLYGON: {
                this.constructMultipolygonWKT(this.currentShapeIndex, shapeIndexEnd);
                break;
            }
            case GEOMETRYCOLLECTION: {
                this.constructGeometryCollectionWKT(shapeIndexEnd);
                break;
            }
            case CURVEPOLYGON: {
                this.constructCurvepolygonWKT(this.currentFigureIndex, figureIndexEnd, this.currentSegmentIndex, segmentIndexEnd);
                break;
            }
            default: {
                this.throwIllegalWKTPosition();
            }
        }
        this.appendToWKTBuffers(")");
    }

    void parseWKTForSerialization(SQLServerSpatialDatatype sd, int startPos, int parentShapeIndex, boolean isGeoCollection) throws SQLServerException {
        while (this.hasMoreToken()) {
            if (startPos != 0) {
                if (this.wkt.charAt(this.currentWktPos) == ')') {
                    return;
                }
                if (this.wkt.charAt(this.currentWktPos) == ',') {
                    ++this.currentWktPos;
                }
            }
            String nextToken = this.getNextStringToken().toUpperCase(Locale.US);
            InternalSpatialDatatype isd = InternalSpatialDatatype.INVALID_TYPE;
            try {
                isd = InternalSpatialDatatype.valueOf(nextToken);
            }
            catch (Exception e) {
                this.throwIllegalWKTPosition();
            }
            byte fa = 0;
            if (this.version == 1 && (CIRCULARSTRING_STR.equals(nextToken) || COMPOUNDCURVE_STR.equals(nextToken) || CURVEPOLYGON_STR.equals(nextToken))) {
                this.version = (byte)2;
            }
            if (FULLGLOBE_STR.equals(nextToken)) {
                if (sd instanceof Geometry) {
                    MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_illegalTypeForGeometry"));
                    throw new SQLServerException(form.format(new Object[]{FULLGLOBE_STR}), null, 0, null);
                }
                if (startPos != 0) {
                    this.throwIllegalWKTPosition();
                }
                this.shapeList.add(new Shape(parentShapeIndex, -1, isd.getTypeCode()));
                this.isLargerThanHemisphere = true;
                this.version = (byte)2;
                break;
            }
            if (this.checkEmptyKeyword(parentShapeIndex, isd, false)) continue;
            this.readOpenBracket();
            switch (nextToken) {
                case "POINT": {
                    if (startPos == 0 && POINT_STR.equalsIgnoreCase(nextToken)) {
                        this.isSinglePoint = true;
                        this.internalType = InternalSpatialDatatype.POINT;
                    }
                    if (isGeoCollection) {
                        this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), isd.getTypeCode()));
                        this.figureList.add(new Figure(1, this.pointList.size()));
                    }
                    this.readPointWkt();
                    break;
                }
                case "LINESTRING": 
                case "CIRCULARSTRING": {
                    this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), isd.getTypeCode()));
                    fa = isd.getTypeCode() == InternalSpatialDatatype.LINESTRING.getTypeCode() ? (byte)1 : 2;
                    this.figureList.add(new Figure(fa, this.pointList.size()));
                    this.readLineWkt();
                    if (startPos != 0 || !LINESTRING_STR.equalsIgnoreCase(nextToken) || this.pointList.size() != 2) break;
                    this.isSingleLineSegment = true;
                    break;
                }
                case "POLYGON": 
                case "MULTIPOINT": 
                case "MULTILINESTRING": {
                    int thisShapeIndex = this.shapeList.size();
                    this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), isd.getTypeCode()));
                    this.readShapeWkt(thisShapeIndex, nextToken);
                    break;
                }
                case "MULTIPOLYGON": {
                    int thisShapeIndex = this.shapeList.size();
                    this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), isd.getTypeCode()));
                    this.readMultiPolygonWkt(thisShapeIndex, nextToken);
                    break;
                }
                case "COMPOUNDCURVE": {
                    this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), isd.getTypeCode()));
                    this.figureList.add(new Figure(3, this.pointList.size()));
                    this.readCompoundCurveWkt(true);
                    break;
                }
                case "CURVEPOLYGON": {
                    this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), isd.getTypeCode()));
                    this.readCurvePolygon();
                    break;
                }
                case "GEOMETRYCOLLECTION": {
                    int thisShapeIndex = this.shapeList.size();
                    this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), isd.getTypeCode()));
                    this.parseWKTForSerialization(this, this.currentWktPos, thisShapeIndex, true);
                    break;
                }
                default: {
                    this.throwIllegalWKTPosition();
                }
            }
            this.readCloseBracket();
        }
        this.populateStructures();
    }

    void constructPointWKT(int pointIndex) {
        if (this.xValues[pointIndex] % 1.0 == 0.0) {
            this.appendToWKTBuffers((int)this.xValues[pointIndex]);
        } else {
            this.appendToWKTBuffers(this.xValues[pointIndex]);
        }
        this.appendToWKTBuffers(" ");
        if (this.yValues[pointIndex] % 1.0 == 0.0) {
            this.appendToWKTBuffers((int)this.yValues[pointIndex]);
        } else {
            this.appendToWKTBuffers(this.yValues[pointIndex]);
        }
        this.appendToWKTBuffers(" ");
        if (this.hasZvalues && !Double.isNaN(this.zValues[pointIndex])) {
            if (this.zValues[pointIndex] % 1.0 == 0.0) {
                this.wktSb.append((long)this.zValues[pointIndex]);
            } else {
                this.wktSb.append(this.zValues[pointIndex]);
            }
            this.wktSb.append(" ");
        } else if (this.hasMvalues && !Double.isNaN(this.mValues[pointIndex])) {
            this.wktSb.append("NULL ");
        }
        if (this.hasMvalues && !Double.isNaN(this.mValues[pointIndex])) {
            if (this.mValues[pointIndex] % 1.0 == 0.0) {
                this.wktSb.append((long)this.mValues[pointIndex]);
            } else {
                this.wktSb.append(this.mValues[pointIndex]);
            }
            this.wktSb.append(" ");
        }
        ++this.currentPointIndex;
        this.wktSb.setLength(this.wktSb.length() - 1);
        this.wktSbNoZM.setLength(this.wktSbNoZM.length() - 1);
    }

    void constructLineWKT(int pointStartIndex, int pointEndIndex) {
        for (int i = pointStartIndex; i < pointEndIndex; ++i) {
            this.constructPointWKT(i);
            if (i == pointEndIndex - 1) continue;
            this.appendToWKTBuffers(", ");
        }
    }

    void constructShapeWKT(int figureStartIndex, int figureEndIndex) {
        for (int i = figureStartIndex; i < figureEndIndex; ++i) {
            this.appendToWKTBuffers("(");
            if (i != this.numberOfFigures - 1) {
                this.constructLineWKT(this.figures[i].getPointOffset(), this.figures[i + 1].getPointOffset());
            } else {
                this.constructLineWKT(this.figures[i].getPointOffset(), this.numberOfPoints);
            }
            if (i != figureEndIndex - 1) {
                this.appendToWKTBuffers("), ");
                continue;
            }
            this.appendToWKTBuffers(")");
        }
    }

    void constructMultiShapeWKT(int shapeStartIndex, int shapeEndIndex) {
        for (int i = shapeStartIndex + 1; i < shapeEndIndex; ++i) {
            if (this.shapes[i].getFigureOffset() == -1) {
                this.appendToWKTBuffers(EMPTY_STR);
            } else {
                this.constructShapeWKT(this.shapes[i].getFigureOffset(), this.shapes[i].getFigureOffset() + 1);
            }
            if (i == shapeEndIndex - 1) continue;
            this.appendToWKTBuffers(", ");
        }
    }

    void constructCompoundcurveWKT(int segmentStartIndex, int segmentEndIndex, int pointEndIndex) {
        block4: for (int i = segmentStartIndex; i < segmentEndIndex; ++i) {
            byte segment = this.segments[i].getSegmentType();
            this.constructSegmentWKT(i, segment, pointEndIndex);
            if (i == segmentEndIndex - 1) {
                this.appendToWKTBuffers(")");
                break;
            }
            switch (segment) {
                case 0: 
                case 2: {
                    if (this.segments[i + 1].getSegmentType() == 0) continue block4;
                    this.appendToWKTBuffers("), ");
                    continue block4;
                }
                case 1: 
                case 3: {
                    if (this.segments[i + 1].getSegmentType() == 1) continue block4;
                    this.appendToWKTBuffers("), ");
                    continue block4;
                }
                default: {
                    return;
                }
            }
        }
    }

    void constructMultipolygonWKT(int shapeStartIndex, int shapeEndIndex) {
        for (int i = shapeStartIndex + 1; i < shapeEndIndex; ++i) {
            int figureEndIndex = this.figures.length;
            if (this.shapes[i].getFigureOffset() == -1) {
                this.appendToWKTBuffers(EMPTY_STR);
                if (i == shapeEndIndex - 1) continue;
                this.appendToWKTBuffers(", ");
                continue;
            }
            int figureStartIndex = this.shapes[i].getFigureOffset();
            if (i == this.shapes.length - 1) {
                figureEndIndex = this.figures.length;
            } else {
                for (int tempCurrentShapeIndex = i + 1; tempCurrentShapeIndex < this.shapes.length; ++tempCurrentShapeIndex) {
                    if (this.shapes[tempCurrentShapeIndex].getFigureOffset() == -1) {
                        continue;
                    }
                    figureEndIndex = this.shapes[tempCurrentShapeIndex].getFigureOffset();
                    break;
                }
            }
            this.appendToWKTBuffers("(");
            for (int j = figureStartIndex; j < figureEndIndex; ++j) {
                this.appendToWKTBuffers("(");
                if (j == this.figures.length - 1) {
                    this.constructLineWKT(this.figures[j].getPointOffset(), this.numberOfPoints);
                } else {
                    this.constructLineWKT(this.figures[j].getPointOffset(), this.figures[j + 1].getPointOffset());
                }
                if (j == figureEndIndex - 1) {
                    this.appendToWKTBuffers(")");
                    continue;
                }
                this.appendToWKTBuffers("), ");
            }
            this.appendToWKTBuffers(")");
            if (i == shapeEndIndex - 1) continue;
            this.appendToWKTBuffers(", ");
        }
    }

    void constructCurvepolygonWKT(int figureStartIndex, int figureEndIndex, int segmentStartIndex, int segmentEndIndex) {
        for (int i = figureStartIndex; i < figureEndIndex; ++i) {
            switch (this.figures[i].getFiguresAttribute()) {
                case 1: {
                    this.appendToWKTBuffers("(");
                    if (i == this.figures.length - 1) {
                        this.constructLineWKT(this.currentPointIndex, this.numberOfPoints);
                    } else {
                        this.constructLineWKT(this.currentPointIndex, this.figures[i + 1].getPointOffset());
                    }
                    this.appendToWKTBuffers(")");
                    break;
                }
                case 2: {
                    this.appendToWKTBuffers("CIRCULARSTRING(");
                    if (i == this.figures.length - 1) {
                        this.constructLineWKT(this.currentPointIndex, this.numberOfPoints);
                    } else {
                        this.constructLineWKT(this.currentPointIndex, this.figures[i + 1].getPointOffset());
                    }
                    this.appendToWKTBuffers(")");
                    break;
                }
                case 3: {
                    this.appendToWKTBuffers("COMPOUNDCURVE(");
                    int pointEndIndex = 0;
                    pointEndIndex = i == this.figures.length - 1 ? this.numberOfPoints : this.figures[i + 1].getPointOffset();
                    while (this.currentPointIndex < pointEndIndex) {
                        byte segment = this.segments[segmentStartIndex].getSegmentType();
                        this.constructSegmentWKT(segmentStartIndex, segment, pointEndIndex);
                        if (this.currentPointIndex >= pointEndIndex) {
                            this.appendToWKTBuffers("))");
                        } else {
                            switch (segment) {
                                case 0: 
                                case 2: {
                                    if (this.segments[segmentStartIndex + 1].getSegmentType() == 0) break;
                                    this.appendToWKTBuffers("), ");
                                    break;
                                }
                                case 1: 
                                case 3: {
                                    if (this.segments[segmentStartIndex + 1].getSegmentType() == 1) break;
                                    this.appendToWKTBuffers("), ");
                                    break;
                                }
                                default: {
                                    return;
                                }
                            }
                        }
                        ++segmentStartIndex;
                    }
                    break;
                }
                default: {
                    return;
                }
            }
            if (i == figureEndIndex - 1) continue;
            this.appendToWKTBuffers(", ");
        }
    }

    void constructSegmentWKT(int currentSegment, byte segment, int pointEndIndex) {
        switch (segment) {
            case 0: {
                this.appendToWKTBuffers(", ");
                this.constructLineWKT(this.currentPointIndex, this.currentPointIndex + 1);
                if (currentSegment == this.segments.length - 1 || this.segments[currentSegment + 1].getSegmentType() == 0) break;
                --this.currentPointIndex;
                this.incrementPointNumStartIfPointNotReused(pointEndIndex);
                break;
            }
            case 1: {
                this.appendToWKTBuffers(", ");
                this.constructLineWKT(this.currentPointIndex, this.currentPointIndex + 2);
                if (currentSegment == this.segments.length - 1 || this.segments[currentSegment + 1].getSegmentType() == 1) break;
                --this.currentPointIndex;
                this.incrementPointNumStartIfPointNotReused(pointEndIndex);
                break;
            }
            case 2: {
                this.appendToWKTBuffers("(");
                this.constructLineWKT(this.currentPointIndex, this.currentPointIndex + 2);
                if (currentSegment == this.segments.length - 1 || this.segments[currentSegment + 1].getSegmentType() == 0) break;
                --this.currentPointIndex;
                this.incrementPointNumStartIfPointNotReused(pointEndIndex);
                break;
            }
            case 3: {
                this.appendToWKTBuffers("CIRCULARSTRING(");
                this.constructLineWKT(this.currentPointIndex, this.currentPointIndex + 3);
                if (currentSegment == this.segments.length - 1 || this.segments[currentSegment + 1].getSegmentType() == 1) break;
                --this.currentPointIndex;
                this.incrementPointNumStartIfPointNotReused(pointEndIndex);
                break;
            }
            default: {
                return;
            }
        }
    }

    void constructGeometryCollectionWKT(int shapeEndIndex) throws SQLServerException {
        ++this.currentShapeIndex;
        this.constructGeometryCollectionWKThelper(shapeEndIndex);
    }

    void readPointWkt() throws SQLServerException {
        int numOfCoordinates = 0;
        double[] coords = new double[4];
        for (int i = 0; i < coords.length; ++i) {
            coords[i] = Double.NaN;
        }
        while (numOfCoordinates < 4) {
            double sign = 1.0;
            if (this.wkt.charAt(this.currentWktPos) == '-') {
                sign = -1.0;
            }
            int startPos = ++this.currentWktPos;
            if (this.wkt.charAt(this.currentWktPos) == ')') break;
            while (this.currentWktPos < this.wkt.length() && (Character.isDigit(this.wkt.charAt(this.currentWktPos)) || this.wkt.charAt(this.currentWktPos) == '.' || this.wkt.charAt(this.currentWktPos) == 'E' || this.wkt.charAt(this.currentWktPos) == 'e')) {
                ++this.currentWktPos;
            }
            try {
                coords[numOfCoordinates] = sign * new BigDecimal(this.wkt.substring(startPos, this.currentWktPos)).doubleValue();
                if (numOfCoordinates == 2) {
                    this.hasZvalues = true;
                } else if (numOfCoordinates == 3) {
                    this.hasMvalues = true;
                }
            }
            catch (Exception e) {
                if (this.wkt.length() > this.currentWktPos + 3 && "null".equalsIgnoreCase(this.wkt.substring(this.currentWktPos, this.currentWktPos + 4))) {
                    coords[numOfCoordinates] = Double.NaN;
                    this.currentWktPos += 4;
                }
                this.throwIllegalWKTPosition();
            }
            this.skipWhiteSpaces();
            if (++numOfCoordinates == 4 && this.checkSQLLength(this.currentWktPos + 1) && this.wkt.charAt(this.currentWktPos) != ',' && this.wkt.charAt(this.currentWktPos) != ')') {
                this.throwIllegalWKTPosition();
            }
            if (this.checkSQLLength(this.currentWktPos + 1) && this.wkt.charAt(this.currentWktPos) == ',') {
                if (numOfCoordinates == 1) {
                    this.throwIllegalWKTPosition();
                }
                ++this.currentWktPos;
                this.skipWhiteSpaces();
                break;
            }
            this.skipWhiteSpaces();
        }
        this.pointList.add(new Point(coords[0], coords[1], coords[2], coords[3]));
    }

    void readLineWkt() throws SQLServerException {
        while (this.currentWktPos < this.wkt.length() && this.wkt.charAt(this.currentWktPos) != ')') {
            this.readPointWkt();
        }
    }

    void readShapeWkt(int parentShapeIndex, String nextToken) throws SQLServerException {
        byte fa = 0;
        while (this.currentWktPos < this.wkt.length() && this.wkt.charAt(this.currentWktPos) != ')') {
            if (!POLYGON_STR.equals(nextToken) && this.checkEmptyKeyword(parentShapeIndex, InternalSpatialDatatype.valueOf(nextToken), true)) continue;
            if (MULTIPOINT_STR.equals(nextToken)) {
                this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), InternalSpatialDatatype.POINT.getTypeCode()));
            } else if (MULTILINESTRING_STR.equals(nextToken)) {
                this.shapeList.add(new Shape(parentShapeIndex, this.figureList.size(), InternalSpatialDatatype.LINESTRING.getTypeCode()));
            }
            if (this.version == 1) {
                if (MULTIPOINT_STR.equals(nextToken)) {
                    fa = 1;
                } else if (MULTILINESTRING_STR.equals(nextToken) || POLYGON_STR.equals(nextToken)) {
                    fa = 2;
                }
                this.versionOneShapeIndexes.add(this.figureList.size());
            } else if (this.version == 2 && (MULTIPOINT_STR.equals(nextToken) || MULTILINESTRING_STR.equals(nextToken) || POLYGON_STR.equals(nextToken) || MULTIPOLYGON_STR.equals(nextToken))) {
                fa = 1;
            }
            this.figureList.add(new Figure(fa, this.pointList.size()));
            this.readOpenBracket();
            this.readLineWkt();
            this.readCloseBracket();
            this.skipWhiteSpaces();
            if (this.checkSQLLength(this.currentWktPos + 1) && this.wkt.charAt(this.currentWktPos) == ',') {
                this.readComma();
                continue;
            }
            if (this.wkt.charAt(this.currentWktPos) == ')') continue;
            this.throwIllegalWKTPosition();
        }
    }

    void readCurvePolygon() throws SQLServerException {
        while (this.currentWktPos < this.wkt.length() && this.wkt.charAt(this.currentWktPos) != ')') {
            String nextPotentialToken = this.getNextStringToken().toUpperCase(Locale.US);
            if (CIRCULARSTRING_STR.equals(nextPotentialToken)) {
                this.figureList.add(new Figure(2, this.pointList.size()));
                this.readOpenBracket();
                this.readLineWkt();
                this.readCloseBracket();
            } else if (COMPOUNDCURVE_STR.equals(nextPotentialToken)) {
                this.figureList.add(new Figure(3, this.pointList.size()));
                this.readOpenBracket();
                this.readCompoundCurveWkt(true);
                this.readCloseBracket();
            } else if (this.wkt.charAt(this.currentWktPos) == '(') {
                this.figureList.add(new Figure(1, this.pointList.size()));
                this.readOpenBracket();
                this.readLineWkt();
                this.readCloseBracket();
            } else {
                this.throwIllegalWKTPosition();
            }
            if (this.checkSQLLength(this.currentWktPos + 1) && this.wkt.charAt(this.currentWktPos) == ',') {
                this.readComma();
                continue;
            }
            if (this.wkt.charAt(this.currentWktPos) == ')') continue;
            this.throwIllegalWKTPosition();
        }
    }

    void readMultiPolygonWkt(int thisShapeIndex, String nextToken) throws SQLServerException {
        while (this.currentWktPos < this.wkt.length() && this.wkt.charAt(this.currentWktPos) != ')') {
            if (this.checkEmptyKeyword(thisShapeIndex, InternalSpatialDatatype.valueOf(nextToken), true)) continue;
            this.shapeList.add(new Shape(thisShapeIndex, this.figureList.size(), InternalSpatialDatatype.POLYGON.getTypeCode()));
            this.readOpenBracket();
            this.readShapeWkt(thisShapeIndex, nextToken);
            this.readCloseBracket();
            if (this.checkSQLLength(this.currentWktPos + 1) && this.wkt.charAt(this.currentWktPos) == ',') {
                this.readComma();
                continue;
            }
            if (this.wkt.charAt(this.currentWktPos) == ')') continue;
            this.throwIllegalWKTPosition();
        }
    }

    void readSegmentWkt(int segmentType, boolean isFirstIteration) throws SQLServerException {
        this.segmentList.add(new Segment((byte)segmentType));
        int segmentLength = segmentType;
        if (segmentLength < 2) {
            ++segmentLength;
        }
        for (int i = 0; i < segmentLength; ++i) {
            if (i == 0 && !isFirstIteration && segmentType >= 2) {
                this.skipFirstPointWkt();
                continue;
            }
            this.readPointWkt();
        }
        if (this.currentWktPos < this.wkt.length() && this.wkt.charAt(this.currentWktPos) != ')') {
            if (segmentType == 3 || segmentType == 1) {
                this.readSegmentWkt(1, false);
            } else if (segmentType == 2 || segmentType == 0) {
                this.readSegmentWkt(0, false);
            }
        }
    }

    void readCompoundCurveWkt(boolean isFirstIteration) throws SQLServerException {
        while (this.currentWktPos < this.wkt.length() && this.wkt.charAt(this.currentWktPos) != ')') {
            String nextPotentialToken = this.getNextStringToken().toUpperCase(Locale.US);
            if (CIRCULARSTRING_STR.equals(nextPotentialToken)) {
                this.readOpenBracket();
                this.readSegmentWkt(3, isFirstIteration);
                this.readCloseBracket();
            } else if (this.wkt.charAt(this.currentWktPos) == '(') {
                this.readOpenBracket();
                this.readSegmentWkt(2, isFirstIteration);
                this.readCloseBracket();
            } else {
                this.throwIllegalWKTPosition();
            }
            isFirstIteration = false;
            if (this.checkSQLLength(this.currentWktPos + 1) && this.wkt.charAt(this.currentWktPos) == ',') {
                this.readComma();
                continue;
            }
            if (this.wkt.charAt(this.currentWktPos) == ')') continue;
            this.throwIllegalWKTPosition();
        }
    }

    String getNextStringToken() {
        int endIndex;
        this.skipWhiteSpaces();
        for (endIndex = this.currentWktPos; endIndex < this.wkt.length() && Character.isLetter(this.wkt.charAt(endIndex)); ++endIndex) {
        }
        int temp = this.currentWktPos;
        this.currentWktPos = endIndex;
        this.skipWhiteSpaces();
        return this.wkt.substring(temp, endIndex);
    }

    void populateStructures() {
        int i;
        if (!this.pointList.isEmpty()) {
            this.xValues = new double[this.pointList.size()];
            this.yValues = new double[this.pointList.size()];
            for (i = 0; i < this.pointList.size(); ++i) {
                this.xValues[i] = this.pointList.get(i).getX();
                this.yValues[i] = this.pointList.get(i).getY();
            }
            if (this.hasZvalues) {
                this.zValues = new double[this.pointList.size()];
                for (i = 0; i < this.pointList.size(); ++i) {
                    this.zValues[i] = this.pointList.get(i).getZ();
                }
            }
            if (this.hasMvalues) {
                this.mValues = new double[this.pointList.size()];
                for (i = 0; i < this.pointList.size(); ++i) {
                    this.mValues[i] = this.pointList.get(i).getM();
                }
            }
        }
        if (this.version == 2) {
            for (i = 0; i < this.versionOneShapeIndexes.size(); ++i) {
                this.figureList.get(this.versionOneShapeIndexes.get(i)).setFiguresAttribute((byte)1);
            }
        }
        if (!this.figureList.isEmpty()) {
            this.figures = new Figure[this.figureList.size()];
            for (i = 0; i < this.figureList.size(); ++i) {
                this.figures[i] = this.figureList.get(i);
            }
        }
        if (this.pointList.isEmpty() && !this.shapeList.isEmpty() && this.shapeList.get(0).getOpenGISType() == 7) {
            this.shapeList.get(0).setFigureOffset(-1);
        }
        if (!this.shapeList.isEmpty()) {
            this.shapes = new Shape[this.shapeList.size()];
            for (i = 0; i < this.shapeList.size(); ++i) {
                this.shapes[i] = this.shapeList.get(i);
            }
        }
        if (!this.segmentList.isEmpty()) {
            this.segments = new Segment[this.segmentList.size()];
            for (i = 0; i < this.segmentList.size(); ++i) {
                this.segments[i] = this.segmentList.get(i);
            }
        }
        this.numberOfPoints = this.pointList.size();
        this.numberOfFigures = this.figureList.size();
        this.numberOfShapes = this.shapeList.size();
        this.numberOfSegments = this.segmentList.size();
    }

    void readOpenBracket() throws SQLServerException {
        this.skipWhiteSpaces();
        if (this.wkt.charAt(this.currentWktPos) == '(') {
            ++this.currentWktPos;
            this.skipWhiteSpaces();
        } else {
            this.throwIllegalWKTPosition();
        }
    }

    void readCloseBracket() throws SQLServerException {
        this.skipWhiteSpaces();
        if (this.wkt.charAt(this.currentWktPos) == ')') {
            ++this.currentWktPos;
            this.skipWhiteSpaces();
        } else {
            this.throwIllegalWKTPosition();
        }
    }

    boolean hasMoreToken() {
        this.skipWhiteSpaces();
        return this.currentWktPos < this.wkt.length();
    }

    void createSerializationProperties() {
        this.serializationProperties = 0;
        if (this.hasZvalues) {
            this.serializationProperties = (byte)(this.serializationProperties + 1);
        }
        if (this.hasMvalues) {
            this.serializationProperties = (byte)(this.serializationProperties + 2);
        }
        if (this.isValid) {
            this.serializationProperties = (byte)(this.serializationProperties + 4);
        }
        if (this.isSinglePoint) {
            this.serializationProperties = (byte)(this.serializationProperties + 8);
        }
        if (this.isSingleLineSegment) {
            this.serializationProperties = (byte)(this.serializationProperties + 16);
        }
        if (this.version == 2 && this.isLargerThanHemisphere) {
            this.serializationProperties = (byte)(this.serializationProperties + 32);
        }
    }

    int determineClrCapacity(boolean excludeZMFromCLR) {
        int totalSize = 0;
        totalSize += 6;
        if (this.isSinglePoint || this.isSingleLineSegment) {
            totalSize += 16 * this.numberOfPoints;
            if (!excludeZMFromCLR) {
                if (this.hasZvalues) {
                    totalSize += 8 * this.numberOfPoints;
                }
                if (this.hasMvalues) {
                    totalSize += 8 * this.numberOfPoints;
                }
            }
            return totalSize;
        }
        int pointSize = 16;
        if (!excludeZMFromCLR) {
            if (this.hasZvalues) {
                pointSize += 8;
            }
            if (this.hasMvalues) {
                pointSize += 8;
            }
        }
        totalSize += 12;
        totalSize += this.numberOfPoints * pointSize;
        totalSize += this.numberOfFigures * 5;
        totalSize += this.numberOfShapes * 9;
        if (this.version == 2) {
            totalSize += 4;
            totalSize += this.numberOfSegments;
        }
        return totalSize;
    }

    int determineWkbCapacity() {
        int totalSize = 0;
        ++totalSize;
        totalSize += 4;
        switch (this.internalType) {
            case POINT: {
                if (this.numberOfPoints == 0) {
                    totalSize += 4;
                }
                totalSize += this.numberOfPoints * 16;
                break;
            }
            case LINESTRING: {
                totalSize += 4;
                totalSize += this.numberOfPoints * 16;
                break;
            }
            case POLYGON: {
                totalSize += 4;
                totalSize += this.figures.length * 4 + this.numberOfPoints * 16;
                break;
            }
            case MULTIPOINT: {
                totalSize += 4;
                totalSize += this.numberOfFigures * 5;
                totalSize += this.numberOfPoints * 16;
                break;
            }
            case MULTILINESTRING: {
                totalSize += 4;
                totalSize += this.numberOfFigures * 9;
                totalSize += this.numberOfPoints * 16;
                break;
            }
            case MULTIPOLYGON: {
                totalSize += 4;
                totalSize += (this.numberOfShapes - 1) * 9;
                for (int i = 1; i < this.shapes.length; ++i) {
                    if (i == this.shapes.length - 1) {
                        totalSize += 4 * (this.figures.length - this.shapes[i].getFigureOffset());
                        continue;
                    }
                    int nextFigureOffset = this.shapes[i + 1].getFigureOffset();
                    totalSize += 4 * (nextFigureOffset - this.shapes[i].getFigureOffset());
                }
                totalSize += this.numberOfPoints * 16;
                break;
            }
            case GEOMETRYCOLLECTION: {
                totalSize += 4;
                int actualNumberOfPoints = this.numberOfPoints;
                for (Segment s : this.segments) {
                    if (s.getSegmentType() != 3 && s.getSegmentType() != 2) continue;
                    totalSize += 9;
                    ++actualNumberOfPoints;
                }
                int numberOfCompositeCurves = 0;
                for (Figure f : this.figures) {
                    if (f.getFiguresAttribute() != 3) continue;
                    ++numberOfCompositeCurves;
                }
                if (numberOfCompositeCurves > 1) {
                    actualNumberOfPoints -= numberOfCompositeCurves - 1;
                }
                if (this.numberOfSegments > 0) {
                    --actualNumberOfPoints;
                }
                for (int i = 1; i < this.shapes.length; ++i) {
                    int localCurrentShapeIndex;
                    int figureIndexEnd;
                    if (this.shapes[i].getOpenGISType() == InternalSpatialDatatype.POINT.getTypeCode()) {
                        if (this.shapes[i].getFigureOffset() == -1) {
                            totalSize += 9;
                            continue;
                        }
                        totalSize += 5;
                        continue;
                    }
                    if (this.shapes[i].getOpenGISType() == InternalSpatialDatatype.POLYGON.getTypeCode()) {
                        if (this.shapes[i].getFigureOffset() != -1) {
                            if (i == this.shapes.length - 1) {
                                totalSize += 4 * (this.figures.length - this.shapes[i].getFigureOffset());
                            } else {
                                figureIndexEnd = -1;
                                for (localCurrentShapeIndex = i; figureIndexEnd == -1 && localCurrentShapeIndex < this.shapes.length - 1; ++localCurrentShapeIndex) {
                                    figureIndexEnd = this.shapes[localCurrentShapeIndex + 1].getFigureOffset();
                                }
                                if (figureIndexEnd == -1) {
                                    figureIndexEnd = this.numberOfFigures;
                                }
                                totalSize += 4 * (figureIndexEnd - this.shapes[i].getFigureOffset());
                            }
                        }
                        totalSize += 9;
                        continue;
                    }
                    if (this.shapes[i].getOpenGISType() == InternalSpatialDatatype.CURVEPOLYGON.getTypeCode()) {
                        if (this.shapes[i].getFigureOffset() != -1) {
                            if (i == this.shapes.length - 1) {
                                totalSize += 9 * (this.figures.length - this.shapes[i].getFigureOffset());
                            } else {
                                figureIndexEnd = -1;
                                for (localCurrentShapeIndex = i; figureIndexEnd == -1 && localCurrentShapeIndex < this.shapes.length - 1; ++localCurrentShapeIndex) {
                                    figureIndexEnd = this.shapes[localCurrentShapeIndex + 1].getFigureOffset();
                                }
                                if (figureIndexEnd == -1) {
                                    figureIndexEnd = this.numberOfFigures;
                                }
                                totalSize += 9 * (figureIndexEnd - this.shapes[i].getFigureOffset());
                            }
                        }
                        totalSize += 9;
                        continue;
                    }
                    totalSize += 9;
                }
                totalSize += actualNumberOfPoints * 16;
                break;
            }
            case CIRCULARSTRING: {
                totalSize += 4;
                totalSize += this.numberOfPoints * 16;
                break;
            }
            case COMPOUNDCURVE: {
                totalSize += 4;
                int actualNumberOfPoints = this.numberOfPoints;
                for (Segment s : this.segments) {
                    if (s.getSegmentType() != 3 && s.getSegmentType() != 2) continue;
                    totalSize += 9;
                    ++actualNumberOfPoints;
                }
                if (this.numberOfSegments > 0) {
                    --actualNumberOfPoints;
                }
                totalSize += actualNumberOfPoints * 16;
                break;
            }
            case CURVEPOLYGON: {
                totalSize += 4;
                int actualNumberOfPoints = this.numberOfPoints;
                for (Segment s : this.segments) {
                    if (s.getSegmentType() != 3 && s.getSegmentType() != 2) continue;
                    totalSize += 9;
                    ++actualNumberOfPoints;
                }
                int numberOfCompositeCurves = 0;
                for (Figure f : this.figures) {
                    totalSize += 9;
                    if (f.getFiguresAttribute() != 3) continue;
                    ++numberOfCompositeCurves;
                }
                if (numberOfCompositeCurves > 1) {
                    actualNumberOfPoints -= numberOfCompositeCurves - 1;
                }
                if (this.numberOfSegments > 0) {
                    --actualNumberOfPoints;
                }
                totalSize += actualNumberOfPoints * 16;
                break;
            }
            case FULLGLOBE: {
                totalSize = 5;
                break;
            }
        }
        return totalSize;
    }

    void appendToWKTBuffers(Object o) {
        this.wktSb.append(o);
        this.wktSbNoZM.append(o);
    }

    void interpretSerializationPropBytes() {
        this.hasZvalues = (this.serializationProperties & 1) != 0;
        this.hasMvalues = (this.serializationProperties & 2) != 0;
        this.isValid = (this.serializationProperties & 4) != 0;
        this.isSinglePoint = (this.serializationProperties & 8) != 0;
        this.isSingleLineSegment = (this.serializationProperties & 0x10) != 0;
        this.isLargerThanHemisphere = (this.serializationProperties & 0x20) != 0;
    }

    void readNumberOfPoints() throws SQLServerException {
        if (this.isSinglePoint) {
            this.numberOfPoints = 1;
        } else if (this.isSingleLineSegment) {
            this.numberOfPoints = 2;
        } else {
            this.numberOfPoints = this.readInt();
            this.checkNegSize(this.numberOfPoints);
        }
    }

    void readZvalues() throws SQLServerException {
        this.zValues = new double[this.numberOfPoints];
        for (int i = 0; i < this.numberOfPoints; ++i) {
            this.zValues[i] = this.readDouble();
        }
    }

    void readMvalues() throws SQLServerException {
        this.mValues = new double[this.numberOfPoints];
        for (int i = 0; i < this.numberOfPoints; ++i) {
            this.mValues[i] = this.readDouble();
        }
    }

    void readNumberOfFigures() throws SQLServerException {
        this.numberOfFigures = this.readInt();
        this.checkNegSize(this.numberOfFigures);
    }

    void readFigures() throws SQLServerException {
        this.figures = new Figure[this.numberOfFigures];
        for (int i = 0; i < this.numberOfFigures; ++i) {
            byte fa = this.readByte();
            int po = this.readInt();
            this.figures[i] = new Figure(fa, po);
        }
    }

    void readNumberOfShapes() throws SQLServerException {
        this.numberOfShapes = this.readInt();
        this.checkNegSize(this.numberOfShapes);
    }

    void readShapes() throws SQLServerException {
        this.shapes = new Shape[this.numberOfShapes];
        for (int i = 0; i < this.numberOfShapes; ++i) {
            int po = this.readInt();
            int fo = this.readInt();
            byte ogt = this.readByte();
            this.shapes[i] = new Shape(po, fo, ogt);
        }
    }

    void readNumberOfSegments() throws SQLServerException {
        this.numberOfSegments = this.readInt();
        this.checkNegSize(this.numberOfSegments);
    }

    void readSegments() throws SQLServerException {
        this.segments = new Segment[this.numberOfSegments];
        for (int i = 0; i < this.numberOfSegments; ++i) {
            byte st = this.readByte();
            this.segments[i] = new Segment(st);
        }
    }

    void determineInternalType() {
        this.internalType = this.isSinglePoint ? InternalSpatialDatatype.POINT : (this.isSingleLineSegment ? InternalSpatialDatatype.LINESTRING : InternalSpatialDatatype.valueOf(this.shapes[0].getOpenGISType()));
    }

    /*
     * Enabled aggressive block sorting
     */
    boolean checkEmptyKeyword(int parentShapeIndex, InternalSpatialDatatype isd, boolean isInsideAnotherShape) throws SQLServerException {
        String potentialEmptyKeyword;
        block8: {
            byte typeCode;
            block7: {
                block9: {
                    byte parentTypeCode;
                    block10: {
                        potentialEmptyKeyword = this.getNextStringToken().toUpperCase(Locale.US);
                        if (!EMPTY_STR.equals(potentialEmptyKeyword)) break block8;
                        typeCode = 0;
                        if (!isInsideAnotherShape) break block9;
                        parentTypeCode = isd.getTypeCode();
                        if (parentTypeCode != 4) break block10;
                        typeCode = InternalSpatialDatatype.POINT.getTypeCode();
                        break block7;
                    }
                    if (parentTypeCode == 5) {
                        typeCode = InternalSpatialDatatype.LINESTRING.getTypeCode();
                        break block7;
                    } else if (parentTypeCode == 6) {
                        typeCode = InternalSpatialDatatype.POLYGON.getTypeCode();
                        break block7;
                    } else {
                        if (parentTypeCode != 7) {
                            String strError = SQLServerException.getErrString("R_illegalWKT");
                            throw new SQLServerException(strError, null, 0, null);
                        }
                        typeCode = InternalSpatialDatatype.GEOMETRYCOLLECTION.getTypeCode();
                    }
                    break block7;
                }
                typeCode = isd.getTypeCode();
            }
            this.shapeList.add(new Shape(parentShapeIndex, -1, typeCode));
            this.skipWhiteSpaces();
            if (this.currentWktPos < this.wkt.length() && this.wkt.charAt(this.currentWktPos) == ',') {
                ++this.currentWktPos;
                this.skipWhiteSpaces();
            }
            return true;
        }
        if (!"".equals(potentialEmptyKeyword)) {
            this.throwIllegalWKTPosition();
        }
        return false;
    }

    void throwIllegalWKT() throws SQLServerException {
        String strError = SQLServerException.getErrString("R_illegalWKT");
        throw new SQLServerException(strError, null, 0, null);
    }

    void throwIllegalByteArray() throws SQLServerException {
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_ParsingError"));
        Object[] msgArgs = new Object[]{JDBCType.VARBINARY};
        throw new SQLServerException((Object)this, form.format(msgArgs), null, 0, false);
    }

    private void incrementPointNumStartIfPointNotReused(int pointEndIndex) {
        if (this.currentPointIndex + 1 >= pointEndIndex) {
            ++this.currentPointIndex;
        }
    }

    private void constructGeometryCollectionWKThelper(int shapeEndIndex) throws SQLServerException {
        block9: while (this.currentShapeIndex < shapeEndIndex) {
            InternalSpatialDatatype isd = InternalSpatialDatatype.valueOf(this.shapes[this.currentShapeIndex].getOpenGISType());
            int figureIndex = this.shapes[this.currentShapeIndex].getFigureOffset();
            int pointIndexEnd = this.numberOfPoints;
            int figureIndexEnd = this.numberOfFigures;
            int segmentIndexEnd = this.numberOfSegments;
            int shapeIndexEnd = this.numberOfShapes;
            int figureIndexIncrement = 0;
            int segmentIndexIncrement = 0;
            int shapeIndexIncrement = 0;
            int localCurrentSegmentIndex = 0;
            int localCurrentShapeIndex = 0;
            if (this.shapes[this.currentShapeIndex].getFigureOffset() == -1) {
                ++this.currentShapeIndex;
                figureIndexEnd = -1;
            } else {
                switch (isd) {
                    case POINT: {
                        ++figureIndexIncrement;
                        ++this.currentShapeIndex;
                        break;
                    }
                    case LINESTRING: 
                    case CIRCULARSTRING: {
                        ++figureIndexIncrement;
                        ++this.currentShapeIndex;
                        if (figureIndex + 1 < this.figures.length) {
                            pointIndexEnd = this.figures[figureIndex + 1].getPointOffset();
                            break;
                        }
                        pointIndexEnd = this.numberOfPoints;
                        break;
                    }
                    case POLYGON: 
                    case CURVEPOLYGON: {
                        figureIndexEnd = -1;
                        for (localCurrentShapeIndex = this.currentShapeIndex; figureIndexEnd == -1 && localCurrentShapeIndex < this.shapes.length - 1; ++localCurrentShapeIndex) {
                            figureIndexEnd = this.shapes[localCurrentShapeIndex + 1].getFigureOffset();
                        }
                        if (figureIndexEnd == -1) {
                            figureIndexEnd = this.numberOfFigures;
                        }
                        ++this.currentShapeIndex;
                        figureIndexIncrement = figureIndexEnd - this.currentFigureIndex;
                        localCurrentSegmentIndex = this.currentSegmentIndex;
                        if (isd.equals((Object)InternalSpatialDatatype.CURVEPOLYGON)) {
                            for (int i = this.currentFigureIndex; i < figureIndexEnd; ++i) {
                                if (this.figures[i].getFiguresAttribute() != 3) continue;
                                int pointOffsetEnd = i == this.figures.length - 1 ? this.numberOfPoints : this.figures[i + 1].getPointOffset();
                                int increment = this.calculateSegmentIncrement(localCurrentSegmentIndex, pointOffsetEnd - this.figures[i].getPointOffset());
                                segmentIndexIncrement += increment;
                                localCurrentSegmentIndex += increment;
                            }
                        }
                        segmentIndexEnd = localCurrentSegmentIndex;
                        break;
                    }
                    case MULTIPOINT: 
                    case MULTILINESTRING: 
                    case MULTIPOLYGON: {
                        int thisShapesParentOffset = this.shapes[this.currentShapeIndex].getParentOffset();
                        int tempShapeIndex = this.currentShapeIndex;
                        ++tempShapeIndex;
                        while (tempShapeIndex < this.shapes.length && this.shapes[tempShapeIndex].getParentOffset() != thisShapesParentOffset) {
                            if (tempShapeIndex != this.shapes.length - 1 && this.shapes[tempShapeIndex + 1].getFigureOffset() != -1) {
                                figureIndexEnd = this.shapes[tempShapeIndex + 1].getFigureOffset();
                            }
                            ++tempShapeIndex;
                        }
                        figureIndexIncrement = figureIndexEnd - this.currentFigureIndex;
                        shapeIndexIncrement = tempShapeIndex - this.currentShapeIndex;
                        shapeIndexEnd = tempShapeIndex;
                        break;
                    }
                    case GEOMETRYCOLLECTION: {
                        this.appendToWKTBuffers(isd.getTypeName());
                        if (this.shapes[this.currentShapeIndex].getFigureOffset() == -1) {
                            this.appendToWKTBuffers(" EMPTY");
                            ++this.currentShapeIndex;
                            if (this.currentShapeIndex >= shapeEndIndex) continue block9;
                            this.appendToWKTBuffers(", ");
                            continue block9;
                        }
                        this.appendToWKTBuffers("(");
                        int geometryCollectionParentIndex = this.shapes[this.currentShapeIndex].getParentOffset();
                        for (localCurrentShapeIndex = this.currentShapeIndex; localCurrentShapeIndex < this.shapes.length - 1 && this.shapes[localCurrentShapeIndex + 1].getParentOffset() > geometryCollectionParentIndex; ++localCurrentShapeIndex) {
                        }
                        ++this.currentShapeIndex;
                        this.constructGeometryCollectionWKThelper(++localCurrentShapeIndex);
                        if (this.currentShapeIndex < shapeEndIndex) {
                            this.appendToWKTBuffers("), ");
                            continue block9;
                        }
                        this.appendToWKTBuffers(")");
                        continue block9;
                    }
                    case COMPOUNDCURVE: {
                        int increment;
                        pointIndexEnd = this.currentFigureIndex == this.figures.length - 1 ? this.numberOfPoints : this.figures[this.currentFigureIndex + 1].getPointOffset();
                        segmentIndexIncrement = increment = this.calculateSegmentIncrement(this.currentSegmentIndex, pointIndexEnd - this.figures[this.currentFigureIndex].getPointOffset());
                        segmentIndexEnd = this.currentSegmentIndex + increment;
                        ++figureIndexIncrement;
                        ++this.currentShapeIndex;
                        break;
                    }
                    case FULLGLOBE: {
                        this.appendToWKTBuffers(FULLGLOBE_STR);
                        break;
                    }
                }
            }
            this.constructWKT(this, isd, pointIndexEnd, figureIndexEnd, segmentIndexEnd, shapeIndexEnd);
            this.currentFigureIndex += figureIndexIncrement;
            this.currentSegmentIndex += segmentIndexIncrement;
            this.currentShapeIndex += shapeIndexIncrement;
            if (this.currentShapeIndex >= shapeEndIndex) continue;
            this.appendToWKTBuffers(", ");
        }
    }

    private int calculateSegmentIncrement(int segmentStart, int pointDifference) {
        int segmentIncrement = 0;
        while (pointDifference > 0) {
            switch (this.segments[segmentStart].getSegmentType()) {
                case 0: {
                    if (segmentStart == this.segments.length - 1 || --pointDifference < 1 || this.segments[segmentStart + 1].getSegmentType() == 0) break;
                    ++pointDifference;
                    break;
                }
                case 1: {
                    if (segmentStart == this.segments.length - 1 || (pointDifference -= 2) < 1 || this.segments[segmentStart + 1].getSegmentType() == 1) break;
                    ++pointDifference;
                    break;
                }
                case 2: {
                    if (segmentStart == this.segments.length - 1 || (pointDifference -= 2) < 1 || this.segments[segmentStart + 1].getSegmentType() == 0) break;
                    ++pointDifference;
                    break;
                }
                case 3: {
                    if (segmentStart == this.segments.length - 1 || (pointDifference -= 3) < 1 || this.segments[segmentStart + 1].getSegmentType() == 1) break;
                    ++pointDifference;
                    break;
                }
                default: {
                    return segmentIncrement;
                }
            }
            ++segmentStart;
            ++segmentIncrement;
        }
        return segmentIncrement;
    }

    private void skipFirstPointWkt() {
        for (int numOfCoordinates = 0; numOfCoordinates < 4; ++numOfCoordinates) {
            if (this.wkt.charAt(this.currentWktPos) == '-') {
                ++this.currentWktPos;
            }
            if (this.wkt.charAt(this.currentWktPos) == ')') break;
            while (this.currentWktPos < this.wkt.length() && (Character.isDigit(this.wkt.charAt(this.currentWktPos)) || this.wkt.charAt(this.currentWktPos) == '.' || this.wkt.charAt(this.currentWktPos) == 'E' || this.wkt.charAt(this.currentWktPos) == 'e')) {
                ++this.currentWktPos;
            }
            this.skipWhiteSpaces();
            if (this.wkt.charAt(this.currentWktPos) == ',') {
                ++this.currentWktPos;
                this.skipWhiteSpaces();
                break;
            }
            this.skipWhiteSpaces();
        }
    }

    private void readComma() throws SQLServerException {
        this.skipWhiteSpaces();
        if (this.wkt.charAt(this.currentWktPos) == ',') {
            ++this.currentWktPos;
            this.skipWhiteSpaces();
        } else {
            this.throwIllegalWKTPosition();
        }
    }

    private void skipWhiteSpaces() {
        while (this.currentWktPos < this.wkt.length() && Character.isWhitespace(this.wkt.charAt(this.currentWktPos))) {
            ++this.currentWktPos;
        }
    }

    private void checkNegSize(int num) throws SQLServerException {
        if (num < 0) {
            this.throwIllegalByteArray();
        }
    }

    private void readPoints(SQLServerSpatialDatatype type) throws SQLServerException {
        this.xValues = new double[this.numberOfPoints];
        this.yValues = new double[this.numberOfPoints];
        if (type instanceof Geometry) {
            for (int i = 0; i < this.numberOfPoints; ++i) {
                this.xValues[i] = this.readDouble();
                this.yValues[i] = this.readDouble();
            }
        } else {
            for (int i = 0; i < this.numberOfPoints; ++i) {
                this.yValues[i] = this.readDouble();
                this.xValues[i] = this.readDouble();
            }
        }
    }

    private void checkBuffer(int i) throws SQLServerException {
        if (this.buffer.remaining() < i) {
            this.throwIllegalByteArray();
        }
    }

    private boolean checkSQLLength(int length) throws SQLServerException {
        if (null == this.wkt || this.wkt.length() < length) {
            this.throwIllegalWKTPosition();
        }
        return true;
    }

    private void throwIllegalWKTPosition() throws SQLServerException {
        MessageFormat form = new MessageFormat(SQLServerException.getErrString("R_illegalWKTposition"));
        throw new SQLServerException(form.format(new Object[]{this.currentWktPos}), null, 0, null);
    }

    byte readByte() throws SQLServerException {
        this.checkBuffer(1);
        return this.buffer.get();
    }

    int readInt() throws SQLServerException {
        this.checkBuffer(4);
        return this.buffer.getInt();
    }

    double readDouble() throws SQLServerException {
        this.checkBuffer(8);
        return this.buffer.getDouble();
    }

    public List<Point> getPointList() {
        return this.pointList;
    }

    public List<Figure> getFigureList() {
        return this.figureList;
    }

    public List<Shape> getShapeList() {
        return this.shapeList;
    }

    public List<Segment> getSegmentList() {
        return this.segmentList;
    }

    static enum InternalSpatialDatatype {
        POINT(1, "POINT"),
        LINESTRING(2, "LINESTRING"),
        POLYGON(3, "POLYGON"),
        MULTIPOINT(4, "MULTIPOINT"),
        MULTILINESTRING(5, "MULTILINESTRING"),
        MULTIPOLYGON(6, "MULTIPOLYGON"),
        GEOMETRYCOLLECTION(7, "GEOMETRYCOLLECTION"),
        CIRCULARSTRING(8, "CIRCULARSTRING"),
        COMPOUNDCURVE(9, "COMPOUNDCURVE"),
        CURVEPOLYGON(10, "CURVEPOLYGON"),
        FULLGLOBE(11, "FULLGLOBE"),
        INVALID_TYPE(0, null);

        private byte typeCode;
        private String typeName;
        private static final InternalSpatialDatatype[] VALUES;

        private InternalSpatialDatatype(byte typeCode, String typeName) {
            this.typeCode = typeCode;
            this.typeName = typeName;
        }

        byte getTypeCode() {
            return this.typeCode;
        }

        String getTypeName() {
            return this.typeName;
        }

        static InternalSpatialDatatype valueOf(byte typeCode) {
            for (InternalSpatialDatatype internalType : VALUES) {
                if (internalType.typeCode != typeCode) continue;
                return internalType;
            }
            return INVALID_TYPE;
        }

        static {
            VALUES = InternalSpatialDatatype.values();
        }
    }
}

