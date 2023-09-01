package modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import services.EquipoService;
import services.EquipoServiceException;
import services.NotFoundException;

public class Equipo {
	public String codigo;
	public String nombre;
	public List<Jugador> listaJugadores;
	
	

	public Equipo() {
		listaJugadores=new ArrayList<>();
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public List<Jugador> getListaJugadores() {
		return listaJugadores;
	}

	public void setListaJugadores(List<Jugador> listaJugadores) {
		this.listaJugadores = listaJugadores;
	}

	@Override
	public String toString() {
		return "Equipo: " + codigo + " / " + nombre+"\n";
	}
	
	
	public BigDecimal getEdadMedia(Equipo equipo) throws SQLException, NotFoundException {
		EquipoService equipoS = new EquipoService();
		LocalDate actualidad=LocalDate.now();
		BigDecimal sumaEdad= BigDecimal.ZERO;
		
		BigDecimal jugadores= new BigDecimal(equipoS.consultarJugadoresEquipo(equipo.getCodigo()).size());
		
		for (Jugador jugador : equipoS.consultarJugadoresEquipo(equipo.getCodigo())) {
			Period periodo=jugador.getFechaNacimiento().until(actualidad);
			Integer edad=periodo.getYears();
			sumaEdad= sumaEdad.add(new BigDecimal(edad));
		}
		return sumaEdad.divide(jugadores,2,RoundingMode.HALF_DOWN);
		
	}
	public void validar() throws EquipoServiceException {
		if (codigo == null || nombre == null ) {
			throw new EquipoServiceException("Los datos no pueden ser null");
		}
		if ( nombre.isBlank()) {
			throw new EquipoServiceException("Los datos no pueden ser vacios");
		}
		if (codigo.length() > 4 || nombre.length() > 30) {
			throw new EquipoServiceException("Los datos no pueden exceder el tamao mximo");
		}
	}


}
