package services;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ProveedorConexiones {
	public Connection obtenerConexion() throws SQLException {
		String URL = "jdbc:mariadb://localhost:3306/tema6";
		String claseDriver = "org.mariadb.jdbc.Driver";
		String usuario = "programacion";
		String password = "programacion";
		try {
			Class.forName(claseDriver);
			return DriverManager.getConnection(URL, usuario, password);
		} catch (ClassNotFoundException e) {
			System.err.println("No se ha encontrado la clase " + claseDriver);
			throw new SQLException("No se ha encontrado,e");
		}

	}
}
