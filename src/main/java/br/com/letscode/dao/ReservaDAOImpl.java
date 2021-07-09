package br.com.letscode.dao;

import br.com.letscode.dominio.Reserva;
import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ReservaDAOImpl implements ReservaDAO{
    private String caminho = "D:\\ProjetosIDEIA\\ReservaLestcode\\src\\main\\java\\br\\com\\letscode\\dao\\documentos/reservas";
    private Path path;
    @PostConstruct
    public void init(){
        try {
            path = Paths.get(caminho);
            if (!path.toFile().exists()) {
                Files.createFile(path);
            }
        }catch (IOException ioException){
            ioException.printStackTrace();
        }
    }

    @Override
    public Reserva inserirNoArquivo(Reserva reserva) throws IOException {
        write(format(reserva), StandardOpenOption.APPEND);
        return reserva;
    }

    private void write(String clienteStr, StandardOpenOption option) throws IOException {
        try(BufferedWriter bf = Files.newBufferedWriter(path, option)){
            bf.flush();
            bf.write(clienteStr);
        }
    }

    @Override
    public List<Reserva> getAll() throws IOException {
        List<Reserva> reservas;
        try(BufferedReader br = Files.newBufferedReader(path)){
            reservas = br.lines().filter(Objects::nonNull).filter(Predicate.not(String::isEmpty)).map(this::convert).collect(Collectors.toList());
        }
        return reservas;
    }

    @Override
    public Optional<Reserva> findByCpf(String cpf) throws IOException {
        List<Reserva> reservas = getAll();
        return reservas.stream().filter(reserva -> reserva.getCpf().equals(cpf)).findFirst();
    }

    @Override
    public Reserva alterarArquivo(Reserva reserva, String identificador) throws IOException {
        List<Reserva> reservar = getAll();
        Optional<Reserva> optionalReserva = reservar.stream()
                .filter(reservaSearch -> reservaSearch.getCpf().equals(identificador)).findFirst();
        if(optionalReserva.isPresent()){
            System.out.println("CONTEUDO ENCONTRADO");
            optionalReserva.get().setHora(reserva.getHora());
            reescreverArquivo(reservar);
            return optionalReserva.get();
        }
        return reserva;
    }

    private void reescreverArquivo(List<Reserva> reservas) throws IOException {

        StringBuilder builder = new StringBuilder();
        for (Reserva reservaBuilder: reservas) {
            builder.append(format(reservaBuilder));
        }
        write(builder.toString(),StandardOpenOption.CREATE);
    }

    @Override
    public void removerItemArquivo(String identificador) throws IOException {
        List<Reserva> reservas = getAll();
        List<Reserva> reservasResultantes = new ArrayList<>();
        for (Reserva reserva:reservas){
            if(!reserva.getCpf().equals(identificador)){
                reservasResultantes.add(reserva);
            }
        }
        eraseContent();
        reescreverArquivo(reservasResultantes);
    }

    @Override
    public Optional<Reserva> findByHours(String hora) throws IOException {
        //TODO testar se funciona :)
        List<Reserva> reservas = getAll();
        return reservas.stream().filter(reserva -> reserva.getHora().equals(hora)).findFirst();
    }

    private String format(Reserva reserva){
        return String.format("%s;%s;%s;%s \r\n",reserva.getCpf(),reserva.getNome(), reserva.getHora(),reserva.getNumeroPessoas());
    }

    private Reserva convert(String linha){
        StringTokenizer token = new StringTokenizer(linha,";");
        Reserva reserva = new Reserva();
        reserva.setCpf(token.nextToken());
        reserva.setNome(token.nextToken());
        reserva.setHora(token.nextToken());
        reserva.setNumeroPessoas(token.nextToken());
        return reserva;
    }

    public void eraseContent() throws IOException {
        BufferedWriter writer = Files.newBufferedWriter(path);
        writer.write("");
        writer.flush();
    }
}
