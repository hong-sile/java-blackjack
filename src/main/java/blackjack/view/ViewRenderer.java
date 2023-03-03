package blackjack.view;

import blackjack.domain.Card;
import blackjack.domain.CardNumber;
import blackjack.domain.CardShape;
import blackjack.domain.Dealer;
import blackjack.domain.WinningStatus;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toUnmodifiableList;

public class ViewRenderer {

    private static final Map<CardShape, String> CARD_SHAPE_STRING_MAPPER = Map.of(
            CardShape.SPADE, "스페이드",
            CardShape.CLOVER, "클로버",
            CardShape.DIAMOND, "다이아몬드",
            CardShape.HEART, "하트"
    );
    private static final Map<CardNumber, String> CARD_NUMBER_STRING_MAPPER = new EnumMap<>(CardNumber.class);
    private static final Map<WinningStatus, String> WINNING_STATUS_MAPPER = Map.of(
            WinningStatus.WIN, "승 ",
            WinningStatus.TIE, "무 ",
            WinningStatus.LOSE, "패 "
    );
    private static final String BLANK = "";

    static {
        CARD_NUMBER_STRING_MAPPER.put(CardNumber.ACE, "A");
        CARD_NUMBER_STRING_MAPPER.put(CardNumber.TWO, "2");
        CARD_NUMBER_STRING_MAPPER.put(CardNumber.THREE, "3");
        CARD_NUMBER_STRING_MAPPER.put(CardNumber.FOUR, "4");
        CARD_NUMBER_STRING_MAPPER.put(CardNumber.FIVE, "5");
        CARD_NUMBER_STRING_MAPPER.put(CardNumber.SIX, "6");
        CARD_NUMBER_STRING_MAPPER.put(CardNumber.SEVEN, "7");
        CARD_NUMBER_STRING_MAPPER.put(CardNumber.EIGHT, "8");
        CARD_NUMBER_STRING_MAPPER.put(CardNumber.NINE, "9");
        CARD_NUMBER_STRING_MAPPER.put(CardNumber.TEN, "10");
        CARD_NUMBER_STRING_MAPPER.put(CardNumber.JACK, "J");
        CARD_NUMBER_STRING_MAPPER.put(CardNumber.QUEEN, "Q");
        CARD_NUMBER_STRING_MAPPER.put(CardNumber.KING, "K");
    }

    private ViewRenderer() {
    }

    public static Map<String, List<String>> renderStatus(final Map<String, List<Card>> status) {
        final Map<String, List<String>> renderedStatus = new HashMap<>();

        for (final String name : status.keySet()) {
            renderedStatus.put(name, renderCardsToString(status.get(name)));
        }
        return renderedStatus;
    }

    public static List<String> renderCardsToString(final List<Card> cards) {
        return cards.stream()
                .map(card -> CARD_NUMBER_STRING_MAPPER.get(card.getNumber())
                        + CARD_SHAPE_STRING_MAPPER.get(card.getShape()))
                .collect(toUnmodifiableList());
    }

    public static Map<String, String> renderWinningResult(Map<String, WinningStatus> winningResult) {
        Map<String, String> renderedWinningResult = new LinkedHashMap<>();
        Map<WinningStatus, Long> dealerWinningResult = winningResult.values().stream()
                .collect(groupingBy(ViewRenderer::recursionWinningStatus, counting()));

        renderedWinningResult.put(Dealer.DEALER_NAME, renderDealerWinningResult(dealerWinningResult));

        for (String name : winningResult.keySet()) {
            renderedWinningResult.put(name, WINNING_STATUS_MAPPER.get(winningResult.get(name)));
        }

        return renderedWinningResult;
    }

    private static WinningStatus recursionWinningStatus(WinningStatus winningStatus) {
        if (winningStatus == WinningStatus.WIN) {
            return WinningStatus.LOSE;
        }
        if (winningStatus == WinningStatus.LOSE) {
            return WinningStatus.WIN;
        }
        return winningStatus;
    }

    private static String renderDealerWinningResult(Map<WinningStatus, Long> dealerWinningResult) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(renderWinningStatus(WinningStatus.WIN, dealerWinningResult));
        stringBuilder.append(renderWinningStatus(WinningStatus.TIE, dealerWinningResult));
        stringBuilder.append(renderWinningStatus(WinningStatus.LOSE, dealerWinningResult));
        return stringBuilder.toString();
    }

    private static String renderWinningStatus(final WinningStatus winningStatus
            , final Map<WinningStatus, Long> dealerWinningResult) {
        if (dealerWinningResult.containsKey(winningStatus)) {
            return dealerWinningResult.get(winningStatus) + WINNING_STATUS_MAPPER.get(winningStatus);
        }
        return BLANK;
    }
}
