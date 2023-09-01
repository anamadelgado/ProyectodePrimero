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
	public List<Equipo> consultarEquipos() throws SQLException {
		Connection conn = null;
		ResultSet re = null;
		PreparedStatement stmt = null;
		List<Equipo> lista = new ArrayList<>();
		try {
			ProveedorConexiones c = new ProveedorConexiones();
			conn = c.obtenerConexion();
			String sql = "select*from equipo";
			stmt = conn.prepareStatement(sql);
			re = stmt.executeQuery();
			while (re.next()) {
				Equipo equipo = new Equipo();
				String codigo = re.getString("codigo");
				String nombre = re.getString("nombre");
				equipo.setCodigo(codigo);
				equipo.setNombre(nombre);
				lista.add(equipo);

			}
			return lista;
		} finally {
			if (conn != null) {
				conn.close();
			}
			if (stmt != null) {
				conn.close();
			}
		}

	}

	public List<Jugador> consultarJugadoresEquipo(String codigo) throws SQLException {
		Connection conn = null;
		ResultSet re = null;
		PreparedStatement stmt = null;
		List<Jugador> lista = new ArrayList<>();
		try {
			ProveedorConexiones c = new ProveedorConexiones();
			conn = c.obtenerConexion();
			String sql = "select*from jugador where codigo_equipo=?";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, codigo);
			re = stmt.executeQuery();
			while (re.next()) {
				Jugador jugador = new Jugador();
				Integer numero = re.getInt("numero");
				String codigoEquipo = re.getString("codigo_equipo");
				String nombre = re.getString("nombre");
				jugador.setFechaNacimiento(re.getDate("nacimiento").toLocalDate());
				jugador.setNombre(nombre);
				jugador.setCodigoEquipo(codigoEquipo);
				jugador.setNumero(numero);
				lista.add(jugador);
			}
			return lista;
		} finally {
			if (conn != null) {
				conn.close();
			}
			if (stmt != null) {
				conn.close();
			}
		}

	}

	public Equipo consultarEquipoCompleto(String codigoEquipo)
			throws SQLException, NotFoundException, EquipoServiceException {
		Connection conn = null;
		ResultSet re = null;
		PreparedStatement stmt = null;
		try {
			ProveedorConexiones c = new ProveedorConexiones();
			conn = c.obtenerConexion();
			String sql = "select*from equipo where codigo=?";
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
				throw new NotFoundException("No se ha encontrado eqquipo");
			}

		} catch (Exception e) {
			throw new EquipoServiceException("No se ha encontrado eqquipo");
		}

		finally {
			if (conn != null) {
				conn.close();
			}
			if (stmt != null) {
				conn.close();
			}
		}
	}

	public void insertarJugador(Jugador jugador) throws SQLException {
		Connection conn = null;
		try {
			conn = new ProveedorConexiones().obtenerConexion();
			conn.setAutoCommit(false);
			insertarJugador(conn, jugador);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

	}

	private void insertarJugador(Connection conn, Jugador jugador) throws SQLException {
		PreparedStatement stmt = null;
		try {
			String sql = "insert into jugador values (?,?, ?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setInt(1, jugador.getNumero());
			stmt.setString(2, jugador.getCodigoEquipo());
			stmt.setString(3, jugador.getNombre());
			stmt.setDate(4, Date.valueOf(jugador.getFechaNacimiento()));
			stmt.execute();

		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

	}

	public void crearEquipo(Equipo equipo) throws SQLException, EquipoServiceException {
		try {
			List<Jugador> jugadores = equipo.getListaJugadores();
			for (Jugador jugador : jugadores) {
				jugador.setCodigoEquipo(equipo.getCodigo());
				jugador.getNumero();
				insertarJugador(jugador);
			}
			insertarEquipo(equipo);
		} catch (Exception e) {
			throw new EquipoServiceException();
		}

	}

	private void insertarEquipo(Connection conn, Equipo equipo) throws SQLException {
		PreparedStatement stmt = null;
		try {
			String sql = "insert into equipo values (?,?)";
			stmt = conn.prepareStatement(sql);
			stmt.setString(1, equipo.getCodigo());
			stmt.setString(2, equipo.getNombre());
			stmt.execute();
		} finally {
			if (stmt != null) {
				stmt.close();
			}
		}

	}

	public void insertarEquipo(Equipo equipo) throws SQLException {
		Connection conn = null;
		try {
			conn = new ProveedorConexiones().obtenerConexion();
			conn.setAutoCommit(false);
			insertarEquipo(conn, equipo);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

	}
	
	
	public void borrarEquipoCompleto(String codigo) throws SQLException, NotFoundException, EquipoServiceException {
			try {
		borrarJugadores(codigo);
			borrarEquipo(codigo);
			} catch (Exception e) {
				throw new EquipoServiceException("Ha ocurrido un error");
			}
			
			
	}
	
	private void borrarJugadores(String codigo, Connection conn) throws SQLException{
		
		PreparedStatement stmt= null;
		try {
		String sql="delete from jugador where codigo_equipo =?";
		stmt=conn.prepareStatement(sql);
		stmt.setString(1, codigo);
		stmt.execute();
		}finally {
			if(stmt !=null) {
				stmt.close();
			}
		}
	}
	
	public void borrarJugadores(String codigo) throws SQLException {
		Connection conn = null;
		try {
			conn = new ProveedorConexiones().obtenerConexion();
			conn.setAutoCommit(false);
			borrarJugadores(codigo,conn);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
		} finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	
	
	
	private void borrarEquipo(String codigo, Connection conn) throws SQLException, NotFoundException {
		PreparedStatement stmt= null;
		try {
		String sql="delete from equipo where codigo=?";
		stmt=conn.prepareStatement(sql);
		stmt.setString(1, codigo);
		if(stmt.executeUpdate() ==0) {
			throw new NotFoundException("No existe equipo con este codigo");
		}
		}finally {
			if(stmt != null) {
				stmt.close();
			}
		}
		
	}
	public void borrarEquipo(String codigo) throws SQLException, NotFoundException {
		Connection conn = null;
		try {
			conn = new ProveedorConexiones().obtenerConexion();
			conn.setAutoCommit(false);
			borrarEquipo(codigo,conn);
			conn.commit();
		} catch (SQLException e) {
			conn.rollback();
		} finally {
			if (conn != null) {
				conn.close();
			}
		}

	}
	
	public void añadirJugadorAEquipo(Equipo equipo, Jugador jugador) throws SQLException, EquipoServiceException {
		try {
		jugador.setCodigoEquipo(equipo.getCodigo());
		jugador.setNumero(consultarJugadoresEquipo(equipo.getCodigo()).size()+1);	
		insertarJugador(jugador);
		}catch (Exception e) {
			throw new EquipoServiceException();
		}
	}

	
	

}