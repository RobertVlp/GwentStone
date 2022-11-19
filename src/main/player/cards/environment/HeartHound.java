package main.player.cards.environment;

import main.game.Table;
import main.player.cards.Card;

import fileio.CardInput;

public final class HeartHound extends Environment {

    public HeartHound(final CardInput card) {
        super(card);
    }

    @Override
    public void castSpecialAbility(final int affectedRow) {
        int mirroredRow = (affectedRow - 3) * (-1);

        Card[][] cardMatrix = Table.getInstance().getCardMatrix();
        Card minion = cardMatrix[affectedRow][0];
        int maxHealth = minion.getHealth();
        int minionColumn = 0;

        for (int i = 1; i < Table.getInstance().getNumberOfColumns(); i++) {
            if (cardMatrix[affectedRow][i].getHealth() > maxHealth) {
                minion = cardMatrix[affectedRow][i];
                maxHealth = cardMatrix[affectedRow][i].getHealth();
                minionColumn = i;
            }
        }

        Table.getInstance().addCardOnRow(minion, mirroredRow);
        Table.getInstance().shiftLeft(affectedRow, minionColumn);
    }
}
