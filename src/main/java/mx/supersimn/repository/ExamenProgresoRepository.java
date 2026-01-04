package mx.supersimn.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.supersimn.model.ExamenProgreso;

public interface ExamenProgresoRepository extends JpaRepository<ExamenProgreso, Long> {
	
	List<ExamenProgreso> findByAlumnoIdOrderByOrdenAsc(Long alumnoId);

}
