package resources;

import java.util.Scanner;
import java.util.List;
public class ConsultaSocio {
    private Clube clube;
    private Scanner scanner;

    public ConsultaSocio(Clube clube, Scanner scanner) {
        this.clube = clube;
        this.scanner = scanner;
    }

    public Socio consultarPorDocumento(String documento) {
        List<Socio> socios = clube.getSocios();
        for (Socio socio : socios) {
            if (socio.getDocumento().equals(documento)) {
                return socio;
            }
        }
        return null;
    }

    public Socio consultarPorCarteirinha(int numeroCarteirinha) {
        List<Socio> socios = clube.getSocios();
        for (Socio socio : socios) {
            if (socio.getNumeroCarteirinha() == numeroCarteirinha) {
                return socio;
            }
        }
        return null;
    }

    public Socio consultarPorNome(String nome) {
        List<Socio> socios = clube.getSocios();
        for (Socio socio : socios) {
            if (socio.getNome().equalsIgnoreCase(nome)) {
                return socio;
            }
        }
        return null;
    }

    public void consultarSocio() {
        System.out.println("===== Consultar Sócio =====");
        System.out.print("Digite o documento, nome ou número da carteirinha do sócio: ");
        String busca = scanner.nextLine();

        Socio socioEncontrado = null;

        if (Character.isDigit(busca.charAt(0))) {
            int numeroCarteirinhaConsulta = Integer.parseInt(busca);
            socioEncontrado = consultarPorCarteirinha(numeroCarteirinhaConsulta);
        } else {
            socioEncontrado = consultarPorDocumento(busca);
            if (socioEncontrado == null) {
                socioEncontrado = consultarPorNome(busca);
            }
        }

        if (socioEncontrado != null) {
            System.out.println("Cadastro encontrado:");
            System.out.println(socioEncontrado);
        } else {
            System.out.println("Cadastro não encontrado.");
        }
    }
}

