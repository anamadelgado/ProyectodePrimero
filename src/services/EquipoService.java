package services;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import modelo.Equipo;
import modelo.Jugador;

public class EquipoService {
	public List<Equipo> consultarEquipos() throws EquipoServiceException, SQLException {
		Connection conn = null;
		ResultSet re = null;
		PreparedStatement stmt = null;
		try {
			ProveedorConexiones proveedorConexiones = new ProveedorConexiones();
			List<Equipo> listaEquipos = new ArrayList<>();
			conn = proveedorConexiones.obtenerConexion();
			String sql = "SELECT*FROM EQUIPO";
			stmt = conn.prepareStatement(sql);
			re = stmt.executeQuery();
			while (re.next()) {
				Equipo equipo = new Equipo();
				String codigo = re.getString("codigo");
				String nombre = re.getString("nombre");
				equipo.setCodigo(codigo);
				equipo.setNombre(nombre);
				listaEquipos.add(equipo);

			}
			if (listaEquipos.isEmpty()) {
				throw new EquipoServiceException("No hay equipos");
			}
			return listaEquipos;

		} finally {
			if (conn != null) {
				conn.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}

	}

	public List<Jugador> consultarJugadoresEquipo(String codigo) throws SQLException, NotFoundException {
		Connection conn = null;
		ResultSet re = null;
		PreparedStatement stmt = null;
		try {
			ProveedorConexiones proveedorConexiones = new ProveedorConexiones();
			List<Jugador> listaJugadores = new ArrayList<>();
			conn = proveedorConexiones.obtenerConexion();
			String sql = "SELECT*FROM JUGADOR WHERE CODIGO_EQUIPO=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, codigo);
			re = stmt.executeQuery();
			while (re.next()) {
				Jugador jugador = new Jugador();
				Integer numero = re.getInt("numero");
				String codigoEquipo = re.getString("codigo_equipo");
				String nombre = re.getString("nombre");
				jugador.setFechaNacimiento(re.getDate("nacimiento").toLocalDate());
				jugador.setNumero(numero);
				jugador.setCodigoEquipo(codigoEquipo);
				jugador.setNombre(nombre);
				listaJugadores.add(jugador);
			}
			if (listaJugadores.isEmpty()) {
				throw new NotFoundException("No hay jugadores");
			}
			return listaJugadores;

		} finally {
			if (conn != null) {
				conn.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}

	}

	public Equipo consultarEquipoCompleto(String codigoEquipo)
			throws SQLException, NotFoundException, EquipoServiceException {

		Connection conn = null;
		ResultSet re = null;
		PreparedStatement stmt = null;
		try {
			ProveedorConexiones proveedorConexiones = new ProveedorConexiones();
			conn = proveedorConexiones.obtenerConexion();
			String sql = "SELECT*FROM EQUIPO WHERE CODIGO=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, codigoEquipo);
			re = stmt.executeQuery();
			if (re.next()) {
				Equipo equipo = new Equipo();
				String codigo = re.getString("codigo");
				String nombre = re.getString("nombre");
				equipo.setCodigo(codigo);
				equipo.setNombre(nombre);
				equipo.setListaJugadores(consultarJugadoresEquipo(codigoEquipo));
				return equipo;
				
			} else {
				throw new NotFoundException("No existe el quipo con ese codigo");
			}

		} finally {
			if (conn != null) {
				conn.close();
			}
			if (stmt != null) {
				stmt.close();
			}

		}
	}

	private void insertarJugador(Jugador jugador, Connection conn) throws SQLException {
		PreparedStatement stmt = null;
		
		try {
			String sql = "INSERT INTO JUGADOR VALUES(?,?,?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1,jugador.getNumero());
			stmt.setString(2, jugador.getCodigoEquipo());
			stmt.setString(3, jugador.getNombre());
			stmt.setDate(4, Date.valueOf(jugador.fechaNacimiento));
			if(stmt.executeUpdate() !=0) {
				System.out.println("Jugador guardado!!!");
			}

		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

	}

	public void insertarJugador(Jugador jugador) throws SQLException, EquipoServiceException {
		Connection conn = null;
		try {
			conn = new ProveedorConexiones().obtenerConexion();
		
			insertarJugador(jugador, conn);
		}  finally {
			if (conn != null) {
				conn.close();
			}
		}
	}

	private void crearEquipo(Equipo equipo, Connection conn) throws SQLException, EquipoServiceException, NotFoundException {
		PreparedStatement stmt = null;
		try {
			String sql = "INSERT INTO EQUIPO VALUES (?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, equipo.getCodigo());
			stmt.setString(2, equipo.getNombre());
			stmt.execute();
			
			for (Jugador jugador : equipo.getListaJugadores()) {
				jugador.setCodigoEquipo(equipo.getCodigo());
				jugador.setNumero(jugador.getNumero());
				añadirJugadorAEquipo(equipo, jugador);
				
			}

		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

	}

	public void crearEquipo(Equipo equipo) throws SQLException, EquipoServiceException, NotFoundException {
		Connection conn = null;
		try {
			conn = new ProveedorConexiones().obtenerConexion();
			conn.setAutoCommit(false);
			crearEquipo(equipo, conn);
			
			conn.commit();

		}catch (SQLException e) {
			conn.rollback();
			throw e;
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

	}

	public void borrarEquipoCompleto(String codigo) throws SQLException, NotFoundException, EquipoServiceException {
		Connection conn = null;
		PreparedStatement stmt1 = null;
		PreparedStatement stmt2 = null;
		try {
			ProveedorConexiones c = new ProveedorConexiones();
			conn = c.obtenerConexion();
			conn.setAutoCommit(false);
			String sql1 = "DELETE FROM JUGADOR WHERE CODIGO_EQUIPO=?";
			stmt1 = conn.prepareStatement(sql1);
			stmt1.setString(1, codigo);
			stmt1.executeUpdate();
			String sql2 = "DELETE FROM EQUIPO WHERE CODIGO=?";
			stmt2 = conn.prepareStatement(sql2);
			stmt2.setString(1, codigo);
			stmt2.executeUpdate();
			conn.commit();
			if (stmt2.executeUpdate() == 0) {
				conn.rollback();
				throw new NotFoundException("No hay equipos con ese codigo");
			}else {
				System.out.println("Equipo eliminado!!");
			}
			

		} catch (Exception e) {
			conn.rollback();
		}

		finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	public void añadirJugadorAEquipo(Equipo equipo, Jugador jugador) throws EquipoServiceException, NotFoundException {
		try {
			jugador.setCodigoEquipo(equipo.getCodigo());
			List<Jugador> cantidad= consultarJugadoresEquipo(equipo.getCodigo());
			jugador.setNumero(cantidad.size()+1);
			insertarJugador(jugador);
		} catch (SQLException e) {
		throw new EquipoServiceException(e);
			
		}
		
	}
	
	
}
