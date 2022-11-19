package main.player.cards.minions;

import fileio.CardInput;
import main.game.Table;
import main.player.cards.Card;

public final class Miraj extends Card {

    public Miraj(final CardInput card) {
        super(card);
    }

    @Override
    public void useSpecialAbility(final int xAttacker,
                                  final int yAttacker,
                                  final int xAttacked,
                                  final int yAttacked) {
        Card[][] cardMatrix = Table.getInstance().getCardMatrix();

        int attackedMinionHealth = cardMatrix[xAttacked][yAttacked].getHealth();
        int attackerMinionHealth = cardMatrix[xAttacker][yAttacker].getHealth();

        cardMatrix[xAttacker][yAttacker].setHealth(attackedMinionHealth);
        cardMatrix[xAttacked][yAttacked].setHealth(attackerMinionHealth);
    }
}
