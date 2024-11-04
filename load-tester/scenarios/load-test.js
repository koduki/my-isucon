import http from 'k6/http';
import { check, sleep, group } from 'k6';

export const options = {
  vus: 10, // 仮想ユーザー数
  duration: '30s', // テスト実行時間
};

const BASE_URL = 'http://app:8080'

export default function () {
  group('Regular scenario', () => {
    const JSON_HEADER = { headers: { 'Content-Type': 'application/json' } }

    // Step 1: Create account
    const randomUserName = `User_${Math.random().toString(36).substring(2, 10)}`;
    let response = http.post(`${BASE_URL}/account/`, JSON.stringify({ userName: randomUserName }), JSON_HEADER);

    let account;
    let cardNumber;
    check(response, {
      'create account status is 200': (r) => r.status === 200,
      'card number is 16 digits': (r) => {
        account = JSON.parse(r.body);
        cardNumber = account.cards[0].cardNumber;
        return /^\d{16}$/.test(cardNumber);
      }
    });

    // Step 2: Add card to account
    response = http.post(`${BASE_URL}/account/${account.id}/card`, null, JSON_HEADER);
    check(response, { 'add card status is 200': (r) => r.status === 200 });
    const newCardData = JSON.parse(response.body);

    // Step 3: Get account details
    response = http.get(`${BASE_URL}/account/${account.id}`, JSON_HEADER);
    let replyAccount = JSON.parse(response.body);
    check(response, {
      'get account status is 200': (r) => r.status === 200,
      'replied account.id is same with created timing': (r) => account.id === replyAccount.id,
      'replied account.username is same with created timing': (r) => account.userName === replyAccount.userName,
      'replied account.card[0].number is same with created timing': (r) => replyAccount.cards[0].cardNumber === account.cards[0].cardNumber,
      'replied account.card[0].limit is same with created timing': (r) => replyAccount.cards[0].limitAmount === account.cards[0].limitAmount,
      'replied account.card[1].number is same with created timing': (r) => replyAccount.cards[1].cardNumber === newCardData.cardNumber,
      'replied account.card[1].limit is same with created timing': (r) => replyAccount.cards[1].limitAmount === newCardData.limitAmount,
    });

    // Step 4: Make a purchase
    const amount = Math.floor(Math.random() * (1000000 - 100 + 1)) + 100; // 100円から100万円までの乱数を生成
    response = http.post(`${BASE_URL}/payment/purchase`, JSON.stringify({
      cardNumber: cardNumber,
      itemName: 'Sample Item',
      amount: amount,
    }), JSON_HEADER);
    check(response, { 'purchase status is 200': (r) => r.status === 200 });

    // Step 5: Get payment history
    response = http.get(`${BASE_URL}/payment/history/${cardNumber}`, JSON_HEADER);
    check(response, { 'get payment history status is 200': (r) => r.status === 200 });

    sleep(1); // 各ステップの間に1秒の待機
  });

  group('Massive Purchase scenario', (amount) => {
    const JSON_HEADER = { headers: { 'Content-Type': 'application/json' } }
    const purchase = (amount) => http.post(`${BASE_URL}/payment/purchase`, JSON.stringify({
      cardNumber: account.cards[0].cardNumber,
      itemName: 'Sample Item',
      amount: amount,
    }), JSON_HEADER);

    // Step 1: Create account for limit check.
    const randomUserName = `User_${Math.random().toString(36).substring(2, 10)}`;
    let response = http.post(`${BASE_URL}/account/`, JSON.stringify({ userName: randomUserName }), JSON_HEADER);
    let account = JSON.parse(response.body);
    const limitAmount = account.cards[0].limitAmount;

    // 限度額未満の購入
    // 限度額未満の購入をランダムに繰り返す
    let totalAmount = 0;
    let purchaseCount = 0;
    const maxPurchases = Math.floor(Math.random() * (10000 - 100 + 1)) + 100; // 100回から10,000回のランダムな購入回数
    console.log(maxPurchases)
    for (let i = 0; i < maxPurchases; i++) {
      let purchaseAmount = Math.floor(Math.random() * (1000 - 100 + 1)) + 100; // 100円から1000円のランダムな金額
      if (totalAmount + purchaseAmount >= limitAmount) {
        purchaseAmount = limitAmount - totalAmount - 1; // 限度額を超えないように調整
      }

      response = purchase(purchaseAmount);
      check(response, { 'purchase status is 200 if under limit': (r) => r.status === 200 });

      totalAmount += purchaseAmount;
      purchaseCount = i;
      if (totalAmount >= limitAmount - 1) {
        console.log(purchaseCount)
        break; // 限度額に近づいたら終了
      }
    }

    // 最後に限度額に達する購入
    let finalAmount = limitAmount - totalAmount;
    response = purchase(finalAmount);
    check(response, { 'final purchase status is 200 if reaching limit': (r) => r.status === 200 });

    // 限度額を超えた購入
    response = purchase(1);
    check(response, {
      'purchase status is 422 if over limit': (r) => r.status === 422,
      'purchase body is Limit exceeded': (r) => r.body === 'Limit exceeded'
    });

    response = http.get(`${BASE_URL}/account/${account.id}`, JSON_HEADER);
    let replyAccount = JSON.parse(response.body);
    check(response, {
      'get account status is 200': (r) => r.status === 200,
      'limitAmount and usedAmount are same': (r) => replyAccount.cards[0].limitAmount === replyAccount.cards[0].usedAmount,
    });

    // Step 5: Get payment history
    response = http.get(`${BASE_URL}/payment/history/${replyAccount.cards[0].cardNumber}`, JSON_HEADER);
    let history = JSON.parse(response.body);
    purchaseCount += 2
    console.log(history.length)
    console.log(purchaseCount)
    check(response, { 'get payment history status is 200': (r) => r.status === 200 });
    check(response, { 'history record counts is same with request counts': (r) => purchaseCount　=== JSON.parse(r.body).length });


    sleep(1); // 各ステップの間に1秒の待機
  });
}