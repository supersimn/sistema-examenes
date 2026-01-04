package mx.supersimn.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import mx.supersimn.model.Alumno;
import mx.supersimn.repository.AlumnoRepository;

@Controller
public class LoginController {

    @Autowired
    private AlumnoRepository alumnoRepository;


    @GetMapping("/")
    public String mostrarLogin() {
        return "login"; // busca login.html en templates
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String boleta, 
                               @RequestParam String password, 
                               HttpSession session, 
                               Model model) {
        
        // Buscamos al alumno por boleta
        // Nota: En un entorno real usarías un Optional y BCrypt para el password
        Alumno alumno = alumnoRepository.findByBoleta(boleta);

        if (alumno != null && alumno.getPassword().equals(password)) {
            session.setAttribute("alumnoLogueado", alumno);
            return "redirect:/examen"; // Te mandará aquí si todo ok
        } else {
            model.addAttribute("error", "Boleta o contraseña incorrectos");
            return "login";
        }
    }
}