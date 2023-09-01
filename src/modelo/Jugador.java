package modelo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import services.EquipoServiceException;

public class Jugador {
	public Integer numero;
	public String codigoEquipo;
	public String nombre;
	public LocalDate fechaNacimiento;

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getCodigoEquipo() {
		return codigoEquipo;
	}

	public void setCodigoEquipo(String codigoEquipo) {
		this.codigoEquipo = codigoEquipo;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	@Override
	public String toString() {
		DateTimeFormatter formato= DateTimeFormatter.ofPattern("dd/MM/yyyy");
		String fecha=fechaNacimiento.format(formato);
		return "- " + nombre + " - "
				+  fecha;
	}
	public void validar() throws EquipoServiceException {
		if ( nombre == null || fechaNacimiento == null) {
			throw new EquipoServiceException("Los datos no pueden ser null");
		}
		if ( codigoEquipo.isBlank() || nombre.isBlank() ) {
			throw new EquipoServiceException("Los datos no pueden ser vacios");
		}
		if ( codigoEquipo.length() > 4 || nombre.length() > 40)  {
			throw new EquipoServiceException("Los datos no pueden exceder el tamao mximo");
		}
	}


}
