package br.com.letscode.dao;

import br.com.letscode.dominio.Reserva;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ReservaDAO {

    Reserva inserirNoArquivo(Reserva reserva) throws IOException;

    List<Reserva> getAll() throws IOException;

    Optional<Reserva> findByCpf(String cpf) throws IOException;

    Reserva alterarArquivo(Reserva reserva, String identificador) throws IOException;

    void removerItemArquivo(String identificador) throws IOException;

    Optional<Reserva> findByHours(int hora) throws IOException;
}
