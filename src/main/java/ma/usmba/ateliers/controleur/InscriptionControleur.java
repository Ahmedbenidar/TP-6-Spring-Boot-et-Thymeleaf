package ma.usmba.ateliers.controleur;

import ma.usmba.ateliers.modele.Atelier;
import ma.usmba.ateliers.modele.Participant;
import ma.usmba.ateliers.service.AtelierService;
import ma.usmba.ateliers.service.ParticipantService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class InscriptionControleur {

    private final ParticipantService participantService;
    private final AtelierService atelierService;

    public InscriptionControleur(ParticipantService participantService, AtelierService atelierService) {
        this.participantService = participantService;
        this.atelierService = atelierService;
    }

    @GetMapping("/")
    public String tableauDeBord(Model modele) {
        List<Atelier> ateliers = atelierService.lister();
        Map<Long, Long> occupation = new HashMap<>();
        for (Atelier atelier : ateliers) {
            occupation.put(atelier.getId(), atelierService.compterInscrits(atelier.getId()));
        }
        modele.addAttribute("totalInscrits", participantService.total());
        modele.addAttribute("ateliers", ateliers);
        modele.addAttribute("occupation", occupation);
        modele.addAttribute("repartition", participantService.repartitionParEtablissement());
        modele.addAttribute("dernieres", participantService.lister().stream().limit(5).toList());
        return "tableau-de-bord";
    }

    @GetMapping("/inscriptions")
    public String lister(@RequestParam(name = "terme", required = false) String terme,
                         @RequestParam(name = "atelierId", required = false) Long atelierId,
                         Model modele) {
        if (atelierId != null) {
            modele.addAttribute("inscriptions", participantService.parAtelier(atelierId));
            modele.addAttribute("atelierFiltre", atelierService.parId(atelierId));
        } else {
            modele.addAttribute("inscriptions", participantService.rechercher(terme));
        }
        modele.addAttribute("terme", terme == null ? "" : terme);
        modele.addAttribute("ateliers", atelierService.lister());
        ajouterOccupation(modele);
        return "liste-inscriptions";
    }

    @GetMapping("/inscriptions/nouvelle")
    public String formulaireNouvelle(@RequestParam(name = "atelierId", required = false) Long atelierId,
                                     Model modele) {
        Participant participant = new Participant();
        if (atelierId != null) {
            participant.setAtelier(atelierService.parId(atelierId));
        }
        modele.addAttribute("participant", participant);
        modele.addAttribute("ateliers", atelierService.lister());
        ajouterOccupation(modele);
        return "formulaire-inscription";
    }

    @PostMapping("/inscriptions")
    public String enregistrer(@Valid @ModelAttribute("participant") Participant participant,
                              BindingResult resultat,
                              Model modele,
                              RedirectAttributes flash) {
        if (resultat.hasErrors()) {
            modele.addAttribute("ateliers", atelierService.lister());
            ajouterOccupation(modele);
            return "formulaire-inscription";
        }
        try {
            participantService.inscrire(participant);
        } catch (IllegalArgumentException ex) {
            modele.addAttribute("erreurMetier", ex.getMessage());
            modele.addAttribute("ateliers", atelierService.lister());
            ajouterOccupation(modele);
            return "formulaire-inscription";
        }
        flash.addFlashAttribute("succes",
                "L'inscription de " + participant.getNomComplet() + " a bien ete enregistree.");
        return "redirect:/inscriptions";
    }

    @PostMapping("/inscriptions/{id}/supprimer")
    public String supprimer(@PathVariable Long id, RedirectAttributes flash) {
        Participant participant = participantService.parId(id);
        participantService.supprimer(id);
        flash.addFlashAttribute("succes",
                "L'inscription de " + participant.getNomComplet() + " a ete annulee.");
        return "redirect:/inscriptions";
    }

    private void ajouterOccupation(Model modele) {
        Map<Long, Long> occupation = new HashMap<>();
        for (Atelier atelier : atelierService.lister()) {
            occupation.put(atelier.getId(), atelierService.compterInscrits(atelier.getId()));
        }
        modele.addAttribute("occupation", occupation);
    }
}
