package main.player.cards.heroes;

import fileio.CardInput;
import main.game.Table;
import main.player.cards.Card;

public final class LordRoyce extends Hero {

    public LordRoyce(final CardInput card) {
        super(card);
    }

    @Override
    public void useHeroAbility(final int affectedRow) {
        Card[][] cardMatrix = Table.getInstance().getCardMatrix();
        int maxAttackDamage = 0;
        int maxAttackDamageCardColumn = 0;

        for (int i = 0; i < Table.getInstance().getNumberOfColumns(); i++) {
            if (cardMatrix[affectedRow][i] != null) {
                if (cardMatrix[affectedRow][i].getAttackDamage() > maxAttackDamage) {
                    maxAttackDamage = cardMatrix[affectedRow][i].getAttackDamage();
                    maxAttackDamageCardColumn = i;
                }
            }
        }

        cardMatrix[affectedRow][maxAttackDamageCardColumn].setFrozen(true);
    }
}
