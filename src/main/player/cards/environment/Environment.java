package main.player.cards.environment;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import fileio.CardInput;
import main.player.cards.Card;

@JsonIgnoreProperties({"attackDamage", "health"})
public class Environment extends Card {

    public Environment(CardInput card) {
        super(card);
    }
    
}
