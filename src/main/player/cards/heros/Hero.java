package main.player.cards.heros;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import fileio.CardInput;
import main.player.cards.Card;

public  class Hero extends Card {

    public Hero(CardInput card) {
        super(card);
    }
    
    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return "{\"mana\":" + this.getMana() + ",\"description\":\"" + this.getDescription() + "\",\"colors\":"
                + objectMapper.writeValueAsString(this.getColors()) + ",\"name\":\"" + this.getName() + "\",\"health\":" + this.getHealth() + "}";
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    } 
}
