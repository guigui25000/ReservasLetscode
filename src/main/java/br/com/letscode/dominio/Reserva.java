package br.com.letscode.dominio;

import lombok.Data;

@Data
public class Reserva {
    private String hora;
    private String cpf;
    private String numeroPessoas;
    private String nome;
}
