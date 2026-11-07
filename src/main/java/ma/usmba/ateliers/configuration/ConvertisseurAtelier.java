package ma.usmba.ateliers.configuration;

import ma.usmba.ateliers.depot.AtelierDepot;
import ma.usmba.ateliers.modele.Atelier;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * Convertit l'identifiant envoye par la liste deroulante du formulaire
 * d'inscription en entite Atelier.
 */
@Component
public class ConvertisseurAtelier implements Converter<String, Atelier> {

    private final AtelierDepot atelierDepot;

    public ConvertisseurAtelier(AtelierDepot atelierDepot) {
        this.atelierDepot = atelierDepot;
    }

    @Override
    public Atelier convert(String source) {
        if (source == null || source.isBlank()) {
            return null;
        }
        try {
            return atelierDepot.findById(Long.valueOf(source.trim())).orElse(null);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
