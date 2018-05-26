//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package ir.fq.common.schema.avro.generated;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.nio.ByteBuffer;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Parser;
import org.apache.avro.data.RecordBuilder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.message.BinaryMessageDecoder;
import org.apache.avro.message.BinaryMessageEncoder;
import org.apache.avro.message.SchemaStore;
import org.apache.avro.specific.AvroGenerated;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.avro.specific.SpecificRecordBuilderBase;

@AvroGenerated
public class Envelope extends SpecificRecordBase implements SpecificRecord {
    private static final long serialVersionUID = -4296566462828006106L;
    public static final Schema SCHEMA$ = (new Parser()).parse("{\"type\":\"record\",\"name\":\"Envelope\",\"namespace\":\"ir.fq.common.schema.avro.generated\",\"doc\":\"* An envelope wraps a single MessageType record.  We use it to transmit and store any number of MessageType records.\",\"fields\":[{\"name\":\"schemaVersion\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"type\",\"type\":{\"type\":\"enum\",\"name\":\"MessageType\",\"symbols\":[\"PIXEL_HIT\",\"AD_COLLISION\",\"AD_CLICK\",\"VIEWABILITY\",\"MOUSEOVER\",\"ANDROID_HIT\",\"SCORING_REQUEST_AND_RESPONSE\",\"WEB_RTC\",\"ENRICHED\",\"SCORED\"]}},{\"name\":\"body\",\"type\":\"bytes\"},{\"name\":\"timestampMs\",\"type\":\"long\",\"doc\":\"* number of milliseconds since epoch -- midnight, January 1, 1970 UTC\\n     *\\n     * NOTE: in most cases this is _server_ time; it denotes the time at which this envelope was created,\\n     * not event time.  If it's crucial to record event time, the corresponding MessageType can store it in its own timestamp field.\"}]}");
    private static SpecificData MODEL$ = new SpecificData();
    private static final BinaryMessageEncoder<Envelope> ENCODER;
    private static final BinaryMessageDecoder<Envelope> DECODER;
    private String schemaVersion;
    private MessageType type;
    private ByteBuffer body;
    private long timestampMs;
    private static final DatumWriter<Envelope> WRITER$;
    private static final DatumReader<Envelope> READER$;

    public static Schema getClassSchema() {
        return SCHEMA$;
    }

    public static BinaryMessageDecoder<Envelope> getDecoder() {
        return DECODER;
    }

    public static BinaryMessageDecoder<Envelope> createDecoder(SchemaStore resolver) {
        return new BinaryMessageDecoder(MODEL$, SCHEMA$, resolver);
    }

    public ByteBuffer toByteBuffer() throws IOException {
        return ENCODER.encode(this);
    }

    public static Envelope fromByteBuffer(ByteBuffer b) throws IOException {
        return (Envelope)DECODER.decode(b);
    }

    public Envelope() {
    }

    public Envelope(String schemaVersion, MessageType type, ByteBuffer body, Long timestampMs) {
        this.schemaVersion = schemaVersion;
        this.type = type;
        this.body = body;
        this.timestampMs = timestampMs;
    }

    public Schema getSchema() {
        return SCHEMA$;
    }

    public Object get(int field$) {
        switch(field$) {
            case 0:
                return this.schemaVersion;
            case 1:
                return this.type;
            case 2:
                return this.body;
            case 3:
                return this.timestampMs;
            default:
                throw new AvroRuntimeException("Bad index");
        }
    }

    public void put(int field$, Object value$) {
        switch(field$) {
            case 0:
                this.schemaVersion = (String)value$;
                break;
            case 1:
                this.type = (MessageType)value$;
                break;
            case 2:
                this.body = (ByteBuffer)value$;
                break;
            case 3:
                this.timestampMs = (Long)value$;
                break;
            default:
                throw new AvroRuntimeException("Bad index");
        }

    }

    public String getSchemaVersion() {
        return this.schemaVersion;
    }

    public MessageType getType() {
        return this.type;
    }

    public ByteBuffer getBody() {
        return this.body;
    }

    public Long getTimestampMs() {
        return this.timestampMs;
    }

    public static Envelope.Builder newBuilder() {
        return new Envelope.Builder();
    }

    public static Envelope.Builder newBuilder(Envelope.Builder other) {
        return new Envelope.Builder(other);
    }

    public static Envelope.Builder newBuilder(Envelope other) {
        return new Envelope.Builder(other);
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        WRITER$.write(this, SpecificData.getEncoder(out));
    }

    public void readExternal(ObjectInput in) throws IOException {
        READER$.read(this, SpecificData.getDecoder(in));
    }

    static {
        ENCODER = new BinaryMessageEncoder(MODEL$, SCHEMA$);
        DECODER = new BinaryMessageDecoder(MODEL$, SCHEMA$);
        WRITER$ = MODEL$.createDatumWriter(SCHEMA$);
        READER$ = MODEL$.createDatumReader(SCHEMA$);
    }

    public static class Builder extends SpecificRecordBuilderBase<Envelope> implements RecordBuilder<Envelope> {
        private String schemaVersion;
        private MessageType type;
        private ByteBuffer body;
        private long timestampMs;

        private Builder() {
            super(Envelope.SCHEMA$);
        }

        private Builder(Envelope.Builder other) {
            super(other);
            if (isValidValue(this.fields()[0], other.schemaVersion)) {
                this.schemaVersion = (String)this.data().deepCopy(this.fields()[0].schema(), other.schemaVersion);
                this.fieldSetFlags()[0] = true;
            }

            if (isValidValue(this.fields()[1], other.type)) {
                this.type = (MessageType)this.data().deepCopy(this.fields()[1].schema(), other.type);
                this.fieldSetFlags()[1] = true;
            }

            if (isValidValue(this.fields()[2], other.body)) {
                this.body = (ByteBuffer)this.data().deepCopy(this.fields()[2].schema(), other.body);
                this.fieldSetFlags()[2] = true;
            }

            if (isValidValue(this.fields()[3], other.timestampMs)) {
                this.timestampMs = (Long)this.data().deepCopy(this.fields()[3].schema(), other.timestampMs);
                this.fieldSetFlags()[3] = true;
            }

        }

        private Builder(Envelope other) {
            super(Envelope.SCHEMA$);
            if (isValidValue(this.fields()[0], other.schemaVersion)) {
                this.schemaVersion = (String)this.data().deepCopy(this.fields()[0].schema(), other.schemaVersion);
                this.fieldSetFlags()[0] = true;
            }

            if (isValidValue(this.fields()[1], other.type)) {
                this.type = (MessageType)this.data().deepCopy(this.fields()[1].schema(), other.type);
                this.fieldSetFlags()[1] = true;
            }

            if (isValidValue(this.fields()[2], other.body)) {
                this.body = (ByteBuffer)this.data().deepCopy(this.fields()[2].schema(), other.body);
                this.fieldSetFlags()[2] = true;
            }

            if (isValidValue(this.fields()[3], other.timestampMs)) {
                this.timestampMs = (Long)this.data().deepCopy(this.fields()[3].schema(), other.timestampMs);
                this.fieldSetFlags()[3] = true;
            }

        }

        public String getSchemaVersion() {
            return this.schemaVersion;
        }

        public Envelope.Builder setSchemaVersion(String value) {
            this.validate(this.fields()[0], value);
            this.schemaVersion = value;
            this.fieldSetFlags()[0] = true;
            return this;
        }

        public boolean hasSchemaVersion() {
            return this.fieldSetFlags()[0];
        }

        public Envelope.Builder clearSchemaVersion() {
            this.schemaVersion = null;
            this.fieldSetFlags()[0] = false;
            return this;
        }

        public MessageType getType() {
            return this.type;
        }

        public Envelope.Builder setType(MessageType value) {
            this.validate(this.fields()[1], value);
            this.type = value;
            this.fieldSetFlags()[1] = true;
            return this;
        }

        public boolean hasType() {
            return this.fieldSetFlags()[1];
        }

        public Envelope.Builder clearType() {
            this.type = null;
            this.fieldSetFlags()[1] = false;
            return this;
        }

        public ByteBuffer getBody() {
            return this.body;
        }

        public Envelope.Builder setBody(ByteBuffer value) {
            this.validate(this.fields()[2], value);
            this.body = value;
            this.fieldSetFlags()[2] = true;
            return this;
        }

        public boolean hasBody() {
            return this.fieldSetFlags()[2];
        }

        public Envelope.Builder clearBody() {
            this.body = null;
            this.fieldSetFlags()[2] = false;
            return this;
        }

        public Long getTimestampMs() {
            return this.timestampMs;
        }

        public Envelope.Builder setTimestampMs(long value) {
            this.validate(this.fields()[3], value);
            this.timestampMs = value;
            this.fieldSetFlags()[3] = true;
            return this;
        }

        public boolean hasTimestampMs() {
            return this.fieldSetFlags()[3];
        }

        public Envelope.Builder clearTimestampMs() {
            this.fieldSetFlags()[3] = false;
            return this;
        }

        public Envelope build() {
            try {
                Envelope record = new Envelope();
                record.schemaVersion = this.fieldSetFlags()[0] ? this.schemaVersion : (String)this.defaultValue(this.fields()[0]);
                record.type = this.fieldSetFlags()[1] ? this.type : (MessageType)this.defaultValue(this.fields()[1]);
                record.body = this.fieldSetFlags()[2] ? this.body : (ByteBuffer)this.defaultValue(this.fields()[2]);
                record.timestampMs = this.fieldSetFlags()[3] ? this.timestampMs : (Long)this.defaultValue(this.fields()[3]);
                return record;
            } catch (Exception var2) {
                throw new AvroRuntimeException(var2);
            }
        }
    }
}
