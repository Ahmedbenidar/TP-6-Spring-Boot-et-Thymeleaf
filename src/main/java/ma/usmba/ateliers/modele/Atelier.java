package ma.usmba.ateliers.modele;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Une session d'atelier proposee par le MakerLab : impression 3D, decoupe laser,
 * prototypage electronique. Chaque participant s'inscrit a un atelier et un seul.
 */
@Entity
@Table(name = "ateliers")
public class Atelier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le code de l'atelier est obligatoire")
    @Size(min = 3, max = 12, message = "Le code doit contenir entre 3 et 12 caracteres")
    @Column(nullable = false, unique = true, length = 12)
    private String code;

    @NotBlank(message = "L'intitule est obligatoire")
    @Size(max = 120, message = "L'intitule ne peut pas depasser 120 caracteres")
    @Column(nullable = false, length = 120)
    private String intitule;

    @NotBlank(message = "L'animateur est obligatoire")
    @Column(nullable = false, length = 80)
    private String animateur;

    @NotNull(message = "La capacite est obligatoire")
    @Min(value = 4, message = "La capacite minimale est de 4 places")
    @Max(value = 60, message = "La capacite maximale est de 60 places")
    @Column(nullable = false)
    private Integer capacite;

    @NotNull(message = "La date de seance est obligatoire")
    @Column(nullable = false)
    private LocalDate dateSeance;

    @OneToMany(mappedBy = "atelier", cascade = CascadeType.REMOVE, orphanRemoval = false)
    private List<Participant> participants = new ArrayList<>();

    public Atelier() {
    }

    public Atelier(String code, String intitule, String animateur, Integer capacite, LocalDate dateSeance) {
        this.code = code;
        this.intitule = intitule;
        this.animateur = animateur;
        this.capacite = capacite;
        this.dateSeance = dateSeance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIntitule() {
        return intitule;
    }

    public void setIntitule(String intitule) {
        this.intitule = intitule;
    }

    public String getAnimateur() {
        return animateur;
    }

    public void setAnimateur(String animateur) {
        this.animateur = animateur;
    }

    public Integer getCapacite() {
        return capacite;
    }

    public void setCapacite(Integer capacite) {
        this.capacite = capacite;
    }

    public LocalDate getDateSeance() {
        return dateSeance;
    }

    public void setDateSeance(LocalDate dateSeance) {
        this.dateSeance = dateSeance;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    @Override
    public String toString() {
        return code + " — " + intitule;
    }
}
