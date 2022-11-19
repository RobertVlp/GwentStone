package main.player.cards.minions;

import fileio.CardInput;
import main.game.Table;
import main.player.cards.Card;

public final class Disciple extends Card {

    public Disciple(final CardInput card) {
        super(card);
    }

    @Override
    public void useSpecialAbility(final int xAttacker,
                                  final int yAttacker,
                                  final int xAttacked,
                                  final int yAttacked) {
        Card[][] cardMatrix = Table.getInstance().getCardMatrix();

        int attackedCardHealth = cardMatrix[xAttacked][yAttacked].getHealth();

        cardMatrix[xAttacked][yAttacked].setHealth(attackedCardHealth + 2);
    }
}
