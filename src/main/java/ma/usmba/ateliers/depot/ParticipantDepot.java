package ma.usmba.ateliers.depot;

import ma.usmba.ateliers.modele.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipantDepot extends JpaRepository<Participant, Long> {

    Optional<Participant> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    List<Participant> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrEtablissementContainingIgnoreCase(
            String nom, String prenom, String email, String etablissement);

    List<Participant> findByAtelierIdOrderByDateInscriptionDesc(Long atelierId);

    List<Participant> findAllByOrderByDateInscriptionDesc();

    @Query("select p.etablissement, count(p) from Participant p group by p.etablissement order by count(p) desc")
    List<Object[]> compterParEtablissement();
}
