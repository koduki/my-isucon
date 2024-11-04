import http from 'k6/http';
import { check, sleep, group } from 'k6';

export const options = {
  vus: 10, // 仮想ユーザー数
  duration: '30s', // テスト実行時間
};

export default function () {
  group('API scenario test', () => {
    // Step 1: Create account
    const randomUserName = `User_${Math.random().toString(36).substring(2, 10)}`;
    let response = http.post('http://localhost:8080/account/', JSON.stringify({ userName: randomUserName }), {
      headers: { 'Content-Type': 'application/json' },
    });
    check(response, { 'create account status is 200': (r) => r.status === 200 });

    const accountData = JSON.parse(response.body);
    const accountId = accountData.id;
    const initialCardNumber = accountData.cards[0].cardNumber;

    // Step 2: Add card to account
    response = http.post(`http://localhost:8080/account/${accountId}/card`, null, {
      headers: { 'Content-Type': 'application/json' },
    });
    check(response, { 'add card status is 200': (r) => r.status === 200 });

    const newCardData = JSON.parse(response.body);
    const newCardNumber = newCardData.cardNumber;

    // Step 3: Get account details
    response = http.get(`http://localhost:8080/account/${accountId}`, {
      headers: { 'Content-Type': 'application/json' },
    });
    check(response, { 'get account status is 200': (r) => r.status === 200 });

    // Step 4: Make a purchase
    response = http.post('http://localhost:8080/payment/purchase', JSON.stringify({
      card: { cardNumber: initialCardNumber },
      itemName: 'Sample Item',
      amount: 100,
    }), {
      headers: { 'Content-Type': 'application/json' },
    });
    check(response, { 'purchase status is 200': (r) => r.status === 200 });

    // Step 5: Get payment history
    response = http.get(`http://localhost:8080/payment/history/${initialCardNumber}`, {
      headers: { 'Content-Type': 'application/json' },
    });
    check(response, { 'get payment history status is 200': (r) => r.status === 200 });

    sleep(1); // 各ステップの間に1秒の待機
  });
}