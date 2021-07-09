package br.com.letscode.service;

import br.com.letscode.dominio.Reserva;
import br.com.letscode.excecoes.ReservaJaExisteException;

import java.io.IOException;
import java.util.List;

public interface ReservaService {

    public Reserva reservarHorario(Reserva reserva) throws ReservaJaExisteException, IOException;
    public Reserva refazerReserva(Reserva reserva,String cpf) throws IOException;
    public void cancelarReserva(String cpf) throws IOException;
    List<Reserva> listAll() throws IOException;
}
