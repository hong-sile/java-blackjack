package blackjack.domain.user;

import blackjack.domain.card.CardGroup;
import blackjack.domain.result.Score;

public class Player extends User {

    static final String NAME_IS_DEALER_NAME_EXCEPTION_MESSAGE = "플레이어의 이름은 '딜러'일 수 없습니다.";
    private static final int FIRST_OPEN_CARD_COUNT = 2;
    private static final double TIE_PROFIT_RATE = 0.0;
    private static final double BLACKJACK_PROFIT_RATE = 1.5;
    private static final double WIN_PROFIT_RATE = 1.0;
    private static final double LOSE_PROFIT_RATE = -WIN_PROFIT_RATE;

    protected Player(final String name, final CardGroup cardGroup) {
        super(name, cardGroup);
        validateIsDealerName(name);
    }

    private void validateIsDealerName(final String name) {
        if (Dealer.DEALER_NAME.equals(name)) {
            throw new IllegalArgumentException(NAME_IS_DEALER_NAME_EXCEPTION_MESSAGE);
        }
    }

    @Override
    public CardGroup getFirstOpenCardGroup() {
        return getCardGroups().getSubCardGroup(FIRST_OPEN_CARD_COUNT);
    }

    public boolean isSameName(final Name name) {
        return getName().isSame(name);
    }

    public boolean isDrawable() {
        return getScore().isDrawAble();
    }

    public Double calculateProfitRate(final Dealer dealer) {
        final Score playerScore = getScore();
        final Score dealerScore = dealer.getScore();
        if (playerScore.isBlackjack()) {
            return calculateProfitRateWhenPlayerIsBlackJack(dealerScore);
        }
        if (playerScore.isBust()) {
            return LOSE_PROFIT_RATE;
        }
        if (dealerScore.isBust()) {
            return WIN_PROFIT_RATE;
        }
        return calculateProfitRateByScoreValue(dealer);
    }

    private Double calculateProfitRateWhenPlayerIsBlackJack(final Score dealerScore) {
        if (dealerScore.isBlackjack()) {
            return TIE_PROFIT_RATE;
        }
        return BLACKJACK_PROFIT_RATE;
    }

    private Double calculateProfitRateByScoreValue(final Dealer dealer) {
        final Score playerScore = getScore();
        final Score dealerScore = dealer.getScore();

        if (playerScore.isBigger(dealerScore)) {
            return WIN_PROFIT_RATE;
        }
        if (playerScore.isEqual(dealerScore)) {
            return TIE_PROFIT_RATE;
        }
        return LOSE_PROFIT_RATE;
    }
}
