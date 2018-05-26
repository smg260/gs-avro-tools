package ir.fq.platform.common.serialization.schema;

public class SchemaNotFoundException extends RuntimeException {
    public SchemaNotFoundException() {
        super();
    }
    public SchemaNotFoundException(Exception ex) {
        super(ex);
    }
    public SchemaNotFoundException(String msg) {
        super(msg);
    }
    public SchemaNotFoundException(String msg, Exception ex) {
        super(msg, ex);
    }
}
