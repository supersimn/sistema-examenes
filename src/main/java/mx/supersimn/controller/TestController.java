package mx.supersimn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.supersimn.model.Alumno;
import mx.supersimn.repository.AlumnoRepository;

@RestController
public class TestController {

    @Autowired
    private AlumnoRepository alumnoRepository;

    @GetMapping("/test-db")
    public List<Alumno> test() {
        return alumnoRepository.findAll();
    }
}
