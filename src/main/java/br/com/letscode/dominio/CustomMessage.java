package br.com.letscode.dominio;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class CustomMessage {

    private Integer status;
    private String message;

}
