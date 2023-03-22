package blackjack.domain.money;

import blackjack.domain.user.Name;
import java.util.HashMap;
import java.util.Map;

public class Deposit {

    private final Map<Name, BettingMoney> deposit = new HashMap<>();

    public void bet(final Name playerName, final BettingMoney money) {
        deposit.put(playerName, money);
    }

    public Money getProfit(final Name playerName, final Double profitRate) {
        return deposit.get(playerName).multiply(profitRate);
    }
}
