#### domain

- [x] 게임을 위한 카드 덱 생성
- [x] 카드 덱 셔플
- [x] 카드 덱에서 카드 한장 뽑기
- [x] 카드마다 정해진 문양과, 숫자를 가진다
    - [x] Ace는 1또는 11로 가진다
    - [x] King, Queen, Jack은 10을 숫자로 가진다
    - [x] 같은 종류의 카드는 중복일 수 없다
- [x] 플레이어 등록
    - [x] validation: 공백 이름이 입력되었는가
    - [x] validation: 플레이어가 하나도 입력되지 않는 경우
    - [x] validation: NPE를 방지하기 위해 플레이어 수를 6명으로 제한
- [x] 게임을 시작하면 플레이와 딜러에게 2장씩의 카드를 지급한다
- [x] 플레이어와 딜러가 가진 패의 숫자합을 계산한다
- [x] 플레이어 게임 진행
    - [x] 21을 넘지 않을 경우 원한다면 얼마든지 카드를 계속 뽑을 수 있다
- [x] 딜러 게임 진행
    - [x] 딜러의 숫자 합이 16이하이면 반드시 1장의 카드를 추가로 받고, 17이상이면 추가로 받지 않는다
- [x] 카드 숫자를 합쳐 21을 초과하지 않으면서 21에 가깝게 만들면 이긴다.

#### view

- [x] 플레이어 이름 입력 안내 메시지 출력
- [x] 플레이어 이름 입력
    - [x] delimiter: `,`
- [x] 카드 분배 안내메시지 출력
- [x] 진행 카드의 목록을 출력한다
- [x] 카드를 더 받을지 안내 메시지 출력
- [x] 카드를 더 받을지 말지 입력 (y, n)
- [x] 딜러 카드 추가 안내 메시지 출력
- [x] 게임 결과 출력
- [x] 최종 승패 출력

#### 리뷰 피드백 사항

- [x] Exception 발생 시 예외 메세지를 출력하고 해당 부분부터 재입력받기.
- [ ] 괄호 중첩시 안에서 밖으로 읽는 방식으로 가독성 높여보기
- [ ] get 줄이고 메시지를 보내기
    - [ ] user를 get하는 메서드를 지우고, 로직을 실행하도록 수정하기
    - [ ] Car가 Ace인지 물어보는 메서드 만들기
- [ ] playDealerTurn이 실행횟수를 반환하는 것을 분리하기
- [ ] blackJackRule과 같은 유틸 클래스 없애기
- [ ] 플레이어의 수에 제한을 둬서 NPE 방지하기
- [ ] 출력할 때 딜러 먼저 나오게 수정하기
- [ ] DrawInput의 위치 고민하기(패키지)
- [ ] 결과를 반환하는 객체 만들기
- [ ] Score라는 점수를 포장하는 객체를 만들어 점수에 대한 책임을 위임(정적 팩토리 메서드로 생성해 반환하는 것 생각)
- [ ] 딜러와 플레이어를 구분하는 것에 대한 고민 하기
- [x] controller의 lineBreak를 view의 메서드로 옮기기
- [ ] 여유가 있으면 custom exception을 구현하여 에러 출력 메세지 또한 domain에서 분리하기
