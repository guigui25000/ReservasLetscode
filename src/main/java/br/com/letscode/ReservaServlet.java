package br.com.letscode;

import br.com.letscode.dominio.Reserva;
import br.com.letscode.dominio.CustomMessage;
import br.com.letscode.excecoes.ReservaJaExisteException;
import br.com.letscode.service.ReservaService;
import com.google.gson.Gson;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@WebServlet(name = "reservaServlet" , urlPatterns = "/reserva")
public class ReservaServlet extends HttpServlet {

    public static final String RERVAS_SESSION = "reservas";
    @Inject
    private ReservaService reservaService;

    private Gson gson;

    @Override
    public void init() throws ServletException {
        super.init();
        gson = new Gson();
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder conteudo = getBody(request);
        Reserva clienteRequest = gson.fromJson(conteudo.toString(), Reserva.class);
        PrintWriter print = prepareResponse(response);
        String resposta = "";
        if(clienteRequest.getHora() == null || null==clienteRequest.getCpf() || clienteRequest.getNome()==null || clienteRequest.getNumeroPessoas() == null){
            CustomMessage message = new CustomMessage(HttpServletResponse.SC_BAD_REQUEST, "Invalid Parameters");
            response.setStatus(message.getStatus());
            resposta= gson.toJson(message);
        }else{

            try {
                HttpSession sessao = request.getSession(true);

                reservaService.reservarHorario(clienteRequest);

                List<Reserva> reservas = reservaService.listAll();


                sessao.setAttribute(RERVAS_SESSION, reservas);

                resposta = gson.toJson(reservas);
            }catch (ReservaJaExisteException usuarioJaExisteException){
                response.setStatus(400);
                resposta = gson.toJson(new CustomMessage(400,usuarioJaExisteException.getMessage()));
            }
        }
        print.write(resposta);
        print.close();

    }

    private PrintWriter prepareResponse(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter print = response.getWriter();
        return print;
    }

    private StringBuilder getBody(HttpServletRequest request) throws IOException {
        BufferedReader br = request.getReader();
        String line="";
        StringBuilder conteudo = new StringBuilder();

        while(null!= (line= br.readLine())){
            conteudo.append(line);
        }
        return conteudo;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String cpfPesquisa =  request.getParameter("cpf");
        HttpSession sessao = request.getSession();
        List<Reserva> clientes = new ArrayList<>();
        if(Objects.nonNull(sessao.getAttribute(RERVAS_SESSION))){
            clientes.addAll((List<Reserva>) sessao.getAttribute(RERVAS_SESSION));
        }else{
            clientes.addAll(reservaService.listAll());
        }

        PrintWriter printWriter =prepareResponse(response);
        if(null!=cpfPesquisa && Objects.nonNull(clientes)){
            Optional<Reserva> optionalCliente = clientes.stream().filter(cliente -> cliente.getCpf().equals(cpfPesquisa)).findFirst();
            if(optionalCliente.isPresent()){

                printWriter.write(gson.toJson(optionalCliente.get()));
            }else{

                CustomMessage message = new CustomMessage(404, "Conteúdo não encontrado");
                response.setStatus(404);
                printWriter.write(gson.toJson(message));
            }
        }else {

            printWriter.write(gson.toJson(clientes));

        }

        printWriter.close();
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        StringBuilder conteudo = getBody(request);
        String identificador = request.getParameter("cpf");
        PrintWriter printWriter = prepareResponse(response);
        String resposta= "";
        if(Objects.isNull(identificador)){
            resposta = erroMessage(response);
        }else{
            Reserva reserva = gson.fromJson(conteudo.toString(),Reserva.class);
            resposta = gson.toJson(reservaService.refazerReserva(reserva,identificador));
            request.getSession().setAttribute(RERVAS_SESSION,reservaService.listAll());
        }

        printWriter.write(resposta);
        printWriter.close();
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String identificador = request.getParameter("cpf");
        PrintWriter printWriter = prepareResponse(response);
        String resposta;
        if(Objects.isNull(identificador)){
            resposta = erroMessage(response);
        }else {
            reservaService.cancelarReserva(identificador);
            resposta = gson.toJson(new CustomMessage(204, "cliente removido"));
            request.getSession().setAttribute(RERVAS_SESSION, reservaService.listAll());
        }
        printWriter.write(resposta);
        printWriter.close();
    }

    private String erroMessage(HttpServletResponse response) {

        response.setStatus(400);
        return gson.toJson(new CustomMessage(400,"cpf não informado"));

    }
}
