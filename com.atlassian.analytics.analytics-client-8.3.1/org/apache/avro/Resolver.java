/*
 * Decompiled with CFR 0.152.
 */
package org.apache.avro;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

public class Resolver {
    public static Action resolve(Schema writer, Schema reader, GenericData data) {
        return Resolver.resolve(Schema.applyAliases(writer, reader), reader, data, new HashMap<Schema.SeenPair, Action>());
    }

    public static Action resolve(Schema writer, Schema reader) {
        return Resolver.resolve(writer, reader, GenericData.get());
    }

    private static Action resolve(Schema w, Schema r, GenericData d, Map<Schema.SeenPair, Action> seen) {
        Schema.Type wType = w.getType();
        Schema.Type rType = r.getType();
        if (wType == Schema.Type.UNION) {
            return WriterUnion.resolve(w, r, d, seen);
        }
        if (wType == rType) {
            switch (wType) {
                case NULL: 
                case BOOLEAN: 
                case INT: 
                case LONG: 
                case FLOAT: 
                case DOUBLE: 
                case STRING: 
                case BYTES: {
                    return new DoNothing(w, r, d);
                }
                case FIXED: {
                    if (w.getName() != null && !w.getName().equals(r.getName())) {
                        return new ErrorAction(w, r, d, ErrorAction.ErrorType.NAMES_DONT_MATCH);
                    }
                    if (w.getFixedSize() != r.getFixedSize()) {
                        return new ErrorAction(w, r, d, ErrorAction.ErrorType.SIZES_DONT_MATCH);
                    }
                    return new DoNothing(w, r, d);
                }
                case ARRAY: {
                    Action et = Resolver.resolve(w.getElementType(), r.getElementType(), d, seen);
                    return new Container(w, r, d, et);
                }
                case MAP: {
                    Action vt = Resolver.resolve(w.getValueType(), r.getValueType(), d, seen);
                    return new Container(w, r, d, vt);
                }
                case ENUM: {
                    return EnumAdjust.resolve(w, r, d);
                }
                case RECORD: {
                    return RecordAdjust.resolve(w, r, d, seen);
                }
            }
            throw new IllegalArgumentException("Unknown type for schema: " + (Object)((Object)wType));
        }
        if (rType == Schema.Type.UNION) {
            return ReaderUnion.resolve(w, r, d, seen);
        }
        return Promote.resolve(w, r, d);
    }

    private static boolean unionEquiv(Schema write, Schema read, Map<Schema.SeenPair, Boolean> seen) {
        Schema.Type wt = write.getType();
        if (wt != read.getType()) {
            return false;
        }
        if (!(wt != Schema.Type.RECORD && wt != Schema.Type.FIXED && wt != Schema.Type.ENUM || write.getName() == null || write.getName().equals(read.getName()))) {
            return false;
        }
        switch (wt) {
            case NULL: 
            case BOOLEAN: 
            case INT: 
            case LONG: 
            case FLOAT: 
            case DOUBLE: 
            case STRING: 
            case BYTES: {
                return true;
            }
            case ARRAY: {
                return Resolver.unionEquiv(write.getElementType(), read.getElementType(), seen);
            }
            case MAP: {
                return Resolver.unionEquiv(write.getValueType(), read.getValueType(), seen);
            }
            case FIXED: {
                return write.getFixedSize() == read.getFixedSize();
            }
            case ENUM: {
                List<String> ws = write.getEnumSymbols();
                List<String> rs = read.getEnumSymbols();
                return ws.equals(rs);
            }
            case UNION: {
                List<Schema> wb = write.getTypes();
                List<Schema> rb = read.getTypes();
                if (wb.size() != rb.size()) {
                    return false;
                }
                for (int i = 0; i < wb.size(); ++i) {
                    if (Resolver.unionEquiv(wb.get(i), rb.get(i), seen)) continue;
                    return false;
                }
                return true;
            }
            case RECORD: {
                Schema.SeenPair wsc = new Schema.SeenPair(write, read);
                if (!seen.containsKey(wsc)) {
                    seen.put(wsc, true);
                    List<Schema.Field> wb = write.getFields();
                    List<Schema.Field> rb = read.getFields();
                    if (wb.size() != rb.size()) {
                        seen.put(wsc, false);
                    } else {
                        for (int i = 0; i < wb.size(); ++i) {
                            if (wb.get(i).name().equals(rb.get(i).name()) && Resolver.unionEquiv(wb.get(i).schema(), rb.get(i).schema(), seen)) continue;
                            seen.put(wsc, false);
                            break;
                        }
                    }
                }
                return seen.get(wsc);
            }
        }
        throw new IllegalArgumentException("Unknown schema type: " + (Object)((Object)write.getType()));
    }

    public static class ReaderUnion
    extends Action {
        public final int firstMatch;
        public final Action actualAction;

        public ReaderUnion(Schema w, Schema r, GenericData d, int firstMatch, Action actual) {
            super(w, r, d, Action.Type.READER_UNION);
            this.firstMatch = firstMatch;
            this.actualAction = actual;
        }

        public static Action resolve(Schema w, Schema r, GenericData d, Map<Schema.SeenPair, Action> seen) {
            if (w.getType() == Schema.Type.UNION) {
                throw new IllegalArgumentException("Writer schema is union.");
            }
            int i = ReaderUnion.firstMatchingBranch(w, r, d, seen);
            if (0 <= i) {
                return new ReaderUnion(w, r, d, i, Resolver.resolve(w, r.getTypes().get(i), d, seen));
            }
            return new ErrorAction(w, r, d, ErrorAction.ErrorType.NO_MATCHING_BRANCH);
        }

        private static int firstMatchingBranch(Schema w, Schema r, GenericData d, Map<Schema.SeenPair, Action> seen) {
            Schema.Type vt = w.getType();
            int j = 0;
            int structureMatch = -1;
            for (Schema b : r.getTypes()) {
                if (vt == b.getType()) {
                    if (vt == Schema.Type.RECORD || vt == Schema.Type.ENUM || vt == Schema.Type.FIXED) {
                        String vname = w.getFullName();
                        String bname = b.getFullName();
                        if (vname != null && vname.equals(bname)) {
                            return j;
                        }
                        if (vt == Schema.Type.RECORD && !ReaderUnion.hasMatchError(RecordAdjust.resolve(w, b, d, seen))) {
                            String vShortName = w.getName();
                            String bShortName = b.getName();
                            if (structureMatch < 0 || vShortName != null && vShortName.equals(bShortName)) {
                                structureMatch = j;
                            }
                        }
                    } else {
                        return j;
                    }
                }
                ++j;
            }
            if (structureMatch >= 0) {
                return structureMatch;
            }
            j = 0;
            for (Schema b : r.getTypes()) {
                switch (vt) {
                    case INT: {
                        switch (b.getType()) {
                            case LONG: 
                            case FLOAT: 
                            case DOUBLE: {
                                return j;
                            }
                        }
                        break;
                    }
                    case LONG: {
                        switch (b.getType()) {
                            case FLOAT: 
                            case DOUBLE: {
                                return j;
                            }
                        }
                        break;
                    }
                    case FLOAT: {
                        switch (b.getType()) {
                            case DOUBLE: {
                                return j;
                            }
                        }
                        break;
                    }
                    case STRING: {
                        switch (b.getType()) {
                            case BYTES: {
                                return j;
                            }
                        }
                        break;
                    }
                    case BYTES: {
                        switch (b.getType()) {
                            case STRING: {
                                return j;
                            }
                        }
                    }
                }
                ++j;
            }
            return -1;
        }

        private static boolean hasMatchError(Action action) {
            if (action instanceof ErrorAction) {
                return true;
            }
            for (Action a : ((RecordAdjust)action).fieldActions) {
                if (!(a instanceof ErrorAction)) continue;
                return true;
            }
            return false;
        }
    }

    public static class WriterUnion
    extends Action {
        public final Action[] actions;
        public final boolean unionEquiv;

        private WriterUnion(Schema w, Schema r, GenericData d, boolean ue, Action[] a) {
            super(w, r, d, Action.Type.WRITER_UNION);
            this.unionEquiv = ue;
            this.actions = a;
        }

        public static Action resolve(Schema writeSchema, Schema readSchema, GenericData data, Map<Schema.SeenPair, Action> seen) {
            boolean unionEquivalent = Resolver.unionEquiv(writeSchema, readSchema, new HashMap());
            List<Schema> writeTypes = writeSchema.getTypes();
            List<Schema> readTypes = unionEquivalent ? readSchema.getTypes() : null;
            int writeTypeLength = writeTypes.size();
            Action[] actions = new Action[writeTypeLength];
            for (int i = 0; i < writeTypeLength; ++i) {
                actions[i] = Resolver.resolve(writeTypes.get(i), unionEquivalent ? readTypes.get(i) : readSchema, data, seen);
            }
            return new WriterUnion(writeSchema, readSchema, data, unionEquivalent, actions);
        }
    }

    public static class RecordAdjust
    extends Action {
        public final Action[] fieldActions;
        public final Schema.Field[] readerOrder;
        public final int firstDefault;
        public final Object[] defaults;
        public final GenericData.InstanceSupplier instanceSupplier;

        public boolean noReorder() {
            boolean result = true;
            for (int i = 0; result && i < this.readerOrder.length; result &= i == this.readerOrder[i].pos(), ++i) {
            }
            return result;
        }

        private RecordAdjust(Schema w, Schema r, GenericData d, Action[] fa, Schema.Field[] ro, int firstD, Object[] defaults) {
            super(w, r, d, Action.Type.RECORD);
            this.fieldActions = fa;
            this.readerOrder = ro;
            this.firstDefault = firstD;
            this.defaults = defaults;
            this.instanceSupplier = d.getNewRecordSupplier(r);
        }

        static Action resolve(Schema writeSchema, Schema readSchema, GenericData data, Map<Schema.SeenPair, Action> seen) {
            Schema.SeenPair writeReadPair = new Schema.SeenPair(writeSchema, readSchema);
            Action result = seen.get(writeReadPair);
            if (result != null) {
                return result;
            }
            List<Schema.Field> writeFields = writeSchema.getFields();
            List<Schema.Field> readFields = readSchema.getFields();
            int firstDefault = 0;
            for (Schema.Field writeField : writeFields) {
                if (readSchema.getField(writeField.name()) == null) continue;
                ++firstDefault;
            }
            Action[] actions = new Action[writeFields.size()];
            Schema.Field[] reordered = new Schema.Field[readFields.size()];
            Object[] defaults = new Object[reordered.length - firstDefault];
            result = new RecordAdjust(writeSchema, readSchema, data, actions, reordered, firstDefault, defaults);
            seen.put(writeReadPair, result);
            int i = 0;
            int ridx = 0;
            for (Schema.Field writeField : writeFields) {
                Schema.Field readField = readSchema.getField(writeField.name());
                if (readField != null) {
                    reordered[ridx++] = readField;
                    actions[i++] = Resolver.resolve(writeField.schema(), readField.schema(), data, seen);
                    continue;
                }
                actions[i++] = new Skip(writeField.schema(), data);
            }
            for (Schema.Field readField : readFields) {
                Schema.Field writeField = writeSchema.getField(readField.name());
                if (writeField != null) continue;
                if (readField.defaultValue() == null) {
                    result = new ErrorAction(writeSchema, readSchema, data, ErrorAction.ErrorType.MISSING_REQUIRED_FIELD);
                    seen.put(writeReadPair, result);
                    return result;
                }
                defaults[ridx - firstDefault] = data.getDefaultValue(readField);
                reordered[ridx++] = readField;
            }
            return result;
        }
    }

    public static class Skip
    extends Action {
        public Skip(Schema w, GenericData d) {
            super(w, null, d, Action.Type.SKIP);
        }
    }

    public static class EnumAdjust
    extends Action {
        public final int[] adjustments;
        public final Object[] values;
        public final boolean noAdjustmentsNeeded;

        private EnumAdjust(Schema w, Schema r, GenericData d, int[] adj, Object[] values) {
            super(w, r, d, Action.Type.ENUM);
            this.adjustments = adj;
            int rsymCount = r.getEnumSymbols().size();
            int count = Math.min(rsymCount, adj.length);
            boolean noAdj = adj.length <= rsymCount;
            for (int i = 0; noAdj && i < count; noAdj &= i == adj[i], ++i) {
            }
            this.noAdjustmentsNeeded = noAdj;
            this.values = values;
        }

        public static Action resolve(Schema w, Schema r, GenericData d) {
            if (w.getName() != null && !w.getName().equals(r.getName())) {
                return new ErrorAction(w, r, d, ErrorAction.ErrorType.NAMES_DONT_MATCH);
            }
            List<String> wsymbols = w.getEnumSymbols();
            List<String> rsymbols = r.getEnumSymbols();
            int defaultIndex = r.getEnumDefault() == null ? -1 : rsymbols.indexOf(r.getEnumDefault());
            int[] adjustments = new int[wsymbols.size()];
            Object[] values = new Object[wsymbols.size()];
            Object defaultValue = defaultIndex == -1 ? null : d.createEnum(r.getEnumDefault(), r);
            for (int i = 0; i < adjustments.length; ++i) {
                int j = rsymbols.indexOf(wsymbols.get(i));
                if (j < 0) {
                    j = defaultIndex;
                }
                adjustments[i] = j;
                values[i] = j == defaultIndex ? defaultValue : d.createEnum(rsymbols.get(j), r);
            }
            return new EnumAdjust(w, r, d, adjustments, values);
        }
    }

    public static class Container
    extends Action {
        public final Action elementAction;

        public Container(Schema w, Schema r, GenericData d, Action e) {
            super(w, r, d, Action.Type.CONTAINER);
            this.elementAction = e;
        }
    }

    public static class Promote
    extends Action {
        private Promote(Schema w, Schema r, GenericData d) {
            super(w, r, d, Action.Type.PROMOTE);
        }

        public static Action resolve(Schema w, Schema r, GenericData d) {
            if (Promote.isValid(w, r)) {
                return new Promote(w, r, d);
            }
            return new ErrorAction(w, r, d, ErrorAction.ErrorType.INCOMPATIBLE_SCHEMA_TYPES);
        }

        public static boolean isValid(Schema w, Schema r) {
            if (w.getType() == r.getType()) {
                throw new IllegalArgumentException("Only use when reader and writer are different.");
            }
            Schema.Type wt = w.getType();
            switch (r.getType()) {
                case LONG: {
                    switch (wt) {
                        case INT: {
                            return true;
                        }
                    }
                    break;
                }
                case FLOAT: {
                    switch (wt) {
                        case INT: 
                        case LONG: {
                            return true;
                        }
                    }
                    break;
                }
                case DOUBLE: {
                    switch (wt) {
                        case INT: 
                        case LONG: 
                        case FLOAT: {
                            return true;
                        }
                    }
                    break;
                }
                case STRING: 
                case BYTES: {
                    switch (wt) {
                        case STRING: 
                        case BYTES: {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }

    public static class ErrorAction
    extends Action {
        public final ErrorType error;

        public ErrorAction(Schema w, Schema r, GenericData d, ErrorType e) {
            super(w, r, d, Action.Type.ERROR);
            this.error = e;
        }

        public String toString() {
            switch (this.error) {
                case INCOMPATIBLE_SCHEMA_TYPES: 
                case NAMES_DONT_MATCH: 
                case SIZES_DONT_MATCH: 
                case NO_MATCHING_BRANCH: {
                    return "Found " + this.writer.getFullName() + ", expecting " + this.reader.getFullName();
                }
                case MISSING_REQUIRED_FIELD: {
                    List<Schema.Field> rfields = this.reader.getFields();
                    String fname = "<oops>";
                    for (Schema.Field rf : rfields) {
                        if (this.writer.getField(rf.name()) != null || rf.defaultValue() != null) continue;
                        fname = rf.name();
                    }
                    return "Found " + this.writer.getFullName() + ", expecting " + this.reader.getFullName() + ", missing required field " + fname;
                }
            }
            throw new IllegalArgumentException("Unknown error.");
        }

        public static enum ErrorType {
            INCOMPATIBLE_SCHEMA_TYPES,
            NAMES_DONT_MATCH,
            SIZES_DONT_MATCH,
            MISSING_REQUIRED_FIELD,
            NO_MATCHING_BRANCH;

        }
    }

    public static class DoNothing
    extends Action {
        public DoNothing(Schema w, Schema r, GenericData d) {
            super(w, r, d, Action.Type.DO_NOTHING);
        }
    }

    public static abstract class Action {
        public final Schema writer;
        public final Schema reader;
        public final Type type;
        public final LogicalType logicalType;
        public final Conversion<?> conversion;

        protected Action(Schema w, Schema r, GenericData data, Type t) {
            this.writer = w;
            this.reader = r;
            this.type = t;
            if (r == null) {
                this.logicalType = null;
                this.conversion = null;
            } else {
                this.logicalType = r.getLogicalType();
                this.conversion = data.getConversionFor(this.logicalType);
            }
        }

        public static enum Type {
            DO_NOTHING,
            ERROR,
            PROMOTE,
            CONTAINER,
            ENUM,
            SKIP,
            RECORD,
            WRITER_UNION,
            READER_UNION;

        }
    }
}

