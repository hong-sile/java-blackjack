package blackjack.controller;

import blackjack.domain.BlackJackGame;
import blackjack.domain.card.CardGroup;
import blackjack.domain.card.RandomDeckGenerator;
import blackjack.domain.result.CardResult;
import blackjack.domain.result.WinningStatus;
import blackjack.view.InputView;
import blackjack.view.OutputView;
import blackjack.view.ViewRenderer;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BlackJackController {

    private final InputView inputView = new InputView();
    private final OutputView outputView = new OutputView();

    public void run() {
        final BlackJackGame blackJackGame = initBlackJackGame();
        printFirstOpenCardGroups(blackJackGame);
        playPlayersTurn(blackJackGame);
        playDealerTurn(blackJackGame);
        printUserNameAndCardResults(blackJackGame);
        printUserWinningResults(blackJackGame);
    }

    private BlackJackGame initBlackJackGame() {
        try {
            outputView.printPlayerNameRequestMessage();
            final List<String> playerNames = inputView.readPlayerNames();
            return new BlackJackGame(playerNames, new RandomDeckGenerator());
        } catch (IllegalArgumentException e) {
            outputView.printExceptionMessage(e);
            return initBlackJackGame();
        }
    }

    private void printFirstOpenCardGroups(final BlackJackGame blackJackGame) {
        final Map<String, CardGroup> firstOpenCardGroups = blackJackGame.getFirstOpenCardGroups();
        final List<String> userNames = firstOpenCardGroups.keySet()
                .stream()
                .collect(Collectors.toUnmodifiableList());
        outputView.printFirstCardGroupInfoMessage(userNames);
        for (final String name : firstOpenCardGroups.keySet()) {
            final List<String> renderedCardGroup = ViewRenderer.renderCardGroup(firstOpenCardGroups.get(name));
            outputView.printCardGroup(name, renderedCardGroup);
        }
    }

    private void playPlayersTurn(final BlackJackGame blackJackGame) {
        final List<String> playerNames = blackJackGame.getPlayerNames();
        for (final String playerName : playerNames) {
            playPlayerTurn(blackJackGame, playerName);
        }
    }

    private void playPlayerTurn(final BlackJackGame blackJackGame, final String playerName) {
        DrawOrStay drawOrStay = DrawOrStay.DRAW;
        while (drawOrStay.isDraw() && isContinuous(playerName, blackJackGame)) {
            outputView.printDrawCardRequestMessage(playerName);
            drawOrStay = repeatUntilReadValidateDrawInput();
            blackJackGame.playPlayer(playerName, drawOrStay);
            final CardGroup userCardGroup = blackJackGame.getCardGroupBy(playerName);
            outputView.printCardGroup(playerName, ViewRenderer.renderCardGroup(userCardGroup));
        }
    }

    private DrawOrStay repeatUntilReadValidateDrawInput() {
        try {
            return DrawOrStay.from(inputView.readDrawOrStay());
        } catch (IllegalArgumentException e) {
            outputView.printExceptionMessage(e);
            return repeatUntilReadValidateDrawInput();
        }
    }

    private boolean isContinuous(final String playerName, final BlackJackGame blackJackGame) {
        if (blackJackGame.isBlackJackScore(playerName) || blackJackGame.isPlayerBust(playerName)) {
            return false;
        }
        return true;
    }

    private void playDealerTurn(BlackJackGame blackJackGame) {
        while (blackJackGame.shouldDealerDraw()) {
            outputView.printDealerDrawInfoMessage();
            blackJackGame.drawDealer();
        }
    }

    private void printUserNameAndCardResults(BlackJackGame blackJackGame) {
        final Map<String, CardResult> userNameAndCardResults = blackJackGame.getUserNameAndCardResults();
        final Map<String, String> renderedUserNameAndCardResults = ViewRenderer
                .renderUserNameAndCardResults(userNameAndCardResults);
        outputView.printUserNameAndCardResults(renderedUserNameAndCardResults);
    }

    private void printUserWinningResults(final BlackJackGame blackJackGame) {
        final Map<WinningStatus, Long> dealerWinningResult = blackJackGame.getDealerWinningResult();
        outputView.printDealerWinningResult(ViewRenderer.renderDealerWinningResult(dealerWinningResult));
        final Map<String, WinningStatus> playersWinningResult = blackJackGame.getPlayersWinningResults();
        outputView.printPlayersWinningResults(ViewRenderer.renderPlayersWinningResults(playersWinningResult));
    }
}
