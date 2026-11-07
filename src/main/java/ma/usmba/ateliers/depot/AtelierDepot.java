package ma.usmba.ateliers.depot;

import ma.usmba.ateliers.modele.Atelier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AtelierDepot extends JpaRepository<Atelier, Long> {

    Optional<Atelier> findByCodeIgnoreCase(String code);

    List<Atelier> findByIntituleContainingIgnoreCaseOrderByDateSeanceAsc(String intitule);

    List<Atelier> findAllByOrderByDateSeanceAsc();

    @Query("select count(p) from Participant p where p.atelier.id = ?1")
    long compterInscrits(Long atelierId);
}
