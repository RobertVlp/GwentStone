package main.player.cards.minions;

import fileio.CardInput;
import main.game.Table;
import main.player.cards.Card;

public final class TheCursedOne extends Card {

    public TheCursedOne(final CardInput card) {
        super(card);
    }

    @Override
    public void useSpecialAbility(final int xAttacker,
                                  final int yAttacker,
                                  final int xAttacked,
                                  final int yAttacked) {
        Card[][] cardMatrix = Table.getInstance().getCardMatrix();

        int attackedMinionHealth = cardMatrix[xAttacked][yAttacked].getHealth();
        int attackedMinionDamage = cardMatrix[xAttacked][yAttacked].getAttackDamage();

        cardMatrix[xAttacked][yAttacked].setAttackDamage(attackedMinionHealth);
        cardMatrix[xAttacked][yAttacked].setHealth(attackedMinionDamage);
    }
}
