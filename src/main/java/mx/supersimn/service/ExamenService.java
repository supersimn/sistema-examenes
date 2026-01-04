package mx.supersimn.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import mx.supersimn.model.Alumno;
import mx.supersimn.model.ExamenProgreso;
import mx.supersimn.model.Pregunta;
import mx.supersimn.model.Respuesta;
import mx.supersimn.repository.ExamenProgresoRepository;
import mx.supersimn.repository.PreguntaRepository;
import mx.supersimn.repository.RespuestaRepository;

@Service
public class ExamenService {
	
	private static final Logger log = LoggerFactory.getLogger(ExamenService.class);

    @Autowired
    private PreguntaRepository preguntaRepo;
    
    @Autowired
    private ExamenProgresoRepository progresoRepo;
    
    @Autowired
    private RespuestaRepository respuestaRepo;

    @Transactional
    public List<ExamenProgreso> obtenerOGenerarExamen(Alumno alumno) {
    	
        // 1. Buscar si ya tiene progreso
        List<ExamenProgreso> progresoExistente = progresoRepo.findByAlumnoIdOrderByOrdenAsc(alumno.getId());
        
        if (!progresoExistente.isEmpty()) {
            return progresoExistente;
        }

        // 2. Si es nuevo, traer todas las preguntas
        List<Pregunta> todas = preguntaRepo.findAll();
        Collections.shuffle(todas); // Aleatoriedad

        // 3. Tomar las primeras 25 y guardarlas
        List<ExamenProgreso> nuevoProgreso = new ArrayList<>();
        for (int i = 0; i < Math.min(25, todas.size()); i++) {
            ExamenProgreso ep = new ExamenProgreso();
            ep.setAlumno(alumno);
            ep.setPregunta(todas.get(i));
            ep.setOrden(i + 1);
            nuevoProgreso.add(progresoRepo.save(ep));
        }
        
        return nuevoProgreso;
    }

    @Transactional
    public boolean registrarRespuesta(Long progresoId, Long respuestaId) {
        // 1. Buscamos el registro de progreso y la respuesta seleccionada
        ExamenProgreso progreso = progresoRepo.findById(progresoId).orElseThrow();
        Respuesta respuestaElegida = respuestaRepo.findById(respuestaId).orElseThrow();

        // 2. Guardamos la elección en la tabla de progreso
        progreso.setRespuestaSeleccionada(respuestaElegida);
        progreso.setRespondidaEn(LocalDateTime.now());
        progresoRepo.save(progreso);

        // 3. Retornamos si fue correcta comparando con el booleano de la tabla respuestas
        return respuestaElegida.isEsCorrecta();
    }
    
    @Transactional
    public void guardarRespuestaIndividual(Long progresoId, Long respuestaId) {
    	try {
    		ExamenProgreso progreso = progresoRepo.findById(progresoId)
                    .orElseThrow(() -> new RuntimeException("No se encontró el progreso con ID: " + progresoId));
            
            // Si respuestaId es null, significa que se le acabó el tiempo y no eligió nada
            if (respuestaId != null) {
                Respuesta res = respuestaRepo.findById(respuestaId).orElse(null);
                progreso.setRespuestaSeleccionada(res);
            }
            
            progreso.setRespondidaEn(LocalDateTime.now());
            progresoRepo.save(progreso);
    		
    	} catch (Exception e) {
            // Aquí capturamos el error de base de datos
            log.error("Fallo en la persistencia de respuesta para Progreso ID {}: {}", progresoId, e.getMessage());
            throw e; // Re-lanzamos para que el controlador también se entere
        }

    }
    
    public List<ExamenProgreso> obtenerProgresoReal(Alumno alumno) {
        return progresoRepo.findByAlumnoIdOrderByOrdenAsc(alumno.getId());
    }
}