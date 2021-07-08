package br.com.letscode.dominio;

import lombok.Data;

@Data
public class Reserva {
    private Integer hora;
    private String cpf;
    private Integer numeroPessoas;
    private String nome;
}
