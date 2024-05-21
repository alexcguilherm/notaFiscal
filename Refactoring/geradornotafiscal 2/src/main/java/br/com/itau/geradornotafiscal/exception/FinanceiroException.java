package br.com.itau.geradornotafiscal.exception;

public class FinanceiroException extends RuntimeException {

    public FinanceiroException(String message, Throwable cause) {
        super(message, cause);
    }
}
