package br.com.letscode.excecoes;

public class ReservaJaExisteException extends RuntimeException {
    public ReservaJaExisteException(String mensagem) {
        super(mensagem);
    }
}
