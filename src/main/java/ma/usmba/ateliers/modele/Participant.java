package ma.usmba.ateliers.modele;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * Demande d'inscription d'un etudiant a un atelier du MakerLab.
 */
@Entity
@Table(name = "participants")
public class Participant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le prenom est obligatoire")
    @Size(max = 60, message = "Le prenom ne peut pas depasser 60 caracteres")
    @Column(nullable = false, length = 60)
    private String prenom;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 60, message = "Le nom ne peut pas depasser 60 caracteres")
    @Column(nullable = false, length = 60)
    private String nom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "L'email doit etre une adresse valide")
    @Column(nullable = false, unique = true, length = 120)
    private String email;

    @NotBlank(message = "L'etablissement est obligatoire")
    @Column(nullable = false, length = 100)
    private String etablissement;

    @NotBlank(message = "Le numero de telephone est obligatoire")
    @Pattern(regexp = "0[5-7][0-9]{8}", message = "Le telephone doit contenir 10 chiffres et commencer par 05, 06 ou 07")
    @Column(nullable = false, length = 10)
    private String telephone;

    @NotNull(message = "L'atelier est obligatoire")
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "atelier_id", nullable = false)
    private Atelier atelier;

    @Column(nullable = false)
    private LocalDateTime dateInscription;

    public Participant() {
    }

    public Participant(String prenom, String nom, String email, String etablissement, String telephone, Atelier atelier) {
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.etablissement = etablissement;
        this.telephone = telephone;
        this.atelier = atelier;
        this.dateInscription = LocalDateTime.now();
    }

    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEtablissement() {
        return etablissement;
    }

    public void setEtablissement(String etablissement) {
        this.etablissement = etablissement;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public Atelier getAtelier() {
        return atelier;
    }

    public void setAtelier(Atelier atelier) {
        this.atelier = atelier;
    }

    public LocalDateTime getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDateTime dateInscription) {
        this.dateInscription = dateInscription;
    }

    @Override
    public String toString() {
        return getNomComplet() + " (" + email + ")";
    }
}
