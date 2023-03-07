package blackjack.view;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toUnmodifiableList;

import blackjack.domain.card.CardGroup;
import blackjack.domain.card.CardNumber;
import blackjack.domain.card.CardShape;
import blackjack.domain.result.CardResult;
import blackjack.domain.result.WinningStatus;
import blackjack.domain.user.Dealer;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            WinningStatus.LOSE, "패"
    );
    private static final String CARD_RESULT_FORMAT = "%s - 결과: %d";
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

    public static Map<String, List<String>> renderStatus(final Map<String, CardGroup> status) {
        final Map<String, List<String>> renderedStatus = new LinkedHashMap<>();
        status.forEach((name, cardGroup) -> renderedStatus.put(name, renderCardsToString(cardGroup)));
        return Collections.unmodifiableMap(renderedStatus);
    }

    public static List<String> renderCardsToString(final CardGroup cardGroup) {
        return cardGroup.getCards().stream()
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

    public static Map<String, String> renderUserNameAndCardResults(
            final Map<String, CardResult> userNameAndCardResults) {
        final Map<String, String> renderedUserNameAndCardResults = new LinkedHashMap<>();

        userNameAndCardResults
                .forEach((key, value) -> renderedUserNameAndCardResults.put(key, renderCardResults(value)));

        return renderedUserNameAndCardResults;
    }

    private static String renderCardResults(final CardResult cardResult) {
        final List<String> cardNames = renderCardsToString(cardResult.getCards());
        return String.format(CARD_RESULT_FORMAT, String.join(", ", cardNames)
                , cardResult.getScore().getValue());
    }
}
