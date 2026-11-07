package ma.usmba.ateliers.service;

import ma.usmba.ateliers.depot.AtelierDepot;
import ma.usmba.ateliers.depot.ParticipantDepot;
import ma.usmba.ateliers.modele.Atelier;
import ma.usmba.ateliers.modele.Participant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ParticipantService {

    private final ParticipantDepot participantDepot;
    private final AtelierDepot atelierDepot;

    public ParticipantService(ParticipantDepot participantDepot, AtelierDepot atelierDepot) {
        this.participantDepot = participantDepot;
        this.atelierDepot = atelierDepot;
    }

    @Transactional(readOnly = true)
    public List<Participant> lister() {
        return participantDepot.findAllByOrderByDateInscriptionDesc();
    }

    @Transactional(readOnly = true)
    public List<Participant> rechercher(String terme) {
        if (terme == null || terme.isBlank()) {
            return lister();
        }
        String motif = terme.trim();
        return participantDepot
                .findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCaseOrEmailContainingIgnoreCaseOrEtablissementContainingIgnoreCase(
                        motif, motif, motif, motif);
    }

    @Transactional(readOnly = true)
    public Participant parId(Long id) {
        return participantDepot.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Participant introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<Participant> parAtelier(Long atelierId) {
        return participantDepot.findByAtelierIdOrderByDateInscriptionDesc(atelierId);
    }

    /**
     * Enregistre une inscription. Trois regles metier sont verifiees avant l'ecriture :
     * l'email doit etre libre, l'atelier doit exister et il doit rester de la place.
     */
    @Transactional
    public Participant inscrire(Participant participant) {
        String email = participant.getEmail() == null ? "" : participant.getEmail().trim().toLowerCase();
        participantDepot.findByEmailIgnoreCase(email).ifPresent(existant -> {
            if (!existant.getId().equals(participant.getId())) {
                throw new IllegalArgumentException("L'adresse " + email + " est deja inscrite a un atelier.");
            }
        });

        if (participant.getAtelier() == null || participant.getAtelier().getId() == null) {
            throw new IllegalArgumentException("Merci de choisir un atelier.");
        }

        Atelier atelier = atelierDepot.findById(participant.getAtelier().getId())
                .orElseThrow(() -> new IllegalArgumentException("Atelier introuvable."));

        long inscrits = atelierDepot.compterInscrits(atelier.getId());
        boolean dejaDansCetAtelier = participant.getId() != null
                && participantDepot.findById(participant.getId())
                        .map(p -> p.getAtelier().getId().equals(atelier.getId()))
                        .orElse(false);

        if (!dejaDansCetAtelier && inscrits >= atelier.getCapacite()) {
            throw new IllegalArgumentException("L'atelier " + atelier.getCode() + " est complet (" + inscrits + "/" + atelier.getCapacite() + ").");
        }

        participant.setEmail(email);
        participant.setAtelier(atelier);
        if (participant.getDateInscription() == null) {
            participant.setDateInscription(LocalDateTime.now());
        }
        return participantDepot.save(participant);
    }

    @Transactional
    public void supprimer(Long id) {
        participantDepot.deleteById(id);
    }

    @Transactional(readOnly = true)
    public long total() {
        return participantDepot.count();
    }

    @Transactional(readOnly = true)
    public List<Object[]> repartitionParEtablissement() {
        return participantDepot.compterParEtablissement();
    }
}
