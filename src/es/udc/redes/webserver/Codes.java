package es.udc.redes.webserver;

public enum Codes {
    OK(200),
    NOT_MODIFIED(304),
    BAD_REQUEST(400),
    NOT_FOUND(404);

    final int code;

    Codes(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
