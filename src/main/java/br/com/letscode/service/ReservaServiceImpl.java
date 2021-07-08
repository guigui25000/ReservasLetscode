package br.com.letscode.service;

import br.com.letscode.dao.ReservaDAO;
import br.com.letscode.dominio.Reserva;
import br.com.letscode.excecoes.ReservaJaExisteException;
import jakarta.inject.Inject;

import java.io.IOException;
import java.util.List;

public class ReservaServiceImpl implements ReservaService{
    @Inject
    private ReservaDAO reservaDAO;
    @Override
    public Reserva reservarHorario(Reserva reserva) throws IOException {
        if(reservaDAO.findByCpf(reserva.getCpf()).isPresent()){
            throw new ReservaJaExisteException("Já existe uma reserva com este cpf");
        }
        if(reservaDAO.findByHours(reserva.getHora()).isPresent()){
            throw new ReservaJaExisteException("Ja existe uma reserva neste horario");
        }
        return reservaDAO.inserirNoArquivo(reserva);
    }

    @Override
    public Reserva refazerReserva(Reserva reserva) throws IOException {
        if(reservaDAO.findByHours(reserva.getHora()).isPresent()){
            throw new ReservaJaExisteException("Ja existe uma reserva neste horario");
        }
        if(reservaDAO.findByCpf(reserva.getCpf()).isEmpty()){
            throw new ReservaJaExisteException("Não existe uma reserva com este cpf");
        }
        return reservaDAO.alterarArquivo(reserva, reserva.getCpf());
    }

    @Override
    public void cancelarReserva(String cpf) throws IOException {
        if(reservaDAO.findByCpf(cpf).isEmpty()){
            throw new ReservaJaExisteException("Não existe uma reserva com este cpf");
        }
        reservaDAO.removerItemArquivo(cpf);
    }

    @Override
    public List<Reserva> listAll() throws IOException {
        return reservaDAO.getAll();
    }

}
