package app;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

import modelo.Equipo;
import modelo.Jugador;
import services.EquipoService;
import services.EquipoServiceException;
import services.NotFoundException;

public class App {

	public static void main(String[] args) throws SQLException {
		Integer opcion = 0;
		Scanner sc = new Scanner(System.in);

		try {
			do {
				System.out.println("Elige una de las siguientes opciones: " + "\n" + "\t"
						+ "(1) Ver todos los equipos registrados " + "\n" + "\t" + "(2) Crear nuevo equipo" + "\n"
						+ "\t" + "(3) Consultar un equipo por su codigo " + "\n" + "\t" + "(0) Salir");
				opcion = sc.nextInt();
				if (opcion == 1) {
					verEquiposRegistrados();
				} else if (opcion == 2) {
					crearNuevoEquipo(sc);
				} else if (opcion == 3) {
					consultarEquipo(sc);
				} else if (opcion == 0) {
					System.out.println("Bye");
				} else {
					System.out.println("Lo siento, usted se ha equivocado de opcion");
				}
			} while (opcion != 0);
		} catch (InputMismatchException e) {
			System.out.println("Lo siento usted ha añadido una letra");
		} catch (EquipoServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void consultarEquipo(Scanner sc) {
		EquipoService equipoService = new EquipoService();

		System.out.println("Introduce un codigo de equipo que quieras buscar");
		sc.nextLine();
		String codigo = sc.nextLine();
		try {

			consultarEquipoYPlantilla(equipoService, codigo);
			Integer opcion1;
			do {
				System.out.println("Elige un de estas opciones para el equipo: " + "\n" + "\t"
						+ "(1) Ver plantilla del equipo" + "\n" + "\t" + "(2) Borrar equipo " + "\n" + "\t"
						+ "(3) Añadir un jugador" + "\n" + "\t" + "(4) Calcular edad media de plantilla " + "\n" + "\t"
						+ "(5) Exportar plantilla del equipo a fichero " + "\n" + "\t"
						+ "(0) Volver al menú principal");
				opcion1 = sc.nextInt();
				if (opcion1 == 0) {

				} else if (opcion1 == 1) {
					consultarEquipoYPlantilla(equipoService, codigo);
				}

				else if (opcion1 == 2) {
					equipoService.borrarEquipoCompleto(codigo);
				} else if (opcion1 == 3) {
					Jugador jugador = new Jugador();
					Equipo equipo = new Equipo();
					equipo.setCodigo(codigo);
					System.out.println("Introduce nombre");
					sc.nextLine();
					jugador.setNombre(sc.nextLine());
					System.out.println("Introduce fecha de nacimiento dd/MM/YYYY");
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
					jugador.setFechaNacimiento(LocalDate.parse(sc.nextLine(), formatter));
					jugador.setCodigoEquipo(equipo.getCodigo());
					jugador.validar();
					equipoService.añadirJugadorAEquipo(equipo, jugador);
				} else if (opcion1 == 4) {
					Equipo equipo = new Equipo();
					equipo.setCodigo(codigo);
					System.out.println("Edad Media : " + equipo.getEdadMedia(equipo));
				}

				else if (opcion1 == 5) {
				} else {
					System.out.println("Creo que usted se ha equivocado vuleve a elegir");
				}
			} while (opcion1 != 0 || opcion1 != 2);

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (EquipoServiceException e) {
			e.printStackTrace();
		}
	}

	private static void crearNuevoEquipo(Scanner sc) throws SQLException {
		Equipo equipo = new Equipo();
		Jugador jugador = new Jugador();
		EquipoService equipoService = new EquipoService();
		List<Jugador> jugadores = new ArrayList<>();
		System.out.println("Indica código equipo");
		sc.nextLine();
		equipo.setCodigo(sc.nextLine());
		System.out.println("Indica el nombre del equipo");
		equipo.setNombre(sc.nextLine());
		try {
			equipo.validar();
			equipoService.crearEquipo(equipo);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (EquipoServiceException e) {
			e.printStackTrace();
		}
		String respuesta = pedirDatosJugador(sc, equipo, jugador, jugadores);
		do {
			if (respuesta.equalsIgnoreCase("S")) {
				respuesta = pedirDatosJugador(sc, equipo, jugador, jugadores);
				try {
					jugador.validar();
					equipoService.añadirJugadorAEquipo(equipo, jugador);
				} catch (EquipoServiceException e) {
					e.printStackTrace();
				}
			} else if (respuesta.equalsIgnoreCase("N")) {
				System.out.println("Equipo guardado");
			} else {
				System.out.println("Lo siento no he entendido su respuesta");
			}

		} while (!respuesta.equalsIgnoreCase("N"));
	}

	private static void verEquiposRegistrados() throws EquipoServiceException {
		EquipoService equipoService = new EquipoService();
		try {
			System.out.println(equipoService.consultarEquipos());

		} catch (SQLException e) {
			System.err.println("Error");
			e.printStackTrace();
		}
	}

	private static void consultarEquipoYPlantilla(EquipoService equipoService, String codigo)
			throws SQLException, NotFoundException, EquipoServiceException {
		System.out.println(equipoService.consultarEquipoCompleto(codigo) + "PLANTILLA : ");
		List<Jugador> listaJugadores = equipoService.consultarJugadoresEquipo(codigo);
		for (Jugador jugador : listaJugadores) {
			System.out.println("\t" + jugador + "\n");
		}
	}

	private static String pedirDatosJugador(Scanner sc, Equipo equipo, Jugador jugador, List<Jugador> jugadores) {
		System.out.println("Indica el nombre jugador");
		jugador.setNombre(sc.nextLine());
		jugador.setCodigoEquipo(equipo.getCodigo());
		jugadores.add(jugador);
		equipo.setListaJugadores(jugadores);
		System.out.println("Indica fecha nacimiento del jugador (dd/MM/yyyy)");
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		jugador.setFechaNacimiento(LocalDate.parse(sc.nextLine(), formatter));
		System.out.println("¿Desea añadir otro jugador?");
		String respuesta = sc.nextLine();
		return respuesta;
	}

}


	
