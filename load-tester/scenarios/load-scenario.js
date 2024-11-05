import http from 'k6/http';
import { check, sleep, group } from 'k6';

const BASE_URL = 'http://app:8080';

export function loadTestScenario() {
  group('Massive Purchase scenario', () => {
    const JSON_HEADER = { headers: { 'Content-Type': 'application/json' } };
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
    let totalAmount = 0;
    let purchaseCount = 0;
    const maxPurchases = Math.floor(Math.random() * (10000 - 100 + 1)) + 100;
    for (let i = 0; i < maxPurchases; i++) {
      let purchaseAmount = Math.floor(Math.random() * (1000 - 100 + 1)) + 100;
      if (totalAmount + purchaseAmount >= limitAmount) {
        purchaseAmount = limitAmount - totalAmount - 1;
      }

      response = purchase(purchaseAmount);
      check(response, { 'purchase status is 200 if under limit': (r) => r.status === 200 });

      totalAmount += purchaseAmount;
      purchaseCount = i;
      if (totalAmount >= limitAmount - 1) {
        break;
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
    purchaseCount += 2;
    check(response, { 'get payment history status is 200': (r) => r.status === 200 });
    check(response, { 'history record counts is same with request counts': (r) => purchaseCount === history.length });

    sleep(1);
  });
}