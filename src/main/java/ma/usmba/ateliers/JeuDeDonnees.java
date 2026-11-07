package ma.usmba.ateliers;

import ma.usmba.ateliers.depot.AtelierDepot;
import ma.usmba.ateliers.depot.ParticipantDepot;
import ma.usmba.ateliers.modele.Atelier;
import ma.usmba.ateliers.modele.Participant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

/**
 * Alimente la base au premier demarrage pour que les pages ne soient pas vides.
 */
@Component
public class JeuDeDonnees implements CommandLineRunner {

    private static final Logger journal = LoggerFactory.getLogger(JeuDeDonnees.class);

    private final AtelierDepot atelierDepot;
    private final ParticipantDepot participantDepot;

    public JeuDeDonnees(AtelierDepot atelierDepot, ParticipantDepot participantDepot) {
        this.atelierDepot = atelierDepot;
        this.participantDepot = participantDepot;
    }

    @Override
    public void run(String... args) {
        if (atelierDepot.count() > 0) {
            journal.info("Base deja alimentee : {} ateliers, {} inscriptions",
                    atelierDepot.count(), participantDepot.count());
            return;
        }

        Atelier impression = atelierDepot.save(new Atelier(
                "IMP3D", "Impression 3D et modelisation parametrique",
                "Pr. Hicham Belkacem", 12, LocalDate.now().plusDays(6)));
        Atelier laser = atelierDepot.save(new Atelier(
                "LASER", "Decoupe et gravure laser sur bois",
                "Mme Souad Rifai", 10, LocalDate.now().plusDays(9)));
        Atelier iot = atelierDepot.save(new Atelier(
                "IOT", "Capteurs connectes et objets intelligents",
                "Dr. Anas Lahlou", 16, LocalDate.now().plusDays(13)));
        Atelier cao = atelierDepot.save(new Atelier(
                "CAO", "Conception assistee par ordinateur avec FreeCAD",
                "Pr. Nabil Choukri", 8, LocalDate.now().plusDays(20)));

        List<Participant> inscriptions = List.of(
                new Participant("Ilyas", "Bennani", "i.bennani@usmba.ac.ma", "Faculte des Sciences", "0612345678", impression),
                new Participant("Sara", "El Idrissi", "s.elidrissi@usmba.ac.ma", "Faculte des Sciences", "0623456789", impression),
                new Participant("Mehdi", "Ouazzani", "m.ouazzani@usmba.ac.ma", "ENSA Fes", "0634567890", laser),
                new Participant("Khadija", "Tahiri", "k.tahiri@usmba.ac.ma", "Faculte de Medecine", "0645678901", iot),
                new Participant("Anas", "Berrada", "a.berrada@usmba.ac.ma", "ENSA Fes", "0656789012", laser),
                new Participant("Lina", "Cherkaoui", "l.cherkaoui@usmba.ac.ma", "Faculte des Sciences", "0667890123", cao));
        participantDepot.saveAll(inscriptions);

        journal.info("Jeu de donnees initialise : {} ateliers, {} inscriptions",
                atelierDepot.count(), participantDepot.count());
    }
}
