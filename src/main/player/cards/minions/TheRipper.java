package main.player.cards.minions;

import fileio.CardInput;
import main.game.Table;
import main.player.cards.Card;

public final class TheRipper extends Card {

    public TheRipper(final CardInput card) {
        super(card);
    }

    @Override
    public void useSpecialAbility(final int xAttacker,
                                  final int yAttacker,
                                  final int xAttacked,
                                  final int yAttacked) {
        Card[][] cardMatrix = Table.getInstance().getCardMatrix();
        int attackDamage = cardMatrix[xAttacked][yAttacked].getAttackDamage();

        if (attackDamage >= 2) {
            cardMatrix[xAttacked][yAttacked].setAttackDamage(attackDamage - 2);
        }
    }
}
