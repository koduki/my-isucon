import http from 'k6/http';
import { check, sleep, group } from 'k6';

const BASE_URL = 'http://app:8080'

import { Counter } from 'k6/metrics';
const errorCounter = new Counter('errors');

export function functionalTestScenario() {
  group('Regular scenario', () => {
    try {
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
      response = http.post(`${BASE_URL}/account/${account.customerNumber}/card`, null, JSON_HEADER);
      check(response, { 'add card status is 200': (r) => r.status === 200 });
      const newCardData = JSON.parse(response.body);

      // Step 3: Get account details
      response = http.get(`${BASE_URL}/account/${account.customerNumber}`, JSON_HEADER);
      let replyAccount = JSON.parse(response.body);
      check(response, {
        'get account status is 200': (r) => r.status === 200,
        'replied account.id is same with created timing and not undefined': (r) => account.id !== undefined && replyAccount.id !== undefined && account.id === replyAccount.id,
        'replied account.username is same with created timing': (r) => account.userName === replyAccount.userName,
        'replied account.card[0].number is same with created timing': (r) => (replyAccount.cards && replyAccount.cards[0]) ? replyAccount.cards[0].cardNumber === account.cards[0].cardNumber : true,
        'replied account.card[0].limit is same with created timing': (r) => (replyAccount.cards && replyAccount.cards[0]) ? replyAccount.cards[0].limitAmount === account.cards[0].limitAmount : true,
        'replied account.card[0].cardType is same with created timing': (r) => (replyAccount.cards && replyAccount.cards[0]) ? replyAccount.cards[0].cardType === account.cards[0].cardType : true,
        'replied account.card[0].cardface is same with created timing': (r) => (replyAccount.cards && replyAccount.cards[0]) ? replyAccount.cards[0].encodedCardFace === account.cards[0].encodedCardFace : true,
        'replied account.card[1].number is same with created timing': (r) => (replyAccount.cards && replyAccount.cards[1]) ? replyAccount.cards[1].cardNumber === newCardData.cardNumber : true,
        'replied account.card[1].limit is same with created timing': (r) => (replyAccount.cards && replyAccount.cards[1]) ? replyAccount.cards[1].limitAmount === newCardData.limitAmount : true,
        'replied account.card[1].cardface is same with created timing': (r) => (replyAccount.cards && replyAccount.cards[1]) ? replyAccount.cards[1].encodedCardFace === newCardData.encodedCardFace : true,
        'replied account.card[1].cardType is same with created timing': (r) => (replyAccount.cards && replyAccount.cards[1]) ? replyAccount.cards[1].cardType === newCardData.cardType : true,
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
    } catch (error) {
      console.error(error.message);
      errorCounter.add(1); // エラーが発生した場合にカウンターをインクリメント
    }
  });

  group('Limit Over scenario', () => {
    try {
      const JSON_HEADER = { headers: { 'Content-Type': 'application/json' } }

      // ユーザの作成
      const randomUserName = `User_${Math.random().toString(36).substring(2, 10)}`;
      let response = http.post(`${BASE_URL}/account/`, JSON.stringify({ userName: randomUserName }), JSON_HEADER);
      let account = JSON.parse(response.body);
      const limitAmount = account.cards[0].limitAmount;

      // 限度額未満の購入
      let amount = limitAmount - 1;
      response = http.post(`${BASE_URL}/payment/purchase`, JSON.stringify({
        cardNumber: account.cards[0].cardNumber,
        itemName: 'Sample Item',
        amount: amount,
      }), JSON_HEADER);
      check(response, { 'purchase status is 200 if under limit': (r) => r.status === 200 });

      // 限度額と同値の購入
      amount = 1;
      response = http.post(`${BASE_URL}/payment/purchase`, JSON.stringify({
        cardNumber: account.cards[0].cardNumber,
        itemName: 'Sample Item',
        amount: amount,
      }), JSON_HEADER);
      check(response, { 'purchase status is 200 if equal to limit': (r) => r.status === 200 });

      // 限度額を超えた購入
      amount = 1;
      response = http.post(`${BASE_URL}/payment/purchase`, JSON.stringify({
        cardNumber: account.cards[0].cardNumber,
        itemName: 'Sample Item',
        amount: amount,
      }), JSON_HEADER);
      check(response, {
        'purchase status is 422 if over limit': (r) => r.status === 422,
        'purchase body is Limit exceeded': (r) => r.body === 'Limit exceeded'
      });

      sleep(1); // 各ステップの間に1秒の待機
    } catch (error) {
      console.error(error.message);
      errorCounter.add(1); // エラーが発生した場合にカウンターをインクリメント
    }
  });

  group('Invalid Card scenario', () => {
    try {
      const JSON_HEADER = { headers: { 'Content-Type': 'application/json' } }

      // ユーザの作成
      const randomUserName = `User_${Math.random().toString(36).substring(2, 10)}`;
      let response = http.post(`${BASE_URL}/account/`, JSON.stringify({ userName: randomUserName }), JSON_HEADER);
      let account = JSON.parse(response.body);
      let cardNumber = account.cards[0].cardNumber;

      // 購入。初期状態はカードが有効なので正常
      let amount = 1;
      response = http.post(`${BASE_URL}/payment/purchase`, JSON.stringify({
        cardNumber: cardNumber,
        itemName: 'Sample Item',
        amount: amount,
      }), JSON_HEADER);
      check(response, { 'purchase status is 200': (r) => r.status === 200 });

      // カードを無効化
      response = http.del(`${BASE_URL}/account/cards/${cardNumber}`, null, JSON_HEADER);
      check(response, {
        'disable-card is 200': (r) => r.status === 200,
        'disable-card is returned card number': (r) => r.body.includes(cardNumber),
      });

      // 購入。無効なのでエラーが発生するべき
      amount = 1;
      response = http.post(`${BASE_URL}/payment/purchase`, JSON.stringify({
        cardNumber: cardNumber,
        itemName: 'Sample Item',
        amount: amount,
      }), JSON_HEADER);
      check(response, {
        'purchase status is 422 if invalid-card': (r) => r.status === 422,
        'purchase body is invalid-card': (r) => r.body === 'Invalid Card'
      });

      sleep(1); // 各ステップの間に1秒の待機
    } catch (error) {
      console.error(error.message);
      errorCounter.add(1); // エラーが発生した場合にカウンターをインクリメント
    }
  });
}