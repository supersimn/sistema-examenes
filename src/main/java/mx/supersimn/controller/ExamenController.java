package mx.supersimn.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import mx.supersimn.model.Alumno;
import mx.supersimn.model.ExamenProgreso;
import mx.supersimn.service.ExamenService;

@Controller
public class ExamenController {
	
	// Definimos el logger para esta clase
    private static final Logger log = LoggerFactory.getLogger(ExamenController.class);

    @Autowired
    private ExamenService examenService;

    @GetMapping("/examen")
    public String mostrarExamen(HttpSession session, Model model) {
        try {
            Alumno alumno = (Alumno) session.getAttribute("alumnoLogueado");
            if (alumno == null) return "redirect:/";

            List<ExamenProgreso> progreso = examenService.obtenerOGenerarExamen(alumno);
            long respondidas = progreso.stream()
                                       .filter(p -> p.getRespuestaSeleccionada() != null)
                                       .count();

            if (respondidas == progreso.size()) {
                int aciertos = (int) progreso.stream()
                                             .filter(p -> p.getRespuestaSeleccionada() != null && 
                                                          p.getRespuestaSeleccionada().isEsCorrecta())
                                             .count();
                
                double calificacion = (aciertos * 10.0) / progreso.size();

                model.addAttribute("alumno", alumno);
                model.addAttribute("aciertos", aciertos);
                model.addAttribute("calificacion", calificacion);
                model.addAttribute("error", "Ya has completado este examen.");
                return "resultado"; 
            }

            int indiceActual = 0;
            for (int i = 0; i < progreso.size(); i++) {
                if (progreso.get(i).getRespuestaSeleccionada() == null) {
                    indiceActual = i;
                    break;
                }
            }

            model.addAttribute("alumno", alumno);
            model.addAttribute("preguntas", progreso);
            model.addAttribute("indiceInicial", indiceActual);
            
            return "examen";

        } catch (Exception e) {
            // Logueamos el error con el objeto excepción para ver el stacktrace completo
            log.error("Error al cargar el examen para el alumno: {}", e.getMessage(), e);
            return "error"; // Te mandará a la página de error personalizada que creamos
        }
    }
    
    @PostMapping("/examen/guardar-paso")
    @ResponseBody
    public ResponseEntity<String> guardarPaso(@RequestParam Long progresoId, 
                                            @RequestParam(required = false) Long respuestaId) {
        try {
            examenService.guardarRespuestaIndividual(progresoId, respuestaId);
            return ResponseEntity.ok("Guardado");
        } catch (Exception e) {
            // Error en segundo plano (Fetch), lo logueamos pero no rompemos la vista
            log.error("Error guardando progreso individual (ID: {}): {}", progresoId, e.getMessage());
            return ResponseEntity.status(500).body("Error al guardar paso");
        }
    }
    
    @GetMapping("/examen/finalizar-y-calcular")
    public String finalizarYCalcular(HttpSession session, Model model) {
        try {
            Alumno alumno = (Alumno) session.getAttribute("alumnoLogueado");
            if (alumno == null) return "redirect:/";

            List<ExamenProgreso> progresoCompleto = examenService.obtenerProgresoReal(alumno);

            int aciertos = (int) progresoCompleto.stream()
                    .filter(p -> p.getRespuestaSeleccionada() != null && 
                                 p.getRespuestaSeleccionada().isEsCorrecta())
                    .count();

            double calificacion = (aciertos * 10.0) / progresoCompleto.size();

            model.addAttribute("aciertos", aciertos);
            model.addAttribute("calificacion", calificacion);
            model.addAttribute("alumno", alumno);

            return "resultado";
        } catch (Exception e) {
            log.error("Error crítico al calcular resultados finales: {}", e.getMessage(), e);
            return "error";
        }
    }
}