package mx.supersimn.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "examen_progreso")

public class ExamenProgreso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    private Alumno alumno;
    
    @ManyToOne
    private Pregunta pregunta;
    
    @ManyToOne
    @JoinColumn(name = "respuesta_id")
    private Respuesta respuestaSeleccionada; // Será null hasta que el alumno responda
    
    private Integer orden; // Para el shuffle aleatorio
    
    private LocalDateTime respondidaEn; 

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Alumno getAlumno() {
		return alumno;
	}

	public void setAlumno(Alumno alumno) {
		this.alumno = alumno;
	}

	public Pregunta getPregunta() {
		return pregunta;
	}

	public void setPregunta(Pregunta pregunta) {
		this.pregunta = pregunta;
	}

	public Respuesta getRespuestaSeleccionada() {
		return respuestaSeleccionada;
	}

	public void setRespuestaSeleccionada(Respuesta respuestaSeleccionada) {
		this.respuestaSeleccionada = respuestaSeleccionada;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public LocalDateTime getRespondidaEn() {
		return respondidaEn;
	}

	public void setRespondidaEn(LocalDateTime respondidaEn) {
		this.respondidaEn = respondidaEn;
	}
    
	
    
}