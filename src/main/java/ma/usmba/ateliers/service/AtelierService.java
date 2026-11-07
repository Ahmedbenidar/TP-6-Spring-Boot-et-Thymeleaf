package ma.usmba.ateliers.service;

import ma.usmba.ateliers.depot.AtelierDepot;
import ma.usmba.ateliers.modele.Atelier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AtelierService {

    private final AtelierDepot atelierDepot;

    public AtelierService(AtelierDepot atelierDepot) {
        this.atelierDepot = atelierDepot;
    }

    @Transactional(readOnly = true)
    public List<Atelier> lister() {
        return atelierDepot.findAllByOrderByDateSeanceAsc();
    }

    @Transactional(readOnly = true)
    public Atelier parId(Long id) {
        return atelierDepot.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Atelier introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public long compterInscrits(Long atelierId) {
        return atelierDepot.compterInscrits(atelierId);
    }

    @Transactional(readOnly = true)
    public boolean placesDisponibles(Long atelierId) {
        Atelier atelier = parId(atelierId);
        return atelierDepot.compterInscrits(atelierId) < atelier.getCapacite();
    }

    @Transactional
    public Atelier enregistrer(Atelier atelier) {
        atelierDepot.findByCodeIgnoreCase(atelier.getCode()).ifPresent(existant -> {
            if (!existant.getId().equals(atelier.getId())) {
                throw new IllegalArgumentException("Le code " + atelier.getCode() + " est deja utilise par un autre atelier.");
            }
        });
        return atelierDepot.save(atelier);
    }
}
